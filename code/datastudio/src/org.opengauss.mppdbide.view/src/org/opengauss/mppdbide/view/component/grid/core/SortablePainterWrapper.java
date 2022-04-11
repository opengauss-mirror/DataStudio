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
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import org.opengauss.mppdbide.view.component.grid.IEditTableGridStyleLabelFactory;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortablePainterWrapper.
 *
 * @since 3.0.0
 */
public class SortablePainterWrapper extends CellPainterDecorator {
    private ImagePainter imagePainterLock;
    private ImagePainter imagePainterSortNone;
    private ImagePainter imagePainterSortDes;
    private ImagePainter imagePainterSortDesSmaller;
    private ImagePainter imagePainterSortDesSmallest;
    private ImagePainter imagePainterSortAsc;
    private ImagePainter imagePainterSortAscSmaller;
    private ImagePainter imagePainterSortAscSmallest;
    private CellPainterDecorator noLockNoSort;
    private CellPainterDecorator noLockAscSort;
    private CellPainterDecorator noLockAscSortSmaller;
    private CellPainterDecorator noLockAscSortSmallest;
    private CellPainterDecorator noLockDesSort;
    private CellPainterDecorator noLockDesSortSmaller;
    private CellPainterDecorator noLockDesSortSmallest;
    private CellPainterDecorator lockNoSort;
    private CellPainterDecorator lockAscSort;
    private CellPainterDecorator lockAscSortSmaller;
    private CellPainterDecorator lockAscSortSmallest;
    private CellPainterDecorator lockDesSort;
    private CellPainterDecorator lockDesSortSmaller;
    private CellPainterDecorator lockDesSortSmallest;

    /**
     * Config label that is added if a column is sorted descending.
     */
    private static final String SORT_DOWN_CONFIG_TYPE = "SORT_DOWN"; // $NON-NLS-1$
    /**
     * Config label that is added if a column is sorted ascending.
     */
    private static final String SORT_UP_CONFIG_TYPE = "SORT_UP"; // $NON-NLS-1$
    /**
     * Sort sequence can be appended to this base
     */
    private static final String CONFIG_TYPE_SORT_SEQ = "SORT_SEQ_"; // $NON-NLS-1$

    /**
     * Instantiates a new sortable painter wrapper.
     */
    public SortablePainterWrapper() {
        super(new BeveledBorderDecorator(new TextPainter()), CellEdgeEnum.RIGHT, 100, null, false, true);
        createImagePainters();
        createCellPainters();
    }

    private void createCellPainters() {
        /* No lock + no sort */
        noLockNoSort = new CellPainterDecorator(new BeveledBorderDecorator(new TextPainter()), CellEdgeEnum.RIGHT, 100,
                imagePainterSortNone, false, true);

        /* No lock + asc sort */
        noLockAscSort = new CellPainterDecorator(new BeveledBorderDecorator(new TextPainter()), CellEdgeEnum.RIGHT, 100,
                imagePainterSortAsc, false, true);

        /* No lock + asc smaller sort */
        noLockAscSortSmaller = new CellPainterDecorator(new BeveledBorderDecorator(new TextPainter()),
                CellEdgeEnum.RIGHT, 100, imagePainterSortAscSmaller, false, true);

        /* No lock + asc smallest sort */
        noLockAscSortSmallest = new CellPainterDecorator(new BeveledBorderDecorator(new TextPainter()),
                CellEdgeEnum.RIGHT, 100, imagePainterSortAscSmallest, false, true);

        /* No lock + des sort */
        noLockDesSort = new CellPainterDecorator(new BeveledBorderDecorator(new TextPainter()), CellEdgeEnum.RIGHT, 100,
                imagePainterSortDes, false, true);

        /* No lock + des smaller sort */
        noLockDesSortSmaller = new CellPainterDecorator(new BeveledBorderDecorator(new TextPainter()),
                CellEdgeEnum.RIGHT, 100, imagePainterSortDesSmaller, false, true);

        /* No lock + des smallest sort */
        noLockDesSortSmallest = new CellPainterDecorator(new BeveledBorderDecorator(new TextPainter()),
                CellEdgeEnum.RIGHT, 100, imagePainterSortDesSmallest, false, true);

        /* lock + no sort */
        lockNoSort = new CellPainterDecorator(noLockNoSort, CellEdgeEnum.LEFT, 100, imagePainterLock, false, true);

        /* lock + asc sort */
        lockAscSort = new CellPainterDecorator(noLockAscSort, CellEdgeEnum.LEFT, 100, imagePainterLock, false, true);

        /* lock + asc smaller sort */
        lockAscSortSmaller = new CellPainterDecorator(noLockAscSortSmaller, CellEdgeEnum.LEFT, 100, imagePainterLock,
                false, true);

        /* lock + asc smallest sort */
        lockAscSortSmallest = new CellPainterDecorator(noLockAscSortSmallest, CellEdgeEnum.LEFT, 100, imagePainterLock,
                false, true);

        /* lock + des sort */
        lockDesSort = new CellPainterDecorator(noLockDesSort, CellEdgeEnum.LEFT, 100, imagePainterLock, false, true);

        /* lock + des smaller sort */
        lockDesSortSmaller = new CellPainterDecorator(noLockDesSortSmaller, CellEdgeEnum.LEFT, 100, imagePainterLock,
                false, true);

        /* lock + des smallest sort */
        lockDesSortSmallest = new CellPainterDecorator(noLockDesSortSmallest, CellEdgeEnum.LEFT, 100, imagePainterLock,
                false, true);
    }

