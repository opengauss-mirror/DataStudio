package org.opengauss.mppdbide.mock.bl;

import java.sql.Date;

import org.opengauss.mppdbide.bl.serverdatacache.UserRole;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

public class MockUserRoleManagerUtils
{
    public static void test_isSysAdmin_001_RS(PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "SELECT rolsystemadmin FROM pg_catalog.pg_roles WHERE rolname = user;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolsystemadmin");
        rs.addRow(new Object[] {true});
        preparedstatementHandler.prepareResultSet(query, rs);
    }
    
    public static void test_isSysAdmin_002_RS(PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "SELECT rolsystemadmin FROM pg_catalog.pg_roles WHERE rolname = user;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolsystemadmin");
        rs.addRow(new Object[] {false});
        preparedstatementHandler.prepareResultSet(query, rs);
    }
    
    public static void test_isSysAdmin_003_RS(PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "SELECT rolsystemadmin FROM pg_catalog.pg_roles WHERE rolname = user;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolsystemadmin");
        preparedstatementHandler.prepareResultSet(query, rs);
    }

    public static void test_fetchAllUserRole_001_RS(PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "SELECT rolname,rolcanlogin,oid FROM pg_catalog.pg_roles;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addColumn("rolcanlogin");
        rs.addColumn("oid");
        rs.addRow(new Object[] {"Chris", true, 001});
        rs.addRow(new Object[] {"Martin", false, 002});
        preparedstatementHandler.prepareResultSet(query, rs);
    }
    
    public static void test_fetchAllUserRole_002_RS(PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "SELECT rolname,rolcanlogin,oid FROM pg_catalog.pg_roles;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addColumn("rolcanlogin");
        rs.addColumn("oid");
        preparedstatementHandler.prepareResultSet(query, rs);
    }

    public static void
            test_fetchAllUserRoleWithOutSuperUser_001_RS(PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "SELECT rolname,rolcanlogin,oid FROM pg_catalog.pg_roles WHERE rolsuper = false;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addColumn("rolcanlogin");
        rs.addColumn("oid");
        rs.addRow(new Object[] {"Chris", true, 001});
        rs.addRow(new Object[] {"Martin", false, 002});
        preparedstatementHandler.prepareResultSet(query, rs);
    }

    public static void
            test_fetchAllUserRoleWithOutSuperUser_002_RS(PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "SELECT rolname,rolcanlogin,oid FROM pg_catalog.pg_roles WHERE rolsuper = false;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addColumn("rolcanlogin");
        rs.addColumn("oid");
        preparedstatementHandler.prepareResultSet(query, rs);
    }

    public static void test_fetchUserRoleDetailInfoByOid_001_RS(
            PreparedStatementResultSetHandler preparedstatementHandler, UserRole userRole)
    {
        String query = "SELECT rolname,rolinherit,rolcreaterole,rolcreatedb,rolcanlogin,rolreplication"
                + ",rolauditadmin,rolsystemadmin,rolconnlimit,rolvalidbegin,rolvaliduntil,oid,rolrespool"
                + " FROM pg_catalog.pg_roles WHERE oid = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addColumn("rolinherit");
        rs.addColumn("rolcreaterole");
        rs.addColumn("rolcreatedb");
        rs.addColumn("rolcanlogin");
        rs.addColumn("rolreplication");
        rs.addColumn("rolauditadmin");
        rs.addColumn("rolsystemadmin");
        rs.addColumn("rolconnlimit");
        rs.addColumn("rolvalidbegin");
        rs.addColumn("rolvaliduntil");
        rs.addColumn("oid");
        rs.addColumn("rolrespool");
        rs.addRow(new Object[] {"Chris", true, true, true, true, true, true, true, -1, new Date(0), new Date(0),
            userRole.getOid(), "default_pool"});
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});
    }

    public static void test_fetchUserRoleDetailInfoByOid_002_RS(
            PreparedStatementResultSetHandler preparedstatementHandler, UserRole userRole)
    {
        String query = "SELECT rolname,rolinherit,rolcreaterole,rolcreatedb,rolcanlogin,rolreplication"
                + ",rolauditadmin,rolsystemadmin,rolconnlimit,rolvalidbegin,rolvaliduntil,oid,rolrespool"
                + " FROM pg_catalog.pg_roles WHERE oid = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addColumn("rolinherit");
        rs.addColumn("rolcreaterole");
        rs.addColumn("rolcreatedb");
        rs.addColumn("rolcanlogin");
        rs.addColumn("rolreplication");
        rs.addColumn("rolauditadmin");
        rs.addColumn("rolsystemadmin");
        rs.addColumn("rolconnlimit");
        rs.addColumn("rolvalidbegin");
        rs.addColumn("rolvaliduntil");
        rs.addColumn("oid");
        rs.addColumn("rolrespool");
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});
    }

    public static void test_fetchUserRoleSimpleInfoByOid_001_RS(
            PreparedStatementResultSetHandler preparedstatementHandler, UserRole userRole)
    {
        String query = "SELECT rolname,rolcanlogin,oid FROM pg_catalog.pg_roles WHERE oid = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addColumn("rolcanlogin");
        rs.addColumn("oid");
        rs.addRow(new Object[] {"Chris", true, userRole.getOid()});
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});
    }

    public static void test_fetchUserRoleSimpleInfoByOid_002_RS(
            PreparedStatementResultSetHandler preparedstatementHandler, UserRole userRole)
    {
        String query = "SELECT rolname,rolcanlogin,oid FROM pg_catalog.pg_roles WHERE oid = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addColumn("rolcanlogin");
        rs.addColumn("oid");
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});
    }

    public static void test_fetchAllParent_001_RS(PreparedStatementResultSetHandler preparedstatementHandler,
            UserRole userRole)
    {
        String query = "SELECT r.oid, r.rolname FROM pg_catalog.pg_roles r, pg_catalog.pg_auth_members m"
                + " WHERE r.oid = m.roleid AND member = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("oid");
        rs.addColumn("rolname");
        rs.addRow(new Object[] {102L, "Martin"});
        rs.addRow(new Object[] {103L, "Arun"});
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});
    }

    public static void test_fetchAllParent_002_RS(PreparedStatementResultSetHandler preparedstatementHandler,
            UserRole userRole)
    {
        String query = "SELECT r.oid, r.rolname FROM pg_catalog.pg_roles r, pg_catalog.pg_auth_members m"
                + " WHERE r.oid = m.roleid AND member = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("oid");
        rs.addColumn("rolname");
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});
    }

    public static void test_fetchAllMember_001_RS(PreparedStatementResultSetHandler preparedstatementHandler,
            UserRole userRole)
    {
        String query = "SELECT r.oid, r.rolname FROM pg_catalog.pg_roles r, pg_catalog.pg_auth_members m"
                + " WHERE r.oid = m.member AND roleid = ?::oid;";
        ;
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("oid");
        rs.addColumn("rolname");
        rs.addRow(new Object[] {102L, "Martin"});
        rs.addRow(new Object[] {103L, "Arun"});
        rs.addRow(new Object[] {104L, "Mark"});
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});
    }

    public static void test_fetchAllMember_002_RS(PreparedStatementResultSetHandler preparedstatementHandler,
            UserRole userRole)
    {
        String query = "SELECT r.oid, r.rolname FROM pg_catalog.pg_roles r, pg_catalog.pg_auth_members m"
                + " WHERE r.oid = m.member AND roleid = ?::oid;";
        ;
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("oid");
        rs.addColumn("rolname");
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});
    }

    public static void test_generatePropertyChangePreviewSQL_001_RS01(
            PreparedStatementResultSetHandler preparedstatementHandler, UserRole userRole)
    {
        String query = "SELECT r.rolname FROM pg_catalog.pg_roles r, pg_catalog.pg_auth_members m"
                + " WHERE r.oid = m.roleid AND m.member = ?::oid;";
        ;
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addRow(new Object[] {"Martin"});
        rs.addRow(new Object[] {"Arun"});
        rs.addRow(new Object[] {"Tom"});
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});
    }

    public static void test_generatePropertyChangePreviewSQL_001_RS02(
            PreparedStatementResultSetHandler preparedstatementHandler, UserRole userRole)
    {
        String query = "SELECT rolname FROM pg_catalog.pg_roles WHERE oid = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addRow(new Object[] {"chris"});
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});
    }

    public static void test_fetchDescriptionOfUserRole_001_RS(
            PreparedStatementResultSetHandler preparedstatementHandler, UserRole userRole)
    {
        String query = "SELECT pg_catalog.shobj_description(?::oid, 'pg_authid') description;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("description");
        rs.addRow(new Object[] {"Winner"});
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});
    }

    public static void test_fetchDescriptionOfUserRole_002_RS(
            PreparedStatementResultSetHandler preparedstatementHandler, UserRole userRole)
    {
        String query = "SELECT pg_catalog.shobj_description(?::oid, 'pg_authid') description;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("description");
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});
    }

    public static void test_fetchResourcePool_001_RS(PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "SELECT respool_name FROM pg_resource_pool;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("respool_name");
        rs.addRow(new Object[] {"default_pool"});
        preparedstatementHandler.prepareResultSet(query, rs);
    }

    public static void test_fetchResourcePool_002_RS(PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "SELECT respool_name FROM pg_resource_pool;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("respool_name");
        preparedstatementHandler.prepareResultSet(query, rs);
    }

    public static void test_isUserRoleExist_001_RS(PreparedStatementResultSetHandler preparedstatementHandler,
            UserRole userRole)
    {
        String query = "SELECT rolname FROM pg_catalog.pg_roles WHERE oid = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addRow(new Object[] {"chris"});
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});

    }

    public static void test_isUserRoleExist_002_RS(PreparedStatementResultSetHandler preparedstatementHandler,
            UserRole userRole)
    {
        String query = "SELECT rolname FROM pg_catalog.pg_roles WHERE oid = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});

    }

    public static void test_fetchLockStatusOfUserRole_001_RS(PreparedStatementResultSetHandler preparedstatementHandler,
            UserRole userRole)
    {
        String query = "SELECT rolstatus FROM pg_catalog.pg_user_status WHERE roloid = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolstatus");
        rs.addRow(new Object[] {"2"});
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});

    }

    public static void test_fetchLockStatusOfUserRole_002_RS(PreparedStatementResultSetHandler preparedstatementHandler,
            UserRole userRole)
    {
        String query = "SELECT rolstatus FROM pg_catalog.pg_user_status WHERE roloid = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolstatus");
        rs.addRow(new Object[] {"0"});
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});

    }

    public static void test_fetchLockStatusOfUserRole_003_RS(PreparedStatementResultSetHandler preparedstatementHandler,
            UserRole userRole)
    {
        String query = "SELECT rolstatus FROM pg_catalog.pg_user_status WHERE roloid = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolstatus");
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(userRole.getOid())});

    }
    
    public static void test_getUserRoleNameByOid_001_RS(PreparedStatementResultSetHandler preparedstatementHandler,
            long oid)
    {
        String query = "SELECT rolname FROM pg_catalog.pg_roles WHERE oid = ?::oid;";
        MockResultSet rs = preparedstatementHandler.createResultSet();
        rs.addColumn("rolname");
        rs.addRow(new Object[] {"chris"});
        preparedstatementHandler.prepareResultSet(query, rs, new Object[] {String.valueOf(oid)});
    }

}