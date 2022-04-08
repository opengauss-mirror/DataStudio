package org.opengauss.mppdbide.test.presentation.table;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.presentation.EditTableDataCore;
import org.opengauss.mppdbide.presentation.edittabledata.DSEditTableDataGridDataProvider;
import org.opengauss.mppdbide.presentation.edittabledata.EditTableExecuteQueryFactory;
import org.opengauss.mppdbide.presentation.edittabledata.EditTableExecuteQueryUtility;
import org.opengauss.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import org.opengauss.mppdbide.presentation.grid.IDSEditGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.utils.JSQLParserUtils;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class EditTableExecuteQueryTest extends BasicJDBCTestCaseAdapter {
    MockConnection connection = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler statementHandler = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler estatementHandler = null;
    DBConnProfCache connProfCache = null;
    ConnectionProfileId profileId = null;
    EditTableDataHelper helper;
    IDSEditGridDataProvider dataProvider;
    EditTableDataCore coreObject;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
        initializeHelper();
    }

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

    public void initializeHelper() {
        helper = new EditTableDataHelper(connProfCache.getDbForProfileId(profileId));
        try {
            dataProvider = helper.getDataProvider(preparedstatementHandler);
            coreObject = helper.getCoreObject();

        } catch (DatabaseCriticalException e) {
            e.printStackTrace();
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
        } catch (MPPDBIDEException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExecuteInsertRowCommandWhenTableIsNull() {
        CommonLLTUtils.prepareDistibutionColumnListResultSetWithHashDistribution(preparedstatementHandler);

        try {
            dataProvider.init();
            List<IDSGridDataRow> consolidatedRows = dataProvider.getConsolidatedRows();
            dataProvider.deleteRecord((IDSGridEditDataRow) consolidatedRows.get(0), false);
            String[] fullTableName = JSQLParserUtils.getSplitQualifiedName(coreObject.getTable().getName(), false);
            EditTableExecuteQueryUtility editTableExcutequery =
                    EditTableExecuteQueryFactory.getEditTableExecuteQuery(dataProvider.getDatabse().getDBType());
            boolean flag = editTableExcutequery.executeInsertRowCommand((IDSGridEditDataRow) consolidatedRows.get(0),
                    (IDSGridEditDataRow) consolidatedRows.get(0), (DSEditTableDataGridDataProvider) dataProvider, null,
                    coreObject.getTermConnection().getConnection(), fullTableName, true, true);
            assertTrue(flag);
        } catch (DatabaseOperationException e) {
            fail("not expected");
        } catch (DatabaseCriticalException e) {
            fail("not expected");
        } catch (MPPDBIDEException e) {
            fail("not expected");
        }
    }

    @Test
    public void testExecuteInsertRowCommandWhenTableNotNull() {
        CommonLLTUtils.prepareDistibutionColumnListResultSetWithHashDistribution(preparedstatementHandler);

        try {
            dataProvider.init();
            List<IDSGridDataRow> consolidatedRows = dataProvider.getConsolidatedRows();
            dataProvider.deleteRecord((IDSGridEditDataRow) consolidatedRows.get(0), false);
            EditTableExecuteQueryUtility editTableExcutequery =
                    EditTableExecuteQueryFactory.getEditTableExecuteQuery(dataProvider.getDatabse().getDBType());
            boolean flag = editTableExcutequery.executeInsertRowCommand((IDSGridEditDataRow) consolidatedRows.get(6),
                    (IDSGridEditDataRow) consolidatedRows.get(0), (DSEditTableDataGridDataProvider) dataProvider,
                    coreObject.getTable(), coreObject.getTermConnection().getConnection(), null, true, true);
            assertTrue(flag);
        } catch (DatabaseOperationException e) {
            fail("not expected");
        } catch (DatabaseCriticalException e) {
            fail("not expected");
        } catch (MPPDBIDEException e) {
            fail("not expected");
        }
    }
}
