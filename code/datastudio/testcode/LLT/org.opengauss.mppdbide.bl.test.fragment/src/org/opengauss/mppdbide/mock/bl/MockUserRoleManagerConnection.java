package org.opengauss.mppdbide.mock.bl;

import java.sql.Date;
import java.text.ParseException;

import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

public class MockUserRoleManagerConnection
{
    public static void test_getDDL_001_RS(PreparedStatementResultSetHandler preparedstatementHandler)
            throws ParseException
    {
        String query = "SELECT rolsystemadmin,rolauditadmin,rolcreatedb,rolcreaterole,rolinherit,rolcanlogin"
                + ",rolreplication,rolconnlimit,rolvalidbegin,rolvaliduntil,rolrespool from pg_roles WHERE oid = "
                + 12345 + ";";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolsystemadmin");
        rs.addColumn("rolauditadmin");
        rs.addColumn("rolcreatedb");
        rs.addColumn("rolcreaterole");
        rs.addColumn("rolinherit");
        rs.addColumn("rolcanlogin");
        rs.addColumn("rolreplication");
        rs.addColumn("rolconnlimit");
        rs.addColumn("rolvalidbegin");
        rs.addColumn("rolvaliduntil");
        rs.addColumn("rolrespool");
        Date startDate = new Date(0);
        Date endDate = new Date(0);
        rs.addRow(new Object[] {true, true, true, true, true, true, true, 3, startDate, endDate, "default_respool"});
        preparedstatementHandler.prepareResultSet(query, rs);

        String query2 = "SELECT r.rolname rolname,m.admin_option admin_option "
                + "FROM pg_auth_members m, pg_roles r WHERE r.oid = m.member AND m.roleid = " + 12345 + ";";
        MockResultSet rs2 = preparedstatementHandler.createResultSet();
        rs2.addColumn("rolname");
        rs2.addColumn("admin_option");
        rs2.addRow(new Object[] {"tom", true});
        preparedstatementHandler.prepareResultSet(query2, rs2);

        String query3 = "SELECT description " + "FROM PG_SHDESCRIPTION WHERE objoid = " + 12345 + ";";
        MockResultSet rs3 = preparedstatementHandler.createResultSet();
        rs3.addColumn("description");
        rs3.addRow(new Object[] {"comment"});
        preparedstatementHandler.prepareResultSet(query3, rs3);
    }

    public static void test_getDDL_002_RS(PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "SELECT r.rolname rolname,m.admin_option admin_option "
                + "FROM pg_auth_members m, pg_roles r WHERE r.oid = m.member AND m.roleid" + 12345 + ";";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addColumn("admin_option");
        rs.addRow(new Object[] {"tom", true});
        preparedstatementHandler.prepareResultSet(query, rs);
    }
}
