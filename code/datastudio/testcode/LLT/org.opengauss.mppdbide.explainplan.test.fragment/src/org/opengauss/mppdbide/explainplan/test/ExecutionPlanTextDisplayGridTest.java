package org.opengauss.mppdbide.explainplan.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Types;
import java.util.List;

import org.junit.Test;

import org.opengauss.mppdbide.explainplan.mock.CommonLLTUtils;
import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.presentation.visualexplainplan.ExecutionPlanTextDisplayGrid;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

public class ExecutionPlanTextDisplayGridTest
{
    
    @Test
    public void test_stream_gather_text_display_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.STREAMING_GATHER_NODE_SAMPLE_JSON);
        node.setAnalyze(true);
        
        ExecutionPlanTextDisplayGrid grid = new ExecutionPlanTextDisplayGrid(node, 0.0);
        try
        {
            grid.init();
        }
        catch (DatabaseOperationException e2)
        {
            fail("fail");
        }
        catch (DatabaseCriticalException e2)
        {
            fail("fail");
        }
        assertNull(grid.getDatabse());
        List<IDSGridDataRow> rowData = null;
        try
        {
            rowData = grid.getNextBatch();
        }
        catch (DatabaseOperationException | DatabaseCriticalException e1)
        {
            fail("fail");
        }
        
        assertEquals(rowData.size(), 2);
        assertTrue(((String) rowData.get(0).getValue(0)).contains("Streaming (type: GATHER)"));
       // assertTrue(((String) rowData.get(1).getValue(0)).contains("All datanodes"));
        assertTrue(((String) rowData.get(1).getValue(0)).contains("Total runtime: 0.0ms"));
        try
        {
            assertNull(grid.getNextBatch());
        }
        catch (DatabaseOperationException e)
        {
            fail("fail");
        }
        catch (DatabaseCriticalException e)
        {
            fail("fail");
        }
        assertTrue(grid.isEndOfRecords());
        assertEquals(grid.getRecordCount(), 2);
        
        assertNull(grid.getColumnGroupProvider());
        assertNull(grid.getTable());
        assertFalse(grid.getResultTabDirtyFlag());
        assertEquals(rowData.size(), grid.getAllFetchedRows().size());
        
        assertEquals(grid.getColumnDataProvider().getColumnCount(), 1);
        assertEquals(grid.getColumnDataProvider().getColumnDataTypeName(0), "STRING");
        assertEquals(grid.getColumnDataProvider().getColumnDatatype(0), Types.VARCHAR);
        assertEquals(grid.getColumnDataProvider().getColumnName(0), MessageConfigLoader.getProperty(
                IMessagesConstants.COLUMN_HEADER_QUERY_PLAN));
    }
    
    @Test
    public void test_nested_plan_text_display()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.NESTED_JSON_STREAMGATHER_HASH);
        node.setAnalyze(true);
        ExecutionPlanTextDisplayGrid grid = new ExecutionPlanTextDisplayGrid(node, 11.0);
        try
        {
            grid.init();
        }
        catch (DatabaseOperationException e2)
        {
            fail("fail");
        }
        catch (DatabaseCriticalException e2)
        {
            fail("fail");
        }
        assertNull(grid.getDatabse());
        List<IDSGridDataRow> rowData = null;
        try
        {
            rowData = grid.getNextBatch();
        }
        catch (DatabaseOperationException | DatabaseCriticalException e1)
        {
            fail("fail");
        }
        
        assertEquals(rowData.size(), 5);
        assertTrue(((String) rowData.get(0).getValue(0)).contains("Streaming (type: GATHER) (cost=45.38..46.03 rows=30 width=54) (actual time=42.104..42.105 rows=4 loops=1)"));
        assertTrue(((String) rowData.get(1).getValue(0)).contains("        ->  Sort (cost=44.76..44.78 rows=30 width=54) (actual time=[29.919,32.86]..[29.921,32.86] rows=4)"));
        assertTrue(((String) rowData.get(2).getValue(0)).contains("            Sort Key: o.orderinfo_id"));
        assertTrue(((String) rowData.get(3).getValue(0)).contains("            Sort Method: quicksort Memory: 25kB"));
        assertTrue(((String) rowData.get(4).getValue(0)).contains("Total runtime: 11.0ms"));
        try
        {
            assertNull(grid.getNextBatch());
        }
        catch (DatabaseOperationException e)
        {
            fail("fail");
        }
        catch (DatabaseCriticalException e)
        {
            fail("fail");
        }
        assertTrue(grid.isEndOfRecords());
        assertEquals(grid.getRecordCount(), 5);
        
        IQueryExecutionSummary summary = grid.getSummary();
        grid.setSummary(summary);
        assertNull(grid.getColumnGroupProvider());
        assertNull(grid.getTable());
        try {
			assertNull(grid.getNextBatch());
		} catch (DatabaseOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabaseCriticalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertFalse(grid.getResultTabDirtyFlag());
        assertEquals(rowData.size(), grid.getAllFetchedRows().size());
        
        assertFalse(grid.isFuncProcExport());
        assertEquals(grid.getColumnDataProvider().getColumnCount(), 1);
        assertEquals(grid.getColumnDataProvider().getColumnDataTypeName(0), "STRING");
        assertEquals(grid.getColumnDataProvider().getColumnDatatype(0), Types.VARCHAR);
        assertEquals(grid.getColumnDataProvider().getColumnName(0), MessageConfigLoader.getProperty(
                IMessagesConstants.COLUMN_HEADER_QUERY_PLAN));
        grid.preDestroy();
        grid.setResultTabDirtyFlag(false);
        grid.setFuncProcExport(false);
        try {
            grid.close();
        } catch (DatabaseOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DatabaseCriticalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
