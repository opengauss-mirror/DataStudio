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

package org.opengauss.mppdbide.view.ui.connectiondialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class SSLWarningDialog.
 *
 * @since 3.0.0
 */
public class SSLWarningDialog extends MessageDialog {
    private Button checkboxButton;

    /**
     * Instantiates a new SSL warning dialog.
     *
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param dialogTitleImage the dialog title image
     * @param dialogMessage the dialog message
     * @param dialogImageType the dialog image type
     * @param dialogButtonLabels the dialog button labels
     * @param defaultIndex the default index
     */
    public SSLWarningDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
            int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {

        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
                defaultIndex);
        checkboxButton = null;

    }

    /**
     * Configure shell.
     *
     * @param shell the shell
     */
    @Override
    protected void configureShell(Shell shell) {

        super.configureShell(shell);
        shell.setImage(IconUtility.getIconImage(IiconPath.ICO_SERVER, this.getClass()));
    }

    /**
     * Checks if is resizable.
     *
     * @return true, if is resizable
     */
    public boolean isResizable() {
        return true;
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Control area = super.createDialogArea(parent);
        GridData data = (GridData) area.getLayoutData();
        area.setLayoutData(data);
        data.verticalIndent = -2;
        Label lbl = new Label(parent, SWT.UP);
        lbl.setText("");
        checkboxButton = new Button(parent, SWT.CHECK | SWT.UP);
        checkboxButton.setSelection(false);
        checkboxButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.MSG_DO_NOT_SHOW_AGAIN));
        parent.layout();
        return area;

    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button continueButton = null;
        Button cancelButton = null;

        final String continueLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CONT)
                + "     ";

        final String cancelLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";

        continueButton = createButton(parent, UIConstants.CONTINUE_ID, continueLabel, false);
        continueButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CONTINUE_001");

        cancelButton = createButton(parent, UIConstants.CANCEL_ID, cancelLabel, true);
        cancelButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CANCEL_001");

    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {

        if (buttonId == UIConstants.CONTINUE_ID) {
            if (checkboxButton.getSelection()) {
                UIDisplayFactoryProvider.getUIDisplayStateIf().setSSLoff(false);

            } else {
                UIDisplayFactoryProvider.getUIDisplayStateIf().setSSLoff(true);
            }

        }

        setReturnCode(buttonId);
        close();
    }

}
