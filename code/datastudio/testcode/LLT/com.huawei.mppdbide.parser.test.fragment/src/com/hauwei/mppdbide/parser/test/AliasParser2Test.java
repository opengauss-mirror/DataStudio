package com.hauwei.mppdbide.parser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hauwei.mppdbide.parser.mock.CommonLLTUtils;
import com.huawei.mppdbide.parser.alias.AliasParser;

public class AliasParser2Test
{
    
    AliasParser aliasParser;
    HashMap<String,List<String>> aliasToTableNameMap;

    @Before
    public void setUp() throws Exception
    {
        aliasParser = new AliasParser();
        aliasToTableNameMap = null;
    }
    
    @After
    public void tearDown() throws Exception
    {
        aliasParser = null;
    }
    
    private void parseAndCheck(String query)
    {
        aliasParser.parseQuery(query);
        aliasToTableNameMap = aliasParser.getParseContext().getAliasToTableNameMap();
        Assert.assertTrue(aliasToTableNameMap.equals(CommonLLTUtils.getAliasMapForQuery(query)));
    }
    
    @Test
    public void TEST_ALIAS_IN_JOINS_1()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_JOINS_1);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_JOINS_2);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_JOINS_2()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_JOINS_3);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_JOINS_4);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_JOINS_3()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_JOINS_5);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_JOINS_6);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_JOINS_4()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_JOINS_7);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_JOINS_8);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_DELETE_QUERY_1()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_DELETE_QUERY_1);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_DELETE_QUERY_2);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_DELETE_QUERY_2()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_DELETE_QUERY_3);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_DELETE_QUERY_4);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_DELETE_QUERY_5);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_UPDATE_QUERY_1()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_UPDATE_QUERY_1);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_UPDATE_QUERY_2);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_UPDATE_QUERY_2()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_UPDATE_QUERY_3);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_UPDATE_QUERY_4);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_UPDATE_QUERY_3()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_UPDATE_QUERY_5);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_UPDATE_QUERY_6);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_USAGE_MISC()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_USAGE_MISC);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_SUBQUERY()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_SUBQUERY_1);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_SUBQUERY_2);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_UNION()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_UNION);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_CASE()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_CASE);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_ERROR_1()
    {
        try
        {
            aliasParser.parseQuery("SELECT * ABC as A");
            assertEquals(0 , aliasParser.getParseContext().getAliasToTableNameMap().size());
        }
        catch (Exception e)
        {
            fail("fail");
        }
    }
    
    @Test
    public void TEST_ALIAS_ERROR_2()
    {
        try
        {
            aliasParser.parseQuery("SELECT * FROM XXX as A YYY as B");
            assertEquals(1, aliasParser.getParseContext().getAliasToTableNameMap().size());
        }
        catch (Exception e)
        {
            fail("fail");
        }
    }
    
    /*@Test
    public void TEST_ALIAS_INTERRUPTED()
    {
        try
        {
            Job aliasJob = new Job("alias job")
            {

                @Override
                protected IStatus run(IProgressMonitor monitor)
                {
                    String query = "SELECT COUNT(*) FROM giri.t1 s"
                            + " WHERE EXISTS"
                            + " ( SELECT 1"
                            + " FROM giri.t1 c"
                            + " WHERE c.customer_id = s.supplier_id"
                            + " AND c.##unknown < 1000 );";
                    
                    String query1 = "select "
                            + " giri.t2.name || giri.t2.name n ||"
                            + " a11."
                            + " giri.t2 a11 ;";
                    aliasParser.parseQuery(query1);
                    return null;
                }
            };
                
            aliasJob.schedule();
            Thread.sleep(10);
            aliasJob.getThread().interrupt();
        }
        catch (RunTimeParserException e)
        {
             expected flow 
            assertTrue(true);
            return;
        }
        catch (Exception e)
        {
            fail("fail");
        }
    }
    
    private class a
    {
        
    }*/
}