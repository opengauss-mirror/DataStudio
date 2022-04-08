package org.opengauss.mppdbide.test.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.IConnectionDriver;
import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.StmtExecutor;
import org.opengauss.mppdbide.adapter.gauss.StmtExecutor.GetFuncProcResultValueParam;
import org.opengauss.mppdbide.adapter.keywordssyntax.Keywords;
import org.opengauss.mppdbide.adapter.keywordssyntax.OLAPKeywords;
import org.opengauss.mppdbide.adapter.keywordssyntax.SQLSyntax;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.mock.adapter.CommonLLTUtils;
import org.opengauss.mppdbide.mock.adapter.CommonLLTUtilsHelper;
import org.opengauss.mppdbide.mock.adapter.CommonLLTUtilsHelper.EXCEPTIONENUM;
import org.opengauss.mppdbide.mock.adapter.ExceptionConnection;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class StmtExecutorTest extends BasicJDBCTestCaseAdapter
{
    
    MockConnection                    connection               = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler         statementHandler         = null;
    
    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
  
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
    public void test_StmtExecutorConstructor_Exception()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(connection);
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
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
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
    public void test_StmtExecutorCloseStatement_Exception()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrowExceptionClosePreparedStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            stmtExecutor.closeStatement();
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
    public void test_StmtExecutorCommitConnection_Exception()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrowExceptionCommit(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            stmtExecutor.commitConnection();
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
    public void test_StmtExecutorCommitConnection_AutoCommitFalse()
    {
        try
        {
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            connection.getConnection().setAutoCommit(false);
            stmtExecutor.commitConnection();
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
    public void test_StmtExecutorRollBack_Exception()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrowExceptionRollback(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();
            
            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            
            
            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            stmtExecutor.commitConnection();
            stmtExecutor.rollback();
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
    public void test_StmtExecutorRollBack_AutoCommitFalse()
    {
        try
        {
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(connection);
            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");
            

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";
            
            connection.dbConnect(props, url);
            StmtExecutor stmtExecutor = new StmtExecutor("MyQuery", connection);
            connection.getConnection().setAutoCommit(false);
            stmtExecutor.rollback();
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
    public void test_StmtExecutor_getNextRecordBatch()
    {
        try
        {
           // CommonLLTUtils.createTableRS(preparedstatementHandler);
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            //exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.NO);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(connection);
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
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.NO);
            //exceptionConnection.setThrowoutofmemerrorinrs(true);
            stmtExecutor.getNextRecordBatch(-1);
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
    
   /* @Test
    public void test_StmtExecutor_getWarning()
    {
        try
        {
           CommonLLTUtils.createTableRS(preparedstatementHandler);
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            //exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.NO);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
           ExceptionPreparedStatement exceptionPrepare = new ExceptionPreparedStatement();
           exceptionPrepare.getWarnings();
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
          
            //ResultSet rs = stmtExecutor.getResultSet();
            //exceptionConnection.setThrowoutofmemerrorinrs(true);
            stmtExecutor.getWarning();
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
    public void test_StmtExecutor_getColumnDataType()
    {
        try
        {
           CommonLLTUtils.createTableRS(preparedstatementHandler);
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrowExceptionMetaData(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);          
            
            
            DBConnection connection = new DBConnection();
            
            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");
            setConnectionDriverOLAP(connection);
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
           // preparedstatementHandler.
            stmtExecutor.getColumnDataTypeStmt(0);
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
    public void test_StmtExecutor_getColumnName()
    {
        try
        {
           CommonLLTUtils.createTableRS(preparedstatementHandler);
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrowExceptionMetaData(true);
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
           // preparedstatementHandler.
            stmtExecutor.getColumnName(0);
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
    public void test_StmtExecutor_GetFuncProcResultValueParam()
    {
        try
        {
            GetFuncProcResultValueParam param = new GetFuncProcResultValueParam(0, false, false);
            param.setColumnCount(0);
            param.setCallableStmt(true);
            param.setInputParaVisited(true);
            assertEquals(param.getColumnCount(), 0);
            assertTrue(param.isCallableStmt());
            assertTrue(param.isInputParaVisited());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
