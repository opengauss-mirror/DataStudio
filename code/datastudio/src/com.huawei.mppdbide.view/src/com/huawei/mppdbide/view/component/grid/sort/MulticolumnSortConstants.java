/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
