/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IGridUIPreferenceBase.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IGridUIPreferenceBase {

    /**
     * Gets the column width.
     *
     * @return the column width
     */
    int getColumnWidth();

    /**
     * Checks if is show query area.
     *
     * @return true, if is show query area
     */
    boolean isShowQueryArea();

    /**
     * Checks if is enable global fuzzy search.
     *
     * @return true, if is enable global fuzzy search
     */
    boolean isEnableGlobalFuzzySearch();

    /**
     * Checks if is show global search panel on start.
     *
     * @return true, if is show global search panel on start
     */
    boolean isShowGlobalSearchPanelOnStart();

    /**
     * Checks if is enable sort.
     *
     * @return true, if is enable sort
     */
    boolean isEnableSort();

    /**
     * Checks if is enable edit.
     *
     * @return true, if is enable edit
     */
    boolean isEnableEdit();

    /**
     * Checks if is allow column reorder.
     *
     * @return true, if is allow column reorder
     */
    boolean isAllowColumnReorder();

    /**
     * Checks if is allow row reorder.
     *
     * @return true, if is allow row reorder
     */
    boolean isAllowRowReorder();

    /**
     * Checks if is allow column hide.
     *
     * @return true, if is allow column hide
     */
    boolean isAllowColumnHide();

    /**
     * Checks if is allow row hide.
     *
     * @return true, if is allow row hide
     */
    boolean isAllowRowHide();

}
