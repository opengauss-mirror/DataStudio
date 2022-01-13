/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.huawei.mppdbide.explainplan.plannode.factory;

import java.util.HashMap;
import java.util.Map;

import com.huawei.mppdbide.explainplan.plannode.CStoreScanNode;
import com.huawei.mppdbide.explainplan.plannode.CTEScanNode;
import com.huawei.mppdbide.explainplan.plannode.DataNodeScan;
import com.huawei.mppdbide.explainplan.plannode.FunctionScanNode;
import com.huawei.mppdbide.explainplan.plannode.HashAggregate;
import com.huawei.mppdbide.explainplan.plannode.HashJoin;
import com.huawei.mppdbide.explainplan.plannode.HashNode;
import com.huawei.mppdbide.explainplan.plannode.IndexScan;
import com.huawei.mppdbide.explainplan.plannode.ModifyTableNode;
import com.huawei.mppdbide.explainplan.plannode.NestLoopJoin;
import com.huawei.mppdbide.explainplan.plannode.NestedLoopNode;
import com.huawei.mppdbide.explainplan.plannode.PartitionItetrator;
import com.huawei.mppdbide.explainplan.plannode.RecursiveUnionNode;
import com.huawei.mppdbide.explainplan.plannode.RowAdapter;
import com.huawei.mppdbide.explainplan.plannode.ScanNode;
import com.huawei.mppdbide.explainplan.plannode.SortNode;
import com.huawei.mppdbide.explainplan.plannode.StreamBroadcast;
import com.huawei.mppdbide.explainplan.plannode.StreamGather;
import com.huawei.mppdbide.explainplan.plannode.StreamRedistribute;
import com.huawei.mppdbide.explainplan.plannode.UnknownOperator;
import com.huawei.mppdbide.explainplan.plannode.ValuesScanNode;
import com.huawei.mppdbide.explainplan.plannode.VectorSetOpNode;
import com.huawei.mppdbide.explainplan.plannode.WorkTableScanNode;

/**
 * Title: GetNodeFromFactory
 * 
 * Description:A factory for creating GetNodeFrom objects.
 * 
 * @since 3.0.0
 */
public abstract class GetNodeFromFactory {
    private static final Map<String, Class> OPERATOR_CLASS_MAP = new HashMap<String, Class>(10);
    static {
        OPERATOR_CLASS_MAP.put("Aggregate", HashAggregate.class);

        OPERATOR_CLASS_MAP.put("CStore Scan", ScanNode.class);
        OPERATOR_CLASS_MAP.put("CTE Scan", CTEScanNode.class);
        OPERATOR_CLASS_MAP.put("Data Node Scan", DataNodeScan.class);

        OPERATOR_CLASS_MAP.put("Function Scan", FunctionScanNode.class);

        OPERATOR_CLASS_MAP.put("Hash", HashNode.class);
        OPERATOR_CLASS_MAP.put("Hash Aggregate", HashAggregate.class);
        OPERATOR_CLASS_MAP.put("Hash Join", HashJoin.class);

        OPERATOR_CLASS_MAP.put("Index Scan", IndexScan.class);

        OPERATOR_CLASS_MAP.put("ModifyTable", ModifyTableNode.class);

        OPERATOR_CLASS_MAP.put("Nest Loop Join", NestLoopJoin.class);
        OPERATOR_CLASS_MAP.put("Nested Loop", NestedLoopNode.class);

        OPERATOR_CLASS_MAP.put("Partitioned CStore Scan", CStoreScanNode.class);
        OPERATOR_CLASS_MAP.put("Partition Iterator", PartitionItetrator.class);
        OPERATOR_CLASS_MAP.put("Partitioned Seq Scan", ScanNode.class);

        OPERATOR_CLASS_MAP.put("Recursive Union", RecursiveUnionNode.class);
        OPERATOR_CLASS_MAP.put("Row Adapter", RowAdapter.class);

        OPERATOR_CLASS_MAP.put("Seq Scan", ScanNode.class);
        OPERATOR_CLASS_MAP.put("Sort", SortNode.class);
        OPERATOR_CLASS_MAP.put("Streaming(type: BROADCAST)", StreamBroadcast.class);
        OPERATOR_CLASS_MAP.put("Streaming (type: GATHER)", StreamGather.class);
        OPERATOR_CLASS_MAP.put("Streaming(type: REDISTRIBUTE)", StreamRedistribute.class);
        OPERATOR_CLASS_MAP.put("Subquery Scan", ScanNode.class);
        OPERATOR_CLASS_MAP.put("SetOp", VectorSetOpNode.class);

        OPERATOR_CLASS_MAP.put("Values Scan", ValuesScanNode.class);
        OPERATOR_CLASS_MAP.put("WorkTable Scan", WorkTableScanNode.class);
    }

    /**
     * Gets the class.
     *
     * @param nodeType the node type
     * @return the class
     */
    public static Class getClass(String nodeType) {
        Class retClass = null;
        if (null != nodeType) {
            String lookupNodeType = nodeType;
            String nodeNameFirstPart = nodeType.split(" ")[0];
            if ("Vector".equalsIgnoreCase(nodeNameFirstPart)) {
                lookupNodeType = nodeType.substring(nodeNameFirstPart.length() + 1);
            }
            retClass = OPERATOR_CLASS_MAP.get(lookupNodeType);
        }
        if (null == retClass) {
            return UnknownOperator.class;
        }
        return retClass;
    }
}
