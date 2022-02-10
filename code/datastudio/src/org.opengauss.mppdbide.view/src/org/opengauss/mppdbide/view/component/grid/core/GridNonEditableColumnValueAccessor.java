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

import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridNonEditableColumnValueAccessor.
 *
 * @since 3.0.0
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
