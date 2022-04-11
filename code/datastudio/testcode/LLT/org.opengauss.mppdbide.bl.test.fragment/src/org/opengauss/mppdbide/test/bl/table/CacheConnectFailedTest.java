package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.executor.AbstractExecutor;
import org.opengauss.mppdbide.bl.executor.Executor;
import org.opengauss.mppdbide.bl.executor.TargetExecutor;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtilsHelper;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class CacheConnectFailedTest extends BasicJDBCTestCaseAdapter
{
    
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    MockConnection connection = null;
    DBConnProfCache connectionCache = null;
    ConnectionProfileId profileId = null;
    ServerConnectionInfo connInfo = null;
    JobCancelStatus status=null;
    
    @Before
	public void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
    }
    
    private void prepareSetup()
    {
        connection = getJDBCMockObjectFactory()
                .getMockConnection();
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        MPPDBIDELoggerUtility.setArgs(null);
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        MockResultSet listnerResultSet = preparedstatementHandler
                .createResultSet();

        listnerResultSet.addColumn("debuggable");
        listnerResultSet.addRow(new Object[] {true});

        preparedstatementHandler
                .prepareResultSet("SELECT pldbg_is_debug_enable();",
                        listnerResultSet);
        
        MockResultSet listenerResult = preparedstatementHandler.createResultSet();
        listenerResult.addRow(new Integer[] { 1 });

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_create_listener();", listenerResult);        

        
        MockResultSet attachResult = preparedstatementHandler.createResultSet();
        attachResult.addRow(new Object[] {true});

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_attach_session(?,?);", attachResult);      
        
        MockResultSet getSessionResult = preparedstatementHandler.createResultSet();
        getSessionResult.addRow(new String[] { "100" });

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_initialize_debug();", getSessionResult);
        
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
        CommonLLTUtilsHelper.datatypes(preparedstatementHandler);
        CommonLLTUtilsHelper.prepareValidateVersion(preparedstatementHandler);
        CommonLLTUtils.mockGetPartitionOverloaded(preparedstatementHandler);
        
        connectionCache = DBConnProfCache.getInstance();
        connInfo = new ServerConnectionInfo();
        status =new JobCancelStatus();
        status.setCancel(false);
        //connInfo.setServerType(DATABASETYPE.GAUSS);
        connInfo.setConectionName("TestConnectionName");
        connInfo.setServerIp("");
        connInfo.setServerPort(5432);
        connInfo.setDatabaseName("Gauss");
        connInfo.setUsername("myusername");
        connInfo.setDriverName("FusionInsight LibrA");
        connInfo.setPrd("mypassword".toCharArray());
        connInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        connInfo.setPrivilegeBasedObAccess(true);
        try
        {
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connInfo);
            profileId = connectionCache.initConnectionProfile(connInfo, status);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Creating connection profile cache failed");
        }
        
        
    }

    public void prepareConnectionResultSets()
    {
        MockConnection connection = getJDBCMockObjectFactory()
                .getMockConnection();

        PreparedStatementResultSetHandler preparedstatementHandler = connection
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

        MockResultSet isDebugResult = preparedstatementHandler.createResultSet();
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
        
        MockResultSet getAllFuncResultSet = preparedstatementHandler
                .createResultSet();

        getAllFuncResultSet.addColumn("oid");
        getAllFuncResultSet.addColumn("objname");
        getAllFuncResultSet.addColumn("namespace");
        getAllFuncResultSet.addColumn("ret");
        getAllFuncResultSet.addColumn("alltype");
        getAllFuncResultSet.addColumn("argtype");
        getAllFuncResultSet.addColumn("argname");
        getAllFuncResultSet.addColumn("argmod");
        getAllFuncResultSet.addColumn("secdef");
        getAllFuncResultSet.addColumn("vola");
        getAllFuncResultSet.addColumn("isstrict");
        getAllFuncResultSet.addColumn("retset");
        getAllFuncResultSet.addColumn("procost");
        getAllFuncResultSet.addColumn("setrows");


        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        getAllFuncResultSet.addRow(new Object[] { new Integer(1), "function1",
                new Integer(16), null, null, null, "mode", false, "volatile",
                false, false, 10, 10});

        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_FUNCTION_QUERY,getAllFuncResultSet);

        MockResultSet getAllTriggerResultSet = preparedstatementHandler
                .createResultSet();

        getAllTriggerResultSet.addColumn("oid");
        getAllTriggerResultSet.addColumn("objname");
        getAllTriggerResultSet.addColumn("namespace");
        getAllTriggerResultSet.addColumn("ret");
        getAllTriggerResultSet.addColumn("alltype");
        getAllTriggerResultSet.addColumn("argtype");
        getAllTriggerResultSet.addColumn("argname");
        getAllTriggerResultSet.addColumn("argmod");
        getAllTriggerResultSet.addColumn("secdef");
        getAllTriggerResultSet.addColumn("vola");
        getAllTriggerResultSet.addColumn("isstrict");
        getAllTriggerResultSet.addColumn("retset");

        ObjectParameter retTriggerParam = new ObjectParameter();
        retTriggerParam.setDataType("int");
        retTriggerParam.setName("a");
        retTriggerParam.setType(PARAMETERTYPE.IN);

        getAllTriggerResultSet.addRow(new Object[] { new Integer(1), "function1",
                new Integer(16), null, null, null, "mode", false, "volatile",
                false, false });

        preparedstatementHandler
                .prepareResultSet(CommonLLTUtilsHelper.GET_ALL_TRIGGER_QUERY,
                        getAllTriggerResultSet);
        
        MockResultSet nameSpaceResultSet = preparedstatementHandler.createResultSet();
        nameSpaceResultSet.addColumn("nspname");
        nameSpaceResultSet.addRow(new Object[] {"public"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.GET_NAMESPACE_QUERY, nameSpaceResultSet);

        
        MockResultSet backEndPIDResult = preparedstatementHandler
                .createResultSet();
        backEndPIDResult.addRow(new Integer[] { 300 });

        preparedstatementHandler.prepareResultSet(
                "SELECT pg_backend_pid();", backEndPIDResult);
        
        CommonLLTUtilsHelper.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtilsHelper.prepareValidateVersion(preparedstatementHandler);
        
        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addColumn("nspowner");
        namespaceRS.addColumn("nspacl");
        namespaceRS.addRow(new Object[]{1, "PUBLIC", 10, "NSPACL"});
        namespaceRS.addRow(new Object[]{1, "pg_catalog", 10, "NSPACL"});
        namespaceRS.addRow(new Object[]{2, "information_schema", 10, "NSPACL"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.GET_ALL_NAMESPACE, namespaceRS);
        
        MockResultSet namespaceRS1 = preparedstatementHandler.createResultSet();
        namespaceRS1.addColumn("oid");
        namespaceRS1.addColumn("nspname");
        namespaceRS1.addColumn("nspowner");
        namespaceRS1.addColumn("nspacl");
        namespaceRS1.addRow(new Object[]{1, "PUBLIC", 10, "NSPACL"});
        namespaceRS1.addRow(new Object[]{1, "pg_catalog", 10, "NSPACL"});
        namespaceRS1.addRow(new Object[]{2, "information_schema", 10, "NSPACL"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_PRIV, namespaceRS1);

        MockResultSet namespaceRS11 = preparedstatementHandler.createResultSet();
        namespaceRS11.addColumn("oid");
        namespaceRS11.addColumn("nspname");
        namespaceRS11.addColumn("nspowner");
        namespaceRS11.addColumn("nspacl");
        namespaceRS11.addRow(new Object[]{1, "PUBLIC", 10, "NSPACL"});
        namespaceRS11.addRow(new Object[]{1, "pg_catalog", 10, "NSPACL"});
        namespaceRS11.addRow(new Object[]{2, "information_schema", 10, "NSPACL"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_ALL, namespaceRS11);
      
    }
    
    public void prepareConnectionResultSetsDebugOffFailed()
    {
        MockConnection connection = getJDBCMockObjectFactory()
                .getMockConnection();

        PreparedStatementResultSetHandler preparedstatementHandler = connection
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

        MockResultSet isDebugResult = preparedstatementHandler.createResultSet();
        isDebugResult.addRow(new Boolean[] { true });

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_is_debug_on();", isDebugResult);

        MockResultSet debugOn = preparedstatementHandler.createResultSet();
        debugOn.addRow(new Boolean[] { true });
        preparedstatementHandler.prepareResultSet("SELECT pldbg_debug_on();",
                debugOn);
        
        MockResultSet debugOff = preparedstatementHandler.createResultSet();
        debugOff.addRow(new Boolean[] { });
        preparedstatementHandler.prepareResultSet("SELECT pldbg_debug_off();",
                debugOff);
        
        MockResultSet getAllFuncResultSet = preparedstatementHandler
                .createResultSet();

        getAllFuncResultSet.addColumn("oid");
        getAllFuncResultSet.addColumn("objname");
        getAllFuncResultSet.addColumn("namespace");
        getAllFuncResultSet.addColumn("ret");
        getAllFuncResultSet.addColumn("alltype");
        getAllFuncResultSet.addColumn("argtype");
        getAllFuncResultSet.addColumn("argname");
        getAllFuncResultSet.addColumn("argmod");
        getAllFuncResultSet.addColumn("secdef");
        getAllFuncResultSet.addColumn("vola");
        getAllFuncResultSet.addColumn("isstrict");
        getAllFuncResultSet.addColumn("retset");
        getAllFuncResultSet.addColumn("procost");
        getAllFuncResultSet.addColumn("setrows");


        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        getAllFuncResultSet.addRow(new Object[] { new Integer(1), "function1",
                new Integer(16), null, null, null, "mode", false, "volatile",
                false, false, 10, 10});

        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_FUNCTION_QUERY,getAllFuncResultSet);

        MockResultSet getAllTriggerResultSet = preparedstatementHandler
                .createResultSet();

        getAllTriggerResultSet.addColumn("oid");
        getAllTriggerResultSet.addColumn("objname");
        getAllTriggerResultSet.addColumn("namespace");
        getAllTriggerResultSet.addColumn("ret");
        getAllTriggerResultSet.addColumn("alltype");
        getAllTriggerResultSet.addColumn("argtype");
        getAllTriggerResultSet.addColumn("argname");
        getAllTriggerResultSet.addColumn("argmod");
        getAllTriggerResultSet.addColumn("secdef");
        getAllTriggerResultSet.addColumn("vola");
        getAllTriggerResultSet.addColumn("isstrict");
        getAllTriggerResultSet.addColumn("retset");

        ObjectParameter retTriggerParam = new ObjectParameter();
        retTriggerParam.setDataType("int");
        retTriggerParam.setName("a");
        retTriggerParam.setType(PARAMETERTYPE.IN);

        getAllTriggerResultSet.addRow(new Object[] { new Integer(1), "function1",
                new Integer(16), null, null, null, "mode", false, "volatile",
                false, false });

        preparedstatementHandler
                .prepareResultSet(CommonLLTUtilsHelper.GET_ALL_TRIGGER_QUERY,
                        getAllTriggerResultSet);
        MockResultSet nameSpaceResultSet = preparedstatementHandler.createResultSet();
        nameSpaceResultSet.addColumn("nspname");
        nameSpaceResultSet.addRow(new Object[] {"public"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.GET_NAMESPACE_QUERY, nameSpaceResultSet);

        CommonLLTUtilsHelper.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtilsHelper.prepareValidateVersion(preparedstatementHandler);
        
        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addColumn("nspowner");
        namespaceRS.addColumn("nspacl");
        namespaceRS.addRow(new Object[]{1, "PUBLIC", 10, "NSPACL"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.GET_ALL_NAMESPACE, namespaceRS);
        
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
        
        CommonLLTUtilsHelper.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtilsHelper.prepareValidateVersion(preparedstatementHandler);
    }
  
    /**
     * Scenario Id: TTA_CACHE_CONNECT_FAILED_002_FUNC_002
     * Title: Test debuggable server and the check setting debug on throws database critical exception 
     * Test case Id: test_TTA_CACHE_CONNECT_FAILED_002_FUNC_002_001
     */
    @Test
    public void test_TTA_CACHE_CONNECT_FAILED_002_FUNC_002_001()
    {
        prepareSetup();
    	Database database = connectionCache.getDbForProfileId(profileId);
        Executor executor = new Executor();
        try
        {
            SQLException sqlException = new SQLException(
                    "Throwing SQL exception intentionally.", "57PSQLException");

            preparedstatementHandler.prepareThrowsSQLException(
                    "SELECT pldbg_is_debug_on();", sqlException);
            
            //executor.connectToServer(connInfo, profileId);
            executor.connectToServer(connInfo, null);
            fail("Not expected to come here.");
        }
        catch (MPPDBIDEException e)
        {
            System.out.println("As expected.");
            try
            {
                connection.close();
            }
            catch (SQLException e1)
            {
            }
        }
        catch (Exception e)
        {
            System.out.println("As expected.");
        }
        finally
        {
        	connectionCache.destroyConnection(database);
            //connectionCache.destroyConnectionProfile(profileId);
            preparedstatementHandler.clearPreparedStatements();
            preparedstatementHandler.clearThrowsSQLException();
            
            connectionCache.closeAllNodes();
            
            Iterator<Server> itr = connectionCache.getServers().iterator();
            
            while(itr.hasNext())
            {
                connectionCache.removeServer(itr.next().getId());
            }
            
            
            connection = null;
            connectionCache = null;
            connInfo = null;
        }
    }
    
    /**
     * Scenario Id: TTA_CACHE_CONNECT_FAILED_002_FUNC_002
     * Title: Test debuggable server and the check setting debug on throws database critical exception 
     * Test case Id: test_TTA_CACHE_CONNECT_FAILED_002_FUNC_002_002
     */
    @Test
    public void test_TTA_CACHE_CONNECT_FAILED_002_FUNC_002_002()
    {
        prepareSetup();
    	Database database = connectionCache.getDbForProfileId(profileId);
        Executor executor = new Executor();
        try
        {
            SQLException sqlException = new SQLException(
                    "Throwing SQL exception intentionally.", "57PSQLException");

            preparedstatementHandler.prepareThrowsSQLException(
                    "SELECT pldbg_initialize_debug();", sqlException);
            
           // executor.connectToServer(connInfo, profileId);
            executor.connectToServer(connInfo, null);
            fail("Not expected to come here.");
        }
        catch (MPPDBIDEException e)
        {
            System.out.println("As expected.");
            try
            {
                connection.close();
            }
            catch (SQLException e1)
            {
            }
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
        finally
        {
        	connectionCache.destroyConnection(database);
            //connectionCache.destroyConnectionProfile(profileId);
            preparedstatementHandler.clearPreparedStatements();
            preparedstatementHandler.clearThrowsSQLException();
            
            connectionCache.closeAllNodes();
            
            Iterator<Server> itr = connectionCache.getServers().iterator();
            
            while(itr.hasNext())
            {
                connectionCache.removeServer(itr.next().getId());
            }
            
            
            connection = null;
            connectionCache = null;
            connInfo = null;
            try {         
                executor.checkIsCriticalExceptionOccurred();
            } catch (DatabaseOperationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Scenario Id: TTA_CACHE_CONNECT_FAILED_002_FUNC_002
     * Title: Test debuggable server and the check setting debug on throws database critical exception 
     * Test case Id: test_TTA_CACHE_CONNECT_FAILED_002_FUNC_002_003
     */
    @Test
    public void test_TTA_CACHE_CONNECT_FAILED_002_FUNC_002_003()
    {
        prepareSetup();
        Database database = connectionCache.getDbForProfileId(profileId);
        Executor executor = new Executor();
        try
        {
            SQLException sqlException = new SQLException(
                    "Throwing SQL exception intentionally.", "57PSQLException");

            preparedstatementHandler.prepareThrowsSQLException(
                    "SELECT pldbg_debug_on();", sqlException);
            
            //executor.connectToServer(connInfo, profileId);
            executor.connectToServer(connInfo, null);
            fail("Not expected to come here.");
        }
        catch (MPPDBIDEException e)
        {
            System.out.println("As expected.");
            try
            {
                connection.close();
            }
            catch (SQLException e1)
            {
            }
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
        finally
        {
        	connectionCache.destroyConnection(database);
           // connectionCache.destroyConnectionProfile(profileId);
            preparedstatementHandler.clearPreparedStatements();
            preparedstatementHandler.clearThrowsSQLException();
            
            connectionCache.closeAllNodes();
            
            Iterator<Server> itr = connectionCache.getServers().iterator();
            
            while(itr.hasNext())
            {
                connectionCache.removeServer(itr.next().getId());
            }
            
            
            connection = null;
            connectionCache = null;
            connInfo = null;
        }
    }
    
    /**
     * Scenario Id: TTA_CACHE_CONNECT_FAILED_002_FUNC_002
     * Title: Test debuggable server and the check setting debug on throws result set exception 
     * Test case Id: test_TTA_CACHE_CONNECT_FAILED_002_FUNC_002_004
     */
    @Test
    public void test_TTA_CACHE_CONNECT_FAILED_002_FUNC_002_004()
    {
        prepareSetup();
    	Database database = connectionCache.getDbForProfileId(profileId);
       
        Executor executor = new Executor();
        try
        {
            MockResultSet isDebugResult = preparedstatementHandler
                    .createResultSet();
            isDebugResult.addRow(new Boolean[] {  });

            preparedstatementHandler.prepareResultSet(
                    "SELECT pldbg_is_debug_on();", isDebugResult);
            
           // executor.connectToServer(connInfo, profileId);
            executor.connectToServer(connInfo, null);
            fail("Not expected to come here.");
        }
        catch (MPPDBIDEException e)
        {
            System.out.println("As expected.");
            try
            {
                connection.close();
            }
            catch (SQLException e1)
            {
            }
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
        finally
        {
        	connectionCache.destroyConnection(database);
           // connectionCache.destroyConnectionProfile(profileId);
            preparedstatementHandler.clearPreparedStatements();
            preparedstatementHandler.clearThrowsSQLException();
            
            connectionCache.closeAllNodes();
            
            Iterator<Server> itr = connectionCache.getServers().iterator();
            
            while(itr.hasNext())
            {
                connectionCache.removeServer(itr.next().getId());
            }
            
            
            connection = null;
            connectionCache = null;
            connInfo = null;
        }
    }
    
    /**
     * Scenario Id: TTA_CACHE_CONNECT_FAILED_002_FUNC_002
     * Title: Test debuggable server and the check setting debug on throws result set exception 
     * Test case Id: test_TTA_CACHE_CONNECT_FAILED_002_FUNC_002_005
     */
    @Test
    public void test_TTA_CACHE_CONNECT_FAILED_002_FUNC_002_005()
    {
        prepareSetup();
    	Database database = connectionCache.getDbForProfileId(profileId);
     
        Executor executor = new Executor();
        try
        {
            MockResultSet getSessionResult = preparedstatementHandler.createResultSet();
            getSessionResult.addRow(new String[] {  });

            preparedstatementHandler.prepareResultSet(
                    "SELECT pldbg_initialize_debug();", getSessionResult);
            
           // executor.connectToServer(connInfo, profileId);
            executor.connectToServer(connInfo, null);
            fail("Not expected to come here.");
        }
        catch (MPPDBIDEException e)
        {
            System.out.println("As expected.");
            try
            {
                connection.close();
            }
            catch (SQLException e1)
            {
            }
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
        finally
        {
        	connectionCache.destroyConnection(database);
           // connectionCache.destroyConnectionProfile(profileId);
            preparedstatementHandler.clearPreparedStatements();
            preparedstatementHandler.clearThrowsSQLException();
            
            connectionCache.closeAllNodes();
            
            Iterator<Server> itr = connectionCache.getServers().iterator();
            
            while(itr.hasNext())
            {
                connectionCache.removeServer(itr.next().getId());
            }
            
            
            connection = null;
            connectionCache = null;
            connInfo = null;
        }
    }
    
    /**
     * TOR ID : TTA_BL_DISCONNECT002
     * Test Case id : testTTA_BL_DISCONNECT002_FUNC_001_001
     * Description : disconnect and check if database critical exception thrown
     */
    @Test
    public void testTTA_BL_DISCONNECT002_FUNC_001_003()
    {
        prepareSetup();
        CommonLLTUtilsHelper.prepareProxyInfo(getJDBCMockObjectFactory().getMockConnection().getPreparedStatementResultSetHandler());

        prepareConnectionResultSets();

        CommonLLTUtilsHelper.prepareFunctionResultSet(getJDBCMockObjectFactory().getMockConnection());
        CommonLLTUtilsHelper.prepareTriggerResultSet(getJDBCMockObjectFactory().getMockConnection());
      
        ConnectionProfileId profileId = null;
        Database db= null;
        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        
       // serverInfo.setServerType(DATABASETYPE.GAUSS);
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);

        DBConnProfCache connectionCache = DBConnProfCache.getInstance();
       
        try
        {
            profileId = connectionCache.initConnectionProfile(serverInfo,status);
            
            //Executor executor = connectionCache.getconnectionprofile(profileId).getExecutor();
            Executor executor = (Executor) connectionCache.getDbForProfileId(profileId).getExecutor();
            //executor.connectToServer(serverInfo, profileId);
            executor.connectToServer(connInfo, null);
           // executor.refreshAllDebuggableObjects(profileId);
            
            TargetExecutor targetExecutor = new TargetExecutor();
            targetExecutor.connect(serverInfo, null);
           // targetExecutor.getAllFunctions(1);      
           // targetExecutor.getAllTriggers(1);
            
            /* test disconnect with existing connection */

            PreparedStatementResultSetHandler preparedstatementHandler = getJDBCMockObjectFactory().getMockConnection().getPreparedStatementResultSetHandler();
            CommonLLTUtilsHelper.datatypes(preparedstatementHandler);
            SQLException sqlException = new SQLException(
                    "Throwing SQL exception intentionally.", "57PSQLException");

            preparedstatementHandler.prepareThrowsSQLException(
                    "SELECT pldbg_debug_off();", sqlException);
            
            fail("Not expected to come here.");
        }
        catch (MPPDBIDEException e)
        {
            System.out.println("As expected.");
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
        finally {
            connectionCache.destroyConnection(db);
            // connectionCache.destroyConnectionProfile(profileId);
             
             connectionCache.closeAllNodes();
             
             Iterator<Server> itr = connectionCache.getServers().iterator();
             
             while(itr.hasNext())
             {
                 connectionCache.removeServer(itr.next().getId());
             }
             
             
             connection = null;
             connectionCache = null;
             connInfo = null;
        }

    }
    
    
    
    /**
     * Scenario Id: TTA_CACHE_CONNECT_FAILED_002_FUNC_002
     * Title: Test debuggable server and the check setting debug on throws result set exception 
     * Test case Id: test_TTA_CACHE_CONNECT_FAILED_002_FUNC_002_006
     */
    @Test
    public void test_TTA_CACHE_CONNECT_FAILED_002_FUNC_002_006()
    {
        prepareSetup();
    	Database database = connectionCache.getDbForProfileId(profileId);
       
        Executor executor = new Executor();
        try
        {
            MockResultSet isDebugResult = preparedstatementHandler
                    .createResultSet();
            isDebugResult.addRow(new Boolean[] {  });

            preparedstatementHandler.prepareResultSet(
                    "SELECT pldbg_debug_on();", isDebugResult);
            
            //executor.connectToServer(connInfo, profileId);
            executor.connectToServer(connInfo, null);
            fail("Not expected to come here.");
        }
        catch (MPPDBIDEException e)
        {
            System.out.println("As expected.");
            try
            {
                connection.close();
            }
            catch (SQLException e1)
            {
            }
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
        finally
        {
        	connectionCache.destroyConnection(database);
            //connectionCache.destroyConnectionProfile(profileId);
            preparedstatementHandler.clearPreparedStatements();
            preparedstatementHandler.clearThrowsSQLException();
            
            connectionCache.closeAllNodes();
            
            Iterator<Server> itr = connectionCache.getServers().iterator();
            
            while(itr.hasNext())
            {
                connectionCache.removeServer(itr.next().getId());
            }
            
            
            connection = null;
            connectionCache = null;
            connInfo = null;
        }
    }
    
    /**
     * TOR ID : TTA_BL_DISCONNECT002
     * Test Case id : testTTA_BL_DISCONNECT002_FUNC_001_001
     * Description : disconnect and check if resultset exception thrown
     */
    @Test
    public void testTTA_BL_DISCONNECT002_FUNC_001_002()
    {
        prepareSetup();
        CommonLLTUtilsHelper.prepareProxyInfo(getJDBCMockObjectFactory().getMockConnection().getPreparedStatementResultSetHandler());
       
        prepareConnectionResultSets();

        CommonLLTUtilsHelper.prepareFunctionResultSet(getJDBCMockObjectFactory().getMockConnection());
        CommonLLTUtilsHelper.prepareTriggerResultSet(getJDBCMockObjectFactory().getMockConnection());
      
        ConnectionProfileId profileId = null;
        Database db= null;
        JobCancelStatus status=new JobCancelStatus();
        status.setCancel(false);
        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        
       // serverInfo.setServerType(DATABASETYPE.GAUSS);
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);

        DBConnProfCache connectionCache = DBConnProfCache.getInstance();
        CommonLLTUtilsHelper.datatypes(getJDBCMockObjectFactory().getMockConnection().getPreparedStatementResultSetHandler());
        try
        {
            profileId = connectionCache.initConnectionProfile(serverInfo,status);
            
            //Executor executor = connectionCache.getconnectionprofile(profileId).getExecutor();
            Executor executor =  (Executor) connectionCache.getDbForProfileId(profileId).getExecutor();
           // executor.connectToServer(serverInfo, profileId);
            //executor.connectToDebugServer(serverInfo, profileId, null);
            executor.connectToServer(connInfo, null);
           // executor.refreshAllDebuggableObjects(profileId);
            TargetExecutor targetExecutor = new TargetExecutor();
            targetExecutor.connect(serverInfo, null);
            //targetExecutor.getAllFunctions(1);      
            //targetExecutor.getAllTriggers();
            
            /* test disconnect with existing connection */

            fail("Not expected to come here.");
        }
        catch (MPPDBIDEException e)
        {
            System.out.println("As expected.");
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
        finally {
            connectionCache.destroyConnection(db);
            // connectionCache.destroyConnectionProfile(profileId);
             
             connectionCache.closeAllNodes();
             
             Iterator<Server> itr = connectionCache.getServers().iterator();
             
             while(itr.hasNext())
             {
                 connectionCache.removeServer(itr.next().getId());
             }
             
             
             connection = null;
             connectionCache = null;
             connInfo = null;
        }

    }

}
