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

package org.opengauss.mppdbide.view.utils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBDisconnectConfirmationDialog.
 *
 * @since 3.0.0
 */
public class DBDisconnectConfirmationDialog extends MessageDialog {

    /**
     * Instantiates a new DB disconnect confirmation dialog.
     *
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param dialogTitleImage the dialog title image
     * @param dialogMessage the dialog message
     * @param dialogImageType the dialog image type
     * @param subMessage the sub message
     * @param defaultIndex the default index
     */
    public DBDisconnectConfirmationDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage,
            String dialogMessage, int dialogImageType, String[] subMessage, int defaultIndex) {

        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, subMessage, defaultIndex);

    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        return super.createDialogArea(parent);
    }

    

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setImage(IconUtility.getIconImage(IiconPath.ICO_DISCONNECTED_DB, this.getClass()));
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
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        Button yesButton = null;
        Button noButton = null;

        final String labelYesButton = "     " + MessageConfigLoader.getProperty(IMessagesConstants.YES_OPTION)
                + "     ";

        final String labelNoButton = "     " + MessageConfigLoader.getProperty(IMessagesConstants.NO_OPTION) + "     ";

        yesButton = createButton(parent, UIConstants.OK_ID, labelYesButton, false);
        yesButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CONTINUE_001");

        noButton = createButton(parent, UIConstants.CANCEL_ID, labelNoButton, true);
        noButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CANCEL_001");

    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        setReturnCode(buttonId);
        close();
    }

}
