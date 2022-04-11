package org.opengauss.mppdbide.explainplan.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.opengauss.mppdbide.explainplan.mock.CommonLLTUtils;
import org.opengauss.mppdbide.explainplan.nodetypes.BuffersInDetail;
import org.opengauss.mppdbide.explainplan.nodetypes.HashDetail;
import org.opengauss.mppdbide.explainplan.nodetypes.HashJoinDNDetails;
import org.opengauss.mppdbide.explainplan.nodetypes.LLVMDNDetails;
import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNode;
import org.opengauss.mppdbide.explainplan.plannode.CStoreScanNode;
import org.opengauss.mppdbide.explainplan.plannode.CTEScanNode;
import org.opengauss.mppdbide.explainplan.plannode.DataNodeScan;
import org.opengauss.mppdbide.explainplan.plannode.FunctionScanNode;
import org.opengauss.mppdbide.explainplan.plannode.GroupBy;
import org.opengauss.mppdbide.explainplan.plannode.HashAggregate;
import org.opengauss.mppdbide.explainplan.plannode.HashJoin;
import org.opengauss.mppdbide.explainplan.plannode.HashNode;
import org.opengauss.mppdbide.explainplan.plannode.ModifyTableNode;
import org.opengauss.mppdbide.explainplan.plannode.NestLoopJoin;
import org.opengauss.mppdbide.explainplan.plannode.NestedLoopNode;
import org.opengauss.mppdbide.explainplan.plannode.PartitionItetrator;
import org.opengauss.mppdbide.explainplan.plannode.RecursiveUnionNode;
import org.opengauss.mppdbide.explainplan.plannode.RowAdapter;
import org.opengauss.mppdbide.explainplan.plannode.ScanNode;
import org.opengauss.mppdbide.explainplan.plannode.SortDetails;
import org.opengauss.mppdbide.explainplan.plannode.SortNode;
import org.opengauss.mppdbide.explainplan.plannode.StreamGather;
import org.opengauss.mppdbide.explainplan.plannode.StreamRedistribute;
import org.opengauss.mppdbide.explainplan.plannode.UnknownOperator;
import org.opengauss.mppdbide.explainplan.plannode.ValuesScanNode;
import org.opengauss.mppdbide.explainplan.plannode.VectorSetOpNode;
import org.opengauss.mppdbide.explainplan.plannode.WorkTableScanNode;
import org.opengauss.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelContentNode;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelOperationalPlanNode;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

