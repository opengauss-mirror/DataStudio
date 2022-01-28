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

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.copy.InternalCellClipboard;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * 
 * Title: class
 * 
 * Description: The Class SystemClipboardWrapper.
 *
 * @since 3.0.0
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
