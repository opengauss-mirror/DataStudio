/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridNonEditableColumnValueAccessor.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GridNonEditableColumnValueAccessor extends GridColumnValueAccessor {

    /**
     * Instantiates a new grid non editable column value accessor.
     *
     * @param dataProvider the data provider
     */
    public GridNonEditableColumnValueAccessor(IDSGridDataProvider dataProvider) {
        super(dataProvider);

    }

    /**
     * Sets the data value.
     *
     * @param rowObject the row object
     * @param columnIndex the column index
     * @param newValue the new value
     */
    @Override
    public void setDataValue(IDSGridDataRow rowObject, int columnIndex, Object newValue) {
        return;
    }
}
