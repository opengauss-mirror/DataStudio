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

import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortEntryData.
 *
 * @since 3.0.0
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
