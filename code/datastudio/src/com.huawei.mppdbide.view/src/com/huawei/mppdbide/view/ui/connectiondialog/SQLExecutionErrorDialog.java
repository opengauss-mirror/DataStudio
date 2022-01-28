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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.dialog.DSErrorDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLExecutionErrorDialog.
 *
 * @since 3.0.0
 */
public class SQLExecutionErrorDialog extends DSErrorDialog {
    private Button checkboxButton;
    private boolean isRememberEnabled = false;

    /**
     * Instantiates a new SQL execution error dialog.
     *
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param dialogMessage the dialog message
     * @param status the status
     * @param defaultIndex the default index
     */
    public SQLExecutionErrorDialog(Shell parentShell, String dialogTitle, String dialogMessage, IStatus status,
            int defaultIndex) {
        super(Display.getDefault().getActiveShell(), dialogTitle, dialogMessage, status, defaultIndex);
        checkboxButton = null;
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
        checkboxButton = new Button(parent, SWT.CHECK | SWT.NONE);
        checkboxButton.setSelection(false);
        checkboxButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_WHILE_EXECUTION));
        checkboxButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                isRememberEnabled = checkboxButton.getSelection();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Nothing to do
            }
        });
        return dialogArea;
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        final String yesButtonLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_YES)
                + "     ";

        final String noButtonLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_NO)
                + "     ";

        createButton(parent, IDialogConstants.OK_ID, yesButtonLabel, true);

        createButton(parent, IDialogConstants.CANCEL_ID, noButtonLabel, true);
        createDetailsButton(parent);
    }

    /**
     * Checks if is remember.
     *
     * @return true, if is remember
     */
    public boolean isRemember() {
        return isRememberEnabled;
    }

}
