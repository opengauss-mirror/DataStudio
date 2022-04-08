package org.opengauss.mppdbide.test.presentation.table;

import java.util.ArrayList;
import java.util.HashSet;
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
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.UserNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewUtils;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.presentation.IViewTableDataCore;
import org.opengauss.mppdbide.presentation.IWindowDetail;
import org.opengauss.mppdbide.presentation.ViewTableData;
import org.opengauss.mppdbide.presentation.ViewTableDataCore;
import org.opengauss.mppdbide.presentation.ViewTableSequenceDataCore;
import org.opengauss.mppdbide.presentation.autorefresh.AutoRefreshQueryFormation;
import org.opengauss.mppdbide.presentation.autorefresh.RefreshObjectDetails;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

import static org.junit.Assert.*;

public class ViewTableDataTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;

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
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.addViewTableData(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);

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
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);

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
            itr = connProfCache.getServers().iterator();
        }

        connProfCache.closeAllNodes();

    }

    @Test
    public void test_initializeCore()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        CommonLLTUtils.addViewTableData(preparedstatementHandler);

        try
        {

            TableMetaData tablemetaData = new TableMetaData(1, "MyTable",
                    database.getNameSpaceById(1), "tablespace");
            IViewTableDataCore core = new ViewTableDataCore();
            core.init(tablemetaData);

        }
        catch (DatabaseOperationException e)
        {
            
            fail("not expected");
        }
    }
    

    public void test_getWindowDetails()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        TableMetaData tablemetaData;
        try
        {
            tablemetaData = new TableMetaData(1, "MyTable",
                    database.getNameSpaceById(1), "tablespace");
            IViewTableDataCore core = new ViewTableDataCore();
            core.init(tablemetaData);
            if (core.getWindowDetails() instanceof IWindowDetail)
            {
                assertTrue(true);
            }
            else
                fail("not expected");

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected to come here");
        }
    }

    
    public void test_getTitle()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        TableMetaData tablemetaData;
        try
        {
            tablemetaData = new TableMetaData(1, "MyTable", database.getNameSpaceById(1), "tablespace");
            IViewTableDataCore core = new ViewTableDataCore();
            core.init(tablemetaData);
            assertEquals(core.getWindowDetails().getTitle(), "pg_catalog.MyTable-Gauss@TestConnectionName");

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected to come here");
        }

    }

    public void test_getUniqueID()
    {

        Database database = connProfCache.getDbForProfileId(profileId);
        TableMetaData tablemetaData;

        try
        {
            tablemetaData = new TableMetaData(1, "MyTable",
                    database.getNameSpaceById(1), "tablespace");

            IViewTableDataCore core = new ViewTableDataCore();
            core.init(tablemetaData);
            assertEquals(core.getWindowDetails().getUniqueID(),
                    "VIEW_TABLE_DATA_pg_catalog.MyTable-Gauss@TestConnectionName");

        }
        catch (DatabaseOperationException e)
        {
            
            e.printStackTrace();
        }

    }

    public void test_getShortTitle()
    {

        Database database = connProfCache.getDbForProfileId(profileId);
        TableMetaData tablemetaData;
        try
        {
            tablemetaData = new TableMetaData(1, "MyTable",
                    database.getNameSpaceById(1), "tablespace");
            IViewTableDataCore core = new ViewTableDataCore();
            core.init(tablemetaData);
            core.getWindowDetails().getShortTitle();
            core.getTermConnection();
            core.getServerObject();
            core.getWindowDetails().isCloseable();
            assertEquals("select * from pg_catalog.\"MyTable\"",
                    core.getQuery());
            assertEquals("pg_catalog.\"MyTable\"",
                    core.getWindowDetails().getShortTitle());

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected to come here");
        }

    }

    

        public void test_cancelQuery()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        TableMetaData tablemetaData;
        try
        {
            tablemetaData = new TableMetaData(1, "MyTable",
                    database.getNameSpaceById(1), "tablespace");
            IViewTableDataCore core = new ViewTableSequenceDataCore();
            core.init(tablemetaData);
            String proglabel = core.getProgressBarLabel();
            assertEquals(proglabel, core.getProgressBarLabel());
            String windTitle = core.getWindowTitle();
            assertEquals(windTitle, core.getWindowTitle());
            String getrelQuery = core.getQuery();
            assertEquals(getrelQuery, core.getQuery());
            boolean isTabdropped = core.isTableDropped();
            assertEquals(isTabdropped, core.isTableDropped());
            assertTrue(true);    
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected to come here");
        }
    }

    @Test
    public void test_setColumnNames()
    {
        try
        {
            String columnNames[] = {"ename", "eid", "age"};
            ViewTableData data = new ViewTableData();
            data.setColumnNames(columnNames);
            assertEquals(3, data.getColumnNames().length);
        }
        catch (Exception e)
        {
            fail("not expected ");

        }
    }

    @Test
    public void test_setColumnValues()
    {
        try
        {

            List<String[]> columnValues = new ArrayList<>();
            String columnValuesRow[] = {"abc", "001", "25"};
            String columnValuesRow1[] = {"def", "002", "42"};

            columnValues.add(columnValuesRow);
            columnValues.add(columnValuesRow1);
            ViewTableData data = new ViewTableData();
            data.setColumnValues(columnValues);

            assertEquals(2, data.getColumnValues().size());
            
        }
        catch (Exception e)
        {
            fail("not expected ");

        }

    }
    
    @Test
    public void test_getElapsedTime()
    {
        IExecTimer timer=new ExecTimer("test");
        timer.start();
        ViewTableData data= new ViewTableData();
        
        try
        {
            timer.stop();
            data.setElapsedTime(timer.getElapsedTime());
            assertNotNull(data.getElapsedTime());
        }
        catch (DatabaseOperationException e)
        {
           fail("not expeted");
        }
    }
    
    @Test
    public void test_isEnabled()
    {
        ViewTableData data=new ViewTableData();
        data.setEndOfTableReached(true);
        assertTrue(data.isEndOfTableReached());
    }

    @Test
    public void test_viewViewData_02() {
        try {
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace ns = database.getNameSpaceById(1);
            ViewMetaData vmd = new ViewMetaData(2, "anything", ns,ns.getDatabase());
            vmd.setNamespace(ns);
            assertEquals("v", vmd.getRelKind());
            assertEquals("", vmd.getMaterViewString());
            assertEquals("OR REPLACE ", vmd.getOrReplaceString());
            vmd.setRelKind(null);
            assertEquals("v", vmd.getRelKind());
            assertEquals("", vmd.getMaterViewString());
            assertEquals("OR REPLACE ", vmd.getOrReplaceString());
            vmd.setRelKind("m");
            assertEquals("m", vmd.getRelKind());
            assertEquals("MATERIALIZED ", vmd.getMaterViewString());
            assertEquals("", vmd.getOrReplaceString());
        } catch (Exception e) {
            // TODO: handle exception
            fail("not execept!");
        }
    }
    
    @Test
    public void test_viewViewData_03() {
        try {
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace ns = database.getNameSpaceById(1);
            ViewMetaData vmd = new ViewMetaData(2, "anything", ns,ns.getDatabase());
            vmd.setNamespace(ns);
            assertEquals("v", vmd.getRelKind());
            assertEquals("DROP VIEW IF EXISTS pg_catalog.anything", vmd.getDropQuery(false));
            assertEquals("DROP VIEW pg_catalog.anything ", vmd.getDropQueryForOB(false));
            vmd.setRelKind(null);
            assertEquals("v", vmd.getRelKind());
            assertEquals("DROP VIEW IF EXISTS pg_catalog.anything", vmd.getDropQuery(false));
            assertEquals("DROP VIEW pg_catalog.anything ", vmd.getDropQueryForOB(false));
            vmd.setRelKind("m");
            assertEquals("DROP MATERIALIZED VIEW IF EXISTS pg_catalog.anything", vmd.getDropQuery(false));
            assertEquals("DROP MATERIALIZED VIEW pg_catalog.anything ", vmd.getDropQueryForOB(false));
        } catch (Exception e) {
            // TODO: handle exception
            fail("not execept!");
        }
    }
    
    @Test
    public void test_viewViewData_04() {
        try {
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace ns = database.getNameSpaceById(1);
            ViewMetaData vmd = new ViewMetaData(2, "anything", ns,ns.getDatabase());
            vmd.setNamespace(ns);
            String query = ViewUtils.getViewQuery(vmd.getOid(), false);
            String compareQuery = "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\" or c.relkind = 'm'::\"char\") and c.oid=2;";
            assertEquals(compareQuery, query);
        } catch (Exception e) {
            // TODO: handle exception
            fail("not execept!");
        }
    }
    
    @Test
    public void test_viewViewData_05() {
        try {
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace ns = database.getNameSpaceById(1);
            ViewMetaData vmd = new ViewMetaData(2, "anything", ns,ns.getDatabase());
            vmd.setNamespace(ns);
            String query = ViewUtils.getViewQueryByNamespaceId(ns.getOid(), false);
            String compareQuery = "SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner , c.relkind as relkind FROM pg_class c WHERE (c.relkind = 'v'::char or c.relkind = 'm'::char) and c.relnamespace = 1;";
            assertEquals(compareQuery, query);
        } catch (Exception e) {
            // TODO: handle exception
            fail("not execept!");
        }
    }
    /*
     * public void test_DatabaseCriticalException() {
     * 
     * Database database = connProfCache.getDbForProfileId(profileId);
     * TableMetaData tablemetaData; try { tablemetaData = new TableMetaData(1,
     * "MyTable", database.getNameSpaceById(1), "tablespace"); ViewTableDataCore
     * core = new ViewTableDataCore(tablemetaData); core.initializeCore();
     * ViewTableDataExecutor executor=new ViewTableDataExecutor(tablemetaData,
     * -1); executor.getResultsetData(2, database.getFreeConnection()); //
     * assertEquals((executor.getColumnNames(database.getFreeConnection())),5);
     * } catch (DatabaseOperationException e) {
     * System.out.println("inside of the dboprs");
     * fail("not expected to come here"); } catch (MPPDBIDEException e) {
     * System.out.println("insde of the mpdb");
     * fail("not expected to come here");
     * 
     * } catch (Exception e) { e.printStackTrace(); } }
     */

}
