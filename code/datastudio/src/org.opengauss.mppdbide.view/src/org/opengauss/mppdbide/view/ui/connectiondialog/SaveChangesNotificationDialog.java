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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.DBDisconnectConfirmationDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class SaveChangesNotificationDialog.
 *
 * @since 3.0.0
 */
public class SaveChangesNotificationDialog extends DBDisconnectConfirmationDialog {
    private Button dontShowAgain;
    private boolean isDontShowAgain;

    /**
     * Instantiates a new save changes notification dialog.
     *
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param dialogTitleImage the dialog title image
     * @param dialogMessage the dialog message
     * @param dialogImageType the dialog image type
     * @param subMessage the sub message
     * @param defaultIndex the default index
     */
    public SaveChangesNotificationDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage,
            String dialogMessage, int dialogImageType, String[] subMessage, int defaultIndex) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, subMessage, defaultIndex);
    }

    /**
     * Checks if is dont show again.
     *
     * @return true, if is dont show again
     */
    public boolean isDontShowAgain() {
        return isDontShowAgain;
    }

    /**
     * Creates the custom area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createCustomArea(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridData data = new GridData();
        data.horizontalAlignment = SWT.CENTER;
        comp.setLayout(new GridLayout());
        comp.setLayoutData(data);

        dontShowAgain = new Button(comp, SWT.CHECK | SWT.UP);
        dontShowAgain.setText(MessageConfigLoader.getProperty(IMessagesConstants.MSG_DO_NOT_SHOW_AGAIN));
        dontShowAgain.setSelection(false);

        return comp;
    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        this.isDontShowAgain = dontShowAgain.getSelection();
        super.buttonPressed(buttonId);
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button okBtn = createButton(parent, MessageDialog.OK,
                MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK), false);
        okBtn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_OK_001");
    }

}