    private void createImagePainters() {
        Image imageSortAsc = IconUtility.getIconImage(IconUtility.ICO_SORT_ASC, getClass());
        imagePainterSortAsc = new ImagePainter(imageSortAsc, false);
        Image imageSortAscSmaller = IconUtility.getIconImage(IconUtility.ICO_SORT_ASC_SMALLER, getClass());
        imagePainterSortAscSmaller = new ImagePainter(imageSortAscSmaller, false);
        Image imageSortAscSmallest = IconUtility.getIconImage(IconUtility.ICO_SORT_ASC_SMALLEST, getClass());
        imagePainterSortAscSmallest = new ImagePainter(imageSortAscSmallest, false);
        Image imageSortDes = IconUtility.getIconImage(IconUtility.ICO_SORT_DES, getClass());
        imagePainterSortDes = new ImagePainter(imageSortDes, false);
        Image imageSortDesSmaller = IconUtility.getIconImage(IconUtility.ICO_SORT_DES_SMALLER, getClass());
        imagePainterSortDesSmaller = new ImagePainter(imageSortDesSmaller, false);
        Image imageSortDesSmallest = IconUtility.getIconImage(IconUtility.ICO_SORT_DES_SMALLEST, getClass());
        imagePainterSortDesSmallest = new ImagePainter(imageSortDesSmallest, false);
        Image imageSortNone = IconUtility.getIconImage(IconUtility.ICO_SORT_NONE, getClass());
        imagePainterSortNone = new ImagePainter(imageSortNone, false);
        Image imageLock = IconUtility.getIconSmallImage(IconUtility.ICON_COLUMN_EDIT_LOCK, getClass());
        imagePainterLock = new ImagePainter(imageLock);
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

        LabelStack configLabels = cell.getConfigLabels();

        boolean hasLock = configLabels.hasLabel(IEditTableGridStyleLabelFactory.COL_HEADER_LABEL_READONLY_CELL);
        boolean hasSortAsc = configLabels.hasLabel(SORT_UP_CONFIG_TYPE);
        boolean hasSortDes = configLabels.hasLabel(SORT_DOWN_CONFIG_TYPE);
        boolean isSortPriorityOne = configLabels.hasLabel(CONFIG_TYPE_SORT_SEQ + 0);
        boolean isSortPriorityTwo = configLabels.hasLabel(CONFIG_TYPE_SORT_SEQ + 1);

        if (hasLock) {
            paintCellLock(cell, gc, adjustedCellBounds, configRegistry, hasSortAsc, hasSortDes, isSortPriorityOne,
                    isSortPriorityTwo);
        } else {
            paintCellNoLock(cell, gc, adjustedCellBounds, configRegistry, hasSortAsc, hasSortDes, isSortPriorityOne,
                    isSortPriorityTwo);
        }
    }

