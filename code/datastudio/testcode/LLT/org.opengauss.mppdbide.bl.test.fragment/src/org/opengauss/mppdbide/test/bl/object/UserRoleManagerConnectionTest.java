package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Date;

import org.junit.Test;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.UserRole;
import org.opengauss.mppdbide.bl.serverdatacache.UserRoleManager;
import org.opengauss.mppdbide.mock.bl.MockUserRoleManagerConnection;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

public class UserRoleManagerConnectionTest extends BLTestAdapter
{
    @Test
    public void test_getDDL_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();

            UserRole userRole = new UserRole(OBJECTTYPE.USER_ROLE, Long.valueOf(12345), "AA", false, false, false,
                    false, false, false, false, 0, null, null, null, server);
            MockUserRoleManagerConnection.test_getDDL_001_RS(preparedstatementHandler);
            assertEquals(
                    ("CREATE USER \"AA\" SYSADMIN AUDITADMIN CREATEDB CREATEROLE INHERIT LOGIN REPLICATION CONNECTION LIMIT 3 VALID BEGIN \'1970-01-01\' VALID UNTIL \'1970-01-01\' RESOURCE POOL \'default_respool\' ROLE tom ADMIN tom PASSWORD '********';"
                            + MPPDBIDEConstants.LINE_SEPARATOR + "COMMENT ON ROLE \"AA\" IS 'comment';"),
                    UserRoleManager.getDDL(dbconn, userRole));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_formCreateQuery_001()
    {
        try
        {

            UserRole userRole = new UserRole();
            userRole.setName("Martin");
            userRole.setRole(true);
            userRole.setUser(false);
            userRole.setRolSystemAdmin(true);
            userRole.setAuditAdmin(true);
            userRole.setRolInherit(true);
            userRole.setRolCreateDb(true);
            userRole.setRolCreateRole(true);
            userRole.setRolCanLogin(true);
            userRole.setRolReplication(true);
            userRole.setRolConnLimit(3);
            userRole.setRolValidBegin(new Date(0));
            userRole.setRolValidUntil(new Date(0));
            userRole.setRolResPool("default_respool");
            userRole.setRoleCombo("tom");
            userRole.setAdminComo("tom");
            userRole.setComment("comment");
            userRole.setPasswordInput("123456".toCharArray());
            System.out.println("\t21435453534");
            assertEquals(
                    ("CREATE ROLE \"Martin\" WITH" + MPPDBIDEConstants.LINE_SEPARATOR + "\t" + "SYSADMIN "
                            + MPPDBIDEConstants.LINE_SEPARATOR + "\t" + "AUDITADMIN " + MPPDBIDEConstants.LINE_SEPARATOR
                            + "\t" + "CREATEDB " + MPPDBIDEConstants.LINE_SEPARATOR + "\t" + "CREATEROLE "
                            + MPPDBIDEConstants.LINE_SEPARATOR + "\t" + "INHERIT " + MPPDBIDEConstants.LINE_SEPARATOR
                            + "\t" + "LOGIN " + MPPDBIDEConstants.LINE_SEPARATOR + "\t" + "REPLICATION "
                            + MPPDBIDEConstants.LINE_SEPARATOR + "\t" + "CONNECTION LIMIT 3 "
                            + MPPDBIDEConstants.LINE_SEPARATOR + "\t" + "RESOURCE POOL \'default_respool\' "
                            + MPPDBIDEConstants.LINE_SEPARATOR + "\t" + "ROLE tom " + MPPDBIDEConstants.LINE_SEPARATOR
                            + "\t" + "ADMIN tom " + MPPDBIDEConstants.LINE_SEPARATOR + "\t" + "PASSWORD \'123456\';"),
                    UserRoleManager.formCreateQuery(userRole));
            UserRoleManager.formSetCommentQuery(userRole);
            UserRoleManager.execCreate(dbconn, userRole);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_formSetCommentQuery_001()
    {
        try
        {

            UserRole userRole = new UserRole();
            userRole.setName("Martin");
            userRole.setComment("comment");
            assertEquals(("COMMENT ON ROLE \"Martin\" IS \'comment\';"), UserRoleManager.formSetCommentQuery(userRole));
            assertEquals((MPPDBIDEConstants.LINE_SEPARATOR + "COMMENT ON ROLE \"Martin\" IS \'comment\';"),
                    UserRoleManager.formRoleCommentQuery(userRole));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_clear_001()
    {
        try
        {

            UserRole userRole = new UserRole();
            userRole.setName("Martin");
            userRole.setPasswordInput("123456".toCharArray());
            UserRoleManager.clear(userRole.getPasswordInput());
            userRole.clearPwd();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_execDrop_001()
    {
        try
        {

            UserRole userRole = new UserRole();
            userRole.setRolCanLogin(true);
            userRole.setName("Martin");
            UserRoleManager.execDrop(dbconn, userRole);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
