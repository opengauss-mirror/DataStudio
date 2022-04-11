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

package org.opengauss.mppdbide.view.filesave;

import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.view.ui.connectiondialog.SecurityDisclaimerDialog;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.uidisplay.uidisplayif.UIDisplayStateIf;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.UserPreference;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class SaveSQLHandlerUtil.
 *
 * @since 3.0.0
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