    private void paintCellNoLock(ILayerCell cell, GC gc, Rectangle adjustedCellBounds, IConfigRegistry configRegistry,
            boolean hasSortAsc, boolean hasSortDes, boolean isSortPriorityOne, boolean isSortPriorityTwo) {
        if (hasSortAsc) {
            paintCellNoLockAsc(cell, gc, adjustedCellBounds, configRegistry, isSortPriorityOne, isSortPriorityTwo);
        } else if (hasSortDes) {
            paintCellNoLockDesc(cell, gc, adjustedCellBounds, configRegistry, isSortPriorityOne, isSortPriorityTwo);
        } else {
            this.noLockNoSort.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        }
    }

    private void paintCellLock(ILayerCell cell, GC gc, Rectangle adjustedCellBounds, IConfigRegistry configRegistry,
            boolean hasSortAsc, boolean hasSortDes, boolean isSortPriorityOne, boolean isSortPriorityTwo) {
        if (hasSortAsc) {
            paintCellLockAsc(cell, gc, adjustedCellBounds, configRegistry, isSortPriorityOne, isSortPriorityTwo);
        } else if (hasSortDes) {
            paintCellLockDesc(cell, gc, adjustedCellBounds, configRegistry, isSortPriorityOne, isSortPriorityTwo);
        } else {
            this.lockNoSort.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        }
    }

    private void paintCellNoLockDesc(ILayerCell cell, GC gc, Rectangle adjustedCellBounds,
            IConfigRegistry configRegistry, boolean isSortPriorityOne, boolean isSortPriorityTwo) {
        if (isSortPriorityOne) {
            this.noLockDesSort.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        } else if (isSortPriorityTwo) {
            this.noLockDesSortSmaller.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        } else {
            this.noLockDesSortSmallest.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        }
    }

    private void paintCellNoLockAsc(ILayerCell cell, GC gc, Rectangle adjustedCellBounds,
            IConfigRegistry configRegistry, boolean isSortPriorityOne, boolean isSortPriorityTwo) {
        if (isSortPriorityOne) {
            this.noLockAscSort.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        } else if (isSortPriorityTwo) {
            this.noLockAscSortSmaller.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        } else {
            this.noLockAscSortSmallest.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        }
    }

    private void paintCellLockDesc(ILayerCell cell, GC gc, Rectangle adjustedCellBounds, IConfigRegistry configRegistry,
            boolean isSortPriorityOne, boolean isSortPriorityTwo) {
        if (isSortPriorityOne) {
            this.lockDesSort.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        } else if (isSortPriorityTwo) {
            this.lockDesSortSmaller.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        } else {
            this.lockDesSortSmallest.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        }
    }

    private void paintCellLockAsc(ILayerCell cell, GC gc, Rectangle adjustedCellBounds, IConfigRegistry configRegistry,
            boolean isSortPriorityOne, boolean isSortPriorityTwo) {
        if (isSortPriorityOne) {
            this.lockAscSort.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        } else if (isSortPriorityTwo) {
            this.lockAscSortSmaller.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        } else {
            this.lockAscSortSmallest.paintCell(cell, gc, adjustedCellBounds, configRegistry);
        }
    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        imagePainterLock = null;
        imagePainterSortNone = null;
        imagePainterSortDes = null;
        imagePainterSortDesSmaller = null;
        imagePainterSortDesSmallest = null;
        imagePainterSortAsc = null;
        imagePainterSortAscSmaller = null;
        imagePainterSortAscSmallest = null;
        noLockNoSort = null;
        noLockAscSort = null;
        noLockAscSortSmaller = null;
        noLockAscSortSmallest = null;
        noLockDesSort = null;
        noLockDesSortSmaller = null;
        noLockDesSortSmallest = null;
        lockNoSort = null;
        lockAscSort = null;
        lockAscSortSmaller = null;
        lockAscSortSmallest = null;
        lockDesSort = null;
        lockDesSortSmaller = null;
        lockDesSortSmallest = null;

    }
}
