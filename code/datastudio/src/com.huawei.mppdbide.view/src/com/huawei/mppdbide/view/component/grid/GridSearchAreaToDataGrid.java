/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
