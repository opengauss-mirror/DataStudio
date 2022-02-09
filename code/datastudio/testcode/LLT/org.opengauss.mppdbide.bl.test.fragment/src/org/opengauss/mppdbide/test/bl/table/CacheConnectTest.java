package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.driver.Gauss200V1R7Driver;
import org.opengauss.mppdbide.bl.executor.Executor;
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
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class CacheConnectTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection               = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    
    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    DBConnProfCache connProfCache = null;
    ConnectionProfileId profileId = null;
    ServerConnectionInfo serverInfo = null;
    JobCancelStatus status=null;
    Gauss200V1R7Driver mockDriver = null;
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
        MPPDBIDELoggerUtility.setArgs(null);
        connection = new MockConnection();
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        mockDriver = CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        IBLPreference sysPref=new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        
        connProfCache = DBConnProfCache.getInstance();
         status=new JobCancelStatus();
        status.setCancel(false);
        
        serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        //serverInfo.setSslPassword("12345");
        //serverInfo.setServerType(DATABASETYPE.GAUSS);
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
        connProfCache.closeAllNodes();
        
        Iterator<Server> itr = connProfCache.getServers().iterator();
        
        while(itr.hasNext())
        {
            connProfCache.removeServer(itr.next().getId());
        }
        
        connProfCache.closeAllNodes();
        
    }
    
    /**
     * Scenario Id: TTA_CACHE_CONNECT002_FUNC_005
     * Title: Test debuggable server
     * Test case Id: test_TTA_CACHE_CONNECT002_FUNC_005_02
     */
    @Test
    public void test_TTA_CACHE_CONNECT002_FUNC_005_02()
    {


        PreparedStatementResultSetHandler preparedstatementHandler = connection
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
        
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        Executor executor = new Executor();
        try
        {
            executor.connectToServer(serverInfo,mockDriver);

            connection.close();
        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
            fail("ExecutorTest failed. Connect to server throws exception for valid parameters.");
            try
            {
                connection.close();
            }
            catch (SQLException e1)
            {
            }
        }
        catch (SQLException e)
        {
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");
        }
        finally
        {
        	connProfCache.destroyConnection(database);
        }

    }
}
