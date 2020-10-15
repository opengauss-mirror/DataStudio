package com.huawei.mppdbide.test.presentation.table;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.mock.presentation.CommonLLTUtils;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.exceptions.PasswordExpiryException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import static org.junit.Assert.*;

public class ServerVersionTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    ServerConnectionInfo              serverInfo;
    public static final String        version1                  =
            "PostgreSQL 9.2.4 (openGauss 1.0 build e2c0f862) compiled at 2020-04-29 10:27:47 commit 2144 last mr 131 debug on aarch64-unknown-linux-gnu, compiled by g++ (GCC) 8.2.0, 64-bit";
    public static final String        version2                  =
            "Gauss200 OLAP V100R006C10 build 8496 compiled at 2017-03-18 01:17:38 on x86_64-unknown-linux-gnu, compiled by g++ (GCC) 5.4.0, 64-bit";
    public static final String        version3                  =
            "PostgreSQL 9.2.4 (Gauss200 OLAP V100R007C10 build 3f29067a) compiled at 2018-03-02 12:28:00 commit 1072 last mr 1378 on x86_64-unknown-linux-gnu, compiled by g++ (GCC) 5.4.0, 64-bit";
    public static final String        version4                  =
            "Gauss200 OLAP V100R006C10 build 8496 compiled at 2017-03-18 01:17:38 on x86_64-unknown-linux-gnu, compiled by g++ (GCC) 5.4.0, 64-bit";

    public static final String        version5                  =
            "GaussDBV100R003C20SPC107B110 (2017-08-31 08:46:25) on x86_64-unknown-linux-gnu, compiled by gcc (SUSE Linux) 4.3.4 [gcc-4_3-branch revision 152973], 64-bit";

    public static final String        version6                  =
            "PostgreSQL 9.2.4 (openGauss 1.0 build e2c0f862) compiled at 2020-04-29 10:27:47 commit 2144 last mr 131 debug on aarch64-unknown-linux-gnu, compiled by g++ (GCC) 8.2.0, 64-bit";
    public static final String        versionOLAPV1r7c10 = 
            "Gauss200 OLAP V100R006C10 build 12125 compiled at 2018-04-25 22:00:40 on x86_64-unknown-linux-gnu, compiled by g++ (GCC) 5.4.0, 64-bit";
   public static final String version7="Gauss200 OLAP V100R006C10SPC007B008 compiled at 2016-11-11 16:18:35 on x86_64-unknown-linux-gnu, compiled by g++ (SUSE Linux) 4.3.4 [gcc-4_3-branch revision 152973], 64-bit";
    private String                    parsedVersion;
    private String                    parsedVersiontooltip;
    JobCancelStatus status;

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
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.columnComments(preparedstatementHandler);
        CommonLLTUtils.getPartitionData(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
        CommonLLTUtils.getPropertiesConstraint(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();

        serverInfo = new ServerConnectionInfo();
        status = new JobCancelStatus();
        status.setCancel(false);
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
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
            itr = connProfCache.getServers().iterator();
        }

        connProfCache.closeAllNodes();

    }

    @Test
    public void test_server_serverversion_1()
    {

        MockResultSet getServerVersionResult = preparedstatementHandler
                .createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {
                version1});
        preparedstatementHandler.prepareResultSet("SELECT * from version();",
                getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            
            e.printStackTrace();
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(version1);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        assertEquals("openGauss 1.0 build e2c0f862", serverVersion);
        parsedVersion = testParseVersionforConnectionDialog(
                ((ServerConnectionInfo) server.getServerConnectionInfo()).getDBVersion());
        assertEquals("openGauss 1.0", parsedVersion);
        parsedVersiontooltip =
                testParseVersionfortoolTip(((ServerConnectionInfo) server.getServerConnectionInfo()).getDBVersion());
        assertEquals("openGauss 1.0", parsedVersiontooltip);
    }

    // method to test the connection dialog database version parsing
    public String testParseVersionforConnectionDialog(String dbVersion) {
        Matcher matchOpenGauss = Pattern.compile("(?i)(openGauss)\\s+([^\\s]+)").matcher(dbVersion);

        if (matchOpenGauss.find()) {
            return matchOpenGauss.group();
        }

        return dbVersion;
    }

    // method to test the object browser connection tooltip database version
    public String testParseVersionfortoolTip(String version)
    {

        Matcher matchOpenGauss = Pattern.compile("(?i)(openGauss)\\s+([^\\s]+)").matcher(version);

        if (matchOpenGauss.find())
        {
            return matchOpenGauss.group();
        }

        return version;

    }
}
