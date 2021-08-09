package com.huawei.mppdbide.test.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.util.HostSpec;

import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.adapter.driver.Gauss200V1R6Driver;
import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussDatatypeUtils;
import com.huawei.mppdbide.adapter.gauss.GaussDriverWrapper;
import com.huawei.mppdbide.adapter.gauss.GaussMppDbNoticeListner;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.adapter.gauss.StmtExecutor;
import com.huawei.mppdbide.adapter.keywordssyntax.Keywords;
import com.huawei.mppdbide.adapter.keywordssyntax.KeywordsToTrieConverter;
import com.huawei.mppdbide.adapter.keywordssyntax.OLAPKeywords;
import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.mock.adapter.BaseConnectionHelper;
import com.huawei.mppdbide.mock.adapter.CommonLLTUtils;
import com.huawei.mppdbide.mock.adapter.CommonLLTUtilsHelper;
import com.huawei.mppdbide.mock.adapter.ExceptionConnection;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.GlobaMessageQueueUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class AdapterTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection               = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler         statementHandler         = null;
    
    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    Gauss200V1R6Driver v1r6Version = null;
  
    @Before
	public void setUp() throws Exception
    {
        super.setUp();
        connection = new MockConnection();
       //test for logging
        MPPDBIDELoggerUtility.setArgs(new String[]{"-logfolder=.","-detailLogging=true"});
               
       // MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();
        v1r6Version = new Gauss200V1R6Driver("\\Desktop");
        CommonLLTUtilsHelper.prepareProxyInfo(preparedstatementHandler);
     
    }

    
    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
	public void tearDown() throws Exception
    {
        super.tearDown();
     
        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();
      
        
    }
    
    /*
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_001_1()
    {
    try
    {
    CommonLLTUtils.createTableRS(preparedstatementHandler);

    DBConnection dbConnection = new DBConnection();
    ExceptionConnection connection = new ExceptionConnection(); 
    getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
    connection.setNeedExceptioStatement(true);
    connection.setNeedExceptionResultset(true);

    String url = null;
    Properties props = new Properties();

    dbConnection.initDriver("org.postgresql.Driver");

    props.setProperty("user", "test");
    props.setProperty("password", "test");
    props.setProperty("allowEncodingChanges", "true");
    String encoding = System.getProperty("file.encoding");
    props.setProperty("characterEncoding", encoding);
    props.setProperty("ApplicationName", "MPP IDE");


    url = "jdbc:postgresql://127.0.0.1:1234/testDB";

    dbConnection.dbConnect(props, url);

    StmtExecutor executor = new StmtExecutor("insert into t1 values(1)", dbConnection);
    executor.getFetchCount();
    connection.setThrowoutofmemerrorinrs(true);
    connection.setThrowExceptionCloseStmt(true);
    executor.execute();

    executor = new StmtExecutor("truncate table t1", dbConnection);
    assertTrue(executor!=null);

    executor.execute();

    }
    catch (Exception e)
    {
    e.printStackTrace();
    }
    }
    */
    
    /*
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_001_2()
    {
    try
    {
    CommonLLTUtils.createTableRS(preparedstatementHandler);
    CommonLLTUtilsHelper.prepareProxyInfo(preparedstatementHandler);
    DBConnection dbConnection = new DBConnection();
    ExceptionConnection connection = new ExceptionConnection(); 
    getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
    connection.setNeedExceptioStatement(true);
    connection.setNeedExceptionResultset(true);

    String url = null;
    Properties props = new Properties();

    dbConnection.initDriver("org.postgresql.Driver");

    props.setProperty("user", "test");
    props.setProperty("password", "test");
    props.setProperty("allowEncodingChanges", "true");
    String encoding = System.getProperty("file.encoding");
    props.setProperty("characterEncoding", encoding);
    props.setProperty("ApplicationName", "MPP IDE");


    url = "jdbc:postgresql://127.0.0.1:1234/testDB";

    dbConnection.dbConnect(props, url);

    StmtExecutor executor = new StmtExecutor("insert into t1 values(1)", dbConnection);
    executor.getFetchCount();
    connection.setThrowoutofmemerrorinrs(true);
    executor.execute();

    executor = new StmtExecutor("truncate table t1", dbConnection);

    executor.execute();
    assertTrue(executor!=null);

    }
    catch (Exception e)
    {
    e.printStackTrace();
    }
    }
    */
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_001_001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            
            //dbConnection.cancelQuery();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_001_002()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            
            statementHandler.prepareThrowsSQLException("select * from t1");
            
            dbConnection.execNonSelect("select * from t1");
            
            dbConnection.closeStatement(dbConnection.getPrepareStmt(""));
            
            
        }
        catch(DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_001_003()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test"                                                                                                              );
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            
            preparedstatementHandler.prepareThrowsSQLException("select * from t1");
            
            dbConnection.execSelectAndReturnRs("select * from t1");
            dbConnection.getDriver().getKeywordList();
            
            
            
        }
        catch(DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_001_004()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            
            preparedstatementHandler.prepareThrowsSQLException("select * from t1");
            
            dbConnection.execSelectAndGetFirstVal("select * from t1");
            
            
        }
        catch(DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_001_005()
    {
        GaussMppDbNoticeListner noticeListner =
                new GaussMppDbNoticeListner(GlobaMessageQueueUtil.getInstance().getMessageQueue());
        SQLWarning sqlWarning = new SQLWarning("NOTICE: Message");
        noticeListner.noticeReceived(sqlWarning);
        sqlWarning = new SQLWarning();
        noticeListner.noticeReceived(sqlWarning);
        assertTrue(sqlWarning != null);
        noticeListner.noticeReceived(null);
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();

            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            
            StmtExecutor executor = new StmtExecutor("insert into t1 values(1)", dbConnection);

            executor.execute();
            
            executor = new StmtExecutor("truncate table t1", dbConnection);
            
            executor.execute();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_002()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            
            MockResultSet colmetadataRS = preparedstatementHandler.createResultSet();
            colmetadataRS.addRow(new Object[]{});
            preparedstatementHandler.prepareResultSet("select * from t1", colmetadataRS);
            
            dbConnection.execSelectAndGetFirstVal("select * from t1");
            fail("Not expected to come here.");   
        }
        catch(DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_003()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();

            CommonLLTUtils.initDriver("org.postgresql.Driver2");

            fail("Not expected to come here.");   
        }
        catch(DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_004()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            
            @SuppressWarnings("unused")
            GaussUtils gaussUtils = new GaussUtils();
            @SuppressWarnings("unused")
            GaussDatatypeUtils datatypeUtils = new GaussDatatypeUtils();
            
            assertEquals(null, GaussDatatypeUtils.convertToClientType(00));
            assertEquals(false, GaussDatatypeUtils.isSupported(00));

            assertEquals("bool", GaussDatatypeUtils.convertToClientType(16));
            assertEquals(true, GaussDatatypeUtils.isSupported(16));
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            
            preparedstatementHandler.prepareThrowsSQLException("select * from t1");
            
            StmtExecutor executor = new StmtExecutor("select * from t1", dbConnection);
            
            executor.execute();
            fail("not expected to come here.");
        }
        catch(DatabaseOperationException e)
        {
            System.out.println("As expected.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_005()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptioForStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            //connection.cancelQuery();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_006()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setThrowExceptionCloseStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            //connection.cancelQuery();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_007()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            
            connection.getPrepareStmt("Myquery");
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
            System.out.println("As expcted");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_008()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setThrowExceptionClosePreparedStmt(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            PreparedStatement statement = connection.getPrepareStmt("Myquery");
            connection.closeStatement(statement);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_009()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setThrowExceptionClosePreparedStmt(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            @SuppressWarnings("unused")
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, null);
            
            PreparedStatement statement = connection.getPrepareStmt("Myquery");
            connection.closeStatement(statement);
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch(DatabaseCriticalException e)
        {
            System.out.println("As expcted");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_010()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionCloseResultSet(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            PreparedStatement statement = connection.getPrepareStmt("Myquery");
            ResultSet rs = statement.executeQuery();
            connection.closeResultSet(rs);
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_011()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionCloseStmt(true);
            //exceptionConnection.setThrowoutofmemerrorinrs(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            connection.execNonSelect("MyQuery");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_012()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            //exceptionConnection.setThrowExceptionCloseStmt(true);
            exceptionConnection.setThrowoutofmemerrorinrs(true);

            connection.execSelectAndReturnRs("MyQuery");
            fail("Not expected to come here..");   
        }
        catch(DatabaseCriticalException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_0112()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptioForStmt(true);
            exceptionConnection.setThrowExceptionCloseResultSet(true);
            exceptionConnection.setThrowoutofmemerrorinrs(true);
           // connection.cancelQuery();
            connection.execSelectToExportCSV("MyQuery",1000);
            fail("Not expected to come here..");   
        }
        catch(DatabaseCriticalException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_1112()
    {
        try
        {
          
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            String query="MyQuery";
            SQLException exc=new SQLException("Intemntionaly");
           preparedstatementHandler.prepareThrowsSQLException(query,exc);
            connection.execSelectToExportCSV(query,1000);
            fail("Not expected to come here..");   
        }
        catch(DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_013()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionClose(true);
            exceptionConnection.setThrowoutofmemerrorinrs(true);

            connection.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_014()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            //exceptionConnection.setThrowoutofmemerrorinrs(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            stmtExecutor.execute();
            stmtExecutor.isLastRecord();
            fail("not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_015()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            //exceptionConnection.setThrowoutofmemerrorinrs(true);
            exceptionConnection.setThrowExceptionNext(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            stmtExecutor.execute();
            //ResultSet rs = stmtExecutor.getResultSet();
            
           int len= stmtExecutor.getNextRecordBatch(0).length;
            
           assertEquals(0, len);
        }
        catch(DatabaseOperationException e)
        {
          fail("not expected");
        }
        catch (Exception e)
        {
         
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_016()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            //exceptionConnection.setThrowoutofmemerrorinrs(true);
            exceptionConnection.setThrowExceptionNext(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            stmtExecutor.execute();
            //ResultSet rs = stmtExecutor.getResultSet();
            
            assertNull(stmtExecutor.getColumnName(0));
            
            //fail("not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_017()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            //exceptionConnection.setThrowoutofmemerrorinrs(true);
            exceptionConnection.setThrowExceptionNext(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            stmtExecutor.execute();
            //ResultSet rs = stmtExecutor.getResultSet();
            
            assertNull(stmtExecutor.getColumnName(0));
        }
        catch(DatabaseOperationException e)
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
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_018()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionCloseStmt(true);
            exceptionConnection.setThrowExceptionNext(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            stmtExecutor.execute();
            //ResultSet rs = stmtExecutor.getResultSet();
            
            assertNull(stmtExecutor.getColumnName(0));
        }
        catch(DatabaseOperationException e)
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
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_019()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            //exceptionConnection.setThrowoutofmemerrorinrs(true);
            exceptionConnection.setThrowExceptionCloseStmt(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            stmtExecutor.execute();
                        
            stmtExecutor.closeStatement();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_020()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            //exceptionConnection.setThrowoutofmemerrorinrs(true);
            exceptionConnection.setThrowExceptionCloseResultSet(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);

           /* exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowoutofmemerrorinrs(true);
            exceptionConnection.setThrowExceptionCloseResultSet(true);*/
            
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            stmtExecutor.execute();
                        
            stmtExecutor.closeResultSet();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
  /*  @Test
    public void testTTA_BL_STMT_EXECUTOR_FUNC_001_021()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowoutofmemerrorinrs(true);
            exceptionConnection.setThrowExceptionCloseResultSet(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            connection.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            stmtExecutor.execute();
                        
            stmtExecutor.closeResultSet();;
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
*/
    
    @Test
    public void testTTA_BL_BASESTMT_FUNC_001_01()
    {
        try
        {
        	//Dependencies on BL
            /*ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SAVE_PRD_OPTIONS.DO_NOT_SAVE);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2,"Gauss");*/

            Properties properties = new Properties();
          //Dependencies on BL
            /*properties.setProperty("user", serverInfo.getUsername());
            properties.setProperty("password", new String(serverInfo.getPrd()));*/
            properties.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            properties.setProperty("characterEncoding", encoding);
            properties.setProperty("ApplicationName", "Data Studio");
            
            BaseConnectionHelper baseConnection = new BaseConnectionHelper("", properties, new HostSpec[]{new HostSpec("127.0.0.1",1111)}, "db", "user", false);
            baseConnection.setReturnBaseStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(baseConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            connection.execNonSelect("MyQuery");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_BASESTMT_FUNC_001_02()
    {
        try
        {
        	//Dependencies on BL
           /* ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SAVE_PRD_OPTIONS.DO_NOT_SAVE);
            Server server = new Server(serverInfo);
            Database database = new Database(server,2,"Gauss");*/

            Properties properties = new Properties();
          //Dependencies on BL
            /*properties.setProperty("user", serverInfo.getUsername());
            properties.setProperty("password", new String(serverInfo.getPrd()));*/
            properties.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            properties.setProperty("characterEncoding", encoding);
            properties.setProperty("ApplicationName", "Data Studio");
            
            BaseConnectionHelper baseConnection = new BaseConnectionHelper("", properties, new HostSpec[]{new HostSpec("127.0.0.1",1111)}, "db", "user", false);
            baseConnection.setReturnBaseStmt(true);
            baseConnection.setThrowSQLException(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(baseConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            
            connection.execNonSelect("MyQuery");
            
        }
        catch(DatabaseOperationException e)
        {
            if(!(e.getCause() instanceof SQLException))
            {
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

public void testTTA_BL_ADAPTER_FUNC_001_005_()
{
    try
    {
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        
        DBConnection dbConnection = new DBConnection();
        
        String url = null;
        Properties props = new Properties();

        CommonLLTUtils.initDriver("org.postgresql.Driver");

        props.setProperty("user", "test");
        props.setProperty("password", "test");
        props.setProperty("allowEncodingChanges", "true");
        String encoding = System.getProperty("file.encoding");
        props.setProperty("characterEncoding", encoding);
        props.setProperty("ApplicationName", "MPP IDE");
        

        url = "jdbc:postgresql://127.0.0.1:1234/testDB";
        
        dbConnection.dbConnect(props, url);
        dbConnection.isTransactionOpen("");
        
        //dbConnection.cancelQuery();
        
    }
    catch (Exception e)
    {
        e.printStackTrace();
        fail(e.getMessage());
    }
}

    public void testTTA_BL_ADAPTER_FUNC_001_006_()
    {
        if (v1r6Version != null)
        {
            v1r6Version.getDriverSpecificProperties();
            v1r6Version.getProtocolMismatchErrorString();
            assertNotNull(true);
        }
        assertNotNull(false);
    }

    private void  setConnectionDriver(DBConnection conn)
    {
        conn.setDriver(new IConnectionDriver()
        {

            @Override
            public String getToolPath(String toolName)
            {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Driver getJDBCDriver()
            {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Properties getDriverSpecificProperties()
            {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getDriverName()
            {
                // TODO Auto-generated method stub
                return MPPDBIDEConstants.TESTDRIVER;
            }

            @Override
            public String extractErrCodeAdErrMsgFrmServErr(SQLException e)
            {
                return "";
            }

			@Override
			public Keywords getKeywordList() {
				// TODO Auto-generated method stub
				return new OLAPKeywords();
			}

            @Override
            public SQLSyntax loadSQLSyntax()
            {
                // TODO Auto-generated method stub
                return null;
            }

        });
    }
    
    
    private void  setConnectionDriverOLAP(DBConnection conn)
    {
        conn.setDriver(new IConnectionDriver()
        {

            @Override
            public String getToolPath(String toolName)
            {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Driver getJDBCDriver()
            {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Properties getDriverSpecificProperties()
            {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getDriverName()
            {
                // TODO Auto-generated method stub
                return MPPDBIDEConstants.GAUSS200V1R6DRIVER;
            }

            @Override
            public String extractErrCodeAdErrMsgFrmServErr(SQLException e)
            {
                return "";
            }

            @Override
            public Keywords getKeywordList() {
                // TODO Auto-generated method stub
                return new OLAPKeywords();
            }

            @Override
            public SQLSyntax loadSQLSyntax()
            {
                // TODO Auto-generated method stub
                return null;
            }

        });
    }
    

    @Test
    public void testTTA_BL_ADAPTER_FUNC_OLAP_RESERVEDKEYWORDS_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            assertEquals(114, dbConnection.getDriver().getKeywordList().getReservedKeywords().length);
                      
            
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_OLAP_UNRESERVEDKEYWORDS_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            assertEquals(380, dbConnection.getDriver().getKeywordList().getUnReservedKeywords().length);
                      
            
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_OLAP_TYPESKEYWORDS_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            assertEquals(58, dbConnection.getDriver().getKeywordList().getTypes().length);
                      
            
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_OLAP_CONSTANTSKEYWORDS_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            assertEquals(22, dbConnection.getDriver().getKeywordList().getConstants().length);
                      
            
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_OLAP_PREDICATESKEYWORDS_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            assertEquals(72, dbConnection.getDriver().getKeywordList().getPredicates().length);
                      
            
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_RESERVEDKEYWORDS_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            assertEquals(114, dbConnection.getDriver().getKeywordList().getReservedKeywords().length);
                      
            
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_UNRESERVEDKEYWORDS_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            assertEquals(380, dbConnection.getDriver().getKeywordList().getUnReservedKeywords().length);
                      
            
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_TYPESKEYWORDS_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            assertEquals(58, dbConnection.getDriver().getKeywordList().getTypes().length);
                      
            
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_CONSTANTSKEYWORDS_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            assertEquals(22, dbConnection.getDriver().getKeywordList().getConstants().length);
                      
            
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_PREDICATESKEYWORDS_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            assertEquals(72, dbConnection.getDriver().getKeywordList().getPredicates().length);
                      
            
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_SQLSyntax_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            SQLSyntax sqlSyntax = new SQLSyntax();
            SQLSyntax convertKeywordstoTrie = KeywordsToTrieConverter.convertKeywordstoTrie(sqlSyntax, dbConnection.getDriver().getKeywordList());
            assertNotNull(convertKeywordstoTrie);
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_SQLSyntaxSet_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            SQLSyntax sqlSyntax = new SQLSyntax();
            Keywords keyword = dbConnection.getDriver().getKeywordList();
            SQLSyntax convertKeywordstoTrie = KeywordsToTrieConverter.convertKeywordstoTrie(sqlSyntax, keyword);
            assertNotNull(convertKeywordstoTrie);
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_ADAPTER_FUNC_SQLSyntaxSetClear_TEST()
    {
        try
        {
            //CommonTestUtils.createTableRS(preparedstatementHandler);
            
            DBConnection dbConnection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriver(dbConnection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            SQLSyntax sqlSyntax = new SQLSyntax();
            Keywords keyword = dbConnection.getDriver().getKeywordList();
            SQLSyntax convertKeywordstoTrie = KeywordsToTrieConverter.convertKeywordstoTrie(sqlSyntax, keyword);
            assertNotNull(convertKeywordstoTrie);
            sqlSyntax.clear();
            assertTrue(sqlSyntax.getConstants().size()==0);
            assertTrue(sqlSyntax.getPredicates().size()==0);
            assertTrue(sqlSyntax.getReservedkrywords().size()==0);
            assertTrue(sqlSyntax.getTypes().size()==0);
            assertTrue(sqlSyntax.getUnreservedkrywords().size()==0);
        }
        catch(DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
 
	@Test
	public void testTTA_BL_ADAPTER_Driver_TEST_01() {
		GaussDriverWrapper wrapper = new GaussDriverWrapper(v1r6Version);
		assertNotNull(wrapper.getDriverSpecificProperties());
		assertNotNull(wrapper.loadSQLSyntax());
		assertEquals(false, wrapper.getShowDDLSupportCheck());
		assertNotNull(wrapper.getJDBCDriver());
		assertNotNull(wrapper.getKeywordList());
		assertEquals(false, wrapper.getShowDDLSupport());
		SQLException excep = new SQLException("Error message");
		assertEquals("SQL Error Code = null\r\nError message\r\n", wrapper.extractErrCodeAdErrMsgFrmServErr(excep));
		assertEquals("Gauss200V1R6Driver", wrapper.getDriverInstance().getDriverName());
	}
}
