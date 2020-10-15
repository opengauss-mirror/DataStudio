package com.huawei.mppdbide.test.presentation.properties;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.search.SearchNameMatchEnum;
import com.huawei.mppdbide.bl.search.SearchObjectEnum;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.serverdatacache.SystemNamespace;
import com.huawei.mppdbide.mock.presentation.CommonLLTUtils;
import com.huawei.mppdbide.mock.presentation.CommonLLTUtilsHelper.EXCEPTIONENUM;
import com.huawei.mppdbide.mock.presentation.ExceptionConnectionHelper;
import com.huawei.mppdbide.presentation.search.AbstractSearchObjUtils;
import com.huawei.mppdbide.presentation.search.SearchObjCore;
import com.huawei.mppdbide.presentation.search.SearchObjInfo;
import com.huawei.mppdbide.test.presentation.table.MockPresentationBLPreferenceImpl;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import static org.junit.Assert.*;

public class SearchTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    SearchObjCore core=null;

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
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.addViewTableData(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setDriverName("FusionInsight LibrA");
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
        core = new SearchObjCore();
        SearchObjInfo obj=new SearchObjInfo();
        core.setSearchInfo(obj);
        core.setSearchStatus(SearchObjectEnum.DATABASELIST_UPDATE);
        core.setSearchStatus(SearchObjectEnum.SEARCH_INI);
        core.getAllProfiles();
        core.getSearchInfo().setSelectedserver(0);
        core.getAllDatabases();
        core.getSearchInfo().setSelectedDB(0);
        core.getNamespaceList();
        core.getSearchInfo().setSelectedNamespace(0);

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
    public void test_connection_db_ns(){
        
        assertEquals("TestConnectionName", core.getSelectedServer().getName());
        assertEquals("Gauss", core.getSelectedDb().getName());
        assertEquals("PUBLIC", core.getSelectedNs().getName());
        assertEquals(4, core.getNameMatchList().size());
        assertEquals(0, core.getObjectBrowserSelectedServer("TestConnectionName (127.0.0.1:5432)"));
        assertEquals(0, core.getObjectBrowserSelectedDatabase("Gauss"));
        assertEquals(1, core.getObjectBrowserSelectedSchema("information_schema"));
        assertEquals(0, core.getObjectBrowserSelectedSchema("intion_schema"));
        
        
    }

    @Test
    public void test_search_only_regulartables()
    {
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT')))) and tbl.parttype in ('n', 'p', 'v')  and relname  LIKE ? ;";

        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");
        
        formSearchQuery.addRow(new Object[] {1,"abc","3","PUBLIC","r","n",null});
        preparedstatementHandler.prepareResultSet(Query,
                formSearchQuery);
        
        try
        {

            core.getConnection();
            core.getSearchInfo().setTableSelected(true);
            core.getSearchInfo().setFunProcSelected(false);
            core.getSearchInfo().setViewsSelected(false);
            core.getSearchInfo().setSearchText("bc");
            core.getSearchInfo().setNameMatch(0);
            core.getSearchInfo().setMatchCase(true);
            core.setSearchStatus(SearchObjectEnum.SEARCH_START);   
            core.search();
            core.setSearchStatus(SearchObjectEnum.SEARCH_END);   

            assertEquals(1, core.getSearchNamespace().getTables().getSize());
            assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getFunctions().getSize());

        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
                fail("not expected");
        }
        finally{
            core.clearData();
            core.cleanUpSearch();
        }
    }
  @Test  
    public void test_search_partitionForeign_table(){

        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT')))) and tbl.parttype in ('n', 'p', 'v')  and relname  LIKE ? ;";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");
        
        formSearchQuery.addRow(new Object[] {1,"abc","3","PUBLIC","r","p",null});
        formSearchQuery.addRow(new Object[] {2,"abcd","3","PUBLIC","f",null,"{location=gsffs://10.18.96.123:5000/,format=orc,delimiter=|,encoding=UTF-8}"});
        preparedstatementHandler.prepareResultSet(Query,
                formSearchQuery);
        
        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(true);
            core.getSearchInfo().setFunProcSelected(false);
            core.getSearchInfo().setViewsSelected(false);
            core.getSearchInfo().setSearchText("ab");
            core.getSearchInfo().setNameMatch(1);
            core.getSearchInfo().setMatchCase(true);
            
            core.search();
            
            assertEquals(1, core.getSearchNamespace().getTables().getSize());
            assertEquals(1, core.getSearchNamespace().getForeignTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getFunctions().getSize());

        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
                fail("not expected");
        }
        finally{
            core.clearData();
            core.cleanUpSearch();
        }
    
        
    }
  
  @Test  
  public void test_search_partitionForeign_table1(){
      
      String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT')))) and tbl.parttype in ('n', 'p', 'v')  and relname  LIKE ? ;";
      MockResultSet formSearchQuery = preparedstatementHandler
              .createResultSet();
      formSearchQuery.addColumn("oid");
      formSearchQuery.addColumn("relname");
      formSearchQuery.addColumn("relnamespace");
      formSearchQuery.addColumn("nsname");
      formSearchQuery.addColumn("relkind");
      formSearchQuery.addColumn("parttype");
      formSearchQuery.addColumn("ftoptions");
      
      formSearchQuery.addRow(new Object[] {0,"notablename","3","pg_catalog","f",null,"{location=gsffs://10.18.96.123:5000/,format=orc,delimiter=|,encoding=UTF-8}"});
      preparedstatementHandler.prepareResultSet(Query,
              formSearchQuery);
      Namespace ns=new SystemNamespace(1, "pg_catalog", core.getSelectedDb());
      ForeignTable forTable = new ForeignTable(ns,
              OBJECTTYPE.FOREIGN_TABLE_HDFS);
      ns.getForeignTablesGroup().addToGroup(forTable);
      core.getSelectedDb().getSystemNamespaceGroup().addToGroup((SystemNamespace) ns);
      try
      {
     
          core.getConnection();
          core.getSearchInfo().setTableSelected(true);
          core.getSearchInfo().setFunProcSelected(false);
          core.getSearchInfo().setViewsSelected(false);
          core.getSearchInfo().setSearchText("ab");
          core.getSearchInfo().setNameMatch(1);
          core.getSearchInfo().setMatchCase(true);
          
          core.search();


      }
      catch (DatabaseOperationException | DatabaseCriticalException e)
      {
              fail("not expected");
      }
      finally{
          core.clearData();
          core.cleanUpSearch();
      }
  
      
  }
  
  @Test  
  public void test_search_partitionForeign_table2(){
      
      String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT')))) and tbl.parttype in ('n', 'p', 'v')  and relname  LIKE ? ;";
      MockResultSet formSearchQuery = preparedstatementHandler
              .createResultSet();
      formSearchQuery.addColumn("oid");
      formSearchQuery.addColumn("relname");
      formSearchQuery.addColumn("relnamespace");
      formSearchQuery.addColumn("nsname");
      formSearchQuery.addColumn("relkind");
      formSearchQuery.addColumn("parttype");
      formSearchQuery.addColumn("ftoptions");
      
      formSearchQuery.addRow(new Object[] {0,"notablename","3","pg_catalog","f","p","{location=gsffs://10.18.96.123:5000/,format=orc,delimiter=|,encoding=UTF-8}"});
      preparedstatementHandler.prepareResultSet(Query,
              formSearchQuery);
      Namespace ns=new SystemNamespace(1, "pg_catalog", core.getSelectedDb());
      ForeignTable forTable = new ForeignTable(ns,
              OBJECTTYPE.FOREIGN_TABLE_HDFS);
      ns.getForeignTablesGroup().addToGroup(forTable);
      core.getSelectedDb().getSystemNamespaceGroup().addToGroup((SystemNamespace) ns);
      try
      {
     
          core.getConnection();
          core.getSearchInfo().setTableSelected(true);
          core.getSearchInfo().setFunProcSelected(false);
          core.getSearchInfo().setViewsSelected(false);
          core.getSearchInfo().setSearchText("ab");
          core.getSearchInfo().setNameMatch(1);
          core.getSearchInfo().setMatchCase(true);
          
          core.search();
          
          assertEquals(0, core.getSearchNamespace().getTables().getSize());
          assertEquals(1, core.getSearchNamespace().getForeignTablesGroup().getSize());
          assertEquals(0, core.getSearchNamespace().getViewGroup().getSize());
          assertEquals(0, core.getSearchNamespace().getFunctions().getSize());


      }
      catch (DatabaseOperationException | DatabaseCriticalException e)
      {
              fail("not expected");
      }
      finally{
          core.clearData();
          core.cleanUpSearch();
      }
  
      
  }
  
  
   @Test 
