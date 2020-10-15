package com.hauwei.mppdbide.explainplan.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.hauwei.mppdbide.explainplan.mock.CommonLLTUtils;
import com.huawei.mppdbide.explainplan.ui.model.ExplainAnalyzePlanNodeTreeDisplayData;
import com.huawei.mppdbide.explainplan.ui.model.ExplainAnalyzePlanNodeTreeDisplayDataFactory;
import com.huawei.mppdbide.explainplan.ui.model.ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat;
import com.huawei.mppdbide.explainplan.ui.model.TreeGridColumnHeader;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

public class ExplainAnalyzePlanNodeTreeDisplayDataTest
{

    @Test
    public void test_plannode_object()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.HASH_NODE_SAMPLE_JSON);
        
        ExplainAnalyzePlanNodeTreeDisplayData nodeData = 
                ExplainAnalyzePlanNodeTreeDisplayDataFactory.getInstance()
                    .createData(node.getAnalysedPlanNode(), false);
        
        assertEquals(nodeData.getNodeType(), "Hash");
        
        ExplainAnalyzePlanNodeTreeDisplayData nodeData1 = 
                ExplainAnalyzePlanNodeTreeDisplayDataFactory.getInstance()
                    .createData(node.getAnalysedPlanNode(), true);
        
        assertEquals(nodeData1.getNodeType(), "Hash");
        assertEquals(nodeData.compareTo(nodeData1), -1);
        assertFalse(nodeData.equals(nodeData1));
        
    }
    
    @Test
    public void test_plannode_type()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.GROUP_AGGREGATE_NODE_SAMPLE_JSON);
        ExplainAnalyzePlanNodeTreeDisplayData nodeData = 
                ExplainAnalyzePlanNodeTreeDisplayDataFactory.getInstance()
                    .createData(node.getAnalysedPlanNode(), false);
        assertEquals(nodeData.getNodeType(), "GroupAggregate");
        node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.HASH_AGGREGATE_NODE_SAMPLE_JSON);
        nodeData = 
                ExplainAnalyzePlanNodeTreeDisplayDataFactory.getInstance()
                    .createData(node.getAnalysedPlanNode(), false);
        assertEquals(nodeData.getNodeType(), "HashAggregate");
        node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.MIX_AGGREGATE_NODE_SAMPLE_JSON);
        nodeData = 
                ExplainAnalyzePlanNodeTreeDisplayDataFactory.getInstance()
                    .createData(node.getAnalysedPlanNode(), false);
        assertEquals(nodeData.getNodeType(), "MixedAggregate");
        
        node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.HASH_JOIN_NODE_SAMPLE_JSON);
        nodeData = 
                ExplainAnalyzePlanNodeTreeDisplayDataFactory.getInstance()
                    .createData(node.getAnalysedPlanNode(), false);
        assertEquals(nodeData.getNodeType(), "Hash  Join");
        
        node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.STREAMING_GATHER_NODE_SAMPLE_JSON);
        nodeData = 
                ExplainAnalyzePlanNodeTreeDisplayDataFactory.getInstance()
                    .createData(node.getAnalysedPlanNode(), false);
        assertEquals(nodeData.getNodeType(), "Streaming (type: GATHER)");
        node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.SEQ_SCAN_NODE_SAMPLE_JSON);
        nodeData = 
                ExplainAnalyzePlanNodeTreeDisplayDataFactory.getInstance()
                    .createData(node.getAnalysedPlanNode(), false);
        assertEquals(nodeData.getNodeType(), "Seq Scan on gsng_rstore_table ng");
        node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.PARTITION_ITERATOR_NODE_SAMPLE_JSON);
        nodeData = 
                ExplainAnalyzePlanNodeTreeDisplayDataFactory.getInstance()
                    .createData(node.getAnalysedPlanNode(), false);
        assertEquals(nodeData.getNodeType(), "Partition Iterator");
    }
    
    @Test
    public void test_tree_format()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.NESTED_JSON_STREAMGATHER_HASH);
        node.setAnalyze(true);
        ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat tree = new ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat(node);
        List<ExplainAnalyzePlanNodeTreeDisplayData> rows = tree.getNodes(); 
        assertEquals(rows.size(), 2);
        
        assertEquals(rows.get(0).getNodeType(), "Streaming (type: GATHER)");
        assertEquals(rows.get(1).getNodeType(), "Sort");
        
        assertEquals(tree.getColumnDataProvider().getColumnCount(), 10);
        assertEquals(tree.getColumnDataProvider().getColumnName(0), TreeGridColumnHeader.LABEL_NODE_TYPE);
        assertEquals(tree.getColumnDataProvider().getColumnName(1), TreeGridColumnHeader.LABEL_STARTUP_COST);
        assertEquals(tree.getColumnDataProvider().getColumnName(2), TreeGridColumnHeader.LABEL_TOTAL_COST);
        assertEquals(tree.getColumnDataProvider().getColumnName(3), TreeGridColumnHeader.LABEL_ROWS);
        assertEquals(tree.getColumnDataProvider().getColumnName(4), TreeGridColumnHeader.LABEL_WIDTH);
        assertEquals(tree.getColumnDataProvider().getColumnName(5), TreeGridColumnHeader.LABEL_ACTUAL_STARTUP_TIME);
        assertEquals(tree.getColumnDataProvider().getColumnName(6), TreeGridColumnHeader.LABEL_ACTUAL_TOTAL_TIME);
        assertEquals(tree.getColumnDataProvider().getColumnName(7), TreeGridColumnHeader.LABEL_ACTUAL_ROWS);
        assertEquals(tree.getColumnDataProvider().getColumnName(8), TreeGridColumnHeader.LABEL_ACTUAL_LOOPS);
        assertEquals(tree.getColumnDataProvider().getColumnName(9), TreeGridColumnHeader.LABEL_ADDITIONAL_INFO);
        
        assertEquals(rows.get(0).getActualLoops(), 1);
        assertEquals(rows.get(0).getActualRows(), 4);
        assertEquals(rows.get(0).getStartupCost(), 45.38, 0.0);
        assertEquals(rows.get(0).getTotalCost(), 46.03, 0.0);
        assertEquals(rows.get(0).getPlanRows(), 30);
        assertEquals(rows.get(0).getPlanWidth(), 54);
        assertEquals(rows.get(0).getActualStartupTime(), 42.104,0);
        assertEquals(rows.get(0).getActualTotalTime(), 42.105,0);
        assertTrue(!rows.get(0).isCostliest());
        assertTrue(!rows.get(0).isHeaviest());
        assertTrue(rows.get(0).isSlowest());
        
        String additionalInfo = rows.get(0).getAdditionalInfo();
        assertEquals(additionalInfo,"");      
        Object[] values = rows.get(0).getValues();
        assertEquals(values.length, 10,0);
        additionalInfo = rows.get(1).getAdditionalInfo();
        assertEquals(additionalInfo,"Sort Key: o.orderinfo_id"+ MPPDBIDEConstants.LINE_SEPARATOR +"Sort Method: quicksort Memory: 25kB");       
        values = rows.get(1).getValues();
        assertEquals(values.length, 10,0);
        
        assertEquals(rows.get(1).getValue(0), "Sort");
        assertEquals(rows.get(1).getValue(1), 44.76);
        assertEquals(rows.get(1).getValue(2), 44.78);
        assertEquals(rows.get(1).getValue(5), 32.86);
        assertEquals(rows.get(1).getValue(6), 32.86);
        assertEquals(rows.get(1).getValue(9), additionalInfo);
        
        assertTrue(!rows.get(0).equals(rows.get(1)));
        ExplainAnalyzePlanNodeTreeDisplayData o1 = rows.get(0);
        ExplainAnalyzePlanNodeTreeDisplayData o2 = rows.get(0);
        if (o1.equals(new Object()))
        {
            fail("fail");
        }
        if (!o1.equals(o2))
        {
            fail("fail");   
        }
        ExplainAnalyzePlanNodeTreeDisplayData o3 = 
                ExplainAnalyzePlanNodeTreeDisplayDataFactory.getInstance().createData(node.getAnalysedPlanNode(), true);
        if (o1.equals(o3))
        {
            fail("fail");
        }
        o3.setPlanRows(3);
        o3.setAdditionalInfo("");
        o3.getParent();
        o3.setActualRows(3);
        o3.setHeaviest(true);
        o3.setCostliest(true);
        o3.hashCode();
        o3.setNodeType("");
        o3.setActualStartupTime(Double.parseDouble("23"));
        o3.setActualLoops(3);
        o3.setStartupCost(Double.parseDouble("2311"));
        o3.setSlowest(false);
        o3.setPlanWidth(4);
        o3.getPlanWidth();
        o3.setActualTotalTime(Double.parseDouble("23"));
        o3.setTotalCost(Double.parseDouble("2311"));
        if (o1.equals(o3))
        {
            fail("fail");
        }
        
    }
    
    @Test
    public void test_tree_format_fields_check()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.NESTED_JSON_STREAMGATHER_HASH);
        node.setAnalyze(false);
        ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat tree = new ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat(node);
        List<ExplainAnalyzePlanNodeTreeDisplayData> rows = tree.getNodes(); 
        assertEquals(rows.size(), 2);
        
        assertEquals(rows.get(0).getNodeType(), "Streaming (type: GATHER)");
        assertEquals(rows.get(1).getNodeType(), "Sort");
        
        assertEquals(tree.getColumnDataProvider().getColumnCount(), 6);
        assertEquals(tree.getColumnDataProvider().getColumnName(0), TreeGridColumnHeader.LABEL_NODE_TYPE);
        assertEquals(tree.getColumnDataProvider().getColumnName(1), TreeGridColumnHeader.LABEL_STARTUP_COST);
        assertEquals(tree.getColumnDataProvider().getColumnName(2), TreeGridColumnHeader.LABEL_TOTAL_COST);
        assertEquals(tree.getColumnDataProvider().getColumnName(3), TreeGridColumnHeader.LABEL_ROWS);
        assertEquals(tree.getColumnDataProvider().getColumnName(4), TreeGridColumnHeader.LABEL_WIDTH);
        assertEquals(tree.getColumnDataProvider().getColumnName(5), TreeGridColumnHeader.LABEL_ADDITIONAL_INFO);
        
        assertEquals(tree.getRecordCount(), 2);
        assertTrue(!tree.isEndOfRecords());
        try
        {
            List<IDSGridDataRow> allrows = tree.getNextBatch();
        }
        catch (DatabaseOperationException e)
        {
            fail("fail");
        }
        catch (DatabaseCriticalException e)
        {
            fail("fail");
        }
        assertTrue(tree.isEndOfRecords());
        
        tree.preDestroy();
        assertNull(tree.getNodes());
    }
    
    @Test
    public void test_tree_format_fields_check_01()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.NESTED_JSON_STREAMGATHER_HASH);
        node.setAnalyze(false);
        ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat tree = new ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat(node);
        tree.treeExport();
        List<ExplainAnalyzePlanNodeTreeDisplayData> rows = tree.getNodes(); 
        tree.init();
        assertTrue(!tree.isSlowest(1));
        assertNull(tree.getTable());
        tree.getColumnDataProvider();
        tree.setResultTabDirtyFlag(false);
        assertEquals(tree.getResultTabDirtyFlag(), false);
        tree.getDatabse();
        assertTrue(tree.isCostliest(1));
        tree.getPath(rows, rows.get(0));
        assertNotNull(tree.getColumnLabelMap());
        node.setAnalyze(true);
        tree.treeExport();
        try {
            tree.getNextBatch();
            tree.getNextBatch(null);
        } catch (DatabaseOperationException e) {
            fail("not expected to come here");
        } catch (DatabaseCriticalException e) {
            fail("not expected to come here");
        }

    }
    
    @Test
    public void test_tree_format_fields_check_02()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.NESTED_JSON_STREAMGATHER_HASH);
        node.setAnalyze(false);
        ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat tree = new ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat(node);
        List<ExplainAnalyzePlanNodeTreeDisplayData> rows = tree.getNodes(); 
        tree.setFuncProcExport(true);
        tree.setSummary(null);
        assertNull(tree.getSummary());
        assertTrue(!tree.isFuncProcExport());
        assertTrue(tree.allowsChildren(rows.get(0)));
        assertNotNull(tree.getAllFetchedRows()); 
        assertTrue(tree.isHeaviest(1)); 
        assertNotNull(tree.getComparator(1));   
        assertNotNull(tree.getNodes()); 
        assertEquals(tree.getColumnDataProvider().getColumnIndex("test"), -1); 
        assertEquals(tree.getColumnDataProvider().getColumnDesc(1), "Startup Cost"); 
        assertEquals(tree.getColumnDataProvider().getPrecision(1), 0); 
        assertEquals(tree.getColumnDataProvider().getMaxLength(1), 0); 
        assertNull(tree.getColumnDataProvider().getDefaultValue(1)); 
        assertNull(tree.getColumnDataProvider().getComparator(1)); 
        assertEquals(tree.getColumnDataProvider().getColumnDataTypeName(1), "String");
        assertEquals(tree.getColumnDataProvider().getColumnDatatype(1), 12);
        assertNotNull(tree.getColumnDataProvider().getColumnNames());
        assertEquals(tree.getColumnDataProvider().getScale(1), 0);
        
        try {
            tree.init(null, null, false);
            tree.close();
        } catch (DatabaseOperationException e) {
            fail("not expected to come here");
        } catch (DatabaseCriticalException e) {
            fail("not expected to come here");
        }
        

    }
}
