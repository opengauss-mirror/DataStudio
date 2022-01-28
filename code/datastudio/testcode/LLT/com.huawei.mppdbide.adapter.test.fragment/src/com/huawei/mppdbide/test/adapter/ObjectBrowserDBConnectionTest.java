package com.huawei.mppdbide.test.adapter;

import static org.junit.Assert.fail;

import java.security.interfaces.RSAKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.adapter.driver.Gauss200V1R6Driver;
import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.adapter.gauss.ObjectBrowserDBConnection;
import com.huawei.mppdbide.mock.adapter.CommonLLTUtils;
import com.huawei.mppdbide.mock.adapter.CommonLLTUtilsHelper;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class ObjectBrowserDBConnectionTest extends BasicJDBCTestCaseAdapter
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
    public void test_new_connection_execSelectAndGetFirstVal()
    {
        DBConnection dbConnection = new ObjectBrowserDBConnection();
        try
        {
            initialize_connection(dbConnection);

        }
        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
               
        try
        {
            String rs = dbConnection.execSelectAndGetFirstVal(CommonLLTUtilsHelper.GET_ALL_SHALLOWLOADTABLES);
        }
        catch (DatabaseCriticalException e)
        {
            fail("fail");
        }
        catch (DatabaseOperationException e)
        {
            fail("fail");
        }
    }
    
    @Test
    public void test_new_connection_execSelectAndReturnRs()
    {
        DBConnection dbConnection = new ObjectBrowserDBConnection();
        try
        {
            initialize_connection(dbConnection);
        }
        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
               
        try
        {
            ResultSet rs = dbConnection.execSelectAndReturnRs(CommonLLTUtilsHelper.GET_ALL_SHALLOWLOADTABLES);
        }
        catch (DatabaseCriticalException e)
        {
            fail("fail");
        }
        catch (DatabaseOperationException e)
        {
            fail("fail");
        }
    }
    
    
    @Test
    public void test_new_connection_execSelectToExportCSV()
    {
        DBConnection dbConnection = new ObjectBrowserDBConnection();
        try
        {
            initialize_connection(dbConnection);
        }
        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
               
        try
        {
            ResultSet rs = dbConnection.execSelectToExportCSV(CommonLLTUtilsHelper.GET_ALL_SHALLOWLOADTABLES, 100);
        }
        catch (DatabaseCriticalException e)
        {
            fail("fail");
        }
        catch (DatabaseOperationException e)
        {
            fail("fail");
        }
    }
    
    @Test
    public void test_new_connection_execNonSelect()
    {
        DBConnection dbConnection = new ObjectBrowserDBConnection();
        try
        {
            initialize_connection(dbConnection);
        }
        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
               
        try
        {
            dbConnection.execNonSelect(CommonLLTUtilsHelper.GET_ALL_SHALLOWLOADTABLES);
        }
        catch (DatabaseCriticalException e)
        {
            fail("fail");
        }
        catch (DatabaseOperationException e)
        {
            fail("fail");
        }
    }
    
    @Test
    public void test_new_connection_execNonSelectForTimeout()
    {
        DBConnection dbConnection = new ObjectBrowserDBConnection();
        try
        {
            initialize_connection(dbConnection);
        }
        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
               
        try
        {
            dbConnection.execNonSelectForTimeout(CommonLLTUtilsHelper.GET_ALL_SHALLOWLOADTABLES);
        }
        catch (DatabaseCriticalException e)
        {
            fail("fail");
        }
        catch (DatabaseOperationException e)
        {
            fail("fail");
        }
    }

    private void initialize_connection(DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException
    {
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
    }

}
