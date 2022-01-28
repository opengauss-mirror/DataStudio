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

package com.huawei.mppdbide.view.component.grid;

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
 * Description: The Class UserPromptMessageForUnqCons.
 *
 * @since 3.0.0
 */
public class UserPromptMessageForUnqCons extends DBDisconnectConfirmationDialog {
    private static final int USE_ALL_COLUMN = 0;
    private static final int CUSTOM_COLUMN = 1;
    private static final int CANCEL = 2;
    private Button rememberDecisionBtn;
    private boolean userSelectionOfRemember;

    /**
     * Checks if is user selection of remember.
     *
     * @return true, if is user selection of remember
     */
    public boolean isUserSelectionOfRemember() {
        return userSelectionOfRemember;
    }

    /**
     * Instantiates a new user prompt message for unq cons.
     *
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param dialogTitleImage the dialog title image
     * @param dialogMessage the dialog message
     * @param dialogImageType the dialog image type
     * @param subMessage the sub message
     * @param defaultIndex the default index
     */
    public UserPromptMessageForUnqCons(Shell parentShell, String dialogTitle, Image dialogTitleImage,
            String dialogMessage, int dialogImageType, String[] subMessage, int defaultIndex) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, subMessage, defaultIndex);
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

        rememberDecisionBtn = new Button(comp, SWT.CHECK | SWT.UP);
        rememberDecisionBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_REMEMBER_CHOICE));
        rememberDecisionBtn.setSelection(false);

        return comp;
    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        this.userSelectionOfRemember = rememberDecisionBtn.getSelection();
        super.buttonPressed(buttonId);
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button useAllColumnBtn = null;
        Button customUnqKeyBtn = null;
        Button cancelBtn = null;

        final String labelUseAllColBtn = "     "
                + MessageConfigLoader.getProperty(IMessagesConstants.USE_ALL_COLUMNS_LABEL) + "     ";

        final String labelCustomUnqKey = "     "
                + MessageConfigLoader.getProperty(IMessagesConstants.CUSTOM_UNIQUE_KEY_LABEL) + "     ";

        final String labelCancel = "      " + MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_LABEL) + "    ";

        useAllColumnBtn = createButton(parent, USE_ALL_COLUMN, labelUseAllColBtn, false);
        useAllColumnBtn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CONTINUE_001");

        customUnqKeyBtn = createButton(parent, CUSTOM_COLUMN, labelCustomUnqKey, true);
        customUnqKeyBtn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CANCEL_001");

        cancelBtn = createButton(parent, CANCEL, labelCancel, false);
        cancelBtn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CANCEL_001");

    }

}
