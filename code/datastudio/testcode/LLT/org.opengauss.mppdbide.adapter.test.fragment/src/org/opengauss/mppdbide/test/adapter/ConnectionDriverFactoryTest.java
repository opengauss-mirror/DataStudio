package org.opengauss.mppdbide.test.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.AbstractConnectionDriver;
import org.opengauss.mppdbide.adapter.driver.DBMSDriverManager;
import org.opengauss.mppdbide.adapter.driver.Gauss200V1R6Driver;
import org.opengauss.mppdbide.adapter.driver.Gauss200V1R7Driver;
import org.opengauss.mppdbide.adapter.factory.ConnectionDriverFactory;
import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.GaussUtils;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import org.opengauss.mppdbide.bl.util.BLUtils;
import org.opengauss.mppdbide.mock.adapter.CommonLLTUtils;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class ConnectionDriverFactoryTest extends BasicJDBCTestCaseAdapter
{
    private String url;
    private Properties props;
    
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;

    @Before
	public void setUp() throws Exception
    {
        super.setUp();
        connection = new MockConnection();

        // MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
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
    }
    

    @Test
    public void testTTA_CONN_DRIVER_FACTORY_001()
    {
     //   init_connection_details();
        DBMSDriverManager instance =
                DBMSDriverManager.getInstance(BLUtils.getInstance().getInstallationLocation());
        ArrayList<AbstractConnectionDriver> l = instance.getOLAPDriverInstance(BLUtils.getInstance().getInstallationLocation());
        
        ConnectionDriverFactory.getInstance().addDriver("mydriver1", l.get(0));
        ConnectionDriverFactory.getInstance().addDriver("mydriver2", l.get(1));
        assertEquals(l.get(0), ConnectionDriverFactory.getInstance().getDriver("mydriver1"));
        assertEquals(l.get(1), ConnectionDriverFactory.getInstance().getDriver("mydriver2"));
    }
    
    @Test
    public void testTTA_sql_error_extract()
    {
        Gauss200V1R6Driver v1r6Version = new Gauss200V1R6Driver("\\Desktop");
        SQLException sqlException = new SQLException(
                "Throwing SQL exception intentionally.", "57PSQLException");
        String err = v1r6Version.extractErrCodeAdErrMsgFrmServErr(sqlException);
        assertTrue(err.contains("\n"));
        assertTrue(err.contains("57PSQLException"));
        assertTrue(err.contains("Throwing SQL exception intentionally."));
    }
    
    @Test
    public void testTTA_isObjectNotFoundErr_test_GaussUtils()
    {
        SQLException sqlException = new SQLException(
                "object does not exist", "42704");
        assertTrue(GaussUtils.isObjectNotFoundErr(sqlException));
    }
    
    @Test
    public void testTTA_driver_versions()
    {
        Gauss200V1R6Driver v1r6Version = new Gauss200V1R6Driver("\\Desktop");
        Gauss200V1R7Driver v1r7Version = new Gauss200V1R7Driver("\\Desktop");
        assertTrue(GaussUtils.isProtocolVersionNeeded(v1r6Version));
        assertFalse(GaussUtils.isProtocolVersionNeeded(v1r7Version));
        
    }
    
    
    @Test
    public void testTTA_getConnection_003()
    {
        url = "";
        try
        {
            DBConnection conn = DBMSDriverManager.getInstance("\\Desktop").getConnection(props, url, "OLAP");
        }
        catch (DatabaseOperationException | DatabaseCriticalException | SQLException e)
        {
            fail("fail");
        }
        
        DBConnection conn1 = null;
        try
        {
            conn1 = DBMSDriverManager.getInstance("\\Desktop").getConnection(props, url, "Unknown");
        }
        catch (DatabaseOperationException | DatabaseCriticalException | SQLException e)
        {
            fail("fail");
        }
        assertNull(conn1);       
    }
    
    @Test
    public void testTTA_handleCritical_error_test_GaussUtils()
    {
        SQLException sqlException = new SQLException(
                "invalid DESCRIBE message subtype", "08P01");
        
        try
        {
            GaussUtils.handleCriticalException(sqlException);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(true);
        }
        
        assertFalse(GaussUtils.isObjectNotFoundErr(sqlException));
    }

}
