package org.opengauss.mppdbide.parser.test;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.parser.mock.CommonLLTUtils;
import org.opengauss.mppdbide.parser.alias.AliasParser;

public class AliasParser1Test
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
    public void TEST_ALIAS_IN_PROJECTION()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_PROJECTION_1);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_PROJECTION_2);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_PROJECTION_3);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_PROJECTION_4);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_CASE_CEHCK()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_CASE_CHECK_1);
            parseAndCheck(CommonLLTUtils.ALIAS_CASE_CHECK_2);
            parseAndCheck(CommonLLTUtils.ALIAS_CASE_CHECK_3);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_1()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_1);
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_2);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_2()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_3);
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_4);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_3()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_5);
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_6);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_4()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_7);
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_8);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_5()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_9);
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_10);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_6()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_11);
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_12);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_8()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_13);
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_14);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_9()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_15);
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_16);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_10()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_17);
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_18);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_11()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_19);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_12()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_21);
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_22);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_13()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_23);
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_24);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_INSIDE_EXPRESSIONS_14()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_25);
            parseAndCheck(CommonLLTUtils.ALIAS_INSIDE_EXPRESSIONS_26);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_TO_CATALOGS()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_TO_CATALOGS);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_GROUP_BY()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_GROUP_BY_1);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_GROUP_BY_2);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_HAVING()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_HAVING_1);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_HAVING_2);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
    
    @Test
    public void TEST_ALIAS_IN_ORDER_BY()
    {
        try
        {
            parseAndCheck(CommonLLTUtils.ALIAS_IN_ORDER_BY_1);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_ORDER_BY_2);
            parseAndCheck(CommonLLTUtils.ALIAS_IN_ORDER_BY_3);
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }
}