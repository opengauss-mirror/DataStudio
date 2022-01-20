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

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridColHeaderDataProvider.
 *
 * @since 3.0.0
 */
public class GridColHeaderDataProvider implements IDataProvider {
    private IDSGridDataProvider dataProvider;

    /**
     * Instantiates a new grid col header data provider.
     *
     * @param dataProvider the data provider
     */
    public GridColHeaderDataProvider(IDSGridDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    /**
     * Gets the data value.
     *
     * @param columnIndex the column index
     * @param rowIndex the row index
     * @return the data value
     */
    @Override
    public Object getDataValue(int columnIndex, int rowIndex) {
        return this.dataProvider.getColumnDataProvider().getColumnName(columnIndex);
    }

    /**
     * Sets the data value.
     *
     * @param columnIndex the column index
     * @param rowIndex the row index
     * @param newValue the new value
     */
    @Override
    public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
        // Ignore. Column Name set would not be supported
    }

    /**
     * Gets the column count.
     *
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        return this.dataProvider.getColumnDataProvider().getColumnCount();
    }

    /**
     * Gets the row count.
     *
     * @return the row count
     */
    @Override
    public int getRowCount() {
        // Assumption: only 1 record for table header as we donot support merged
        // column header feature now.
        return 1;
    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        this.dataProvider = null;
    }
}
