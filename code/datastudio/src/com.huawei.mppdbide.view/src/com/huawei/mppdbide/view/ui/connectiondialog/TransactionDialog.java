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

package com.huawei.mppdbide.view.ui.connectiondialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class TransactionDialog.
 *
 * @since 3.0.0
 */
public class TransactionDialog extends Dialog {
    private String lable;
    private StyledText styledText;

    /**
     * Instantiates a new transaction dialog.
     *
     * @param lable the lable
     * @param shell the shell
     */
    public TransactionDialog(String lable, Shell shell) {
        super(shell);
        this.lable = lable;
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        FormLayout formlayout = new FormLayout();
        Composite transComp = new Composite(parent, SWT.NONE);
        transComp.setLayout(formlayout);
        FormData data = new FormData();
        data.top = new FormAttachment(transComp, 10);
        data.left = new FormAttachment(3, 5);
        data.width = 423;
        data.height = 33;
        Label label = new Label(transComp, SWT.WRAP);
        label.setText(MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_DIALOG_BODY));
        label.setLayoutData(data);
        data = new FormData();
        data.top = new FormAttachment(label, 10);
        data.left = new FormAttachment(4, 5);
        data.bottom = new FormAttachment(103, -5);
        data.right = new FormAttachment(100, -5);
        data.width = 423;
        data.height = 148;
        styledText = new StyledText(transComp, SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        styledText.setBlockSelection(true);
        styledText.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_IBEAM));
        styledText.setMargins(4, 4, 4, 4);
        styledText.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
        styledText.setLayoutData(new GridData(GridData.FILL_BOTH));
        styledText.setLayoutData(data);
        styledText.setText(this.lable);
        return transComp;
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, OK, MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_DIALOG_OK_BUTTION),
                true);
    }

    /**
     * Configure shell.
     *
     * @param newShellWindow the new shell window
     */
    @Override
    protected void configureShell(Shell newShellWindow) {
        super.configureShell(newShellWindow);
        newShellWindow.setText(MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_DIALOG_TITLE));
        newShellWindow.setSize(500, 300);
        newShellWindow.setImage(IconUtility.getIconImage(IiconPath.ICO_TRANSACTION_COMMIT, this.getClass()));
    }

}
