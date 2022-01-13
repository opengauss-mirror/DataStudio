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

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.connectiondialog.NumberValidator;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.MaxSizeHelper;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetMaxsize.
 *
 * @since 3.0.0
 */
public class SetMaxsize {
    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final Tablespace selTablespace = IHandlerUtilities.getSelectedTablespace();

        UserInputMaxSize resizeMaxSizeDialog = new UserInputMaxSize(shell, selTablespace) {

            @Override
            protected void performOkOperation() {
                final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();

                StatusMessage statMssage = new StatusMessage(
                        MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_RESIZE_TABLESPACE));

                String maxSize = getMaxSize();
                SetmaxSizeWorker worker = new SetmaxSizeWorker(selTablespace, maxSize, this, statMssage, bttmStatusBar);

                StatusMessageList.getInstance().push(statMssage);
                if (null != bttmStatusBar) {
                    bttmStatusBar.activateStatusbar();
                }
                worker.schedule();
            }

            @Override
            protected String getWindowTitle() {
                return MessageConfigLoader.getProperty(IMessagesConstants.SET_MAX_SIZE);
            }

            @Override
            public boolean close() {
                return super.close();
            }

            @Override
            protected String getHeader() {
                return MessageConfigLoader.getProperty(IMessagesConstants.RESIZE_TABLESPACE_NEW_NAME,
                        selTablespace.getName());

            }
        };

        resizeMaxSizeDialog.open();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Tablespace selectedTablespace = IHandlerUtilities.getSelectedTablespace();
        if (selectedTablespace == null) {
            return false;
        }

        if (!IHandlerUtilities.getActiveDB(selectedTablespace.getServer())) {
            return false;
        }
        return true;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SetmaxSizeWorker.
     */
    private static final class SetmaxSizeWorker extends UIWorkerJob {
        private Tablespace tablespace;
        private String newsize;
        private UserInputMaxSize dialog;
        private StatusMessage statusMsg;
        private BottomStatusBar statusBar;

        /**
         * Instantiates a new setmax size worker.
         *
         * @param obj the obj
         * @param newsize the newsize
         * @param dialog the dialog
         * @param statusMsg the status msg
         * @param statusBar the status bar
         */
        private SetmaxSizeWorker(Tablespace obj, String newsize, UserInputMaxSize dialog, StatusMessage statusMsg,
                BottomStatusBar statusBar) {
            super("Resize Maxsize", null);
            this.tablespace = obj;
            this.dialog = dialog;
            this.newsize = newsize;
            this.statusMsg = statusMsg;
            this.statusBar = statusBar;
        }

        @Override
        public Object doJob() throws DatabaseOperationException, DatabaseCriticalException {
            tablespace.setTablespaceSize(newsize, tablespace.getServer().getAnotherConnection(tablespace.getOid()));
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                    .getProperty(IMessagesConstants.RESIZE_TABLESPACE_SUCCESS, newsize, this.tablespace.getName())));
            statusBar.hideStatusbar(statusMsg);
            IHandlerUtilities.pritnAndRefresh(tablespace.getServer().getTablespaceGroup());
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            dialog.printMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERR_DURING_RESIZING_TABLESPACE,
                            MPPDBIDEConstants.LINE_SEPARATOR, e.getDBErrorMessage()));
            statusBar.hideStatusbar(statusMsg);
            dialog.enableButtons();
        }

        @Override
        public void finalCleanupUI() {
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (null != bttmStatusBar) {

                bttmStatusBar.hideStatusbar(this.statusMsg);
            }
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            String msg = e.getServerMessage();
            if (null == msg) {
                msg = e.getDBErrorMessage();
            }

            if (msg.contains("Position:")) {
                msg = msg.split("Position:")[0];
            }
            dialog.printMessage(msg);
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_RESIZING_ERROR,
                            tablespace.getServer().getServerConnectionInfo().getConectionName(),
                            tablespace.getName())));
            statusBar.hideStatusbar(statusMsg);
            dialog.enableButtons();
        }

        @Override
        public void finalCleanup() {
            // Nothing to be done.
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class UserInputMaxSize.
     */
    private abstract class UserInputMaxSize extends Dialog {

        private Label setmaxsizeLabel;
        private Label setmaxsizeUnlimitedLabel;
        private Text setmaxsizeInput;
        private Button setmaxsizeUnlimitedBtn;
        private Combo setmaxSizeCombo;
        private Button okButton;
        private Button cancelButton;
        private Label setmaxcomboLblNotice;

        /**
         * Instantiates a new user input max size.
         *
         * @param prnt the prnt
         * @param tablespace the tablespace
         */
        private UserInputMaxSize(Shell prnt, Tablespace tablespace) {
            super(prnt);
            setDefaultImage(getWindowImage());
        }

        /**
         * Gets the window image.
         *
         * @return the window image
         */
        private Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.TABLESPACE, this.getClass());
        }

        @Override
        protected void configureShell(Shell newShel) {
            super.configureShell(newShel);
            newShel.setText(getWindowTitle());
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite curComposite = getCurrentComposite(parent);
            addMaxSizeUnlimitedBtn(curComposite);
            Composite sizeComposite = addSetMaxSizeText(curComposite);

            Composite sizeComboComp = new Composite(sizeComposite, SWT.NONE);

            addMaxSizeCombo(sizeComboComp);
            addMaxSizeLblNotice(curComposite);
            return curComposite;

        }

        /**
         * Adds the max size lbl notice.
         *
         * @param curComposite the cur composite
         */
        private void addMaxSizeLblNotice(Composite curComposite) {
            setmaxcomboLblNotice = new Label(curComposite, SWT.WRAP);
            setmaxcomboLblNotice.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
            setmaxcomboLblNotice.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
            setmaxcomboLblNotice
                    .setText(' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ');
        }

        /**
         * Adds the max size combo.
         *
         * @param sizeComboComp the size combo comp
         */
        private void addMaxSizeCombo(Composite sizeComboComp) {
            setmaxSizeCombo = new Combo(sizeComboComp, SWT.READ_ONLY);
            setmaxSizeCombo.setItems(new String[] {"KB", "MB", "GB", "TB", "PB"});
            GridData maxSizeGD = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
            maxSizeGD.widthHint = 25;
            setmaxSizeCombo.setLayoutData(maxSizeGD);
            setmaxSizeCombo.select(0);
            setmaxSizeCombo.setSize(setmaxSizeCombo.computeSize(15, 15));
        }

        /**
         * Adds the set max size text.
         *
         * @param curComposite the cur composite
         * @return the composite
         */
        private Composite addSetMaxSizeText(Composite curComposite) {
            setmaxsizeLabel = new Label(curComposite, SWT.NONE);
            setmaxsizeLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
            setmaxsizeLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_MAXSIZE));

            Composite sizeComposite = new Composite(curComposite, SWT.NONE);
            GridLayout sizeLayout = new GridLayout(2, false);
            sizeLayout.marginHeight = 0;
            sizeLayout.marginWidth = 0;
            sizeComposite.setLayout(sizeLayout);
            sizeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
            setmaxsizeInput = userInputControlText(sizeComposite);
            setmaxsizeInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            setmaxsizeInput.setData("field", "3");
            setmaxsizeInput.addKeyListener(new SetMaxSizeKeyListener());
            NumberValidator numberValidator = new NumberValidator(setmaxsizeInput);
            setmaxsizeInput.addVerifyListener(numberValidator);
            return sizeComposite;
        }

        /**
         * Adds the max size unlimited btn.
         *
         * @param curComposite the cur composite
         */
        private void addMaxSizeUnlimitedBtn(Composite curComposite) {
            setmaxsizeUnlimitedLabel = new Label(curComposite, SWT.NONE);
            setmaxsizeUnlimitedLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
            setmaxsizeUnlimitedLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.UNLIMITED_SIZE));

            setmaxsizeUnlimitedBtn = new Button(curComposite, SWT.CHECK);
            setmaxsizeUnlimitedBtn.addSelectionListener(new SetMaxSizeUnlimitedBtnSelectionListener());
            setmaxsizeUnlimitedBtn.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false, 1, 2));
        }

        /**
         * Gets the current composite.
         *
         * @param parent the parent
         * @return the current composite
         */
        private Composite getCurrentComposite(Composite parent) {
            Composite curComposite = (Composite) super.createDialogArea(parent);
            curComposite.setLayout(new GridLayout(2, false));

            GridData gridData2 = new GridData();
            gridData2.grabExcessHorizontalSpace = true;
            gridData2.horizontalAlignment = GridData.FILL;
            gridData2.verticalAlignment = GridData.FILL;
            gridData2.horizontalIndent = 5;
            gridData2.verticalIndent = 0;
            gridData2.minimumWidth = 400;
            gridData2.minimumHeight = 400;

            curComposite.setLayoutData(gridData2);
            return curComposite;
        }

        /**
         * The listener interface for receiving setMaxSizeKey events. The class
         * that is interested in processing a setMaxSizeKey event implements
         * this interface, and the object created with that class is registered
         * with a component using the component's
         * <code>addSetMaxSizeKeyListener<code> method. When the setMaxSizeKey
         * event occurs, that object's appropriate method is invoked.
         *
         * SetMaxSizeKeyEvent
         */
        private class SetMaxSizeKeyListener implements KeyListener {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!((Text) setmaxsizeInput).getText().isEmpty()) {
                    okButton.setEnabled(true);
                } else {
                    okButton.setEnabled(false);
                }

            }

            @Override
            public void keyPressed(KeyEvent e) {
                String eChar = e.character + "";
                try {
                    // Validates the input is long value only.
                    if (e.keyCode != 8 && e.keyCode != 127 && e.keyCode != 16777219 && e.keyCode != 16777220
                            && e.character != '.' && Long.parseLong(eChar) < 0) {
                        e.doit = false;
                    }
                } catch (final NumberFormatException numberFormatException) {
                    e.doit = false;
                }

            }
        }

        /**
         * The listener interface for receiving setMaxSizeUnlimitedBtnSelection
         * events. The class that is interested in processing a
         * setMaxSizeUnlimitedBtnSelection event implements this interface, and
         * the object created with that class is registered with a component
         * using the component's
         * <code>addSetMaxSizeUnlimitedBtnSelectionListener<code> method. When
         * the setMaxSizeUnlimitedBtnSelection event occurs, that object's
         * appropriate method is invoked.
         *
         * SetMaxSizeUnlimitedBtnSelectionEvent
         */
        private class SetMaxSizeUnlimitedBtnSelectionListener implements SelectionListener {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setmaxsizeInput.setEnabled(!setmaxsizeUnlimitedBtn.getSelection());
                setmaxSizeCombo.setEnabled(!setmaxsizeUnlimitedBtn.getSelection());
                if (setmaxsizeUnlimitedBtn.getSelection()) {
                    okButton.setEnabled(true);
                } else {
                    okButton.setEnabled(false);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        }

        /**
         * Gets the max size.
         *
         * @return the max size
         */
        protected String getMaxSize() {

            if (!setmaxsizeUnlimitedBtn.isDisposed() && setmaxsizeUnlimitedBtn.getSelection()) {
                return "UNLIMITED";
            }
            Text text = (Text) setmaxsizeInput;
            if (text.isDisposed()) {
                return "";
            }
            String getSize = null;
            String mexSizeForServer = null;
            String sizeText = text.getText();
            if (!sizeText.isEmpty()) {
                mexSizeForServer = MaxSizeHelper.convertMaxSizeIntoServerFormate(setmaxSizeCombo.getText());
                getSize = sizeText.concat(mexSizeForServer).trim();
            } else {
                getSize = sizeText.trim();
            }

            return getSize;

        }

        /**
         * Gets the window title.
         *
         * @return the window title
         */
        protected abstract String getWindowTitle();

        /**
         * Perform ok operation.
         */
        protected abstract void performOkOperation();

        /**
         * User input control text.
         *
         * @param comp the comp
         * @return the text
         */
        protected Text userInputControlText(Composite comp) {
            int txtProp = SWT.BORDER | SWT.SINGLE;
            Text txtInput = new Text(comp, txtProp);
            txtInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
            txtInput.forceFocus();
            return txtInput;
        }

        @Override
        protected void okPressed() {
            performOkOperation();
        }

        /**
         * Prints the message.
         *
         * @param msg the msg
         */
        public void printMessage(String msg) {
            setmaxcomboLblNotice.setText(msg);
            setmaxcomboLblNotice.redraw();
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
         * Gets the header.
         *
         * @return the header
         */
        protected abstract String getHeader();

        @Override
        protected void createButtonsForButtonBar(Composite parentObj) {
            final String okLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";
            final String cancelLabel = "     "
                    + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC) + "     ";
            okButton = createButton(parentObj, UIConstants.OK_ID, okLabel, true);
            okButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_OK_001");
            cancelButton = createButton(parentObj, CANCEL, cancelLabel, false);
            cancelButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_CANCEL_001");
            okButton.setEnabled(false);
        }

    }
}
