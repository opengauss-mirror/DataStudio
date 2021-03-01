package com.huawei.mppdbide.test.presentation.properties;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.util.HostSpec;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.importexportdata.ImportExportDataExecuter;
import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintType;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.ImportExportOption;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.serverdatacache.SystemNamespace;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TypeMetaData;
import com.huawei.mppdbide.mock.presentation.BaseConnectionHelper;
import com.huawei.mppdbide.mock.presentation.CommonLLTUtils;
import com.huawei.mppdbide.mock.presentation.ResultSetMetaDataImplementation;
import com.huawei.mppdbide.presentation.exportdata.ExportCursorExecuteVisitor;
import com.huawei.mppdbide.presentation.exportdata.ExportCursorExecuteVisitor.ColumnDataType;
import com.huawei.mppdbide.test.presentation.table.MockPresentationBLPreferenceImpl;
import com.huawei.mppdbide.presentation.exportdata.ExportCursorQueryExecuter;
import com.huawei.mppdbide.presentation.exportdata.ExportExcelApachePOI;
import com.huawei.mppdbide.presentation.exportdata.ImportExportDataCore;
import com.huawei.mppdbide.utils.CustomStringUtility;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.exceptions.PasswordExpiryException;
import com.huawei.mppdbide.utils.exceptions.TableImporExportException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import static org.junit.Assert.*;

