package org.opengauss.mppdbide.test.bl.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.util.HostSpec;

import org.opengauss.mppdbide.adapter.IConnectionDriver;
import org.opengauss.mppdbide.adapter.driver.Gauss200V1R7Driver;
import org.opengauss.mppdbide.adapter.factory.ConnectionDriverFactory;
import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.StmtExecutor;
import org.opengauss.mppdbide.adapter.gauss.StmtExecutor.GetFuncProcResultValueParam;
import org.opengauss.mppdbide.adapter.keywordssyntax.Keywords;
import org.opengauss.mppdbide.adapter.keywordssyntax.KeywordsToTrieConverter;
import org.opengauss.mppdbide.adapter.keywordssyntax.OLAPKeywords;
import org.opengauss.mppdbide.adapter.keywordssyntax.SQLSyntax;
import org.opengauss.mppdbide.bl.contentassist.ContentAssistUtil;
import org.opengauss.mppdbide.bl.contentassist.ContentAssistUtilOLAP;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.AccessMethod;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintType;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseHelper;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseUtils;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.ForeignTable;
import org.opengauss.mppdbide.bl.serverdatacache.IQueryResult;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.NamespaceUtils;
import org.opengauss.mppdbide.bl.serverdatacache.NamespaceUtilsBase;
import org.opengauss.mppdbide.bl.serverdatacache.NotificationData;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.ProfileDiskUtility;
import org.opengauss.mppdbide.bl.serverdatacache.QueryResult;
import org.opengauss.mppdbide.bl.serverdatacache.ResultSetColumn;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.SystemNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.Tablespace;
import org.opengauss.mppdbide.bl.serverdatacache.TablespaceType;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.UserNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.groups.DatabaseObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.helper.SchemaHelper;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.util.BLUtils;
import org.opengauss.mppdbide.mock.bl.BaseConnectionHelper;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils.EXCEPTIONENUM;
import org.opengauss.mppdbide.mock.bl.EncryptionHelper;
import org.opengauss.mppdbide.mock.bl.ExceptionConnection;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.mock.bl.ProfileDiskUtilityHelper;
//import org.opengauss.mppdbide.presentation.exportdata.ImportExportDataCore;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.QueryResultType;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.exceptions.PasswordExpiryException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.files.FileValidationUtils;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.MessageQueue;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class DatabaseTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    ServerConnectionInfo              serverInfo                = null;
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
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        status = new JobCancelStatus();
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getViewMockRS(preparedstatementHandler);
        CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
        CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
        CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
        CommonLLTUtils.datatypes(preparedstatementHandler);
        CommonLLTUtils.preparePartitionConstrainstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionIndexLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.fetchAllSynonyms(preparedstatementHandler);
        CommonLLTUtils.mockCheckDebugSupport(preparedstatementHandler);
        CommonLLTUtils.mockCheckExplainPlanSupport(preparedstatementHandler);
        CommonLLTUtils.fetchTriggerQuery(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status=new JobCancelStatus();
        status.setCancel(false);

        serverInfo = new ServerConnectionInfo();
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
            itr = connProfCache.getServers().iterator();
        }

        connProfCache.closeAllNodes();

    }
    
    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_010_1()
    {
        try
        {

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);

            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            exceptionConnection.setThrowExceptionGetLong(true);

            // database.shallowLoadTables(null);
            // fail("Not expected to come here");
            assertNotNull(exceptionConnection);
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CREATE_DB_FUNC_001_001()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            Server server = database.getServer();
            server.createDatabase("tempdb", "", database, database.getConnectionManager().getFreeConnection());
            server.refresh();

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            assertTrue(1 == database.getOid());
            assertTrue(database.isConnected());
            assertTrue(DatabaseHelper.fetchDBOid(database) == 1);
            assertTrue(DatabaseUtils.getAllDBListInServer(database).size() == 2);

            assertTrue(database.getDebugObjectById(1, 1).getName()
                    .equalsIgnoreCase("auto1"));
            assertEquals(database.getServer().getDisplayName(),
                    "TestConnectionName (:5432)");

        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FUNC_001_001()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            List<Namespace> list = database.getAllNameSpaces();
            String str = null;
            database.getSearchPathHelper().fetchUserSearchPath("public");
            List<String> searchpathList = database.getSearchPathHelper().getSearchPath();
            List<String> namespacelist = new ArrayList<String>();
            for (Namespace nMspace : list)
            {
                str = nMspace.getName();
                namespacelist.add(str);
            }
            assertTrue(!namespacelist.containsAll(searchpathList));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_breakpoint()
    {
        try
        {
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            // connectionInfo.setPasswordRemembered(true);
            connectionInfo.setSavePrdOption(2, true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            Server server = new Server(connectionInfo);

            CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(10, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            server.setHost("abc");
            server.getHost();
            server.setPort(9000);
            server.getPort();
            server.getDbByName("tempdb");
            server.setSavePrdOption(1);
            server.getSavePrdOption();
            database.belongsTo(database, server);
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server1 = new Server(serverInfo);
            Database database1 = new Database(server, 2, "Gauss1");
            assertFalse(database.belongsTo(database1, server1));
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FUNC_001_003()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getTables();
            assertEquals(namespace.getTables().getSize(), 0);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            assertEquals(namespace.getTables().getName(), "Regular Tables");
            assertEquals(namespace.getTables().getSize(), 1);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FUNC_001_005()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            DebugObjectGroup debugObj = (DebugObjectGroup) namespace.getFunctions();
            assertEquals(debugObj.getSize(), 0);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            assertEquals(namespace.getFunctions().getName(),
                    "Functions/Procedures");
            assertEquals(debugObj.getSize(), 2);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FUNC_001_006()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            ViewObjectGroup debugObj = namespace.getViewGroup();
            assertEquals(debugObj.getSize(), 0);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            assertEquals(namespace.getViewGroup().getName(), "Views");
            assertEquals(debugObj.getSize(), 3);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FUNC_001_008()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            SystemNamespace namespace = new SystemNamespace(10, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            UserNamespace namespace1 = new UserNamespace(20, "PUBLIC", database);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) namespace1);
            UserNamespace namespace2 = new UserNamespace(30, "KKK", database);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) namespace2);
            UserNamespace namespace3 = new UserNamespace(40, "KLM", database);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) namespace3);

            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    namespace3, "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            namespace3.addTableToSearchPool(tablemetaData);

            assertEquals(namespace3.getTables().getSize(), 1);
            assertEquals(namespace2.getTables().getSize(), 0);
            assertEquals(namespace2.getTables().getSize(), 0);
            assertEquals(database.getAllNameSpaces().size(), 6);

        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FUNC_001_009()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            UserNamespace namespace1 = new UserNamespace(5, "schema1", database);
            UserNamespace namespace2 = new UserNamespace(6, "schema2", database);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) namespace1);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) namespace2);

            List<Namespace> list = database.getAllNameSpaces();
            String substr = null;
            database.getSearchPathHelper().fetchUserSearchPath("public");
            List<String> namespacelist = new ArrayList<String>();

            for (Namespace substr1 : list)
            {
                substr = substr1.getName();
                namespacelist.add(substr);
            }
            assertEquals(Namespace.getQualifiedObjectName(substr), "pg_catalog");
            assertEquals(Namespace.getQualifiedObjectName(database.getDbName()),
                    "\"Gauss\"");
            assertEquals(database.getServerName(), "TestConnectionName");

        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FUNC_001_010()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils
                    .prepareProxyInfoForSearchPath(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            UserNamespace namespace1 = new UserNamespace(5, "schema1", database);
            UserNamespace namespace2 = new UserNamespace(6, "schema2", database);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) namespace1);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) namespace2);

            assertEquals(database.getAllNameSpaces().size(), 6);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        finally
        {
            preparedstatementHandler.clearPreparedStatements();
        }
    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FUNC_001_011()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils
                    .prepareProxyInfoForSearchPath(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getSearchPathHelper().fetchUserSearchPath("public");

            assertEquals(database.getSearchPathHelper().getSearchPath().get(0), "PUBLIC");
            assertEquals(database.getSearchPathHelper().getSearchPath().get(2), "pg_catalog");
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        finally
        {
            preparedstatementHandler.clearPreparedStatements();
        }
    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FUNC_001_0012()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
          /*  assertEquals(namespace.getTables().getObjectById(1)
                    .getColumnMetaDataList().get(0).getQualifiedObjectName(),
                    "\"ColName\"");*/
            String name = namespace.getTables().getObjectById(1).getName();
            assertEquals("MyTable",name);
                   
            
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FUNC_001_0013()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            UserNamespace namespace = new UserNamespace(1, "my_schema", database);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) namespace);

            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            ((UserNamespace) namespace).drop(database.getConnectionManager().getObjBrowserConn());

            assertEquals(namespace.getTables().getObjectById(1), null);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FUNC_001_0014()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            List<Namespace> list = database.getAllNameSpaces();
            String subStr = null;
            List<String> searchpathList = database.getSearchPathHelper().getSearchPath();
            List<String> namespacelist = new ArrayList<String>();
            for (Namespace substring : list)
            {
                subStr = substring.getName();
                namespacelist.add(subStr);
            }
            assertTrue((namespacelist.size() == searchpathList.size() +1 ));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CREATE_DB_FUNC_001_004()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            server.createDatabase("tempdb", "UTF-8", database, database.getConnectionManager().getFreeConnection());
            server.refresh();
            CommonLLTUtils.updateDataBaseRS(preparedstatementHandler);
            database = server.getDbById(2);
            database.renameDatabase("tempdb2");
            database.renameDatabase("like");
            assertEquals("tempdb2", database.getName());

        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CREATE_DB_FUNC_001_005()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            server.createDatabase("tempdb", "UTF-8", database, database.getConnectionManager().getFreeConnection());
            server.refresh();

            database = server.getDbById(2);
            CommonLLTUtils.updateDataBaseRS(preparedstatementHandler);
            database.renameDatabase("tempdb2");

            assertEquals("tempdb2", database.getName());
            CommonLLTUtils.dropDataBaseRS(preparedstatementHandler);
            database.dropDatabase();

            database = server.getDbById(1);
            assertTrue(DatabaseUtils.getAllDBListInServer(database).size() == 1);

        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }
    

    @Test
    public void testTTA_BL_CREATE_DB_FUNC_001_002()
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

            DebugObjects object = (DebugObjects) database.getDebugObjectById(1, 1);
            CommonLLTUtils.refreshDebugObjectRS2(preparedstatementHandler);
            database.getNameSpaceById(1).refreshDbObject(object);

            // database.refreshDBObjectById(2);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CREATE_DB_FUNC_001_0012()
    {
        try
        {
            String query = "select a.headerlines, a.definition, b.xmin, b.cmin from pg_proc b left join "
                    + "(select * from PG_GET_FUNCTIONDEF(" + 2
                    + ")) a on (1) where b.oid=" + 2;

            StringBuilder strSourcecode = new StringBuilder();

            strSourcecode.append("\"Declare").append("\nc INT = 6;")
                    .append("\nd INT;BEGIN");
            strSourcecode.append("\nc := c+1;")
                    .append("\nc := perform nestedfunc()");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;")
                    .append("\nc := 100;");
            strSourcecode.append("\nd := c + 200;").append("\nreturn d;")
                    .append("\nend;\")");

            MockResultSet indexRS = preparedstatementHandler.createResultSet();
            indexRS.addRow(new Object[] {4, strSourcecode.toString(), 1, 1});

            preparedstatementHandler.prepareResultSet(query, indexRS);
            Database database = connProfCache.getDbForProfileId(profileId);

            DebugObjects object = (DebugObjects) database.getDebugObjectById(2, 1);
            CommonLLTUtils.refreshDebugObjectRS(preparedstatementHandler);
            database.getNameSpaceById(1).refreshDbObject(object);

            // database.refreshDBObjectById(2);

        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }

    @Test
    public void testTTA_BL_CREATE_DB_FUNC_001_002_1()
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

            DebugObjects object = (DebugObjects) database.getDebugObjectById(1, 1);
            CommonLLTUtils.refreshDebugObjectRS2(preparedstatementHandler);
            database.getNameSpaceById(1).refreshDbObject(object);
            database.getNameSpaceById(1).refreshDbObject(object);

            // database.refreshDBObjectById(2);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CREATE_DB_FUNC_001_002_2()
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

            DebugObjects object = (DebugObjects) database.getDebugObjectById(1, 1);
            CommonLLTUtils.refreshDebugObjectRS3(preparedstatementHandler);
            database.getNameSpaceById(1).refreshDbObject(object);

            // database.refreshDBObjectById(2);
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CREATE_DB_FUNC_001_003()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);

            DatabaseUtils.getDebugObjects(database, 2);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CREATE_NAMESPACE_FUNC_002_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            server.createDatabase("tempdb", "UTF-8", database, database.getConnectionManager().getFreeConnection());
            server.refresh();

            DatabaseHelper.createNewSchema("testSchema", database);

            ArrayList<Namespace> namespaces = database.getAllNameSpaces();

            assertEquals(4, namespaces.size());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CREATE_NAMESPACE_FUNC_002_002()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            database.connectToServer();
            DatabaseHelper.createNewSchema("testSchema", database);

            ArrayList<Namespace> namespaces = database.getAllNameSpaces();

            assertEquals(1, namespaces.size());
        }

        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CREATE_NAMESPACE_FUNC_002_003()
    {

        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(false);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            String fetchSYSNAMESPACE                          = "SELECT oid, nspname from pg_namespace where ((oid < 16384 and nspname NOT LIKE 'public') or nspname LIKE 'pg_%') ORDER BY nspname;";
            String shallowDebug="SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, lng.lanname lang FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname in ('plpgsql','sql','c') and pr.pronamespace= 1 ORDER BY objname";

            /*
             * ExceptionConnection exceptionConnection = new
             * ExceptionConnection();
             * exceptionConnection.setNeedExceptioStatement(true);
             * exceptionConnection.setNeedExceptionResultset(true);
             * exceptionConnection.setThrowExceptionNext(true);
             * exceptionConnection
             * .setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
             * exceptionConnection.setExceptionForNextOn("loadview");
             * 
             * getJDBCMockObjectFactory().getMockDriver().setupConnection(
             * exceptionConnection);
             */
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
            MockResultSet sysNameSpace = preparedstatementHandler
                    .createResultSet();
            sysNameSpace.addColumn("oid");
            sysNameSpace.addColumn("nspname");
            sysNameSpace.addRow(new Object[]{100,"cstore"});
            preparedstatementHandler.prepareResultSet(fetchSYSNAMESPACE,sysNameSpace);
            MockResultSet shallowDebugSet = preparedstatementHandler
                    .createResultSet();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addRow(new Object[]{100,"cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore"});
            preparedstatementHandler.prepareResultSet(shallowDebug,shallowDebugSet);
            NamespaceUtils.fetchAllSystemNamespaces(database);
            database.connectToServer();
            DatabaseHelper.createNewSchema("testSchema", database);
            database.getServer().setServerCompatibleToNodeGroup(true);

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            assertEquals(0, namespace.getTypes().getSize());
            assertEquals(0, namespace.getFunctions().getSize());
           // namespace.refreshDatatype(database.getObjBrowserConn());
           // assertTrue(namespace.getTypes().getSize() == 1);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            ArrayList<Namespace> namespaces = database.getAllNameSpaces();

            assertEquals(2, namespaces.size());
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    @Test
    public void testTTA_BL_CREATE_NAMESPACE_FUNC_002_0003()
    {

        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            String fetchSYSNAMESPACE                          = "SELECT oid, nspname from pg_namespace where ((oid < 16384 and nspname NOT LIKE 'public') or nspname LIKE 'pg_%') ORDER BY nspname;";
            String shallowDebug="SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, lng.lanname lang FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname in ('plpgsql','sql') and pr.pronamespace= 1 ORDER BY objname";

            /*
             * ExceptionConnection exceptionConnection = new
             * ExceptionConnection();
             * exceptionConnection.setNeedExceptioStatement(true);
             * exceptionConnection.setNeedExceptionResultset(true);
             * exceptionConnection.setThrowExceptionNext(true);
             * exceptionConnection
             * .setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
             * exceptionConnection.setExceptionForNextOn("loadview");
             * 
             * getJDBCMockObjectFactory().getMockDriver().setupConnection(
             * exceptionConnection);
             */
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
            MockResultSet sysNameSpace = preparedstatementHandler
                    .createResultSet();
            sysNameSpace.addColumn("oid");
            sysNameSpace.addColumn("nspname");
            sysNameSpace.addRow(new Object[]{100,"cstore"});
            preparedstatementHandler.prepareResultSet(fetchSYSNAMESPACE,sysNameSpace);
            MockResultSet shallowDebugSet = preparedstatementHandler
                    .createResultSet();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addColumn();
            shallowDebugSet.addRow(new Object[]{100,"cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore","cstore"});
            preparedstatementHandler.prepareResultSet(shallowDebug,shallowDebugSet);
            NamespaceUtils.fetchAllSystemNamespaces(database);
            database.connectToServer();
            DatabaseHelper.createNewSchema("testSchema", database);
            database.getServer().setServerCompatibleToNodeGroup(true);

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            assertEquals(0, namespace.getTypes().getSize());
            assertEquals(0, namespace.getFunctions().getSize());
           // namespace.refreshDatatype(database.getObjBrowserConn());
           // assertTrue(namespace.getTypes().getSize() == 1);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            ArrayList<Namespace> namespaces = database.getAllNameSpaces();

            assertEquals(3, namespaces.size());
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CREATE_NAMESPACE_FUNC_002_004()
    {
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            /*
             * ExceptionConnection exceptionConnection = new
             * ExceptionConnection();
             * exceptionConnection.setNeedExceptioStatement(true);
             * exceptionConnection.setNeedExceptionResultset(true);
             * exceptionConnection.setThrowExceptionNext(true);
             * exceptionConnection.setSqlState("57P");
             * exceptionConnection.setThrownResultSetNext
             * (EXCEPTIONENUM.EXCEPTION);
             * exceptionConnection.setExceptionForNextOn("loadview");
             * 
             * getJDBCMockObjectFactory().getMockDriver().setupConnection(
             * exceptionConnection);
             */
            CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);

            database.connectToServer();
            DatabaseHelper.createNewSchema("testSchema", database);
            database.getServer().setServerCompatibleToNodeGroup(true);
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            ArrayList<Namespace> namespaces = database.getAllNameSpaces();

            assertEquals(2, namespaces.size());
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DROP_DBG_OBJ_FUNC_002_001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            Server server = database.getServer();
            server.createDatabase("tempdb", "UTF-8",database, database.getConnectionManager().getFreeConnection());
            server.refresh();

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            ((Namespace) ((ServerObject) database.getDebugObjectById(1, 1)).getParent()).dropDbObject(database.getDebugObjectById(1, 1), database.getConnectionManager().getObjBrowserConn());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_FETCH_NAMESPACE_FUNC_002_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            server.createDatabase("tempdb", "UTF-8",database, database.getConnectionManager().getFreeConnection());
            server.refresh();
            CommonLLTUtils.fetchNamespaceRS(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getViewMockRS(preparedstatementHandler);

            DebugObjects debugObject = new DebugObjects(6, "func",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            NamespaceUtilsBase.refreshNamespace(3, false, database);
            assertNotNull(debugObject);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_EXEC_ON_SQL_TERMINAL_FUNC_002_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.runOnSQLTerminalRS(preparedstatementHandler);
            QueryResult result = DatabaseUtils.executeOnSqlTerminal(
                    "select * from tbl1", 1000, database.getConnectionManager().getFreeConnection(),
                    new MessageQueue());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_EXEC_ON_SQL_TERMINAL_FUNC_002_0034()
    {

        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.runOnSQLTerminalRS(preparedstatementHandler);
            IQueryResult result = DatabaseUtils.executeOnSqlTerminal(
                    "select * from tbl1", 1000, database.getConnectionManager().getFreeConnection(),
                    new MessageQueue());

            assertEquals(false, result.isEndOfRecordsReached());
            assertEquals(0, result.getRowsAffected());
            assertEquals(QueryResultType.RESULTTYPE_RESULTSET,
                    result.getReturnType());
            ResultSetColumn[] columns = result.getColumnMetaData();

            assertEquals("id", columns[0].getColumnName());
            assertEquals("name", columns[1].getColumnName());

            assertEquals(2, columns.length);
            result.getNextRecordBatch(0);

            ResultSet rs = result.getResultsSet();

            assertNotNull(rs);

            result.rollback();
            result.closeStament();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    
    }

    @Test
    public void testTTA_BL_EXEC_ON_SQL_TERMINAL_FUNC_002_002()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.runOnSQLTerminalRS(preparedstatementHandler);
            
            
            StmtExecutor stmt = new StmtExecutor("select * from tbl1", database.getConnectionManager().getFreeConnection());
            int fetchSize = 1000;
            stmt.setFetchCount(fetchSize);
            stmt.registerNoticeListner(new MessageQueue());
            stmt.execute();
            IQueryResult result = new QueryResult(stmt);
            
            try
            {
                stmt.getScale(100);
            }
            catch (DatabaseOperationException e)
            {
                assertTrue(true);
            }
            
            try
            {
                stmt.getMaxLength(100);
            }
            catch (DatabaseOperationException e)
            {
                assertTrue(true);
            }
            
            try
            {
                stmt.getPrecision(100);
            }
            catch (DatabaseOperationException e)
            {
                assertTrue(true);
            }
            
            try
            {
                stmt.getColumnTypeName(100);
            }
            catch (DatabaseOperationException e)
            {
                assertTrue(true);
            }
            
            try
            {
                stmt.getColumnName(100);
            }
            catch (DatabaseOperationException e)
            {
                assertTrue(true);
            }
            
            ResultSet rs = result.getResultsSet();
            result.getColumnCommentOfOLAP(database.getConnectionManager().getFreeConnection(), rs.getStatement());

            assertNotNull(rs);

            result.rollback();
            result.closeStament();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_EXEC_ON_SQL_TERMINAL_FUNC_002_003()
    {
        try
        {

            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.runOnSQLTerminalRS(preparedstatementHandler);
            ExceptionConnection exceptionConnection = new ExceptionConnection();

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            DBConnection con = database.getConnectionManager().getFreeConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionCloseStmt(true);
            exceptionConnection.setThrowoutofmemerrorinrs(true);

            IQueryResult result = DatabaseUtils.executeOnSqlTerminal(
                    "select * from tbl1", 1000, con, new MessageQueue());
            assertEquals(false, result.isEndOfRecordsReached());
            assertEquals(0, result.getRowsAffected());
            assertEquals(QueryResultType.RESULTTYPE_RESULTSET,
                    result.getReturnType());
            ResultSetColumn[] columns = result.getColumnMetaData();

            assertEquals("id", columns[0].getColumnName());
            assertEquals("name", columns[1].getColumnName());

            assertEquals(2, columns.length);
            result.getNextRecordBatch(1);

            ResultSet rs = result.getResultsSet();

            assertNotNull(rs);

            result.closeStament();

        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().contains("Out of memory error"));
        }
    }

    @Test
    public void testTTA_BL_GET_OBJECT_TYPE_FUNC_002_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            NamespaceUtilsBase.getDebugObjectTypeByGroupType(OBJECTTYPE.FUNCTION_GROUP);
            NamespaceUtilsBase.getDebugObjectTypeByGroupType(OBJECTTYPE.PROCEDURE_GROUP);
            // database.getDebugObjectTypeByGroupType(OBJECTTYPE.TRIGGER_GROUP);
            NamespaceUtilsBase.getDebugObjectTypeByGroupType(OBJECTTYPE.OBJECTTYPE_BUTT);
            database.getDefaultDatatype();
            database.getORCDatatype();
            database.getExecutor();
            database.getAllNameSpaces().size();
            database.getServer().getServerConnectionInfo(database.getDbName());
            database.getConnectionManager().setObjBrowserConn(database.getConnectionManager().getSqlTerminalConn());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_REFRESH_DBG_OBJ_GRP_FUNC_002_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getNameSpaceById(1).refreshDebugObjectGroup(OBJECTTYPE.PLSQLFUNCTION);
            database.getNameSpaceById(1).refreshDebugObjectGroup(OBJECTTYPE.FUNCTION_GROUP);
            assertEquals(database.getNameSpaceById(1).getFunctions().getSize(),
                    2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_REFRESH_DBG_OBJ_GRP_FUNC_002_0011()
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

            DebugObjects object = (DebugObjects) database.getDebugObjectById(1, 1);
            CommonLLTUtils.refreshDbgObj1(preparedstatementHandler);
            // Failing Test Case On 02-07-2016
            // database.refreshDebugObjectGroup(OBJECTTYPE.FUNCTION_GROUP,
            // database.getNameSpaceById(1));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_REFRESH_NAMESPACE_FUNC_002_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            // database.refreshNamespaces();//Code commented by Arun as per lazy
            // loading changed

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DATABASE_FUNC_002_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getNameSpaceById(12);
            fail("Not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DATABASE_FUNC_002_002()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            database.destroy();
            database.getServer().getServerConnectionInfo(database.getDbName())
                    .setSSLEnabled(true);
            database.getServer().getServerConnectionInfo(database.getDbName())
                    .setRootCertificate("rootCertificatePath");
            database.getServer().getServerConnectionInfo(database.getDbName())
                    .setClientSSLCertificate("clSSLCertificatePath");
            database.getServer().getServerConnectionInfo(database.getDbName())
                    .setClientSSLKey("clSSLKeyPath");
            database.getServer().getServerConnectionInfo(database.getDbName())
                    .setSSLMode("require");
            database.connectToServer();
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            System.out.println("As expected...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DATABASE_FUNC_002_002_1()
    {
        String[] array = {"-loginTimeout=3.4ms", "test2"};
        BLUtils.getInstance().setPlatformArgs(array);
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            database.destroy();
            database.getServer().getServerConnectionInfo(database.getDbName())
                    .setSSLEnabled(true);
            database.getServer().getServerConnectionInfo(database.getDbName())
                    .setRootCertificate("rootCertificatePath");
            database.getServer().getServerConnectionInfo(database.getDbName())
                    .setClientSSLCertificate("clSSLCertificatePath");
            database.getServer().getServerConnectionInfo(database.getDbName())
                    .setClientSSLKey("clSSLKeyPath");
            database.getServer().getServerConnectionInfo(database.getDbName())
                    .setSSLMode("require");
            database.connectToServer();
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            System.out.println("As expected...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DATABASE_FUNC_002_003()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            server.isAleastOneDbConnected();
            database.destroy();
            server.isAleastOneDbConnected();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DATABASE_FUNC_002_004()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            server.getDbByName("db");
            server.getDbByName("Gauss");
        }

        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DATABASE_FUNC_002_005()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            // server.refreshNodes();
            // server.refreshNodeGroups();
            server.getAccessMethod(0);
            server.getAccessMethods();
            server.getAllDatabases();
            server.getName();
            // server.getSortedNodes();
            server.removeFromAccessMethod(0);
            server.setPort(0);
        }

        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /*
     * @Test public void testTTA_BL_DATABASE_FUNC_002_005_1() { try {
     * CommonLLTUtils.getAllNodes(preparedstatementHandler); Database database =
     * connProfCache.getDbForProfileId(profileId); Server server =
     * database.getServer(); server.refreshNodes();
     * 
     * }
     * 
     * catch (Exception e) { e.printStackTrace(); fail(e.getMessage()); } }
     */

    @Test
    public void testTTA_BL_DATABASE_FUNC_002_006()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            database.destroy();
            server.findOneActiveDb();
            fail("not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DATABASE_FUNC_002_007()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            database.destroy();
            server.getAnotherConnection(0);
            fail("not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DATABASE_FUNC_002_008()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
            getdbsrs.addColumn("oid");
            getdbsrs.addColumn("datname");

            getdbsrs.addRow(new Object[] {1, "Gauss"});
            getdbsrs.addRow(new Object[] {2, "MPPDB"});
            preparedstatementHandler.prepareResultSet(
                    "select oid, datname from pg_database where datistemplate='f'",
                    getdbsrs);
            server.refresh();

            getdbsrs = preparedstatementHandler.createResultSet();
            getdbsrs.addColumn("oid");
            getdbsrs.addColumn("datname");
            getdbsrs.addRow(new Object[] {1, "Gauss"});

            preparedstatementHandler.prepareResultSet(
                    "select oid, datname from pg_database where datistemplate='f'",
                    getdbsrs);
            server.refresh();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DATABASE_FUNC_002_009()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            database.dropDatabase();
            fail("Not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected..");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DATABASE_FUNC_002_010()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            database.renameDatabase("all");
            
            fail("Not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected..");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DATABASE_FUNC_002_011()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            // database.renameDatabase("newName");
            Database database2 = new Database(database.getServer(), 2,
                    database.getDbName());
            ConnectionDriverFactory.getInstance().addDriver("Gauss200V1R7Driver", new Gauss200V1R7Driver("newDriver"));
            database2.connectToServer();
            CommonLLTUtils.updateDataBaseRS(preparedstatementHandler);
            database2.renameDatabase("all");
          //  database2.clearAllDebugObjects();
            DatabaseUtils.getDebugObjects(database, 12);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_SERVER_FUNC_002_001()
    {
        try
        {
            // Database database = connProfCache.getDbForProfileId(profileId);
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            connectionInfo.setConectionName("TestConnectionName");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            // serverInfo.setSslPassword("12345");
            // serverInfo.setServerType(DATABASETYPE.GAUSS);

            ConnectionProfileId connectionProfileId = connProfCache
                    .initConnectionProfile(connectionInfo,status);

            connectionInfo.setServerIp("");
            connectionProfileId = connProfCache
                    .initConnectionProfile(connectionInfo, status);

            Thread.sleep(1);

            fail("Not expected to come here." + connectionProfileId);
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected..");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_SERVER_FUNC_002_002()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            connProfCache.destroyConnection(database);
            connProfCache.getDbForProfileId(null);

            ConnectionProfileId connectionProfileId = new ConnectionProfileId(
                    23, 66);
            connProfCache.getDbForProfileId(connectionProfileId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_initConnectionProfile_Sucess_node_clear_RememebrPassword()
    {
        try
        {
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.PERMANENTLY);
            connectionInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            Server node = new Server(connectionInfo);
            Database database = new Database(node, 2, "Gauss");
            ConnectionProfileId id = node
                    .createDBConnectionProfile(connectionInfo, status);
            node.clearPrds();

            IServerConnectionInfo newInfo = node
                    .getServerConnectionInfo("Gauss");
            assertTrue(Arrays.equals(newInfo.getPrd(),
                    "mypassword".toCharArray()));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_initConnectionProfile_Sucess_node_clear_Password_Do_Not_Saved()
    {
        try
        {
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            Server node = new Server(connectionInfo);
            Database database = new Database(node, 2, "Gauss");

            node.clearPrds();

            assertEquals(node.getEncrpytedProfilePrd(), "");
            //assertEquals(node.getEncrpytedsslPrd(), "");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_Save_SeverPassword_permanently_Sucessfully()
    {
        try
        {
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.PERMANENTLY);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            Server node = new Server(connectionInfo);
            Database database = new Database(node, 2, "Gauss");
            SecureUtil sec = new SecureUtil();
            String path = ConnectionProfileManagerImpl.getInstance().getProfilePath(connectionInfo);
            sec.setPackagePath(path);
            String strpsw = sec.encryptPrd("password".toCharArray());
            node.setPrd(strpsw);
            ServerConnectionInfo connectionInfo1 = new ServerConnectionInfo();
            assertEquals(connectionInfo1.getPrd().length, 0);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_Save_SeverPassword_permanently_To_Disk_utility_Sucessfully()
    {
        try
        {
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.PERMANENTLY);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            Server node = new Server(connectionInfo);
            Database database = new Database(node, 2, "Gauss");

            ConnectionProfileManagerImpl.getInstance()
                    .setDiskUtility(new myProfileDiskUtity());
            node.setPrd(connectionInfo.getPrd().toString());
            ServerConnectionInfo connectionInfo1 = new ServerConnectionInfo();
            assertEquals(connectionInfo1.getPrd().length, 0);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private class myProfileDiskUtity extends ProfileDiskUtility
    {

        @Override
        public void writeProfileToDisk(IServerConnectionInfo serverInfo)
                throws DatabaseOperationException, DataStudioSecurityException
        {
            System.out.println("As aspected");
        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_001()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            // database.fetchAllColumnMetaData();//Code commented by Arun as per
            // lazy loading changed
            // fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_002()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            exceptionConnection.setSqlState("57P sql exception");

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            // database.fetchAllColumnMetaData();//Code commented by Arun as per
            // lazy loading changed
            // fail("Not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_113()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionGetLong(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);
            database.connectToServer();
            // database.fetchAllColumnMetaData();//Code commented by Arun as per
            // lazy loading changed
            // fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_005()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            // database.fetchAllNodeGroups();
            // fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    // test case for getFreeConnection
    @Test
    public void testTTA_BL_getFreeConnection_001()
    {
        Database database = null;
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            server.createDatabase("tempdb", "",database, database.getConnectionManager().getFreeConnection());
            server.refresh();
            for (int i = 1; i <= 21; i++)
                database.getConnectionManager().getFreeConnection();

        }
        catch (Exception e)
        {
            System.out.println("value of e " + e.getMessage());
            assertEquals(
                    MessageConfigLoader.getProperty(
                            IMessagesConstants.DATABASE_CONNECTION_LIMIT_REACHED,
                            20,
                            database.getName() + '@'
                                    + database.getServerName()),
                    e.getMessage());

        }
    }

    @Test
    public void testTTA_BL_releaseConnection_001()
    {
        Database database = null;
        DBConnection testconn = null;
        DBConnection testconn1 = null;

        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            testconn = database.getConnectionManager().getFreeConnection();
            testconn1 = database.getConnectionManager().getFreeConnection();

            database.getConnectionManager().releaseConnection(testconn);
            database.getConnectionManager().releaseConnection(testconn1);
            assertNotSame(testconn1, database.getConnectionManager().getFreeConnection());

        }
        catch (Exception e)
        {
            System.out.println("value of e " + e.getMessage());
            assertEquals(
                    MessageConfigLoader.getProperty(
                            IMessagesConstants.DATABASE_CONNECTION_LIMIT_REACHED,
                            20,
                            database.getName() + '@'
                                    + database.getServerName()),
                    e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_releaseConnection_002()
    {
        Database database = null;
        DBConnection testconn = null;
        DBConnection testconn1 = null;

        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            // Set the password save option to current session
            SavePrdOptions savePrdOption = server.getSavePrdOption();
            server.setSavePrdOption(1);
            testconn = database.getConnectionManager().getFreeConnection();
            testconn1 = database.getConnectionManager().getFreeConnection();

            database.getConnectionManager().releaseConnection(testconn);
            database.getConnectionManager().releaseConnection(testconn1);
            assertEquals(testconn, database.getConnectionManager().getFreeConnection());
            server.setSavePrdOption(savePrdOption.ordinal());

        }
        catch (Exception e)
        {
            System.out.println("value of e " + e.getMessage());
            assertEquals(
                    MessageConfigLoader.getProperty(
                            IMessagesConstants.DATABASE_CONNECTION_LIMIT_REACHED,
                            20,
                            database.getName() + '@'
                                    + database.getServerName()),
                    e.getMessage());

        }

    }
    
    @Test
    public void test_releaseConnection_for_DO_NOT_SAVE()
    {
        Database database = null;
        DBConnection testconn = null;

        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            // Set the password save option to current session
            SavePrdOptions savePrdOption = server.getSavePrdOption();
            server.setSavePrdOption(2);
            testconn = database.getConnectionManager().getFreeConnection();

            database.getConnectionManager().releaseConnection(testconn);
            assertNotSame(testconn, database.getConnectionManager().getFreeConnection());
            assertTrue(testconn.isClosed());
            server.setSavePrdOption(savePrdOption.ordinal());

        }
        catch (Exception e)
        {
            System.out.println("value of e " + e.getMessage());
            assertEquals(
                    MessageConfigLoader.getProperty(
                            IMessagesConstants.DATABASE_CONNECTION_LIMIT_REACHED,
                            20,
                            database.getName() + '@'
                                    + database.getServerName()),
                    e.getMessage());

        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_006()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            exceptionConnection.setSqlState("57P sql exception");

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            // database.fetchAllNodeGroups();
            // fail("Not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_007()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            // database.fetchAllNodes();
            // fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_008()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            exceptionConnection.setSqlState("57P sql exception");

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            // database.fetchAllNodes();
            // fail("Not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_009()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            // CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS1(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                    "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1,
                    "Col1", new TypeMetaData(1, "bigint",
                            database.getNameSpaceById(1)));
            newTempColumn.isLoaded();
            tablemetaData.getColumns().addItem(newTempColumn);

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1,
                    "Col2",
                    new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());

            tablemetaData.addIndex(indexMetaData);
            constraintMetaData.isLoaded();
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_010()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            // CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS1(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                    "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1,
                    "Col1", new TypeMetaData(1, "bigint",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1,
                    "Col2",
                    new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());

            tablemetaData.addIndex(indexMetaData);
            // database.fetchTableIndexes(1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_010_4()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS1(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                    "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1,
                    "Col1", new TypeMetaData(1, "bigint",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1,
                    "Col2",
                    new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);

            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);
            // database.fetchTableIndexes(1);
        }

        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_010_2()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.prepareProxyInfoForRs(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS1(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                    "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1,
                    "Col1", new TypeMetaData(1, "bigint",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1,
                    "Col2",
                    new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);

            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);
            // database.fetchTableIndexes(1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_010_3()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS1(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                    "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1,
                    "Col1", new TypeMetaData(1, "bigint",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1,
                    "Col2",
                    new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);
            // database.fetchTableIndexes(1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_011()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            database.fetchAllDatatypes();
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_014_01()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            database.connectToServer();
            database.fetchAllDatatypes();
            assertTrue(namespace.getTypes().getList().size() == 2);

        }
        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_012()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            exceptionConnection.setSqlState("57P sql exception");

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            database.fetchAllDatatypes();
            fail("Not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_013()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            // database.shallowLoadTables(null);
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_014()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            exceptionConnection.setSqlState("57P sql exception");

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            // database.shallowLoadTables(null);
            // fail("Not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_014_03()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
            CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
            // CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            database.getServer().setServerCompatibleToNodeGroup(true);
            database.connectToServer();

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            assertTrue(namespace.getTablesGroup().getSize() == 1);
        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_FUNC_001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
            CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
            // CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            database.getServer().setServerCompatibleToNodeGroup(true);
            database.connectToServer();

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog1", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            assertTrue(namespace.getTablesGroup().getSize() == 1);
        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_015()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            AccessMethod.fetchAllAccessMethods(database);
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_016()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            exceptionConnection.setSqlState("57P sql exception");

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            AccessMethod.fetchAllAccessMethods(database);
            fail("Not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_017()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            database.fetchAllTablespace();
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_017_01()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            Tablespace ts = new Tablespace(1L, "tablespace", "location", "10",
                    new String[] {"options"}, server, TablespaceType.NORMAL,true, false);
            database.fetchTablespaceMetaData(ts.getOid());
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_017_02()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();
            database.fetchSearchPathObjects(false);
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_017_03()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);
            database.connectToServer();
/*            database.getNameSpaceById(1).shallowLoadDebugableObjects(OBJECTTYPE.NAMESPACE,
                    database.getObjBrowserConn(), false);*/
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_018()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            exceptionConnection.setSqlState("57P sql exception");

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            database.fetchAllTablespace();
            fail("Not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {

            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());

        }
    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_034()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);

            preparedstatementHandler
                    .prepareThrowsSQLException("select * from tbl1");

            DebugObjects dbObj = new DebugObjects(2, "name",
                    OBJECTTYPE.PLSQLFUNCTION, database);

            SystemNamespace namespace = new SystemNamespace(1, "namespace", database);
            dbObj.setNamespace(namespace);
            // namespace.addFunction(dbObj);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);

            DatabaseUtils.executeOnSqlTerminal("select * from tbl1", 1000,
                    database.getConnectionManager().getFreeConnection(), new MessageQueue());
            fail("Not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_020()
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

            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            preparedstatementHandler.prepareThrowsSQLException(
                    "DROP FUNCTION pg_catalog.auto1()", sqlException);
            
            ((Namespace) ((ServerObject) database.getDebugObjectById(1, 1)).getParent()).dropDbObject(database.getDebugObjectById(1, 1), database.getConnectionManager().getObjBrowserConn());
            fail("Not expected to come here.");
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_021()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);

            database.getNameSpaceById(1).getDebugObjectGroupByType(OBJECTTYPE.OBJECTTYPE_BUTT);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_GetDebugObject()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObjects = new DebugObjects(6, "func",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            DebugObjects debugObjects1 = new DebugObjects(7, "func",
                    OBJECTTYPE.SQLFUNCTION, database);
            debugObjects.setNamespace(database.getNameSpaceById(1));
            debugObjects1.setNamespace(database.getNameSpaceById(1));

            Namespace ns = database.getNameSpaceById(1);
            ns.addDebugObjectToSearchPool(debugObjects);
            ns.addDebugObjectToSearchPool(debugObjects1);
            // ns.addFunction(debugObjects);
            DebugObjectGroup debugObjectGroupByType = ns
                    .getDebugObjectGroupByType(OBJECTTYPE.FUNCTION_GROUP);

            assertEquals(debugObjectGroupByType.getObjectGroupType(),
                    OBJECTTYPE.FUNCTION_GROUP);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_RefreshDebugObject_1()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.fetchNamespaceRS(preparedstatementHandler);
            CommonLLTUtils.refreshDebugObjectRS3(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObjects1 = new DebugObjects(6, "func",
                    OBJECTTYPE.PLSQLFUNCTION, database);

            Namespace ns = database.getNameSpaceById(1);
            DebugObjectGroup debugObjectGroup = ns
                    .getDebugObjectGroupByType(OBJECTTYPE.PLSQLFUNCTION);

            debugObjects1.setNamespace(ns);

            ns.addDebugObjectToSearchPool(debugObjects1);
            ns.addDebugObjectToSearchPool(debugObjects1);

            ns.refreshDebugObjectGroup();
            int size = ns.getFunctions().getSize();
            assertEquals(2, size);
            ;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_RefreshDebugObject_2()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.fetchNamespaceRS(preparedstatementHandler);
            CommonLLTUtils.refreshDebugObjectRS3(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObjects1 = new DebugObjects(6, "func",
                    OBJECTTYPE.SQLFUNCTION, database);

            Namespace ns = database.getNameSpaceById(1);
            DebugObjectGroup debugObjectGroup = ns
                    .getDebugObjectGroupByType(OBJECTTYPE.SQLFUNCTION);

            debugObjects1.setNamespace(ns);

            ns.addDebugObjectToSearchPool(debugObjects1);
            ns.addDebugObjectToSearchPool(debugObjects1);

            ns.refreshDebugObjectGroup();
            int size = ns.getFunctions().getSize();
            assertEquals(2, size);
            ;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    // fail Scenario
    @Test
    public void testTTA_BL_DB_RefreshDebugObject_3()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.fetchNamespaceRS(preparedstatementHandler);
            CommonLLTUtils.refreshDebugObjectRS3(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObjects1 = new DebugObjects(6, "func",
                    OBJECTTYPE.FUNCTION_GROUP, database);

            Namespace ns = database.getNameSpaceById(1);
            DebugObjectGroup debugObjectGroup = ns
                    .getDebugObjectGroupByType(OBJECTTYPE.FUNCTION_GROUP);

            debugObjects1.setNamespace(ns);

            ns.addDebugObjectToSearchPool(debugObjects1);
            ns.addDebugObjectToSearchPool(debugObjects1);

            ns.refreshDebugObjectGroup();
            int size = ns.getFunctions().getSize();
            assertEquals(2, size);
            ;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    // fail Scenario
    @Test
    public void testTTA_BL_DB_DebugObject_4()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.fetchNamespaceRS(preparedstatementHandler);
            CommonLLTUtils.refreshDebugObjectRS3(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObjects1 = new DebugObjects(6, "func",
                    OBJECTTYPE.TABLEMETADATA, database);

            Namespace ns = database.getNameSpaceById(1);
            DebugObjectGroup debugObjectGroup = ns
                    .getDebugObjectGroupByType(OBJECTTYPE.TABLEMETADATA);

            debugObjects1.setNamespace(ns);

            ns.addDebugObjectToSearchPool(debugObjects1);
            ns.addDebugObjectToSearchPool(debugObjects1);

            assertEquals(null, debugObjectGroup);

        }
        catch (Exception e)
        {

            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    // fail scenario
    @Test
    public void testTTA_BL_DB_RefreshDebugObject_5()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObjects1 = new DebugObjects(7, "func1",
                    OBJECTTYPE.TABLEMETADATA, database);
            debugObjects1.setNamespace(database.getNameSpaceById(1));

            Namespace ns = database.getNameSpaceById(1);
            // ns.addFunction(debugObjects1);
            ns.addDebugObjectToSearchPool(debugObjects1);

            DebugObjectGroup debugObjectGroup = ns
                    .getDebugObjectGroupByType(OBJECTTYPE.TABLEMETADATA);

            assertEquals(null, debugObjectGroup);

        }
        catch (Exception e)
        {

            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    // fail scenario
    @Test
    public void testTTA_BL_DB_RefreshDebugObject_6()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObjects1 = new DebugObjects(7, "func1",
                    OBJECTTYPE.TYPEMETADATA, database);
            debugObjects1.setNamespace(database.getNameSpaceById(1));

            Namespace ns = database.getNameSpaceById(1);
            // ns.addFunction(debugObjects1);
            ns.addDebugObjectToSearchPool(debugObjects1);

            DebugObjectGroup debugObjectGroup = ns
                    .getDebugObjectGroupByType(OBJECTTYPE.TYPEMETADATA);

            assertEquals(null, debugObjectGroup);

        }
        catch (Exception e)
        {

            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    // fail scenario
    @Test
    public void testTTA_BL_DB_RefreshDebugObject_7()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObjects1 = new DebugObjects(7, "func1",
                    OBJECTTYPE.TYPEMETADATA, database);
            debugObjects1.setNamespace(database.getNameSpaceById(1));

            Namespace ns = database.getNameSpaceById(1);

            ns.addDebugObjectToSearchPool(debugObjects1);

            DebugObjectGroup debugObjectGroup = ns
                    .getDebugObjectGroupByType(OBJECTTYPE.PROCEDURE_GROUP);

            assertEquals(null, debugObjectGroup);

        }
        catch (Exception e)
        {

            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_FUNC_DebugObject()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);

            database.getNameSpaceById(1).getDebugObjectGroupByType(OBJECTTYPE.PLSQLFUNCTION);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_022()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();
            DatabaseUtils.getAllDBListInServer(database);
            fail("Not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_023()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            exceptionConnection.setSqlState("57P sql exception");

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();
            DatabaseUtils.getAllDBListInServer(database);
            fail("Not expected to come here.");
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_024()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            database.getSystemNamespaceGroup().addToGroup(new SystemNamespace(1, "pg_catalog", database));

            NamespaceUtilsBase.refreshNamespace(1, false, database);
            fail("Not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_025()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();

            database.getSystemNamespaceGroup().addToGroup(new SystemNamespace(1, "pg_catalog", database));
            DebugObjects debugObject = new DebugObjects(1, "name",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            debugObject.setNamespace(new Namespace(1, "pg_catalog", database));
            database.getNameSpaceById(1).refreshDbObject(debugObject);
            fail("Not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_026()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");

            MockResultSet getnoders = preparedstatementHandler
                    .createResultSet();
            getnoders.addColumn("oid");
            getnoders.addColumn("objname");
            getnoders.addColumn("namespace");
            getnoders.addColumn("ret");
            getnoders.addColumn("alltype");
            getnoders.addColumn("nargs");
            getnoders.addColumn("argtype");
            getnoders.addColumn("argname");
            getnoders.addColumn("argmod");
            getnoders.addColumn("secdef");
            getnoders.addColumn("vola");
            getnoders.addColumn("isstrict");
            getnoders.addColumn("retset");
            getnoders.addColumn("procost");
            getnoders.addColumn("setrows");
            getnoders.addColumn("lang");

            getnoders.addRow(new Object[] {1, "objname", 1, 23456, "{1}",
                    "{argname}", true, "{argname}", "{argmode}", 1, 1, 1, 1, 1,
                    1, "plpgsql"});
            preparedstatementHandler.prepareResultSet(
                    "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, lng.lanname lang FROM pg_proc pr JOIN pg_language lng ON lng.oid=pr.prolang WHERE lng.lanname in ('plpgsql','sql','c)  and oid = 1 and has_function_privilege(pr.oid, 'EXECUTE')",
                    getnoders);

            preparedstatementHandler.prepareThrowsSQLException(
                    "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, lng.lanname lang FROM pg_proc pr JOIN pg_language lng ON lng.oid=pr.prolang WHERE lng.lanname in ('plpgsql','sql','c')  and oid = 1 and has_function_privilege(pr.oid, 'EXECUTE')",
                    sqlException);

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            database.getNameSpaceById(1).refreshDbObject(database.getDebugObjectById(1, 1));
            fail("Not expected to come here.");
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_029()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionGetInt(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();
            // database.refreshNamespaces();//Code commented by Arun as per lazy
            // loading changed
            // fail("Not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_030()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionGetString(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();
            AccessMethod.fetchAllAccessMethods(database);
            fail("Not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_031()
    {
        try
        {

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionGetString(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();
            database.fetchAllTablespace();
            fail("Not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_NODE_EXCEPTIONS_FUNC_001_001()
    {
        try
        {

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionGetString(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);

            database.connectToServer();
            // database.fetchAllNodes();
            // fail("Not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_NODE_EXCEPTIONS_FUNC_001_002()
    {

        CommonLLTUtils.getNotifyInfoRs(preparedstatementHandler);

        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            DatabaseUtils.getNotifyInfo(1000, database);
        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail("Not expected to come here.");

        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("Not expected to come here.");
        }

    }

    @Test
    public void testTTA_BL_NODE_EXCEPTIONS_FUNC_001_013()
    {
        CommonLLTUtils.getDeadLineInfoRs(preparedstatementHandler);
        ExceptionConnection exceptionConnection = new ExceptionConnection();
        exceptionConnection.setThrowExceptionCommit(true);

        getJDBCMockObjectFactory().getMockDriver()
                .setupConnection(exceptionConnection);
        Database database = connProfCache.getDbForProfileId(profileId);

        try
        {
            DatabaseUtils.getDeadlineInfo(1000, database);
            assertEquals(DatabaseUtils.getDeadlineInfo(1000, database), "2Days");
        }
        catch (DatabaseCriticalException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_NODE_EXCEPTIONS_FUNC_001_003()
    {
        CommonLLTUtils.getDeadLineInfoRs(preparedstatementHandler);

        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            DatabaseUtils.getDeadlineInfo(1000, database);
        }
        catch (DatabaseCriticalException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_passwordExpired()
    {
        CommonLLTUtils
                .getDeadLineInfoRsPasswordExpired(preparedstatementHandler);
        try
        {
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("Gaussdba@Mpp".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.PERMANENTLY);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            Server node = new Server(connectionInfo);
            assertTrue(
                    node.getServerConnectionInfo().getConectionName().equalsIgnoreCase("connection_name1"));
            Database database = new Database(node, 2, "Gauss");
            ConnectionProfileId id = node
                    .createDBConnectionProfile(connectionInfo, status);
            fail("Not expected to come here");
        }
        catch (PasswordExpiryException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_versionCheck()
    {
        try
        {
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.PERMANENTLY);
            connectionInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            Server node = new Server(connectionInfo);
            assertTrue(
                    node.getServerConnectionInfo().getConectionName().equalsIgnoreCase("connection_name1"));
            ConnectionProfileId id = node
                    .createDBConnectionProfile(connectionInfo,status);
            assertTrue(node.getDbById(1).getServerVersion().equalsIgnoreCase(
                    "Gauss200 OLAP V100R005C10 build 7123 compiled at 2016-11-11 16:18:35 on x86_64-unknown-linux-gnu, compiled by g++ (SUSE Linux) 4.3.4 [gcc-4_3-branch revision 152973], 64-bit"));
        }
        catch (PasswordExpiryException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DB_EXCEPTIONS_FUNC_001_12()
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

            ((Namespace) ((ServerObject) database.getDebugObjectById(1, 1)).getParent()).dropDbObject(database.getDebugObjectById(1, 1), database.getConnectionManager().getObjBrowserConn());
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    /*
     * @Test public void testTTA_BL_NODEGRP_EXCEPTIONS_FUNC_001_001() { try {
     * 
     * ServerConnectionInfo serverInfo = new ServerConnectionInfo();
     * serverInfo.setConectionName("TestConnectionName");
     * serverInfo.setServerIp(""); serverInfo.setServerPort(5432);
     * serverInfo.setDatabaseName("Gauss");
     * serverInfo.setUsername("myusername");
     * serverInfo.setPassword("mypassword"); Database database = new
     * Database(serverInfo, new Server(serverInfo), 2);
     * 
     * 
     * ExceptionConnection exceptionConnection = new ExceptionConnection();
     * exceptionConnection.setNeedExceptioStatement(true);
     * exceptionConnection.setNeedExceptionResultset(true);
     * exceptionConnection.setThrowExceptionGetString(true);
     * exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);
     * 
     * getJDBCMockObjectFactory().getMockDriver().setupConnection(
     * exceptionConnection);
     * 
     * database.connectToServer(); NodeGroup group = new NodeGroup(1, "name");
     * group.getAllNodes(); database.fetchAllNodeGroups();; fail(
     * "Not expected to come here."); } catch(DatabaseOperationException e) {
     * System.out.println("As expected"); } catch (Exception e) {
     * e.printStackTrace(); fail(e.getMessage()); }
     * 
     * }
     */
    @Test
    public void test_getServerName_01()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server1 = new Server(serverInfo);

            Server server2 = DBConnProfCache.getInstance()
                    .getServerByName(serverInfo.getConectionName());

            assertTrue(server1.getName().equalsIgnoreCase(server2.getName()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getServerName_11()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server1 = new Server(serverInfo);

            Server server2 = DBConnProfCache.getInstance()
                    .getServerByName("Test");
            assertNotNull(server1);
            assertEquals(null, server2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getServerName_02()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionNameInfo");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server1 = new Server(serverInfo);

            /*
             * Server server2 = DBConnProfCache.getInstance().getServerByName(
             * serverInfo.getConectionName());
             * 
             * assertTrue(!(server1.getName().equalsIgnoreCase(server2.getName()
             * ) ));
             */
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getServerByID_01()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server1 = new Server(serverInfo);

            Server server2 = DBConnProfCache.getInstance()
                    .getServerById(profileId.getServerId());

            assertTrue(server1.getName().equalsIgnoreCase(server2.getName()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getServerByID_02()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionNameInfo");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server1 = new Server(serverInfo);

            Server server2 = DBConnProfCache.getInstance()
                    .getServerById(profileId.getServerId());

            assertTrue(!server1.getName().equalsIgnoreCase(server2.getName()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_ClearPassword_Success()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            final Database database = new Database(server, 2, "Gauss");

            Properties properties = new Properties();

            properties.setProperty("user", serverInfo.getDsUsername());
            properties.setProperty("password", new String(serverInfo.getPrd()));
            properties.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            properties.setProperty("characterEncoding", encoding);
            properties.setProperty("ApplicationName", "Data Studio");

            BaseConnectionHelper connectionHelper = new BaseConnectionHelper("",
                    properties,
                    new HostSpec[] {new HostSpec("127.0.0.1", 1111)}, "db",
                    "user", false);
            connectionHelper.setEncoding("");
            
            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(connectionHelper);
            CommonLLTUtils.mockServerEncoding(connectionHelper.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckDebugSupport(connectionHelper.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckExplainPlanSupport(connectionHelper.getPreparedStatementResultSetHandler());
            SecureUtil sec = new SecureUtil();
            String path = ConnectionProfileManagerImpl.getInstance().getProfilePath(serverInfo);
            sec.setPackagePath(path);
            String strpsw = sec.encryptPrd("password".toCharArray());
            database.getServer().setPrd(strpsw);
            database.connectToServer();
            assertTrue(Arrays.equals(database.getServer()
                    .getServerConnectionInfo(database.getName()).getPrd(),
                    new char[0]));

        }
        catch (DatabaseCriticalException e)
        {
            if (!(e.getCause() instanceof IOException))
            {
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_ClearPassword_Failure()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            SecureUtil encryptionDecryption = new SecureUtil();
            EncryptionHelper encryptionHelper = new EncryptionHelper(
                    encryptionDecryption);
            encryptionHelper.setThrowInvalidAlgorithmParameterException(true);
            Field enc = encryptionDecryption.getClass()
                    .getDeclaredField("encryption");
            enc.setAccessible(true);
            enc.set(encryptionDecryption, encryptionHelper);
            enc.setAccessible(false);
            Server server = new Server(serverInfo);
            final Database database = new Database(server, 2, "Gauss");

            Properties properties = new Properties();

            properties.setProperty("user", serverInfo.getDsUsername());
            properties.setProperty("password", new String(serverInfo.getPrd()));
            properties.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            properties.setProperty("characterEncoding", encoding);
            properties.setProperty("ApplicationName", "Data Studio");

            BaseConnectionHelper connectionHelper = new BaseConnectionHelper("",
                    properties,
                    new HostSpec[] {new HostSpec("127.0.0.1", 1111)}, "db",
                    "user", false);
            connectionHelper.setEncoding("");
            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(connectionHelper);
            SecureUtil sec = new SecureUtil();
            String strpsw = sec.encryptPrd("password".toCharArray());
            database.getServer().setPrd(strpsw);
            database.connectToServer();
            assertTrue(Arrays.equals(database.getServer()
                    .getServerConnectionInfo(database.getName()).getPrd(),
                    new char[0]));

        }
        catch (DataStudioSecurityException e)
        {
            System.out.println("As expected");
        }

        catch (DatabaseCriticalException e)
        {
            if (!(e.getCause() instanceof IOException))
            {
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CheckRememberPasswrodPermanently()
    {
        try
        {
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            JobCancelStatus status = new JobCancelStatus();
            status.setCancel(false);
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            connectionInfo.setPrivilegeBasedObAccess(true);
            // connectionInfo.setPasswordRemembered(true);
            connectionInfo.setSavePrdOption(0, true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            Server node = new Server(connectionInfo);
            Database database = new Database(node, 2, "Gauss");
            ConnectionProfileId id = node
                    .createDBConnectionProfile(connectionInfo,status);
            node.clearPrds();

            IServerConnectionInfo newInfo = node
                    .getServerConnectionInfo("Gauss");
            assertEquals(SavePrdOptions.PERMANENTLY,
                    newInfo.getSavePrdOption());
            assertTrue(Arrays.equals("mypassword".toCharArray(),
                    newInfo.getPrd()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CheckRememberPasswrodCurrentSessionOnly()
    {
        try
        {
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            connectionInfo.setPrivilegeBasedObAccess(true);
            // connectionInfo.setPasswordRemembered(true);
            connectionInfo.setSavePrdOption(1, true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            Server node = new Server(connectionInfo);
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            Database database = new Database(node, 2, "Gauss");
            ConnectionProfileId id = node
                    .createDBConnectionProfile(connectionInfo, status);
            node.clearPrds();

            IServerConnectionInfo newInfo = node
                    .getServerConnectionInfo("Gauss");
            assertEquals(SavePrdOptions.CURRENT_SESSION_ONLY,
                    newInfo.getSavePrdOption());
            assertTrue(Arrays.equals("mypassword".toCharArray(),
                    newInfo.getPrd()));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_CheckRememberPasswrdDoNotSave()
    {
        try
        {
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            connectionInfo.setPrivilegeBasedObAccess(true);
            // connectionInfo.setPasswordRemembered(true);
            connectionInfo.setSavePrdOption(2, true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            Server node = new Server(connectionInfo);
            Database database = new Database(node, 2, "Gauss");
            ConnectionProfileId id = node
                    .createDBConnectionProfile(connectionInfo, status);
            node.clearPrds();

            IServerConnectionInfo newInfo = node
                    .getServerConnectionInfo("Gauss");
            assertEquals(SavePrdOptions.DO_NOT_SAVE,
                    newInfo.getSavePrdOption());
            assertEquals(0, newInfo.getPrd().length);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_fetchTablespaceMetaData()
    {
        try
        {   
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            connectionInfo.setSavePrdOption(2, true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            Server server = new Server(connectionInfo);

            CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(10, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            server.setHost("abc");
            server.getHost();
            server.setPort(9000);
            server.getPort();
            server.getDbByName("tempdb");
            server.setSavePrdOption(1);
            server.getSavePrdOption();
            database.fetchTablespaceMetaData(10);
            assertTrue(
                    database.getServer().getTablespaceGroup().getSize() == 2);
           
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_fetchTablespaceMetaData_2()
    {
        try
        {
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            // connectionInfo.setPasswordRemembered(true);
            connectionInfo.setSavePrdOption(2, true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            Server server = new Server(connectionInfo);

            preparedstatementHandler
                    .prepareThrowsSQLException(CommonLLTUtils.TBL_SPC_META);
            CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(10, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            server.setHost("abc");
            server.getHost();
            server.setPort(9000);
            server.getPort();
            server.getDbByName("tempdb");
            server.setSavePrdOption(1);
            server.getSavePrdOption();
            database.fetchTablespaceMetaData(10);
            fail("not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            if (!(e.getCause() instanceof SQLException))
            {
                fail("not expected to come here.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

   
    @Test
    public void test_Databasecon_isclosed_01()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getConnectionManager().getFreeConnection().isClosed();
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("As expected");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testsetsave()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.PERMANENTLY);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            server.setSavePrdOption(0);
            assertTrue(
                    server.getSavePrdOption() == SavePrdOptions.PERMANENTLY);
            server.setSavePrdOption(100);
            assertTrue(
                    server.getSavePrdOption() == SavePrdOptions.DO_NOT_SAVE);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testsetsave_01()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.CURRENT_SESSION_ONLY);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            server.setSavePrdOption(0);
            assertTrue(
                    server.getSavePrdOption() == SavePrdOptions.PERMANENTLY);
            server.setSavePrdOption(100);
            assertTrue(
                    server.getSavePrdOption() == SavePrdOptions.DO_NOT_SAVE);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

 
    @Test
    public void test_Function_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            database.setLoadingNamespaceInProgress(false);

            assertFalse(database.isLoadingNamespaceInProgress());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FUNC_001_012()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);

            database.getSearchPathHelper().fetchUserSearchPath("abc");

            assertEquals(true, database.getSearchPathHelper().getSearchPath().contains("abc"));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not expected to come here");
        }
    }

    @Test
    public void testTTA_BL_EXEC_ON_SQL_TERMINAL_FUNC_002_005()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.runOnSQLTerminalRS(preparedstatementHandler);
            // database.getFreeConnection().disconnect();
            DBConnection con = database.getConnectionManager().getFreeConnection();
            con.disconnect();
            QueryResult result = DatabaseUtils.executeOnSqlTerminal(
                    "select * from tbl1", 1000, con, new MessageQueue());
            assertEquals(null, result);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_EXEC_ON_SQL_TERMINAL_FUNC_002_0051()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.runOnSQLTerminalRS(preparedstatementHandler);
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionGetAutoCommitTrue(true);
            exceptionConnection.setSqlException(new SQLException());
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            exceptionConnection.setSqlState("57P sql exception");

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);
            DBConnection con = database.getConnectionManager().getFreeConnection();
            QueryResult result = DatabaseUtils.executeOnSqlTerminal(
                    "select * from tbl1", 1000, con, new MessageQueue());
            fail("Not Expected to come here");

        }
        catch (Exception e)
        {
            assertTrue(true);
            
        }
    }


    /**
     * 
     */
    @Test
    public void testTTA_BL_SEARCH_PATH_FAILURE()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            MockResultSet searchpath = preparedstatementHandler
                    .createResultSet();
            searchpath.addColumn("search_path");

            searchpath.addRow(new Object[] {null});
            preparedstatementHandler.prepareResultSet("SHOW search_path",
                    searchpath);

            database.connectToServer();

            database.getSearchPathHelper().fetchUserSearchPath("public");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FAILURE1()
    {

        ExceptionConnection connection = new ExceptionConnection();
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        connection.setNeedExceptioStatement(true);
        connection.setNeedExceptionResultset(true);
        connection.setThrownResultSetNext(EXCEPTIONENUM.YES);
        connection.setThrowExceptionGetString(true);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            MockResultSet searchpath = preparedstatementHandler
                    .createResultSet();
            searchpath.addColumn("search_path");

            searchpath.addRow(new Object[] {""});
            preparedstatementHandler.prepareResultSet("SHOW search_path",
                    searchpath);
            // preparedstatementHandler.prepareThrowsSQLException("SHOW
            // search_path",new
            // SQLException());
            database.connectToServer();

            database.getSearchPathHelper().fetchUserSearchPath("public");
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            assert (true);

        }
        catch (DataStudioSecurityException e)
        {
            fail("Not expected to come here");
        }
        catch (MPPDBIDEException e)
        {
            fail("Not expected to come here");
        } catch (IOException e) {
            fail("Not expected to come here");
        }
    }

    @Test
    public void testTTA_BL_SEARCH_PATH_FAILURE2()
    {

        ExceptionConnection connection = new ExceptionConnection();
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        connection.setNeedExceptioStatement(true);
        connection.setNeedExceptionResultset(true);
        connection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
        connection.setSqlState("57P");
        connection.setThrowExceptionGetString(true);

        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            MockResultSet searchpath = preparedstatementHandler
                    .createResultSet();
            searchpath.addColumn("search_path");

            searchpath.addRow(new Object[] {""});
            preparedstatementHandler.prepareResultSet("SHOW search_path",
                    searchpath);
            // preparedstatementHandler.prepareThrowsSQLException("SHOW
            // search_path",new
            // SQLException());
            database.connectToServer();

            database.getSearchPathHelper().fetchUserSearchPath("public");
            fail("Not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            assert (true);

        }
        catch (DataStudioSecurityException e)
        {
            fail("Not expected to come here");
        }
        catch (MPPDBIDEException e)
        {
            fail("Not expected to come here");
        } 
        catch (IOException e) 
        {
            fail("Not expected to come here");
        }
    }

    @Test
    public void testTTA_isDatabaseRefreshinProgress_1()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);

            database.getServer().setServerInProgress(true);
            assertEquals(database.getServer().isServerInProgress(), true);
            assertEquals(database.getServer().isDatabaseRefreshinProgress(),
                    false);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getLastSuccessfullLogin()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.notificationResultSet(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            NotificationData data = database.getLoginNotifyManager().getLastSuccessfullLogin();
            database.getConnectionManager().getSuccessfullLogin().disconnect();
            assertEquals("[unknown]@10.18.214.64", data.getClientInfo());
            Timestamp timestamp = CommonLLTUtils.TIMESTAMP;
            String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                    .format(timestamp);
            assertEquals(time, data.getTime());

        }
        catch (Exception e)
        {
            fail("not expected here");
        }
    }

    @Test
    public void test_getLastSuccessfullLoginException()
    {

        CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
        CommonLLTUtils.notificationResultSet(preparedstatementHandler);
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            preparedstatementHandler.prepareThrowsSQLException(
                    "SELECT logintime, client_conninfo from login_audit_messages_pid(true)");
            database.connectToServer();
            database.getConnectionManager().getSuccessfullLogin().setDriver(new IConnectionDriver()
            {
                
                @Override
                public String getToolPath(String toolName)
                {
                    // TODO Auto-generated method stub
                    return null;
                }
                
             
                @Override
                public Driver getJDBCDriver()
                {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public Properties getDriverSpecificProperties()
                {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public String getDriverName()
                {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public String extractErrCodeAdErrMsgFrmServErr(SQLException e)
                {
                    // TODO Auto-generated method stub
                    return null;
                }

				@Override
				public Keywords getKeywordList() {
					// TODO Auto-generated method stub
					return new OLAPKeywords();
				}

                @Override
                public SQLSyntax loadSQLSyntax()
                {
                    // TODO Auto-generated method stub
                    return null;
                }

            });
            NotificationData data = database.getLoginNotifyManager().getLastSuccessfullLogin();
           assertNull(data);
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (MPPDBIDEException e)
        {
            fail();
        }
        catch (IOException e) 
        {
            fail("Not expected to come here");
        }

    }

    @Test
    public void test_getFailureLoginAttempts()
    {
        CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
        CommonLLTUtils.notificationResultSetFailure(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);

        List<NotificationData> notifyList = new ArrayList<>();
        try
        {
            notifyList = database.getLoginNotifyManager()
                    .getFailureLoginAttempts();
            assertEquals(2, notifyList.size());

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }

    }
    
    @Test
    public void test_cancelCreateDBOperation()
    {
        CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {

            Server server = database.getServer();
            server.createDatabase("tempdb", "", database,
                    database.getConnectionManager().getObjBrowserConn());
            server.refresh();
            status.setCancel(true);

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(), status);

            fail("not expected");

          

        }
        catch (DatabaseOperationException e )
        {
           assertTrue(true);
            
        }
        catch(DatabaseCriticalException e)
        {
            fail("not expected");
        }
        
        catch(Exception e)
        {
            fail("not expected");
        }
    }
    
    @Test
    public void test_cancelCreateDBOperationException()
    {
        CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            Server server = database.getServer();
            server.createDatabase("tempdb", "", database, null);
            server.refresh();
            status.setCancel(true);

            Namespace namespace = new Namespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            database.getConnectionManager().setObjBrowserConn(null);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(), status);

            fail("not expected");
        }
        catch (DatabaseOperationException e )
        {
            fail("not expected");
            
        }
        catch(DatabaseCriticalException e)
        {
            fail("not expected");
        }
        
        catch(Exception e)
        {
            assertTrue(true);
        }
    }
    
    @Test
    public void test_getObjectBrowserLabel()
    {
        CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.preparePartitionConstrainstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionIndexLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionstLoadLevel(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
        try
        {

            Server server = database.getServer();
            server.createDatabase("tempdb", "", database, database.getConnectionManager().getObjBrowserConn());
            server.refresh();
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(), status);
            namespace.getParent();
            namespace.setLoaded();
            namespace.getObjectBrowserLabel();

            assertEquals("pg_catalog (11) ", namespace.getObjectBrowserLabel());

          

        }
     
        catch(Exception e)
        {
            fail("not expected");
        }
    }
    
    @Test
    public void test_Parent()
    {
        CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
        try
        {

            Server server = database.getServer();
            server.createDatabase("tempdb", "", database,  database.getConnectionManager().getObjBrowserConn());
            server.refresh();
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(), status);
            namespace.setLoaded();

            assertEquals("pg_catalog (11) ", namespace.getObjectBrowserLabel());

          

        }
     
        catch(Exception e)
        {
            fail("not expected");
        }
    }
    
  
    @Test
    public void test_CancelLastSuccessfullLogin()
    {
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.notificationResultSet(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            server.createDatabase("postgres", "", database,  database.getConnectionManager().getObjBrowserConn());
            status.setCancel(true);
            DatabaseUtils.checkCancelStatusAndAbort(status, database);
          
            fail("not expected here");

        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected here");
        }
        catch (Exception e)
        {
            fail("not expected here");
        }
    }
    
   
    @Test
    public void testTTA_BL_DB_getAllTablesForNamespace()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace ns = database.getNameSpaceById(1);
            TableMetaData tbData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            PartitionTable partitionTable = new PartitionTable(ns);

            ForeignTable foreignTable = new ForeignTable(ns,
                    OBJECTTYPE.FOREIGN_TABLE);
            ns.addTableToSearchPool(tbData);
            ns.addTableToSearchPool(partitionTable);
            ns.addTableToSearchPool(foreignTable);
            database.getChildren();
            assertEquals(3, ns.getAllTablesForNamespace().size());

        }
        catch (Exception e)
        {

            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    
    @Test
    public void test_checkForInvalidNamespacesInIncludeList()
    {
        try
        {

            ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
            serverInfonew.setConectionName("newConnection");
            serverInfonew.setServerIp("");
            serverInfonew.setServerPort(5432);
            serverInfonew.setDatabaseName("Gauss");
            serverInfonew.setUsername("myusername");
            serverInfonew.setPrd("mypassword".toCharArray());
            serverInfonew.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);

            Set<String> schemaExclusionList = new HashSet<String>(
                    MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
            serverInfonew.setSchemaInclusionList(schemaExclusionList);
            schemaExclusionList.add("postgres");
            schemaExclusionList.add("db1");

            serverInfonew.getSchemaExclusionList().size();

            assertEquals(2, serverInfonew.getSchemaInclusionList().size());

            Set<String> schemaInclusionList = new HashSet<String>(
                    MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
            serverInfonew.setSchemaInclusionList(schemaInclusionList);
            schemaInclusionList.add("postgres");
            schemaInclusionList.add("db1");
            schemaInclusionList.add("db2");
    

            for (int i = 1; i <= schemaInclusionList.size(); i++)
            {
              
                serverInfonew.getSchemaInclusionList()
                        .removeAll(serverInfonew.getSchemaExclusionList());
            }
            assertEquals(3, serverInfonew.getSchemaInclusionList().size());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getDBDefaultTableSpace()
    {
        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        try {
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        } catch (FileOperationException | DataStudioSecurityException | DatabaseOperationException | IOException e1) {
            fail("not expected to come here");
        }
        Server server = null;
        try
        {
            server = new Server(serverInfo);
        }
        catch (DataStudioSecurityException e)
        {
            fail("not expected to come here");
        }
        Database database = new Database(server, 2, "Gauss");
        database.getDBDefaultTblSpc();
        assertEquals(database.getDBDefaultTblSpc(), null);
    }

    @Test
    public void test_findMatchingChildObjects()
    {
        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        try {
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        } catch (FileOperationException | DataStudioSecurityException | DatabaseOperationException | IOException e1) {
            fail("not expected to come here");
        }
        Server server = null;
        try
        {
            server = new Server(serverInfo);
        }
        catch (DataStudioSecurityException e)
        {
            fail("not expected to come here");
        }
        Database database = new Database(server, 2, "Gauss");
        ContentAssistUtil contUtil = new ContentAssistUtilOLAP(database);
        
        SortedMap<String, ServerObject> retObj = new TreeMap<String, ServerObject>();
        retObj.putAll(contUtil.findExactMatchingNamespaces("name"));
        retObj.putAll(contUtil.findExactMatchingTables("name"));
        retObj.putAll(contUtil.findExactMatchingDebugObjects("name"));
        retObj.putAll(contUtil.findExactMatchingViews("name"));
        retObj.putAll(contUtil.findExactMatchingSequences("name"));

        assertNotNull(retObj);
        assertNotNull(database.getDatabase());

    }

    @Test
    public void test_removeFromSearchPath()
    {
        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        try {
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        } catch (FileOperationException | DataStudioSecurityException | DatabaseOperationException | IOException e1) {
            fail("not expected to come here");
        }
        Server server = null;
        SystemNamespace ns = null;
        try
        {
            server = new Server(serverInfo);
        }
        catch (DataStudioSecurityException e)
        {
            fail("not expected to come here");
        }
        Database database = new Database(server, 2, "Gauss");
        try
        {
            ns = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) ns);
            ns = (SystemNamespace) database.getNameSpaceById(1);
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected to come here");
        }
        database.getSearchPathHelper().removeFromSearchPath(ns.getName());
        assertEquals(ns.getName(),"pg_catalog");
    }

    @Test
    public void test_getDisplayLabel()
    {
        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        try {
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        } catch (FileOperationException | DataStudioSecurityException | DatabaseOperationException | IOException e1) {
            fail("not expected to come here");
        }
        Server server = null;

        try
        {
            server = new Server(serverInfo);
        }
        catch (DataStudioSecurityException e)
        {
            fail("not expected to come here");
        }
        Database database = new Database(server, 2, "Gauss");
        database.getDisplayLabel();
        assertNotNull(database.getDisplayLabel());
    }

    @Test
    public void test_removeObjectFromSearchPool()
    {

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        try {
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        } catch (FileOperationException | DataStudioSecurityException | DatabaseOperationException | IOException e1) {
            fail("not expected to come here");
        }
        Server server = null;

        try
        {
            server = new Server(serverInfo);
        }
        catch (DataStudioSecurityException e)
        {
            fail("not expected to come here");
        }
        Database database = new Database(server, 2, "Gauss");
        SystemNamespace ns = new SystemNamespace(1, "pg_catalog", database);
        database.getSystemNamespaceGroup().addToGroup((SystemNamespace) ns);
        TableMetaData tbData = null;
        PartitionTable partitionTable = new PartitionTable(ns);

        ForeignTable foreignTable = new ForeignTable(ns,
                OBJECTTYPE.FOREIGN_TABLE);

        try
        {
            tbData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected to come here");
        }
        ns.addTableToSearchPool(tbData);
        ns.addTableToSearchPool(partitionTable);
        ns.addTableToSearchPool(foreignTable);

        assertEquals(ns.getTables().getSize(), 3);

        database.getSearchPoolManager().removeObjectFromSearchPool("Table1", OBJECTTYPE.TABLEMETADATA);
        database.getSearchPoolManager().removeObjectFromSearchPool("Table1",
                OBJECTTYPE.PARTITION_TABLE);
        database.getSearchPoolManager().removeObjectFromSearchPool("Table1", OBJECTTYPE.FOREIGN_TABLE);

        String include = SchemaHelper.checkForInvalidNamespacesInExcludeList(database);
        String exclude = SchemaHelper.checkForInvalidNamespacesInIncludeList(database);

        assertNull(include);
        assertNull(exclude);
    }
    
    @Test
    public void testTTA_BL_DB_OLAPKeywords_Test12() {
        try {

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            DatabaseObjectGroup obj = new DatabaseObjectGroup(OBJECTTYPE.DATABASE_GROUP, server);
            obj.setLoadingDatabaseGroupInProgress(true);
            assertTrue(obj.isLoadingDatabaseGroupInProgress());
            assertNotNull(obj.getServer());
            assertNotNull(obj.hashCode());

            obj.equals(server.getDatabaseGroup());
            Database database = new Database(server, 2, "Gauss");

            database.connectToServer();
            assertEquals(new OLAPKeywords(), database.getConnectionManager().getConnectionDriver().getKeywordList());

        } catch (DatabaseOperationException e) {
            System.out.println("as expected");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    
    @Test
    public void testTTA_BL_DATABASE_FUNC_BatchDrop_001()
    {

        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);

            database.connectToServer();
            DatabaseHelper.createNewSchema("testSchema", database);
            database.getServer().setServerCompatibleToNodeGroup(true);

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            
            UserNamespace namespace1 = new UserNamespace(2, "testschema", database);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) namespace1);

            namespace.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            ArrayList<Namespace> namespaces = database.getAllNameSpaces();

            assertEquals(3, namespaces.size());
            
            assertEquals("Schema", namespace.getObjectTypeName());
            assertEquals("pg_catalog", namespace.getObjectFullName());
            String dropQry = namespace.getDropQuery(false);
            assertEquals("DROP SCHEMA IF EXISTS pg_catalog", dropQry);
            
            dropQry = namespace.getDropQuery(true);
            assertEquals("DROP SCHEMA IF EXISTS pg_catalog CASCADE", dropQry);
            
            assertEquals(namespace1.isDropAllowed(), true);
            assertEquals("Schema", namespace1.getObjectTypeName());
            assertEquals("testschema", namespace1.getObjectFullName());
            String dropQry1 = namespace1.getDropQuery(false);
            assertEquals("DROP SCHEMA IF EXISTS testschema", dropQry1);
            
            dropQry1 = namespace1.getDropQuery(true);
            assertEquals("DROP SCHEMA IF EXISTS testschema CASCADE", dropQry1);
            
            // Remove of User and System Namespace
            List<SystemNamespace> list = database.getAllSystemNameSpaces();
            assertEquals(list.size(), 1);

            database.remove(namespace);
            list = database.getAllSystemNameSpaces();
            assertEquals(list.size(), 0);
            
            List<UserNamespace> list1 = database.getAllUserNameSpaces();
            assertEquals(list1.size(), 2);

            database.remove(namespace1);
            list1 = database.getAllUserNameSpaces();
            assertEquals(list1.size(), 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_FUNC_002()
    {

        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);

            database.connectToServer();
            DatabaseHelper.createNewSchema("testSchema", database);
            database.getServer().setServerCompatibleToNodeGroup(true);

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            namespace.getTypes().addItem(new TypeMetaData(12, "integer", namespace));
            TypeMetaData tmd = namespace.getTypeByOid(12);
            assertEquals(tmd.getOid(), 12);
            assertEquals(tmd.getName(), "integer");
            assertEquals(null,  namespace.getTypeByOid(20));
            
            
            MockResultSet getVersionResult = preparedstatementHandler
                    .createResultSet();
            getVersionResult.addColumn("boolean");
            getVersionResult.addRow(new Object[] {
                   true});
            preparedstatementHandler.prepareResultSet(
                    "select has_database_privilege('Gauss', 'CONNECT');", getVersionResult);
            assertTrue(DatabaseHelper.canBeConnected(database));
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionGetBoolean(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver()
                    .setupConnection(exceptionConnection);
            DatabaseHelper.canBeConnected(database);
            
        }catch( DatabaseOperationException ex) {
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_DB_OLAPKeywords_Test()
    {
        try
        {

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            database.connectToServer();
            assertEquals(new OLAPKeywords(),database.getConnectionManager().getConnectionDriver().getKeywordList());

        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }
    @Test
    public void testTTA_BL_DB_getDefaultSchemaName_Test()
    {
        try
        {

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            database.isWordBreakSpecialChar('*');
            database.setLoadingNamespaceInProgress(true);
            database.setLoadingSystemNamespaceInProgress(false);
            database.setLoadingUserNamespaceInProgress(false);
            database.connectToServer();
            database.getDefaultSchemaName(database.getConnectionManager().getFreeConnection());
            database.setClientSSLPrivateKey("");
           assertTrue(!database.isLoadingNamespaceInProgress());
           assertTrue(!database.isLoadingSystemNamespaceInProgress());
           assertTrue(!database.isLoadingUserNamespaceInProgress());
           assertTrue(database.isSupportedExplainPlan());
           assertTrue(!database.isShowDDLSupportByServer());
           assertTrue(database.hasSupportForAtomicDDL());
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
           // fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_DB_OLAPKeywords_SQLSyntaxTrie_Test()
    {
        try
        {

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            database.connectToServer();
            try {
                CommonLLTUtils.setConnectionManagerConnectionDriver(new IConnectionDriver()
                { 
                    private SQLSyntax sqlSyntax;
                    private Keywords keywords;

                    @Override
                    public String getToolPath(String toolName)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }


                    @Override
                    public Driver getJDBCDriver()
                    {
                        return new org.postgresql.Driver();
                    }

                    @Override
                    public Properties getDriverSpecificProperties()
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public String getDriverName()
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public String extractErrCodeAdErrMsgFrmServErr(SQLException e)
                    {
                        return "";
                    }

                    @Override
                    public Keywords getKeywordList() {
                        // TODO Auto-generated method stub
                        return new OLAPKeywords();
                    }

                    @Override
                    public SQLSyntax loadSQLSyntax()
                    {
                        keywords = loadKeyWords();
                        if(sqlSyntax == null){
                        sqlSyntax = new SQLSyntax();
                        sqlSyntax = KeywordsToTrieConverter.convertKeywordstoTrie(sqlSyntax, keywords);
                        }
                        return sqlSyntax;
                    }


                    
                    public Keywords loadKeyWords() {
                        return getKeywordList();
                    }
                },database);
            } catch (Exception e) {
                System.out.println("not expected to come here");
            }
            assertEquals(new OLAPKeywords(),database.getConnectionManager().getConnectionDriver().getKeywordList());
            assertNotNull(database.getSqlSyntax());
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_DB_SQLSyntaxTrie_Load_Test()
    {
        try
        {

            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            database.connectToServer();
            try {
                CommonLLTUtils.setConnectionManagerConnectionDriver(new IConnectionDriver()
                { 
                    private SQLSyntax sqlSyntax;
                    private Keywords keywords;

                    @Override
                    public String getToolPath(String toolName)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }


                    @Override
                    public Driver getJDBCDriver()
                    {
                        return new org.postgresql.Driver();
                    }

                    @Override
                    public Properties getDriverSpecificProperties()
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public String getDriverName()
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public String extractErrCodeAdErrMsgFrmServErr(SQLException e)
                    {
                        return "";
                    }

                    @Override
                    public Keywords getKeywordList() {
                        // TODO Auto-generated method stub
                        return new OLAPKeywords();
                    }

                    @Override
                    public SQLSyntax loadSQLSyntax()
                    {
                        keywords = loadKeyWords();
                        if(sqlSyntax == null){
                        sqlSyntax = new SQLSyntax();
                        sqlSyntax = KeywordsToTrieConverter.convertKeywordstoTrie(sqlSyntax, keywords);
                        }
                        return sqlSyntax;
                    }

                    
                    public Keywords loadKeyWords() {
                        return getKeywordList();
                    }
                },database);
            } catch (Exception e) {
                System.out.println("not expected to come here");
            }
            assertEquals(new OLAPKeywords(),database.getConnectionManager().getConnectionDriver().getKeywordList());
            assertTrue(database.getSqlSyntax().getConstants().size()>0);
            assertTrue(database.getSqlSyntax().getPredicates().size()>0);
            assertTrue(database.getSqlSyntax().getReservedkrywords().size()>0);
            assertTrue(database.getSqlSyntax().getTypes().size()>0);
            assertTrue(database.getSqlSyntax().getUnreservedkrywords().size()>0);
            database.getSqlSyntax().clear();
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            System.out.println("as expected");
        }
    }
    
    @Test
    public void test_StmtExecutor_GetFuncProcResultValueParam()
    {
        try
        {
            GetFuncProcResultValueParam param = new GetFuncProcResultValueParam(0, false, false);
            param.setColumnCount(0);
            param.setCallableStmt(true);
            param.setInputParaVisited(true);
            assertEquals(param.getColumnCount(), 0);
            assertTrue(param.isCallableStmt());
            assertTrue(param.isInputParaVisited());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_fileValidationUtilTest() {
    	String fileName = "testfile";
    	assertEquals(true, FileValidationUtils.validateFileName(fileName));
    }
    
    @Test
    public void test_filePathValidationTest() {
    	String fileName = "D:\\testfile";
    	assertEquals(true, FileValidationUtils.validateFilePathName(fileName));
    }
}
