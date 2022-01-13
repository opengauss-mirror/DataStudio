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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.IScreenResolutionUtil;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserComboDialog.
 *
 * @since 3.0.0
 */
public abstract class UserComboDialog extends AbstractDialog {
    private Object obj;
    private Combo inputCombo;
    private Label comboLblNotice;
    private Button okButton;
    private boolean isOkButtonEnable;

    /**
     * Instantiates a new user combo dialog.
     *
     * @param prnt the prnt
     * @param serverObject the server object
     */
    public UserComboDialog(Shell prnt, Object serverObject) {
        super(prnt);
        this.obj = serverObject;
        setDefaultImage(getWindowImage());
    }

    /**
     * Gets the object.
     *
     * @return the object
     */
    protected Object getObject() {
        return this.obj;
    }

    /**
     * Configure shell.
     *
     * @param newShell the new shell
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getWindowTitle());
    }

    /**
     * Creates the dialog area.
     *
     * @param parentComposite the parent composite
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parentComposite) {
        Composite curComposite = (Composite) super.createDialogArea(parentComposite);
        curComposite.setLayout(new GridLayout(1, false));

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalIndent = 15;
        gridData.verticalIndent = 0;
        gridData.minimumWidth = IScreenResolutionUtil.getScreenWidth() / 6;
        gridData.heightHint = IScreenResolutionUtil.getScreenHeight() / 7;

        curComposite.setLayoutData(gridData);

        Label lebelTxt = new Label(curComposite, SWT.WRAP);
        lebelTxt.setText(getHeader());
        lebelTxt.pack();

        int txtProp = SWT.BORDER | SWT.SINGLE;
        txtProp |= SWT.READ_ONLY;

        inputCombo = new Combo(curComposite, txtProp);
        inputCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        final ControlDecoration decoration = new ControlDecoration(inputCombo, SWT.TOP | SWT.LEFT);

        // use an existing image
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());

        // set image
        decoration.setImage(image);

        // always show decoration
        decoration.setShowOnlyOnFocus(false);

        comboDisplayValues(inputCombo);

        inputCombo.forceFocus();

        comboLblNotice = new Label(curComposite, SWT.WRAP);
        GridData comboLblNoticeGD = new GridData(SWT.FILL, SWT.NONE, true, false, 1, 3);
        comboLblNoticeGD.heightHint = 80;
        comboLblNotice.setLayoutData(comboLblNoticeGD);
        comboLblNotice.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        comboLblNotice.setText(' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ');
        return curComposite;
    }

    /**
     * Gets the user input.
     *
     * @return the user input
     */
    protected String getUserInput() {
        if (inputCombo.getSelectionIndex() >= 0) {
            return inputCombo.getText();
        } else {
            return inputCombo.getText().trim();
        }
    }

    /**
     * Prints the error message.
     *
     * @param message the message
     */
    public void printErrorMessage(String message) {
        printColourMessageLebel(message, true);
    }

    /**
     * Prints the message.
     *
     * @param message the message
     */
    public void printMessage(String message) {
        printColourMessageLebel(message, false);
    }

    private void printColourMessageLebel(String msg, boolean isErrormsg) {
        if (comboLblNotice.isDisposed()) {
            return;
        }
        if (isErrormsg) {
            comboLblNotice.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        } else {
            comboLblNotice.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        }
        comboLblNotice.setText(msg);
        comboLblNotice.redraw();
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        performOkOperation();
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        final String comboOkLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK)
                + "     ";
        final String comboCancelLabel = "     "
                + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC) + "     ";
        this.okButton = createButton(parent, IDialogConstants.OK_ID, comboOkLabel, true);
        setOkButtonEnabled(isOkButtonEnable());
        createButton(parent, IDialogConstants.CANCEL_ID, comboCancelLabel, false);
    }

    /**
     * Sets the ok button enabled.
     *
     * @param isEnabled the new ok button enabled
     */
    public void setOkButtonEnabled(boolean isEnabled) {
        this.isOkButtonEnable = isEnabled;

        if (null != okButton) {
            if (!okButton.isDisposed()) {
                okButton.setEnabled(isEnabled);
            }
        }
    }

    /**
     * Checks if is ok button enable.
     *
     * @return true, if is ok button enable
     */
    public boolean isOkButtonEnable() {
        return isOkButtonEnable;
    }
}
