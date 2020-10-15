/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridViewPortLayer.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
