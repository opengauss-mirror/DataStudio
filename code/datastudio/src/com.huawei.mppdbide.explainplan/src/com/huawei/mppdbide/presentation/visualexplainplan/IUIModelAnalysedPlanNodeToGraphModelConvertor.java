/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Edge.Builder;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.algorithms.SpaceTreeLayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestProperties;

import com.huawei.mppdbide.explainplan.service.AnalysedPlanNode;

/**
 * Title: IUIModelAnalysedPlanNodeToGraphModelConvertor
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 11-Oct-2019]
 * @since 11-Oct-2019
 */

public class IUIModelAnalysedPlanNodeToGraphModelConvertor {

    /**
     * Gets the graph model.
     *
     * @param flattenedExplainPlanNodes the flattened explain plan nodes
     * @param flattenedExplainPlanEdges the flattened explain plan edges
     * @return the graph model
     */
    public static Graph getGraphModel(List<UIModelAnalysedPlanNode> flattenedExplainPlanNodes,
            List<Relationship> flattenedExplainPlanEdges) {
        List<Node> nodes = new ArrayList<>(1);
        List<Edge> edges = new ArrayList<>(1);
        Map<String, Node> nodeMap = new HashMap<String, Node>(1);

        for (UIModelAnalysedPlanNode node : flattenedExplainPlanNodes) {
            org.eclipse.gef.graph.Node.Builder nodeBuilder = new org.eclipse.gef.graph.Node.Builder();
            AnalysedPlanNode analysedPlanNode = node.getAnalysedPlanNode();
            nodeBuilder.attr(VEPNodeAttributeId.ELE_ID, analysedPlanNode.getNodeSequenceNum())
                    .attr(VEPNodeAttributeId.LABEL, analysedPlanNode.getNodeUniqueNameWithType())
                    .attr(VEPNodeAttributeId.NODE_PROPERTY, node);
            Node builtNode = nodeBuilder.attr(ZestProperties.TOOLTIP__N, node.getToopTipText()).buildNode();
            nodeMap.put(analysedPlanNode.getNodeUniqueNameWithType(), builtNode);
            nodes.add(builtNode);
        }

        for (Relationship edge : flattenedExplainPlanEdges) {
            Node sourceNode = nodeMap.get(edge.getParentNode().getAnalysedPlanNode().getNodeUniqueNameWithType());
            Node targetNode = nodeMap.get(edge.getChildNode().getAnalysedPlanNode().getNodeUniqueNameWithType());

            Builder edgeBuilder = new Builder(sourceNode, targetNode).attr(VEPNodeAttributeId.LABEL,
                    Long.toString(edge.getRecordCount()));
            edges.add(edgeBuilder.buildEdge());
        }

        return new Graph.Builder().nodes(nodes.toArray(new Node[] {})).edges(edges.toArray(new Edge[] {}))
                .attr(ZestProperties.LAYOUT_ALGORITHM__G,
                        new DSSpaceTreeLayoutAlgorithm(SpaceTreeLayoutAlgorithm.TOP_DOWN))
                .build();
    }
}
