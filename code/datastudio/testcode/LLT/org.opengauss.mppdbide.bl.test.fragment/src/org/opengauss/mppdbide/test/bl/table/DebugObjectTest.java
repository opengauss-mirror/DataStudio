package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.bl.serverdatacache.ISourceCode;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.SourceCode;
import org.opengauss.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtilsHelper.EXCEPTIONENUM;
import org.opengauss.mppdbide.mock.bl.ExceptionConnectionHelper;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class DebugObjectTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    ServerConnectionInfo              serverInfo                = null;
    JobCancelStatus                   status                    = null;

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
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        status = new JobCancelStatus();
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
        CommonLLTUtils.fetchViewColumnInfo(preparedstatementHandler);
        CommonLLTUtils.preparePartitionConstrainstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionIndexLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.fetchAllSynonyms(preparedstatementHandler);
        CommonLLTUtils.fetchTriggerQuery(preparedstatementHandler);
        // org.opengauss.mppdbide.bl.test.gaussintegration.helpers.CommonLLTUtils.prepareConnectionResultSets(getJDBCMockObjectFactory());

        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status=new JobCancelStatus();
        status.setCancel(false);

        serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
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
       // database.getServer().close();

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
    public void testTTA_BL_DebugObject_FUNC_001_001()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
        try
        {
            /*
             * CommonLLTUtils.createTableRS(preparedstatementHandler);
             * CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
             * DBConnection dbconn1 = CommonLLTUtils.getDBConnection(); Database
             * database = connProfCache.getDbForProfileId(profileId);
             * database.setObjBrowserConn(dbconn1); Server server=new
             * Server(serverInfo); Namespace ns = database.getNameSpaceById(1);
             * DebugObjects debugObject = new DebugObjects(1, "test",
             * OBJECTTYPE.FUNCTION, database);
             */
           // CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
          //  CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
            CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
            Namespace ns = database.getNameSpaceById(1);
            ns.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            DebugObjects debugObject = (DebugObjects) database.getDebugObjectById(1, 1);
            debugObject.isDebuggable();
            assertEquals("auto1() - integer", debugObject.getDisplayName(false));

            debugObject.setExecuteTemplate("SELECT pg_catalog.function2(2, 3)");
            debugObject.generateExecutionTemplate();
            assertEquals(database, debugObject.getDatabase());
         

        }
        catch (Exception e)
        {
            System.out.println("code from debug object");
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }

    @Test
    public void testTTA_BL_DebugObject_FUNC_001_007()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
         
            DebugObjects debugObject = new DebugObjects(1,"DEbug",OBJECTTYPE.PLSQLFUNCTION,database);

            debugObject.setExecuteTemplate("SELECT pg_catalog.function2(2, 3)");
            ObjectParameter op = new ObjectParameter();
            op.setType(PARAMETERTYPE.IN);
            op.setDataType("refcursor");
            ObjectParameter[] params = new ObjectParameter[1];
            params[0] = op;
            debugObject.setObjectParameters(params);
            assertEquals(database, debugObject.getDatabase());
           
            database.getExecutor().getQueryExectuionString(debugObject);

            assertEquals("SELECT pg_catalog.function2(, )",
                    debugObject.getExecutionQuery());

            assertEquals(1, debugObject.getNameSpaceId());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }

    @Test
    public void testTTA_BL_DebugObject_FUNC_001_002()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObject = new DebugObjects(1, "test",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            debugObject.refreshSourceCode();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }

    @Test
    public void testTTA_BL_DebugObject_FUNC_001_002_01()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObject = new DebugObjects(1, "test",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            assertEquals(debugObject.getObjectType(), OBJECTTYPE.PLSQLFUNCTION);
            debugObject.refreshSourceCode();
            try
            {
                debugObject.setNamespace(database.getNameSpaceById(1));
                DebugObjects.DebugObjectsUtils.convertToObject(null, database);
            }
            catch (NullPointerException e)
            {
                System.out.println("expected...");
            }
            assertNotNull(debugObject);
            assertNull(debugObject.getLang());
            assertEquals("pg_catalog.test", debugObject.getDisplayName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }
    
    @Test
    public void testTTA_BL_DebugObject_FUNC_001_002_02()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObject = new DebugObjects(1, "test",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            debugObject.refreshSourceCode();
            ObjectParameter op = new ObjectParameter();
            op.setType(PARAMETERTYPE.IN);
            op.setDataType("refcursor");
            ObjectParameter[] params = new ObjectParameter[1];
            params[0] = op;
            debugObject.setObjectParameters(params);
            debugObject.generateDropQuery();
            debugObject.getLang();
            assertNotNull(debugObject);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }


    @Test
    public void test_convertToObject_Plpgsql()
    {
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
        CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
        DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getConnectionManager().setObjBrowserConn(dbconn1);
        DebugObjects debugObject = new DebugObjects(1, "test",
                OBJECTTYPE.PLSQLFUNCTION, database);
        assertEquals(debugObject.getObjectType(), OBJECTTYPE.PLSQLFUNCTION);

        try
        {
            debugObject.refreshSourceCode();
            // System.out.println(CommonLLTUtils.getResultSet());
            ResultSet rs = CommonLLTUtils.getResultSet();
            // DebugObjects.convertToObject(null, database);
            boolean res = rs.next();
            assertEquals(DebugObjects.DebugObjectsUtils.convertToObject(rs, database)
                    .isDebuggable(), true);

        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }

    @Test
    public void test_convertToObject_sql()
    {
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
        CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
        CommonLLTUtils.getAllFunctionQuery(preparedstatementHandler);
        DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getConnectionManager().setObjBrowserConn(dbconn1);
        DebugObjects debugObject = new DebugObjects(1, "test",
                OBJECTTYPE.PLSQLFUNCTION, database);
        assertEquals(debugObject.getObjectType(), OBJECTTYPE.PLSQLFUNCTION);
        boolean editVal = debugObject.getEditTerminalInputValues();
        assertEquals(editVal, debugObject.getEditTerminalInputValues());
        String windtitle = debugObject.getWindowTitleName();
        assertEquals(windtitle, debugObject.getWindowTitleName());
        String execQuery = debugObject.getExecutionQuery();
        assertEquals(execQuery, debugObject.getExecutionQuery());
     
        try
        {
            debugObject.refreshSourceCode();
            // System.out.println(CommonLLTUtils.getResultSet());
            ResultSet rs = CommonLLTUtils.getResultSet();
            // DebugObjects.convertToObject(null, database);
            boolean res = rs.next();
            assertEquals(DebugObjects.DebugObjectsUtils.convertToObject(rs, database)
                    .isDebuggable(), false);
            debugObject.setIsEditTerminalInputValues(false);
            assertTrue(debugObject.validateObjectType());
            debugObject.setIsCurrentTerminal(false);
            assertEquals(false, debugObject.getCurrentTerminal());
            debugObject.setCodeReloaded(false);
            assertEquals(false, debugObject.isCodeReloaded());

        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }

    }

    @Test
    public void test_TTA_OBJECT_PARAMETER_CHECK_01()
    {
        ObjectParameter objpar = new ObjectParameter();
        ObjectParameter.getVariables("10", "select {o, lll } from abc",
                "{b, mm } ", true, null);
        assertNotNull(objpar);
    }

    @Test
    public void test_TTA_OBJECT_PARAMETER_CHECK_04()
    {
        ObjectParameter objpar = new ObjectParameter();
        ObjectParameter.getVariables("10", "select {o, lll } from abc",
                "{o, mm } ", true, null);
        assertNotNull(objpar);
    }

    @Test
    public void test_TTA_OBJECT_PARAMETER_CHECK_05()
    {
        ObjectParameter objpar = new ObjectParameter();
        ObjectParameter.getVariables("10", "select {b, lll } from abc",
                "{kk, mm } ", true, null);
        assertNotNull(objpar);
    }

    @Test
    public void testTTA_BL_DebugObject_FUNC_001_004()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
          
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            Server server = new Server(serverInfo);
            Namespace ns = database.getNameSpaceById(1);
            DebugObjects dbgobj = new DebugObjects(1, "test",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            dbgobj.setDatabase(database);
            dbgobj.setNamespace(ns);
            dbgobj.belongsTo(database, server);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }

    @Test
    public void testTTA_BL_DebugObject_FUNC_001_005()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            Server server = new Server(serverInfo);
            Namespace ns = database.getNameSpaceById(1);
            DebugObjects dbgobj = new DebugObjects(1, "test",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            dbgobj.setDatabase(database);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }
    
    
    

    @Test
    public void testTTA_BL_DebugObject_FUNC_001_06()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            ExceptionConnectionHelper exceptionConnection = new ExceptionConnectionHelper();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    exceptionConnection);
            StringBuilder strSourcecode = new StringBuilder();

            strSourcecode.append("\"Declare").append("\nc INT = 6;")
                    .append("\nd INT;BEGIN");
            strSourcecode.append("\nc := c+1;").append(
                    "\nc := perform nestedfunc()");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;")
                    .append("\nc := 100;");
            strSourcecode.append("\nd := c + 200;").append("\nreturn d;")
                    .append("\nend;\")");
            MockResultSet indexRS = preparedstatementHandler.createResultSet();
            indexRS.addRow(new Object[] { 4, strSourcecode.toString()});
            String query = "select headerlines, definition from PG_GET_FUNCTIONDEF(1);";
            preparedstatementHandler.prepareResultSet(query, indexRS);
            
            String query2 = "select xmin1, cmin1 from pldbg_get_funcVer(" + 1 + ")";
            MockResultSet versionRS = preparedstatementHandler.createResultSet();
            versionRS.addRow(new Object[] {1, 1});
            preparedstatementHandler.prepareResultSet(query2, versionRS);
            
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            Server server = new Server(serverInfo);
            Namespace ns = database.getNameSpaceById(1);
            DebugObjects dbgobj = new DebugObjects(1, "test",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            exceptionConnection.setThrowExceptionSetString(true);

            ISourceCode code = dbgobj.getLatestSouceCode();
            assertNotNull(code);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not excepted to come here");
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }

    @Test
    public void testTTA_BL_DebugObject_FUNC_001_66()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            ExceptionConnectionHelper exceptionConnection = new ExceptionConnectionHelper();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    exceptionConnection);
            StringBuilder strSourcecode = new StringBuilder();

            strSourcecode.append("\"Declare").append("\nc INT = 6;")
                    .append("\nd INT;BEGIN");
            strSourcecode.append("\nc := c+1;").append(
                    "\nc := perform nestedfunc()");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;")
                    .append("\nc := 100;");
            strSourcecode.append("\nd := c + 200;").append("\nreturn d;")
                    .append("\nend;\")");
            MockResultSet indexRS = preparedstatementHandler.createResultSet();
            indexRS.addRow(new Object[] { null });
            String query = "select a.headerlines, a.definition, b.xmin, b.cmin from pg_proc b left join "
                    + "(select * from PG_GET_FUNCTIONDEF("
                    + 1
                    + ")) a on (1) where b.oid=" + 1;
            preparedstatementHandler.prepareResultSet(query, indexRS);
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            Server server = new Server(serverInfo);
            Namespace ns = database.getNameSpaceById(1);
            DebugObjects dbgobj = new DebugObjects(1, "test",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            exceptionConnection.setThrowExceptionSetString(true);

            dbgobj.getLatestSouceCode();
            fail("Not Excepted to come here");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assert (true);
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }
    
    @Test
    public void testTTA_BL_DebugObject_FUNC_BatchDrop_001()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
          //  CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
            CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
            Namespace ns = database.getNameSpaceById(1);
            ns.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            DebugObjects debugObject = (DebugObjects) database.getDebugObjectById(2, 1);
            
            assertEquals(debugObject.isDropAllowed(), true);
            assertEquals("Function", debugObject.getObjectTypeName());
            assertEquals("pg_catalog.function2", debugObject.getObjectFullName());
            String dropQry = debugObject.getDropQuery(false);
            assertEquals("DROP FUNCTION IF EXISTS pg_catalog.function2(IN integer,OUT integer,INOUT integer)", dropQry);
            
            dropQry = debugObject.getDropQuery(true);
            assertEquals("DROP FUNCTION IF EXISTS pg_catalog.function2(IN integer,OUT integer,INOUT integer) CASCADE", dropQry);
            
            // Remove the Object from namespace also
            ns = database.getNameSpaceById(1);
            
            DebugObjectGroup dog = (DebugObjectGroup) ns.getFunctions();
            assertEquals(2, dog.getSize());
            
            ns.remove(debugObject);
            
            dog = (DebugObjectGroup) ns.getFunctions();
            assertEquals(1, dog.getSize());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }
    
    @Test
    public void testTTA_BL_DebugObject_getLatestInfo()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObject = new DebugObjects(1, "test",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            assertNotNull(debugObject.getLatestInfo());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }
    
    @Test
    public void testTTA_BL_DebugObject_isChanged()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObject = new DebugObjects(1, "test",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            assertFalse(debugObject.isChanged(debugObject.getLatestInfo()));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }
    
    @Test
    public void testTTA_BL_DebugObject_handleChange()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObject = new DebugObjects(1, "test", OBJECTTYPE.PLSQLFUNCTION, database);
            debugObject.handleChange(debugObject.getLatestInfo());
            assertEquals(debugObject.getSourceCode().getCode(), debugObject.getLatestInfo());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
        
        
        
    }
    
    @Test
    public void test_defaultParameter()
    {
        DefaultParameter dp = new DefaultParameter("paramName", "paramType", "paramValue", PARAMETERTYPE.IN); 
        dp.setDefaultParameterName("paramName");
        dp.setDefaultParameterType("paramType");
        dp.setDefaultParameterValue("paramValue");
        dp.setDefaultParameterMode(PARAMETERTYPE.IN);
        assertEquals("paramName", dp.getDefaultParameterName());
        assertEquals("paramType", dp.getDefaultParameterType());
        assertEquals("paramValue", dp.getDefaultParameterValue());
        assertEquals(PARAMETERTYPE.IN, dp.getDefaultParameterMode());
        
    }
    
}
