/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.connectiondialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
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
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.utils.DsEncodingEnum;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.prefernces.UserEncodingOption;
import com.huawei.mppdbide.view.utils.UIVerifier;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateRenamDatabaseDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class CreateRenamDatabaseDialog extends Dialog {
    private static final String SQL_ASCII = "SQL_ASCII";
    private Object object;

    /**
     * The input control.
     */
    protected Object inputControl;

    /**
     * The is password required.
     */
    protected Button isPasswordRequired;

    /**
     * The input control combo.
     */
    protected Combo inputControlCombo;
    private Text errorMsg;
    private Button okBtn;

    /**
     * Instantiates a new creates the renam database dialog.
     *
     * @param parent the parent
     * @param serverObject the server object
     */
    public CreateRenamDatabaseDialog(Shell parent, Object serverObject) {
        super(parent);
        this.object = serverObject;
        setDefaultImage(getWindowImage());
    }

    /**
     * Enable disable text.
     *
     * @param value the value
     */
    public void enableDisableText(boolean value) {
        if (inputControl instanceof Text && !((Text) inputControl).isDisposed()) {
            ((Text) inputControl).setEnabled(value);
        }
    }

    /**
     * Checks if is disposed.
     *
     * @return true, if is disposed
     */
    public boolean isDisposed() {
        return (null == this.getShell()) || this.getShell().isDisposed();
    }

    /**
     * Gets the object.
     *
     * @return the object
     */
    protected Object getObject() {
        return this.object;
    }

    /**
     * Configure shell.
     *
     * @param newShellWin the new shell win
     */
    @Override
    protected void configureShell(Shell newShellWin) {
        super.configureShell(newShellWin);
        newShellWin.setText(getWindowTitle());
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite curntComposite = (Composite) super.createDialogArea(parent);
        curntComposite.setLayout(new GridLayout(1, false));

        GridData innerGrid = new GridData(GridData.FILL, GridData.FILL, true, false);
        innerGrid.horizontalIndent = 5;
        innerGrid.verticalIndent = 0;
        innerGrid.minimumWidth = 265;

        curntComposite.setLayoutData(innerGrid);

        String dataStudioEncoding = PreferenceWrapper.getInstance().getPreferenceStore()
                .getString(UserEncodingOption.DATA_STUDIO_ENCODING);

        Label lblText = new Label(curntComposite, SWT.NONE);
        GridData gd = new GridData(SWT.NONE, SWT.NONE, true, true, 1, 1);
        gd.widthHint = 350; // Fixed size to avoid re-sizing window for long
                            // database names.
        lblText.setLayoutData(gd);

        lblText.setText(getHeader());

        inputControl = userInputControl(curntComposite);
        if (!(object instanceof Database)) {
            Label lblText1 = new Label(curntComposite, SWT.NONE);
            lblText1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            lblText1.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_ENCODING));
            inputControlCombo = new Combo(curntComposite, SWT.READ_ONLY);
            DsEncodingEnum[] values = DsEncodingEnum.values();

            for (DsEncodingEnum encoding : values) {
                inputControlCombo.add(encoding.getEncoding());
            }

            inputControlCombo.add(SQL_ASCII);
            inputControlCombo.setText(dataStudioEncoding);
        }
        isPasswordRequired = new Button(curntComposite, SWT.CHECK);
        isPasswordRequired.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CREATE_DB_IS_PSWD_REQD_001");
        isPasswordRequired.setText(MessageConfigLoader.getProperty(IMessagesConstants.USER_INPUT_PSW_DIA_MSG));

        errorMsg = new Text(curntComposite, SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        errorMsg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
        errorMsg.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        errorMsg.setBackground(parent.getBackground());
        errorMsg.setText(' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ');
        errorMsg.setVisible(false);
        return curntComposite;
    }

    /**
     * User input control.
     *
     * @param comp the comp
     * @return the object
     */
    protected Object userInputControl(Composite comp) {
        int txtProp = SWT.BORDER | SWT.SINGLE;

        Text txtInput = new Text(comp, txtProp);
        txtInput.addMenuDetectListener(new DisableMouseRightClick());
        txtInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        txtInput.forceFocus();
        UIVerifier.verifyTextSize(txtInput, 63);
        final ControlDecoration deco = new ControlDecoration(txtInput, SWT.TOP | SWT.LEFT);

        // use an existing image
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());

        // set image
        deco.setImage(image);

        // always show decoration
        deco.setShowOnlyOnFocus(false);

        return txtInput;
    }

    /**
     * User input pswd control.
     *
     * @param comp the comp
     * @return the object
     */
    protected Object userInputPswdControl(Composite comp) {
        int txtProp = SWT.BORDER | SWT.SINGLE | SWT.PASSWORD;

        Text txtInput = new Text(comp, txtProp);
        txtInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        txtInput.setEnabled(false);
        UIVerifier.verifyTextSize(txtInput, 32);
        return txtInput;
    }

    /**
     * Gets the user input.
     *
     * @return the user input
     */
    protected String getUserInput() {
        Text text = (Text) inputControl;
        if (text.isDisposed()) {
            return "";
        }
        return text.getText().trim();
    }

    /**
     * Gets the combo input.
     *
     * @return the combo input
     */
    protected String getComboInput() {
        Combo text = inputControlCombo;
        if (text.isDisposed()) {
            return "";
        }
        return text.getText();
    }

    /**
     * Prints the error message.
     *
     * @param msg the msg
     */
    public void printErrorMessage(String msg) {
        printColourMessageLebel(msg, true);
    }

    /**
     * Prints the message.
     *
     * @param msg the msg
     */
    public void printMessage(String msg) {
        printColourMessageLebel(msg, false);
    }

    private void printColourMessageLebel(String msg, boolean isErrormsg) {
        if (errorMsg.isDisposed()) {
            return;
        }
        if (isErrormsg) {
            errorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        } else {
            errorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        }

        errorMsg.setText(msg);
        errorMsg.setVisible(true);
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
        final String userOKLABEL = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";
        final String userCANCELLABEL = "     "
                + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CLOSE) + "     ";
        okBtn = createButton(parent, IDialogConstants.OK_ID, userOKLABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, userCANCELLABEL, false);

        ((Text) inputControl).addKeyListener(ipCtrlKeyListener());
        okBtn.setEnabled(false);
        setButtonLayoutData(okBtn);

    }

    private KeyListener ipCtrlKeyListener() {
        return new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (((Text) inputControl).getText().isEmpty()) {
                    okBtn.setEnabled(false);
                } else {
                    okBtn.setEnabled(true);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        };
    }

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    protected abstract String getWindowTitle();

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected Image getWindowImage() {
        return IconUtility.getIconImage(IiconPath.ICO_SERVER, this.getClass());
    }

    /**
     * Gets the header.
     *
     * @return the header
     */
    protected abstract String getHeader();

    /**
     * Gets the header pswd.
     *
     * @return the header pswd
     */
    protected abstract String getHeaderPswd();

    /**
     * Checks if is password.
     *
     * @return true, if is password
     */
    protected boolean isPassword() {
        return false;
    }

    /**
     * Perform ok operation.
     */
    protected abstract void performOkOperation();

    /**
     * Enable OK button.
     *
     * @param isEnabled the is enabled
     */
    public void enableOKButton(boolean isEnabled) {
        okBtn.setEnabled(isEnabled);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DisableMouseRightClick.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class DisableMouseRightClick implements MenuDetectListener {
        @Override
        public void menuDetected(MenuDetectEvent event) {
            event.doit = false;
        }
    }
}
