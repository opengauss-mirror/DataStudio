package com.hauwei.mppdbide.utils.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.huawei.mppdbide.utils.CheckSelectQuery;

/**
 * Check Select Query or Non Select Query
 *
 */
public class CheckSelectQueryTest {
    @Test
    public void test_select_query_executor_001_01() {
        CheckSelectQuery selQuery = new CheckSelectQuery();
        String query = "with tmp as (SELECT * FROM student) select * from tmp;";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertTrue(isSelect);
    }
    
    @Test
    public void test_select_query_executor_001_02() {
        String query = "with tmp as (select id, random() col1, random() col2 from generate_series(1,10000) t(id)), "
                + "ins1 as (insert into a (id,c1) select id,col1 from tmp) insert into b (id,c2)"
                + "select id,col2 from tmp;";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertFalse(isSelect);
    }
    
    @Test
    public void test_select_query_executor_001_03() {
        String query = "WITH EMP_STATUS AS (SELECT EMPID, ENAME, SALARY, DEPTNO FROM DSUSER.EMPLOYEE "
                + "WHERE DEPTNO = 10)\r\n INSERT INTO DSUSER.EMP_LOG (EMP_ID, ISSUE_TYPE, STATUS, "
                + "CREATED_ON) SELECT EMPID, 'NEW ISSUE', 'ACTIVE', CURRENT_TIMESTAMP FROM EMP_STATUS;";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertFalse(isSelect);
    }
    
    @Test
    public void test_select_query_executor_001_04() {
        String query = "WITH moved_rows AS ( DELETE FROM COMPANY WHERE\r\n" + 
                "SALARY >= 30000 RETURNING * )\r\n" + 
                "INSERT INTO COMPANY1 (SELECT * FROM moved_rows);";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertFalse(isSelect);
    }
    
    @Test
    public void test_select_query_executor_001_05() {
        String query = "WITH EMP_update AS (\r\n" + 
                "select * from dsuser.company)\r\n" + 
                "update dsuser.company1 set salary = salary * 0.1 where id in (select id from EMP_update);";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertFalse(isSelect);
    }
    
    @Test
    public void test_select_query_executor_001_06() {
        String query = "WITH EMP_DEL AS (\r\n" + 
                "select * from dsuser.employee)\r\n" + 
                "DELETE from dsuser.company1 where id in (select id from EMP_DEL);";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertFalse(isSelect);
    }
    
    @Test
    public void test_select_query_executor_001_07() {
        String query = "SELECT (SELECT name, games, goals FROM tblMadrid\r\n" + 
                "WHERE  name = 'ronaldo') AS table_a, (SELECT name,\r\n" + 
                "games, goals FROM tblBarcelona WHERE  name = 'messi')   AS table_b ORDER  BY goals;";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertTrue(isSelect);
    }
    
    @Test
    public void test_select_query_executor_001_08() {
        String query = "WITH EmployeeData_Temp([ FirstName ], [ CountryRegionName ]) AS\r\n" + 
                "(SELECT p.[ FirstName ], cr.[ Name ] AS [ CountryRegionName ]\r\n" + 
                "FROM [ HumanResources ].[ Employee ] e\r\n" + 
                "INNER JOIN [ Person ].[ Person ] p ON p.[ BusinessEntityID ] = e.[ BusinessEntityID ]\r\n" + 
                "LEFT OUTER JOIN [ Person ].[ EmailAddress ] ea ON p.[ BusinessEntityID ] = "
                + "ea.[ BusinessEntityID ])\r\n" + 
                "INSERT INTO HumanResources.EmployeeData\r\n" + 
                "SELECT *    \r\n" + 
                "  FROM EmployeeData_Temp;";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertFalse(isSelect);
    }
    
    @Test
    public void test_select_query_executor_001_09() {
        String query = "WITH temporaryTable(averageValue) as\r\n" + 
                "    (SELECT avg(Salary)\r\n" + 
                "    from Employee), \r\n" + 
                "SELECT EmployeeID,Name, Salary \r\n" + 
                "        FROM Employee, temporaryTable \r\n" + 
                "        WHERE Employee.Salary > temporaryTable.averageValue;";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertTrue(isSelect);
    }
    
    @Test
    public void test_select_query_executor_001_10() {
        String query = "WITH dept_count AS (\r\n" + 
                "  SELECT deptno, COUNT(*) AS dept_count\r\n" + 
                "  FROM   emp\r\n" + 
                "  GROUP BY deptno)\r\n" + 
                "SELECT e.ename AS employee_name,\r\n" + 
                "       dc.dept_count AS emp_dept_count\r\n" + 
                "FROM   emp e,\r\n" + 
                "       dept_count dc\r\n" + 
                "WHERE  e.deptno = dc.deptno;";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertTrue(isSelect);
    }
    
    @Test
    public void test_select_query_executor_001_011() {
        String query = "Explain with temp as\r\n" + 
                "(select 1 as C1 union allselect 2 union allselect 3)\r\n" + 
                "select * from temp order by 1 ASC;";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertFalse(isSelect);
    }
    
    @Test
    public void test_select_query_executor_001_012() {
        String query = "create view s_v as (with temp as (select 1 as C1\r\n" + 
                "union all select 2 union all select 3) select * from temp\r\n" + 
                "order by 1 ASC);\r\n";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertFalse(isSelect);
    }
    
    @Test
    public void test_select_query_executor_001_013() {
        String query = "WITH regional_sales AS ( SELECT region, SUM(amount) "
                + "AS total_sales FROM orders GROUP BY region), "
                + "top_regions AS ( SELECT region FROM regional_sales "
                + "WHERE total_sales > (SELECT SUM(total_sales)/10 "
                + "FROM regional_sales))\r\n" + 
                "SELECT region, product, SUM(quantity) AS product_units, "
                + "SUM(amount) AS product_sales FROM orders\r\n" 
                + "WHERE region IN (SELECT region FROM top_regions)\r\n" 
                + "GROUP BY region, product;";
        boolean isSelect = CheckSelectQuery.isSelectQuery(query);
        assertTrue(isSelect);
    }
}
