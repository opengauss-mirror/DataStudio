
package org.opengauss.mppdbide.test.bl.table;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.erd.model.AbstractERAttribute;
import org.opengauss.mppdbide.bl.erd.model.ERConstraint;
import org.opengauss.mppdbide.bl.erd.model.EREntity;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.EREntityMockUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class EREntityTest extends BasicJDBCTestCaseAdapter {

    MockConnection connection = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler statementHandler = null;
    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler estatementHandler = null;
    DBConnProfCache connProfCache = null;
    ConnectionProfileId profileId = null;
    ServerConnectionInfo serverInfo = null;

    @Before
	public void setUp() throws Exception {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");

        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
 
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
        CommonLLTUtils.fetchViewColumnInfo(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);

        serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
    }

    /*
     * (non-Javadoc)
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    @After
	public void tearDown() throws Exception {
        super.tearDown();

        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().close();

        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();
        connProfCache.closeAllNodes();

        Iterator<Server> itr = connProfCache.getServers().iterator();

        while (itr.hasNext()) {
            connProfCache.removeServer(itr.next().getId());
            itr = connProfCache.getServers().iterator();
        }

        connProfCache.closeAllNodes();

    }

    @Test
    public void test_getOLAPTableConstraintDetailsQuery() {
        try {
            EREntityMockUtils.getOLAPTableConstraintDetails(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData obj = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");

            EREntity entity = new EREntity(obj, false, database.getConnectionManager().getFreeConnection());
            entity.fillConstraints(database.getConnectionManager().getFreeConnection());
            ERConstraint constraint = (ERConstraint) entity.getConstraints().get(0);
            assertEquals("Primary key", constraint.getConsType());
            assertEquals(new Long(1), constraint.getKeyColIndex().get(0));
        } catch (DatabaseOperationException e) {
            MPPDBIDELoggerUtility.error("as expected");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getOLAPColumnCommentsQuery() {
        try {
            EREntityMockUtils.getOLAPColumnComments(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData obj = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");

            EREntity entity = new EREntity(obj, false, database.getConnectionManager().getFreeConnection());
            entity.fillColumnComments(database.getConnectionManager().getFreeConnection());
            assertEquals("this is to test column comments", entity.getColumnComments().get("col1"));
            entity.setHasColumnComments();
            boolean checkcomments = entity.isHasColumnComments();
            assertEquals(entity.isHasColumnComments(), checkcomments);
            boolean isHasNotNull = entity.isHasNotNullColumns();
            assertEquals(entity.isHasNotNullColumns(), isHasNotNull);
            boolean currTable = entity.isCurrentTable();
            DBConnection db = entity.getDbcon();
            assertEquals(db, entity.getDbcon());
            entity.setHasNotNullColumns();
            assertNotNull(entity.getAttributes());
        } catch (DatabaseOperationException e) {
            MPPDBIDELoggerUtility.error("as expected");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getOLAPTableCommentsQuery() {
        try {
            EREntityMockUtils.getOLAPTableComments(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData obj = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            EREntity entity = new EREntity(obj, false, database.getConnectionManager().getFreeConnection());
            entity.fillTableComments(database.getConnectionManager().getFreeConnection());
            assertEquals("This is test for table comments", entity.getTableComments());
            String name = entity.getFullyQualifiedName();
            assertEquals(name, entity.getFullyQualifiedName());

        } catch (DatabaseOperationException e) {
            MPPDBIDELoggerUtility.error("as expected");
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
    
    @Test
    public void test_getEREntityFillAttributes() {
        try {
            EREntityMockUtils.getOLAPTableComments(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData obj = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            EREntity entity = new EREntity(obj, false, database.getConnectionManager().getFreeConnection());
            entity.fillAttributes();

        } catch (DatabaseOperationException e) {
            MPPDBIDELoggerUtility.error("as expected");
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void test_virtual() {
        try {
            assertNotNull(profileId.getDatabase().getConnectionManager().getFreeConnection());
        } catch (MPPDBIDEException e) {
            e.printStackTrace();
        }
    }
}