public void test_search_only_views(){
        
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='v' and has_table_privilege(tbl.oid, 'SELECT'))) and tbl.parttype in ('n', 'p', 'v')  and relname  LIKE ? ;";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");
        
        formSearchQuery.addRow(new Object[] {1,"view","3","PUBLIC","v","n",null});
        preparedstatementHandler.prepareResultSet(Query,
                formSearchQuery);
        
        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(false);
            core.getSearchInfo().setFunProcSelected(false);
            core.getSearchInfo().setViewsSelected(true);
            core.getSearchInfo().setSearchText("view");
            core.getSearchInfo().setNameMatch(2);
            core.getSearchInfo().setMatchCase(true);
            
            core.search();
            
            assertEquals(0, core.getSearchNamespace().getTables().getSize());
            //assertEquals(0, core.getSearchNamespace().getPartitionTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
            assertEquals(1, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getFunctions().getSize());

        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
                fail("not expected");
        }
        finally{
            core.clearData();
            core.cleanUpSearch();
        }
    
        
    }
 
@Test
public void test_search_only_funcProc(){

    String Query = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang,ns.nspname nsname , regexp_matches(objname, ?) FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang left join  pg_namespace ns ON ns.oid=pr.pronamespace WHERE lng.lanname in ('plpgsql','sql','c')  and pr.pronamespace=3";
    MockResultSet formSearchQuery = preparedstatementHandler
            .createResultSet();
    formSearchQuery.addColumn("oid");
    formSearchQuery.addColumn("objname");
    formSearchQuery.addColumn("namespace");
    formSearchQuery.addColumn("ret");
    formSearchQuery.addColumn("alltype");
    formSearchQuery.addColumn("nargs");
    formSearchQuery.addColumn("argtype");
    formSearchQuery.addColumn("argname");
    formSearchQuery.addColumn("argmod");
    formSearchQuery.addColumn("retset");
    formSearchQuery.addColumn("lang");
    formSearchQuery.addColumn("nsname");

    formSearchQuery.addRow(new Object[] {1,"function","3",1,null,0,null,null,null,"f","plpgsql","PUBLIC"});
    preparedstatementHandler.prepareResultSet(Query,
            formSearchQuery);
    
    try
    {
   
        core.getConnection();
        core.getSearchInfo().setTableSelected(false);
        core.getSearchInfo().setFunProcSelected(true);
        core.getSearchInfo().setViewsSelected(false);
        core.getSearchInfo().setSearchText("function%");
        core.getSearchInfo().setNameMatch(3);
        core.getSearchInfo().setMatchCase(true);
        
        core.search();
        
        assertEquals(0, core.getSearchNamespace().getTables().getSize());
        //assertEquals(0, core.getSearchNamespace().getPartitionTablesGroup().getSize());
        assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
        assertEquals(0, core.getSearchNamespace().getViewGroup().getSize());
        assertEquals(1, core.getSearchNamespace().getFunctions().getSize());

    }
    catch (DatabaseOperationException | DatabaseCriticalException e)
    {
            fail("not expected");
    }
    finally{
        core.clearData();
        core.cleanUpSearch();
    }

    
}
@Test
public void test_table_view_matchCase(){
    String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT'))) or (tbl.relkind='v' and has_table_privilege(tbl.oid, 'SELECT'))) and tbl.parttype in ('n', 'p', 'v')  and relname  LIKE ? ;";
    MockResultSet formSearchQuery = preparedstatementHandler
            .createResultSet();
    formSearchQuery.addColumn("oid");
    formSearchQuery.addColumn("relname");
    formSearchQuery.addColumn("relnamespace");
    formSearchQuery.addColumn("nsname");
    formSearchQuery.addColumn("relkind");
    formSearchQuery.addColumn("parttype");
    formSearchQuery.addColumn("ftoptions");
   
    formSearchQuery.addRow(new Object[] {1,"abc","3","PUBLIC","r","n",null});
    formSearchQuery.addRow(new Object[] {1,"view","3","PUBLIC","v","n",null});
    preparedstatementHandler.prepareResultSet(Query,
            formSearchQuery);
    
    try
    {
   
        core.getConnection();
        core.getSearchInfo().setTableSelected(true);
        core.getSearchInfo().setFunProcSelected(false);
        core.getSearchInfo().setViewsSelected(true);
        core.getSearchInfo().setSearchText("abc");
        core.getSearchInfo().setNameMatch(0);
        core.getSearchInfo().setMatchCase(true);
        
        core.search();
        
        assertEquals(1, core.getSearchNamespace().getTables().getSize());
        //assertEquals(0, core.getSearchNamespace().getPartitionTablesGroup().getSize());
        assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
        assertEquals(1, core.getSearchNamespace().getViewGroup().getSize());
        assertEquals(0, core.getSearchNamespace().getFunctions().getSize());

    }
    catch (DatabaseOperationException | DatabaseCriticalException e)
    {
            fail("not expected");
    }
    finally{
        core.clearData();
        core.cleanUpSearch();
    }
    
    
}
  @Test  
