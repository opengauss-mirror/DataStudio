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

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ModifyPartitionDialog.
 *
 * @since 3.0.0
 */
public abstract class ModifyPartitionDialog extends AbstractDialog {
    private Object obj;
    private Combo inputCombo;
    private Label comboLblNotice;
    private Button okButton;
    private Button cancelButton;
    private boolean isOkButtonEnable;

    /**
     * Instantiates a new modify partition dialog.
     *
     * @param shell the shell
     * @param serverObject the server object
     */
    public ModifyPartitionDialog(Shell shell, Object serverObject) {
        super(shell);
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
     * @param newShel the new shel
     */
    @Override
    protected void configureShell(Shell newShel) {
        super.configureShell(newShel);
        newShel.setText(getWindowTitle());
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite currentComposite = (Composite) super.createDialogArea(parent);
        currentComposite.setLayout(new GridLayout(1, false));

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalIndent = 15;
        gridData.verticalIndent = 0;
        gridData.minimumWidth = 265;
        gridData.heightHint = 150;

        currentComposite.setLayoutData(gridData);

        Label lebelText = new Label(currentComposite, SWT.NONE);
        lebelText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        lebelText.setText(getHeader());

        int txtProp = SWT.BORDER | SWT.SINGLE;
        txtProp |= SWT.READ_ONLY;

        inputCombo = new Combo(currentComposite, txtProp);
        inputCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        final ControlDecoration deco = new ControlDecoration(inputCombo, SWT.TOP | SWT.LEFT);

        // use an existing image
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());

        deco.setImage(image);

        deco.setShowOnlyOnFocus(false);

        comboDisplayValues(inputCombo);

        inputCombo.forceFocus();

        comboLblNotice = new Label(currentComposite, SWT.WRAP);
        comboLblNotice.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
        comboLblNotice.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        comboLblNotice.setText(' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ');

        return currentComposite;
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
     * Prints the message.
     *
     * @param msg the msg
     */
    public void printMessage(String msg) {
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
     * @param parentObj the parent obj
     */
    @Override
    protected void createButtonsForButtonBar(Composite parentObj) {
        final String oklabel = "    " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "    ";
        final String cancelLabel = "    " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "    ";
        okButton = createButton(parentObj, UIConstants.OK_ID, oklabel, true);
        cancelButton = createButton(parentObj, UIConstants.CANCEL_ID, cancelLabel, false);
    }

    /**
     * Enable buttons.
     */
    public void enableButtons() {
        if (okButton.isDisposed() || cancelButton.isDisposed()) {
            return;
        }
        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    /**
     * Sets the ok button enabled.
     *
     * @param isEnabled the new ok button enabled
     */
    public void setOkButtonEnabled(boolean isEnabled) {
        this.isOkButtonEnable = isEnabled;

        if (null != okButton) {
            okButton.setEnabled(isEnabled);
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
