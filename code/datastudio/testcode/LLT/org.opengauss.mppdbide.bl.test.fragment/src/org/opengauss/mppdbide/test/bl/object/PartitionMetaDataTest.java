package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.StmtExecutor.GetFuncProcResultValueParam;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintType;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionColumnExpr;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionColumnType;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.serverdatacache.TableOrientation;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.files.FileValidationUtils;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class PartitionMetaDataTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;

    @Before
	public void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.addViewTableData(preparedstatementHandler);
        CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        connProfCache = DBConnProfCache.getInstance();
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setUsername("myusername");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo,status);
        
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
    }
    
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
    public void test_partitionMetaData_ExecRename()

    {
        CommonLLTUtils.mockGetPartitionOverloaded(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            Namespace ns = database.getNameSpaceById(1);
            PartitionTable ps = new PartitionTable(ns);
            DBConnection conn = database.getConnectionManager().getObjBrowserConn();

            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData colmetadata = new ColumnMetaData(ps, 2, "col1",
                    typeMetaData);
            ColumnMetaData colmetadata1 = new ColumnMetaData(ps, 2, "col2",
                    typeMetaData);
            PartitionColumnExpr column = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            PartitionColumnExpr column1 = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            column.setCol(colmetadata);
            column1.setCol(colmetadata1);
            List<PartitionColumnExpr> columnlist = new ArrayList<>();
            columnlist.add(column);
            columnlist.add(column1);
            ps.setSelColumns(columnlist);
            ps.setOrientation(TableOrientation.ROW);
            ps.getColumnsList();
            ps.hashCode();
            ps.isPartitionsAvailable();
            ps.validateForDuplicateName("col2");
            PartitionMetaData partition = new PartitionMetaData(10, "p1", ps);
            partition.setPartitionName("P1");
            partition.setPartitionValue("50");
            partition.getPartitionValuesAsList();
            partition.setPartitionType("");
            partition.getPartitionType();
            partition.getColumnMetadata();
            partition.setColumnMetadata(colmetadata);
            
            PartitionMetaData partitionTable=new PartitionMetaData("PartName");
            partitionTable.setParent(ps);
            partitionTable.getDatabase();
            
            ps.getPartitions().addItem(partition);
            String name = "rename";
            partition.execRename(name, conn);

        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
            fail("not expected");
        }

    }


    @Test
    public void test_partitionMetaData_execDrop()

    {
        CommonLLTUtils
                .preparePartitionColumnInfoResultSet(preparedstatementHandler);
        CommonLLTUtils
                .preparePartitionConstrainstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionIndexLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.refreshTableForPartitionTable(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            Namespace ns = database.getNameSpaceById(1);
            PartitionTable ps = new PartitionTable(ns);

            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData colmetadata = new ColumnMetaData(ps, 2, "col1",
                    typeMetaData);
            ColumnMetaData colmetadata1 = new ColumnMetaData(ps, 2, "col2",
                    typeMetaData);
            PartitionColumnExpr column = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            PartitionColumnExpr column1 = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            column.setCol(colmetadata);
            column1.setCol(colmetadata1);
            List<PartitionColumnExpr> columnlist = new ArrayList<>();
            columnlist.add(column);
            columnlist.add(column1);
            ps.setSelColumns(columnlist);
            ps.setOrientation(TableOrientation.ROW);
            PartitionMetaData partition = new PartitionMetaData(10, "p1", ps);
            partition.setPartitionName("P1");
            partition.setPartitionValue("50");
            ps.getPartitions().addItem(partition);
            partition.execDrop(database.getConnectionManager().getObjBrowserConn());

        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
            assertTrue(true);
        }

    }

    @Test
    public void test_partitionMetaData_MovePartition()

    {
        CommonLLTUtils.getConstaraintForTableRSEx2(preparedstatementHandler);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
        CommonLLTUtils.mockGetPartitionOverloaded(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            Namespace ns = database.getNameSpaceById(1);
            PartitionTable ps = new PartitionTable(ns);
            DBConnection conn = database.getConnectionManager().getObjBrowserConn();

            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData colmetadata = new ColumnMetaData(ps, 2, "col1",
                    typeMetaData);
            ColumnMetaData colmetadata1 = new ColumnMetaData(ps, 2, "col2",
                    typeMetaData);
            PartitionColumnExpr column = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            PartitionColumnExpr column1 = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            column.setCol(colmetadata);
            column1.setCol(colmetadata1);
            List<PartitionColumnExpr> columnlist = new ArrayList<>();
            columnlist.add(column);
            columnlist.add(column1);
            ps.setSelColumns(columnlist);
            ps.setOrientation(TableOrientation.ROW);
            PartitionMetaData partition = new PartitionMetaData(10, "p1", ps);
            partition.setPartitionName("P1");
            partition.setPartitionValue("50");
            ps.getPartitions().addItem(partition);
            String name = "move";

        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail("not expected");
        }

    }

    @Test
    public void test_partitionMetaData_ModifyPartition()

    {
        CommonLLTUtils.getConstaraintForTableRSEx2(preparedstatementHandler);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
        CommonLLTUtils.mockGetPartitionOverloaded(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            Namespace ns = database.getNameSpaceById(1);
            PartitionTable ps = new PartitionTable(ns);
            DBConnection conn = database.getConnectionManager().getObjBrowserConn();

            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData colmetadata = new ColumnMetaData(ps, 2, "col1",
                    typeMetaData);
            ColumnMetaData colmetadata1 = new ColumnMetaData(ps, 2, "col2",
                    typeMetaData);
            PartitionColumnExpr column = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            PartitionColumnExpr column1 = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            column.setCol(colmetadata);
            column1.setCol(colmetadata1);
            List<PartitionColumnExpr> columnlist = new ArrayList<>();
            columnlist.add(column);
            columnlist.add(column1);
            ps.setSelColumns(columnlist);
            ps.setOrientation(TableOrientation.ROW);
            PartitionMetaData partition = new PartitionMetaData(10, "p1", ps);
            partition.setPartitionName("P1");
            partition.setPartitionValue("50");
            ps.getPartitions().addItem(partition);
            ps.validateForDuplicateName("row1");
            String name = "move";
            List<String> mergeList = new ArrayList<>();
            mergeList.add("newList");

        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail("not expected");
        }

    }

    @Test
    public void test_partitionTable_execCreate()

    {

        CommonLLTUtils
                .preparePartitionColumnInfoResultSet(preparedstatementHandler);
        CommonLLTUtils
                .preparePartitionConstrainstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionIndexLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.mockResultsetForNewlyCreatedTable(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
        try
        {
            Namespace ns = database.getNameSpaceById(1);
            PartitionTable ps = new PartitionTable(ns);
            ps.setName("MyTable");
            DBConnection conn = database.getConnectionManager().getObjBrowserConn();

            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData colmetadata = new ColumnMetaData(ps, 2, "col1",
                    typeMetaData);
            ColumnMetaData colmetadata1 = new ColumnMetaData(ps, 2, "col2",
                    typeMetaData);
            PartitionColumnExpr column = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            PartitionColumnExpr column1 = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);

            column.setCol(colmetadata);
            column1.setCol(colmetadata1);
            List<PartitionColumnExpr> columnlist = new ArrayList<>();
            columnlist.add(column);
            columnlist.add(column1);
            ps.setSelColumns(columnlist);
            ps.setOrientation(TableOrientation.ROW);
            PartitionMetaData partition = new PartitionMetaData(10, "p1", ps);
            partition.setPartitionName("P1");
            partition.setPartitionValue("50");
            partition.setPartitionType("BY RANGE");
            ps.getPartitions().addItem(partition);
            PartitionTable table = partition.getParent();
            table.execCreate(conn);

        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail("not expected");
        }

    }
    
    @Test
    public void test_partitionTable_partitionValueList()

    {

        CommonLLTUtils
                .preparePartitionColumnInfoResultSet(preparedstatementHandler);
        CommonLLTUtils
                .preparePartitionConstrainstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionIndexLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionstLoadLevel(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            Namespace ns = database.getNameSpaceById(1);
            PartitionTable ps = new PartitionTable(ns);
            DBConnection conn = database.getConnectionManager().getObjBrowserConn();

            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData colmetadata = new ColumnMetaData(ps, 2, "col1",
                    typeMetaData);
            ColumnMetaData colmetadata1 = new ColumnMetaData(ps, 2, "col2",
                    typeMetaData);
            PartitionColumnExpr column = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            PartitionColumnExpr column1 = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);

            column.setCol(colmetadata);
            column1.setCol(colmetadata1);
            List<PartitionColumnExpr> columnlist = new ArrayList<>();
            columnlist.add(column);
            columnlist.add(column1);
            ps.setSelColumns(columnlist);
            ps.setOrientation(TableOrientation.ROW);
            PartitionMetaData partition = new PartitionMetaData(10, "p1", ps);
            partition.setPartitionName("P1");
            partition.setPartitionValue("1,2,3");
            assertEquals(Arrays.asList(partition.getPartitionValue().split(",", -1)), partition.getPartitionValuesAsList());

        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail("not expected");
        }

    }
    
    @Test
    public void test_partition_BatchDrop_001()
    {

        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            Namespace ns = database.getNameSpaceById(1);
            PartitionTable ps = new PartitionTable(ns);

            TypeMetaData typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
            ColumnMetaData colmetadata = new ColumnMetaData(ps, 2, "col1",
                    typeMetaData);
            ColumnMetaData colmetadata1 = new ColumnMetaData(ps, 2, "col2",
                    typeMetaData);
            PartitionColumnExpr column = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            PartitionColumnExpr column1 = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            /*PartitionTableGroup group = new PartitionTableGroup(
                    OBJECTTYPE.PARTITION_GROUP, ns);
            group.getDisplayLabel();*/
            column.setCol(colmetadata);
            column1.setCol(colmetadata1);
            List<PartitionColumnExpr> columnlist = new ArrayList<>();
            columnlist.add(column);
            columnlist.add(column1);
            ps.setSelColumns(columnlist);
            ps.setOrientation(TableOrientation.ROW);
            PartitionMetaData partition = new PartitionMetaData(10, "p1", ps);
            partition.setPartitionName("P1");
            partition.setPartitionValue("50");
            PartitionMetaData part = new PartitionMetaData(11, "p2", ps);
            part.setPartitionName("P2");
            part.setPartitionValue("100");

            ps.getPartitions().addItem(partition);
            ps.getPartitions().addItem(part);
            ps.setName("abc");
            
            ps.getColumns().addItem(colmetadata);
            ps.getColumns().addItem(colmetadata1);
            
            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(ps);
            indexMetaData.setNamespace(ps.getNamespace());
            ps.addIndex(indexMetaData);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            
            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
                    "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            ps.addConstraint(constraintMetaData);
            
            assertEquals(partition.isDropAllowed(), true);
            assertEquals("Partition", partition.getObjectTypeName());
            assertEquals("pg_catalog.abc.p1", partition.getObjectFullName());
            String dropQry = partition.getDropQuery(false);
            assertEquals("ALTER TABLE IF EXISTS pg_catalog.abc DROP PARTITION p1", dropQry);
            
            dropQry = partition.getDropQuery(true);
            assertEquals("ALTER TABLE IF EXISTS pg_catalog.abc DROP PARTITION p1", dropQry);
        }
        catch (Exception e)
        {
            fail("not expected");
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
