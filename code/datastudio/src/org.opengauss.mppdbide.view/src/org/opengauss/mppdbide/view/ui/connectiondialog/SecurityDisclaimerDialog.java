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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
 * Description: The Class SecurityDisclaimerDialog.
 *
 * @since 3.0.0
 */
public class SecurityDisclaimerDialog extends Dialog {

    /**
     * The confirmation btn.
     */
    protected Button confirmationBtn;
    private Label lblNotice;
    private Button okButton;
    private Button cnlButton;

    /**
     * Instantiates a new security disclaimer dialog.
     *
     * @param parent the parent
     */
    public SecurityDisclaimerDialog(Shell parent) {
        super(parent);
        super.setDefaultImage(getWindowImage());
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        final String okLabel = MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK);
        final String cancelLabel = MessageConfigLoader.getProperty(IMessagesConstants.BTN_CANCEL);
        okButton = createButton(parent, UIConstants.OK_ID, okLabel, true);
        cnlButton = createButton(parent, UIConstants.CANCEL_ID, cancelLabel, true);
        okButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_OK_001");
        cnlButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_CANCEL_001");
    }

    /**
     * Configure shell.
     *
     * @param newShellSecurity the new shell security
     */
    @Override
    protected void configureShell(Shell newShellSecurity) {
        super.configureShell(newShellSecurity);
        newShellSecurity.setText(getWindowTitle());
    }

    /**
     * Creates the dialog area.
     *
     * @param parentSecurity the parent security
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parentSecurity) {
        Composite currComposite = (Composite) super.createDialogArea(parentSecurity);
        currComposite.setLayout(new GridLayout(1, false));

        GridData gridDataSecurity = new GridData();
        gridDataSecurity.grabExcessHorizontalSpace = true;
        gridDataSecurity.horizontalAlignment = GridData.FILL;
        gridDataSecurity.verticalAlignment = GridData.FILL;
        gridDataSecurity.horizontalIndent = 5;
        gridDataSecurity.verticalIndent = 0;
        gridDataSecurity.minimumWidth = 265;

        currComposite.setLayoutData(gridDataSecurity);

        lblNotice = new Label(currComposite, SWT.WRAP);
        lblNotice.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        StringBuilder notice = new StringBuilder(
                MessageConfigLoader.getProperty(IMessagesConstants.MSG_DS_NO_DATA_ENCRYPT_DISCLAIMER));

        notice.append(
                MessageConfigLoader.getProperty(IMessagesConstants.MSG_DS_NO_DATA_ENCRYPT_DISCLAIMER_SECONDPART2));

        lblNotice.setText(notice.toString());

        confirmationBtn = new Button(currComposite, SWT.CHECK);
        confirmationBtn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_DISCLAIMER_SEC_001");
        confirmationBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.MSG_DO_NOT_SHOW_AGAIN));
        return currComposite;
    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == UIConstants.OK_ID) {
            if (confirmationBtn.getSelection()) {
                UIDisplayFactoryProvider.getUIDisplayStateIf().setDisclaimerReq(false);
            }
        } else {
            UIDisplayFactoryProvider.getUIDisplayStateIf().setDisclaimerReq(true);
        }
        setReturnCode(buttonId);
        close();
    }

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    protected String getWindowTitle() {
        return MessageConfigLoader.getProperty(IMessagesConstants.TITLE_DISCLAIMER);
    }

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected final Image getWindowImage() {
        return IconUtility.getIconImage(IiconPath.ICON_WARNING, this.getClass());
    }

}
