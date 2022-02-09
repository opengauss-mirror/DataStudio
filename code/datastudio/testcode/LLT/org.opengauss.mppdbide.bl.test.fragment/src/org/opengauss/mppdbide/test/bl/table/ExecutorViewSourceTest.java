package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.StmtExecutor;
import org.opengauss.mppdbide.bl.executor.Executor;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.serverdatacache.SourceCode;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtilsHelper;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtilsHelper.EXCEPTIONENUM;
import org.opengauss.mppdbide.mock.bl.ExceptionConnectionHelper;
import org.opengauss.mppdbide.mock.bl.GaussMockPreparedStatementToHang;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.mock.bl.MockConnectionStubPS;
import org.opengauss.mppdbide.mock.bl.MockStatementToHang;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.exceptions.PasswordExpiryException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class ExecutorViewSourceTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    Database                          connectionProfile         = null;
    DebugObjects                      debugObject               = null;
    Executor                          executor                  = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;

    SourceCode                        code                      = null;
    StringBuilder                     strSourcecode             = null;
    ConnectionProfileId               profileId                 = null;
    ServerConnectionInfo              serverInfo                = null;

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
        MockStatementToHang.resetHangQueries();
        GaussMockPreparedStatementToHang.resetHangqueries();
        connection = new MockConnectionStubPS(true);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        MPPDBIDELoggerUtility.setArgs(null);
        // connection = getJDBCMockObjectFactory().getMockConnection();
        strSourcecode = new StringBuilder();
        code = new SourceCode();
        code.setVersionNumber1(1);
        code.setVersionNumber2(1);
        code.setHeaderLength(4);
        prepareConnectionResultSets();
        connectionProfile = prepareSetup();
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
        MockStatementToHang.resetHangQueries();
        GaussMockPreparedStatementToHang.resetHangqueries();
        connectionProfile = null;
        executor = null;
        strSourcecode = null;
        code = null;
        preparedstatementHandler.clearPreparedStatements();

        Iterator<Server> itr = DBConnProfCache.getInstance().getServers()
                .iterator();

        while (itr.hasNext())
        {
            DBConnProfCache.getInstance().removeServer(itr.next().getId());
        }

    }

    /**
     * Utility method to create basic breakpoint setup
     */
    public Database prepareSetup()
    {
        serverInfo = new ServerConnectionInfo();
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        JobCancelStatus status=new JobCancelStatus();
        status.setCancel(false);
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        // serverInfo.setSslPassword("12345");
        // ConnectionProfile connectionProfile = null;
        CommonLLTUtilsHelper.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtilsHelper.datatypes(preparedstatementHandler);
        ConnectionProfileId profileId = null;
        try
        {
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);
            connectionProfile = DBConnProfCache.getInstance()
                    .getDbForProfileId(profileId);

            executor = (Executor) connectionProfile.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
            // executor.refreshAllDebuggableObjects(profileId);
            debugObject = new DebugObjects(2, "func", OBJECTTYPE.PLSQLFUNCTION,
                    connectionProfile);

            debugObject.setExecuteTemplate("SELECT func()");
            debugObject.setNamespace(connectionProfile.getNameSpaceById(1));
            debugObject.getNamespace().setName("Public");
            connectionProfile.getNameSpaceById(1).getFunctions().addToGroup(debugObject);
            debugObject.setSourceCode(code);

            ObjectParameter objectParameter = new ObjectParameter();
            objectParameter.setDataType("integer");
            // objectParameter.setType(PARAMETERTYPE.)

            debugObject.setObjectReturns(objectParameter);

            strSourcecode.append("\"\nDeclare").append("\nc INT = 6;")
                    .append("\nd INT;BEGIN");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;")
                    .append("\nc := c+1;");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;")
                    .append("\nc := 100;");
            StringBuilder append = strSourcecode.append("\nd := c + 200;").append("\nreturn d;")
                    .append("\nend;\"").append("/");
            code.setCode(strSourcecode.toString());
        }
        /*
         * catch (Exception e) { e.printStackTrace();
         * fail("Presetup for breakpoint testcase failed. Throws exception " +
         * "Exception"); }
         */
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
            fail("Presetup for breakpoint testcase failed. Throws exception "
                    + "MPPDBIDEException : " + e.getMessage());
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }

        return connectionProfile;
    }

    /*
     * Utility to create initial connection setup
     */
    private void prepareConnectionResultSets()
    {
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();

        MockResultSet listnerResultSet = preparedstatementHandler
                .createResultSet();
        listnerResultSet.addRow(new Integer[] {12});
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_create_listener();", listnerResultSet);

        MockResultSet initializeDebugResult = preparedstatementHandler
                .createResultSet();
        initializeDebugResult.addRow(new String[] {"1231"});
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_initialize_debug();", initializeDebugResult);

        MockResultSet attachResult = preparedstatementHandler.createResultSet();
        attachResult.addRow(new Boolean[] {true});

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_attach_session(?,?);", attachResult);

        MockResultSet detachResult = preparedstatementHandler.createResultSet();
        detachResult.addRow(new Boolean[] {true});
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_detach_session(?);", detachResult);

        MockResultSet isDebugResult = preparedstatementHandler
                .createResultSet();
        isDebugResult.addRow(new Boolean[] {true});

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_is_debug_on();", isDebugResult);

        MockResultSet debugOn = preparedstatementHandler.createResultSet();
        debugOn.addRow(new Boolean[] {true});
        preparedstatementHandler.prepareResultSet("SELECT pldbg_debug_on();",
                debugOn);

        MockResultSet debugOff = preparedstatementHandler.createResultSet();
        debugOff.addRow(new Boolean[] {true});
        preparedstatementHandler.prepareResultSet("SELECT pldbg_debug_off();",
                debugOff);
        CommonLLTUtilsHelper.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtilsHelper.prepareValidateVersion(preparedstatementHandler);

        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addColumn("nspowner");
        namespaceRS.addColumn("nspacl");
        namespaceRS.addRow(new Object[] {1, "PUBLIC", 10, "NSPACL"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_NAMESPACE, namespaceRS);
        
        MockResultSet namespaceSysRS = preparedstatementHandler.createResultSet();
        namespaceSysRS.addColumn("oid");
        namespaceSysRS.addColumn("nspname");
        namespaceSysRS.addColumn("nspowner");
        namespaceSysRS.addColumn("nspacl");
        namespaceSysRS.addRow(new Object[] {1, "PUBLIC", 10, "NSPACL"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_SYSTEM_NAMESPACE, namespaceSysRS);
        
        MockResultSet namespaceRS1 = preparedstatementHandler.createResultSet();
        namespaceRS1.addColumn("oid");
        namespaceRS1.addColumn("nspname");
        namespaceRS1.addColumn("nspowner");
        namespaceRS1.addColumn("nspacl");
        namespaceRS1.addRow(new Object[]{1, "Public", 10, "NSPACL"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_PRIV, namespaceRS1);

        MockResultSet namespaceRS11 = preparedstatementHandler.createResultSet();
        namespaceRS11.addColumn("oid");
        namespaceRS11.addColumn("nspname");
        namespaceRS11.addColumn("nspowner");
        namespaceRS11.addColumn("nspacl");
        namespaceRS11.addRow(new Object[]{1, "Public", 10, "NSPACL"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_ALL, namespaceRS11);
        
        CommonLLTUtilsHelper.prepareFunctionResultSet(connection);
        CommonLLTUtilsHelper.prepareTriggerResultSet(connection);
        CommonLLTUtils.mockCheckDebugSupport(preparedstatementHandler);
        CommonLLTUtils.mockCheckExplainPlanSupport(preparedstatementHandler);
    }

    private void prepareConnectionResultSetsForException(
            ExceptionConnectionHelper econnection)
    {
        epreparedstatementHandler = econnection
                .getPreparedStatementResultSetHandler();

      //  estatementHandler = econnection.getStatementResultSetHandler();

        MockResultSet listnerResultSet = epreparedstatementHandler
                .createResultSet();
        listnerResultSet.addRow(new Integer[] {12});
        epreparedstatementHandler.prepareResultSet(
                "SELECT pldbg_create_listener();", listnerResultSet);

        MockResultSet initializeDebugResult = epreparedstatementHandler
                .createResultSet();
        initializeDebugResult.addRow(new String[] {"1231"});
        epreparedstatementHandler.prepareResultSet(
                "SELECT pldbg_initialize_debug();", initializeDebugResult);

        MockResultSet attachResult = epreparedstatementHandler
                .createResultSet();
        attachResult.addRow(new Boolean[] {true});

        epreparedstatementHandler.prepareResultSet(
                "SELECT pldbg_attach_session(?,?);", attachResult);

        MockResultSet detachResult = epreparedstatementHandler
                .createResultSet();
        detachResult.addRow(new Boolean[] {true});
        epreparedstatementHandler.prepareResultSet(
                "SELECT pldbg_detach_session(?);", detachResult);

        MockResultSet isDebugResult = preparedstatementHandler
                .createResultSet();
        isDebugResult.addRow(new Boolean[] {true});

        epreparedstatementHandler.prepareResultSet(
                "SELECT pldbg_is_debug_on();", isDebugResult);

        MockResultSet debugOn = epreparedstatementHandler.createResultSet();
        debugOn.addRow(new Boolean[] {true});
        epreparedstatementHandler.prepareResultSet("SELECT pldbg_debug_on();",
                debugOn);

        MockResultSet debugOff = epreparedstatementHandler.createResultSet();
        debugOff.addRow(new Boolean[] {true});
        epreparedstatementHandler.prepareResultSet("SELECT pldbg_debug_off();",
                debugOff);
        CommonLLTUtilsHelper.prepareProxyInfo(epreparedstatementHandler);
        CommonLLTUtilsHelper.prepareValidateVersion(epreparedstatementHandler);

        MockResultSet namespaceRS = epreparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addColumn("nspowner");
        namespaceRS.addColumn("nspacl");
        namespaceRS.addRow(new Object[] {1, "PUBLIC", 10, "NSPACL"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_NAMESPACE, namespaceRS);
        
        MockResultSet namespaceRS1 = preparedstatementHandler.createResultSet();
        namespaceRS1.addColumn("oid");
        namespaceRS1.addColumn("nspname");
        namespaceRS1.addRow(new Object[] {3, "PUBLIC"});
        namespaceRS.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_PRIV, namespaceRS1);
        
        MockResultSet namespaceRS11 = preparedstatementHandler.createResultSet();
        namespaceRS11.addColumn("oid");
        namespaceRS11.addColumn("nspname");
        namespaceRS11.addRow(new Object[] {3, "PUBLIC"});
        namespaceRS.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_ALL, namespaceRS11);

        CommonLLTUtilsHelper.prepareFunctionResultSet(connection);
        CommonLLTUtilsHelper.prepareTriggerResultSet(connection);
    }

    /**
     * TOR ID : TTA.BL.VIEWSOURCECODE002.FUNC.003 Test Case id :
     * TTA.BL.VIEWSOURCECODE002.FUNC.003_001 Description : Start Debugging and
     * call getSourcecode with a valid function id. Verify the sourcode returned
     * by server.
     */
    // //////////////////////
    @Test
    public void testTTA_BL_VIEWSOURCECODE002_FUNC_003_001()
    {
        try
        {
            createBreakpoint("f");
            debugPositionRelated();
            getServerBreakPoints();
            MockResultSet removeBPResult = preparedstatementHandler
                    .createResultSet();
            removeBPResult.addRow(new Boolean[] {false});
            preparedstatementHandler.prepareResultSet(
                    "SELECT pldbg_drop_breakpoint(?,?,?,?);", removeBPResult);

            debugObject.setIsDebuggable(true);

            // Will be unlocked by GaussMockConnectionStubPS
            GaussMockPreparedStatementToHang.setHang(CommonLLTUtilsHelper.SYNC_QUERY);
            GaussMockPreparedStatementToHang.setHang(debugObject.getExecuteTemplate());
            GaussMockPreparedStatementToHang.setHang(debugObject.getExecuteTemplate());
            
            StmtExecutor.setUniqCursorName("cursortest");
            MockStatementToHang.setHang("CURSOR cursortest NO SCROLL FOR SELECT func()");
            MockStatementToHang.setHang("FETCH FORWARD 1000 FROM cursortest");

            debugObject.setExecutionQuery(debugObject.getExecuteTemplate());
            CommonLLTUtilsHelper.prepareVersionCheckInitial(preparedstatementHandler);
            MockStatementToHang.resetHang(debugObject.getExecuteTemplate());

            mockRefreshSourceCode();
            
            debugObject.refreshSourceCode();


            assertTrue(
                    "Source code sent to server and returned back are mismatch",
                    debugObject.getSourceCode().getCode()
                            .equalsIgnoreCase(strSourcecode.toString()));

            assertEquals("SELECT func()", debugObject.getExecuteTemplate());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private void mockRefreshSourceCode() {
        MockResultSet getSourceCode = preparedstatementHandler.createResultSet();
        String query = "select headerlines, definition from PG_GET_FUNCTIONDEF(" + 2 + ");";

        getSourceCode.addColumn("headerlines");
        getSourceCode.addColumn("definition");
        getSourceCode.addRow(new Object[] {4,
            "\"\nDeclare\nc INT = 6;\nd INT;BEGIN\nc := c+1;\nc := c+1;\nc := c+1;\nc := c+1;\nc := c+1;\nc := 100;\nd := c + 200;\nreturn d;\nend;\""});

        preparedstatementHandler.prepareResultSet(query, getSourceCode);

        String query2 = "select xmin1, cmin1 from pldbg_get_funcVer(" + 2 + ")";
        MockResultSet versionRS = preparedstatementHandler.createResultSet();
        versionRS.addRow(new Object[] {1, 1});
        preparedstatementHandler.prepareResultSet(query2, versionRS);
    }

    @Test
    public void testTTA_BL_VIEWSOURCECODE002_FUNC_003_001_1()
    {
        try
        {
            createBreakpoint("f");
            debugPositionRelated();
            getServerBreakPoints();
            MockResultSet removeBPResult = preparedstatementHandler
                    .createResultSet();
            removeBPResult.addRow(new Boolean[] {false});
            preparedstatementHandler.prepareResultSet(
                    "SELECT pldbg_drop_breakpoint(?,?,?,?);", removeBPResult);
            debugObject.setIsDebuggable(true);
            GaussMockPreparedStatementToHang.setHang(CommonLLTUtilsHelper.SYNC_QUERY);
            GaussMockPreparedStatementToHang.setHang(debugObject.getExecuteTemplate());
            GaussMockPreparedStatementToHang.setHang(debugObject.getExecuteTemplate());
            
            StmtExecutor.setUniqCursorName("cursortest");
            MockStatementToHang.setHang("CURSOR cursortest NO SCROLL FOR SELECT func()");
            MockStatementToHang.setHang("FETCH FORWARD 1000 FROM cursortest");

            debugObject.setExecutionQuery(debugObject.getExecuteTemplate());
            CommonLLTUtilsHelper.prepareVersionCheckInitial(preparedstatementHandler);
            mockRefreshSourceCode();
            debugObject.refreshSourceCode();

            MockStatementToHang.resetHang(debugObject.getExecuteTemplate());
            
            debugObject.refreshSourceCode();

            assertTrue(
                    "Source code sent to server and returned back are mismatch",
                    debugObject.getSourceCode().getCode()
                            .equalsIgnoreCase(strSourcecode.toString()));

            assertEquals("SELECT func()", debugObject.getExecuteTemplate());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * TOR ID : TTA.BL.VIEWSOURCECODE002.FUNC.003 Test Case id :
     * TTA.BL.VIEWSOURCECODE002.FUNC.003_001 Description : Start Debugging fails
     * when source code is changed by server.
     */
    // ///////////////////////
    @Test
    public void testTTA_BL_VIEWSOURCECODE002_FUNC_003_002()
    {
        try
        {
            ExceptionConnectionHelper exceptionConnection = new ExceptionConnectionHelper();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    exceptionConnection);

            createBreakpoint("f");
            debugPositionRelated();
            getServerBreakPoints();
            MockResultSet removeBPResult = preparedstatementHandler
                    .createResultSet();
            removeBPResult.addRow(new Boolean[] {false});
            preparedstatementHandler.prepareResultSet(
                    "SELECT pldbg_drop_breakpoint(?,?,?,?);", removeBPResult);

            debugObject.setIsDebuggable(true);

            MockResultSet srcVersionResult = preparedstatementHandler
                    .createResultSet();
            srcVersionResult.addRow(new Integer[] {1, 2});
            preparedstatementHandler.prepareResultSet(
                    "SELECT * from pldbg_get_funcVer(?)", srcVersionResult);

            // Will be unlocked by GaussMockConnectionStubPS
            GaussMockPreparedStatementToHang.setHang(CommonLLTUtilsHelper.SYNC_QUERY);
            GaussMockPreparedStatementToHang.setHang(debugObject.getExecuteTemplate());
            GaussMockPreparedStatementToHang.setHang(debugObject.getExecuteTemplate());
            
            StmtExecutor.setUniqCursorName("cursortest");
            MockStatementToHang.setHang("CURSOR cursortest NO SCROLL FOR SELECT func()");
            MockStatementToHang.setHang("FETCH FORWARD 1000 FROM cursortest");


            debugObject.setExecutionQuery(debugObject.getExecuteTemplate());
            CommonLLTUtilsHelper.prepareVersionCheckInitial(preparedstatementHandler);
            exceptionConnection.setThrowExceptionGetInt(true);
            mockRefreshSourceCode();
            debugObject.refreshSourceCode();

            MockStatementToHang.resetHang(debugObject.getExecuteTemplate());

            assertTrue(
                    "Source code sent to server and returned back are mismatch",
                    debugObject.getSourceCode().getCode()
                            .equalsIgnoreCase(strSourcecode.toString()));

            assertEquals("SELECT func()", debugObject.getExecuteTemplate());
        }
        catch (Exception e)
        {
            assertTrue("Not expected to come here", false);
        }
    }

    private void createBreakpoint(String isDisabledStr)
    {
        MockResultSet addBPResult = preparedstatementHandler.createResultSet();
        addBPResult.addColumn("FUNC");
        addBPResult.addColumn("LINENUMBER");
        addBPResult.addColumn("TARGETNAME");
        addBPResult.addRow(new String[] {"2", "6", isDisabledStr + ":6:o)"});
        preparedstatementHandler
                .prepareResultSet(
                        "SELECT FUNC, LINENUMBER, TARGETNAME from pldbg_set_breakpoint(?,?,?,?,?);",
                        addBPResult);
    }

    private void getServerBreakPoints()
    {
        MockResultSet breakPointsFromServer = preparedstatementHandler
                .createResultSet();
        breakPointsFromServer.addColumn("FUNC");
        breakPointsFromServer.addColumn("LINENUMBER");
        breakPointsFromServer.addColumn("TARGETNAME");
        breakPointsFromServer.addRow(new String[] {"2", "6", "f:6:o"});
        preparedstatementHandler.prepareResultSet("SELECT FUNC, LINENUMBER,"
                + " TARGETNAME from pldbg_get_breakpoints(?,?);",
                breakPointsFromServer);
    }

    private void debugPositionRelated()
    {
        MockResultSet debugOnRS = preparedstatementHandler.createResultSet();
        debugOnRS.addRow(new Boolean[] {true});
        preparedstatementHandler.prepareResultSet("SELECT pldbg_debug_on();",
                debugOnRS);

        MockResultSet setDebugPositionRS = preparedstatementHandler
                .createResultSet();
        setDebugPositionRS.addColumn("FUNC");
        setDebugPositionRS.addColumn("LINENUMBER");
        setDebugPositionRS.addColumn("TARGETNAME");
        setDebugPositionRS.addRow(new String[] {"2", "6", "f:6:o"});
        preparedstatementHandler.prepareResultSet("SELECT FUNC, LINENUMBER,"
                + " TARGETNAME from pldbg_sync_target(?);", setDebugPositionRS);

        MockResultSet continueExecutionRS = preparedstatementHandler
                .createResultSet();
        continueExecutionRS.addColumn("FUNC");
        continueExecutionRS.addColumn("LINENUMBER");
        continueExecutionRS.addColumn("TARGETNAME");
        continueExecutionRS.addRow(new String[] {"0", "-1", "f:6:o"});
        preparedstatementHandler.prepareResultSet("SELECT FUNC, LINENUMBER,"
                + " TARGETNAME from pldbg_continue(?);", continueExecutionRS);

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("\"\nDeclare").append("\nc INT = 6;")
                .append("\nd INT;BEGIN");
        strBuilder.append("\nc := c+1;").append("\nc := c+1;")
                .append("\nc := c+1;");
        strBuilder.append("\nc := c+1;").append("\nc := c+1;")
                .append("\nc := 100;");
        strBuilder.append("\nd := c + 200;").append("\nreturn d;")
                .append("\nend;\"");

        MockResultSet getSourceResult = preparedstatementHandler
                .createResultSet();
        getSourceResult.addRow(new Object[] {4, strBuilder.toString(), 1, 1});

        /*
         * preparedstatementHandler.prepareResultSet("SELECT prosrc FROM pg_proc "
         * + "WHERE oid = ?;", getSourceResult);
         */
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_FUNCTION_WITH_HEADER, getSourceResult);

        MockResultSet getFrameResult = preparedstatementHandler
                .createResultSet();
        getFrameResult.addRow(new String[] {"(0,func,2,6)"});

        preparedstatementHandler.prepareResultSet("SELECT pldbg_get_stack(?);",
                getFrameResult);

        MockResultSet getFrameVarResult = preparedstatementHandler
                .createResultSet();
        getFrameVarResult.addColumn("name");
        getFrameVarResult.addColumn("varClass");
        getFrameVarResult.addColumn("lineNumber");
        getFrameVarResult.addColumn("isUnique");
        getFrameVarResult.addColumn("isConst");
        getFrameVarResult.addColumn("isNotNull");
        getFrameVarResult.addColumn("isValueNull");
        getFrameVarResult.addColumn("dtype");
        getFrameVarResult.addColumn("value");
        /*
         * getFrameVarResult.addRow(new String[] { "(c,L,3,t,f,f,f,23,6)" });
         * getFrameVarResult.addRow(new String[] { "(d,L,4,t,f,f,t,23,NULL)" });
         */
        getFrameVarResult.addRow(new String[] {"c", "L", "3", "t", "f", "f",
                "f", "23", "6"});
        getFrameVarResult.addRow(new String[] {"d", "L", "4", "t", "f", "f",
                "t", "23", "NULL"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.GET_VARIABLES,
                getFrameVarResult);

        MockResultSet getSelectFrameResult = preparedstatementHandler
                .createResultSet();
        getSelectFrameResult.addRow(new String[] {"(2,6,func)"});

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_select_frame(?,?);", getSelectFrameResult);

       MockResultSet funcResult = preparedstatementHandler.createResultSet();
        funcResult.addRow(new Integer[] {300});

        preparedstatementHandler.prepareResultSet("SELECT func()", funcResult);

        MockResultSet backEndPIDResult = preparedstatementHandler
                .createResultSet();
        backEndPIDResult.addRow(new Integer[] {300});

        preparedstatementHandler.prepareResultSet("SELECT pg_backend_pid();",
                backEndPIDResult);

        MockResultSet serverInSyncResult = preparedstatementHandler
                .createResultSet();
        serverInSyncResult.addRow(new Boolean[] {true});

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_server_in_sync(?);", serverInSyncResult);
    }

    /**
     * TOR ID : TTA.BL.SETBREAKPOINT002.FUNC.001 Test Case id :
     * TTA.BL.SETBREAKPOINT002.FUNC.001_006 Description : add breakpoint failure
     */

    @Test
    public void testTTA_BL_SETBREAKPOINT002_FUNC_001_006()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setServerPort(54322);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance()
                    .initConnectionProfile(serverInfo, status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);

            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");
            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());

            mockConnection.setNeedExceptioStatement(true);
            mockConnection.setThrowExceptionSetLong(true);

        }
        catch (MPPDBIDEException e)
        {
                assertTrue(false);
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

    /**
     * TOR ID : TTA.BL.SETBREAKPOINT002.FUNC.001 Test Case id :
     * TTA.BL.SETBREAKPOINT002.FUNC.001_007 Description : add breakpoint failure
     */
    /**
     * 
     */
    @Test
    public void testTTA_BL_SETBREAKPOINT002_FUNC_001_007()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setServerPort(54322);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);

        }
        catch (MPPDBIDEException e)
        {
                assertTrue(false);
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

    /**
     * TOR ID : TTA.BL.SETBREAKPOINT002.FUNC.001 Test Case id :
     * TTA.BL.SETBREAKPOINT002.FUNC.001_008 Description : add breakpoint failure
     */
    /**
     * 
     */
    @Test
    public void testTTA_BL_SETBREAKPOINT002_FUNC_001_008()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);

            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");

            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
            mockConnection.setNeedExceptioStatement(true);
            mockConnection.setThrowExceptionGetString(true);
            mockConnection.setThrowExceptionGetInt(true);
            mockConnection.setNeedExceptionResultset(true);
            mockConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

        }
        catch (MPPDBIDEException e)
        {
                assertTrue(false);
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

    /**
     * TOR ID : TTA.BL.DELETEBREAKPOINT002.FUNC.001 Test Case id :
     * TTA.BL.DELETEBREAKPOINT002.FUNC.001_006 Description : delete breakpoint
     * failure
     */
    /**
     * 
     */
    @Test
    public void testTTA_BL_REMOVEBREAKPOINT002_FUNC_001_006()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");
            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());

            mockConnection.setNeedExceptioStatement(true);
            mockConnection.setThrowExceptionSetLong(true);
        }
        catch (MPPDBIDEException e)
        {
                assertTrue(false);
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
    public void testTTA_BL_REMOVEBREAKPOINT002_FUNC_001_007()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setServerPort(54322);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");
            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());

            mockConnection.setNeedExceptioStatement(true);
            mockConnection.setThrowExceptionSetLong(true);
            DebugObjects debugObject = (DebugObjects) database.getDebugObjectById(1, 1);

        }
        catch (MPPDBIDEException e)
        {
                assertTrue(false);
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
    public void testTTA_BL_REMOVEBREAKPOINT002_FUNC_001_007_1()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            // prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");

            MockResultSet server_encoding = preparedstatementHandler
                    .createResultSet();
            server_encoding.addRow(new Object[] {"UTF-8"});
            preparedstatementHandler.prepareResultSet("show server_encoding",
                    server_encoding);

            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());

            mockConnection.setNeedExceptioStatement(true);
            mockConnection.setThrowExceptionSetLong(true);
            DebugObjects debugObject = (DebugObjects) database.getDebugObjectById(1, 1);
        
        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
                assertTrue(false);
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

    /**
     * TOR ID : TTA.BL.testTTA_BL_ENABLEBREAKPOINT002_FUNC_001_006.FUNC.001 Test
     * Case id : TTA.BL.ENABLEBREAKPOINT002.FUNC.001_006 Description : enable
     * breakpoint failure
     */
    /**
     * 
     */
    @Test
    public void testTTA_BL_ENABLEBREAKPOINT002_FUNC_001_006()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");
            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
            mockConnection.setNeedExceptioStatement(true);
            mockConnection.setThrowExceptionSetLong(true);

        }
        catch (MPPDBIDEException e)
        {
                assertTrue(false);
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

    /**
     * TOR ID : TTA.BL.testTTA_BL_ENABLEBREAKPOINT002_FUNC_001_006.FUNC.001 Test
     * Case id : TTA.BL.ENABLEBREAKPOINT002.FUNC.001_008 Description : enable
     * breakpoint failure
     */
    /**
     * 
     */
    @Test
    public void testTTA_BL_ENABLEBREAKPOINT002_FUNC_001_008()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setServerPort(54322);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);

            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
            mockConnection.setNeedExceptioStatement(true);
            mockConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

        }
        catch (MPPDBIDEException e)
        {
                assertTrue(false);
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

    /**
     * TOR ID : TTA.BL.testTTA_BL_ENABLEBREAKPOINT002_FUNC_001_007.FUNC.001 Test
     * Case id : TTA.BL.ENABLEBREAKPOINT002.FUNC.001_007 Description : enable
     * breakpoint failure
     */
    /**
     * 
     */
    @Test
    public void testTTA_BL_ENABLEBREAKPOINT002_FUNC_001_007()
    {   
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");

            MockResultSet enableBPResult = preparedstatementHandler
                    .createResultSet();
            enableBPResult.addRow(new Boolean[] {false});
            preparedstatementHandler.prepareResultSet(
                    "SELECT pldbg_enable_breakpoint(?,?,?,?);", enableBPResult);

            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
            mockConnection.setNeedExceptioStatement(true);
            mockConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);
            mockConnection.setThrowExceptionGetBoolean(true);
            mockConnection.setThrowExceptionCloseResultSet(true);

        }
        catch (MPPDBIDEException e)
        {
                assertTrue(false);
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

    /**
     * TOR ID : TTA.BL.testTTA_BL_DISABLEBREAKPOINT002_FUNC_001_006.FUNC.001
     * Test Case id : TTA.BL.DISABLEBREAKPOINT002.FUNC.001_006 Description :
     * disable breakpoint failure
     */
    /**
     * 
     */
    @Test
    public void testTTA_BL_DISABLEBREAKPOINT002_FUNC_001_006()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");
            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
            mockConnection.setNeedExceptioStatement(true);
            mockConnection.setThrowExceptionSetLong(true);
        }
        catch (MPPDBIDEException e)
        {
                assertTrue(false);
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
    public void testTTA_BL_debug002_FUNC_001_013()
    {

        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");
            Executor executor = (Executor) database.getExecutor();
            mockConnection.setNeedExceptioStatement(true);
            mockConnection.setThrowExceptionGetInt(true);
            mockConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);
        }
        catch (MPPDBIDEException e)
        {
            if (MessageConfigLoader.getProperty(
                    IMessagesConstants.ERR_BL_GET_SESSION_ID_FAILED).equals(
                    e.getDBErrorMessage()))
            {
                assertTrue(true);
            }
            else
            {
                assertTrue(false);
            }
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
    public void testTTA_BL_SETBREAKPOINT002_FUNC_001_009()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);

            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");
            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
            mockConnection.setNeedExceptioStatement(true);
            mockConnection.setThrowExceptionSetLong(true);
            mockConnection.setThrowExceptionMetaData(true);
        }
        catch (MPPDBIDEException e)
        {
            assertTrue(true);
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
    public void testTTA_BL_SETBREAKPOINT002_FUNC_001_019()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status = new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
        }
        catch (MPPDBIDEException e)
        {
            assertTrue(true);
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
    public void testTTA_BL_ENABLEBREAKPOINT002_FUNC_002_007()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
           
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();

            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setServerPort(54322);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");

            MockResultSet enableBPResult = preparedstatementHandler
                    .createResultSet();
            enableBPResult.addRow(new Boolean[] {true});
            preparedstatementHandler.prepareResultSet(
                    "SELECT pldbg_enable_breakpoint(?,?,?,?);", enableBPResult);

            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
        }
        catch (MPPDBIDEException e)
        {
         
                fail("not expected");
        }
        catch (Exception e)
        {
            fail("not expected");
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(database);
            DBConnProfCache.getInstance().removeServer(
                    database.getServer().getId());
        }
    }

    @Test
    public void testTTA_BL_ENABLEBREAKPOINT002_FUNC_002_008()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        SQLException sqlException = new SQLException("57P sql expection",
                "57P sql expection");
        preparedstatementHandler.prepareThrowsSQLException(
                "SELECT pldbg_enable_breakpoint(?,?,?,?);", sqlException);
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();

            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);

            MockResultSet enableBPResult = preparedstatementHandler
                    .createResultSet();
            enableBPResult.addRow(new Boolean[] {true});
            preparedstatementHandler.prepareResultSet(
                    "SELECT pldbg_enable_breakpoint(?,?,?,?);", enableBPResult);

            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
            assert(true);
        }
        catch (PasswordExpiryException e)
        {
            e.printStackTrace();
            fail("Not excepted to come here");
        } catch (IOException e) {
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
    public void testTTA_BL_SYNCBREAKPOINT001_FUNC_001()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);

            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");

            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
        }
        catch (MPPDBIDEException e)
        {
                fail();
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
    public void testTTA_BL_SYNCBREAKPOINT001_FUNC_002()
    {
        ConnectionProfileId profileId = null;
        Database database = null;

        SQLException sqlException = new SQLException("57P sql expection",
                "57P sql expection");
        preparedstatementHandler.prepareThrowsSQLException(
                "SELECT FUNC, LINENUMBER,"
                        + " TARGETNAME from pldbg_set_breakpoint(?,?,?,?,?);",
                sqlException);
        try
        {

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);

            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);

            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());

        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
            assert (true);
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
    public void testTTA_BL_DISABLEBREAKPOINT_TEST()
    {
        ConnectionProfileId profileId = null;
        Database database = null;
        try
        {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            // serverInfo.setServerType(DATABASETYPE.GAUSS);
            serverInfo.setConectionName("TestConnectionName2");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(54322);
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
           // serverInfo.setSslPrd("12345".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);
            database = DBConnProfCache.getInstance().getDbForProfileId(
                    profileId);
            ExceptionConnectionHelper mockConnection = new ExceptionConnectionHelper();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    mockConnection);
            prepareConnectionResultSetsForException(mockConnection);

            PreparedStatementResultSetHandler preparedstatementHandler = mockConnection
                    .getPreparedStatementResultSetHandler();
            preparedstatementHandler.prepareThrowsSQLException("valid query");
            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
            mockConnection.setNeedExceptioStatement(true);
            mockConnection.setThrowExceptionSetLong(true);

                assertTrue(executor != null);
            
        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
//            if (MessageConfigLoader.getProperty(
//                    IMessagesConstants.ERR_BL_DISABLE_BREAKPOINT_FAILED)
//                    .equals(e.getDBErrorMessage()))
//            {
//                assertTrue(true);
//            }
//            else
//            {
//                assertTrue(false);
//            }
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

}
