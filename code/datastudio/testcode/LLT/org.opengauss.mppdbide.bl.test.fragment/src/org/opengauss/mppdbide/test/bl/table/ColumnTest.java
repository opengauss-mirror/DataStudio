package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.export.ExportObjectDataManager;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnUtil;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintType;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.SequenceMetadata;
//import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
//import org.opengauss.mppdbide.bl.test.util.ExceptionConnection;
//import org.opengauss.mppdbide.bl.test.util.CommonLLTUtils.EXCEPTIONENUM;
//import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class ColumnTest extends BasicJDBCTestCaseAdapter
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
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        this.dbconn = CommonLLTUtils.getDBConnection();
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
        CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        JobCancelStatus status=new JobCancelStatus();
        status.setCancel(false);
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
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "MyColumn", typeMetaData);
            columnMetaData.setDataTypeScheam("pg_catalog");
            columnMetaData.setLenOrPrecision(24);
            columnMetaData.setArrayNDim(1);
            columnMetaData.setNotNull(true);
            columnMetaData.setScale(1);
            columnMetaData.setUnique(true);
            columnMetaData.setCheckConstraint("a > b");
            columnMetaData.setDefaultValue("default value");
            columnMetaData.columnDetails(0, false);

            assertEquals("\"MyColumn\"", columnMetaData.getDisplayName());

            assertEquals(
                    "\"MyColumn\" pg_catalog.bigint(24,1) NOT NULL DEFAULT 'default value'",
                    columnMetaData.formColumnString(true));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_TABLE_FUNC_001_011()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "MyColumn", typeMetaData);
            columnMetaData.setDataTypeScheam("pg_catalog");
            columnMetaData.setLenOrPrecision(24);
            columnMetaData.setArrayNDim(1);
            columnMetaData.setNotNull(true);
            columnMetaData.setScale(1);
            columnMetaData.setUnique(true);
            columnMetaData.setCheckConstraint("a > b");
            columnMetaData.setDefaultValue("default value");
            columnMetaData.columnDetails(0, true);

            assertEquals("\"MyColumn\"", columnMetaData.getDisplayName());

            assertEquals(
                    "\"MyColumn\" pg_catalog.bigint(24,1) NOT NULL DEFAULT 'default value'",
                    columnMetaData.formColumnString(true));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_TABLE_FUNC_001_002()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "MyColumn", typeMetaData);
            columnMetaData.setDataTypeScheam("pg_catalog");
            columnMetaData.setLenOrPrecision(24);
            columnMetaData.setArrayNDim(1);
            columnMetaData.setNotNull(true);
            columnMetaData.setScale(1);
            columnMetaData.setUnique(true);
            columnMetaData.setCheckConstraint("a > b");
            columnMetaData.setDefaultValue("default value");

            columnMetaData.execAlterToggleSetNull(this.dbconn);
            columnMetaData.execAlterToggleSetNull(this.dbconn);
            assertEquals(columnMetaData.getDefaultValue(), "default value");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_TABLE_FUNC_001_003()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "MyColumn", typeMetaData);
            columnMetaData.setDataTypeScheam("pg_catalog");
            columnMetaData.setLenOrPrecision(24);
            columnMetaData.setArrayNDim(1);
            columnMetaData.setNotNull(true);
            columnMetaData.setScale(1);
            columnMetaData.setUnique(true);
            columnMetaData.setCheckConstraint("a > b");
            columnMetaData.setDefaultValue("default value");

            columnMetaData.execAlterDefault(null,false, this.dbconn);
            columnMetaData.execAlterDefault("", false,this.dbconn);
            columnMetaData.execAlterDefault("New description", false,this.dbconn);
            assertEquals(columnMetaData.getColDataTypeSchema(), "pg_catalog");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_ColumnMetaData_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "MyColumn", typeMetaData);

            assertEquals(tablemetaData.getDatabase(),
                    columnMetaData.getParentDB());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_ColumnMetaData_002()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "MyColumn", typeMetaData);

            assertFalse(ColumnUtil.isColumnNameValid(columnMetaData));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_TABLE_FUNC_001_004()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "MyColumn", typeMetaData);
            columnMetaData.setDataTypeScheam("pg_catalog");
            columnMetaData.setLenOrPrecision(24);
            columnMetaData.setArrayNDim(1);
            columnMetaData.setNotNull(true);
            columnMetaData.setScale(1);
            columnMetaData.setUnique(true);
            columnMetaData.setCheckConstraint("a > b");
            columnMetaData.setDefaultValue("default value");

            columnMetaData.execAlterAddColumn(this.dbconn);
            assertEquals(columnMetaData.getLenOrPrecision(), 24);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_TABLE_FUNC_001_005()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "MyColumn", typeMetaData);
            columnMetaData.setDataTypeScheam("pg_catalog");
            columnMetaData.setLenOrPrecision(24);
            columnMetaData.setArrayNDim(1);
            columnMetaData.setNotNull(true);
            columnMetaData.setScale(1);
            columnMetaData.setUnique(true);
            columnMetaData.setCheckConstraint("a > b");
            columnMetaData.setDefaultValue("default value");

            columnMetaData.execDrop(this.dbconn);
            assertEquals(columnMetaData.getArrayNDim(), 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_TABLE_FUNC_001_006()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "MyColumn", typeMetaData);
            columnMetaData.setDataTypeScheam("pg_catalog");
            columnMetaData.setLenOrPrecision(24);
            columnMetaData.setArrayNDim(1);
            columnMetaData.setNotNull(true);
            columnMetaData.setScale(1);
            columnMetaData.setUnique(true);
            columnMetaData.setCheckConstraint("a > b");
            columnMetaData.setDefaultValue("default value");

            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);

            columnMetaData.execRename("NewName", this.dbconn);
            assertEquals(columnMetaData.getCheckConstraintExpr(), "a > b");
            columnMetaData.execRename("like", this.dbconn);
            assertEquals(columnMetaData.getCheckConstraintExpr(), "a > b");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_TABLE_FUNC_001_007()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "MyColumn", typeMetaData);
            columnMetaData.setDataTypeScheam("pg_catalog");
            columnMetaData.setLenOrPrecision(24);
            columnMetaData.setArrayNDim(1);
            columnMetaData.setNotNull(true);
            columnMetaData.setScale(1);
            columnMetaData.setUnique(true);
            columnMetaData.setCheckConstraint("a > b");
            columnMetaData.setDefaultValue("default value");

            columnMetaData.setDataType(typeMetaData);
            columnMetaData.getDataType();

            columnMetaData.execChangeDataType(this.dbconn);
            assertEquals(columnMetaData.getScale(), 1);
            ;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_TABLE_FUNC_001_008()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "MyColumn", typeMetaData);
            columnMetaData.setDataTypeScheam("pg_catalog");
            columnMetaData.setLenOrPrecision(24);
            columnMetaData.setArrayNDim(1);
            columnMetaData.setNotNull(true);
            columnMetaData.setScale(1);
            columnMetaData.setUnique(true);
            columnMetaData.setCheckConstraint("a > b");
            columnMetaData.setDefaultValue("default value");

            columnMetaData.setDataType(typeMetaData);
            columnMetaData.getDataType();

            columnMetaData.getLenOrPrecision();
            columnMetaData.getScale();
            columnMetaData.getArrayNDim();
            columnMetaData.getDefaultValue();
            columnMetaData.isNotNull();
            columnMetaData.isUnique();
            columnMetaData.setPre(0, 0);
            columnMetaData.getParentDB();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_TABLE_FUNC_001_009()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);
            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(database.getNameSpaceById(1));
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
            tablemetaData.execCreate(this.dbconn);

            tablemetaData.isLevel3Loaded();
            tablemetaData.isLevel3LoadInProgress();
            assertEquals(tablemetaData.isLevel3Loaded(), false);
            assertEquals(tablemetaData.isLevel3LoadInProgress(), false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_TABLE_FUNC_001_010()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale2(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);
            newTempColumn1.getCheckConstraintExpr();
            newTempColumn1.getColDataTypeSchema();
            IndexMetaData indexMetaData = new IndexMetaData("Idx1");
            indexMetaData.setNamespace(database.getNameSpaceById(1));
            indexMetaData.setTable(tablemetaData);

            ColumnUtil.isDataTypeValid(newTempColumn);
            newTempColumn.setColumnCase(true);
            newTempColumn.getColumnCase();
            newTempColumn.getDatabase();
            newTempColumn.hashCode();
            assertEquals(newTempColumn.getColumnCase(), true);
            assertNotNull(newTempColumn.getDatabase());
            
            tablemetaData.addIndex(indexMetaData);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
            tablemetaData.execCreate(this.dbconn);
            assertFalse(newTempColumn.equals(newTempColumn1));
            //assertNotEquals(newTempColumn, newTempColumn1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_COLUMN_FUNC_BatchDrop_001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");
            newTempColumn1.setLenOrPrecision(2);
            newTempColumn1.setScale(2);
            String sclm1 = newTempColumn1.getClmNameWithDatatype(true);
            assertEquals(sclm1, newTempColumn1.getClmNameWithDatatype(true));
            String sclm2 = newTempColumn1.getClmNameWithDatatype(false);
            assertEquals(sclm2, newTempColumn1.getClmNameWithDatatype(false));
            String attStr = newTempColumn1.getAttDefString();
            newTempColumn1.setDistributionColm(false);
            assertEquals(attStr, newTempColumn1.getAttDefString());
            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
            tablemetaData.execCreate(database.getConnectionManager().getObjBrowserConn());
            assertNotNull(indexMetaData.getNamespace());
            
            assertEquals(newTempColumn.isDropAllowed(), true);
            assertEquals("Column", newTempColumn.getObjectTypeName());
            assertEquals("pg_catalog.\"MyTable\".\"Col1\"", newTempColumn.getObjectFullName());
            String dropQry = newTempColumn.getDropQuery(false);
            assertEquals("ALTER TABLE IF EXISTS pg_catalog.\"MyTable\" DROP COLUMN IF EXISTS \"Col1\"", dropQry);
            
            dropQry = newTempColumn.getDropQuery(true);
            assertEquals("ALTER TABLE IF EXISTS pg_catalog.\"MyTable\" DROP COLUMN IF EXISTS \"Col1\" CASCADE", dropQry);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_exportManagerTest_SeqDDL() {
        File dir = new File("Test");
        try {
            CommonLLTUtils.getOwnerId(statementHandler);
            CommonLLTUtils.getTableDDL(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = new Namespace(1, "schema", database);
            namespace.setLoaded();
            SequenceMetadata seq=new SequenceMetadata(1, "test", namespace);
            Path exportFilePath = Paths.get("Test" + File.separator).toAbsolutePath().normalize();
            boolean fileExists = Files.exists(exportFilePath);
            if (!fileExists) {
                Files.createDirectory(exportFilePath);
            }
            String qry = "SELECT start_value, increment_by, max_value, min_value, is_called FROM schema.test ;";
            MockResultSet getselectrs1 = statementHandler.createResultSet();
            getselectrs1.addColumn("start_value");
            getselectrs1.addColumn("increment_by");
            getselectrs1.addColumn("max_value");
            getselectrs1.addColumn("min_value");
            getselectrs1.addColumn("is_called");
            getselectrs1.addRow(new Object[] {2, 3, 120, 2, true});
            statementHandler.prepareResultSet(qry, getselectrs1);
            String query="SELECT pg_catalog.nextval(?)";
            DBConnection freeConnection = database.getConnectionManager().getFreeConnection();
            Path path = Paths.get("234.sql");
            ExportObjectDataManager exportManager = new ExportObjectDataManager(freeConnection, path, "UTF8");
            MockResultSet getselectrs = preparedstatementHandler.createResultSet();
            getselectrs.addColumn("nextval");
            getselectrs.addRow(new Object[] {2});
            preparedstatementHandler.prepareResultSet(query, getselectrs);
           
          String ddl=  exportManager.getSequenceNextValue(seq);
          assertEquals("SELECT pg_catalog.setVal('test',2,true);", ddl);
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
            fail("Operation exception not expected");
        } catch (DatabaseCriticalException e) {
            fail("Critical exception not expected");
        } catch (DataStudioSecurityException e) {
            fail("Security exception not expected");
        } catch (IOException e) {
            fail("Security exception not expected");
        } catch (MPPDBIDEException e) {
            fail("Security exception not expected");
        } catch (OutOfMemoryError e) {
            fail("Security exception not expected");
        } finally {
            dir.delete();
        }
    }
    
    @Test
public void testTTA_exportManagerTest_SeqDDL_Exception() {
    File dir = new File("Test");
    try {
        CommonLLTUtils.getOwnerId(statementHandler);
        CommonLLTUtils.getTableDDL(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        Namespace namespace = new Namespace(1, "schema", database);
        namespace.setLoaded();
        SequenceMetadata seq = new SequenceMetadata(1, "test", namespace);
        seq.setMaxValue("10");
        seq.setMinValue("2");
        Path exportFilePath = Paths.get("Test" + File.separator).toAbsolutePath().normalize();
        boolean fileExists = Files.exists(exportFilePath);
        if (!fileExists) {
            Files.createDirectory(exportFilePath);
        }
            String qry = "SELECT start_value, increment_by, max_value, min_value, is_called FROM schema.test ;";
            MockResultSet getselectrs1 = statementHandler.createResultSet();
            getselectrs1.addColumn("start_value");
            getselectrs1.addColumn("increment_by");
            getselectrs1.addColumn("max_value");
            getselectrs1.addColumn("min_value");
            getselectrs1.addColumn("is_called");
            getselectrs1.addRow(new Object[] {2, 3, 120, 2, true});
            statementHandler.prepareResultSet(qry, getselectrs1);
        String query = "SELECT pg_catalog.nextval(?)";
        DBConnection freeConnection = database.getConnectionManager().getFreeConnection();
        Path path = Paths.get("234.sql");
        ExportObjectDataManager exportManager = new ExportObjectDataManager(freeConnection, path, "UTF8");
        MockResultSet getselectrs = preparedstatementHandler.createResultSet();
        getselectrs.addColumn("nextval");
        getselectrs.addRow(new Object[] {2});
        preparedstatementHandler.prepareResultSet(query, getselectrs);
        statementHandler.prepareThrowsSQLException(query);

        String ddl = exportManager.getSequenceNextValue(seq);
        System.out.println(ddl);
        assertEquals("SELECT pg_catalog.setVal('test',2,true);", ddl);
        statementHandler.prepareThrowsSQLException(query, new SQLException("Sequence reached maximum value"));
        ddl = exportManager.getSequenceNextValue(seq);
        assertEquals("SELECT pg_catalog.setVal('test',2,true);", ddl);
        statementHandler.prepareThrowsSQLException(query, new SQLException("Sequence reached minimum value"));
        ddl = exportManager.getSequenceNextValue(seq);
    } catch (DatabaseOperationException e) {
        e.printStackTrace();
        fail("Operation exception not expected");
    } catch (DatabaseCriticalException e) {
        fail("Critical exception not expected");
    } catch (DataStudioSecurityException e) {
        fail("Security exception not expected");
    } catch (IOException e) {
        fail("Security exception not expected");
    } catch (MPPDBIDEException e) {
        fail("Security exception not expected");
    } catch (OutOfMemoryError e) {
        fail("Security exception not expected");
    } finally {
        dir.delete();
    }
}
}
