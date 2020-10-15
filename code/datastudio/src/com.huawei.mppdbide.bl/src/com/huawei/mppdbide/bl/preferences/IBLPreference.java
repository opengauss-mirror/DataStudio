/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.preferences;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IBLPreference.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
