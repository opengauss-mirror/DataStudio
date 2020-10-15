/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.sort;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortErrors.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SortErrors {

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum SORTERRORTYPE.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    public enum SORTERRORTYPE {

        /**
         * The blank column.
         */
        BLANK_COLUMN,
        /**
         * The duplicate column.
         */
        DUPLICATE_COLUMN,
        /**
         * The no error.
         */
        NO_ERROR;
    };

    /**
     * Generate error dialog.
     *
     * @param err the err
     * @param columnDetails the column details
     */
    public static void generateErrorDialog(SORTERRORTYPE err, String[] columnDetails) {
        String msgDialogTitle = MessageConfigLoader.getProperty(IMessagesConstants.SORT_ERROR_WINDOW_TITLE);
        String msg = null;
        switch (err) {
            case BLANK_COLUMN: {
                msg = MessageConfigLoader.getProperty(IMessagesConstants.SORT_ERROR_BLANK_COLUMN, columnDetails[0]);
                break;
            }
            case DUPLICATE_COLUMN: {
                msg = MessageConfigLoader.getProperty(IMessagesConstants.SORT_ERROR_DUPLICATE_COLUMN, columnDetails[0],
                        columnDetails[1]);
                break;
            }
            default: {
                break;
            }
        }

        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, msgDialogTitle, msg);
    }
}
