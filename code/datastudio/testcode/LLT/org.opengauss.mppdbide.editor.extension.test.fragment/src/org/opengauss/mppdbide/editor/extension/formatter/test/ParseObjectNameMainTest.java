/*
 * Copyright (c) 2026 Huawei Technologies Co., Ltd.
 * DataStudio is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.editor.extension.formatter.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.opengauss.mppdbide.editor.extension.nameparser.ParseObjectNameMain;

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
