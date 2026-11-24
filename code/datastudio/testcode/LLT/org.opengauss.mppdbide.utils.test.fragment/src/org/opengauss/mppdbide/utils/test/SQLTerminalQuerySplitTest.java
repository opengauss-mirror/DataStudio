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

package org.opengauss.mppdbide.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.utils.SQLTerminalQuerySplit;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

public class SQLTerminalQuerySplitTest
{
    SQLTerminalQuerySplit sqlTerminalSplit = null;
    ArrayList<String>     queryArray       = new ArrayList<String>();

    @Before
    public void setUp() throws Exception
    {
        sqlTerminalSplit = new SQLTerminalQuerySplit();
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void test_split_queries()
    {
        try
        {
            sqlTerminalSplit.splitQuerries(queryArray,
                    "CREATE TABLE COMPANY(ID INT PRIMARY KEY NOT NULL,NAME  TEXT NOT NULL,AGE INT NOT NULL,ADDRESS CHAR('50'),SALARY REAL);", true);
            assertTrue(queryArray != null);
        }
        catch (DatabaseOperationException e)
        {
            fail(" not expected");
            assertTrue(queryArray != null);
        }
    }

    @Test
    public void test_split_queries_1()
    {
        try
        {
            sqlTerminalSplit.splitQuerries(queryArray,
                    "/*CREATE FUNCTION totalRecords () + \n + RETURNS integer AS $total$ + \n + declare + \n + total integer; + \n+ BEGIN + \n +SELECT count(*) into total FROM COMPANY; + \n + RETURN total; + \n +END; +\n +$total$ LANGUAGE plpgsql;*/", true);
            assertTrue(queryArray != null);
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(queryArray != null);
            fail(" not expected");
        }
    }

    @Test
    public void test_split_queries_2()
    {
        try
        {
            sqlTerminalSplit.splitQuerries(queryArray,
                    "--CREATE FUNCTION totalRecords () + \n + RETURNS integer \\AS $total$ + \n + declare + \n + total integer; + \n+ BEGIN + \n +SELECT \\count(*) into total FROM COMPANY; + \n + RETURN total; + \n +END; +\n +$total$ LANGUAGE plpgsql;", true);
            assertTrue(queryArray != null);
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(queryArray != null);
            fail(" not expected");
        }
    }

    @Test
    public void test_split_queries_3()
    {
        try
        {
            sqlTerminalSplit.splitQuerries(queryArray,
                    "CREATE FUNCTION totalRecords () + \n + RETURNS integer AS $total$ + \n + declare + \n + total integer; + \n+ BEGIN + \n +SELECT count(*) into total FROM /*company details*/ COMPANY; + \n + RETURN total; + \n +END; +\n +$total$ LANGUAGE plpgsql;", true);
            assertTrue(queryArray != null);
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(queryArray != null);
            fail(" not expected");
        }
    }

    @Test
    public void test_split_queries_4()
    {
        try
        {
            sqlTerminalSplit.splitQuerries(queryArray,
                    "CREATE FUNCTION totalRecords () + \n + RETURNS integer AS /* names*/ \"$total$\" /*total  numbers*/ + \n + declare + \n + total integer;$$+ \n+  BEGIN + \n +SELECT /* names*/count(*) into total FROM COMPANY; + \n + RETURN total; + \n +END; +\n +$total$ LANGUAGE plpgsql;", true);
            assertTrue(queryArray != null);
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(queryArray != null);
            fail(" not expected");
        }
    }

    @Test
    public void test_split_queries_5()
    {
        try
        {
            sqlTerminalSplit.splitQuerries(queryArray,
                    "create procedure company /* company name*/ returns integer;/*end*/", true);
            assertTrue(queryArray != null);
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(queryArray != null);
            fail(" not expected");
        }
    }
    
    @Test
    public void test_split_queries_6()
    {
        try
        {
            sqlTerminalSplit.splitQuerries(queryArray,
                    "insert into \"test--table\"  values ('aaa--bbb');", false);
            assertEquals("insert into \"test--table\"  values ('aaa--bbb')", queryArray.get(0));
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(queryArray != null);
            fail(" not expected");
        }
    }
    
    @Test
    public void test_split_queries_7()
    {
        try
        {
            sqlTerminalSplit.splitQuerries(queryArray,
                    "insert into \"a--a'--'--bb\"  values ('aaa--bbb');", false);
            assertEquals("insert into \"a--a'--'--bb\"  values ('aaa--bbb')", queryArray.get(0));
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(queryArray != null);
            fail(" not expected");
        }
    }

}
