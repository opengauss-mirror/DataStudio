package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionColumnExpr;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionColumnType;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.TableOrientation;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ColumnList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ConstraintList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ForeignTableGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.IndexList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.PartitionList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
/*import org.opengauss.mppdbide.presentation.IWindowDetail;
import org.opengauss.mppdbide.presentation.ViewTableData;
import org.opengauss.mppdbide.presentation.ViewTableDataCore;*/
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class PartitionTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;

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

        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.addViewTableData(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setUsername("myusername");
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
            itr = connProfCache.getServers().iterator();
        }

        connProfCache.closeAllNodes();

    }

    @Test
    public void test_partition_formPartitionQueries_ROW_ByRange()
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
            PartitionColumnExpr column = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            column.setCol(colmetadata);
            List<PartitionColumnExpr> columnlist = new ArrayList<>();
            columnlist.add(column);
            ps.setSelColumns(columnlist);
            ps.setOrientation(TableOrientation.ROW);
            PartitionMetaData partition = new PartitionMetaData(10, "p1", ps);
            partition.setPartitionName("p1");
            partition.setPartitionValue("50");
            partition.setPartitionType("BY RANGE");
            PartitionMetaData part = new PartitionMetaData(11, "p2", ps);
            part.setPartitionName("p2");
            part.setPartitionValue("100");
            part.setPartitionType("BY RANGE");

            ps.getPartitions().addItem(partition);
            ps.getPartitions().addItem(part);
            String query = ps.formPartitionQueries();
            assertTrue(query.contains("PARTITION BY RANGE"));
            assertTrue(query.contains("partition p1 values less than (50)"));

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }

    }

    @Test
    public void test_partition_formPartitionQueries_ROW_ByList()
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
            PartitionColumnExpr column = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            column.setCol(colmetadata);
            List<PartitionColumnExpr> columnlist = new ArrayList<>();
            columnlist.add(column);
            ps.setSelColumns(columnlist);
            ps.setOrientation(TableOrientation.ROW);
            PartitionMetaData partition = new PartitionMetaData(10, "p1", ps);
            partition.setPartitionName("p1");
            partition.setPartitionValue("10,20,30");
            partition.setPartitionType("BY LIST");
            PartitionMetaData part = new PartitionMetaData(11, "p2", ps);
            part.setPartitionName("p2");
            part.setPartitionValue("40,50,60");
            part.setPartitionType("BY LIST");

            ps.getPartitions().addItem(partition);
            ps.getPartitions().addItem(part);
            String query = ps.formPartitionQueries();
            assertTrue(query.contains("PARTITION BY LIST"));
            assertTrue(query.contains("partition p1 values (10,20,30)"));

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }

    }

    @Test
    public void test_partition_formPartitionQueries_ROW_ByHash()
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
            PartitionColumnExpr column = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            column.setCol(colmetadata);
            List<PartitionColumnExpr> columnlist = new ArrayList<>();
            columnlist.add(column);
            ps.setSelColumns(columnlist);
            ps.setOrientation(TableOrientation.ROW);
            PartitionMetaData partition = new PartitionMetaData(10, "p1", ps);
            partition.setPartitionName("p1");
            partition.setPartitionType("BY HASH");
            PartitionMetaData part = new PartitionMetaData(11, "p2", ps);
            part.setPartitionName("p2");
            part.setPartitionType("BY HASH");

            ps.getPartitions().addItem(partition);
            ps.getPartitions().addItem(part);
            String query = ps.formPartitionQueries();
            assertTrue(query.contains("PARTITION BY HASH"));
            assertTrue(query.contains("partition p1"));
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }

    }

    @Test
    public void test_partition_formPartitionQueries_ROW_ByInterval()
    {

        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            Namespace ns = database.getNameSpaceById(1);
            PartitionTable ps = new PartitionTable(ns);

            TypeMetaData typeMetaData = new TypeMetaData(20, "timestamp with time zone",
                    database.getNameSpaceById(1));
            ColumnMetaData colmetadata = new ColumnMetaData(ps, 2, "col1",
                    typeMetaData);
            PartitionColumnExpr column = new PartitionColumnExpr(
                    PartitionColumnType.COLUMN);
            column.setCol(colmetadata);
            List<PartitionColumnExpr> columnlist = new ArrayList<>();
            columnlist.add(column);
            ps.setSelColumns(columnlist);
            ps.setOrientation(TableOrientation.ROW);
            PartitionMetaData partition = new PartitionMetaData(10, "p1", ps);
            partition.setPartitionName("p1");
            partition.setPartitionType("BY INTERVAL");
            partition.setPartitionValue("2021-05-01");
            partition.setIntervalPartitionExpr("1 month");
            PartitionMetaData part = new PartitionMetaData(11, "p2", ps);
            part.setPartitionName("p2");
            part.setPartitionType("BY INTERVAL");
            part.setPartitionValue("2021-06-01");
            part.setIntervalPartitionExpr("1 month");

            ps.getPartitions().addItem(partition);
            ps.getPartitions().addItem(part);
            String query = ps.formPartitionQueries();
            assertTrue(query.contains("PARTITION BY RANGE"));
            assertTrue(query.contains("interval ('1 month')"));
            assertTrue(query.contains("partition p1 values less than ('2021-05-01')"));

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }

    }

    @Test
    public void test_partition_movePartitions()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            Namespace ns = database.getNameSpaceById(1);
            PartitionTable ps = new PartitionTable(ns);
            PartitionList list = new PartitionList(
                    OBJECTTYPE.PARTITION_METADATA, ps);
            list.getParent();

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
        PartitionMetaData part = new PartitionMetaData(11, "p2", ps);

        ps.getPartitions().addItem(partition);
        ps.getPartitions().addItem(part);
        String str1=ps.getPartitions().getList().get(0).getName();
        ps.movePartition(1, true);
        String str2=ps.getPartitions().getList().get(1).getName();
        assertTrue(str1.equals(str2));
        }
        catch(DatabaseOperationException e)
        {
            fail("not expected");
        }
        

    }
    
    @Test
    public void test_partition_addPartitions()
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
            PartitionColumnExpr type = new PartitionColumnExpr(
                    PartitionColumnType.EXPRESSION);
            column.setCol(colmetadata);
            column1.setCol(colmetadata1);
            List<PartitionColumnExpr> columnlist = new ArrayList<>();
            columnlist.add(column);
            columnlist.add(column1);
            column.setPosition(1);
            column.setExpr("expression");
            type.setCol(colmetadata);
            ps.setSelColumns(columnlist);
            ps.setOrientation(TableOrientation.ROW);
            PartitionMetaData partition = new PartitionMetaData(10, "p1", ps);
            PartitionMetaData part = new PartitionMetaData(11, "p2", ps);

            ps.getPartitions().addItem(partition);
            ps.getPartitions().addItem(part);

            PartitionMetaData part1 = new PartitionMetaData(12, "p3", ps);
            ps.addPartition(part1);
            assertTrue(ps.getPartitions().getList().size() == 3);
            ps.removePartition(1);
            assertTrue(ps.getPartitions().getList().size() == 2);
            assertEquals(1, column.getPosition());
            assertEquals("expression", column.getExpr());
            type.setExpr("expr");
            type.toString();
            assertEquals("expr", type.toString());
  
        }
        catch(DatabaseOperationException e)
        {
            fail("not expected");
        }
        

    }
    
    @Test
    public void test_partition_removeAllPartitions()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try{
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
        PartitionMetaData part = new PartitionMetaData(11, "p2", ps);

        ps.getPartitions().addItem(partition);
        ps.getPartitions().addItem(part);
        
        PartitionMetaData part1=new PartitionMetaData(12,"p3", ps);
        ps.addPartition(part1);
        ps.removeAllPartition();
        assertTrue(ps.getPartitions().getList().size()==0);
        assertTrue(ps.getPartitions().getList().size()==0);
       
        }
        catch(DatabaseOperationException e)
        {
            fail("not expected");
        }
        

    }
    @Test
    public void test_exec_drop(){
        CommonLLTUtils
                .preparePartitionColumnInfoResultSet(preparedstatementHandler);
        CommonLLTUtils
                .preparePartitionConstrainstLoadLevel(preparedstatementHandler);
        CommonLLTUtils
                .preparePartitionIndexLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionstLoadLevel(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
        Namespace ns = null;
        try
        {
            ns = database.getNameSpaceById(1);
            assertTrue(ns != null);
        } 
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        PartitionTable ptable = new PartitionTable(ns);
        
        DBConnection dconn = CommonLLTUtils.getDBConnection();
        
        try
        {
            ptable.execDrop(dconn);
            ptable.isTableDropped();
            assertTrue(ptable != null);
            //ptable.getPartKey();
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
    }
    @Test
    public void test_get_children(){
        Database database = connProfCache.getDbForProfileId(profileId);
        Namespace ns = null;
        try
        {
            ns = database.getNameSpaceById(1);
            assertTrue(ns != null);
        } 
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        PartitionTable ptable = new PartitionTable(ns);
        TypeMetaData typeMetaData = null;
        try
        {
            typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        ColumnMetaData colmetadata = new ColumnMetaData(ptable, 2, "col1",
                typeMetaData);
        ColumnMetaData colmetadata1 = new ColumnMetaData(ptable, 2, "col2",
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
        ptable.setSelColumns(columnlist);
        ptable.setOrientation(TableOrientation.ROW);
        PartitionMetaData partition = new PartitionMetaData(10, "p1", ptable);
        PartitionMetaData part = new PartitionMetaData(11, "p2", ptable);

        ptable.getPartitions().addItem(partition);
        ptable.getPartitions().addItem(part);
        
        Object[] arr = ptable.getChildren();
        assertTrue(arr.length!=0);
    }
    @Test
    public void test_get_part_key(){
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
        Namespace ns = null;
        try
        {
            ns = database.getNameSpaceById(1);
            assertTrue(ns != null);
        } 
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        PartitionTable ptable = new PartitionTable(ns);
        
        DBConnection dconn = CommonLLTUtils.getDBConnection();
        TypeMetaData typeMetaData = null;
        try
        {
            typeMetaData = new TypeMetaData(20, "bigint",
                    database.getNameSpaceById(1));
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        ColumnMetaData colmetadata = new ColumnMetaData(ptable, 2, "col1",
                typeMetaData);
        ColumnMetaData colmetadata1 = new ColumnMetaData(ptable, 2, "col2",
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
        ptable.setSelColumns(columnlist);
        PartitionMetaData partition = new PartitionMetaData(10, "p1", ptable);
        PartitionMetaData part = new PartitionMetaData(11, "p2", ptable);
        ptable.getPartitions().addItem(partition);
        ptable.getPartitions().addItem(part);
        ptable.setOrientation(TableOrientation.ROW);
        String str = ptable.getPartKey();
        if(null != str){
        assertTrue(!str.isEmpty());
        }
    }
    
    // This code is intensionally commented, to be used later
    @Test public void test_partition_refreshTableDetails() {

      /*  CommonLLTUtils
                .preparePartitionColumnInfoResultSet(preparedstatementHandler);*/
        CommonLLTUtils.preparePartitionConstrainstResultSet(preparedstatementHandler);
        CommonLLTUtils.preparePartitionIndexestResultSet(preparedstatementHandler);
        CommonLLTUtils.preparePartitionstResultSet(preparedstatementHandler);
        
 String  refreshalldetailscolumnloadqry="select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name,  pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,  c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod  as precision, c.attndims as dimentions,  c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as  default_value, d.adbin as attDefStr  from pg_class t  left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v'))  left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum)  left join pg_type typ on (c.atttypid = typ.oid)  where c.attisdropped = 'f' and c.attnum > 0 and t.oid = 0 and t.relkind <> 'i'  order by c.attnum;";
        
        
        MockResultSet fecthForeignTableColumnsRS1 = preparedstatementHandler.createResultSet();
        fecthForeignTableColumnsRS1.addColumn("tableid");
        fecthForeignTableColumnsRS1.addColumn("datatypeoid");
        fecthForeignTableColumnsRS1.addColumn("columnidx");
        fecthForeignTableColumnsRS1.addColumn("name");
        fecthForeignTableColumnsRS1.addColumn("displaycolumns");
        fecthForeignTableColumnsRS1.addColumn("datatypeoid");
        fecthForeignTableColumnsRS1.addColumn("dtns");
        fecthForeignTableColumnsRS1.addColumn("length");
        fecthForeignTableColumnsRS1.addColumn("precision");
        fecthForeignTableColumnsRS1.addColumn("dimentions");
        fecthForeignTableColumnsRS1.addColumn("notnull");
        fecthForeignTableColumnsRS1.addColumn("isdefaultvalueavailable");
        fecthForeignTableColumnsRS1.addColumn("default_value");
        fecthForeignTableColumnsRS1.addColumn("attdefstr");
       /* fecthForeignTableColumnsRS1.addRow(new Object[] {1, 1, 1, "column", null, null,
            1, null, null, null, null, null, true, 1});*/
        preparedstatementHandler.prepareResultSet(refreshalldetailscolumnloadqry, fecthForeignTableColumnsRS1);
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
            ps.refreshTableDetails(database.getConnectionManager().getObjBrowserConn());
             
            assertTrue(ps.getPartitions().getList().size() == 1);

        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
            fail("not expected");
        }

    }
    
    @Test
    public void test_partitionTable_BatchDrop_001()
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
            TableObjectGroup group = new TableObjectGroup(
                    OBJECTTYPE.TABLE_GROUP, ns);
            group.getDisplayLabel();
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
            
            ns.getTables().addToGroup(ps);
            
            assertEquals(ps.isDropAllowed(), true);
            assertEquals("Partition Table", ps.getObjectTypeName());
            assertEquals("pg_catalog.abc", ps.getObjectFullName());
            String dropQry = ps.getDropQuery(false);
            assertEquals("DROP TABLE IF EXISTS pg_catalog.abc", dropQry);
            
            dropQry = ps.getDropQuery(true);
            assertEquals("DROP TABLE IF EXISTS pg_catalog.abc CASCADE", dropQry);
            
            // Remove of partition, column, index, constraint
            ColumnList list = (ColumnList) ps.getColumns();
            assertEquals(list.getSize(), 2);

            ps.remove(list.getItem(0));
            list = (ColumnList) ps.getColumns();
            assertEquals(list.getSize(), 1);
            
            ConstraintList list1 = (ConstraintList) ps.getConstraints();
            assertEquals(list1.getSize(), 1);
            
            ps.remove(list1.getItem(0));
            list1 = (ConstraintList) ps.getConstraints();
            assertEquals(list1.getSize(), 0);
            
            IndexList list2 = ps.getIndexes();
            assertEquals(list2.getSize(), 1);
            
            ps.remove(list2.getItem(0));
            list2 = ps.getIndexes();
            assertEquals(list2.getSize(), 0);
            
            PartitionList list3 = (PartitionList) ps.getPartitions();
            assertEquals(list3.getSize(), 2);
            
            ps.remove(list3.getItem(0));
            list3 = (PartitionList) ps.getPartitions();
            assertEquals(list3.getSize(), 1);
            
            // Remove the Object from namespace also
            ns = database.getNameSpaceById(1);
            
            TableObjectGroup ptog = ns.getTables();
            assertEquals(1, ptog.getSize());
            
            ns.remove(ps);
            
            ptog = ns.getTables();
            assertEquals(0, ptog.getSize());

        }
        catch (Exception e)
        {
            fail("not expected");
        }

    }
}
