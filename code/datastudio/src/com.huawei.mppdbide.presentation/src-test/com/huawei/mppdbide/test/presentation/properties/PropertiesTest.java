package com.huawei.mppdbide.test.presentation.properties;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintType;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.PartitionMetaData;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.SystemNamespace;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TypeMetaData;
import com.huawei.mppdbide.bl.serverdatacache.UserRole;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.mock.presentation.CommonLLTUtils;
import com.huawei.mppdbide.mock.presentation.CommonLLTUtils.EXCEPTIONENUM;
import com.huawei.mppdbide.mock.presentation.ExceptionConnection;
import com.huawei.mppdbide.mock.presentation.MockUserRoleManagerUtils;
import com.huawei.mppdbide.presentation.PropertyOperationType;
import com.huawei.mppdbide.presentation.edittabledata.CommitStatus;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataRow;
import com.huawei.mppdbide.presentation.objectproperties.IObjectPropertyData;
import com.huawei.mppdbide.presentation.objectproperties.IServerObjectProperties;
import com.huawei.mppdbide.presentation.objectproperties.ObjectPropColumnTabInfo;
import com.huawei.mppdbide.presentation.objectproperties.PropertiesDatabaseImpl;
import com.huawei.mppdbide.presentation.objectproperties.PropertiesPartitionTableImpl;
import com.huawei.mppdbide.presentation.objectproperties.PropertiesTableImpl;
import com.huawei.mppdbide.presentation.objectproperties.factory.ServerFactory;
import com.huawei.mppdbide.presentation.objectproperties.handler.IPropertyDetail;
import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import com.huawei.mppdbide.test.presentation.table.MockPresentationBLPreferenceImpl;
import com.huawei.mppdbide.test.presentation.table.ServerVersionTest;
import com.huawei.mppdbide.utils.JSQLParserUtils;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.exceptions.PasswordExpiryException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import static org.junit.Assert.*;