public class ExportExcelApachePOITest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    private DBConnection              dbconn;

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
        this.dbconn = CommonLLTUtils.getDBConnection();
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setDateFormat("yyyy-MM-dd");
        MockPresentationBLPreferenceImpl.setTimeFormat("HH:mm:ss");

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
    public void testTTA_EXPORT_RESULT_001_01()
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
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            Properties properties = new Properties();

            properties.setProperty("user", serverInfo.getDsUsername());
            properties.setProperty("password", new String(serverInfo.getPrd()));
            properties.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            properties.setProperty("characterEncoding", encoding);
            properties.setProperty("ApplicationName", "Data Studio");

            BaseConnectionHelper connectionHelper = new BaseConnectionHelper("", properties,
                    new HostSpec[] {new HostSpec("127.0.0.1", 1111)}, "db", "user", false);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(connectionHelper);
            CommonLLTUtils.mockServerEncoding(connectionHelper.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckDebugSupport(connectionHelper.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckExplainPlanSupport(connectionHelper.getPreparedStatementResultSetHandler());
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
            ImportExportOption option = new ImportExportOption();
            option.setExport(true);
            option.setFileFormat("Csv");
            option.setHeader(true);
            option.setQuotes("");
            option.setEscape("");
            option.setReplaceNull("");
            option.setEncoding("");
            option.setDelimiter(",");
            option.setAllColunms(true);
            option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
            ImportExportDataCore core = new ImportExportDataCore(database, new ArrayList<String>(Arrays.asList("col1")),
                    "select * from pg_catalog.\"MyTable\" ;", "", null);
            core.setImportExportoptions(option);
            core.validateImportExportOptParameters();
            core.initializeCore();
            core.setFilePath(Paths.get("myfile.csv"));
            String query = core.composeQuery();
            assertEquals("COPY (select * from pg_catalog.\"MyTable\" ;) TO STDOUT DELIMITER ',' HEADER CSV ;", query);
            core.executeExportData(CommonLLTUtils.getDBConnection(), false);
            assertTrue(true);
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
    public void testTTA_EXPORT_RESULT_001_02()
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
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            Properties properties = new Properties();

            properties.setProperty("user", serverInfo.getDsUsername());
            properties.setProperty("password", new String(serverInfo.getPrd()));
            properties.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            properties.setProperty("characterEncoding", encoding);
            properties.setProperty("ApplicationName", "Data Studio");

            BaseConnectionHelper connectionHelper = new BaseConnectionHelper("", properties,
                    new HostSpec[] {new HostSpec("127.0.0.1", 1111)}, "db", "user", false);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(connectionHelper);
            CommonLLTUtils.mockServerEncoding(connectionHelper.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckDebugSupport(connectionHelper.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckExplainPlanSupport(connectionHelper.getPreparedStatementResultSetHandler());
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
            ImportExportOption option = new ImportExportOption();
            option.setExport(true);
            option.setFileFormat("Binary");
            option.setHeader(true);
            option.setQuotes("");
            option.setEscape("");
            option.setReplaceNull("");
            option.setEncoding("");
            option.setDelimiter(",");
            option.setAllColunms(true);
            option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
            ImportExportDataCore core = new ImportExportDataCore(database, new ArrayList<String>(Arrays.asList("col1")),
                    "select * from pg_catalog.\"MyTable\" ;", "", null);
            core.setImportExportoptions(option);
            core.validateImportExportOptParameters();
            core.initializeCore();
            core.setFilePath(Paths.get("myfile.bin"));
            String query = core.composeQuery();
            assertEquals("COPY (select * from pg_catalog.\"MyTable\" ;) TO STDOUT BINARY ;", query);
            core.executeExportData(CommonLLTUtils.getDBConnection(), false);
            assertTrue(true);
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
    public void testTTA_EXPORT_RESULT_001_03()
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
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            Properties properties = new Properties();

            properties.setProperty("user", serverInfo.getDsUsername());
            properties.setProperty("password", new String(serverInfo.getPrd()));
            properties.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            properties.setProperty("characterEncoding", encoding);
            properties.setProperty("ApplicationName", "Data Studio");

            BaseConnectionHelper connectionHelper = new BaseConnectionHelper("", properties,
                    new HostSpec[] {new HostSpec("127.0.0.1", 1111)}, "db", "user", false);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(connectionHelper);
            CommonLLTUtils.mockServerEncoding(connectionHelper.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckDebugSupport(connectionHelper.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckExplainPlanSupport(connectionHelper.getPreparedStatementResultSetHandler());
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

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            try
            {

                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter("#");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core =
                        new ImportExportDataCore(database, new ArrayList<String>(Arrays.asList("col1")),
                                "select * from pg_catalog.\"MyTable\" ;", "", null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("myfile.csv"));
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY (select * from pg_catalog.\"MyTable\" ;) TO STDOUT DELIMITER '#' CSV ;", query);
                core.executeExportData(CommonLLTUtils.getDBConnection(), false);
                assertTrue(true);
            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_EXPORT_RESULT_001_04()
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
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            Properties properties = new Properties();

            properties.setProperty("user", serverInfo.getDsUsername());
            properties.setProperty("password", new String(serverInfo.getPrd()));
            properties.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            properties.setProperty("characterEncoding", encoding);
            properties.setProperty("ApplicationName", "Data Studio");

            BaseConnectionHelper connectionHelper = new BaseConnectionHelper("", properties,
                    new HostSpec[] {new HostSpec("127.0.0.1", 1111)}, "db", "user", false);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(connectionHelper);
            CommonLLTUtils.mockServerEncoding(connectionHelper.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckDebugSupport(connectionHelper.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckExplainPlanSupport(connectionHelper.getPreparedStatementResultSetHandler());
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

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            try
            {

                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("\"");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("UTF-8");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core =
                        new ImportExportDataCore(database, new ArrayList<String>(Arrays.asList("col1")),
                                "select * from pg_catalog.\"MyTable\" ;", "", null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("myfile.csv"));
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals(
                        "COPY (select * from pg_catalog.\"MyTable\" ;) TO STDOUT DELIMITER ',' CSV QUOTE '\"' ENCODING 'UTF-8' ;",
                        query);
                core.executeExportData(CommonLLTUtils.getDBConnection(), false);
                assertTrue(true);
            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_EXPORT_RESULT_001_05()
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
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            Properties properties = new Properties();

            properties.setProperty("user", serverInfo.getDsUsername());
            properties.setProperty("password", new String(serverInfo.getPrd()));
            properties.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            properties.setProperty("characterEncoding", encoding);
            properties.setProperty("ApplicationName", "Data Studio");

            BaseConnectionHelper connectionHelper = new BaseConnectionHelper("", properties,
                    new HostSpec[] {new HostSpec("127.0.0.1", 1111)}, "db", "user", false);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(connectionHelper);
            CommonLLTUtils.mockServerEncoding(connectionHelper.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckDebugSupport(connectionHelper.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckExplainPlanSupport(connectionHelper.getPreparedStatementResultSetHandler());
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

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            // Empty Quote
            try
            {

                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("CSV");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("UTF-8");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core =
                        new ImportExportDataCore(database, new ArrayList<String>(Arrays.asList("col1")),
                                "select * from pg_catalog.\"MyTable\" ;", "", null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("myfile.csv"));
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals(
                        "COPY (select * from pg_catalog.\"MyTable\" ;) TO STDOUT DELIMITER ',' CSV ENCODING 'UTF-8' ;",
                        query);
                core.executeExportData(CommonLLTUtils.getDBConnection(), false);
                assertTrue(core != null);
            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }

            // Quote with special character && Empty Escape character && Empty
            // NULL string
            try
            {

                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("CSV");
                option.setHeader(false);
                option.setQuotes("&");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("UTF-8");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core =
                        new ImportExportDataCore(database, new ArrayList<String>(Arrays.asList("col1")),
                                "select * from pg_catalog.\"MyTable\" ;", "", null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("myfile.csv"));
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals(
                        "COPY (select * from pg_catalog.\"MyTable\" ;) TO STDOUT DELIMITER ',' CSV QUOTE '&' ENCODING 'UTF-8' ;",
                        query);
                core.executeExportData(CommonLLTUtils.getDBConnection(), false);
                assertTrue(core != null);
            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }

            // Single Escape character

            try
            {

                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("CSV");
                option.setHeader(false);
                option.setQuotes("&");
                option.setEscape("g");
                option.setReplaceNull("");
                option.setEncoding("UTF-8");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core =
                        new ImportExportDataCore(database, new ArrayList<String>(Arrays.asList("col1")),
                                "select * from pg_catalog.\"MyTable\" ;", "", null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("myfile.csv"));
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals(
                        "COPY (select * from pg_catalog.\"MyTable\" ;) TO STDOUT DELIMITER ',' CSV QUOTE '&' ESCAPE 'g' ENCODING 'UTF-8' ;",
                        query);
                core.executeExportData(CommonLLTUtils.getDBConnection(), false);
                assertTrue(core != null);
            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }

            // Escape character with special character
            try
            {

                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("CSV");
                option.setHeader(false);
                option.setQuotes("&");
                option.setEscape("@");
                option.setReplaceNull("");
                option.setEncoding("UTF-8");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core =
                        new ImportExportDataCore(database, new ArrayList<String>(Arrays.asList("col1")),
                                "select * from pg_catalog.\"MyTable\" ;", "", null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("myfile.csv"));
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals(
                        "COPY (select * from pg_catalog.\"MyTable\" ;) TO STDOUT DELIMITER ',' CSV QUOTE '&' ESCAPE '@' ENCODING 'UTF-8' ;",
                        query);
                core.executeExportData(CommonLLTUtils.getDBConnection(), false);
                assertTrue(core != null);
            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }

            // Having NULL parameter less than 100 character && Empty Encoding

            try
            {

                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("CSV");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("1234");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core =
                        new ImportExportDataCore(database, new ArrayList<String>(Arrays.asList("col1")),
                                "select * from pg_catalog.\"MyTable\" ;", "", null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("myfile.csv"));
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY (select * from pg_catalog.\"MyTable\" ;) TO STDOUT DELIMITER ',' NULL '1234' CSV ;",
                        query);
                core.executeExportData(CommonLLTUtils.getDBConnection(), false);
                assertTrue(core != null);
            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }

            // Having NULL parameter as Special character

            try
            {

                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("CSV");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("&");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core =
                        new ImportExportDataCore(database, new ArrayList<String>(Arrays.asList("col1")),
                                "select * from pg_catalog.\"MyTable\" ;", "", null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("myfile.csv"));
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY (select * from pg_catalog.\"MyTable\" ;) TO STDOUT DELIMITER ',' NULL '&' CSV ;",
                        query);
                core.executeExportData(CommonLLTUtils.getDBConnection(), false);
                assertTrue(core != null);
            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_ImportExportDataCore()
    {
        Database database = connProfCache.getDbForProfileId(profileId);

        try
        {
            TableMetaData tableMetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tableMetaData.setTempTable(true);
            tableMetaData.setIfExists(true);
            tableMetaData.setName("MyTable");
            tableMetaData.setHasOid(true);
            tableMetaData.setDistributeOptions("HASH");
            tableMetaData.setNodeOptions("Node1");
            tableMetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tableMetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tableMetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tableMetaData.getColumns().addItem(newTempColumn);

            ImportExportOption option = new ImportExportOption();
            option.setFileFormat("Csv");
            option.setQuotes("");
            option.setEscape("a");
            option.getQuotes().length();
            option.getEscape().length();
            String exec = "executedQuery";

            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), exec, null, null);

            String tablename = tableMetaData.getDisplayName();
            assertEquals("Exported data from table: pg_catalog.\"MyTable\"", core.getDisplayName());
        }

        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());

        }
    }

    @Test
    public void testTTA_ImportExportDataCore_getDisplayTableName()
    {
        Database database = connProfCache.getDbForProfileId(profileId);

        try
        {
            TableMetaData tableMetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tableMetaData.setTempTable(true);
            tableMetaData.setIfExists(true);
            tableMetaData.setName("MyTable");
            tableMetaData.setHasOid(true);
            tableMetaData.setDistributeOptions("HASH");
            tableMetaData.setNodeOptions("Node1");
            tableMetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tableMetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tableMetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tableMetaData.getColumns().addItem(newTempColumn);

            ImportExportOption option = new ImportExportOption();
            option.setFileFormat("Csv");
            option.setQuotes("");
            option.setEscape("a");
            option.getQuotes().length();
            option.getEscape().length();
            String exec = "executedQuery";

            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), exec, null, null);


        }

        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());

        }
    }

    @Test
    public void testTTA_ImportExportDataCore_getDatabase()
    {
        Database database = connProfCache.getDbForProfileId(profileId);

        try
        {
            TableMetaData tableMetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tableMetaData.setTempTable(true);
            tableMetaData.setIfExists(true);
            tableMetaData.setName("MyTable");
            tableMetaData.setHasOid(true);
            tableMetaData.setDistributeOptions("HASH");
            tableMetaData.setNodeOptions("Node1");
            tableMetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tableMetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tableMetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tableMetaData.getColumns().addItem(newTempColumn);

            ImportExportOption option = new ImportExportOption();
            option.setFileFormat("Csv");
            option.setQuotes("");
            option.setEscape("a");
            option.getQuotes().length();
            option.getEscape().length();
            String exec = "executedQuery";

            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), exec, null, null);

            String tablename = tableMetaData.getDisplayName();
            String name = tableMetaData.getBottombarDisplayName();

            Database db = core.getDatabase();
            assertEquals(tableMetaData.getDatabase(), core.getDatabase());
            assertEquals("pg_catalog.MyTable", name);
        }

        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_ImportExportDataCore_getPath()
    {
        Database database = connProfCache.getDbForProfileId(profileId);

        try
        {
            TableMetaData tableMetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tableMetaData.setTempTable(true);
            tableMetaData.setIfExists(true);
            tableMetaData.setName("MyTable");
            tableMetaData.setHasOid(true);
            tableMetaData.setDistributeOptions("HASH");
            tableMetaData.setNodeOptions("Node1");
            tableMetaData.setDescription("Table description");
            Path path = Paths.get("D:/dstest/abc1.sql");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tableMetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tableMetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tableMetaData.getColumns().addItem(newTempColumn);

            ImportExportOption option = new ImportExportOption();
            option.setFileFormat("Csv");
            option.setQuotes("");
            option.setEscape("a");
            option.getQuotes().length();
            option.getEscape().length();
            String exec = "executedQuery";

            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), exec, null, null);
            core.setFilePath(path);
            Path print = core.getFilePath();

            assertEquals(print, core.getFilePath());
        }

        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());

        }

    }

    @Test
    public void testTTA_ImportExportDataCore_getFileName()
    {
        Database database = connProfCache.getDbForProfileId(profileId);

        try
        {
            TableMetaData tableMetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tableMetaData.setTempTable(true);
            tableMetaData.setIfExists(true);
            tableMetaData.setName("MyTable");
            tableMetaData.setHasOid(true);
            tableMetaData.setDistributeOptions("HASH");
            tableMetaData.setNodeOptions("Node1");
            tableMetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tableMetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tableMetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tableMetaData.getColumns().addItem(newTempColumn);

            ImportExportOption option = new ImportExportOption();
            option.setFileFormat("Csv");
            option.setQuotes("");
            option.setEscape("a");
            option.getQuotes().length();
            option.getEscape().length();
            String exec = "executedQuery";

            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), exec, null, null);

            core.getFileName();

            assertEquals("MyTable", core.getFileName());
        }

        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());

        }

    }


    public void testTTA_ExportExcel_XLSX_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            try
            {

                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("Excel(xlsx)");
                option.setHeader(true);
                option.setQuotes("\"");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("./firstExcel.xlsx"));
                ExportCursorQueryExecuter exportExecuter = new ExportCursorQueryExecuter(
                        "select * from pg_catalog.MyTable;", database.getConnectionManager().getFreeConnection());
                String uniqCursorName = exportExecuter.getUniqCursorName();
                MockResultSet getselectrs = statementHandler.createResultSet();
                getselectrs.addColumn("Col1");
                getselectrs.addRow(new Object[] {2});

                statementHandler.prepareResultSet(uniqCursorName, getselectrs);
                ExportCursorExecuteVisitor executeVisitor = new ExportCursorExecuteVisitor(core.getFilePath(),
                        option.getEncoding(), option.getFileFormat(), core.getSafeSheetName(), true);
                long exportExcelData = exportExecuter.exportExcelData(executeVisitor, false);
                assertEquals(1, exportExcelData);

            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_ExportExcel_XLS_002()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            try
            {

                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("Excel(xls)");
                option.setHeader(true);
                option.setQuotes("\"");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("./firstExcel.xls"));
                ExportCursorQueryExecuter exportExecuter = new ExportCursorQueryExecuter(
                        "select * from pg_catalog.MyTable;", database.getConnectionManager().getFreeConnection());
                String uniqCursorName = exportExecuter.getUniqCursorName();
                MockResultSet getselectrs = statementHandler.createResultSet();
                getselectrs.addColumn("Col1");
                getselectrs.addRow(new Object[] {2});

                statementHandler.prepareResultSet(uniqCursorName, getselectrs);
                ExportCursorExecuteVisitor executeVisitor = new ExportCursorExecuteVisitor(core.getFilePath(),
                        option.getEncoding(), option.getFileFormat(), core.getSafeSheetName(), true);
                long exportExcelData = exportExecuter.exportExcelData(executeVisitor, false);
                assertEquals(1, exportExcelData);
                executeVisitor.cleanUpworkbook();

            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    public void testTTA_ExportExcel_XLSX_MultipleDatatype_003()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 2, "Col2",
                    new TypeMetaData(1, "float8", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn2 = new ColumnMetaData(tablemetaData, 3, "Col3",
                    new TypeMetaData(1, "float4", database.getNameSpaceById(1)));
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

            try
            {

                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("Excel(xlsx)");
                option.setHeader(true);
                option.setQuotes("\"");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(
                        new ArrayList<String>(Arrays.asList("col1", "col2", "col3", "col4", "col5", "col6", "col7")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1", "col2", "col3", "col4", "col5", "col6", "col7")),
                        null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("./firstExcel.xlsx"));
                ExportCursorQueryExecuter exportExecuter = new ExportCursorQueryExecuter(
                        "select * from pg_catalog.MyTable;", database.getConnectionManager().getFreeConnection());
                String uniqCursorName = exportExecuter.getUniqCursorName();
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
                ExportCursorExecuteVisitor executeVisitor = new ExportCursorExecuteVisitor(core.getFilePath(),
                        option.getEncoding(), option.getFileFormat(), core.getSafeSheetName(), true);
                long exportExcelData = exportExecuter.exportExcelData(executeVisitor, false);
                assertEquals(1, exportExcelData);

            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_ExportExcel_XLS_MultipleDatatype_004()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 2, "Col2",
                    new TypeMetaData(1, "float8", database.getNameSpaceById(1)));
            ColumnMetaData newTempColumn2 = new ColumnMetaData(tablemetaData, 3, "Col3",
                    new TypeMetaData(1, "float4", database.getNameSpaceById(1)));
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

            try
            {

                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("Excel(xls)");
                option.setHeader(true);
                option.setQuotes("\"");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setEncoding("UTF-8");
                option.setTablecolumns(
                        new ArrayList<String>(Arrays.asList("col1", "col2", "col3", "col4", "col5", "col6", "col7")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1", "col2", "col3", "col4", "col5", "col6", "col7")),
                        null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("./firstExcel.xls"));
                ExportCursorQueryExecuter exportExecuter = new ExportCursorQueryExecuter(
                        "select * from pg_catalog.MyTable;", database.getConnectionManager().getFreeConnection());
                String uniqCursorName = exportExecuter.getUniqCursorName();
                MockResultSet getselectrs = statementHandler.createResultSet();
                getselectrs.setResultSetMetaData(new ResultSetMetaDataImplementation());
                getselectrs.addColumn("Col1");
                getselectrs.addColumn("Col2");
                getselectrs.addColumn("Col3");
                getselectrs.addColumn("Col4");
                getselectrs.addColumn("Col5");
                getselectrs.addColumn("Col6");
                getselectrs.addColumn("Col7");
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long time = sdf.parse("2018-09-18 16:37:06").getTime();
                Timestamp timeStampValue = new Timestamp(time);
                
                getselectrs.addRow(new Object[] {2, 2.04, 1.004, new Date(System.currentTimeMillis()), timeStampValue,
                    timeStampValue, true});
                statementHandler.prepareResultSet(uniqCursorName, getselectrs);
                ExportCursorExecuteVisitor executeVisitor = new ExportCursorExecuteVisitor(core.getFilePath(),
                        option.getEncoding(), option.getFileFormat(), core.getSafeSheetName(), true);
                long exportExcelData = exportExecuter.exportExcelData(executeVisitor, false);
                assertEquals(1, exportExcelData);

            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    
    public void testTTA_ExportExcel_XLSX_cleanupWorkbook()
    {
        try
        {

            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            try
            {
                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("Excel(xlsx)");
                option.setHeader(true);
                option.setQuotes("\"");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.setFilePath(Paths.get("./firstExcel.xlsx"));
                ExportCursorQueryExecuter exportExecuter = new ExportCursorQueryExecuter(
                        "select * from pg_catalog.MyTable;", database.getConnectionManager().getFreeConnection());
                String uniqCursorName = exportExecuter.getUniqCursorName();
                MockResultSet getselectrs = statementHandler.createResultSet();
                getselectrs.addColumn("Col1");
                getselectrs.addRow(new Object[] {2});
                statementHandler.prepareResultSet(uniqCursorName, getselectrs);
                ExportCursorExecuteVisitor executeVisitor = new ExportCursorExecuteVisitor(core.getFilePath(),
                        option.getEncoding(), option.getFileFormat(), core.getSafeSheetName(), true);
                executeVisitor.getHeaderOfRecord(getselectrs, true, false);
                core.cleanUp();
                executeVisitor.cleanUpworkbook();
            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testExportExcel_without_cursor()
    {
    		Database database = connProfCache.getDbForProfileId(profileId);
            try
            {
                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("Excel(xlsx)");
                option.setHeader(true);
                option.setQuotes("\"");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(database,
                        new ArrayList<String>(Arrays.asList("col1")), "select * from pg_catalog.MyTable;", "Result_Tab", "20180909");
                core.setImportExportoptions(option);
                core.initializeCore();
                core.getFileFormat();
                core.getImportExportServerObj();
                core.getImportExportoptions();
                core.importExportCleanUp();
                core.cancelImportExportOperation();
                core.setFilePath(Paths.get("./firstExcel.xlsx"));
                assertTrue(core.isOLAPDB());
                assertEquals(core.getFileName(), "Result_Tab" + '_'
                        + CustomStringUtility.convertStringDateFormat("20180909",
                                MPPDBIDEConstants.DATE_COLLAPSE_FORMAT));
                core.setFileLocation("./firstExcel.xlsx");
                assertEquals(core.getFileLocation(), "./firstExcel.xlsx");
                core.setExportIsInProgress(true);
                core.setExport(true);
                assertEquals(option.isExport(), true);
                String uniqCursorName = core.composeExcelQuery();
                DefaultParameter dp1 = new DefaultParameter("N1", "BINARY_INTEGER", "5", PARAMETERTYPE.IN);
                DefaultParameter dp2 = new DefaultParameter("N2", "BINARY_INTEGER", "5", PARAMETERTYPE.IN);
                DefaultParameter dp3 = new DefaultParameter("TEMP_RESULT", "BINARY_INTEGER", "500", PARAMETERTYPE.OUT);
                ArrayList<DefaultParameter> debugInputValueList = new ArrayList<DefaultParameter>();
                debugInputValueList.add(dp1);
                debugInputValueList.add(dp2);
                debugInputValueList.add(dp3);
                ArrayList<Object> outResultList = new ArrayList<Object>();
                outResultList.add(500);
                MockResultSet getselectrs = statementHandler.createResultSet();
                getselectrs.addColumn("Col1");
                getselectrs.addColumn("col02");
                getselectrs.addColumn("col03");
                getselectrs.addColumn("col04");
                getselectrs.addColumn("col05");
                getselectrs.addColumn("col06");
                getselectrs.addRow(new Object[] {2, "ITEMT1", 25, 12.25, false, "2019-10-16 11:00:00"});
                getselectrs.addRow(new Object[] {2});
                statementHandler.prepareResultSet(uniqCursorName, getselectrs);
                ExportCursorExecuteVisitor executeVisitor = new ExportCursorExecuteVisitor(core.getFilePath(),
                        option.getEncoding(), option.getFileFormat(), core.getSafeSheetName(), true);
                executeVisitor.getHeaderOfRecord(getselectrs, true, false);
            }
            catch (MPPDBIDEException e1)
            {
                fail("Not excepted to come here");
            }
    }

    @Test
    public void testTTA_ExportExcel_XLSX_rowCount()
    {
        ExportExcelApachePOI exportExcel = new ExportExcelApachePOI("Excel(xlsx)");
        boolean lessRows = exportExcel.checkRowLength(10);
        boolean moreRows = exportExcel.checkRowLength(1000001);
        assertTrue(lessRows);
        assertFalse(moreRows);
    }

    @Test
    public void testTTA_ExportExcel_XLS_rowCount()
    {
        ExportExcelApachePOI exportExcel = new ExportExcelApachePOI("Excel(xls)");
        boolean lessRows = exportExcel.checkRowLength(10);
        boolean moreRows = exportExcel.checkRowLength(64001);
        assertTrue(lessRows);
        assertFalse(moreRows);
    }

    @Test
    public void testTTA_ExportExcel_XLSX_colCount()
    {
        ExportExcelApachePOI exportExcel = new ExportExcelApachePOI("Excel(xlsx)");
        try
        {
            exportExcel.createSheet("xlsxSheet");
            exportExcel.setCellValue(Arrays.asList("2", "", "false", "2019-10-16 11:00:00", "25.2"), 1);
        }
        catch (ParseException e)
        {
            fail("Not excepted to come here");
        }
        catch (DatabaseOperationException e)
        {
            fail("Not excepted to come here");
        }
        boolean lessCol = exportExcel.checkColLength(10);
        boolean moreCol = exportExcel.checkColLength(16385);
        assertTrue(lessCol);
        assertFalse(moreCol);
    }

    @Test
    public void testTTA_ExportExcel_XLS_colCount()
    {
        ExportExcelApachePOI exportExcel = new ExportExcelApachePOI("Excel(xls)");
        try
        {
            exportExcel.createSheet("xlsSheet");
            exportExcel.setCellValue(Arrays.asList("2", "", "false"), 1);
        }
        catch (ParseException e)
        {
            fail("Not excepted to come here");
        }
        catch (DatabaseOperationException e)
        {
            fail("Not excepted to come here");
        }
        boolean lessCol = exportExcel.checkColLength(10);
        boolean moreCol = exportExcel.checkColLength(257);
        assertTrue(lessCol);
        assertFalse(moreCol);
    }

    @Test
    public void testTTA_ExportExcel_executeExportData()
    {
        try
        {

            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            try
            {
                ImportExportOption option = new ImportExportOption();
                option.setExport(true);
                option.setFileFormat("Excel(xlsx)");
                option.setHeader(true);
                option.setQuotes("\"");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(database,
                        new ArrayList<String>(Arrays.asList("col1")), "select * from pg_catalog.MyTable;", "Result_Tab", "20180909");
                core.setImportExportoptions(option);
                core.initializeCore();
                core.getFileFormat();
                core.getImportExportServerObj();
                core.getImportExportoptions();
                core.importExportCleanUp();
                core.cancelImportExportOperation();
                core.setFilePath(Paths.get("./firstExcel.xlsx"));
                assertTrue(core.isOLAPDB());
                assertEquals(core.getFileName(), "Result_Tab" + '_'
                        + CustomStringUtility.convertStringDateFormat("20180909",
                                MPPDBIDEConstants.DATE_COLLAPSE_FORMAT));
                core.setFileLocation("./firstExcel.xlsx");
                assertEquals(core.getFileLocation(), "./firstExcel.xlsx");
                core.setExportIsInProgress(true);
                core.setExport(true);
                assertEquals(option.isExport(), true);
                String uniqCursorName = core.composeExcelQuery();
                MockResultSet getselectrs = statementHandler.createResultSet();
                getselectrs.addColumn("Col1");
                getselectrs.addRow(new Object[] {2});
                statementHandler.prepareResultSet(uniqCursorName, getselectrs);
                core.executeExportData(database.getConnectionManager().getFreeConnection(), true);
                assertTrue(core!=null);
            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }
    
    @Test
    public void testTTA_ImportExportDataCore_getProgressLabelName()
    {
        Database database = connProfCache.getDbForProfileId(profileId);

        try
        {
            TableMetaData tableMetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tableMetaData.setTempTable(true);
            tableMetaData.setIfExists(true);
            tableMetaData.setName("MyTable");
            tableMetaData.setHasOid(true);
            tableMetaData.setDistributeOptions("HASH");
            tableMetaData.setNodeOptions("Node1");
            tableMetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tableMetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tableMetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tableMetaData.getColumns().addItem(newTempColumn);

            ImportExportOption option = new ImportExportOption();
            option.setFileFormat("Excel(xls)");
            option.setEncoding("UTF-8");
            option.setExport(true);
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);

            String progressLabelName = core.getProgressLabelName();
            assertEquals("MyTable.pg_catalog.Gauss@TestConnectionName", progressLabelName);
        }

        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());

        }
        catch (MPPDBIDEException e)
        {
            fail(e.getMessage());

        }
    }
    
    @Test
    public void testTTA_ExportExcel_XLSX_createTitle() {
        try {
            ExportExcelApachePOI poi = new ExportExcelApachePOI("Excel(xlsx)", true);
            poi.createSheet("xsshSheet");
            poi.createHeaderRow(Arrays.asList("c1", "c2"));
            poi.setCellValue(Arrays.asList("11", "22"), 1);
        } catch (Exception e) {
            fail("can\'t run here");
        }
    }
    
    @Test
    public void testTTA_ExportExcel_XLSX_createTitle_01() {
        try {
            ExportExcelApachePOI poi = new ExportExcelApachePOI("Excel(xlsx)");
            poi.createSheet("xsshSheet");
            poi.createHeaderRow(Arrays.asList("c1", "c2"));
            poi.setCellValue(Arrays.asList("11", "22"), 1);
        } catch (Exception e) {
            fail("can\'t run here");
        }
    }
}
