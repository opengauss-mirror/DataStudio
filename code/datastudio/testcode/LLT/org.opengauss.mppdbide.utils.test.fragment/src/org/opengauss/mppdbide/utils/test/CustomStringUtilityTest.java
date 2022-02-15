package org.opengauss.mppdbide.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.utils.CustomStringUtility;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.MathUtils;
import org.opengauss.mppdbide.utils.SSLUtility;

public class CustomStringUtilityTest
{
    CustomStringUtility cutomUtil = null;
    MathUtils           mathUtil  = null;
    List<String>        list      = new ArrayList<String>();

    @Before
    public void setUp() throws Exception
    {
        cutomUtil = new CustomStringUtility();
        mathUtil = new MathUtils();
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void test_cutsom_String_Utility_getFormatedOutput_mathUtils()
    {
        list.add("A");
        if (!list.isEmpty())
        {
            CustomStringUtility.getFormatedOutput(list, "A");
            assertEquals(list.size(), 1);
        }
        MathUtils.roundDoubleValues(20.00, 12);
        MathUtils.roundDoubleValues(20.00, -1);
    }

    @Test
    public void test_cutsom_String_Utility_getFormatedOutput()
    {
        list.add("A");
        list.add("ABC");
        list.add("ABCD");
        if (!list.isEmpty())
        {
            CustomStringUtility.getFormatedOutput(list, "B");
            assertNotEquals(list.size(), 1);
        }
    }

    @Test
    public void test_cutsom_String_Utility_convertStringDateFormat()
    {
        assertTrue(null != CustomStringUtility.convertStringDateFormat("2018-02-27 17:23:16.654 IST",
                MPPDBIDEConstants.DATE_COLLAPSE_FORMAT));
    }

    @Test
    public void test_cutsom_String_Utility_convertStringDateFormat_failure()
    {
        assertTrue(null != CustomStringUtility.convertStringDateFormat("2018-02-27 12:10:22",
                MPPDBIDEConstants.DATE_COLLAPSE_FORMAT));
        SSLUtility.removeSSLLoginStatus("");
    }

    @Test
    public void test_cutsom_String_Utility_stringEndsWithDot_Success()
    {
        assertTrue(CustomStringUtility.isEndsWithDot("abc."));
        assertTrue(CustomStringUtility.isEndsWithDot("\"abc\"."));
        assertTrue(CustomStringUtility.isEndsWithDot("\"a.....bc\"."));
    }

    @Test
    public void test_cutsom_String_Utility_stringEndsWithDot_Failure()
    {
        assertFalse(CustomStringUtility.isEndsWithDot("abc"));
        assertFalse(CustomStringUtility.isEndsWithDot("\"abc\""));
        assertFalse(CustomStringUtility.isEndsWithDot("\"a.....bc\""));
        assertFalse(CustomStringUtility.isEndsWithDot("a.....bc"));
    }

    @Test
    public void test_cutsom_String_Utility_getFormattedStringForAliasCompare_Success()
    {
        assertEquals("aaaaaaaaaa", CustomStringUtility.getFormattedStringForAliasCompare("AAAAAAAAAA"));
        assertEquals("ababababab", CustomStringUtility.getFormattedStringForAliasCompare("AbAbAbAbAb"));
        assertEquals("AbAbAbAbAb", CustomStringUtility.getFormattedStringForAliasCompare("\"AbAbAbAbAb\""));
        assertEquals("aaaaaaaaaa", CustomStringUtility.getFormattedStringForAliasCompare("\"aaaaaaaaaa\""));
    }

    @Test
    public void test_cutsom_String_Utility_getFormattedStringForAliasCompare_Failure()
    {
        assertNotEquals("AAAAAAAAAA", CustomStringUtility.getFormattedStringForAliasCompare("AAAAAAAAAA"));
        assertNotEquals("AbAbAbAbAb", CustomStringUtility.getFormattedStringForAliasCompare("AbAbAbAbAb"));
        assertNotEquals("ababababab", CustomStringUtility.getFormattedStringForAliasCompare("\"AbAbAbAbAb\""));
        assertNotEquals("AAAAAAAAAA", CustomStringUtility.getFormattedStringForAliasCompare("\"aaaaaaaaaa\""));
    }

    @Test
    public void test_cutsom_String_Utility_parseServerVersion_1()
    {
        String unparsedServerVersionString = "PostgreSQL 9.2.4 (Gauss200 OLAP V100R007C10 build e542e841) "
                + "compiled at 2018-07-29 09:10:33 commit 1880 last mr 2754  on x86_64-unknown-linux-gnu, compiled by g++ (GCC) 5.4.0, 64-bit";
        String parsedServerVersionString = CustomStringUtility.parseServerVersion(unparsedServerVersionString);
        assertEquals("openGauss", parsedServerVersionString);
    }
    
    @Test
    public void test_cutsom_String_Utility_parseServerVersion_2()
    {
        String unparsedServerVersionString = "PostgreSQL 9.2.4 (openGauss 1.0 build e2c0f862) compiled at 2020-04-29 10:27:47 commit 2144 last mr 131 debug on aarch64-unknown-linux-gnu, compiled by g++ (GCC) 8.2.0, 64-bit";
        String parsedServerVersionString = CustomStringUtility.parseServerVersion(unparsedServerVersionString);
        assertEquals("openGauss 1.0",
                parsedServerVersionString);
    }
    
    @Test
    public void test_cutsom_String_Utility_parseServerVersion_3()
    {
        String unparsedServerVersionString = "PostgreSQL 9.2.4 (GaussDB Kernel V500R001C20 build ) compiled at 2021-01-11 21:30:21 commit 0 last mr  debug on x86_64-unknown-linux-gnu, compiled by g++ (GCC) 7.3.0, -bit";
        String parsedServerVersionString = CustomStringUtility.parseServerVersion(unparsedServerVersionString);
        assertEquals("PostgreSQL 9.2.4 (GaussDB Kernel V500R001C20 build )",
                parsedServerVersionString);
    }
    
    @Test
    public void test_server_typeString() {
        String originalString = "PostgreSQL 9.2.4 (openGauss 1.0 build e2c0f862) compiled at 2020-04-29 10:27:47 commit 2144 last mr 131 debug on aarch64-unknown-linux-gnu, compiled by g++ (GCC) 8.2.0, 64-bit";
        String trimmedVersion = CustomStringUtility.getFullServerVersionString(originalString);
        assertEquals("openGauss 1.0 build e2c0f862", trimmedVersion);
        String[] info = CustomStringUtility.getServerType(trimmedVersion);
        assertEquals("openGauss", info[0]);
        assertEquals("1.0 build e2c0f862", info[1]);

    }

}
