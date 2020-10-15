/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.copy.InternalCellClipboard;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * 
 * Title: class
 * 
 * Description: The Class SystemClipboardWrapper.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SystemClipboardWrapper extends InternalCellClipboard {

    /**
     * Gets the copied cells.
     *
     * @return the copied cells
     */
    @Override
    public ILayerCell[][] getCopiedCells() {
        return super.getCopiedCells();
    }

    /**
     * Sets the copied cells.
     *
     * @param copiedCells the new copied cells
     */
    @Override
    public void setCopiedCells(ILayerCell[][] copiedCells) {
        super.setCopiedCells(copiedCells);
    }

    /**
     * Clear.
     */
    @Override
    public void clear() {
        super.clear();
    }

}
