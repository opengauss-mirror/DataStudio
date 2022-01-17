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
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortErrors.
 *
 * @since 3.0.0
 */
public class SortErrors {

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum SORTERRORTYPE.
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
