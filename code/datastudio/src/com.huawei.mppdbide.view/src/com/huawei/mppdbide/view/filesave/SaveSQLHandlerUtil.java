/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.filesave;

import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.view.ui.connectiondialog.SecurityDisclaimerDialog;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.uidisplay.uidisplayif.UIDisplayStateIf;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class SaveSQLHandlerUtil.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SaveSQLHandlerUtil {

    /**
     * Execute command.
     *
     * @param isSaveAs the is save as
     */
    public static void executeCommand(boolean isSaveAs) {
        SQLTerminal sqlTerminal = UIElement.getInstance().getVisibleTerminal();

        if (sqlTerminal == null) {
            return;
        }

        UIDisplayStateIf uiDisplayState = UIDisplayFactoryProvider.getUIDisplayStateIf();
        if (uiDisplayState.isDisclaimerReq() && enableSecurityWarningOption()) {
            SecurityDisclaimerDialog dialog = new SecurityDisclaimerDialog(Display.getDefault().getActiveShell());

            dialog.open();
            if (UIConstants.OK_ID != dialog.getReturnCode()) {
                return;
            }
        }
        SaveReloadSQLQueries saveReloadSQLQueries = new SaveReloadSQLQueries();
        if (isSaveAs) {
            saveReloadSQLQueries.saveToNewFile(sqlTerminal);
        } else {
            saveReloadSQLQueries.saveToExistFile(sqlTerminal);
        }
    }

    /**
     * Enable security warning option.
     *
     * @return true, if successful
     */
    private static boolean enableSecurityWarningOption() {
        return UserPreference.getInstance().getEnableSecurityWarningOption();
    }
}
