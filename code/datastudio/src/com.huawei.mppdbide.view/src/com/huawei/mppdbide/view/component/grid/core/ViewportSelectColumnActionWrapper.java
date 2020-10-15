/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.viewport.action.ViewportSelectColumnAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;

import com.huawei.mppdbide.view.utils.icon.IconUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewportSelectColumnActionWrapper.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ViewportSelectColumnActionWrapper extends ViewportSelectColumnAction {

    /**
     * Instantiates a new viewport select column action wrapper.
     *
     * @param withShiftMask the with shift mask
     * @param withControlMask the with control mask
     */
    public ViewportSelectColumnActionWrapper(boolean withShiftMask, boolean withControlMask) {
        super(withShiftMask, withControlMask);
    }

    /**
     * Run.
     *
     * @param natTable the nat table
     * @param event the event
     */
    @Override
    public void run(NatTable natTable, MouseEvent event) {
        if ((event.stateMask & SWT.MOD3) == SWT.MOD3) {
            return;
        } else if (((event.stateMask & SWT.MOD1) == SWT.MOD1) || ((event.stateMask & SWT.MOD2) == SWT.MOD2)) {
            super.run(natTable, event);
        } else {
            int colPos = natTable.getColumnPositionByX(event.x);
            int columnWidth = natTable.getColumnWidthByPosition(colPos);
            int columnHeight = natTable.getRowHeightByPosition(0);
            int startXOfColumn = natTable.getStartXOfColumnPosition(colPos);
            int startYOfColumn = natTable.getStartYOfRowPosition(0);
            /* Dimension of all sort images is same */
            Image myImage = IconUtility.getIconSmallImage(IconUtility.ICO_SORT_NONE, getClass());
            int sortImageWidth = myImage.getBounds().width;

            /*
             * A buffer of 3 pixel is added between image right edge and cell
             * edge to avoid sorting during cell resize
             */
            if ((event.x >= startXOfColumn + columnWidth - sortImageWidth - 3)
                    && (event.x <= startXOfColumn + columnWidth - 3) && (event.y >= startYOfColumn)
                    && (event.y <= startYOfColumn + columnHeight)) {
                return;
            } else {
                super.run(natTable, event);
            }
        }
    }

}
