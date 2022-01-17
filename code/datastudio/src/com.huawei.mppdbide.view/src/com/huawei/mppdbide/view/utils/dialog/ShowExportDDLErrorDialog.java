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

package com.huawei.mppdbide.view.utils.dialog;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ShowExportDDLErrorDialog.
 *
 * @since 3.0.0
 */
public class ShowExportDDLErrorDialog extends DSErrorDialog {

    /**
     * Instantiates a new show export DDL error dialog.
     *
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param message the message
     * @param status the status
     * @param displayMask the display mask
     */
    public ShowExportDDLErrorDialog(Shell parentShell, String dialogTitle, String message, IStatus status,
            int displayMask) {
        super(Display.getDefault().getActiveShell(), dialogTitle, message, status, displayMask);

    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Control dialogArea = super.createDialogArea(parent);
        return dialogArea;
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        final String yesButtonLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK) + "     ";

        final String noButtonLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.BTN_CANCEL) + "     ";

        createButton(parent, IDialogConstants.OK_ID, yesButtonLabel, true);

        createButton(parent, IDialogConstants.CANCEL_ID, noButtonLabel, true);
        createDetailsButton(parent);
    }

}
