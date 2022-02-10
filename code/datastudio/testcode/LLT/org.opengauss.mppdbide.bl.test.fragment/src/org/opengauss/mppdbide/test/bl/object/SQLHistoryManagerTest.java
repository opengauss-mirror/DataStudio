
package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.junit.After;
import org.junit.Before;

import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.sqlhistory.QueryExecutionSummary;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryCore;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryItem;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryManager;
import org.opengauss.mppdbide.bl.sqlhistory.manager.ISqlHistoryManager;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class SQLHistoryManagerTest extends BasicJDBCTestCaseAdapter {
    MockConnection connection = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler statementHandler = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler estatementHandler = null;
    DBConnProfCache connProfCache = null;
    ConnectionProfileId profileId = null;
    ServerConnectionInfo serverInfo = null;
    ISqlHistoryManager hismgr = null;
    HashMap<String, SQLHistoryCore> profileHistroy = null;
    QueryExecutionSummary query = null;

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */

    @Before
	public void setUp() throws Exception {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        MPPDBIDELoggerUtility.setArgs(null);
        connection = new MockConnection();
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();

        serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);

        // profileId = connProfCache.initConnectionProfile(serverInfo);

        System.setProperty("file.encoding", "utf8");

        String input = "2016-12-16 18:29:09";

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = (Date) sdf1.parse(input);
        String dt = sdf1.format(date);

        query = new QueryExecutionSummary("postgres", "ds", "select * from pg_class", true, dt, 1090000, 80);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */

    @After
	public void tearDown() throws Exception {
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
         * while(itr.hasNext()) {
         * connProfCache.removeServer(itr.next().getId()); }
         * 
         * connProfCache.closeAllNodes();
         */

    }

    public void test_SQLHistoryManager_1() throws MPPDBIDEException, ParseException {
        hismgr = SQLHistoryManager.getInstance();
        hismgr.addNewQueryExecutionInfo(query.getProfileId(), query);
        hismgr.getHistoryContent("ds", 0);
        System.out.println(hismgr.getHistoryContent("ds", 0));
        assertNotNull(hismgr);
    }

    public void test_SQLHistoryManager_2() throws MPPDBIDEException, ParseException {
        hismgr = SQLHistoryManager.getInstance();
        hismgr.doHistoryManagementForProfile("ds", "");
        hismgr.addNewQueryExecutionInfo(query.getProfileId(), query);
        assertNotNull(hismgr);
    }

    public void test_SQLHistoryManager_3() throws MPPDBIDEException, ParseException {
        hismgr = SQLHistoryManager.getInstance();
        hismgr.doHistoryManagementForProfile("ds", "");
        hismgr.addNewQueryExecutionInfo(query.getProfileId(), query);
        hismgr.setHistoryRetensionSize(50);
        assertNotNull(hismgr);
    }

    public void test_SQLHistoryManager_4() throws MPPDBIDEException, ParseException {
        hismgr = SQLHistoryManager.getInstance();
        hismgr.doHistoryManagementForProfile("ds", "");
        hismgr.addNewQueryExecutionInfo(query.getProfileId(), query);

        String dbname = "postgres";

        String profilename = "test_connection";

        String query = "select * from pg_am;";

        boolean executionResult = true;

        QueryExecutionSummary summary = generateSummary(dbname, profilename, query, executionResult);

        SQLHistoryItem qryItem = new SQLHistoryItem(summary, 4);
        qryItem.setDatabaseName("postgres");
        qryItem.setProfileId("ds");
        qryItem.setQuery("select * from pg_class");
        qryItem.setElapsedTime("800000");
        assertEquals(qryItem.getElapsedTime(), "800000");
        hismgr.setPinStatus(qryItem, true);
        assertNotNull(hismgr);
    }

    public void test_SQLHistoryManager_5() throws MPPDBIDEException, ParseException {
        hismgr = SQLHistoryManager.getInstance();
        hismgr.doHistoryManagementForProfile("ds", "");
        hismgr.addNewQueryExecutionInfo(query.getProfileId(), query);
        assertNotNull(hismgr);
    }

    public void test_SQLHistoryManager_6() throws MPPDBIDEException, ParseException {
        hismgr = SQLHistoryManager.getInstance();
        hismgr.doHistoryManagementForProfile("ds", "");
        hismgr.addNewQueryExecutionInfo(query.getProfileId(), query);
        hismgr.stopHistoryManagementForProfile("ds");
        assertNotNull(hismgr);
    }

    public void test_SQLHistoryManager_7() throws MPPDBIDEException, ParseException {
        hismgr = SQLHistoryManager.getInstance();
        hismgr.doHistoryManagementForProfile("ds", "");
        hismgr.addNewQueryExecutionInfo(query.getProfileId(), query);
        hismgr.stopHistoryManagementForProfile("");
        assertNotNull(hismgr);
    }

    public void test_SQLHistoryManager_8() throws MPPDBIDEException, ParseException {
        hismgr = SQLHistoryManager.getInstance();
        hismgr.addNewQueryExecutionInfo(query.getProfileId(), query);
        hismgr.removeHistoryManagementForProfile("ds");
        assertNotNull(hismgr);
    }

    public void test_SQLHistoryManager_9() throws MPPDBIDEException, ParseException {
        hismgr = SQLHistoryManager.getInstance();
        hismgr.addNewQueryExecutionInfo(query.getProfileId(), query);
        hismgr.removeHistoryManagementForProfile("");
        assertNotNull(hismgr);
    }

    public void test_SQLHistoryManager_12() throws MPPDBIDEException, ParseException {
        hismgr = SQLHistoryManager.getInstance();
        IEventBroker eventBroker = new org.opengauss.mppdbide.mock.bl.EventBroker();
        hismgr.init(eventBroker);
    }

    public void test_SQLHistoryManager_10() throws MPPDBIDEException, ParseException {
        hismgr = SQLHistoryManager.getInstance();
        hismgr.doHistoryManagementForProfile("ds", "");
        hismgr.addNewQueryExecutionInfo(query.getProfileId(), query);

        String dbname = "postgres";

        String profilename = "test_connection";

        String query = "select * from pg_am;";

        boolean executionResult = true;

        QueryExecutionSummary summary = generateSummary(dbname, profilename, query, executionResult);

        SQLHistoryItem qryItem = new SQLHistoryItem(summary, 4);
        qryItem.setDatabaseName("postgres");
        qryItem.setProfileId("ds");
        qryItem.setQuery("select * from pg_class");
        qryItem.setElapsedTime(summary.getElapsedTime());

        List<SQLHistoryItem> arr = new ArrayList<SQLHistoryItem>();
        arr.add(qryItem);
        hismgr.deleteHistoryItems(arr);
        assertNotNull(hismgr);
    }

    private QueryExecutionSummary generateSummary(String dbname, String profilename, String query,
            boolean executionResult) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
        String startDate = sdf1.format(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        int numRecordsFetched = 2;
        long elapsedTimeLong = 10;

        QueryExecutionSummary summary = new QueryExecutionSummary(dbname, profilename, query, executionResult,
                startDate, elapsedTimeLong, numRecordsFetched);
        return summary;
    }

    public void test_SQLHistoryManager_10_1() throws MPPDBIDEException, ParseException {

        hismgr = SQLHistoryManager.getInstance();
        hismgr.doHistoryManagementForProfile("ds", "");
        hismgr.purgeHistorybeforeClose();

        String dbname = "postgres";

        String profilename = "test_connection";

        String query = "select * from ";

        boolean executionResult = true;

        QueryExecutionSummary summary = generateSummary(dbname, profilename, query, executionResult);
        SQLHistoryItem qryItem = new SQLHistoryItem(summary, 5);
        qryItem.setDatabaseName("postgres");
        qryItem.setProfileId("ds");
        qryItem.setQuery("select * from pg_class");
        qryItem.setElapsedTime(summary.getElapsedTime());

        List<SQLHistoryItem> arr = new ArrayList<SQLHistoryItem>();
        arr.add(qryItem);
        hismgr.deleteHistoryItems(arr);
        assertNotNull(hismgr);
    }

    /*
     * public void test_SQLHistoryManager_11() throws MPPDBIDEException,
     * ParseException { ServerConnectionInfo serverInfo = new
     * ServerConnectionInfo();
     * serverInfo.setConectionName("TestConnectionName");
     * serverInfo.setServerIp(""); serverInfo.setServerPort(5432);
     * serverInfo.setDatabaseName("Gauss");
     * serverInfo.setUsername("myusername");
     * serverInfo.setPassword("mypassword".toCharArray());
     * serverInfo.setSavePasswordOption(SAVE_PRD_OPTIONS.DO_NOT_SAVE); try {
     * DBConnProfCache.getInstance().initConnectionProfile(serverInfo); } catch
     * (PasswordExpiryException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); }
     * 
     * hismgr = SQLHistoryManager.getInstance();
     * hismgr.purgeHistorybeforeClose(); assertNotNull(hismgr); }
     */
}
