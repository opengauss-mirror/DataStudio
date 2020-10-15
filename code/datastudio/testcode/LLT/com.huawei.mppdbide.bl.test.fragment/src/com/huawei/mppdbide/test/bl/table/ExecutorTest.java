package com.huawei.mppdbide.test.bl.table;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.bl.executor.Executor;
import com.huawei.mppdbide.bl.executor.TargetExecutor;
import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.mock.bl.CommonLLTUtils;
import com.huawei.mppdbide.mock.bl.ExceptionConnection;
import com.huawei.mppdbide.mock.bl.MockBLPreferenceImpl;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.UnknownException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class ExecutorTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection               = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler         statementHandler         = null;
    
    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache connProfCache = null;
    ConnectionProfileId profileId = null;
    Executor                          executor                 = null;
    ServerConnectionInfo serverInfo								= null;
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

        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();
        
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        
        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status=new JobCancelStatus();
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
        //database.getServer().close();
        
        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();
        connProfCache.closeAllNodes();
        
        Iterator<Server> itr = connProfCache.getServers().iterator();
        
        while(itr.hasNext())
        {
            connProfCache.removeServer(itr.next().getId());
        }
        
        connProfCache.closeAllNodes();
        
    }
    
   
    
   
    //TODO
    /*@Test
    public void testTTA_BL_EXECUTOR_FUNC_001_003()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            DebugObjects debugObject = database.getDebugObjectById(1, 1);
            
            debugObject.setExecuteTemplate("SELECT pg_catalog.function2(2, 3)");
            debugObject.generateExecutionTemplate();
            
            CommonLLTUtils.executeExecutor(statementHandler);
            
            database.getExecutor().execute("SELECT pg_catalog.function2(1, 1)", profileId);
            //database.getExecutor().isTriggerQueryValid("SELECT pg_catalog.function2(1, 1)");
            
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
    }*/
   
    @Test
    public void testTTA_BL_EXECUTOR_FUNC_001_006()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setThrowExceptioForStmt(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowoutofmemerrorinrs(true);
            exceptionConnection.setThrowExceptionCloseResultSet(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);
            
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            
            
            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(null,null);
            fail("not expected to come here");
        }
        catch(UnknownException e)
        {
            assertTrue(true);
        }
        catch(Exception e)
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
    public void testTTA_BL_EXECUTOR_FUNC_001_007()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setThrowExceptioForStmt(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowoutofmemerrorinrs(true);
            exceptionConnection.setThrowExceptionCloseResultSet(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);
            
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            
            
            Executor executor = (Executor) database.getExecutor();
            executor.connectToServer(null,null);
            fail("not expected to come here");
        }
        catch(UnknownException e)
        {
            assertTrue(true);
        }
        catch(Exception e)
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
    
   
}
