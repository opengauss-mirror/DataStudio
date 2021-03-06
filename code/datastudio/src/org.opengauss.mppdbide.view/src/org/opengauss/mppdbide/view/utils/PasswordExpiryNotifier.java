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

package org.opengauss.mppdbide.view.utils;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseUtils;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class PasswordExpiryNotifier.
 *
 * @since 3.0.0
 */
public class PasswordExpiryNotifier {
    private Database database;

    /**
     * Instantiates a new password expiry notifier.
     *
     * @param database the database
     */
    public PasswordExpiryNotifier(Database database) {
        this.database = database;
    }

    /**
     * Check and notify password expiry.
     */
    public void checkAndNotifyPasswordExpiry() {
        float deadLine = 0;
        if (database != null && database.getDBType() == DBTYPE.OPENGAUSS) {
            try {
                String deadlineStamp = DatabaseUtils.getDeadlineInfo(MPPDBIDEConstants.FETCH_COUNT, database);
                if (deadlineStamp != null) {
                    deadLine = Float.parseFloat(deadlineStamp);
                }
                int deadLineTime = (int) Math.ceil(deadLine);

                float notifyTime = (float) DatabaseUtils.getNotifyInfo(MPPDBIDEConstants.FETCH_COUNT, database);

                if (deadLine > Math.abs(0.0f) && deadLine <= Math.abs(notifyTime)) {
                    MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                            IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                            MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_EXPIRE_CONFIRMATION),
                            MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_YET_TO_EXPIRE, deadLineTime),
                            MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));
                }

            } catch (DatabaseCriticalException e) {
                UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(e, database);
            } catch (DatabaseOperationException e) {
                UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(e, database);
            }
        }
    }
}
