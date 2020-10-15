package com.hauwei.mppdbide.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.huawei.mppdbide.utils.ConvertValueToInsertSqlFormat;
import com.huawei.mppdbide.utils.JSQLParserUtils;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;

public class JSQLParserUtilsTest
{
    static final String SIMPLE_SELECT_1 = "SELECT * FROM PUBLIC.abc";
    static final String SIMPLE_SELECT_2 =
            "SELECT * FROM PUBLIC.abc WHERE PUBLIC.abc.id1 >= 10 AND PUBLIC.abc.id1 < 100 OR PUBLIC.abc.id2 <= 100 AND PUBLIC.abc.id2 > 10";
    static final String NESTED_SELECT_1 = "SELECT p.product_id, p.product_name"
            + " FROM products p"
            + " WHERE p.category_id IN"
            + " (SELECT c.category_id"
            + " FROM categories c"
            + " WHERE c.category_id > 25"
            + " AND c.category_name like 'S%');";
    static final String SELECT_WITH_JOIN_1 = "SELECT p.product_id, p.product_name"
            + " FROM products p"
            + " INNER JOIN categories c"
            + " ON p.category_id = c.category_id"
            + " WHERE c.category_id > 25"
            + " AND c.category_name like 'S%';";
    static final String SELECT_WITH_JOIN_2 = "SELECT products.product_name, subquery1.category_name"
            + " FROM products,"
            + " (SELECT categories.category_id, categories.category_name, COUNT(category_id) AS total"
            + " FROM categories"
            + " GROUP BY categories.category_id, categories.category_name) subquery1"
            + " WHERE subquery1.category_id = products.category_id;";
    static final String SELECT_WITH_AS = "With CTE AS"
            + " (Select"
            + " ID"
            + " , NAME"
            + " , AGE"
            + " , ADDRESS"
            + " , SALARY"
            + " FROM COMPANY )"
            + " Select * From CTE;";
    static final String SELECT_CASE_WHEN = "SELECT a,"
            + " CASE WHEN a=1 THEN 'one'"
            + " WHEN a=2 THEN 'two'"
            + " ELSE 'other'"
            + " END"
            + " FROM test;";
    
