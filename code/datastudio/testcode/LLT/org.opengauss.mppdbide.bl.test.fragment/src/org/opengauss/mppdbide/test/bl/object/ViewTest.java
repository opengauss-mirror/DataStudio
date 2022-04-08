package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.contentassist.ContentAssistUtil;
import org.opengauss.mppdbide.bl.contentassist.ContentAssistUtilOLAP;
import org.opengauss.mppdbide.bl.export.ExportObjectDataManager;
import org.opengauss.mppdbide.bl.export.GenerateCursorExecuteUtil;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.SystemNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewUtils;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.groups.FilterObject;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ViewColumnList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils.EXCEPTIONENUM;
import org.opengauss.mppdbide.mock.bl.ExceptionConnection;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.mock.bl.MockViewUtils;
import org.opengauss.mppdbide.mock.bl.MockedResultSetMetaData;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.exceptions.PasswordExpiryException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class ViewTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
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
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        status = new JobCancelStatus();
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.refreshTableinnamespace(preparedstatementHandler);
        CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
        CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
        CommonLLTUtils.fetchViewColumnInfo(preparedstatementHandler);
        CommonLLTUtils.preparePartitionConstrainstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionIndexLoadLevel(preparedstatementHandler);
        CommonLLTUtils.preparePartitionstLoadLevel(preparedstatementHandler);
        CommonLLTUtils.fetchAllSynonyms(preparedstatementHandler);
        CommonLLTUtils.fetchTriggerQuery(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();

        serverInfo.setConectionName("Conn1");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setPrivilegeBasedObAccess(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        // profileId = connProfCache.initConnectionProfile(serverInfo,status);
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
    public void test_ViewCreation_001_01()
    {
        try
        {
            CommonLLTUtils.getSchemaNameOne(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
          //  CommonLLTUtils.getViewMockView1(preparedstatementHandler, "view1");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionCloseStmt(true);
            exceptionConnection.setThrowoutofmemerrorinrs(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    exceptionConnection);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            ArrayList<ViewMetaData> views = namespace.getViewGroup()
                    .getSortedServerObjectList();

            assertTrue(views.size() == 3);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_ViewCreation_001_02()
    {
        try
        {
            CommonLLTUtils.getViewMockRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            ArrayList<ViewMetaData> views = namespace.getViewGroup()
                    .getSortedServerObjectList();

            System.out.println("ViewTest.testTTA_BL_CREATE_DB_FUNC_001_001(): "
                    + views.size());

            ViewMetaData vmd = views.get(0);
            assertTrue(vmd.getOid() == 2);
            assertTrue(vmd.getName().equals("mytestview2"));
            assertTrue(vmd.getNamespace().getName().equals("pg_catalog"));
            assertEquals(
                    vmd.getDDL(database),
                    "CREATE OR REPLACE VIEW pg_catalog.mytestview2"+MPPDBIDEConstants.LINE_SEPARATOR+"\tAS "+MPPDBIDEConstants.LINE_SEPARATOR+"select * from something");

            vmd = views.get(1);
            assertTrue(vmd.getOid() == 1);
            assertTrue(vmd.getName().equals("myview"));
            assertTrue(vmd.getNamespace().getName().equals("pg_catalog"));
            vmd.isLoaded();
            vmd.getNameSpaceName();
            vmd.getParent();
            vmd.getViewPathQualifiedName();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_ViewName_Spcl_Char_001()
    {
        try
        {
            CommonLLTUtils.getSchemaNameOne(preparedstatementHandler);
            CommonLLTUtils.getViewMockView1(preparedstatementHandler,
                    "schema one");
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
           // CommonLLTUtils.createTableRS(preparedstatementHandler);
           
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());

            ArrayList<ViewMetaData> views = namespace.getViewGroup()
                    .getSortedServerObjectList();

            ViewMetaData vmd = views.get(0);
            System.out.println(vmd.getQualifiedObjectName() + "-"
                    + vmd.getSearchName());
            assertEquals(vmd.getQualifiedObjectName(), "mytestview2");
            assertEquals(vmd.getSearchName(), "mytestview2 - pg_catalog - View");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_ViewName_Spcl_Char_002()
    {
        try
        {
            CommonLLTUtils.getSchemaNameOne(preparedstatementHandler);
            CommonLLTUtils.getViewMockView1(preparedstatementHandler,
                    "pg_catalog");
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());

            ArrayList<ViewMetaData> views = namespace.getViewGroup()
                    .getSortedServerObjectList();

            ViewMetaData vmd = views.get(0);
            System.out.println(vmd.getQualifiedObjectName() + "-"
                    + namespace.getSearchName());
            System.out.println(vmd.getSearchName());
            assertEquals(vmd.getQualifiedObjectName(), "mytestview2");
            assertEquals(vmd.getSearchName(), "mytestview2 - pg_catalog - View");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_ViewName_Spcl_Char_003()
    {
        try
        {    
            
            CommonLLTUtils.getSchemaNameOne(preparedstatementHandler);
            CommonLLTUtils.getViewMockView1(preparedstatementHandler,
                    "SechemaOne");
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
              
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());

            ArrayList<ViewMetaData> views = namespace.getViewGroup()
                    .getSortedServerObjectList();

            ViewMetaData vmd = views.get(0);
            System.out.println(vmd.getQualifiedObjectName() + "-"
                    + namespace.getSearchName());

            System.out
                    .println(vmd.getDisplayName() + "-" + vmd.getSearchName());
            assertEquals(vmd.getQualifiedObjectName(), "mytestview2");
            assertEquals(vmd.getDisplayName(),"pg_catalog.mytestview2");
            assertEquals(vmd.getSearchName(), "mytestview2 - pg_catalog - View");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_ViewCreationLazyLoad_001_06()
    {
        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(10);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            SortedMap<String, ViewMetaData> views = namespace.getViewGroup()
                    .getMatching("myview");
            for (Entry<String, ViewMetaData> entry : views.entrySet())
            {
                System.out.println("Key = " + entry.getKey() + ", Value = "
                        + entry.getValue());
            }
            ViewMetaData viewMeta = views.get("myview - newSchema - View");
            assertNotNull(viewMeta);

            ViewColumnList list = viewMeta.getColumns();
            assertEquals(list.getSize(), 2);

            ViewColumnMetaData itemList = list.getItem(0);
            assertEquals(itemList.getName(), "col1");
            assertEquals(itemList.getDataTypeSchema(), null);
            assertEquals(itemList.getLenOrPrecision(), 64);
            assertEquals(itemList.isNotNull(), false);
            assertEquals(itemList.getDefaultValue(), "");
            assertEquals(itemList.getCheckConstraintExpr(), null);
            assertEquals(itemList.isUnique(), false);
            assertEquals(itemList.getParent().getName(), "myview");

        }
        catch (Exception ex)
        {

        }
    }

    @Test
    public void test_ViewCreationRefresh_001_07()
    {
        try
        {
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);


            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();
            for (ViewMetaData v : views)
            {
                System.out.println(v.getName());
            }
            assertEquals(views.size(), 3);
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 3);
            nm.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            views = nm.getViewGroup().getSortedServerObjectList();
            for (ViewMetaData v : views)
            {
                System.out.println(v.getName());
            }
            assertEquals(views.size(), 3);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_ViewDropSucces_001_08()
    {
        try
        {
              CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
              CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
              CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            MockViewUtils.DropView(preparedstatementHandler, "mytestview1");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            database.getNameSpaceById(10);
            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);


            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();
            ViewMetaData vmd = views.get(0);
            assertEquals(views.size(), 3);
            vmd.dropView(database.getConnectionManager().getFreeConnection(), false);
            views = nm.getViewGroup().getSortedServerObjectList();
            assertEquals(views.size(), 2);

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
             //CommonLLTUtils.createTableRS(preparedstatementHandler);
             CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
             CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
             CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            //MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            MockViewUtils.DropView(preparedstatementHandler, "mytestview1");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace namespace = database.getNameSpaceById(10);
            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            SortedMap<String, ViewMetaData> views = nm.getViewGroup()
                    .getMatching("mytestview2");
           
            ViewMetaData viewMeta = views.get("mytestview2 - pg_catalog - View");
            assertNotNull(viewMeta);
            viewMeta.dropView( database.getConnectionManager().getFreeConnection(), false);
            views = nm.getViewGroup().getMatching("mytestview2");
            assertEquals(views.size(), 0);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_ViewRename_001_08()
    {
        try
        {
            //CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
         //   MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
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

            assertTrue(views.size() == 3);

            views.get(0).rename("mytestview11", conn);

            SortedMap<String, ServerObject> view1 = namespace
                    .findMatchingHyperlink("mytestview1");
            assertNotNull(view1);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_DropView_Sucess_001_01()
    {
        try
        {
            boolean isCascade = false;
           // CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
         //   MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 3);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace namespace = database.getNameSpaceById(10);
            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);


            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();

            views.get(0).dropView(database.getConnectionManager().getObjBrowserConn(),
                    isCascade);

            views = nm.getViewGroup().getSortedServerObjectList();
            // to check view is drop for that we have taken 3 view and below
            // checking equal for 2
            assertEquals(views.size(), 2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_DropCascadeView_Sucess_001_01()
    {
        try
        {
            boolean isCascade = true;
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 3);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);


            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();
            views.get(1).dropView( database.getConnectionManager().getObjBrowserConn(),
                    isCascade);

            views = nm.getViewGroup().getSortedServerObjectList();
            // to check view is drop for that we have taken 3 view and below
            // checking equal for 2
            assertEquals(views.size(), 2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_CreateViewTemplate_Sucess()
    {
        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 3);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(10);
            ViewMetaData viewMetadat = new ViewMetaData(1, "MyTestView",
                    namespace,database);
            assertEquals(ViewUtils.getCreateViewTemplate(namespace),
                    MockViewUtils.getMockedCreateViewTemplate(namespace));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_CreateViewTemplate_withSchemaInQuotes_Sucess()
    {
        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "new Schema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 3);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(10);
            ViewMetaData viewMetadat = new ViewMetaData(1, "MyTestView",
                    namespace,database);
            assertEquals(ViewUtils.getCreateViewTemplate(namespace),
                    MockViewUtils.getMockedCreateViewTemplate(namespace));
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
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
          //  MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.renameView(preparedstatementHandler);
            MockViewUtils.refreshNameSpace(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            MockViewUtils.createViewMetadata_Oid(preparedstatementHandler, 10, 2, nm.getName(), nm );
            DBConnection conn = database.getConnectionManager().getFreeConnection();

            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();

            assertTrue(views.size() == 3);
            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            preparedstatementHandler
                    .prepareThrowsSQLException(
                            "ALTER VIEW pg_catalog.mytestview2  RENAME TO mytestview ;",
                            sqlException);
            views.get(0).rename("mytestview", conn);

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
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockResultSet namespaceRS1 = preparedstatementHandler.createResultSet();
            namespaceRS1.addColumn("oid");
            namespaceRS1.addColumn("nspname");
            namespaceRS1.addRow(new Object[] {10, "newSchema"});
            preparedstatementHandler.prepareResultSet(
                    CommonLLTUtils.FETCH_ALL_NAMESPACE_LOAD_PRIV, namespaceRS1);
            
            MockViewUtils.renameView(preparedstatementHandler);
            MockViewUtils.refreshNameSpace(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace nm=database.getNameSpaceById(10);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);


            DBConnection conn = database.getConnectionManager().getFreeConnection();

            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();

            assertTrue(views.size() == 2);
            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            statementHandler
                    .prepareThrowsSQLException("ALTER VIEW \"newSchema\".myview RENAME TO mytestview11");
            views.get(0).rename("mytestview11", conn);

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
             CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
             CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
             CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.DropView(preparedstatementHandler, "mytestview1");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);


            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();
            ViewMetaData vmd = views.get(0);
            assertEquals(views.size(), 3);

            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            statementHandler.prepareThrowsSQLException(
                    "DROP VIEW \"newSchema\".myview", sqlException);
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

    public void test_createViewdrop_Failure_002()
    {
        try
        {
              CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
              CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
              CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.DropView(preparedstatementHandler, "myview");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);


            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();
            ViewMetaData vmd = views.get(0);
            assertEquals(views.size(), 3);

            statementHandler
                    .prepareThrowsSQLException("DROP VIEW \"newSchema\".mytestview1");
            vmd.dropView( database.getConnectionManager().getFreeConnection(), false);
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

    public void test_createViewsetnamespace_001()
    {
        try
        {
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            MockViewUtils.refreshNameSpace(preparedstatementHandler);
            MockViewUtils.setNamespace(preparedstatementHandler, 10, "schema2");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            SystemNamespace namespace1 = new SystemNamespace(1, "schema2", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace1);

            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();
            ViewMetaData vmd = views.get(0);
            assertEquals(views.size(), 3);

            System.out.println(vmd.getNamespace().getName());

            vmd.setNamespace(namespace1);

            ViewMetaData vmd1 = views.get(0);
            assertEquals(vmd1.getNamespace().getName(), "schema2");

        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    
    public void test_createViewsetnamespaceTo()
    {
        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            MockViewUtils.refreshNameSpace(preparedstatementHandler);
            MockViewUtils.setNamespace(preparedstatementHandler, 10, "schema2");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(10);
            ViewMetaData viewMetadat = new ViewMetaData(2, "MyTestView",
                    namespace,database);


            viewMetadat.setNamespaceTo("newSchema2", database.getConnectionManager().getFreeConnection());
            assertFalse("newSchema2".equals(viewMetadat.getNamespace().getName()));
            //assertNotEquals(viewMetadat.getNamespace().getName(), "newSchema2");

        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    public void test_createViewsetnamespace_Failure_002()
    {
        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            MockViewUtils.refreshNameSpace(preparedstatementHandler);
            MockViewUtils.setNamespace(preparedstatementHandler, 10, "schema2");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(10);
            Namespace namespace1 = new Namespace(1, "schema2", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace1);

            ArrayList<ViewMetaData> views = namespace.getViewGroup()
                    .getSortedServerObjectList();
            ViewMetaData vmd = views.get(0);
            assertEquals(views.size(), 2);

            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            statementHandler
                    .prepareThrowsSQLException(
                            "ALTER VIEW \"newSchema\".mytestview1 SET schema newSchema2",
                            sqlException);

            vmd.setNamespaceTo("newSchema2", database.getConnectionManager().getFreeConnection());

            ViewMetaData vmd1 = views.get(0);
            assertEquals(vmd1.getNamespace().getName(), "newSchema");

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

        }

    }

    public void test_createViewsetnamespace_Failure_001()
    {
        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            MockViewUtils.refreshNameSpace(preparedstatementHandler);
            MockViewUtils.setNamespace(preparedstatementHandler, 10, "schema2");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(10);
            Namespace namespace1 = new Namespace(1, "schema2", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace1);

            ArrayList<ViewMetaData> views = namespace.getViewGroup()
                    .getSortedServerObjectList();
            ViewMetaData vmd = views.get(0);
            assertEquals(views.size(), 2);

            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            statementHandler
                    .prepareThrowsSQLException("ALTER VIEW \"newSchema\".mytestview1 SET schema newSchema2");

            vmd.setNamespaceTo("newSchema2", database.getConnectionManager().getFreeConnection());

            ViewMetaData vmd1 = views.get(0);
            assertEquals(vmd1.getNamespace().getName(), "newSchema");

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

        }

    }

    @Test
    public void testTTA_BL_ViewMetaDataTest_24()
    {
        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            MockViewUtils.refreshNameSpace(preparedstatementHandler);
            MockViewUtils.setNamespace(preparedstatementHandler, 10, "schema2");
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace xyz =database.getNameSpaceById(1);
            long vmdOid = 2;
            ViewMetaData vmd = new ViewMetaData(vmdOid, "anything", xyz,database);
            // ViewMetaData vmd1=new ViewMetaData(2, "anything_!", xyz);
            Namespace namespace = database.getNameSpaceById(10);
            vmd.setName("ashish");
            vmd.setNamespace(namespace);

            assertEquals(31 + vmdOid, vmd.hashCode());

            assertTrue("true", vmd.equals(vmd));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_ViewMetaDataTest_25()
    {
        try
        {
            Namespace xyz = null;
            TableMetaData parentTable = null;
            TypeMetaData dataType = null;
            ViewMetaData vmd = new ViewMetaData(2, "anything", xyz,null);

            ColumnMetaData cmd = new ColumnMetaData(parentTable, 3, "table",
                    dataType);
            assertEquals(false, vmd.equals(xyz));
            assertEquals(false, vmd.equals(cmd));
            assertNotSame(cmd, vmd);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_ViewMetaDataTest_28()
    {
        try
        {

            Namespace xyz = null;
            ViewMetaData vmd = new ViewMetaData(2, "anything", xyz,null);
            ViewColumnMetaData vcd = new ViewColumnMetaData(vmd, 1, "myView1",
                    null);
            assertFalse(vcd.equals(vmd));
            assertTrue(vcd.equals(vcd));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_ViewMetaDataTest_29()
    {
        try
        {
            Namespace xyz = null;
            ViewMetaData vmd = new ViewMetaData(2, "anything", xyz,null);
            ViewColumnMetaData vcd = new ViewColumnMetaData(vmd, 1, "myView1",
                    null);
            ViewColumnMetaData vcd1 = new ViewColumnMetaData(vmd, 1, "myiew1",
                    null);
            ViewColumnMetaData vcd2 =null;
            
            assertFalse(vcd.equals(vcd2));
            assertFalse(vcd.equals(vcd1));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_ViewMetaDataTest_32()
    {
        try
        {

            ViewColumnList obj1 = new ViewColumnList(null);
            obj1.setName("myView");

            assertTrue(obj1.equals(obj1));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_ViewMetaDataTest_33()
    {
        try
        {
            ViewColumnList obj1 = new ViewColumnList(null);
            obj1.setName("myView");
            ViewColumnList obj2 = new ViewColumnList(null);
            obj1.setName("myView2");
            assertFalse(obj1.equals(obj2));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_findMatchingPrefix_success()
    {

        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "pg_catalog");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace nmd = database.getNameSpaceById(1);

            ArrayList<ViewMetaData> views = nmd.getViewGroup()
                    .getSortedServerObjectList();
            for (ViewMetaData v : views)
            {
                System.out.println(v.getName());
            }
            nmd.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            views = nmd.getViewGroup().getSortedServerObjectList();
            for (ViewMetaData v : views)
            {
                System.out.println(v.getName());
            }

            SortedMap<String, ServerObject> retObj = new TreeMap<String, ServerObject>();
            ContentAssistUtil contUtil = new ContentAssistUtilOLAP(database);
            retObj.putAll(contUtil.findMatchingViewsObject("pg_catalog"));
            System.out.println(contUtil.findMatchingViewsObject("newSchema"));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_Namespace_Disconnection_View_001_01()
            throws MPPDBIDEException, PasswordExpiryException
    {
        try
        {

            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());

            Namespace namespace1 = namespace;
            ArrayList<ViewMetaData> views = namespace1.getViewGroup()
                    .getSortedServerObjectList();

            System.out
                    .println("ViewTest.test_Namespace_Disconnection_View_001_01(): "
                            + views.size());

            connProfCache.destroyConnection(database);

            assertNull(namespace1.getViewGroup());

        }
        catch (NullPointerException e)
        {

            e.printStackTrace();
            fail(e.getMessage());

        }
        catch (DatabaseOperationException e)
        {

            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseCriticalException e)
        {

            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_DDLCheck_VIEW_001_01()
    {
        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());

            ArrayList<ViewMetaData> views = namespace.getViewGroup()
                    .getSortedServerObjectList();

            String ddl = views.get(0).getDDL(database);

            assertTrue(views.size() == 3);
            assertTrue((ddl.replaceAll("\\t|\\s", ""))
                    .equalsIgnoreCase("CREATE OR REPLACE VIEW pg_catalog.mytestview2 AS select * from something"
                            .replaceAll("\\t|\\s", "")));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_findMatchingPrefix_success_0001()
    {

        try
        {  
            CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
             CommonLLTUtils.createTableRS(preparedstatementHandler);
           //  CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
             CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
             CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
             MockViewUtils.createNamespace(preparedstatementHandler, 10,
                     "newSchema");
           //  MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
             MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                     10, 10);
             profileId = connProfCache.initConnectionProfile(serverInfo,status);
             Database database = connProfCache.getDbForProfileId(profileId);
             database.getServer().setServerCompatibleToNodeGroup(true);
             Namespace nm=database.getNameSpaceById(1);
             nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
             SortedMap<String, ServerObject> retObj = nm
                     .findMatchingHyperlink("myview");
    
             assertEquals(retObj.size(), 1);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("message is :"+e.getMessage());
            fail(e.getMessage());
        }

    }

    @Test
    public void test_findMatchingPrefix_success_0002()
    {

        try
        {
            //CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            //MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            Namespace nmd = database.getNameSpaceById(10);
            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            SortedMap<String, ServerObject> retObj = nmd.findMatchingHyperlink("mytestview2");
            assertEquals(retObj.size(), 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_findMatchingPrefix_success_0003()
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
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            SortedMap<String, ServerObject> retObj = nm
                    .findMatchingHyperlink("myview");
            assertEquals(retObj.size(), 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_findMatchingPrefix_success_0004()
    {

        try
        {
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            nm.findMatchingHyperlink("mytestview2");
            SortedMap<String, ServerObject> retObj = nm
                    .findMatchingHyperlink("mytestview2");
            assertEquals(retObj.size(), 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_findMatchingPrefix_success_0005()
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
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            Namespace nmd = database.getNameSpaceById(10);
            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);
            nm.findMatchingHyperlink("mytestview2");
            nm.findMatchingHyperlink("mytestview2");
            SortedMap<String, ServerObject> retObj = nm
                    .findMatchingHyperlink("mytestview2");
            assertEquals(retObj.size(), 1);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_View_Meta_21()
    {
        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "schema_one");
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
            
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            SortedMap<String, ViewMetaData> views = namespace.getViewGroup()
                    .getMatching("myview");
            ViewMetaData viewMeta = views
                    .get("myview - pg_catalog - View");
            assertNotNull(viewMeta);

            ViewColumnList list = viewMeta.getColumns();
            assertEquals(list.getSize(), 2);

            ViewColumnMetaData itemList = list.getItem(0);
            // itemList.setDefaultValue("abc");
            itemList.setDefaultValue("abc", database.getConnectionManager().getFreeConnection());
            itemList.getSetDefaultValueQuery("");
            
            assertEquals(itemList.getSetDefaultValueQuery("abc"),
                    "ALTER VIEW pg_catalog.myview ALTER COLUMN col1 SET DEFAULT 'abc'");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_View_Meta_21_01()
    {
        try
        {
            CommonLLTUtils.getViewMockRS(preparedstatementHandler);
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "schema_one");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            SortedMap<String, ViewMetaData> views = namespace.getViewGroup()
                    .getMatching("mytestview2");
            ViewMetaData viewMeta = views
                    .get("mytestview2 - pg_catalog - View");
            assertNotNull(viewMeta);

            ViewColumnList list = viewMeta.getColumns();
            assertEquals(list.getSize(), 0);

            ViewColumnMetaData itemList = list.getItem(0);
            // itemList.setDefaultValue("abc");
            itemList.setDefaultValue("abc", database.getConnectionManager().getFreeConnection());
            assertEquals(itemList.getSetDefaultValueQuery("abc"),
                    "ALTER VIEW schema_one.mytestview1 ALTER COLUMN col1 SET DEFAULT 'abc'");
            
            namespace.fetchViewColumnInfo(viewMeta, database.getConnectionManager().getFreeConnection());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Test
    public void test_View_Meta_21_02()
    {
        try
        {
            CommonLLTUtils.getViewMockRS(preparedstatementHandler);
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "schema_one");
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            SortedMap<String, ViewMetaData> views = namespace.getViewGroup()
                    .getMatching("mytestview2");
            ViewMetaData viewMeta = views
                    .get("mytestview2 - pg_catalog - View");
            assertNotNull(viewMeta);

            ViewColumnList list = viewMeta.getColumns();
            assertEquals(list.getSize(), 0);

            ViewColumnMetaData itemList = list.getItem(0);
            // itemList.setDefaultValue("abc");
            itemList.setDefaultValue("abc", database.getConnectionManager().getFreeConnection());
            assertEquals(itemList.getSetDefaultValueQuery("abc"),
                    "ALTER VIEW schema_one.mytestview1 ALTER COLUMN col1 SET DEFAULT 'abc'");
            

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            
            namespace.fetchViewColumnInfo(viewMeta, database.getConnectionManager().getFreeConnection());
            fail("Not expected to come here");

        }
        catch (Exception e)
        {
           
        }
    }
    @Test
    public void test_View_Meta_21_04_01()
    {
        try
        {
            CommonLLTUtils.getViewMockRS(preparedstatementHandler);
            //MockViewUtils.createNamespace(preparedstatementHandler, 10,
                   // "schema_one");
            /*MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    1, 1);*/
            CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            SortedMap<String, ViewMetaData> views = namespace.getViewGroup()
                    .getMatching("mytestview2");
            ViewMetaData viewMeta = views
                    .get("mytestview2 - pg_catalog - View");
            assertNotNull(viewMeta);

            ViewColumnList list = viewMeta.getColumns();
            assertEquals(list.getSize(), 0);

            ViewColumnMetaData itemList = list.getItem(0);
            // itemList.setDefaultValue("abc");
            itemList.setDefaultValue("abc", database.getConnectionManager().getFreeConnection());
            assertEquals(itemList.getSetDefaultValueQuery("abc"),
                    "ALTER VIEW schema_one.mytestview1 ALTER COLUMN col1 SET DEFAULT 'abc'");
            String checkType = itemList.getClmNameWithDatatype(true);
            assertEquals(checkType, itemList.getClmNameWithDatatype(true));
            
            String checkTypenew = itemList.getClmNameWithDatatype(false);
            assertEquals(checkTypenew, itemList.getClmNameWithDatatype(false));
            itemList.getDefaultValue();
            itemList.getDataTypeSchema();
            itemList.isNotNull();
            itemList.getViewDisplayDatatype();
            itemList.isUnique();
            itemList.getLenOrPrecision();
            itemList.setParent(viewMeta);
            itemList.getDatabase();
            itemList.getCheckConstraintExpr();
            itemList.setLenOrPrecision(2);
            itemList.setScale(2);
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            
            namespace.fetchLevel2ViewColumnInfo(database.getConnectionManager().getFreeConnection());
            fail("Not expected to come here");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
  
    @Test
    public void test_View_Meta_21_03()
    
    {
        try
        {
            CommonLLTUtils.getViewMockRS(preparedstatementHandler);
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "schema_one");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            SortedMap<String, ViewMetaData> views = namespace.getViewGroup()
                    .getMatching("mytestview2");
            ViewMetaData viewMeta = views
                    .get("mytestview2 - pg_catalog - View");
            assertNotNull(viewMeta);

            ViewColumnList list = viewMeta.getColumns();
            assertEquals(list.getSize(), 0);

            ViewColumnMetaData itemList = list.getItem(0);
            // itemList.setDefaultValue("abc");
            itemList.setDefaultValue("abc", database.getConnectionManager().getFreeConnection());
            assertEquals(itemList.getSetDefaultValueQuery("abc"),
                    "ALTER VIEW schema_one.mytestview1 ALTER COLUMN col1 SET DEFAULT 'abc'");
            

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            
            namespace.fetchLevel2ViewColumnInfo(database.getConnectionManager().getFreeConnection());
            fail("Not expected to come here");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Test
    public void test_View_Meta_21_04()
    {
        try
        {
            CommonLLTUtils.getViewMockRS(preparedstatementHandler);
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "schema_one");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            SortedMap<String, ViewMetaData> views = namespace.getViewGroup()
                    .getMatching("mytestview2");
            ViewMetaData viewMeta = views
                    .get("mytestview2 - pg_catalog - View");
            assertNotNull(viewMeta);

           /* ViewColumnList list = viewMeta.getColumns();
            assertEquals(list.getSize(), 2);

            ViewColumnMetaData itemList = list.getItem(0);
            // itemList.setDefaultValue("abc");
            itemList.setDefaultValue("abc", database.getFreeConnection());
            assertEquals(itemList.getSetDefaultValueQuery("abc"),
                    "ALTER VIEW schema_one.mytestview1 ALTER COLUMN col1 SET DEFAULT 'abc'");*/
            

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            namespace.fetchTableColumnMetaData(1,namespace.getTablesGroup(), database.getConnectionManager().getFreeConnection());
            fail("Not expected to come here");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Test
    public void test_View_Meta_21_06()
    {
        try
        {
            CommonLLTUtils.getViewMockRS(preparedstatementHandler);
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "schema_one");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            SortedMap<String, ViewMetaData> views = namespace.getViewGroup()
                    .getMatching("mytestview2");
            ViewMetaData viewMeta = views
                    .get("mytestview2 - pg_catalog - View");
            assertNotNull(viewMeta);

            ViewColumnList list = viewMeta.getColumns();
            assertEquals(list.getSize(), 0);

            /*ViewColumnMetaData itemList = list.getItem(0);
            // itemList.setDefaultValue("abc");
            itemList.setDefaultValue("abc", database.getFreeConnection());
            assertEquals(itemList.getSetDefaultValueQuery("abc"),
                    "ALTER VIEW schema_one.mytestview1 ALTER COLUMN col1 SET DEFAULT 'abc'");*/
            

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            TableMetaData tableMetaData =new TableMetaData(namespace);
            tableMetaData.fetchIndexForTable( database.getConnectionManager().getFreeConnection());
            fail("Not expected to come here");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Test
    public void test_View_Meta_21_05()
    {
        try
        {
            CommonLLTUtils.getViewMockRS(preparedstatementHandler);
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "schema_one");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace namespace = database.getNameSpaceById(10);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            SortedMap<String, ViewMetaData> views = namespace.getViewGroup()
                    .getMatching("mytestview");
            ViewMetaData viewMeta = views
                    .get("mytestview2 - schema_one - View");
            assertNotNull(viewMeta);

            ViewColumnList list = viewMeta.getColumns();
            assertEquals(list.getSize(), 0);

            ViewColumnMetaData itemList = list.getItem(0);
            // itemList.setDefaultValue("abc");
            itemList.setDefaultValue("abc", database.getConnectionManager().getFreeConnection());
            assertEquals(itemList.getSetDefaultValueQuery("abc"),
                    "ALTER VIEW schema_one.mytestview1 ALTER COLUMN col1 SET DEFAULT 'abc'");
            

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            
            String query = "with x as (SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner "
                    + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\" or c.relkind = 'm'::\"char\") ";
            ViewUtils.fetchViews(namespace, query, database.getConnectionManager().getFreeConnection());
            fail("Not expected to come here");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void test_Load_Connection_Database_Operation_Exceptions()
    {
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            JobCancelStatus status = new JobCancelStatus();
            status.setCancel(false);
            Server server = new Server(serverInfo);

            preparedstatementHandler
                    .prepareThrowsSQLException("with x as (SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner "
                            + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\" or c.relkind = 'm'::\"char\") ");
            server.createDBConnectionProfile(serverInfo,status);

        }
        catch (DatabaseCriticalException e)
        {
          
            fail(e.getMessage());
        }

        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            
            fail(e.getMessage());
        }
    }

    @Test
    public void test_Load_Connection_Database_Critical_Exceptions_01()
    {

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Server server = new Server(serverInfo);
            JobCancelStatus status=new JobCancelStatus();
             status.setCancel(false);
            

            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            preparedstatementHandler
                    .prepareThrowsSQLException(
                            "with x as (SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner "
                                    + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\" or c.relkind = 'v'::\"char\") ",
                            sqlException);
            server.createDBConnectionProfile(serverInfo, status);

        }
        catch (DatabaseCriticalException e)
        {
            fail(e.getMessage());
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
    public void test_Load_Connection_Database_Critical_Exceptions_011()
    {

        try
        {

          //  CommonLLTUtils.getDeadLineInfoRs(preparedstatementHandler);
            String deadLineQry = "select intervaltonum(gs_password_deadline()) as DEADLINE;";
            MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
            getdbsrs.addColumn("DEADLINE");
            getdbsrs.addRow(new Object[] {"2Days"});
            preparedstatementHandler.prepareResultSet(
                    deadLineQry,
                    getdbsrs);

            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            Server server = new Server(serverInfo);

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptionCommit(true);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(
                    exceptionConnection);
            server.createDBConnectionProfile(serverInfo, status);
            fail("Not excepted to comer here");
        }
        catch (DatabaseCriticalException e)
        {
            fail("Not excepted to comer here");

        }
        catch (DatabaseOperationException e)
        {
           
            fail(e.getMessage());
        }
        catch (Exception e)
        {
          //  fail(e.getMessage());
        }
    }


    @Test
    public void test_Load_Connection_Database_Critical_Exceptions_02()
    {

        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "new Schema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 3);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            Server server = new Server(serverInfo);
            server.createDBConnectionProfile(serverInfo,status);

            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            preparedstatementHandler
                    .prepareThrowsSQLException(
                            "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and v.relnamespace = 10 order by v.oid, c.attnum",
                            sqlException);
            namespace.fetchLevel2ViewColumnInfo(database.getConnectionManager().getObjBrowserConn());

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
    public void test_Load_Connection_Database_Critical_Exceptions_021()
    {

        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "new Schema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 3);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(true);
            Server server = new Server(serverInfo);
            server.createDBConnectionProfile(serverInfo,status);

            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            preparedstatementHandler
                    .prepareThrowsSQLException(
                            "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and v.relnamespace = 10 order by v.oid, c.attnum",
                            sqlException);
            namespace.fetchLevel2ViewColumnInfo(database.getConnectionManager().getObjBrowserConn());

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
    public void test_Column_Details_Fetched_Database_Critical_Exceptions()
    {

        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "new Schema");
            CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 3);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            preparedstatementHandler
                    .prepareThrowsSQLException(
                            "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0  and v.relnamespace = 1 order by v.oid, c.attnum",
                            sqlException);
            namespace.fetchLevel2ViewColumnInfo(database.getConnectionManager().getObjBrowserConn());

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
    public void testTTA_BL_ViewMetaDataTest_23()
    {
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Iterator<Server> servers = connProfCache.getServers().iterator();
            Database db = servers.next().getAllDatabases().iterator().next();
            Namespace ns = db.getNameSpaceById(1);

            ViewMetaData vmd = new ViewMetaData(2, "anything", ns,db);
            ViewMetaData vmd1 = new ViewMetaData(2, "anything", ns,db);
            assertTrue(vmd.equals(vmd1));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_ViewMetaDataTest_26()
    {
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Iterator<Server> servers = connProfCache.getServers().iterator();
            Database db = servers.next().getAllDatabases().iterator().next();
            Namespace ns = db.getNameSpaceById(1);

            ServerConnectionInfo serverInfo1 = new ServerConnectionInfo();
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            serverInfo1.setConectionName("Conn2");
            serverInfo1.setServerIp("");
            serverInfo1.setServerPort(5432);
            serverInfo1.setDatabaseName("Gauss");
            serverInfo1.setUsername("myusername");
            serverInfo1.setPrd("mypassword".toCharArray());
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo1
                    .setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo1.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo1);
            profileId = connProfCache.initConnectionProfile(serverInfo1,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace ns1 = database.getNameSpaceById(1);

            ViewMetaData vmd = new ViewMetaData(2, "anything", ns1,database);
            ViewMetaData vmd1 = new ViewMetaData(2, "anything", ns,database);
            assertFalse(vmd.equals(vmd1));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_ViewMetaDataTest_27()
    {
        try
        {
            TypeMetaData dataType = null;
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Iterator<Server> servers = connProfCache.getServers().iterator();
            Database db = servers.next().getAllDatabases().iterator().next();
            Namespace ns = db.getNameSpaceById(1);

            ViewMetaData vmd = new ViewMetaData(2, "anything", ns,db);
            ViewMetaData vmd1 = new ViewMetaData(2, "anything", ns,db);
            ViewColumnMetaData vcmd = new ViewColumnMetaData(vmd, 1, "Hello",
                    dataType);
            ViewColumnMetaData vcmd1 = new ViewColumnMetaData(vmd1, 1, "Hello",
                    dataType);

            vcmd1.setDefaultValue("abc", db.getConnectionManager().getFreeConnection());
                assertEquals(
                        "ALTER VIEW pg_catalog.anything ALTER COLUMN \"Hello\" SET DEFAULT 'abc'",vcmd1.getSetDefaultValueQuery("abc"));
                vcmd1.getDefaultValue();
                vcmd1.getDataTypeSchema();
                vcmd1.isNotNull();
                vcmd1.getViewDisplayDatatype();
                vcmd1.isUnique();
                vcmd1.getLenOrPrecision();
                vcmd1.setParent(vmd);
                vcmd1.getDatabase();
                vcmd1.getCheckConstraintExpr();
                
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }


    @Test
    public void testTTA_BL_ViewMetaDataTest_31()
    {
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Iterator<Server> servers = connProfCache.getServers().iterator();
            Database db = servers.next().getAllDatabases().iterator().next();
            Namespace ns = db.getNameSpaceById(1);

            ViewColumnList obj1 = new ViewColumnList(ns);
            obj1.setName("myView");
            ViewColumnList obj2 = new ViewColumnList(ns);
            obj2.setName("myView");
            assertTrue(obj1.equals(obj2));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_ViewMetaDataTest_34()
    {
        try
        {
            TypeMetaData dataType = null;
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Iterator<Server> servers = connProfCache.getServers().iterator();
            Database db = servers.next().getAllDatabases().iterator().next();
            Namespace ns = db.getNameSpaceById(1);
                
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo1 = new ServerConnectionInfo();
            serverInfo1.setConectionName("Conn2");
            serverInfo1.setServerIp("");
            serverInfo1.setServerPort(5432);
            serverInfo1.setDatabaseName("Gauss");
            serverInfo1.setUsername("myusername");
            serverInfo1.setPrd("mypassword".toCharArray());
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo1
                    .setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo1.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo1);
            profileId = connProfCache.initConnectionProfile(serverInfo1,status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace ns1 = database.getNameSpaceById(1);

            ViewColumnList obj1 = new ViewColumnList(ns);
            obj1.setName("myView");
            ViewColumnList obj2 = new ViewColumnList(ns1);
            obj2.setName("myView");
            assertFalse(obj1.equals(obj2));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_ViewMetaDataTest_getOwner_001()
    {

        try
        {
            TypeMetaData dataType = null;
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Iterator<Server> servers = connProfCache.getServers().iterator();
            Database db = servers.next().getAllDatabases().iterator().next();
            Namespace ns = db.getNameSpaceById(1);
            JobCancelStatus status=new JobCancelStatus();
            status.setCancel(false);
            ServerConnectionInfo serverInfo1 = new ServerConnectionInfo();
            serverInfo1.setConectionName("Conn2");
            serverInfo1.setServerIp("");
            serverInfo1.setServerPort(5432);
            serverInfo1.setDatabaseName("Gauss");
            serverInfo1.setUsername("myusername");
            serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo1.setPrd("mypassword".toCharArray());
            serverInfo1
                    .setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo1.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo1);
            profileId = connProfCache.initConnectionProfile(serverInfo1,
                    status);
            Database database = connProfCache.getDbForProfileId(profileId);

            Namespace ns1 = database.getNameSpaceById(1);

            ViewMetaData vmd = new ViewMetaData(2, "anything", ns,database);
            vmd.setNamespace(ns1);
          //  vmd.setOwner("ashish");
            String owner = vmd.getOwner();
            vmd.setLevel3Loaded(true);
            boolean level3loaded = vmd.isLevel3Loaded();
            vmd.setLevel3LoadInProgress(true);
            boolean level3loadinprogress = vmd.isLevel3LoadInProgress();

            assertEquals(ns1.getName(), vmd.getNamespace().getName());
            assertEquals(true, level3loadinprogress);
            assertEquals(true, level3loaded);
           // assertEquals("ashish", owner);
        }
        catch (Exception e)
        {
            fail("not expected");
        }
    }

    @Test
    public void testTTA_BL_ViewColumnMetaDataTest_01()
    {
        try
        {
            preparedstatementHandler = connection
                    .getPreparedStatementResultSetHandler();
            statementHandler = connection.getStatementResultSetHandler();

            CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
            CommonLLTUtils.prepareProxyInfoForDB(preparedstatementHandler);
            connProfCache = DBConnProfCache.getInstance();
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace ns1 = database.getNameSpaceById(1);
            ViewColumnList obj1 = new ViewColumnList(ns1);

            ViewMetaData vmd = new ViewMetaData(2, "anything", null,database);
            vmd.isLevel3Loaded();
            ViewColumnMetaData vcd = new ViewColumnMetaData(vmd, 1, "myView1",
                    null);
            vcd.getDataType();
            vcd.setScale(1);
            vcd.equals(vcd);

            vmd.getOwner();
            vmd.setLevel3Loaded(true);
            vmd.setLevel3LoadInProgress(true);
            vmd.isLevel3LoadInProgress();
            vmd.setColumns(obj1);
            vcd.setDataTypeSchema("abc");
            vcd.getScale();
            vcd.getArrayNDim();
            vcd.setCheckConstraintExpr("abc");
            vcd.setUnique(true);
            vcd.isLoaded();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_ViewMetaDataTest_023()
    {
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Iterator<Server> servers = connProfCache.getServers().iterator();
            Database db = servers.next().getAllDatabases().iterator().next();
            Namespace ns = db.getNameSpaceById(1);

            ViewMetaData vmd = new ViewMetaData(2, "anything", ns,db);
            ViewMetaData vmd1 = new ViewMetaData(2, "anything", ns,db);
            Object o1 = new Object();
            o1 = vmd;
            vmd.equals(o1);
            vmd.equals(01);
            vmd.equals(null);
            assertTrue(vmd.equals(vmd1));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

/*    @Test
    public void testTTA_BL_ViewColumnList_01()
    {
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Iterator<Server> servers = connProfCache.getServers().iterator();
            Database db = servers.next().getAllDatabases().iterator().next();

            Namespace nm=db.getNameSpaceById(1);
            nm.getAllObjects(db.getObjBrowserConn());

            SortedMap<String, ViewMetaData> views = nm.getViewGroup()
                    .getMatching("myview");
            ViewMetaData viewMeta = views
                    .get("myview - schema_one - View");
            ViewColumnList list = new ViewColumnList(viewMeta);
            ViewColumnList list2 = new ViewColumnList(viewMeta);
            list2.setName("ashish");
            list.setName("ashish");

            Object o1 = new Object();
            o1 = list2;
            list.equals(null);
            list.equals(o1);
            list.equals(new Object());
            list.hashCode();
            assertTrue(list.equals(list2));

        }
        catch (Exception e)
        {
               e.printStackTrace();
            fail(e.getMessage());
        }
    }
*/    @Test
    public void testTTA_BL_HashCode_viewColumnLIst()
    {
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Iterator<Server> servers = connProfCache.getServers().iterator();
            Database db = servers.next().getAllDatabases().iterator().next();
            Namespace ns = db.getNameSpaceById(1);
            ViewColumnList obj1 = new ViewColumnList(ns);

            assertTrue(obj1.hashCode()==obj1.hashCode());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    @Test
    public void testTTA_BL_HashCode_viewColumnMetatada()
    {
        try
        {
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "schema_one");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler,
                    10, 10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(10);
            TypeMetaData type=new TypeMetaData(1, "myView1", namespace);
            ViewMetaData vmd = new ViewMetaData(2, "anything", namespace,database);
            ViewColumnMetaData vcd = new ViewColumnMetaData(vmd, 1, "myView1",
                    type);
            vcd.setLenOrPrecision(2);
            vcd.setScale(2);
            String sclm1 = vcd.getClmNameWithDatatype(true);
            assertEquals(sclm1, vcd.getClmNameWithDatatype(true));
            String sclm2 = vcd.getClmNameWithDatatype(false);
            assertEquals(sclm2, vcd.getClmNameWithDatatype(false));
            assertTrue(vcd.hashCode()==vcd.hashCode());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    @Test
    public void test_to_load_fetchLevel2ViewColumnInfo()
    {

        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "new Schema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 3);
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            nm.fetchLevel2ViewColumnInfo(database.getConnectionManager().getObjBrowserConn());
           
            assertEquals("mytestview2",nm.getViewGroup().get("mytestview2").getName());
        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail(e.getMessage());

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
    public void testViewDDLLazyload()
    {
        try
        {
            CommonLLTUtils.getViewMockRS(preparedstatementHandler);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            ArrayList<ViewMetaData> views = namespace.getViewGroup()
                    .getSortedServerObjectList();

            ViewMetaData vmd = views.get(0);
            assertTrue(vmd.getOid() == 2);
            assertTrue(vmd.getName().equals("mytestview2"));
            assertTrue(vmd.getNamespace().getName().equals("pg_catalog"));
            assertNull(vmd.getSource());
            assertEquals(
                    vmd.getDDL(database),
                    "CREATE OR REPLACE VIEW pg_catalog.mytestview2"+ MPPDBIDEConstants.LINE_SEPARATOR+"\tAS "+MPPDBIDEConstants.LINE_SEPARATOR+"select * from something");
            assertEquals(vmd.getSource(), "select * from something");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_ViewBatchDropOp_001()
    {
        try
        {
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createNamespace(preparedstatementHandler, 10,
                    "newSchema");
            MockViewUtils.createViewColunmMetadata(preparedstatementHandler, 1,
                    10);
            profileId = connProfCache.initConnectionProfile(serverInfo,status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace nm=database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(),status);

            ArrayList<ViewMetaData> views = nm.getViewGroup()
                    .getSortedServerObjectList();
            assertTrue(views.size() == 3);

            ViewMetaData viewObj = views.get(1);
            
            assertEquals(viewObj.isDropAllowed(), true);
            assertEquals("View", viewObj.getObjectTypeName());
            assertEquals("pg_catalog.myview", viewObj.getObjectFullName());
            String dropQry = viewObj.getDropQuery(false);
            assertEquals("DROP VIEW IF EXISTS pg_catalog.myview", dropQry);
            
            dropQry = viewObj.getDropQuery(true);
            assertEquals("DROP VIEW IF EXISTS pg_catalog.myview CASCADE", dropQry);
            
            // Test Remove of View Column
            ViewColumnList list = viewObj.getColumns();
            assertEquals(list.getSize(), 2);

            viewObj.remove(list.getItem(0));
            list = viewObj.getColumns();
            assertEquals(list.getSize(), 1);
            
            // Remove the Object from namespace also
            Namespace ns = database.getNameSpaceById(1);
            
            ViewObjectGroup vog = ns.getViewGroup();
            assertEquals(3, vog.getSize());
            
            ns.remove(viewObj);
            
            vog = ns.getViewGroup();
            assertEquals(2, vog.getSize());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_getObjectType() {
        try {
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);

            MockViewUtils.createNamespace(preparedstatementHandler, 10, "newSchema");
            MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
            MockViewUtils.DropView(preparedstatementHandler, "mytestview1");
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);

            Namespace nm = database.getNameSpaceById(1);
            nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(), status);

            boolean viewObjectGroup = nm.getViewGroup().getObjectType();
            assertTrue(viewObjectGroup);


            boolean tableObjectGroup = nm.getTablesGroup().getObjectType();
            assertTrue(tableObjectGroup);


            boolean foreignTableObjectGroup = nm.getForeignTablesGroup().getObjectType();
            assertTrue(foreignTableObjectGroup);


            boolean sequenceObjectGroup = nm.getSequenceGroup().getObjectType();
            assertTrue(sequenceObjectGroup);


            boolean funcObjectGroup = nm.getFunctions().getObjectType();
            assertTrue(funcObjectGroup);


            SQLException sqlException = new SQLException("57P sql expection", "57P sql expection");
            statementHandler.prepareThrowsSQLException("DROP VIEW \"newSchema\".myview", sqlException);
        } catch (DatabaseCriticalException e) {
            System.out.println("As expected");
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
        @Test
        public void test_getChildren() {
            try {
                CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
                CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
                CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
    
                MockViewUtils.createNamespace(preparedstatementHandler, 10, "newSchema");
                MockViewUtils.createViewMetadata(preparedstatementHandler, 10, 2);
              
                profileId = connProfCache.initConnectionProfile(serverInfo, status);
                Database database = connProfCache.getDbForProfileId(profileId);
                database.getServer().setServerCompatibleToNodeGroup(true);
    
                Namespace nm = database.getNameSpaceById(1);
                nm.getAllObjects(database.getConnectionManager().getObjBrowserConn(), status);
    
                FilterObject.getInstance().setFilterText("");
                ViewObjectGroup viewObjectGroup = nm.getViewGroup();
                Object[] views=viewObjectGroup.getChildren();
                assertEquals(3, views.length);
                assertEquals("Views (3) ", viewObjectGroup.getObjectBrowserLabel());
            } catch (DatabaseCriticalException e) {
                System.out.println("As expected");
            } catch (DatabaseOperationException e) {
                e.printStackTrace();
                fail(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
        
        @Test
        public void testTTA_exportManagerTest_TableDDL() {
            File dir = new File("Test");
            try {
                CommonLLTUtils.getOwnerId(statementHandler);
                CommonLLTUtils.getTableDDL(preparedstatementHandler);
                profileId = connProfCache.initConnectionProfile(serverInfo, status);
                Database database = connProfCache.getDbForProfileId(profileId);
                Namespace namespace = new Namespace(1, "schema", database);
                TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
                tablemetaData.setTempTable(true);
                tablemetaData.setIfExists(true);
                tablemetaData.setName("test");
                tablemetaData.setHasOid(true);
                tablemetaData.setDistributeOptions("HASH");
                tablemetaData.setNodeOptions("Node1");
                tablemetaData.setDescription("Table description");
                ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                        new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
                ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 2, "Col2",
                        new TypeMetaData(1, "double", database.getNameSpaceById(1)));
                tablemetaData.getColumns().addItem(newTempColumn);
                tablemetaData.getColumns().addItem(newTempColumn1);
                Path exportFilePath = Paths.get("Test" + File.separator).toAbsolutePath().normalize();
                boolean fileExists = Files.exists(exportFilePath);
                if (!fileExists) {
                    Files.createDirectory(exportFilePath);
                }
              String query="select * from test";
                           
                GenerateCursorExecuteUtil genrateUtil = new GenerateCursorExecuteUtil(tablemetaData.getName(),
                        "UTF8", true);
                DBConnection freeConnection = database.getConnectionManager().getFreeConnection();
                Path path = Paths.get("234.sql");
                ExportObjectDataManager exportManager = new ExportObjectDataManager(freeConnection, path, "UTF8",
                        query, genrateUtil);
                String uniqCursorName = exportManager.getUniqCursorName();
                MockResultSet getselectrs = statementHandler.createResultSet();
                getselectrs.setResultSetMetaData(new MockedResultSetMetaData());
                getselectrs.addColumn("Col1");
                getselectrs.addColumn("Col2");
                getselectrs.addColumn("Col3");
                getselectrs.addColumn("Col4");
                getselectrs.addColumn("Col5");
                getselectrs.addColumn("Col6");
                getselectrs.addColumn("Col7");
                getselectrs.addRow(new Object[] {2, 2.04, 1.004, "2018-09-18 16:37:06", "2018-09-18 16:37:06",
                    "2018-09-18 16:37:06", true});
                getselectrs.addRow(new Object[] {2});
                statementHandler.prepareResultSet(uniqCursorName, getselectrs);
               
                exportManager.exportTableData();
                genrateUtil.cleanOutputInsertSql();

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
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                fail("Security exception not expected");
            } catch (PasswordExpiryException e) {
                fail("Security exception not expected");
            } finally {
                dir.delete();
            }
        }
        
        @Test
        public void testTTA_cleanTest() {
            File dir = new File("Test");
            try {
                CommonLLTUtils.getOwnerId(statementHandler);
                CommonLLTUtils.getTableDDL(preparedstatementHandler);
                profileId = connProfCache.initConnectionProfile(serverInfo, status);
                Database database = connProfCache.getDbForProfileId(profileId);
                Namespace namespace = new Namespace(1, "schema", database);
                TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
                tablemetaData.setTempTable(true);
                tablemetaData.setIfExists(true);
                tablemetaData.setName("test");
                tablemetaData.setHasOid(true);
                tablemetaData.setDistributeOptions("HASH");
                tablemetaData.setNodeOptions("Node1");
                tablemetaData.setDescription("Table description");
                ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                        new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
                ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 2, "Col2",
                        new TypeMetaData(1, "double", database.getNameSpaceById(1)));
                tablemetaData.getColumns().addItem(newTempColumn);
                tablemetaData.getColumns().addItem(newTempColumn1);
                Path exportFilePath = Paths.get("Test" + File.separator).toAbsolutePath().normalize();
                boolean fileExists = Files.exists(exportFilePath);
                if (!fileExists) {
                    Files.createDirectory(exportFilePath);
                }
              String query="select * from test";
                           
                GenerateCursorExecuteUtil genrateUtil = new GenerateCursorExecuteUtil(tablemetaData.getName(),
                        "UTF8", true);
                DBConnection freeConnection = database.getConnectionManager().getFreeConnection();
                Path path = Paths.get("234.sql");
                ExportObjectDataManager exportManager = new ExportObjectDataManager(freeConnection, path, "UTF8",
                        query, genrateUtil);
                String uniqCursorName = exportManager.getUniqCursorName();
                MockResultSet getselectrs = statementHandler.createResultSet();
                getselectrs.setResultSetMetaData(new MockedResultSetMetaData());
                getselectrs.addColumn("Col1");
                getselectrs.addColumn("Col2");
                getselectrs.addColumn("Col3");
                getselectrs.addColumn("Col4");
                getselectrs.addColumn("Col5");
                getselectrs.addColumn("Col6");
                getselectrs.addColumn("Col7");
                getselectrs.addRow(new Object[] {2, 2.04, 1.004, "2018-09-18 16:37:06", "2018-09-18 16:37:06",
                    "2018-09-18 16:37:06", true});
                getselectrs.addRow(new Object[] {2});
                statementHandler.prepareResultSet(uniqCursorName, getselectrs);
                exportManager.cleanData();
                genrateUtil.cleanOutputInsertSql();

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
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                fail("Security exception not expected");
            } catch (PasswordExpiryException e) {
                fail("Security exception not expected");
            } finally {
                dir.delete();
            }
        }
}
