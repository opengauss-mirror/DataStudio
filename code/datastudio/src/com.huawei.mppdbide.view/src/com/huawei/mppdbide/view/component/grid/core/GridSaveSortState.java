/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridSaveSortState.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
