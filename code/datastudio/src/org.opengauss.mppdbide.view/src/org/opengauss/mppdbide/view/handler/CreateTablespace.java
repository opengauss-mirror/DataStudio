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

package org.opengauss.mppdbide.view.handler;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.TablespaceProperties;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.ui.connectiondialog.NumberValidator;
import org.opengauss.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import org.opengauss.mppdbide.view.ui.connectiondialog.TablespacePageCostValidator;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.MaxSizeHelper;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateTablespace.
 *
 * @since 3.0.0
 */
public class CreateTablespace {
    private StatusMessage statusMessage;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(Shell shell) {
        UserInputForTablespace tablespaceDialog = new UserInputForTablespace(shell);
        tablespaceDialog.open();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Server server = IHandlerUtilities.getSelectedTableSpaceGroup();
        if (server == null) {
            return false;
        }
        if (!IHandlerUtilities.getActiveDB(server)) {
            return false;
        }
        return true;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CreateTablespaceWorker.
     */
    private static final class CreateTablespaceWorker extends UIWorkerJob {
        private String name;
        private Server connectedServer;
        private String query;
        private UserInputForTablespace dialog;
        private StatusMessage statusMsg;
        private BottomStatusBar bttmStatusBar;
        private TerminalExecutionConnectionInfra conn;
        private Database db;

        /**
         * Instantiates a new creates the tablespace worker.
         *
         * @param name the name
         * @param server the server
         * @param query the query
         * @param dialog the dialog
         * @param message the message
         * @param bttmStatusBar the bttm status bar
         * @param db the db
         */
        private CreateTablespaceWorker(String name, Server server, String query, UserInputForTablespace dialog,
                StatusMessage message, BottomStatusBar bttmStatusBar, Database db) {
            super("Create Tablespace", null);
            this.name = name;
            this.connectedServer = server;
            this.query = query;
            this.dialog = dialog;
            this.statusMsg = message;
            this.bttmStatusBar = bttmStatusBar;
            this.db = db;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            conn = PromptPrdGetConnection.getConnection(db);
            connectedServer.createTablespace(query, conn.getConnection());
            connectedServer.refreshTablespace();
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();

            bttmStatusBar.hideStatusbar(statusMsg);
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_SUCCESSFULLY,
                            connectedServer.getServerConnectionInfo().getConectionName(), name)));
            IHandlerUtilities.pritnAndRefresh(connectedServer.getTablespaceGroup());
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_CREATION_FAILED),
                    MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERROR_DURING_TABLESPACE_CREATION,
                            MPPDBIDEConstants.LINE_SEPARATOR, e.getServerMessage()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_CREATION_ERROR,
                            connectedServer.getServerConnectionInfo().getConectionName(), name)));
            bttmStatusBar.hideStatusbar(statusMsg);
            dialog.printMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERROR_DURING_TABLESPACE_CREATION,
                            MPPDBIDEConstants.LINE_SEPARATOR, e.getServerMessage()));
            dialog.enableButtons();
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            String msg = e.getServerMessage();
            if (null == msg) {
                msg = e.getDBErrorMessage();
            }
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_CREATION_FAILED),
                    MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERROR_DURING_TABLESPACE_CREATION,
                            MPPDBIDEConstants.LINE_SEPARATOR, e.getServerMessage()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_CREATION_ERROR,
                            connectedServer.getServerConnectionInfo().getConectionName(), name)));
            dialog.printMessage(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_WHILE_CREATING_TABLESPACE,
                    MPPDBIDEConstants.LINE_SEPARATOR, msg));
            bttmStatusBar.hideStatusbar(statusMsg);
            dialog.enableButtons();

        }

        @Override
        public void finalCleanup() {
            if (this.conn != null) {
                this.conn.releaseConnection();
            }
        }

        @Override
        public void finalCleanupUI() {
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(statusMsg);
            }

        }

    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMessage = statMessage;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class UserInputForTablespace.
     */
    private final class UserInputForTablespace extends Dialog {
        private Label nameLabel;
        private Label locationlabel;
        private Label sizeLabel;
        private Label sizeUnlimitedLabel;
        private Label seqPageCost;
        private Label randomPageCost;
        private Text nameInput;
        private Text locationInput;
        private Text sizeInput;
        private Button sizeUnlimitedBtn;
        private Combo maxSizeCombo;
        private Text seqPageCostInput;
        private Text randomPageCostInput;
        private Label errorLabel;
        private Button okButton;
        private Button cancelButton;
        private Button runInBackGround;
        private Button relativeBtn;
        private Server server;
        private CreateTablespaceWorker worker;

        /**
         * Instantiates a new user input for tablespace.
         *
         * @param prnt the prnt
         */
        private UserInputForTablespace(Shell prnt) {
            super(prnt);
            setDefaultImage(getWindowImage());
            server = IHandlerUtilities.getSelectedTableSpaceGroup();
        }

        /**
         * Gets the window image.
         *
         * @return the window image
         */
        private Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_TABLESPACE, getClass());
        }

        @Override
        protected void configureShell(Shell newShel) {
            super.configureShell(newShel);
            newShel.setText(getWindowTitle());
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite curComposite = (Composite) super.createDialogArea(parent);
            curComposite.setLayout(new GridLayout(2, false));

            GridData gridData2 = new GridData();
            gridData2.grabExcessHorizontalSpace = true;
            gridData2.horizontalAlignment = GridData.FILL;
            gridData2.verticalAlignment = GridData.FILL;
            gridData2.horizontalIndent = 5;
            gridData2.verticalIndent = 0;
            gridData2.minimumWidth = 400;

            curComposite.setLayoutData(gridData2);
            addUiForTablespaceName(curComposite);

            addUiForTablespaceRelativePath(curComposite);

            addUiForTablespaceLocation(curComposite);

            addUiForUnlimitedSize(curComposite);

            addUiForTablespaceMaxSize(curComposite);

            addUiForsegAndRandomPageCost(curComposite);

            addUiForErrorMsg(curComposite);

            ((Text) sizeInput).addKeyListener(new SizeInputKeyListener());

            return curComposite;

        }

        /**
         * Adds the ui for error msg.
         *
         * @param curComposite the cur composite
         */
        private void addUiForErrorMsg(Composite curComposite) {
            errorLabel = new Label(curComposite, SWT.WRAP);
            errorLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            errorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            errorLabel.setText(' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ');
        }

        /**
         * Adds the ui forseg and random page cost.
         *
         * @param curComposite the cur composite
         */
        private void addUiForsegAndRandomPageCost(Composite curComposite) {
            seqPageCost = new Label(curComposite, SWT.NONE);
            seqPageCost.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            seqPageCost.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_SEQCOST));
            seqPageCostInput = userInputControlText(curComposite);
            seqPageCostInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            seqPageCostInput.setData("field", "8");

            seqPageCostInput.addKeyListener(new RandomPageCostIpKeyListener());
            setInitTextProperties(seqPageCostInput);
            seqPageCostInput.addVerifyListener(new TablespacePageCostValidator(seqPageCostInput));

            randomPageCost = new Label(curComposite, SWT.NONE);
            randomPageCost.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            randomPageCost.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_RANCOST));
            randomPageCostInput = userInputControlText(curComposite);
            randomPageCostInput.setData("field", "9");
            randomPageCostInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            randomPageCostInput.addKeyListener(new RandomPageCostIpKeyListener());
            setInitTextProperties(randomPageCostInput);
            randomPageCostInput.addVerifyListener(new TablespacePageCostValidator(randomPageCostInput));
        }

        /**
         * Adds the ui for tablespace max size.
         *
         * @param curComposite the cur composite
         */
        private void addUiForTablespaceMaxSize(Composite curComposite) {
            sizeLabel = new Label(curComposite, SWT.NONE);
            sizeLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            sizeLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_MAXSIZE));

            Composite sizeComposite = new Composite(curComposite, SWT.NONE);
            GridLayout sizeLayout = new GridLayout(2, false);
            sizeLayout.marginHeight = 0;
            sizeLayout.marginWidth = 0;
            sizeComposite.setLayout(sizeLayout);
            sizeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            sizeInput = userInputControlText(sizeComposite);
            sizeInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            sizeInput.setData("field", "3");
            sizeInput.addKeyListener(new SizeInputKeyListener2());
            NumberValidator numberValidator = new NumberValidator(sizeInput);
            sizeInput.addVerifyListener(numberValidator);
            setInitTextProperties(sizeInput);

            Composite sizeComboComp = new Composite(sizeComposite, SWT.NONE);
            sizeComboComp.setLayout(new GridLayout(1, false));

            maxSizeCombo = new Combo(sizeComboComp, SWT.READ_ONLY);
            maxSizeCombo.setItems(new String[] {"KB", "MB", "GB", "TB", "PB"});
            GridData maxSizeGD = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
            maxSizeCombo.setLayoutData(maxSizeGD);
            maxSizeCombo.select(0);
        }

        /**
         * Adds the ui for unlimited size.
         *
         * @param curComposite the cur composite
         */
        private void addUiForUnlimitedSize(Composite curComposite) {
            sizeUnlimitedLabel = new Label(curComposite, SWT.NONE);
            sizeUnlimitedLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            sizeUnlimitedLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.UNLIMITED_SIZE));

            sizeUnlimitedBtn = new Button(curComposite, SWT.CHECK);
            sizeUnlimitedBtn.addSelectionListener(new SizeUnlimitedSelectionListener());
            sizeUnlimitedBtn.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false, 1, 1));
        }

        /**
         * Adds the ui for tablespace location.
         *
         * @param curComposite the cur composite
         */
        private void addUiForTablespaceLocation(Composite curComposite) {
            locationlabel = new Label(curComposite, SWT.NONE);
            locationlabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            locationlabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_LOCATION));
            locationInput = new Text(curComposite, SWT.BORDER | SWT.SINGLE);
            setInitTextProperties(locationInput);
            locationInput.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CONNECTION_HOST_001");
        }

        /**
         * Adds the ui for tablespace relative path.
         *
         * @param curComposite the cur composite
         */
        private void addUiForTablespaceRelativePath(Composite curComposite) {
            Label relativeLabel = new Label(curComposite, SWT.None);
            relativeLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            relativeLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_RELATIVE_PATH));

            relativeBtn = new Button(curComposite, SWT.CHECK);
        }

        /**
         * Adds the ui for tablespace name.
         *
         * @param curComposite the cur composite
         */
        private void addUiForTablespaceName(Composite curComposite) {
            nameLabel = new Label(curComposite, SWT.NONE);
            nameLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            nameLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_NAME));
            nameInput = new Text(curComposite, SWT.BORDER | SWT.SINGLE);
            setInitTextProperties(nameInput);
            nameInput.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CONNECTION_CONNECTIONNAME_001");
            nameInput.addVerifyListener(new NameIpVerifyListener());
        }

        /**
         * The listener interface for receiving nameIpVerify events. The class
         * that is interested in processing a nameIpVerify event implements this
         * interface, and the object created with that class is registered with
         * a component using the component's <code>addNameIpVerifyListener<code>
         * method. When the nameIpVerify event occurs, that object's appropriate
         * method is invoked.
         *
         * NameIpVerifyEvent
         */
        private class NameIpVerifyListener implements VerifyListener {

            @Override
            public void verifyText(VerifyEvent e) {

                Text text = (Text) e.getSource();

                final String oldTablespaceName = text.getText();
                String newTablespaceName = oldTablespaceName.substring(0, e.start) + e.text
                        + oldTablespaceName.substring(e.end);
                if (newTablespaceName.length() > 63) {
                    e.doit = false;
                }

            }

        }

        /**
         * The listener interface for receiving sizeUnlimitedSelection events.
         * The class that is interested in processing a sizeUnlimitedSelection
         * event implements this interface, and the object created with that
         * class is registered with a component using the component's
         * <code>addSizeUnlimitedSelectionListener<code> method. When the
         * sizeUnlimitedSelection event occurs, that object's appropriate method
         * is invoked.
         *
         * SizeUnlimitedSelectionEvent
         */
        private class SizeUnlimitedSelectionListener implements SelectionListener {

            @Override
            public void widgetSelected(SelectionEvent e) {
                sizeInput.setEnabled(!sizeUnlimitedBtn.getSelection());
                maxSizeCombo.setEnabled(!sizeUnlimitedBtn.getSelection());
                validateData();

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Auto-generated method stub

            }

        }

        /**
         * The listener interface for receiving randomPageCostIpKey events. The
         * class that is interested in processing a randomPageCostIpKey event
         * implements this interface, and the object created with that class is
         * registered with a component using the component's
         * <code>addRandomPageCostIpKeyListener<code> method. When the
         * randomPageCostIpKey event occurs, that object's appropriate method is
         * invoked.
         *
         * RandomPageCostIpKeyEvent
         */
        private class RandomPageCostIpKeyListener implements KeyListener {

            @Override
            public void keyPressed(KeyEvent e) {
                validateInputData(e);

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        }

        /**
         * The listener interface for receiving sizeInputKey events. The class
         * that is interested in processing a sizeInputKey event implements this
         * interface, and the object created with that class is registered with
         * a component using the component's <code>addSizeInputKeyListener<code>
         * method. When the sizeInputKey event occurs, that object's appropriate
         * method is invoked.
         *
         * SizeInputKeyEvent
         */
        private class SizeInputKeyListener implements KeyListener {

            @Override
            public void keyPressed(KeyEvent e) {

                if (!validate((Text) sizeInput)) {
                    validateData();
                } else {
                    validateData();
                    printMessage("");
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        }

        /**
         * 
         * Title: class
         * 
         * Description: The Class SizeInputKeyListener2.
         */
        private class SizeInputKeyListener2 implements KeyListener {

            @Override
            public void keyReleased(KeyEvent e) {
                validateData();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                validateInputData(e);
                validateData();
            }

        }

        /**
         * Validate input data.
         *
         * @param e the e
         */
        private void validateInputData(KeyEvent e) {
            String eChar = e.character + "";
            try {
                // Validates the input is long value only.

                if (((e.stateMask & SWT.CTRL) == SWT.CTRL)
                        && (e.keyCode == 'v' || e.keyCode == 'c' || e.keyCode == 'x' || e.keyCode == 'a')) {
                    e.doit = true;
                } else if (e.keyCode != 8 && e.keyCode != 127 && e.keyCode != 16777219 && e.keyCode != 16777220
                        && e.character != '.' && Long.parseLong(eChar) < 0) {
                    e.doit = false;
                }
            } catch (final NumberFormatException numberFormatException) {
                e.doit = false;
            }
        }

        /**
         * Close run inm back ground.
         */
        private void closeRunInmBackGround() {
            close();
        }

        /**
         * Prints the message.
         *
         * @param msg the msg
         */
        public void printMessage(String msg) {
            if (errorLabel.isDisposed()) {
                return;
            }
            errorLabel.setText(msg);
        }

        /**
         * Enable buttons.
         */
        public void enableButtons() {
            if (!okButton.isDisposed()) {
                okButton.setEnabled(true);
            }

            if (!cancelButton.isDisposed()) {
                cancelButton.setEnabled(true);
            }

            if (!runInBackGround.isDisposed()) {
                runInBackGround.setEnabled(false);
            }
        }

        /**
         * Perform ok operation.
         */
        protected void performOkOperation() {
            worker = null;
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            Database db;
            try {
                db = server.findOneActiveDb();
            } catch (MPPDBIDEException e1) {
                enableButtons(bttmStatusBar);
                return;
            }
            StatusMessage statMssage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CREATE_TABLESPACE));
            TablespaceProperties properties = new TablespaceProperties(getNameInputtext(), getLocationInputtext(),
                    getSizeInputtext(), getFileSystemInputtext(), getSeqPageCostInputtext(),
                    getRandomPageCostInputtext(), relativeBtn.getSelection());

            properties.setServer(server);
            String qry = properties.buildQuery();

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.CREATING_TABLESPACE));
            worker = new CreateTablespaceWorker(properties.getName(), server, qry, this, statMssage, bttmStatusBar, db);

            setStatusMessage(statMssage);
            StatusMessageList.getInstance().push(statMssage);
            if (bttmStatusBar != null) {
                bttmStatusBar.activateStatusbar();
            }
            worker.schedule();
            okButton.setEnabled(false);
            cancelButton.setEnabled(true);
            runInBackGround.setEnabled(true);
        }

        /**
         * Enable buttons.
         *
         * @param bttmStatusBar the bttm status bar
         */
        private void enableButtons(final BottomStatusBar bttmStatusBar) {
            okButton.setEnabled(true);
            cancelButton.setEnabled(true);
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(getStatusMessage());
            }
        }

        @Override
        protected void cancelPressed() {
            performCancelOperation();
        }

        /**
         * Perform cancel operation.
         */
        protected void performCancelOperation() {
            if (worker != null && worker.getState() == Job.RUNNING) {
                int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG));

                if (0 == returnValue) {
                    worker.cancelJob();
                    worker = null;
                } else {
                    cancelButton.setEnabled(true);
                }
            } else {
                close();
            }

        }

        /**
         * Gets the window title.
         *
         * @return the window title
         */
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLESPACE_DIA_TITILE);
        }

        /**
         * User input control text.
         *
         * @param comp the comp
         * @return the text
         */
        protected Text userInputControlText(Composite comp) {
            int txtProp = SWT.BORDER | SWT.SINGLE;
            Text txtInput = new Text(comp, txtProp);
            txtInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

            return txtInput;
        }

        /**
         * Gets the name inputtext.
         *
         * @return the name inputtext
         */
        protected String getNameInputtext() {

            Text text = (Text) nameInput;
            if (text.isDisposed()) {
                return "";
            }
            return text.getText().trim();

        }

        /**
         * Gets the location inputtext.
         *
         * @return the location inputtext
         */
        protected String getLocationInputtext() {

            Text text = (Text) locationInput;
            if (text.isDisposed()) {
                return "";
            }
            return text.getText().trim();

        }

        /**
         * Gets the size inputtext.
         *
         * @return the size inputtext
         */
        protected String getSizeInputtext() {

            if (!sizeUnlimitedBtn.isDisposed() && sizeUnlimitedBtn.getSelection()) {
                return "UNLIMITED";
            }
            Text text = (Text) sizeInput;
            if (text.isDisposed()) {
                return "";
            }
            String maxSize = null;
            String mexSizeForServer = null;
            String sizeStr = text.getText();
            if (!sizeStr.isEmpty()) {
                mexSizeForServer = MaxSizeHelper.convertMaxSizeIntoServerFormate(maxSizeCombo.getText());
                maxSize = sizeStr.concat(mexSizeForServer).trim();
            } else {
                maxSize = sizeStr.trim();
            }

            return maxSize;
        }

        /**
         * Gets the file system inputtext.
         *
         * @return the file system inputtext
         */
        protected String getFileSystemInputtext() {
            return MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_GENERAL);
        }

        /**
         * Gets the seq page cost inputtext.
         *
         * @return the seq page cost inputtext
         */
        protected String getSeqPageCostInputtext() {

            Text text = (Text) seqPageCostInput;
            if (text.isDisposed()) {
                return "";
            }
            return text.getText().trim();

        }

        /**
         * Gets the random page cost inputtext.
         *
         * @return the random page cost inputtext
         */
        protected String getRandomPageCostInputtext() {

            Text text = (Text) randomPageCostInput;
            if (text.isDisposed()) {
                return "";
            }
            return text.getText().trim();

        }

        @Override
        protected void okPressed() {
            performOkOperation();
        }

        /**
         * Validate.
         *
         * @param text the text
         * @return true, if successful
         */
        private boolean validate(Text text) {
            boolean isValid = true;

            if (sizeUnlimitedBtn.getSelection()) {
                isValid = true;
            } else if (text.getText().startsWith("-")) {
                isValid = false;
            } else {
                String str = text.getText();
                String regex = "[0-9]+$";
                if (!str.matches(regex)) {
                    isValid = false;
                }

            }

            return isValid;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            final String okLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";
            final String cancelLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                    + "     ";

            final String runInBackgroundLebel = "     "
                    + MessageConfigLoader.getProperty(IMessagesConstants.RUN_IN_BACK_GROUND) + "     ";
            okButton = createButton(parent, UIConstants.OK_ID, okLbl, true);
            okButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_OK_001");
            cancelButton = createButton(parent, CANCEL, cancelLbl, false);
            cancelButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_CANCEL_001");
            runInBackGround = createButton(parent, UIConstants.RUN_IN_BACK_GROUND_ID, runInBackgroundLebel, false);
            runInBackGround.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_CANCEL_001");
            runInBackGround.setEnabled(false);
            okButton.setEnabled(false);
            setButtonLayoutData(okButton);

            runInBackGround.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    closeRunInmBackGround();
                }
            });
        }

        /**
         * Checks if is dialog complete.
         *
         * @return true, if is dialog complete
         */
        public boolean isDialogComplete() {
            return !((Text) nameInput).getText().isEmpty() && !((Text) locationInput).getText().isEmpty();
        }

        /**
         * Validate data.
         */
        public void validateData() {
            if (isDialogComplete()) {
                okButton.setEnabled(true);
            } else {
                okButton.setEnabled(false);
            }
        }

        /**
         * Sets the inits the text properties.
         *
         * @param ctrl the new inits the text properties
         */
        private void setInitTextProperties(Text ctrl) {
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            ctrl.setLayoutData(gd);

            ctrl.setText("");
            ctrl.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    // Do Nothing
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    /**
                     * Validate the connection info data.
                     */
                    validateData();
                }
            });

            ctrl.addListener(SWT.MenuDetect, new InitListener());
        }

        /**
         * The listener interface for receiving init events. The class that is
         * interested in processing a init event implements this interface, and
         * the object created with that class is registered with a component
         * using the component's <code>addInitListener<code> method. When the
         * init event occurs, that object's appropriate method is invoked.
         *
         * InitEvent
         */
        private class InitListener implements Listener {
            @Override
            public void handleEvent(Event event) {
                event.doit = false;
            }
        }

    }

}
