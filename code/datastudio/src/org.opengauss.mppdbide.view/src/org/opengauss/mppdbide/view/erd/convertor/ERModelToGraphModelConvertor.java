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

package org.opengauss.mppdbide.view.erd.convertor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Edge.Builder;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.zest.fx.ZestProperties;

import org.opengauss.mppdbide.bl.erd.model.AbstractERAssociation;
import org.opengauss.mppdbide.bl.erd.model.AbstractEREntity;
import org.opengauss.mppdbide.bl.erd.model.IERNodeConstants;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.presentation.erd.AbstractERPresentation;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.view.erd.visuals.ERLayout;
import org.opengauss.mppdbide.view.erd.visuals.IERVisualStyleConstants;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

/**
 * Title: ERModelToGraphModelConvertor
 * 
 * @since 3.0.0
 */
public class ERModelToGraphModelConvertor {

    /**
     * Gets the graph model.
     *
     * @param presentation the presentation
     * @return the graph model
     */
    public static Graph getGraphModel(AbstractERPresentation<ServerObject> presentation) {
        List<Node> nodes = new ArrayList<>(1);
        List<Edge> edges = new ArrayList<>(1);
        Map<AbstractEREntity, Node> nodeMap = new HashMap<AbstractEREntity, Node>(1);

        for (AbstractEREntity entity : presentation.getEntities()) {
            org.eclipse.gef.graph.Node.Builder nodeBuilder = new org.eclipse.gef.graph.Node.Builder();
            nodeBuilder.attr(IERNodeConstants.LABEL, entity.getName()).attr(IERNodeConstants.NODE_PROPERTY, entity);
            Node builtNode = nodeBuilder.buildNode();
            nodeMap.put(entity, builtNode);
            nodes.add(builtNode);
        }

        for (AbstractERAssociation association : presentation.getAssociations()) {
            Node sourceNode = nodeMap.get(association.getSourceEntity());
            Node targetNode = nodeMap.get(association.getTargetEntity());
            int associationNum = association.getAssociationNum();
            List<Point> list = new ArrayList<Point>();
            if (associationNum == 0) {
                if (sourceNode.equals(targetNode)) {
                    list.add(new Point(600, 500));
                    list.add(new Point(600, 550));
                }
            } else if (associationNum >= 1) {
                if (sourceNode.equals(targetNode)) {
                    list.add(new Point(associationNum * 40 + 500, associationNum * 40 + 500));
                    list.add(new Point(associationNum * 40 + 500, associationNum * 40 + 550));
                } else {
                    list.add(new Point(associationNum * 40 + 500, associationNum * 40 + 600));
                }
            }
            Builder edgeBuilder = new Builder(sourceNode, targetNode)
                    .attr(ZestProperties.TOOLTIP__E, getTooltip(association))
                    .attr(ZestProperties.CONTROL_POINTS__E, list);
            Edge edge = edgeBuilder.buildEdge();
            getEdgeAttributes(edge);
            edges.add(edge);
        }
        return new Graph.Builder().nodes(nodes.toArray(new Node[] {})).edges(edges.toArray(new Edge[] {}))
                .attr(ZestProperties.LAYOUT_ALGORITHM__G, new ERLayout()).build();
    }

    private static void getEdgeAttributes(Edge edge) {
        Map<String, Object> edgeAttributes = new HashMap<>();
        edgeAttributes.put(ZestProperties.SOURCE_DECORATION__E,
                new Polygon(10.0, 0.0, 5, -5, 0.0, 0.0, 5, 5, 10.0, 0.0));
        edgeAttributes.put(ZestProperties.TARGET_DECORATION__E, new Circle(4));
        edgeAttributes.put(ZestProperties.CURVE_CSS_STYLE__E, IERVisualStyleConstants.EDGE_STYLE);
        edge.attributesProperty().putAll(edgeAttributes);
    }

    private static String getTooltip(AbstractERAssociation association) {
        StringBuffer toolTipText = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        toolTipText.append(association.getSourceEntity().getFullyQualifiedName());
        toolTipText.append("-- " + association.getTargetEntity().getFullyQualifiedName());
        return toolTipText.toString();
    }
}