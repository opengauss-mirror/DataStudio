/*
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.test.presentation.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.SequenceMetadata;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.UserNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.presentation.userrole.GrantRevokeCore;
import org.opengauss.mppdbide.presentation.userrole.GrantRevokeParameters;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.MessageQueue;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

/**
 * 
 * @ClassName: GrantRevokeCoreTest
 * @Description: junit test for GrantRevokeCore
 *
 * @since 3.0.0
 */
public class GrantRevokeCoreTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;
    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;

    Database        database;
    DBConnection    dbconn;
    GrantRevokeCore grantRevokeCore;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(new String[] {"-logfolder=.", "-detailLogging=true"});

        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
        database = connProfCache.getDbForProfileId(profileId);
        getAllDatabaseObjects();
        dbconn = CommonLLTUtils.getDBConnection();

        grantRevokeCore = new GrantRevokeCore(true,
                Arrays.asList(database.getAllNameSpaces().get(0).getTablesGroup().getChildren()));
    }

    @After
    public void tearDown() throws Exception
    {
        super.tearDown();

        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().close();

        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();
        connProfCache.closeAllNodes();

        Iterator<Server> itr = connProfCache.getServers().iterator();

        while (itr.hasNext())
        {
            connProfCache.removeServer(itr.next().getId());
        }

        connProfCache.closeAllNodes();

    }

    private void getAllDatabaseObjects()
    {
        try
        {

            database.getServer().refresh();
            Namespace ns1 = new UserNamespace(6, "ns1", database);
            Namespace ns2 = new UserNamespace(2, "NS1", database);
            Namespace ns3 = new UserNamespace(3, "NS2", database);
            Namespace ns4 = new UserNamespace(4, "yns2", database);
            Namespace ns6 = new UserNamespace(5, "\"NS1\"", database);
            Namespace ns7 = new UserNamespace(7, "Namespc", database);

            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns1);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns2);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns3);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns4);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns6);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns7);

            TableMetaData ptab1 = new PartitionTable(ns7);
            ns7.addTableToSearchPool(ptab1);

            TableMetaData tbl1 = new TableMetaData(1, "tbl1", ns1, "");
            ColumnMetaData clm = new ColumnMetaData(tbl1, 1, "Col1", null);
            tbl1.addColumn(clm);
            TableMetaData tbl2 = new TableMetaData(2, "tbl2", ns1, "");
            ColumnMetaData clm1 = new ColumnMetaData(tbl2, 1, "Col2", null);
            tbl2.addColumn(clm1);
            TableMetaData tbl3 = new TableMetaData(3, "TBL1", ns1, "");
            ColumnMetaData clm2 = new ColumnMetaData(tbl3, 1, "Col3", null);
            tbl3.addColumn(clm2);

            ns1.addTableToSearchPool(tbl1);
            ns1.addTableToSearchPool(tbl2);
            ns1.addTableToSearchPool(tbl3);

            TableMetaData tbl4 = new TableMetaData(4, "tbl1", ns2, "");
            ColumnMetaData clm3 = new ColumnMetaData(tbl4, 1, "Col4", null);
            tbl4.addColumn(clm3);
            TableMetaData tbl5 = new TableMetaData(5, "tbl2", ns2, "");
            ColumnMetaData clm4 = new ColumnMetaData(tbl5, 1, "Col5", null);
            tbl5.addColumn(clm4);
            TableMetaData tbl6 = new TableMetaData(6, "TBL1", ns2, "");
            ColumnMetaData clm5 = new ColumnMetaData(tbl6, 1, "Col6", null);
            tbl6.addColumn(clm5);
            TableMetaData tbl7 = new TableMetaData(7, "TBL2", ns2, "");
            ColumnMetaData clm6 = new ColumnMetaData(tbl7, 1, "Col7", null);
            tbl7.addColumn(clm6);
            TableMetaData tbl110 = new TableMetaData(110, "T}BL1", ns2, "");
            ColumnMetaData clm110 = new ColumnMetaData(tbl110, 1, "Col7", null);
            tbl7.addColumn(clm110);

            ViewMetaData view = new ViewMetaData(1, "Yiew1", ns2,ns2.getDatabase());
            SequenceMetadata seq = new SequenceMetadata(ns2);

            ns2.addTableToSearchPool(tbl5);
            ns2.addTableToSearchPool(tbl6);
            ns2.addTableToSearchPool(tbl7);
            ns2.addTableToSearchPool(tbl4);
            ns2.addTableToSearchPool(tbl110);
            ns2.addView(view);
            ns2.addSequence(seq);

            TableMetaData tbl8 = new TableMetaData(8, "tbl1", ns3, "");
            ColumnMetaData clm8 = new ColumnMetaData(tbl8, 1, "Col8", null);
            tbl8.addColumn(clm8);
            TableMetaData tbl9 = new TableMetaData(9, "tbl2", ns3, "");
            ColumnMetaData clm9 = new ColumnMetaData(tbl9, 1, "Col9", null);
            tbl9.addColumn(clm9);

            ns3.addTableToSearchPool(tbl8);
            ns3.addTableToSearchPool(tbl9);

            TableMetaData tbl10 = new TableMetaData(10, "Tbl11", ns4, "");
            ColumnMetaData clm10 = new ColumnMetaData(tbl10, 1, "Col10", null);
            tbl10.addColumn(clm10);
            TableMetaData tbl11 = new TableMetaData(11, "xtbl2", ns4, "");
            ColumnMetaData clm11 = new ColumnMetaData(tbl11, 1, "Col11", null);
            tbl11.addColumn(clm11);
            TableMetaData tbl12 = new TableMetaData(12, "NS1", ns4, "");
            ColumnMetaData clm12 = new ColumnMetaData(tbl12, 1, "Col12", null);
            tbl12.addColumn(clm12);

            ns4.addTableToSearchPool(tbl10);
            ns4.addTableToSearchPool(tbl11);
            ns4.addTableToSearchPool(tbl12);

            TableMetaData tbl14 = new TableMetaData(13, "ybl1", ns6, "");
            ColumnMetaData clm14 = new ColumnMetaData(tbl14, 1, "Col14", null);
            tbl14.addColumn(clm14);

            ns6.addTableToSearchPool(tbl14);
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void test_GrantRevokeCore_001()
    {
        try
        {
            TableObjectGroup tablesGroup = database.getAllNameSpaces().get(0).getTablesGroup();
            GrantRevokeCore tempGrantRevokeCore = new GrantRevokeCore(false, tablesGroup);
            assertEquals(true, tempGrantRevokeCore != null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_GrantRevokeCore_002()
    {
        try
        {
            List<Object> tableList = Arrays.asList(database.getAllNameSpaces().get(0).getTablesGroup().getChildren());
            GrantRevokeCore tempGrantRevokeCore = new GrantRevokeCore(true, tableList);
            assertEquals(true, tempGrantRevokeCore != null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getUserRoleOption_001()
    {
        try
        {
            grantRevokeCore.getUserRoleOption();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getDatabase_001()
    {
        try
        {
            grantRevokeCore.getDatabase();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getObjectOption_001()
    {
        try
        {
            grantRevokeCore.getObjectOption();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_modifyObjectPrivilege_001()
    {
        try
        {
            List<String> sqls = new ArrayList<>();
            sqls.add("grant select on table t_test to chris;");
            MessageQueue messageQueue = new MessageQueue();

            grantRevokeCore.modifyObjectPrivilege(dbconn, sqls, messageQueue);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    public GrantRevokeParameters setGrantRevokeParameters(List<Object> selectedObjects, String userRolesStr,
            List<String> withGrantOptionPrivileges, List<String> withoutGrantOptionPrivileges, boolean allPrivilege,
            boolean allWithGrantOption, List<String> revokePrivileges, List<String> revokeGrantPrivileges,
            boolean revokeAllPrivilege, boolean revokeAllGrantPrivilege, boolean isGrant)
    {
        GrantRevokeParameters params = new GrantRevokeParameters();
        params.setSelectedObjects(selectedObjects);
        params.setUserRolesStr(userRolesStr);
        params.setWithGrantOptionPrivileges(withGrantOptionPrivileges);
        params.setWithoutGrantOptionPrivileges(withoutGrantOptionPrivileges);
        params.setAllPrivilege(allPrivilege);
        params.setAllWithGrantOption(allWithGrantOption);
        params.setRevokePrivileges(revokePrivileges);
        params.setRevokeGrantPrivileges(revokeGrantPrivileges);
        params.setRevokeAllPrivilege(revokeAllPrivilege);
        params.setRevokeAllGrantPrivilege(revokeAllGrantPrivilege);
        params.setGrant(isGrant);
        return params;
    }
    
    @Test
    public void test_generateSql_001()
    {
        try
        {
            List<Object> selectedObjects =
                    Arrays.asList(database.getAllNameSpaces().get(0).getTablesGroup().getChildren());
            String userRolesStr = "chris, alex";
            List<String> withGrantOptionPrivileges = Arrays.asList(new String[] {"select", "update"});
            List<String> withoutGrantOptionPrivileges = Arrays.asList(new String[] {"delete"});
            boolean allPrivilege = false;
            boolean allWithGrantOption = false;
            List<String> revokePrivileges = Arrays.asList(new String[] {"insert"});
            List<String> revokeGrantPrivileges = Arrays.asList(new String[] {"truncate"});
            boolean revokeAllPrivilege = false;
            boolean revokeAllGrantPrivilege = false;
            boolean isGrant = true;

            grantRevokeCore.generateSql(setGrantRevokeParameters(selectedObjects, userRolesStr,
                    withGrantOptionPrivileges, withoutGrantOptionPrivileges, allPrivilege, allWithGrantOption,
                    revokePrivileges, revokeGrantPrivileges, revokeAllPrivilege, revokeAllGrantPrivilege, isGrant));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_generateSql_002()
    {
        try
        {
            List<Object> selectedObjects =
                    Arrays.asList(database.getAllNameSpaces().get(0).getTablesGroup().getChildren());
            String userRolesStr = "chris, alex";
            List<String> withGrantOptionPrivileges = Arrays.asList(new String[] {"select", "update"});
            List<String> withoutGrantOptionPrivileges = Arrays.asList(new String[] {"delete"});
            boolean allPrivilege = true;
            boolean allWithGrantOption = false;
            List<String> revokePrivileges = Arrays.asList(new String[] {"insert"});
            List<String> revokeGrantPrivileges = Arrays.asList(new String[] {"truncate"});
            boolean revokeAllPrivilege = true;
            boolean revokeAllGrantPrivilege = false;
            boolean isGrant = false;

            grantRevokeCore.generateSql(setGrantRevokeParameters(selectedObjects, userRolesStr,
                    withGrantOptionPrivileges, withoutGrantOptionPrivileges, allPrivilege, allWithGrantOption,
                    revokePrivileges, revokeGrantPrivileges, revokeAllPrivilege, revokeAllGrantPrivilege, isGrant));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_generateSql_003()
    {
        try
        {
            List<Object> selectedObjects =
                    Arrays.asList(database.getAllNameSpaces().get(0).getTablesGroup().getChildren());
            String userRolesStr = "chris, alex";
            List<String> withGrantOptionPrivileges = Arrays.asList(new String[] {"select", "update"});
            List<String> withoutGrantOptionPrivileges = Arrays.asList(new String[] {"delete"});
            boolean allPrivilege = true;
            boolean allWithGrantOption = false;
            List<String> revokePrivileges = Arrays.asList(new String[] {"insert"});
            List<String> revokeGrantPrivileges = Arrays.asList(new String[] {"truncate"});
            boolean revokeAllPrivilege = true;
            boolean revokeAllGrantPrivilege = false;
            boolean isGrant = true;

            grantRevokeCore.generateSql(setGrantRevokeParameters(selectedObjects, userRolesStr,
                    withGrantOptionPrivileges, withoutGrantOptionPrivileges, allPrivilege, allWithGrantOption,
                    revokePrivileges, revokeGrantPrivileges, revokeAllPrivilege, revokeAllGrantPrivilege, isGrant));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_generateSql_004()
    {
        try
        {
            List<Object> selectedObjects =
                    Arrays.asList(database.getAllNameSpaces().get(0).getTablesGroup().getChildren());
            String userRolesStr = "chris, alex";
            List<String> withGrantOptionPrivileges = Arrays.asList(new String[] {"select", "update"});
            List<String> withoutGrantOptionPrivileges = Arrays.asList(new String[] {"delete"});
            boolean allPrivilege = true;
            boolean allWithGrantOption = false;
            List<String> revokePrivileges = Arrays.asList(new String[] {"insert"});
            List<String> revokeGrantPrivileges = Arrays.asList(new String[] {"truncate"});
            boolean revokeAllPrivilege = false;
            boolean revokeAllGrantPrivilege = true;
            boolean isGrant = false;

            grantRevokeCore.generateSql(setGrantRevokeParameters(selectedObjects, userRolesStr,
                    withGrantOptionPrivileges, withoutGrantOptionPrivileges, allPrivilege, allWithGrantOption,
                    revokePrivileges, revokeGrantPrivileges, revokeAllPrivilege, revokeAllGrantPrivilege, isGrant));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_generateSql_005()
    {
        try
        {
            List<Object> selectedObjects =
                    Arrays.asList(database.getAllNameSpaces().get(0).getTablesGroup().getChildren());
            String userRolesStr = "chris, alex";
            List<String> withGrantOptionPrivileges = Arrays.asList(new String[] {"select", "update"});
            List<String> withoutGrantOptionPrivileges = Arrays.asList(new String[] {"delete"});
            boolean allPrivilege = true;
            boolean allWithGrantOption = false;
            List<String> revokePrivileges = Arrays.asList(new String[] {"insert"});
            List<String> revokeGrantPrivileges = Arrays.asList(new String[] {"truncate"});
            boolean revokeAllPrivilege = false;
            boolean revokeAllGrantPrivilege = false;
            boolean isGrant = false;

            grantRevokeCore.generateSql(setGrantRevokeParameters(selectedObjects, userRolesStr,
                    withGrantOptionPrivileges, withoutGrantOptionPrivileges, allPrivilege, allWithGrantOption,
                    revokePrivileges, revokeGrantPrivileges, revokeAllPrivilege, revokeAllGrantPrivilege, isGrant));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_generateSql_006()
    {
        try
        {
            List<Object> selectedObjects = Arrays.asList(database.getAllNameSpaces().get(0));
            String userRolesStr = "chris, alex";
            List<String> withGrantOptionPrivileges = Arrays.asList(new String[] {"select", "update"});
            List<String> withoutGrantOptionPrivileges = Arrays.asList(new String[] {"delete"});
            boolean allPrivilege = true;
            boolean allWithGrantOption = false;
            List<String> revokePrivileges = Arrays.asList(new String[] {"insert"});
            List<String> revokeGrantPrivileges = Arrays.asList(new String[] {"truncate"});
            boolean revokeAllPrivilege = false;
            boolean revokeAllGrantPrivilege = false;
            boolean isGrant = false;

            grantRevokeCore.generateSql(setGrantRevokeParameters(selectedObjects, userRolesStr,
                    withGrantOptionPrivileges, withoutGrantOptionPrivileges, allPrivilege, allWithGrantOption,
                    revokePrivileges, revokeGrantPrivileges, revokeAllPrivilege, revokeAllGrantPrivilege, isGrant));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_generateSql_007()
    {
        try
        {
            List<Object> selectedObjects =
                    Arrays.asList(new DebugObjects(1L, "debug_object", OBJECTTYPE.PLSQLFUNCTION, database));
            String userRolesStr = "chris, alex";
            List<String> withGrantOptionPrivileges = Arrays.asList(new String[] {"select", "update"});
            List<String> withoutGrantOptionPrivileges = Arrays.asList(new String[] {"delete"});
            boolean allPrivilege = true;
            boolean allWithGrantOption = false;
            List<String> revokePrivileges = Arrays.asList(new String[] {"insert"});
            List<String> revokeGrantPrivileges = Arrays.asList(new String[] {"truncate"});
            boolean revokeAllPrivilege = false;
            boolean revokeAllGrantPrivilege = false;
            boolean isGrant = false;

            grantRevokeCore.generateSql(setGrantRevokeParameters(selectedObjects, userRolesStr,
                    withGrantOptionPrivileges, withoutGrantOptionPrivileges, allPrivilege, allWithGrantOption,
                    revokePrivileges, revokeGrantPrivileges, revokeAllPrivilege, revokeAllGrantPrivilege, isGrant));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_generateSql_008()
    {
        try
        {
            List<Object> selectedObjects = Arrays.asList(database.getAllNameSpaces().get(0));
            String userRolesStr = "chris, alex";
            List<String> withGrantOptionPrivileges = Arrays.asList(new String[] {"select", "update"});
            List<String> withoutGrantOptionPrivileges = Arrays.asList(new String[] {"delete"});
            boolean allPrivilege = true;
            boolean allWithGrantOption = false;
            List<String> revokePrivileges = Arrays.asList(new String[] {"insert"});
            List<String> revokeGrantPrivileges = Arrays.asList(new String[] {"truncate"});
            boolean revokeAllPrivilege = false;
            boolean revokeAllGrantPrivilege = false;
            boolean isGrant = true;

            grantRevokeCore.generateSql(setGrantRevokeParameters(selectedObjects, userRolesStr,
                    withGrantOptionPrivileges, withoutGrantOptionPrivileges, allPrivilege, allWithGrantOption,
                    revokePrivileges, revokeGrantPrivileges, revokeAllPrivilege, revokeAllGrantPrivilege, isGrant));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_generateSql_009()
    {
        try
        {
            List<Object> selectedObjects =
                    Arrays.asList(new DebugObjects(1L, "debug_object", OBJECTTYPE.PLSQLFUNCTION, database));
            String userRolesStr = "chris, alex";
            List<String> withGrantOptionPrivileges = Arrays.asList(new String[] {"select", "update"});
            List<String> withoutGrantOptionPrivileges = Arrays.asList(new String[] {"delete"});
            boolean allPrivilege = true;
            boolean allWithGrantOption = false;
            List<String> revokePrivileges = Arrays.asList(new String[] {"insert"});
            List<String> revokeGrantPrivileges = Arrays.asList(new String[] {"truncate"});
            boolean revokeAllPrivilege = false;
            boolean revokeAllGrantPrivilege = false;
            boolean isGrant = true;

            grantRevokeCore.generateSql(setGrantRevokeParameters(selectedObjects, userRolesStr,
                    withGrantOptionPrivileges, withoutGrantOptionPrivileges, allPrivilege, allWithGrantOption,
                    revokePrivileges, revokeGrantPrivileges, revokeAllPrivilege, revokeAllGrantPrivilege, isGrant));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_generateSql_010()
    {
        try
        {
            List<Object> selectedObjects =
                    Arrays.asList(database.getAllNameSpaces().get(0).getTablesGroup().getChildren());
            String userRolesStr = "chris, alex";
            List<String> withGrantOptionPrivileges = Arrays.asList(new String[] {"select", "update"});
            List<String> withoutGrantOptionPrivileges = Arrays.asList(new String[] {"delete"});
            boolean allPrivilege = true;
            boolean allWithGrantOption = true;
            List<String> revokePrivileges = Arrays.asList(new String[] {"insert"});
            List<String> revokeGrantPrivileges = Arrays.asList(new String[] {"truncate"});
            boolean revokeAllPrivilege = true;
            boolean revokeAllGrantPrivilege = false;
            boolean isGrant = true;

            grantRevokeCore.generateSql(setGrantRevokeParameters(selectedObjects, userRolesStr,
                    withGrantOptionPrivileges, withoutGrantOptionPrivileges, allPrivilege, allWithGrantOption,
                    revokePrivileges, revokeGrantPrivileges, revokeAllPrivilege, revokeAllGrantPrivilege, isGrant));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
