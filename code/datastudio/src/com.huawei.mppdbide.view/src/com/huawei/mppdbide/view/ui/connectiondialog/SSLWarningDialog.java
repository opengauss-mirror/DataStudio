/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.connectiondialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class SSLWarningDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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