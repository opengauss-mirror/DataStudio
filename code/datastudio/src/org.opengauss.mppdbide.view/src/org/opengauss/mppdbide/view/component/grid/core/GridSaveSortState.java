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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridSaveSortState.
 *
 * @since 3.0.0
 */
public class GridSaveSortState {
    private List<SortEntryData> saveSortList;

    /**
     * Instantiates a new grid save sort state.
     */
    public GridSaveSortState() {
        saveSortList = new ArrayList<SortEntryData>(10);
    }

    /**
     * Gets the clone.
     *
     * @return the clone
     */
    public GridSaveSortState getClone() {

        GridSaveSortState dup = new GridSaveSortState();

        for (SortEntryData str : this.saveSortList) {
            dup.saveSortEntry(str);
        }
        return dup;
    }

    /**
     * Checks for sort keys.
     *
     * @return true, if successful
     */
    public boolean hasSortKeys() {
        return this.saveSortList.size() > 0;
    }

    /**
     * Save sort entry.
     *
     * @param entry the entry
     */
    public void saveSortEntry(SortEntryData entry) {
        saveSortList.add(entry);
    }

    /**
     * Gets the saved sort list.
     *
     * @return the saved sort list
     */
    public List<SortEntryData> getSavedSortList() {
        return this.saveSortList;
    }

    /**
     * Clean up.
     */
    public void cleanUp() {
        if (null != this.saveSortList) {
            this.saveSortList.clear();
        }
    }
}
