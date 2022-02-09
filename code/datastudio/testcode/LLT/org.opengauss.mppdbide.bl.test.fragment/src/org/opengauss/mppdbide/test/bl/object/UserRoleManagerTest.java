package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.UserRole;
import org.opengauss.mppdbide.bl.serverdatacache.UserRoleManager;
import org.opengauss.mppdbide.mock.bl.MockUserRoleManagerUtils;
import org.opengauss.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

public class UserRoleManagerTest extends BLTestAdapter
{
    @Test
    public void test_isSysAdmin_001()
    {
        try
        {
            MockUserRoleManagerUtils.test_isSysAdmin_001_RS(preparedstatementHandler);
            assertEquals(true, UserRoleManager.isSysAdmin(dbconn));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_isSysAdmin_002()
    {
        try
        {
            MockUserRoleManagerUtils.test_isSysAdmin_002_RS(preparedstatementHandler);
            assertEquals(false, UserRoleManager.isSysAdmin(dbconn));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_isSysAdmin_003()
    {
        try
        {
            MockUserRoleManagerUtils.test_isSysAdmin_003_RS(preparedstatementHandler);
            assertEquals(false, UserRoleManager.isSysAdmin(dbconn));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchAllUserRole_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();

            MockUserRoleManagerUtils.test_fetchAllUserRole_001_RS(preparedstatementHandler);
            assertEquals(2, UserRoleManager.fetchAllUserRole(server, dbconn).size());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchAllUserRole_002()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();

            MockUserRoleManagerUtils.test_fetchAllUserRole_002_RS(preparedstatementHandler);
            assertTrue(UserRoleManager.fetchAllUserRole(server, dbconn).isEmpty());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchAllUserRoleWithOutSuperUser_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();

            MockUserRoleManagerUtils.test_fetchAllUserRoleWithOutSuperUser_001_RS(preparedstatementHandler);
            assertEquals(2, UserRoleManager.fetchAllUserRoleWithOutSuperUser(server, dbconn).size());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchAllUserRoleWithOutSuperUser_002()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();

            MockUserRoleManagerUtils.test_fetchAllUserRoleWithOutSuperUser_002_RS(preparedstatementHandler);
            assertTrue(UserRoleManager.fetchAllUserRoleWithOutSuperUser(server, dbconn).isEmpty());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchUserRoleDetailInfoByOid_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();

            UserRole userRole = new UserRole();
            userRole.setOid(101L);

            MockUserRoleManagerUtils.test_fetchUserRoleDetailInfoByOid_001_RS(preparedstatementHandler, userRole);
            assertEquals("Chris", UserRoleManager.fetchUserRoleDetailInfoByOid(server, dbconn, userRole).getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchUserRoleDetailInfoByOid_002()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();

            UserRole userRole = new UserRole();
            userRole.setOid(101L);

            MockUserRoleManagerUtils.test_fetchUserRoleDetailInfoByOid_002_RS(preparedstatementHandler, userRole);
            try
            {
                UserRoleManager.fetchUserRoleDetailInfoByOid(server, dbconn, userRole);
            }
            catch (MPPDBIDEException e)
            {
                assertEquals(MessageConfigLoader.getProperty(IMessagesConstants.ERR_USER_ROLE_IS_NOT_EXIST,
                        String.valueOf(userRole.getOid())), e.getServerMessage());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchUserRoleSimpleInfoByOid_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();

            UserRole userRole = new UserRole();
            userRole.setOid(101L);

            MockUserRoleManagerUtils.test_fetchUserRoleSimpleInfoByOid_001_RS(preparedstatementHandler, userRole);
            assertEquals("Chris", UserRoleManager.fetchUserRoleSimpleInfoByOid(server, dbconn, userRole).getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchUserRoleSimpleInfoByOid_002()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();

            UserRole userRole = new UserRole();
            userRole.setOid(101L);

            MockUserRoleManagerUtils.test_fetchUserRoleSimpleInfoByOid_002_RS(preparedstatementHandler, userRole);

            try
            {
                UserRoleManager.fetchUserRoleSimpleInfoByOid(server, dbconn, userRole);
            }
            catch (MPPDBIDEException e)
            {
                assertEquals(MessageConfigLoader.getProperty(IMessagesConstants.ERR_USER_ROLE_IS_NOT_EXIST,
                        String.valueOf(userRole.getOid())), e.getServerMessage());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchAllParent_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();

            UserRole userRole = new UserRole();
            userRole.setOid(101L);

            MockUserRoleManagerUtils.test_fetchAllParent_001_RS(preparedstatementHandler, userRole);
            assertEquals(2, UserRoleManager.fetchAllParent(server, dbconn, userRole).size());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchAllParent_002()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();

            UserRole userRole = new UserRole();
            userRole.setOid(101L);

            MockUserRoleManagerUtils.test_fetchAllParent_002_RS(preparedstatementHandler, userRole);
            assertTrue(UserRoleManager.fetchAllParent(server, dbconn, userRole).isEmpty());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_alterUserRole_001()
    {
        try
        {
            String query = "ALTER ROLE chris NOLOGIN;";

            UserRoleManager.alterUserRole(dbconn, Arrays.asList(query));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_generatePropertyChangePreviewSQL_001()
    {
        try
        {
            UserRole userRole = new UserRole();
            userRole.setIsLock(false);
            userRole.setRolCanLogin(false);
            userRole.setIsUser(false);
            userRole.setRolSystemAdmin(true);
            userRole.setComment("Wel'come");
            userRole.setOid(101L);
            userRole.setName("cherry");

            UserRole parent1 = new UserRole();
            parent1.setOid(100L);
            parent1.setName("parent1");
            UserRole parent2 = new UserRole();
            parent2.setOid(99L);
            parent2.setName("Parent2");
            UserRole parent3 = new UserRole();
            parent3.setOid(99L);
            parent3.setName("Tom");
            userRole.setParents(Arrays.asList(parent1, parent2, parent3));

            MockUserRoleManagerUtils.test_generatePropertyChangePreviewSQL_001_RS01(preparedstatementHandler, userRole);
            MockUserRoleManagerUtils.test_generatePropertyChangePreviewSQL_001_RS02(preparedstatementHandler, userRole);

            List<String> querys = new ArrayList<>();
            querys.add("ALTER ROLE chris ACCOUNT UNLOCK;");
            querys.add("ALTER ROLE chris NOLOGIN SYSADMIN ;");
            querys.add("COMMENT ON ROLE chris IS 'Wel''come';");
            querys.add("REVOKE \"Martin\" FROM chris;");
            querys.add("REVOKE \"Arun\" FROM chris;");
            querys.add("GRANT parent1 TO chris;");
            querys.add("GRANT \"Parent2\" TO chris;");
            querys.add("ALTER ROLE chris RENAME TO cherry;");

            List<String> previewSQLs = UserRoleManager.generatePropertyChangePreviewSQL(dbconn, userRole, "chris");

            assertTrue(querys.size() == previewSQLs.size());
            for (String sql : previewSQLs)
            {
                assertTrue(querys.contains(sql));
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_generatePropertyChangePreviewSQL_login_001()
    {
        try
        {
            UserRole userRole = new UserRole();
            userRole.setIsLock(false);
            userRole.setRolCanLogin(true);
            userRole.setIsUser(true);
            userRole.setRolSystemAdmin(true);
            userRole.setComment("Wel'come");
            userRole.setOid(101L);
            userRole.setName("cherry");

            UserRole parent1 = new UserRole();
            parent1.setOid(100L);
            parent1.setName("parent1");
            UserRole parent2 = new UserRole();
            parent2.setOid(99L);
            parent2.setName("Parent2");
            UserRole parent3 = new UserRole();
            parent3.setOid(99L);
            parent3.setName("Tom");
            userRole.setParents(Arrays.asList(parent1, parent2, parent3));

            MockUserRoleManagerUtils.test_generatePropertyChangePreviewSQL_001_RS01(preparedstatementHandler, userRole);
            MockUserRoleManagerUtils.test_generatePropertyChangePreviewSQL_001_RS02(preparedstatementHandler, userRole);

            List<String> querys = new ArrayList<>();
            querys.add("ALTER USER chris ACCOUNT UNLOCK;");
            querys.add("ALTER USER chris LOGIN SYSADMIN ;");
            querys.add("COMMENT ON USER chris IS 'Wel''come';");
            querys.add("REVOKE \"Martin\" FROM chris;");
            querys.add("REVOKE \"Arun\" FROM chris;");
            querys.add("GRANT parent1 TO chris;");
            querys.add("GRANT \"Parent2\" TO chris;");
            querys.add("ALTER USER chris RENAME TO cherry;");

            List<String> previewSQLs = UserRoleManager.generatePropertyChangePreviewSQL(dbconn, userRole, "chris");

            assertTrue(querys.size() == previewSQLs.size());
            for (String sql : previewSQLs)
            {
                assertTrue(querys.contains(sql));
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_generatePropertyChangePreviewSQL_002()
    {
        try
        {
            UserRole userRole = new UserRole();
            userRole.setIsLock(false);
            userRole.setRolCanLogin(true);
            userRole.setIsUser(true);
            userRole.setRolSystemAdmin(true);
            userRole.setComment("Welcome");
            userRole.setOid(101L);
            userRole.setName("chris");
            userRole.setRolAuditAdmin(false);
            userRole.setRolCreateDb(true);
            userRole.setRolCreateRole(true);
            userRole.setRolValidBegin(new Date());
            userRole.setRolValidUntil(new Date());
            userRole.setRolConnLimit(-1);
            userRole.setRolInherit(false);
            userRole.setRolReplication(true);
            userRole.setRolResPool("default_pool");

            UserRole parent1 = new UserRole();
            parent1.setOid(100L);
            parent1.setName("parent1");
            UserRole parent2 = new UserRole();
            parent2.setOid(99L);
            parent2.setName("Parent2");
            userRole.setParents(Arrays.asList(parent1, parent2));

            MockUserRoleManagerUtils.test_generatePropertyChangePreviewSQL_001_RS01(preparedstatementHandler, userRole);
            MockUserRoleManagerUtils.test_generatePropertyChangePreviewSQL_001_RS02(preparedstatementHandler, userRole);

            List<String> previewSQLs = UserRoleManager.generatePropertyChangePreviewSQL(dbconn, userRole, "chris");

            assertFalse(previewSQLs.isEmpty());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchDescriptionOfUserRole_001()
    {
        try
        {
            UserRole userRole = new UserRole();
            userRole.setOid(100L);

            MockUserRoleManagerUtils.test_fetchDescriptionOfUserRole_001_RS(preparedstatementHandler, userRole);
            assertEquals("Winner", UserRoleManager.fetchDescriptionOfUserRole(dbconn, userRole));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchDescriptionOfUserRole_002()
    {
        try
        {
            UserRole userRole = new UserRole();
            userRole.setOid(100L);

            MockUserRoleManagerUtils.test_fetchDescriptionOfUserRole_002_RS(preparedstatementHandler, userRole);
            assertNull(UserRoleManager.fetchDescriptionOfUserRole(dbconn, userRole));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchResourcePool_001()
    {
        try
        {
            MockUserRoleManagerUtils.test_fetchResourcePool_001_RS(preparedstatementHandler);
            List<String> resourcePools = UserRoleManager.fetchResourcePool(dbconn);
            assertTrue(resourcePools.contains("default_pool"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchResourcePool_002()
    {
        try
        {
            MockUserRoleManagerUtils.test_fetchResourcePool_002_RS(preparedstatementHandler);
            List<String> resourcePools = UserRoleManager.fetchResourcePool(dbconn);
            assertTrue(resourcePools.isEmpty());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchLockStatusOfUserRole_001()
    {
        try
        {
            UserRole userRole = new UserRole();
            userRole.setOid(101L);
            MockUserRoleManagerUtils.test_fetchLockStatusOfUserRole_001_RS(preparedstatementHandler, userRole);
            assertTrue(UserRoleManager.fetchLockStatusOfUserRole(dbconn, userRole));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchLockStatusOfUserRole_002()
    {
        try
        {
            UserRole userRole = new UserRole();
            userRole.setOid(101L);
            MockUserRoleManagerUtils.test_fetchLockStatusOfUserRole_002_RS(preparedstatementHandler, userRole);
            assertFalse(UserRoleManager.fetchLockStatusOfUserRole(dbconn, userRole));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_fetchLockStatusOfUserRole_003()
    {
        try
        {
            UserRole userRole = new UserRole();
            userRole.setOid(101L);
            MockUserRoleManagerUtils.test_fetchLockStatusOfUserRole_003_RS(preparedstatementHandler, userRole);
            try
            {
                UserRoleManager.fetchLockStatusOfUserRole(dbconn, userRole);
            }
            catch (MPPDBIDEException e)
            {
                assertEquals(MessageConfigLoader.getProperty(IMessagesConstants.ERR_FETCH_USER_ROLE_LOCK_STATUS,
                        userRole.getOid()), e.getServerMessage());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_copyProperties_001()
    {
        try
        {
            UserRole sourceUserRole = new UserRole();
            sourceUserRole.setOid(101L);
            sourceUserRole.setName("chris");
            sourceUserRole.setIsLock(false);
            sourceUserRole.setRolValidBegin(new Date());

            UserRole parent1 = new UserRole();
            parent1.setOid(100L);
            parent1.setName("parent1");
            UserRole parent2 = new UserRole();
            parent2.setOid(99L);
            parent2.setName("Parent2");
            sourceUserRole.setParents(Arrays.asList(parent1, parent2));

            UserRole targetUserRole = new UserRole();

            UserRoleManager.copyProperties(sourceUserRole, targetUserRole);

            assertEquals(sourceUserRole.getOid(), targetUserRole.getOid());
            assertEquals(sourceUserRole.getName(), targetUserRole.getName());
            assertEquals(sourceUserRole.getIsLock(), targetUserRole.getIsLock());
            assertEquals(sourceUserRole.getRolValidBegin(), targetUserRole.getRolValidBegin());
            assertSame(sourceUserRole.getParents(), targetUserRole.getParents());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_userRoleObjectGroup_001()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        assertSame(server, server.getUserRoleObjectGroup().getServer());
    }
    @Test
    public void test_getAllProperties_001()
	{
		MockUserRoleManagerUtils.test_isSysAdmin_001_RS(preparedstatementHandler);

		Database database = connProfCache.getDbForProfileId(profileId);
		Server server = database.getServer();
		UserRole userRole = new UserRole();
		userRole.setName("Chris");
		userRole.setServer(server);
		userRole.setOid(101L);
		MockUserRoleManagerUtils.test_fetchUserRoleDetailInfoByOid_001_RS(preparedstatementHandler, userRole);
		MockUserRoleManagerUtils.test_fetchAllParent_001_RS(preparedstatementHandler, userRole);
		MockUserRoleManagerUtils.test_fetchDescriptionOfUserRole_001_RS(preparedstatementHandler, userRole);
		MockUserRoleManagerUtils.test_fetchLockStatusOfUserRole_001_RS(preparedstatementHandler, userRole);
		PropertyHandlerCore core = new PropertyHandlerCore(userRole);
		try {
			core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
			assertNotNull(core.getproperty());
		} catch (MPPDBIDEException | SQLException e) {
			fail("not expected");
		}
		
		UserRole userRole1 = new UserRole(server);
		userRole1.getParent();
		assertNotNull(userRole1.getDatabase());
	}
    
    @Test
    public void test_getUserRoleNameByOid_001()
    {
        try
        {
            MockUserRoleManagerUtils.test_getUserRoleNameByOid_001_RS(preparedstatementHandler, 100L);
            assertEquals("chris", UserRoleManager.getUserRoleNameByOid(dbconn, 100L));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    
    @Test
    public void test_getUserRoleNameByOid_0011()
    {
        try
        {
            UserRole userRole = new UserRole();
            userRole.setName("Chris");
            userRole.setOid(101L);
            userRole.setAll(true);
            userRole.setClear(true);
            userRole.setSpinnerPreSize(23);
            userRole.setCmbResoucePoolName("Test");
            userRole.setUserGroupName("Test123");
            userRole.setBeginTime("1:33");
            userRole.setUntilTime("3:45");
            userRole.setRolCanLogin(true);
            assertTrue(userRole.getAll());
            assertTrue(userRole.getClear());
            assertEquals(23,userRole.getSpinnerPreSize());
            assertEquals("Test",userRole.getCmbResoucePoolName());
            assertEquals("Test123",userRole.getUserGroupName());
            assertEquals("DROP USER IF EXISTS \"Chris\" CASCADE", userRole.getDropQuery(true));
            assertEquals("DROP USER IF EXISTS \"Chris\"", userRole.getDropQuery(false));
            userRole.setRolCanLogin(false);
            assertEquals("DROP ROLE IF EXISTS \"Chris\"", userRole.getDropQuery(true));
            assertEquals("DROP ROLE IF EXISTS \"Chris\"", userRole.getDropQuery(false));
            
            UserRole userRole1 = new UserRole();
            userRole1.setName("Chris1");
            UserRole userRole2 = new UserRole();
            userRole2.setName("Chris2");
            List<UserRole> list=new ArrayList<UserRole>();
            list.add(userRole1);
            list.add(userRole2);
            userRole.setMembers(list);
            assertEquals(2,userRole.getMembers().size());
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    
}
