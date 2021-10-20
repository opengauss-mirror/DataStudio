/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DatabaseUtils;
import com.huawei.mppdbide.bl.serverdatacache.IJobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.groups.DatabaseObjectGroup;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.DatabaseListControl;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.connectiondialog.CreateRenamDatabaseDialog;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateDatabase.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CreateDatabase {

    private StatusMessage statusMessage;
    private Button okBtn;
    private Button cancelButton;
    private Server server;

    private Database getDB;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        DatabaseObjectGroup dbGroup = IHandlerUtilities.getSelectedDBGroup();
        if (dbGroup == null) {
            return;
        }
        Server selectedServer = dbGroup.getServer();
        // DTS2014102808375 start
        if (selectedServer != null) {
            CreateRenamDatabaseDialog createDbDialog = new CreateDatabaseInner(shell, selectedServer, shell);

            createDbDialog.open();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    // DTS2014102808375 end
    @CanExecute
    public boolean canExecute() {
        DatabaseObjectGroup dbGroup = IHandlerUtilities.getSelectedDBGroup();
        if (dbGroup == null) {
            return false;
        }
        Server selectedServer = dbGroup.getServer();
        return null != selectedServer && selectedServer.isAleastOneDbConnected();
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
     * Description: The Class CreateDatabaseInner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class CreateDatabaseInner extends CreateRenamDatabaseDialog {
        private final Shell shell;

        /**
         * Instantiates a new creates the database inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param shell the shell
         */
        private CreateDatabaseInner(Shell parent, Object serverObject, Shell shell) {
            super(parent, serverObject);
            this.shell = shell;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parentObject) {
            final String okLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";
            final String cancelLabel = "     "
                    + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC) + "     ";
            okBtn = createButton(parentObject, UIConstants.OK_ID, okLabel, true);
            okBtn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_OK_001");
            cancelButton = createButton(parentObject, CANCEL, cancelLabel, false);
            cancelButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_CANCEL_001");

            ((Text) inputControl).addKeyListener(new KeyListener() {

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
            });
            okBtn.setEnabled(false);
            setButtonLayoutData(okBtn);
        }

        @Override
        protected void cancelPressed() {
            close();
        }

        @Override
        protected void performOkOperation() {
            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            okBtn.setEnabled(false);
            cancelButton.setEnabled(false);

            boolean needToConnect = false;

            String dbName = getUserInput();
            if ("".equals(dbName)) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.ENTER_DATABASE_NAME_TO_CREATE));
                if (bottomStatusBar != null) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                return;
            }
            String encodingType = getComboInput();

            server = (Server) getObject();

            if (isPasswordRequired.getSelection()) {
                needToConnect = true;
            }

            Database db;
            try {
                db = server.findOneActiveDb();
            } catch (MPPDBIDEException e1) {
                enableButtons(bottomStatusBar);
                return;
            }

            StatusMessage statMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CREATE_DATABASE));
            setStatusMessage(statMessage);
            StatusMessageList.getInstance().push(statMessage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForDatabase(dbName, server.getName(),
                    IMessagesConstants.CREATE_DB_PROGRESS_NAME);
            CreateDBWorkerJob workerJob = new CreateDBWorkerJob(progressLabel, dbName, server, encodingType,
                    needToConnect, db, this, shell);
            workerJob.schedule();
        }

        /**
         * Enable buttons.
         *
         * @param bottomStatusBar the bottom status bar
         */
        private void enableButtons(final BottomStatusBar bottomStatusBar) {
            okBtn.setEnabled(true);
            cancelButton.setEnabled(true);
            if (bottomStatusBar != null) {
                bottomStatusBar.hideStatusbar(getStatusMessage());
            }
        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.CREATE_DATABASE);
        }

        @Override
        protected String getHeader() {
            return MessageConfigLoader.getProperty(IMessagesConstants.NAME_OF_THE_DATABASE);
        }

        @Override
        protected String getHeaderPswd() {
            // start DTS2016012610981
            return MessageConfigLoader.getProperty(IMessagesConstants.CURRENT_USER_PASSWORD_TO_CONNECT_TO_DB);
            // end DTS2016012610981
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CreateDBWorkerJob.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class CreateDBWorkerJob extends UIWorkerJob {

        private Server server;
        String dbName;
        String encodingType;
        private boolean needToConnect;
        CreateRenamDatabaseDialog dialog;
        Shell shell;
        TerminalExecutionConnectionInfra connInfra;
        private IJobCancelStatus iDSCancellable;
        private Database db;

        /**
         * Instantiates a new creates the DB worker job.
         *
         * @param progressLabel the progress label
         * @param name the name
         * @param server the server
         * @param encodingType the encoding type
         * @param needToConnect the need to connect
         * @param db the db
         * @param dialog the dialog
         * @param shell the shell
         */
        public CreateDBWorkerJob(String progressLabel, String name, Server server, String encodingType,
                boolean needToConnect, Database db, CreateRenamDatabaseDialog dialog, Shell shell) {
            super(progressLabel, MPPDBIDEConstants.CANCELABLEJOB);
            this.dbName = name;
            this.encodingType = encodingType;
            this.needToConnect = needToConnect;
            this.server = server;
            this.dialog = dialog;
            this.shell = shell;
            iDSCancellable = new JobCancelStatus();
            this.db = db;

        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            connInfra = PromptPrdGetConnection.getConnection(db);
            server.createDatabase(dbName, encodingType, connInfra.getDatabase(), connInfra.getConnection());
            getDB = server.getDbByName(dbName);

            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            if (needToConnect && null != getDB) {
                if (!isCancel()) {
                    ConnectToDB connectDb = new ConnectToDB();
                    connectDb.connect(getDB, shell);
                    DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
                    if (null != databaseListControl) {
                        databaseListControl.setSelectedDatabase(getDB);
                    }
                } else {
                    UIDisplayFactoryProvider.getUIDisplayStateIf().cleanupUIItems();
                }
            }

            dialog.close();
            
            BottomStatusBar bttmStatusBar = null;
            if (getDB != null) {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_SUCCESSFULLY,
                        server.getServerConnectionInfo().getConectionName(), getDB.getName())));
                bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            }
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(getStatusMessage());
            }

        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(getStatusMessage());
            }
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            String msg = dbOperationException.getServerMessage();
            if (null == msg) {
                msg = dbOperationException.getDBErrorMessage();
            }
            // Bala issue List #12 start
            if (msg.contains("Position:")) {
                msg = msg.split("Position:")[0];
            }
            // Bala issue List #12 end
            dialog.printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_WHILE_CREATING_DATABASE,
                    MPPDBIDEConstants.LINE_SEPARATOR, msg));
            okBtn.setEnabled(true);
            cancelButton.setEnabled(true);

            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(getStatusMessage());
            }

        }

        @Override
        public void finalCleanup() throws MPPDBIDEException {
            if (connInfra != null) {
                connInfra.releaseConnection();
            }
        }

        @Override
        public void finalCleanupUI() {
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObject(server);
            }
            UIElement.getInstance().updateTextEditorsIconAndConnButtons(server);
            MPPDBIDELoggerUtility.info("New databasse created .");

        }

        @Override
        protected void canceling() {
            super.canceling();
            if (needToConnect) {
                iDSCancellable.setCancel(true);
                cancelCreateDbOperation();
            }

        }

        /**
         * Cancel create db operation.
         */
        private void cancelCreateDbOperation() {
            try {
                DatabaseUtils.checkCancelStatusAndAbort(iDSCancellable, getDB);

            } catch (DatabaseOperationException e) {
                MPPDBIDELoggerUtility.info("Operation cancelled on user request");
            } catch (DatabaseCriticalException e) {
                MPPDBIDELoggerUtility.info("Operation cancelled on user request");
            }

        }

    }

}
