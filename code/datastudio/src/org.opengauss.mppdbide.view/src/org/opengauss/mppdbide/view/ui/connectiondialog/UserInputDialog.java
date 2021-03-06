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
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.ui.table.IDialogWorkerInteraction;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.UIVerifier;
import org.opengauss.mppdbide.view.utils.UserPreference;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserInputDialog.
 *
 * @since 3.0.0
 */
public abstract class UserInputDialog extends Dialog implements IDialogWorkerInteraction {

    private Object object;

    /**
     * The input control.
     */
    protected Object inputControl;
    private Label lebelNotice;
    private Button okButton;
    private Button cancelButton;
    private boolean passwordprompt;
    private Combo savePswdOptions;
    private String noticeLabelMsg;

    // Dialog Buttons

    /**
     * Instantiates a new user input dialog.
     *
     * @param parent the parent
     * @param serverObject the server object
     */
    public UserInputDialog(Shell parent, Object serverObject) {
        super(parent);
        this.object = serverObject;
        setDefaultImage(getWindowImage());
    }

    /**
     * Instantiates a new user input dialog.
     *
     * @param parent the parent
     * @param serverObject the server object
     * @param noticelabelMsg the noticelabel msg
     */
    public UserInputDialog(Shell parent, Object serverObject, String noticelabelMsg) {
        this(parent, serverObject);
        this.noticeLabelMsg = noticelabelMsg;
    }

    /**
     * For table description.
     *
     * @return true, if successful
     */
    protected boolean forTableDescription() {
        return false;
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
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        final String okLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";
        final String cancelLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        okButton = createButton(parent, UIConstants.OK_ID, okLabel, true);
        okButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_OK_001");
        cancelButton = createButton(parent, CANCEL, cancelLabel, false);
        cancelButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_CANCEL_001");
        if (!(inputControl instanceof Spinner) && !(inputControl instanceof Combo)) {
            if (isConnectDB()) {
                ((Text) inputControl).addKeyListener(new InputControlKeyListener());
            } else {
                ((StyledText) inputControl).addKeyListener(new StyledTextKeyListener());
            }
            okButton.setEnabled(false);
        } else if (inputControl instanceof Combo) {
            okButton.setEnabled(false);
            ((Combo) inputControl).addSelectionListener(new ComboSelectionListener());
        }

        setButtonLayoutData(okButton);
    }

