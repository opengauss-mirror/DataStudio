package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.AesAlgorithmUtilHelper;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.DecryptionHelper;
import org.opengauss.mppdbide.mock.bl.EncryptionHelper;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.mock.bl.ProfileDiskUtilityHelper;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.AESAlgorithmUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class EncryptionDecrytionTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    ServerConnectionInfo              serverInfo                = null;
    Path connectionProfilePath = null;
    
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
        MPPDBIDELoggerUtility.setArgs(null);
        connection = new MockConnection();
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();

        serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("127.0.0.1");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        ProfileDiskUtilityHelper profile=new ProfileDiskUtilityHelper();
        profile.setOption(4);
        ConnectionProfileManagerImpl.getInstance().setDiskUtility(profile);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        connectionProfilePath = ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);

        System.setProperty("file.encoding", "utf8");
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
        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();

        /*
         * Database database = connProfCache.getDbForProfileId(profileId);
         * database.getServer().close();
         * 
         * preparedstatementHandler.clearPreparedStatements();
         * statementHandler.clearStatements(); connProfCache.closeAllNodes();
         * 
         * Iterator<Server> itr = connProfCache.getServers().iterator();
         * 
         * while (itr.hasNext()) {
         * connProfCache.removeServer(itr.next().getId()); }
         * 
         * connProfCache.closeAllNodes();
         */

    }

    @Test
    public void test_TTA_DIRECTORY_CHECK_001_FUNC_001_02() throws IOException
    {
        if (CommonLLTUtils.isLinux())
        {
            return;
        }
        
        char[] pswd = "pswd".toCharArray();
        SecureUtil sec = new SecureUtil();
        try
        {
            sec.setPackagePath(connectionProfilePath.toString());
            sec.encryptPrd(pswd);
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");
        }
        sec.setPackagePath("./security");
        File file = new File(CommonLLTUtils.getPackagePathSecureUtil(sec));
        if (file.exists())
        {
            if (file.isDirectory())
            {
                assertEquals(CommonLLTUtils.getPackagePathSecureUtil(sec), "./security");
                System.out.println("As expected...");
            }
            else
            {
                fail("Not expected to come here.");
            }
        }
        else
        {
            fail("Not expected to come here.");
        }
    }

    @Test
    public void test_TTA_DIRECTORY_CHECK_001_FUNC_001_03() throws IOException
    {
        SecureUtil sec=new SecureUtil();
        sec.setPackagePath("./security/KEY_PART" + "ok" + ".txt");

        File file = new File(CommonLLTUtils.getPackagePathSecureUtil(sec));
        if (!file.exists())
        {
            assertFalse("./security/KEY_PART1.txt"
                    .equals(CommonLLTUtils.getPackagePathSecureUtil(sec)));
            /*
             * assertNotEquals(SecureUtil.getPackagePath().toString(),
             * "./security/KEY_PART1.txt");
             */
            System.out.println("As expected...");
        }
    }

    @Test
    public void test_TTA_DIRECTORY_CHECK_001_FUNC_001_04() throws IOException
    {
        SecureUtil sec=new SecureUtil();
        sec.setPackagePath("./security/KEY_PART1.txt");

        File file = new File(CommonLLTUtils.getPackagePathSecureUtil(sec));
        if (file.exists())
        {

            assertEquals(CommonLLTUtils.getPackagePathSecureUtil(sec),
                    "./security/KEY_PART1.txt");
            System.out.println("As expected...");
        }
    }

    @Test
    public void test_TTA_TestConnection_001_FUNC_001_01()
    {

        try
        {
            serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            database.connectToServer();
            assertFalse("mypassword".equals(new String(serverInfo.getPrd())));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");
        }

    }

    @Test
    public void test_TTA_TestConnection_001_FUNC_001_01_02()
    {

        try
        {
            serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setSSLEnabled(true);
            serverInfo.setRootCertificate("path");
            serverInfo.setClientSSLCertificate("clSSLCertificatePath");
            serverInfo.setClientSSLKey("clSSLKeyPath");
            serverInfo.setSSLMode("require");
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            assertFalse("mypassword".equals(new String(serverInfo.getPrd())));
            Database database = new Database(server, 2, "Gauss");
            database.connectToServer();
            assertEquals("path", serverInfo.getRootCertificate());
            assertEquals("clSSLCertificatePath",
                    serverInfo.getClientSSLCertificate());
            assertEquals("clSSLKeyPath",
                    serverInfo.getClientSSLKey());
            assertEquals("require",
                    serverInfo.getSSLMode());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");
        }

    }

    @Test
    public void test_SSLClientCertificate()
    {

        try
        {
            serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setSSLEnabled(true);
            serverInfo.setRootCertificate("path");
            serverInfo.setClientSSLCertificate("clSSLCertificatePath");
            serverInfo.setClientSSLKey("clSSLKeyPath");
            serverInfo.setSSLMode("require");
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            assertFalse("mypassword".equals(new String(serverInfo.getPrd())));
            Database database = new Database(server, 2, "Gauss");
            database.connectToServer();
            assertEquals("path", serverInfo.getRootCertificate());
            assertEquals("clSSLCertificatePath",
                    serverInfo.getClientSSLCertificate());
            assertEquals("clSSLKeyPath",
                    serverInfo.getClientSSLKey());
            assertEquals("require",
                    serverInfo.getSSLMode());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");
        }

    }

    @Test
    public void test_SSLClientKey()
    {

        try
        {
            serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setSSLEnabled(true);
            serverInfo.setRootCertificate("path");
            serverInfo.setClientSSLCertificate("clSSLCertificatePath");
            serverInfo.setClientSSLKey("clSSLKeyPath");
            serverInfo.setSSLMode("require");
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            assertFalse("mypassword".equals(new String(serverInfo.getPrd())));
            Database database = new Database(server, 2, "Gauss");
            database.connectToServer();
            assertEquals("path", serverInfo.getRootCertificate());
            assertEquals("clSSLCertificatePath",
                    serverInfo.getClientSSLCertificate());
            assertEquals("clSSLKeyPath",
                    serverInfo.getClientSSLKey());
            assertEquals("require",
                    serverInfo.getSSLMode());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");
        }

    }

    @Test
    public void test_TTA_TestPasswordClear_001_FUNC_001_02()
    {

        try
        {
            serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            database.connectToServer();
            assertTrue("".equals(new String(serverInfo.getPrd()).trim()));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");
        }

    }

    @Test
    public void test_TTA_getServerConnectionInfo_001_FUNC_001_03()
    {
        try
        {
            ServerConnectionInfo serverInfo1 = new ServerConnectionInfo();
            serverInfo1.setConectionName("TestConnectionName");
            serverInfo1.setServerIp("");
            serverInfo1.setServerPort(5432);
            serverInfo1.setDatabaseName("Gauss");
            serverInfo1.setUsername("myusername");
            serverInfo1.setPrd("password".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            // serverInfo1.setPasswordRemembered(true);
            serverInfo1.setSavePrdOption(1, true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo1);
            Server ser = new Server(serverInfo1);

            IServerConnectionInfo serinfo = ser
                    .getServerConnectionInfo(serverInfo1.getDatabaseName());
            assertEquals("password", new String(serinfo.getPrd()));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_TTA_getServerConnectionInfo_001_FUNC_001_04()
    {
        try
        {
            ServerConnectionInfo serverInfo1 = new ServerConnectionInfo();
            serverInfo1.setConectionName("TestConnectionName");
            serverInfo1.setServerIp("");
            serverInfo1.setServerPort(5432);
            serverInfo1.setDatabaseName("Gauss");
            serverInfo1.setUsername("myusername");
            serverInfo1.setPrd("password".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            // serverInfo1.setPasswordRemembered(true);
            serverInfo1.setSavePrdOption(1, false);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo1);
            Server ser = new Server(serverInfo1);

            IServerConnectionInfo serinfo = ser
                    .getServerConnectionInfo(serverInfo1.getDatabaseName());
            assertEquals("password", new String(serinfo.getPrd()));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_TTA_getServerConnectionInfo_001_FUNC_001_05()
    {
        try
        {
            ServerConnectionInfo serverInfo1 = new ServerConnectionInfo();
            serverInfo1.setConectionName("TestConnectionName");
            serverInfo1.setServerIp("");
            serverInfo1.setServerPort(5432);
            serverInfo1.setDatabaseName("Gauss");
            serverInfo1.setUsername("myusername");
            serverInfo1.setPrd("password".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            // serverInfo1.setPasswordRemembered(true);
            serverInfo1.setSavePrdOption(0, false);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo1);
            Server ser = new Server(serverInfo1);

            IServerConnectionInfo serinfo = ser
                    .getServerConnectionInfo(serverInfo1.getDatabaseName());
            assertEquals("password", new String(serinfo.getPrd()));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void test_TTA_ENCRYPTION_001_FUNC_001_01()
    {
        SecureUtil encryptionDecryption = new SecureUtil();
        encryptionDecryption.setPackagePath(connectionProfilePath.toString());
        char[] pswd = "password".toCharArray();
        try
        {
            encryptionDecryption.encryptPrd(pswd);
            assertFalse(new String(pswd)
                    .equals(encryptionDecryption.getEncryptedString()));
            /*
             * assertNotEquals(encryptionDecryption.getEncryptedString(), new
             * String(pswd));
             */
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");
        }

    }


    @Test
    public void test_TTA_DECRYPTION_001_FUNC_001_01()
    {
        SecureUtil encryptionDecryption = new SecureUtil();
        char[] pswd = "password".toCharArray();
        String encryptedPassword = null;
        try
        {
            encryptionDecryption.setPackagePath(connectionProfilePath.toString());
            encryptionDecryption.encryptPrd(pswd);
            encryptedPassword = encryptionDecryption.getEncryptedString();
            encryptionDecryption.decryptPrd(encryptedPassword);
            assertEquals(encryptionDecryption.getDecryptedString(),
                    new String(pswd));
        }
        catch (DataStudioSecurityException e)

        {
        	System.out.println("not same case");
        	MPPDBIDELoggerUtility.securityError("Error occured during encrypting or decrypting");
            e.printStackTrace();
            // fail("Not expected to come here.");
        }

    }

    public void test_TTA_DECRYPTION_001_FUNC_001_04()
    {
        SecureUtil encryptionDecryption =  new SecureUtil();
        char[] pswd = "password".toCharArray();
        String encryptedPassword = null;
        try
        {
            encryptionDecryption.setPackagePath(connectionProfilePath.toString());
            encryptionDecryption.encryptPrd(pswd);
            encryptedPassword = encryptionDecryption.getEncryptedString();
            encryptionDecryption.decryptPrd(encryptedPassword);
            assertEquals(encryptionDecryption.getDecryptedString(),
                    new String(pswd));
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");

        }

    }

    public void test_TTA_DECRYPTION_001_FUNC_001_05()
    {
        SecureUtil encryptionDecryption = new SecureUtil();
        char[] pswd = "password".toCharArray();
        String encryptedPassword = null;
        try
        {
            encryptionDecryption.setPackagePath(connectionProfilePath.toString());
            encryptionDecryption.encryptPrd(pswd);
            encryptedPassword = encryptionDecryption.getEncryptedString();

            DecryptionHelper decryptionHelper = new DecryptionHelper(
                    encryptionDecryption);
            decryptionHelper.setThrowBadPaddingException(true);
            Field enc = encryptionDecryption.getClass()
                    .getDeclaredField("decryption");
            enc.setAccessible(true);
            enc.set(encryptionDecryption, decryptionHelper);
            enc.setAccessible(false);

            encryptionDecryption.decryptPrd(encryptedPassword);
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");

        }

    }

    public void test_TTA_DECRYPTION_001_FUNC_001_06()
    {
        SecureUtil encryptionDecryption = new SecureUtil();
        char[] pswd = "password".toCharArray();
        String encryptedPassword = null;
        try
        {
            encryptionDecryption.setPackagePath(connectionProfilePath.toString());
            encryptionDecryption.encryptPrd(pswd);
            encryptedPassword = encryptionDecryption.getEncryptedString();
            DecryptionHelper decryptionHelper = new DecryptionHelper(
                    encryptionDecryption);
            decryptionHelper.setThrowIllegalBlockSizeException(true);
            Field enc = encryptionDecryption.getClass()
                    .getDeclaredField("decryption");
            enc.setAccessible(true);
            enc.set(encryptionDecryption, decryptionHelper);
            enc.setAccessible(false);

            encryptionDecryption.decryptPrd(encryptedPassword);
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");

        }

    }

    public void test_TTA_DECRYPTION_001_FUNC_001_07()
    {
        SecureUtil encryptionDecryption =  new SecureUtil();
        char[] pswd = "password".toCharArray();
        String encryptedPassword = null;
        try
        {
            encryptionDecryption.setPackagePath(connectionProfilePath.toString());
            encryptionDecryption.encryptPrd(pswd);
            encryptedPassword = encryptionDecryption.getEncryptedString();
            DecryptionHelper decryptionHelper = new DecryptionHelper(
                    encryptionDecryption);
            decryptionHelper.setThrowInvalidAlgorithmParameterException(true);
            Field enc = encryptionDecryption.getClass()
                    .getDeclaredField("decryption");
            enc.setAccessible(true);
            enc.set(encryptionDecryption, decryptionHelper);
            enc.setAccessible(false);

            encryptionDecryption.decryptPrd(encryptedPassword);
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");

        }

    }

    public void test_TTA_DECRYPTION_001_FUNC_001_08()
    {
        SecureUtil encryptionDecryption =  new SecureUtil();
        char[] pswd = "password".toCharArray();
        String encryptedPassword = null;
        try
        {
            encryptionDecryption.setPackagePath(connectionProfilePath.toString());
            encryptionDecryption.encryptPrd(pswd);
            encryptedPassword = encryptionDecryption.getEncryptedString();
            DecryptionHelper decryptionHelper = new DecryptionHelper(
                    encryptionDecryption);
            decryptionHelper.setThrowInvalidKeyException(true);
            Field enc = encryptionDecryption.getClass()
                    .getDeclaredField("decryption");
            enc.setAccessible(true);
            enc.set(encryptionDecryption, decryptionHelper);
            enc.setAccessible(false);

            encryptionDecryption.decryptPrd(encryptedPassword);
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");

        }
    }

    public void test_TTA_DECRYPTION_001_FUNC_001_09()
    {
        SecureUtil encryptionDecryption =  new SecureUtil();
        char[] pswd = "password".toCharArray();
        String encryptedPassword = null;
        try
        {
            encryptionDecryption.setPackagePath(connectionProfilePath.toString());
            encryptionDecryption.encryptPrd(pswd);
            encryptedPassword = encryptionDecryption.getEncryptedString();
            DecryptionHelper decryptionHelper = new DecryptionHelper(
                    encryptionDecryption);
            decryptionHelper.setThrowNoSuchAlogoException(true);
            Field enc = encryptionDecryption.getClass()
                    .getDeclaredField("decryption");
            enc.setAccessible(true);
            enc.set(encryptionDecryption, decryptionHelper);
            enc.setAccessible(false);

            encryptionDecryption.decryptPrd(encryptedPassword);
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");

        }

    }

    public void test_TTA_DECRYPTION_001_FUNC_001_10()
    {
        SecureUtil encryptionDecryption = new SecureUtil();
        char[] pswd = "password".toCharArray();
        String encryptedPassword = null;
        try
        {
            encryptionDecryption.setPackagePath(connectionProfilePath.toString());
            encryptionDecryption.encryptPrd(pswd);
            encryptedPassword = encryptionDecryption.getEncryptedString();
            DecryptionHelper decryptionHelper = new DecryptionHelper(
                    encryptionDecryption);
            decryptionHelper.setThrowNoSuchPaddingException(true);
            Field enc = encryptionDecryption.getClass()
                    .getDeclaredField("decryption");
            enc.setAccessible(true);
            enc.set(encryptionDecryption, decryptionHelper);
            enc.setAccessible(false);

            encryptionDecryption.decryptPrd(encryptedPassword);
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");

        }
    }

    public void test_TTA_DECRYPTION_001_FUNC_001_11()
    {
        SecureUtil encryptionDecryption =  new SecureUtil();
        char[] pswd = "password".toCharArray();
        String encryptedPassword = null;
        try
        {
            System.setProperty("file.encoding", "utf8");
            encryptionDecryption.setPackagePath(connectionProfilePath.toString());
            encryptionDecryption.encryptPrd(pswd);
            encryptedPassword = encryptionDecryption.getEncryptedString();

            AesAlgorithmUtilHelper aesAlgorithmUtil = new AesAlgorithmUtilHelper(
                    encryptionDecryption);
            aesAlgorithmUtil.setThrowInvalidKeySpecException(true);
            Field enc = encryptionDecryption.getClass()
                    .getDeclaredField("aesAlgorithmUtil");
            enc.setAccessible(true);
            enc.set(encryptionDecryption, aesAlgorithmUtil);
            enc.setAccessible(false);

            encryptionDecryption.decryptPrd(encryptedPassword);
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");

        }

    }

    public void test_TTA_AES_001_FUNC_001_01()
    {
        SecureUtil encryptionDecryption = new SecureUtil();
        encryptionDecryption.setPackagePath(connectionProfilePath.toString());
        char[] pswd = "password".toCharArray();
        try
        {
            encryptionDecryption.getKey();

            System.setProperty("file.encoding", "");

            AESAlgorithmUtility aesAlgorithm = new AESAlgorithmUtility(
                    encryptionDecryption);
            aesAlgorithm.generatePBKDF("pswd", false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");

        }

    }
}
