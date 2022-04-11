package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.RefreshCounter;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.Tablespace;
import org.opengauss.mppdbide.bl.serverdatacache.TablespaceProperties;
import org.opengauss.mppdbide.bl.serverdatacache.TablespaceType;
import org.opengauss.mppdbide.bl.serverdatacache.groups.TablespaceObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.mock.bl.ProfileDiskUtilityHelper;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class TablespaceTest extends BasicJDBCTestCaseAdapter
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
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        ProfileDiskUtilityHelper profile=new ProfileDiskUtilityHelper();
        profile.setOption(4);
        ConnectionProfileManagerImpl.getInstance().setDiskUtility(profile);
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
    public void test_createTablespaceQuery_Genaral()
    {
        try
        {
            TablespaceProperties prop = new TablespaceProperties("Shalini", "/home/dsdev/shalini", "10K", "Genral",
                    "1.0", "2.0", true);
            Database database = connProfCache.getDbForProfileId(profileId);

            TablespaceObjectGroup group = new TablespaceObjectGroup(
                    OBJECTTYPE.TABLESPACE_GROUP, database.getServer());
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("Gauss");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            TablespaceObjectGroup group2 = new TablespaceObjectGroup(
                    OBJECTTYPE.TABLE_GROUP, server);
            TablespaceObjectGroup group3 = new TablespaceObjectGroup(
                    OBJECTTYPE.TABLESPACE_GROUP, database.getServer());
            group.equals(group);
            group.equals(group2);
            group.equals(group3);
            TablespaceObjectGroup group1 = null;
            assertFalse(group.equals(group1));
            assertFalse(group.equals(serverInfo));
            group.hashCode();
            prop.buildQuery();
            String qry = "CREATE TABLESPACE \"Shalini\" LOCATION '/home/dsdev/shalini' MAXSIZE '10K' WITH ( filesystem =Genral, random_page_cost=2.0, seq_page_cost=1.0 );";
            // assertTrue(qry.equalsIgnoreCase());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_createTablespace_Failure_01()
    {
        int initial_value = 0;
        int final_val = 0;
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
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            statementHandler.prepareThrowsSQLException(
                    "CREATE TABLESPACE Shalini LOCATION '/home/dsdev/shalini' MAXSIZE '10K' WITH ( filesystem =Genral, random_page_cost=2.0, seq_page_cost=1.0);",
                    sqlException);

            TablespaceProperties prop = new TablespaceProperties("Shalini", "/home/dsdev/shalini", "10K", "Genral",
                    "1.0", "2.0", true);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value + 1;
            server.createTablespace(prop.buildQuery());
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() != final_val);
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
    public void test_createTablespace_Failure_03()
    {
        int initial_value = 0;
        int final_val = 0;
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
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            TablespaceProperties prop = new TablespaceProperties("Shalini", "/home/dsdev/shalini", "10K", "Genral",
                    "1.0", "2.0", true);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value + 1;
            database.destroy();
            server.createTablespace(prop.buildQuery());
        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() != final_val);
            System.out.println("As expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_createTablespace_Success()
    {
        int initial_value = 0;
        int final_val = 0;
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
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            TablespaceProperties prop = new TablespaceProperties("Shalini", "/home/dsdev/shalini", "10K", "Genral",
                    "1.0", "2.0", true);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value + 1;
            server.createTablespace(prop.buildQuery());
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() == final_val);
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
    public void test_createTablespace_Success_01()
    {
        try
        {
            ServerConnectionInfo connectionInfo = new ServerConnectionInfo();
            connectionInfo.setConectionName("connection_name1");
            connectionInfo.setServerIp("");
            connectionInfo.setServerPort(5432);
            connectionInfo.setDatabaseName("Gauss");
            connectionInfo.setUsername("myusername");
            connectionInfo.setPrd("mypassword".toCharArray());
            connectionInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            connectionInfo.setSavePrdOption(2, true);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(connectionInfo);
            Server server = new Server(connectionInfo);
            CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(10, "Table1",
                    database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            server.setHost("abc");
            server.getHost();
            server.setPort(9000);
            server.getPort();
            server.getDbByName("tempdb");
            server.setSavePrdOption(1);
            server.getSavePrdOption();
            database.fetchTablespaceMetaData(10);
            database.getServer().addDBtoList(database);

            database.fetchAllTablespace();

            TablespaceProperties prop = new TablespaceProperties("Shalini", "/home/dsdev/shalini", "10K", "Genral",
                    "1.0", "2.0", true);
            database.getServer().createTablespace(prop.buildQuery());
            database.fetchTablespaceMetaData(10);
            Tablespace tb = database.getServer().getTablespaceGroup()
                    .get("tblspc");
            tb.refresh();
            assertTrue(database.getServer().getName()
                    .equalsIgnoreCase(tb.getServer().getName()));
            assertTrue(tb.getName().equalsIgnoreCase("tblspc"));
            System.out.println(
                    "TablespaceTest.test_createTablespace_Success_01():\n"
                            + tb.getFileOption());
            // assertTrue(tb.getFileOption().equals("filesystem=general,random_page_cost=2,seq_page_cost=2,address
            // ='address'"));;
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
    public void test_setTablespaceOptionQuery()
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
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            TablespaceProperties prop = new TablespaceProperties("Shalini",
                    "1.0", "3.0");
            String query = "ALTER TABLESPACE Shalini SET ( random_page_cost=3.0 );ALTER TABLESPACE Shalini SET ( seq_page_cost=1.0 );";
            assertTrue(query.equalsIgnoreCase(prop.buildSetOptionQry()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_setTablespaceOption_Success()
    {
        int initial_value = 0;
        int final_val = 0;
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
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);
            TablespaceProperties prop = new TablespaceProperties("Shalini",
                    "1.0", "3.0");
            Tablespace tablespace = new Tablespace(1, "Shalini",
                    "/home/dsdev/shalini", "10K", null, server,
                    TablespaceType.NORMAL,true, false);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value + 1;
            tablespace.setTablespaceOption(prop.buildSetOptionQry(),
                    database.getConnectionManager().getFreeConnection());
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() == final_val);
            TablespaceProperties prop1 = new TablespaceProperties("Shalini", "",
                    "");
            tablespace.setTablespaceOption(prop1.buildSetOptionQry(),
                    database.getConnectionManager().getFreeConnection());
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
    public void test_setTablespaceOption_Failure01()
    {
        int initial_value = 0;
        int final_val = 0;
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
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            statementHandler.prepareThrowsSQLException(
                    "ALTER TABLESPACE Shalini SET ( random_page_cost=3.0 );ALTER TABLESPACE Shalini SET ( seq_page_cost=1.0 );",
                    sqlException);

            TablespaceProperties prop = new TablespaceProperties("Shalini",
                    "1.0", "3.0");
            Tablespace tablespace = new Tablespace(1, "Shalini",
                    "/home/dsdev/shalini", "10K", null, server,
                    TablespaceType.NORMAL,true, false);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value + 1;
            tablespace.setTablespaceOption(prop.buildSetOptionQry(),
                    database.getConnectionManager().getFreeConnection());
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() == final_val);
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() != final_val);
            System.out.println("As Expected");
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
    public void test_setTablespaceOption_Failure02()
    {
        int initial_value = 0;
        int final_val = 0;
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
          //serverInfo.setDriverName("FusionInsight LibrA");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            statementHandler.prepareThrowsSQLException(
                    "ALTER TABLESPACE Shalini SET ( random_page_cost=3.0 );ALTER TABLESPACE Shalini SET ( seq_page_cost=1.0 );");

            TablespaceProperties prop = new TablespaceProperties("Shalini",
                    "1.0", "3.0");
            Tablespace tablespace = new Tablespace(1, "Shalini",
                    "/home/dsdev/shalini", "10K", null, server,
                    TablespaceType.NORMAL,true, false);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value + 1;
            tablespace.setTablespaceOption(prop.buildSetOptionQry(),
                    database.getConnectionManager().getFreeConnection());
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() == final_val);
        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() != final_val);
            System.out.println("As Expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_setTablespaceOption_Failure03()
    {
        int initial_value = 0;
        int final_val = 0;
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
           serverInfo.setDriverName("FusionInsight LibrA");
           ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            preparedstatementHandler.prepareThrowsSQLException(
                    "ALTER TABLESPACE Shalini SET ( random_page_cost=3.0 );ALTER TABLESPACE Shalini SET ( seq_page_cost=1.0 );");

            TablespaceProperties prop = new TablespaceProperties("Shalini",
                    "1.0", "3.0");
            Tablespace tablespace = new Tablespace(1, "Shalini",
                    "/home/dsdev/shalini", "10K", null, server,
                    TablespaceType.NORMAL,true, false);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value + 1;
            database.destroy();
            tablespace.setTablespaceOption(prop.buildSetOptionQry(),
                    database.getConnectionManager().getFreeConnection());
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() == final_val);
        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() != final_val);
            System.out.println("As Expected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_tablespaceResizeQuery()
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
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            Tablespace tablespace = new Tablespace(1, "Shalini",
                    "/home/dsdev/shalini", "10K", null, server,
                    TablespaceType.NORMAL,true, false);
            String query = "ALTER TABLESPACE \"Shalini\" RESIZE MAXSIZE '12K'";
            assertTrue(query.equalsIgnoreCase(tablespace.resizeQuery("12K")));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void test_resize_Success() throws MPPDBIDEException
    {
        Database db = connProfCache.getDbForProfileId(profileId);
        Server server = db.getServer();
        Tablespace tablespace = new Tablespace(1, "tablespace",
                "/home/dsdev/shalini", "10K", null, server,
                TablespaceType.NORMAL,true, false);
        int initial_value = RefreshCounter.getInstance().getCountValue();
        int final_val = initial_value + 1;
        try
        {
            tablespace.setTablespaceSize("12K", db.getConnectionManager().getFreeConnection());
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() == final_val);
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseCriticalException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getDDL()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("db");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 1, "db");

            server.addDBtoList(database);
            server.getDbByName("db");
            Tablespace tablespace = new Tablespace(1, "Shalini",
                    "/home/dsdev/shalini", "10K",
                    new String[] {"filesystem=general"}, server,
                    TablespaceType.NORMAL,true, false);
            String ddl = tablespace.getDDL();

            System.out.println("TablespaceTest.test_getDDL(): \n" + ddl);

            assertTrue(ddl.equalsIgnoreCase(
                    "set enable_absolute_tablespace=on ;CREATE TABLESPACE \"Shalini\" LOCATION '/home/dsdev/shalini' MAXSIZE '10K' WITH ( filesystem=General ) ;"));
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
    public void test_getDDL_relative()
    {
        try
        {
            ServerConnectionInfo serverInfo = new ServerConnectionInfo();
            serverInfo.setConectionName("TestConnectionName");
            serverInfo.setServerIp("");
            serverInfo.setServerPort(5432);
            serverInfo.setDatabaseName("db");
            serverInfo.setUsername("myusername");
            serverInfo.setPrd("mypassword".toCharArray());
            serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 1, "db");

            server.addDBtoList(database);
            server.getDbByName("db");
            Tablespace tablespace = new Tablespace(1, "Deepthi",
                    "/home/dsdev/deepthi", "10K",
                    new String[] {"filesystem=general"}, server,
                    TablespaceType.NORMAL,true, true);
            String ddl = tablespace.getDDL();

            System.out.println("TablespaceTest.test_getDDL(): \n" + ddl);

            assertTrue(ddl.equalsIgnoreCase(
                    "set enable_absolute_tablespace=off ;CREATE TABLESPACE \"Deepthi\" RELATIVE LOCATION '/home/dsdev/deepthi' MAXSIZE '10K' WITH ( filesystem=General ) ;"));
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
    public void test_renameTablespace_Success() throws MPPDBIDEException
    {
        Database db = connProfCache.getDbForProfileId(profileId);
        Server server = db.getServer();
        Tablespace tablespace = new Tablespace(1, "tablespace",
                "/home/dsdev/shalini", "10K", null, server,
                TablespaceType.NORMAL,true, false);
        int initial_value = RefreshCounter.getInstance().getCountValue();
        int final_val = initial_value + 1;
        try
        {
            tablespace.renameTablespace("tablespace1", db.getConnectionManager().getFreeConnection());
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() == final_val);
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseCriticalException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_renameTablespace_Failure_01()
    {
        int initial_value = 0;
        int final_val = 0;
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
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            preparedstatementHandler.prepareThrowsSQLException(
                    "ALTER TABLESPACE \"Shalini\" RENAME TO \"Shalini1\" ;",
                    sqlException);

            Tablespace tablespace = new Tablespace(1, "Shalini",
                    "/home/dsdev/shalini", "10K", null, server,
                    TablespaceType.NORMAL,true, false);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value + 1;
            tablespace.renameTablespace("Shalini1",
                    database.getConnectionManager().getFreeConnection());
            fail("Not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() != final_val);
            System.out.println("As Expected");
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
    public void test_renameTablespace_Failure_02()
    {
        int initial_value = 0;
        int final_val = 0;
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
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            preparedstatementHandler.prepareThrowsSQLException(
                    "ALTER TABLESPACE \"Shalini\" RENAME TO \"Shalini1\" ;");

            Tablespace tablespace = new Tablespace(1, "Shalini",
                    "/home/dsdev/shalini", "10K", null, server,
                    TablespaceType.NORMAL,true, false);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value + 1;
            tablespace.renameTablespace("Shalini1",
                    database.getConnectionManager().getFreeConnection());
            fail("Not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() != final_val);
            System.out.println("As Expected");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_dropTablespace_Success() throws MPPDBIDEException
    {
        Database db = connProfCache.getDbForProfileId(profileId);
        Server server = db.getServer();

        Tablespace tablespace = new Tablespace(1, "tablespace",
                "/home/dsdev/shalini", "10K", null, server,
                TablespaceType.NORMAL,true, false);
        int initial_value = RefreshCounter.getInstance().getCountValue();
        int final_val = initial_value + 1;

        try
        {
            tablespace.dropTablespace(db.getConnectionManager().getFreeConnection());
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() == final_val);
            System.out.println("As expected...");

        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseCriticalException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_dropTablespace_Failure_01()
    {
        int initial_value = 0;
        int final_val = 0;
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
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            SQLException sqlException = new SQLException("57P sql expection",
                    "57P sql expection");
            preparedstatementHandler.prepareThrowsSQLException(
                    "DROP TABLESPACE IF EXISTS \"Shalini\"", sqlException);

            Tablespace tablespace = new Tablespace(1, "Shalini",
                    "/home/dsdev/shalini", "10K", null, server,
                    TablespaceType.NORMAL,true, false);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value + 1;
            tablespace.dropTablespace(database.getConnectionManager().getFreeConnection());
            fail("Not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() != final_val);
            System.out.println("As Expected");
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
    public void test_dropTablespace_Failure_02()
    {
        int initial_value = 0;
        int final_val = 0;
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
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            preparedstatementHandler.prepareThrowsSQLException(
                    "DROP TABLESPACE IF EXISTS \"Shalini\"");

            Tablespace tablespace = new Tablespace(1, "Shalini",
                    "/home/dsdev/shalini", "10K", null, server,
                    TablespaceType.NORMAL,true, false);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value + 1;
            tablespace.dropTablespace(database.getConnectionManager().getFreeConnection());
            fail("Not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() != final_val);
            System.out.println("As Expected");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_createDropQueryTablespace_Success()
            throws MPPDBIDEException
    {

        Database db = connProfCache.getDbForProfileId(profileId);
        Server server = db.getServer();

        Tablespace tablespace = new Tablespace(1, "tablespace", null, null,
                new String[0], server, TablespaceType.NORMAL,true, false);
        String qry1 = "DROP TABLESPACE IF EXISTS " + "tablespace";

        assertEquals(tablespace.createDropQuery(), qry1);
    }

    @Test
    public void test_createRenameQueryTablespace_Success()
            throws MPPDBIDEException
    {

        Database db = connProfCache.getDbForProfileId(profileId);
        Server server = db.getServer();

        Tablespace tablespace = new Tablespace(1, "tablespace", null, null,
                new String[0], server, TablespaceType.NORMAL,true, false);
        String qry1 = "ALTER TABLESPACE " + "tablespace" + " RENAME TO "
                + "tablespace1 ;";

        assertEquals(tablespace.createRenameQuery("tablespace1"), qry1);
    }

    @Test
    public void test_SettablespaceSizeSuccess() throws MPPDBIDEException
    {
        Database db = connProfCache.getDbForProfileId(profileId);
        Server server = db.getServer();

        Tablespace tablespace = new Tablespace(1, "tablespace",
                "/home/dsdev/shalini", "10K", new String[0], server,
                TablespaceType.NORMAL,true, false);
        TablespaceObjectGroup tablespaceObjectGroup = new TablespaceObjectGroup(
                OBJECTTYPE.TABLESPACE_GROUP, server);
        tablespaceObjectGroup.getServer();
        
        int initial_value = RefreshCounter.getInstance().getCountValue();
        int final_val = initial_value + 1;

        try
        {
            tablespace.setTablespaceSize("30K", db.getConnectionManager().getFreeConnection());
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() == final_val);
            System.out.println("As expected...");

        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseCriticalException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_setTablespaceSize_failure() throws MPPDBIDEException
    {
        Database db = connProfCache.getDbForProfileId(profileId);
        Server server = db.getServer();

        Tablespace tablespace = new Tablespace(1, "tablespace",
                "/home/dsdev/shalini", "10K", new String[0], server,
                TablespaceType.NORMAL,true, false);
        int initial_value = RefreshCounter.getInstance().getCountValue();
        int final_val = initial_value + 1;
        SQLException sqlException = new SQLException("57P sql expection",
                "57P sql expection");
        preparedstatementHandler.prepareThrowsSQLException(
                "ALTER TABLESPACE tablespace RESIZE MAXSIZE '30k'",
                sqlException);
        try
        {
            tablespace.setTablespaceSize("30K", db.getConnectionManager().getFreeConnection());
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (DatabaseCriticalException e)
        {
            System.out.println("As expected");
        }
    }

    @Test
    public void test_setTablespaceSize_failure_01() throws MPPDBIDEException
    {
        int initial_value = 0;
        int final_val = 0;
        try
        {
            Database db = connProfCache.getDbForProfileId(profileId);
            Server server = db.getServer();
            Namespace ns = db.getNameSpaceById(1);
            ns.getServer();
            ns.belongsTo(db, server);
            // SQLException sqlException = new SQLException("57P sql expection",
            // "57P sql expection");
            preparedstatementHandler.prepareThrowsSQLException(
                    "ALTER TABLESPACE tablespace RESIZE MAXSIZE '30k'");

            Tablespace tablespace = new Tablespace(1, "tablespace",
                    "/home/dsdev/shalini", "10K", new String[0], server,
                    TablespaceType.NORMAL,true, false);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value++;

            tablespace.setTablespaceSize("30k", db.getConnectionManager().getFreeConnection());
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("As expected");
        }
        catch (DatabaseCriticalException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_createTablespaceproperties_Genaral()
    {
        TablespaceProperties prop = new TablespaceProperties("Shalini", "/home/dsdev/shalini", "10K", "Genral", "1.0",
                "2.0", true);
        prop.getLocation();
        prop.setName("xyz");
        prop.getName();
        prop.setLocation("abc");
        prop.setTsMaxsize("10");
        prop.getTsMaxsize();
        prop.setFileOption("HDFS");
        prop.getFileOption();
        String pgCost = prop.getSeqPageCost();
        prop.setRandomPageCost(prop.getRandomPageCost());
        prop.setSeqPageCost(pgCost);
        prop.setRelativePath(prop.isRelativePath());
        prop.setServer(prop.getServer());
        assertTrue(null != prop);
    }
    
    @Test
    public void test_createTablespace_Success_having_Connection()
    {
        int initial_value = 0;
        int final_val = 0;
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
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);

            Database database = connProfCache.getDbForProfileId(profileId);

            server.addDBtoList(database);

            TablespaceProperties prop = new TablespaceProperties("Shalini", "/home/dsdev/shalini", "10K", "Genral",
                    "1.0", "2.0", true);
            initial_value = RefreshCounter.getInstance().getCountValue();
            final_val = initial_value + 1;
            server.setServerConnectionInfo(serverInfo);
            server.createTablespace(prop.buildQuery(),database.getConnectionManager().getObjBrowserConn());
            assertTrue(
                    RefreshCounter.getInstance().getCountValue() == final_val);
            //user role management feature add a new child 'users/roles' for Server, so change 2 to 3 
            assertEquals(3, server.getChildren().length);
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
}
