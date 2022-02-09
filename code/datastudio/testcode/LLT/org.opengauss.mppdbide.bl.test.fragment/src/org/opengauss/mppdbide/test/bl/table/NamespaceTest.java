package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;

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
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.SystemNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.UserNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils.EXCEPTIONENUM;
import org.opengauss.mppdbide.mock.bl.ExceptionConnection;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class NamespaceTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection               = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler         statementHandler         = null;
    
    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache connProfCache = null;
    ConnectionProfileId profileId = null;
    private DBConnection dbconn;
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

        this.dbconn = CommonLLTUtils.getDBConnection();
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();
        
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.prepareProxyInfoForDB(preparedstatementHandler);
        //CommonLLTUtils.prepareProxyInfoForDatabase(preparedstatementHandler);
        CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
        CommonLLTUtils.mockCheckDebugSupport(preparedstatementHandler);
        CommonLLTUtils.mockCheckExplainPlanSupport(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        
        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
       // serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setUsername("myusername");
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
       // serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        //serverInfo.setSslPassword("12345");
        //serverInfo.setServerType(DATABASETYPE.GAUSS);
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
        
        while(itr.hasNext())
        {
            connProfCache.removeServer(itr.next().getId());
        }
        
        connProfCache.closeAllNodes();
        
    }
    //To be fixed 07/12/2016
   /* @Test
    public void testTTA_BL_NAMESPACE_FUNC_001_001_02()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.renameRS(preparedstatementHandler);
            CommonLLTUtils.dropRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            String query = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
                    + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
                    + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
                    + "from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
                    + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
                    + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = "
                    + 1 + " order by t.oid, c.attnum;";
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace nm = database.getNameSpaceById(1);
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
                    "MyConstarint", CONSTRAINT_TYPE.UNIQUE_KEY_CONSTRSINT);

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

            tablemetaData.addIndex(indexMetaData);
            ColumnList columnList = new ColumnList(OBJECTTYPE.OBJECTTYPE_BUTT,
                    tablemetaData);
            columnList.addItemAtIndex(newTempColumn, 1);
            // nm.createTable(query);
            // nm.rename("newName");
            // nm.drop();

        }

        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }*/
    @Test
    public void testTTA_BL_NAMESPACE_FUNC_001_001_01()
    {
    	try
        {
        	CommonLLTUtils.createTableRS(preparedstatementHandler);
        	//CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
        	  CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        	DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace nm = database.getNameSpaceById(1);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            database.getSearchPoolManager().removeTableFromSearchPool(tablemetaData);
            
            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            
            tablemetaData.addConstraint(constraintMetaData);
            
            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1", new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);
            
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1, "Col2", new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);
            
            IndexMetaData indexMetaData = new IndexMetaData("Idx1");
            
            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(nm);
            
            tablemetaData.addIndex(indexMetaData);
            nm.addTableToSearchPool(tablemetaData);
            nm.fetchLevel2ViewColumnInfo(dbconn1);
            //nm.refreshTriggers();
            
           //nm.setLevel2LoadInProgress(true);
            //nm.setLevel2Loaded(true);
         //   nm.isLevel2Loaded();
            
        }
        catch(DatabaseCriticalException e)
        {
           
            assertTrue(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }  
    }
    @Test
    public void testTTA_BL_NAMESPACE_FUNC_001_001_0() {
    try {
    CommonLLTUtils.createTableRS(preparedstatementHandler);
    //CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
    CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
    Database database = connProfCache.getDbForProfileId(profileId);
    Namespace nm = database.getNameSpaceById(1);
    TableMetaData table = new TableMetaData(1, "tbl100", nm, null);
    ColumnMetaData newTempColumn = new ColumnMetaData(table, 1, "Col1",
    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
    ColumnMetaData newTempColumn1 = new ColumnMetaData(table, 1,
    "Col2", new TypeMetaData(1, "bigint",
    database.getNameSpaceById(1)));

    table.addColumn(newTempColumn);
    table.addColumn(newTempColumn1);
    nm.addTableToSearchPool(table);
    nm.fetchLevel2ViewColumnInfo(dbconn);
    assertEquals("\"Col1\"", nm.getTables().getObjectById(1)
    .getColumnMetaDataList().get(0).getQualifiedObjectName());
    assertEquals("\"Col2\"", nm.getTables().getObjectById(1)
    .getColumnMetaDataList().get(1).getQualifiedObjectName());
    } catch (DatabaseCriticalException e) {

    fail(e.getMessage());
    } catch (Exception e) {

    fail(e.getMessage());
    }
    }

    // test case to hit getter&setters
    @Test
    public void testTTA_BL_NAMESPACE_FUNC_001_001() {
           try {
                  CommonLLTUtils.createTableRS(preparedstatementHandler);
                  CommonLLTUtils.fetchNamespaceRS(preparedstatementHandler);
                  Database database = connProfCache.getDbForProfileId(profileId);
                  CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
                  CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
                  CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
                  Namespace namespace = database.getNameSpaceById(3);
                  database.getAllNameSpaces().toString();
                  ((UserNamespace)namespace).rename("newName", dbconn);
                  ((UserNamespace)namespace).drop(dbconn);
                  namespace.getTypeByName("name");
                  assertEquals(false, ((UserNamespace)namespace).isDrop());
                  namespace.setLoadingInProgress();
                  assertTrue(namespace.isLoadingInProgress());
                  assertEquals(false, namespace.isLoaded());
                  namespace.isNotLoaded();
                  assertEquals(false, namespace.isNotLoaded());
                  namespace.isLoadFailed();
                  assertEquals(false, namespace.isLoadFailed());
                  assertEquals(false, database.isLoadingNamespaceInProgress());

           } catch (Exception e) {

                  fail(e.getMessage());
           }
}

    
    //TODO
    /*@Test
    public void testTTA_BL_NAMESPACE_FUNC_001_001_1()
    {
        try
        {
        	Namespace nm=new Namespace();
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.fetchNamespaceRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            Namespace namespace =database.getNameSpaceById(1);
            database.getAllNameSpaces().toString();
            namespace.refreshDebugObjectGroup(OBJECTTYPE.FUNCTION_GROUP);
            //namespace.refreshDebugObjectGroup(OBJECTTYPE.TRIGGER_GROUP);
            namespace.refreshDebugObjectGroup(OBJECTTYPE.OBJECTTYPE_BUTT);
            namespace.createTable("create table tbl123(a int);");
            namespace.rename("newName");
            namespace.drop();
            namespace.getTypeByName("name");
            namespace.refreshFunctions();
            //namespace.refreshTriggers();
            //namespace.getTriggers();
            namespace.getTables();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }*/
    
    @Test
    public void test_namespace_getTables()
    {
    	 Database database = connProfCache.getDbForProfileId(profileId);
    	  try {
			Namespace namespace =database.getNameSpaceById(1);
			TableObjectGroup tableObjectGroup= namespace.getTablesGroup();
			SortedMap< String , TableMetaData> map=tableObjectGroup.getMatching(" ");
			for (Entry<String, TableMetaData> entry: map.entrySet()){
			   System.out.println(entry.getKey()+"---"+entry.getValue());
			    
			
			}
			//assertEquals(tableObjectGroup, namespace.getTablesGroup());
			
		} catch (DatabaseOperationException e) {
			fail("not expected here");
		}
    }
    
    @Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_001()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
           // serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server=new Server(serverInfo);
            Database database = new Database(server, 2,"Gauss");
            
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.connectToServer();            
            
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            
            namespace.refreshTableHirarchy(dbconn1);
            
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
           
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_002()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
           // serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server=new Server(serverInfo);
            Database database = new Database(server, 2,"Gauss");
            
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.connectToServer();            
            
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            
            namespace.refreshTableHirarchy(dbconn1);
            
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_002_1()
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
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server=new Server(serverInfo);
            Database database = new Database(server, 2,"Gauss");
            
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(false);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);
            exceptionConnection.setThrowExceptionGetString(true);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.connectToServer();            
            
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            
            namespace.refreshTableHirarchy(dbconn1);
            
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
           assertTrue(true);
        }
        catch (Exception e)
        {
           
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_003()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
           // serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server=new Server(serverInfo);
            Database database = new Database(server, 2,"Gauss");
            
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.connectToServer();            
            
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            TableMetaData tbl = new TableMetaData(1, "tbl100", namespace, null);
            namespace.fetchConstraintForTable(tbl,namespace.getTablesGroup(), dbconn1);
            
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (Exception e)
        {
           
            fail(e.getMessage());
        }
    }
    
	 @Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_013()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
           // serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server=new Server(serverInfo);
            Database database = new Database(server, 2,"Gauss");
            
            
           /* ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection); */  
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.connectToServer();            
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            //TableMetaData tablemetaData = new TableMetaData(1, "tbl100", namespace, null);
            
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            
            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            
            tablemetaData.addConstraint(constraintMetaData);
            
            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1", new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);
            
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1, "Col2", new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);
            
            IndexMetaData indexMetaData = new IndexMetaData(1,"Idx1");
            
            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);
            namespace.fetchConstraintForTable(tablemetaData,namespace.getTablesGroup(), dbconn1);
            
            
            
        }
        catch(DatabaseOperationException e)
        {
           
            fail("not excepted to come here");
        }
        catch (Exception e)
        {
          
            fail(e.getMessage());
        }
    }
    @Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_113()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
           // serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server=new Server(serverInfo);
            Database database = new Database(server, 2,"Gauss");
            
            
           /* ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection); */  
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.connectToServer();            
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);

            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            //TableMetaData tablemetaData = new TableMetaData(1, "tbl100", namespace, null);
            
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            
            ConstraintMetaData constraintMetaData = new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            
            tablemetaData.addConstraint(constraintMetaData);
            
            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1", new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);
            
            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1, "Col2", new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);
            
            IndexMetaData indexMetaData = new IndexMetaData("Idx1");
            
            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);
            namespace.getTablesGroup().addToGroup(tablemetaData);
            namespace.fetchConstraintForTable(tablemetaData,namespace.getTablesGroup(), dbconn1);
            assertEquals("MyConstarint", namespace.getTablesGroup().getObjectById(1).getConstraints().getItem(0).getName());
           //System.out.println(namespace.getTablesGroup().getObjectById(1).getConstraints().getItem(1).getName());
          

            
        }
        catch(DatabaseOperationException e)
        {
            fail("not excepted to come here");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_004()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
           // serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server=new Server(serverInfo);
            Database database = new Database(server, 2,"Gauss");
            
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            CommonLLTUtils.mockServerEncoding(exceptionConnection.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckDebugSupport(exceptionConnection.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckExplainPlanSupport(exceptionConnection.getPreparedStatementResultSetHandler());
            database.connectToServer();            
            
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup(namespace);
            TableMetaData tableMetaData =new TableMetaData(1,"sampleTable", namespace, null);
            namespace.addTableToSearchPool(tableMetaData);
            
            namespace.getTablesGroup().getObjectById(1).fetchIndexForTable(database.getConnectionManager().getFreeConnection());
            
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
            assertEquals("Result set fetch invalid.", e.getMessage());
        }
        catch (Exception e)
        {
            // Lazy loding checks
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_005()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
           // serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server=new Server(serverInfo);
            Database database = new Database(server, 2,"Gauss");
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.connectToServer();            
            
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            
            namespace.refreshAllTableMetadataInNamespace(dbconn1);
            
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
           assertTrue(true);
        }
        catch (Exception e)
        {
          
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_006()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
           // serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server=new Server(serverInfo);
            Database database = new Database(server, 2,"Gauss");
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.connectToServer();            
            
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            
            namespace.refreshAllTableMetadataInNamespace(dbconn1);
            
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
           assertTrue(true);
        }
        catch (Exception e)
        {
          
            fail(e.getMessage());
        }
    }

    //Lazy loding checks
    /*@Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_008()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPassword("mypassword");
            Database database = new Database(serverInfo, null, 2);
            
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            
            database.connectToServer();            
            
            Namespace namespace = new Namespace(1, "pg_catalog", database);
            database.addNamespace(namespace);
            
            namespace.getAllFunctions();
            
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    */
  /*  @Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_009()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPassword("mypassword");
            Database database = new Database(serverInfo, null, 2);
            
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            
            database.connectToServer();            
            
            Namespace namespace = new Namespace(1, "pg_catalog", database);
            database.addNamespace(namespace);
            
            namespace.getAllTriggers();
            
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
            System.out.println("as expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }*/
    
    @Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_010()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
           // serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server=new Server(serverInfo);
            Database database = new Database(server, 2,"Gauss");
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            CommonLLTUtils.mockServerEncoding(exceptionConnection.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckDebugSupport(exceptionConnection.getPreparedStatementResultSetHandler());
            CommonLLTUtils.mockCheckExplainPlanSupport(exceptionConnection.getPreparedStatementResultSetHandler());
            database.connectToServer();            
            
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            assertEquals("pg_catalog", database.getNameSpaceById(1).getDisplayName());
            
            //Namespace.getVariables("{1,2,3}", "{name1,name2,name3}", null, false);
        }
        catch (Exception e)
        {
           
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_NAMESPACE_EXCEPTIONS_FUNC_001_011()
    {
        try
        {
        	DebugObjects obj=new DebugObjects();
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
           // serverInfo.setDriverName("FusionInsight LibrA");
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            serverInfo.setPrivilegeBasedObAccess(true);
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server=new Server(serverInfo);
            Database database = new Database(server, 2,"Gauss");
            
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionGetInt(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);
            
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);   
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.connectToServer();            
            
            SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            DebugObjects debugObject = new DebugObjects(1, "name", OBJECTTYPE.PLSQLFUNCTION, database);
            debugObject.setNamespace(namespace);
            namespace.refreshDebugObject(1, dbconn1);
            fail("Not expected to come here");
        }
        catch(DatabaseOperationException e)
        {
           assertTrue(true);
        }
        catch (Exception e)
        {
            
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testTTA_BL_NAMESPACE_RENAMETABLE() {
           try {
                  CommonLLTUtils.createTableRS(preparedstatementHandler);
                  CommonLLTUtils.fetchNamespaceRS(preparedstatementHandler);
                  Database database = connProfCache.getDbForProfileId(profileId);
                  CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
                  CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
                  CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
                
                  DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
                  SystemNamespace namespace = new SystemNamespace(1, "pg_catalog", database);
                  database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
                  
                  TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
                  tablemetaData.setTempTable(true);
                  tablemetaData.setIfExists(true);
                  tablemetaData.setName("MyTable");
                  tablemetaData.setHasOid(true);
                  tablemetaData.setDistributeOptions("HASH");
                  tablemetaData.setNodeOptions("Node1");
                  tablemetaData.setDescription("Table description");
                  ConstraintMetaData constraintMetaData = new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);
                  
                  tablemetaData.addConstraint(constraintMetaData);
                  
                  ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1", new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
                  tablemetaData.getColumns().addItem(newTempColumn);
                  
                  ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1, "Col2", new TypeMetaData(1, "text", database.getNameSpaceById(1)));
                  tablemetaData.getColumns().addItem(newTempColumn1);
                  
                  IndexMetaData indexMetaData = new IndexMetaData("Idx1");
                  
                  indexMetaData.setTable(tablemetaData);
                  indexMetaData.setNamespace(tablemetaData.getNamespace());
                  tablemetaData.addIndex(indexMetaData);
                  namespace.getTablesGroup().addToGroup(tablemetaData);
                  namespace.fetchConstraintForTable(tablemetaData,namespace.getTablesGroup(), dbconn1);
                  namespace.execRenameTable(tablemetaData, "MyTable", dbconn);
                  namespace.setLoadFailed();
                  namespace.setNotLoaded();
                  assertEquals("MyTable", tablemetaData.getName());
                  namespace.getConnectionManager().releaseConnection(dbconn);
                  
                  SynonymObjectGroup synGroup=new SynonymObjectGroup(OBJECTTYPE.SYNONYM_GROUP,namespace);
                  assertNotNull(synGroup.getDatabase());
                  assertNotNull(synGroup.hashCode());
                  assertNotNull(synGroup.getParent());
                  assertTrue(synGroup.equals(namespace.getSynonymGroup()));

           } catch (Exception e) {

                  fail(e.getMessage());
           }
}


}
