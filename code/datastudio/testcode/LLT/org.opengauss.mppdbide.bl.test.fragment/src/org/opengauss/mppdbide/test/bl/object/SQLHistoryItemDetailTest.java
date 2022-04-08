package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
//import com.google.gson.JsonSyntaxException;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.sqlhistory.QueryExecutionSummary;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryItem;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryItemDetail;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class SQLHistoryItemDetailTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;

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
        IBLPreference sysPref = new MockBLPreferenceImpl();
        
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

       
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
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
        profileId = connProfCache.initConnectionProfile(serverInfo,status);
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
    public void test_SQLHistory_Item_Detail_01()
    {
        try
        {
            String input = "2016-12-16 18:29:09";

            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
           Date date= sdf1.parse(input);
           String dt=sdf1.format(date);
        	QueryExecutionSummary query = new QueryExecutionSummary("postgres", "ds", "Select * from pg_am;", true, dt, 109, 0);
        	
        	SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,20);
        	assertNotNull(item);
        	System.out.println("As Expected...");
        	String qry = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes "
                    + "from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) "
                    + "from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) "
                    + "from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) "
                    + "from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) "
                    + "left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.oid = "
                    + 10 + ';';
            QueryExecutionSummary query1 = new QueryExecutionSummary("postgres", "ds", qry, true, dt, 109, 0);
            
            SQLHistoryItemDetail item2 = new SQLHistoryItemDetail(query1,20);
            assertNotNull(item2);
            System.out.println("As Expected...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_SQLHistory_Item_Detail_02()
    {
        try
        {
            String input = "2016-12-16 18:29:09";

            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
           Date date= sdf1.parse(input);
           String dt=sdf1.format(date);
        	
        	QueryExecutionSummary query = new QueryExecutionSummary("postgres", "ds", "select * from pg_class", true, dt, 109, 0);
        	
        	SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,22);
        	//item1.persist();
        	item.getFileName();
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
    public void test_SQLHistory_Item_Detail_03()
    {
        try
        {
            String input = "2016-12-16 18:29:09";

            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
           Date date= sdf1.parse(input);
           String dt=sdf1.format(date);
        	
        	QueryExecutionSummary query = new QueryExecutionSummary("postgres", "ds", "select * from pg_class", true, dt, 109, 0);
        	
        	SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,22);
        	item.getComparator();
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
    public void test_SQLHistory_Item_Detail_04()
    {
        try
        {
            String input = "2016-12-16 18:29:09";

            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
           Date date= sdf1.parse(input);
           String dt=sdf1.format(date);
        	
        	QueryExecutionSummary query = new QueryExecutionSummary("postgres", "ds", "select * from pg_class", true, dt, 109, 0);
        	
        	SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,22);
        	
        	item.getSerializedContent();
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
    public void test_SQLHistory_Item_Detail_05()
    {
        try
        {
            String input = "2016-12-16 18:29:09";

            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
           Date date= sdf1.parse(input);
           String dt=sdf1.format(date);
        	
        	QueryExecutionSummary query = new QueryExecutionSummary("postgres", "ds", "select * from pg_class", true, dt, 109, 0);
        	
        	SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,22);
        	item.hashCode();
        	System.out.println("As Expected...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    @Test
    public void test_SQLHistory_Item_Detail_06()
    {
        try
        {
            String input = "2016-12-16 18:29:09";

            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
           Date date= sdf1.parse(input);
           String dt=sdf1.format(date);
        	
        	QueryExecutionSummary query = new QueryExecutionSummary("postgres", "ds", "select * from pg_class", false, dt, 109, 0);
        	
        	SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,22);
        	item.equals(null);
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
    public void test_SQLHistory_Item_Detail_07()
    {
        try
        {
            String input = "2016-12-16 18:29:09";

            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
           Date date= sdf1.parse(input);
           String dt=sdf1.format(date);
        	
        	QueryExecutionSummary query = new QueryExecutionSummary("postgres", "ds", "select * from pg_class", true, dt, 109, 0);
        	
        	SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,22);
        	SQLHistoryItem qryItem = new SQLHistoryItem(query,4);
        	item.equals(item.getDatabaseName());
        	item.equals(item);
        	item.equals(qryItem);
        	assertEquals("ds", item.getProfileId());
        	System.out.println("As Expected...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
  
  
    @Test
    public void test_SQLHistory_Item_Detail_10()
    {
        try
        {
            String input = "2016-12-16 18:29:09";

            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
           Date date= sdf1.parse(input);
           String dt=sdf1.format(date);
        	QueryExecutionSummary query = new QueryExecutionSummary("postgres", "ds", "select * from pg_class", true, dt, 109, 0);
        	
        	SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,22);
        	SQLHistoryItem qryItem = new SQLHistoryItem(query,4);
        	qryItem.setDatabaseName("postgres");
        	qryItem.setProfileId("ds");
        	qryItem.setQuery("select * from pg_class");
        	
        	qryItem.equals(qryItem);
        	qryItem.equals(qryItem.getDatabaseName());
        	qryItem.equals(item);
        	assertNotNull(qryItem);
        	
        	System.out.println("As Expected...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    @Test
    public void test_SQLHistory_Item_Detail_10_1()
    {
        try
        {
            String input = "2016-12-16 18:29:09";

            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
           Date date= sdf1.parse(input);
           String dt=sdf1.format(date);
            QueryExecutionSummary query = new QueryExecutionSummary("postgres", "ds", "select * from pg_class", true, dt, 109, 0);
            
            SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,22);
            SQLHistoryItem qryItem = new SQLHistoryItem(query,4);
            qryItem.setDatabaseName("postgres");
            qryItem.setProfileId("ds");
            qryItem.setQuery("select * from pg_class");
            System.out.println("fetched records size is " + item.getResultSetSize());
            System.out.println(item.getExecutionTime());
            qryItem.setResultSetSize(query.getNumRecordsFetched());
           // SQLHistoryItemDetail item1 = new SQLHistoryItemDetail(item);
            String dtt = qryItem.getExecutionTime();
            System.out.println(dtt.toString());
            qryItem.equals(qryItem);
            boolean succ = qryItem.getFinalStatus();
            qryItem.setFinalStatus(succ);
            System.out.println("Status " + succ);
            qryItem.equals(qryItem.getDatabaseName());
            qryItem.equals(qryItem.getExecutionTime());
            qryItem.equals(item);
            //qryItem.equals(item1);
            assertNotNull(qryItem);
            
            System.out.println("As Expected...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    @Test
    public void test_SQLHistory_Item_Detail_11()
    {
        try
        {
            String input = "2016-12-16 18:29:09";

            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
           Date date= sdf1.parse(input);
           String dt=sdf1.format(date);
        	QueryExecutionSummary query = new QueryExecutionSummary("postgres", "ds", "select * from pg_class", true, dt, 109, 0);
        	
        	SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,22);
        	SQLHistoryItem qryItem = new SQLHistoryItem(query,4);
        	qryItem.setDatabaseName("postgress");
        	
        	qryItem.equals(qryItem);
        	qryItem.equals(qryItem.getDatabaseName());
        	qryItem.equals(item);
        	assertNotNull(qryItem);
        	System.out.println("As Expected...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    @Test
    public void test_SQLHistory_ITEMDEtail_Test_02()
    {
        try
        {
            String input = "2016-12-16 18:29:09";

            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
           Date date= sdf1.parse(input);
           String dt=sdf1.format(date);

            QueryExecutionSummary query = new QueryExecutionSummary("postgres",
                    "ds", "select * from pg_class", true, dt, 109, 0);
            QueryExecutionSummary query1 = new QueryExecutionSummary("postgres",
                    "ds", "select * from pg_class", true, dt,109, 0);

            SQLHistoryItemDetail queryInfra = new SQLHistoryItemDetail(
                    query,22);
            SQLHistoryItemDetail queryInfra1 = new SQLHistoryItemDetail(
                    query1,22);
            queryInfra.equals(null);
            queryInfra.equals(new Object());
            Object sr = new Object();
            sr= new SQLHistoryItemDetail(
                    query,2);
            queryInfra.equals(sr);
            
            Object sr2 = new Object();
            sr2= new SQLHistoryItemDetail(
                    query,22);
            queryInfra.equals(sr2);
            queryInfra1.equals(sr2);
            queryInfra1.equals(sr);
            queryInfra1.equals(sr);
            queryInfra.hashCode();
          
            SQLHistoryItemDetail item = new SQLHistoryItemDetail(query,22);

            assertNotNull(item);
            System.out.println("As Expected...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
