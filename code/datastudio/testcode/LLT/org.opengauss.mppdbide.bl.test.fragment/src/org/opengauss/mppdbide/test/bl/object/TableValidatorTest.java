package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TableOrientation;
import org.opengauss.mppdbide.bl.serverdatacache.TableValidatorRules;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class TableValidatorTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

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
    public void testTTA_BL_TABLE_FUNC_001_002()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
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
            TableValidatorRules validatorRules=new TableValidatorRules(tablemetaData);
            
            tablemetaData.setOrientation(TableOrientation.ROW);
            ConstraintMetaData constraintMetaData=null;
            if(validatorRules.enableDisable())
            {

                constraintMetaData = new ConstraintMetaData(1, "MyConstarint",
                        ConstraintType.UNIQUE_KEY_CONSTRSINT);

                tablemetaData.addConstraint(constraintMetaData);

                ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData,
                        1, "Col1", new TypeMetaData(1, "bigint",
                                database.getNameSpaceById(1)));
                tablemetaData.getColumns().addItem(newTempColumn);

                ColumnMetaData newTempColumn1 = new ColumnMetaData(
                        tablemetaData, 1, "Col2", new TypeMetaData(1, "text",
                                database.getNameSpaceById(1)));
                tablemetaData.getColumns().addItem(newTempColumn1);
            }
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
            tablemetaData.execCreate(database.getConnectionManager().getObjBrowserConn());
            assertTrue(tablemetaData.getConstraintMetaDataList().size() > 0);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_TABLE_FUNC_001_001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
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
            TableValidatorRules validatorRules=new TableValidatorRules(tablemetaData);
            
            tablemetaData.setOrientation(TableOrientation.COLUMN);
            ConstraintMetaData constraintMetaData=null;
            if(validatorRules.enableDisable())
            {

                constraintMetaData = new ConstraintMetaData(1, "MyConstarint",
                        ConstraintType.UNIQUE_KEY_CONSTRSINT);

                tablemetaData.addConstraint(constraintMetaData);

                ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData,
                        1, "Col1", new TypeMetaData(1, "bigint",
                                database.getNameSpaceById(1)));
                tablemetaData.getColumns().addItem(newTempColumn);

                ColumnMetaData newTempColumn1 = new ColumnMetaData(
                        tablemetaData, 1, "Col2", new TypeMetaData(1, "text",
                                database.getNameSpaceById(1)));
                tablemetaData.getColumns().addItem(newTempColumn1);
            }
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
            tablemetaData.execCreate(database.getConnectionManager().getObjBrowserConn());
            assertTrue(tablemetaData.getConstraintMetaDataList().size() == 0);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testT_GetDataTypeList_ROW()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
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
            TableValidatorRules validatorRules=new TableValidatorRules(tablemetaData);
            
            tablemetaData.setOrientation(TableOrientation.ROW);
//            ConstraintMetaData constraintMetaData=null;
//           
//                constraintMetaData = new ConstraintMetaData(1, "MyConstarint",
//                        CONSTRAINT_TYPE.UNIQUE_KEY_CONSTRSINT);
//
//                tablemetaData.addConstraint(constraintMetaData);
//
//                ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData,
//                        1, "Col1", new TypeMetaData(1, "bigint",
//                                database.getNameSpaceById(1)));
//                tablemetaData.getColumns().addItem(newTempColumn);
//
//                ColumnMetaData newTempColumn1 = new ColumnMetaData(
//                        tablemetaData, 1, "Col2", new TypeMetaData(1, "text",
//                                database.getNameSpaceById(1)));
//                tablemetaData.getColumns().addItem(newTempColumn1);
            
            validatorRules.getDataTypeList(database,true);
                
            
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
            tablemetaData.execCreate(database.getConnectionManager().getObjBrowserConn());
            assertTrue( validatorRules.getDataTypeList(database,true).size()==40);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testT_GetDataTypeList_COLUMN()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
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
            TableValidatorRules validatorRules = new TableValidatorRules(
                    tablemetaData);

            tablemetaData.setOrientation(TableOrientation.COLUMN);

            validatorRules.getDataTypeList(database, true);
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
            tablemetaData.execCreate(database.getConnectionManager().getObjBrowserConn());
            assertTrue(validatorRules.getDataTypeList(database, true)
                    .size() == 40);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }



}
