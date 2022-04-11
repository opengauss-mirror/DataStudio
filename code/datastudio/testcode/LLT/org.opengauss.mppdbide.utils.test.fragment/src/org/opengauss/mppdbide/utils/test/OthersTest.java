package org.opengauss.mppdbide.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

/*import org.opengauss.mppdbide.adapter.gauss.Activator;
import org.opengauss.mppdbide.adapter.gauss.StmtExecutor;*/
/*import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ProfileDiskUtility;
import org.opengauss.mppdbide.bl.serverdatacache.QueryBuilder;
import org.opengauss.mppdbide.bl.serverdatacache.QueryResult;
import org.opengauss.mppdbide.bl.serverdatacache.SOURCE_VERSION_CHECK_FLAG;
import org.opengauss.mppdbide.bl.serverdatacache.Server.SAVE_PRD_OPTIONS;
import org.opengauss.mppdbide.bl.serverdatacache.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.sqlhistory.QueryExecutionSummary;
import org.opengauss.mppdbide.bl.sqlhistory.QueryExecutionSummaryInfra;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryCore;*/
import org.opengauss.mppdbide.utils.CustomStringUtility;
import org.opengauss.mppdbide.utils.DsEncodingEnum;
import org.opengauss.mppdbide.utils.ExpressionVisitorAdapterWrap;
import org.opengauss.mppdbide.utils.MemoryCleaner;
import org.opengauss.mppdbide.utils.QueryResultType;
import org.opengauss.mppdbide.utils.ResultSetDatatypeMapping;
import org.opengauss.mppdbide.utils.SQLKeywords;
import org.opengauss.mppdbide.utils.SSLUtility;
import org.opengauss.mppdbide.utils.SelectVisitorWrap;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFolderDeleteUtility;
import org.opengauss.mppdbide.utils.files.FileValidationUtils;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.utils.observer.DSEventWithCount;
import org.opengauss.mppdbide.utils.security.AESAlgorithmUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;

import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.statement.select.SetOperationList;

public class OthersTest extends BasicJDBCTestCaseAdapter{
	PreparedStatementResultSetHandler preparedstatementHandler = null;
	
	char[] password;
	
