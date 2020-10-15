/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.connectiondialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.DBDisconnectConfirmationDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class SaveChangesNotificationDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
