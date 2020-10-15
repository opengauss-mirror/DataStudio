package com.huawei.mppdbide.editor.extension.formatter.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import com.huawei.mppdbide.editor.extension.nameparser.ParseObjectNameMain;

import org.junit.Test;

public class ParseObjectNameMainTest
{
    String         input;
    String         output;
    List<String[]> inputList;
    List<String[]> outputList;
    
    @Test
    public void getObjectNameTest()
    {
        ParseObjectNameMain formatter = new ParseObjectNameMain();
        formatter.parsename("CREATE OR REPLACE FUNCTION public.aa01()"); 
        this.input = "public.aa01";
        this.output = formatter.getObjectName(); 
        assertTrue(input.equals(output));
    }
    
    @Test
    public void getSchemaNameTest()
    {
        ParseObjectNameMain formatter = new ParseObjectNameMain();
        formatter.parsename("CREATE OR REPLACE FUNCTION public.aa01()"); 
        this.input = "public";
        this.output = formatter.getSchemaName(); 
        assertTrue(input.equals(output));
    }
    
    @Test
    public void getFuncNameTest()
    {
        ParseObjectNameMain formatter = new ParseObjectNameMain();
        formatter.parsename("CREATE OR REPLACE FUNCTION public.aa01()"); 
        this.input = ".aa01";
        this.output = formatter.getFuncName(); 
        assertTrue(input.equals(output));
    }
    
    @Test
    public void getArguementsTest_NoArgs()
    {
        List<String[]> ls = Collections.<String[]>emptyList();
        ParseObjectNameMain formatter = new ParseObjectNameMain();
        formatter.parsename("CREATE OR REPLACE FUNCTION public.aa01()");
        this.inputList=ls;
        this.outputList=formatter.getArgs();
        assertEquals(inputList, outputList);
    }
    
    @Test
    public void getObjectType_NoObjectType()
    {
        ParseObjectNameMain formatter = new ParseObjectNameMain();
        formatter.parsename("CREATE OR REPLACE FUNCTION public.aa01()");
        this.input=null;
        this.output=formatter.getObjectType();
        assertFalse(output.equals(input));
    }
    
   /*
    public void testDummy()
    {
        assertEquals(true, true);
    }*/
}
