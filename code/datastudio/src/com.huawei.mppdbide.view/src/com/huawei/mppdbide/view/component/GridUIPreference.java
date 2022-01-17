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

import org.eclipse.jface.preference.PreferenceStore;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.view.prefernces.EditTableOptionProviderForPreferences;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.prefernces.UserEncodingOption;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridUIPreference.
 *
 * @since 3.0.0
 */
public class GridUIPreference implements IGridUIPreference {

    /**
     * The pref store.
     */
    protected PreferenceStore prefStore;
    private static final String NULL_VALUE = "[NULL]";
    private static final String DEFAULT_VALUE = "[DEFAULT]";

    /**
     * Instantiates a new grid UI preference.
     */
    public GridUIPreference() {
        prefStore = PreferenceWrapper.getInstance().getPreferenceStore();
    }

    /**
     * Gets the column width.
     *
     * @return the column width
     */
    @Override
    public int getColumnWidth() {
        return (getColumnWidthStrategy() == ColumnWidthType.FIXED_WIDTH)
                ? prefStore.getInt(MPPDBIDEConstants.PREF_COLUMN_WIDTH_LENGTH)
                : 100;
    }

    /**
     * Checks if is show query area.
     *
     * @return true, if is show query area
     */
    @Override
    public boolean isShowQueryArea() {
        return true;
    }

    /**
     * Checks if is enable global fuzzy search.
     *
     * @return true, if is enable global fuzzy search
     */
    @Override
    public boolean isEnableGlobalFuzzySearch() {
        return true;
    }

    /**
     * Checks if is show global search panel on start.
     *
     * @return true, if is show global search panel on start
     */
    @Override
    public boolean isShowGlobalSearchPanelOnStart() {
        return true;
    }

    /**
     * Checks if is enable sort.
     *
     * @return true, if is enable sort
     */
    @Override
    public boolean isEnableSort() {
        return false;
    }

    /**
     * Checks if is enable edit.
     *
     * @return true, if is enable edit
     */
    @Override
    public boolean isEnableEdit() {
        return false;
    }

    /**
     * Checks if is allow column reorder.
     *
     * @return true, if is allow column reorder
     */
    @Override
    public boolean isAllowColumnReorder() {
        return true;
    }

    /**
     * Checks if is allow row reorder.
     *
     * @return true, if is allow row reorder
     */
    @Override
    public boolean isAllowRowReorder() {
        return false;
    }

    /**
     * Checks if is allow column hide.
     *
     * @return true, if is allow column hide
     */
    @Override
    public boolean isAllowColumnHide() {
        return false;
    }

    /**
     * Checks if is allow row hide.
     *
     * @return true, if is allow row hide
     */
    @Override
    public boolean isAllowRowHide() {
        return false;
    }

    /**
     * Gets the column width strategy.
     *
     * @return the column width strategy
     */
    @Override
    public ColumnWidthType getColumnWidthStrategy() {
        boolean isWidthByColumnData = prefStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COLUMN_LENGTH_BY_VALUE);
        return isWidthByColumnData ? ColumnWidthType.DATA_WIDTH : ColumnWidthType.FIXED_WIDTH;
    }

    /**
     * Checks if is support data export.
     *
     * @return true, if is support data export
     */
    @Override
    public boolean isSupportDataExport() {
        return true;
    }

    /**
     * Checks if is copy with column header.
     *
     * @return true, if is copy with column header
     */
    @Override
    public boolean isCopyWithColumnHeader() {
        return prefStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COPY_COLUMN_HEADER);
    }

    /**
     * Checks if is copywith row header.
     *
     * @return true, if is copywith row header
     */
    @Override
    public boolean isCopywithRowHeader() {
        return prefStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COPY_ROW_HEADER);
    }

    /**
     * Checks if is word wrap.
     *
     * @return true, if is word wrap
     */
    @Override
    public boolean isWordWrap() {
        return true;
    }

    /**
     * Checks if is fit to one page.
     *
     * @return true, if is fit to one page
     */
    @Override
    public boolean isFitToOnePage() {
        return false;
    }

    /**
     * Gets the max display data length.
     *
     * @return the max display data length
     */
    @Override
    public int getMaxDisplayDataLength() {
        return 1000;
    }

    /**
     * Edits the table data UI preference.
     *
     * @return true, if successful
     */
    @Override
    public boolean editTableDataUIPreference() {

        return !PreferenceWrapper.getInstance().getPreferenceStore()
                .getBoolean(EditTableOptionProviderForPreferences.EDITTABLE_COMMIT_ON_FAILURE);
    }

    /**
     * Gets the NULL value text.
     *
     * @return the NULL value text
     */
    @Override
    public String getNULLValueText() {
        return NULL_VALUE;
    }

    /**
     * Checks if is refresh supported.
     *
     * @return true, if is refresh supported
     */
    @Override
    public boolean isRefreshSupported() {
        return false;
    }

    /**
     * Checks if is need advanced copy.
     *
     * @return true, if is need advanced copy
     */
    @Override
    public boolean isNeedAdvancedCopy() {
        return true;
    }

    /**
     * Gets the default value text.
     *
     * @return the default value text
     */
    @Override
    public String getDefaultValueText() {
        return DEFAULT_VALUE;
    }

    /**
     * Checks if is adds the batch drop tool.
     *
     * @return true, if is adds the batch drop tool
     */
    @Override
    public boolean isAddBatchDropTool() {
        return false;
    }

    /**
     * Checks if is edits the query results flow.
     *
     * @return true, if is edits the query results flow
     */
    @Override
    public boolean isEditQueryResultsFlow() {
        return false;
    }

    /**
     * Checks if is show status bar.
     *
     * @return true, if is show status bar
     */
    @Override
    public boolean isShowStatusBar() {
        return true;
    }

    /**
     * Checks if is delete item supported.
     *
     * @return true, if is delete item supported
     */
    @Override
    public boolean isDeleteItemSupported() {
        return false;
    }

    /**
     * Gets the default encoding.
     *
     * @return the default encoding
     */
    @Override
    public String getDefaultEncoding() {
        return prefStore.getString(UserEncodingOption.DATA_STUDIO_ENCODING);
    }

    /**
     * Checks if is include encoding.
     *
     * @return true, if is include encoding
     */
    @Override
    public boolean isIncludeEncoding() {
        return prefStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_ENCODING);
    }

    /**
     * Replace tab.
     *
     * @return the int
     */
    @Override
    public int replaceTab() {
        return prefStore.getInt(MPPDBIDEConstants.TAB_WIDTH_OPTION);
    }

    /**
     * Checks if is adds the item supported.
     *
     * @return true, if is adds the item supported
     */
    @Override
    public boolean isAddItemSupported() {
        return false;
    }

    /**
     * Checks if is cancel changes supported.
     *
     * @return true, if is cancel changes supported
     */
    @Override
    public boolean isCancelChangesSupported() {
        return false;
    }
}
