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

package org.opengauss.mppdbide.view.component;

/**
 * 
 * Title: class
 * 
 * Description: The Class TabGridUIPreference.
 *
 * @since 3.0.0
 */
public class TabGridUIPreference extends GridUIPreference {

    /**
     * Checks if is allow column reorder.
     *
     * @return true, if is allow column reorder
     */
    @Override
    public boolean isAllowColumnReorder() {
        return false;
    }

    /**
     * Checks if is support data export.
     *
     * @return true, if is support data export
     */
    @Override
    public boolean isSupportDataExport() {
        return false;
    }

    /**
     * Gets the column width.
     *
     * @return the column width
     */
    @Override
    public int getColumnWidth() {
        return 300;
    }

    /**
     * Gets the column width strategy.
     *
     * @return the column width strategy
     */
    @Override
    public ColumnWidthType getColumnWidthStrategy() {
        return ColumnWidthType.FIXED_WIDTH;
    }

    /**
     * Gets the max display data length.
     *
     * @return the max display data length
     */
    @Override
    public int getMaxDisplayDataLength() {
        return 2000;
    }

    /**
     * Checks if is fit to one page.
     *
     * @return true, if is fit to one page
     */
    @Override
    public boolean isFitToOnePage() {
        return true;
    }

    /**
     * Edits the table data UI preference.
     *
     * @return true, if successful
     */
    @Override
    public boolean editTableDataUIPreference() {
        return false;
    }

    /**
     * Checks if is need advanced copy.
     *
     * @return true, if is need advanced copy
     */
    @Override
    public boolean isNeedAdvancedCopy() {
        return false;
    }

    /**
     * Gets the default value text.
     *
     * @return the default value text
     */
    @Override
    public String getDefaultValueText() {
        return "";
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
     * Checks if is include encoding.
     *
     * @return true, if is include encoding
     */
    @Override
    public boolean isIncludeEncoding() {

        return false;
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
}
