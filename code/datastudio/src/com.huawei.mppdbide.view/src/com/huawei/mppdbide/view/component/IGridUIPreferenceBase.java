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

package com.huawei.mppdbide.view.component;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IGridUIPreferenceBase.
 *
 * @since 3.0.0
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