public class NodeTypesTest
{
    @Test
    public void test_stream_gather_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.STREAMING_GATHER_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof StreamGather);
        assertEquals(child.getNodeType(), "Streaming (type: GATHER)");
        assertEquals(child.getStartupCost(), 74.28, 0);
        assertEquals(child.getTotalCost(), 123.37, 0);
        assertEquals(child.getPlanRows(), 30, 0);
        assertEquals(child.getPlanWidth(), 268, 0);
        assertEquals(child.getActualStartupTime(), 33.401, 0);
        assertEquals(child.getActualTotalTime(), 34.134, 0);
        assertEquals(child.getActualLoopCount(), 1, 0);
        assertEquals(child.getOutput().length, 5);  
        
      //  assertEquals(child.getAdditionalInfo(true).size(), 1);
     //   assertEquals(child.getAdditionalInfo(true).get(0), "Nodes: All datanodes");
        
        List<String[]> nodeSpec = child.getNodeSpecificProperties();
        assertEquals(nodeSpec.get(9)[0], "Shared Hit Blocks");
        assertEquals(nodeSpec.get(9)[1], "1");
        assertEquals(nodeSpec.get(10)[0], "Shared Read Blocks");
        assertEquals(nodeSpec.get(10)[1], "2");
        assertEquals(nodeSpec.get(11)[0], "Shared Dirtied Blocks");
        assertEquals(nodeSpec.get(11)[1], "3");
        assertEquals(nodeSpec.get(12)[0], "Shared Written Blocks");
        assertEquals(nodeSpec.get(12)[1], "4");
        assertEquals(nodeSpec.get(13)[1], "5");
        assertEquals(nodeSpec.get(14)[1], "6");
        assertEquals(nodeSpec.get(15)[1], "7");
        assertEquals(nodeSpec.get(16)[1], "8");
        assertEquals(nodeSpec.get(17)[1], "9");
        assertEquals(nodeSpec.get(18)[1], "10");
        
        List<String> spawnOn = child.getAdditionalInfo(true);
        assertTrue(spawnOn.size() == 0);        
        List<String[]> nl = child.getNodeSpecificProperties();
        assertEquals(nl.size(),23);        
    }
    
    @Test
    public void test_hash_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.HASH_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof HashNode);
        assertEquals(child.getNodeType(), "Hash");
        assertEquals(child.getStartupCost(), 74.28, 0);
        assertEquals(child.getTotalCost(), 123.37, 0);
        assertEquals(child.getPlanRows(), 30, 0);
        assertEquals(child.getPlanWidth(), 268, 0);
        assertEquals(child.getActualStartupTime(), 33.401, 0);
        assertEquals(child.getActualTotalTime(), 34.134, 0);
        assertEquals(child.getActualLoopCount(), 1, 0);
        
        assertEquals(child.getAdditionalInfo(true).size(), 2);
        assertEquals(child.getAdditionalInfo(true).get(0), "Max Buckets: 11 Max Batches: 13 (max originally 15)  Max Memory Usage: 3kB");
        assertEquals(child.getAdditionalInfo(true).get(1), "Min Buckets: 2 Min Batches: 4 (min originally 6)  Min Memory Usage: 2kB");
        
        assertTrue(child.toText(true).equals(child.toText(false)));
        List<String> l = child.getNodeSpecific();
        assertNotNull(l);
        List<String[]> nl = child.getNodeSpecificProperties();
        assertEquals(nl.size(),14);
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertTrue(child.getPerDNSpecificDetails(inputMap).size() == 0);
    }
    
    @Test
    public void test_hash_node_hashDetails_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.HASH_NODE_HASH_DETAILS_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof HashNode);
        assertEquals(child.getNodeType(), "Hash");
        assertEquals(child.getStartupCost(), 74.28, 0);
        assertEquals(child.getTotalCost(), 123.37, 0);
        assertEquals(child.getPlanRows(), 30, 0);
        assertEquals(child.getPlanWidth(), 268, 0);
        assertEquals(child.getActualStartupTime(), 33.401, 0);
        assertEquals(child.getActualTotalTime(), 34.134, 0);
        assertEquals(child.getActualLoopCount(), 1, 0);
        
        assertEquals(child.getAdditionalInfo(true).size(), 3);
        assertEquals(child.getAdditionalInfo(true).get(0), "Buckets: 5 Batches: 7 Memory Usage: 3");
        assertEquals(child.getAdditionalInfo(true).get(1), "Max Buckets: 11 Max Batches: 13 (max originally 15)  Max Memory Usage: 3kB");
        assertEquals(child.getAdditionalInfo(true).get(2), "Min Buckets: 2 Min Batches: 4 (min originally 6)  Min Memory Usage: 2kB");
        
        assertTrue(child.toText(true).equals(child.toText(false)));
        List<String> l = child.getNodeSpecific();
        assertNull(l);
        List<String[]> nl = child.getNodeSpecificProperties();
        assertEquals(nl.size(),10);
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertTrue(child.getPerDNSpecificDetails(inputMap).size() == 1);
        List<DNIntraNodeDetailsColumn> colgrp = node.getAnalysedPlanNode().getPerDNSpecificColumnGroupingInfo();
        assertEquals(colgrp.size(), 3);
    }
    
    @Test
    public void test_hash_join_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.HASH_JOIN_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof HashJoin);
        assertEquals(child.getNodeType(), "Hash Join");
        
        List<String[]> nodeSpec = child.getNodeSpecificProperties();
        assertEquals(nodeSpec.get(10)[0], "Join Type");
        assertEquals(nodeSpec.get(10)[1], "Inner");
        assertEquals(nodeSpec.get(11)[0], "Hash Condition");
        assertEquals(nodeSpec.get(11)[1], "(comp.id = cindex.id)");
        
        assertEquals(child.getAdditionalInfo(true).size(), 3);
        assertEquals(child.getAdditionalInfo(true).get(0), "Hash Cond: (comp.id = cindex.id)");
        assertEquals(child.getAdditionalInfo(true).get(1), "Join Filter: filter cond");
        assertEquals(child.getAdditionalInfo(true).get(2), "Rows Removed by Join Filter: 23");
        
        assertTrue(child.toText(true).equals(child.toText(false)));
        List<String> nodeSpecs = child.getNodeSpecific(); 
        assertEquals(nodeSpecs.size(), 1);
        List<DNIntraNodeDetailsColumn> colgrp = node.getAnalysedPlanNode().getPerDNSpecificColumnGroupingInfo();
        assertEquals(colgrp.size(), 3);
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertTrue(child.getPerDNSpecificDetails(inputMap).size() == 1);
    }
    
    @Test
    public void test_stream_redistribute_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.STREAMING_REDISTRIBUTE_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof StreamRedistribute);
        assertEquals(child.getNodeType(), "Streaming(type: REDISTRIBUTE)");
               
        assertEquals(child.getAdditionalInfo(true).size(), 1);
        assertEquals(child.getAdditionalInfo(true).get(0), "Spawn on: (gs_ng) dn_6001_6002");
        
        List<String[]> nodeSpec = child.getNodeSpecificProperties();
        assertEquals(nodeSpec.get(10)[0], "Distribution Key");
        assertEquals(nodeSpec.get(10)[1], "ng.id");
        assertEquals(nodeSpec.get(11)[0], "Spawn On");
        assertEquals(nodeSpec.get(11)[1], "(gs_ng) dn_6001_6002");
    }

    @Test
    public void test_seq_scan_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.SEQ_SCAN_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof ScanNode);
        assertEquals(child.getNodeType(), "Seq Scan");
               
        assertEquals(child.getAdditionalInfo(true).size(), 2);
        assertEquals(child.getAdditionalInfo(true).get(0), "Filter: filter cond");
        assertEquals(child.getAdditionalInfo(true).get(1), "Rows Removed by Filter: 2");
        
        List<String> nodeSpec = child.getNodeSpecific(); 
        assertEquals(nodeSpec.size(), 5);
        List<String[]> nodeSpecProps = child.getNodeSpecificProperties(); 
        assertEquals(nodeSpecProps.size(), 15);
        
        assertTrue(child.getEntityName().equals("gsng_rstore_table as ng"));
        assertTrue(child.getItemName().equals("Table: gsng_rstore_table"));        
        assertTrue(child.toText(true).equals(child.toText(false)));
    }
    
    @Test
    public void test_index_scan_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.INDEX_SCAN_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof ScanNode);
        assertEquals(child.getNodeType(), "Index Scan");
        assertEquals(child.getNodeName(), "Index Scan using idx on gsng_rstore_table ng");
        assertEquals(child.getAdditionalInfo(true).size(), 2);
        assertEquals(child.getAdditionalInfo(true).get(0), "Index Cond: idx >= 2");
        assertEquals(child.getAdditionalInfo(true).get(1), "Filter: filter cond");
        
        List<String> nodeSpec = child.getNodeSpecific(); 
        assertEquals(nodeSpec.size(), 7);
        List<String[]> nodeSpecProps = child.getNodeSpecificProperties(); 
        assertEquals(nodeSpecProps.size(), 20);
        assertTrue(child.toText(true).equals(child.toText(false)));
    }
    
    @Test
    public void test_partition_iterator_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.PARTITION_ITERATOR_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof PartitionItetrator);
        assertEquals(child.getNodeType(), "Partition Iterator");
    }
    @Test
    public void test_row_adapter_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.ROW_ADAPTER_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof RowAdapter);
        assertEquals(child.getNodeType(), "Row Adapter");
    }
    
    @Test
    public void test_partitioned_cstore_scan_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.PARTITIONED_CSTORE_SCAN_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof CStoreScanNode);
        assertEquals(child.getNodeType(), "Partitioned CStore Scan");
    }
    
    @Test
    public void test_hash_aggregate_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.HASH_AGGREGATE_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof HashAggregate);
        assertEquals(child.getNodeType(), "HashAggregate");
        
        assertEquals(child.getAdditionalInfo(true).size(), 4);
        assertEquals(child.getAdditionalInfo(true).get(0), "Group By Key: grp1, grp2");
        assertEquals(child.getAdditionalInfo(true).get(1), "Filter: filter cond");
        assertEquals(child.getAdditionalInfo(true).get(2), "Rows Removed by Filter: 2");
        assertEquals(child.getAdditionalInfo(true).get(3), "Max File Num: 2  Min File Num: 0");
        
        List<String[]> nodeSpec = child.getNodeSpecificProperties();
        assertEquals(nodeSpec.get(10)[0], "Parent Relationship");
        assertEquals(nodeSpec.get(10)[1], "Outer");
        assertEquals(nodeSpec.get(11)[0], "Group By key");
        assertEquals(nodeSpec.get(11)[1], "grp1,grp2");
        
        assertTrue(child.toText(true).equals(child.toText(false)));
        List<DNIntraNodeDetailsColumn> colgrp = node.getAnalysedPlanNode().getPerDNSpecificColumnGroupingInfo();
        assertEquals(colgrp.size(), 3);
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertTrue(child.getPerDNSpecificDetails(inputMap).size() == 1);
        
        node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.GROUP_AGGREGATE_NODE_SAMPLE_JSON);        
        child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof HashAggregate);
        assertEquals(child.getNodeType(), "GroupAggregate");
        
        assertEquals(child.getAdditionalInfo(true).size(), 4);
        assertEquals(child.getAdditionalInfo(true).get(0), "Group By Key: grp");
        assertEquals(child.getAdditionalInfo(true).get(1), "Filter: filter cond");
        assertEquals(child.getAdditionalInfo(true).get(2), "Rows Removed by Filter: 2");
        assertEquals(child.getAdditionalInfo(true).get(3), "Max File Num: 2  Min File Num: 0");        
        node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.MIX_AGGREGATE_NODE_SAMPLE_JSON);
        
        child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof HashAggregate);
        assertEquals(child.getNodeType(), "MixedAggregate");       
        assertEquals(child.getAdditionalInfo(true).size(), 4);
        assertEquals(child.getAdditionalInfo(true).get(0), "Group By Key: grp");
        assertEquals(child.getAdditionalInfo(true).get(1), "Filter: filter cond");
        assertEquals(child.getAdditionalInfo(true).get(2), "Rows Removed by Filter: 2");
        assertEquals(child.getAdditionalInfo(true).get(3), "Max File Num: 2  Min File Num: 0");
    }
    
    @Test
    public void test_sort_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.SORT_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof SortNode);
        assertEquals(child.getNodeType(), "Sort");
        assertEquals(child.getAdditionalInfo(true).size(), 2);
        assertEquals(child.getAdditionalInfo(true).get(0), "Sort Key: o.orderinfo_id");
        assertEquals(child.getAdditionalInfo(true).get(1), "Sort Method: quicksort Memory: 25kB");    
        List<String> l = child.getNodeSpecific();
        assertNotNull(l);
        List<String[]> nl = child.getNodeSpecificProperties();
        assertEquals(nl.size(),12);
    }
    
    @Test
    public void test_nested_loop_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.NESTED_LOOP_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof NestedLoopNode);
        assertEquals(child.getNodeType(), "Nested Loop");
        assertEquals(child.getNodeCategoryName(), MessageConfigLoader.getProperty(
            IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_NESTEDLOOP));
        assertEquals(child.getAdditionalInfo(true).size(), 0);
        assertEquals(child.getNodeSpecificProperties().get(10)[0], "Join Type");
        assertEquals(child.getNodeSpecificProperties().get(10)[1], "Inner");
        assertEquals(child.getNodeSpecificProperties().get(11)[0], "Subplan Name");
        assertEquals(child.getNodeSpecificProperties().get(11)[1], "plan1");
        
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertTrue(child.getPerDNSpecificDetails(inputMap).size() == 1);
    }
    
    @Test
    public void test_nested_loop_join_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.NESTED_LOOP_JOIN_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof NestLoopJoin);
        assertEquals(child.getNodeType(), "Nest Loop Join");
        assertEquals(child.getNodeCategoryName(), MessageConfigLoader.getProperty(
            IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_NESTLOOPJOIN));
        assertEquals(child.getAdditionalInfo(true).size(), 1);
        assertEquals(child.getNodeSpecificProperties().get(10)[0], "Join Type");
        assertEquals(child.getNodeSpecificProperties().get(10)[1], "Inner");
        assertEquals(child.getNodeSpecificProperties().get(11)[0], "Join Filter");
        assertEquals(child.getNodeSpecificProperties().get(11)[1], "filter cond");
        assertEquals(child.getNodeSpecificProperties().get(12)[0], "Rows Removed By Join Filter");
        assertEquals(child.getNodeSpecificProperties().get(12)[1], "23");
        
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(1, child.getPerDNSpecificDetails(inputMap).size());
        assertEquals(child.toString().trim(), "Nest Loop Join");
        assertEquals(null, child.getParent());
        
        ArrayList<String> dnsInvolved = new ArrayList<String>(5);
        assertEquals(1, child.getDNInvolved(dnsInvolved).size());
    }
    
    @Test
    public void test_modify_table_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.FUNCTIONSCAN_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof FunctionScanNode);
        
        List<String> nodeSpec = child.getNodeSpecific(); 
        assertEquals(nodeSpec.size(), 4);
        List<String[]> nodeSpecProps = child.getNodeSpecificProperties(); 
        assertEquals(nodeSpecProps.size(), 14);
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertTrue(child.getPerDNSpecificDetails(inputMap).size() == 0);
        assertTrue(child.getItemName().equals("Function: dummy"));
       
    }
    
    @Test
    public void test_workTableScanNode_methods()
    {
        WorkTableScanNode workTbleScnNde = new WorkTableScanNode();
        assertEquals(0, workTbleScnNde.getAdditionalInfo(true).size());
        assertEquals(10, workTbleScnNde.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, workTbleScnNde.getPerDNSpecificDetails(inputMap).size());
    }
    
    @Test
    public void test_ValuesScanNode_methods()
    {
        ValuesScanNode valuesScanNode = new ValuesScanNode();
        assertEquals(0, valuesScanNode.getNodeSpecific().size());
        assertEquals(10, valuesScanNode.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, valuesScanNode.getPerDNSpecificDetails(inputMap).size());
        assertNull(valuesScanNode.getItemName());
    }
    
    @Test
    public void test_VectorSetOpNode_methods()
    {
        VectorSetOpNode vectorScnNde = new VectorSetOpNode();
        assertEquals(2, vectorScnNde.getNodeSpecific().size());
        assertEquals(12, vectorScnNde.getNodeSpecificProperties().size());
        List<DNIntraNodeDetailsColumn> colGroup = new ArrayList<DNIntraNodeDetailsColumn>(5);
        assertEquals(3, vectorScnNde.getPerDNSpecificColumnGroupingInfo(colGroup).size());
        
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.VECTOR_SET_OP_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof VectorSetOpNode);
        assertEquals(child.getNodeType(), "Vector SetOp");
        assertEquals(child.getNodeCategoryName(), MessageConfigLoader.getProperty(
            IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_SETOP));
        
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(1, child.getPerDNSpecificDetails(inputMap).size());
        assertEquals(null, child.getEntityName());
    }
    
    @Test
    public void test_RecursiveUnionNode_methods()
    {
        RecursiveUnionNode node = new RecursiveUnionNode();
        assertEquals(0, node.getAdditionalInfo(true).size());
        assertEquals(10, node.getNodeSpecificProperties().size());
        assertNull(node.getItemName());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
    }
    
    @Test
    public void test_HashAggregate_methods()
    {
        HashAggregate hashAggregate = new HashAggregate();
        assertEquals(10, hashAggregate.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, hashAggregate.getPerDNSpecificDetails(inputMap).size());
        List<DNIntraNodeDetailsColumn> colGroup = new ArrayList<DNIntraNodeDetailsColumn>(5);
        assertEquals(2, hashAggregate.getPerDNSpecificColumnGroupingInfo(colGroup).size());
    }
    
    @Test
    public void test_CStoreScanNode_methods()
    {
        CStoreScanNode node = new CStoreScanNode();
        assertEquals(1, node.getAdditionalInfo(true).size());
        assertEquals(10, node.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
    }
    
    @Test
    public void test_GroupBy_methods()
    {
        GroupBy node = new GroupBy();
        assertEquals(null, node.getNodeSpecific());
        assertEquals(11, node.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
    }

    @Test
    public void test_CTEScanNode_methods()
    {
        CTEScanNode node = new CTEScanNode();
        assertEquals(0, node.getNodeSpecific().size());
        assertEquals(10, node.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
        assertEquals(null, node.getItemName());
        
        UIModelAnalysedPlanNode cteNode = CommonLLTUtils.json_to_node_test(CommonLLTUtils.CTESCAN_NODE_SAMPLE_JSON);
        
        OperationalNode child = cteNode.getAnalysedPlanNode().getChild();
        assertTrue(child instanceof CTEScanNode);
        assertEquals(child.getNodeType(), "CTE Scan");
        
        assertEquals(child.getAdditionalInfo(true).size(), 0);
        assertTrue(child.toText(true).equals(child.toText(false)));
        List<String> nodeSpecs = child.getNodeSpecific(); 
        assertEquals(nodeSpecs.size(), 2);
        assertEquals("CTE: Inner", child.getItemName());
    }

    @Test
    public void test_DataNodeScan_methods()
    {
        DataNodeScan node = new DataNodeScan();
        assertEquals(2, node.getNodeSpecific().size());
        assertEquals(12, node.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
    }

    @Test
    public void test_ModifyTableNode_methods()
    {
        ModifyTableNode node = new ModifyTableNode();
        assertEquals(5, node.getNodeSpecific().size());
        assertEquals(15, node.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
        assertEquals("Table: ", node.getItemName());
    }

    @Test
    public void test_NestLoopJoin_methods()
    {
        NestLoopJoin node = new NestLoopJoin();
        assertEquals(0, node.getAdditionalInfo(true).size());
        assertEquals(13, node.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
        List<DNIntraNodeDetailsColumn> colGroup = new ArrayList<DNIntraNodeDetailsColumn>(5);
        assertEquals(2, node.getPerDNSpecificColumnGroupingInfo(colGroup).size());
    }

    @Test
    public void test_NestedLoopNode_methods()
    {
        NestedLoopNode node = new NestedLoopNode();
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
        List<DNIntraNodeDetailsColumn> colGroup = new ArrayList<DNIntraNodeDetailsColumn>(5);
        assertEquals(2, node.getPerDNSpecificColumnGroupingInfo(colGroup).size());
    }

    @Test
    public void test_BuffersInDetail_methods()
    {
        BuffersInDetail buffersInDetail = new BuffersInDetail();
        assertEquals(12, buffersInDetail.propertyDetails().size());
        assertNotNull(BuffersInDetail.fillColumnPropertyHeader());
        assertNull(buffersInDetail.getDnName());
    }

    @Test
    public void test_HashDetail_methods()
    {
        HashDetail hashDetail = new HashDetail();
        assertEquals(4, hashDetail.propertyDetails().size());
        assertNotNull(HashDetail.fillColumnPropertyHeader());
        assertNull(hashDetail.getDnName());
    }

    @Test
    public void test_HashJoinDNDetails_methods()
    {
        HashJoinDNDetails hashJoinDNDetail = new HashJoinDNDetails();
        assertNotNull(HashJoinDNDetails.fillColumnPropertyHeader());
        hashJoinDNDetail.setDnName("DB1");
        assertEquals("DB1", hashJoinDNDetail.getDnName());
        hashJoinDNDetail.setMemoryUsed(20);
        assertEquals(20, hashJoinDNDetail.getMemoryUsed(), 0);
    }
    
    @Test
    public void test_HashJoinDNDetails_methods1()
    {
        HashJoinDNDetails hashJoinDNDetail = new HashJoinDNDetails();
        ArrayList<OperationalNode> childNodes = new ArrayList<OperationalNode>(1);
        hashJoinDNDetail.setDnName("DB1");
        hashJoinDNDetail.setMemoryUsed(20);
        hashJoinDNDetail.propertyDetails(childNodes);
        assertEquals(20, hashJoinDNDetail.getMemoryUsed(), 0);
    }

    @Test
    public void test_LLVMDNDetails_methods()
    {
        LLVMDNDetails details = new LLVMDNDetails();
        assertEquals(1, details.propertyDetails().size());
        assertNotNull(LLVMDNDetails.fillColumnPropertyHeader());
        assertNull(details.getDnName());
    }
    
    @Test
    public void test_UIModelOperationalPlanNode_methods()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.FUNCTIONSCAN_NODE_SAMPLE_JSON);
        OperationalNode opNode = node.getAnalysedPlanNode().getChild();
        UIModelOperationalPlanNode uiModelOpPlanNode = new UIModelOperationalPlanNode(opNode);
        assertNotNull(uiModelOpPlanNode.toString());
        assertEquals("Output : []", uiModelOpPlanNode.getOutput());
        assertNull(uiModelOpPlanNode.getNodeSpecific());
        assertNotNull(uiModelOpPlanNode.getParentRelationship());
        UIModelOperationalPlanNode parent = new UIModelOperationalPlanNode(opNode);
        uiModelOpPlanNode.setParent(parent);
        assertEquals(parent, uiModelOpPlanNode.getParent());
        UIModelOperationalPlanNode child1 = new UIModelOperationalPlanNode(opNode);
        uiModelOpPlanNode.addChildNode(child1);
        assertNotNull(uiModelOpPlanNode.toString());
    }

    @Test
    public void test_UIModelContentNode_methods()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.FUNCTIONSCAN_NODE_SAMPLE_JSON);
        OperationalNode opNode = node.getAnalysedPlanNode().getChild();
        UIModelOperationalPlanNode uiModelOpPlanNode = new UIModelOperationalPlanNode(opNode);
        UIModelContentNode uiModelContentNode = new UIModelContentNode(null);
        assertNull(uiModelContentNode.getLabelText());
        assertNotNull(UIModelContentNode.getNodeContents(uiModelOpPlanNode));
    }

    @Test
    public void test_UIModelAnalysedPlanNode_methods()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.FUNCTIONSCAN_NODE_SAMPLE_JSON);
        assertNotNull(node.getActualRecordCount());

        assertEquals("1. Function Scan", node.getNodeTitle());
        assertNotNull(node.getNodeType());
        assertNotNull(node.getActualMaxTimeTaken());
        assertNotNull(node.getTotalTimeContributionPercentage());
        assertNotNull(node.getModelChildren());
        assertNotNull(node.getSourceRelationship());
        assertNotNull(node.getTargetRelationship());
        assertNotNull(node.getParentRelationship());
        assertNotNull(node.getPlanRecordCount());
        assertNotNull(node.getModelInTextFormat(20));
        assertNull(node.getNodeSpecificProperties());
        assertNotNull(node.getNodeSpecific());
        assertNotNull(node.getOutput());
    }
     
    @Test
    public void test_fillDNCPU_node_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.HASH_EXEC_CYCLE_AGGREGATE_NODE_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        List<String[]> nodeSpec = child.getNodeSpecificProperties();
        
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        
        Map<String, Object[]> inputMap1 = new HashMap<String, Object[]>(1);
        Object[] objArr  = {"obj1"};
        inputMap1.put("key", objArr);
        
        child.addNodeDNPlanView(inputMap1, 0);
        assertTrue(child.getPerDNSpecificDetails(inputMap).size() == 1);
        assertNotNull(nodeSpec);
        
    }
    
	@Test
	public void test_sortdeatils() {
		SortDetails sortDetails = new SortDetails();
		String json = "{\"Sort Key\": [\"key1\",\"key2\"]}";
		Gson gson = new Gson();
		try {
			sortDetails = gson.fromJson(json, SortDetails.class);
		} catch (JsonSyntaxException excep) {
			excep.printStackTrace();
		}
		assertNotNull(sortDetails.getSortDetails());
	}
	
	
	@Test
    public void test_basic_node_fill_info()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.SEQ_SCAN_EXEC_NODE_SAMPLE_JSON);
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        child.addChildNode(child);
        child.getAdditionalInfo(true);
        List<String[]> nodeSpecProps = child.getNodeSpecificProperties(); 
        child.addChildNode(child);
        child.addChildNode(child);
        nodeSpecProps = child.getNodeSpecificProperties(); 
        assertNotNull(nodeSpecProps);

    }
	
	
	@Test
    public void test_basic_node_additionalinfo()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.NESTED_LOOP_BUFFER_SAMPLE_JSON);
        
        OperationalNode child = node.getAnalysedPlanNode().getChild();
        child.addChildNode(child);
        child.addChildNode(child);
        List<String[]> nodeSpecProps = child.getNodeSpecificProperties(); 
        assertNotNull(nodeSpecProps);
        assertNotNull(child.getAdditionalInfo(true));
        
    }
	
	@Test
    public void test_unknownOperator_01()
    {
	    UnknownOperator unknwnOp = new UnknownOperator();
	    assertEquals("", unknwnOp.getNodeCategoryName());
    }
}
