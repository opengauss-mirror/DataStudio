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

package com.huawei.mppdbide.view.component.grid.sort;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface MulticolumnSortConstants.
 *
 * @since 3.0.0
 */
public interface MulticolumnSortConstants {

    /**
     * The Constant PRIORITY_INDEX.
     */
    public static final int PRIORITY_INDEX = 0;

    /**
     * The Constant COLUMN_INDEX.
     */
    public static final int COLUMN_INDEX = 1;

    /**
     * The Constant DATATYPE_INDEX.
     */
    public static final int DATATYPE_INDEX = 2;

    /**
     * The Constant ORDER_INDEX.
     */
    public static final int ORDER_INDEX = 3;

    /**
     * The Constant BUFFER_LEN.
     */
    public static final int BUFFER_LEN = 10;

    /**
     * The Constant PRIORITY_COL_LEN.
     */
    public static final int PRIORITY_COL_LEN = 5 * BUFFER_LEN;

    /**
     * The Constant COLNAME_COL_LEN.
     */
    public static final int COLNAME_COL_LEN = MessageConfigLoader.getProperty(IMessagesConstants.COMBO_TEXT_SORT_COLUMN)
            .length() * BUFFER_LEN;

    /**
     * The Constant ORDER_COL_LEN.
     */
    public static final int ORDER_COL_LEN = MessageConfigLoader.getProperty(IMessagesConstants.COMBO_TEXT_SORT_OREDER)
            .length() * BUFFER_LEN;

    /**
     * The Constant DATATYPE_COL_LEN.
     */
    public static final int DATATYPE_COL_LEN = MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_DATATYPE)
            .length() * BUFFER_LEN;

}
