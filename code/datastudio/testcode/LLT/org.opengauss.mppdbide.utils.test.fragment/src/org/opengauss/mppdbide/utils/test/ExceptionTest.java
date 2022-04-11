package org.opengauss.mppdbide.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
//import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.exceptions.PasswordExpiryException;
import org.opengauss.mppdbide.utils.exceptions.UnknownException;
import org.opengauss.mppdbide.utils.exceptions.UserOperationCancelException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

public class ExceptionTest
{

    @Before
    public void setUp() throws Exception
    {
    	MPPDBIDELoggerUtility.setArgs(null);
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void test_DatabaseOperationException_001()
    {
        try
        {
            throw new DatabaseOperationException("Test exception",
                    new Exception("Test exception"));
        }
        catch (MPPDBIDEException e)
        {
            assertEquals("Test exception", e.getCause().getMessage());
        }
    }

    @Test 
    public void test_ExecutionPasswordExpiryException_002(){
    	try
        {
            throw new PasswordExpiryException("Password Is expired");
        }
    catch (PasswordExpiryException e) {
    	assertEquals("Password Is expired", e.getMessage());
		}	
    }

    @Test
    public void test_DatabaseOperationException_002()
    {
        try
        {
            throw new DatabaseOperationException("Test exception");
        }
        catch (MPPDBIDEException e)
        {
        	e.getErrorCode();
            assertTrue(e instanceof DatabaseOperationException);
        }
    }

    /*@Test
    public void testDatabaseOperationException_004()
    {
        try
        {
            throw new DatabaseOperationException("Test exception", "test Source", "test reason");
        }
        catch (PLSQLIDEException e)
        {
            assertTrue(e.getDBErrorMessage().equalsIgnoreCase("Source: test Source :: Unknown error. :: Reason: test reason"));
        }
    }*/
    
    @Test
    public void test_UnknownException_001()
    {
        try
        {
            throw new UnknownException ("test exception", new Exception("Test exception"));
        }
        catch (UnknownException e)
        {
            assertTrue(e.getCause().getMessage().equalsIgnoreCase("Test exception"));
        }
    }
    
    @Test
    public void test_UserOperationCancelException_001()
    {
        try
        {
            throw new UserOperationCancelException("Test exception");
        }
        catch (UserOperationCancelException e)
        {
            assertTrue(true);
        }
    }
    
    @Test
    public void test_DataStudioSecurityException_001()
    {
        try
        {
            throw new DataStudioSecurityException("Test exception");
        }
        catch (DataStudioSecurityException e)
        {
            assertTrue(true);
        }
    }
    
    @Test
    public void test_MPPDBIDEException_001()
    {
        MPPDBIDEException exception = new MPPDBIDEException("Test exception", new Exception("Test exception"));
        assertEquals("Data Studio Internal error.Test exception", exception.getServerMessage());
        exception.clearServerMessage();
    }

    @Test
    public void test_MPPDBIDEException_002()
    {
        MPPDBIDEException exception = new MPPDBIDEException("Test exception", new IOException("IO Exception"));
        assertEquals("IO Exception", exception.getServerMessage());
    }

    @Test
    public void test_MPPDBIDEException_003()
    {
        try
        {
            throw new MPPDBIDEException("Test exception", new MPPDBIDEException("MPP Exception"));
        }
        catch (MPPDBIDEException e)
        {
            assertTrue(true);
        }
    }
    
    @Test
    public void test_MPPDBIDEException_005()
    {
        MPPDBIDEException exception =
                new MPPDBIDEException("Test Exception", new Object[] {null});
        assertNotNull(exception.getDBErrorMessage());
    }
}
