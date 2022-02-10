package org.opengauss.mppdbide.test.presentation.table;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

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
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.mock.presentation.ExceptionConnection;
import org.opengauss.mppdbide.presentation.EditTableDataCore;
import org.opengauss.mppdbide.presentation.grid.IDSEditGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridColumnProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.presentation.grid.batchdrop.BatchDropDataProvider;
import org.opengauss.mppdbide.presentation.grid.batchdrop.BatchDropDataRow;
import org.opengauss.mppdbide.presentation.grid.batchdrop.BatchDropStatusEnum;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.exceptions.PasswordExpiryException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.observer.DSEventTable;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class BatchDropDataProviderTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    EditTableDataHelper               helper;
    IDSEditGridDataProvider           dataProvider;
    EditTableDataCore                 coreObject;

    ConstraintMetaData                constraintMetaData        = null;
    ColumnMetaData                    newTempColumn             = null;

    DSEventTable                      eventTable                = null;
    JobCancelStatus status =null;
    ServerConnectionInfo serverInfo =null;
    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        connection = new MockConnection();
        setupBase(connection);
    }

    private void setupBase(MockConnection connectionLocal) throws OutOfMemoryError, Exception {
        CommonLLTUtils.runLinuxFilePermissionInstance();
        
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connectionLocal);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");

        preparedstatementHandler = connectionLocal.getPreparedStatementResultSetHandler();
        statementHandler = connectionLocal.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();
        status = new JobCancelStatus();
        status.setCancel(false);

        serverInfo = new ServerConnectionInfo();
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

        init();
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

    private void init()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getConstaraintForTableRSEx(preparedstatementHandler);
            CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
            Database database = connProfCache.getDbForProfileId(profileId);
            TableMetaData tablemetaData;

            tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");

            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("MyTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            constraintMetaData = new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);
            constraintMetaData.setTable(tablemetaData);
            tablemetaData.addConstraint(constraintMetaData);

            newTempColumn = new ColumnMetaData(tablemetaData, 1, "Col1",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn);

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tablemetaData, 1, "Col2",
                    new TypeMetaData(1, "text", database.getNameSpaceById(1)));
            tablemetaData.getColumns().addItem(newTempColumn1);

            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tablemetaData);
            indexMetaData.setNamespace(tablemetaData.getNamespace());
            tablemetaData.addIndex(indexMetaData);
            CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
            // tablemetaData.execCreate(database.getObjBrowserConn());

            eventTable = new DSEventTable();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void test_BatchDataProvider_verifyRowMetadata()
    {
        try
        {
            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();
            List<IDSGridDataRow> rows1 = batchDropProvider.getNextBatch();

            if (rows.size() != rows1.size())
            {
                fail("Record Batch Match Failed");
                return;
            }

            if (batchDropProvider.getRecordCount() != 2)
            {
                fail("Record Count Match Failed");
                return;
            }

            assertEquals(batchDropProvider.getTable(), null);
            assertEquals(batchDropProvider.isEndOfRecords(), true);
            assertEquals(false, batchDropProvider.getResultTabDirtyFlag());
            batchDropProvider.setResultTabDirtyFlag(true);
            batchDropProvider.preDestroy();
            batchDropProvider.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_BatchDataProvider_verifyRowData()
    {
        try
        {
            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();

            if (null != batchDropProvider.getColumnGroupProvider())
            {
                fail("Group provider should not be set");
                return;
            }

            IDSGridColumnProvider columnMD = batchDropProvider.getColumnDataProvider();
            int columnCnt = columnMD.getColumnCount();
            IDSGridDataRow row = rows.get(0);

            Object obj[] = row.getValues();
            if (obj.length > 0)
            {
                fail("Record Data Values set wrong");
                return;
            }

            for (int j = 0; j < columnCnt; j++)
            {
                Object val = row.getValue(j);
                if (j == 3 && !(val instanceof BatchDropStatusEnum))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j != 3 && !(val instanceof String))
                {
                    fail("Value is not correct");
                    return;
                }

                String str = val.toString();
                if (j == 0 && !str.equals(constraintMetaData.getObjectTypeName()))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j == 1 && !str.equals(constraintMetaData.getObjectFullName()))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j == 2 && !str.equals(constraintMetaData.getDropQuery(false)))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j == 3 && !str.equals("To start"))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j == 4 && !str.equals(""))
                {
                    fail("Value is not correct");
                    return;
                }
            }

            if (!((BatchDropDataRow) row).getServerObject().equals(constraintMetaData))
            {
                fail("Values Object is not correct");
                return;
            }

            if (((BatchDropDataRow) row).isDropped())
            {
                fail("Values Object is not correct");
                return;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_BatchDataProvider_verifyRowCloneValue()
    {
        try
        {
            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();

            if (null != batchDropProvider.getColumnGroupProvider())
            {
                fail("Group provider should not be set");
                return;
            }

            IDSGridColumnProvider columnMD = batchDropProvider.getColumnDataProvider();
            int columnCnt = columnMD.getColumnCount();
            IDSGridDataRow row = rows.get(0);

            Object obj[] = row.getClonedValues();
            if (obj == null)
            {
                fail("Record Data Values set wrong");
                return;
            }

            for (int j = 0; j < columnCnt; j++)
            {
                Object val = obj[j];
                if (j == 3 && !(val instanceof BatchDropStatusEnum))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j != 3 && !(val instanceof String))
                {
                    fail("Value is not correct");
                    return;
                }

                String str = (String) val.toString();
                if (j == 0 && !str.equals(constraintMetaData.getObjectTypeName()))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j == 1 && !str.equals(constraintMetaData.getObjectFullName()))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j == 2 && !str.equals(constraintMetaData.getDropQuery(false)))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j == 3 && !str.equals("To start"))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j == 4 && !str.equals(""))
                {
                    fail("Value is not correct");
                    return;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_BatchDataProvider_verifyColumnProvider()
    {
        try
        {
            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            IDSGridColumnProvider columnMD = batchDropProvider.getColumnDataProvider();
            int columnCnt = columnMD.getColumnCount();
            if (columnCnt != 5)
            {
                fail("Column Count is wrong");
                return;
            }

            String columnName[] = columnMD.getColumnNames();
            if (columnName.length != columnCnt)
            {
                fail("Column Name Array is wrong");
                return;
            }

            String validColumnList[] = new String[5];
            validColumnList[0] = "Type";
            validColumnList[1] = "Name";
            validColumnList[2] = "Query";
            validColumnList[3] = "Status";
            validColumnList[4] = "Error Message";

            for (int i = 0; i < columnCnt; i++)
            {
                if (!columnName[i].equals(validColumnList[i]))
                {
                    fail("Column Name Value is wrong");
                    return;
                }

                if (!columnMD.getColumnDesc(i).equals(validColumnList[i]))
                {
                    fail("Column Name Get is wrong");
                    return;
                }

                if (columnMD.getColumnIndex(validColumnList[i]) != 0)
                {
                    fail("Column Name Filler info wrong");
                    return;
                }

                if (columnMD.getColumnDatatype(i) != Types.VARCHAR)
                {
                    fail("Column Name Filler info wrong");
                    return;
                }

                if (!columnMD.getColumnDataTypeName(i).equals("varchar"))
                {
                    fail("Column Name Filler info wrong");
                    return;
                }

                if (columnMD.getPrecision(i) != 0)
                {
                    fail("Column Name Filler info wrong");
                    return;
                }

                if (columnMD.getScale(i) != 0)
                {
                    fail("Column Name Filler info wrong");
                    return;
                }

                if (columnMD.getMaxLength(i) != 0)
                {
                    fail("Column Name Filler info wrong");
                    return;
                }

                if (columnMD.getComparator(i) == null)
                {
                    fail("Column Name Filler info wrong");
                    return;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_BatchDataProvider_verifyCascade()
    {
        try
        {
            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            batchDropProvider.setCascade(true);
            batchDropProvider.setAtomic(true);

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();

            IDSGridColumnProvider columnMD = batchDropProvider.getColumnDataProvider();
            int columnCnt = columnMD.getColumnCount();
            IDSGridDataRow row = rows.get(0);

            for (int j = 0; j < columnCnt; j++)
            {
                Object val = row.getValue(j);
                if (j == 3 && !(val instanceof BatchDropStatusEnum))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j != 3 && !(val instanceof String))
                {
                    fail("Value is not correct");
                    return;
                }

                String str = val.toString();
                if (j == 0 && !str.equals(constraintMetaData.getObjectTypeName()))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j == 1 && !str.equals(constraintMetaData.getObjectFullName()))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j == 2 && !str.equals(constraintMetaData.getDropQuery(true)))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j == 3 && !str.equals("To start"))
                {
                    fail("Value is not correct");
                    return;
                }
                else if (j == 4 && !str.equals(""))
                {
                    fail("Value is not correct");
                    return;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_BatchDataProvider_verifyRowStatusChange()
    {
        try
        {
            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            batchDropProvider.setCascade(true);
            batchDropProvider.setAtomic(true);

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();

            BatchDropDataRow row = (BatchDropDataRow) rows.get(0);

            row.updateStatus(BatchDropStatusEnum.IN_PROGRESS, false);

            Object val = row.getValue(3).toString();
            String str = val.toString();
            assertEquals("In progress", str);

            row.updateStatus(BatchDropStatusEnum.ERROR, false);
            row.updateError("Error Occured");

            val = row.getValue(3).toString();
            str = val.toString();
            assertEquals("Error", str);

            val = row.getValue(4);
            str = (String)val;
            assertEquals("Error Occured", str);

            row.updateStatus(BatchDropStatusEnum.COMPLETED, true);
            row.updateError("");

            val = row.getValue(3).toString();
            str = val.toString();
            assertEquals("Completed", str);

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_BatchDataProvider_atomicExecute_Success()
    {
        try
        {
            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            batchDropProvider.setAtomic(true);

            Database database = connProfCache.getDbForProfileId(profileId);
            DBConnection conn = database.getConnectionManager().getFreeConnection();
            batchDropProvider.startExecute(conn);

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();

            BatchDropDataRow row = (BatchDropDataRow) rows.get(0);

            Object val = row.getValue(3).toString();
            String str = (String) val;
            assertEquals("Completed", str);
            assertEquals(true, row.isDropped());

            row = (BatchDropDataRow) rows.get(1);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("Completed", str);
            assertEquals(true, row.isDropped());
            batchDropProvider.setPauseStopOperation(batchDropProvider.isPauseStopOperation());
            batchDropProvider.setEditSupported(batchDropProvider.isEditSupported());
            batchDropProvider.setFuncProcExport(batchDropProvider.isFuncProcExport());
            batchDropProvider.setCancelOperation(batchDropProvider.isCancelOperation());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_BatchDataProvider_atomicExecute_Failure()
    {
        try
        {
            tearDown();
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);
            setupBase(exceptionConnection);
            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            batchDropProvider.setAtomic(true);
            int totalcnt = batchDropProvider.getTotalObjectCnt();
            assertEquals(totalcnt, batchDropProvider.getTotalObjectCnt());
            Database database = connProfCache.getDbForProfileId(profileId);
            DBConnection conn = database.getConnectionManager().getFreeConnection();

            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExecuteException(true);
            exceptionConnection.setSqlException(new SQLException("Error: Failed!!!"));

            try
            {
                batchDropProvider.startExecute(conn);
                fail("Execute should fail");
            }
            catch (DatabaseOperationException e1)
            {

            }

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();

            BatchDropDataRow row = (BatchDropDataRow) rows.get(0);

            Object val = row.getValue(3).toString();
            String str = (String) val;
            assertEquals("Error", str);
            assertEquals(false, row.isDropped());

            row = (BatchDropDataRow) rows.get(1);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("To start", str);
            assertEquals(false, row.isDropped());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_BatchDataProvider_execute_Failure()
    {
        try
        {
            tearDown();
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);
            setupBase(exceptionConnection);
            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            batchDropProvider.setAtomic(false);

            Database database = connProfCache.getDbForProfileId(profileId);
            DBConnection conn = database.getConnectionManager().getFreeConnection();

            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExecuteException(true);
            exceptionConnection.setSqlException(new SQLException("Error: Failed!!!"));
            try
            {
                batchDropProvider.startExecute(conn);
            }
            catch (DatabaseOperationException e1)
            {
                fail("Execute should fail");
            }

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();

            BatchDropDataRow row = (BatchDropDataRow) rows.get(0);

            Object val = row.getValue(3).toString();
            String str = (String) val;
            assertEquals("Error", str);
            assertEquals(false, row.isDropped());

            row = (BatchDropDataRow) rows.get(1);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("Error", str);
            assertEquals(false, row.isDropped());

            // Try Rollback to return the states after setting the Atomic flag
            batchDropProvider.setAtomic(true);
            batchDropProvider.rollbackAndNotifyUIMgr(conn);
            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("To start", str);
            assertEquals(false, row.isDropped());

            row = (BatchDropDataRow) rows.get(1);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("To start", str);
            assertEquals(false, row.isDropped());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
  /*  @Test
    public void test_BatchDataProvider_execute_Failure_DoesNotExist()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);

            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            batchDropProvider.setAtomic(false);

            Database database = connProfCache.getDbForProfileId(profileId);
            DBConnection conn = database.getFreeConnection();

            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExecuteException(true);
            exceptionConnection.setExceptionFromUser(true);
            
            SQLException exp = new SQLException("Object does not exist", "42704");
            exceptionConnection.setSqlException(exp);
            try
            {
                batchDropProvider.startExecute(conn);
            }
            catch (DatabaseOperationException e1)
            {
                fail("Execute should fail");
            }

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();

            BatchDropDataRow row = (BatchDropDataRow) rows.get(0);

            Object val = row.getValue(3).toString();
            String str = (String) val;
            assertEquals("Completed", str);
            assertEquals(true, row.isDropped());

            row = (BatchDropDataRow) rows.get(1);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("Completed", str);
            assertEquals(true, row.isDropped());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }*/

    @Test
    public void test_BatchDataProvider_execute_CommitSetFailure()
    {
        try
        {
            tearDown();
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);
            setupBase(exceptionConnection);

            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            batchDropProvider.setAtomic(false);

            Database database = connProfCache.getDbForProfileId(profileId);
            DBConnection conn = database.getConnectionManager().getFreeConnection();

            exceptionConnection.setThrowExceptionSetAutoCommitTrue(true);
            exceptionConnection.setSqlException(new SQLException("Error: Failed!!!"));
            // exceptionConnection.setNeedExceptioStatement(true);
            // exceptionConnection.setNeedExecuteException(true);
            try
            {
                batchDropProvider.startExecute(conn);
                fail("Execute should fail");
            }
            catch (DatabaseOperationException e1)
            {

            }

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();

            BatchDropDataRow row = (BatchDropDataRow) rows.get(0);

            Object val = row.getValue(3).toString();
            String str = (String) val;
            assertEquals("To start", str);
            assertEquals(false, row.isDropped());

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);

            row = (BatchDropDataRow) rows.get(1);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("To start", str);
            assertEquals(false, row.isDropped());

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

  /*  @Test
    public void test_BatchDataProvider_atomicexecute_CommitFailure()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);

            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            batchDropProvider.setAtomic(true);

            Database database = connProfCache.getDbForProfileId(profileId);
            DBConnection conn = database.getFreeConnection();

            exceptionConnection.setThrowExceptionCommit(true);
            exceptionConnection.setSqlException(new SQLException("Error: Failed!!!"));
            // exceptionConnection.setNeedExceptioStatement(true);
            // exceptionConnection.setNeedExecuteException(true);
            try
            {
                batchDropProvider.startExecute(conn);
                fail("Execute should fail");
            }
            catch (DatabaseOperationException e1)
            {

            }

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();

            BatchDropDataRow row = (BatchDropDataRow) rows.get(0);

            Object val = row.getValue(3).toString();
            String str = (String) val;
            assertEquals("Completed", str);
            assertEquals(true, row.isDropped());

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);

            row = (BatchDropDataRow) rows.get(1);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("Completed", str);
            assertEquals(true, row.isDropped());

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);

            // Fire Rollback as Commit Failed
            batchDropProvider.rollbackAndNotifyUIMgr(conn);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("To start", str);
            assertEquals(false, row.isDropped());

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);

            row = (BatchDropDataRow) rows.get(1);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("To start", str);
            assertEquals(false, row.isDropped());

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
*/
   /* @Test
    public void test_BatchDataProvider_atomicexecute_CommitAndRollbackFailure()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);

            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);

            batchDropProvider.setAtomic(true);

            assertEquals(batchDropProvider.getTotalObjectCnt(), 2);
            assertEquals(batchDropProvider.isCancelOperation(), false);
            assertEquals(batchDropProvider.isPauseStopOperation(), false);

            Database database = connProfCache.getDbForProfileId(profileId);
            DBConnection conn = database.getFreeConnection();

            exceptionConnection.setThrowExceptionCommit(true);
            exceptionConnection.setSqlException(new SQLException("Error: Failed!!!"));
            // exceptionConnection.setNeedExceptioStatement(true);
            // exceptionConnection.setNeedExecuteException(true);
            try
            {
                batchDropProvider.startExecute(conn);
                fail("Execute should fail");
            }
            catch (DatabaseOperationException e1)
            {

            }

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();

            BatchDropDataRow row = (BatchDropDataRow) rows.get(0);

            Object val = row.getValue(3).toString();
            String str = (String) val;
            assertEquals("Completed", str);
            assertEquals(true, row.isDropped());

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);

            row = (BatchDropDataRow) rows.get(1);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("Completed", str);
            assertEquals(true, row.isDropped());

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);

            // Fire Rollback as Commit Failed
            exceptionConnection.setThrowExceptionRollback(true);
            batchDropProvider.rollbackAndNotifyUIMgr(conn);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("To start", str);
            assertEquals(false, row.isDropped());

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);

            row = (BatchDropDataRow) rows.get(1);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("To start", str);
            assertEquals(false, row.isDropped());

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }*/

    /*@Test
    public void test_BatchDataProvider_execute_PauseCancel()
    {
        try
        {
            ExceptionConnection exceptionConnection = new ExceptionConnection();
            exceptionConnection.setThrowExceptioForPrepareStmt(true);
            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);

            List<ServerObject> objectsToDrop = new ArrayList<ServerObject>();
            objectsToDrop.add((ServerObject) constraintMetaData);
            objectsToDrop.add((ServerObject) newTempColumn);
            BatchDropDataProvider batchDropProvider = new BatchDropDataProvider(objectsToDrop);
            batchDropProvider.init();
            batchDropProvider.setEventTable(eventTable);
            batchDropProvider.setCancelOperation(true);
            batchDropProvider.setPauseStopOperation(true);

            batchDropProvider.setAtomic(false);

            assertEquals(batchDropProvider.getTotalObjectCnt(), 2);
            assertEquals(batchDropProvider.isCancelOperation(), true);
            assertEquals(batchDropProvider.isPauseStopOperation(), true);

            Database database = connProfCache.getDbForProfileId(profileId);
            DBConnection conn = database.getFreeConnection();

            try
            {
                batchDropProvider.startExecute(conn);
            }
            catch (DatabaseOperationException e1)
            {
                fail("Execute should fail");
            }

            List<IDSGridDataRow> rows = batchDropProvider.getAllFetchedRows();

            BatchDropDataRow row = (BatchDropDataRow) rows.get(0);

            Object val = row.getValue(3).toString();
            String str = (String) val;
            assertEquals("Completed", str);
           assertEquals(true, row.isDropped());

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);

            row = (BatchDropDataRow) rows.get(1);

            val = row.getValue(3).toString();
            str = (String) val;
            assertEquals("To start", str);
            assertEquals(false, row.isDropped());

            val = row.getValue(4);
            str = (String) val;
            assertEquals("", str);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }*/
}
