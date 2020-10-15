package com.huawei.mppdbide.test.bl.windows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntryPermission;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
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
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ProfileMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewColumnList;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.mock.bl.windows.CommonLLTUtils;
import com.huawei.mppdbide.mock.bl.windows.MockBLPreferenceImpl;
import com.huawei.mppdbide.utils.connectionprofileversion.IConnectionProfileVersions;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.files.SetFilePermission;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class ProfileMetaDataTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                               connection                = null;
    PreparedStatementResultSetHandler            preparedstatementHandler  = null;
    StatementResultSetHandler                    statementHandler          = null;

    PreparedStatementResultSetHandler            epreparedstatementHandler = null;
    StatementResultSetHandler                    estatementHandler         = null;
    DBConnProfCache                              connProfCache             = null;
    ConnectionProfileId                          profileId                 = null;
    ServerConnectionInfo                         serverInfo                = null;
    ProfileMetaData                              prof                      = new ProfileMetaData();

    List<String>                                 list                      = new ArrayList<String>();

    private static final Set<AclEntryPermission> DEFAULT_PERMISSIONS       = EnumSet
            .of(AclEntryPermission.DELETE, AclEntryPermission.DELETE_CHILD,

                    AclEntryPermission.WRITE_DATA,
                    AclEntryPermission.WRITE_ATTRIBUTES,
                    AclEntryPermission.WRITE_NAMED_ATTRS,
                    AclEntryPermission.APPEND_DATA,
                    AclEntryPermission.SYNCHRONIZE,

                    AclEntryPermission.READ_ATTRIBUTES,
                    AclEntryPermission.READ_DATA,
                    AclEntryPermission.READ_NAMED_ATTRS,

                    AclEntryPermission.READ_ACL);

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
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);

        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.prepareProxyInfoForDB(preparedstatementHandler);
        // CommonLLTUtils.prepareProxyInfoForDatabase(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
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
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
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
    public void test_getAllProfiles()
            throws DatabaseOperationException, DataStudioSecurityException
    {
        prof.getAllProfiles();
        assertNotNull(prof);
    }

    @Test
    public void test_fileExists()
    {
        SecureUtil sec=new SecureUtil();
        sec.setPackagePath("./security/diskConnection.txt");
        File file = new File(CommonLLTUtils.getPackagePathSecureUtil(sec));
        if (file.exists())
        {

            assertEquals(CommonLLTUtils.getPackagePathSecureUtil(sec), "./security/diskConnection.txt");
            System.out.println("As expected...");
        }
    }

    @Test
    public void test_addProfiles()
            throws DatabaseOperationException, DataStudioSecurityException
    {
        int k = prof.getAllProfiles().size();
        System.out.println("before add : " + k);
        prof.addProfile("ds", "" + 1, "./security/diskConnection.txt",IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION);
        ConnectionProfileManagerImpl.getInstance().getProfile("ds");
        int i = prof.getAllProfiles().size();
        System.out.println("after add : " + i);
        assertEquals(1, i);
        prof.addProfile("ds", "" + 1, "./security/diskConnection.txt",IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION);
    }

    @Test
    public void test_addProfiles1()
            throws DatabaseOperationException, DataStudioSecurityException
    {
        int k = prof.getAllProfiles().size();
        System.out.println("before add : " + k);
        prof.addProfile("ds", "" + 1, "./security/diskConnection.txt",IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION);
        int i = prof.getAllProfiles().size();
        System.out.println("after add : " + i);
        assertEquals(1, i);
        assertNotNull(prof.getProfileId("ds"));
    }

    @Test
    public void test_addProfiles3()
            throws DatabaseOperationException, DataStudioSecurityException
    {
        int k = prof.getAllProfiles().size();
        System.out.println("before add : " + k);
        prof.addProfile("ds", "" + 1, "./security/diskConnection.txt",IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION);
        int i = prof.getAllProfiles().size();
        System.out.println("after add : " + i);
        assertEquals(1, i);
    }

    @Test
    public void test_deleteProfiles()
            throws DatabaseOperationException, DataStudioSecurityException
    {
        ConnectionProfileManagerImpl.getInstance()
                .getProfile("TestConnectionName");
        prof.addProfile("ds", "" + 1, "./security/diskConnection.txt",IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION);
        int k = prof.getAllProfiles().size();
        System.out.println("before delete map size : " + k);
        prof.deleteProfile("ds");
        int i = prof.getAllProfiles().size();
        System.out.println("after delete map size : " + i);
        assertEquals(0, i);
    }

    @Test
    public void test_writeToDisk()
    {

        ISetFilePermission permission = new SetFilePermission();
        try
        {
            Files.deleteIfExists(Paths.get("diskConnection.txt"));
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try
        {
            permission.createFileWithPermission("./security/diskConnection.txt",
                    true, DEFAULT_PERMISSIONS, true);
        }
        catch (DatabaseOperationException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try
        {
            prof.writeToDisk("./security/diskConnection.txt");
            assertNotNull(prof);
            System.out.println("as expected...");
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_writeToDisk1()
    {
        try
        {
            Files.deleteIfExists(Paths.get("conn.txt"));

        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try
        {
            FilePermissionFactory.getFilePermissionInstance().createFileWithPermission("./security/diskConnection.txt",
                    false, null, true);
        }
        catch (DatabaseOperationException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try
        {
            prof.writeToDisk("./security/diskConnection.txt");
            assertNotNull(prof);
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_readFromDisk()
    {
        try
        {
            prof.readFromDisk("./security/diskConnection.txt");
            assertNotNull(prof);
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_readFromDisk1()
    {
        try
        {
            prof.readFromDisk("./security/connection.txt");
            assertNotNull(prof);
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_ViewObjectTest()
    {
        Object obj = new Object();
        Database db = connProfCache.getDbForProfileId(profileId);
        Namespace ns = new Namespace(1, "namespace", db);
        ViewObjectGroup objGrp = new ViewObjectGroup(ns);
        ViewObjectGroup objGrp1 = new ViewObjectGroup(ns);
        obj = objGrp;
        objGrp.equals(obj);
        objGrp.equals(objGrp1);
        objGrp.equals(objGrp);
        objGrp.hashCode();
        assertNotNull(objGrp);
    }

    @Test
    public void test_ViewObjectTest_1()
    {
        Object obj = null;
        ViewObjectGroup objGrp = new ViewObjectGroup(obj);
        objGrp.equals(null);
        assertNotNull(objGrp);
    }

    @Test
    public void test_ViewColum_List()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        Namespace namespace;
        try
        {
            namespace = database.getNameSpaceById(1);
            ViewMetaData vmd = new ViewMetaData(2, "anything", namespace,database);
            ViewColumnList objGrp = new ViewColumnList(vmd);
            ViewObjectGroup viewObjectGroup = new ViewObjectGroup(this);
            objGrp.equals(objGrp);
            objGrp.hashCode();
            assertNotNull(objGrp);
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            fail("Should not throw exception");
        }
    }

    @Test
    public void test_writeToDisk1_1()
    {
        try
        {
            Files.deleteIfExists(Paths.get("conn.txt"));

        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try
        {
            FilePermissionFactory.getFilePermissionInstance().createFileWithPermission("./security/diskConnection.txt",
                    false, null, true);
        }
        catch (DatabaseOperationException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try
        {
            prof.writeToDisk("./security/disk123Connection.txt");
            assertNotNull(prof);
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_writeToDisk1_2()
    {
        try
        {
            FilePermissionFactory.getFilePermissionInstance().createFileWithPermission("./security/diskConnection.txt",
                    false, null, true);
        }
        catch (DatabaseOperationException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try
        {
            File file = new File("./security/disk123Connection123.txt");
            file.createNewFile();
            file.setWritable(false);
            // Path path = Paths.get("./security/disk123Connection.txt");
            // boolean iswrite = !Files.isWritable(path);
            prof.writeToDisk("./security/disk123Connection123.txt");

            assertNotNull(prof);
            file.delete();
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
