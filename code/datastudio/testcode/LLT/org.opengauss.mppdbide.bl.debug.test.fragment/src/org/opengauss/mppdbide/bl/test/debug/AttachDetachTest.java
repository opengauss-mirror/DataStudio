package org.opengauss.mppdbide.bl.test.debug;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.StmtExecutor.GetFuncProcResultValueParam;
import org.opengauss.mppdbide.bl.executor.Executor;
import org.opengauss.mppdbide.bl.mock.debug.CommonLLTUtilsHelper;
import org.opengauss.mppdbide.bl.mock.debug.GaussMockPreparedStatementToHang;
import org.opengauss.mppdbide.bl.mock.debug.MockConnectionStubPS;
import org.opengauss.mppdbide.bl.mock.debug.MockStatementToHang;
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
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.SourceCode;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class AttachDetachTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection               = null;
    Database                 connectionProfile        = null;
    DebugObjects                      debugObject              = null;
    DebugObjects                      nestedDebugObject        = null;
    Executor                          executor                 = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    SourceCode                        code                     = null;
    StringBuilder                     strSourcecode            = null;
    SourceCode                        nestedCode               = null;
    StringBuilder                     nestedStrSourcecode      = null;
    ServerConnectionInfo serverInfo = null;
    ConnectionProfileId profileId = null;

    /*
     * (non-Javadoc)
     *
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
	public void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtilsHelper.runLinuxFilePermissionInstance();
        IBLPreference sysPref = new MockDebugBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockDebugBLPreferenceImpl.setDsEncoding("UTF-8");
        MockDebugBLPreferenceImpl.setFileEncoding("UTF-8");
        MockStatementToHang.resetHangQueries();
        GaussMockPreparedStatementToHang.resetHangqueries();
        connection = new MockConnectionStubPS(false);
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtilsHelper.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        strSourcecode = new StringBuilder();
        code = new SourceCode();
        code.setVersionNumber1(1);
        code.setVersionNumber2(1);
        nestedStrSourcecode = new StringBuilder();
        nestedCode = new SourceCode();
        prepareConnectionResultSets();
       
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
        connectionProfile = null;
        executor = null;
        strSourcecode = null;
        code = null;
        nestedStrSourcecode = null;
        nestedCode = null;
        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearThrowsSQLException();
        MockStatementToHang.resetHangQueries();
        GaussMockPreparedStatementToHang.resetHangqueries();
        
        Iterator<Server> itr = DBConnProfCache.getInstance().getServers().iterator();
        
        while(itr.hasNext())
        {
            DBConnProfCache.getInstance().removeServer(itr.next().getId());
        }
        
    }

    /**
     * Utility method to create basic breakpoint setup
     */
    private Database prepareSetup()
    {
        serverInfo = new ServerConnectionInfo();
       // serverInfo.setServerType(DATABASETYPE.GAUSS);
        JobCancelStatus status=new JobCancelStatus();
        status.setCancel(false);
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
       
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        CommonLLTUtilsHelper.prepareProxyInfoForGetAllNamespaces(preparedstatementHandler);
        CommonLLTUtilsHelper.mockCheckDebugSupportValid(preparedstatementHandler);
        CommonLLTUtilsHelper.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtilsHelper.datatypes(preparedstatementHandler);
        CommonLLTUtilsHelper.prepareConnectionResultSets(getJDBCMockObjectFactory());
//        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        try
        {
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
                  

           // connectionProfile.connectToServer();
           // executor = connectionProfile.getExecutor();
           // executor.connectToServer(serverInfo);
            //executor.connectToDebugServer(serverInfo, profileId);
            //executor.connectToDebugServer(serverInfo, profileId);
            //executor.refreshAllDebuggableObjects(profileId);
            debugObject = new DebugObjects(6, "func", OBJECTTYPE.PLSQLFUNCTION,
                    connectionProfile);
            System.out.println(debugObject.getDatabase().getName());

            debugObject.setExecuteTemplate("SELECT func()");

           // connectionProfile.addFunction(debugObject, 1);
            debugObject.setSourceCode(code);


            ObjectParameter objectParameter = new ObjectParameter();
            objectParameter.setDataType("integer");
            objectParameter.setType(PARAMETERTYPE.IN);

            debugObject.setObjectReturns(objectParameter);

            ArrayList<ObjectParameter> templateParameters = new ArrayList<ObjectParameter>();
            templateParameters.add(objectParameter);

            debugObject.setTemplateParameters(templateParameters);

            debugObject.setIsDebuggable(true);

            strSourcecode.append("\nDeclare").append("\nc INT = 6;")
                    .append("\nd INT;BEGIN");
            strSourcecode.append("\nc := c+1;").append(
                    "\nc := perform nestedfunc()");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;")
                    .append("\nc := 100;");
            strSourcecode.append("\nd := c + 200;").append("\nreturn d;")
                    .append("\nend;");
            
            System.out.println(strSourcecode);
            code.setCode(strSourcecode.toString());
            code.setVersionNumber1(1);
            code.setVersionNumber2(1);

            nestedDebugObject = new DebugObjects(7, "nestedfunc",
                    OBJECTTYPE.PLSQLFUNCTION, connectionProfile);

            nestedDebugObject.setExecuteTemplate("SELECT nestedfunc()");

           // connectionProfile.addFunction(nestedDebugObject, 1);
            nestedDebugObject.setSourceCode(nestedCode);


            ObjectParameter nestedobjectParameter = new ObjectParameter();
            nestedobjectParameter.setDataType("integer");
            // objectParameter.setType(PARAMETERTYPE.)

            nestedDebugObject.setObjectReturns(nestedobjectParameter);

            nestedDebugObject.setIsDebuggable(true);

            nestedStrSourcecode.append("\nDeclare").append("\na INT = 12;")
                    .append("\nb INT;BEGIN");
            nestedStrSourcecode.append("\na := a+1;").append("\na := a+1;")
                    .append("\nb := a + 100;");
            nestedStrSourcecode.append("\nreturn b;").append("\nend;");

            nestedCode.setCode(nestedStrSourcecode.toString());
            nestedCode.setVersionNumber1(1);
            nestedCode.setVersionNumber2(1);

        }
        /*catch (Exception e)
        {
            e.printStackTrace();
            fail("Presetup for breakpoint testcase failed. Throws exception "
                    + "Exception");
        }*/
        catch (MPPDBIDEException e)
        {
            fail("Presetup for breakpoint testcase failed. Throws exception "
                    + "PLSQLIDEException : " + e.getMessage());
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
        listnerResultSet.addRow(new Integer[] { 12 });
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_create_listener();", listnerResultSet);

        MockResultSet initializeDebugResult = preparedstatementHandler
                .createResultSet();
        initializeDebugResult.addRow(new String[] { "1231" });
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_initialize_debug();", initializeDebugResult);

        MockResultSet attachResult = preparedstatementHandler.createResultSet();
        attachResult.addRow(new Boolean[] { true });

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_attach_session(?,?);", attachResult);

        MockResultSet detachResult = preparedstatementHandler.createResultSet();
        detachResult.addRow(new Boolean[] { true });
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_detach_session(?);", detachResult);

        MockResultSet isDebugResult = preparedstatementHandler
                .createResultSet();
        isDebugResult.addRow(new Boolean[] { true });

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_is_debug_on();", isDebugResult);

        MockResultSet debugOn = preparedstatementHandler.createResultSet();
        debugOn.addRow(new Boolean[] { true });
        preparedstatementHandler.prepareResultSet("SELECT pldbg_debug_on();",
                debugOn);

        MockResultSet debugOff = preparedstatementHandler.createResultSet();
        debugOff.addRow(new Boolean[] { true });
        preparedstatementHandler.prepareResultSet("SELECT pldbg_debug_off();",
                debugOff);
        CommonLLTUtilsHelper.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtilsHelper.prepareValidateVersion(preparedstatementHandler);
        
        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addColumn("nspowner");
        namespaceRS.addColumn("nspacl");
        namespaceRS.addRow(new Object[]{1, "PUBLIC", 10, "NSPACL"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.GET_ALL_NAMESPACE, namespaceRS);
        
        MockResultSet namespaceSysRS = preparedstatementHandler.createResultSet();
        namespaceSysRS.addColumn("oid");
        namespaceSysRS.addColumn("nspname");
        namespaceSysRS.addColumn("nspowner");
        namespaceSysRS.addColumn("nspacl");
        namespaceSysRS.addRow(new Object[]{1, "PUBLIC", 10, "NSPACL"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.GET_ALL_SYSTEM_NAMESPACE, namespaceSysRS);
        MockResultSet namespaceRS1 = preparedstatementHandler.createResultSet();
        namespaceRS1.addColumn("oid");
        namespaceRS1.addColumn("nspname");
        namespaceRS1.addColumn("nspowner");
        namespaceRS1.addColumn("nspacl");
        namespaceRS1.addRow(new Object[]{1, "PUBLIC", 10, "NSPACL"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_PRIV, namespaceRS1);
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_ALL, namespaceRS1);
        CommonLLTUtilsHelper.prepareFunctionResultSet(connection);
        CommonLLTUtilsHelper.prepareTriggerResultSet(connection);
        
        MockResultSet getUserRoleRs = preparedstatementHandler.createResultSet();
        getUserRoleRs.addColumn("rolname");
        getUserRoleRs.addColumn("rolcanlogin");
        getUserRoleRs.addColumn("oid");
        getUserRoleRs.addRow(new Object[] {"chris", true, 16408});
        getUserRoleRs.addRow(new Object[] {"tom", false, 16410});
        preparedstatementHandler.prepareResultSet(
                "SELECT rolname,rolcanlogin,oid FROM pg_catalog.pg_roles WHERE rolsuper = false;", getUserRoleRs);
        
        MockResultSet serverEncodingRs = preparedstatementHandler.createResultSet();
        serverEncodingRs.addColumn("server_encoding");
        serverEncodingRs.addRow(new Object[] {"UTF-8"});
        preparedstatementHandler.prepareResultSet("show server_encoding", serverEncodingRs);
    }





    @Test
    public void testTTA_BL_ATTACH002_FUNC_003_001()
    {
        preparedstatementHandler
                .prepareThrowsSQLException("SELECT pldbg_attach_session(?,?);");
        
        
        try
        {
            connectionProfile = prepareSetup();
            ((Executor)connectionProfile.getExecutor()).connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
            assertTrue(null != connectionProfile);
        }
        catch (MPPDBIDEException e)
        {
            // fail("AttacheDetach test failed. Attaching to target session failed.");
            try
            {
            	DBConnProfCache.getInstance().destroyConnection(connectionProfile);
                preparedstatementHandler.clearThrowsSQLException();
                verifyConnectionClosed();
            }
            catch (Exception e1)
            {
                fail("AttacheDetach test failed. Attaching to target session failed.");
            }

        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
    } 
    
    @Test
    public void testTTA_BL_ATTACH002_FUNC_003_002()
    {
        SQLException sqlException = new SQLException(
                "Throwing SQL exception intentionally.", "57PSQLException");

        preparedstatementHandler.prepareThrowsSQLException(
                "SELECT pldbg_attach_session(?,?);", sqlException);
        try
        {
        	 connectionProfile = prepareSetup();
        	 ((Executor)connectionProfile.getExecutor()).connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
        	  assertTrue(null != connectionProfile);
        }
        catch (MPPDBIDEException e)
        {
            // fail("AttacheDetach test failed. Attaching to target session failed.");
            try
            {
            	DBConnProfCache.getInstance().destroyConnection(connectionProfile);
                preparedstatementHandler.clearThrowsSQLException();
                verifyConnectionClosed();
            }
            catch (Exception e1)
            {
                fail("AttacheDetach test failed. Attaching to target session failed.");
            }

        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
    }

    @Test
    public void test_StmtExecutor_GetFuncProcResultValueParam()
    {
        try
        {
            GetFuncProcResultValueParam param = new GetFuncProcResultValueParam(0, false, false);
            param.setColumnCount(0);
            param.setCallableStmt(true);
            param.setInputParaVisited(true);
            assertEquals(param.getColumnCount(), 0);
            assertTrue(param.isCallableStmt());
            assertTrue(param.isInputParaVisited());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }



}
