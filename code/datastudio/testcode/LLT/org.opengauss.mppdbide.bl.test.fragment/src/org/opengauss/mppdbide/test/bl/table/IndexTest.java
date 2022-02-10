package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
import org.opengauss.mppdbide.bl.serverdatacache.ForeignKeyActionType;
import org.opengauss.mppdbide.bl.serverdatacache.ForeignKeyMatchType;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.IndexedColumnExpr;
import org.opengauss.mppdbide.bl.serverdatacache.IndexedColumnType;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.Tablespace;
import org.opengauss.mppdbide.bl.serverdatacache.TablespaceType;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class IndexTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection               = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler         statementHandler         = null;
    
    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache connProfCache = null;
    ConnectionProfileId profileId = null;
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

        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();
        
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
        CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
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
        //serverInfo.setSslPassword("12345");
        //serverInfo.setServerType(DATABASETYPE.GAUSS);
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
        
        while(itr.hasNext())
        {
            connProfCache.removeServer(itr.next().getId());
        }
        
        connProfCache.closeAllNodes();
        
    }
    
    
    
    @Test
    public void testTTA_BL_INDEX_FUNC_001_001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getViewMockRS(preparedstatementHandler);
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            
            IndexMetaData indexMetaData = new IndexMetaData("Idx1");
            indexMetaData.setTable(tablemetaData);
            
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            
            IndexedColumnExpr columnExpr = new IndexedColumnExpr(IndexedColumnType.COLUMN);
            columnExpr.setExpr("expr");
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData, 1, "Col1", new TypeMetaData(2, "integer", tablemetaData.getNamespace()));
            columnExpr.setCol(columnMetaData);
            ArrayList<IndexedColumnExpr> columnExprs = new ArrayList<IndexedColumnExpr>();
            columnExprs.add(columnExpr);
            indexMetaData.setIndexedColumns(columnExprs);
            
            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1, "MyConstarint", ConstraintType.CHECK_CONSTRSINT);
            
            tablemetaData.addConstraint(constraintMetaData);
            
            
            
            constraintMetaData = new ConstraintMetaData(2, "MyConstarint2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            constraintMetaData.setPkeyOrUkeyConstraint("col1", "tableSpace");
            tablemetaData.addConstraint(constraintMetaData);
            
            constraintMetaData = new ConstraintMetaData(3, "MyConstarint3", ConstraintType.FOREIGN_KEY_CONSTRSINT);
            constraintMetaData.setRefernceTable(tablemetaData);
            constraintMetaData.setFkActions(ForeignKeyMatchType.FK_MATCH_FULL, ForeignKeyActionType.FK_CASCADE, ForeignKeyActionType.FK_CASCADE);
            
            constraintMetaData.setReferenceIndex(indexMetaData);
            
            tablemetaData.addConstraint(constraintMetaData);
            
            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1", new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);
            
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1, "Col2", new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);
            newTempColumn1.getCheckConstraintExpr();
            newTempColumn1.getColDataTypeSchema();
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            
            tablemetaData.addIndex(indexMetaData);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            tablemetaData.execCreate(database.getConnectionManager().getObjBrowserConn());
            
            indexMetaData.addExpr("expr");
            //indexMetaData.addExprAt("expr", 0);
            indexMetaData.changeFillFactor(86,database.getConnectionManager().getObjBrowserConn());
            indexMetaData.changeTablespace("tblspc",database.getConnectionManager().getObjBrowserConn());
            indexMetaData.drop(database.getConnectionManager().getObjBrowserConn());
            indexMetaData.formDropQuery(true);
            indexMetaData.getAccessMethod();
            indexMetaData.getFillFactor();
           // indexMetaData.getIndexedColumnsAt(0);
            indexMetaData.getNamespace();
            indexMetaData.getTable();
            indexMetaData.getTablespace();
            indexMetaData.getWhereExpr();
            indexMetaData.isCheckxmin();
            indexMetaData.isExclusion();
            indexMetaData.isLastClustered();
            indexMetaData.isPrimary();
            indexMetaData.isReady();
            indexMetaData.isLoaded();
            indexMetaData.getDatabase();
            indexMetaData.rename("MyIdx",database.getConnectionManager().getObjBrowserConn());
            indexMetaData.getIndexdeff();
            indexMetaData.setAccessMethod(new AccessMethod(1, "AM"));
            indexMetaData.setIndexFillFactor(56);
            indexMetaData.setTablespace(new Tablespace(1, "/home/dsdev/shalini", "tablespace1", "10K",
                    new String[] {"filesystem=general", "random_page_cost=2", "seq_page_cost=2"}, database.getServer(),
                    TablespaceType.NORMAL,true, false));
            indexMetaData.setWhereExpr("expr");
            indexMetaData.setUnique(true);
            indexMetaData.formCreateQuery(false);
            indexMetaData.isImmediate();
            //indexMetaData.getColumnsString();
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_INDEX_FUNC_BatchDrop_001()
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
            constraintMetaData.setTable(tablemetaData);

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
            
            assertEquals(indexMetaData.isDropAllowed(), true);
            assertEquals("Index", indexMetaData.getObjectTypeName());
            assertEquals("pg_catalog.\"Idx1\"", indexMetaData.getObjectFullName());
            String dropQry = indexMetaData.getDropQuery(false);
            assertEquals("DROP INDEX IF EXISTS pg_catalog.\"Idx1\"", dropQry);
            
            dropQry = indexMetaData.getDropQuery(true);
            assertEquals("DROP INDEX IF EXISTS pg_catalog.\"Idx1\" CASCADE", dropQry);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
