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

import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridViewPortLayer.
 *
 * @since 3.0.0
 */
public class GridViewPortLayer extends ViewportLayer {

    /**
     * Instantiates a new grid view port layer.
     *
     * @param underlyingLayer the underlying layer
     */
    public GridViewPortLayer(IUniqueIndexLayer underlyingLayer) {
        super(underlyingLayer);
    }

    /**
     * Checks if is scrolled to end of page.
     *
     * @return true, if is scrolled to end of page
     */
    public boolean isScrolledToEndOfPage() {
        return isLastRowCompletelyDisplayed();
    }
}