    public static final String  SELECT_EXPRESSIONS_1 = "SELECT e.first_name || '  ' || e.lastname FROM emp e;";
    public static final String  SELECT_EXPRESSIONS_2 = "SELECT 'Mr. '||UPPER(a.fname)||' , '|| lower(a.lname),((a.cust + a.customer_id)) FROM public.customer a";
    public static final String SELECT_EXPRESSIONS_3  =
            "SELECT 'Mr. '||UPPER(\"Hello Wold''s\".fname)||' , '|| lower(\"Hello Wold''s\".gname),(( \"Hello Wold''s\".customer_id + \"Hello Wold''s\".customer_id )) "
            + "FROM public.customer \"Hello Wold''s\";";
    public static final String  SELECT_EXPRESSIONS_4 = "SELECT min(e.salary), max(e.name) FROM emp e;";
    public static final String  SELECT_EXPRESSIONS_5 = "SELECT avg(e.salary), max(e.name) FROM emp e;";
    public static final String  SELECT_EXPRESSIONS_6 = "SELECT max(e.salary), avg(e.name) FROM emp e;";
    public static final String  SELECT_EXPRESSIONS_7 = "SELECT min(e.salary), count(e.name) FROM emp e;";
    public static final String  SELECT_EXPRESSIONS_8 = "SELECT P.ProductName, P.UnitPrice * (P.UnitsInStock + IFNULL(P.prod, 0)) FROM Products P;";
    public static final String  SELECT_EXPRESSIONS_9 = "SELECT P.ProductName, P.UnitPrice * (P.UnitsInStock + COALESCE(P.prod, 0)) FROM Products P;";
    public static final String  SELECT_EXPRESSIONS_10 = "SELECT COUNT(DISTINCT e.prod) FROM emp e;";
    public static final String  SELECT_EXPRESSIONS_11 = "SELECT COALESCE(NULL,e.prod) from dsuser.\"emp salar y\" e;";
    public static final String  SELECT_EXPRESSIONS_12 = "SELECT DECODE('A','A', 1,'B',2,0) FROM DUAL;";
    public static final String  SELECT_EXPRESSIONS_13 = "SELECT CONCAT (e.prod || '  ' || e.Last_name ) from emp e;";
    public static final String  SELECT_EXPRESSIONS_14 = "SELECT e.name *12 from emp e;";
    public static final String  SELECT_EXPRESSIONS_15 = "SELECT * from emp e where e.name *12 > 60000;";
    public static final String  SELECT_EXPRESSIONS_16 = "select char_length(e.name) from emp e;";
    public static final String  SELECT_EXPRESSIONS_17 = "SELECT e.name | 3 from emp e;";
    public static final String  SELECT_EXPRESSIONS_18 = "SELECT e.id # 15 from emp e;";
    public static final String  SELECT_EXPRESSIONS_19 = "SELECT e.id ~ 1 from emp e;";
    public static final String  SELECT_EXPRESSIONS_20 = "SELECT e.id << 4 from emp e;";
    public static final String  SELECT_EXPRESSIONS_21 = "select string_agg(e.id , ',') from dsuser.\"emp salar y\" e;";
    public static final String  SELECT_EXPRESSIONS_22 = "select array_agg(e.name) from dsuser.\"emp salar y\" e;";
    public static final String  SELECT_EXPRESSIONS_23 = "select trim(e.name) like trim('uk') from dsuser.emp e;";
    public static final String  SELECT_EXPRESSIONS_24 = "select e.country, trim(e.country) similar to 'usa', trim(e.name) similar to '%uk%' from dsuser.emp e;";
    public static final String  SELECT_EXPRESSIONS_25 = "select upper(trim(e.country)) from dsuser.emp e;";
    public static final String  SELECT_EXPRESSIONS_26 = "select (e.name * 0.1) as new_bonus from emp e;";
    
    public static final String SELECT_JOINS_1 = "SELECT C.ID, C.NAME, C.AGE, D.DEPT FROM COMPANY AS C, DEPARTMENT AS D WHERE  C.ID = D.EMP_ID;";
    public static final String SELECT_JOINS_2 = "SELECT C.ID, C.NAME, C.AGE, D.DEPT FROM COMPANY AS C, DEPARTMENT AS D WHERE  C.ID > D.EMP_ID;";
    public static final String SELECT_JOINS_3 = "SELECT C.ID, C.NAME, C.AGE, D.DEPT FROM COMPANY AS C, DEPARTMENT AS D WHERE  C.ID < D.EMP_ID;";
    public static final String SELECT_JOINS_4 = "SELECT C.ID, C.NAME, C.AGE, D.DEPT FROM COMPANY AS C, DEPARTMENT AS D WHERE  C.ID <= D.EMP_ID;";
    public static final String SELECT_JOINS_5 = "SELECT C.ID, C.NAME, C.AGE, D.DEPT FROM COMPANY AS C, DEPARTMENT AS D WHERE  C.ID >= D.EMP_ID;";
    public static final String SELECT_JOINS_6 = "SELECT A.CustomerName AS CustomerName1, B.CustomerName AS CustomerName2, A.City"
            + " FROM Customers A, Customers B WHERE A.CustomerID <> B.CustomerID AND A.City = B.City  ORDER BY A.City;";
    public static final String SELECT_JOINS_7 = "SELECT Ord.OrderID, Cust.CustomerName, Ord.OrderDate"
            + " FROM Orders Ord INNER JOIN Customers Cust ON Orders.CustomerID=Customers.CustomerID;";
    public static final String SELECT_JOINS_8 = "SELECT Cust.CustomerName, Ord.OrderID"
            + " FROM Customers Cust LEFT JOIN Orders Ord ON Cust.CustomerID = Ord.CustomerID"
            + " ORDER BY Customers.CustomerName;";
    public static final String SELECT_JOINS_9 = "SELECT Ord.OrderID, Emp.LastName, Emp.FirstName"
            + " FROM Orders Ord RIGHT JOIN Employees Emp ON Orders.EmployeeID = Employees.EmployeeID"
            + " ORDER BY Ord.OrderID;";

