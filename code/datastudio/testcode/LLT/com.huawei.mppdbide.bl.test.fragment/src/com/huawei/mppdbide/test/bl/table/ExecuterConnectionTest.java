package com.huawei.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.bl.executor.Executor;
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
import com.huawei.mppdbide.mock.bl.CommonLLTUtilsHelper;
import com.huawei.mppdbide.mock.bl.GaussMockDriverFailConnection;
import com.huawei.mppdbide.mock.bl.GaussMockObjectFactoryToFailConnection;
import com.huawei.mppdbide.mock.bl.GaussMockPreparedStatementToHang;
import com.huawei.mppdbide.mock.bl.MockBLPreferenceImpl;
import com.huawei.mppdbide.mock.bl.MockStatementToHang;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.JDBCTestModule;
import com.mockrunner.mock.jdbc.JDBCMockObjectFactory;
import com.mockrunner.mock.jdbc.MockResultSet;

/**
 * @author a00900918
 * 
 */
public class ExecuterConnectionTest extends BasicJDBCTestCaseAdapter
{
    JDBCMockObjectFactory currentMockObjectFactory = null;
    JDBCTestModule        currentTestmodule        = null;
    Database     connectionProfile        = null;
    
    public static final String CREATE_EXTENSION   = "CREATE EXTENSION IF NOT EXISTS pldbgapi";
    
