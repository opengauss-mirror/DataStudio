package com.hauwei.mppdbide.explainplan.mock;

import static org.junit.Assert.fail;

import com.huawei.mppdbide.explainplan.service.ExplainPlanAnlysisService;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelConverter;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

public class CommonLLTUtils
{
    public static final String STREAMING_GATHER_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Streaming (type: GATHER)\",\"Startup Cost\": 74.28,"
            + "\"Total Cost\": 123.37,\"Plan Rows\": 30,\"Plan Width\": 268,\"Actual Startup Time\": 33.401,"
            + "\"Actual Total Time\": 34.134,\"Actual Rows\": 150,\"Actual Loops\": 1,\"Output\": [\"comp.name\", "
            + "\"cindex.idx_col\", \"ng.dob\", \"part.part_range\", \"cpart.part_range\"],\"Nodes\": "
            + "\"All datanodes\",\"Shared Hit Blocks\": 1,\"Shared Read Blocks\": 2,\"Shared Dirtied Blocks\": 3,"
            + "\"Shared Written Blocks\": 4,\"Local Hit Blocks\": 5,\"Local Read Blocks\": 6,\"Local Dirtied Blocks\": 7,"
            + "\"Local Written Blocks\": 8,\"Temp Read Blocks\": 9,\"Temp Written Blocks\": 10,\"IO Read Time\": 0.000,"
            + "\"IO Write Time\": 0.000}}]";
    
    public static final String HASH_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Hash\",\"Startup Cost\": 74.28,"
            + "\"Total Cost\": 123.37,\"Plan Rows\": 30,\"Plan Width\": 268,\"Actual Startup Time\": 33.401,"
            + "\"Actual Total Time\": 34.134,\"Actual Rows\": 150,\"Actual Loops\": 1,"
            + "\"Max Hash Buckets\": 11,\"Min Hash Buckets\": 2,\"Max Hash Batches\": 13,"
            + "\"Min Hash Batches\": 4,\"Max Original Hash Batches\": 15,\"Min Original Hash Batches\": 6,\"Max Peak Memory Usage\": 3,"
            + "\"min Peak Memory Usage\": 2,\"Temp Read Blocks\": 9,\"Temp Written Blocks\": 10,\"IO Read Time\": 0.000,"
            + "\"IO Write Time\": 0.000}}]";
    
    public static final String HASH_NODE_HASH_DETAILS_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Hash\",\"Startup Cost\": 74.28,"
            + "\"Total Cost\": 123.37,\"Plan Rows\": 30,\"Plan Width\": 268,\"Actual Startup Time\": 33.401,"
            + "\"Actual Total Time\": 34.134,\"Actual Rows\": 150,\"Actual Loops\": 1,"
            + "\"Hash Buckets\": 5, \"Hash Batches\": 7, \"Original Hash Batches\": 9, \"Peak Memory Usage\": 3,"
            + "\"Hash Detail\": [{\"DN Name\":\"DB1\", \"Hash Buckets\": 5, \"Hash Batches\": 7, \"Original Hash Batches\": 9, \"Peak Memory Usage\": 3}],"
            + "\"Max Hash Buckets\": 11,\"Min Hash Buckets\": 2,\"Max Hash Batches\": 13,"
            + "\"Min Hash Batches\": 4,\"Max Original Hash Batches\": 15,\"Min Original Hash Batches\": 6,\"Max Peak Memory Usage\": 3,"
            + "\"min Peak Memory Usage\": 2,\"Temp Read Blocks\": 9,\"Temp Written Blocks\": 10,\"IO Read Time\": 0.000,"
            + "\"IO Write Time\": 0.000}}]";
    