    public static final String SELECT_EXPRESSIONS_27 =
            "SELECT P.ProductName, P.UnitPrice / (P.UnitsInStock - COALESCE(P.prod, 0)) FROM Products P;";
    public static final String SELECT_EXPRESSIONS_28 =
            "SELECT P.ProductName, P.UnitPrice & (P.UnitsInStock - COALESCE(P.prod, 0)) FROM Products P where P.UnitPrice > 100;";
    public static final String SELECT_EXPRESSIONS_29 =
            "SELECT P.ProductName, P.UnitPrice # (P.UnitsInStock - COALESCE(P.prod, 0)) FROM Products P;";
    public static final String EXAMPLE_QUERY = "SELECT a,b,"
            + " CASE WHEN a>11 THEN 'great than eleven'"
            + " WHEN a=2 THEN 'two'"
            + " WHEN a ~ '^w' THEN 'regex'"
            + " WHEN INTERVAL 1 DAY THEN 'interval'"
            + " WHEN NOT a THEN 'not a'"
            + " WHEN a!=2 THEN 'not two'"
            + " WHEN a IN (1,2) THEN 'in one two'"
            + " WHEN a<2 THEN 'less than two'"
            + "WHEN a BETWEEN 1 AND 2 THEN 'between one and two'"
            + "WHEN a<=12 THEN 'less than equal to twelve'"
            + "WHEN a>=12 THEN 'great than equal to twelve'"
            + "WHEN a AND b THEN 'a and b'"
            + "WHEN a OR b THEN 'a or b'"
            + "WHEN a LIKE '%a_' THEN 'a like %a'"
            + " ELSE 'other'"
            + " END"
            + " FROM test;";
    
    public static final String EXAMPLE_QUERY_2 = "SELECT a,(SELECT b FROM test)"
    		+ " FROM test;";
    public static final String  ALL_TABLE_COLUMN_CUST_ISSUE_QUERY_1 = "SELECT dsuser.aaaaa.* FROM dsuser.aaaaa;";
    public static final String  ALL_TABLE_COLUMN_CUST_ISSUE_QUERY_2 = "SELECT ali.* FROM dsuser.aaaaa ali;";
    public static final String  ALL_TABLE_COLUMN_CUST_ISSUE_QUERY_3 = "SELECT ali.* FROM dsuser.aaaaa as ali;";
    public static final String  ALL_TABLE_COLUMN_CUST_ISSUE_QUERY_4 = "SELECT ali.* , ali.c1 , ali.c2 FROM dsuser.aaaaa as ali;";
    public static final String  ALL_TABLE_COLUMN_CUST_ISSUE_QUERY_5 = "SELECT dsuser.aaaaa.* , dsuser.aaaaa.c1 FROM dsuser.aaaaa ;";
    
    @Test
    public void test_simpleSelect_1()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SIMPLE_SELECT_1);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertTrue(queryResultEditSupported);
    }
    
    @Test
    public void test_simpleSelect_2()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SIMPLE_SELECT_2);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertTrue(queryResultEditSupported);
    }
    
   @Test
    public void test_nestedSelect_1()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(NESTED_SELECT_1);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertTrue(queryResultEditSupported);
    }
    
    @Test
    public void test_selectWithJoin_1()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_WITH_JOIN_1);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectWithJoin_2()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_WITH_JOIN_2);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectWithAs()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_WITH_AS);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectCaseWhen()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_CASE_WHEN);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_1()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_1);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_2()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_2);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_3()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_3);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_4()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_4);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_5()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_5);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_6()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_6);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_7()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_7);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_8()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_8);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_9()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_9);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_10()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_10);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_11()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_11);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_12()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_12);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_13()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_13);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_14()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_14);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_15()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_15);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertTrue(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_16()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_16);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_17()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_17);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_18()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_18);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_19()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_19);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_20()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_20);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_21()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_21);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_22()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_22);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_23()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_23);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_24()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_24);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_25()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_25);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_26()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_26);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectJoin_1()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_JOINS_1);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectJoin_2()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_JOINS_2);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectJoin_3()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_JOINS_3);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectJoin_4()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_JOINS_4);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectJoin_5()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_JOINS_5);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectJoin_6()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_JOINS_6);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectJoin_7()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_JOINS_7);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectJoin_8()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_JOINS_8);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectJoin_9()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_JOINS_9);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }

@Test
    public void test_selectExp_27()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_27);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_28()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_28);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }
    
    @Test
    public void test_selectExp_29()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(SELECT_EXPRESSIONS_29);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertFalse(queryResultEditSupported);
    }

    @Test
    public void test_randExp_01()
    {
    	boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(EXAMPLE_QUERY);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
    	assertFalse(queryResultEditSupported);
    }
    @Test
    public void test_randExp_02()
    {
    	boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(EXAMPLE_QUERY_2);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
    	assertFalse(queryResultEditSupported);
    }
    
    
    public static final String EXAMPLE_QUERY_3 = "SELECT t "
    		+ "FROM (SELECT 1, 'x', NUMERIC '42.1')"
    		+ " AS t;";
    
    
    @Test
    public void test_randExp_03()
    {
    	boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(EXAMPLE_QUERY_3);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
    	assertFalse(queryResultEditSupported);
    }
    
    /* Test cases for customer issue : DTS2018122006310  
     * Table is not editable when query like below is given 
     * SELECT a.* FROM table as a */
    
    @Test
    public void test_DTS2018122006310_01()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(ALL_TABLE_COLUMN_CUST_ISSUE_QUERY_1);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertTrue(queryResultEditSupported);
    }
    
    @Test
    public void test_DTS2018122006310_02()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(ALL_TABLE_COLUMN_CUST_ISSUE_QUERY_2);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertTrue(queryResultEditSupported);
    }
    
    @Test
    public void test_DTS2018122006310_03()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(ALL_TABLE_COLUMN_CUST_ISSUE_QUERY_3);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertTrue(queryResultEditSupported);
    }
    
    @Test
    public void test_DTS2018122006310_04()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(ALL_TABLE_COLUMN_CUST_ISSUE_QUERY_4);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertTrue(queryResultEditSupported);
    }
    
    @Test
    public void test_DTS2018122006310_05()
    {
        boolean queryResultEditSupported = false;
        try
        {
            queryResultEditSupported = JSQLParserUtils.isQueryResultEditSupported(ALL_TABLE_COLUMN_CUST_ISSUE_QUERY_5);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(false);
            
        }
        assertTrue(queryResultEditSupported);
    }
    
    
    @Test
    public void test_convert_to_sql_01()
    {
        ConvertValueToInsertSqlFormat convertSQl = new ConvertValueToInsertSqlFormat();
        assertEquals(null,convertSQl.convertValueToSQL(0, "NULL", null));
        assertEquals("\'money\'",convertSQl.convertValueToSQL(8, "money", "money"));
        assertEquals("money",convertSQl.convertValueToSQL(8, "money", "rupee"));
        assertEquals("\'1\'",convertSQl.convertValueToSQL(-7, "true", null));
        assertEquals("\'0\'",convertSQl.convertValueToSQL(-7, "false", null));
        assertEquals("false",convertSQl.convertValueToSQL(-7, "false", "bool"));
        assertEquals("true",convertSQl.convertValueToSQL(-7, "true", "bool"));
        assertEquals("null",convertSQl.convertValueToSQL(-7, "null", "bool"));
        assertEquals("\'\'false\'\'",convertSQl.convertValueToSQL(-7, "\'false\'", "bool"));
    }
}