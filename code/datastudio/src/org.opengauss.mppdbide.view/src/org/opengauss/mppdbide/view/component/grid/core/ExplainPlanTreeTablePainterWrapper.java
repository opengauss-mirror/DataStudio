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

package org.opengauss.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.RichTextCellPainter;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainPlanTreeTablePainterWrapper.
 *
 * @since 3.0.0
 */
public class ExplainPlanTreeTablePainterWrapper extends CellPainterDecorator {
    private CellPainterDecorator planNodePainter;

    private String getImagePath(String nodeCategory) {
        String iconPath = null;
        if (nodeCategory == null) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_UNKNOWN; // unknown
            return iconPath;
        }

        iconPath = getImagePathInitial(nodeCategory);

        if (null == iconPath) {
            iconPath = getImagePathSec(nodeCategory);
        }

        if (null == iconPath) {
            iconPath = getImagePathThi(nodeCategory);
        }

        return iconPath;
    }

    private String getImagePathThi(String nodeCategory) {
        String iconPath;
        if (nodeCategory.contains(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_SETOP))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_SETOP;
        } else if (nodeCategory
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_SORT))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_SORT;
        } else if (nodeCategory
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_STREAM))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_STREAM;
        } else if (nodeCategory
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_UNION))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_UNION;
        } else {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_UNKNOWN; // unknown
        }
        return iconPath;
    }

    private String getImagePathSec(String nodeCategory) {
        String iconPath = null;
        if (nodeCategory
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_MODIFYTABLE))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_MODIFYTABLE;
        } else if (nodeCategory
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_NESTLOOPJOIN))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_NESTEDLOOPJOIN;
        } else if (nodeCategory
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_NESTEDLOOP))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_NESTEDLOOP;
        } else if (nodeCategory.contains(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_PARTITIONITERATOR))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_PARTITIONITERATOR;
        } else if (nodeCategory
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_ROWADAPTER))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_ROWADAPTOR;
        } else if (nodeCategory
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_SCAN))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_SCAN;
        }

        return iconPath;
    }

    private String getImagePathInitial(String nodeCategory) {
        String iconPath = null;
        if (nodeCategory
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_AGGREGATE))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_AGGREGATE;
        } else if (nodeCategory
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_FUNCTION))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_FUNCTION;
        } else if (nodeCategory
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_GROUPAGGREGATE))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_AGGREGATEGRP;
        } else if (nodeCategory
                .equals(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_HASH))) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_HASH;
        } else if (nodeCategory.startsWith("Hash") && nodeCategory.endsWith("Join")) {
            iconPath = IconUtility.ICON_EXPLAIN_PLAN_NODE_HASHJOIN;
        }

        return iconPath;
    }

    /**
     * Instantiates a new explain plan tree table painter wrapper.
     */
    public ExplainPlanTreeTablePainterWrapper() {
        super(new BeveledBorderDecorator(new TextPainter()), CellEdgeEnum.RIGHT, 100, null, false, true);
    }

    /**
     * Paint cell.
     *
     * @param cell the cell
     * @param gc the gc
     * @param adjustedCellBounds the adjusted cell bounds
     * @param configRegistry the config registry
     */
    @Override
    public void paintCell(ILayerCell cell, GC gc, Rectangle adjustedCellBounds, IConfigRegistry configRegistry) {
        String value = cell.getDataValue().toString();
        ImagePainter imagepainter = new ImagePainter(IconUtility.getIconImage(getImagePath(value), getClass()), false);

        this.planNodePainter = new CellPainterDecorator(new RichTextCellPainter(), CellEdgeEnum.LEFT, imagepainter);
        this.planNodePainter.paintCell(cell, gc, adjustedCellBounds, configRegistry);
    }
}
