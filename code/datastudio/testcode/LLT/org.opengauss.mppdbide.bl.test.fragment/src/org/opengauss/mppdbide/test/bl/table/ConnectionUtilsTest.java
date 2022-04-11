package org.opengauss.mppdbide.test.bl.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionUtils;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class ConnectionUtilsTest extends BasicJDBCTestCaseAdapter {
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    private DBConnection              dbconn;

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

        this.dbconn = CommonLLTUtils.getDBConnection();
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        MockBLPreferenceImpl.setFileEncoding("UTF-8");

        profileId = createProfileId("TestConnectionName", "Gauss").get();
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
        }

        connProfCache.closeAllNodes();

    }
    
    @Test
    public void test_isAleastOneDbConnected() {
        try {
            Database database = connProfCache.getDbForProfileId(profileId);
            ArrayList<Database> dbs = new ArrayList<>();
            dbs.add(database);
            assertTrue(ConnectionUtils.isAleastOneDbConnected(dbs));
            database.setConnected(false);
            assertFalse(ConnectionUtils.isAleastOneDbConnected(dbs));
            database.setConnected(true);
        } catch (Exception e) {
            fail("can\'t run here");
        }
        
    }
    
    @Test
    public void test_getAnotherConnection() {
        try {
            ConnectionProfileId id1 = createProfileId("db1", "Gauss").get();
            ConnectionProfileId id2 = createProfileId("db2", "Gauss").get();
            ArrayList<Database> dbs = new ArrayList<>();
            Database database1 = connProfCache.getDbForProfileId(id1);
            database1.setOid(2);
            Database database2 = connProfCache.getDbForProfileId(id2);
            database2.setOid(3);
            dbs.add(database1);
            dbs.add(database2);
            assertNotNull(ConnectionUtils.getAnotherConnection(
                    database1.getOid(),
                    dbs));
            connProfCache.removeServer(id1.getServerId());
            connProfCache.removeServer(id2.getServerId());
        } catch (Exception e) {
            fail("can\'t run here");
        }
    }
    
    @Test
    public void test_getAnotherConnection_01() {
        ConnectionProfileId id1 = createProfileId("db1", "Gauss").get();
        try {
            ArrayList<Database> dbs = new ArrayList<>();
            Database database = connProfCache.getDbForProfileId(id1);
            database.setOid(2);
            dbs.add(database);
            ConnectionUtils.getAnotherConnection(
                    database.getOid(),
                    dbs);
            fail("can\'t run here");
        } catch (Exception e) {
            System.out.println("pass in get another connection");
        } finally {
            connProfCache.removeServer(id1.getServerId());
        }
 
    }

    private Optional<ConnectionProfileId> createProfileId(
            String connName,
            String dbName) {
        try {
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo = getServerConnectionInfo(connName, dbName);
                        return Optional.of(connProfCache.initConnectionProfile(serverInfo, status));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    private static ServerConnectionInfo getServerConnectionInfo(String connName, String dbName) {
        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName(connName);
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName(dbName);
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        try {
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        } catch (Exception e) {
            System.out.println("profile security failed!");
        }
        return serverInfo;
    }
    
}
