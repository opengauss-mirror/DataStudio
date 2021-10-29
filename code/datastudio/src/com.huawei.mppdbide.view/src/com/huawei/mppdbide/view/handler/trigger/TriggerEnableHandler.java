/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.trigger;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionManager;
import com.huawei.mppdbide.bl.serverdatacache.TriggerMetaData;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;

/**
 * Title: class
 * Description: The Class TriggerEnableHandler.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 30,04,2021]
 * @since 30,04,2021
 */
public class TriggerEnableHandler {
    /**
     * Execute
     *
     * @param Shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        baseExecute(shell, true);
    }

    /**
     * Can execute
     *
     * @return boolean if can execute
     */
    @CanExecute
    public boolean canExecute() {
        return baseCanExecute(true);
    }

    /**
     * Base execute
     *
     * @param Shell the shell
     * @param boolean true if is enable
     */
    public void baseExecute(final Shell shell, boolean enable) {
        TriggerMetaData trigger = TriggerUtils.getTrigger();
        if (trigger == null) {
            return;
        }

        ConnectionManager connManager = trigger.getNamespace().getDatabase().getConnectionManager();
        DBConnection conn = null;
        MPPDBIDEException exp = null;
        String optSql = trigger.alterEnableString(enable);

        try {
            conn = connManager.getFreeConnection();
            conn.execNonSelect(optSql);
            trigger.setEnable(enable);
            IHandlerUtilities.pritnAndRefresh(trigger.getParent());
        } catch (MPPDBIDEException catchedExp) {
            MPPDBIDELoggerUtility.error("enable constraint failed!");
            exp = catchedExp;
        } finally {
            if (conn != null) {
                connManager.releaseConnection(conn);
                conn = null;
            }
        }
        if (exp != null) {
            MPPDBIDEDialogs.generateDSErrorDialog("enable constraint error",
                    "enable constraint error",
                    exp.getMessage(),
                    exp);
        }
    }

    /**
     * Base can execute
     *
     * @param boolean if is enable
     * @return boolean if can execute
     */
    public boolean baseCanExecute(boolean isEnable) {
        TriggerMetaData trigger = TriggerUtils.getTrigger();
        if (trigger == null) {
            return false;
        }
        return trigger.getEnable() != isEnable;
    }
}
