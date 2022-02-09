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

package org.opengauss.mppdbide.view.erd.visuals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;

/**
 * Title: ERLayout Description: The layout algorithm for ER Diagram.
 *
 * @since 3.0.0
 */
public class ERLayout implements ILayoutAlgorithm {
    private static final double HEIGHT_GAP = 10;

    private static final double VERTICAL_SPACING = 16;

    /**
     * Makes this algorithm perform layout computation and apply it to its
     * context.
     */
    public void applyLayout(LayoutContext context, boolean clean) {
        if (!clean) {
            return;
        }

        ArrayList<List<Node>> entitiesList = new ArrayList<>();
        List<Node> entities = new ArrayList<>();
        // all the tables needs to layout.
        for (Node node : context.getGraph().getNodes()) {
            entities.add(node);
        }

        for (Node node : entities) {
            addToEntitiesList(node, entitiesList);
        }

        Collections.sort(entitiesList, new Comparator<List<Node>>() {
            /**
             * Compare the height of two rows, the higher will be layout below.
             */
            public int compare(List<Node> row1, List<Node> row2) {
                Node entityRow1 = row1.get(0);
                Node entityRow2 = row2.get(0);
                return (int) (LayoutProperties.getLocation(entityRow1).y - LayoutProperties.getLocation(entityRow2).y);
            }
        });

        int heightSoFar = 0;
        Rectangle bounds = LayoutProperties.getBounds(context.getGraph());
        for (List<Node> currentRow : entitiesList) {
            int index = 0;
            /**
             * bounds.getWidth() / 2 is the center of the graph, 75 is the
             * estimated width of each table the width represent the begin
             * loyoutX of currentRow
             */
            int width = (int) (bounds.getWidth() / 2 - currentRow.size() * 75);

            // the currentRow's height and the vertical spacing
            heightSoFar += LayoutProperties.getSize(currentRow.get(0)).height + VERTICAL_SPACING;
            for (Node entity : currentRow) {
                Dimension size = LayoutProperties.getSize(entity);
                /**
                 * set location of the entity, (size.width / 2, size.height / 2)
                 * is the center of the entity 10 * ++index is the spacing in
                 * currentRow tables
                 */
                LayoutProperties.setLocation(entity,
                        new Point(width + 10 * ++index + size.width / 2, heightSoFar + size.height / 2));
                width += size.width;
            }
        }
    }

    /**
     * @Title: addToEntitiesList
     * @Description: add the entity to entitiesList, entities will add to the
     * same row, if their heights differ slightly
     * @param entity: the entity need add to the entitiesList.
     * @param entitiesList: all the tables divided into different rows.
     */
    private void addToEntitiesList(Node entity, ArrayList<List<Node>> entitiesList) {
        double layoutY = LayoutProperties.getLocation(entity).y;

        for (List<Node> currentRow : entitiesList) {
            Node currentRowEntity = currentRow.get(0);
            double currentRowY = LayoutProperties.getLocation(currentRowEntity).y;
            if (layoutY >= currentRowY - HEIGHT_GAP && layoutY <= currentRowY + HEIGHT_GAP) {
                currentRow.add(entity);
                return;
            }
        }

        List<Node> newRow = new ArrayList<>();
        newRow.add(entity);
        entitiesList.add(newRow);
    }
}
