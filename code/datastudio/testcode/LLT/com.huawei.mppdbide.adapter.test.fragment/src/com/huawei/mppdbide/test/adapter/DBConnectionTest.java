package com.huawei.mppdbide.test.adapter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.adapter.driver.Gauss200V1R6Driver;
import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.keywordssyntax.Keywords;
import com.huawei.mppdbide.adapter.keywordssyntax.OLAPKeywords;
import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.mock.adapter.CommonLLTUtils;
import com.huawei.mppdbide.mock.adapter.CommonLLTUtilsHelper;
import com.huawei.mppdbide.mock.adapter.ExceptionConnection;
import com.huawei.mppdbide.utils.DsEncodingEnum;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class DBConnectionTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;

    @Before
	public void setUp() throws Exception
    {
        super.setUp();
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

        CommonLLTUtilsHelper.prepareProxyInfo(preparedstatementHandler);

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
        statementHandler.clearStatements();

    }

    @Test
    public void test_execNonSelectForTimeout_Exception()
    {
        try
        {
            // CommonTestUtils.createTableRS(preparedstatementHandler);

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

            preparedstatementHandler
                    .prepareThrowsSQLException("select * from t1");

            dbConnection.execNonSelectForTimeout("select * from t1");

        }
        catch (DatabaseOperationException e)
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
    public void test_DBConnectionConstructor()
    {
        try
        {
            // CommonTestUtils.createTableRS(preparedstatementHandler);

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

            DBConnection dbConnection1 = new DBConnection(dbConnection.getDriver(), dbConnection.getConnection());

        }
        catch (DatabaseOperationException e)
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
    public void test_DBConnectionSetFetchSize()
    {
        try
        {
            // CommonTestUtils.createTableRS(preparedstatementHandler);

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
            
        }
        catch (DatabaseOperationException e)
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
    public void test_DBConnectionDBConnect_Exception()
    {
        try
        {
            // CommonTestUtils.createTableRS(preparedstatementHandler);

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
            props.setProperty("protocolVersion", "3.5");
            url = "jdbc:postgresql://127.0.0.1:1234/testDB";

            dbConnection.dbConnect(props, url);

        }
        catch (DatabaseOperationException e)
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
    public void test_DBConnectionexecNonSelect_AutoCommitFalse()
    {
        try
        {
            // CommonTestUtils.createTableRS(preparedstatementHandler);

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
            props.setProperty("protocolVersion", "3.5");
            url = "jdbc:postgresql:127.0.0.1:1234/testDB";

            dbConnection.dbConnect(props, url);
            
            String query="MyQuery";
            SQLException exc=new SQLException("Intemntionaly");
           preparedstatementHandler.prepareThrowsSQLException(query,exc);
           dbConnection.getConnection().setAutoCommit(false);
           dbConnection.execNonSelect(query);

        }
        catch (DatabaseOperationException e)
        {
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_DBConnectionexecNonSelect_Exception()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptionRollback(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection); 
            
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
            props.setProperty("protocolVersion", "3.5");
            url = "jdbc:postgresql:127.0.0.1:1234/testDB";

            dbConnection.dbConnect(props, url);
            
            String query="MyQuery";
           dbConnection.getConnection().setAutoCommit(false);
           exceptionConnection.setThrowExceptioForPrepareStmt(true);
           dbConnection.execNonSelect(query);

        }
        catch (DatabaseOperationException e)
        {
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_DBConnectionexecQueryWithMsgQueue_PrepareException()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);

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
            props.setProperty("protocolVersion", "3.5");
            url = "jdbc:postgresql:127.0.0.1:1234/testDB";

            dbConnection.dbConnect(props, url);

            String query = "MyQuery";
            MessageQueue queue = new MessageQueue();
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            dbConnection.execQueryWithMsgQueue(query, queue);
            fail("Failed");
        }
        catch (DatabaseOperationException e)
        {
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_DBConnectionexecQueryWithMsgQueue_StmtExecuteException()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);

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
            props.setProperty("protocolVersion", "3.5");
            url = "jdbc:postgresql:127.0.0.1:1234/testDB";

            dbConnection.dbConnect(props, url);

            String query = "MyQuery";
            MessageQueue queue = new MessageQueue();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExecuteException(true);
            dbConnection.execQueryWithMsgQueue(query, queue);
            fail("Failed");
        }
        catch (DatabaseOperationException e)
        {
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_DBConnectionexecQueryWithMsgQueue_StmtCloseException()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptioForPrepareStmt(false);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);

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
            props.setProperty("protocolVersion", "3.5");
            url = "jdbc:postgresql:127.0.0.1:1234/testDB";

            dbConnection.dbConnect(props, url);

            String query = "MyQuery";
            MessageQueue queue = new MessageQueue();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setThrowExceptionClosePreparedStmt(true);
            dbConnection.execQueryWithMsgQueue(query, queue);
        }
        catch (DatabaseOperationException e)
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
    public void test_DBConnection_rollbackException()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptionRollback(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection); 
            
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
            props.setProperty("protocolVersion", "3.5");
            url = "jdbc:postgresql:127.0.0.1:1234/testDB";

            dbConnection.dbConnect(props, url);
            dbConnection.getConnection().setAutoCommit(false);  
            dbConnection.rollback();

        }
        catch (DatabaseOperationException e)
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
    public void test_DBConnection_rollback()
    {
        try
        {
            
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
            props.setProperty("protocolVersion", "3.5");
            url = "jdbc:postgresql:127.0.0.1:1234/testDB";

            dbConnection.dbConnect(props, url);
            dbConnection.getConnection().setAutoCommit(false);  
            dbConnection.rollback();

        }
        catch (DatabaseOperationException e)
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
    public void test_DBConnection_connectViaDriver_1()
    {
        try
        {
            
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
            props.setProperty("protocolVersion", "3.5");
            url = "jdbc:postgresql:127.0.0.1:1234/testDB";
            setConnectionDriver(dbConnection);
            try
            {
                dbConnection.connectViaDriver(props, url);
            }
            catch (DatabaseCriticalException e)
            {
                /* Expected exception */
                assertTrue(true);
            }
        }
        catch (DatabaseOperationException e)
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
    public void test_DBConnection_clientEncoding()
    {
        try
        {
            
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
            props.setProperty("protocolVersion", "3.5");
            url = "jdbc:postgresql:127.0.0.1:1234/testDB";
            dbConnection.dbConnect(props, url);
            dbConnection.executeClientEncoding(DsEncodingEnum.LATIN1.getEncoding());
        }
        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
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
                return new org.postgresql.Driver();
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
                return null;
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
	public void test_DBConnectionConstructor_02() {
		try {
			DBConnection dbConnection = new DBConnection();
			Properties props = new Properties();
			CommonLLTUtils.initDriver("org.postgresql.Driver");
			props.setProperty("user", "test");
			props.setProperty("password", "test");
			props.setProperty("allowEncodingChanges", "true");
			String encoding = System.getProperty("file.encoding");
			props.setProperty("characterEncoding", encoding);
			props.setProperty("ApplicationName", "MPP IDE");
			String url = null;
			url = "jdbc:postgresql://127.0.0.1:1234/testDB";
			Gauss200V1R6Driver v1r6Version = new Gauss200V1R6Driver("\\Desktop");
			DBConnection dbConnection1 = new DBConnection(v1r6Version, dbConnection.getConnection());
			dbConnection1.dbConnect(props, url);
			String grp = "procfunc";
			String schemaName = "dsuser";
			String functionOidQuery = "select oid from pg_proc where  proname = ? and pronamespace = (select oid from pg_namespace where nspname = ?)";
			MockResultSet functionOidQueryRS = preparedstatementHandler.createResultSet();
			functionOidQueryRS.addColumn("oid");
			preparedstatementHandler.prepareResultSet(functionOidQuery, functionOidQueryRS);
			dbConnection1.execSelectForSearch2Parmeters(functionOidQuery,
					grp.contains("\"") ? String.valueOf(grp).replace("\"", "")
							: String.valueOf(grp).toLowerCase(Locale.ENGLISH),
					schemaName.toLowerCase(Locale.ENGLISH).replace("\"", ""));
		} catch (DatabaseOperationException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
    
	@Test
    public void test_DBConnection_001() {
        try {
            Properties props = new Properties();
            CommonLLTUtils.initDriver("org.postgresql.Driver");
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            String url = null;
            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            Gauss200V1R6Driver v1r6Version = new Gauss200V1R6Driver("\\Desktop");
            DBConnection dbConnection = new DBConnection(v1r6Version, new DBConnection().getConnection());
            dbConnection.dbConnect(props, url);
            String serverSupportDdlQuery = "select count(*) from pg_catalog.pg_proc "
                    + "where proname='pg_get_tabledef';";
            
            MockResultSet fetchRS = preparedstatementHandler.createResultSet();
            fetchRS.addColumn("");
            fetchRS.addRow(new Object[] {1});
            preparedstatementHandler.prepareResultSet(serverSupportDdlQuery, fetchRS);
            assertTrue(dbConnection.getShowDDLSupportFromServer());
            
            MockResultSet fetchRS2 = preparedstatementHandler.createResultSet();
            fetchRS2.addColumn("");
            fetchRS2.addRow(new Object[] {});
            preparedstatementHandler.prepareResultSet(serverSupportDdlQuery, fetchRS2);
            assertTrue(dbConnection.getShowDDLSupportFromServer());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
	
	  @Test
      public void test_DBConnection_isClosedException()
      {
          try
          {
              ExceptionConnection exceptionConnection = new ExceptionConnection();
              exceptionConnection.setSqlException(new SQLException());
              exceptionConnection.setThrowExceptionisClosed(true);
              getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection); 
              
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
              props.setProperty("protocolVersion", "3.5");
              url = "jdbc:postgresql:127.0.0.1:1234/testDB";

              dbConnection.dbConnect(props, url);
              dbConnection.isClosed();

          }
          catch (DatabaseOperationException e)
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
      public void test_DBConnection_commitConn()
      {
          try
          {
              
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
              props.setProperty("protocolVersion", "3.5");
              url = "jdbc:postgresql:127.0.0.1:1234/testDB";

              dbConnection.dbConnect(props, url);
              dbConnection.getConnection().setAutoCommit(false);  
              dbConnection.commitConnection("test");

          }
          catch (DatabaseOperationException e)
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
    public void test_DBConnection_commitException()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setSqlException(new SQLException());
            exceptionConnection.setThrowExceptionCommit(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection); 
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
            props.setProperty("protocolVersion", "3.5");
            url = "jdbc:postgresql:127.0.0.1:1234/testDB";

            dbConnection.dbConnect(props, url);
            dbConnection.getConnection().setAutoCommit(false);
          
            dbConnection.commitConnection("test");

        }
        catch (DatabaseOperationException e)
        {
           assertTrue(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
