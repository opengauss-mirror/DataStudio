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

package com.huawei.mppdbide.view.component.grid;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridSearchAreaToDataGrid.
 *
 * @since 3.0.0
 */
public class GridSearchAreaToDataGrid {
    private GridSearchArea searchArea;

    /**
     * Instantiates a new grid search area to data grid.
     *
     * @param searchArea the search area
     */
    public GridSearchAreaToDataGrid(GridSearchArea searchArea) {
        this.searchArea = searchArea;
    }

    /**
     * Gets the txt search str.
     *
     * @return the txt search str
     */
    public Text getTxtSearchStr() {
        return searchArea.getTxtSearchStr();
    }

    /**
     * Gets the trigger search.
     *
     * @param targetValueStr the target value str
     * @param doSearch the do search
     * @return the trigger search
     */
    public void getTriggerSearch(String targetValueStr, boolean doSearch) {
        searchArea.triggerSearch(targetValueStr, doSearch);
    }

    /**
     * Gets the cmb search opt.
     *
     * @return the cmb search opt
     */
    public Combo getCmbSearchOpt() {
        return searchArea.getCmbSearchOpt();
    }

    /**
     * Gets the trigger search.
     *
     * @param doSearch the do search
     * @return the trigger search
     */
    public void getTriggerSearch(boolean doSearch) {
        searchArea.triggerSearch(doSearch);

    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        this.searchArea = null;
    }
}
