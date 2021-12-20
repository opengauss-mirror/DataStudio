/**
 * 
 */
package com.huawei.mppdbide.test.presentation.properties;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.contentassist.ContentAssistProcesserData;
import com.huawei.mppdbide.bl.contentassist.ContentAssistUtilIf;
import com.huawei.mppdbide.bl.contentassist.ContentAssistUtilOLAP;
import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadataUtil;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import com.huawei.mppdbide.mock.presentation.CommonLLTUtils;
import com.huawei.mppdbide.mock.presentation.MockViewUtils;
import com.huawei.mppdbide.presentation.SequenceDataCore;
import com.huawei.mppdbide.presentation.objectbrowser.ObjectBrowserObjectRefreshPresentation;
import com.huawei.mppdbide.test.presentation.table.MockPresentationBLPreferenceImpl;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

import static org.junit.Assert.*;

/**
 * @author aWX353263
 *
 */
public class SequenceTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    ServerConnectionInfo              serverInfo                = new ServerConnectionInfo();
    JobCancelStatus                   status                    = null;

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
        status=new JobCancelStatus();
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
        CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
        CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
        CommonLLTUtils.fetchViewColumnInfo(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);
        CommonLLTUtils.fetchAllSynonyms(preparedstatementHandler);
        CommonLLTUtils.fetchTriggerQuery(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();

        serverInfo.setConectionName("Conn1");
        serverInfo.setServerIp("");
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setPrivilegeBasedObAccess(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        // profileId = connProfCache.initConnectionProfile(serverInfo);
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
        if (null != database)
        {
            database.getServer().close();
        }

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
    public void test_Sequence_Creation_001_01()
    {

        try
        {

            profileId = connProfCache.initConnectionProfile(serverInfo, status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshSequences(database.getConnectionManager().getObjBrowserConn());
            CommonLLTUtils.createSequence(preparedstatementHandler);
            SequenceDataCore sequenceDataCore = new SequenceDataCore(namespace);
            sequenceDataCore.getSequenceMetadata().setSequenceName("Seq_01");
            sequenceDataCore.getSequenceMetadata().setMinValue("10");
            sequenceDataCore.getSequenceMetadata().setMaxValue("10000");
            sequenceDataCore.getSequenceMetadata().setIncrementBy("2");
            sequenceDataCore.getSequenceMetadata().setStartValue("10");
            sequenceDataCore.getSequenceMetadata().setCache("1");
            sequenceDataCore.getSequenceMetadata().setCycle(true);
            SequenceObjectGroup sequenceObjectGroup = new SequenceObjectGroup(
                    OBJECTTYPE.SEQUENCE_GROUP, namespace);
            sequenceObjectGroup.getDisplayLabel();
            sequenceObjectGroup.getNamespace();

           sequenceDataCore.createConnection();
           sequenceDataCore.executeCreateSequence();
          assertEquals(namespace.getSequenceGroup().getSortedServerObjectList().size(), 2);
          sequenceDataCore.releaseConnection();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    
    }
    @Test
    public void test_Sequence_Creation_001_02()
    {

        try
        {

            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshSequences(database.getConnectionManager().getObjBrowserConn());
            CommonLLTUtils.createSequenceWithOwner(preparedstatementHandler);
            SequenceDataCore sequenceDataCore = new SequenceDataCore(namespace);
            sequenceDataCore.getSequenceMetadata().setSequenceName("Seq_01");
            sequenceDataCore.getSequenceMetadata().setMinValue("10");
            sequenceDataCore.getSequenceMetadata().setMaxValue("10000");
            sequenceDataCore.getSequenceMetadata().setIncrementBy("2");
            sequenceDataCore.getSequenceMetadata().setStartValue("10");
            sequenceDataCore.getSequenceMetadata().setCache("1");
            sequenceDataCore.getSequenceMetadata().setCycle(true);

            sequenceDataCore.createConnection();
            sequenceDataCore.executeCreateSequence();
            assertEquals(namespace.getSequenceGroup()
                    .getSortedServerObjectList().size(), 2);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    @Test
    public void test_Sequence_Creation_001_03()
    {

        try
        {

            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshSequences(database.getConnectionManager().getObjBrowserConn());
            CommonLLTUtils.createSequenceWithOwner(preparedstatementHandler);
            SequenceDataCore sequenceDataCore = new SequenceDataCore(namespace);
            sequenceDataCore.getSequenceMetadata().setSequenceName("Seq_01");
            sequenceDataCore.getSequenceMetadata().setMinValue("10");
            sequenceDataCore.getSequenceMetadata().setMaxValue("10000");
            sequenceDataCore.getSequenceMetadata().setIncrementBy("2");
            sequenceDataCore.getSequenceMetadata().setStartValue("10");
            sequenceDataCore.getSequenceMetadata().setCache("1");
            sequenceDataCore.getSequenceMetadata().setCycle(true);
            sequenceDataCore.getSequenceMetadata().setSchemaName("pg_catalog");
            sequenceDataCore.getSequenceMetadata().setTableName("table1");
            sequenceDataCore.getSequenceMetadata().setColumnName("col1");
           sequenceDataCore.createConnection();
           sequenceDataCore.executeCreateSequence();
           assertEquals(namespace.getSequenceGroup()
                   .getSortedServerObjectList().size(), 2);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    
    }
    @Test
    public void test_Listsequence_001()
    {
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshSequences(database.getConnectionManager().getObjBrowserConn());
            ArrayList<SequenceMetadata> views = namespace.getSequenceGroup()
                    .getSortedServerObjectList();
            System.out.println(views.size() );
            assertTrue(views.size() == 2);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_Listsequence_002()
    {
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshSequences(database.getConnectionManager().getObjBrowserConn());
            ArrayList<SequenceMetadata> views = namespace.getSequenceGroup()
                    .getSortedServerObjectList();


            SequenceMetadata vmd = views.get(0);
            assertTrue(vmd.getOid() == 1);
            assertTrue(vmd.getName().equals("sequence1"));
            assertTrue(vmd.getNamespace().getName().equals("pg_catalog"));

            vmd = views.get(1);
            assertTrue(vmd.getOid() == 2);
            assertTrue(vmd.getName().equals("sequence2"));
            assertTrue(vmd.getNamespace().getName().equals("pg_catalog"));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    @Test
    public void test_sequenceCreationLazyLoad_001_06()
    {
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshSequences(database.getConnectionManager().getObjBrowserConn());
            SortedMap<String, SequenceMetadata> seq = namespace.getSequenceGroup()
                    .getMatching("sequence1");
            SequenceMetadata meta = seq.get("sequence1 - pg_catalog - Sequence");
            assertNotNull(meta);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

   
    @Test
    public void test_sequenceDropSucces_001_08()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.Dropsequence(preparedstatementHandler);
            CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
            CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);


            ArrayList<SequenceMetadata> views = nm.getSequenceGroup()
                    .getSortedServerObjectList();
            SequenceMetadata vmd = views.get(0);
            assertEquals(views.size(), 2);
            vmd.dropSequence(database.getConnectionManager().getObjBrowserConn(), false);
            views = nm.getSequenceGroup()
                    .getSortedServerObjectList();
            assertEquals(views.size(), 1);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    @Test
    public void test_ViewCreation_001_09()
    {
        
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
            CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
            CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
            
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            // MockViewUtils.createViewMetadata(preparedstatementHandler, 10,
            // 2);
            /*
             * MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
             * 1, 10);
             */
            // MockViewUtils.DropView(preparedstatementHandler, "mytestview1");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace nm = database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            SortedMap<String, ViewMetaData> views = nm.getViewGroup()
                    .getMatching("myview");
            for (Entry<String, ViewMetaData> entry : views.entrySet())
            {
                System.out.println("Key = " + entry.getKey() + ", Value = "
                        + entry.getValue());
            }
            ViewMetaData viewMeta = views.get("myview - pg_catalog - View");
            assertNotNull(viewMeta);
            viewMeta.dropView( database.getConnectionManager().getFreeConnection(), false);
            views = nm.getViewGroup().getMatching("mytestview2");
            assertEquals(views.size(), 1);
            
            SequenceMetadata seq = new SequenceMetadata(1, "s1", nm);
            nm.getSequenceGroup().addToGroup(seq);

            String qry = "select oid,relnamespace,relowner,relname from pg_class where relkind='S'  and oid=1 and has_sequence_privilege(oid, 'USAGE');";
            MockResultSet refreshtablemetadatainnamspace = preparedstatementHandler.createResultSet();
            refreshtablemetadatainnamspace.addColumn("oid");
            refreshtablemetadatainnamspace.addColumn("relnamespace");
            refreshtablemetadatainnamspace.addColumn("relowner");
            refreshtablemetadatainnamspace.addColumn("relname");
            refreshtablemetadatainnamspace.addRow(new Object[] {});
            preparedstatementHandler.prepareResultSet(qry, refreshtablemetadatainnamspace);

            try {
                SequenceMetadataUtil.refresh(1, database, seq);
            } catch (DatabaseOperationException exe) {
                System.out.println("Expected Result");
            }

             seq = new SequenceMetadata(1, "s1", nm);
            nm.getSequenceGroup().addToGroup(seq);
            
            refreshtablemetadatainnamspace.addRow(new Object[] {1, 1, "newSchema", "TestSeq"});
            preparedstatementHandler.prepareResultSet(qry, refreshtablemetadatainnamspace);

            SequenceMetadataUtil.refresh(1, database, seq);
            
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    @Test
    public void test_createViewRename_Failure_001()
    {
        try
        {
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
          
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.renameView(preparedstatementHandler);
            MockViewUtils.refreshNameSpace(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);


            DBConnection conn = database.getConnectionManager().getFreeConnection();

            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();

            assertTrue(views.size() == 2);
            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            statementHandler
                    .prepareThrowsSQLException(
                            "ALTER VIEW \"newSchema\".mytestview1 RENAME TO mytestview11",
                            sqlException);
            views.get(0).rename("mytestview11", conn);

            SortedMap<String, ServerObject> view1 = nm
                    .findMatchingHyperlink("mytestview11");
            assertNotNull(view1);

        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("As expected");
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
    public void test_createViewRename_Failure_002()
    {
        try
        {
              CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            MockViewUtils.renameView(preparedstatementHandler);
            MockViewUtils.refreshNameSpace(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace namespace = database.getNameSpaceById(10);
            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);


            DBConnection conn = database.getConnectionManager().getFreeConnection();

            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();

            assertTrue(views.size() == 2);
            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            statementHandler
                    .prepareThrowsSQLException("ALTER VIEW \"newSchema\".mytestview1 RENAME TO mytestview11");
            views.get(0).rename("mytestview11", conn);
            ObjectBrowserObjectRefreshPresentation.refreshSeverObject(views.get(0));
            SortedMap<String, ServerObject> view1 = nm
                    .findMatchingHyperlink("mytestview11");
            assertNotNull(view1);

        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
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
    public void test_createViewDrop_Failure_001()
    {
        try
        {
               CommonLLTUtils.createTableRS(preparedstatementHandler);
             CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
             CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
             CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            MockViewUtils.DropView(preparedstatementHandler, "mytestview1");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace namespace = database.getNameSpaceById(10);
            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);


            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();
            ViewMetaData vmd = views.get(0);
            assertEquals(views.size(), 2);

            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            statementHandler.prepareThrowsSQLException(
                    "DROP VIEW \"newSchema\".mytestview1", sqlException);
            vmd.dropView( database.getConnectionManager().getFreeConnection(), false);

        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("As expected");
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
    public void test_createViewdrop_Failure_002()
    {
        try
        {
                CommonLLTUtils.createTableRS(preparedstatementHandler);
              CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
              CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
              CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

              CommonLLTUtils.Dropsequence(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace namespace = database.getNameSpaceById(1);
            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);


            ArrayList<SequenceMetadata> views = namespace.getSequenceGroup()
                    .getSortedServerObjectList();
            SequenceMetadata vmd = views.get(0);
            assertEquals(views.size(), 2);

            statementHandler
                    .prepareThrowsSQLException("DROP SEQUENCE \"newSchema\".sequence1");
            vmd.dropSequence(database.getConnectionManager().getFreeConnection(), false);
        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("Expected to throw exception here");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_sequencefindMatchingPrefix_success()
    {

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace nmd = database.getNameSpaceById(1);
            nmd.refreshSequences(database.getConnectionManager().getObjBrowserConn());
            ArrayList<SequenceMetadata> views = nmd.getSequenceGroup()
                    .getSortedServerObjectList();
            for (SequenceMetadata v : views)
            {
                System.out.println(v.getName());
            }
            views = nmd.getSequenceGroup().getSortedServerObjectList();
            for (SequenceMetadata v : views)
            {
                System.out.println(v.getName());
            }

            SortedMap<String, ServerObject> retObj = new TreeMap<String, ServerObject>();
            ContentAssistUtilIf conUtil = new ContentAssistUtilOLAP(database);
            retObj.putAll(conUtil.findMatchingSequenceObject("sequence1"));
            System.out.println(conUtil.findMatchingViewsObject("sequence1"));
            assertEquals(retObj.size(),1);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    
    @Test
    public void test_sequenceRefreshFailure_001_08()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.Dropsequence(preparedstatementHandler);
            CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
            CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            
            ArrayList<SequenceMetadata> views = nm.getSequenceGroup()
                    .getSortedServerObjectList();
            SequenceMetadata vmd = views.get(0);
            SequenceMetadataUtil.refresh(vmd.getOid(), database,vmd);

        }
        catch (Exception e)
        {
            System.out.println("As expected");
        }
    }

    @Test
    public void test_autosuggest_findstring_1() {
        try {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.Dropsequence(preparedstatementHandler);
            CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
            CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
            Database database = connProfCache.getDbForProfileId(profileId);

            ContentAssistUtilOLAP contentAssist = new ContentAssistUtilOLAP(database);
            assertEquals(null, contentAssist.findString("", null));
            List<Character> li = new ArrayList<Character>();
            li.add('t');
            assertEquals("example", contentAssist.findString("content.assistexample", li));
            assertEquals(".", contentAssist.findString("content.", li));
            assertEquals("", contentAssist.findString("content\n", li));
            assertEquals(".\"assist\".example", contentAssist.findString("content.\"assist\".example", li));
            assertEquals(".\"\"", contentAssist.findString("content.assist.\"\"", li));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    
    @Test
    public void test_autosuggest_getPrefixHyperLink_1() {
        try {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            CommonLLTUtils.Dropsequence(preparedstatementHandler);
            CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
            CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
            Database database = connProfCache.getDbForProfileId(profileId);
            
            ContentAssistUtilOLAP contentAssist = new ContentAssistUtilOLAP(database);
            assertEquals("content", contentAssist.getPrefixHyperLink("content.\"assist\".\'ds\'")[0]);
            assertEquals("assist", contentAssist.getPrefixHyperLink("content.\"assist\".\'ds\'")[1]);
            assertEquals("\"DataStudio\"", contentAssist.getPrefixHyperLink("content.\"assist\".\"\"\"DataStudio\"\"\"")[2]);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


}
