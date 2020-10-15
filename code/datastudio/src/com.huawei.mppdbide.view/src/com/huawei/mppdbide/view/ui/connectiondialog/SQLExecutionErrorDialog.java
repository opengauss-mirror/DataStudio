/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
