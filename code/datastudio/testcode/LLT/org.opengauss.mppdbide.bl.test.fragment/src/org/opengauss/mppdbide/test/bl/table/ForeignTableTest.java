package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintType;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ForeignTable;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ColumnList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ConstraintList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ForeignTableGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.IndexList;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class ForeignTableTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    private DBConnection              dbconn;

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
	public void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        this.dbconn = CommonLLTUtils.getDBConnection();
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
        CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
        CommonLLTUtils.fetchViewColumnInfo(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();
        
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setUsername("myusername");
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo,status);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    @After
	public void tearDown() throws Exception
    {
        super.tearDown();

        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().close();

        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();
        connProfCache.closeAllNodes();

        Iterator<Server> itr = connProfCache.getServers().iterator();

        while (itr.hasNext())
        {
            connProfCache.removeServer(itr.next().getId());
        }

        connProfCache.closeAllNodes();

    }

    @Test
    public void testTTA_BL_FOREIGNTABLE_FUNC_BatchDrop_001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            ForeignTable foreigntablemetaData =
                    new ForeignTable(database.getNameSpaceById(1), OBJECTTYPE.FOREIGN_TABLE);
            foreigntablemetaData.setTempTable(true);
            foreigntablemetaData.setIfExists(true);
            foreigntablemetaData.setName("MyTable");
            foreigntablemetaData.setHasOid(true);
            foreigntablemetaData.setDistributeOptions("HASH");
            foreigntablemetaData.setNodeOptions("Node1");
            foreigntablemetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            constraintMetaData.setTable(foreigntablemetaData);
            foreigntablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(foreigntablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            foreigntablemetaData.getColumns().addItem(newTempColumn);

            ColumnMetaData newTempColumn1 = new ColumnMetaData(foreigntablemetaData, 1, "Col2",
                    new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            foreigntablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(foreigntablemetaData);
            indexMetaData.setNamespace(foreigntablemetaData.getNamespace());
            foreigntablemetaData.addIndex(indexMetaData);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            foreigntablemetaData.execCreate(database.getConnectionManager().getObjBrowserConn());
            assertNotNull(indexMetaData.getNamespace());

            database.getNameSpaceById(1).getForeignTablesGroup().addToGroup(foreigntablemetaData);

            assertEquals(indexMetaData.isDropAllowed(), false);
            assertEquals(newTempColumn.isDropAllowed(), false);
            assertEquals(constraintMetaData.isDropAllowed(), false);

            assertEquals(foreigntablemetaData.isDropAllowed(), false);
            assertEquals("Foreign Table", foreigntablemetaData.getObjectTypeName());
            assertEquals("pg_catalog.\"MyTable\"", foreigntablemetaData.getObjectFullName());
            String dropQry = foreigntablemetaData.getDropQuery(false);
            assertEquals("DROP FOREIGN TABLE IF EXISTS pg_catalog.\"MyTable\"", dropQry);

            dropQry = foreigntablemetaData.getDropQuery(true);
            assertEquals("DROP FOREIGN TABLE IF EXISTS pg_catalog.\"MyTable\" CASCADE", dropQry);

            // Test Remove of Table Column, Index, Constraint
            ColumnList list = (ColumnList) foreigntablemetaData.getColumns();
            assertEquals(list.getSize(), 2);

            foreigntablemetaData.remove(list.getItem(0));
            list = (ColumnList) foreigntablemetaData.getColumns();
            assertEquals(list.getSize(), 1);

            ConstraintList list1 = (ConstraintList) foreigntablemetaData.getConstraints();
            assertEquals(list1.getSize(), 1);

            foreigntablemetaData.remove(list1.getItem(0));
            list1 = (ConstraintList) foreigntablemetaData.getConstraints();
            assertEquals(list1.getSize(), 0);

            IndexList list2 = foreigntablemetaData.getIndexes();
            assertEquals(list2.getSize(), 1);

            foreigntablemetaData.remove(list2.getItem(0));
            list2 = foreigntablemetaData.getIndexes();
            assertEquals(list2.getSize(), 0);

            // Remove the Object from namespace also
            Namespace ns = database.getNameSpaceById(1);

            ForeignTableGroup ftg = ns.getForeignTablesGroup();
            assertEquals(1, ftg.getSize());

            ns.remove(foreigntablemetaData);

            ftg = ns.getForeignTablesGroup();
            assertEquals(0, ftg.getSize());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