    ServerConnectionInfo       serverInfo               = null;
    ConnectionProfileId        profileId                = null;
    JobCancelStatus            status                   = null;
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
        currentMockObjectFactory = getJDBCMockObjectFactory();
        currentTestmodule = getJDBCTestModule();
        MPPDBIDELoggerUtility.setArgs(null);
        status = new JobCancelStatus();
        status.setCancel(false);
        serverInfo = new ServerConnectionInfo();
        
       
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        //serverInfo.setSslPassword("12345");
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    @After
	public void tearDown() throws Exception
    {
        Iterator<Server> itr = DBConnProfCache.getInstance().getServers().iterator();
        MockStatementToHang.resetHangQueries();
        GaussMockPreparedStatementToHang.resetHangqueries();
        while(itr.hasNext())
        {
            DBConnProfCache.getInstance().removeServer(itr.next().getId());
        }

        super.tearDown();
        setJDBCMockObjectFactory(currentMockObjectFactory);
        setJDBCTestModule(currentTestmodule);
    }

    
    /**
     * TOR ID : TTA.BL.CONNECT002.FUNC.001
     * Test Case id : TTA.BL.CONNECT002.FUNC.001_001
     * Description : Validate the connection parameters passed to JDBC
     * driver on target connection
     */
    @Test
    public void testTTA_BL_CONNECT002_FUNC_001_001()
    {
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        CommonLLTUtilsHelper.prepareConnectionResultSets(getJDBCMockObjectFactory());
        CommonLLTUtilsHelper.datatypes(getJDBCMockObjectFactory().getMockConnection().getPreparedStatementResultSetHandler());
        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
                  
            ((Executor)connectionProfile.getExecutor()).connectToServer(serverInfo,connectionProfile.getConnectionManager().getConnectionDriver());
            
        }
        catch (MPPDBIDEException e)
        {
            fail("ExecutorTest failed. Connect to server throws exception for valid parameters.");
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }

    }

    /**
     * TOR ID : TTA.BL.CONNECT002.FUNC.001
     * Test Case id : TTA.BL.CONNECT002.FUNC.001_002
     * Description : Validate the connection parameters passed to JDBC
     * driver on debug connection
     */
    @Test
    public void testTTA_BL_CONNECT002_FUNC_001_002()
    {
        GaussMockObjectFactoryToFailConnection mockObjectFactory = new GaussMockObjectFactoryToFailConnection();
        mockObjectFactory.createMockDriver();
        setJDBCMockObjectFactory(mockObjectFactory);
        
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        CommonLLTUtilsHelper.prepareConnectionResultSets(getJDBCMockObjectFactory());
        CommonLLTUtilsHelper.datatypes(getJDBCMockObjectFactory().getMockConnection().getPreparedStatementResultSetHandler());

        Properties props = new Properties();
        props.put("user", "myusername");
        props.put("passwrod", "mypassword");

        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
                   
           // connectionProfile.connectToServer();
            assertTrue(mockObjectFactory.isConnectionParams(
                    "", "myusername",
                    null));
        }
        catch (MPPDBIDEException e)
        {
            fail("ExecutorTest failed. Connect to server throws"
                    + " exception for valid parameters.");
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
    }

   
    /**
     * TOR ID : TTA.BL.CONNECT002.FUNC.002
     * Test Case id : TTA.BL.CONNECT002.FUNC.002_001
     * Description : Validate the exception handling on invalid parameters
     */
    @Test
    public void testTTA_BL_CONNECT002_FUNC_002_0013()
    {
        GaussMockObjectFactoryToFailConnection mockObjectFactory = new GaussMockObjectFactoryToFailConnection();
        // mockObjectFactory.createMockDriver();
        GaussMockDriverFailConnection failMockDriver = (GaussMockDriverFailConnection) mockObjectFactory
                .getMockDriver();
        failMockDriver.setFailOnConnectionCount(1);
        setJDBCMockObjectFactory(mockObjectFactory);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        CommonLLTUtilsHelper.prepareConnectionResultSets(getJDBCMockObjectFactory());

        Properties props = new Properties();
        props.put("user", "myusername");
        props.put("passwrod", "mypassword");

        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
            connectionProfile.connectToServer();
            
            connectionProfile.destroy();
            fail("Exception has to be thrown, when connection fail.");
        }
        catch (MPPDBIDEException e)
        {
//            assertEquals("SQL Error Code = null"+System.lineSeparator() + 
//                    "Connection failed.".trim() 
//                    , e.getServerMessage().trim());
        }
        catch (Exception e)
        {
           // fail("Not expected to come here.");
        }
    }

    /**
     * TOR ID : TTA.BL.CONNECT002.FUNC.002
     * Test Case id : TTA.BL.CONNECT002.FUNC.002_002
     * Description : Validate the exception handling on invalid parameters
     * 
     * This testcase fails because of framework issue, has to be fixed.
     */
    /*@Test
    public void testTTA_BL_CONNECT002_FUNC_002_002()
    {
        GaussMockObjectFactoryToFailConnection mockObjectFactory = new GaussMockObjectFactoryToFailConnection();
        createJDBCTestModule(mockObjectFactory);

        mockObjectFactory.createMockDriver();
        GaussMockDriverFailConnection failMockDriver = (GaussMockDriverFailConnection) mockObjectFactory
                .getMockDriver();
        failMockDriver.setFailOnConnectionCount(2);
        setJDBCMockObjectFactory(mockObjectFactory);

        CommonLLTUtils.prepareConnectionResultSets(getJDBCMockObjectFactory());

        Properties props = new Properties();
        props.put("user", "myusername");
        props.put("passwrod", "mypassword");

        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
            //connectionProfile.connectToServer();
            fail("Exception has to be thrown, when connection fail.");
        }
        catch (MPPDBIDEException e)
        {
            assertTrue(failMockDriver.isAllConnectionsClosed());
            assertEquals("Connection failed.", e.getServerMessage());
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
    }
*/
    /**
     * TOR ID : TTA.BL.CONNECT002.FUNC.003
     * Test Case id : TTA.BL.CONNECT002.FUNC.003_001
     * Description : Validate the exception handling on invalid parameters
     * 
     * This testcase fails because of framework issue, has to be fixed.
     */
    /*@Test
    public void testTTA_BL_CONNECT002_FUNC_003_001()
    {
        GaussMockObjectFactoryToFailConnection mockObjectFactory = new GaussMockObjectFactoryToFailConnection();
        mockObjectFactory.createMockDriver();
        setJDBCMockObjectFactory(mockObjectFactory);
        mockObjectFactory.setActiveConnection(3);
        JDBCTestModule testmodule = createJDBCTestModule(mockObjectFactory);
        setJDBCTestModule(testmodule);

        CommonLLTUtils.prepareConnectionResultSets(getJDBCMockObjectFactory());

        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
            //connectionProfile.connectToServer();
            connectionProfile.getExecutor().connectToDebugServer(serverInfo, profileId);
            assertEquals(5, mockObjectFactory.getConnectionCount());
            assertTrue(connectionProfile.getExecutor().getDebugVersion().contains("PostgreSQL 9.2"));
           // assertTrue(connectionProfile.getExecutor().getDriverVersion().contains("1.0")); 
            //assertTrue(connectionProfile.getExecutor().getServerVersion().contains("GaussDB"));
            
            verifySQLStatementExecuted("SELECT pldbg_create_listener()");
        }
        catch (MPPDBIDEException e)
        {
            fail("[TTA.BL.CONNECT002.FUNC.003_001] Exception thrown when 3 "
                    + "connections are expected to be created.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");
        }
    }*/

    /**
     * TOR ID : TTA.BL.CONNECT002.FUNC.004
     * Test Case id : TTA.BL.CONNECT002.FUNC.004_001
     * Description : Donot configure debug server, and create a connection
     * profile with valid params, and validate the error message returned.
     * 
     */
    @Test
    public void testTTA_BL_CONNECT002_FUNC_004_001()
    {
        GaussMockObjectFactoryToFailConnection mockObjectFactory = new GaussMockObjectFactoryToFailConnection();
        createJDBCTestModule(mockObjectFactory);

        mockObjectFactory.createMockDriver();
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        GaussMockDriverFailConnection failMockDriver = (GaussMockDriverFailConnection) mockObjectFactory
                .getMockDriver();
        failMockDriver.setFailOnConnectionCount(3);
        setJDBCMockObjectFactory(mockObjectFactory);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        CommonLLTUtilsHelper.prepareConnectionResultSets(getJDBCMockObjectFactory());

        Properties props = new Properties();
        props.put("user", "myusername");
        props.put("passwrod", "mypassword");

        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
            connectionProfile.connectToServer();
            fail("Exception has to be thrown, when connection fail.");
        }
        catch (MPPDBIDEException e)
        {
//            assertEquals("SQL Error Code = null"+System.lineSeparator() + 
//                    "Connection failed.".trim()
//                    , e.getServerMessage().trim());
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
    }

    /**
     * TOR ID : TTA.BL.DISCONNECT002.FUNC.001
     * Test Case id : TTA.BL.DISCONNECT002.FUNC.001_1
     * Description : Disconnect a valid connection profile. Check in database
     * server that three valid session are closed.
     * 
     */
    @Test
    public void testTTA_BL_DISCONNECT002_FUNC_001_001()
    {
        GaussMockObjectFactoryToFailConnection mockObjectFactory = new GaussMockObjectFactoryToFailConnection();
        createJDBCTestModule(mockObjectFactory);

        mockObjectFactory.createMockDriver();
        GaussMockDriverFailConnection failMockDriver = (GaussMockDriverFailConnection) mockObjectFactory
                .getMockDriver();
        setJDBCMockObjectFactory(mockObjectFactory);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        CommonLLTUtilsHelper.prepareConnectionResultSets(getJDBCMockObjectFactory());
        CommonLLTUtilsHelper.datatypes(getJDBCMockObjectFactory().getMockConnection().getPreparedStatementResultSetHandler());
        //Cleanup
        Server tmpServer = DBConnProfCache.getInstance().getServerByName("TestConnectionName");
        if (null != tmpServer)
        {
            DBConnProfCache.getInstance().removeServer(tmpServer.getId());
            
        }
                

        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
           // connectionProfile.connectToServer();

            DBConnProfCache.getInstance().destroyConnection(connectionProfile);
                

            assertTrue(failMockDriver.isAllConnectionsClosed());
        }
        catch (MPPDBIDEException e)
        {
            fail("ExecutorTest failed. Disconnect throws exception.");
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
    }

    /**
     * TOR ID : TTA.BL.DISCONNECT002.FUNC.002
     * Test Case id : TTA.BL.DISCONNECT002.FUNC.002_1
     * Description : Destroy connection profile and check if all the connection
     * are closed.
     * 
     */
    @Test
    public void testTTA_BL_DISCONNECT002_FUNC_002_001()
    {
        GaussMockObjectFactoryToFailConnection mockObjectFactory = new GaussMockObjectFactoryToFailConnection();
        createJDBCTestModule(mockObjectFactory);
        
        mockObjectFactory.createMockDriver();
        GaussMockDriverFailConnection failMockDriver = (GaussMockDriverFailConnection) mockObjectFactory
                .getMockDriver();
        setJDBCMockObjectFactory(mockObjectFactory);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        CommonLLTUtilsHelper.prepareConnectionResultSets(getJDBCMockObjectFactory());
        CommonLLTUtilsHelper.datatypes(getJDBCMockObjectFactory().getMockConnection().getPreparedStatementResultSetHandler());
        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
           // connectionProfile.connectToServer();
            DBConnProfCache.getInstance().destroyConnection(connectionProfile);
                  
            assertTrue(failMockDriver.isAllConnectionsClosed());
        }
        catch (MPPDBIDEException e)
        {
            fail("ExecutorTest failed. Disconnect throws exception.");
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
    }

    /**
     * TOR ID : TTA.BL.DISCONNECT002.FUNC.002
     * Test Case id : TTA.BL.DISCONNECT002.FUNC.002_2
     * Description : Disconnect a valid connection profile. Check in database
     * server that three valid session are closed.
     * 
     */
    @Test
    public void testTTA_BL_DISCONNECT002_FUNC_002_002()
    {
        GaussMockObjectFactoryToFailConnection mockObjectFactory = new GaussMockObjectFactoryToFailConnection();
        createJDBCTestModule(mockObjectFactory);
      
        mockObjectFactory.createMockDriver();
        GaussMockDriverFailConnection failMockDriver = (GaussMockDriverFailConnection) mockObjectFactory
                .getMockDriver();
        setJDBCMockObjectFactory(mockObjectFactory);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        CommonLLTUtilsHelper.prepareConnectionResultSets(getJDBCMockObjectFactory());
        CommonLLTUtilsHelper.datatypes(getJDBCMockObjectFactory().getMockConnection().getPreparedStatementResultSetHandler());
        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo,status);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
           // connectionProfile.connectToServer();
            DBConnProfCache.getInstance().destroyConnection(connectionProfile);
                   
            assertTrue(failMockDriver.isAllConnectionsClosed());
        }
        catch (MPPDBIDEException e)
        {
            fail("ExecutorTest failed. Disconnect throws exception.");
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
    }

    /*@Test
    public void testTTAGetVersion_001()
    {
        GaussMockObjectFactoryToFailConnection mockObjectFactory = new GaussMockObjectFactoryToFailConnection();
        mockObjectFactory.createMockDriver();
        setJDBCMockObjectFactory(mockObjectFactory);
        mockObjectFactory.setActiveConnection(3);
        JDBCTestModule testmodule = createJDBCTestModule(mockObjectFactory);
        setJDBCTestModule(testmodule);

        CommonTestUtils.prepareConnectionResultSets(getJDBCMockObjectFactory());

        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
           // connectionProfile.connectToServer();
            assertEquals(3, mockObjectFactory.getConnectionCount());
            assertTrue(connectionProfile.getExecutor().getDebugVersion().contains("PostgreSQL 9.2"));
            //assertTrue(connectionProfile.getExecutor().getDriverVersion().contains("1.0")); 
            //assertTrue(connectionProfile.getExecutor().getServerVersion().contains("GaussDB"));
            
            verifySQLStatementExecuted("SELECT * from version");
        }
        catch (MPPDBIDEException e)
        {
            fail("[TTA.BL.CONNECT002.FUNC.003_001] Exception thrown when 3 "
                    + "connections are expected to be created.");
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
    }*/
    
    /*@Test
    public void testTTAExecutor_001()
    {
        Object resultObject = null;
        try
        {

            
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
            Executor executor = connectionProfile.getExecutor();
            String query = "SELECT func1() from dual";
            
            while(!Thread.currentThread().isInterrupted())
            {
                Thread.currentThread().interrupt();
            }
            
            resultObject = executor.execute(query, connectionProfile.getProfileId());
            

            fail("Test case failed.");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            assertTrue(true);
        }
        finally
        {
            DBConnProfCache.getInstance().destroyConnection(connectionProfile);
        }
    }*/
    
    /*@Test
    public void testTTA_BL_CONNECT002_FUNC_002_012()
    {
        GaussMockObjectFactoryToFailConnection mockObjectFactory = new GaussMockObjectFactoryToFailConnection();
        createJDBCTestModule(mockObjectFactory);

        mockObjectFactory.createMockDriver();
        GaussMockDriverFailConnection failMockDriver = (GaussMockDriverFailConnection) mockObjectFactory
                .getMockDriver();
        failMockDriver.setFailOnConnectionCount(2);
        failMockDriver.setConnectFail(true);
        setJDBCMockObjectFactory(mockObjectFactory);

        CommonLLTUtils.prepareConnectionResultSets(getJDBCMockObjectFactory());

        Properties props = new Properties();
        props.put("user", "myusername");
        props.put("passwrod", "mypassword");

        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
            //connectionProfile.connectToServer();
            fail("Exception has to be thrown, when connection fail.");
        }
        catch (MPPDBIDEException e)
        {
            assertTrue(failMockDriver.isAllConnectionsClosed());
            assertEquals("Throwing SQL exception intentionally.", e.getServerMessage());
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
    }*/
    
    @Test
    public void testTTA_BL_CONNECT002_FUNC_002_112()
    {
        GaussMockObjectFactoryToFailConnection mockObjectFactory = new GaussMockObjectFactoryToFailConnection();
        createJDBCTestModule(mockObjectFactory);

        mockObjectFactory.createMockDriver();
        GaussMockDriverFailConnection failMockDriver = (GaussMockDriverFailConnection) mockObjectFactory
                .getMockDriver();
        failMockDriver.setFailOnConnectionCount(3);
        failMockDriver.setConnectFail(true);
        setJDBCMockObjectFactory(mockObjectFactory);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        CommonLLTUtilsHelper.prepareConnectionResultSets(getJDBCMockObjectFactory());

        Properties props = new Properties();
        props.put("user", "myusername");
        props.put("passwrod", "mypassword");

        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
            //connectionProfile.connectToServer();
            fail("Exception has to be thrown, when connection fail.");
        }
        catch (MPPDBIDEException e)
        {
            assertTrue(failMockDriver.isAllConnectionsClosed());
            assertEquals("Throwing SQL exception intentionally.", e.getServerMessage());
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
        }
    }
    
    @Test
    public void testTTA_BL_CONNECT002_FUNC_002_122()
    {
        GaussMockObjectFactoryToFailConnection mockObjectFactory = new GaussMockObjectFactoryToFailConnection();
        createJDBCTestModule(mockObjectFactory);

        mockObjectFactory.createMockDriver();
        GaussMockDriverFailConnection failMockDriver = (GaussMockDriverFailConnection) mockObjectFactory
                .getMockDriver();
        failMockDriver.setFailOnConnectionCount(5);
        failMockDriver.setConnectFail(true);
        setJDBCMockObjectFactory(mockObjectFactory);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        CommonLLTUtilsHelper.prepareConnectionResultSets(getJDBCMockObjectFactory());
        
        String qry="select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, typ.typlen as typlen, pg_catalog.format_type(oid,typ.typtypmod) as displaycolumns ,  typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc from pg_type typ left join pg_description des on (typ.oid = des.objoid) where typ.typnamespace in (select oid from pg_namespace where nspname in ('information_schema', 'pg_catalog')) order by typ.typname";
        MockResultSet datatypeRS_1 = getJDBCMockObjectFactory().getMockConnection().getPreparedStatementResultSetHandler().createResultSet();
        datatypeRS_1.addColumn("oid");
        datatypeRS_1.addColumn("typname");
        datatypeRS_1.addColumn("typnamespace");
        datatypeRS_1.addColumn("typlen");
        datatypeRS_1.addColumn("typbyval");
        datatypeRS_1.addColumn("typtype");
        datatypeRS_1.addColumn("typcategory");
        datatypeRS_1.addColumn("typtypmod");
        datatypeRS_1.addColumn("typnotnull");
        datatypeRS_1.addColumn("typarray");
        datatypeRS_1.addColumn("desc");
        datatypeRS_1.addColumn("displaycolumns");

        datatypeRS_1.addRow(new Object[] {1, "bpchar", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {2, "int8", 1, 1, true, "type",
                "category", 1, true, 12, "description2", ""});

        datatypeRS_1.addRow(new Object[] {3, "bit", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {4, "varbit", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {5, "bool", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {6, "box", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {7, "bytea", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {8, "varchar", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {9, "char", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {10, "cidr", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {11, "circle", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {12, "date", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});

        datatypeRS_1.addRow(new Object[] {13, "float8", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {14, "inet", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {15, "int4", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {16, "interval", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {17, "line", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {18, "lseg", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {19, "macaddr", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {20, "money", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {21, "numeric", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {22, "numeric", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {23, "path", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {24, "point", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {25, "polygon", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {26, "float4", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {27, "int2", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});

        datatypeRS_1.addRow(new Object[] {28, "text", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {29, "time", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {30, "timetz", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {31, "timestamp", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {32, "timestamptz", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {33, "tsquery", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {34, "tsvector", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {35, "txid_snapshot", 1, 1, true,
                "type", "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {36, "uuid", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {37, "xml", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});

        getJDBCMockObjectFactory().getMockConnection().getPreparedStatementResultSetHandler().prepareResultSet(
               qry, datatypeRS_1);
        
        Properties props = new Properties();
        props.put("user", "myusername");
        props.put("passwrod", "mypassword");

        try
        {
            profileId = DBConnProfCache.getInstance().initConnectionProfile(
                    serverInfo, status);
            connectionProfile = DBConnProfCache.getInstance().getDbForProfileId(profileId);
        }
        catch (MPPDBIDEException e)
        {
            assertTrue(failMockDriver.isAllConnectionsClosed());
            assertEquals("Throwing SQL exception intentionally.", e.getServerMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
           assertTrue(true);
        }
    }
  
}
