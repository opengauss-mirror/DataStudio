
package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import org.opengauss.mppdbide.adapter.gauss.Activator;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintType;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseUtils;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.IndexedColumnExpr;
import org.opengauss.mppdbide.bl.serverdatacache.IndexedColumnType;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.ServerProperty;
import org.opengauss.mppdbide.bl.serverdatacache.SourceCode;
import org.opengauss.mppdbide.bl.serverdatacache.SystemNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ColumnList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ConstraintList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.IndexList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ObjectList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.util.BLUtils;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IBLUtils;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.mock.bl.ProfileDiskUtilityHelper;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class CommonTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    JobCancelStatus                   status                    = null;

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
	public void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        MockBLPreferenceImpl.setDateFormat("yyyy-MM-dd");
        MockBLPreferenceImpl.setTimeFormat("HH:mm:ss");
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        status=new JobCancelStatus();
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
        CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
        CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
        CommonLLTUtils.fetchViewColumnInfo(preparedstatementHandler);
        CommonLLTUtils.preparePartitionConstrainstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionIndexLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.fetchAllSynonyms(preparedstatementHandler);
        CommonLLTUtils.fetchTriggerQuery(preparedstatementHandler);
            connProfCache = DBConnProfCache.getInstance();
       
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        ProfileDiskUtilityHelper profile=new ProfileDiskUtilityHelper();
        profile.setOption(4);
        ConnectionProfileManagerImpl.getInstance().setDiskUtility(profile);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo,status);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
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

    @Test
    public void testTTA_BL_TABLE_FUNC_001_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(1);

            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    namespace, "tablespace");

            ColumnList columnList = new ColumnList(OBJECTTYPE.OBJECTTYPE_BUTT,
                    tablemetaData);
            assertEquals(tablemetaData, columnList.getParent());

            ConstraintList constraintList = new ConstraintList(
                    OBJECTTYPE.OBJECTTYPE_BUTT, tablemetaData);
            assertEquals(tablemetaData, constraintList.getParent());

            DebugObjectGroup debugObjectGroup = new DebugObjectGroup(
                    OBJECTTYPE.OBJECTTYPE_BUTT, namespace);
            assertEquals(namespace, debugObjectGroup.getNamespace());

            IndexList indexList = new IndexList(OBJECTTYPE.OBJECTTYPE_BUTT,
                    tablemetaData);
            assertEquals(tablemetaData, indexList.getParent());

            /*
             * NodeLists nodeLists = new NodeLists(database.getServer());
             * assertEquals(nodeLists.getServer(), database.getServer());
             */

            TableObjectGroup tableObjectGroup = new TableObjectGroup(
                    OBJECTTYPE.OBJECTTYPE_BUTT, namespace);
            assertEquals(namespace, tableObjectGroup.getNamespace());

            ObjectGroup<ServerObject> objectGroup = new ObjectGroup<ServerObject>(
                    OBJECTTYPE.OBJECTTYPE_BUTT, new Object());

            assertEquals(OBJECTTYPE.OBJECTTYPE_BUTT,
                    objectGroup.getObjectGroupType());
            ObjectGroup<ServerObject> objectGroup1 = new ObjectGroup<ServerObject>(
                    OBJECTTYPE.DATATYPE_GROUP, new Object());
            objectGroup.getName();

            assertTrue(objectGroup.getParent() instanceof Object);

            ObjectList<ServerObject> objectList = new ObjectList<ServerObject>(
                    OBJECTTYPE.OBJECTTYPE_BUTT, new Object());
            objectList.getType();
            objectList.setName("Name");
            objectList.getName();
            ObjectList<ServerObject> objectList1 = new ObjectList<ServerObject>(
                    OBJECTTYPE.OBJECTTYPE_BUTT, database.getServer());

            @SuppressWarnings("unused")
            ObjectList<ServerObject> objectList2 = new ObjectList<ServerObject>(
                    OBJECTTYPE.DATATYPE_GROUP, null);
        }
        catch (Exception e)
        {
           
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_SERVER_EQUALS_HASHCODE_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            server.hashCode();
            server.equals(server);
            server.equals(null);
            server.equals(new Object());

            Server server2 = new Server(database.getServer().getServerConnectionInfo(database.getDbName()));
            server.equals(server2);

            server2 = database.getServer();
            server.equals(server2);
            ServerConnectionInfo info = new ServerConnectionInfo();
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(info);
            server = new Server(info);
            server.setHost(null);
            server.hashCode();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DATABSE_EQUALS_HASHCODE_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            database.hashCode();

            database.equals(database);
            database.equals(null);
            Database database2 = new Database(database.getServer(),
                    database.getOid(),database.getDbName());
            database.equals(database2);

            database2 = new Database(
                    database.getServer(), 1234,database.getDbName());
            database.equals(database2);

            database.equals(new Object());

            database2 = new Database(
                    new Server(database.getServer().getServerConnectionInfo(database.getDbName())),
                    database.getOid(),database.getDbName());
            assertFalse(database.equals(database2));
            //assertNotEquals(database, database2);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_NAMESPACE_EQUALS_HASHCODE_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(1);
            namespace.hashCode();

            namespace.equals(namespace);
            namespace.equals(null);
            namespace.equals(new Object());

            Namespace namespace2 = new Namespace(namespace.getOid(),
                    "namepsace", database);
            assertEquals(namespace, namespace2);

            namespace2 = new Namespace(2, "namepsace", database);
            namespace.equals(namespace2);

            Database database2 = new Database(database.getServer(),
                    database.getOid(),database.getDbName());
            namespace2 = new Namespace(namespace.getOid(), "namepsace",
                    database2);
            namespace.equals(namespace2);

            database2 = new Database(
                    database.getServer(), 2,database.getDbName());
            namespace2 = new Namespace(namespace.getOid(), "namepsace",
                    database2);
            namespace.equals(namespace2);

            database2 = new Database(new Server(database.getServer().getServerConnectionInfo(database.getDbName())),
                    database.getOid(),database.getDbName());
            namespace2 = new Namespace(namespace.getOid(), "namepsace",
                    database2);
            assertFalse(namespace.equals(namespace2));
            //assertNotEquals(namespace, namespace2);
            /*namespace = database.getNameSpaceById(1);
            namespace.setName(null);
            namespace.hashCode();*/

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_TABLEMETADATA_EQUALS_HASHCODE_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            tablemetaData.hashCode();
            tablemetaData.equals(null);
            tablemetaData.equals(tablemetaData);
            tablemetaData.equals(new Object());

            TableMetaData tableMetaData2 = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            tablemetaData.equals(tableMetaData2);

            tableMetaData2 = new TableMetaData(2, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            tablemetaData.equals(tableMetaData2);

            Database database2 = new Database(database.getServer(), 2,database.getDbName());
            Namespace namespace = new Namespace(1, "namepsace", database2);

            tableMetaData2 = new TableMetaData(1, "Table1", namespace,
                    "tablespace");
            tablemetaData.equals(tableMetaData2);

            database2 = new Database(
                    new Server(database.getServer().getServerConnectionInfo(database.getDbName())), 2,database.getDbName());
            namespace = new Namespace(1, "namepsace", database2);

            tableMetaData2 = new TableMetaData(1, "Table1", namespace,
                    "tablespace");
            assertFalse(tablemetaData.equals(tableMetaData2));
            //assertNotEquals(tablemetaData, tableMetaData2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    /*
     * @Test public void testTTA_BL_NODEGROUP_EQUALS_HASHCODE_001() { try {
     * //Database database = connProfCache.getDbForProfileId(profileId);
     * NodeGroup nodeGroup = new NodeGroup(1, "Nodegroup 1");
     * nodeGroup.hashCode(); nodeGroup.equals(null);
     * nodeGroup.equals(nodeGroup); nodeGroup.equals(new Object());
     * 
     * NodeGroup nodeGroup2 = new NodeGroup(2, "Nodegroup 2");
     * nodeGroup.equals(nodeGroup2); } catch(Exception e) { e.printStackTrace();
     * fail(e.getMessage()); }
     * 
     * }
     */

    @Test
    public void testTTA_BL_COLUMNLIST_EQUALS_HASHCODE_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            ColumnList columnList = (ColumnList) tablemetaData.getColumns();
            columnList.hashCode();

            columnList.equals(null);
            columnList.equals(columnList);
            columnList.equals(new Object());

            ColumnList columnList2 = new ColumnList(OBJECTTYPE.COLUMN_GROUP,
                    tablemetaData);
            columnList.equals(columnList2);

            tablemetaData = new TableMetaData(2, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            columnList2 = new ColumnList(OBJECTTYPE.COLUMN_GROUP, tablemetaData);
            columnList.equals(columnList2);

            Database database2 = new Database(database.getServer(), 2,database.getDbName());
            Namespace namespace = new Namespace(1, "namepsace", database2);

            tablemetaData = new TableMetaData(1, "Table1", namespace,
                    "tablespace");
            columnList2 = new ColumnList(OBJECTTYPE.COLUMN_GROUP, tablemetaData);
            columnList.equals(columnList2);

            database2 = new Database(
                    new Server(database.getServer().getServerConnectionInfo(database.getDbName())), 2,database.getDbName()
                    );
            namespace = new Namespace(1, "namepsace", database2);

            tablemetaData = new TableMetaData(1, "Table1", namespace,
                    "tablespace");
            columnList2 = new ColumnList(OBJECTTYPE.COLUMN_GROUP, tablemetaData);
            assertFalse(columnList.equals(columnList2));
            //assertNotEquals(columnList, columnList2);
            ;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_CONSTRAINTLIST_EQUALS_HASHCODE_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            ConstraintList columnList = (ConstraintList) tablemetaData
                    .getConstraints();
            columnList.hashCode();

            columnList.equals(null);
            columnList.equals(columnList);
            columnList.equals(new Object());

            ConstraintList columnList2 = new ConstraintList(
                    OBJECTTYPE.COLUMN_GROUP, tablemetaData);
            columnList.equals(columnList2);

            tablemetaData = new TableMetaData(2, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            columnList2 = new ConstraintList(OBJECTTYPE.COLUMN_GROUP,
                    tablemetaData);
            columnList.equals(columnList2);

            Database database2 = new Database(database.getServer(), 2,database.getDbName());
            Namespace namespace = new Namespace(1, "namepsace", database2);

            tablemetaData = new TableMetaData(1, "Table1", namespace,
                    "tablespace");
            columnList2 = new ConstraintList(OBJECTTYPE.COLUMN_GROUP,
                    tablemetaData);
            columnList.equals(columnList2);

            database2 = new Database(
                    new Server(database.getServer().getServerConnectionInfo(database.getDbName())), 2,database.getDbName());
            namespace = new Namespace(1, "namepsace", database2);

            tablemetaData = new TableMetaData(1, "Table1", namespace,
                    "tablespace");
            columnList2 = new ConstraintList(OBJECTTYPE.COLUMN_GROUP,
                    tablemetaData);
            assertFalse(columnList.equals(columnList2));
            //assertNotEquals(columnList, columnList2);
           
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_INDEXLIST_EQUALS_HASHCODE_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            IndexList columnList = (IndexList) tablemetaData.getIndexes();
            columnList.hashCode();

            columnList.equals(null);
            columnList.equals(columnList);
            columnList.equals(new Object());

            IndexList columnList2 = new IndexList(OBJECTTYPE.COLUMN_GROUP,
                    tablemetaData);
            columnList.equals(columnList2);

            tablemetaData = new TableMetaData(2, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            columnList2 = new IndexList(OBJECTTYPE.COLUMN_GROUP, tablemetaData);
            columnList.equals(columnList2);

            Database database2 = new Database(database.getServer(), 2,database.getDbName());
            Namespace namespace = new Namespace(1, "namepsace", database2);

            tablemetaData = new TableMetaData(1, "Table1", namespace,
                    "tablespace");
            columnList2 = new IndexList(OBJECTTYPE.COLUMN_GROUP, tablemetaData);
            columnList.equals(columnList2);

            database2 = new Database(new Server(database.getServer().getServerConnectionInfo(database.getDbName())), 2,database.getDbName());
            namespace = new Namespace(1, "namepsace", database2);

            tablemetaData = new TableMetaData(1, "Table1", namespace,
                    "tablespace");
            columnList2 = new IndexList(OBJECTTYPE.COLUMN_GROUP, tablemetaData);
            assertFalse(columnList.equals(columnList2));
            //assertNotEquals(columnList, columnList2);
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DEBUGOBJECTGROUP_EQUALS_HASHCODE_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            DebugObjectGroup debugObjectGroup = new DebugObjectGroup(
                    OBJECTTYPE.FUNCTION_GROUP, database.getNameSpaceById(1));

            debugObjectGroup.hashCode();
            debugObjectGroup.equals(null);
            debugObjectGroup.equals(debugObjectGroup);
            debugObjectGroup.equals(new Object());

            Namespace namespace = new Namespace(2, "namepsace", database);
            DebugObjectGroup debugObjectGroup2 = new DebugObjectGroup(
                    OBJECTTYPE.FUNCTION_GROUP, namespace);
            debugObjectGroup.equals(debugObjectGroup2);

            namespace = new Namespace(1, "namepsace", database);
            debugObjectGroup2 = new DebugObjectGroup(OBJECTTYPE.FUNCTION_GROUP,
                    namespace);
            debugObjectGroup.equals(debugObjectGroup2);

            Database database2 = new Database(database.getServer(), 2,database.getDbName());
            namespace = new Namespace(namespace.getOid(), "namepsace",
                    database2);
            debugObjectGroup2 = new DebugObjectGroup(OBJECTTYPE.FUNCTION_GROUP,
                    namespace);
            debugObjectGroup.equals(debugObjectGroup2);

            database2 = new Database(new Server(database.getServer().getServerConnectionInfo(database.getDbName())),
                    database.getOid(),database.getDbName());
            namespace = new Namespace(namespace.getOid(), "namepsace",
                    database2);
            debugObjectGroup2 = new DebugObjectGroup(OBJECTTYPE.FUNCTION_GROUP,
                    namespace);
            assertFalse(debugObjectGroup.equals(debugObjectGroup2));
            //assertNotEquals(debugObjectGroup, debugObjectGroup2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    /*
     * @Test public void testTTA_BL_NODELIST_EQUALS_HASHCODE_001() { try {
     * Database database = connProfCache.getDbForProfileId(profileId); NodeLists
     * nodeLists = new NodeLists(database.getServer()); nodeLists.hashCode();
     * 
     * nodeLists.equals(null); nodeLists.equals(nodeLists); nodeLists.equals(new
     * Object());
     * 
     * NodeLists nodeLists2 = new NodeLists(database.getServer());
     * nodeLists.equals(nodeLists2);
     * 
     * nodeLists2 = new NodeLists(new
     * Server(database.getServerConnectionInfo()));
     * nodeLists.equals(nodeLists2); } catch(Exception e) { e.printStackTrace();
     * fail(e.getMessage()); }
     * 
     * }
     */

    @Test
    public void testTTA_BL_TBLOBJGRP_EQUALS_HASHCODE_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableObjectGroup group = new TableObjectGroup(
                    OBJECTTYPE.TABLE_GROUP, database.getNameSpaceById(1));

            group.hashCode();
            group.equals(null);
            group.equals(group);
            group.equals(new Object());

            Namespace namespace = new Namespace(2, "namepsace", database);
            TableObjectGroup group2 = new TableObjectGroup(
                    OBJECTTYPE.TABLE_GROUP, namespace);
            group.equals(group2);

            group2 = new TableObjectGroup(OBJECTTYPE.TABLE_GROUP, null);
            group2.hashCode();

            namespace = new Namespace(2, null, database);
            group2 = new TableObjectGroup(OBJECTTYPE.TABLE_GROUP, namespace);
            group2.hashCode();

            namespace = new Namespace(1, "namepsace", database);
            group2 = new TableObjectGroup(OBJECTTYPE.TABLE_GROUP, namespace);
            group.equals(group2);

            Database database2 = new Database(database.getServer(), 2,database.getDbName());
            namespace = new Namespace(namespace.getOid(), "namepsace",
                    database2);
            group2 = new TableObjectGroup(OBJECTTYPE.TABLE_GROUP, namespace);
            group.equals(group2);

            database2 = new Database(new Server(database.getServer().getServerConnectionInfo(database.getDbName())),
                    database.getOid(),database.getDbName());
            namespace = new Namespace(namespace.getOid(), "namepsace",
                    database2);
            group2 = new TableObjectGroup(OBJECTTYPE.TABLE_GROUP, namespace);
            assertFalse(group.equals(group2));
            //assertNotEquals(group, group2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_INDEXCOL_EXPR_FUNC_001_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(1);

            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    namespace, "tablespace");

            IndexedColumnExpr columnExpr = new IndexedColumnExpr(
                    IndexedColumnType.EXPRESSION);
            columnExpr.setExpr("expr");
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "Col1", new TypeMetaData(2, "integer", namespace));
            columnExpr.setCol(columnMetaData);
            columnExpr.getExpr();
            columnExpr.toString();

            columnExpr = new IndexedColumnExpr(IndexedColumnType.COLUMN);
            columnExpr.setExpr("expr");
            columnMetaData = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(2, "integer", namespace));
            columnExpr.setCol(columnMetaData);
            columnExpr.getExpr();
            columnExpr.toString();
            columnExpr.getPosition();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_CONN_PROF_ID_FUNC_001_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);

            ConnectionProfileId id = database.getProfileId();
            id.getDatabase();

            id.isEquals(id);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_SRV_PROP_FUNC_001_001()
    {
        try
        {
            ServerProperty property = new ServerProperty("key", "value");
            property.getKey();
            property.getValue();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_SOURCE_CODE_FUNC_001_001()
    {
        try
        {
            SourceCode code = new SourceCode();
            code.getCode();
            code.getSourceLineNumFromServerEquivalent(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_TYPE_METADATA_FUNC_001_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TypeMetaData metaData = new TypeMetaData(1, "name",
                    database.getNameSpaceById(1));
            TypeMetaData.getPGTypeCategory("A");
            TypeMetaData.getPGTypeCategory("B");
            TypeMetaData.getPGTypeCategory("C");
            TypeMetaData.getPGTypeCategory("D");
            TypeMetaData.getPGTypeCategory("E");
            TypeMetaData.getPGTypeCategory("G");
            TypeMetaData.getPGTypeCategory("I");
            TypeMetaData.getPGTypeCategory("N");
            TypeMetaData.getPGTypeCategory("P");
            TypeMetaData.getPGTypeCategory("R");
            TypeMetaData.getPGTypeCategory("S");
            TypeMetaData.getPGTypeCategory("T");
            TypeMetaData.getPGTypeCategory("U");
            TypeMetaData.getPGTypeCategory("V");
            TypeMetaData.getPGType("b");
            TypeMetaData.getPGType("c");
            TypeMetaData.getPGType("d");
            metaData.getCategory();
            metaData.getDescription();
            metaData.getNamespace();
            metaData.getTyparray();
            metaData.getType();
            metaData.getTypelen();
            metaData.getTyptypmod();
            metaData.isIsbyval();
            metaData.isTypnotnull();
            metaData.setNamespace(database.getNameSpaceById(1));
            //ArrayList<TypeMetaData> types = new ArrayList<TypeMetaData>();
            TypeMetaData metaData1=   TypeMetaData.getTypeById(metaData.getNamespace() , 1);
            assertNotNull(metaData1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }


    /*
     * @Test public void testTTA_BL_NODE_FUNC_001_001() { try { Node node = new
     * Node(0, "name"); node.getHost(); node.getNodeExternalId();
     * node.getPort(); node.getType(); node.isPreferred(); node.isPrimary(); }
     * catch(Exception e) { e.printStackTrace(); fail(e.getMessage());
     * 
     * }
     * 
     * }
     */

    @Test
    public void testTTA_BL_DEBUGOBJECT_FUNC_001_001()
    {
        try
        {
        	CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
            CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            
            DebugObjects debugObject = DatabaseUtils.getDebugObjects(database, 1);

            String query = "select headerlines, definition from PG_GET_FUNCTIONDEF(1);";

            StringBuilder strSourcecode = new StringBuilder();

            strSourcecode.append("\"Declare").append("\nc INT = 6;")
                    .append("\nd INT;BEGIN");
            strSourcecode.append("\nc := c+1;").append(
                    "\nc := perform nestedfunc()");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;")
                    .append("\nc := 100;");
            strSourcecode.append("\nd := c + 200;").append("\nreturn d;")
                    .append("\nend;");

            MockResultSet indexRS = preparedstatementHandler.createResultSet();
            indexRS.addRow(new Object[] {});

            preparedstatementHandler.prepareResultSet(query, indexRS);
            
            String query2 = "select xmin1, cmin1 from pldbg_get_funcVer(" + 1 + ")";
            MockResultSet versionRS = preparedstatementHandler.createResultSet();
            versionRS.addRow(new Object[] {1, 1});
            preparedstatementHandler.prepareResultSet(query2, versionRS);

            ArrayList<ObjectParameter> templateParameters = new ArrayList<ObjectParameter>();

            ObjectParameter objectParameter1 = new ObjectParameter();
            objectParameter1.setType(PARAMETERTYPE.IN);
            objectParameter1.setDataType("char");
            objectParameter1.setName("param1");
            objectParameter1.setValue("Val1");

            templateParameters.add(objectParameter1);

            objectParameter1 = new ObjectParameter();
            objectParameter1.setType(PARAMETERTYPE.IN);
            objectParameter1.setDataType("text");
            objectParameter1.setName("param2");
            objectParameter1.setValue("null");
            templateParameters.add(objectParameter1);

            objectParameter1 = new ObjectParameter();
            objectParameter1.setType(PARAMETERTYPE.IN);
            objectParameter1.setDataType("text");
            objectParameter1.setName("param3");
            objectParameter1.setValue("'text123'");
            templateParameters.add(objectParameter1);

            objectParameter1 = new ObjectParameter();
            objectParameter1.setType(PARAMETERTYPE.IN);
            objectParameter1.setDataType("<unknown>");
            objectParameter1.setName("param3");
            objectParameter1.setValue("'text123'");
            templateParameters.add(objectParameter1);

            debugObject.setTemplateParameters(templateParameters);

            debugObject.clearTemplateParameterValues();
            debugObject.getUsagehint();

            debugObject.setSourceCode(null);

            debugObject.refreshSourceCode();
            fail("Not expected to come here...");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected ");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_DEBUGOBJECT_FUNC_001_002()
    {
        try
        {
        	CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            DebugObjects debugObject = DatabaseUtils.getDebugObjects(database, 1);

            debugObject.setSourceCode(new SourceCode());
            debugObject.getSourceCode();
            debugObject.generateExecutionTemplate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_UTILS_FUNC_001_002()
    {
        try
        {
            final String path = ".";
            IBLUtils blUtils = BLUtils.getInstance();
            assertEquals(path, blUtils.getInstallationLocation());
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }


    @Test
    public void testTTA_BL_COMMON_FUNC_001_001()
    {
        try
        {
            ObjectParameter parameter = new ObjectParameter();
            parameter.getIsSupportedDatatype();

            ConstraintMetaData metaData = new ConstraintMetaData(1, "name",
                    ConstraintType.PRIMARY_KEY_CONSTRSINT);
            metaData.setPkeyOrUkeyConstraint("col1,col2", "col1,col2");
            metaData.getColumnList();

            metaData.setDeffearableOptions(true, false);
            metaData.formConstraintString();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_COMMON_FUNC_001_001_1()
    {
        try
        {
            ObjectParameter parameter = new ObjectParameter();
            parameter.getIsSupportedDatatype();

            ConstraintMetaData metaData = new ConstraintMetaData(1, "name",
                    ConstraintType.CHECK_CONSTRSINT);
            metaData.setPkeyOrUkeyConstraint("col1,col2", "col1,col2");
            metaData.getColumnList();

            metaData.setDeffearableOptions(true, false);
            metaData.setCheckConstraintExpr("expression");
            metaData.formConstraintString();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }
	
	@Test
    public void testTTA_BL_COMMON_FUNC_001_011_1()
    {
        try
        {
            ObjectParameter parameter = new ObjectParameter();
            parameter.getIsSupportedDatatype();

            ConstraintMetaData metaData = new ConstraintMetaData(1, "name",
                    ConstraintType.EXCLUSION_CONSTRSINT);
            metaData.setPkeyOrUkeyConstraint("col1,col2", "col1,col2");
            metaData.getColumnList();

            metaData.setDeffearableOptions(true, false);
            metaData.setCheckConstraintExpr("expression");
         String query=   metaData.formConstraintString();
         assertEquals("CONSTRAINT name  DEFERRABLE INITIALLY IMMEDIATE", query);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_COMMON_FUNC_001_001_2()
    {
        try
        {
            ObjectParameter parameter = new ObjectParameter();
            parameter.getIsSupportedDatatype();

            ConstraintMetaData metaData = new ConstraintMetaData(1, "name",
                    ConstraintType.CHECK_CONSTRSINT);
            metaData.setPkeyOrUkeyConstraint("col1,col2", "col1,col2");
            metaData.getColumnList();

            metaData.setDeffearableOptions(true, false);
            metaData.setCheckConstraintExpr("\"Expression");
            metaData.formConstraintString();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_COMMON_FUNC_001_001_3()
    {
        try
        {
            ObjectParameter parameter = new ObjectParameter();
            parameter.getIsSupportedDatatype();

            ConstraintMetaData metaData = new ConstraintMetaData(1, "name",
                    ConstraintType.CHECK_CONSTRSINT);
            metaData.setPkeyOrUkeyConstraint("col1,col2", "col1,col2");
            metaData.getColumnList();

            metaData.setDeffearableOptions(true, false);
            metaData.setCheckConstraintExpr("Expression");
            metaData.formConstraintString();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_COMMON_FUNC_001_001_4()
    {
        try
        {
            ObjectParameter parameter = new ObjectParameter();
            parameter.getIsSupportedDatatype();

            ConstraintMetaData metaData = new ConstraintMetaData(1, "name",
                    ConstraintType.FOREIGN_KEY_CONSTRSINT);
            metaData.setPkeyOrUkeyConstraint("col1,col2", "col1,col2");
            metaData.getColumnList();

            metaData.setDeffearableOptions(true, false);
            assertTrue(null!=metaData);
            // metaData.setCheckConstraintExpr("Expression");
            metaData.formConstraintString();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            // fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_COMMON_FUNC_001_001_5()
    {
        try
        {
            ObjectParameter parameter = new ObjectParameter();
            parameter.getIsSupportedDatatype();

            ConstraintMetaData metaData = new ConstraintMetaData(1, "name",
                    ConstraintType.FOREIGN_KEY_CONSTRSINT);
            metaData.setPkeyOrUkeyConstraint("col1,col2", "col1,col2");
            metaData.getColumnList();
            assertTrue(null!=metaData);

            metaData.setDeffearableOptions(true, false);
            // metaData.setCheckConstraintExpr("Expression");
            metaData.setPkeyOrUkeyConstraint("Column", "table");
            metaData.formConstraintString();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            // fail(e.getMessage());

        }

    }

    @Test
    public void test_ExecTimer_001()
    {
        IExecTimer timer = new ExecTimer("test_timer1");
        try
        {
            assertTrue(null!=timer);
            timer.stop();
            // timer.logTime();
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
   
    @Test
    public void test_ExecTimer_002()
    {
        IExecTimer timer = new ExecTimer("test_timer2");
        try
        {
            assertTrue(null!=timer);
            // timer.stop();
            timer.logTime();
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_ExecTimer_0022()
    {
        IExecTimer timer = new ExecTimer("test_timer3");
        try {
            timer.start();
            timer.stop();
        } catch (DatabaseOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        boolean isTimesStop = timer.isTimerStop();
        assertTrue(timer !=null);
    }

    @Test
    public void testTTA_BL_STATUSMSG_FUNC_001_02()
    {
        try
        {
            StatusMessage msg = new StatusMessage("");
            assertTrue(null!=msg);
            msg.getMessage();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_Activator()
    {
        try
        {

            BundleContext bundleActivator = org.opengauss.mppdbide.utils.Activator
                    .getContext();
            org.opengauss.mppdbide.utils.Activator act = new org.opengauss.mppdbide.utils.Activator();
            act.start(bundleActivator);
            act.stop(bundleActivator);
        }

        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_Activator_Util()
    {
        try
        {

            BundleContext bundleActivator = Activator.getContext();
            org.opengauss.mppdbide.utils.Activator act = new org.opengauss.mppdbide.utils.Activator();
            act.start(bundleActivator);
            act.stop(bundleActivator);
        }

        catch (Exception e)
        {
           
            fail(e.getMessage());
        }
    }
    @Test
    public void test_ExecTimer_003()
    {
        IExecTimer timer = new ExecTimer("test_timer4");
        try
        {
            assertTrue(null!=timer);
            // timer.stop();
            timer.getElapsedTime();
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
           
        }
    }
    
    @Test
    public void test_ExecTimer_004()
    {
        ExecTimer timer =new ExecTimer("testing");
        timer.start();
        try
        {
            timer.stop();
            long time=timer.getElapsedTimeInMs();
            assertNotNull(timer.getElapsedTimeWithUnits(time));
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
    }

    @Test
    public void test_ExecTimer_005()
    {
        ExecTimer timer = new ExecTimer("testing");
        timer.start();
        String timerStr=timer.getDynamicElapsedTime(true);
        assertNotNull(timerStr);
    }

    @Test
    public void test_ExecTimer_006()
    {
        ExecTimer timer = new ExecTimer("testing");
        String timerStr = timer.getDynamicElapsedTime(true);
        assertEquals("", timerStr);

        timer.start();
        timerStr = timer.getDynamicElapsedTime(false);
        assertEquals("", timerStr);

        timerStr = timer.getDynamicElapsedTime(true);
        assertNotEquals("", timerStr);
    }
    
    @Test
    public void testTTA_BL_DEBUGOBJECT_FUNC_001_0012()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            DebugObjects debugObject = DatabaseUtils.getDebugObjects(database, 1);
            ObjectParameter op = new ObjectParameter();
            op.setType(PARAMETERTYPE.IN);
            op.setDataType("int");
            ObjectParameter op1 = new ObjectParameter();
            op1.setType(PARAMETERTYPE.OUT);
            op1.setDataType("int");
            ObjectParameter[] params = new ObjectParameter[2];
            params[0] = op;
            params[1]=op1;
            debugObject.setObjectParameters(params);
            debugObject.setSourceCode(new SourceCode());
            debugObject.getSourceCode();
            debugObject.generateExecutionTemplate();
            debugObject.setNamespace(database.getNameSpaceById(1));
            debugObject.canSupportDebug();
            debugObject.getObjectBrowserLabel();
            debugObject.getParent();
            debugObject.getPLSourceEditorElmId();
            debugObject.getPLSourceEditorElmTooltip();      
            assertEquals("pg_catalog.auto1", debugObject.getDisplayName());
        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
            fail("Not Excepted to come here");
        }

    }
    @Test
    public void testTTA_BL_DEBUGOBJECT_FUNC_001_002_Exception()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            DebugObjects debugObject = DatabaseUtils.getDebugObjects(database, 1);
            ObjectParameter op = new ObjectParameter();
            op.setType(PARAMETERTYPE.IN);
            op.setDataType("refcursor");
            ObjectParameter op1 = new ObjectParameter();
            op1.setType(PARAMETERTYPE.OUT);
            op1.setDataType("int");
            ObjectParameter[] params = new ObjectParameter[2];
            params[0] = op;
            params[1]=op1;
            debugObject.setObjectParameters(params);
            debugObject.setSourceCode(new SourceCode());
            debugObject.getSourceCode();
            debugObject.generateExecutionTemplate();
           
            fail("Not Excepted to come here");
        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
           assertTrue(true);
        }

    }
}

  