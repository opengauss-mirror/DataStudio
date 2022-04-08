package org.opengauss.mppdbide.parser.mock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CommonLLTUtils
{
    
    /* QUERIES SUPPORTED FOR ALIAS AUTO SUGGEST FOR GaussTools 18.1.0 VERSION */
    private static final String     ALIAS_DUMMY_STRING  =   "##unknown";
    public static final String  ALIAS_IN_PROJECTION_1 = "SELECT e." + ALIAS_DUMMY_STRING + " FROM public.emp e;";
    public static final String  ALIAS_IN_PROJECTION_2 = "SELECT e.empid,e." + ALIAS_DUMMY_STRING + " FROM public.emp e;";
    public static final String  ALIAS_IN_PROJECTION_3 = "SELECT distinct e." + ALIAS_DUMMY_STRING + " FROM public.emp e;";
    public static final String  ALIAS_IN_PROJECTION_4 = "SELECT e." + ALIAS_DUMMY_STRING + " FROM public.emp AS e;";
    
    public static final String  ALIAS_CASE_CHECK_1 = "SELECT e.empid, e." + ALIAS_DUMMY_STRING + " FROM public.emp AS E;";
    public static final String  ALIAS_CASE_CHECK_2 = "SELECT e.empid, e." + ALIAS_DUMMY_STRING + " FROM public.emp AS \"E\";";
    public static final String  ALIAS_CASE_CHECK_3 = "SELECT \"E\"." + ALIAS_DUMMY_STRING + " FROM public.emp AS \"E\";";
    
    public static final String  ALIAS_INSIDE_EXPRESSIONS_1 = "SELECT e.first_name || '  ' || e." + ALIAS_DUMMY_STRING +" FROM emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_2 = "SELECT 'Mr. '||UPPER(a.fname)||' , '|| lower(a.lname),((a." 
                    + ALIAS_DUMMY_STRING + " + a.customer_id)) FROM public.customer a";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_3 = "SELECT 'Mr. '||UPPER(\"Hello Wold''s\".fname)||' , '|| lower(\"Hello Wold''s\"."
                    + ALIAS_DUMMY_STRING +"),(( \"Hello Wold''s\".customer_id + \"Hello Wold''s\".customer_id )) FROM public.customer \"Hello Wold''s\";";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_4 = "SELECT min(e.salary), max(e."+ ALIAS_DUMMY_STRING +") FROM emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_5 = "SELECT avg(e.salary), max(e."+ ALIAS_DUMMY_STRING +") FROM emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_6 = "SELECT max(e.salary), avg(e."+ ALIAS_DUMMY_STRING +") FROM emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_7 = "SELECT min(e.salary), count(e."+ ALIAS_DUMMY_STRING +") FROM emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_8 = "SELECT P.ProductName, P.UnitPrice * (P.UnitsInStock + IFNULL(P."+ ALIAS_DUMMY_STRING +", 0)) FROM Products P;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_9 = "SELECT P.ProductName, P.UnitPrice * (P.UnitsInStock + COALESCE(P."+ ALIAS_DUMMY_STRING +", 0)) FROM Products P;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_10 = "SELECT COUNT(DISTINCT e."+ ALIAS_DUMMY_STRING +") FROM emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_11 = "SELECT COALESCE(NULL,e."+ ALIAS_DUMMY_STRING +") from dsuser.\"emp salar y\" e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_12 = "SELECT DECODE('A','A', 1,'B',2,0) FROM DUAL;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_13 = "SELECT CONCAT (e."+ ALIAS_DUMMY_STRING +" || '  ' || e.Last_name ) from emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_14 = "SELECT e."+ ALIAS_DUMMY_STRING +" *12 from emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_15 = "SELECT * from emp e where e."+ ALIAS_DUMMY_STRING +" *12 > 60000;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_16 = "select char_length(e."+ ALIAS_DUMMY_STRING +") from emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_17 = "SELECT e."+ ALIAS_DUMMY_STRING +"| 3 from emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_18 = "SELECT e."+ ALIAS_DUMMY_STRING +"#15 from emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_19 = "SELECT e."+ ALIAS_DUMMY_STRING +"~ 1 from emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_20 = "SELECT e."+ ALIAS_DUMMY_STRING +"<< 4 from emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_21 = "select string_agg(e."+ ALIAS_DUMMY_STRING +",',') from dsuser.\"emp salar y\" e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_22 = "select array_agg(e."+ ALIAS_DUMMY_STRING +") from dsuser.\"emp salar y\" e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_23 = "select trim(e."+ ALIAS_DUMMY_STRING +") like trim('uk') from dsuser.emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_24 = "select e.country, trim(e.country) similar to 'usa', trim(e."
                    + ALIAS_DUMMY_STRING +") similar to '%uk%' from dsuser.emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_25 = "select upper(trim(e.country)) from dsuser.emp e;";
    public static final String  ALIAS_INSIDE_EXPRESSIONS_26 = "select (e."+ ALIAS_DUMMY_STRING +" * 0.1) as new_bonus from emp e;";
    
    public static final String  ALIAS_TO_CATALOGS = "SELECT collation for (pg_desc."+ ALIAS_DUMMY_STRING +") FROM pg_description pg_desc LIMIT 1;";
    
    public static final String ALIAS_IN_GROUP_BY_1 = "select e.deptno, count(*) from  emp e group by e." + ALIAS_DUMMY_STRING + ";";
    public static final String ALIAS_IN_GROUP_BY_2 = "SELECT  s.brand, s.segment, SUM (quantity) FROM sales s GROUP BY GROUPING SETS ("
                    + "(s.brand, s.segment),"
                    + "(s.brand),"
                    + "(s."+ ALIAS_DUMMY_STRING +"));";
    
    public static final String ALIAS_IN_HAVING_1 = "select e.deptno, count(*) from  emp e group by e.deptno having e." + ALIAS_DUMMY_STRING + ";";
    public static final String ALIAS_IN_HAVING_2 = "select e.deptno, count(*) from  emp e group by e.deptno having e." + ALIAS_DUMMY_STRING + " <100;";
    
    public static final String ALIAS_IN_ORDER_BY_1 = "select e.deptno, count(*) from  emp e where e.salary <=6000 "
                    + "group by e.deptno having e.deptno in (10,20,30) order by e." + ALIAS_DUMMY_STRING + ";";
    public static final String ALIAS_IN_ORDER_BY_2 = "select * from  emp e order by e."+ ALIAS_DUMMY_STRING +" limit 100;";
    public static final String ALIAS_IN_ORDER_BY_3 = "select * from  emp e order by e.ename ASC, e."+ ALIAS_DUMMY_STRING +" DESC;";
    
    public static final String ALIAS_IN_JOINS_1 = "SELECT C.ID, C.NAME, C.AGE, D.DEPT FROM COMPANY AS C, DEPARTMENT AS D WHERE  C."+ ALIAS_DUMMY_STRING +" = D.EMP_ID;";
    public static final String ALIAS_IN_JOINS_2 = "SELECT C.ID, C."+ ALIAS_DUMMY_STRING +", C.AGE, D.DEPT FROM COMPANY AS C, DEPARTMENT AS D WHERE  C.ID = D.EMP_ID;";
    public static final String ALIAS_IN_JOINS_3 = "SELECT p.product_id, p.product_name, inventory.quantity"
                    + " FROM products p"
                    + " INNER JOIN inventory"
                    + " ON p."+ ALIAS_DUMMY_STRING +" = inventory.product_id"
                    + " ORDER BY p.product_name ASC, inventory.quantity DESC;";
    public static final String ALIAS_IN_JOINS_4 = "SELECT a.emp_id AS \"Emp_ID\",a.emp_name AS \"Employee Name\", b.emp_id AS \"Supervisor ID\",b.emp_name AS \"Supervisor Name\""
            + " FROM employee a, employee b WHERE a.emp_supv = b.emp_id;";
    public static final String ALIAS_IN_JOINS_5 = "SELECT A.CustomerName AS CustomerName1, B.CustomerName AS CustomerName2, A.City"
            + " FROM Customers A, Customers B WHERE A.CustomerID <> B.CustomerID AND A.City = B.City  ORDER BY A.City;";
    public static final String ALIAS_IN_JOINS_6 = "SELECT Ord.OrderID, Cust.CustomerName, Ord.OrderDate"
            + " FROM Orders Ord INNER JOIN Customers Cust ON Orders.CustomerID=Customers.CustomerID;";
    public static final String ALIAS_IN_JOINS_7 = "SELECT Cust.CustomerName, Ord.OrderID"
            + " FROM Customers Cust LEFT JOIN Orders Ord ON Cust.CustomerID = Ord.CustomerID"
            + " ORDER BY Customers.CustomerName;";
    public static final String ALIAS_IN_JOINS_8 = "SELECT Ord.OrderID, Emp.LastName, Emp.FirstName"
            + " FROM Orders Ord RIGHT JOIN Employees Emp ON Orders.EmployeeID = Employees.EmployeeID"
            + " ORDER BY Ord.OrderID;";
    public static final String ALIAS_IN_JOINS_9 = "SELECT Ship.ShipperName, COUNT(Ord.OrderID) AS NumberOfOrders"
            + " FROM Orders Ord LEFT JOIN Shippers Ship ON Ord.ShipperID = Ship.ShipperID"
            + " GROUP BY ShipperName;";
    public static final String ALIAS_IN_JOINS_10 = "SELECT Ord.OrderID, Emp.LastName, Emp.FirstName"
            + " FROM Orders Ord RIGHT JOIN Employees Emp ON Orders.EmployeeID = Employees.EmployeeID"
            + " ORDER BY Orders.OrderID;";
    public static final String ALIAS_IN_JOINS_11 = "select e1.ename, e2.ename from emp as e1, emp as e2 where e1." + ALIAS_DUMMY_STRING + ";";
    
    public static final String ALIAS_IN_DELETE_QUERY_1 = "DELETE from emp as e;";
    public static final String ALIAS_IN_DELETE_QUERY_2 = "DELETE from emp as e where e." + ALIAS_DUMMY_STRING + ";";
    public static final String ALIAS_IN_DELETE_QUERY_3 = "DELETE from public.emp as E where e."+ ALIAS_DUMMY_STRING +"    > 100;";
    public static final String ALIAS_IN_DELETE_QUERY_4 = "DELETE from public.emp \"E\" where E." + ALIAS_DUMMY_STRING + ";";
    public static final String ALIAS_IN_DELETE_QUERY_5 = "DELETE FROM films USING producers  WHERE producer_id = producers.id AND producers.name = 'foo';";

    public static final String ALIAS_IN_UPDATE_QUERY_1 = "UPDATE emp as e set e." + ALIAS_DUMMY_STRING + ";";
    public static final String ALIAS_IN_UPDATE_QUERY_2 = "UPDATE public.emp as E set e.bonus=0.1 where e." + ALIAS_DUMMY_STRING + ";";
    public static final String ALIAS_IN_UPDATE_QUERY_3 = "UPDATE public.emp as \"E\" set E.bonus=0.1 where E." + ALIAS_DUMMY_STRING + ";";
    public static final String ALIAS_IN_UPDATE_QUERY_4 = "UPDATE Customers c SET c.ContactName = 'Alfred Schmidt', c.City= 'Frankfurt' WHERE c.CustomerID = 1;";
    public static final String ALIAS_IN_UPDATE_QUERY_5 = "UPDATE public.emp as \"E\" set E.bonus=0.1 where E.dept no in (select deptno from dept);";
    public static final String ALIAS_IN_UPDATE_QUERY_6 = "UPDATE products p"
            + " SET net_price = price - price * discount"
            + " FROM product_segment"
            + " WHERE product.segment_id = product_segment.id;";
    
    public static final String ALIAS_USAGE_MISC = "SELECT * FROM emp e, dept e WHERE e";
    
    public static final String ALIAS_IN_SUBQUERY_1 = "SELECT COUNT(*) FROM suppliers s"
            + " WHERE EXISTS"
            + " ( SELECT 1"
            + " FROM customers c"
            + " WHERE c.customer_id = s.supplier_id AND c.customer_id < 1000 );";
    public static final String ALIAS_IN_SUBQUERY_2 = "SELECT p1.product_name,"
            + " (SELECT MAX(product_id)"
            + " FROM products p2"
            + " WHERE p1.product_id = p2.product_id) subquery2 FROM products p1;";
    
    public static final String ALIAS_IN_UNION = "SELECT p.category_id FROM products p"
            + " UNION"
            + " SELECT c.category_id FROM categories c;";
    public static final String ALIAS_IN_CASE = "SELECT tst.A,"
            + " CASE WHEN tst.A=1 THEN 'one'"
            + " WHEN tst.A=2 THEN 'two'"
            + " ELSE 'other'"
            + " END"
            + " FROM TEST tst;";
    
    
    public static HashMap<String, List<String>> getAliasMapForQuery(String inString)
    {
        HashMap<String, List<String>> aliasMap = new HashMap<String, List<String>>(1);
        LinkedList<String> tableList = new LinkedList<String>();
        switch (inString)
        {
            case ALIAS_IN_PROJECTION_1:
            case ALIAS_IN_PROJECTION_2:
            case ALIAS_IN_PROJECTION_3:
            case ALIAS_IN_PROJECTION_4:
            {
                tableList.add("public.emp");
                aliasMap.put("e", tableList);
                break;
            }
            
            case ALIAS_CASE_CHECK_1:
            case ALIAS_IN_DELETE_QUERY_3:
            case ALIAS_IN_UPDATE_QUERY_2:
            {
                tableList.add("public.emp");
                aliasMap.put("E", tableList);
                break;
            }
            
            case ALIAS_CASE_CHECK_2:
            case ALIAS_CASE_CHECK_3:
            case ALIAS_IN_DELETE_QUERY_4:
            case ALIAS_IN_UPDATE_QUERY_3:
            case ALIAS_IN_UPDATE_QUERY_5:
            {
                tableList.add("public.emp");
                aliasMap.put("\"E\"", tableList);
                break;
            }
            
            case ALIAS_INSIDE_EXPRESSIONS_1:
            case ALIAS_INSIDE_EXPRESSIONS_4:
            case ALIAS_INSIDE_EXPRESSIONS_5:
            case ALIAS_INSIDE_EXPRESSIONS_6:
            case ALIAS_INSIDE_EXPRESSIONS_7:
            case ALIAS_INSIDE_EXPRESSIONS_10:
            case ALIAS_INSIDE_EXPRESSIONS_13:
            case ALIAS_INSIDE_EXPRESSIONS_14:
            case ALIAS_INSIDE_EXPRESSIONS_15:
            case ALIAS_INSIDE_EXPRESSIONS_16:
            case ALIAS_INSIDE_EXPRESSIONS_17:
            case ALIAS_INSIDE_EXPRESSIONS_18:
            case ALIAS_INSIDE_EXPRESSIONS_19:
            case ALIAS_INSIDE_EXPRESSIONS_20:
            case ALIAS_INSIDE_EXPRESSIONS_26:
            case ALIAS_IN_GROUP_BY_1:
            case ALIAS_IN_HAVING_1:
            case ALIAS_IN_HAVING_2:
            case ALIAS_IN_ORDER_BY_1:
            case ALIAS_IN_ORDER_BY_2:
            case ALIAS_IN_ORDER_BY_3:
            case ALIAS_IN_DELETE_QUERY_1:
            case ALIAS_IN_DELETE_QUERY_2:
            case ALIAS_IN_UPDATE_QUERY_1:
            {
                tableList.add("emp");
                aliasMap.put("e", tableList);
                break;
            }
            
            case ALIAS_INSIDE_EXPRESSIONS_2:
            {
                tableList.add("public.customer");
                aliasMap.put("a", tableList);
                break;
            }
            
            case ALIAS_INSIDE_EXPRESSIONS_3:
            {
                tableList.add("public.customer");
                aliasMap.put("\"Hello Wold''s\"", tableList);
                break;
            }
            
            case ALIAS_INSIDE_EXPRESSIONS_8:
            case ALIAS_INSIDE_EXPRESSIONS_9:
            {
                tableList.add("Products");
                aliasMap.put("P", tableList);
                break;
            }
            
            case ALIAS_INSIDE_EXPRESSIONS_11:
            case ALIAS_INSIDE_EXPRESSIONS_21:
            case ALIAS_INSIDE_EXPRESSIONS_22:
            {
                tableList.add("dsuser.\"emp salar y\"");
                aliasMap.put("e", tableList);
                break;
            }
            
            case ALIAS_INSIDE_EXPRESSIONS_12:
            case ALIAS_IN_DELETE_QUERY_5:
            {
                /* NO output */
                break;
            }
            
            case ALIAS_INSIDE_EXPRESSIONS_23:
            case ALIAS_INSIDE_EXPRESSIONS_24:
            case ALIAS_INSIDE_EXPRESSIONS_25:
            {
                tableList.add("dsuser.emp");
                aliasMap.put("e", tableList);
                break;
            }
            
            case ALIAS_TO_CATALOGS:
            {
                tableList.add("pg_description");
                aliasMap.put("pg_desc", tableList);
                break;
            }
            
            case ALIAS_IN_GROUP_BY_2:
            {
                tableList.add("sales");
                aliasMap.put("s", tableList);
                break;
            }
            
            case ALIAS_IN_JOINS_1:
            case ALIAS_IN_JOINS_2:
            {
                tableList.add("COMPANY");
                aliasMap.put("C", tableList);
                LinkedList<String> tableList1 = new LinkedList<String>();
                tableList1.add("DEPARTMENT");
                aliasMap.put("D", tableList1);
                break;
            }
            
            case ALIAS_IN_JOINS_3:
            case ALIAS_IN_UPDATE_QUERY_6:
            {
                tableList.add("products");
                aliasMap.put("p", tableList);
                break;
            }
            
            case ALIAS_IN_JOINS_4:
            {
                tableList.add("employee");
                aliasMap.put("a", tableList);
                LinkedList<String> tableList1 = new LinkedList<String>();
                tableList1.add("employee");
                aliasMap.put("b", tableList1);
                break;
            }
            
            case ALIAS_IN_JOINS_5:
            {
                tableList.add("Customers");
                aliasMap.put("A", tableList);
                LinkedList<String> tableList1 = new LinkedList<String>();
                tableList1.add("Customers");
                aliasMap.put("B", tableList1);
                break;
            }
            
            case ALIAS_IN_JOINS_6:
            case ALIAS_IN_JOINS_7:
            {
                tableList.add("Orders");
                aliasMap.put("Ord", tableList);
                LinkedList<String> tableList1 = new LinkedList<String>();
                tableList1.add("Customers");
                aliasMap.put("Cust", tableList1);
                break;
            }
            
            case ALIAS_IN_JOINS_8:
            case ALIAS_IN_JOINS_10:
            {
                tableList.add("Orders");
                aliasMap.put("Ord", tableList);
                LinkedList<String> tableList1 = new LinkedList<String>();
                tableList1.add("Employees");
                aliasMap.put("Emp", tableList1);
                break;
            }
            
            case ALIAS_IN_JOINS_9:
            {
                tableList.add("Orders");
                aliasMap.put("Ord", tableList);
                LinkedList<String> tableList1 = new LinkedList<String>();
                tableList1.add("Shippers");
                aliasMap.put("Ship", tableList1);
                break;
            }
            
            case ALIAS_IN_JOINS_11:
            {
                tableList.add("emp");
                aliasMap.put("e1", tableList);
                LinkedList<String> tableList1 = new LinkedList<String>();
                tableList1.add("emp");
                aliasMap.put("e2", tableList1);
                break;
            }
            
            case ALIAS_IN_UPDATE_QUERY_4:
            {
                tableList.add("Customers");
                aliasMap.put("c", tableList);
                break;
            }
            
            case ALIAS_USAGE_MISC:
            {
                tableList.add("emp");
                tableList.add("dept");
                aliasMap.put("e", tableList);
                break;
            }
            
            case ALIAS_IN_SUBQUERY_1:
            {
                tableList.add("suppliers");
                aliasMap.put("s", tableList);
                LinkedList<String> tableList1 = new LinkedList<String>();
                tableList1.add("customers");
                aliasMap.put("c", tableList1);
                break;
            }
            
            case ALIAS_IN_SUBQUERY_2:
            {
                tableList.add("products");
                aliasMap.put("p2", tableList);
                LinkedList<String> tableList1 = new LinkedList<String>();
                tableList1.add("products");
                aliasMap.put("p1", tableList1);
                break;
            }
            
            case ALIAS_IN_UNION:
            {
                tableList.add("products");
                aliasMap.put("p", tableList);
                LinkedList<String> tableList1 = new LinkedList<String>();
                tableList1.add("categories");
                aliasMap.put("c", tableList1);
                break;
            }
            
            case ALIAS_IN_CASE:
            {
                tableList.add("TEST");
                aliasMap.put("tst", tableList);
                break;
            }
        }
        return aliasMap;
    }
}