    /**
     * The listener interface for receiving comboSelection events. The class
     * that is interested in processing a comboSelection event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addComboSelectionListener<code>
     * method. When the comboSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * ComboSelectionEvent
     */
    private class ComboSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            if (((Combo) inputControl).getText().isEmpty()) {
                okButton.setEnabled(false);
            } else {
                okButton.setEnabled(true);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }
    }

    /**
     * The listener interface for receiving styledTextKey events. The class that
     * is interested in processing a styledTextKey event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addStyledTextKeyListener<code>
     * method. When the styledTextKey event occurs, that object's appropriate
     * method is invoked.
     *
     * StyledTextKeyEvent
     */
    private class StyledTextKeyListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent e) {
            if (isSetTableDescription()) {
                enableDisableButtonForSetDescription();
            } else {
                if (((StyledText) inputControl).getText().isEmpty()) {
                    okButton.setEnabled(false);
                } else {
                    okButton.setEnabled(true);
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

    }

    /**
     * The listener interface for receiving inputControlKey events. The class
     * that is interested in processing a inputControlKey event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addInputControlKeyListener<code>
     * method. When the inputControlKey event occurs, that object's appropriate
     * method is invoked.
     *
     * InputControlKeyEvent
     */
    private class InputControlKeyListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent e) {
            if (((Text) inputControl).getText().isEmpty()) {
                okButton.setEnabled(false);
            } else {
                okButton.setEnabled(true);
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }
    }

    private void enableDisableButtonForSetDescription() {
        if (((StyledText) inputControl).getText().equals(getInitialText())) {
            TableMetaData table = (TableMetaData) getObject();
            printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLE_NEW_DESC,
                    table.getNamespace().getName(), table.getName()), false);
            okButton.setEnabled(false);
        } else {
            printErrorMessage("", false);
            okButton.setEnabled(true);
        }
    }

    /**
     * Gets the blank dialog area.
     *
     * @param parent the parent
     * @return the blank dialog area
     */
    protected Control getBlankDialogArea(Composite parent) {
        return (Composite) super.createDialogArea(parent);
    }

    /**
     * Configure shell.
     *
     * @param newShellWindow the new shell window
     */
    @Override
    protected void configureShell(Shell newShellWindow) {
        super.configureShell(newShellWindow);
        newShellWindow.setText(getWindowTitle());
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite curComposite = (Composite) super.createDialogArea(parent);
        curComposite.setLayout(new GridLayout(1, false));

        GridData grid2 = new GridData();
        grid2.grabExcessHorizontalSpace = true;
        grid2.horizontalAlignment = GridData.FILL;
        grid2.verticalAlignment = GridData.FILL;
        grid2.horizontalIndent = 5;
        grid2.verticalIndent = 0;
        grid2.minimumWidth = 400;

        curComposite.setLayoutData(grid2);

        Label lblTextVal = new Label(curComposite, SWT.NONE);
        GridData gd = new GridData(SWT.NONE, SWT.NONE, true, true, 1, 1);
        gd.widthHint = 450; // Fixed size to avoid re-sizing window for long
                            // database names.
        lblTextVal.setLayoutData(gd);
        lblTextVal.setText(getHeader());
        if (isConnectDB()) {
            inputControl = userInputControlText(curComposite);
        } else {
            inputControl = userInputControl(curComposite);
        }
        if (passwordprompt) {
            Label lblSaveOption = new Label(curComposite, SWT.NONE);
            lblSaveOption.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            lblSaveOption.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONN_DIALOG_SAVE_CIPHER));

            savePswdOptions = new Combo(curComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
            String[] saveOptions = null;

            if (UserPreference.getInstance().getEnablePermanentPasswordSaveOption()) {
                saveOptions = new String[] {
                    MessageConfigLoader.getProperty(SavePrdOptions.PERMANENTLY.toString() + "_SAVE"),
                    MessageConfigLoader.getProperty(SavePrdOptions.CURRENT_SESSION_ONLY.toString()),
                    MessageConfigLoader.getProperty(SavePrdOptions.DO_NOT_SAVE.toString())};
            } else {
                saveOptions = new String[] {
                    MessageConfigLoader.getProperty(SavePrdOptions.CURRENT_SESSION_ONLY.toString()),
                    MessageConfigLoader.getProperty(SavePrdOptions.DO_NOT_SAVE.toString())};
            }
            savePswdOptions.setItems(saveOptions);
            savePswdOptions.select(getComboSelectionIndex(SavePrdOptions.DO_NOT_SAVE));
        }
        createLebelNoticeLabel(curComposite);

        return curComposite;
    }

    private void createLebelNoticeLabel(Composite curComposite) {
        lebelNotice = new Label(curComposite, SWT.WRAP);
        lebelNotice.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
        lebelNotice.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        if (this.noticeLabelMsg == null) {
            lebelNotice.setText(' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ');
        } else {
            lebelNotice.setText(this.noticeLabelMsg);
        }
    }

    /**
     * Gets the combo selection index.
     *
     * @param option the option
     * @return the combo selection index
     */
    protected int getComboSelectionIndex(SavePrdOptions option) {
        if (UserPreference.getInstance().getEnablePermanentPasswordSaveOption()) {
            return option.ordinal();
        } else {
            return option.ordinal() - 1;
        }
    }

    /**
     * User input control.
     *
     * @param comp the comp
     * @return the object
     */
    protected Object userInputControl(Composite comp) {
        int txtProp = SWT.BORDER | SWT.SINGLE;
        if (isPassword()) {
            txtProp |= SWT.PASSWORD;
        }

        StyledText txtInput = new StyledText(comp, txtProp);
        if (isPassword()) {
            UIVerifier.verifyStyledTextSize(txtInput);
        }
        txtInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        txtInput.forceFocus();
        txtInput.setText(getInitialText());
        final ControlDecoration deco = new ControlDecoration(txtInput, SWT.TOP | SWT.LEFT);

        // use an existing image
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());

        // set image
        deco.setImage(image);

        // always show decoration
        deco.setShowOnlyOnFocus(false);
        if (!forTableDescription()) {
            txtInput.addVerifyListener(new InputControlListener());
        }

        return txtInput;
    }

    /**
     * The listener interface for receiving inputControl events. The class that
     * is interested in processing a inputControl event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addInputControlListener<code>
     * method. When the inputControl event occurs, that object's appropriate
     * method is invoked.
     *
     * InputControlEvent
     */
    private static class InputControlListener implements VerifyListener {
        @Override
        public void verifyText(VerifyEvent event) {
            String text = ((StyledText) event.widget).getText() + event.text;
            try {
                if (text.length() > 63) {
                    event.doit = false;
                }
            } catch (NumberFormatException e) {
                event.doit = false;
            }
        }
    }

    /**
     * User input control text.
     *
     * @param comp the comp
     * @return the object
     */
    protected Object userInputControlText(Composite comp) {
        int txtProp = SWT.BORDER | SWT.SINGLE;
        if (isPassword()) {
            txtProp |= SWT.PASSWORD;
        }

        Text txtInput = new Text(comp, txtProp);
        if (isPassword()) {
            UIVerifier.verifyTextSize(txtInput, 32);
        }
        txtInput.addMenuDetectListener(new DisableMouseRightClick());

        txtInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        txtInput.forceFocus();

        final ControlDecoration deco = new ControlDecoration(txtInput, SWT.TOP | SWT.LEFT);

        // use an existing image
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());

        // always show decoration
        deco.setShowOnlyOnFocus(false);

        // set image
        deco.setImage(image);

        txtInput.addVerifyListener(new InputTextListener());

        return txtInput;
    }

    /**
     * The listener interface for receiving inputText events. The class that is
     * interested in processing a inputText event implements this interface, and
     * the object created with that class is registered with a component using
     * the component's <code>addInputTextListener<code> method. When the
     * inputText event occurs, that object's appropriate method is invoked.
     *
     * InputTextEvent
     */
    private static class InputTextListener implements VerifyListener {
        @Override
        public void verifyText(VerifyEvent event) {
            String text = ((Text) event.widget).getText() + event.text;
            try {
                if (text.length() > 63) {
                    event.doit = false;
                }
            } catch (NumberFormatException e) {
                event.doit = false;
            }
        }
    }

    /**
     * Gets the user input.
     *
     * @return the user input
     */
    protected String getUserInput() {
        if (isConnectDB()) {
            Text text = (Text) inputControl;
            if (text.isDisposed()) {
                return "";
            }
            return text.getText().trim();
        } else {
            StyledText text = (StyledText) inputControl;
            if (text.isDisposed()) {
                return "";
            }
            return text.getText().trim();
        }
    }

    /**
     * Gets the max size.
     *
     * @return the max size
     */
    protected String getMaxSize() {

        StyledText text = (StyledText) inputControl;
        String maxSize = "";
        String str = text.getText();
        String regex = "[0-9]+|[0-9]+(?i)[kmgtp]$";
        if (text.isDisposed()) {
            return maxSize;
        }
        if ("unlimited".equalsIgnoreCase(text.getText())) {
            maxSize = text.getText();
        } else if (text.getText().startsWith("-")) {
            printErrorMessage("Max Size can not have negative value", false);
        } else if (!str.matches(regex)) {
            printErrorMessage("Size value need to be with either number or K/M/G/T/P ", false);
        } else {
            maxSize = text.getText();
        }
        return maxSize;
    }

    /**
     * Prints the error message.
     *
     * @param msg the msg
     * @param isInProgressMsg the is in progress msg
     */
    public void printErrorMessage(String msg, boolean isInProgressMsg) {
        printColourMessageLebel(msg, isInProgressMsg, true);
    }

    /**
     * Prints the message.
     *
     * @param msg the msg
     * @param isInProgressMsg the is in progress msg
     */
    public void printMessage(String msg, boolean isInProgressMsg) {
        printColourMessageLebel(msg, isInProgressMsg, false);
    }

    private void printColourMessageLebel(String msg, boolean isInProgressMsg, boolean isErrormsg) {
        if (lebelNotice.isDisposed()) {
            return;
        }
        if (isErrormsg) {
            lebelNotice.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        } else {
            lebelNotice.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        }

        lebelNotice.setText(msg);
        lebelNotice.redraw();
        if (!isInProgressMsg) {
            okButton.setEnabled(true);
            cancelButton.setEnabled(true);
        }
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        okButton.setEnabled(false);
        cancelButton.setEnabled(false);
        performOkOperation();
    }

    /**
     * Cancel pressed.
     */
    @Override
    protected void cancelPressed() {
        performCancelOperation();
        super.cancelPressed();
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
     * Enable cancel button.
     */
    public void enableCancelButton() {
        if (cancelButton.isDisposed()) {
            return;
        }

        cancelButton.setEnabled(true);
    }

    /**
     * Disable buttons.
     */
    public void disableButtons() {
        if (okButton.isDisposed() || cancelButton.isDisposed()) {
            return;
        }
        okButton.setEnabled(false);
        cancelButton.setEnabled(true);
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
        return IconUtility.getIconImage(IiconPath.ICO_TOOL_128X128, this.getClass());
    }

    /**
     * Gets the header.
     *
     * @return the header
     */
    protected abstract String getHeader();

    /**
     * Checks if is connect DB.
     *
     * @return true, if is connect DB
     */
    protected boolean isConnectDB() {
        return false;
    }

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
     * Perform cancel operation.
     */
    protected void performCancelOperation() {

    }

    /**
     * Sets the server connection info.
     *
     * @return the char[]
     */
    public char[] setServerConnectionInfo() {
        return getUserInput().toCharArray();
    }

    /**
     * Gets the save pswd option.
     *
     * @return the save pswd option
     */
    public int getSavePswdOption() {
        return savePswdOptions.getSelectionIndex();
    }

    /**
     * Sets the passwordprompt.
     *
     * @param passwordprompt the new passwordprompt
     */
    public void setPasswordprompt(boolean passwordprompt) {
        this.passwordprompt = passwordprompt;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DisableMouseRightClick.
     */
    private static class DisableMouseRightClick implements MenuDetectListener {
        @Override
        public void menuDetected(MenuDetectEvent menuDetectEvent) {
            menuDetectEvent.doit = false;
        }
    }

    /**
     * Gets the initial text.
     *
     * @return the initial text
     */
    protected String getInitialText() {
        return "";
    }

    /**
     * Checks if is sets the table description.
     *
     * @return true, if is sets the table description
     */
    protected boolean isSetTableDescription() {
        return false;
    }
    /**
     * Added for findbugs Static inner class creation check
     */
}
