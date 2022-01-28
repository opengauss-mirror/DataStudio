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

package com.huawei.mppdbide.view.utils.dialog;

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
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.data.DSViewDataManager;
import com.huawei.mppdbide.view.utils.consts.WHICHOPTION;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSMessageDialogWithDoNotShowAgain.Any kind of dialog
 * box may need do not show again/remember this decision s
 *
 * @since 3.0.0
 */
public class DSMessageDialogWithDoNotShowAgain extends MessageDialog {
    private Button checkboxButton;
    private WHICHOPTION option;

    /**
     * Instantiates a new DS message dialog with do not show again.
     *
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param dialogTitleImage the dialog title image
     * @param dialogMessage the dialog message
     * @param dialogImageType the dialog image type
     * @param dialogButtonLabels the dialog button labels
     * @param defaultIndex the default index
     * @param op the op
     */
    public DSMessageDialogWithDoNotShowAgain(Shell parentShell, String dialogTitle, Image dialogTitleImage,
            String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex, WHICHOPTION op) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
                defaultIndex);
        checkboxButton = null;
        option = op;
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Control ara = super.createDialogArea(parent);
        GridData dta = (GridData) ara.getLayoutData();
        ara.setLayoutData(dta);
        dta.verticalIndent = -2;
        Label label = new Label(parent, SWT.UP);
        label.setText("");
        checkboxButton = new Button(parent, SWT.CHECK | SWT.UP);
        checkboxButton.setSelection(false);
        checkboxButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.MSG_DO_NOT_SHOW_AGAIN));
        parent.layout();
        return ara;

    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        switch (option) {
            case MULTIPLE_QUERY: {
                DSViewDataManager.getInstance().setShowExplainPlanWarningsMultipleQuery(checkboxButton.getSelection());
                break;
            }
            case ANALYZE_QUERY_EXECUTION: {
                DSViewDataManager.getInstance().setShowExplainPlanWarningsAnalyzeQuery(checkboxButton.getSelection());
                break;
            }
            case BREAKPOINT: {
                DSViewDataManager.getInstance().setDebugBkptNotSupportedPopup(checkboxButton.getSelection());
                break;
            }
            case COMMIT_CONFIRMATION: {
                DSViewDataManager.getInstance().setShowCommitConfirmation(checkboxButton.getSelection());
                break;
            }
            case ROLLBACK_CONFIRMATION: {
                DSViewDataManager.getInstance().setShowRollbackConfirmation(checkboxButton.getSelection());
                break;
            }
            default: {
                break;
            }
        }

        super.buttonPressed(buttonId);
    }
}
