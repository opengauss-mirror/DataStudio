/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortEntryData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SortEntryData {
    private String sortedColumnName;
    private SortDirectionEnum sortDirection;

    /**
     * Instantiates a new sort entry data.
     *
     * @param sortedColumnName the sorted column name
     * @param sortDirection the sort direction
     */
    public SortEntryData(String sortedColumnName, SortDirectionEnum sortDirection) {
        this.sortedColumnName = sortedColumnName;
        this.sortDirection = sortDirection;
    }

    /**
     * Gets the column name.
     *
     * @return the column name
     */
    public String getColumnName() {
        return this.sortedColumnName;
    }

    /**
     * Gets the sort direction.
     *
     * @return the sort direction
     */
    public SortDirectionEnum getSortDirection() {
        return this.sortDirection;
    }

}
