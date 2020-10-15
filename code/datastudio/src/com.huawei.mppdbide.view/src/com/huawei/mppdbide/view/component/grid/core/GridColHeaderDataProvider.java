/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
