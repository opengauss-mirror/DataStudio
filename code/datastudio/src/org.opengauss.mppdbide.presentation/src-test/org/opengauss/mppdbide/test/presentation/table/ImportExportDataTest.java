package org.opengauss.mppdbide.test.presentation.table;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.util.HostSpec;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintType;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ExportOption;
import org.opengauss.mppdbide.bl.serverdatacache.ImportExportOption;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.serverdatacache.SystemNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.UserNamespace;
import org.opengauss.mppdbide.mock.presentation.BaseConnectionHelper;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.presentation.exportdata.ImportExportDataCore;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.exceptions.TableImporExportException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import static org.junit.Assert.*;

public class ImportExportDataTest extends BasicJDBCTestCaseAdapter
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
    public void testTTA_EXPORT_TABLE_001_01()
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
            ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.validateImportExportOptParameters();
            core.initializeCore();
            core.setFilePath(Paths.get("myfile.csv"));
            core.executeExportData(CommonLLTUtils.getDBConnection(), true);
            ExportOption exportOption = new ExportOption("csv", true, "Test", "TestName");
            exportOption.getFilePath();
            assertNotNull(exportOption.getFilePathWithSuffixFormat());
            assertTrue(exportOption.isZip());
            exportOption.setFileName("Test123");
            exportOption.setZip(false);
            assertNotNull( exportOption.getFilePathWithSuffixFormat());
            
            exportOption.setFolderName("Check");
            exportOption.setFormat(".sql");
            assertNotNull(exportOption.getFilePathWithSuffixFormat());
            
            exportOption.setFormat("Excel(xlsx)");
            assertNotNull(exportOption.getFilePathWithSuffixFormat());
            exportOption.setFormat("Excel(xls)");
            assertNotNull(exportOption.getFilePathWithSuffixFormat());
            assertEquals("Test123",exportOption.getFileName());
            assertEquals("Check",exportOption.getFolderName());
            assertEquals("Excel(xls)",exportOption.getFormat());
            
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
    public void testTTA_EXPORT_TABLE_001_001()
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
            ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.validateImportExportOptParameters();
            core.initializeCore();
            core.setFilePath(Paths.get("myfile.bin"));
            core.executeExportData(CommonLLTUtils.getDBConnection(), true);
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
    public void testTTA_EXPORT_TABLE_001_02()
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
            ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            core.setFilePath(Paths.get(""));
            core.executeExportData(this.dbconn, true);

        }
        catch (DatabaseOperationException e)
        {
            if (!(e.getCause() instanceof FileNotFoundException))
            {
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_EXPORT_TABLE_001_03()
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
                    new HostSpec[] {new HostSpec("127.0.0.1", 1111)}, "db", "user", true);

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
            option.setReplaceNull("*");
            option.setEncoding("");
            option.setDelimiter(",");
            option.setAllColunms(true);
            option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
            ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.initializeCore();
            core.setImportExportoptions(option);
            core.validateImportExportOptParameters();
            core.setFilePath(Paths.get("somefile.csv"));
            DBConnection dbConnection = CommonLLTUtils.getDBConnection();
            core.executeExportData(CommonLLTUtils.getDBConnection(), true);
        }
        catch (DatabaseOperationException e)
        {
            if (!(e.getCause() instanceof SQLException))
            {
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }


    @Test
    public void testTTA_IMPORT_TABLE_FUNC_001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            Namespace namespace = new UserNamespace(1, "namespace1", database);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) namespace);

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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1, "Col2",
                    new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);

            Files.deleteIfExists(Paths.get("samplecsv.csv"));

            Files.createFile(Paths.get("samplecsv.csv"));

            ImportExportOption option = new ImportExportOption();
            option.setFileFormat("other");
            option.setFileName("samplecsv.csv");
            option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
            ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.initializeCore();
            core.setImportExportoptions(option);
            core.validateImportExportOptParameters();
            core.executeImportData();

        }
        catch (TableImporExportException e)
        {
            assertEquals("Please enter delimiter value.", e.getDBErrorMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_ExportTableData_composeQuery1()
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
                option.setFileFormat("Csv");
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
                core.setFilePath(Paths.get("myfile.xls"));
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                String query1 = core.composeExcelQuery();
                assertEquals("SELECT * FROM pg_catalog.\"MyTable\"", query1);
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS ',' HEADER CSV QUOTE '\"' ;", query);

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
    public void test_ExportTableData_composeQuery2()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter("#");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS '#' CSV ;", query);
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
    public void test_ExportTableData_composeQuery3()
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
                option.setFileFormat("Binary");
                option.setHeader(false);
                option.setQuotes("");
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
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT BINARY ;", query);
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
    public void test_ExportTableData_composeQuery4()
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
                option.setFileFormat("Binary");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(false);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" (col1) TO STDOUT BINARY ;", query);
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
    public void test_ExportTableData_composeQuery5()
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
                option.setFileFormat("Binary");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("UTF-8");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT BINARY ENCODING 'UTF-8' ;", query);
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
    public void test_ExportTableData_composeQuery6()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("\"");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("UTF-8");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS ',' CSV QUOTE '\"' ENCODING 'UTF-8' ;",
                        query);
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
    public void test_ExportTableData_composeQuery7()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
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
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS ',' CSV ;", query);
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
    public void test_ExportTableData_composeQuery8()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("\"");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS ',' CSV ESCAPE '\"' ;", query);
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
    public void test_ExportTableData_composeQuery9()
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
                option.setFileFormat("Csv");
                option.setHeader(true);
                option.setQuotes("");
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
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS ',' HEADER CSV ;", query);
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
    public void test_ExportTableData_composeQuery10()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter("\t");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS '\t' CSV ;", query);
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
    public void test_ExportTableData_composeQuery11()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter("|");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS '|' CSV ;", query);
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
    public void test_ExportTableData_composeQuery12()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter(";");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS ';' CSV ;", query);
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
    public void test_ExportTableData_composeQuery13()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("UTF-8");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS ',' CSV ENCODING 'UTF-8' ;", query);
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
    public void test_ExportTableData_composeQuery14()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("UTF-8");
                option.setDelimiter(",");
                option.setAllColunms(false);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" (col1) TO STDOUT DELIMITERS ',' CSV ENCODING 'UTF-8' ;",
                        query);
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

    public void test_ExportTableData_composeQuery15()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("*");
                option.setEncoding("UTF-8");
                option.setDelimiter(",");
                option.setAllColunms(false);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals(
                        "COPY pg_catalog.\"MyTable\" (col1) TO STDOUT DELIMITERS ',' NULL '*' CSV ENCODING 'UTF-8' ;",
                        query);

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

    public void test_ExportTableData_composeQuery16()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("SQL_ASCII");
                option.setDelimiter("\t");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS '\t' CSV ENCODING 'SQL_ASCII' ;", query);
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

    public void test_ExportTableData_composeQuery17()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("GBK");
                option.setDelimiter("\t");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS '\t' CSV ENCODING 'GBK' ;", query);
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

    public void test_ExportTableData_composeQuery18()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("LATIN1");
                option.setDelimiter("\t");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS '\t' CSV ENCODING 'LATIN1' ;", query);
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

    public void test_ExportTableData_composeQuery19()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("SQL_ASCII");
                option.setDelimiter("p");
                option.setAllColunms(false);

                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1", "col2")));
                option.getTablecolumns();
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals(
                        "COPY pg_catalog.\"MyTable\" (col1,col2) TO STDOUT DELIMITERS 'p' CSV ENCODING 'SQL_ASCII' ;",
                        query);
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
    public void test_ExportTableData_composeQuery20()
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
                option.setFileFormat("Csv");
                option.setHeader(true);
                option.setQuotes("#");
                option.setEscape("*");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" TO STDOUT DELIMITERS ',' HEADER CSV QUOTE '#' ESCAPE '*' ;",
                        query);

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

    public void test_ImportTableData_composeQuery1()
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
            ImportExportOption option = new ImportExportOption();
            option.setExport(false);
            option.setFileFormat("Csv");

            try
            {
                option.setHeader(true);
                option.setQuotes("");
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
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" FROM STDIN DELIMITERS ',' HEADER CSV ;", query);
            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }
            try
            {
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("SQL_ASCII");
                option.setDelimiter(";");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" FROM STDIN DELIMITERS ';' CSV ENCODING 'SQL_ASCII' ;", query);
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

    public void test_ImportTableData_composeQuery2()
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
            ImportExportOption option = new ImportExportOption();
            option.setExport(false);
            option.setFileFormat("Csv");

            try
            {

                option.setHeader(true);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter(",");
                option.setAllColunms(false);

                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1", "col2")));
                option.getTablecolumns();
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" (col1,col2) FROM STDIN DELIMITERS ',' HEADER CSV ;", query);
            }
            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }
            try
            {
                option.setHeader(true);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("UTF-8");
                option.setDelimiter("/t");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" FROM STDIN DELIMITERS '/t' HEADER CSV ENCODING 'UTF-8' ;",
                        query);
            }

            catch (TableImporExportException e1)
            {
                fail("Not excepted to come here");
            }
            try
            {
                option.setHeader(true);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("GBK");
                option.setDelimiter("|");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals("COPY pg_catalog.\"MyTable\" FROM STDIN DELIMITERS '|' HEADER CSV ENCODING 'GBK' ;",
                        query);
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

    public void test_ImportTableData_composeQuery3()
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
            ImportExportOption option = new ImportExportOption();
            option.setExport(false);
            option.setFileFormat("Csv");

            try
            {
                option.setHeader(true);
                option.setQuotes("p");
                option.setEscape("q");
                option.setReplaceNull("r");
                option.setEncoding("");
                option.setDelimiter(";");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals(
                        "COPY pg_catalog.\"MyTable\" FROM STDIN DELIMITERS ';' NULL 'r' HEADER CSV QUOTE 'p' ESCAPE 'q' ;",
                        query);
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

    public void test_ImportResultData_composeQuery1()
    {

        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);

            ImportExportOption option = new ImportExportOption();
            option.setExport(false);
            option.setFileFormat("Csv");

            try
            {
                option.setHeader(true);
                option.setQuotes("p");
                option.setEscape("q");
                option.setReplaceNull("r");
                option.setEncoding("");
                option.setDelimiter(";");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(database,
                        new ArrayList<String>(Arrays.asList("col1")), "select * from pg_class", null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals(
                        "COPY (select * from pg_class) FROM STDIN DELIMITER ';' NULL 'r' HEADER CSV QUOTE 'p' ESCAPE 'q' ;",
                        query);
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

    public void test_ImportResultData_composeQuery2()
    {

        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);

            ImportExportOption option = new ImportExportOption();
            option.setExport(false);
            option.setFileFormat("Csv");

            try
            {
                option.setHeader(true);
                option.setQuotes("p");
                option.setEscape("q");
                option.setReplaceNull("r");
                option.setEncoding("");
                option.setDelimiter(";");
                option.setAllColunms(false);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(database,
                        new ArrayList<String>(Arrays.asList("col1")), "select * from pg_class", null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                String query = core.composeQuery();
                assertEquals(
                        "COPY (SELECT col1 FROM (select * from pg_class)) FROM STDIN DELIMITER ';' NULL 'r' HEADER CSV QUOTE 'p' ESCAPE 'q' ;",
                        query);
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
    public void test_impotExport_validation1()
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
                ImportExportOption option1 = new ImportExportOption();
                option1.setFileFormat("csv");
                option1.setDelimiter("");
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option1);
                core.initializeCore();
                core.validateImportExportOptParameters();
            }
            catch (TableImporExportException e1)
            {
                assertEquals("Enter delimiter value.", e1.getDBErrorMessage());
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_impotExport_validation2()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        ImportExportOption option = new ImportExportOption();
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

            option.setFileFormat("Csv");
            option.setReplaceNull(
                    "datastudiodatastudiodatastudiodatastudiodatastudiodatastudiodatastudiodatastudiodatastudiodatastudiodatastudiodatastudiodtastudiodatastudio");
            option.getReplaceNull().length();
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            // String query = core.composeQuery();
        }

        catch (TableImporExportException e1)
        {
            assertEquals("Maximum 100 characters allowed for null string.", e1.getDBErrorMessage());

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
    public void test_impotExport_validation3()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        ImportExportOption option = new ImportExportOption();
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

            option.setFileFormat("Csv");
            option.setReplaceNull("a");
            option.setQuotes("a");
            option.setDelimiter("a");

            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();

        }

        catch (TableImporExportException e1)
        {
            assertEquals("Null parameter can not be same as the delimiter and quote parameter.",
                    e1.getDBErrorMessage());

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
    public void test_impotExport_validation4()
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
            option.setQuotes("abc");
            option.setEscape("");
            option.getQuotes().length();
            option.getEscape().length();
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            // String query = core.composeQuery();
        }

        catch (TableImporExportException e1)
        {
            assertEquals("Only single character is allowed for quote/escape.", e1.getDBErrorMessage());

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
    public void test_impotExport_validation5()
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
            option.setEscape("abc");
            option.getQuotes().length();
            option.getEscape().length();
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            // String query = core.composeQuery();
        }

        catch (TableImporExportException e1)
        {
            assertEquals("Only single character is allowed for quote/escape.", e1.getDBErrorMessage());

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
    public void test_impotExport_validation6()
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
            option.setEscape("");
            option.setReplaceNull("a");
            option.setDelimiter("a");
            option.getQuotes().length();
            option.getDelimiter().length();
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            // String query = core.composeQuery();
        }

        catch (TableImporExportException e1)
        {
            assertEquals("Null parameter can not be same as the delimiter and quote parameter.",
                    e1.getDBErrorMessage());

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
    public void test_impotExport_validation7()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        ImportExportOption option = new ImportExportOption();
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

            option.setFileFormat("Csv");
            option.setQuotes("'");
            option.setEscape("");
            option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            // String query = core.composeQuery();
            assertEquals("'", option.getQuotes());

        }

        catch (TableImporExportException e1)
        {

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
    public void test_impotExport_validation8()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        ImportExportOption option = new ImportExportOption();
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

            option.setFileFormat("Csv");

            option.setEscape("'");
            option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            // String query = core.composeQuery();
            assertEquals("'", option.getEscape());

        }

        catch (TableImporExportException e)
        {
            fail(e.getMessage());

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
    public void test_impotExport_validation9()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        ImportExportOption option = new ImportExportOption();
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

            option.setFileFormat("Csv");
            option.setDelimiter("'");
            option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            // String query = core.composeQuery();
            assertEquals("'", option.getDelimiter());

        }

        catch (TableImporExportException e)
        {
            fail(e.getMessage());

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
    public void test_impotExport_validation10()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        ImportExportOption option = new ImportExportOption();
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

            option.setFileFormat("Csv");
            option.setReplaceNull("'");
            option.setQuotes("");
            option.setEscape("");
            option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            // String query = core.composeQuery();
            assertEquals("'", option.getReplaceNull());

        }

        catch (TableImporExportException e)
        {
            fail(e.getMessage());

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
    public void test_impotExport_validation11()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        ImportExportOption option = new ImportExportOption();
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

            option.setFileFormat("Csv");
            option.setReplaceNull("\r\n");
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            // String query = core.composeQuery();
            option.getReplaceNull();

        }

        catch (TableImporExportException e1)
        {
            assertEquals("Null String should not contain newline or carriage return.", e1.getDBErrorMessage());
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
    public void test_impotExport_validation12()
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
            option.setQuotes("a");
            option.setEscape("a");
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
        }

        catch (TableImporExportException e1)
        {
            assertEquals("Quote character should not be same as the Escape character.", e1.getDBErrorMessage());

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
    public void test_impotExport_validation13()
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
            option.setFileFormat("Binary");
            option.setEncoding("UTF-8");
            option.setQuotes("");
            option.setEscape("");
            option.setReplaceNull(null);
            option.setDelimiter("");
            ArrayList<String> list = new ArrayList<String>(4);
            list.add("col1");
            list.add("col2");
            option.setTablecolumns(list);

            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            assertEquals("UTF-8", option.getEncoding());
            assertEquals("", option.getQuotes());
            assertEquals("", option.getEscape());
            assertEquals("", option.getDelimiter());
            assertEquals(null, option.getReplaceNull());

        }

        catch (TableImporExportException e)
        {
            fail(e.getMessage());

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
    public void test_impotExport_validation14()
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
            option.setFileFormat("CSv");
            option.setEncoding("UTF-8");
            option.setEscape("");
            option.setQuotes("a");
            option.setDelimiter("a");
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
        }

        catch (TableImporExportException e1)
        {
            assertEquals("Quote character should not be same as the delimiter.", e1.getDBErrorMessage());
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
    public void test_impotExport_validation15()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        ImportExportOption option = new ImportExportOption();
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

            option.setFileFormat("Csv");
            option.setQuotes("");
            option.setEscape("");
            option.setReplaceNull("\"");
            option.setDelimiter("|");

            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
        }

        catch (TableImporExportException e1)
        {
            assertEquals("Null parameter can not be same as the default quote parameter.", e1.getDBErrorMessage());

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
    public void test_impotExport_validation16()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        ImportExportOption option = new ImportExportOption();
        ArrayList<String> selectedColsList = new ArrayList<String>(4);
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
            option.setExport(true);
            option.setFileFormat("Csv");
            option.setQuotes("a");
            option.setEscape("b");
            option.setReplaceNull("\"");
            option.setDelimiter(",");

            option.setTablecolumns(selectedColsList);
            option.getTablecolumns().clear();

            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col1")), null, null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
        }

        catch (TableImporExportException e1)
        {
            assertEquals("Select at least one column to Export.", e1.getDBErrorMessage());
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
    public void test_ExportTableData_validate17()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter("hasfgjshdafgjshdafhadfh");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                fail();
            }
            catch (TableImporExportException e1)
            {
                assertEquals("Delimiter must be maximum of 10 bytes.", e1.getDBErrorMessage());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_ExportTableData_validate18()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("dfgdfgdgh");
                option.setEscape("");
                option.setReplaceNull("");
                option.setEncoding("");
                option.setDelimiter("hasfgjshdafgjshdafhadfh");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                fail();
            }
            catch (TableImporExportException e1)
            {
                assertEquals("Only single character is allowed for quote/escape.", e1.getDBErrorMessage());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_ExportTableData_validate19()
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
                option.setFileFormat("Csv");
                option.setHeader(false);
                option.setQuotes("");
                option.setEscape("3456546");
                option.setReplaceNull("");
                option.setEncoding("UTF-8");
                option.setDelimiter(",");
                option.setAllColunms(true);
                option.setTablecolumns(new ArrayList<String>(Arrays.asList("col1")));
                ImportExportDataCore core = new ImportExportDataCore(tablemetaData,
                        new ArrayList<String>(Arrays.asList("col1")), null, null, null);
                core.setImportExportoptions(option);
                core.initializeCore();
                core.validateImportExportOptParameters();
                fail();
            }
            catch (TableImporExportException e1)
            {
                assertEquals("Only single character is allowed for quote/escape.", e1.getDBErrorMessage());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
