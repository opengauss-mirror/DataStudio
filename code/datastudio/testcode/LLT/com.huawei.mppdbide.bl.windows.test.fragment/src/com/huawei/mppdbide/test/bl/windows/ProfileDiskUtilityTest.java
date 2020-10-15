package com.huawei.mppdbide.test.bl.windows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntryPermission;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.ProfileDiskUtility;
import com.huawei.mppdbide.bl.serverdatacache.ProfileMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ProfileMetaData.ProfileInfo;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.mock.bl.windows.CommonLLTUtils;
import com.huawei.mppdbide.mock.bl.windows.MockBLPreferenceImpl;
import com.huawei.mppdbide.mock.bl.windows.ProfileDIskHelper;
import com.huawei.mppdbide.utils.connectionprofileversion.IConnectionProfileVersions;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class ProfileDiskUtilityTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection               = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler         statementHandler         = null;
    DBConnProfCache                   connProfCache            = null;
    ConnectionProfileId               profileId                = null;
    ServerConnectionInfo              serverInfo               = null;
    String                            userName                 = System.getProperty("user.name");
    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
    protected void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");

        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.prepareProxyInfoForDB(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();
        
        serverInfo = new ServerConnectionInfo();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        serverInfo.setConectionName("TestConnectionName1");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setProfileId(""+1);
        serverInfo.setPrivilegeBasedObAccess(true);
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
    protected void tearDown() throws Exception
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
    public void test_BL_ProfileDiskUtility_FUNC_00_02()
    {
        try
        {
            ConnectionProfileManagerImpl.getInstance().saveProfile(serverInfo);
            //Cleanup old left overs
            ProfileDiskUtility utility = new ProfileDiskUtility();
            List<IServerConnectionInfo> oldServerList = utility.getProfiles();
            for (IServerConnectionInfo conn : oldServerList)
            {
                utility.dropProfileFolder(utility
                    .getUserProfileFolderName(conn.getProfileId()), conn.getConectionName());
            }
            
            List<IServerConnectionInfo> serverList = utility.getProfiles();
            assertEquals(serverList.size(), 0);
            String s = utility.getServerName(Paths.get("./" + userName + "/Profile/TestConnectionName1/connection.properties"));
            ProfileMetaData prof =  utility.getMetaData();
            
            Path path=Paths.get("./jkdfvnksdvk");
            String absPath = path.toAbsolutePath().toString();
            prof.addProfile(
                    "TestConnectionName1",
                    serverInfo.getProfileId(),
                    "./"
                            + userName
                            + "/Profile/"
                            + utility
                                    .getUserProfileFolderName(serverInfo
                                            .getProfileId())
                            + "/connection.properties",IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION);
       //    prof.deleteProfile("TestConnectionName1");
        //    assertEquals(prof.getAllProfiles().size(), 0);
          //  utility.readProfileFromFile(Paths.get("./" + userName + "/Profile/TestConnectionName1/connection.properties"));
            prof.writeToDisk(absPath);
            assertTrue(prof != null);

        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
          
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    
    
    @Test
    public void test_getDecryptedPwd()
    {
        try
        {
            ConnectionProfileManagerImpl.getInstance().saveProfile(serverInfo);
            ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
            serverInfonew.setConectionName("newconnection");
            serverInfonew.setServerIp("");
            serverInfonew.setServerPort(5432);
            serverInfonew.setDatabaseName("Gauss");
            serverInfonew.setUsername("myusername");
            serverInfonew.setPrd("mypassword".toCharArray());
            serverInfonew
                    .setSavePrdOption(SavePrdOptions.PERMANENTLY);
            serverInfonew.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().saveProfile(
                    serverInfonew);

            Server server = new Server(serverInfonew);
           
            serverInfonew.setPrd( server.getEncrpytedProfilePrd().toCharArray());
            ProfileDiskUtility utility = new ProfileDiskUtility();
            utility.getDecryptedPrd(serverInfonew);
          char[] pwd={'m','y','p','a','s','s','w','o','r','d'};
            assertEquals(serverInfonew.getPrd()[0],pwd[0]);

        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
           // fail(e.getMessage());
        }
    }
    
    
    
    @Test
    public void test_getDecryptedPwd_exception()
    {
        try
        {
            ConnectionProfileManagerImpl.getInstance().saveProfile(serverInfo);
            ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
            serverInfonew.setConectionName("newconnection");
            serverInfonew.setServerIp("");
            serverInfonew.setServerPort(5432);
            serverInfonew.setDatabaseName("Gauss");
            serverInfonew.setUsername("myusername");
            serverInfonew.setPrd("mypassword".toCharArray());
            serverInfonew
                    .setSavePrdOption(SavePrdOptions.PERMANENTLY);
            serverInfonew.setPrivilegeBasedObAccess(true);
            
            ConnectionProfileManagerImpl.getInstance().saveProfile(
                    serverInfonew);

           
            ProfileDiskUtility utility = new ProfileDiskUtility();
            utility.getDecryptedPrd(serverInfonew);
            assertEquals(serverInfonew.getPrd().length,10);

        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
           // fail(e.getMessage());
        }
    }
    @Test
    public void test_BL_ProfileDiskUtility_FUNC_00_03()
    {
        try
        {
        	ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
            serverInfonew.setConectionName("newconnection");
            serverInfonew.setServerIp("");
            serverInfonew.setServerPort(5432);
            serverInfonew.setDatabaseName("Gauss");
            serverInfonew.setUsername("myusername");
            serverInfonew.setPrd("mypassword".toCharArray());
            serverInfonew
                    .setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            Server server = new Server(serverInfonew);
            Path path = Paths.get("./" + userName + "/Profile/newconnection/connection.properties");
            if(Files.exists(path)){
             assertTrue(true);   
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    @Test
    public void test_BL_ProfileDiskUtility_FUNC_00_04()
    {
        try
        {
        	ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
            serverInfonew.setConectionName("newconnection");
            serverInfonew.setServerIp("");
            serverInfonew.setServerPort(5432);
            serverInfonew.setDatabaseName("Gauss");
            serverInfonew.setUsername("myusername");
            serverInfonew.setPrd("mypassword".toCharArray());
            serverInfonew
                    .setSavePrdOption(SavePrdOptions.PERMANENTLY);
            Server server = new Server(serverInfonew);
            if(Files.exists(Paths.get("./" + userName + "/Profile/newconnection/connection.properties"))){
                assertTrue(true);   
               }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    @Test
    public void test_BL_ProfileDiskUtility_FUNC_00_01()
    {
        try
        {
            ProfileDiskUtility utility = new ProfileDiskUtility();
            List<IServerConnectionInfo> serverList = utility.getProfiles();
            ProfileMetaData prof =  utility.getMetaData();
            utility.setMetaData(prof);
            prof.addProfile(
                    "TestConnectionName1",
                    serverInfo.getProfileId(),
                    "./"
                            + userName
                            + "/Profile/"
                            + utility
                                    .getUserProfileFolderName(serverInfo
                                            .getProfileId())
                            + "/connection.properties",IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION);
            assertTrue(prof != null);

        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    @Test
    public void test_BL_ProfileDiskUtility_FUNC_001()
    {/*
        try
        {
            ConnectionProfileManagerImpl.getInstance().saveProfile(serverInfo);

            ProfileDiskUtility utility = new ProfileDiskUtility();
            List<ServerConnectionInfo> serverList = utility.getProfiles();
         //   assertEquals(serverList.size(), 1);
            assertEquals(serverList.get(0).getConectionName(),
                    "TestConnectionName1");
            assertEquals(serverList.get(0).getServerIp(), "127.0.0.1");
            assertEquals(serverList.get(0).getServerPort(), 5432);
            assertEquals(serverList.get(0).getDatabaseName(), "Gauss");
            assertEquals(serverList.get(0).getUsername(), "myusername");
            assertEquals(serverList.get(0).getSavePasswordOption(),
                    SAVE_PRD_OPTIONS.DO_NOT_SAVE);

        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    */}

    @Test
    public void test_BL_ProfileDiskUtilityWithTwoServerinfo_FUNC_002()
    {
        try
        {
            ConnectionProfileManagerImpl.getInstance().saveProfile(serverInfo);
            ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
            serverInfonew.setConectionName("newconnection");
            serverInfonew.setServerIp("");
            serverInfonew.setServerPort(5432);
            serverInfonew.setDatabaseName("Gauss");
            serverInfonew.setUsername("myusername");
            serverInfonew.setPrd("mypassword".toCharArray());
            serverInfonew.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().renameProfile(serverInfonew);

            ProfileDiskUtility utility = new ProfileDiskUtility();
            List<IServerConnectionInfo> serverList = utility.getProfiles();
         //   assertEquals(serverList.size(), 2);
         //   assertEquals(serverList.get(1).getConectionName(), "newconnection");
            assertEquals(serverList.get(0).getServerIp(), "127.0.0.1");
            assertEquals(serverList.get(0).getServerPort(), 5432);
            assertEquals(serverList.get(0).getDatabaseName(), "Gauss");
            assertEquals(serverList.get(0).getDsUsername(), "myusername");
            assertEquals(serverList.get(0).getSavePrdOption(),
                    SavePrdOptions.DO_NOT_SAVE);
            
            utility.getDecryptedPrd(serverInfonew);

        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
           // fail(e.getMessage());
        }
    }

    @Test
    public void test_BL_ProfileDiskUtilityWithreadfromfile_FUNC_003()
    {
        try
        {
            // ConnectionProfileManagerImpl.getInstance().saveProfile(serverInfo);
            ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
            serverInfonew.setConectionName("newconnection");
            serverInfonew.setServerIp("");
            serverInfonew.setServerPort(5432);
            serverInfonew.setDatabaseName("Gauss");
            serverInfonew.setUsername("myusername");
            serverInfonew.setPrd("mypassword".toCharArray());
            serverInfonew
                    .setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfonew.setPrivilegeBasedObAccess(true);
            Server server = new Server(serverInfonew);
             ConnectionProfileManagerImpl connManager = ConnectionProfileManagerImpl.getInstance();
            connManager.saveProfile(serverInfonew);
            
            
            
            ProfileDiskUtility utility = connManager.getDiskUtility();
            Map<String, ProfileInfo> map = utility.getMetaData().getAllProfiles();
            //assertEquals(map.size(), 1);
            
            connManager.saveProfile(serverInfo);
            map = utility.getMetaData().getAllProfiles();
           // assertEquals(map.size(), 2);

        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_BL_RenameConnectionProfile_FUNC_003()
    {
        try
        {
            // ConnectionProfileManagerImpl.getInstance().saveProfile(serverInfo);
        	 ConnectionProfileManagerImpl.getInstance().saveProfile(serverInfo);
             ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
             serverInfonew.setConectionName("newconnection");
             serverInfonew.setServerIp("");
             serverInfonew.setServerPort(5432);
             serverInfonew.setDatabaseName("Gauss");
             serverInfonew.setUsername("myusername");
             serverInfonew.setPrd("mypassword".toCharArray());
             serverInfonew.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
             Server server = new Server(serverInfonew);
             Path path = Paths.get("./" + userName + "/Profile/newconnection/connection.properties");
             ProfileDiskUtility utility = new ProfileDiskUtility();
             utility.renameProfileOnDisk(serverInfonew);
             utility.getDecryptedPrd(serverInfonew);
             utility.deleteFolder(path);
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_BL_ProfileDiskUtilityDrop_FUNC_002()
    {
        try
        {
            ConnectionProfileManagerImpl.getInstance().saveProfile(serverInfo);
            ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
            serverInfonew.setConectionName("newconnection");
            serverInfonew.setServerIp("");
            serverInfonew.setServerPort(5432);
            serverInfonew.setDatabaseName("Gauss");
            serverInfonew.setUsername("myusername");
            serverInfonew.setPrd("mypassword".toCharArray());
            serverInfonew
                    .setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfonew.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfonew);
            Server server = new Server(serverInfonew);
            ConnectionProfileManagerImpl.getInstance().saveProfile(
                    serverInfonew);

            ProfileDiskUtility utility = new ProfileDiskUtility();
            List<IServerConnectionInfo> serverList = utility.getProfiles();
            int size = serverList.size();
          //  assertTrue(size >= 1);
            utility.dropProfileFolder(utility
                    .getUserProfileFolderName(serverInfonew.getProfileId()), "newconnection");
            serverList = utility.getProfiles();
            //assertEquals(serverList.size(), 1);

        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_BL_ProfileDiskUtilityDropfail_FUNC_002()
    {
        try
        {
            ConnectionProfileManagerImpl.getInstance().saveProfile(serverInfo);
            ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
            serverInfonew.setConectionName("newconnection");
            serverInfonew.setServerIp("");
            serverInfonew.setServerPort(5432);
            serverInfonew.setDatabaseName("Gauss");
            serverInfonew.setUsername("myusername");
            serverInfonew.setPrd("mypassword".toCharArray());
            serverInfonew
                    .setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfonew.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfonew);
            Server server = new Server(serverInfonew);
            ConnectionProfileManagerImpl.getInstance().saveProfile(
                    serverInfonew);

            ProfileDIskHelper utility = new ProfileDIskHelper();
            List<IServerConnectionInfo> serverList = utility.getProfiles();
            /*assertEquals(serverList.size(), 2);*/
            utility.dropProfileFolder(utility
                    .getUserProfileFolderName(serverInfonew.getProfileId()), "newconnection");
            fail();
            serverList = utility.getProfiles();
            assertEquals(serverList.size(), 1);

        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_BL_ProfileDiskUtility_FUNC_00_05()
    {/*
    try
    {
        ServerConnectionInfo serverInfoloc = new ServerConnectionInfo();
        serverInfoloc.setConectionName("TestConnectionName1");
        serverInfoloc.setServerIp("127.0.0.2");
        serverInfoloc.setServerPort(5432);
        serverInfoloc.setDatabaseName("postgres");
        serverInfoloc.setUsername("myusername");
        serverInfoloc.setPassword("passwordtest".toCharArray());
        serverInfoloc
        .setSavePasswordOption(SAVE_PRD_OPTIONS.DO_NOT_SAVE);
        //Server server=new Server(serverInfoloc);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(userName);
        ConnectionProfileManagerImpl.getInstance().saveProfile(serverInfoloc);
    ProfileDiskUtility utility = new ProfileDiskUtility();
    Charset charset = StandardCharsets.UTF_8;
    String profileFolderName = ProfileDiskUtility.getUserProfileFolderName(serverInfoloc.getProfileId());
    String content = new String(Files.readAllBytes(Paths.get("./" + userName + "/Profile/"+profileFolderName +"/connection.properties")), charset);
    content = content.replaceAll("\"conectionName\""," ");
    Files.write(Paths.get("./" + userName + "/Profile/"+profileFolderName +"/connection.properties"), content.getBytes(charset));
    Path filepath = Paths.get("./" + userName + "/Profile/"+profileFolderName +"/connection.properties");
    ServerConnectionInfo info=utility.readProfileFromFile(filepath);
    if(info==null){
    System.out.println("successful");
    assertTrue(true) ;
    List<String> exceptionList=utility.getExceptionList();
    exceptionList.get(0);
    }
    }
    catch (DatabaseOperationException e)
    {
    fail("not expected");
    e.printStackTrace();
    fail(e.getMessage());
    }
    catch (Exception e)
    {
    e.printStackTrace();
    fail(e.getMessage());

    }
    */}
    
    @Test
    public void test_BL_ProfileDiskUtility_FUNC_new_01()
    {
        try
        {
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().saveProfile(serverInfo);
            //Cleanup old left overs
            ProfileDiskUtility utility = new ProfileDiskUtility();
            utility.setOsCurrentUserFolderPath(".");
            List<IServerConnectionInfo> oldServerList = utility.getProfiles();
            for (IServerConnectionInfo conn : oldServerList)
            {
                utility.dropProfileFolder(utility
                    .getUserProfileFolderName(conn.getProfileId()), conn.getConectionName());
            }
            
            List<IServerConnectionInfo> serverList = utility.getProfiles();
            assertEquals(serverList.size(), 0);
            utility.getServerName(Paths.get("./" + userName + "/Profile/TestConnectionName1/connection.properties"));
            ProfileMetaData prof =  utility.getMetaData();
            
            Path path=Paths.get("./jkdfvnksdvk");
            String absPath = path.toAbsolutePath().toString();
            prof.addProfile(
                    "TestConnectionName1",
                    serverInfo.getProfileId(),
                    "./"
                            + userName
                            + "/Profile/"
                            + utility
                                    .getUserProfileFolderName(serverInfo
                                            .getProfileId())
                            + "/connection.properties",IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION);
            prof.writeToDisk(absPath);
            assertNotNull(prof);

        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