public void test_tables_view_funProc_matchCase(){
    String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions , regexp_matches(relname,?) from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT'))) or (tbl.relkind='v' and has_table_privilege(tbl.oid, 'SELECT'))) and tbl.parttype in ('n', 'p', 'v') ";
    MockResultSet formSearchQuery = preparedstatementHandler
            .createResultSet();
    formSearchQuery.addColumn("oid");
    formSearchQuery.addColumn("relname");
    formSearchQuery.addColumn("relnamespace");
    formSearchQuery.addColumn("nsname");
    formSearchQuery.addColumn("relkind");
    formSearchQuery.addColumn("parttype");
    formSearchQuery.addColumn("ftoptions");
    formSearchQuery.addRow(new Object[] {1,"abc","3","PUBLIC","r","n",null});
    formSearchQuery.addRow(new Object[] {3,"abc","3","PUBLIC","r","p",null});
    formSearchQuery.addRow(new Object[] {2,"abcd","3","PUBLIC","f",null,"{location=gsfs://10.18.96.123:5000/,format=csv,delimiter=|,encoding=UTF-8}"});
    formSearchQuery.addRow(new Object[] {1,"view","3","PUBLIC","v","n",null}); 
    
    preparedstatementHandler.prepareResultSet(Query,
            formSearchQuery);
    
    String Query2 = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang,ns.nspname nsname , regexp_matches(objname, ?) FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang left join  pg_namespace ns ON ns.oid=pr.pronamespace WHERE lng.lanname in ('plpgsql','sql','c')  and pr.pronamespace=3 and has_function_privilege(pr.oid, 'EXECUTE')";
    MockResultSet formSearchQuery2 = preparedstatementHandler
            .createResultSet();
    formSearchQuery2.addColumn("oid");
    formSearchQuery2.addColumn("objname");
    formSearchQuery2.addColumn("namespace");
    formSearchQuery2.addColumn("ret");
    formSearchQuery2.addColumn("alltype");
    formSearchQuery2.addColumn("nargs");
    formSearchQuery2.addColumn("argtype");
    formSearchQuery2.addColumn("argname");
    formSearchQuery2.addColumn("argmod");
    formSearchQuery2.addColumn("retset");
    formSearchQuery2.addColumn("lang");
    formSearchQuery2.addColumn("nsname");

    formSearchQuery2.addRow(new Object[] {1,"function","3",1,null,0,null,null,null,"f","plpgsql","PUBLIC"});
    preparedstatementHandler.prepareResultSet(Query2,
            formSearchQuery2);
    try
    {
   
        core.getConnection();
        core.getSearchInfo().setTableSelected(true);
        core.getSearchInfo().setFunProcSelected(true);
        core.getSearchInfo().setViewsSelected(true);
        core.getSearchInfo().setSearchText("%");
        core.getSearchInfo().setNameMatch(3);
        core.getSearchInfo().setMatchCase(true);
        
        core.search();
        core.getSearchNamespace().getChildren();
        core.getSearchNamespace().getTablesGroup();
        assertEquals(2, core.getSearchNamespace().getTables().getSize());
        assertEquals(1, core.getSearchNamespace().getForeignTablesGroup().getSize());
        assertEquals(1, core.getSearchNamespace().getViewGroup().getSize());
        assertEquals(1, core.getSearchNamespace().getFunctions().getSize());
        assertEquals(1, core.getSearchNamespace().getFunctions().getSize());
        assertTrue(core.getSearchNamespace().getChildren()!=null);

    }
    catch (DatabaseOperationException | DatabaseCriticalException e)
    {
            fail("not expected");
    }
    finally{
        core.clearData();
        core.cleanUpSearch();
    }
    
    
}
@Test
public void test_view_funProc_matchCase(){
    String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='v' and has_table_privilege(tbl.oid, 'SELECT'))) and tbl.parttype in ('n', 'p', 'v') ";
    MockResultSet formSearchQuery = preparedstatementHandler
            .createResultSet();
    formSearchQuery.addColumn("oid");
    formSearchQuery.addColumn("relname");
    formSearchQuery.addColumn("relnamespace");
    formSearchQuery.addColumn("nsname");
    formSearchQuery.addColumn("relkind");
    formSearchQuery.addColumn("parttype");
    formSearchQuery.addColumn("ftoptions");

    formSearchQuery.addRow(new Object[] {1,"abdcf","3","PUBLIC","v","n",null}); 
    
    preparedstatementHandler.prepareResultSet(Query,
            formSearchQuery);
    
    String Query2 = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang,ns.nspname nsname FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang left join  pg_namespace ns ON ns.oid=pr.pronamespace WHERE lng.lanname in ('plpgsql','sql','c')  and pr.pronamespace=3 and has_function_privilege(pr.oid, 'EXECUTE') and objname  LIKE ? ;";
    MockResultSet formSearchQuery2 = preparedstatementHandler
            .createResultSet();
    formSearchQuery2.addColumn("oid");
    formSearchQuery2.addColumn("objname");
    formSearchQuery2.addColumn("namespace");
    formSearchQuery2.addColumn("ret");
    formSearchQuery2.addColumn("alltype");
    formSearchQuery2.addColumn("nargs");
    formSearchQuery2.addColumn("argtype");
    formSearchQuery2.addColumn("argname");
    formSearchQuery2.addColumn("argmod");
    formSearchQuery2.addColumn("retset");
    formSearchQuery2.addColumn("lang");
    formSearchQuery2.addColumn("nsname");

    formSearchQuery2.addRow(new Object[] {1,"abdfghj","3",1,null,0,null,null,null,"f","plpgsql","PUBLIC"});
    preparedstatementHandler.prepareResultSet(Query2,
            formSearchQuery2);
    try
    {
   
        core.getConnection();
        core.getSearchInfo().setTableSelected(false);
        core.getSearchInfo().setFunProcSelected(true);
        core.getSearchInfo().setViewsSelected(true);
        core.getSearchInfo().setSearchText("abd");
        core.getSearchInfo().setNameMatch(1);
        core.getSearchInfo().setMatchCase(true);
        
        core.search();
        
        assertEquals(0, core.getSearchNamespace().getTables().getSize());
        //assertEquals(0, core.getSearchNamespace().getPartitionTablesGroup().getSize());
        assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
        assertEquals(1, core.getSearchNamespace().getViewGroup().getSize());
        assertEquals(1, core.getSearchNamespace().getFunctions().getSize());

    }
    catch (DatabaseOperationException | DatabaseCriticalException e)
    {
            fail("not expected");
    }
    finally{
        core.clearData();
        core.cleanUpSearch();
    }
    
    
}
@Test
    public void test_tables_funProc_matchCase()
    {
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT')))) and tbl.parttype in ('n', 'p', 'v') ";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");
        formSearchQuery
                .addRow(new Object[] {3, "abc", "3", "PUBLIC", "r", "n", null});
        formSearchQuery.addRow(new Object[] {3, "abcd", "3", "PUBLIC", "f",
                null,
                "{location=gsfs://10.18.96.123:5000/,format=csv,delimiter=|,encoding=UTF-8}"});

        preparedstatementHandler.prepareResultSet(Query, formSearchQuery);

        String Query2 = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang,ns.nspname nsname FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang left join  pg_namespace ns ON ns.oid=pr.pronamespace WHERE lng.lanname in ('plpgsql','sql','c')  and pr.pronamespace=3 and has_function_privilege(pr.oid, 'EXECUTE') and objname  LIKE ? ;";
        MockResultSet formSearchQuery2 = preparedstatementHandler
                .createResultSet();
        formSearchQuery2.addColumn("oid");
        formSearchQuery2.addColumn("objname");
        formSearchQuery2.addColumn("namespace");
        formSearchQuery2.addColumn("ret");
        formSearchQuery2.addColumn("alltype");
        formSearchQuery2.addColumn("nargs");
        formSearchQuery2.addColumn("argtype");
        formSearchQuery2.addColumn("argname");
        formSearchQuery2.addColumn("argmod");
        formSearchQuery2.addColumn("retset");
        formSearchQuery2.addColumn("lang");
        formSearchQuery2.addColumn("nsname");

        formSearchQuery2.addRow(new Object[] {3,"function","3",1,null,0,null,null,null,"f","plpgsql","PUBLIC"});
        preparedstatementHandler.prepareResultSet(Query2, formSearchQuery2);
        try
        {

            core.getConnection();
            core.getSearchInfo().setTableSelected(true);
            core.getSearchInfo().setFunProcSelected(true);
            core.getSearchInfo().setViewsSelected(false);
            core.getSearchInfo().setSearchText("abc");
            core.getSearchInfo().setNameMatch(2);
            core.getSearchInfo().setMatchCase(true);

            core.search();

            assertEquals(1, core.getSearchNamespace().getTables().getSize());
//            assertEquals(1, core.getSearchNamespace().getPartitionTablesGroup()
//                    .getSize());
            assertEquals(1, core.getSearchNamespace().getForeignTablesGroup()
                    .getSize());
            assertEquals(0, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(1, core.getSearchNamespace().getFunctions().getSize());

        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
            fail("not expected");
        }
        finally
        {
            core.clearData();
            core.cleanUpSearch();
        }

    }
  @Test  
    public void test_table_view(){
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT'))) or (tbl.relkind='v' and has_table_privilege(tbl.oid, 'SELECT'))) and tbl.parttype in ('n', 'p', 'v')  and relname  ILIKE ? ;";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");
       
        formSearchQuery.addRow(new Object[] {1,"abc","3","PUBLIC","r","n",null});
        formSearchQuery.addRow(new Object[] {1,"view","3","PUBLIC","v","n",null});
        preparedstatementHandler.prepareResultSet(Query,
                formSearchQuery);
        
        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(true);
            core.getSearchInfo().setFunProcSelected(false);
            core.getSearchInfo().setViewsSelected(true);
            core.getSearchInfo().setSearchText("abc");
            core.getSearchInfo().setNameMatch(0);
            core.getSearchInfo().setMatchCase(false);
            
            core.search();
            
            assertEquals(1, core.getSearchNamespace().getTables().getSize());
//            assertEquals(0, core.getSearchNamespace().getPartitionTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
            assertEquals(1, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getFunctions().getSize());

        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
                fail("not expected");
        }
        finally{
            core.clearData();
            core.cleanUpSearch();
        }
        
        
    }
     @Test   
    public void test_tables_view_funProc(){
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions , regexp_matches(relname,?) from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT'))) or (tbl.relkind='v' and has_table_privilege(tbl.oid, 'SELECT'))) and tbl.parttype in ('n', 'p', 'v') ";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");
        formSearchQuery.addRow(new Object[] {1,"abc","3","PUBLIC","r","n",null});
        formSearchQuery.addRow(new Object[] {3,"abc","3","PUBLIC","r","p",null});
        formSearchQuery.addRow(new Object[] {2,"abcd","3","PUBLIC","f",null,"{location=gsfs://10.18.96.123:5000/,format=csv,delimiter=|,encoding=UTF-8}"});
        formSearchQuery.addRow(new Object[] {1,"view","3","PUBLIC","v","n",null}); 
        
        preparedstatementHandler.prepareResultSet(Query,
                formSearchQuery);
        
        String Query2 = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang,ns.nspname nsname , regexp_matches(objname, ?,'i') FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang left join  pg_namespace ns ON ns.oid=pr.pronamespace WHERE lng.lanname in ('plpgsql','sql','c')  and pr.pronamespace=3 and has_function_privilege(pr.oid, 'EXECUTE')";
        MockResultSet formSearchQuery2 = preparedstatementHandler
                .createResultSet();
        formSearchQuery2.addColumn("oid");
        formSearchQuery2.addColumn("objname");
        formSearchQuery2.addColumn("namespace");
        formSearchQuery2.addColumn("ret");
        formSearchQuery2.addColumn("alltype");
        formSearchQuery2.addColumn("nargs");
        formSearchQuery2.addColumn("argtype");
        formSearchQuery2.addColumn("argname");
        formSearchQuery2.addColumn("argmod");
        formSearchQuery2.addColumn("retset");
        formSearchQuery2.addColumn("lang");
        formSearchQuery2.addColumn("nsname");

        formSearchQuery2.addRow(new Object[] {1,"abdfghj","3",1,null,0,null,null,null,"f","plpgsql","PUBLIC"});
        preparedstatementHandler.prepareResultSet(Query2,
                formSearchQuery2);
        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(true);
            core.getSearchInfo().setFunProcSelected(true);
            core.getSearchInfo().setViewsSelected(true);
            core.getSearchInfo().setSearchText("%");
            core.getSearchInfo().setNameMatch(3);
            core.getSearchInfo().setMatchCase(false);
            
            core.search();
            
            assertEquals(2, core.getSearchNamespace().getTables().getSize());
            assertEquals(1, core.getSearchNamespace().getForeignTablesGroup().getSize());
            assertEquals(1, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(1, core.getSearchNamespace().getFunctions().getSize());

        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
                fail("not expected");
        }
        finally{
            core.clearData();
            core.cleanUpSearch();
        }
        
        
    }

    @Test
    public void test_view_funProc(){
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='v' and has_table_privilege(tbl.oid, 'SELECT'))) and tbl.parttype in ('n', 'p', 'v') ";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");

        formSearchQuery.addRow(new Object[] {1,"abdcf","3","PUBLIC","v","n",null}); 
        
        preparedstatementHandler.prepareResultSet(Query,
                formSearchQuery);
        
        String Query2 = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang,ns.nspname nsname FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang left join  pg_namespace ns ON ns.oid=pr.pronamespace WHERE lng.lanname in ('plpgsql','sql','c')  and pr.pronamespace=3 and has_function_privilege(pr.oid, 'EXECUTE') and objname  ILIKE ? ;";
        MockResultSet formSearchQuery2 = preparedstatementHandler
                .createResultSet();
        formSearchQuery2.addColumn("oid");
        formSearchQuery2.addColumn("objname");
        formSearchQuery2.addColumn("namespace");
        formSearchQuery2.addColumn("ret");
        formSearchQuery2.addColumn("alltype");
        formSearchQuery2.addColumn("nargs");
        formSearchQuery2.addColumn("argtype");
        formSearchQuery2.addColumn("argname");
        formSearchQuery2.addColumn("argmod");
        formSearchQuery2.addColumn("retset");
        formSearchQuery2.addColumn("lang");
        formSearchQuery2.addColumn("nsname");

        formSearchQuery2.addRow(new Object[] {1,"abdfghj","3",1,null,0,null,null,null,"f","sql","PUBLIC"});
        preparedstatementHandler.prepareResultSet(Query2,
                formSearchQuery2);
        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(false);
            core.getSearchInfo().setFunProcSelected(true);
            core.getSearchInfo().setViewsSelected(true);
            core.getSearchInfo().setSearchText("abd");
            core.getSearchInfo().setNameMatch(1);
            core.getSearchInfo().setMatchCase(false);
            
            core.search();
            
            assertEquals(0, core.getSearchNamespace().getTables().getSize());
