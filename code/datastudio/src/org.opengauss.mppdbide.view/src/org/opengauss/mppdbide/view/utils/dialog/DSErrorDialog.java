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

package org.opengauss.mppdbide.view.utils.dialog;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSErrorDialog.
 *
 * @since 3.0.0
 */
public class DSErrorDialog extends ErrorDialog {

    private Button detailsButton;

    /**
     * Instantiates a new DS error dialog.
     *
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param message the message
     * @param status the status
     * @param displayMask the display mask
     */
    public DSErrorDialog(Shell parentShell, String dialogTitle, String message, IStatus status, int displayMask) {
        super(parentShell, dialogTitle, message, status, displayMask);

    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        final String yesButtonLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK) + "     ";
        createButton(parent, IDialogConstants.OK_ID, yesButtonLabel, true);
        createDetailsButton(parent);
    }

    /**
     * Creates the button.
     *
     * @param parent the parent
     * @param id the id
     * @param label the label
     * @param defaultButton the default button
     * @return the button
     */
    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {

        if (id == IDialogConstants.DETAILS_ID) {
            detailsButton = super.createButton(parent, IDialogConstants.DETAILS_ID,
                    MessageConfigLoader.getProperty(IMessagesConstants.BUTTON_LABEL_SHOW_DETAILS), false);
            return detailsButton;

        } else {
            return super.createButton(parent, id, label, defaultButton);
        }

    }

    /**
     * The is details shown.
     */
    boolean isDetailsShown = false;

    /**
     * Button pressed.
     *
     * @param id the id
     */
    @Override
    protected void buttonPressed(int id) {
        detailsButton.setRedraw(false);
        super.buttonPressed(id);
        if (id == IDialogConstants.DETAILS_ID) {
            if (isDetailsShown) {
                detailsButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.BUTTON_LABEL_SHOW_DETAILS));
            } else {
                detailsButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.BUTTON_LABEL_HIDE_DETAILS));
            }
            isDetailsShown = !isDetailsShown;
        }
        if (!detailsButton.isDisposed()) {
            detailsButton.setRedraw(true);
        }

    }

    /**
     * Open DS error.
     *
     * @param parentShell the parent shell
     * @param title the title
     * @param message the message
     * @param status the status
     * @param displayMask the display mask
     * @return the int
     */
    public static int openDSError(Shell parentShell, String title, String message, IStatus status, int displayMask) {
        DSErrorDialog dialog = new DSErrorDialog(parentShell, title, message, status, displayMask);
        return dialog.open();
    }

    /**
     * Open DS error.
     *
     * @param parent the parent
     * @param dialogTitle the dialog title
     * @param message the message
     * @param status the status
     * @return the int
     */
    public static int openDSError(Shell parent, String dialogTitle, String message, IStatus status) {
        return openDSError(parent, dialogTitle, message, status,
                IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR);
    }

}
