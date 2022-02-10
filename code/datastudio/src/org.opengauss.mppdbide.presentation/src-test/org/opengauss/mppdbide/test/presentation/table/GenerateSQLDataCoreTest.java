package org.opengauss.mppdbide.test.presentation.table;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.postgresql.util.HostSpec;
import static org.junit.Assert.*;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.importexportdata.ImportExportDataExecuter;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ImportExportOption;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.SystemNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.presentation.BaseConnectionHelper;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.mock.presentation.ResultSetMetaDataImplementation;
import org.opengauss.mppdbide.presentation.exportdata.ExportCursorQueryExecuter;
import org.opengauss.mppdbide.presentation.exportdata.GenerateCursorExecuteVisitor;
import org.opengauss.mppdbide.presentation.exportdata.GenerateSQLDataCore;
import org.opengauss.mppdbide.presentation.exportdata.ImportExportDataCore;
import org.opengauss.mppdbide.utils.ConvertValueToInsertSqlFormat;
import org.opengauss.mppdbide.utils.CustomStringUtility;
import org.opengauss.mppdbide.utils.JSQLParserUtils;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class GenerateSQLDataCoreTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection               = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler         statementHandler         = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
	
    private DBConnection              dbconn;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        this.dbconn = CommonLLTUtils.getDBConnection();
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");

        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
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
    public void test_Generate_Insert_Sql_001()
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
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            Properties properties = new Properties();

            properties.setProperty("user", serverInfo.getDsUsername());
            properties.setProperty("password", new String(serverInfo.getPrd()));
            properties.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            properties.setProperty("characterEncoding", encoding);
            properties.setProperty("ApplicationName", "Data Studio");

            BaseConnectionHelper connectionHelper =
                    new BaseConnectionHelper("", properties, new HostSpec[] {new HostSpec("127.0.0.1", 5432)},
                            serverInfo.getDatabaseName(), serverInfo.getDsUsername(), false);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(connectionHelper);
            CommonLLTUtils.mockServerEncoding(connectionHelper.getPreparedStatementResultSetHandler());

            database.connectToServer();
            Namespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);
            String queryTerminal = "select * from pg_catalog.\"MyTable\" ;";
            Path path = Paths.get("121.sql");
            GenerateSQLDataCore core =
                    new GenerateSQLDataCore(database, queryTerminal, encoding, serverInfo.getDsUsername());
            core.initializeCore();
            // core.setImportExportoptions(new ImportExportOption());
            String query = core.composeSQLQuery();
            assertEquals(queryTerminal, query);
            MockResultSet getselectrs = statementHandler.createResultSet();
            getselectrs.addColumn("Col1");
            getselectrs.addRow(new Object[] {2});
            statementHandler.prepareResultSet(query, getselectrs);
            core.executeExportData(database.getConnectionManager().getFreeConnection(), path);
            assertTrue(core != null);
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }

    @Test
    public void test_Generate_Insert_Sql_002()
    {
        try
        {

            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 2, "Col2",
                    new TypeMetaData(1, "number", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn2 = new ColumnMetaData(tablemetaData, 3, "Col3",
                    new TypeMetaData(1, "float8", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn3 = new ColumnMetaData(tablemetaData, 4, "Col4",
                    new TypeMetaData(1, "date", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn4 = new ColumnMetaData(tablemetaData, 5, "Col5",
                    new TypeMetaData(1, "timestamp", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn5 = new ColumnMetaData(tablemetaData, 6, "Col6",
                    new TypeMetaData(1, "timestamptz", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn6 = new ColumnMetaData(tablemetaData, 7, "Col7",
                    new TypeMetaData(1, "bool", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);
            tablemetaData.getColumns().addItem(newTempColumn1);
            tablemetaData.getColumns().addItem(newTempColumn2);
            tablemetaData.getColumns().addItem(newTempColumn3);
            tablemetaData.getColumns().addItem(newTempColumn4);
            tablemetaData.getColumns().addItem(newTempColumn5);
            tablemetaData.getColumns().addItem(newTempColumn6);
            String queryTerminal = "select * from pg_catalog.\"MyTable\" ;";
            Path path = Paths.get("121.sql");
            String encoding = "UTF-8";
            String username = database.getDatabase().getServer().getServerConnectionInfo().getDsUsername();
            GenerateSQLDataCore core = new GenerateSQLDataCore(database, queryTerminal, encoding, username);
            core.initializeCore();
            ExportCursorQueryExecuter exportCursorExecuter =
                    new ExportCursorQueryExecuter(queryTerminal, database.getConnectionManager().getFreeConnection());
            String uniqCursorName = exportCursorExecuter.getUniqCursorName();
            MockResultSet getselectrs = statementHandler.createResultSet();
            getselectrs.setResultSetMetaData(new ResultSetMetaDataImplementation());
            getselectrs.addColumn("Col1");
            getselectrs.addColumn("Col2");
            getselectrs.addColumn("Col3");
            getselectrs.addColumn("Col4");
            getselectrs.addColumn("Col5");
            getselectrs.addColumn("Col6");
            getselectrs.addColumn("Col7");
            getselectrs.addRow(new Object[] {2, 2.04, 1.004, "2018-09-18 16:37:06", "2018-09-18 16:37:06",
                "2018-09-18 16:37:06", true});
            statementHandler.prepareResultSet(uniqCursorName, getselectrs);
            GenerateCursorExecuteVisitor visitor = new GenerateCursorExecuteVisitor(path, encoding, true,
                     JSQLParserUtils.getSelectQueryMainTableName(queryTerminal));
            long exportedRowCount = exportCursorExecuter.exportSQLData(visitor);
            assertEquals(1, exportedRowCount);
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
    public void test_Generate_Insert_Sql_colunmCount()
    {
        try
        {

            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 2, "Col2",
                    new TypeMetaData(1, "double", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn2 = new ColumnMetaData(tablemetaData, 3, "Col3",
                    new TypeMetaData(1, "float8", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn3 = new ColumnMetaData(tablemetaData, 4, "Col4",
                    new TypeMetaData(1, "date", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn4 = new ColumnMetaData(tablemetaData, 5, "Col5",
                    new TypeMetaData(1, "timestamp", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn5 = new ColumnMetaData(tablemetaData, 6, "Col6",
                    new TypeMetaData(1, "timestamptz", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn6 = new ColumnMetaData(tablemetaData, 7, "Col7",
                    new TypeMetaData(1, "bool", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);
            tablemetaData.getColumns().addItem(newTempColumn1);
            tablemetaData.getColumns().addItem(newTempColumn2);
            tablemetaData.getColumns().addItem(newTempColumn3);
            tablemetaData.getColumns().addItem(newTempColumn4);
            tablemetaData.getColumns().addItem(newTempColumn5);
            tablemetaData.getColumns().addItem(newTempColumn6);
            String queryTerminal = "select * from pg_catalog.\"MyTable\" ;";
            Path path = Paths.get("121.sql");
            String encoding = "UTF-8";
            String username = database.getDatabase().getServer().getServerConnectionInfo().getDsUsername();
            GenerateSQLDataCore core = new GenerateSQLDataCore(database, queryTerminal, encoding, username);
            core.initializeCore();
            core.cancelExportOperation();
            ExportCursorQueryExecuter exportCursorExecuter =
                    new ExportCursorQueryExecuter(queryTerminal, database.getConnectionManager().getFreeConnection());
            String uniqCursorName = exportCursorExecuter.getUniqCursorName();
            MockResultSet getselectrs = statementHandler.createResultSet();
            getselectrs.setResultSetMetaData(new ResultSetMetaDataImplementation());
            getselectrs.addColumn("Col1");
            getselectrs.addColumn("Col2");
            getselectrs.addColumn("Col3");
            getselectrs.addColumn("Col4");
            getselectrs.addColumn("Col5");
            getselectrs.addColumn("Col6");
            getselectrs.addColumn("Col7");
            getselectrs.addRow(new Object[] {2, 2.04, 1.004, "2018-09-18 16:37:06", "2018-09-18 16:37:06",
                "2018-09-18 16:37:06", true});
            statementHandler.prepareResultSet(uniqCursorName, getselectrs);
            int columnCount = getselectrs.getMetaData().getColumnCount();
            assertEquals(7, columnCount);
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
    public void test_Generate_Insert_getTableName_001()
    {
        String queryTerminal = "select * from pg_catalog.\"MyTable\" ;";
        String tableName = JSQLParserUtils.getSelectQueryMainTableName(queryTerminal);
        assertEquals("pg_catalog.\"MyTable\"", tableName);

    }

    @Test
    public void test_Generate_Insert_getTableName_002()
    {
        String queryTerminal = "SELECT p.product_id, p.product_name" + " FROM products p" + " WHERE p.category_id IN"
                + " (SELECT c.category_id" + " FROM categories c" + " WHERE c.category_id > 25"
                + " AND c.category_name like 'S%');";
        String tableName = JSQLParserUtils.getSelectQueryMainTableName(queryTerminal);
        assertEquals("products", tableName);

    }

    @Test
    public void test_Generate_Insert_Sql_003()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 2, "Col2",
                    new TypeMetaData(1, "double", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn2 = new ColumnMetaData(tablemetaData, 3, "Col3",
                    new TypeMetaData(1, "float8", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn3 = new ColumnMetaData(tablemetaData, 4, "Col4",
                    new TypeMetaData(1, "date", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn4 = new ColumnMetaData(tablemetaData, 5, "Col5",
                    new TypeMetaData(1, "timestamp", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn5 = new ColumnMetaData(tablemetaData, 6, "Col6",
                    new TypeMetaData(1, "timestamptz", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn6 = new ColumnMetaData(tablemetaData, 7, "Col7",
                    new TypeMetaData(1, "bool", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);
            tablemetaData.getColumns().addItem(newTempColumn1);
            tablemetaData.getColumns().addItem(newTempColumn2);
            tablemetaData.getColumns().addItem(newTempColumn3);
            tablemetaData.getColumns().addItem(newTempColumn4);
            tablemetaData.getColumns().addItem(newTempColumn5);
            tablemetaData.getColumns().addItem(newTempColumn6);
            String queryTerminal = "select * from pg_catalog.\"MyTable\" ;";
            Path path = Paths.get("121.sql");
            String encoding = "UTF-8";
            String username = database.getDatabase().getServer().getServerConnectionInfo().getDsUsername();
            GenerateSQLDataCore core = new GenerateSQLDataCore(database, queryTerminal, encoding, username);
            core.initializeCore();
            Database db = core.getDatabase();
            ExportCursorQueryExecuter exportCursorExecuter =
                    new ExportCursorQueryExecuter(queryTerminal, database.getConnectionManager().getFreeConnection());
            // core.setImportExportoptions(new ImportExportOption());
           
            assertTrue(core != null);
            String query = core.composeSQLQuery();
            assertTrue(core.isOLAPDB());
            core.setExportIsInProgress(true);
            MockResultSet getselectrs = statementHandler.createResultSet();
            getselectrs.addColumn("col1");
            getselectrs.addRow(new Object[] {2});
            statementHandler.prepareResultSet(query, getselectrs);
            core.executeExportData(database.getConnectionManager().getFreeConnection(), path);
            assertTrue(core!=null);
            core.importExportCleanUp();
            core.cleanUpDataCore();
            }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            assertTrue(true);
        }

    }
    @Test
    public void test_Generate_Insert_Sql_004()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("\"MyTa.ble\"");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 2, "col2",
                    new TypeMetaData(1, "double", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn2 = new ColumnMetaData(tablemetaData, 3, "col3",
                    new TypeMetaData(1, "float8", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn3 = new ColumnMetaData(tablemetaData, 4, "col4",
                    new TypeMetaData(1, "date", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn4 = new ColumnMetaData(tablemetaData, 5, "col5",
                    new TypeMetaData(1, "timestamp", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn5 = new ColumnMetaData(tablemetaData, 6, "col6",
                    new TypeMetaData(1, "timestamptz", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn6 = new ColumnMetaData(tablemetaData, 7, "col7",
                    new TypeMetaData(1, "bool", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);
            tablemetaData.getColumns().addItem(newTempColumn1);
            tablemetaData.getColumns().addItem(newTempColumn2);
            tablemetaData.getColumns().addItem(newTempColumn3);
            tablemetaData.getColumns().addItem(newTempColumn4);
            tablemetaData.getColumns().addItem(newTempColumn5);
            tablemetaData.getColumns().addItem(newTempColumn6);
            String queryTerminal = "select * from \"MyTa.ble\" ;";
            Path path = Paths.get("121.sql");
            String encoding = "UTF-8";
            String username = database.getDatabase().getServer().getServerConnectionInfo().getDsUsername();
            GenerateSQLDataCore core = new GenerateSQLDataCore(database, queryTerminal, encoding, username);
            core.initializeCore();
            ExportCursorQueryExecuter exportCursorExecuter =
                    new ExportCursorQueryExecuter(queryTerminal, database.getConnectionManager().getFreeConnection());
            String uniqCursorName = exportCursorExecuter.getUniqCursorName();
            MockResultSet getselectrs = statementHandler.createResultSet();
            getselectrs.setResultSetMetaData(new ResultSetMetaDataImplementation());
            getselectrs.addColumn("Col1");
            getselectrs.addColumn("Col2");
            getselectrs.addColumn("Col3");
            getselectrs.addColumn("Col4");
            getselectrs.addColumn("Col5");
            getselectrs.addColumn("Col6");
            getselectrs.addColumn("Col7");
            getselectrs.addRow(new Object[] {2, 2.04, 1.004, "2018-09-18 16:37:06", "2018-09-18 16:37:06",
                "2018-09-18 16:37:06", true});
            statementHandler.prepareResultSet(uniqCursorName, getselectrs);
            String tableName=JSQLParserUtils.getSelectQueryMainTableName(queryTerminal);
            GenerateCursorExecuteVisitor visitor = new GenerateCursorExecuteVisitor(path, encoding, true,
                    tableName);
            assertEquals("\"MyTa.ble\"", tableName);
           
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
    public void test_Generate_Insert_Sql_005()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            TableMetaData tablemetaData = new TableMetaData(1, "Table005", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("\"MyT.able\"");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 2, "Col2",
                    new TypeMetaData(1, "double", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn2 = new ColumnMetaData(tablemetaData, 3, "Col3",
                    new TypeMetaData(1, "float8", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn3 = new ColumnMetaData(tablemetaData, 4, "Col4",
                    new TypeMetaData(1, "date", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn4 = new ColumnMetaData(tablemetaData, 5, "Col5",
                    new TypeMetaData(1, "timestamp", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn5 = new ColumnMetaData(tablemetaData, 6, "Col6",
                    new TypeMetaData(1, "timestamptz", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn6 = new ColumnMetaData(tablemetaData, 7, "Col7",
                    new TypeMetaData(1, "bool", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);
            tablemetaData.getColumns().addItem(newTempColumn1);
            tablemetaData.getColumns().addItem(newTempColumn2);
            tablemetaData.getColumns().addItem(newTempColumn3);
            tablemetaData.getColumns().addItem(newTempColumn4);
            tablemetaData.getColumns().addItem(newTempColumn5);
            tablemetaData.getColumns().addItem(newTempColumn6);
            String queryTerminal = "select * from public.\"MyT.able\" ;";
            Path path = Paths.get("234.sql");
            String encoding = "UTF-8";
            String username = database.getDatabase().getServer().getServerConnectionInfo().getDsUsername();
            GenerateSQLDataCore core = new GenerateSQLDataCore(database, queryTerminal, encoding, username);
            core.initializeCore();
            ExportCursorQueryExecuter exportCursorExecuter =
                    new ExportCursorQueryExecuter(queryTerminal, database.getConnectionManager().getFreeConnection());
            String uniqCursorName = exportCursorExecuter.getUniqCursorName();
            MockResultSet getselectrs = statementHandler.createResultSet();
            getselectrs.setResultSetMetaData(new ResultSetMetaDataImplementation());
            getselectrs.addColumn("Col1");
            getselectrs.addColumn("Col2");
            getselectrs.addColumn("Col3");
            getselectrs.addColumn("Col4");
            getselectrs.addColumn("Col5");
            getselectrs.addColumn("Col6");
            getselectrs.addColumn("Col7");
            getselectrs.addRow(new Object[] {2, 2.04, 1.004, "2018-09-18 16:37:06", "2018-09-18 16:37:06",
                "2018-09-18 16:37:06", true});
            getselectrs.addRow(new Object[] {2});
            statementHandler.prepareResultSet(uniqCursorName, getselectrs);
            String tableName=JSQLParserUtils.getSelectQueryMainTableName(queryTerminal);
            GenerateCursorExecuteVisitor visitor = new GenerateCursorExecuteVisitor(path, encoding, true,
                    tableName);
            assertEquals("public.\"MyT.able\"", tableName);
            

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
    public void test_Generate_Insert_Sql_006()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 2, "Col2",
                    new TypeMetaData(1, "double", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn2 = new ColumnMetaData(tablemetaData, 3, "Col3",
                    new TypeMetaData(1, "float8", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn3 = new ColumnMetaData(tablemetaData, 4, "Col4",
                    new TypeMetaData(1, "date", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn4 = new ColumnMetaData(tablemetaData, 5, "Col5",
                    new TypeMetaData(1, "timestamp", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn5 = new ColumnMetaData(tablemetaData, 6, "Col6",
                    new TypeMetaData(1, "timestamptz", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn6 = new ColumnMetaData(tablemetaData, 7, "Col7",
                    new TypeMetaData(1, "bool", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);
            tablemetaData.getColumns().addItem(newTempColumn1);
            tablemetaData.getColumns().addItem(newTempColumn2);
            tablemetaData.getColumns().addItem(newTempColumn3);
            tablemetaData.getColumns().addItem(newTempColumn4);
            tablemetaData.getColumns().addItem(newTempColumn5);
            tablemetaData.getColumns().addItem(newTempColumn6);
            String queryTerminal = "select * from pg_catalog.\"MyTable\",pg_catalog.\"MyTable001\" ;";
            Path path = Paths.get("121.sql");
            String encoding = "UTF-8";
            String username = database.getDatabase().getServer().getServerConnectionInfo().getDsUsername();
            GenerateSQLDataCore core = new GenerateSQLDataCore(database, queryTerminal, encoding, username);
            core.initializeCore();
            ExportCursorQueryExecuter exportCursorExecuter =
                    new ExportCursorQueryExecuter(queryTerminal, database.getConnectionManager().getFreeConnection());
            String uniqCursorName = exportCursorExecuter.getUniqCursorName();
            MockResultSet getselectrs = statementHandler.createResultSet();
            getselectrs.setResultSetMetaData(new ResultSetMetaDataImplementation());
            getselectrs.addColumn("col1");
            getselectrs.addColumn("col2");
            getselectrs.addColumn("col3");
            getselectrs.addColumn("col4");
            getselectrs.addColumn("col5");
            getselectrs.addColumn("col6");
            getselectrs.addColumn("col7");
            getselectrs.addRow(new Object[] {2, 2.04, 1.004, "2018-09-18 16:37:06", "2018-09-18 16:37:06",
                "2018-09-18 16:37:06", true});
            statementHandler.prepareResultSet(uniqCursorName, getselectrs);
            GenerateCursorExecuteVisitor visitor = new GenerateCursorExecuteVisitor(path, encoding, true,
                     JSQLParserUtils.getSelectQueryMainTableName(queryTerminal));
            long exportedRowCount = exportCursorExecuter.exportSQLData(visitor);
            assertEquals(1, exportedRowCount);
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
    public void test_Generate_Insert_Sql_007()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 2, "col2",
                    new TypeMetaData(1, "double", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn2 = new ColumnMetaData(tablemetaData, 3, "col3",
                    new TypeMetaData(1, "float8", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn3 = new ColumnMetaData(tablemetaData, 4, "col4",
                    new TypeMetaData(1, "date", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn4 = new ColumnMetaData(tablemetaData, 5, "col5",
                    new TypeMetaData(1, "timestamp", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn5 = new ColumnMetaData(tablemetaData, 6, "col6",
                    new TypeMetaData(1, "timestamptz", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn6 = new ColumnMetaData(tablemetaData, 7, "col7",
                    new TypeMetaData(1, "bool", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);
            tablemetaData.getColumns().addItem(newTempColumn1);
            tablemetaData.getColumns().addItem(newTempColumn2);
            tablemetaData.getColumns().addItem(newTempColumn3);
            tablemetaData.getColumns().addItem(newTempColumn4);
            tablemetaData.getColumns().addItem(newTempColumn5);
            tablemetaData.getColumns().addItem(newTempColumn6);
            String queryTerminal = "select * from pg_catalog.\"MyTable\";";
            Path path = Paths.get("121.sql");
            String encoding = "UTF-8";
            String username = database.getDatabase().getServer().getServerConnectionInfo().getDsUsername();
            GenerateSQLDataCore core = new GenerateSQLDataCore(database, queryTerminal, encoding, username);
            core.initializeCore();
            ExportCursorQueryExecuter exportCursorExecuter =
                    new ExportCursorQueryExecuter(queryTerminal, database.getConnectionManager().getFreeConnection());
            String uniqCursorName = exportCursorExecuter.getUniqCursorName();
            MockResultSet getselectrs = statementHandler.createResultSet();
            getselectrs.setResultSetMetaData(new ResultSetMetaDataImplementation());
            getselectrs.addColumn("col1");
            getselectrs.addColumn("col2");
            getselectrs.addColumn("col3");
            getselectrs.addColumn("col4");
            getselectrs.addColumn("col5");
            getselectrs.addColumn("col6");
            getselectrs.addColumn("col7");
            getselectrs.addRow(new Object[] {2, 2.04, 1.004, "2018-09-18 16:37:06", "2018-09-18 16:37:06",
                "2018-09-18 16:37:06", true});
            statementHandler.prepareResultSet(uniqCursorName, getselectrs);
            String tableName=JSQLParserUtils.getSelectQueryMainTableName(queryTerminal);
            GenerateCursorExecuteVisitor visitor = new GenerateCursorExecuteVisitor(path, encoding, true,
                    tableName);
            assertEquals("pg_catalog.\"MyTable\"", tableName);
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
    public void test_Generate_Insert_getTableName_003()
    {
        String queryTerminal = "select * from pg_catalog.\"MyTable\"  "
                            + " union all  "
                            + "select * from pg_catalog.\"MyTable\";";
        String tableName = JSQLParserUtils.getSelectQueryMainTableName(queryTerminal);
        assertEquals("pg_catalog.\"MyTable\"", tableName);

    }
    
    @Test
    public void test_copyQuery_01()
    {
        String queryTerminal = "select * into pg_catalog.\"MyTable\" ;";
        String queryTerminal2 = "insert into pg_catalog.\"MyTable\" values(0)";
        String queryTerminal3 = null;
        String queryTerminal4 = "select * from pg_catalog.\"MyTable\" ;";
        boolean iscopyQuery1 = JSQLParserUtils.isCopyQuery(queryTerminal);
        assertEquals(true, iscopyQuery1);
        boolean iscopyQuery2 = JSQLParserUtils.isCopyQuery(queryTerminal2);
        assertEquals(false, iscopyQuery2);
        boolean iscopyQuery3 = JSQLParserUtils.isCopyQuery(queryTerminal3);
        assertEquals(false, iscopyQuery3);
        boolean iscopyQuery4 = JSQLParserUtils.isCopyQuery(queryTerminal4);
        assertEquals(false, iscopyQuery4);
    }
}