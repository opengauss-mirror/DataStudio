/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
