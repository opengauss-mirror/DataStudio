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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.edit.editor.IComboBoxDataProvider;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IGridUIPreference.
 *
 * @since 3.0.0
 */
public interface IGridUIPreference extends IGridUIPreferenceBase {

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum ColumnWidthType.
     */
    enum ColumnWidthType {

        /**
         * The fixed width.
         */
        FIXED_WIDTH,
        /**
         * The header width.
         */
        HEADER_WIDTH,
        /**
         * The data width.
         */
        DATA_WIDTH
    };

    /**
     * Gets the column width strategy.
     *
     * @return the column width strategy
     */
    ColumnWidthType getColumnWidthStrategy();

    /**
     * Checks if is support data export.
     *
     * @return true, if is support data export
     */
    boolean isSupportDataExport();

    /**
     * Checks if is copy with column header.
     *
     * @return true, if is copy with column header
     */
    boolean isCopyWithColumnHeader();

    /**
     * Checks if is copywith row header.
     *
     * @return true, if is copywith row header
     */
    boolean isCopywithRowHeader();

    /**
     * Checks if is word wrap.
     *
     * @return true, if is word wrap
     */
    boolean isWordWrap();

    /**
     * Checks if is fit to one page.
     *
     * @return true, if is fit to one page
     */
    boolean isFitToOnePage();

    /**
     * Gets the max display data length.
     *
     * @return the max display data length
     */
    int getMaxDisplayDataLength();

    /**
     * Edits the table data UI preference.
     *
     * @return true, if successful
     */
    boolean editTableDataUIPreference();

    /**
     * Gets the NULL value text.
     *
     * @return the NULL value text
     */
    String getNULLValueText();

    /**
     * Checks if is need advanced copy.
     *
     * @return true, if is need advanced copy
     */
    boolean isNeedAdvancedCopy();

    /**
     * Gets the default value text.
     *
     * @return the default value text
     */
    String getDefaultValueText();

    /**
     * Checks if is adds the batch drop tool.
     *
     * @return true, if is adds the batch drop tool
     */
    boolean isAddBatchDropTool();

    /**
     * Checks if is edits the query results flow.
     *
     * @return true, if is edits the query results flow
     */
    boolean isEditQueryResultsFlow();

    /**
     * Checks if is show status bar.
     *
     * @return true, if is show status bar
     */
    boolean isShowStatusBar();

    /**
     * Checks if is include encoding.
     *
     * @return true, if is include encoding
     */
    boolean isIncludeEncoding();

    /**
     * Gets the default encoding.
     *
     * @return the default encoding
     */
    String getDefaultEncoding();

    /**
     * Checks if is adds the item supported.
     *
     * @return true, if is adds the item supported
     */
    boolean isAddItemSupported();

    /**
     * Checks if is delete item supported.
     *
     * @return true, if is delete item supported
     */
    boolean isDeleteItemSupported();

    /**
     * Checks if is cancel changes supported.
     *
     * @return true, if is cancel changes supported
     */
    boolean isCancelChangesSupported();

    /**
     * Checks if is refresh supported.
     *
     * @return true, if is refresh supported
     */
    boolean isRefreshSupported();

    /**
     * Gets the combo box data providers.
     *
     * @return the combo box data providers
     */
    default Map<String, IComboBoxDataProvider> getComboBoxDataProviders() {
        // returning an empty Map for default behaviour instead of null
        return new HashMap<>();
    }

    /**
     * Checks if is show right click menu.
     *
     * @return true, if is show right click menu
     */
    default boolean isShowRightClickMenu() {
        return false;
    }

    /**
     * Checks if is show generate insert.
     *
     * @return true, if is show generate insert
     */
    default boolean isShowGenerateInsert() {
        return false;
    }

    /**
     * Checks if is start select query.
     *
     * @return true, if is start select query
     */
    default boolean isStartSelectQuery() {
        return false;
    }

    /**
     * Gets the selected encoding.
     *
     * @return the selected encoding
     */
    default String getSelectedEncoding() {
        return null;
    }

    /**
     * Checks if is closed connection.
     *
     * @return true, if is closed connection
     */
    default boolean isClosedConnection() {
        return false;
    }

    /**
     * Checks if is need create text mode.
     *
     * @return true, if is need create text mode
     */
    default boolean isNeedCreateTextMode() {
        return false;
    }

    /**
     * Replace tab.
     *
     * @return the int
     */
    default int replaceTab() {
        return 0;
    }

    /**
     * Checks if is show load more record button.
     *
     * @return true, if is show load more record button
     */
    default boolean isShowLoadMoreRecordButton() {
        return false;
    }

    /**
     * checks if add export all button
     * 
     * @return true, if is add export all button
     */
    default boolean isAddItemExportAll() {
        return true;
    }
}
