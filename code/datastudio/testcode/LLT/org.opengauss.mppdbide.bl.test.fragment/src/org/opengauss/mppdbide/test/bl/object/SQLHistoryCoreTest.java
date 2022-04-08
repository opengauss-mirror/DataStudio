package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.sqlhistory.QueryExecutionSummary;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryCore;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryCorePersistence;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryItem;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryItemDetail;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class SQLHistoryCoreTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    ServerConnectionInfo              serverInfo                = null;
    String                            userName                  = System
            .getProperty("user.name");

    @Before
	public void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
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
         * while(itr.hasNext()) {
         * connProfCache.removeServer(itr.next().getId()); }
         * 
         * connProfCache.closeAllNodes();
         */

    }

    @Test
    public void test_addQuerySummary_001()
    {
        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            String query = "select * from pg_am;";

            boolean executionResult = true;

            QueryExecutionSummary summary = generateSummary(dbname, profilename,
                    query, executionResult);

            SQLHistoryCore sqlhistorycore = new SQLHistoryCore(
                    summary.getProfileId(), "test_connection/history",5, 20,
                    false);

            List<SQLHistoryItem> beforelist = sqlhistorycore
                    .getHistoryContent(20);

            sqlhistorycore.addQuerySummary(summary);

            List<SQLHistoryItem> afterlist = sqlhistorycore
                    .getHistoryContent(20);

            assertNotSame(beforelist.size(), afterlist.size());
            assertNull(summary.getExecutionTime());
        }
        catch (Exception e)
        {

            fail("not expected");

            e.printStackTrace();
        }
    }

    public void test_addQuerySummary_002()
    {
        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            String query = "create user ashish identified by " + "'"
                    + "ashish@123" + "'";

            boolean executionResult = true;

            String result = "";
            QueryExecutionSummary summary = generateSummary(dbname, profilename,
                    query, executionResult);
            QueryExecutionSummary summary1 = generateSummary_Info(dbname, profilename,
                    query, result);
            SQLHistoryCore sqlhistorycore = new SQLHistoryCore(
                    summary.getProfileId(), "test_connection/history",5, 50,
                    false);

            List<SQLHistoryItem> beforelist = sqlhistorycore
                    .getHistoryContent(50);

            sqlhistorycore.addQuerySummary(summary);
            summary.startQueryTimer();
            summary.stopQueryTimer();
            summary.setQueryExecutionStatus(false);
            summary1.setQueryExecutionStatus(false);
            List<SQLHistoryItem> afterlist = sqlhistorycore
                    .getHistoryContent(50);

            assertEquals(beforelist.size(), afterlist.size());
            assertEquals(summary1.isAnalyze(), false);
        }
        catch (Exception e)
        {

            fail("not expected");

            e.printStackTrace();

        }

    }

    public void test_addQuerySummary_003()
    {
        try
		{
			String dbname = "postgres";

			String profilename = "test_connection";

			String query = "--select * from pg_tables";

			String queryComment = "/* select * from pg_tables*/";

			String noQueryComment = "/select * from pg_tables";

			boolean executionResult = true;

			QueryExecutionSummary summary = generateSummary(dbname, profilename, query, executionResult);
			QueryExecutionSummary summarycomment = generateSummary(dbname, profilename, queryComment, executionResult);

			QueryExecutionSummary summarynocomment = generateSummary(dbname, profilename, noQueryComment,
					executionResult);

			SQLHistoryCore sqlhistorycore = new SQLHistoryCore(summary.getProfileId(), "test_connection/history", 5, 50,
					false);

			List<SQLHistoryItem> beforelist = sqlhistorycore.getHistoryContent(50);

			sqlhistorycore.addQuerySummary(summary);

			List<SQLHistoryItem> afterlist = sqlhistorycore.getHistoryContent(50);

			sqlhistorycore.addQuerySummary(summarycomment);

			sqlhistorycore.addQuerySummary(summarynocomment);

			assertEquals(beforelist.size(), afterlist.size());

		} catch (Exception e) {

			fail("not expected");

			e.printStackTrace();
		}
	}

    public void test_addQuerySummary_004()
    {
        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            String query = "select * from ";

            boolean executionResult = true;

            QueryExecutionSummary summary = generateSummary(dbname, profilename,
                    query, executionResult);

            SQLHistoryCore sqlhistorycore = new SQLHistoryCore(
                    summary.getProfileId(), "test_connection/history", 5,14,
                    false);

            List<SQLHistoryItem> beforelist = sqlhistorycore
                    .getHistoryContent(14);

            sqlhistorycore.addQuerySummary(summary);

            List<SQLHistoryItem> afterlist = sqlhistorycore
                    .getHistoryContent(14);

            assertNotSame(beforelist.size(), afterlist.size());

        }
        catch (Exception e)
        {

            fail("not expected");

            e.printStackTrace();
        }

    }

    private QueryExecutionSummary generateSummary(String dbname,
            String profilename, String query, boolean executionResult)
    {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
        String startDate = sdf1.format(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        int numRecordsFetched = 2;
        long elapsedTimeLong = 10;

        QueryExecutionSummary summary = new QueryExecutionSummary(dbname,
                profilename, query, executionResult, startDate, elapsedTimeLong,
                numRecordsFetched);
        return summary;
    }
    private QueryExecutionSummary generateSummary_Info(String dbname,
            String profilename,String profileID, String query)
    {
        QueryExecutionSummary summary = new QueryExecutionSummary(dbname,
                profilename,profileID, query);
        assertEquals(summary.getProfileName(), profilename);
        return summary;
    }

    @Test
    public void test_addItemToList_001()
    {
        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            boolean executionResult = true;

            QueryExecutionSummary firstsummary =
                    generateSummary(dbname, profilename, "select * from pg_tables", executionResult);
            SQLHistoryCore sqlhistorycore1 =
                    new SQLHistoryCore(firstsummary.getProfileId(), "test_connection/history", 5, 23, false);
            QueryExecutionSummary secondsummary =
                    generateSummary(dbname, profilename, "select * from pg_views", executionResult);
            SQLHistoryCore sqlhistorycore2 =
                    new SQLHistoryCore(firstsummary.getProfileId(), "test_connection/history", 5, 22, false);
            QueryExecutionSummary thirdsummary =
                    generateSummary(dbname, profilename, "select * from pg_am;", executionResult);
            SQLHistoryCore sqlhistorycore3 =
                    new SQLHistoryCore(firstsummary.getProfileId(), "test_connection/history", 5, 20, false);

           

            sqlhistorycore1.addQuerySummary(firstsummary);

            sqlhistorycore2.addQuerySummary(secondsummary);

            sqlhistorycore3.addQuerySummary(thirdsummary);

            List<SQLHistoryItem> list = sqlhistorycore3.getHistoryContent(20);

            assertEquals("select * from pg_am;", list.get(0).getQuery());

        }
        catch (Exception e)
        {
            fail(" not expected");

            e.printStackTrace();
        }
    }

    @Test
    public void test_addItemToList_002()
    {
        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            boolean executionResult = true;

            QueryExecutionSummary firstsummary = generateSummary(dbname,
                    profilename, "select * from pg_tables", executionResult);
            
          /*  QueryExecutionSummary secondsummary = generateSummary(dbname,
                    profilename, "select * from pg_views", executionResult);
            QueryExecutionSummary thirdsummary = generateSummary(dbname,
                    profilename, "select * from pg_am", executionResult);*/

            SQLHistoryCore sqlhistorycore = new SQLHistoryCore(
                    firstsummary.getProfileId(), "test_connection/history", 4,23,
                    false);

            sqlhistorycore.addQuerySummary(firstsummary);

           // sqlhistorycore.addQuerySummary(secondsummary);

            List<SQLHistoryItem> beforelist = sqlhistorycore
                    .getHistoryContent(23);

            //sqlhistorycore.addQuerySummary(thirdsummary);

            List<SQLHistoryItem> afterlist = sqlhistorycore
                    .getHistoryContent(23);

            List<SQLHistoryItem> sublist = sqlhistorycore.getHistoryContent(23);// added
                                                                               // with
                                                                               // itemcount=2
                                                                               // to
                                                                               // get
                                                                               // the
                                                                               // sublist
            // added with needPersistence=true and retentionsize=2
            SQLHistoryCore sqlhistorycore1 = new SQLHistoryCore(
                    firstsummary.getProfileId(), "test_connection/history", 2,23,
                    true);
            sqlhistorycore1.addQuerySummary(firstsummary);
         //   sqlhistorycore1.addQuerySummary(secondsummary);
         //   sqlhistorycore1.addQuerySummary(thirdsummary);

            assertEquals(1, sublist.size());

            assertNotSame(beforelist, afterlist);

        }
        catch (Exception e)
        {
            fail(" not expected");

            e.printStackTrace();
        }
    }

    @Test
    public void test_setPinStatus_001()
    {

        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            boolean executionResult = true;
            
            QueryExecutionSummary firstsummary = generateSummary(dbname,
                    profilename, "select * from pg_tables", executionResult);
            QueryExecutionSummary secondsummary = generateSummary(dbname,
                    profilename, "select * from pg_views", executionResult);
            QueryExecutionSummary thirdsummary = generateSummary(dbname,
                    profilename, "select * from pg_am;", executionResult);          

            SQLHistoryCore sqlhistorycore = new SQLHistoryCore(
                    firstsummary.getProfileId(), "test_connection/history",4, 20,
                    false);

            sqlhistorycore.addQuerySummary(firstsummary);

            sqlhistorycore.addQuerySummary(secondsummary);
            
            sqlhistorycore.addQuerySummary(thirdsummary);

            List<SQLHistoryItem> unpinnedhistorylist = sqlhistorycore
                    .getHistoryContent(50);

            SQLHistoryItemDetail itemToBePinned = (SQLHistoryItemDetail) unpinnedhistorylist
                    .get(0);

            sqlhistorycore.setPinStatus(itemToBePinned, true);

            List<SQLHistoryItem> finalList = sqlhistorycore
                    .getHistoryContent(20);

            SQLHistoryItem pinnedItem = finalList.get(0);

            assertEquals(pinnedItem.getQuery(), itemToBePinned.getQuery());

        }
        catch (Exception e)
        {
            fail(" not expected");

            e.printStackTrace();
        }

    }

    @Test
    public void test_setPinStatus_002()
    {

        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            boolean executionResult = true;
            
            QueryExecutionSummary firstsummary = generateSummary(dbname,
                    profilename, "select * from pg_tables", executionResult);
            QueryExecutionSummary secondsummary = generateSummary(dbname,
                    profilename, "select * from pg_views", executionResult);
            QueryExecutionSummary thirdsummary = generateSummary(dbname,
                    profilename, "select * from pg_am;", executionResult);

            
            SQLHistoryCore sqlhistorycore = new SQLHistoryCore(
                    firstsummary.getProfileId(), "test_connection/history",4, 20,
                    false);

            sqlhistorycore.addQuerySummary(firstsummary);

            sqlhistorycore.addQuerySummary(secondsummary);
            
            sqlhistorycore.addQuerySummary(thirdsummary);

            List<SQLHistoryItem> unpinnedhistorylist = sqlhistorycore
                    .getHistoryContent(50);

            SQLHistoryItemDetail itemToBePinned = (SQLHistoryItemDetail) unpinnedhistorylist
                    .get(0);

            sqlhistorycore.setPinStatus(itemToBePinned, true);

            List<SQLHistoryItem> afterPin = sqlhistorycore
                    .getHistoryContent(20);
            sqlhistorycore.setPinStatus(itemToBePinned, false);

            List<SQLHistoryItem> afterunpinning = sqlhistorycore
                    .getHistoryContent(20);
            for (SQLHistoryItem item : afterunpinning)
            {
                if (item.isPinned()) fail("not expected");
            }
            assertTrue(true);

        }
        catch (Exception e)
        {
            fail(" not expected");

            e.printStackTrace();
        }

    }

    @Test
    public void test_persistHistory_01()
    {
        try
        {
            String dbname = "postgres";
            String profilename = "test_connection";
            boolean executionResult = true;
            
            QueryExecutionSummary firstsummary = generateSummary(dbname,
                    profilename, "select * from pg_tables", executionResult);
            
            SQLHistoryCore sqlhistorycore = new SQLHistoryCore(
                    firstsummary.getProfileId(), "test_connection/history",4, 50,
                    true);
            sqlhistorycore.persistHistory();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_deleteHistoryItems_001()
    {
        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            boolean executionResult = true;
 
            
            QueryExecutionSummary firstsummary = generateSummary(dbname,
                    profilename, "select * from pg_tables", executionResult);
            QueryExecutionSummary secondsummary = generateSummary(dbname,
                    profilename, "select * from pg_views", executionResult);
            QueryExecutionSummary thirdsummary = generateSummary(dbname,
                    profilename, "select * from pg_am;", executionResult);
            
            SQLHistoryCore sqlhistorycore = new SQLHistoryCore(
                    firstsummary.getProfileId(), "test_connection/history",4, 20,
                    false);

            sqlhistorycore.addQuerySummary(firstsummary);

            sqlhistorycore.addQuerySummary(secondsummary);

            sqlhistorycore.addQuerySummary(thirdsummary);

            List<SQLHistoryItem> beforelist = sqlhistorycore
                    .getHistoryContent(20);

            sqlhistorycore.deleteHistoryItems(beforelist);

            List<SQLHistoryItem> afterList = sqlhistorycore
                    .getHistoryContent(20);

            assertEquals(0, afterList.size());

        }
        catch (Exception e)
        {
            fail(" not expected");

            e.printStackTrace();
        }
    }

    @Test
    public void test_cancelPersist_01()
    {
        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            boolean executionResult = true;
            
            QueryExecutionSummary firstsummary = generateSummary(dbname,
                    profilename, "select * from pg_tables", executionResult);
            
            SQLHistoryCore sqlhistorycore = new SQLHistoryCore(
                    firstsummary.getProfileId(), "test_connection/history",4, 50,
                    true);
            sqlhistorycore.cancelPersist();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
	@Test
	public void test_setSQLQuerySize() {
		try {
			String dbname = "postgres";

			String profilename = "test_connection";
			boolean executionResult = true;

			QueryExecutionSummary firstsummary = generateSummary(dbname, profilename, "select * from pg_tables",
					executionResult);

			SQLHistoryCore sqlhistorycore = new SQLHistoryCore(firstsummary.getProfileId(), "test_connection/history",
					4, 50, true);
			sqlhistorycore.setSQLQuerySize(1);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
    @Test
    public void test_addtoDeleteList_01()
    {
        try
        {
            String dbname = "postgres";

            String profilename = "ds";

            String query = "select * from pg_class";

            boolean executionResult = true;

            SQLHistoryCorePersistence historyPersistence = new SQLHistoryCorePersistence(
                    "test_connection/history", 5);

            QueryExecutionSummary query1 = generateSummary(dbname,
                    profilename, query, executionResult);
            

            SQLHistoryItemDetail historyItem = new SQLHistoryItemDetail(query1,20);
            historyPersistence.addtoDeleteList(historyItem);
            // historyPersistence.loadValidQueries();
            historyPersistence.cancelPersistenceOperation();
            historyPersistence.deleteOlderUnwantedFiles();
            historyPersistence.isPurgeInProgress();
            historyPersistence.readDetailFromFile("abc.txt");
            SQLHistoryItemDetail.getDeserializedContent("");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_addtoDeleteList_02()
    {
        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            String query = "select * from pg_class";

            boolean executionResult = true;

            SQLHistoryCorePersistence historyPersistence = new SQLHistoryCorePersistence(
                    "test_connection/history", 5);

            
            QueryExecutionSummary query1 = generateSummary(dbname,
                    profilename, query, executionResult);

            SQLHistoryItemDetail historyItem = new SQLHistoryItemDetail(query1,20);
            historyPersistence.addtoDeleteList(historyItem);
            // historyPersistence.loadValidQueries();
            historyPersistence.deleteOlderUnwantedFiles();
            historyPersistence.isPurgeInProgress();
            SQLHistoryCorePersistence.readDetailFromFile("abc.txt");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_addtoDeleteList_03()
    {
        try
        {
            String dbname = "postgres";

            String profilename = "ds";

            String query = "select * from pg_class";

            boolean executionResult = true;

            SQLHistoryCorePersistence historyPersistence = new SQLHistoryCorePersistence(
                    "test_connection/history", 5);
            
            QueryExecutionSummary query1 = generateSummary(dbname,
                    profilename, query, executionResult);
            
            SQLHistoryItemDetail historyItem = new SQLHistoryItemDetail(query1,20);
            historyPersistence.addtoDeleteList(historyItem);
            // historyPersistence.loadValidQueries();
            historyPersistence.deleteOlderUnwantedFiles();
            historyPersistence.cancelPersistenceOperation();
            SQLHistoryCorePersistence.readDetailFromFile("./atfhdtfbc.txt");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_SQLHistory_Summery_Infra_01()
    {
        try
        {
            String input = "2016-12-16 18:29:09";
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf1.parse(input);
            String dt = sdf1.format(date);
            long elapsedTimeLong = 10;

            QueryExecutionSummary query = new QueryExecutionSummary("postgres",
                    "ds", "select * from pg_class", true, dt, elapsedTimeLong, 0);

         
            SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,20);

            assertNotNull(item);
            System.out.println("As Expected...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_SQLHistory_Summery_Infra_02()
    {
        try
        {
            String input = "2016-12-16 18:29:09";
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf1.parse(input);
            String dt = sdf1.format(date);
            long elapsedTimeLong = 109;

            QueryExecutionSummary query = new QueryExecutionSummary("postgres",
                    "ds", "select * from pg_class", true, dt, elapsedTimeLong, 0);
            QueryExecutionSummary query1 = new QueryExecutionSummary("postgres",
                    "ds", "select * from pg_cs", true, dt, elapsedTimeLong, 0);

            SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,20);

            assertNotNull(item);
            System.out.println("As Expected...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_purgeDeletedHistoryItems_01()
    {

        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            String query = "select * from pg_am";

            boolean executionResult = true;
            long elapsedTimeLong = 109;

            QueryExecutionSummary summary = generateSummary(dbname, profilename,
                    query, executionResult);
            SQLHistoryCorePersistence historyPersistence = new SQLHistoryCorePersistence(
                    "test_connection/history", 5);
            String input = "2016-12-16 18:29:09";
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf1.parse(input);
            String dt = sdf1.format(date);
            QueryExecutionSummary query1 = new QueryExecutionSummary("postgres",
                    "ds", "select * from pg_class", true, dt, elapsedTimeLong, 0);
            SQLHistoryItemDetail historyItem = new SQLHistoryItemDetail(query1,20);

            for (int i = 0; i <= 2; i++)
            {
                historyPersistence.addtoDeleteList(historyItem);
            }

            historyPersistence.deleteOlderUnwantedFiles();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_equals()
    {

        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            boolean executionResult = true;

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
            String startDate = sdf1.format(new Date());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String elapsedTime = sdf.format(new Date());
            int numRecordsFetched = 2;
            long elapsedTimeLong = 109;

            QueryExecutionSummary firstsummary = new QueryExecutionSummary(
                    dbname, profilename, "select * from pg_tables",
                    executionResult, startDate, elapsedTimeLong, numRecordsFetched);

            SQLHistoryItemDetail detail = new SQLHistoryItemDetail(
                    firstsummary,23);
            SQLHistoryItemDetail detail1 = new SQLHistoryItemDetail(
                    firstsummary,23);
            // SQLHistoryItem sqlhistoryitem=new SQLHistoryItem(firstsummary);
            Object obj1 = new Object();
            obj1 = detail;
            Object obj2 = new Object();
            obj2 = detail1;
            assertEquals(true, detail.equals(obj1));
            assertEquals(false, detail.equals(detail1));
            assertEquals(false, detail.equals(obj2));
        }
        catch (Exception e)
        {

            fail("not expected");
        }

    }

    @Test
    public void test_getHistoryContent_exception()
    {
        try
        {
            String dbname = "postgres";

            String profilename = "test_connection";

            boolean executionResult = true;
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
            String startDate = sdf1.format(new Date());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String elapsedTime = sdf.format(new Date());
            int numRecordsFetched = 2;
            long elapsedTimeLong = 109;

            QueryExecutionSummary firstsummary = new QueryExecutionSummary(
                    dbname, profilename, "select * from pg_tables",
                    executionResult, startDate, elapsedTimeLong, numRecordsFetched);
            SQLHistoryCore sqlhistorycore = new SQLHistoryCore(
                    firstsummary.getProfileId(), "test_connection/history",4, 23,
                    true);

            QueryExecutionSummary secondsummary = new QueryExecutionSummary(
                    dbname, profilename, "select * from pg_views",
                    executionResult, startDate, elapsedTimeLong, numRecordsFetched);

            SQLHistoryCore sqlhistorycore1 = new SQLHistoryCore(
                    firstsummary.getProfileId(), "test_connection/history",4, 22,
                    true);
   
            sqlhistorycore.addQuerySummary(firstsummary);
            sqlhistorycore1.addQuerySummary(secondsummary);

            List<SQLHistoryItem> beforelist = sqlhistorycore
                    .getHistoryContent(50);

            List<SQLHistoryItem> afterList = sqlhistorycore
                    .getHistoryContent(50);
            assertTrue(null!=sqlhistorycore);

        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();

        }
    }

 
}
