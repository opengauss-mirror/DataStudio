package org.opengauss.mppdbide.test.presentation.table;

import java.text.ParseException;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.presentation.CanContextContinueExecuteRule;
import org.opengauss.mppdbide.presentation.ContextExecutionOperationType;
import org.opengauss.mppdbide.presentation.DummyTerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.presentation.ExecutionFailureActionOptions;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.presentation.exportdata.ImportExportDataCore;
import org.opengauss.mppdbide.presentation.objectproperties.ColumnValuePropertiesComparator;
import org.opengauss.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import org.opengauss.mppdbide.presentation.objectproperties.DSGeneraicGridDataProvider;
import org.opengauss.mppdbide.presentation.objectproperties.DSGenericGridColumnProvider;
import org.opengauss.mppdbide.presentation.objectproperties.DSGenericGroupedGridColumnProvider;
import org.opengauss.mppdbide.presentation.objectproperties.DSObjectPropertiesGridColumnDataProvider;
import org.opengauss.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import org.opengauss.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataRow;
import org.opengauss.mppdbide.presentation.util.DataTypeUtility;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;

public class DNIntraNodeDetailsColumnTest extends BasicJDBCTestCaseAdapter
{
    DummyTerminalExecutionConnectionInfra    dummyconn                = null;
    ExecutionFailureActionOptions            enumObj                  = null;
    TerminalExecutionConnectionInfra         termConnInfra            = null;
    DNIntraNodeDetailsColumn                 dnDetailCol              = null;
    ImportExportDataCore                     impoExpoCore             = null;
    DSObjectPropertiesGridColumnDataProvider objectproertiescolData   = null;
    DSObjectPropertiesGridDataRow            dsGridDataRow            = null;
    DSGenericGridColumnProvider              dsgenericGridColProvider = null;
    DSGenericGroupedGridColumnProvider       dsColProvider            = null;
    DSGeneraicGridDataProvider               dsDataProvider           = null;
    DSObjectPropertiesGridDataProvider       dsObjectGridDataProvider = null;

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
    public void setUp() throws Exception
    {
        CommonLLTUtils.runLinuxFilePermissionInstance();
        dummyconn = new DummyTerminalExecutionConnectionInfra();
        termConnInfra = new TerminalExecutionConnectionInfra();
        dnDetailCol = new DNIntraNodeDetailsColumn();
        objectproertiescolData = new DSObjectPropertiesGridColumnDataProvider();
        Object[] rows = {1, "name"};
        dsGridDataRow = new DSObjectPropertiesGridDataRow(rows);
        dsgenericGridColProvider = new DSGenericGridColumnProvider();
        List<DNIntraNodeDetailsColumn> colgrpDetails = new ArrayList<DNIntraNodeDetailsColumn>();
        DNIntraNodeDetailsColumn element = new DNIntraNodeDetailsColumn();
        element.setColCount(1);
        element.setGroupColumnName("Columns");
        List<String> colList = new ArrayList<String>();
        colList.add(0, "name");
        colList.add(1, "types");
        colgrpDetails.add(0, element);
        colgrpDetails.add(1, element);
        element.setColnames(colList);
        dsColProvider = new DSGenericGroupedGridColumnProvider(colgrpDetails);
        List<Object[]> colList1 = new ArrayList<Object[]>();
        String[] element1 = {"name", "desc", "type", "table"};
        colList1.add(0, element1);
        colList1.add(1, element1);
        dsDataProvider = new DSGeneraicGridDataProvider(colList1, "name", colgrpDetails);

        List<String[]> colListArr = new ArrayList<String[]>();
        String[] data = {"name", "desc", "type", "table"};
        colListArr.add(0, data);
        colListArr.add(1, data);
        dsObjectGridDataProvider = new DSObjectPropertiesGridDataProvider(colListArr, "name", null, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    @After
    public void tearDown() throws Exception
    {

    }

    @Test
    public void test_Dummy_Connection()
    {
        dummyconn.isConnected();
        assertNotNull(ExecutionFailureActionOptions.EXECUTION_FAILURE_ACTION_ABORT);
        assertNotNull(ExecutionFailureActionOptions.EXECUTION_FAILURE_ACTION_CONTINUE);
        assertEquals(dummyconn.isConnected(), true);
    }

    @Test
    public void test_terminal_execution_connection_infra()
    {
        if (termConnInfra != null)
        {
            termConnInfra.setAutoCommitFlag(true);
            termConnInfra.getAutoCommitFlag();
            termConnInfra.resetInformation();
            termConnInfra.releaseConnection();
            termConnInfra.isConnected();
            assertEquals(termConnInfra.isConnected(), false);
            assertEquals(termConnInfra.getAutoCommitFlag(), true);
        }
    }

    @Test
    public void test_CanContextContinueExecuteRule()
    {
        assertNotNull(CanContextContinueExecuteRule.CONTEXT_EXECUTION_PROCEED);
        assertNotNull(CanContextContinueExecuteRule.CONTEXT_EXECUTION_STOP);
        assertNotNull(CanContextContinueExecuteRule.CONTEXT_EXECUTION_UNKNOWN);
    }

    @Test
    public void test_ContextExecutionOperationType()
    {
        assertNotNull(ContextExecutionOperationType.CONTEXT_OPERATION_TYPE_NEW_PL_SQL_CREATION);
        assertNotNull(ContextExecutionOperationType.CONTEXT_OPERATION_TYPE_PL_SQL_COMPILATION);
        assertNotNull(ContextExecutionOperationType.CONTEXT_OPERATION_TYPE_SQL_TERMINAL_EXECUTION);
        assertNotNull(ContextExecutionOperationType.CONTEXT_OPERATION_TYPE_VIEW_OBJECT_DATA);
        assertNotNull(ContextExecutionOperationType.CONTEXT_OPERATION_TYPE_VIEW_OBJECT_PROPERTY);
    }

    @Test
    public void test_DNIntraNodeDetailsColumn()
    {
        List<String> colnames = new ArrayList<String>();
        colnames.add("A");
        colnames.add("B");
        colnames.add("C");
        dnDetailCol.setColCount(5);
        dnDetailCol.getColCount();
        dnDetailCol.setColnames(colnames);
        dnDetailCol.getColnames();
        dnDetailCol.setGroupColumnName("Columns");
        dnDetailCol.getGroupColumnName();
        assertNotNull(colnames.size());
        assertEquals(colnames.size(), 3);
        assertEquals(dnDetailCol.getColCount(), 5);
    }

    @Test
    public void test_DataTypeUtility_convertToDateObj()
    {
        try
        {
            java.util.Date date = DataTypeUtility.convertToDateObj("2018-02-27 12:10:22", "yyyy-MM-dd HH:mm:ss");
            assertNotNull(date);
        }
        catch (ParseException e)
        {
            fail("Not expected to come here");
        }
    }

    @Test
    public void test_DataTypeUtility_convertToTimeObj()
    {
        try
        {
            java.util.Date date = DataTypeUtility.convertToTimeObj("12:10:2200", "HH:mm:ss");
            assertNotNull(date);
        }
        catch (ParseException e)
        {
            fail("Not expected to come here");
        }
    }

    @Test
    public void test_DataTypeUtility_convertToTimeObj1()
    {

        assertEquals("char", DataTypeUtility.convertToDisplayDatatype("bpchar"));
        assertEquals("boolean", DataTypeUtility.convertToDisplayDatatype("bool"));
        assertEquals("binary double", DataTypeUtility.convertToDisplayDatatype("float"));
        assertEquals("smallint", DataTypeUtility.convertToDisplayDatatype("int2"));
        assertEquals("integer", DataTypeUtility.convertToDisplayDatatype("int4"));
        assertEquals("bigint", DataTypeUtility.convertToDisplayDatatype("int8"));
        assertEquals("double precision", DataTypeUtility.convertToDisplayDatatype("float8"));
        assertEquals("real", DataTypeUtility.convertToDisplayDatatype("float4"));
        assertEquals("time with time zone", DataTypeUtility.convertToDisplayDatatype("timetz"));
        assertEquals("timestamp with time zone", DataTypeUtility.convertToDisplayDatatype("timestamptz"));
        assertEquals("time without time zone", DataTypeUtility.convertToDisplayDatatype("time"));
        assertEquals("timestamp without time zone", DataTypeUtility.convertToDisplayDatatype("timestamp"));
    }

    @Test
    public void test_DSObjectPropertiesGridColumnDataProvider()
    {
        if (objectproertiescolData != null)
        {
            List<String[]> colList = new ArrayList<String[]>();
            String[] element = {"name", "desc", "type", "table"};
            colList.add(0, element);
            colList.add(1, element);
            objectproertiescolData.init(colList);
            objectproertiescolData.getColumnCount();
            objectproertiescolData.getColumnName(0);
            objectproertiescolData.getColumnNames();
            objectproertiescolData.getColumnDesc(0);
            objectproertiescolData.getColumnIndex("ABC");
            ColumnValuePropertiesComparator<Object> comparator = (ColumnValuePropertiesComparator<Object>) objectproertiescolData.getComparator(0);
            comparator.compare(new Object(), new Object());
            objectproertiescolData.getColumnDatatype(0);
            objectproertiescolData.getColumnDataTypeName(0);
            objectproertiescolData.getPrecision(0);
            objectproertiescolData.getScale(0);
            objectproertiescolData.getMaxLength(0);
            assertEquals(objectproertiescolData.getColumnCount(), 4);
            assertEquals(objectproertiescolData.getColumnName(0), "name");
            assertEquals(objectproertiescolData.getColumnDesc(0), "name");
        }
    }

    @Test
    public void test_DSObjectPropertiesGridDataRow()
    {
        if (dsGridDataRow != null)
        {
            dsGridDataRow.getValue(1);
            dsGridDataRow.getClonedValues();
            assertEquals(dsGridDataRow.getValue(1), "name");
        }
    }

    @Test
    public void test_DSGenericGridColumnProvider()
    {
        if (dsgenericGridColProvider != null)
        {
            List<Object[]> colList = new ArrayList<Object[]>();
            String[] element = {"name", "desc", "type", "table"};
            colList.add(0, element);
            colList.add(1, element);
            dsgenericGridColProvider.init(colList);
            dsgenericGridColProvider.getColumnCount();
            dsgenericGridColProvider.getColumnDatatype(0);
            dsgenericGridColProvider.getColumnDataTypeName(0);
            dsgenericGridColProvider.getColumnDesc(0);
            dsgenericGridColProvider.getColumnIndex("name");
            dsgenericGridColProvider.getColumnName(0);
            dsgenericGridColProvider.getColumnNames();
            dsgenericGridColProvider.getComparator(0);
            dsgenericGridColProvider.getPrecision(0);
            dsgenericGridColProvider.getScale(0);
            dsgenericGridColProvider.getMaxLength(0);

            assertEquals(dsgenericGridColProvider.getColumnCount(), 4);
            assertEquals(dsgenericGridColProvider.getColumnName(0), "name");
            assertEquals(dsgenericGridColProvider.getColumnDesc(0), "name");
        }
    }

    @Test
    public void test_DSGenericGroupedGridColumnProvider()
    {
        if (dsColProvider != null)
        {
            dsColProvider.getColumnGroupIndex(1);
            dsColProvider.getColumnGroupName(1);
            dsColProvider.getColumnIndexInGroup(1);
            dsColProvider.getGroupCount();

            assertEquals(dsColProvider.getGroupCount(), 2);
            assertEquals(dsColProvider.getColumnGroupName(1), "Columns");
        }
    }

    @Test
    public void test_DSGeneraicGridDataProvider()
    {
        if (dsDataProvider != null)
        {
            try
            {
                dsDataProvider.init();
                assertTrue(true);
            }
            catch (DatabaseOperationException e1)
            {
                fail("not expected to come here");
            }
            catch (DatabaseCriticalException e1)
            {
                fail("not expected to come here");
            }
            dsDataProvider.getAllFetchedRows();

            dsDataProvider.getColumnDataProvider();
            dsDataProvider.getColumnGroupProvider();
            try
            {
                dsDataProvider.getNextBatch();
                assertEquals(dsDataProvider.getNextBatch(), null);
            }
            catch (DatabaseOperationException e)
            {
                fail("not expected to come here");
            }
            catch (DatabaseCriticalException e)
            {
                fail("not expected to come here");
            }
            dsDataProvider.getObjectPropertyName();
            assertEquals(dsDataProvider.getObjectPropertyName(), "name");
            dsDataProvider.getRecordCount();
            assertEquals(dsDataProvider.getRecordCount(), 0);
            dsDataProvider.isEndOfRecords();
            try
            {
                dsDataProvider.close();
            }
            catch (DatabaseOperationException e)
            {
                fail("not expected to come here");
            }
            catch (DatabaseCriticalException e)
            {
                fail("not expected to come here");
            }
        }
    }

    @Test
    public void test_DSObjectPropertiesGridDataProvider()
    {
        if (dsObjectGridDataProvider != null)
        {
            try
            {
                dsObjectGridDataProvider.init();
                assertTrue(true);
            }
            catch (DatabaseOperationException e)
            {
                fail("not expected to come here");
            }
            catch (DatabaseCriticalException e)
            {
                fail("not expected to come here");
            }
            dsObjectGridDataProvider.getAllFetchedRows();
            dsObjectGridDataProvider.getColumnDataProvider();
            dsObjectGridDataProvider.getColumnGroupProvider();
            assertNull(dsObjectGridDataProvider.getTableName());
            dsObjectGridDataProvider.setResultTabDirtyFlag(false);
            assertNull(dsObjectGridDataProvider.getColumnNames());
            assertNull(dsObjectGridDataProvider.getEmptyRowForInsert(0));
            assertEquals(0,dsObjectGridDataProvider.getColumnCount());
            dsObjectGridDataProvider.setCancel(false);
            assertEquals(false, dsObjectGridDataProvider.isUniqueKeyPresent());
            assertEquals(false, dsObjectGridDataProvider.isDistributionColumnsRequired());
            dsObjectGridDataProvider.setFuncProcExport(false);
            try
            {
                dsObjectGridDataProvider.getNextBatch();
                assertEquals(dsObjectGridDataProvider.getNextBatch(), null);
            }
            catch (DatabaseOperationException e)
            {
                fail("not expected to come here");
            }
            catch (DatabaseCriticalException e)
            {
                fail("not expected to come here");
            }
            dsObjectGridDataProvider.getObjectPropertyName();

            assertEquals(dsObjectGridDataProvider.getObjectPropertyName(), "name");
            dsObjectGridDataProvider.getRecordCount();
            System.out.println("DNIntraNodeDetailsColumnTest.test_DSObjectPropertiesGridDataProvider()"
                    + dsObjectGridDataProvider.getRecordCount());
            assertEquals(dsObjectGridDataProvider.getRecordCount(), 1);
            dsObjectGridDataProvider.isEndOfRecords();

            assertEquals(dsObjectGridDataProvider.isEndOfRecords(), true);
            assertEquals(false, dsObjectGridDataProvider.getResultTabDirtyFlag());
            assertEquals(false, dsObjectGridDataProvider.isDistributionColumn(0));
            assertEquals(false, dsObjectGridDataProvider.isFuncProcExport());
            try {
                dsObjectGridDataProvider.cancelCommit();
                boolean canc = dsObjectGridDataProvider.isCancelled();
                assertEquals(canc, dsObjectGridDataProvider.isCancelled());
                dsObjectGridDataProvider.preDestroy();
            } catch (DatabaseCriticalException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (DatabaseOperationException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try
            {
                dsObjectGridDataProvider.close();
            }
            catch (DatabaseOperationException e)
            {
                fail("not expected to come here");
            }
            catch (DatabaseCriticalException e)
            {
                fail("not expected to come here");
            }
        }
    }
}
