package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
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
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaDataUtils;
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
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class ConstraintTest extends BasicJDBCTestCaseAdapter
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
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        MockBLPreferenceImpl.setFileEncoding("UTF-8");

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        JobCancelStatus status=new JobCancelStatus();
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
    public void testTTA_BL_TABLE_FUNC_001_010()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
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

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");
            indexMetaData.setTable(tablemetaData);

            indexMetaData.setNamespace(database.getNameSpaceById(1));

            IndexedColumnExpr columnExpr = new IndexedColumnExpr(
                    IndexedColumnType.COLUMN);
            columnExpr.setExpr("expr");
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "Col1", new TypeMetaData(2, "integer",
                            tablemetaData.getNamespace()));
            columnExpr.setCol(columnMetaData);
            ArrayList<IndexedColumnExpr> columnExprs = new ArrayList<IndexedColumnExpr>();
            columnExprs.add(columnExpr);
            indexMetaData.setIndexedColumns(columnExprs);

            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                    "MyConstarint", ConstraintType.CHECK_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            constraintMetaData = new ConstraintMetaData(2, "MyConstarint2",
                    ConstraintType.UNIQUE_KEY_CONSTRSINT);
            constraintMetaData.setPkeyOrUkeyConstraint("col1", "tableSpace");
            tablemetaData.addConstraint(constraintMetaData);

            constraintMetaData = new ConstraintMetaData(3, "MyConstarint3",
                    ConstraintType.FOREIGN_KEY_CONSTRSINT);
            constraintMetaData.setRefernceTable(tablemetaData);
            constraintMetaData.setFkActions(
                    ForeignKeyMatchType.FK_MATCH_FULL,
                    ForeignKeyActionType.FK_CASCADE,
                    ForeignKeyActionType.FK_CASCADE);

            constraintMetaData.setReferenceIndex(indexMetaData);

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

            tablemetaData.addIndex(indexMetaData);
            
            CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
            tablemetaData.execCreate(this.dbconn);
            assertEquals("\"MyTable\"", database.getNameSpaceById(1)
                    .getTables().getObjectById(1).getQualifiedObjectName());

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

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");
            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(database.getNameSpaceById(1));

            IndexedColumnExpr columnExpr = new IndexedColumnExpr(
                    IndexedColumnType.COLUMN);
            columnExpr.setExpr("expr");
            ColumnMetaData columnMetaData = new ColumnMetaData(tablemetaData,
                    1, "Col1", new TypeMetaData(2, "integer",
                            tablemetaData.getNamespace()));
            columnExpr.setCol(columnMetaData);
            ArrayList<IndexedColumnExpr> columnExprs = new ArrayList<IndexedColumnExpr>();
            columnExprs.add(columnExpr);
            indexMetaData.setIndexedColumns(columnExprs);

            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                    "MyConstarint", ConstraintType.CHECK_CONSTRSINT);

            tablemetaData.addConstraint(constraintMetaData);

            constraintMetaData = new ConstraintMetaData(2, "MyConstarint2",
                    ConstraintType.UNIQUE_KEY_CONSTRSINT);
            constraintMetaData.setPkeyOrUkeyConstraint("col1", "tableSpace");
            tablemetaData.addConstraint(constraintMetaData);

            constraintMetaData = new ConstraintMetaData(3, "MyConstarint3",
                    ConstraintType.FOREIGN_KEY_CONSTRSINT);
            constraintMetaData.setRefernceTable(tablemetaData);
            constraintMetaData.setFkActions(
                    ForeignKeyMatchType.FK_MATCH_FULL,
                    ForeignKeyActionType.FK_CASCADE,
                    ForeignKeyActionType.FK_CASCADE);

            constraintMetaData.setReferenceIndex(indexMetaData);
            ;

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

            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            tablemetaData.addIndex(indexMetaData);

            tablemetaData.execCreate(this.dbconn);
            constraintMetaData.setTable(tablemetaData);
            constraintMetaData.execAlterAddConstraint(tablemetaData,
                    this.dbconn);
            constraintMetaData.execDrop(this.dbconn);
            constraintMetaData.execDeferableConstraint(this.dbconn);
            constraintMetaData.isCondeferred();
            constraintMetaData.isConvalidated();
            constraintMetaData.isDeferable();
            constraintMetaData.execValidateConstraint(this.dbconn);
            constraintMetaData.execRenameConstraint("newcon", this.dbconn);
            constraintMetaData.getConstraintType();
           
            constraintMetaData.getCheckConstraintExpr();
            constraintMetaData.getColumnList();
            constraintMetaData.getConstraintFillfactor();

            
            constraintMetaData.getNamespace();
            constraintMetaData.getTableSpace();
            constraintMetaData.setCheckConstraintExpr("expr");
            constraintMetaData.setFillfactor(56);

            constraintMetaData.setRerenceOptions(tablemetaData, indexMetaData);
            constraintMetaData.setDeffearableOptions(true, true);
            constraintMetaData.setFkActions(
                    ForeignKeyMatchType.FK_MATCH_FULL,
                    ForeignKeyActionType.FK_NO_ACTION,
                    ForeignKeyActionType.FK_NO_ACTION);
            constraintMetaData.formConstraintString();
            constraintMetaData.setFkActions(
                    ForeignKeyMatchType.FK_MATCH_DEFAULT,
                    ForeignKeyActionType.FK_RESTRICT,
                    ForeignKeyActionType.FK_RESTRICT);
            constraintMetaData.formConstraintString();
            constraintMetaData.setFkActions(
                    ForeignKeyMatchType.FK_MATCH_PARTIAL,
                    ForeignKeyActionType.FK_SET_DEFAULT,
                    ForeignKeyActionType.FK_SET_DEFAULT);
            constraintMetaData.formConstraintString();
            constraintMetaData.setFkActions(
                    ForeignKeyMatchType.FK_MATCH_SIMPLE,
                    ForeignKeyActionType.FK_SET_NULL,
                    ForeignKeyActionType.FK_SET_NULL);
            constraintMetaData.formConstraintString();
            constraintMetaData.getColumnList();
            assertEquals("CHECK_CONSTRSINT", ConstraintMetaDataUtils
                    .getConstraintType("c").toString());
            assertEquals("FOREIGN_KEY_CONSTRSINT", ConstraintMetaDataUtils
                    .getConstraintType("f").toString());
            assertEquals("PRIMARY_KEY_CONSTRSINT", ConstraintMetaDataUtils
                    .getConstraintType("p").toString());
            assertEquals("UNIQUE_KEY_CONSTRSINT", ConstraintMetaDataUtils
                    .getConstraintType("u").toString());
            assertEquals("EXCLUSION_CONSTRSINT", ConstraintMetaDataUtils
                    .getConstraintType("t").toString());
            assertEquals("FK_NO_ACTION", ConstraintMetaDataUtils.getActionType("a")
                    .toString());
            assertEquals("FK_RESTRICT", ConstraintMetaDataUtils.getActionType("r")
                    .toString());
            assertEquals("FK_CASCADE", ConstraintMetaDataUtils.getActionType("c")
                    .toString());
            assertEquals("FK_SET_NULL", ConstraintMetaDataUtils.getActionType("n")
                    .toString());
            assertEquals("FK_MATCH_FULL", ConstraintMetaDataUtils.getMatchType("f")
                    .toString());
            assertEquals("FK_MATCH_PARTIAL",
                    ConstraintMetaDataUtils.getMatchType("p").toString());
            assertEquals("FK_MATCH_SIMPLE", ConstraintMetaDataUtils
                    .getMatchType("z").toString());

        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }

    }

    /*
     * @Test public void testTTA_BL_TABLE_FUNC_001_011_1() { try {
     * CommonLLTUtils.createTableRS(preparedstatementHandler);
     * CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
     * CommonLLTUtils.getIndexForTableRS(preparedstatementHandler); Database
     * database = connProfCache.getDbForProfileId(profileId); TableMetaData
     * tablemetaData = new TableMetaData(1, "Table1",
     * database.getNameSpaceById(1), "tablespace");
     * tablemetaData.setTempTable(true); tablemetaData.setIfExists(true);
     * tablemetaData.setName("MyTable"); tablemetaData.setHasOid(true);
     * tablemetaData.setDistributeOptions("HASH");
     * tablemetaData.setNodeOptions("Node1");
     * tablemetaData.setDescription("Table description");
     * 
     * IndexMetaData indexMetaData = new IndexMetaData("Idx1");
     * indexMetaData.setTable(tablemetaData);
     * 
     * 
     * IndexedColumnExpr columnExpr = new IndexedColumnExpr(
     * INDEXED_COLUNM_TYPE.COLUMN); columnExpr.setExpr("expr"); ColumnMetaData
     * columnMetaData = new ColumnMetaData(tablemetaData, 1, "Col1", new
     * TypeMetaData(2, "integer", tablemetaData.getNamespace()));
     * columnExpr.setCol(columnMetaData); ArrayList<IndexedColumnExpr>
     * columnExprs = new ArrayList<IndexedColumnExpr>();
     * columnExprs.add(columnExpr);
     * indexMetaData.setIndexedColumns(columnExprs);
     * 
     * ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
     * "MyConstarint", CONSTRAINT_TYPE.CHECK_CONSTRSINT);
     * 
     * tablemetaData.addConstraint(constraintMetaData);
     * 
     * constraintMetaData = new ConstraintMetaData(2, "MyConstarint2",
     * CONSTRAINT_TYPE.UNIQUE_KEY_CONSTRSINT);
     * constraintMetaData.setPkeyOrUkeyConstraint("col1", "tableSpace");
     * tablemetaData.addConstraint(constraintMetaData);
     * 
     * constraintMetaData = new ConstraintMetaData(3, "MyConstarint3",
     * CONSTRAINT_TYPE.FOREIGN_KEY_CONSTRSINT);
     * constraintMetaData.setRefernce_table(tablemetaData);
     * constraintMetaData.setFkActions( FOREIGN_KEY_MATCH_TYPE.FK_MATCH_FULL,
     * FOREIGN_KEY_ACTION_TYPE.FK_CASCADE, FOREIGN_KEY_ACTION_TYPE.FK_CASCADE);
     * 
     * constraintMetaData.setReference_index(indexMetaData);
     * 
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
     * newTempColumn1.getCheckConstraintExpr();
     * newTempColumn1.getDataTypeSchema();
     * 
     * CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
     * indexMetaData.setNamespace(database.getNameSpaceById(1));
     * tablemetaData.addIndex(indexMetaData);
     * 
     * tablemetaData.execCreate(this.dbconn);
     * 
     * constraintMetaData.setTable(tablemetaData);
     * constraintMetaData.execAlterAddConstraint(tablemetaData, this.dbconn); //
     * tablemetaData.getNamespace().setName("\"MyConstraint"); //
     * tablemetaData.setName("\"MyConstraint");
     * constraintMetaData.setName("\"MyConstraint");
     * constraintMetaData.execDrop(this.dbconn);
     * constraintMetaData.execDeferableConstraint(this.dbconn);
     * constraintMetaData.isCondeferred(); constraintMetaData.isConvalidated();
     * constraintMetaData.isDeferable();
     * constraintMetaData.execValidateConstraint(this.dbconn);
     * constraintMetaData.execRenameConstraint("newcon", this.dbconn);
     * constraintMetaData.getConstraintType(); constraintMetaData
     * .getActionForCombo(FOREIGN_KEY_ACTION_TYPE.FK_CASCADE);
     * constraintMetaData
     * .getActionForCombo(FOREIGN_KEY_ACTION_TYPE.FK_NO_ACTION);
     * constraintMetaData
     * .getActionForCombo(FOREIGN_KEY_ACTION_TYPE.FK_RESTRICT);
     * constraintMetaData
     * .getActionForCombo(FOREIGN_KEY_ACTION_TYPE.FK_SET_DEFAULT);
     * constraintMetaData
     * .getActionForCombo(FOREIGN_KEY_ACTION_TYPE.FK_SET_NULL);
     * constraintMetaData.getCheckConstraintExpr();
     * constraintMetaData.getColumn_list();
     * constraintMetaData.getConstraintFillfactor();
     * constraintMetaData.getExclude_where_clause_expr();
     * constraintMetaData.getFk_match_type(); constraintMetaData.getIndex();
     * constraintMetaData
     * .getMatchTypeForCombo(FOREIGN_KEY_MATCH_TYPE.FK_MATCH_DEFAULT);
     * constraintMetaData
     * .getMatchTypeForCombo(FOREIGN_KEY_MATCH_TYPE.FK_MATCH_FULL);
     * constraintMetaData
     * .getMatchTypeForCombo(FOREIGN_KEY_MATCH_TYPE.FK_MATCH_PARTIAL);
     * constraintMetaData
     * .getMatchTypeForCombo(FOREIGN_KEY_MATCH_TYPE.FK_MATCH_SIMPLE);
     * constraintMetaData.getNamespace();
     * constraintMetaData.getOn_delete_action();
     * constraintMetaData.getOn_update_action();
     * constraintMetaData.getReference_index();
     * constraintMetaData.getRefernce_table();
     * constraintMetaData.getTableSpace();
     * constraintMetaData.setCheckConstraintExpr("expr");
     * constraintMetaData.setFillfactor(56);
     * constraintMetaData.setIndex(indexMetaData);
     * constraintMetaData.setRerenceOptions(tablemetaData, indexMetaData);
     * constraintMetaData.setDeffearableOptions(true, true);
     * constraintMetaData.setFkActions( FOREIGN_KEY_MATCH_TYPE.FK_MATCH_FULL,
     * FOREIGN_KEY_ACTION_TYPE.FK_NO_ACTION,
     * FOREIGN_KEY_ACTION_TYPE.FK_NO_ACTION);
     * constraintMetaData.formConstraintString(false);
     * constraintMetaData.setFkActions( FOREIGN_KEY_MATCH_TYPE.FK_MATCH_DEFAULT,
     * FOREIGN_KEY_ACTION_TYPE.FK_RESTRICT,
     * FOREIGN_KEY_ACTION_TYPE.FK_RESTRICT);
     * constraintMetaData.formConstraintString(false);
     * constraintMetaData.setFkActions( FOREIGN_KEY_MATCH_TYPE.FK_MATCH_PARTIAL,
     * FOREIGN_KEY_ACTION_TYPE.FK_SET_DEFAULT,
     * FOREIGN_KEY_ACTION_TYPE.FK_SET_DEFAULT);
     * constraintMetaData.formConstraintString(false);
     * constraintMetaData.setFkActions( FOREIGN_KEY_MATCH_TYPE.FK_MATCH_SIMPLE,
     * FOREIGN_KEY_ACTION_TYPE.FK_SET_NULL,
     * FOREIGN_KEY_ACTION_TYPE.FK_SET_NULL);
     * constraintMetaData.formConstraintString(false);
     * constraintMetaData.getColumn_list();
     * System.out.println(ConstraintMetaData.getConstraintType("c"));;
     * System.out.println(ConstraintMetaData.getConstraintType("f"));
     * System.out.println( ConstraintMetaData.getConstraintType("p"));
     * System.out.println( ConstraintMetaData.getConstraintType("u"));
     * ConstraintMetaData.getConstraintType("t");
     * ConstraintMetaData.getActionType("a");
     * ConstraintMetaData.getActionType("r");
     * ConstraintMetaData.getActionType("c");
     * ConstraintMetaData.getActionType("n");
     * ConstraintMetaData.getMatchType("f");
     * ConstraintMetaData.getMatchType("p");
     * 
     * } catch (Exception e) {
     * 
     * fail(e.getMessage()); }
     * 
     * }
     */

    @Test
    public void test_get_columnlist()
    {
        ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                "MyConstarint", ConstraintType.CHECK_CONSTRSINT);
        constraintMetaData.setPkeyOrUkeyConstraint("column", "table");
        String msg = constraintMetaData.getColumnList();
        assertEquals("column", msg);
    }

    @Test
    public void test_Cons_def()
    {
        try
        {
            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                    "MyConstarint", ConstraintType.CHECK_CONSTRSINT);
            constraintMetaData.setConsDef("ashish");
            assertEquals("ashish", constraintMetaData.getConsDef());
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }

    }

    @Test
    public void test_getTable()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);

            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                    "MyConstarint", ConstraintType.CHECK_CONSTRSINT);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");

            constraintMetaData.setTable(tablemetaData);
            assertEquals(tablemetaData, constraintMetaData.getTable());
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }

    }

    @Test
    public void test_getTableConstraintQry()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);

            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                    "MyConstarint", ConstraintType.CHECK_CONSTRSINT);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");

            assertEquals(
                    "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname ,c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def , ts.spcname as tablespace  FROM pg_constraint c LEFT JOIN pg_class t on (t.oid = c.conrelid) LEFT JOIN pg_index ind ON c.conindid = ind.indexrelid LEFT JOIN pg_class ci on (ind.indexrelid = ci.oid) LEFT JOIN  pg_tablespace ts ON (ts.oid = ci.reltablespace) WHERE t.relkind = 'r' and t.oid = 1001",
                    ConstraintMetaDataUtils.getTableConstraintQry(1001));
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }

    }
    
    @Test
    public void test_getDatabase_getTable()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);

            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                    "MyConstarint", ConstraintType.CHECK_CONSTRSINT);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            constraintMetaData.setTable(tablemetaData);
            constraintMetaData.getDatabase();
            assertEquals(tablemetaData.getDatabase(), constraintMetaData.getDatabase());
            assertEquals(tablemetaData,constraintMetaData.getParent());
        }
        catch (Exception e)
        {

            fail(e.getMessage());
        }

    }
    
    @Test
    public void testTTA_BL_CONSTRAINT_FUNC_BatchDrop_001()
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
            tablemetaData.execCreate(database.getConnectionManager().getObjBrowserConn());
            assertNotNull(indexMetaData.getNamespace());
            
            assertEquals(constraintMetaData.isDropAllowed(), true);
            assertEquals("Constraint", constraintMetaData.getObjectTypeName());
            assertEquals("pg_catalog.\"MyTable\".\"MyConstarint\"", constraintMetaData.getObjectFullName());
            String dropQry = constraintMetaData.getDropQuery(false);
            assertEquals("ALTER TABLE IF EXISTS pg_catalog.\"MyTable\" DROP CONSTRAINT IF EXISTS \"MyConstarint\"", dropQry);
            
            dropQry = constraintMetaData.getDropQuery(true);
            assertEquals("ALTER TABLE IF EXISTS pg_catalog.\"MyTable\" DROP CONSTRAINT IF EXISTS \"MyConstarint\" CASCADE", dropQry);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