	@Before
	public void setUp() throws Exception {
		MPPDBIDELoggerUtility.setArgs(null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQueryResultType() {
		QueryResultType[] types = QueryResultType.values();
		assertTrue(types.length == 4);
		assertTrue(QueryResultType.valueOf("RESULTTYPE_DML").toString()
				.equalsIgnoreCase("RESULTTYPE_DML"));
	}

	/*
	 * @Test public void testQuerySourceType() { QuerySourceType[] types =
	 * QuerySourceType.values(); assertTrue(types.length == 2);
	 * assertTrue(QuerySourceType
	 * .valueOf("QuerySource_TRIGGER").toString().equalsIgnoreCase
	 * ("QuerySource_TRIGGER")); }
	 */

	@Test
	public void testMessageconfigLoader() {
		MessageConfigLoader configLoader = new MessageConfigLoader();
		assertTrue(null != configLoader);
	}

	@Test
	public void testLoggerUtility() {
		MPPDBIDELoggerUtility loggerUtility = new MPPDBIDELoggerUtility();
		assertTrue(null != loggerUtility);
	}

	/*@Test
	public void testSourceCodeVersion() {
		SOURCE_VERSION_CHECK_FLAG[] types = SOURCE_VERSION_CHECK_FLAG.values();
		assertTrue(types.length == 3);
		assertTrue(SOURCE_VERSION_CHECK_FLAG.valueOf("NOT_CHANGED").toString()
				.equalsIgnoreCase("NOT_CHANGED"));
	}*/

	@Test
	public void testStatusMessage_01() {
		StatusMessageList.getInstance();
		assertTrue(null!=StatusMessageList.class);
		StatusMessageList.getInstance().push(null);
		StatusMessageList.getInstance().pop(null);
		StatusMessageList.getInstance().isEmpty();

	}

	@Test
	public void testStatusMessage_02() {
		SSLUtility.putSSLLoginStatus("ssl", true);
		SSLUtility.getStatus("ssl");
		SSLUtility.getStatus("sslTest");
		assertTrue(null!=SSLUtility.class);

	}

	@Test
	public void testStatusMessage_03() {
		StatusMessageList.getInstance();
		assertTrue(null!=StatusMessageList.class);
		StatusMessageList.getInstance().push(null);
		StatusMessageList.getInstance().pop(null);
		StatusMessageList.getInstance().push(null);
		StatusMessageList.getInstance().pop();
	}



	@Test
	public void testQueryExecSummary_01() {
		try {
			String dbname = "postgres";

			String profilename = "test_connection";

			boolean executionResult = true;

			
            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
           String startDate= sdf1.format(new Date());
           
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String elapsedTime = sdf.format(new Date());

			int numRecordsFetched = 2;
			long elapsedTimeLong = 108;
			assertTrue(null!=StatusMessageList.class);

			/*QueryExecutionSummary firstsummary = new QueryExecutionSummary(
					dbname, profilename, "select * from pg_tables",
					executionResult, startDate, elapsedTimeLong, numRecordsFetched);
			firstsummary.setElapsedTime(elapsedTime);
			firstsummary.getExecutionTime();

			QueryExecutionSummary secondsummary = new QueryExecutionSummary(
					dbname, profilename, "select * from pg_views",
					executionResult, startDate, elapsedTimeLong, numRecordsFetched);

			SQLHistoryCore sqlhistorycore = new SQLHistoryCore(
					firstsummary.getProfileId(), "test_connection/history",
					2, false);

			sqlhistorycore.addQuerySummary(firstsummary);

			sqlhistorycore.addQuerySummary(secondsummary);

			IEventBroker IEventBroker = null;
			QueryExecutionSummaryInfra infra = new QueryExecutionSummaryInfra(firstsummary, IEventBroker , "new event") ;
			infra.getBroker();
			infra.getSummary();
			infra.getEventName();
			infra.setNumRecordsFetched(10);
			System.out.println(infra.getSummary()+","+infra.getEventName());*/

		} catch (Exception e) {
			fail(" not expected");

			e.printStackTrace();
		}

	}
	 @Test
	    public void testTTA_Activator_Gauss()
	    {
	        try
	        {

	           /* BundleContext bundleActivator = Activator.getContext();
	            Activator act =new Activator();
	            act.start(bundleActivator);
	            act.stop(bundleActivator);*/
	        }

	        catch (Exception e)
	        {
	            e.printStackTrace();
	            fail(e.getMessage());
	        }
	    }
	 @Test
	    public void testTTA_QueryResult_getReturnType()
	    {
	        try
	        {
	        	   // StmtExecutor executor = null;  //dependent on adapter Gauss
	        	   /* QueryResult qryRes = new QueryResult(executor);
	        	    assertNull(qryRes.getReturnType());
	        	    assertNull(qryRes.getResultsSet());*/   //dependent on BL
	        }

	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	 @Test
	    public void testTTA_DSFolderDeleteUtility_visitFileFailed()
	    {
	        try
	        {
	        	Path file = null;
	        	IOException exc = null;
	        	DSFolderDeleteUtility dsfdUtil = new DSFolderDeleteUtility();
	        	assertNotNull(dsfdUtil.visitFileFailed(file, exc));
	        }

	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	
	 @Test
	 public void testTTA_ServerObject_getTypeLabel()
	 {
		 
	        try
	        {
	        	/*ServerObject serObj = new ServerInst(OBJECTTYPE.NODE);
	        	assertNotNull(serObj.getTypeLabel());*/
	        }

	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	 }
	 
	 @Test
     public void testTTA_ServerObject_getTypeLabel2()
     {
         
            try
            {
                /*ServerObject serObj = new ServerInst(OBJECTTYPE.FOREIGN_TABLE_HDFS);
                assertNotNull(serObj.getTypeLabel());*/
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
     }
	/* private final class ServerInst extends ServerObject
	 {
		 public ServerInst(OBJECTTYPE type)
		 {
			 super(type);
		 }
	 }*/
	 
	 @Test
	    public void test_BL_ProfileDiskUtility_FUNC_00_06()
	    {/*
	    try
	    {
	         String userName = System.getProperty("user.name"); 
	    	 StringBuilder packagePath = new StringBuilder();
	         packagePath.append(".");
	         SecureUtil.setPackagePath(packagePath);
	    ServerConnectionInfo serverInfoloc = new ServerConnectionInfo();
	    serverInfoloc.setConectionName("NEWCONTOTEST");
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
	    //content = content.replaceAll("\"conectionName\""," ");
	    Files.deleteIfExists(Paths.get("./Profile/NEWCONTOTEST/connection.properties"));
		ISetFilePermission permission = new SetFilePermission();
		permission.createFileWithPermission("./Profile/NEWCONTOTEST/connection.properties", false, DEFAULT_PERMISSIONS, true);
	    Files.write(Paths.get("./" + userName + "/Profile/"+profileFolderName +"/connection.properties"), content.getBytes(charset));
	    Path filepath = Paths.get("./" + userName + "/Profile/"+profileFolderName +"/connection.properties");
	    ServerConnectionInfo info=utility.readProfileFromFile(filepath);
	    List<String> exceptionList=utility.getExceptionList();
	    exceptionList.get(0);
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
	 public void testSanitizeExportFilename_001()
	 {
	     assertEquals("abc", CustomStringUtility.sanitizeExportFileName("abc"));
	     assertEquals("a_b_c", CustomStringUtility.sanitizeExportFileName("a b c"));
	     assertEquals("abc", CustomStringUtility.sanitizeExportFileName("a:b:c"));
	     assertEquals("a[bc", CustomStringUtility.sanitizeExportFileName("a[b/c"));
	     assertEquals("abc", CustomStringUtility.sanitizeExportFileName("a<b>c"));
	     assertEquals("abc", CustomStringUtility.sanitizeExportFileName("a\"b|c"));
	     assertEquals("a.b_c", CustomStringUtility.sanitizeExportFileName("a.b c"));
	 }
	 
    @Test
    public void test_Message_001()
    {
        try
        {
            assertNotNull(Message.getInfo("Test Info"));
            assertNotNull(Message.getWarn("Test Warn"));
            assertNotNull(Message.getError("Test Error"));
            assertNotNull(Message.getInfoFromConst("Test Info Message"));
            assertNotNull(Message.getWarnFromConst("Test Warn Message"));
            assertNotNull(Message.getErrorFromConst("Test Error Message"));
        }
        catch (Exception e)
        {
            System.out.println(
                    "OthersTest.test_MPPDBIDEException_001():Not expected to come here");
        }
    }
    
    @Test
    public void test_status_message(){
    	StatusMessage stsMsg=new StatusMessage("Done");
    	assertEquals("Done",stsMsg.getMessage());
    	stsMsg.setMessage("Done");
    }
    
    @Test
    public void test_get_reserved_words(){
    	String[] reservedWords={"ALL", "ANALYSE", "ANALYZE", "AND", "ANY", "ARRAY",
    	        "AS", "ASC", "ASYMMETRIC", "AUTHID", "BOTH", "CASE", "CAST", "CHECK", "COLLATE", "COLUMN", "CONSTRAINT",
    	        "CREATE", "CURRENT_CATALOG", "CURRENT_DATE", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
    	        "CURRENT_USER", "DEFAULT", "DEFERRABLE", "DESC", "DISTINCT", "DO", "ELSE", "END", "EXCEPT", "FALSE", "FETCH",
    	        "FOR", "FOREIGN", "FROM", "FUNCTION", "GRANT", "GROUP", "HAVING", "IN", "INITIALLY", "INTERSECT", "INTO", "IS",
    	        "LEADING", "LESS", "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "MINUS", "MODIFY", "NLSSORT", "NOT", "NULL",
    	        "OFFSET", "ON", "ONLY", "OR", "ORDER", "PERFORMANCE", "PLACING", "PRIMARY", "PROCEDURE", "REFERENCES", "RETURN",
    	        "RETURNING", "SELECT", "SESSION_USER", "SOME", "SPLIT", "SYMMETRIC", "SYSDATE", "TABLE", "THEN", "TO",
    	        "TRAILING", "TRUE", "UNION", "UNIQUE", "USER", "USING", "VARIADIC", "WHEN", "WHERE", "WINDOW", "WITH",
    	        "DIAGNOSTICS", "ELSEIF", "ELSIF", "EXCEPTION", "EXIT", "FORALL", "FOREACH", "GET", "OPEN", "PERFORM", "RAISE",
    	        "WHILE", "BUCKETS", "REJECT", "ADD", "ALTER", "BEGIN", "BETWEEN", "BY", "COLLATION", "COMPRESS", "CONCURRENTLY",
    	        "CONNECT", "CROSS", "CURRENT", "DELETE", "DROP", "FREEZE", "FULL", "IDENTIFIED", "ILIKE", "INCREMENT", "INDEX",
    	        "INNER", "INSERT", "ISNULL", "JOIN", "LEFT", "LEVEL", "LIKE", "LOCK", "NATURAL", "NOTNULL", "NOWAIT", "OF",
    	        "OUTER", "OVER", "OVERLAPS", "PRIVILEGES", "RAW", "RENAME", "RIGHT", "ROWS", "SESSION", "SET", "SIMILAR",
    	        "START", "TRIGGER", "UNTIL", "UPDATE", "VERBOSE", "VIEW"};
    	assertTrue(Arrays.equals(reservedWords, SQLKeywords.getRESERVEDWORDS()));
    	/*assertTrue(SQLKeywords.getRESERVEDWORDS()!=null);*/
    	SQLKeywords.initMap();
    	assertTrue(SQLKeywords.getKeywords()!=null);
    }
    
    @Test
    public void test_DsEncodingEnum(){
    	assertEquals("UTF-8",DsEncodingEnum.UTF_8.getEncoding());
    	assertEquals("GBK",DsEncodingEnum.GBK.getEncoding());
    	assertEquals("LATIN1",DsEncodingEnum.LATIN1.getEncoding());
    }
    
    @Test
    public void test_AESAlgorithm_getRandom(){
    	AESAlgorithmUtility aesAlg=new AESAlgorithmUtility(null);
    	assertTrue(aesAlg.getRandom()!=null);
    }
    
    @Test
    public void test_ExpressionVisitorAdapterWrap(){
    	ExpressionVisitorAdapterWrap eVAW=new ExpressionVisitorAdapterWrap();
    	eVAW.resetHasNonEditableSelectItem();
    	assertTrue(eVAW.hasNonEditableSelectItem()==false);
    }
    
    @Test
    public void test_DSEventWithCount(){
    	DSEventWithCount dsEventWC=new DSEventWithCount(0, null);
    	assertEquals(1,dsEventWC.getCount());
    }
    @Test
    public void test_SelectVisitorWrap(){
    	SelectVisitorWrap selectVW= new SelectVisitorWrap();
    	selectVW.visit(new SetOperationList());
    	assertEquals(true,selectVW.hasSetOperations());
    }
    
    @Test
    public void test_fileValidationUtilTest() {
    	String fileName = "testfile";
    	assertEquals(true, FileValidationUtils.validateFileName(fileName));
    }
    
    @Test
    public void test_filePathValidationTest() {
    	String fileName = "D:\\testfile";
    	assertEquals(true, FileValidationUtils.validateFilePathName(fileName));
    }
    
}
