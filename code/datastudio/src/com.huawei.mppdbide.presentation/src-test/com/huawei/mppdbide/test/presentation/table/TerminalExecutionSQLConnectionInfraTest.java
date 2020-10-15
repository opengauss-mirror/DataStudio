package com.huawei.mppdbide.test.presentation.table;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.contentassist.ContentAssistProcesserData;
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
import com.huawei.mppdbide.mock.presentation.CommonLLTUtils;
import com.huawei.mppdbide.presentation.IUIWorkerJobNotifier;
import com.huawei.mppdbide.presentation.TerminalExecutionSQLConnectionInfra;
import com.huawei.mppdbide.presentation.contentassistprocesser.ContentAssistProcesserCore;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import static org.junit.Assert.*;

public class TerminalExecutionSQLConnectionInfraTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    Database                          database;
    TerminalExecutionSQLConnectionInfra connInfra = null;
    private boolean notifyCancel;
    private boolean notifiedFlag;
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
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        connection = new MockConnection();
        // test for logging
        MPPDBIDELoggerUtility
                .setArgs(new String[] {"-logfolder=.", "-detailLogging=true"});

        // MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
        database = connProfCache.getDbForProfileId(profileId);  
        connInfra = new TerminalExecutionSQLConnectionInfra();
        connInfra.setDatabase(database);
        notifyCancel = false;
        notifiedFlag = false;

    }
    
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
    public void testTTA_getAndRelease_001()
    {
        try
        {
            connInfra.setConnection(database.getConnectionManager().getFreeConnection());
        }
        catch (MPPDBIDEException e)
        {
            fail("fail");
        }
        DBConnection conn1 = connInfra.getSecureConnection(null);
        assertNotNull(conn1);
        assertTrue(connInfra.isConnectionBusy());
        connInfra.releaseSecureConnection(conn1);
        assertFalse(connInfra.isConnectionBusy());        
    }
    
    @Test
    public void testTTA_getAndRelease_002()
    {
        connInfra.setConnection(null);
        assertFalse(notifyCancel);
        DBConnection conn1 = connInfra.getSecureConnection(new IUIWorkerJobNotifier()
        {
            
            @Override
            public void setNotified(boolean notify)
            {
                
                
            }
            
            @Override
            public void setCancelled(boolean cancel)
            {
                notifyCancel = true;              
            }
        });
        assertTrue(notifyCancel);
        assertNull(conn1);      
    }

    @Test
    public void testTTA_getAndRelease_003()
    {
        connInfra.setReuseConnectionFlag(false);
        DBConnection conn1 = connInfra.getSecureConnection(null);
        assertNotNull(conn1);
        assertFalse(connInfra.isConnectionBusy());
        connInfra.releaseSecureConnection(conn1);
        assertFalse(connInfra.isConnectionBusy());  
    }
    
    @Test
    public void testTTA_getAndRelease_004()
    {
        try
        {
            connInfra.setConnection(database.getConnectionManager().getFreeConnection());
        }
        catch (MPPDBIDEException e)
        {
            fail("fail");
        }
        connInfra.setReuseConnectionFlag(true);
        DBConnection conn1 = connInfra.getSecureConnection(new IUIWorkerJobNotifier()
        {
            
            @Override
            public void setNotified(boolean notify)
            {
                
                
            }
            
            @Override
            public void setCancelled(boolean cancel)
            {
             
            }
        });

        DBConnection conn2 = connInfra.getSecureConnection(new IUIWorkerJobNotifier()
        {
            
            @Override
            public void setNotified(boolean notify)
            {
                notifiedFlag = true;
                
            }
            
            @Override
            public void setCancelled(boolean cancel)
            {
             
            }
        });
        assertNotNull(conn1);
        assertNull(conn2);
        assertTrue(connInfra.isConnectionBusy());
        assertFalse(notifiedFlag);
        connInfra.releaseSecureConnection(conn1);
        assertTrue(notifiedFlag);
    }
    
    @Test
    public void testTTA_notifyWaitingJobs_tests()
    {
        try
        {
            connInfra.setConnection(database.getConnectionManager().getFreeConnection());
        }
        catch (MPPDBIDEException e)
        {
            fail("fail");
        }
        connInfra.setReuseConnectionFlag(true);
        notifiedFlag = false;
        DBConnection conn1 = connInfra.getSecureConnection(new IUIWorkerJobNotifier()
        {
            
            @Override
            public void setNotified(boolean notify)
            {
                
                
            }
            
            @Override
            public void setCancelled(boolean cancel)
            {
             
            }
        });

        DBConnection conn2 = connInfra.getSecureConnection(new IUIWorkerJobNotifier()
        {
            
            @Override
            public void setNotified(boolean notify)
            {
                notifiedFlag = true;
                
            }
            
            @Override
            public void setCancelled(boolean cancel)
            {
             
            }
        });
        assertNotNull(conn1);  
        assertNull(conn2);
        connInfra.notifyAllWaitingJobs();
        assertTrue(notifiedFlag);
        
    }
    
    @Test
    public void testTTA_cancelWaitingJobs_tests()
    {
        try
        {
            connInfra.setConnection(database.getConnectionManager().getFreeConnection());
        }
        catch (MPPDBIDEException e)
        {
            fail("fail");
        }
        connInfra.setReuseConnectionFlag(true);
        notifyCancel = false;

        DBConnection conn1 = connInfra.getSecureConnection(new IUIWorkerJobNotifier()
        {
            
            @Override
            public void setNotified(boolean notify)
            {
                
                
            }
            
            @Override
            public void setCancelled(boolean cancel)
            {
             
            }
        });

        DBConnection conn2 = connInfra.getSecureConnection(new IUIWorkerJobNotifier()
        {
            
            @Override
            public void setNotified(boolean notify)
            {
                
                
            }
            
            @Override
            public void setCancelled(boolean cancel)
            {
                notifyCancel = true;
            }
        });
        assertNotNull(conn1);  
        assertNull(conn2);
        connInfra.cancelAllWaitingJobs();
        assertTrue(notifyCancel);
        
    }
    
    @Test
    public void testTTA_database_sanity_test()
    {
        assertTrue(connInfra.isDatabaseValid());
        String name = null;
        try
        {
            name = connInfra.getDatabaseName();
        }
        catch (MPPDBIDEException e)
        {
            fail("fail");
        }
        assertNotNull(name);
        connInfra.setDatabase(null);
        try
        {
            name = connInfra.getDatabaseName();
        }
        catch (MPPDBIDEException e)
        {
            assertTrue(true);
        }
        
    }

}
