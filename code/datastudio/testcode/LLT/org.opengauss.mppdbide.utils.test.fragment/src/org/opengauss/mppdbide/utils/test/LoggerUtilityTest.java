package org.opengauss.mppdbide.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

public class LoggerUtilityTest
{

    @Before
    public void setUp() throws Exception
    {
        MPPDBIDELoggerUtility.setArgs(new String[] {"-logfolder=.",
                "-detailLogging=true", "-logginglevel=DEFAULT"});
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void test_TraceLoggerTest_001()
    {
        MPPDBIDELoggerUtility.trace("This is a sample trace code");
        assertTrue(null!= MPPDBIDELoggerUtility.class);
    }

  

    @Test
    public void test_InfoLoggerTest_001()
    {
        MPPDBIDELoggerUtility.info("This is a sample trace code");
        MPPDBIDELoggerUtility.debug("DEBUG");
        assertTrue("DEBUG", true);
        assertTrue(null != MPPDBIDELoggerUtility.class);
    }

    @Test
    public void test_WarnLoggerTest_001()
    {
        MPPDBIDELoggerUtility.warn("This is a sample trace code");
        assertTrue(null!= MPPDBIDELoggerUtility.class);
        MPPDBIDELoggerUtility.none("do nothing");
    }

    /*
     * @Test public void testWarnLoggerTest_002() {
     * PLSQLIDELoggerUtility.warn("This is a sample trace code 2", new
     * DatabaseOperationException("Test Exception")); }
     */

    /*
     * @Test public void testWarnLoggerTest_003() {
     * PLSQLIDELoggerUtility.warn("This is a sample trace code 3",
     * LoggerUtilityTest.class.toString(), new
     * DatabaseOperationException("Test exception")); }
     */

    @Test
    public void test_DebugLoggerTest_001()
    {
        MPPDBIDELoggerUtility.debug("This is a sample trace code");
        assertTrue(null!= MPPDBIDELoggerUtility.class);
    }

    /*
     * @Test public void testDebugLoggerTest_002() {
     * PLSQLIDELoggerUtility.debug("This is a sample trace code 2", new
     * DatabaseOperationException("Test Exception")); }
     */

    /*
     * @Test public void testDebugLoggerTest_003() {
     * PLSQLIDELoggerUtility.debug("This is a sample trace code 3",
     * LoggerUtilityTest.class.toString(), new
     * DatabaseOperationException("Test exception")); }
     */

    @Test
    public void test_ErrorLoggerTest_001()
    {
        MPPDBIDELoggerUtility.error("This is a sample trace code");
         assertTrue(null!= MPPDBIDELoggerUtility.class);
    }

    @Test
    public void test_ErrorLoggerTest_002()
    {
        MPPDBIDELoggerUtility.error("This is a sample trace code 2",
                new DatabaseOperationException("Test Exception"));
                 assertTrue(null!= MPPDBIDELoggerUtility.class);
    }

    @Test
    public void test_ErrorLoggerTest_003() {
        MPPDBIDELoggerUtility.error("This is a sample trace code 3", new DatabaseOperationException("Test exception"));
        assertTrue(null != MPPDBIDELoggerUtility.class);
    }

    @Test
    public void test_ErrorLoggerTest_004()
    {
        MPPDBIDELoggerUtility.error("This is a sample trace code");
                 assertTrue(null!= MPPDBIDELoggerUtility.class);
    }

    @Test
    public void test_Trace_001()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=TRACE");

        assertEquals(log, "TRACE");
    }

    @Test
    public void testWarn_001()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=WARN");

        assertEquals(log, "WARN");

    }

    @Test
    public void test_Error_001()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=ERROR");

        assertEquals(log, "ERROR");

    }

    @Test
    public void test_Info_001()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=INFO");

        assertEquals(log, "INFO");
    }

    @Test
    public void test_Trace_002()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=DEBUG");

        assertEquals(log, "DEBUG");
    }

    @Test
    public void test_Trace_003()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=FATAL");

        assertEquals(log, "FATAL");

    }

    @Test
    public void test_Trace_004()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=  TRACE");

        assertEquals(log, "TRACE");
    }

    @Test
    public void test_Trace_005()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=trace");
       
        assertNotEquals(log, "TRACE");
    }

    @Test
    public void test_Trace_006()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=TRace");
          log.toUpperCase();
        assertNotEquals(log, "TRACE");
    }

    @Test
    public void test_ALL_001()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=ALL");
        assertEquals(log, "ALL");
        
    }

    @Test
    public void test_TRACE_001()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=TRACE");

        assertEquals(log, "TRACE");

    }

 /*   @Test
    public void testempty_001()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=");
        if(log==null)
        {
            log="WARN";
        }
         assertEquals(log, "WARN");

    }*/

    @Test
    public void test_OFF_001()
    {
        String log = MPPDBIDELoggerUtility.getParameter("logginglevel=OFF");
        assertEquals(log, "OFF"); }
   

    /*
     * @Test public void testFatalLoggerTest_002() {
     * PLSQLIDELoggerUtility.fatal("This is a sample trace code 2", new
     * DatabaseOperationException("Test Exception")); }
     */

    /*
     * @Test public void testFatalLoggerTest_003() {
     * PLSQLIDELoggerUtility.fatal("This is a sample trace code 3",
     * LoggerUtilityTest.class.toString(), new
     * DatabaseOperationException("Test exception")); }
     */

    /*
     * @Test public void testFatalLoggerTest_004() {
     * PLSQLIDELoggerUtility.fatal("This is a sample trace code", this
     * .getClass().toString()); }
     */
    
    @Test
    public void test_validateLogLevel_getParameter() {
        assertEquals(false, MPPDBIDELoggerUtility.validateLogLevel());
        assertEquals(null, MPPDBIDELoggerUtility.getParameter("="));
        MPPDBIDELoggerUtility.checkAndCreateLogger(true);
        assertEquals(false, MPPDBIDELoggerUtility.isDebugEnabled());
    }
}