public class PropertiesTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    ServerConnectionInfo              serverInfo;
    JobCancelStatus                   status;
    public static final String        serverip                  = "127.0.0.1";
    private StringBuilder             builder;

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
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.columnComments(preparedstatementHandler);
        CommonLLTUtils.getPartitionData(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
        CommonLLTUtils.getPropertiesConstraint(preparedstatementHandler);
        CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();

        serverInfo = new ServerConnectionInfo();
        status = new JobCancelStatus();
        status.setCancel(false);
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);

        // Database database = connProfCache.getDbForProfileId(profileId);
        // database.getServer().setServerCompatibleToNodeGroup(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
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
        if (database != null)
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
    public void testgetServerfactoryObject()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        ServerFactory factory = new ServerFactory();
        Database database = connProfCache.getDbForProfileId(profileId);
        IServerObjectProperties iServerObject1 = factory.getObject(null, null);
        assertTrue(iServerObject1 == null);
        IServerObjectProperties iServerObject = factory.getObject(database, PropertyOperationType.PROPERTY_OPERATION_VIEW);
        assertTrue(iServerObject instanceof PropertiesDatabaseImpl);
        TableMetaData tablemetaData = null;
        PartitionTable partTable = null;
        try
        {
            tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            partTable = new PartitionTable(database.getNameSpaceById(1));
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
        }
        IServerObjectProperties iServerObject3 = factory.getObject(tablemetaData, PropertyOperationType.PROPERTY_OPERATION_VIEW);
        assertTrue(iServerObject3 instanceof PropertiesTableImpl);
        IServerObjectProperties iServerObject4 = factory.getObject(partTable, PropertyOperationType.PROPERTY_OPERATION_VIEW);
        assertTrue(iServerObject4 instanceof PropertiesPartitionTableImpl);
        IServerObjectProperties iServerObject5 = factory.getObject(new Object(), PropertyOperationType.PROPERTY_OPERATION_VIEW);
        assertTrue(iServerObject5 == null);
    }

    @Test
    public void testIsExecutableForDatabase()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        boolean istrue;
        PropertyHandlerCore core = new PropertyHandlerCore(database);
        istrue = core.isExecutable();
        assertTrue(istrue);
    }

    @Test
    public void testIsExecutableForTable()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        TableMetaData tablemetaData = null;
        try
        {
            tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
        }
        catch (DatabaseOperationException e)
        {
            

            e.printStackTrace();
        }
        boolean istrue;
        PropertyHandlerCore core1 = new PropertyHandlerCore(tablemetaData);
        istrue = core1.isExecutable();
        assertTrue(istrue);
    }

    @Test
    public void testIsExecutableForInvalidServerObject()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Namespace namespace = database.getAllNameSpaces().get(0);

        boolean istrue;
        PropertyHandlerCore core1 = new PropertyHandlerCore(namespace);
        istrue = core1.isExecutable();
        assertFalse(istrue);
    }

    @Test
    public void testgetpropertyWindowUId()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            CommonLLTUtils.getDataBasePtropertiesRS(preparedstatementHandler);
            PropertyHandlerCore core = new PropertyHandlerCore(database);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            String tableType = "u";

            CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, tableType);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            PropertyHandlerCore coret = new PropertyHandlerCore(tablemetaData);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            coret.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            IPropertyDetail prop = core.getproperty();
            IPropertyDetail propt = coret.getproperty();
            assertEquals("65TestConnectionNameproperties", propt.getUniqueID());
            assertEquals("65TestConnectionNameproperties", prop.getUniqueID());
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
        catch (SQLException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void testgetpropertyWindowTitle()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            CommonLLTUtils.getDataBasePtropertiesRS(preparedstatementHandler);

            PropertyHandlerCore core = new PropertyHandlerCore(database);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            String tableType = "u";
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, tableType);

            PropertyHandlerCore coret = new PropertyHandlerCore(tablemetaData);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            coret.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            IPropertyDetail prop = core.getproperty();
            IPropertyDetail propt = coret.getproperty();
            assertEquals("pg_catalog.MyTable-Gauss@TestConnectionName", propt.getHeader());
            assertEquals("Gauss@TestConnectionName", prop.getHeader());
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
        catch (SQLException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void testgetPropertyDatabase()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
            CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");

            Database database = connProfCache.getDbForProfileId(profileId);

            CommonLLTUtils.getDataBasePtropertiesRS(preparedstatementHandler);

            PropertyHandlerCore core = new PropertyHandlerCore(database);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            IPropertyDetail prop = core.getproperty();
            assertTrue(prop.objectproperties().size() > 0);

        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testtablegetProperty()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            String tableType = "u";

            CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, tableType);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            PropertyHandlerCore core = new PropertyHandlerCore(tablemetaData);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            IPropertyDetail prop = core.getproperty();
            assertTrue(prop.objectproperties().size() > 0);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testtablegetProperty_forOptions()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            String tableType = "u";

            CommonLLTUtils.getTablePtropertiesRS_forOptions(preparedstatementHandler, tableType);
            CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, tableType);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            PropertyHandlerCore core = new PropertyHandlerCore(tablemetaData);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            IPropertyDetail prop = core.getproperty();
            assertTrue(prop.objectproperties().size() > 0);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_tablegetProperty_TableDoesNotExist()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            String tableType = "u";

            CommonLLTUtils.getTablePtropertiesRS_TableDoesNotExist(preparedstatementHandler, tableType);
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            PropertyHandlerCore core = new PropertyHandlerCore(tablemetaData);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            IPropertyDetail prop = core.getproperty();
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }

        catch (SQLException e)
        {
            fail("not expected");
        }
    }

    public void test_tablegetProperty_SQLException()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }

        ExceptionConnection exceptionConnection = new ExceptionConnection();
        exceptionConnection.setNeedExceptioStatement(true);
        exceptionConnection.setNeedExceptionResultset(true);
        exceptionConnection.setThrowExceptionNext(true);
        exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

        getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);

        String query =
                "SELECT tbl.relpersistence as relpersistence, case when tbl.reltablespace = 0 then 'DEFAULT' else tblsp.spcname end, auth.rolname as owner, tbl.relpages pages, tbl.reltuples as rows_count, tbl.relhasindex as has_index, tbl.relisshared as is_shared, tbl.relchecks as check_count, tbl.relhaspkey as has_pkey, tbl.relhasrules as has_rules, tbl.relhastriggers as has_triggers, array_to_string(tbl.reloptions, ',') as options,tbl.relhasoids as hashoid, d.description as tbl_desc FROM pg_class tbl LEFT JOIN pg_roles auth on (tbl.relowner = auth.oid) left join pg_description d on (tbl.oid = d.objoid) LEFT JOIN pg_tablespace tblsp ON (tbl.reltablespace = tblsp.oid) WHERE tbl.oid = 1;";
        String tableType = "u";
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("relpersistence");
        getdbsrs.addColumn("owner");
        getdbsrs.addColumn("pages");
        getdbsrs.addColumn("rows_count");
        getdbsrs.addColumn("has_index");
        getdbsrs.addColumn("is_shared");
        getdbsrs.addColumn("check_count");
        getdbsrs.addColumn("has_pkey");
        getdbsrs.addColumn("has_rules");
        getdbsrs.addColumn("has_triggers");
        getdbsrs.addColumn("options");
        getdbsrs.addColumn("hashoid");
        getdbsrs.addColumn("tbl_desc");
        getdbsrs.addColumn("spcname");
        getdbsrs.addRow(new Object[] {tableType, "GaussMPPDB", "12", "1200", false, false, false, true, true, false,
            "1 2 3", true, "Tbl Description", "tblspc_prop"});

        preparedstatementHandler.prepareResultSet(query, getdbsrs);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.getServer().setServerCompatibleToNodeGroup(true);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            PropertyHandlerCore core = new PropertyHandlerCore(tablemetaData);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            IPropertyDetail prop = core.getproperty();
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }

        catch (SQLException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void testgetdbemptyProperty()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        try
        {
            CommonLLTUtils.createDataBaseRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            database.setOid(23);

            CommonLLTUtils.getDataBasePtropertiesemptyRS(preparedstatementHandler);

            PropertyHandlerCore core = new PropertyHandlerCore(database);
            core.getproperty();
            fail("not expected here");

        }
        catch (Exception e)
        {
            System.out.println("as  expected");
        }
    }

    @Test
    public void testtablegetEmptyProperty()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            String tableType = "u";

            CommonLLTUtils.getTablePtropertiesemptyRS(preparedstatementHandler, tableType);
            tablemetaData.setOid(2);
            PropertyHandlerCore core = new PropertyHandlerCore(tablemetaData);
            core.getproperty();
            fail("not to come here");
        }
        catch (Exception e)
        {
            System.out.println("as expected");
        }
    }

    @Test
    public void testPropertyDatabaseOperationalException()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
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
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            preparedstatementHandler.prepareThrowsSQLException(
                    "SELECT  oid as oid, datname AS name, pg_encoding_to_char(encoding) as encoding, datallowconn as allow_conn, datconnlimit as max_conn_limit, (select spcname from pg_tablespace where oid=dattablespace) as  default_tablespace, datcollate as collation, datctype as char_type from pg_database where oid = 2;");

            database.connectToServer();
            PropertyHandlerCore core = new PropertyHandlerCore(database);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            core.getproperty();
        }
        catch (SQLException e)
        {
            fail();
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (MPPDBIDEException e)
        {
            fail();
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testPropertyDatabasecriticalException()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
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
            ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
            ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ArrayList<Object> list = new ArrayList<Object>();
            SQLException sqlException = new SQLException("Throwing SQL exception intentionally.", "57PSQLException");
            preparedstatementHandler.prepareThrowsSQLException(
                    "SELECT  oid as oid, datname AS name, pg_encoding_to_char(encoding) as encoding, datallowconn as allow_conn, datconnlimit as max_conn_limit, (select spcname from pg_tablespace where oid=dattablespace) as  default_tablespace, datcollate as collation, datctype as char_type from pg_database where oid = 2;",
                    sqlException, list);
            database.connectToServer();
            PropertyHandlerCore core = new PropertyHandlerCore(database);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            core.getproperty();
        }
        catch (SQLException e)
        {
            fail();
        }
        catch (DatabaseCriticalException e)
        {
            assertTrue(true);
        }
        catch (MPPDBIDEException e)
        {
            fail();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            fail();
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_005_table_type_P()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            String tableType = "p";

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            constraintMetaData.setNamespace(database.getNameSpaceById(1));
            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1, "Col2",
                    new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, tableType);
            PropertyHandlerCore core = new PropertyHandlerCore(tablemetaData);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            assertNotNull(core.getproperty());

        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    public void testTTA_BL_TABLE_FUNC_001_005_table_type_T()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);

            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            String tableType = "t";

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            constraintMetaData.setNamespace(database.getNameSpaceById(1));
            tablemetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1, "Col2",
                    new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, tableType);
            PropertyHandlerCore core = new PropertyHandlerCore(tablemetaData);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            assertNotNull(core.getproperty());

        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    /*
     * public void testTTA_BL_TABLE_FUNC_001_005_Table_Type_U() { try {
     * CommonLLTUtils.createTableRS(preparedstatementHandler);
     * CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
     * Database database = connProfCache.getDbForProfileId(profileId); Namespace
     * nm = database.getNameSpaceById(1); TableMetaData tablemetaData = new
     * TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
     * tablemetaData.setTempTable(true); tablemetaData.setIfExists(true);
     * tablemetaData.setName("MyTable"); tablemetaData.setHasOid(true);
     * tablemetaData.setDistributeOptions("HASH");
     * tablemetaData.setNodeOptions("Node1"); tablemetaData.setDescription(
     * "Table description"); String tableType = "u";
     * nm.addTableToSearchPool(tablemetaData);
     * 
     * ConstraintMetaData constraintMetaData = new ConstraintMetaData(1,
     * "MyConstarint", CONSTRAINT_TYPE.UNIQUE_KEY_CONSTRSINT);
     * constraintMetaData.setNamespace(nm);
     * tablemetaData.addConstraint(constraintMetaData);
     * 
     * ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1,
     * "Col1", new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
     * tablemetaData.getColumns().addItem(newTempColumn);
     * 
     * ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1,
     * "Col2", new TypeMetaData(1, "text", database.getNameSpaceById(1)));
     * tablemetaData.getColumns().addItem(newTempColumn1);
     * 
     * CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler,
     * tableType); tablemetaData.belongsTo(database, database.getServer());
     * PropertyHandlerCore core = new PropertyHandlerCore(tablemetaData);
     * core.getTermConnection() .setConnection(database.getFreeConnection());
     * IPropertyDetail prop = core.getproperty(); //
     * tablemetaData.getProperties(this.dbconn); ArrayList<String> list =
     * tablemetaData .getDistributedColumnNamesForEdit(
     * database.getFreeConnection()); assertTrue(list.size() == 1);
     * 
     * } catch (Exception e) { fail(e.getMessage()); } }
     */

    public void testTTA_BL_TABLE_FUNC_001_005_Table_Type_V()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            String tableType = "u";

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            constraintMetaData.setNamespace(database.getNameSpaceById(1));
            tablemetaData.addConstraint(constraintMetaData);

            IndexMetaData index = new IndexMetaData(1, "Index_1");
            index.setNamespace(database.getNameSpaceById(1));
            index.setTable(tablemetaData);
            index.setTablespace("pg_default");
            tablemetaData.addIndex(index);

            ColumnMetaData newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1, "Col2",
                    new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            PropertyHandlerCore core = new PropertyHandlerCore(tablemetaData);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            assertNotNull(core.getproperty());

        }
        catch (Exception e)
        {
            fail("not expected");
        }
    }

    @Test
    public void test_getObjectName()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        CommonLLTUtils.createDataBaseRS(preparedstatementHandler);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");

        Database database = connProfCache.getDbForProfileId(profileId);

        CommonLLTUtils.getDataBasePtropertiesRS(preparedstatementHandler);

        PropertyHandlerCore core = new PropertyHandlerCore(database);


    }

    @Test
    public void testTTA_BL_TABLE_EXCEPTIONS_FUNC_001_014()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
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
            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");

            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrowExceptionNext(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.EXCEPTION);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);

            database.connectToServer();

            Namespace namespace = new Namespace(1, "pg_catalog", database);
            database.getSystemNamespaceGroup().addToGroup((SystemNamespace) namespace);
            DebugObjects debugObject = new DebugObjects(1, "name", OBJECTTYPE.PLSQLFUNCTION, database);
            debugObject.setNamespace(namespace);
            TableMetaData tableMetaData = new TableMetaData(namespace);
            PropertyHandlerCore core = new PropertyHandlerCore(tableMetaData);
            core.getproperty();
            fail("Not expected to come here");
        }
        catch (Exception e)
        {
            System.out.println("as expected");
        }
    }