    public static final String NESTED_JSON_STREAMGATHER_HASH = 
            "[{\"Plan\": {\"Node Type\": \"Streaming (type: GATHER)\",\"Startup Cost\": 45.38,\"Total Cost\": 46.03,"
            + "\"Plan Rows\": 30,\"Plan Width\": 54,\"Actual Startup Time\": 42.104,\"Actual Total Time\": 42.105,"
            + "\"Actual Rows\": 4,\"Actual Loops\": 1,\"Output\": [\"o.orderinfo_id\", "
            + "\"(sum((i.sell_price * (l.quantity)::numeric)))\"],\"Nodes\": \"All datanodes\","
            + "\"Plans\": [{\"Node Type\": \"Sort\",\"Parent Relationship\": \"Outer\",\"Startup Cost\": 44.76,"
            + "\"Total Cost\": 44.78,\"Plan Rows\": 30,\"Plan Width\": 54,\"Actual Min Startup Time\": 29.919,"
            + "\"Actual Max Startup Time\": 32.860,\"Actual Min Total Time\": 29.921,\"Actual Max Total Time\": 32.860,"
            + "\"Actual Total Rows\": 4,\"Output\": [\"o.orderinfo_id\", \"(sum((i.sell_price * "
            + "(l.quantity)::numeric)))\"],\"Sort Key\": [\"o.orderinfo_id\"],\"Sort Method\": \"quicksort\","
            + "\"Sort Space Used\": 25,\"Sort Space Type\": \"Memory\"}]}}]";
    
    public static final String HASH_JOIN_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Hash Join\",\"Join Type\": \"Inner\","
            + "\"Hash Cond\": \"(comp.id = cindex.id)\", \"VecJoin Detail\": [{\"DN Name\": \"DB1\", \"Memory Used\": 0 }],"
            + " \"Join Filter\": \"filter cond\", \"Rows Removed by Join Filter\": 23 }}]";
    
    public static final String STREAMING_REDISTRIBUTE_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Streaming(type: REDISTRIBUTE)\",\"Distribute Key\": [\"ng.id\"],"
            + "\"Spawn on\": \"(gs_ng) dn_6001_6002\"}}]";
    
    public static final String SEQ_SCAN_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Seq Scan\",\"Relation Name\": \"gsng_rstore_table\","
            + "\"Schema\": \"pschema\", \"Alias\": \"ng\", \"Filter\": \"filter cond\","
            + "\"Rows Removed by Filter\": 2}}]";
    
    public static final String INDEX_SCAN_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Index Scan\",\"Relation Name\": \"gsng_rstore_table\","
            + "\"Index Name\": \"idx\", \"Alias\": \"ng\", \"Filter\": \"filter cond\","
            + "\"Index Cond\": \"idx >= 2\","
            + "\"Rows Removed by Filter\": 2}}]";
    
    public static final String FUNCTIONSCAN_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Function Scan\",\"Function Name\": \"dummy\","
            + "\"Alias\": \"ng\","
            + "\"Schema\": \"public\"}}]";
    
    public static final String PARTITION_ITERATOR_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Partition Iterator\",\"Iterations\": 3}}]";
    
    public static final String ROW_ADAPTER_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Row Adapter\",\"Startup Cost\": 30.03}}]";
    
    public static final String PARTITIONED_CSTORE_SCAN_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Partitioned CStore Scan\",\"Relation Name\": \"gsng_rstore_table\","
            + "\"Schema\": \"pschema\", \"Alias\": \"ng\", \"Filter\": \"filter cond\","
            + "\"Rows Removed by Filter\": 2}}]";
    
    public static final String HASH_AGGREGATE_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Aggregate\","
            + "\"Strategy\": \"Hashed\",\"Parent Relationship\": \"Outer\","
            + "\"Group By Key\": [\"grp1\", \"grp2\"],"
            + "\"Aggregate Detail\": [{\"DN Name\": \"DB1\", \"Temp File Num\": 12 }],"
            + "\"Filter\": \"filter cond\","
            + "\"Rows Removed by Filter\": 2,"
            + "\"Max File Num\": 2, \"Min File Num\": 0}}]";
    
    public static final String GROUP_AGGREGATE_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Aggregate\","
            + "\"Strategy\": \"Sorted\",\"Parent Relationship\": \"Outer\","
            + "\"Group By Key\": [\"grp\"],"
            + "\"Filter\": \"filter cond\","
            + "\"Rows Removed by Filter\": 2,"
            + "\"Max File Num\": 2, \"Min File Num\": 0}}]";
    
    public static final String MIX_AGGREGATE_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Aggregate\","
            + "\"Strategy\": \"Mixed\",\"Parent Relationship\": \"Outer\","
            + "\"Group By Key\": [\"grp\"],"
            + "\"Filter\": \"filter cond\","
            + "\"Rows Removed by Filter\": 2,"
            + "\"Max File Num\": 2, \"Min File Num\": 0}}]";
    