//            assertEquals(0, core.getSearchNamespace().getPartitionTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
            assertEquals(1, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(1, core.getSearchNamespace().getFunctions().getSize());

        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
                fail("not expected");
        }
        finally{
            core.clearData();
            core.cleanUpSearch();
        }
        
        
    }

    @Test
    public void test_getConnection_failure()
    {
        try
        {
            for (int i = 1; i <= 20; i++)
            {

                core.getSelectedDb().getConnectionManager().getFreeConnection();
            }
            core.getConnection();
            fail("Connection should be successful");
        }
        catch (MPPDBIDEException e)
        {
            assertTrue(true);

        }

    }
    
    @Test
    public void test_search_only_regulartables_DBOperationException()
    {
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT')))) and tbl.parttype in ('n', 'p', 'v')  and relname  LIKE ? ;";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");

        formSearchQuery
                .addRow(new Object[] {3, "abc", "3", "PUBLIC", "r", "n", null});
        preparedstatementHandler.prepareThrowsSQLException(Query);

        try
        {

            core.getConnection();
            core.getSearchInfo().setTableSelected(true);
            core.getSearchInfo().setFunProcSelected(false);
            core.getSearchInfo().setViewsSelected(false);
            core.getSearchInfo().setSearchText("bc");
            core.getSearchInfo().setNameMatch(0);
            core.getSearchInfo().setMatchCase(true);

            core.search();
            fail("Not Excepted to come here");

        }
        catch (DatabaseCriticalException e)
        {
            
            fail("Not Excepted to come here");
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }

        finally
        {
            core.clearData();
            core.cleanUpSearch();
        }
    }
    
    @Test
    public void test_search_only_regulartables_DBCriticleException()
    {
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT')))) and tbl.parttype in ('n', 'p', 'v')  and relname  LIKE ? ;";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");

        formSearchQuery
                .addRow(new Object[] {1, "abc", "3", "PUBLIC", "r", "n", null});
        preparedstatementHandler.prepareThrowsSQLException(Query,new SQLException(
                "Throwing SQL exception intentionally.", "57PSQLException"));

        try
        {

            core.getConnection();
            core.getSearchInfo().setTableSelected(true);
            core.getSearchInfo().setFunProcSelected(false);
            core.getSearchInfo().setViewsSelected(false);
            core.getSearchInfo().setSearchText("bc");
            core.getSearchInfo().setNameMatch(0);
            core.getSearchInfo().setMatchCase(true);

            core.search();
            fail("Not Excepted to come here");

        }
        catch (DatabaseCriticalException e)
        {
            
            assertTrue(true);

        }
        catch (DatabaseOperationException e)
        {
            fail("Not Excepted to come here");
        }

        finally
        {
            core.clearData();
            core.cleanUpSearch();
        }
    }
    
    @Test
    public void test_search_only_funcProc_DBOperation(){

        String Query = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang,ns.nspname nsname , regexp_matches(objname, ?) FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang left join  pg_namespace ns ON ns.oid=pr.pronamespace WHERE lng.lanname in ('plpgsql','sql','c')  and pr.pronamespace=3";
        MockResultSet formSearchQuery2 = preparedstatementHandler
                .createResultSet();
        formSearchQuery2.addColumn("oid");
        formSearchQuery2.addColumn("objname");
        formSearchQuery2.addColumn("namespace");
        formSearchQuery2.addColumn("ret");
        formSearchQuery2.addColumn("alltype");
        formSearchQuery2.addColumn("nargs");
        formSearchQuery2.addColumn("argtype");
        formSearchQuery2.addColumn("argname");
        formSearchQuery2.addColumn("argmod");
        formSearchQuery2.addColumn("retset");
        formSearchQuery2.addColumn("lang");
        formSearchQuery2.addColumn("nsname");

        formSearchQuery2.addRow(new Object[] {1,"abdfghj","3",1,null,0,null,null,null,"f","sql","PUBLIC"});
        preparedstatementHandler.prepareThrowsSQLException(Query);
        
        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(false);
            core.getSearchInfo().setFunProcSelected(true);
            core.getSearchInfo().setViewsSelected(false);
            core.getSearchInfo().setSearchText("function%");
            core.getSearchInfo().setNameMatch(3);
            core.getSearchInfo().setMatchCase(true);
            
            core.search();
            fail("not expected");

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
           assertTrue(true);
        }
        
        finally{
            core.clearData();
            core.cleanUpSearch();
        }

        
    }
    
    @Test
    public void test_search_only_funcProc_DBCriticle(){
        String Query = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang,ns.nspname nsname , regexp_matches(objname, ?) FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang left join  pg_namespace ns ON ns.oid=pr.pronamespace WHERE lng.lanname in ('plpgsql','sql','c')  and pr.pronamespace=3";
        MockResultSet formSearchQuery2 = preparedstatementHandler
                .createResultSet();
        formSearchQuery2.addColumn("oid");
        formSearchQuery2.addColumn("objname");
        formSearchQuery2.addColumn("namespace");
        formSearchQuery2.addColumn("ret");
        formSearchQuery2.addColumn("alltype");
        formSearchQuery2.addColumn("nargs");
        formSearchQuery2.addColumn("argtype");
        formSearchQuery2.addColumn("argname");
        formSearchQuery2.addColumn("argmod");
        formSearchQuery2.addColumn("retset");
        formSearchQuery2.addColumn("lang");
        formSearchQuery2.addColumn("nsname");

        formSearchQuery2.addRow(new Object[] {1,"abdfghj","3",1,null,0,null,null,null,"f","sql","PUBLIC"});
        preparedstatementHandler.prepareThrowsSQLException(Query,new SQLException(
                "Throwing SQL exception intentionally.", "57PSQLException"));
        
        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(false);
            core.getSearchInfo().setFunProcSelected(true);
            core.getSearchInfo().setViewsSelected(false);
            core.getSearchInfo().setSearchText("function%");
            core.getSearchInfo().setNameMatch(3);
            core.getSearchInfo().setMatchCase(true);
            
            core.search();
            fail("not expected");

        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(true);
        }
        catch (DatabaseOperationException e)
        {
           fail("not expected");
        }
        
        finally{
            core.clearData();
            core.cleanUpSearch();
        }

        
    }
    
    @Test
    public void test_search_only_funcProc_SQLEXception(){
        
        ExceptionConnectionHelper exceptionConnection = new ExceptionConnectionHelper();
        exceptionConnection.setNeedExceptioStatement(true);
        exceptionConnection.setNeedExceptionResultset(true);
        exceptionConnection.setThrowExceptionNext(true);
        exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
        
        getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection); 
        
        String Query = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang,ns.nspname nsname , regexp_matches(objname,'function%') FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang left join  pg_namespace ns ON ns.oid=pr.pronamespace WHERE lng.lanname in ('plpgsql','sql')  and pr.pronamespace=1;";
        MockResultSet formSearchQuery2 = preparedstatementHandler
                .createResultSet();
        formSearchQuery2.addColumn("oid");
        formSearchQuery2.addColumn("objname");
        formSearchQuery2.addColumn("namespace");
        formSearchQuery2.addColumn("ret");
        formSearchQuery2.addColumn("alltype");
        formSearchQuery2.addColumn("nargs");
        formSearchQuery2.addColumn("argtype");
        formSearchQuery2.addColumn("argname");
        formSearchQuery2.addColumn("argmod");
        formSearchQuery2.addColumn("retset");
        formSearchQuery2.addColumn("lang");
        formSearchQuery2.addColumn("nsname");

        formSearchQuery2.addRow(new Object[] {1,"abdfghj","1",1,null,0,null,null,null,"f","sql","PUBLIC"});
        preparedstatementHandler.prepareResultSet(Query,formSearchQuery2);
        

        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(false);
            core.getSearchInfo().setFunProcSelected(true);
            core.getSearchInfo().setViewsSelected(false);
            core.getSearchInfo().setSearchText("function%");
            core.getSearchInfo().setNameMatch(3);
            core.getSearchInfo().setMatchCase(true);
            
            core.search();
            fail("not expected");

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
        finally
        {
            core.clearData();
            core.cleanUpSearch();
        }

        
    }
    
    @Test 
    public void test_search_only_views_SQLException(){
        
        ExceptionConnectionHelper exceptionConnection = new ExceptionConnectionHelper();
        exceptionConnection.setNeedExceptioStatement(true);
        exceptionConnection.setNeedExceptionResultset(true);
        exceptionConnection.setThrowExceptionNext(true);
        exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
        
        getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection); 

            String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =1 and ( (tbl.relkind='v' and has_table_privilege(tbl.oid, 'SELECT'))) and tbl.parttype in ('n', 'p', 'v')  and relname  LIKE ? ;";
            MockResultSet formSearchQuery = preparedstatementHandler
                    .createResultSet();
            formSearchQuery.addColumn("oid");
            formSearchQuery.addColumn("relname");
            formSearchQuery.addColumn("relnamespace");
            formSearchQuery.addColumn("nsname");
            formSearchQuery.addColumn("relkind");
            formSearchQuery.addColumn("parttype");
            formSearchQuery.addColumn("ftoptions");
            
            formSearchQuery.addRow(new Object[] {1,"view","1","PUBLIC","v","n",null});
            preparedstatementHandler.prepareResultSet(Query,
                    formSearchQuery);
            
            try
            {
           
                core.getConnection();
                core.getSearchInfo().setTableSelected(false);
                core.getSearchInfo().setFunProcSelected(false);
                core.getSearchInfo().setViewsSelected(true);
                core.getSearchInfo().setSearchText("view");
                core.getSearchInfo().setNameMatch(2);
                core.getSearchInfo().setMatchCase(true);
                
                core.search();
                
            }
            catch (DatabaseCriticalException e)
            {
                fail("not expected");
            }
            catch (DatabaseOperationException e)
            {
                assertTrue(true);
            }
        catch (Exception e)
        {
            assertTrue(true);
        }
            finally
            {
                core.clearData();
                core.cleanUpSearch();
            }
        
            
        }
    
    @Test
    public void test_search_only_funcProc_1(){
        String Query = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang,ns.nspname nsname , regexp_matches(objname, ?) FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang left join  pg_namespace ns ON ns.oid=pr.pronamespace WHERE lng.lanname in ('plpgsql','sql','c')  and pr.pronamespace=3";
        MockResultSet formSearchQuery2 = preparedstatementHandler
                .createResultSet();
        formSearchQuery2.addColumn("oid");
        formSearchQuery2.addColumn("objname");
        formSearchQuery2.addColumn("namespace");
        formSearchQuery2.addColumn("ret");
        formSearchQuery2.addColumn("alltype");
        formSearchQuery2.addColumn("nargs");
        formSearchQuery2.addColumn("argtype");
        formSearchQuery2.addColumn("argname");
        formSearchQuery2.addColumn("argmod");
        formSearchQuery2.addColumn("retset");
        formSearchQuery2.addColumn("lang");
        formSearchQuery2.addColumn("nsname");

        formSearchQuery2.addRow(new Object[] {1,"function","3",1,null,0,null,null,null,"f","sql","information_schema"});
        

        preparedstatementHandler.prepareResultSet(Query,formSearchQuery2);
        

        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(false);
            core.getSearchInfo().setFunProcSelected(true);
            core.getSearchInfo().setViewsSelected(false);
            core.getSearchInfo().setSearchText("function%");
            core.getSearchInfo().setNameMatch(3);
            core.getSearchInfo().setMatchCase(true);
            
            core.search();
            assertEquals(0, core.getSearchNamespace().getTables().getSize());
//            assertEquals(0, core.getSearchNamespace().getPartitionTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(1, core.getSearchNamespace().getFunctions().getSize());
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }

        finally
        {
            core.clearData();
            core.cleanUpSearch();
        }

    }
    
    
    @Test 
    public void test_search_only_views_2(){
            String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='v' and has_table_privilege(tbl.oid, 'SELECT'))) and tbl.parttype in ('n', 'p', 'v')  and relname  LIKE ? ;";
            MockResultSet formSearchQuery = preparedstatementHandler
                    .createResultSet();
            formSearchQuery.addColumn("oid");
            formSearchQuery.addColumn("relname");
            formSearchQuery.addColumn("relnamespace");
            formSearchQuery.addColumn("nsname");
            formSearchQuery.addColumn("relkind");
            formSearchQuery.addColumn("parttype");
            formSearchQuery.addColumn("ftoptions");
            
            formSearchQuery.addRow(new Object[] {1,"view","3","information_schema","v","n",null});
            preparedstatementHandler.prepareResultSet(Query,
                    formSearchQuery);
            
            try
            {
           
                core.getConnection();
                core.getSearchInfo().setTableSelected(false);
                core.getSearchInfo().setFunProcSelected(false);
                core.getSearchInfo().setViewsSelected(true);
                core.getSearchInfo().setSearchText("view");
                core.getSearchInfo().setNameMatch(2);
                core.getSearchInfo().setMatchCase(true);
                
                core.search();
                core.getSearchedDatabase();
                core.setExecutionTime("1ms");
                assertEquals(0, core.getSearchNamespace().getTables().getSize());
//                assertEquals(0, core.getSearchNamespace().getPartitionTablesGroup().getSize());
                assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
                assertEquals(1, core.getSearchNamespace().getViewGroup().getSize());
                assertEquals(0, core.getSearchNamespace().getFunctions().getSize());
                assertEquals(1, core.getRowsFetched());
                assertEquals("1ms", core.getExecutionTime());
                assertEquals("Gauss", core.getSearchedDatabase().getDbName());
                assertEquals(1, core.getSearchedDatabase().getAllSearchNameSpaces().size());
                assertTrue(core.getSearchedDatabase().getDb()!=null);
                assertFalse(core.getSearchedDatabase().equals(null));
                assertFalse(core.getSearchNamespace().equals(null));
            }
            catch (DatabaseCriticalException e)
            {
                fail("not expected");
            }
            catch (DatabaseOperationException e)
            {
                fail("not expected");
            }

            finally
            {
                core.clearData();
                core.cleanUpSearch();
            }
        
            
        }
    
    @Test 
    public void test_search_only_views_3(){
            String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='v' and has_table_privilege(tbl.oid, 'SELECT'))) and tbl.parttype in ('n', 'p', 'v')  and relname  LIKE ? ;";
            MockResultSet formSearchQuery = preparedstatementHandler
                    .createResultSet();
            formSearchQuery.addColumn("oid");
            formSearchQuery.addColumn("relname");
            formSearchQuery.addColumn("relnamespace");
            formSearchQuery.addColumn("nsname");
            formSearchQuery.addColumn("relkind");
            formSearchQuery.addColumn("parttype");
            formSearchQuery.addColumn("ftoptions");
            
            formSearchQuery.addRow(new Object[] {1,"view","3","information_schema","v","n",null});
            preparedstatementHandler.prepareResultSet(Query,
                    formSearchQuery);
            
            try
            {
           
                core.getConnection();
                core.getSearchInfo().setTableSelected(false);
                core.getSearchInfo().setFunProcSelected(false);
                core.getSearchInfo().setViewsSelected(true);
                core.getSearchInfo().setSearchText("view");
                core.getSearchInfo().setNameMatch(2);
                core.getSearchInfo().setMatchCase(true);
                
                core.search();
                core.cancelQuery();
            }
            catch (DatabaseCriticalException e)
            {
                fail("not expected");
            }
            catch (DatabaseOperationException e)
            {
                fail("not expected");
            }

            finally
            {
                core.clearData();
                core.cleanUpSearch();
            }
        

        }
    
    @Test   
    public void test_tables_view_funProc_sequences(){
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions , regexp_matches(relname,?) from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT'))) or (tbl.relkind='v' and has_table_privilege(tbl.oid, 'SELECT')) or (tbl.relkind='S' and has_sequence_privilege('\"'|| ns.nspname||'\"'||'.'||'\"'||tbl.relname||'\"', 'USAGE'))) and tbl.parttype in ('n', 'p', 'v') ";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relowner");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");
        formSearchQuery.addRow(new Object[] {1,"abc","3","PUBLIC",null,"r","n",null});
        formSearchQuery.addRow(new Object[] {3,"abd","3","PUBLIC",null,"r","p",null});
        formSearchQuery.addRow(new Object[] {2,"abcd","3","PUBLIC",null,"f",null,"{location=gsfs://10.18.96.123:5000/,format=csv,delimiter=|,encoding=UTF-8}"});
        formSearchQuery.addRow(new Object[] {1,"view","3","PUBLIC",null,"v","n",null}); 
        formSearchQuery.addRow(new Object[] {1,"s1","3","PUBLIC",null,"S","n",null});
        preparedstatementHandler.prepareResultSet(Query,
                formSearchQuery);
        
        String Query2 = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang,ns.nspname nsname , regexp_matches(objname, ?,'i') FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang left join  pg_namespace ns ON ns.oid=pr.pronamespace WHERE lng.lanname in ('plpgsql','sql','c')  and pr.pronamespace=3 and has_function_privilege(pr.oid, 'EXECUTE')";
        MockResultSet formSearchQuery2 = preparedstatementHandler
                .createResultSet();
        formSearchQuery2.addColumn("oid");
        formSearchQuery2.addColumn("objname");
        formSearchQuery2.addColumn("namespace");
        formSearchQuery2.addColumn("ret");
        formSearchQuery2.addColumn("alltype");
        formSearchQuery2.addColumn("nargs");
        formSearchQuery2.addColumn("argtype");
        formSearchQuery2.addColumn("argname");
        formSearchQuery2.addColumn("argmod");
        formSearchQuery2.addColumn("retset");
        formSearchQuery2.addColumn("lang");
        formSearchQuery2.addColumn("nsname");

        formSearchQuery2.addRow(new Object[] {1,"abdfghj","3",1,null,0,null,null,null,"f","plpgsql","PUBLIC"});
        preparedstatementHandler.prepareResultSet(Query2,
                formSearchQuery2);
        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(true);
            core.getSearchInfo().setFunProcSelected(true);
            core.getSearchInfo().setViewsSelected(true);
            core.getSearchInfo().setSearchText("%");
            core.getSearchInfo().setNameMatch(3);
            core.getSearchInfo().setMatchCase(false);
            core.getSearchInfo().setSequenceSelected(true);
            
            core.search();
            
            assertEquals(2, core.getSearchNamespace().getTables().getSize());
            assertEquals(1, core.getSearchNamespace().getForeignTablesGroup().getSize());
            assertEquals(1, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(1, core.getSearchNamespace().getFunctions().getSize());
            assertEquals(1, core.getSearchNamespace().getSequenceGroup().getSize());

        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
                fail("not expected");
        }
        finally{
            core.clearData();
            core.cleanUpSearch();
        }
        
        
    }
    
    @Test   
    public void test_tables_view_sequences(){
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions , regexp_matches(relname,?) from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT'))) or (tbl.relkind='v' and has_table_privilege(tbl.oid, 'SELECT')) or (tbl.relkind='S' and has_sequence_privilege('\"'|| ns.nspname||'\"'||'.'||'\"'||tbl.relname||'\"', 'USAGE'))) and tbl.parttype in ('n', 'p', 'v') ";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relowner");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");
        formSearchQuery.addRow(new Object[] {1,"abc","3","PUBLIC",null,"r","n",null});
        formSearchQuery.addRow(new Object[] {1,"view","3","PUBLIC",null,"v","n",null}); 
        formSearchQuery.addRow(new Object[] {1,"s1","3","PUBLIC",null,"S","n",null});

        preparedstatementHandler.prepareResultSet(Query,
                formSearchQuery);
        
     
        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(true);
            core.getSearchInfo().setFunProcSelected(false);
            core.getSearchInfo().setViewsSelected(true);
            core.getSearchInfo().setSearchText("%");
            core.getSearchInfo().setNameMatch(3);
            core.getSearchInfo().setMatchCase(false);
            core.getSearchInfo().setSequenceSelected(true);
            
            core.search();
            
            assertEquals(1, core.getSearchNamespace().getTables().getSize());
