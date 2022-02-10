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

package org.opengauss.mppdbide.bl.preferences;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IBLPreference.
 */

public interface IBLPreference {

    /**
     * Gets the SQL history size.
     *
     * @return the SQL history size
     */
    int getSQLHistorySize();

    /**
     * Gets the SQL query length.
     *
     * @return the SQL query length
     */
    int getSQLQueryLength();

    /**
     * Gets the DS encoding.
     *
     * @return the DS encoding
     */
    String getDSEncoding();

    /**
     * Gets the file encoding.
     *
     * @return the file encoding
     */
    String getFileEncoding();

    /**
     * Checks if is include encoding.
     *
     * @return true, if is include encoding
     */
    boolean isIncludeEncoding();

    /**
     * Get Object Browser lazy rendering object count
     *
     * @return object count
     */
    default int getLazyRenderingObjectCount() {
        return MPPDBIDEConstants.DEFAULT_TREE_NODE_COUNT;
    };

    /**
     * gets the date format
     * 
     * @return the date format
     */
    String getDateFormat();

    /**
     * gets the time format
     * 
     * @return the time format
     */
    String getTimeFormat();

    /**
     * gets import file size limit
     * 
     * @return returns the size limit of import file
     */
    default int getImportFileSizeInMb() {
        return 0;
    };

}
