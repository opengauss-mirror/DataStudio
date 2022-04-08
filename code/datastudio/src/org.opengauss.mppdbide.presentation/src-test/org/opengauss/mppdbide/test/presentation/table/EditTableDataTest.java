package org.opengauss.mppdbide.test.presentation.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.sqlhistory.QueryExecutionSummary;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.presentation.EditTableDataCore;
import org.opengauss.mppdbide.presentation.IWindowDetail;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.presentation.edittabledata.CommitStatus;
import org.opengauss.mppdbide.presentation.edittabledata.DSEditTableDataGridDataProvider;
import org.opengauss.mppdbide.presentation.edittabledata.DSEditTableDataGridRow;
import org.opengauss.mppdbide.presentation.edittabledata.EditTableCellState;
import org.opengauss.mppdbide.presentation.edittabledata.EditTableRecordStates;
import org.opengauss.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import org.opengauss.mppdbide.presentation.edittabledata.QueryResultMaterializer;
import org.opengauss.mppdbide.presentation.grid.IDSEditGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.presentation.resultset.ConsoleDataWrapper;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.observer.DSEventTable;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import static org.junit.Assert.*;

public class EditTableDataTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    EditTableDataHelper               helper;
    IDSEditGridDataProvider           dataProvider;
    EditTableDataCore                 coreObject;

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
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");

        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);

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
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
        initializeHelper();
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

    public void initializeHelper()
    {
        helper = new EditTableDataHelper(
                connProfCache.getDbForProfileId(profileId));
        try
        {
            dataProvider = helper.getDataProvider(preparedstatementHandler);
            coreObject = helper.getCoreObject();

        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
        }
    }

    // Edit Table Data core test begins
    @Test
    public void test_EditTableDataCore_getQuery()
    {

        String query = coreObject.getQuery();
        assertEquals("select * from " + "pg_catalog.\"EditTable\"", query);

    }

    @Test
    public void test_EditTableDataCore_getWindowTitle()
    {
        String windowTitle = coreObject.getWindowTitle();
        assertEquals("pg_catalog.EditTable-Gauss@TestConnectionName",
                windowTitle);
    }

    @Test
    public void test_EditTableDataCore_getWindowDetails()
    {
        IWindowDetail windowDetail = null;
        windowDetail = coreObject.getWindowDetails();
        assertNotNull(windowDetail);
        assertEquals("pg_catalog.\"EditTable\"",
                windowDetail.getShortTitle());
        assertEquals("pg_catalog.EditTable-Gauss@TestConnectionName",
                windowDetail.getTitle());
        assertEquals(
                "EDIT_TABLE_DATA"
                        + "pg_catalog.EditTable-Gauss@TestConnectionName",
                windowDetail.getUniqueID());
        assertNull(windowDetail.getIcon());
        assertEquals(true, windowDetail.isCloseable());

    }

    @Test
    public void test_EditTableDataCore_getTermConnection()
    {
        TerminalExecutionConnectionInfra termConnection = coreObject
                .getTermConnection();
        assertNotNull(termConnection);
    }

    @Test
    public void test_EditTableDataCore_getTable()
    {
        TableMetaData table = coreObject.getTable();
        assertEquals("EditTable", table.getName());
    }

    @Test
    public void test_EditTableDataCore_getProgressBarLabel() {
        String progressBarLabel = coreObject.getProgressBarLabel();
        assertEquals("Editing table data query: EditTable.pg_catalog.Gauss@TestConnectionName", progressBarLabel);
    }
    
    @Test
    public void test_EditTableDataCore_isTableDropped() {
        
        assertEquals(true, coreObject.isTableDropped());
    }
    // Edit Table Data core test ends
    

    @Test
    public void test_DSEditTableDataGridProvider_init_WithReplicationDistribution()
    {

        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithReplicationDistribution(
                        preparedstatementHandler);
        try
        {
            this.dataProvider.init();
            assertEquals(0,
                    this.dataProvider.getDistributedColumnList().size());
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
    public void test_DSEditTableDataGridProvider_getEmptyRowForInsert_1()
    {

        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        try
        {
            dataProvider.init();

            assertNotNull(dataProvider.getEmptyRowForInsert(0));

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
    public void test_DSEditTableDataGridProvider_getEmptyRowForInsert_2()
    {

        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        try
        {
            dataProvider.init();

            IDSGridEditDataRow emptyRowForInsert = this.dataProvider
                    .getEmptyRowForInsert(0);
            assertEquals(null, emptyRowForInsert.getValue(2));

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
    public void test_DSEditTableDataGridProvider_isEditSupported()
    {
        assertTrue(this.dataProvider.isEditSupported());
    }

    @Test
    public void test_QueryResultMaterializer_materializeQueryResult_1() throws SQLException
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        try
        {
            QueryExecutionSummary queryExecutionSummary = (QueryExecutionSummary) helper
                    .getQueryExecutionSummary();
            queryExecutionSummary.startQueryTimer();
            Object obj = QueryResultMaterializer.materializeQueryResult(
                    helper.getQueryResult(), helper.getResultConfig(),
                    queryExecutionSummary, new ConsoleDataWrapper(),
                    helper.getExecutionContext(), true, false, null, false);
            assertNotNull(obj);
        }
        catch (DatabaseCriticalException e)
        {
            fail("not exptected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not exptected");

        }

    }

    @Test
    public void test_QueryResultMaterializer_materializeQueryResult_2() throws SQLException
    {
        try
        {
            QueryExecutionSummary queryExecutionSummary = (QueryExecutionSummary) helper
                    .getQueryExecutionSummary();
            queryExecutionSummary.startQueryTimer();
            Object obj = QueryResultMaterializer.materializeQueryResult(
                    helper.getQueryResult(), helper.getResultConfig(),
                    queryExecutionSummary, new ConsoleDataWrapper(),
                    helper.getExecutionContext(), false, false, null, false);
            assertNotNull(obj);
        }
        catch (DatabaseCriticalException e)
        {
            fail("not exptected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not exptected");

        }

    }

    @Test
    public void test_DSEditTableDataGridProvider_getConsolidatedRows_1()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        try
        {
            dataProvider.init();
            dataProvider.getEmptyRowForInsert(0);
            int size = dataProvider.getConsolidatedRows().size();
            assertEquals(8, size);
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (DatabaseCriticalException e)
        {
            fail("not exptected");

        }
    }

    @Test
    public void test_DSEditTableDataGridProvider_getConsolidatedRows_2()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        try
        {
            dataProvider.init();
            int size = dataProvider.getConsolidatedRows().size();
            assertEquals(7, size);
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");

        }
    }

    @Test
    public void test_DSEditTableDataGridProvider_deleteRecord_1()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        try
        {
            dataProvider.init();
            List<IDSGridDataRow> consolidatedRows = dataProvider
                    .getConsolidatedRows();
            dataProvider.deleteRecord(
                    (IDSGridEditDataRow) consolidatedRows.get(0), false);
            int deleteListSize = dataProvider.getDeletedRowCount();
            assertEquals(1, deleteListSize);

        }
        catch (DatabaseOperationException e)
        {
            fail("not exptected");

        }
        catch (DatabaseCriticalException e)
        {
            fail("not exptected");

        }
    }

    @Test
    public void test_DSEditTableDataGridProvider_deleteRecord_2()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        try
        {
            dataProvider.init();
            dataProvider.getEmptyRowForInsert(0);
            dataProvider.getEmptyRowForInsert(1);
            List<IDSGridDataRow> consolidatedRows = dataProvider
                    .getConsolidatedRows();
            assertEquals(2, dataProvider.getInsertedRowCount());
            dataProvider.deleteRecord(
                    (IDSGridEditDataRow) consolidatedRows.get(0), true);
            int deleteListSize = dataProvider.getDeletedRowCount();
            int insertListSize = dataProvider.getInsertedRowCount();
            assertEquals(0, deleteListSize);
            assertEquals(1, insertListSize);

        }
        catch (DatabaseOperationException e)
        {
            fail("not exptected");

        }
        catch (DatabaseCriticalException e)
        {
            fail("not exptected");

        }
    }

    @Test
    public void test_DSEditTableDataGridProvider_getUniquekeys()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        try
        {
            dataProvider.init();
            assertTrue(dataProvider.isUniqueKeyPresent());

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void test_DSEditTableDataGridProvider_getEditTable()
    {
        DSEditTableDataGridDataProvider provider = (DSEditTableDataGridDataProvider)dataProvider;
        TableMetaData editTable = (TableMetaData) provider.getTable();
        assertEquals("EditTable", editTable.getName());
    }

    @Test
    public void test_DSEditTableDataGridProvider_isDistributionColumn()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        try
        {
            this.dataProvider.init();
            dataProvider.getDistributedColumnList().add("Emp_ID");
            assertEquals(false, this.dataProvider.isDistributionColumn(0));
            assertEquals(false, this.dataProvider.isDistributionColumn(1));
            assertEquals(false, this.dataProvider.isDistributionColumn(2));
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void test_DSEditTableDataGridProvider_getUniqueKeys()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);

        try
        {
            this.dataProvider.init();
            List<String> uniqueKeys = ((DSEditTableDataGridDataProvider) this.dataProvider)
                    .getUniqueKeys();
            int numberOfUniquekeys = ((TableMetaData) this.dataProvider.getTable())
                    .getColumnMetaDataList().size();
            assertEquals(numberOfUniquekeys, uniqueKeys.size());

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");

        }

    }

    @Test
    public void test_DSEditTableDataGridProvider_isGridDataEdited()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);

        try
        {
            boolean gridDataEdited = this.dataProvider.isGridDataEdited();
            this.dataProvider.init();
            assertEquals(false, gridDataEdited);
            IDSGridDataRow idsGridDataRow = this.dataProvider
                    .getConsolidatedRows().get(0);
            this.dataProvider.deleteRecord((IDSGridEditDataRow) idsGridDataRow,
                    false);
            boolean afterDeleting = this.dataProvider.isGridDataEdited();
            ;
            assertTrue(afterDeleting);
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }

    }

    @Test
    public void test_DSEditTableDataGridProvider_rollBack()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        try
        {
            this.dataProvider.init();
            IDSGridEditDataRow emptyRowForInsert = this.dataProvider.getEmptyRowForInsert(0);
            this.dataProvider.getEmptyRowForInsert(1);
            this.dataProvider.getEmptyRowForInsert(2);
            List<IDSGridDataRow> consolidatedRows = this.dataProvider
                    .getConsolidatedRows();
            assertEquals(10, consolidatedRows.size());
            assertEquals(3, this.dataProvider.getInsertedRowCount());
            this.dataProvider.rollBackProvider();
            assertEquals(7, this.dataProvider.getConsolidatedRows().size());
            assertEquals(0, this.dataProvider.getInsertedRowCount());

            assertEquals(emptyRowForInsert.getCommitStatusMessage(), "");
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
    public void test_getOriginalValue_success()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);

        try
        {
            this.dataProvider.init();
            IDSGridEditDataRow emptyRowForInsert = this.dataProvider
                    .getEmptyRowForInsert(0);
            emptyRowForInsert.setValue(0, "11");
            assertNull(emptyRowForInsert.getOriginalValue(0));
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");

        }

    }

    @Test
    public void test_getUpdatedState_success()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);

        try
        {
            this.dataProvider.init();
            IDSGridEditDataRow emptyRowForInsert = this.dataProvider
                    .getEmptyRowForInsert(0);
            assertEquals(EditTableRecordStates.INSERT,
                    emptyRowForInsert.getUpdatedState());

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");

        }

    }

    @Test
    public void test_DSEditTableDataGridProvider_isDistributionColumnsRequired()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);

        try
        {
            this.dataProvider.init();
            assertEquals(false,
                    this.dataProvider.isDistributionColumnsRequired());
            this.dataProvider
                    .deleteRecord((IDSGridEditDataRow) this.dataProvider
                            .getConsolidatedRows().get(0), false);
            assertTrue(this.dataProvider.isDistributionColumnsRequired());

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");

        }
    }

    @Test
    public void test_DSEditTableDataGridProvider_Commit_updateRowsFailure()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        CommonLLTUtils.prepareUpdateQueryForEditTableData(
                preparedstatementHandler, connection);

        try
        {
            dataProvider.init();
            IDSGridDataRow idsGridDataRow = this.dataProvider
                    .getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "101");
            ((IDSGridEditDataRow) idsGridDataRow).setValue(1, "newItem1");
            ((IDSGridEditDataRow) idsGridDataRow).setValue(2, "25");
            dataProvider.commit(helper.getUniqueKeys(), true,
                    helper.getRowEffectedConfirm(false), CommonLLTUtils.getDBConnection());

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void test_DSEditTableDataGridProvider_Commit_insertRows()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        CommonLLTUtils.prepareInsertQueryForEditTableData(
                preparedstatementHandler, connection);

        try
        {
            dataProvider.init();
            IDSGridEditDataRow emptyRowForInsert = dataProvider
                    .getEmptyRowForInsert(0);
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(0, "101");
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(1, "newItem1");
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(2, "25");
            dataProvider.commit(helper.getUniqueKeys(), true,
                    helper.getRowEffectedConfirm(false), CommonLLTUtils.getDBConnection());

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void test_DSEditTableDataGridProvider_Commit_deleteRows()
    {

        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);

        try
        {
            dataProvider.init();
            IDSGridDataRow idsGridDataRow = this.dataProvider
                    .getAllFetchedRows().get(0);
            this.dataProvider.deleteRecord((IDSGridEditDataRow) idsGridDataRow,
                    false);
            CommitStatus commit = dataProvider.commit(helper.getUniqueKeys(),
                    true, helper.getRowEffectedConfirm(false), CommonLLTUtils.getDBConnection());
            assertEquals(1, commit.getListOfFailureRows().size());

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }
    
    

    @Test
    public void test_DSEditTableDataGridProvider_Commit_updateRows_success()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        CommonLLTUtils.prepareUpdateQueryForEditTableData(
                preparedstatementHandler, connection);

        try
        {
            dataProvider.init();
            this.dataProvider.getEmptyRowForInsert(0);
            int size = this.dataProvider.getAllFetchedRows().size();
            IDSGridDataRow idsGridDataRowForDeletion = this.dataProvider
                    .getAllFetchedRows().get(size - 1);
            this.dataProvider.deleteRecord(
                    (IDSGridEditDataRow) idsGridDataRowForDeletion, false);
            IDSGridDataRow idsGridDataRow = this.dataProvider
                    .getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "101");
            ((IDSGridEditDataRow) idsGridDataRow).setValue(1, "newItem1");
            ((IDSGridEditDataRow) idsGridDataRow).setValue(2, "25");
            dataProvider.getDistributedColumnList().add("Emp_ID");
            dataProvider.commit(helper.getUniqueKeys(), true,
                    helper.getRowEffectedConfirm(false), CommonLLTUtils.getDBConnection());

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void test_insertRowsNotAtomic()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        CommonLLTUtils.prepareInsertQueryForEditTableData(
                preparedstatementHandler, connection);

        try
        {
            dataProvider.init();
            IDSGridEditDataRow emptyRowForInsert = dataProvider
                    .getEmptyRowForInsert(0);
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(0, "101");
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(1, "newItem1");
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(2, "25");
            dataProvider.commit(helper.getUniqueKeys(), false,
                    helper.getRowEffectedConfirm(false), CommonLLTUtils.getDBConnection());

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void test_undoRowUpdate()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        CommonLLTUtils.prepareInsertQueryForEditTableData(
                preparedstatementHandler, connection);

        try
        {
            dataProvider.init();
            IDSGridEditDataRow emptyRowForInsert = dataProvider
                    .getEmptyRowForInsert(0);
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(0, "101");
            ((IDSGridEditDataRow) emptyRowForInsert).undo(0);
            Object value = ((IDSGridEditDataRow) emptyRowForInsert).getValue(0);
            assertNull(value);
//            ((IDSGridEditDataRow) emptyRowForInsert).setValue(1, "newItem1");
//            ((IDSGridEditDataRow) emptyRowForInsert).setValue(2, "25");
//            dataProvider.commit(helper.getUniqueKeys(), false,
//                    helper.getRowEffectedConfirm(false));

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }
    
    @Test
    public void test_undo_For_UPDATED_ROW()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        CommonLLTUtils.prepareInsertQueryForEditTableData(
                preparedstatementHandler, connection);

        try
        {
            dataProvider.init();
            List<IDSGridDataRow> allFetchedRows = dataProvider.getAllFetchedRows();
            if (allFetchedRows.size() > 0)
            {
                IDSGridEditDataRow editRow = (IDSGridEditDataRow) allFetchedRows.get(0);
                Object earlierVal = ((IDSGridEditDataRow) editRow).getValue(0);
                editRow.setValue(0, "101");
                ((IDSGridEditDataRow) editRow).undo(0);
                Object value = ((IDSGridEditDataRow) editRow).getValue(0);
                assertEquals(earlierVal, value);
            }
            else
            {
                fail("Expected rows more than 0");
            }
            
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }
    
    @Test
    public void test_getClonedValues()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        CommonLLTUtils.prepareInsertQueryForEditTableData(
                preparedstatementHandler, connection);

        try
        {
            dataProvider.init();
            IDSGridEditDataRow emptyRowForInsert = dataProvider
                    .getEmptyRowForInsert(0);
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(0, "101");
            Object[] clonedValues = emptyRowForInsert.getClonedValues();

            assertEquals(clonedValues.length, 3);
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }
    
    @Test
    public void test_getUpdatedState_INSERTED_ROW()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        CommonLLTUtils.prepareInsertQueryForEditTableData(
                preparedstatementHandler, connection);

        try
        {
            dataProvider.init();
            IDSGridEditDataRow emptyRowForInsert = dataProvider
                    .getEmptyRowForInsert(0);
            
            emptyRowForInsert.setValue(0, "101");
            assertEquals(emptyRowForInsert.getUpdatedState(0), EditTableRecordStates.INSERT);
            
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }
    
    @Test
    public void test_getUpdatedState_UPDATED_ROW()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        CommonLLTUtils.prepareInsertQueryForEditTableData(
                preparedstatementHandler, connection);

        try
        {
            dataProvider.init();
            List<IDSGridDataRow> allFetchedRows = dataProvider.getAllFetchedRows();
            if (allFetchedRows.size() > 0)
            {
                IDSGridEditDataRow editRow = (IDSGridEditDataRow) allFetchedRows.get(0);
                editRow.setValue(0, "101");
                assertEquals(editRow.getUpdatedState(0), EditTableRecordStates.UPDATE);
            }
            else
            {
                fail("Expected rows more than 0");
            }
            
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }
    
    @Test
    public void test_updateRowsNotAtomic()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        CommonLLTUtils.prepareUpdateQueryForEditTableData(
                preparedstatementHandler, connection);

        try
        {
            dataProvider.init();
            IDSGridDataRow idsGridDataRow = this.dataProvider
                    .getAllFetchedRows().get(0);
            ((IDSGridEditDataRow) idsGridDataRow).setValue(0, "101");
            ((IDSGridEditDataRow) idsGridDataRow).setValue(1, "newItem1");
            ((IDSGridEditDataRow) idsGridDataRow).setValue(2, "25");
            dataProvider.commit(helper.getUniqueKeys(), false,
                    helper.getRowEffectedConfirm(false), CommonLLTUtils.getDBConnection());

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void test_deleteRowsNotAtomic()
    {

        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);

        try
        {
            dataProvider.init();
            IDSGridDataRow idsGridDataRow = this.dataProvider
                    .getAllFetchedRows().get(0);
            this.dataProvider.deleteRecord((IDSGridEditDataRow) idsGridDataRow,
                    false);
            dataProvider.commit(helper.getUniqueKeys(), false,
                    helper.getRowEffectedConfirm(false), CommonLLTUtils.getDBConnection());

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void test_successRowsCount()
    {

        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        CommonLLTUtils.prepareInsertQueryForEditTableData(
                preparedstatementHandler, connection);

        try
        {
            dataProvider.init();
            IDSGridEditDataRow emptyRowForInsert = dataProvider
                    .getEmptyRowForInsert(0);
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(0, "101");
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(1, "newItem1");
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(2, "25");

            CommitStatus commit = dataProvider.commit(helper.getUniqueKeys(),
                    true, helper.getRowEffectedConfirm(false), CommonLLTUtils.getDBConnection());
            List<IDSGridEditDataRow> listOfRows = new ArrayList<>();

            for (IDSGridDataRow row : dataProvider.getAllFetchedRows())
            {
                listOfRows.add((IDSGridEditDataRow) row);
            }
            commit.setListOfSuccessRows(listOfRows);
            assertEquals(7, commit.getListOfSuccessRows().size());

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void test_updateRowsCount()
    {
        CommonLLTUtils
                .prepareDistibutionColumnListResultSetWithHashDistribution(
                        preparedstatementHandler);
        CommonLLTUtils.prepareInsertQueryForEditTableData(
                preparedstatementHandler, connection);
        try
        {

            dataProvider.init();
            IDSGridEditDataRow emptyRowForInsert = dataProvider
                    .getEmptyRowForInsert(0);
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(0, "101");
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(1, "newItem1");
            ((IDSGridEditDataRow) emptyRowForInsert).setValue(2, "25");

            CommitStatus commit = dataProvider.commit(helper.getUniqueKeys(),
                    true, helper.getRowEffectedConfirm(false), CommonLLTUtils.getDBConnection());
            List<IDSGridEditDataRow> listOfRows = new ArrayList<>();

            for (IDSGridDataRow row : dataProvider.getAllFetchedRows())
            {
                listOfRows.add((IDSGridEditDataRow) row);
            }
            commit.setUpdatedRecords(listOfRows.size());
            
            IDSGridEditDataRow idsGridEditDataRow = listOfRows.get(0);
            idsGridEditDataRow.setCellSatus(EditTableCellState.MODIFIED, 0);
            assertEquals(EditTableCellState.MODIFIED, idsGridEditDataRow.getCellStatus(0));
            assertEquals(7, commit.getUpdatedRecords());

        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected");
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected");
        }
    }

    @Test
    public void test_getUpdatedRecords()
    {
        DSEventTable eventTable = new DSEventTable();

        DSEditTableDataGridRow gridRow = new DSEditTableDataGridRow(false, 0,
                eventTable, (DSEditTableDataGridDataProvider) dataProvider);
        assertEquals(0, gridRow.getUpdatedRecords());

    }
    
    @Test
    public void test_EditTableDataGridDataProvider_NoColumnDetails()
    {
        assertEquals(dataProvider.getColumnCount(), 0);
        assertNull(dataProvider.getColumnDataProvider());
        assertNull(dataProvider.getColumnNames());
        assertNull(dataProvider.getColumnDataTypeNames());
        ((DSEditTableDataGridDataProvider) dataProvider).changeEncoding("UTF-8");
        dataProvider.preDestroy();
        assertFalse(dataProvider.isDistributionColumn(0));
        assertNull(dataProvider.getDistributedColumnList());
        assertNull(dataProvider.getDatabse());
    }
    
    @Test
    public void test_commit_error_flow()
    {
        assertNotNull(dataProvider.getDatabse());
        CommitStatus commit = null;
        try
        {
            commit = dataProvider.commit(null,
                    true, helper.getRowEffectedConfirm(false), CommonLLTUtils.getDBConnection());
        }
        catch (NullPointerException e)
        {
            assertTrue(true);
        }
        catch (MPPDBIDEException e)
        {
            fail("fail");
        }
        assertNotNull(commit);
        try
        {
            dataProvider.cancelCommit();
        }
        catch (DatabaseCriticalException | DatabaseOperationException e)
        {
            fail("fail");
        }
        assertNotNull(dataProvider.getLastCommitStatus());
        dataProvider.setCancel(true);
        assertEquals(0, dataProvider.getLastCommitStatus().getListOfFailureRows().size());
        assertEquals(0, dataProvider.getLastCommitStatus().getListOfSuccessRows().size());
        assertEquals(0, dataProvider.getLastCommitStatus().getUpdatedRecords());
        assertNotNull(dataProvider.getTableName());
        dataProvider.preDestroy();
        assertFalse(dataProvider.isUniqueKeyPresent());
    }    

}
