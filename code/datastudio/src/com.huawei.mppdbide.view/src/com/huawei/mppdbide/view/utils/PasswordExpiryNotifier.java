/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DatabaseUtils;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class PasswordExpiryNotifier.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
                            MessageConfigLoader.getProperty(IMessagesConstants.PWD_EXPIRE_CONFIRMATION),
                            MessageConfigLoader.getProperty(IMessagesConstants.PWD_YET_TO_EXPIRE, deadLineTime),
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
