package com.huawei.mppdbide.test.presentation.properties;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.StmtExecutor;
import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DatabaseUtils;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import com.huawei.mppdbide.bl.serverdatacache.QueryResult;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.SynonymMetaData;
import com.huawei.mppdbide.bl.serverdatacache.SynonymUtil;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.UserNamespace;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.bl.sqlhistory.QueryExecutionSummary;
import com.huawei.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import com.huawei.mppdbide.mock.presentation.CommonLLTUtils;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.autorefresh.AutoRefreshQueryFormation;
import com.huawei.mppdbide.presentation.autorefresh.RefreshObjectDetails;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridColumnDataProvider;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataRow;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.presentation.grid.resultset.ColumnValueSqlTypeComparator;
import com.huawei.mppdbide.presentation.objectproperties.ConstraintInfo;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataRow;
import com.huawei.mppdbide.presentation.objectproperties.PropertiesSynonymImpl;
import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.presentation.synonym.olap.SynonymInfo;
import com.huawei.mppdbide.presentation.synonym.olap.SynonymWrapper;
import com.huawei.mppdbide.test.presentation.table.MockPresentationBLPreferenceImpl;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.IDSListener;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class DsResultSetGridDataProviderTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    int                               actionFetchResult;
    DSResultSetGridColumnDataProvider colData;

    public int getActionFetchResult()
    {
        return actionFetchResult;
    }

    public void setActionFetchResult(int actionFetchResult)
    {
        this.actionFetchResult = actionFetchResult;
    }

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
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setDateFormat("yyyy-MM-dd");
        MockPresentationBLPreferenceImpl.setTimeFormat("HH:mm:ss");
        
        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
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
    public void testINIT_FUNC_001_001_1()
    {
        final class IResultConfigTest implements IResultConfig
        {

            @Override
            public int getFetchCount()
            {
                return 1;
            }

            @Override
            public ActionAfterResultFetch  getActionAfterFetch()
            {
                return ActionAfterResultFetch.ISSUE_COMMIT_CONNECTION_AFTER_FETCH;
            }
        }
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.runOnSQLTerminalRS(preparedstatementHandler);
            IQueryResult result = DatabaseUtils.executeOnSqlTerminal(
                    "select * from tbl1", 1000, database.getConnectionManager().getFreeConnection(),
                    new MessageQueue());
            long elapsedTimeLong = 109;
            String input = "2016-12-16 18:29:09";
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf1.parse(input);
            String dt = sdf1.format(date);
            QueryExecutionSummary summary = new QueryExecutionSummary(
                    "postgres", "ds", "select * from pg_class", true, dt,
                    elapsedTimeLong, 0);
            DSResultSetGridDataProvider dsr = new DSResultSetGridDataProvider(
                    result, new IResultConfigTest(), summary);
            new DsResultSetGridDataProviderTest().setActionFetchResult(10);
            dsr.init();
            List<IDSGridDataRow> rows = dsr.getAllFetchedRows();
            //assertEquals("1", rows.get(0).getValue(0).toString());
           // assertEquals("PUBLIC", rows.get(0).getValue(1));
            assertNotNull(dsr.getAllFetchedRows());
            assertNotNull(dsr.isEndOfRecords());
            assertNotNull(dsr.getColumnDataProvider());
            assertNotNull(dsr.getRecordCount());
            assertNotNull(dsr.getSummary());
            assertNotNull(dsr.getDataProviderConfig());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not Expected to come here");
        }
    }

    @Test
    public void testINIT_FUNC_001_001_2()
    {

        final class DSResultSetGridDataProviderInnerTest
                extends DSResultSetGridDataProvider
        {
            private IQueryResult           queryResult;
            private IResultConfig          config;
            private IQueryExecutionSummary summary;
            private IDSGridColumnProvider  columnDataProvider;
            private List<IDSGridDataRow>   rows;
            private boolean                isEndOfRecordsReached;
            private DSEventTable           eventTable;

            public DSResultSetGridDataProviderInnerTest(IQueryResult result,
                    IResultConfig rsConfig, IQueryExecutionSummary summary)
            {
                super(result, rsConfig, summary);
                this.queryResult = result;
                this.config = rsConfig;
                this.summary = summary;
                this.rows = new ArrayList<IDSGridDataRow>(5);
                this.eventTable = new DSEventTable();
            }

            @Override
            public void init()
                    throws DatabaseOperationException, DatabaseCriticalException
            {
                colData = new DSResultSetGridColumnDataProvider();
                colData.init(queryResult);
                this.columnDataProvider = colData;
                getNextBatch();
            }
        }
        final class IResultConfigTest implements IResultConfig
        {

            @Override
            public int getFetchCount()
            {
                return 2;
            }

            @Override
            public ActionAfterResultFetch getActionAfterFetch()
            {
                return ActionAfterResultFetch.ISSUE_ROLLBACK_CONNECTION_AFTER_FETCH;
            }
        }
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.runOnSQLTerminalRS(preparedstatementHandler);
            IQueryResult result = DatabaseUtils.executeOnSqlTerminal(
                    "select * from tbl1", 1000, database.getConnectionManager().getFreeConnection(),
                    new MessageQueue());
            long elapsedTimeLong = 109;
            String input = "2016-12-16 18:29:09";
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf1.parse(input);
            String dt = sdf1.format(date);
            QueryExecutionSummary summary = new QueryExecutionSummary(
                    "postgres", "ds", "select * from pg_class", true, dt,
                    elapsedTimeLong, 0);
            DSResultSetGridDataProviderInnerTest dsr = new DSResultSetGridDataProviderInnerTest(
                    result, new IResultConfigTest(), summary);
            dsr.init();
            assertEquals("id", colData.getColumnName(0));
            assertEquals("name - ", colData.getColumnDesc(1));
            assertNotNull(colData.getDataTypeName(0));
            assertNotNull(colData.getColumnCount());
            assertNotNull(colData.getColumnIndex(""));
            assertNotNull(colData.getColumnIndex("name"));
            assertNotNull(colData.getDataTypeNames());
            assertNotNull(colData.getColumnNames());
            assertNotNull(colData.getComparator(1));
            assertNotNull(colData.getPrecision(0));
            assertNotNull(colData.getScale(0));
            assertNotNull(colData.getMaxLength(0));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not Expected to come here");
        }
    }

    @Test
    public void testINIT_FUNC_001_001_3()
    {
        final class IResultConfigTest implements IResultConfig
        {

            @Override
            public int getFetchCount()
            {
                return 1;
            }

            @Override
            public ActionAfterResultFetch getActionAfterFetch()
            {
                return ActionAfterResultFetch.CLOSE_CONNECTION_AFTER_FETCH;
            }
        }
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.runOnSQLTerminalRS(preparedstatementHandler);
            IQueryResult result = DatabaseUtils.executeOnSqlTerminal(
                    "select * from tbl1", 1000, database.getConnectionManager().getFreeConnection(),
                    new MessageQueue());
            long elapsedTimeLong = 109;
            String input = "2016-12-16 18:29:09";
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf1.parse(input);
            String dt = sdf1.format(date);
            QueryExecutionSummary summary = new QueryExecutionSummary(
                    "postgres", "ds", "select * from pg_class", true, dt,
                    elapsedTimeLong, 0);
            DSResultSetGridDataProvider dsr = new DSResultSetGridDataProvider(
                    result, new IResultConfigTest(), summary);
            dsr.init();
          //Observation point - It shouldn't throw any exception while closing the connection after fetching all records
            assertTrue(dsr != null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not Expected to come here");
        }
    }
    
    @Test
    public void testINIT_FUNC_001_004()
    {
        final class IResultConfigTest implements IResultConfig
        {

            @Override
            public int getFetchCount()
            {
                return 1;
            }

            @Override
            public ActionAfterResultFetch  getActionAfterFetch()
            {
                return ActionAfterResultFetch.ISSUE_COMMIT_CONNECTION_AFTER_FETCH;
            }
        }
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.runOnSQLTerminalRS(preparedstatementHandler);
            IQueryResult result = DatabaseUtils.executeOnSqlTerminal(
                    "select * from tbl1", 1000, database.getConnectionManager().getFreeConnection(),
                    new MessageQueue());
            long elapsedTimeLong = 109;
            String input = "2016-12-16 18:29:09";
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf1.parse(input);
            String dt = sdf1.format(date);
            QueryExecutionSummary summary = new QueryExecutionSummary(
                    "postgres", "ds", "select * from pg_class", true, dt,
                    elapsedTimeLong, 0);
            DSResultSetGridDataProvider dsr = new DSResultSetGridDataProvider(
                    result, new IResultConfigTest(), summary);
            new DsResultSetGridDataProviderTest().setActionFetchResult(10);
            
            DefaultParameter dp1 = new DefaultParameter("N1", "BINARY_INTEGER", "5", PARAMETERTYPE.IN);
            DefaultParameter dp2 = new DefaultParameter("N2", "BINARY_INTEGER", "5", PARAMETERTYPE.IN);
            DefaultParameter dp3 = new DefaultParameter("TEMP_RESULT", "BINARY_INTEGER", "500", PARAMETERTYPE.OUT);
            ArrayList<DefaultParameter> debugInputValueList = new ArrayList<DefaultParameter>();
            debugInputValueList.add(dp1);
            debugInputValueList.add(dp2);
            debugInputValueList.add(dp3);
            dsr.init(result, debugInputValueList, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not Expected to come here");
        }
    }

    @Test
    public void testINIT_FUNC_001_001_5()
    {
        try
        {
            ColumnValueSqlTypeComparator cvstTinyInt = new ColumnValueSqlTypeComparator(
                    new Integer(-6));
            assertEquals(1, cvstTinyInt.compare(Byte.valueOf((byte) 1),
                    Byte.valueOf((byte) 2)));
            ColumnValueSqlTypeComparator cvstSmallInt = new ColumnValueSqlTypeComparator(
                    new Integer(5));
            assertEquals(1, cvstSmallInt.compare(Short.valueOf((short) 20),
                    Short.valueOf((short) 10)));
            ColumnValueSqlTypeComparator cvstInt = new ColumnValueSqlTypeComparator(
                    new Integer(4));
            assertEquals(0, cvstInt.compare(100, 100));
            ColumnValueSqlTypeComparator cvstBigInt = new ColumnValueSqlTypeComparator(
                    new Integer(-5));
            assertEquals(-1,
                    cvstBigInt.compare(Long.valueOf(10), Long.valueOf(20)));
            ColumnValueSqlTypeComparator cvsFloat = new ColumnValueSqlTypeComparator(
                    new Integer(6));
            assertEquals(0, cvsFloat.compare(20.5f, 20.5f));
            ColumnValueSqlTypeComparator cvsDouble = new ColumnValueSqlTypeComparator(
                    new Integer(8));
            assertEquals(-1, cvsDouble.compare(Double.valueOf(10.5),
                    Double.valueOf(15.5)));
            ColumnValueSqlTypeComparator cvsNumeric = new ColumnValueSqlTypeComparator(
                    new Integer(2));
            assertEquals(1, cvsNumeric.compare(new BigDecimal(101.1), new BigDecimal(1.10)));
            ColumnValueSqlTypeComparator cvstBool = new ColumnValueSqlTypeComparator(
                    new Integer(16));
            assertEquals(0, cvstBool.compare(true, true));
            ColumnValueSqlTypeComparator cvstDate = new ColumnValueSqlTypeComparator(
                    new Integer(91));
            Date date1 = new Date(98, 5, 21);
            Date date2 = new Date(99, 1, 9);
            java.sql.Date sqlDate1 = new java.sql.Date(date1.getTime());
            java.sql.Date sqlDate2 = new java.sql.Date(date2.getTime());
            assertEquals(-1, cvstDate.compare(sqlDate1, sqlDate2));
            ColumnValueSqlTypeComparator cvstTime = new ColumnValueSqlTypeComparator(
                    new Integer(92));
            assertEquals(-1, cvstTime.compare(Time.valueOf("00:00:00"),
                    Time.valueOf("23:59:59")));
            ColumnValueSqlTypeComparator cvstTimeStamp = new ColumnValueSqlTypeComparator(
                    new Integer(93));
            assertEquals(-1,
                    cvstTimeStamp.compare(new Timestamp(date1.getTime()),
                            new Timestamp(date2.getTime())));
            ColumnValueSqlTypeComparator cvstString = new ColumnValueSqlTypeComparator(
                    new Integer(12));
            assertEquals(-23, cvstString.compare("abc", "xyz"));
            ColumnValueSqlTypeComparator cvstNull = new ColumnValueSqlTypeComparator(
                    new Integer(0));
            assertEquals(1, cvstNull.compare(null, "pqr"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not Expected to come here");
        }
    }
    
    @Test
    public void testTTA_DSResultSetGridDataRow_test_001() {
        final class IResultConfigTest implements IResultConfig {

            @Override
            public int getFetchCount() {
                return 1;
            }

            @Override
            public ActionAfterResultFetch getActionAfterFetch() {
                return ActionAfterResultFetch.CLOSE_CONNECTION_AFTER_FETCH;
            }
        }

        try {
            Database database = connProfCache.getDbForProfileId(profileId);

            CommonLLTUtils.runOnSQLTerminalBytesRS(preparedstatementHandler);
            IQueryResult result = DatabaseUtils.executeOnSqlTerminal("select * from bytes_table", 1000,
                    database.getConnectionManager().getFreeConnection(), new MessageQueue());
            long elapsedTimeLong = 109;
            String input = "2016-12-16 18:29:09";
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = sdf1.parse(input);
            String dt = sdf1.format(date);
            QueryExecutionSummary summary = new QueryExecutionSummary("postgres", "ds", "select * from pg_class", true,
                    dt, elapsedTimeLong, 0);
            DSResultSetGridDataProvider dsr = new DSResultSetGridDataProvider(result, new IResultConfigTest(), summary);
            dsr.init();

            DSResultSetGridDataRow row = new DSResultSetGridDataRow(dsr);
            Object[] values = {"A".getBytes(), "B".getBytes(), "C".getBytes(), "D".getBytes(), "E".getBytes()};
            row.setValues(values);
            row.setIncludeEncoding(true);
            row.setEncoding("UTF-8");

            assertEquals(row.getValue(0), "A");
            assertEquals(row.getValue(1), "B");
            assertEquals(row.getValue(2), "C");
            assertEquals(row.getValue(3), "D");
            assertEquals(row.getValue(4), "E");

            assertEquals(row.getEncoding(), "UTF-8");

            row.setEncoding("Garbage");
            assertFalse(row.getValue(0).equals("A"));
            assertFalse(row.getValue(1).equals("B"));
            assertFalse(row.getValue(2).equals("C"));
            assertFalse(row.getValue(3).equals("D"));
            assertFalse(row.getValue(4).equals("E"));

            row.setEncoding("GBK");
            assertTrue(row.getValue(0).equals("A"));
            assertTrue(row.getValue(1).equals("B"));
            assertTrue(row.getValue(2).equals("C"));
            assertTrue(row.getValue(3).equals("D"));
            assertTrue(row.getValue(4).equals("E"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Not Expected to come here");
        }

    }
        
    public static class MockListener implements IDSListener
    {

        @Override
        public void handleEvent(DSEvent event)
        {
            assertTrue(true);             
        }            
    }
    
    @Test
    public void testINIT_FUNC_001_001_4()
    {
        final class IResultConfigTest implements IResultConfig
        {

            @Override
            public int getFetchCount()
            {
                return 1;
            }

            @Override
            public ActionAfterResultFetch getActionAfterFetch()
            {
                return ActionAfterResultFetch.CLOSE_CONNECTION_AFTER_FETCH;
            }
        }

        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.runOnSQLTerminalRS(preparedstatementHandler);
            IQueryResult result = DatabaseUtils.executeOnSqlTerminal(
                    "select * from tbl1", 1000, database.getConnectionManager().getFreeConnection(),
                    new MessageQueue());
            long elapsedTimeLong = 109;
            String input = "2016-12-16 18:29:09";
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf1.parse(input);
            String dt = sdf1.format(date);
            QueryExecutionSummary summary = new QueryExecutionSummary(
                    "postgres", "ds", "select * from pg_class", true, dt,
                    elapsedTimeLong, 0);
            DSResultSetGridDataProvider dsr = new DSResultSetGridDataProvider(
                    result, new IResultConfigTest(), summary);
            dsr.init();
          //Observation point - It shouldn't throw any exception while closing the connection after fetching all records
            assertTrue(dsr != null);
            dsr.setDatabase(database);
            
            assertEquals(dsr.getDatabse(), database);
            MockListener l = new MockListener();
            dsr.addListener(2, l);
            
            assertNull(dsr.getTable());
            assertNull(dsr.getColumnGroupProvider());
            assertFalse(dsr.getResultTabDirtyFlag());
            
            dsr.setEditSupported(true);
            dsr.setEncodingChanged(true);
            dsr.changeEncoding("UTF-8");
            
            assertTrue(dsr.isEditSupported());
            
            dsr.changeEncoding("GBK");
            assertTrue(dsr.isEncodingChanged());            
            dsr.preDestroy();
            assertNull(dsr.getSummary());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not Expected to come here");
        }
    }
    
    @Test
    public void testINIT_FUNC_PROC_001_001()
    {
        final class IResultConfigTest implements IResultConfig
        {

            @Override
            public int getFetchCount()
            {
                return 1;
            }

            @Override
            public ActionAfterResultFetch getActionAfterFetch()
            {
                return ActionAfterResultFetch.CLOSE_CONNECTION_AFTER_FETCH;
            }
        }

        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.runOnSQLTerminalRS(preparedstatementHandler);
            String query = "EXEC DSUSER."+"\"ADD_PROC1\""+"\r\n(\r\n\t5,\t--N1 BINARY_INTEGER\r\n\t5,\t--N2 BINARY_INTEGER"
                            +"\r\n\t?,\t--TEMP_RESULT BINARY_INTEGER"
                            +"\r\n\t?\t--RESULT BINARY_INTEGER\r\n)";
            IQueryExecutionSummary summary = SQLHistoryFactory.getNewExlainQueryExecutionSummary("DataBase","Server",
                    "conn", query);
            StmtExecutor executor = new StmtExecutor(query, database.getConnectionManager().getFreeConnection());
            StmtExecutor executor1 = new StmtExecutor(query, database.getConnectionManager().getFreeConnection());
            ArrayList<Object> outResultList = new ArrayList<Object>();
            outResultList.add(500);
            executor1.setOutResultList(outResultList );
            QueryResult queryResult = new QueryResult(executor, database.getConnectionManager().getFreeConnection(),
                    true);
            QueryResult queryResult1 = new QueryResult(executor1, database.getConnectionManager().getFreeConnection(),
                    true);
            DSResultSetGridDataProvider dsr = new DSResultSetGridDataProvider(
                    queryResult, new IResultConfigTest(), summary);
            
            DSResultSetGridDataProvider dsr1 = new DSResultSetGridDataProvider(
                    queryResult1, new IResultConfigTest(), summary);
            
            DefaultParameter dp1 = new DefaultParameter("N1", "BINARY_INTEGER", "5", PARAMETERTYPE.IN);
            DefaultParameter dp2 = new DefaultParameter("N2", "BINARY_INTEGER", "5", PARAMETERTYPE.IN);
            DefaultParameter dp3 = new DefaultParameter("TEMP_RESULT", "BINARY_INTEGER", "500", PARAMETERTYPE.OUT);
            ArrayList<DefaultParameter> debugInputValueList = new ArrayList<DefaultParameter>();
            debugInputValueList.add(dp1);
            debugInputValueList.add(dp2);
            debugInputValueList.add(dp3);
            dsr.init(queryResult, debugInputValueList, true);
            dsr1.init(queryResult1, debugInputValueList, true);
            assertTrue(dsr != null);           
            dsr.preDestroy();
            assertNull(dsr.getSummary());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not Expected to come here");
        }
    }
    
    
    @Test
    public void testINIT_FUNC_001_001_Boolean()
    {
        ColumnValueSqlTypeComparator csvBool = new ColumnValueSqlTypeComparator(Types.BOOLEAN);
        assertEquals(1, csvBool.compare("true", "false"));
        assertEquals(-1, csvBool.compare("false", "true"));
        assertEquals(0,csvBool.compare(1, 2));
        try
        {
            csvBool.compare("random", "NULL");            
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }
    
    @Test
    public void testINIT_FUNC_001_001_Double()
    {
        ColumnValueSqlTypeComparator csvBool = new ColumnValueSqlTypeComparator(Types.DOUBLE);
        assertEquals(1, csvBool.compare("33.67", "23.67"));
        assertEquals(-1, csvBool.compare("25.67", "35.67"));
        assertEquals(1, csvBool.compare("+33.67", "-23.67"));
        assertEquals(1, csvBool.compare("-25.67", "-35.67"));
        assertEquals(1,csvBool.compare(1, 2));
        try
        {
            csvBool.compare("random", "NULL");            
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }
    
    @Test
    public void testINIT_FUNC_001_001_Float()
    {
        ColumnValueSqlTypeComparator csvBool = new ColumnValueSqlTypeComparator(Types.FLOAT);
        assertEquals(1, csvBool.compare("33.67", "23.67"));
        assertEquals(-1, csvBool.compare("25.67", "35.67"));
        assertEquals(1,csvBool.compare(1, 2));
        try
        {
            csvBool.compare("random", "NULL");            
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }
    
    @Test
    public void testINIT_FUNC_001_001_Long()
    {
        ColumnValueSqlTypeComparator csvBool = new ColumnValueSqlTypeComparator(Types.BIGINT);
        assertEquals(1, csvBool.compare("3399999", "34560"));
        assertEquals(-1, csvBool.compare("3456", "800000000000000000"));
        assertEquals(1,csvBool.compare(1, 2));
        try
        {
            csvBool.compare("77.0", "23.0");            
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }
    
    @Test
    public void testINIT_FUNC_001_001_TimeStamp()
    {
        ColumnValueSqlTypeComparator csvBool = new ColumnValueSqlTypeComparator(Types.TIMESTAMP);
        assertEquals(1, csvBool.compare("02-04-2013 11:35:42", "03-04-2013 11:35:42"));
        assertEquals(1, csvBool.compare("03-04-2013 11:35:42", "02-04-2013 11:35:42"));
        assertEquals(1, csvBool.compare("02-04-2013 11:40:42", "02-04-2013 11:35:42"));
        assertEquals(1, csvBool.compare("11:35:42", "11:35:45"));
        assertEquals(1,csvBool.compare(1, 2));
        try
        {
            csvBool.compare("random", "NULL");            
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }
    
    @Test
    public void testINIT_FUNC_001_001_Time()
    {
        ColumnValueSqlTypeComparator csvBool = new ColumnValueSqlTypeComparator(Types.TIME);
        assertEquals(1, csvBool.compare("02-04-2013 11:35:42", "03-04-2013 11:35:42"));
        assertEquals(1, csvBool.compare("03-04-2013 11:35:42", "02-04-2013 11:35:42"));
        assertEquals(1, csvBool.compare("02-04-2013 11:40:42", "02-04-2013 11:35:42"));
        assertEquals(1, csvBool.compare("11:35:42", "11:35:42"));
        assertEquals(1,csvBool.compare(1, 2));
        try
        {
            csvBool.compare("random", "NULL");            
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }
    
    @Test
    public void testINIT_FUNC_001_001_Date()
    {
        ColumnValueSqlTypeComparator csvBool = new ColumnValueSqlTypeComparator(Types.DATE);
        assertEquals(1, csvBool.compare("2015-03-31", "2015-02-31"));
        assertEquals(-1, csvBool.compare("2015-01-23", "2015-03-31"));
        assertEquals(1,csvBool.compare(1, 2));
        try
        {
            csvBool.compare("03-04-2013:00:00:00", "02-04-2013:00:00:00");            
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }

    private void fetchALLDBMSJobs() {
        String query = "SELECT job,log_user,"
                + "priv_user,dbname,last_date,this_date,next_date,broken,interval,failures,what FROM USER_JOBS where dbname = ?";
        MockResultSet functionParamRs = preparedstatementHandler.createResultSet();
        functionParamRs.addColumn("job");
        functionParamRs.addColumn("log_user");
        functionParamRs.addColumn("priv_user");
        functionParamRs.addColumn("dbname");
        functionParamRs.addColumn("last_date");
        functionParamRs.addColumn("this_date");
        functionParamRs.addColumn("next_date");
        functionParamRs.addColumn("broken");
        functionParamRs.addColumn("interval");
        functionParamRs.addColumn("failures");
        functionParamRs.addColumn("what");
        functionParamRs.addRow(new Object[] {1, "DSUSER", "DSUSER", "DSUSER", "2020-2-11 00:00:00.0", null,
            "2020-2-11 00:00:00.0", "N", "sysdate", 0, "function();"});
        preparedstatementHandler.prepareResultSet(query, functionParamRs);
    }
    
    private void fetchALLSynonyms() {
        String fetchAllsynonys = SynonymUtil.FETCH_SYNONYM_STATEMENT;
        MockResultSet fetchAllsynonysRS = preparedstatementHandler.createResultSet();
        fetchAllsynonysRS.addColumn("synonym_name");
        fetchAllsynonysRS.addColumn("owner");
        fetchAllsynonysRS.addColumn("schema_name");
        fetchAllsynonysRS.addColumn("table_name");
        fetchAllsynonysRS.addRow(new Object[] {"syn1", "user", "user", "tbl1"});
        preparedstatementHandler.prepareResultSet(fetchAllsynonys, fetchAllsynonysRS);
        
        String refresh = SynonymMetaData.REFRESH_SYNONYM_STATEMENT;
        MockResultSet fetchAllsynonysRefresh = preparedstatementHandler.createResultSet();
        fetchAllsynonysRefresh.addColumn("synonym_name");
        fetchAllsynonysRefresh.addColumn("owner");
        fetchAllsynonysRefresh.addColumn("schema_name");
        fetchAllsynonysRefresh.addColumn("table_name");
        fetchAllsynonysRefresh.addRow(new Object[] {"syn1", "user", "user", "tbl1"});
        preparedstatementHandler.prepareResultSet(refresh, fetchAllsynonysRefresh);
    }
    
    @Test
    public void test_AutoRefresh_01() {
        Database database = connProfCache.getDbForProfileId(profileId);
        RefreshObjectDetails refreshObj = new RefreshObjectDetails();
        Namespace ns1 = new UserNamespace(6, "ns1", database);
        refreshObj.setDesctNamespace(ns1);
        assertNotNull(refreshObj.getDesctNamespace());
        refreshObj.setObjToBeRefreshed(ns1);
        assertNotNull(refreshObj.getObjToBeRefreshed());
        refreshObj.setOperationType("NameSpace");
        assertEquals(refreshObj.getOperationType(), "NameSpace");
        refreshObj.setObjectName("table1");
        assertEquals(refreshObj.getObjectName(), "table1");
        refreshObj.setParent(ns1);
        assertNotNull(refreshObj.getParent());
        refreshObj.setNamespace(ns1);
        assertNotNull(refreshObj.getNamespace());
        assertNotNull(refreshObj.getClone());
    }
    
    @Test
    public void test_ConstraintInfo() {
        ConstraintInfo info = new ConstraintInfo();
        info.setConstraintExpr("where");
        info.setColumns("col1");
        info.setConstraintType("conditional");
        info.setConstraintName("WHERE");
        info.setTablespace("TablseSpace1");
        info.setConsSchema("DS_USER");
        info.setDeferred(true);
        assertEquals(info.getConstraintExpr(), "where");
        assertEquals(info.getColumns(), "col1");
        assertEquals(info.getConstraintType(), "conditional");
        assertEquals(info.getConstraintName(), "WHERE");
        assertEquals(info.getTableSpace(), "TablseSpace1");
        assertEquals(info.getConsSchema(), "DS_USER");
        assertTrue(info.isDeferred());
    }
    
    @Test
    public void test_TerminalExecutionConnectionInfra() {
        Database database = connProfCache.getDbForProfileId(profileId);
        TerminalExecutionConnectionInfra infra = new TerminalExecutionConnectionInfra();
        try {
            infra.setConnection(database.getConnectionManager().getFreeConnection());
            assertNotNull(infra.getConnection());
            infra.setDatabase(database);
            assertNotNull(infra.getDatabase());
            assertTrue(infra.isConnected());
            infra.releaseConnection();
            infra.setReconnectOnTerminal(true);
            assertTrue(infra.isReconnectOnTerminal());
            
            
        } catch (MPPDBIDEException e) {
            fail("not expected to come here");
        }
    }
    
    @Test
    public void test_DSObjectPropertiesGridDataRow() {
        Object[] row = new Object[] {"row1"};
        DSObjectPropertiesGridDataRow gridRow = new DSObjectPropertiesGridDataRow(row);
        gridRow.getValues();
        gridRow.getValue(0);
    }
    
    @Test
    public void test_AutoRefresh_02() {
        Database database = connProfCache.getDbForProfileId(profileId);
        RefreshObjectDetails refreshObj = new RefreshObjectDetails();
        Namespace ns1 = new UserNamespace(6, "ns1", database);
        refreshObj.setDesctNamespace(ns1);
        refreshObj.setObjToBeRefreshed(ns1);
        refreshObj.setParent(ns1);
        refreshObj.setNamespace(ns1);
        HashSet<Object> listOfObjects = new HashSet<Object>();
        
        refreshObj.setOperationType(MPPDBIDEConstants.CREATE_TABLE);
        refreshObj.setObjectName("table1");
        String newlycreatedTableQuery = "select tbl.relname relname,tbl.parttype parttype,tbl.relnamespace relnamespace"
                + ",tbl.oid oid,ts.spcname as reltablespace,tbl.relpersistence relpersistence,"
                + " d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions"
                + " from pg_class tbl left join (select d.description, d.objoid from pg_description"
                + " d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on "
                + "(tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace"
                + " where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.relname = 'table1'"
                + " and tbl.relnamespace= 6";
        MockResultSet fetchRS = preparedstatementHandler.createResultSet();
        fetchRS.addColumn("");
        fetchRS.addRow(new Object[] {});
        preparedstatementHandler.prepareResultSet(newlycreatedTableQuery, fetchRS);
        
        AutoRefreshQueryFormation.getObjectToBeRefreshed(refreshObj, listOfObjects);
        assertFalse(listOfObjects.isEmpty());
    }
    @Test
    public void test_set_schema_table() {
        Database database = connProfCache.getDbForProfileId(profileId);
        RefreshObjectDetails refreshObj = new RefreshObjectDetails();
        Namespace ns1 = new UserNamespace(6, "ns1", database);
        
        Namespace ns2 = new UserNamespace(7, "ns2", database);
        TableMetaData table=new TableMetaData(1,"table1",ns2,"");
        ns2.addTableToGroup(table);
        
        refreshObj.setDesctNamespace(ns1);
        refreshObj.setObjToBeRefreshed(ns1);
        refreshObj.setParent(ns1);
        refreshObj.setNamespace(ns2);
        HashSet<Object> listOfObjects = new HashSet<Object>();
        
        refreshObj.setOperationType(MPPDBIDEConstants.SET_SCHEMA_TABLE);
        refreshObj.setObjectName("table1");
        String newlycreatedTableQuery = "select tbl.relname relname,tbl.parttype parttype,tbl.relnamespace relnamespace"
                + ",tbl.oid oid,ts.spcname as reltablespace,tbl.relpersistence relpersistence,"
                + " d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions"
                + " from pg_class tbl left join (select d.description, d.objoid from pg_description"
                + " d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on "
                + "(tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace"
                + " where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.relname = 'table1'"
                + " and tbl.relnamespace= 6";
        MockResultSet fetchRS = preparedstatementHandler.createResultSet();
        fetchRS.addColumn("");
        fetchRS.addRow(new Object[] {});
        preparedstatementHandler.prepareResultSet(newlycreatedTableQuery, fetchRS);
        
        AutoRefreshQueryFormation.getObjectToBeRefreshed(refreshObj, listOfObjects);
        assertFalse(listOfObjects.isEmpty());
    }
    @Test
    public void test_AutoRefresh_create_view() {
        Database database = connProfCache.getDbForProfileId(profileId);
        RefreshObjectDetails refreshObj = new RefreshObjectDetails();
        Namespace ns1 = new UserNamespace(6, "ns1", database);
        refreshObj.setDesctNamespace(ns1);
        refreshObj.setObjToBeRefreshed(ns1);
        refreshObj.setParent(ns1);
        refreshObj.setNamespace(ns1);
        HashSet<Object> listOfObjects = new HashSet<Object>();
        
        refreshObj.setOperationType(MPPDBIDEConstants.CREATE_VIEW);
        refreshObj.setObjectName("table1");
        String newlycreatedTableQuery = "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner, c.relkind as relkind FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\") and c.relname='table1' and n.nspname='ns1';";
        MockResultSet fetchRS = preparedstatementHandler.createResultSet();
        fetchRS.addColumn("");
        fetchRS.addRow(new Object[] {});
        preparedstatementHandler.prepareResultSet(newlycreatedTableQuery, fetchRS);
        
        AutoRefreshQueryFormation.getObjectToBeRefreshed(refreshObj, listOfObjects);
        assertFalse(listOfObjects.isEmpty());
    }
    @Test
    public void test_AutoRefresh_setSchema_view() {
        Database database = connProfCache.getDbForProfileId(profileId);
        RefreshObjectDetails refreshObj = new RefreshObjectDetails();
        Namespace ns1 = new UserNamespace(6, "ns1", database);
        
        Namespace ns2 = new UserNamespace(7, "ns2", database);
        ViewMetaData view=new ViewMetaData(1,"table1",ns2,database);
        ns2.addView(view);
        
        refreshObj.setDesctNamespace(ns1);
        refreshObj.setObjToBeRefreshed(ns1);
        refreshObj.setParent(ns1);
        refreshObj.setNamespace(ns2);
        HashSet<Object> listOfObjects = new HashSet<Object>();
        
        refreshObj.setOperationType(MPPDBIDEConstants.SET_SCHEMA_VIEW);
        refreshObj.setObjectName("table1");
        String newlycreatedTableQuery = "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner, c.relkind as relkind FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\") and c.relname='table1' and n.nspname='ns1';";
        MockResultSet fetchRS = preparedstatementHandler.createResultSet();
        fetchRS.addColumn("");
        fetchRS.addRow(new Object[] {});
        preparedstatementHandler.prepareResultSet(newlycreatedTableQuery, fetchRS);
        
        AutoRefreshQueryFormation.getObjectToBeRefreshed(refreshObj, listOfObjects);
        assertFalse(listOfObjects.isEmpty());
    }
    @Test
    public void test_AutoRefresh_alter_table() {
        Database database = connProfCache.getDbForProfileId(profileId);
        RefreshObjectDetails refreshObj = new RefreshObjectDetails();
        Namespace ns1 = new UserNamespace(6, "ns1", database);
        TableMetaData table=new TableMetaData(1,"table1",ns1,"");
        ns1.addTableToGroup(table);
        refreshObj.setDesctNamespace(ns1);
        refreshObj.setObjToBeRefreshed(table);
        refreshObj.setParent(ns1);
        refreshObj.setNamespace(ns1);
        HashSet<Object> listOfObjects = new HashSet<Object>();
        
        refreshObj.setOperationType(MPPDBIDEConstants.ALTER_TABLE);
        refreshObj.setObjectName("table1");
        String newlycreatedTableQuery = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1;";
        MockResultSet fetchRS = preparedstatementHandler.createResultSet();
        fetchRS.addColumn("");
        fetchRS.addRow(new Object[] {});
        preparedstatementHandler.prepareResultSet(newlycreatedTableQuery, fetchRS);
        
        
        String constraint="SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred as deferred, c.convalidated as validate, c.conindid as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c where c.conrelid = 1;";
        MockResultSet fetchRS1 = preparedstatementHandler.createResultSet();
        fetchRS1.addColumn("");
        fetchRS1.addRow(new Object[] {});
        preparedstatementHandler.prepareResultSet(constraint, fetchRS1);
        
        String index="SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef , def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind = 'r' and t.oid = 1;";
        MockResultSet fetchRS2 = preparedstatementHandler.createResultSet();
        fetchRS2.addColumn("");
        fetchRS2.addRow(new Object[] {});
        preparedstatementHandler.prepareResultSet(index, fetchRS2);
        AutoRefreshQueryFormation.getObjectToBeRefreshed(refreshObj, listOfObjects);
        assertFalse(listOfObjects.isEmpty());
    }
    @Test
    public void test_AutoRefresh_alter_View() {
        Database database = connProfCache.getDbForProfileId(profileId);
        RefreshObjectDetails refreshObj = new RefreshObjectDetails();
        Namespace ns1 = new UserNamespace(6, "ns1", database);
        ViewMetaData view=new ViewMetaData(1,"table1",ns1,database);
        ns1.addView(view);
        refreshObj.setDesctNamespace(ns1);
        refreshObj.setObjToBeRefreshed(view);
        refreshObj.setParent(ns1);
        refreshObj.setNamespace(ns1);
        HashSet<Object> listOfObjects = new HashSet<Object>();
        
        refreshObj.setOperationType(MPPDBIDEConstants.ALTER_VIEW);
        refreshObj.setObjectName("table1");
        String newlycreatedTableQuery = "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\") and c.oid=1;";
        MockResultSet fetchRS = preparedstatementHandler.createResultSet();
        fetchRS.addColumn("");
        fetchRS.addRow(new Object[] {});
        preparedstatementHandler.prepareResultSet(newlycreatedTableQuery, fetchRS);
        
        
        String constraint="select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and v.oid = 1 order by v.oid, c.attnum";
        MockResultSet fetchRS1 = preparedstatementHandler.createResultSet();
        fetchRS1.addColumn("");
        fetchRS1.addRow(new Object[] {});
        preparedstatementHandler.prepareResultSet(constraint, fetchRS1);
        
        String index="SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef , def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind = 'r' and t.oid = 1;";
        MockResultSet fetchRS2 = preparedstatementHandler.createResultSet();
        fetchRS2.addColumn("");
        fetchRS2.addRow(new Object[] {});
        preparedstatementHandler.prepareResultSet(index, fetchRS2);
        AutoRefreshQueryFormation.getObjectToBeRefreshed(refreshObj, listOfObjects);
        assertFalse(listOfObjects.isEmpty());
    }
    @Test
    public void test_AutoRefresh_drop_View() {
        Database database = connProfCache.getDbForProfileId(profileId);
        RefreshObjectDetails refreshObj = new RefreshObjectDetails();
        Namespace ns1 = new UserNamespace(6, "ns1", database);
        ViewMetaData view=new ViewMetaData(1,"table1",ns1,database);
        ns1.addView(view);
        refreshObj.setDesctNamespace(ns1);
        refreshObj.setObjToBeRefreshed(view);
        refreshObj.setParent(ns1);
        refreshObj.setNamespace(ns1);
        HashSet<Object> listOfObjects = new HashSet<Object>();
        
        refreshObj.setOperationType(MPPDBIDEConstants.DROP_VIEW);
        refreshObj.setObjectName("table1");
        
        AutoRefreshQueryFormation.getObjectToBeRefreshed(refreshObj, listOfObjects);
        assertFalse(listOfObjects.isEmpty());
    }
    @Test
    public void test_AutoRefresh_drop_table() {
        Database database = connProfCache.getDbForProfileId(profileId);
        RefreshObjectDetails refreshObj = new RefreshObjectDetails();
        Namespace ns1 = new UserNamespace(6, "ns1", database);
        TableMetaData table=new TableMetaData(1,"table1",ns1,"");
        ns1.addTableToGroup(table);
        refreshObj.setDesctNamespace(ns1);
        refreshObj.setObjToBeRefreshed(table);
        refreshObj.setParent(ns1);
        refreshObj.setNamespace(ns1);
        HashSet<Object> listOfObjects = new HashSet<Object>();
        
        refreshObj.setOperationType(MPPDBIDEConstants.DROP_TABLE);
        refreshObj.setObjectName("table1");
        
        AutoRefreshQueryFormation.getObjectToBeRefreshed(refreshObj, listOfObjects);
        assertFalse(listOfObjects.isEmpty());
    }
    
    
    @Test
    public void test_SynonymInfo_01() {
        fetchALLSynonyms();
        Database database = connProfCache.getDbForProfileId(profileId);
        Namespace ns1 = new UserNamespace(6, "ns1", database);
        try {
            ns1.loadSynonyms(database.getConnectionManager().getFreeConnection());
        } catch (MPPDBIDEException e) {
            e.printStackTrace();
        }
        SynonymInfo info = new SynonymInfo(ns1.getSynonymGroup());
        info.setObjectName("objName");
        assertNotNull(info.getObjectName());
        info.setObjectOwner("objOwner");
        assertNotNull(info.getObjectOwner());
        info.setObjectType("objType");
        assertNotNull(info.getObjectType());
        info.setOwner("owner");
        assertNotNull(info.getOwner());
        info.setSynonymName("syn1");
        assertNotNull(info.getSynonymName());
        info.setReplaceIfExist(true);
        assertNotNull(info.getNamespace());
        assertNotNull(info.getNameSpaceName());
        assertNotNull(info.generateCreateSynonymSql());

        SynonymWrapper wrapper = new SynonymWrapper(ns1.getSynonymGroup(), info);
        assertNotNull(wrapper.getMetadata());
        assertNotNull(wrapper.getMetadata().getDatabaseName());
        assertNotNull(wrapper.getMetadata().getServer());
        assertNotNull(wrapper.getMetadata().getServerName());
        assertNotNull(wrapper.getMetadata().getDatabase());
        assertNotNull(wrapper.getMetadata().getDropName());
        assertNotNull(wrapper.getMetadata().getWindowTitleName());
        assertNotNull(wrapper.getDatabase());

        String fetchsynonyProp = "select * from sys.all_synonyms sy1 where sy1.schema_name = ? and sy1.synonym_name = ?";
        MockResultSet fetchsynonyPropRS = preparedstatementHandler.createResultSet();
        fetchsynonyPropRS.addColumn("synonym_name");
        fetchsynonyPropRS.addColumn("owner");
        fetchsynonyPropRS.addColumn("schema_name");
        fetchsynonyPropRS.addColumn("table_name");
        fetchsynonyPropRS.addRow(new Object[] {"syn1", "user", "user", "tbl1"});
        preparedstatementHandler.prepareResultSet(fetchsynonyProp, fetchsynonyPropRS);
        try {
            DBConnection conn = wrapper.getDatabase().getConnectionManager().getFreeConnection();
            wrapper.getMetadata().refresh(conn);
            wrapper.executeComposeQuery(conn);
            wrapper.refresh(conn);
            wrapper.getMetadata().dropSynonym(conn, false);
            PropertiesSynonymImpl impl = new PropertiesSynonymImpl(wrapper.getMetadata());
            assertNotNull(impl.getDatabase());
            assertNotNull(impl.getHeader());
            assertNotNull(impl.getObjectName());
            assertNotNull(impl.getUniqueID());
            assertNotNull(impl.getAllProperties(conn));

        } catch (MPPDBIDEException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_SynonymInfo_02() {
        fetchALLSynonyms();
        Database database = connProfCache.getDbForProfileId(profileId);
        Namespace ns1 = new UserNamespace(6, "ns1", database);
        try {
            ns1.loadSynonyms(database.getConnectionManager().getFreeConnection());
        } catch (MPPDBIDEException e) {
            e.printStackTrace();
        }
        String all = "select  tbl.relname relname from pg_class tbl left join"
                + " pg_partition part on(tbl.oid=part.parentid and part.parttype='r')"
                + " where tbl.relkind = 'r' and tbl.relnamespace = 6 UNION SELECT"
                + " c.relname AS relname FROM pg_class c WHERE (c.relkind = 'f' :: char)"
                + " and c.relnamespace = 6 UNION SELECT c.relname AS relname "
                + "FROM pg_class c WHERE (c.relkind = 'v' :: char or c.relkind = 'm' :: char) and c.relnamespace = 6"
                + "  UNION SELECT pr.proname relname FROM pg_proc pr JOIN pg_type typ ON"
                + " typ.oid = prorettype JOIN pg_namespace typns ON typns.oid = typ.typnamespace"
                + " JOIN pg_language lng ON lng.oid = prolang and pronamespace = 6 and"
                + " has_function_privilege(pr.oid, 'EXECUTE') ORDER BY relname";
        MockResultSet fetchAllRS = preparedstatementHandler.createResultSet();
        fetchAllRS.addColumn("relname");
        fetchAllRS.addRow(new Object[] {"table1"});
        fetchAllRS.addRow(new Object[] {"function1"});
        fetchAllRS.addRow(new Object[] {"view1"});
        fetchAllRS.addRow(new Object[] {"materview1"});
        preparedstatementHandler.prepareResultSet(all, fetchAllRS);

        String funcProc = "SELECT pr.proname relname FROM pg_proc pr JOIN pg_type typ "
                + "ON typ.oid = prorettype JOIN pg_namespace typns ON typns.oid = typ.typnamespace"
                + " JOIN pg_language lng ON lng.oid = prolang and pronamespace = 6 "
                + "and has_function_privilege(pr.oid, 'EXECUTE') ORDER BY relname";
        MockResultSet fetchfuncProcRS = preparedstatementHandler.createResultSet();
        fetchfuncProcRS.addColumn("relname");
        fetchfuncProcRS.addRow(new Object[] {"function1"});
        preparedstatementHandler.prepareResultSet(funcProc, fetchfuncProcRS);

        String table = "select  tbl.relname relname from pg_class tbl left join pg_partition"
                + " part on(tbl.oid=part.parentid and part.parttype='r') where tbl.relkind = 'r'"
                + " and tbl.relnamespace = 6 UNION SELECT c.relname AS relname FROM pg_class c"
                + " WHERE (c.relkind = 'f' :: char) and c.relnamespace = 6";
        MockResultSet fetchtableRS = preparedstatementHandler.createResultSet();
        fetchtableRS.addColumn("relname");
        fetchtableRS.addRow(new Object[] {"table1"});
        preparedstatementHandler.prepareResultSet(table, fetchtableRS);

        String view = "SELECT c.relname AS relname FROM pg_class c WHERE (c.relkind = 'v' :: char or c.relkind = 'm' :: char)"
                + " and c.relnamespace = 6 ";
        MockResultSet fetchviewRS = preparedstatementHandler.createResultSet();
        fetchviewRS.addColumn("relname");
        fetchviewRS.addRow(new Object[] {"view1"});
        fetchviewRS.addRow(new Object[] {"materview1"});
        preparedstatementHandler.prepareResultSet(view, fetchviewRS);

        DBConnection conn;
        try {
            conn = database.getConnectionManager().getFreeConnection();
            assertNotNull(SynonymObjectGroup.fetchObjectName(ns1, conn, "owner", MPPDBIDEConstants.PRIVILEGE_ALL));
            assertNotNull(SynonymObjectGroup.fetchObjectName(ns1, conn, "owner", "Functions/Procedures"));
            assertNotNull(SynonymObjectGroup.fetchObjectName(ns1, conn, "owner", "Regular Tables"));
            assertNotNull(SynonymObjectGroup.fetchObjectName(ns1, conn, "owner", "Views"));
            assertNotNull(SynonymObjectGroup.fetchObjectName(ns1, conn, "owner", ""));
        } catch (MPPDBIDEException e) {
            fail("Not Expected to come here");
        }
        SynonymObjectGroup objGrp = ns1.getSynonymGroup();
        assertTrue(objGrp.equals(objGrp));
        assertNotNull(objGrp.hashCode());
    }
}
