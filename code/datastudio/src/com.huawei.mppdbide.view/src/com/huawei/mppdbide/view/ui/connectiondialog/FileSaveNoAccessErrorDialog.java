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

package com.huawei.mppdbide.view.ui.connectiondialog;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.filesave.SaveReloadSQLQueries;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.dialog.DSErrorDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class FileSaveNoAccessErrorDialog.
 *
 * @since 3.0.0
 */
public class FileSaveNoAccessErrorDialog extends DSErrorDialog {
    private SQLTerminal sqlTerminal;

    /**
     * Instantiates a new file save no access error dialog.
     *
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param message the message
     * @param status the status
     * @param displayMask the display mask
     * @param sqlTerminal the sql terminal
     */
    public FileSaveNoAccessErrorDialog(Shell parentShell, String dialogTitle, String message, IStatus status,
            int displayMask, SQLTerminal sqlTerminal) {
        super(parentShell, dialogTitle, message, status, displayMask);
        this.sqlTerminal = sqlTerminal;

    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        final String saveAsButtonLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MENU_SAVE_AS)
                + "     ";
        createButton(parent, IDialogConstants.YES_ID, saveAsButtonLabel, true);

        final String cancelButtonLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.BTN_CANCEL)
                + "     ";
        createButton(parent, IDialogConstants.CANCEL_ID, cancelButtonLabel, true);

        createDetailsButton(parent);
    }

    /**
     * Button pressed.
     *
     * @param id the id
     */
    @Override
    protected void buttonPressed(int id) {
        super.buttonPressed(id);

        // handle Save As option
        if (IDialogConstants.YES_ID == id) {
            close();
            new SaveReloadSQLQueries().saveToNewFile(this.sqlTerminal);
        }

    }

}