    public static final String SORT_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Sort\",\"Parent Relationship\": \"Outer\","
            + "\"Sort Key\": [\"o.orderinfo_id\"],\"Sort Method\": \"quicksort\","
            + "\"Sort Space Used\": 25,\"Sort Space Type\": \"Memory\"}}]";
    
    public static final String NESTED_LOOP_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Nested Loop\",\"Join Type\": \"Inner\","
            + "\"Subplan Name\": \"plan1\","
            + "\"Actual In Detail\": [{\"DN Name\":\"DB1\", \"Actual Startup Time\": 33.141,"
            + " \"Actual Total Time\": 34.134,\"Actual Rows\": 150,\"Actual Loops\": 1}]}}]";
    
    public static final String CTESCAN_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"CTE Scan\",\"CTE Name\": \"Inner\","
            + "\"Alias\": \"dummy\"}}]";
 
    public static final String NESTED_LOOP_JOIN_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Nest Loop Join\",\"Join Type\": \"Inner\","
            + "\"Join Filter\": \"filter cond\", \"Rows Removed by Join Filter\": 23,"
            + "\"Actual In Detail\": [{\"DN Name\":\"DB1\", \"Actual Startup Time\": 33.141,"
            + " \"Actual Total Time\": 34.134,\"Actual Rows\": 150,\"Actual Loops\": 1}]}}]";
    
    public static final String VECTOR_SET_OP_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Vector SetOp\","
            + "\"Strategy\": \"Hashed\", \"Command\": \"cmd123\","
            + "\"Setop Detail\": [{\"DN Name\":\"DB1\",  \"Temp File Num\": 12}]}}]";
    
    public static final String HASH_EXEC_CYCLE_AGGREGATE_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Aggregate\","
            + "\"Strategy\": \"Hashed\",\"Exclusive Cycles/Row\": \"2\","
            + "\"Group By Key\": [\"grp1\", \"grp2\"],"
            + "\"Actual In Detail\": [{\"DN Name\": \"DB1\", \"Actual Startup Time\": \"2\", \"Actual Total Time\": \"12\","
            + "\"Actual Rows\": \"4\", \"Actual Loops\": \"2\"}],"
            + "\"Cpus In Detail\": [{\"DN Name\": \"DB1\", \"Exclusive Cycles/Row\": \"2\", \"Exclusive Cycles\": \"12\","
            + "\"Inclusive Cycles\": \"4\"}],"
            + "\"Filter\": \"filter cond\","
            + "\"Rows Removed by Filter\": 2,"
            + "\"Max File Num\": 2, \"Min File Num\": 0}}]";
    public static final String SEQ_SCAN_EXEC_NODE_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Seq Scan\",\"Exclusive Cycles/Row\": \"2\","
            + "\"Schema\": \"pschema\", \"Alias\": \"ng\", \"Filter\": \"filter cond\","
            + "\"Rows Removed by Filter\": 2}}]";
    
    public static final String NESTED_LOOP_BUFFER_SAMPLE_JSON =
            "[{\"Plan\": {\"Node Type\": \"Nested Loop\",\"Node/s\": \"nodes\","
            + " \"Shared Read Blocks\": 4,\"Temp Read Blocks\": 4,"
            + "\"Buffers In Detail\": [{\"DN Name\":\"DB1\", \"Shared Hit Blocks\": 2,"
            + " \"Shared Read Blocks\": 4,\"Shared Dirtied Blocks\": 4,"
            + " \"Shared Written Blocks\": 4,\"Local Hit Blocks\": 2,"
            + " \"Local Read Blocks\": 3,\"Local Dirtied Blocks\": 2,"
            + " \"Local Written Blocks\": 3,\"Temp Read Blocks\": 2,"
            + " \"Temp Written Blocks\": 3,\"I/O Read Time\": 2,"
            + " \"I/O Write Time\": 3}]}}]";

    public static UIModelAnalysedPlanNode json_to_node_test(String jsonOutput)
    {
        ExplainPlanAnlysisService planAnalysis = new ExplainPlanAnlysisService(jsonOutput);
        UIModelAnalysedPlanNode node = null;
        
        try
        {
            node = UIModelConverter.covertToUIModel(planAnalysis.doAnalysis());
        }
        catch (DatabaseOperationException e)
        {
            fail("fail");
        }
        
        if (node == null)
        {
            fail("fail");
        }
        return node;
    }
}
