package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.Alias;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintType;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.SystemNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ColumnList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ConstraintList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.IndexList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.explainplan.service.AnalysedPlanNode;
import org.opengauss.mppdbide.explainplan.service.ExplainPlanAnlysisService;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils.EXCEPTIONENUM;
import org.opengauss.mppdbide.mock.bl.ExceptionConnection;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.presentation.IWindowDetail;
import org.opengauss.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import org.opengauss.mppdbide.presentation.visualexplainplan.AnalysedPlanNodeProperties;
import org.opengauss.mppdbide.presentation.visualexplainplan.ExplainPlanNodeDetails;
import org.opengauss.mppdbide.presentation.visualexplainplan.ExplainPlanNodePropertiesCore;
import org.opengauss.mppdbide.presentation.visualexplainplan.ExplainPlanOverAllPlanPropertiesCore;
import org.opengauss.mppdbide.presentation.visualexplainplan.ExplainPlanOverAllProperties;
import org.opengauss.mppdbide.presentation.visualexplainplan.ExplainPlanPresentation;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelConverter;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.FilePermissionFactory;
import org.opengauss.mppdbide.utils.files.ISetFilePermission;
import org.opengauss.mppdbide.utils.loader.MessagePropertiesLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.MessageQueue;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class TableTest extends BasicJDBCTestCaseAdapter
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
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        this.dbconn = CommonLLTUtils.getDBConnection();
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
        CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
        CommonLLTUtils.fetchViewColumnInfo(preparedstatementHandler);
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
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo,status);
        
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
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
            
            Alias alias1=new Alias("Alias1",OBJECTTYPE.TABLEMETADATA);
            alias1.getAutoSuggestionName(true);
            
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
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
            tablemetaData.execCreate(database.getConnectionManager().getObjBrowserConn());
            assertNotNull(indexMetaData.getNamespace());

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
            tablemetaData.getServer();
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
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            tablemetaData.execDrop(database.getConnectionManager().getObjBrowserConn());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_table_method()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            assertNotNull(tablemetaData);
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void testTTA_BL_TABLE_FUNC_001_009()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);

            tablemetaData.execAnalyze(database.getConnectionManager().getObjBrowserConn());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_010()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getCountRS(preparedstatementHandler);
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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            tablemetaData.execCreateIndex(indexMetaData,
                    database.getConnectionManager().getObjBrowserConn());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_011()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            tablemetaData.execReindex(database.getConnectionManager().getObjBrowserConn());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_012()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            tablemetaData.execRename("NewTable", database.getConnectionManager().getObjBrowserConn());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_013()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            tablemetaData.execSetSchema("PUBLIC", database.getConnectionManager().getObjBrowserConn());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_014()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            tablemetaData.execSetTableDescription("Some description",
                    database.getConnectionManager().getObjBrowserConn());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_015()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            tablemetaData.execSetTableSpace("TableSpace",
                    database.getConnectionManager().getObjBrowserConn());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_016()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            tablemetaData.execTruncate(database.getConnectionManager().getObjBrowserConn());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_017()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            tablemetaData.execVacumm(database.getConnectionManager().getObjBrowserConn());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_018()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            tablemetaData.formIndexQueries();
            tablemetaData.execCreateIndex(indexMetaData,
                    database.getConnectionManager().getObjBrowserConn());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_019()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            assertNotNull(tablemetaData);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_020()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
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
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            CommonLLTUtils.getCountRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            assertNotNull(tablemetaData.getConstraints());
            assertNotNull(tablemetaData.getDescription());
            assertNotNull(tablemetaData.getIndexes());
            assertNotNull(tablemetaData.getTablespaceName());
            tablemetaData.moveColumn(0, false);
            tablemetaData.refresh(database.getConnectionManager().getObjBrowserConn());
          /*  tablemetaData.removeConstraint(0);
            tablemetaData.removeIndex(0);*/
            tablemetaData.rollbackTableCreate(0, database.getConnectionManager().getObjBrowserConn());
            tablemetaData.setFillfactor(100);
            tablemetaData.setTablespaceName("TblSpace");
            tablemetaData.setUnLoggedTable(false);
            //tablemetaData.move_column(1, true);
            assertEquals("TblSpace", tablemetaData.getTablespaceName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    

    public void testTTA_BL_TABLE_FUNC_001_021()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            CommonLLTUtils.getCountRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            /*
             * assertNotNull(tablemetaData.getConstraints());
             * assertEquals(1,tablemetaData.getCurrentPage());
             * assertNotNull(tablemetaData.getDescription());
             * assertNotNull(tablemetaData.getIndexes());
             * assertNotNull(tablemetaData.getLastPageNumber());
             * assertNotNull(tablemetaData.getTablespaceName());
             * assertFalse(tablemetaData.isColumnStore());
             * tablemetaData.move_column(0, false);
             */
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            tablemetaData.refresh(database.getConnectionManager().getObjBrowserConn());
            /*tablemetaData.removeConstraint(0);*/
            tablemetaData.rollbackTableCreate(0, database.getConnectionManager().getObjBrowserConn());
            tablemetaData.setFillfactor(100);
            tablemetaData.setTablespaceName("TblSpace");
            tablemetaData.setUnLoggedTable(false);
            //tablemetaData.move_column(1, true);

            //tablemetaData.rollbackTableCreate(1, database.getObjBrowserConn());
            assertEquals("TblSpace", tablemetaData.getTablespaceName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_022()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            CommonLLTUtils.getCountRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            /*
             * tablemetaData.getConstraints(); tablemetaData.getCurrentPage();
             * tablemetaData.getDescription(); tablemetaData.getIndexes();
             * tablemetaData.getLastPageNumber();
             * tablemetaData.getTablespaceName(); tablemetaData.isColumnStore();
             * tablemetaData.move_column(0, false);
             */
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            tablemetaData.refresh(database.getConnectionManager().getObjBrowserConn());
            //tablemetaData.removeConstraint(0);
            tablemetaData.rollbackTableCreate(0, database.getConnectionManager().getObjBrowserConn());
            tablemetaData.setFillfactor(100);
            tablemetaData.setTablespaceName("TblSpace");
            tablemetaData.setUnLoggedTable(false);
            //tablemetaData.move_column(1, true);

            statementHandler
                    .prepareThrowsSQLException("DROP INDEX IF EXISTS pg_catalog.Idx1;");

           // tablemetaData.rollbackTableCreate(1, database.getObjBrowserConn());
            assertEquals(1, tablemetaData.getIndexes().getSize());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_222()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            CommonLLTUtils.getCountRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            /*
             * tablemetaData.getConstraints(); tablemetaData.getCurrentPage();
             * tablemetaData.getDescription(); tablemetaData.getIndexes();
             * tablemetaData.getLastPageNumber();
             * tablemetaData.getTablespaceName(); tablemetaData.isColumnStore();
             * tablemetaData.move_column(0, false);
             */
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            tablemetaData.refresh(database.getConnectionManager().getObjBrowserConn());
           // tablemetaData.removeConstraint(0);
            tablemetaData.rollbackTableCreate(0, database.getConnectionManager().getObjBrowserConn());
            tablemetaData.setFillfactor(100);
            tablemetaData.setTablespaceName("TblSpace");
            tablemetaData.setUnLoggedTable(false);
            //tablemetaData.move_column(1, true);

            statementHandler.prepareThrowsSQLException(
                    "DROP TABLE IF EXISTS pg_catalog.\"MyTable\";",
                    new SQLException());
            int currentSize = database.getNameSpaceById(1).getTables()
                    .getSize();
           // tablemetaData.rollbackTableCreate(1, database.getObjBrowserConn());
            assertEquals(currentSize, database.getNameSpaceById(1).getTables()
                    .getSize());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_TABLE_EXCEPTIONS_FUNC_001_112()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);

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
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            String query = "CREATE INDEX \"Idx1\" ON pg_catalog.\"MyTable\" () ;";
            preparedstatementHandler.prepareThrowsSQLException(query,
                    new SQLException());
            tablemetaData.execCreate(database.getConnectionManager().getObjBrowserConn());
            fail("Not excepted to come here");
            // database.getNameSpaceById(1).fetchlevel3DataForDisplay(1);
        }
        catch (DatabaseOperationException e)
        {
            assertEquals(
                    "Update failed. No rows affected. Cancel changes, refresh the data and try again.",
                    e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /*
     * @Test public void testTTA_BL_TABLE_FUNC_001_025() { try {
     * CommonLLTUtils.createTableRS1(preparedstatementHandler);
     * 
     * Database database = connProfCache.getDbForProfileId(profileId);
     * TableMetaData tablemetaData = new TableMetaData(1, "Table1",
     * database.getNameSpaceById(1), "tablespace");
     * tablemetaData.setTempTable(true); tablemetaData.setIfExists(true);
     * tablemetaData.setName("MyTable"); tablemetaData.setHasOid(true);
     * tablemetaData.setDistributeOptions("HASH");
     * tablemetaData.setNodeOptions("Node1");
     * tablemetaData.setDescription("Table description");
     * 
     * ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
     * "MyConstarint", CONSTRAINT_TYPE.UNIQUE_KEY_CONSTRSINT);
     * 
     * tablemetaData.addConstraint(constraintMetaData);
     * 
     * ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1,
     * "Col1", new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
     * tablemetaData.getColumns().addItem(newTempColumn);
     * 
     * ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1,
     * "Col2", new TypeMetaData(1, "text", database.getNameSpaceById(1)));
     * tablemetaData.getColumns().addItem(newTempColumn1);
     * 
     * IndexMetaData indexMetaData = new IndexMetaData("Idx1");
     * 
     * indexMetaData.setTable(tablemetaData);
     * indexMetaData.setNamespace(tablemetaData.getNamespace());
     * tablemetaData.addIndex(indexMetaData);
     * 
     * tablemetaData.setFillfactor(89); tablemetaData.setHasOid(true);
     * tablemetaData.setColumnStore(true);
     * 
     * tablemetaData.refresh(database.getObjBrowserConn());
     * fail("Not expected to come here"); } catch(DatabaseOperationException e)
     * { System.out.println("as expected"); } catch (Exception e) {
     * e.printStackTrace(); fail(e.getMessage()); } }
     */

    @Test
    public void testTTA_BL_TABLE_FUNC_001_026()
    {
        try
        {
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

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            tablemetaData.setFillfactor(89);
            tablemetaData.setHasOid(true);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            CommonLLTUtils.getCountRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);

            CommonLLTUtils.createTableRS1(preparedstatementHandler);
            // tablemetaData.refresh(database.getObjBrowserConn());
            CommonLLTUtils.createTableRS2(preparedstatementHandler);
            tablemetaData.refresh(database.getConnectionManager().getObjBrowserConn());
            CommonLLTUtils.createTableRS3(preparedstatementHandler);
            tablemetaData.refresh(database.getConnectionManager().getObjBrowserConn());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_TABLE_EXCEPTIONS_FUNC_001_012()
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
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionGetInt(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    exceptionConnection);

            database.connectToServer();

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            DebugObjects debugObject = new DebugObjects(1, "name",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            debugObject.setNamespace(namespace);
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
    public void testTTA_BL_TABLE_EXCEPTIONS_FUNC_001_015()
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
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionGetInt(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    exceptionConnection);

            database.connectToServer();

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            DebugObjects debugObject = new DebugObjects(1, "name",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            debugObject.setNamespace(namespace);
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
    public void testTTA_BL_TABLE_EXCEPTIONS_FUNC_001_013_01()
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
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionGetString(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    exceptionConnection);

            database.connectToServer();

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            DebugObjects debugObject = new DebugObjects(1, "name",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            debugObject.setNamespace(namespace);

        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_TABLE_EXCEPTIONS_FUNC_001_013_02()
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
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionGetString(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    exceptionConnection);
            database.connectToServer();

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            DebugObjects debugObject = new DebugObjects(1, "name",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            debugObject.setNamespace(namespace);
            TableMetaData tableMetaData = new TableMetaData(namespace);
            tableMetaData.setLevel3Loaded(true);
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_TABLE_FUNC_001_027()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), null);
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
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            indexMetaData.setTable(tablemetaData);

            tablemetaData.addIndex(indexMetaData);

            tablemetaData.setFillfactor(89);
            tablemetaData.setHasOid(true);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            CommonLLTUtils.getCountRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);

            tablemetaData.refresh(database.getConnectionManager().getObjBrowserConn());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_030()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
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
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);

            CommonLLTUtils.selectFromTableRS(preparedstatementHandler);
            CommonLLTUtils.getCountRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            tablemetaData.getConstraints();
            tablemetaData.getDescription();
            tablemetaData.getIndexes();
            tablemetaData.getTablespaceName();
            tablemetaData.moveColumn(0, false);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            tablemetaData.refresh(database.getConnectionManager().getObjBrowserConn());
            tablemetaData.rollbackTableCreate(0, database.getConnectionManager().getObjBrowserConn());
            tablemetaData.setFillfactor(100);
            tablemetaData.setTablespaceName("TblSpace");
            tablemetaData.setUnLoggedTable(false);
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptioForStmt(true);
            exceptionConnection.setThrowoutofmemerrorinrs(true);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    exceptionConnection);
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
            tablemetaData.execCreate(database.getConnectionManager().getObjBrowserConn());
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
    public void testTTA_BL_TABLE_FUNC_001_031()
    {
        try
        {
            MessagePropertiesLoader mp = new MessagePropertiesLoader(null);
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }


    @Test
    public void testTTA_BL_TABLE_COV_001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
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
            tablemetaData.isUnLoggedTable();
            tablemetaData.getServerName();
            tablemetaData.getDatabaseName();

            TableMetaData tablemetaData1 = new TableMetaData(1, "Table1", null,
                    "tablespace");
            assertEquals("", tablemetaData1.getServerName());
            assertEquals("", tablemetaData1.getDatabaseName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_MOVE__001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

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

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1,
                    "Col1", new TypeMetaData(1, "bigint",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData,
                    1, "Col2", new TypeMetaData(1, "text",
                            database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);
//
//            IndexMetaData indexMetaData = new IndexMetaData("Idx1");
//
//            indexMetaData.setTable(tablemetaData);
//            indexMetaData.setNamespace(tablemetaData.getNamespace());
//            tablemetaData.addIndex(indexMetaData);

            tablemetaData.moveColumn(1, true);
            newTempColumn1.getDisplayName();
          //  tablemetaData.refresh(database.getObjBrowserConn());
          /*  tablemetaData.removeConstraint(0);
            tablemetaData.removeIndex(0);*/
            assertEquals("Col2", newTempColumn1.getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
   
    @Test
    public void test_visualplan_01()
    {
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        ExplainPlanPresentation presentation = null;
        String query = "select * from Table1;";
        String query1 =
                "explain (analyze, verbose, format json, costs true, cpu true, buffers true, timing true) select * from Table1";
        MockResultSet colmetadataRS1 = preparedstatementHandler.createResultSet();
        colmetadataRS1.addColumn("QUERY PLAN");
        colmetadataRS1.addRow(new Object[] {
            "[{\"Plan\": {\"Node Type\": \"Data Node Scan\",\"Primary node count\": 0,\"Node count\": 3,\"Startup Cost\": 0.00,\"Total Cost\": 0.00,\"Plan Rows\": 0,\"Plan Width\": 0,\"Actual Startup Time\": 3.081,\"Actual Total Time\": 3.081,\"Actual Rows\": 0,\"Actual Loops\": 1,\"Output\": [\"table1.col1\", \"table1.col2\"],\"Nodes\": \"All datanodes\",\"Remote query\": \"SELECT col1, col2 FROM public.table1\",\"Shared Hit Blocks\": 0,\"Shared Read Blocks\": 0,\"Shared Dirtied Blocks\": 0,\"Shared Written Blocks\": 0,\"Local Hit Blocks\": 0,\"Local Read Blocks\": 0,\"Local Dirtied Blocks\": 0,\"Local Written Blocks\": 0,\"Temp Read Blocks\": 0,\"Temp Written Blocks\": 0,\"IO Read Time\": 0.000,\"IO Write Time\": 0.000,\"Exclusive Cycles Per Row\": 0,\"Exclusive Cycles\": 9241332,\"Inclusive Cycles\": 9241332},\"Triggers\": [],\"Total Runtime\": 3.143}]"});
        preparedstatementHandler.prepareResultSet(query1, colmetadataRS1);
        ArrayList<String> queryArray = new ArrayList<String>(4);
        queryArray.add("select * from Table1");
        MessageQueue msgQ = new MessageQueue();
        try
        {
            presentation = new ExplainPlanPresentation(query, query, msgQ,
                    database.getConnectionManager().getSqlTerminalConn(), database);
            presentation.doExplainPlanAnalysis(false);
            assertNotNull(presentation.getAllDNViewofPlan());
            assertNotNull(presentation.getPlanNodeNames());
        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
        }
        ExplainPlanOverAllPlanPropertiesCore propertiesCore = new ExplainPlanOverAllPlanPropertiesCore(presentation);
        ExplainPlanOverAllProperties overallProperties = new ExplainPlanOverAllProperties(presentation, propertiesCore);
        assertEquals(null, overallProperties.getDatabase());
        try
        {
            assertNotNull(overallProperties.getAllProperties(database.getConnectionManager().getSqlTerminalConn()));
        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_visualplan_02()
    {
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        ExplainPlanPresentation presentation = null;
        String jsonString = "[{\"Plan\": {\"Node Type\": \"Streaming (type: GATHER)\",\"Startup Cost\": 74.28,"
                + "\"Total Cost\": 123.37,\"Plan Rows\": 30,\"Plan Width\": 268,\"Actual Startup Time\": 33.401,"
                + "\"Actual Total Time\": 34.134,\"Actual Rows\": 150,\"Actual Loops\": 1,\"Output\": [\"comp.name\", "
                + "\"cindex.idx_col\", \"ng.dob\", \"part.part_range\", \"cpart.part_range\"],\"Nodes\": "
                + "\"All datanodes\",\"Shared Hit Blocks\": 1,\"Shared Read Blocks\": 2,\"Shared Dirtied Blocks\": 3,"
                + "\"Shared Written Blocks\": 4,\"Local Hit Blocks\": 5,\"Local Read Blocks\": 6,\"Local Dirtied Blocks\": 7,"
                + "\"Local Written Blocks\": 8,\"Temp Read Blocks\": 9,\"Temp Written Blocks\": 10,\"IO Read Time\": 0.000,"
                + "\"IO Write Time\": 0.000}}]";
        String query = "select * from Table1;";
        String query1 =
                "explain (analyze, verbose, format json, costs true, cpu true, buffers true, timing true) select * from Table1";
        MockResultSet colmetadataRS1 = preparedstatementHandler.createResultSet();
        colmetadataRS1.addColumn("QUERY PLAN");
        colmetadataRS1.addRow(new Object[] {
            "[{\"Plan\": {\"Node Type\": \"Data Node Scan\",\"Primary node count\": 0,\"Node count\": 3,\"Startup Cost\": 0.00,\"Total Cost\": 0.00,\"Plan Rows\": 0,\"Plan Width\": 0,\"Actual Startup Time\": 3.081,\"Actual Total Time\": 3.081,\"Actual Rows\": 0,\"Actual Loops\": 1,\"Output\": [\"table1.col1\", \"table1.col2\"],\"Nodes\": \"All datanodes\",\"Remote query\": \"SELECT col1, col2 FROM public.table1\",\"Shared Hit Blocks\": 0,\"Shared Read Blocks\": 0,\"Shared Dirtied Blocks\": 0,\"Shared Written Blocks\": 0,\"Local Hit Blocks\": 0,\"Local Read Blocks\": 0,\"Local Dirtied Blocks\": 0,\"Local Written Blocks\": 0,\"Temp Read Blocks\": 0,\"Temp Written Blocks\": 0,\"IO Read Time\": 0.000,\"IO Write Time\": 0.000,\"Exclusive Cycles Per Row\": 0,\"Exclusive Cycles\": 9241332,\"Inclusive Cycles\": 9241332},\"Triggers\": [],\"Total Runtime\": 3.143}]"});
        preparedstatementHandler.prepareResultSet(query1, colmetadataRS1);
        ArrayList<String> queryArray = new ArrayList<String>(4);
        queryArray.add("select * from Table1");
        MessageQueue msgQ = new MessageQueue();
        try
        {
            presentation = new ExplainPlanPresentation(query, query, msgQ,
                    database.getConnectionManager().getSqlTerminalConn(), database);
            ExplainPlanAnlysisService planAnalysis = new ExplainPlanAnlysisService(jsonString);
            presentation.setAnalysedPlanOutput(UIModelConverter.covertToUIModel(planAnalysis.doAnalysis()));
            presentation.doExplainPlanAnalysis(true);
            presentation.getAllQuery();
            presentation.getDatabase();

        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_AnalysedPlanNodeProperties_methods_01()
    {
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
        CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
        try
        {
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            String jsonString = "[{\"Plan\": {\"Node Type\": \"Streaming (type: GATHER)\",\"Startup Cost\": 74.28,"
                    + "\"Total Cost\": 123.37,\"Plan Rows\": 30,\"Plan Width\": 268,\"Actual Startup Time\": 33.401,"
                    + "\"Actual Total Time\": 34.134,\"Actual Rows\": 150,\"Actual Loops\": 1,\"Output\": [\"comp.name\", "
                    + "\"cindex.idx_col\", \"ng.dob\", \"part.part_range\", \"cpart.part_range\"],\"Nodes\": "
                    + "\"All datanodes\",\"Shared Hit Blocks\": 1,\"Shared Read Blocks\": 2,\"Shared Dirtied Blocks\": 3,"
                    + "\"Shared Written Blocks\": 4,\"Local Hit Blocks\": 5,\"Local Read Blocks\": 6,\"Local Dirtied Blocks\": 7,"
                    + "\"Local Written Blocks\": 8,\"Temp Read Blocks\": 9,\"Temp Written Blocks\": 10,\"IO Read Time\": 0.000,"
                    + "\"IO Write Time\": 0.000}}]";
            ExplainPlanAnlysisService planAnalysis = new ExplainPlanAnlysisService(jsonString);
            UIModelAnalysedPlanNode node = null;

            node = UIModelConverter.covertToUIModel(planAnalysis.doAnalysis());
  
            AnalysedPlanNode analysedPlanNode = node.getAnalysedPlanNode();
            PropertyHandlerCore core = new PropertyHandlerCore(tablemetaData);
            AnalysedPlanNodeProperties analysedPlanNodeProps = new AnalysedPlanNodeProperties(analysedPlanNode, core);
            assertEquals("Streaming (type: GATHER)", analysedPlanNodeProps.getObjectName());
            assertEquals("1. Streaming (type: GATHER)", analysedPlanNodeProps.getHeader());
            assertEquals("1", analysedPlanNodeProps.getUniqueID());
            assertNotNull(analysedPlanNodeProps.getAllProperties(database.getConnectionManager().getSqlTerminalConn()));
            assertNull(analysedPlanNodeProps.getDatabase());
            assertEquals(core, analysedPlanNodeProps.getPropertyCore());
            assertNotNull(analysedPlanNodeProps.objectproperties());
        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testTTA_BL_TABLE_FUNC_BatchDrop_001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
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
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            tablemetaData.execCreate(database.getConnectionManager().getObjBrowserConn());
            assertNotNull(indexMetaData.getNamespace());
            
            assertEquals(tablemetaData.isDropAllowed(), true);
            assertEquals("Table", tablemetaData.getObjectTypeName());
            assertEquals("pg_catalog.\"MyTable\"", tablemetaData.getObjectFullName());
            String dropQry = tablemetaData.getDropQuery(false);
            assertEquals("DROP TABLE IF EXISTS pg_catalog.\"MyTable\"", dropQry);
            
            dropQry = tablemetaData.getDropQuery(true);
            assertEquals("DROP TABLE IF EXISTS pg_catalog.\"MyTable\" CASCADE", dropQry);
            
            // Test Remove of Table Column, Index, Constraint
            ColumnList list = (ColumnList) tablemetaData.getColumns();
            assertEquals(list.getSize(), 2);

            tablemetaData.remove(list.getItem(0));
            list = (ColumnList) tablemetaData.getColumns();
            assertEquals(list.getSize(), 1);
            
            ConstraintList list1 = (ConstraintList) tablemetaData.getConstraints();
            assertEquals(list1.getSize(), 1);
            
            tablemetaData.remove(list1.getItem(0));
            list1 = (ConstraintList) tablemetaData.getConstraints();
            assertEquals(list1.getSize(), 0);
            
            IndexList list2 = tablemetaData.getIndexes();
            assertEquals(list2.getSize(), 1);
            
            tablemetaData.remove(list2.getItem(0));
            list2 = tablemetaData.getIndexes();
            assertEquals(list2.getSize(), 0);
            
            
            // Remove the Object from namespace also
            Namespace ns = database.getNameSpaceById(1);
            
            TableObjectGroup tog = ns.getTablesGroup();
            assertEquals(1, tog.getSize());
            
            ns.remove(tablemetaData);
            
            tog = ns.getTablesGroup();
            assertEquals(0, tog.getSize());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
	@Test
	public void test_AnalysedPlanNodeProperties_methods_02() {
		CommonLLTUtils.createTableRS(preparedstatementHandler);
		CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
		CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
		Database database = connProfCache.getDbForProfileId(profileId);
		database.getServer().setServerCompatibleToNodeGroup(true);
		try {
			String jsonString = "[{\"Plan\": {\"Node Type\": \"Streaming (type: GATHER)\",\"Startup Cost\": 74.28,"
					+ "\"Total Cost\": 123.37,\"Plan Rows\": 30,\"Plan Width\": 268,\"Actual Startup Time\": 33.401,"
					+ "\"Actual Total Time\": 34.134,\"Actual Rows\": 150,\"Actual Loops\": 1,\"Output\": [\"comp.name\", "
					+ "\"cindex.idx_col\", \"ng.dob\", \"part.part_range\", \"cpart.part_range\"],\"Nodes\": "
					+ "\"All datanodes\",\"Shared Hit Blocks\": 1,\"Shared Read Blocks\": 2,\"Shared Dirtied Blocks\": 3,"
					+ "\"Shared Written Blocks\": 4,\"Local Hit Blocks\": 5,\"Local Read Blocks\": 6,\"Local Dirtied Blocks\": 7,"
					+ "\"Local Written Blocks\": 8,\"Temp Read Blocks\": 9,\"Temp Written Blocks\": 10,\"IO Read Time\": 0.000,"
					+ "\"IO Write Time\": 0.000}}]";
			ExplainPlanAnlysisService planAnalysis = new ExplainPlanAnlysisService(jsonString);
			UIModelAnalysedPlanNode node = null;

			node = UIModelConverter.covertToUIModel(planAnalysis.doAnalysis());
			AnalysedPlanNode analysedPlanNode = node.getAnalysedPlanNode();
			ExplainPlanNodePropertiesCore explainPlnNdePropCore = new ExplainPlanNodePropertiesCore(analysedPlanNode);
			IWindowDetail windowDetail = explainPlnNdePropCore.getWindowDetails();
			assertEquals("1. Streaming (type: GATHER)", windowDetail.getTitle());
			assertEquals("1", windowDetail.getUniqueID());
			assertNull(windowDetail.getIcon());
			assertEquals("1. Streaming (type: GATHER) - DN Details", windowDetail.getShortTitle());
			ExplainPlanNodeDetails explainPlnNdeDetails = new ExplainPlanNodeDetails(analysedPlanNode);
			assertEquals("1. Stream", explainPlnNdeDetails.getNodeTitle());
			assertEquals(1, explainPlnNdeDetails.getNodeSequenceNum());
		} catch (DatabaseOperationException e) {
			e.printStackTrace();
		}
	}
	
	@Test
    public void test_AnalysedPlanNodeProperties_methods_03() {
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
        CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
        try {
            String jsonString = "[{\"Plan\": {\"Node Type\": \"Streaming (type: GATHER)\",\"Startup Cost\": 74.28,"
                    + "\"Total Cost\": 123.37,\"Plan Rows\": 30,\"Plan Width\": 268,\"Actual Startup Time\": 33.401,"
                    + "\"Actual Total Time\": 34.134,\"Actual Rows\": 150,\"Actual Loops\": 1,\"Output\": [\"comp.name\", "
                    + "\"cindex.idx_col\", \"ng.dob\", \"part.part_range\", \"cpart.part_range\"],\"Nodes\": "
                    + "\"All datanodes\",\"Shared Hit Blocks\": 1,\"Shared Read Blocks\": 2,\"Shared Dirtied Blocks\": 3,"
                    + "\"Shared Written Blocks\": 4,\"Local Hit Blocks\": 5,\"Local Read Blocks\": 6,\"Local Dirtied Blocks\": 7,"
                    + "\"Local Written Blocks\": 8,\"Temp Read Blocks\": 9,\"Temp Written Blocks\": 10,\"IO Read Time\": 0.000,"
                    + "\"IO Write Time\": 0.000}}]";
            ExplainPlanAnlysisService planAnalysis = new ExplainPlanAnlysisService(jsonString);
            UIModelAnalysedPlanNode node = null;

            node = UIModelConverter.covertToUIModel(planAnalysis.doAnalysis());
            AnalysedPlanNode analysedPlanNode = node.getAnalysedPlanNode();
            ExplainPlanNodePropertiesCore explainPlnNdePropCore = new ExplainPlanNodePropertiesCore(analysedPlanNode);
            explainPlnNdePropCore.setExplainPlanType(1);
            assertEquals(explainPlnNdePropCore.getExplainPlanType(), 1);
            assertEquals(explainPlnNdePropCore.isExecutable(), true);
            assertNotNull(explainPlnNdePropCore.getTermConnection());
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
        }
	}
    @Test
    public void test_visualplan_001() {
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        ExplainPlanPresentation presentation = null;
        String query = "select * from Table1;";
        MessageQueue msgQ = new MessageQueue();
        Path pathFileName = null;
        FileOutputStream fileOutputStream = null;
        try {
            ISetFilePermission withPermissionLogFileName = FilePermissionFactory.getFilePermissionInstance();
            pathFileName = withPermissionLogFileName.createFileWithPermission(MPPDBIDEConstants.JSON_PLAN_DUMP_FILE,
                    false, null, true);
            if (Files.isWritable(pathFileName) || pathFileName.toFile().exists()) {
                try {
                    String sampleJson = "[{\"Plan\": {\"Node Type\": \"Aggregate\","
                            + "\"Strategy\": \"Hashed\",\"Parent Relationship\": \"Outer\","
                            + "\"Group By Key\": [\"grp1\", \"grp2\"],"
                            + "\"Aggregate Detail\": [{\"DN Name\": \"DB1\", \"Temp File Num\": 12 }],"
                            + "\"Filter\": \"filter cond\"," + "\"Rows Removed by Filter\": 2,"
                            + "\"Max File Num\": 2, \"Min File Num\": 0}}]";
                    byte[] stringTobytes = sampleJson.getBytes();
                    fileOutputStream = new FileOutputStream(pathFileName.toFile());
                    fileOutputStream.write(stringTobytes);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            presentation = new ExplainPlanPresentation(query, query, msgQ,
                    database.getConnectionManager().getSqlTerminalConn(), database);

            presentation.doExplainPlanAnalysis(true);
            assertNotNull(presentation.getAllDNViewofPlan());
            assertNotNull(presentation.getPlanNodeNames());
        } catch (MPPDBIDEException e) {
            e.printStackTrace();
        } finally {
            try {
                Files.deleteIfExists(pathFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}