//            assertEquals(0, core.getSearchNamespace().getPartitionTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
            assertEquals(1, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getFunctions().getSize());
            assertEquals(1, core.getSearchNamespace().getSequenceGroup().getSize());


        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
                fail("not expected");
        }
        finally{
            core.clearData();
            core.cleanUpSearch();
        }
        
        
    }
    
    @Test   
    public void test_tables_sequences(){
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions , regexp_matches(relname,?) from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='r' or tbl.relkind='f' and tbl.oid in (with x as (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE')) select * from x where has_table_privilege(x.pcrelid, 'SELECT'))) or (tbl.relkind='S' and has_sequence_privilege('\"'|| ns.nspname||'\"'||'.'||'\"'||tbl.relname||'\"', 'USAGE'))) and tbl.parttype in ('n', 'p', 'v') ";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relowner");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");
        formSearchQuery.addRow(new Object[] {1,"abc","3","PUBLIC",null,"r","n",null});
        formSearchQuery.addRow(new Object[] {1,"s1","3","PUBLIC",null,"S","n",null});
        preparedstatementHandler.prepareResultSet(Query,
                formSearchQuery);
        
     
        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(true);
            core.getSearchInfo().setFunProcSelected(false);
            core.getSearchInfo().setViewsSelected(false);
            core.getSearchInfo().setSearchText("%");
            core.getSearchInfo().setNameMatch(3);
            core.getSearchInfo().setMatchCase(false);
            core.getSearchInfo().setSequenceSelected(true);
            
            core.search();
            
            assertEquals(1, core.getSearchNamespace().getTables().getSize());
//            assertEquals(0, core.getSearchNamespace().getPartitionTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getFunctions().getSize());
            assertEquals(1, core.getSearchNamespace().getSequenceGroup().getSize());


        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
                fail("not expected");
        }
        finally{
            core.clearData();
            core.cleanUpSearch();
        }
        
        
    }
    
    @Test   
    public void test_only_sequences(){
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions , regexp_matches(relname,?) from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='S' and has_sequence_privilege('\"'|| ns.nspname||'\"'||'.'||'\"'||tbl.relname||'\"', 'USAGE'))) and tbl.parttype in ('n', 'p', 'v') ";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relowner");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");
        formSearchQuery.addRow(new Object[] {1,"s1","3","PUBLIC",null,"S","n",null});
        preparedstatementHandler.prepareResultSet(Query,
                formSearchQuery);
        
     
        try
        {
       
            core.getConnection();
            core.getSearchInfo().setTableSelected(false);
            core.getSearchInfo().setFunProcSelected(false);
            core.getSearchInfo().setViewsSelected(false);
            core.getSearchInfo().setSearchText("%");
            core.getSearchInfo().setNameMatch(3);
            core.getSearchInfo().setMatchCase(false);
            core.getSearchInfo().setSequenceSelected(true);
            
            core.search();
            
            assertEquals(0, core.getSearchNamespace().getTables().getSize());
//            assertEquals(0, core.getSearchNamespace().getPartitionTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getFunctions().getSize());
            assertEquals(1, core.getSearchNamespace().getSequenceGroup().getSize());
            core.getSearchNamespace().getChildren();
            assertEquals(6, SearchObjectEnum.values().length);
            assertEquals(SearchObjectEnum.SEARCH_INI,SearchObjectEnum.valueOf("SEARCH_INI"));
            assertEquals(SearchNameMatchEnum.CONTAINS,SearchNameMatchEnum.valueOf("CONTAINS"));
        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
                fail("not expected");
        }
        finally{
            core.clearData();
            core.cleanUpSearch();
        }
        
        
    }
    
    @Test   
    public void test_AbstractSearchObjUtils_methods(){
        ArrayList<String> array = new ArrayList<String>();
        array.add("test1");
        array.add("test2");
        assertEquals(0,AbstractSearchObjUtils.getIndexByValue(array, "test1"));
        assertEquals(1,AbstractSearchObjUtils.getIndexByValue(array, "test2"));
        assertEquals(0,AbstractSearchObjUtils.getIndexByValue(array, "test3"));
    }
    
    @Test   
    public void test_SearchObjCore_methods(){
        SearchObjCore searchObjCore = new SearchObjCore();
        assertNotNull(searchObjCore.getNameMatchList());
    }
    
    @Test   
    public void test_only_sequences1(){
        String Query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions , regexp_matches(relname,?) from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relnamespace =3 and ( (tbl.relkind='S' and has_sequence_privilege('\"'|| ns.nspname||'\"'||'.'||'\"'||tbl.relname||'\"', 'USAGE'))) and tbl.parttype in ('n', 'p', 'v') ";
        MockResultSet formSearchQuery = preparedstatementHandler
                .createResultSet();
        formSearchQuery.addColumn("oid");
        formSearchQuery.addColumn("relname");
        formSearchQuery.addColumn("relnamespace");
        formSearchQuery.addColumn("nsname");
        formSearchQuery.addColumn("relowner");
        formSearchQuery.addColumn("relkind");
        formSearchQuery.addColumn("parttype");
        formSearchQuery.addColumn("ftoptions");
        formSearchQuery.addRow(new Object[] {1,"s1","3","pg_catalog",null,"S","n",null});
        preparedstatementHandler.prepareResultSet(Query,
                formSearchQuery);
        
     
        try
        {
            Namespace ns=new SystemNamespace(1, "pg_catalog", core.getSelectedDb());

            SequenceMetadata seq = new SequenceMetadata(1, "s1",
                    ns);
            ns.getSequenceGroup().addToGroup(seq);
            core.getSelectedDb().getSystemNamespaceGroup().addToGroup((SystemNamespace) ns);
            core.getConnection();
            core.getSearchInfo().setTableSelected(false);
            core.getSearchInfo().setFunProcSelected(false);
            core.getSearchInfo().setViewsSelected(false);
            core.getSearchInfo().setSearchText("%");
            core.getSearchInfo().setNameMatch(3);
            core.getSearchInfo().setMatchCase(false);
            core.getSearchInfo().setSequenceSelected(true);
            
            core.search();
            
            assertEquals(0, core.getSearchNamespace().getTables().getSize());
//            assertEquals(0, core.getSearchNamespace().getPartitionTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getForeignTablesGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getViewGroup().getSize());
            assertEquals(0, core.getSearchNamespace().getFunctions().getSize());
            assertEquals(1, core.getSearchNamespace().getSequenceGroup().getSize());
        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
                fail("not expected");
        }
        finally{
            core.clearData();
            core.cleanUpSearch();
        }
        
        
    }
}
