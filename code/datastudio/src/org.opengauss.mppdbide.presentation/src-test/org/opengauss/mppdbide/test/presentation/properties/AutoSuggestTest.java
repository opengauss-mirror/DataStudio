package org.opengauss.mppdbide.test.presentation.properties;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.contentassist.ContentAssistKeywords;
import org.opengauss.mppdbide.bl.contentassist.ContentAssistProcesserData;
import org.opengauss.mppdbide.bl.keyword.KeywordObject;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseUtils;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.ForeignTable;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.SequenceMetadata;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.UserNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.presentation.autorefresh.AutoRefreshQueryFormation;
import org.opengauss.mppdbide.presentation.autorefresh.RefreshObjectDetails;
import org.opengauss.mppdbide.presentation.contentassistprocesser.ContentAssistProcesserCore;
import org.opengauss.mppdbide.presentation.view.ViewViewDataCore;
import org.opengauss.mppdbide.test.presentation.table.MockPresentationBLPreferenceImpl;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.files.FileValidationUtils;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class AutoSuggestTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    ContentAssistProcesserCore        core;
    ContentAssistProcesserCore        core1;
    ContentAssistProcesserData        data;
    Database                          database;

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
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        connection = new MockConnection();
        // test for logging
        MPPDBIDELoggerUtility.setArgs(new String[] {"-logfolder=.", "-detailLogging=true"});

        // MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
        database = connProfCache.getDbForProfileId(profileId);
        core = new ContentAssistProcesserCore(database);
        core1 = new ContentAssistProcesserCore(null);
        data = new ContentAssistProcesserData(database);
        getAllDatabaseObjects();
    }

    private void getAllDatabaseObjects()
    {
        // MockViewUtils.DropView(preparedstatementHandler, "mytestview1");
        try
        {

            database.getServer().refresh();
            Namespace ns1 = new UserNamespace(6, "ns1", database);
            Namespace ns2 = new UserNamespace(2, "NS1", database);
            Namespace ns3 = new UserNamespace(3, "NS2", database);
            Namespace ns4 = new UserNamespace(4, "yns2", database);
            Namespace ns6 = new UserNamespace(5, "\"NS1\"", database);
            Namespace ns7 = new UserNamespace(7, "Namespc", database);

                
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns1);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns2);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns3);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns4);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns6);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns7);

            TableMetaData ptab1 = new PartitionTable(ns7);
            ns7.addTableToSearchPool(ptab1);

            TableMetaData tbl1 = new TableMetaData(1, "tbl1", ns1, "");
            ColumnMetaData clm = new ColumnMetaData(tbl1, 1, "Col1",  new TypeMetaData(11, "Integer", ns3));
            tbl1.addColumn(clm);
            TableMetaData tbl2 = new TableMetaData(2, "tbl2", ns1, "");
            ColumnMetaData clm1 = new ColumnMetaData(tbl2, 1, "Col2", null);
            tbl2.addColumn(clm1);
            TableMetaData tbl3 = new TableMetaData(3, "TBL1", ns1, "");
            ColumnMetaData clm2 = new ColumnMetaData(tbl3, 1, "Col3", null);
            tbl3.addColumn(clm2);

            ns1.addTableToSearchPool(tbl1);
            ns1.addTableToSearchPool(tbl2);
            ns1.addTableToSearchPool(tbl3);

            TableMetaData tbl4 = new TableMetaData(4, "tbl1", ns2, "");
            ColumnMetaData clm3 = new ColumnMetaData(tbl4, 1, "Col4", new TypeMetaData(11, "Integer", ns3));
            tbl4.addColumn(clm3);
            TableMetaData tbl5 = new TableMetaData(5, "tbl2", ns2, "");
            ColumnMetaData clm4 = new ColumnMetaData(tbl5, 1, "Col5", null);
            tbl5.addColumn(clm4);
            TableMetaData tbl6 = new TableMetaData(6, "TBL1", ns2, "");
            ColumnMetaData clm5 = new ColumnMetaData(tbl6, 1, "Col6", null);
            tbl6.addColumn(clm5);
            TableMetaData tbl7 = new TableMetaData(7, "TBL2", ns2, "");
            ColumnMetaData clm6 = new ColumnMetaData(tbl7, 1, "Col7", null);
            tbl7.addColumn(clm6);
            TableMetaData tbl110 = new TableMetaData(110, "T}BL1", ns2, "");
            ColumnMetaData clm110 = new ColumnMetaData(tbl110, 1, "Col7", null);
            tbl7.addColumn(clm110);

            ViewMetaData view = new ViewMetaData(1, "Yiew1", ns2,ns2.getDatabase());
            ViewColumnMetaData viewCol = new ViewColumnMetaData(view, 2, "viewclm", new TypeMetaData(0, "int", ns2));
            view.getColumns().addItem(viewCol);
            
            SequenceMetadata seq = new SequenceMetadata(ns2);
            seq.setName("seq");

            ns2.addTableToSearchPool(tbl5);
            ns2.addTableToSearchPool(tbl6);
            ns2.addTableToSearchPool(tbl7);
            ns2.addTableToSearchPool(tbl4);
            ns2.addTableToSearchPool(tbl110);
            ns2.addView(view);
            ns2.addSequence(seq);
            database.getSearchPoolManager().addsequenceToSearchPool(seq);

            TableMetaData tbl8 = new TableMetaData(8, "tbl1", ns3, "");
            ColumnMetaData clm8 = new ColumnMetaData(tbl8, 1, "Col8", new TypeMetaData(11, "Integer", ns3));
            tbl8.addColumn(clm8);
            TableMetaData tbl9 = new TableMetaData(9, "tbl2", ns3, "");
            ColumnMetaData clm9 = new ColumnMetaData(tbl9, 1, "Col9", null);
            tbl9.addColumn(clm9);

            ns3.addTableToSearchPool(tbl8);
            ns3.addTableToSearchPool(tbl9);

            TableMetaData tbl10 = new TableMetaData(10, "Tbl11", ns4, "");
            ColumnMetaData clm10 = new ColumnMetaData(tbl10, 1, "Col10", null);
            tbl10.addColumn(clm10);
            TableMetaData tbl11 = new TableMetaData(11, "xtbl2", ns4, "");
            ColumnMetaData clm11 = new ColumnMetaData(tbl11, 1, "Col11", null);
            tbl11.addColumn(clm11);
            TableMetaData tbl12 = new TableMetaData(12, "NS1", ns4, "");
            ColumnMetaData clm12 = new ColumnMetaData(tbl12, 1, "Col12", null);
            tbl12.addColumn(clm12);

            ns4.addTableToSearchPool(tbl10);
            ns4.addTableToSearchPool(tbl11);
            ns4.addTableToSearchPool(tbl12);

            TableMetaData tbl14 = new TableMetaData(13, "ybl1", ns6, "");
            ColumnMetaData clm14 = new ColumnMetaData(tbl14, 1, "Col14", null);
            tbl14.addColumn(clm14);

            ns6.addTableToSearchPool(tbl14);

         
            ForeignTable ForeignTbl = new ForeignTable(ns1, OBJECTTYPE.FOREIGN_TABLE_GDS);
            ForeignTbl.setName("Ftable1");
            ForeignTbl.setOid(1);
            ns1.addForeignTableToGroup(ForeignTbl);
            ns1.addTableToSearchPool(ForeignTbl);
            ColumnMetaData fClm10 = new ColumnMetaData(ForeignTbl, 1, "FCol10", null);
            ForeignTbl.addColumn(fClm10);
            
            ForeignTable ForeignTbl2 = new ForeignTable(ns1, OBJECTTYPE.FOREIGN_TABLE_GDS);
            ForeignTbl2.setName("Ftable2");
            ForeignTbl.setOid(2);
            ns1.addForeignTableToGroup(ForeignTbl2);
            ns1.addTableToSearchPool(ForeignTbl2);
            ColumnMetaData fClm11 = new ColumnMetaData(ForeignTbl, 1, "FCol11", null);
            ForeignTbl2.addColumn(fClm11);
            
            PartitionTable ptab = new PartitionTable(ns2);
            ptab.setName("partTable");
            ptab.setOid(1);
            ns2.addTableToGroup(ptab);
            ns2.addTableToSearchPool(ptab);
            ColumnMetaData pClm11 = new ColumnMetaData(ptab, 1, "PCol11", null);
            ptab.addColumn(pClm11);
            
            PartitionTable ptab11 = new PartitionTable(ns2);
            ptab11.setName("partTable1");
            ptab.setOid(2);
            ns2.addTableToGroup(ptab11);
            ns2.addTableToSearchPool(ptab11);
            ColumnMetaData pClm12 = new ColumnMetaData(ptab11, 1, "PCol12", null);
            ptab11.addColumn(pClm12);
            
           
            System.out.println(database.getAllNameSpaces());
            
          

            /*
             * System.out.println(namespaces.size()); for (Namespace namespace :
             * namespaces) {
             * namespace.getAllObjects(database.getObjBrowserConn()); }
             */

        }
        catch (DatabaseOperationException e)
        {
            
            e.printStackTrace();
        }
        catch (DatabaseCriticalException e)
        {
            
            e.printStackTrace();
        }
        catch (Exception e)
        {
            
            e.printStackTrace();
        }
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
    public void testAutoSuggestForPrefixNull()
    {
        LinkedHashMap<String, ServerObject> map = core.getContextProposals("", " ");
        int noOfNameSpaces = 0;
        int noOfTables = 0;
        int noOfViews = 0;
        int noOfSequences = 0;
        int noOfFunctios = 0;
        int noOfDatatypes = 0;
        int noOfKeywords = 0;
        for (ServerObject obj : map.values())
        {
            if (obj instanceof Namespace) {
                noOfNameSpaces++;
            } else if (obj instanceof TableMetaData) {
                noOfTables++;
            } else if (obj instanceof DebugObjects) {
                noOfFunctios++;
            } else if (obj instanceof SequenceMetadata) {
                noOfSequences++;
            } else if (obj instanceof ViewMetaData) {
                noOfViews++;
            } else if (obj instanceof TypeMetaData) {
                noOfDatatypes++;
            }else if(obj instanceof KeywordObject){
                noOfKeywords++;
            }
        }
        int sum = noOfNameSpaces + noOfTables + noOfFunctios + noOfViews
                + noOfSequences+noOfDatatypes+noOfKeywords;
        assertEquals(map.size(), sum);
    }

    @Test
    public void testAutoSuggestForPrefix1()
    {
        LinkedHashMap<String, ServerObject> map = core.getContextProposals("y",
                "select * from y");
        for (ServerObject obj : map.values())
        {
            if (obj instanceof Namespace)
            {
                assertEquals("yns2", obj.getName());
            }
            else if (obj instanceof TableMetaData)
            {
                assertEquals("ybl1", obj.getName());
            }
            else if (obj instanceof ViewMetaData)
            {
                assertEquals("Yiew1", obj.getName());
            }
        }
        assertEquals(5, map.size());

    }

    @Test
    public void testAutoSuggestForPrefix2()
    {
        LinkedHashMap<String, ServerObject> map = core.getContextProposals("\"NS\"",
                "select * from \"NS\"");
        for (ServerObject obj : map.values())
        {
            if (obj instanceof Namespace)
            {
                assertTrue(obj.getName().equals("ns1")
                        || obj.getName().equals("NS1")
                        || obj.getName().equals("NS2"));
                ;
            }
            else if (obj instanceof TableMetaData)
            {
                assertEquals("NS1", obj.getName());
            }
        }
        assertEquals(4, map.size());
    }

    @Test
    public void testAutoSuggestForPrefix3()
    {
        LinkedHashMap<String, ServerObject> map = core
                .getContextProposals("\"NS1\"", "select * from \"NS1\"");
        for (ServerObject obj : map.values())
        {
            if (obj instanceof Namespace)
            {
                assertTrue(obj.getName().equals("ns1")
                        || obj.getName().equals("NS1"));
            }
            else if (obj instanceof TableMetaData)
            {
                assertEquals("NS1", obj.getName());
            }
        }
        assertEquals(3, map.size());
    }

    @Test
    public void testAutoSuggestForPrefix4()
    {
        LinkedHashMap<String, ServerObject> map = core
                .getContextProposals("\"NS1\".", "select * from \"NS1\".");
        // try{
        Namespace ns;
        try
        {
            ns = database.getNameSpaceById(2);
            for (ServerObject obj : map.values())
            {
                if (obj instanceof TableMetaData)
                {
                    TableMetaData tbl = (TableMetaData) obj;
                    Namespace ns1 = (Namespace) tbl.getParent();
                    assertEquals(ns.getName(), ns1.getName());
                }
                else if (obj instanceof SequenceMetadata)
                {
                    SequenceMetadata seq = (SequenceMetadata) obj;
                    Namespace ns1 = (Namespace) seq.getParent();
                    assertEquals(ns.getName(), ns1.getName());
                }
                else if (obj instanceof ViewMetaData)
                {
                    ViewMetaData view = (ViewMetaData) obj;
                    Namespace ns1 = (Namespace) view.getParent();
                    assertEquals(ns.getName(), ns1.getName());
                }
                else if (obj instanceof ColumnMetaData)
                {
                    ColumnMetaData clm = (ColumnMetaData) obj;
                    TableMetaData tbl = (TableMetaData) clm.getParent();
                    assertEquals("NS1", tbl.getName());
                }
            }
            assertEquals(10, map.size());
        }
        catch (DatabaseOperationException e)
        {
            
            e.printStackTrace();
        }
    }

    @Test
    public void testAutoSuggestForPrefix5()
    {
        LinkedHashMap<String, ServerObject> map = core.getContextProposals(
                "\"NS1\".\"TBL\"", "select * from \"NS1\".\"TBL\"");
        for (ServerObject obj : map.values())
        {
            assertTrue(obj.getName().startsWith("TBL")
                    || obj.getName().startsWith("tbl"));
        }
        assertEquals(4, map.size());
    }

    @Test
    public void testAutoSuggestForPrefix6()
    {
        LinkedHashMap<String, ServerObject> map = core.getContextProposals(
                "\"NS1\".\"TBL1\"", "select * from \"NS1\".\"TBL1\"");
        for (ServerObject obj : map.values())
        {
            assertTrue(obj.getName().startsWith("TBL1")
                    || obj.getName().startsWith("tbl1"));
        }
        assertEquals(2, map.size());
    }

    @Test
    public void testAutoSuggestForPrefix7()
    {
        LinkedHashMap<String, ServerObject> map = core.getContextProposals(
                "\"NS1\".\"TBL1\".", "select * from \"NS1\".\"TBL1\".");
        for (ServerObject obj : map.values())
        {
            if (obj instanceof ColumnMetaData)
            {
                TableMetaData tbl = (TableMetaData) obj.getParent();
                Namespace ns = (Namespace) tbl.getParent();
                assertTrue(tbl.getName().equals("TBL1"));
                assertTrue(ns.getName().equals("NS1"));
            }
        }
        assertEquals(1, map.size());
    }

    @Test
    public void testAutoSuggestForPrefix8()
    {
        String[] name = {"Yiew1"};
        LinkedHashMap<String, ServerObject> map = data
                .findExactMatchingObjects(name);

        for (ServerObject obj : map.values())
        {
            if (obj instanceof ViewMetaData)
            {
                Namespace ns = (Namespace) obj.getParent();
                assertTrue(ns.getName().equals("NS1"));
            }
        }
        assertEquals(1, map.size());
    }
    
    @Test
    public void testAutoSuggestForNull()
    {
        if (core != null)
        {
            core.autoSuggectForNullPrefix();
            core.getCurrentPrefix();
            core.getReplaceLength();
        }
        assertFalse(core.isAnyNonLoadedObject());
    }

    @Test
    public void testTableTableNameSpace() {
        try {
            Namespace ns = database.getNameSpaceById(1);
            TableMetaData tableMetaData = new TableMetaData(1, "t1", ns, null);
            ns.addTableToGroup(tableMetaData);
            ns.addTableToSearchPool(tableMetaData);


            String query ="select spcname from pg_class as class "
                            + "left join pg_tablespace as tablespace"
                            + " on class.reltablespace=tablespace.oid where class.oid=1";

            String stardandTsName = "ts_1";
            MockResultSet queryRS = preparedstatementHandler.createResultSet();
            queryRS.addColumn("spcname");
            queryRS.addRow(new Object[] {stardandTsName});
            preparedstatementHandler.prepareResultSet(query, queryRS);
            String tsName = tableMetaData.getTablespaceForTable(null);
            assertEquals(tsName, stardandTsName);
        } catch (MPPDBIDEException e) {
            fail("can\'t run here");
        }
    }

    @Test
    public void testTableTableNameSpaceQueryDefault() {
        try {
            Namespace ns = database.getNameSpaceById(1);
            TableMetaData tableMetaData = new TableMetaData(1, "t1", ns, null);
            ns.addTableToGroup(tableMetaData);
            ns.addTableToSearchPool(tableMetaData);


            String query ="select spcname from pg_class as class "
                            + "left join pg_tablespace as tablespace"
                            + " on class.reltablespace=tablespace.oid where class.oid=1";

            MockResultSet queryRS = preparedstatementHandler.createResultSet();
            queryRS.addColumn("spcname");
            preparedstatementHandler.prepareResultSet(query, queryRS);
            String tsName = tableMetaData.getTablespaceForTable(null);
            assertEquals(tsName, "");
        } catch (MPPDBIDEException e) {
            fail("can\'t run here");
        }
    }
    
    @Test
    public void testTableTableNameSpaceWithConnection() {
        try {
            Namespace ns = database.getNameSpaceById(1);
            TableMetaData tableMetaData = new TableMetaData(1, "t1", ns, null);
            ns.addTableToGroup(tableMetaData);
            ns.addTableToSearchPool(tableMetaData);


            String query ="select spcname from pg_class as class "
                            + "left join pg_tablespace as tablespace"
                            + " on class.reltablespace=tablespace.oid where class.oid=1";

            String stardandTsName = "ts_1";
            MockResultSet queryRS = preparedstatementHandler.createResultSet();
            queryRS.addColumn("spcname");
            queryRS.addRow(new Object[] {stardandTsName});
            preparedstatementHandler.prepareResultSet(query, queryRS);
            DBConnection connection = database.getConnectionManager().getFreeConnection();
            String tsName = tableMetaData.getTablespaceForTable(connection);
            assertEquals(tsName, stardandTsName);
        } catch (MPPDBIDEException e) {
            fail("can\'t run here");
        }
    }
    @Test
    public void testAutoSuggestForPrefixSpecialCharacter()
    {
        LinkedHashMap<String, ServerObject> map = core.getContextProposals(
                "\"NS1\".\"T}BL1\".", "select * from \"NS1\".\"T}BL1\".");
        assertEquals(0, map.size());
    }

    @Test
    public void testAutoSuggestForNonLoaded()
    {
        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
        CommonLLTUtils.fetchAllSynonyms(preparedstatementHandler);
        
        String getShallowDebuggableQRYOnDemand = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang WHERE lng.lanname in ('plpgsql','sql','c') and has_function_privilege(pr.oid, 'EXECUTE') and pr.pronamespace= 1 ORDER BY objname";
        MockResultSet shallowDbgObjOnDmd = preparedstatementHandler
                .createResultSet();
        shallowDbgObjOnDmd.addColumn("oid");
        shallowDbgObjOnDmd.addColumn("objname");
        shallowDbgObjOnDmd.addColumn("namespace");
        shallowDbgObjOnDmd.addColumn("ret");
        shallowDbgObjOnDmd.addColumn("alltype");
        shallowDbgObjOnDmd.addColumn("nargs");
        shallowDbgObjOnDmd.addColumn("argtype");
        shallowDbgObjOnDmd.addColumn("argname");
        shallowDbgObjOnDmd.addColumn("argmod");
        shallowDbgObjOnDmd.addColumn("retset");
        shallowDbgObjOnDmd.addColumn("lang");
        shallowDbgObjOnDmd.addRow(new Object[] {1, "auto1", 1, 23, null, 1,
                null, null, null, 'f', "plpgsql"});
        preparedstatementHandler.prepareResultSet(
                getShallowDebuggableQRYOnDemand, shallowDbgObjOnDmd);

        String refreshAllMetadataInNamespaceOnDemandQRY = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relkind in ('r','v','f') and tbl.parttype in ('n', 'p', 'v') and tbl.relnamespace = 1";
        MockResultSet refereshInNmspcOnDmd = preparedstatementHandler
                .createResultSet();
        refereshInNmspcOnDmd.addColumn("oid");
        refereshInNmspcOnDmd.addColumn("relname");
        refereshInNmspcOnDmd.addColumn("relnamespace");
        refereshInNmspcOnDmd.addColumn("relkind");
        refereshInNmspcOnDmd.addColumn("parttype");
        refereshInNmspcOnDmd.addColumn("foptions");
        refereshInNmspcOnDmd
                .addRow(new Object[] {1, "pg_type", 1, 'r', 'n', null});
        refereshInNmspcOnDmd
                .addRow(new Object[] {1, "pg_type", 1, 'v', 'n', null});
        preparedstatementHandler.prepareResultSet(
                refreshAllMetadataInNamespaceOnDemandQRY, refereshInNmspcOnDmd);
        try
        {
            database.getNameSpaceById(1);

            LinkedHashMap<String, ServerObject> map = core.getContextProposals(
                    "\"pg_catalog\".", "select * from \"pg_catalog\".");
            assertEquals(0, map.size());
            LinkedHashMap<String, ServerObject> mapLoaded = core
                    .findNonLoadedDatabaseObjectsOnDemand(
                            database.getConnectionManager().getFreeConnection());
            assertEquals(6, mapLoaded.size());
        }
        catch (DatabaseOperationException e)
        {
            fail("Not Excepted to come here");
        }
        catch (MPPDBIDEException e)
        {
            
            fail("Not Excepted to come here");
        }
    }

    @Test
    public void testAutoSuggestForNonLoadedChild()
    {

        CommonLLTUtils.createTableRS(preparedstatementHandler);
        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

        String getShallowDebuggableQRYOnDemand = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, pr.proretset retset, lng.lanname lang FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang WHERE lng.lanname in ('plpgsql','sql')  and has_function_privilege(pr.oid, 'EXECUTE') and pr.pronamespace= 1 ORDER BY objname";
        MockResultSet shallowDbgObjOnDmd = preparedstatementHandler
                .createResultSet();
        shallowDbgObjOnDmd.addColumn("oid");
        shallowDbgObjOnDmd.addColumn("objname");
        shallowDbgObjOnDmd.addColumn("namespace");
        shallowDbgObjOnDmd.addColumn("ret");
        shallowDbgObjOnDmd.addColumn("alltype");
        shallowDbgObjOnDmd.addColumn("nargs");
        shallowDbgObjOnDmd.addColumn("argtype");
        shallowDbgObjOnDmd.addColumn("argname");
        shallowDbgObjOnDmd.addColumn("argmod");
        shallowDbgObjOnDmd.addColumn("retset");
        shallowDbgObjOnDmd.addColumn("lang");
        shallowDbgObjOnDmd.addRow(new Object[] {1, "auto1", 1, 23, null, 1,
                null, null, null, 'f', "plpgsql"});
        preparedstatementHandler.prepareResultSet(
                getShallowDebuggableQRYOnDemand, shallowDbgObjOnDmd);

        String refreshAllMetadataInNamespaceOnDemandQRY = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions from pg_class tbl left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) where tbl.relkind in ('r','v','f') and tbl.parttype in ('n', 'p', 'v') and tbl.relnamespace = 1";
        MockResultSet refereshInNmspcOnDmd = preparedstatementHandler
                .createResultSet();
        refereshInNmspcOnDmd.addColumn("oid");
        refereshInNmspcOnDmd.addColumn("relname");
        refereshInNmspcOnDmd.addColumn("relnamespace");
        refereshInNmspcOnDmd.addColumn("relkind");
        refereshInNmspcOnDmd.addColumn("parttype");
        refereshInNmspcOnDmd.addColumn("foptions");
        refereshInNmspcOnDmd
                .addRow(new Object[] {1, "pg_type", 1, 'r', 'n', null});
        refereshInNmspcOnDmd
                .addRow(new Object[] {1, "pg_type", 1, 'v', 'n', null});
        preparedstatementHandler.prepareResultSet(
                refreshAllMetadataInNamespaceOnDemandQRY, refereshInNmspcOnDmd);
        try
        {
            LinkedHashMap<String, ServerObject> map = core.getContextProposals(
                    "\"pg_am\".", "select * from \"pg_am\".");
            assertEquals(0, map.size());
            LinkedHashMap<String, ServerObject> mapLoaded = core
                    .findNonLoadedDatabaseObjectsOnDemand(
                            database.getConnectionManager().getFreeConnection());
            assertEquals(0, mapLoaded.size());
        }
        catch (DatabaseOperationException e)
        {
            fail("Not Excepted to come here");
        }
        catch (MPPDBIDEException e)
        {
            
            fail("Not Excepted to come here");
        }

    }

    @Test
    public void testAutoSuggestForDisconnectedDB()
    {
        DBConnProfCache.getInstance().destroyConnection(database);
        LinkedHashMap<String, ServerObject> map = core
                .getContextProposals("\"pg_am\".", "select * from \"pg_am\". ");
        assertEquals(0, map.size());
    }
    
    @Test
    public void testAutoSuggestForPrefixPartitionTable()
    {
        LinkedHashMap<String, ServerObject> map = core.getContextProposals("notable",
                "select * from Namespc");
        for (ServerObject obj : map.values())
        {
            if (obj instanceof PartitionTable)
            {
                assertEquals("notablename", obj.getName());
            }
        }
        assertEquals(1, map.size());

    }
    
    @Test
    public void testAutoSuggestForNullPrefixPartitionTable()
    {
        LinkedHashMap<String, ServerObject> map = core.getContextProposals(null,
                "select * from Namespc");
        assertTrue(!map.isEmpty());

    }
    
    @Test
    public void testAutoSuggestFindString()
    {
        String prefix=core.findString("Select * from tab", DatabaseUtils.getCharacterList(database));
        assertEquals("tab", prefix);
      
    }
    
    @Test
    public void testAutoSuggestFindString1()
    {
        String prefix=core.findString("Select * from tab.", DatabaseUtils.getCharacterList(database));
        assertEquals("tab.", prefix);
        
        prefix=core.findString("Select * from \"tab\"", DatabaseUtils.getCharacterList(database));
        assertEquals("\"tab\"", prefix);
        
        prefix=core.findString("\"tab\"", DatabaseUtils.getCharacterList(database));
        assertEquals("\"tab\"", prefix);
        
        prefix=core.findString("\"\\\"tab\"", DatabaseUtils.getCharacterList(database));
        assertEquals("\"\\\"tab\"", prefix);
        
        prefix=core.findString("Select * from \n", DatabaseUtils.getCharacterList(database));
        assertEquals("", prefix);
        
        prefix=core.findString("Insert into tab(", DatabaseUtils.getCharacterList(database));
        assertEquals("tab", prefix);
        
        prefix=core.findString("Insert into tab(c1,", DatabaseUtils.getCharacterList(database));
        assertEquals("tab", prefix);
        
        prefix=core.findString("Select into tab(c1,", DatabaseUtils.getCharacterList(database));
        assertEquals("", prefix);
        
        prefix=core.findString("Select into tab(c1,c2) ,", DatabaseUtils.getCharacterList(database));
        assertEquals("", prefix);
        
        prefix=core.findString("Insert into tab(c", DatabaseUtils.getCharacterList(database));
        assertEquals("tab.c", prefix);
        
        prefix=core.findString("Insert into tab(c1,c2", DatabaseUtils.getCharacterList(database));
        assertEquals("tab.c2", prefix);

        String st = "qwertyuiopasdfghjklzxcvbnmqwreeewurqwyetrytqwerqwerqyweryqtwerytqwertyqwerqytwetr"
                + "hsdghfgsdhfgshdfgshdddfdgjfhgsdjfhghsdgfhsgdddfsjdgfhkajsdfgjasdfgjhhhfgsjdgfalsjdgfjhasgdjfgsjdgksjjjjjjjjfgkajsdgfakjsdfgajsdgfhjdgfasjldfgajksdgfhaaafghagsdfjaaaafgjagsfjghjasdfgaafgjaaafgaagau7234587134gsdgfcvgjhsdgfjhfghdcvnbzxxxxxxvfguyyyyyrtyudsfvgajksdgsfhgwe73472345hjfggghsdfhadfakjsdfgajsgdfgasdfakjsdgfmxzc,bxxxxvhjjjfgeiurt2873445689134589ISDHFUASKGDGAHSGDGHASDJKGFAKDFGGJHHFGAKDFGADUFQ9WOEUIWEOYEQORYGIVBBBBasdjhgakjdfgakjdfgadjkfgjdfggsdkfjg";
        prefix = core.findString(st, DatabaseUtils.getCharacterList(database));
        assertTrue(!prefix.isEmpty());
        
    }
    
    @Test
    public void testAutoSuggestFindAllChildObjectsForeignTab() {
        String[] name = {"Ftable1"};
        LinkedHashMap<String, ServerObject> map = data.findExactMatchingObjects(name);
        assertEquals(1, map.size());

        LinkedHashMap<String, ServerObject> map1 = core.getContextProposals("\"Ftable1\".FCo",
                "select * from Ftable1.FCo");
        assertEquals(1, map1.size());
        
        LinkedHashMap<String, ServerObject> map3 = core.getContextProposals("ns1.\"Ftable1\".",
                "select * from Ftable1.FCo");
        assertEquals(1, map3.size());
        
        LinkedHashMap<String, ServerObject> map4 = core.getContextProposals("\"Ftable1\".\"FCol10\".",
                "select * from Ftable1.FCo");
        assertEquals(0, map4.size());
        
    }
    
    @Test
    public void testAutoSuggestFindAllChildObjectsForPartTable() {
        String[] name = {"partTable1"};
        LinkedHashMap<String, ServerObject> map = data.findExactMatchingObjects(name);
        assertEquals(1, map.size());

        LinkedHashMap<String, ServerObject> map1 = core.getContextProposals("\"partTable1\".PCo",
                "select * from partTable1.PCo");
        assertEquals(1, map1.size());
        
        LinkedHashMap<String, ServerObject> map3 = core.getContextProposals("\"NS1\".\"partTable1\".",
                "select * from Ftable1.FCo");
        assertEquals(1, map3.size());
        
        LinkedHashMap<String, ServerObject> map4 = core.getContextProposals("\"partTable1\".\"PCol11\".",
                "select * from partTable1.PCo");
        assertEquals(0, map4.size());
    }
    
    @Test
    public void testAutoSuggestFindAllChildObjectsForView() {
        String[] name = {"Yiew1"};
        LinkedHashMap<String, ServerObject> map = data.findExactMatchingObjects(name);
        assertEquals(1, map.size());

        LinkedHashMap<String, ServerObject> map1 = core.getContextProposals("\"Yiew1\".viewc",
                "select * from partTable1.PCo");
        assertEquals(1, map1.size());
        
        LinkedHashMap<String, ServerObject> map3 = core.getContextProposals("\"NS1\".\"Yiew1\".",
                "select * from partTable1.PCo");
        assertEquals(1, map3.size());
        
        LinkedHashMap<String, ServerObject> map4 = core.getContextProposals("\"Yiew1\".viewclm.",
                "select * from partTable1.PCo");
        assertEquals(0, map4.size());
    }
    
    
    
    @Test
    public void testAutoSuggestFindAllChildObjectsForSeq() {
        String[] name = {"seq"};
        LinkedHashMap<String, ServerObject> map = data.findExactMatchingObjects(name);
        assertEquals(0, map.size());

    }
    
    @Test
    public void testAutoSuggestFindAllChildObjectsForDebugObj() {
        String[] name = {"add"};
        LinkedHashMap<String, ServerObject> map = data.findExactMatchingObjects(name);
        assertEquals(0, map.size());

    }
    
    @Test
    public void testInsertAutoSuggestFindColumnsForTable() {
       String prefix=core.findString("Insert into tbl1(", DatabaseUtils.getCharacterList(database));
       LinkedHashMap<String, ServerObject> map1 = core.getContextProposals(prefix,
               "select * from partTable1.PCo");
        assertTrue(map1.size() > 0);
        
    }
    
    @Test
    public void testInsertAutoSuggestFindColumnsForView() {
       String prefix=core.findString("Insert into \"Yiew1\"(", DatabaseUtils.getCharacterList(database));
       LinkedHashMap<String, ServerObject> map1 = core.getContextProposals(prefix,
               "select * from partTable1.PCo");
       assertEquals(1, map1.size());
       String[] str=data.getContentAssistUtil().getPrefixHyperLink("ns1.tbl");
       assertEquals(2,str.length);
       str=data.getContentAssistUtil().getPrefixHyperLink("\"Ns1\".tbl");
       assertEquals(2,str.length);
       str=data.getContentAssistUtil().getPrefixHyperLink("\"N\"\"s1\".tbl");
       assertEquals(2,str.length);
        
    }
    
    @Test
    public void testAutoSuggestForDbNull() {
        String prefix=core1.findString("select * from tab", DatabaseUtils.getCharacterList(null));
        assertEquals("tab",prefix);
        
        prefix=core1.findString("", DatabaseUtils.getCharacterList(null));
        assertNull(prefix);
        
        prefix=core1.findString("insert into tab(", DatabaseUtils.getCharacterList(null));
        assertEquals("tab(",prefix);
        
        ContentAssistProcesserData contentData = new ContentAssistProcesserData(null);
        String[] strArray = contentData.getContentAssistUtil().getPrefixHyperLink("sel");
        assertTrue(strArray!=null);
        
        LinkedHashMap<String, ServerObject> map1 = core1.getContextProposals(prefix,
                "select * from partTable1.PCo");
        int noOfObjects=0;
        for (ServerObject obj : map1.values())
        {
            if (obj instanceof KeywordObject)
            {
                assertEquals("TABLE", obj.getName());
                noOfObjects++;
            }
        }
        assertEquals(map1.size(),noOfObjects);
        
        LinkedHashMap<String, ServerObject> map2 = core1.getContextProposals("TAB.",
                "select * from partTable1.PCo");
       
        assertEquals(0,map2.size());
       
    }
    
    @Test
    public void testAutoSuggestForNonLoadedTable() {
        
        try {

            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColumnMetaDataRS(preparedstatementHandler);
            Namespace ns = database.getNameSpaceById(1);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", ns, "tablespace");
            ns.addTableToGroup(tablemetaData);
            ns.addTableToSearchPool(tablemetaData);

            core.getContextProposals("\"Table1\".", "select * from partTable1.PCo");
            LinkedHashMap<String, ServerObject> map2 = core
                    .findNonLoadedDatabaseObjectsOnDemand(database.getConnectionManager().getFreeConnection());
            assertEquals(1, map2.size());
           
        } catch (MPPDBIDEException e) {
            fail("Not expected to come here");
        }
        
    }
    
    @Test
    public void testAutoSuggestForNonLoadedForeignTable() {

        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
        CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
        CommonLLTUtils.fetchColumnMetaDataRS(preparedstatementHandler);
        try {
            Namespace ns = database.getNameSpaceById(1);

            ForeignTable ForeignTbl = new ForeignTable(ns, OBJECTTYPE.FOREIGN_TABLE_GDS);
            ForeignTbl.setName("FTable1");
            ForeignTbl.setOid(1);
            ns.addForeignTableToGroup(ForeignTbl);
            ns.addTableToSearchPool(ForeignTbl);

            core.getContextProposals("\"FTable1\".", "select * from partTable1.PCo");
            LinkedHashMap<String, ServerObject> map2 = core
                    .findNonLoadedDatabaseObjectsOnDemand(database.getConnectionManager().getFreeConnection());
            assertEquals(1, map2.size());
        } catch (MPPDBIDEException e) {
            fail("Not expected to come here");
        }

    }
    
    @Test
    public void testAutoSuggestForNonLoadedPartTable() {
        
        try {
        Namespace ns = database.getNameSpaceById(1);
        PartitionTable ptab = new PartitionTable(ns);
        ptab.setName("partTable");
        ptab.setOid(1);
        ns.addTableToGroup(ptab);
        ns.addTableToSearchPool(ptab);
        
        MockResultSet getcolumninfoRSptble_1 = preparedstatementHandler
                .createResultSet();
        getcolumninfoRSptble_1.addColumn("tableid");
        getcolumninfoRSptble_1.addColumn("namespaceid");
        getcolumninfoRSptble_1.addColumn("columnidx");
        getcolumninfoRSptble_1.addColumn("name");
        getcolumninfoRSptble_1.addColumn("datatypeoid");
        getcolumninfoRSptble_1.addColumn("dtns");
        getcolumninfoRSptble_1.addColumn("length");
        getcolumninfoRSptble_1.addColumn("precision");
        getcolumninfoRSptble_1.addColumn("dimentions");
        getcolumninfoRSptble_1.addColumn("notnull");
        getcolumninfoRSptble_1.addColumn("isdefaultvalueavailable");
        getcolumninfoRSptble_1.addColumn("default_value");
        getcolumninfoRSptble_1.addColumn("attDefStr");
        getcolumninfoRSptble_1.addColumn("displayColumns");
        getcolumninfoRSptble_1
                .addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200, 0, 0,
                        false, true, "Default value", "attrString", "bigint"});
        preparedstatementHandler.prepareResultSet(
                "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name,  pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,  c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod  as precision, c.attndims as dimentions,  c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as  default_value, d.adbin as attDefStr  from pg_class t  left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v'))  left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum)  left join pg_type typ on (c.atttypid = typ.oid)  where c.attisdropped = 'f' and c.attnum > 0 and t.oid = 2 and t.relkind <> 'i'  order by c.attnum;",
                getcolumninfoRSptble_1);
        
        preparedstatementHandler.prepareResultSet(
                "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name,  pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,  c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod  as precision, c.attndims as dimentions,  c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as  default_value, d.adbin as attDefStr  from pg_class t  left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v'))  left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum)  left join pg_type typ on (c.atttypid = typ.oid)  where c.attisdropped = 'f' and c.attnum > 0 and t.oid = 1 and t.relkind <> 'i'  order by c.attnum;",
                getcolumninfoRSptble_1);
        
        MockResultSet partitionConstraintRS = preparedstatementHandler
                .createResultSet();
        partitionConstraintRS.addColumn("constraintid");
        partitionConstraintRS.addColumn("tableid");
        partitionConstraintRS.addColumn("namespaceid");
        partitionConstraintRS.addColumn("constraintname");
        partitionConstraintRS.addColumn("constrainttype");
        partitionConstraintRS.addColumn("deferrable");
        partitionConstraintRS.addColumn("deferred");
        partitionConstraintRS.addColumn("validate");
        partitionConstraintRS.addColumn("indexid");
        partitionConstraintRS.addColumn("fkeytableId");
        partitionConstraintRS.addColumn("updatetype");
        partitionConstraintRS.addColumn("deletetype");
        partitionConstraintRS.addColumn("matchtype");
        partitionConstraintRS.addColumn("expr");
        partitionConstraintRS.addColumn("columnlist");
        partitionConstraintRS.addColumn("fkeycolumnlist");
        partitionConstraintRS.addColumn("const_def");
        partitionConstraintRS.addRow(new Object[] {1, 1, 1, "ConstraintName",
                "ConstraintType", false, false, false, 1, 1, "", "", "", "",
                "1", "1", ""});
        preparedstatementHandler.prepareResultSet(
               "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.conrelid=2 and cl.parttype in ('p','v') and c.conrelid <> 0;",
                partitionConstraintRS);
        preparedstatementHandler.prepareResultSet("SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.conrelid=1 and cl.parttype in ('p','v') and c.conrelid <> 0;", partitionConstraintRS);

        
        MockResultSet indexRS = preparedstatementHandler
                .createResultSet();
        indexRS.addColumn("oid");
        indexRS.addColumn("tableId");
        indexRS.addColumn("indexname");
        indexRS.addColumn("namespaceid");
        indexRS.addColumn("accessmethodid");
        indexRS.addColumn("isunique");
        indexRS.addColumn("isprimary");
        indexRS.addColumn("isexclusion");
        indexRS.addColumn("isimmediate");
        indexRS.addColumn("isclustered");
        indexRS.addColumn("checkmin");
        indexRS.addColumn("isready");
        indexRS.addColumn("cols");
        indexRS.addColumn("reloptions");
        indexRS.addColumn("indexdef");
        indexRS.addColumn("tablespace");
        indexRS.addRow(new Object[] {1, 1, "IndexName", 1, 1, true, false,
                false, false, false, false, false, "1", "", "", ""});
        preparedstatementHandler.prepareResultSet(
                "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef, def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind in ('r', 'f', 'I') and ci.parttype in ('p','n') and ci.relnamespace = 2 and tableid= 2;", indexRS);
        
        preparedstatementHandler.prepareResultSet(
                "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef, def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind in ('r', 'f', 'I') and ci.parttype in ('p','n') and ci.relnamespace = 1 and tableid= 1;", indexRS);        MockResultSet getPartitionsRS_1 = preparedstatementHandler
                .createResultSet();
        getPartitionsRS_1.addColumn("partition_id");
        getPartitionsRS_1.addColumn("partition_name");
        getPartitionsRS_1.addColumn("partition_type");
        getPartitionsRS_1.addColumn("table_id");

        getPartitionsRS_1.addRow(new Object[] {1, "part_1", "r", 1});
        preparedstatementHandler.prepareResultSet("select p.oid AS partition_id , p.relname AS partition_name, p.partstrategy as partition_type, p.parentid AS table_id  from pg_class c, pg_partition p  where c.oid =  2 and c.parttype = 'p'  and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;",
                getPartitionsRS_1);
        preparedstatementHandler.prepareResultSet("select p.oid AS partition_id , p.relname AS partition_name, p.partstrategy as partition_type, p.parentid AS table_id  from pg_class c, pg_partition p  where c.oid =  1 and c.parttype = 'p'  and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;",
                getPartitionsRS_1);
        core.getContextProposals("\"partTable\".", "select * from partTable1.PCo");
        LinkedHashMap<String, ServerObject> map2;
 
            map2 = core
                    .findNonLoadedDatabaseObjectsOnDemand(database.getConnectionManager().getFreeConnection());
            assertEquals(2, map2.size());
        } catch (MPPDBIDEException e) {
            fail("Not expected to come here");
        }
      
        
    }
    
    
    
    @Test
    public void testAutoSuggestForNonLoadedPartTable1() {
        
        try {
        Namespace ns = database.getNameSpaceById(1);
        PartitionTable ptab = new PartitionTable(ns);
        ptab.setName("partTable");
        ptab.setOid(1);
        ns.addTableToGroup(ptab);
        ns.addTableToSearchPool(ptab);
        
        MockResultSet getcolumninfoRSptble_1 = preparedstatementHandler
                .createResultSet();
        getcolumninfoRSptble_1.addColumn("tableid");
        getcolumninfoRSptble_1.addColumn("namespaceid");
        getcolumninfoRSptble_1.addColumn("columnidx");
        getcolumninfoRSptble_1.addColumn("name");
        getcolumninfoRSptble_1.addColumn("datatypeoid");
        getcolumninfoRSptble_1.addColumn("dtns");
        getcolumninfoRSptble_1.addColumn("length");
        getcolumninfoRSptble_1.addColumn("precision");
        getcolumninfoRSptble_1.addColumn("dimentions");
        getcolumninfoRSptble_1.addColumn("notnull");
        getcolumninfoRSptble_1.addColumn("isdefaultvalueavailable");
        getcolumninfoRSptble_1.addColumn("default_value");
        getcolumninfoRSptble_1.addColumn("attDefStr");
        getcolumninfoRSptble_1.addColumn("displayColumns");
        getcolumninfoRSptble_1
                .addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200, 0, 0,
                        false, true, "Default value", "attrString", "bigint"});
        preparedstatementHandler.prepareResultSet(
                "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name,  pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,  c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod  as precision, c.attndims as dimentions,  c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as  default_value, d.adbin as attDefStr  from pg_class t  left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v'))  left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum)  left join pg_type typ on (c.atttypid = typ.oid)  where c.attisdropped = 'f' and c.attnum > 0 and t.oid = 2 and t.relkind <> 'i'  order by c.attnum;",
                getcolumninfoRSptble_1);
        
        preparedstatementHandler.prepareResultSet(
                "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name,  pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,  c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod  as precision, c.attndims as dimentions,  c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as  default_value, d.adbin as attDefStr  from pg_class t  left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v'))  left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum)  left join pg_type typ on (c.atttypid = typ.oid)  where c.attisdropped = 'f' and c.attnum > 0 and t.oid = 1 and t.relkind <> 'i'  order by c.attnum;",
                getcolumninfoRSptble_1);
        
        MockResultSet partitionConstraintRS = preparedstatementHandler
                .createResultSet();
        partitionConstraintRS.addColumn("constraintid");
        partitionConstraintRS.addColumn("tableid");
        partitionConstraintRS.addColumn("namespaceid");
        partitionConstraintRS.addColumn("constraintname");
        partitionConstraintRS.addColumn("constrainttype");
        partitionConstraintRS.addColumn("deferrable");
        partitionConstraintRS.addColumn("deferred");
        partitionConstraintRS.addColumn("validate");
        partitionConstraintRS.addColumn("indexid");
        partitionConstraintRS.addColumn("fkeytableId");
        partitionConstraintRS.addColumn("updatetype");
        partitionConstraintRS.addColumn("deletetype");
        partitionConstraintRS.addColumn("matchtype");
        partitionConstraintRS.addColumn("expr");
        partitionConstraintRS.addColumn("columnlist");
        partitionConstraintRS.addColumn("fkeycolumnlist");
        partitionConstraintRS.addColumn("const_def");
        partitionConstraintRS.addRow(new Object[] {1, 1, 1, "ConstraintName",
                "ConstraintType", false, false, false, 1, 1, "", "", "", "",
                "1", "1", ""});
        preparedstatementHandler.prepareResultSet(
               "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.conrelid=2 and cl.parttype in ('p','v') and c.conrelid <> 0;",
                partitionConstraintRS);
        preparedstatementHandler.prepareResultSet("SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.conrelid=1 and cl.parttype in ('p','v') and c.conrelid <> 0;", partitionConstraintRS);

        
        MockResultSet indexRS = preparedstatementHandler
                .createResultSet();
        indexRS.addColumn("oid");
        indexRS.addColumn("tableId");
        indexRS.addColumn("indexname");
        indexRS.addColumn("namespaceid");
        indexRS.addColumn("accessmethodid");
        indexRS.addColumn("isunique");
        indexRS.addColumn("isprimary");
        indexRS.addColumn("isexclusion");
        indexRS.addColumn("isimmediate");
        indexRS.addColumn("isclustered");
        indexRS.addColumn("checkmin");
        indexRS.addColumn("isready");
        indexRS.addColumn("cols");
        indexRS.addColumn("reloptions");
        indexRS.addColumn("indexdef");
        indexRS.addColumn("tablespace");
        indexRS.addRow(new Object[] {1, 1, "IndexName", 1, 1, true, false,
                false, false, false, false, false, "1", "", "", ""});
        preparedstatementHandler.prepareResultSet(
                "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef, def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind in ('r', 'f') and ci.parttype in ('p','v') and ci.oid = 2;", indexRS);
        
        preparedstatementHandler.prepareResultSet(
                "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef, def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind in ('r', 'f') and ci.parttype in ('p','v') and ci.oid = 1;", indexRS);
        MockResultSet getPartitionsRS_1 = preparedstatementHandler
                .createResultSet();
        getPartitionsRS_1.addColumn("partition_id");
        getPartitionsRS_1.addColumn("partition_name");
        getPartitionsRS_1.addColumn("partition_type");
        getPartitionsRS_1.addColumn("table_id");

        getPartitionsRS_1.addRow(new Object[] {1, "part_1", "r", 1});
        preparedstatementHandler.prepareResultSet("select p.oid AS partition_id , p.relname AS partition_name, p.partstrategy as partition_type, p.parentid AS table_id  from pg_class c, pg_partition p  where c.oid =  2 and c.parttype = 'p'  and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;",
                getPartitionsRS_1);
        preparedstatementHandler.prepareResultSet("select p.oid AS partition_id , p.relname AS partition_name, p.partstrategy as partition_type, p.parentid AS table_id  from pg_class c, pg_partition p  where c.oid =  1 and c.parttype = 'p'  and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;",
                getPartitionsRS_1);
        core.getContextProposals("pg_catalog.\"partTable\".\"PCo\"", "select * from pg_catalog.partTable.PCo");
        LinkedHashMap<String, ServerObject> map2;
 
            map2 = core
                    .findNonLoadedDatabaseObjectsOnDemand(database.getConnectionManager().getFreeConnection());
            ContentAssistKeywords.getInstance().clearOLAPKeywords(database.getServer().getAllDatabases());
            ContentAssistKeywords.getInstance().clear();
        } catch (MPPDBIDEException e) {
            fail("Not expected to come here");
        }
    }
    
    
    @Test
    public void testAutoSuggestForNonLoadedPartTable11() {
        
        try {
        Namespace ns = database.getNameSpaceById(1);
        PartitionTable ptab = new PartitionTable(ns);
        ptab.setName("partTable");
        ptab.setOid(1);
        ns.addTableToGroup(ptab);
        ns.addTableToSearchPool(ptab);
        
        MockResultSet getcolumninfoRSptble_1 = preparedstatementHandler
                .createResultSet();
        getcolumninfoRSptble_1.addColumn("tableid");
        getcolumninfoRSptble_1.addColumn("namespaceid");
        getcolumninfoRSptble_1.addColumn("columnidx");
        getcolumninfoRSptble_1.addColumn("name");
        getcolumninfoRSptble_1.addColumn("datatypeoid");
        getcolumninfoRSptble_1.addColumn("dtns");
        getcolumninfoRSptble_1.addColumn("length");
        getcolumninfoRSptble_1.addColumn("precision");
        getcolumninfoRSptble_1.addColumn("dimentions");
        getcolumninfoRSptble_1.addColumn("notnull");
        getcolumninfoRSptble_1.addColumn("isdefaultvalueavailable");
        getcolumninfoRSptble_1.addColumn("default_value");
        getcolumninfoRSptble_1.addColumn("attDefStr");
        getcolumninfoRSptble_1.addColumn("displayColumns");
        getcolumninfoRSptble_1
                .addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200, 0, 0,
                        false, true, "Default value", "attrString", "bigint"});
        preparedstatementHandler.prepareResultSet(
                "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name,  pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,  c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod  as precision, c.attndims as dimentions,  c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as  default_value, d.adbin as attDefStr  from pg_class t  left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v'))  left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum)  left join pg_type typ on (c.atttypid = typ.oid)  where c.attisdropped = 'f' and c.attnum > 0 and t.oid = 2 and t.relkind <> 'i'  order by c.attnum;",
                getcolumninfoRSptble_1);
        
        preparedstatementHandler.prepareResultSet(
                "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name,  pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,  c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod  as precision, c.attndims as dimentions,  c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as  default_value, d.adbin as attDefStr  from pg_class t  left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v'))  left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum)  left join pg_type typ on (c.atttypid = typ.oid)  where c.attisdropped = 'f' and c.attnum > 0 and t.oid = 1 and t.relkind <> 'i'  order by c.attnum;",
                getcolumninfoRSptble_1);
        
        MockResultSet partitionConstraintRS = preparedstatementHandler
                .createResultSet();
        partitionConstraintRS.addColumn("constraintid");
        partitionConstraintRS.addColumn("tableid");
        partitionConstraintRS.addColumn("namespaceid");
        partitionConstraintRS.addColumn("constraintname");
        partitionConstraintRS.addColumn("constrainttype");
        partitionConstraintRS.addColumn("deferrable");
        partitionConstraintRS.addColumn("deferred");
        partitionConstraintRS.addColumn("validate");
        partitionConstraintRS.addColumn("indexid");
        partitionConstraintRS.addColumn("fkeytableId");
        partitionConstraintRS.addColumn("updatetype");
        partitionConstraintRS.addColumn("deletetype");
        partitionConstraintRS.addColumn("matchtype");
        partitionConstraintRS.addColumn("expr");
        partitionConstraintRS.addColumn("columnlist");
        partitionConstraintRS.addColumn("fkeycolumnlist");
        partitionConstraintRS.addColumn("const_def");
        partitionConstraintRS.addRow(new Object[] {1, 1, 1, "ConstraintName",
                "ConstraintType", false, false, false, 1, 1, "", "", "", "",
                "1", "1", ""});
        preparedstatementHandler.prepareResultSet(
               "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.conrelid=2 and cl.parttype in ('p','v') and c.conrelid <> 0;",
                partitionConstraintRS);
        preparedstatementHandler.prepareResultSet("SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.conrelid=1 and cl.parttype in ('p','v') and c.conrelid <> 0;", partitionConstraintRS);

        
        MockResultSet indexRS = preparedstatementHandler
                .createResultSet();
        indexRS.addColumn("oid");
        indexRS.addColumn("tableId");
        indexRS.addColumn("indexname");
        indexRS.addColumn("namespaceid");
        indexRS.addColumn("accessmethodid");
        indexRS.addColumn("isunique");
        indexRS.addColumn("isprimary");
        indexRS.addColumn("isexclusion");
        indexRS.addColumn("isimmediate");
        indexRS.addColumn("isclustered");
        indexRS.addColumn("checkmin");
        indexRS.addColumn("isready");
        indexRS.addColumn("cols");
        indexRS.addColumn("reloptions");
        indexRS.addColumn("indexdef");
        indexRS.addColumn("tablespace");
        indexRS.addRow(new Object[] {1, 1, "IndexName", 1, 1, true, false,
                false, false, false, false, false, "1", "", "", ""});
        preparedstatementHandler.prepareResultSet(
                "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef, def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind in ('r', 'f') and ci.parttype in ('p','v') and ci.oid = 2;", indexRS);
        
        preparedstatementHandler.prepareResultSet(
                "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef, def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind in ('r', 'f') and ci.parttype in ('p','v') and ci.oid = 1;", indexRS);        MockResultSet getPartitionsRS_1 = preparedstatementHandler
                .createResultSet();
        getPartitionsRS_1.addColumn("partition_id");
        getPartitionsRS_1.addColumn("partition_name");
        getPartitionsRS_1.addColumn("table_id");

        getPartitionsRS_1.addRow(new Object[] {1, "part_1", 1});
        preparedstatementHandler.prepareResultSet("select p.oid AS partition_id , p.relname AS partition_name , p.parentid AS table_id  from pg_class c, pg_partition p  where c.oid =  2 and c.parttype = 'p'  and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;",
                getPartitionsRS_1);
        preparedstatementHandler.prepareResultSet("select p.oid AS partition_id , p.relname AS partition_name , p.parentid AS table_id  from pg_class c, pg_partition p  where c.oid =  1 and c.parttype = 'p'  and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;",
                getPartitionsRS_1);
        core.findString("insert into pg_catalog.\"partTable\"(PCo", DatabaseUtils.getCharacterList(database));
        core.getContextProposals("pg_catalog.\"partTable\".\"PCo\"", "select * from pg_catalog.partTable.PCo");
        LinkedHashMap<String, ServerObject> map2;
 
            map2 = core
                    .findNonLoadedDatabaseObjectsOnDemand(database.getConnectionManager().getFreeConnection());
        } catch (MPPDBIDEException e) {
            fail("Not expected to come here");
        }
    }
    
    @Test
    public void testAutoSuggestForNonLoadedView() {
        try {
        Namespace ns = database.getNameSpaceById(1);
        ViewMetaData view = new ViewMetaData(1, "Yiew1", ns,ns.getDatabase());
        ns.addView(view);
        ns.addViewInSearchPool(view);


        String query = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and v.oid = 1 order by v.oid, c.attnum";

        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("viewid");
        getdbsrs.addColumn("namespaceid");
        getdbsrs.addColumn("columnidx");
        getdbsrs.addColumn("name");
        getdbsrs.addColumn("datatypeoid");
        getdbsrs.addColumn("dtns");
        getdbsrs.addColumn("length");
        getdbsrs.addColumn("precision");
        getdbsrs.addColumn("dimentions");
        getdbsrs.addColumn("notnull");
        getdbsrs.addColumn("isdefaultvalueavailable");
        getdbsrs.addColumn("default_value");
        getdbsrs.addColumn("displayColumns");

        getdbsrs.addRow(
                new Object[] {2, 1, 1, "col1", 2, 1, 64, -1, 0, 'f', 'f', "",""});
        getdbsrs.addRow(
                new Object[] {2, 2, 2, "col2", 1, 1, 64, -1, 0, 'f', 'f', "",""});

        preparedstatementHandler.prepareResultSet(query, getdbsrs);
        
        LinkedHashMap<String, ServerObject> map2=core.getContextProposals("\"Yiew1\".", "select * from partTable1.PCo");
        LinkedHashMap<String, ServerObject> map1 = core
                    .findNonLoadedDatabaseObjectsOnDemand(database.getConnectionManager().getFreeConnection());
        assertEquals(1, map2.size());
        
        }catch(MPPDBIDEException e) {
            fail("Not expected to come here");
        }
        
    }
    
    @Test
    public void testAutoSuggestForNonLoadedView11() {
        try {
        Namespace ns = database.getNameSpaceById(1);
        ViewMetaData view = new ViewMetaData(1, "Yiew1", ns,ns.getDatabase());
        ns.addView(view);
        ns.addViewInSearchPool(view);


        String query = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and v.oid = 1 order by v.oid, c.attnum";

        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("viewid");
        getdbsrs.addColumn("namespaceid");
        getdbsrs.addColumn("columnidx");
        getdbsrs.addColumn("name");
        getdbsrs.addColumn("datatypeoid");
        getdbsrs.addColumn("dtns");
        getdbsrs.addColumn("length");
        getdbsrs.addColumn("precision");
        getdbsrs.addColumn("dimentions");
        getdbsrs.addColumn("notnull");
        getdbsrs.addColumn("isdefaultvalueavailable");
        getdbsrs.addColumn("default_value");
        getdbsrs.addColumn("displayColumns");

        getdbsrs.addRow(
                new Object[] {2, 1, 1, "col1", 2, 1, 64, -1, 0, 'f', 'f', "",""});
        getdbsrs.addRow(
                new Object[] {2, 2, 2, "col2", 1, 1, 64, -1, 0, 'f', 'f', "",""});

        preparedstatementHandler.prepareResultSet(query, getdbsrs);
        
        LinkedHashMap<String, ServerObject> map2=core.getContextProposals("\"Yiew1\".", "select * from partTable1.PCo");
        LinkedHashMap<String, ServerObject> map1 = core
                    .findNonLoadedDatabaseObjectsOnDemand(database.getConnectionManager().getFreeConnection());
        assertEquals(1, map2.size());
        
        }catch(MPPDBIDEException e) {
            fail("Not expected to come here");
        }
        
    }
    
    @Test
    public void test_viewViewData_01() {
        try {
            Namespace ns = database.getNameSpaceById(1);
            ViewMetaData vmd = new ViewMetaData(2, "anything", ns,ns.getDatabase());
            vmd.setNamespace(ns);
            ViewViewDataCore viewViewDataCore = new ViewViewDataCore();
            viewViewDataCore.init(vmd);
            assertNotNull(viewViewDataCore.getProgressBarLabel());
            assertNotNull(viewViewDataCore.getWindowDetails().getShortTitle());
            assertNotNull(viewViewDataCore.getWindowDetails().getTitle());
            assertNotNull(viewViewDataCore.getWindowDetails().getUniqueID());
            assertNotNull(viewViewDataCore.getQuery());
            assertNotNull(viewViewDataCore.getWindowTitle());
            assertTrue(viewViewDataCore.isTableDropped());
            assertEquals(vmd, viewViewDataCore.getServerObject());
            assertTrue(vmd.isDbConnected());

        } catch (OutOfMemoryError | MPPDBIDEException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_AutoRefreshForCreateTable() {

        String tableQuery = "select tbl.relname relname,tbl.parttype parttype,tbl.relnamespace relnamespace,tbl.oid oid,ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.relname = 'table1' and tbl.relnamespace= 6";
        MockResultSet createTableRS = preparedstatementHandler.createResultSet();
        createTableRS.addColumn("relname");
        createTableRS.addColumn("parttype");
        createTableRS.addColumn("relnamespace");
        createTableRS.addColumn("oid");
        createTableRS.addColumn("reltablespace");
        createTableRS.addColumn("relpersistence");
        createTableRS.addColumn("desc");
        createTableRS.addColumn("nodes");
        createTableRS.addColumn("reloptions");
        createTableRS.addRow(new Object[] {"table1","n",6,1,"","","","",""});
        preparedstatementHandler.prepareResultSet(
                tableQuery, createTableRS);
        
        String viewQuery = "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner, c.relkind as relkind FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\") and c.relname='table1' and n.nspname='ns1';";
        MockResultSet createViewRS = preparedstatementHandler.createResultSet();
        createViewRS.addColumn("oid");
        createViewRS.addColumn("viewname");
        createViewRS.addColumn("viewowner");
        createViewRS.addColumn("relkind");
        createViewRS.addRow(new Object[] {7,"table1", "", "v"});
        preparedstatementHandler.prepareResultSet(
                viewQuery, createViewRS);
        
        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
        CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
        CommonLLTUtils.fetchColumnMetaDataRS(preparedstatementHandler);
        
        Database database = connProfCache.getDbForProfileId(profileId);
        RefreshObjectDetails refreshObj = new RefreshObjectDetails();
        Namespace ns1 = new UserNamespace(6, "ns1", database);
        refreshObj.setDesctNamespace(ns1);
        refreshObj.setObjToBeRefreshed(ns1);
        refreshObj.setOperationType("CREATE_TABLE");
        refreshObj.setObjectName("table1");
        refreshObj.setParent(ns1);
        refreshObj.setNamespace(ns1);
        HashSet<Object> listOfObjects = new HashSet<>();
        AutoRefreshQueryFormation.getObjectToBeRefreshed(refreshObj, listOfObjects);
        assertTrue(!listOfObjects.isEmpty());
        refreshObj.setOperationType("CREATE_VIEW");
        AutoRefreshQueryFormation.getObjectToBeRefreshed(refreshObj, listOfObjects);
        assertTrue(!listOfObjects.isEmpty());
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