@Test
    public void test_viewProperty()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }

        CommonLLTUtils.getViewMockRS(preparedstatementHandler);
        CommonLLTUtils.fetchViewQuery(preparedstatementHandler);
        CommonLLTUtils.createViewColunmMetadata(preparedstatementHandler);
        CommonLLTUtils.fetchViewColumnInfo(preparedstatementHandler);
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Namespace namespace = database.getNameSpaceById(1);
            namespace.refreshAllViewsInNamespace(database.getConnectionManager().getObjBrowserConn());
            ArrayList<ViewMetaData> views = namespace.getViewGroup().getSortedServerObjectList();

            ViewMetaData view = views.get(0);
            PropertyHandlerCore core = new PropertyHandlerCore(view);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            IPropertyDetail detail = core.getproperty();
            assertEquals(3, detail.objectproperties().get(0).getAllFetchedRows().size());

            assertEquals("Gauss1234", detail.objectproperties().get(0).getAllFetchedRows().get(1).getValue(1));
        }
        catch (Exception e)
        {
            fail("not expected here");
        }

    }

    @Test
    public void test_partitionTableProperty()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.versionOLAPV1r7c10});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }

        CommonLLTUtils.fetchColMetadataForTableRS(preparedstatementHandler);
        CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
        CommonLLTUtils.refreshTableForPartitionTable(preparedstatementHandler);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        MockResultSet partitionTablemetadataRS = preparedstatementHandler.createResultSet();
        partitionTablemetadataRS.addColumn("oid");
        partitionTablemetadataRS.addColumn("relname");
        partitionTablemetadataRS.addColumn("relnamespace");
        partitionTablemetadataRS.addColumn("reltablespace");
        partitionTablemetadataRS.addColumn("relpersistence");
        partitionTablemetadataRS.addColumn("desc");
        partitionTablemetadataRS.addColumn("nodes");
        partitionTablemetadataRS.addColumn("reloptions");

        partitionTablemetadataRS.addRow(new Object[] {1, "MyTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes, tbl.reloptions as reloptions  from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.parttype in ('p', 'v') and tbl.oid = 1 and tbl.relkind <>  'i' and oid in (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE'))) select * from x where has_table_privilege(x.oid,'SELECT')",
                partitionTablemetadataRS);

        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
    }

    @Test
    public void test_connectionProperty_4()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version6});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        String incorrectVersion = "ORACleV1R00C10";
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(incorrectVersion);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(incorrectVersion);
        PropertyHandlerCore core = new PropertyHandlerCore(server);
        try
        {
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            IPropertyDetail getproperty = core.getproperty();
            IObjectPropertyData iObjectPropertyData = getproperty.objectproperties().get(0);
            Object value = iObjectPropertyData.getAllFetchedRows().get(6).getValue(1);
            Object value2 = iObjectPropertyData.getAllFetchedRows().get(5).getValue(1);
            core.getWindowDetails().isCloseable();
            core.getWindowDetails().getShortTitle();
            core.getWindowDetails().getTitle();
            core.getWindowDetails().getIcon();
            core.getWindowDetails().getUniqueID();
            assertEquals("1.0 build e2c0f862", value);
            assertEquals("openGauss", value2);
        }
        catch (MPPDBIDEException | SQLException e)
        {
            fail("not expected");
        }

    }

    @Test
    public void test_ServerIPProperty_1()
    {
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }

        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            String serverIP = database.fetchServerIP();
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            IPropertyDetail getproperty = core.getproperty();
            IObjectPropertyData iObjectPropertyData = getproperty.objectproperties().get(0);
            Object value = iObjectPropertyData.getAllFetchedRows().get(4).getValue(1);
            assertEquals("127.0.0.1", value);
        }
        catch (MPPDBIDEException | SQLException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void test_ServerIPProperty_2()
    {
        MockResultSet serverResultset = preparedstatementHandler.createResultSet();
        serverResultset.addColumn("SERVERIP");
        preparedstatementHandler.prepareResultSet("select inet_server_addr();", serverResultset);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            String serverIP = database.fetchServerIP();
            System.out.println("server ip is " + serverIP);
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            IPropertyDetail getproperty = core.getproperty();
            IObjectPropertyData iObjectPropertyData = getproperty.objectproperties().get(0);
            Object value = iObjectPropertyData.getAllFetchedRows().get(4).getValue(1);
            assertEquals("127.0.0.1", value);
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
        }
        catch (OutOfMemoryError e)
        {
            
            e.printStackTrace();
        }
        catch (PasswordExpiryException e)
        {
            
            e.printStackTrace();
        }
        catch (MPPDBIDEException | SQLException e)
        {
            
            e.printStackTrace();
        }
    }

    @Test
    public void test_init_general()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "Emp_ID", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            List<String> tabNameList = new ArrayList<>();
            tabNameList.add("General");
            tabNameList.add("Columns");
            tabNameList.add("Constraints");
            tabNameList.add("Index");

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            Map<String, String> commentsList = propertiestable.getComments(table.getConnectionManager().getFreeConnection());
            List<String[]> generalProperty =
                    propertiestable.getGeneralProperty(table.getConnectionManager().getFreeConnection(), commentsList);

            List<String[]> columnInfo = propertiestable.getColumnInfo(table.getConnectionManager().getFreeConnection(), commentsList);

            List<String[]> constraintInfo = propertiestable.getConstraintInfo(table.getConnectionManager().getFreeConnection());

            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(generalProperty, "General", table, propertiestable);
            dataProvider.init();
            List<IDSGridDataRow> allFetchedRows = dataProvider.getAllFetchedRows();
            assertEquals(18, allFetchedRows.size());
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }
    }

    @Test
    public void test_init_columns()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "Emp_ID", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            List<String> tabNameList = new ArrayList<>();
            tabNameList.add("General");
            tabNameList.add("Columns");
            tabNameList.add("Constraints");
            tabNameList.add("Index");

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            Map<String, String> commentsList = propertiestable.getComments(table.getConnectionManager().getFreeConnection());
            List<String[]> columnInfo = propertiestable.getColumnInfo(table.getConnectionManager().getFreeConnection(), commentsList);
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(columnInfo, "columns", table, propertiestable);
            dataProvider.init();
            List<IDSGridDataRow> allFetchedRows = dataProvider.getAllFetchedRows();
            for (IDSGridDataRow idsGridDataRow : allFetchedRows)
            {
                if (idsGridDataRow.getValue(0).equals("Emp_ID"))
                {
                    ((IDSGridEditDataRow) idsGridDataRow).setValue(1, "bigint");
                    ((IDSGridEditDataRow) idsGridDataRow).setValue(3, "employee id");

                }
                else if (idsGridDataRow.getValue(0).equals("Emp_NAME"))
                {
                    ((IDSGridEditDataRow) idsGridDataRow).setValue(1, "varchar");
                }
                else
                {
                    ((IDSGridEditDataRow) idsGridDataRow).setValue(1, "integer");
                }
            }
            assertEquals(3, allFetchedRows.size());
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }
    }

    @Test
    public void test_init_constraint()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "Emp_ID", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            List<String> tabNameList = new ArrayList<>();
            tabNameList.add("General");
            tabNameList.add("Columns");
            tabNameList.add("Constraints");
            tabNameList.add("Index");

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> constraintInfo = propertiestable.getConstraintInfo(table.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(constraintInfo, "constraints", table, propertiestable);
            dataProvider.init();
            List<IDSGridDataRow> allFetchedRows = dataProvider.getAllFetchedRows();
            assertEquals(1, allFetchedRows.size());
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }
    }

    @Test
    public void test_init_index()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        CommonLLTUtils.getIndexWhereExpr(preparedstatementHandler);
        getServerVersionResult.addColumn("VERSION");
        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);
        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "Emp_ID", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);

            IndexMetaData index1 = new IndexMetaData(1, "index1");
            index1.setTable(table);
            index1.setNamespace(database.getNameSpaceById(1));
            index1.addColumn(empId);
            table.addIndex(index1);
            List<String> tabNameList = new ArrayList<>();
            tabNameList.add("General");
            tabNameList.add("Columns");
            tabNameList.add("Constraints");
            tabNameList.add("Index");

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> indexInfo = propertiestable.getIndexInfo(table.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(indexInfo, "index", table, propertiestable);
            dataProvider.init();
            List<IDSGridDataRow> allFetchedRows = dataProvider.getAllFetchedRows();
            assertEquals(1, allFetchedRows.size());
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }
    }

    @Test
    public void test_init_createEmptyRow_general()
    {

        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            List<String> tabNameList = new ArrayList<>();
            tabNameList.add("General");
            tabNameList.add("Columns");
            tabNameList.add("Constraints");
            tabNameList.add("Index");

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);

            Map<String, String> commentsList = propertiestable.getComments(table.getConnectionManager().getFreeConnection());
            List<String[]> generalProperty =
                    propertiestable.getGeneralProperty(table.getConnectionManager().getFreeConnection(), commentsList);

            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(generalProperty, "General", table, propertiestable);
            dataProvider.init();
            String objectPropertyName = dataProvider.getObjectPropertyName();
            assertEquals("General", objectPropertyName);
            assertEquals("propertiesTable", dataProvider.getTable().getName());
            assertEquals(18,dataProvider.getRecordCount() );
            builder = new StringBuilder();
            if (builder.length() == 0)
            {
                for (int i = 0; i < 5200; i++)
                {
                    builder.append("a");
                }
            }
            String validObjectName =
                    dataProvider.isValidObjectName(1, builder.toString(), dataProvider.getAllFetchedRows().get(17));
            assertEquals("Maximum 5000 characters allowed for table description.", validObjectName);
            List<IDSGridDataRow> allFetchedRows = dataProvider.getAllFetchedRows();
            IDSGridDataRow idsGridDataRow = allFetchedRows.get(12);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(1, "my descrption");
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(1, commit2.getListOfSuccessRows().size());
            dataProvider.refresh();

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }

    }

    @Test
    public void test_init_createEmptyRow_column()
    {

        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");
        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            List<String> tabNameList = new ArrayList<>();
            tabNameList.add("General");
            tabNameList.add("Columns");
            tabNameList.add("Constraints");
            tabNameList.add("Index");

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            Map<String, String> commentsList = propertiestable.getComments(table.getConnectionManager().getFreeConnection());
            List<String[]> columnInfo = propertiestable.getColumnInfo(table.getConnectionManager().getFreeConnection(), commentsList);
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(columnInfo, "Columns", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(table, 1);
            createNewRow.setValue(0, "ColName");
            createNewRow.setValue(1, "bigint");
            createNewRow.setValue(2, true);
            createNewRow.setValue(3, "newComment");
            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(empId);
            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(1, commit.getListOfSuccessRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            dataProvider.commit();
            assertEquals(1, commit.getListOfSuccessRows().size());
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
           ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "colnew");
            ObjectPropColumnTabInfo tabInfo = new ObjectPropColumnTabInfo();
            tabInfo.setColDataType(type1);
            tabInfo.setPrecision(1);
            tabInfo.setScale(1);
            tabInfo.setDataTypeSchema("testschema");
            ((IDSGridEditDataRow) idsGridDataRow).setValue(1, tabInfo);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(2, true);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(3, "newComment");
            assertEquals(1, dataProvider.getUpdatedRowCount());
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(1, commit2.getListOfSuccessRows().size());
            dataProvider.refresh();
            dataProvider.getAllRows();
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }
    }

    @Test
    public void test_init_createEmptyRow_constraint()
    {

        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            primaryCons.setTable(table);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            ConstraintMetaData checkCons = new ConstraintMetaData(3, "ConstraintName", ConstraintType.CHECK_CONSTRSINT);
            table.addConstraint(checkCons);
            checkCons.setTable(table);
            checkCons.setCheckConstraintExpr(ServerObject.getQualifiedObjectName("ColName"));
            checkCons.setCheckConstraintExpr(ServerObject.getQualifiedObjectName("all"));
            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> constraintInfo = propertiestable.getConstraintInfo(table.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(constraintInfo, "Constraints", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(checkCons, 1);

            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(1, commit.getListOfSuccessRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            assertEquals(1, dataProvider.commit().getListOfSuccessRows().size());
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "colnew");
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(1, commit2.getListOfSuccessRows().size());
            dataProvider.refresh();
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }

    }

    @Test
    public void test_init_createEmptyRow_index()
    {

        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            primaryCons.setTable(table);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);

            IndexMetaData index1 = new IndexMetaData(1, "IndexName");
            index1.setTable(table);
            index1.setNamespace(database.getNameSpaceById(1));
            index1.addColumn(empId);
            table.addIndex(index1);

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> indexInfo = propertiestable.getIndexInfo(table.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(indexInfo, "Index", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(index1, 1);
            createNewRow.setValue(0, "IndexName");
            createNewRow.setValue(1, "age");
            createNewRow.setValue(2, "false");
            createNewRow.setValue(3, "c<1");
            createNewRow.setValue(4, "pg_default");

            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(index1);
            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(1, commit.getListOfSuccessRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            assertEquals(1, dataProvider.commit().getListOfSuccessRows().size());
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "idx_1");
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(1, commit2.getListOfSuccessRows().size());
            dataProvider.refresh();
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }

    }

    @Test
    public void test_rearrangeInsertedRowIndex()
    {

        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            Server server = database.getServer();
            server.setServerVersion(ServerVersionTest.version4);
            String serverVersion = server.getServerVersion(true);
            ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
            CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            primaryCons.setTable(table);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);

            IndexMetaData index1 = new IndexMetaData(1, "IndexName");
            index1.setTable(table);
            index1.setNamespace(database.getNameSpaceById(1));
            index1.addColumn(empId);
            table.addIndex(index1);

            IndexMetaData index2 = new IndexMetaData(2, "IndexName2");
            index2.setTable(table);
            index2.setNamespace(database.getNameSpaceById(1));
            index2.addColumn(empAge);
            table.addIndex(index2);

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> indexInfo = propertiestable.getIndexInfo(table.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(indexInfo, "Index", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow1 = dataProvider.createNewRow(index2, 2);
            createNewRow1.setValue(0, "IndexName1");
            createNewRow1.setValue(1, "age1");
            createNewRow1.setValue(2, "false");
            createNewRow1.setValue(3, "c<2");
            createNewRow1.setValue(4, "pg_default");
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(index1, 1);
            createNewRow.setValue(0, "IndexName");
            createNewRow.setValue(1, "age");
            createNewRow.setValue(2, "false");
            createNewRow.setValue(3, "c<1");
            createNewRow.setValue(4, "pg_default");

            dataProvider.deleteRecord(createNewRow, true);

            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(index1);
            assertEquals(1, dataProvider.getInsertedRowCount());
        }
        catch (DatabaseOperationException e)
        {
            
            e.printStackTrace();
        }
        catch (DatabaseCriticalException e)
        {
            
            e.printStackTrace();
        }
        catch (MPPDBIDEException e)
        {
            
            e.printStackTrace();
        }

    }

    @Test
    public void test_rollback()
    {

        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            primaryCons.setTable(table);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);

            IndexMetaData index1 = new IndexMetaData(1, "IndexName");
            index1.setTable(table);
            index1.setNamespace(database.getNameSpaceById(1));
            index1.addColumn(empId);
            table.addIndex(index1);

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> indexInfo = propertiestable.getIndexInfo(table.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(indexInfo, "Index", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(table, 1);
            createNewRow.setValue(0, "IndexName");
            createNewRow.setValue(1, "age");
            createNewRow.setValue(2, "false");
            createNewRow.setValue(3, "c<1");
            createNewRow.setValue(4, "pg_default");
            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(index1);
            assertEquals(2, dataProvider.getConsolidatedRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            dataProvider.rollBackProvider();
            assertEquals(0, dataProvider.getDeletedRowCount());

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }

    }

    @Test
    public void test_Objectpropinfo()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            List<String> tabNameList = new ArrayList<>();
            tabNameList.add("General");
            tabNameList.add("Columns");
            tabNameList.add("Constraints");
            tabNameList.add("Index");

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            Map<String, String> commentsList = propertiestable.getComments(table.getConnectionManager().getFreeConnection());
            List<String[]> columnInfo = propertiestable.getColumnInfo(table.getConnectionManager().getFreeConnection(), commentsList);
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(columnInfo, "Columns", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(empId, 1);

            ObjectPropColumnTabInfo tabInfo = new ObjectPropColumnTabInfo();
            tabInfo.setColDataType(type1);
            tabInfo.setPrecision(1);
            tabInfo.setScale(1);
            createNewRow.setValue(0, "ColName");
            createNewRow.setValue(1, tabInfo);
            createNewRow.setValue(2, true);
            createNewRow.setValue(3, "newComment");
            assertEquals(1, dataProvider.getInsertedRowCount());
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }

    }

    @Test
    public void test_init_createEmptyRow_column_negative()
    {

        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName1", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            List<String> tabNameList = new ArrayList<>();
            tabNameList.add("General");
            tabNameList.add("Columns");
            tabNameList.add("Constraints");
            tabNameList.add("Index");

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            Map<String, String> commentsList = propertiestable.getComments(table.getConnectionManager().getFreeConnection());
            List<String[]> columnInfo = propertiestable.getColumnInfo(table.getConnectionManager().getFreeConnection(), commentsList);
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(columnInfo, "Columns", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(table, 1);
            createNewRow.setValue(0, "c1");
            createNewRow.setValue(1, "bigint");
            createNewRow.setValue(2, true);
            createNewRow.setValue(3, "newComment");
            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(empId);
            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(1, commit.getListOfSuccessRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            dataProvider.commit();
            assertEquals(1, commit.getListOfSuccessRows().size());
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "colnew");
            assertEquals(1, dataProvider.getUpdatedRowCount());
            boolean checkneg = dataProvider.isGridDataEdited();
            assertEquals(checkneg, dataProvider.isGridDataEdited());
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(2, commit2.getListOfFailureRows().size());
            dataProvider.refresh();
            dataProvider.getAllRows();
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }
    }

    @Test
    public void test_init_createEmptyRow_constraint_negative()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            primaryCons.setTable(table);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            ConstraintMetaData checkCons = new ConstraintMetaData(3, "Cons3", ConstraintType.CHECK_CONSTRSINT);
            table.addConstraint(checkCons);
            checkCons.setTable(table);
            checkCons.setCheckConstraintExpr(ServerObject.getQualifiedObjectName("ColName"));
            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> constraintInfo = propertiestable.getConstraintInfo(table.getConnectionManager().getFreeConnection());

            String[] strings = constraintInfo.get(1);
            strings[0] = "ConstraintNameModified";
            constraintInfo.set(1, strings);
            /*
             * String[] strings2 = constraintInfo.get(1); strings2[0] = "";
             * constraintInfo.add(strings2);
             */
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(constraintInfo, "Constraints", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(checkCons, 1);
            createNewRow.setValue(0, "ConstraintName1");
            createNewRow.setValue(1, "age");
            createNewRow.setValue(2, "c");
            createNewRow.setValue(3, "c<1");
            createNewRow.setValue(4, "false");
            createNewRow.setValue(5, "public");
            createNewRow.setValue(6, "pg_default");

            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(primaryCons);
            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(1, commit.getListOfSuccessRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            assertEquals(1, dataProvider.commit().getListOfFailureRows().size());
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "colnew");
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(2, commit2.getListOfFailureRows().size());
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }
    }

    @Test
    public void test_init_createEmptyRow_constraint_negative_1()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "MyTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "MyTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            primaryCons.setTable(table);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            ConstraintMetaData checkCons = new ConstraintMetaData(3, "Cons3", ConstraintType.CHECK_CONSTRSINT);
            table.addConstraint(checkCons);
            checkCons.setTable(table);
            checkCons.setCheckConstraintExpr(ServerObject.getQualifiedObjectName("ColName"));
            checkCons.setCheckConstraintExpr(ServerObject.getQualifiedObjectName("like"));
            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> constraintInfo = propertiestable.getConstraintInfo(table.getConnectionManager().getFreeConnection());

            String[] strings2 = constraintInfo.get(1);
            strings2[0] = "";
            constraintInfo.set(1, strings2);
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(constraintInfo, "Constraints", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(checkCons, 1);
            createNewRow.setValue(0, "");
            createNewRow.setValue(1, "age");
            createNewRow.setValue(2, "c");
            createNewRow.setValue(3, "c<1");
            createNewRow.setValue(4, "false");
            createNewRow.setValue(5, "public");
            createNewRow.setValue(6, "pg_default");

            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(primaryCons);
            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(1, commit.getListOfSuccessRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            assertEquals(1, dataProvider.commit().getListOfFailureRows().size());
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "colnew");
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(2, commit2.getListOfFailureRows().size());
            boolean val=JSQLParserUtils.isQueryResultEditSupported("select * from temp");
            assertEquals(true, val);
            String str=JSQLParserUtils.getQualifiedTableName("select * from temp");
            assertEquals("temp", str);
            String strCreateView=JSQLParserUtils.getQualifiedTblRViewName("create view temp_view as select column1 from temp");
            assertEquals("temp_view", strCreateView);
            String strAlterTable=JSQLParserUtils.getQualifiedTblRViewName("ALTER TABLE alter_table ADD (col1 varchar2(50), col2 varchar2(50))");
            assertEquals("alter_table", strAlterTable);
            String strDropTable=JSQLParserUtils.getQualifiedTblRViewName("drop table drop_table");
            assertEquals("drop_table", strDropTable);
            String strObjType=JSQLParserUtils.setObjectType("create view temp_view as select column1 from temp");
            assertEquals("CREATE_VIEW", strObjType);
            String strAlterType=JSQLParserUtils.setObjectType("ALTER TABLE alter_table ADD (col1 varchar2(50), col2 varchar2(50))");
            assertEquals("ALTER_TABLE", strAlterType);
            String strDropType=JSQLParserUtils.setObjectType("drop table drop_table");
            assertEquals("DROP_TABLE", strDropType);
            String qualifiedObjectName = ServerObject.getQualifiedObjectName(table.getName());
            String[] splitQualifiedName2 = JSQLParserUtils.getSplitQualifiedName(qualifiedObjectName, false);
            assertEquals("\"MyTable\"",splitQualifiedName2[0] );
            String string="\"public\".\"table\"";
           String[] splitQualifiedName = JSQLParserUtils.getSplitQualifiedName(string, true);
           assertEquals("public", splitQualifiedName[0]);
           
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }
    }

    public void test_performIndex_negative()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            primaryCons.setTable(table);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);

            IndexMetaData index1 = new IndexMetaData(1, "IndexName2");
            index1.setTable(table);
            index1.setNamespace(database.getNameSpaceById(1));
            index1.addColumn(empId);
            table.addIndex(index1);

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> indexInfo = propertiestable.getIndexInfo(table.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(indexInfo, "Index", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(index1, 1);
            createNewRow.setValue(0, "IndexName1");
            createNewRow.setValue(1, "age");
            createNewRow.setValue(2, "false");
            createNewRow.setValue(3, "c<1");
            createNewRow.setValue(4, "pg_default");

            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(index1);
            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(1, commit.getListOfSuccessRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            assertEquals(1, dataProvider.commit().getListOfFailureRows().size());
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "idx_1");
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(2, commit2.getListOfFailureRows().size());
            dataProvider.refresh();

            dataProvider.isEditSupported();
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }
    }

    @Test
    public void test_isEditSupported()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            primaryCons.setTable(table);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);

            IndexMetaData index1 = new IndexMetaData(1, "IndexName2");
            index1.setTable(table);
            index1.setNamespace(database.getNameSpaceById(1));
            index1.addColumn(empId);
            table.addIndex(index1);

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> indexInfo = propertiestable.getIndexInfo(table.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(indexInfo, "Index", table, propertiestable);
            boolean editSupported = dataProvider.isEditSupported();

            String validObjectName = dataProvider.isValidObjectName(0,
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", null);
            assertEquals("Maximum 64 characters allowed for table, column, constraint, and index name.", validObjectName);
            assertEquals(true, editSupported);
            DSObjectPropertiesGridDataProvider dataProvider1 =
                    new DSObjectPropertiesGridDataProvider(indexInfo, "Index", null, propertiestable);
            assertEquals(false, dataProvider1.isEditSupported());

            PartitionTable partTable = new PartitionTable(database.getNameSpaceById(1));
            DSObjectPropertiesGridDataProvider dataProvider2 =
                    new DSObjectPropertiesGridDataProvider(indexInfo, "Index", partTable, propertiestable);
            assertEquals(false, dataProvider2.isEditSupported());
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }
    }

    
    @Test
    public void test__addColumnQuery_SQLException_CriticalException()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");
        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        SQLException sqlException = new SQLException("Throwing SQL exception intentionally.", "57PSQLException");
        preparedstatementHandler.prepareThrowsSQLException("ALTER TABLE pg_catalog.\"propertiesTable\" ADD COLUMN \"ColName\" bigint DEFAULT '1'",sqlException);
        
        SQLException sqlException1 = new SQLException("Throwing SQL exception intentionally.", "57PSQLException");
        preparedstatementHandler.prepareThrowsSQLException("ALTER TABLE pg_catalog.\"propertiesTable\" DROP COLUMN \"ColName\";",sqlException1);
        
        
        preparedstatementHandler.prepareThrowsSQLException("ALTER TABLE pg_catalog.\"propertiesTable\" RENAME COLUMN \"ColName\" TO colnew",sqlException);
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            List<String> tabNameList = new ArrayList<>();
            tabNameList.add("General");
            tabNameList.add("Columns");
            tabNameList.add("Constraints");
            tabNameList.add("Index");

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            Map<String, String> commentsList = propertiestable.getComments(table.getConnectionManager().getFreeConnection());
            List<String[]> columnInfo = propertiestable.getColumnInfo(table.getConnectionManager().getFreeConnection(), commentsList);
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(columnInfo, "Columns", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(table, 1);
            createNewRow.setValue(0, "ColName");
            createNewRow.setValue(1, "bigint");
            createNewRow.setValue(2, true);
            createNewRow.setValue(3, "newComment");
            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(empId);
            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(1, commit.getListOfFailureRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            dataProvider.commit();
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "colnew");
            dataProvider.commit();
    }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }
    }
            
    
    @Test
    public void test__addColumnQuery_SQLException_DatabaseOperationException()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");
        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareThrowsSQLException("ALTER TABLE pg_catalog.\"propertiesTable\" ADD COLUMN \"ColName\" bigint DEFAULT '1'");
        preparedstatementHandler.prepareThrowsSQLException("ALTER TABLE pg_catalog.\"propertiesTable\" DROP COLUMN \"ColName\";");
        preparedstatementHandler.prepareThrowsSQLException("ALTER TABLE pg_catalog.\"propertiesTable\" RENAME COLUMN \"ColName\" TO colnew");
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            List<String> tabNameList = new ArrayList<>();
            tabNameList.add("General");
            tabNameList.add("Columns");
            tabNameList.add("Constraints");
            tabNameList.add("Index");

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            Map<String, String> commentsList = propertiestable.getComments(table.getConnectionManager().getFreeConnection());
            List<String[]> columnInfo = propertiestable.getColumnInfo(table.getConnectionManager().getFreeConnection(), commentsList);
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(columnInfo, "Columns", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(table, 1);
            createNewRow.setValue(0, "ColName");
            createNewRow.setValue(1, "bigint");
            createNewRow.setValue(2, true);
            createNewRow.setValue(3, "newComment");
            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(empId);
            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(1, commit.getListOfFailureRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            dataProvider.commit();
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "colnew");
            dataProvider.commit();
           
            
    }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }
    }
    @Test
    public void test__addConstraint_SQLException_CriticalException()
    {

        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "MyTable", 1, 1, true, "description", "1 2", ""});
        SQLException sqlException = new SQLException("Throwing SQL exception intentionally.", "57PSQLException");
        preparedstatementHandler.prepareThrowsSQLException("ALTER TABLE pg_catalog.\"MyTable\" ADD CONSTRAINT \"Cons1\" PRIMARY KEY ()WITH (fillfactor=100)",sqlException);
        preparedstatementHandler.prepareThrowsSQLException("ALTER TABLE pg_catalog.\"MyTable\" DROP CONSTRAINT \"ConstraintName\";",sqlException);
        preparedstatementHandler.prepareThrowsSQLException("ALTER TABLE pg_catalog.\"MyTable\" RENAME CONSTRAINT \"ConstraintName\" TO colnew",sqlException);
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "MyTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            primaryCons.setTable(table);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            ConstraintMetaData checkCons = new ConstraintMetaData(3, "Cons3", ConstraintType.CHECK_CONSTRSINT);
            table.addConstraint(checkCons);
            checkCons.setTable(table);
            checkCons.setCheckConstraintExpr(ServerObject.getQualifiedObjectName("ColName"));
            checkCons.setCheckConstraintExpr(ServerObject.getQualifiedObjectName("all"));
            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> constraintInfo = propertiestable.getConstraintInfo(table.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(constraintInfo, "Constraints", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(checkCons, 1);
            createNewRow.setValue(0, "ConstraintName");
            createNewRow.setValue(1, "age");
            createNewRow.setValue(2, "c");
            createNewRow.setValue(3, "c<1");
            createNewRow.setValue(4, "false");
            createNewRow.setValue(5, "public");
            createNewRow.setValue(6, "pg_default");

            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(primaryCons);
            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(1, commit.getListOfFailureRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            assertEquals(2, dataProvider.commit().getListOfFailureRows().size());
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "colnew");
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(3, commit2.getListOfFailureRows().size());
            dataProvider.refresh();
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }

    }
    
    
    
    @Test
    public void test__addConstraint_SQLException_DatabaseOperationException()
    {

        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "MyTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareThrowsSQLException("ALTER TABLE pg_catalog.\"MyTable\" ADD CONSTRAINT \"Cons1\" PRIMARY KEY ()WITH (fillfactor=100)");
        preparedstatementHandler.prepareThrowsSQLException("ALTER TABLE pg_catalog.\"MyTable\" DROP CONSTRAINT \"ConstraintName\";");
        preparedstatementHandler.prepareThrowsSQLException("ALTER TABLE pg_catalog.\"MyTable\" RENAME CONSTRAINT \"ConstraintName\" TO colnew");
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "MyTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            primaryCons.setTable(table);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            ConstraintMetaData checkCons = new ConstraintMetaData(3, "Cons3", ConstraintType.CHECK_CONSTRSINT);
            table.addConstraint(checkCons);
            checkCons.setTable(table);
            checkCons.setCheckConstraintExpr(ServerObject.getQualifiedObjectName("ColName"));
            checkCons.setCheckConstraintExpr(ServerObject.getQualifiedObjectName("all"));
            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> constraintInfo = propertiestable.getConstraintInfo(table.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(constraintInfo, "Constraints", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(checkCons, 1);
            createNewRow.setValue(0, "ConstraintName");
            createNewRow.setValue(1, "age");
            createNewRow.setValue(2, "c");
            createNewRow.setValue(3, "c<1");
            createNewRow.setValue(4, "false");
            createNewRow.setValue(5, "public");
            createNewRow.setValue(6, "pg_default");

            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(primaryCons);
            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(1, commit.getListOfFailureRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            assertEquals(2, dataProvider.commit().getListOfFailureRows().size());
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "colnew");
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(3, commit2.getListOfFailureRows().size());
            dataProvider.refresh();
            CommitStatus commit3 = dataProvider.commit();
            commit3.setListOfFailureRows(commit2.getListOfFailureRows());
            assertEquals(3, commit3.getListOfFailureRows().size());
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }

    }
      
    @Test
    public void test__addIndex_SQLException_DatabaseOperationException()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
       preparedstatementHandler.prepareThrowsSQLException("CREATE INDEX pg_default ON pg_catalog.\"propertiesTable\" (\"ColName\") ;");
       preparedstatementHandler.prepareThrowsSQLException("drop index pg_catalog.\"IndexName\";");
       preparedstatementHandler.prepareThrowsSQLException("ALTER INDEX pg_catalog.\"IndexName\" RENAME TO idx_1 ;");
       preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            primaryCons.setTable(table);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);

            IndexMetaData index1 = new IndexMetaData(1, "IndexName");
            index1.setTable(table);
            index1.setNamespace(database.getNameSpaceById(1));
            index1.addColumn(empId);
            table.addIndex(index1);

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> indexInfo = propertiestable.getIndexInfo(table.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(indexInfo, "Index", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(index1, 1);
            createNewRow.setValue(0, "IndexName");
            createNewRow.setValue(1, "age");
            createNewRow.setValue(2, "false");
            createNewRow.setValue(3, "c<1");
            createNewRow.setValue(4, "pg_default");

            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(index1);
            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(0, commit.getListOfSuccessRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            assertEquals(0, dataProvider.commit().getListOfSuccessRows().size());
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "idx_1");
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(0, commit2.getListOfSuccessRows().size());
            dataProvider.refresh();
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }

    
        
    }
    
    
    @Test
    public void test__addIndex_SQLException_CriticalException()
    {

        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        SQLException sqlException = new SQLException("Throwing SQL exception intentionally.", "57PSQLException");
       preparedstatementHandler.prepareThrowsSQLException("CREATE INDEX pg_default ON pg_catalog.\"propertiesTable\" (\"ColName\") ;",sqlException);
       preparedstatementHandler.prepareThrowsSQLException("drop index pg_catalog.\"IndexName\";",sqlException);
       preparedstatementHandler.prepareThrowsSQLException("ALTER INDEX pg_catalog.\"IndexName\" RENAME TO idx_1 ;",sqlException);
       preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            primaryCons.setTable(table);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);

            IndexMetaData index1 = new IndexMetaData(1, "IndexName");
            index1.setTable(table);
            index1.setNamespace(database.getNameSpaceById(1));
            index1.addColumn(empId);
            table.addIndex(index1);

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);
            List<String[]> indexInfo = propertiestable.getIndexInfo(table.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(indexInfo, "Index", table, propertiestable);
            dataProvider.init();
            IDSGridEditDataRow createNewRow = dataProvider.createNewRow(index1, 1);
            createNewRow.setValue(0, "IndexName");
            createNewRow.setValue(1, "age");
            createNewRow.setValue(2, "false");
            createNewRow.setValue(3, "c<1");
            createNewRow.setValue(4, "pg_default");

            ((DSObjectPropertiesGridDataRow) createNewRow).setServerObject(index1);
            assertEquals(1, dataProvider.getInsertedRowCount());
            CommitStatus commit = dataProvider.commit();
            assertEquals(0, commit.getListOfSuccessRows().size());
            dataProvider.deleteRecord(createNewRow, false);
            assertEquals(0, dataProvider.commit().getListOfSuccessRows().size());
            IDSGridDataRow idsGridDataRow = dataProvider.getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "idx_1");
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(0, commit2.getListOfSuccessRows().size());
            dataProvider.refresh();
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }

    }
    
    @Test
    public void test__setTableDesc_SQLException_CriticalException()
    {

        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        SQLException sqlException = new SQLException("Throwing SQL exception intentionally.", "57PSQLException");
        preparedstatementHandler.prepareThrowsSQLException("COMMENT ON TABLE pg_catalog.\"propertiesTable\" IS 'my descrption';",sqlException);
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            List<String> tabNameList = new ArrayList<>();
            tabNameList.add("General");
            tabNameList.add("Columns");
            tabNameList.add("Constraints");
            tabNameList.add("Index");

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);

            Map<String, String> commentsList = propertiestable.getComments(table.getConnectionManager().getFreeConnection());
            List<String[]> generalProperty =
                    propertiestable.getGeneralProperty(table.getConnectionManager().getFreeConnection(), commentsList);

            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(generalProperty, "General", table, propertiestable);
            dataProvider.init();
            String objectPropertyName = dataProvider.getObjectPropertyName();
            assertEquals("General", objectPropertyName);
            assertEquals("propertiesTable", dataProvider.getTable().getName());
            assertEquals(18,dataProvider.getRecordCount() );
            builder = new StringBuilder();
            if (builder.length() == 0)
            {
                for (int i = 0; i < 5200; i++)
                {
                    builder.append("a");
                }
            }
            String validObjectName =
                    dataProvider.isValidObjectName(1, builder.toString(), dataProvider.getAllFetchedRows().get(17));
            assertEquals("Maximum 5000 characters allowed for table description.", validObjectName);
            List<IDSGridDataRow> allFetchedRows = dataProvider.getAllFetchedRows();
            IDSGridDataRow idsGridDataRow = allFetchedRows.get(12);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(1, "my descrption");
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(0, commit2.getListOfSuccessRows().size());
            dataProvider.refresh();

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }

    
    }
    @Test
    public void test__setDesc_SQLException_DatabaseOperationException()
    {


        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareThrowsSQLException("COMMENT ON TABLE pg_catalog.\"propertiesTable\" IS 'my descrption';");
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        CommonLLTUtils.getTablePtropertiesRS(preparedstatementHandler, "u");
        try
        {
            PropertyHandlerCore core = new PropertyHandlerCore(server);
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());

            TableMetaData table = new TableMetaData(1, "propertiesTable", database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint", database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar", database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer", database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(table, 1, "ColName", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(table, 2, "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(table, 3, "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            table.addColumn(empId);
            table.addColumn(empName);
            table.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1", ConstraintType.PRIMARY_KEY_CONSTRSINT);
            table.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            table.addConstraint(uniqueCons);
            List<String> tabNameList = new ArrayList<>();
            tabNameList.add("General");
            tabNameList.add("Columns");
            tabNameList.add("Constraints");
            tabNameList.add("Index");

            PropertiesTableImpl propertiestable = new PropertiesTableImpl(table);

            Map<String, String> commentsList = propertiestable.getComments(table.getConnectionManager().getFreeConnection());
            List<String[]> generalProperty =
                    propertiestable.getGeneralProperty(table.getConnectionManager().getFreeConnection(), commentsList);

            DSObjectPropertiesGridDataProvider dataProvider =
                    new DSObjectPropertiesGridDataProvider(generalProperty, "General", table, propertiestable);
            dataProvider.init();
            String objectPropertyName = dataProvider.getObjectPropertyName();
            assertEquals("General", objectPropertyName);
            assertEquals("propertiesTable", dataProvider.getTable().getName());
            assertEquals(18,dataProvider.getRecordCount() );
            builder = new StringBuilder();
            if (builder.length() == 0)
            {
                for (int i = 0; i < 5200; i++)
                {
                    builder.append("a");
                }
            }
            String validObjectName =
                    dataProvider.isValidObjectName(1, builder.toString(), dataProvider.getAllFetchedRows().get(17));
            assertEquals("Maximum 5000 characters allowed for table description.", validObjectName);
            List<IDSGridDataRow> allFetchedRows = dataProvider.getAllFetchedRows();
            IDSGridDataRow idsGridDataRow = allFetchedRows.get(12);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(1, "my descrption");
            CommitStatus commit2 = dataProvider.commit();
            assertEquals(0, commit2.getListOfSuccessRows().size());
            dataProvider.refresh();

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (OutOfMemoryError e)
        {
            fail("not expected");

        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");

        }

    
    
    }
    
    @Test
    public void test_getAllProperties_001()
    {
        MockResultSet getServerVersionResult = preparedstatementHandler.createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {ServerVersionTest.version4});
        preparedstatementHandler.prepareResultSet("SELECT * from version();", getServerVersionResult);

        MockResultSet refreshtablemetada = preparedstatementHandler.createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "propertiesTable", 1, 1, true, "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                refreshtablemetada);

        try
        {
            profileId = connProfCache.initConnectionProfile(serverInfo, status);
        }
        catch (OutOfMemoryError | MPPDBIDEException | PasswordExpiryException e)
        {
            fail("not exptected");
        }
        MockUserRoleManagerUtils.test_isSysAdmin_001_RS(preparedstatementHandler);
        Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();
        server.setServerVersion(ServerVersionTest.version4);
        String serverVersion = server.getServerVersion(true);
        ((ServerConnectionInfo) server.getServerConnectionInfo()).setDBVersion(serverVersion);
        /*Database database = connProfCache.getDbForProfileId(profileId);
        Server server = database.getServer();*/
        UserRole userRole = new UserRole();
        userRole.setName("Chris");
        userRole.setServer(server);
        userRole.setOid(101L);
        userRole.setRolCanLogin(false);
        userRole.setIsUser(false);
        MockUserRoleManagerUtils.test_getUserRoleNameByOid_001_RS(preparedstatementHandler, 101);
        MockUserRoleManagerUtils.test_fetchUserRoleDetailInfoByOid_001_RS(preparedstatementHandler, userRole);
        MockUserRoleManagerUtils.test_getUserRoleloginByOid_001_RS(preparedstatementHandler, 101);
        MockUserRoleManagerUtils.test_fetchAllParent_001_RS(preparedstatementHandler, userRole);
        MockUserRoleManagerUtils.test_fetchDescriptionOfUserRole_001_RS(preparedstatementHandler, userRole);
        MockUserRoleManagerUtils.test_fetchLockStatusOfUserRole_001_RS(preparedstatementHandler, userRole);
        PropertyHandlerCore core = new PropertyHandlerCore(userRole);
        try {
            core.getTermConnection().setConnection(database.getConnectionManager().getFreeConnection());
            List<IObjectPropertyData> objectproperties = core.getproperty().objectproperties();
            DSObjectPropertiesGridDataProvider p = (DSObjectPropertiesGridDataProvider)objectproperties.get(0);
            List<IDSGridDataRow> allRows = p.getAllRows();
            IDSGridEditDataRow idsGridDataRow1 = (IDSGridEditDataRow)allRows.get(2);
            idsGridDataRow1.setValue(1, "10");
            IDSGridEditDataRow idsGridDataRow2=(IDSGridEditDataRow)allRows.get(3);
            idsGridDataRow2.setValue(1, "2019-06-04");
            IDSGridEditDataRow idsGridDataRow3=(IDSGridEditDataRow)allRows.get(4);
            idsGridDataRow3.setValue(1,"2019-06-04");
            IDSGridEditDataRow idsGridDataRow4=(IDSGridEditDataRow)allRows.get(5);
            idsGridDataRow4.setValue(1, "Default_value");
            IDSGridEditDataRow idsGridDataRow6=(IDSGridEditDataRow)allRows.get(6);
            idsGridDataRow6.setValue(1, "comment userrole");
            List<String> generateUserRolePropertyChangePreviewSql = p.generateUserRolePropertyChangePreviewSql(database.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider p1 = (DSObjectPropertiesGridDataProvider)objectproperties.get(1);
            List<IDSGridDataRow> allRows2 = p1.getAllRows();
            allRows2.stream().forEach(row->{
                ((IDSGridEditDataRow)row).setValue(1, false);
            });
            p1.generateUserRolePropertyChangePreviewSql(database.getConnectionManager().getFreeConnection());
            DSObjectPropertiesGridDataProvider p2 = (DSObjectPropertiesGridDataProvider)objectproperties.get(2);
            List<IDSGridDataRow> allRows3 = p2.getAllRows();
            IDSGridDataRow idsGridDataRow = allRows3.get(0);
            IDSGridEditDataRow row = (IDSGridEditDataRow)idsGridDataRow;
            row.setValue(1, "chris");
            p2.generateUserRolePropertyChangePreviewSql(database.getConnectionManager().getFreeConnection());
            p.commitUserRoleProperty(database.getConnectionManager().getFreeConnection(), generateUserRolePropertyChangePreviewSql);
            
            assertNotNull(core.getproperty());
        } catch (MPPDBIDEException | SQLException e) {
            fail("not expected");
        } catch (Exception e) {
            
            fail("not expected");
        } finally {}

    }
    
    
    
}
