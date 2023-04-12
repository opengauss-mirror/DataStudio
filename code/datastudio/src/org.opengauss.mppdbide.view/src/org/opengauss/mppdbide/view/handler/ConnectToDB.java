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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseHelper;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseUtils;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.LoginNotificationManager;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.exceptions.PasswordExpiryException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.core.ConnectionNotification;
import org.opengauss.mppdbide.view.core.LoadLevel1Objects;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.connection.PasswordDialog;
import org.opengauss.mppdbide.view.ui.DatabaseListControl;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
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
 * Description: The Class ConnectToDB.
 *
 * @since 3.0.0
 */
public class ConnectToDB {
    private static StatusMessage statusMessage;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {

        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        Database database = null;
        if (obj instanceof Database) {
            database = (Database) obj;
        }
        Database db = database;
        if (null != db) {
            connect(db, shell);
        }
    }

    /**
     * Run cunnect to DB job.
     *
     * @param db the db
     */
    private void runCunnectToDBJob(final Database db) {
        final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        StatusMessage statMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CONNECT_DATABASE));
        setStatusMessage(statMessage);

        if (null != bottomStatusBar) {

            bottomStatusBar.setStatusMessage(statusMessage.getMessage());
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForDatabase(db.getName(),
                    db.getDatabase().getServerName(), IMessagesConstants.CONNECT_DB_PROGRESS_NAME);
            ConnectDBWorker worker = new ConnectDBWorker(progressLabel, db, bottomStatusBar);
            worker.setTaskDB(db);
            StatusMessageList.getInstance().push(statusMessage);
            bottomStatusBar.activateStatusbar();

            worker.schedule();

        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ConnectDBWorker.
     */
    private final class ConnectDBWorker extends UIWorkerJob {
        private static final String INVALID_USERNAME_CIPHER_OLAP = "Invalid username/password";
        private static final String YOU_CANNOT_CHANGE_THE_STATE_FROM_CONNECT_TO_CONNECT = "You cannot change the state from CONNECT to CONNECT";
        private Database db;
        private BottomStatusBar btmStatusBar;
        private String elapsedTime = "";
        private IExecTimer exc = null;
        private JobCancelStatus cancelStatus = null;

        /**
         * Instantiates a new connect DB worker.
         *
         * @param name the name
         * @param db the db
         * @param bottomStatusBar the bottom status bar
         */
        private ConnectDBWorker(String name, Database db, BottomStatusBar bottomStatusBar) {
            super(name, MPPDBIDEConstants.CANCELABLEJOB);
            this.db = db;
            this.btmStatusBar = bottomStatusBar;
            this.cancelStatus = new JobCancelStatus();
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            exc = new ExecTimer("Connect to databse");
            exc.start();

            switch (db.getDBType()) {
                case OPENGAUSS: {
                    performConnectToDBJobForOLAP();
                    break;
                }
                default: {
                    break;
                }
            }

            exc.stopAndLog();
            return null;
        }

        private void performConnectToDBJobForOLAP() throws MPPDBIDEException, DatabaseOperationException,
                DatabaseCriticalException, DataStudioSecurityException, OutOfMemoryError, PasswordExpiryException {
            IServerConnectionInfo serverConnectionInfo = null;
            String serverVersion = null;
            boolean isDatabaseConnected = db.getServer().isAleastOneDbConnected();
            db.connectToServer();
            serverVersion = db.getExecutor().getServerVersion();
            Server server = db.getServer();
            db.initDolphinTypesIfNeeded();
            serverConnectionInfo = server.getServerConnectionInfo();
            server.setServerVersion(serverVersion);
            ((ServerConnectionInfo) serverConnectionInfo).setDBVersion(server.getServerVersion(true));
            serverConnectionInfo.setModifiedSchemaExclusionList(serverConnectionInfo.getSchemaExclusionList());
            serverConnectionInfo.setModifiedSchemaInclusionList(serverConnectionInfo.getSchemaInclusionList());
            if (!serverVersion.equals(((ServerConnectionInfo) serverConnectionInfo).getDBVersion())) {
                server.persistConnectionDetails(serverConnectionInfo);
            }
            if (!cancelStatus.getCancel()) {
                LoginNotificationManager.loginOnPswdExpiry(db);

                UIDisplayFactoryProvider.getUIDisplayStateIf().setConnectedProfileId(db.getProfileId());
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                        Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.SUCCESSFULLY_CONNECTED_TO,
                                db.getServer().getServerConnectionInfo().getConectionName(), db.getName())));
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message
                        .getInfo(MessageConfigLoader.getProperty(IMessagesConstants.LOADING_OBJECT_INTO_BROWSER)));

                db.fetchSearchPathObjects(true);
                db.fetchDefaultDatatypes(true);
                DatabaseHelper.fetchTablespaceName(db);

                if (!isDatabaseConnected) {
                    showPasswordExpiryWarning(db);
                }
            }
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            /**
             * New database is connected. Show login notification.
             */
            if (cancelStatus.getCancel()) {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                        Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.CONNECT_DB_CANCEL_MSG)));
                return;
            } else {
                MPPDBIDELoggerUtility
                        .info(MessageConfigLoader.getProperty(IMessagesConstants.LOGIN_NOTIFICATION_STARTED));
                if (MPPDBIDEConstants.OPENGAUSS.equals(db.getDBType().toString())) {

                    ConnectionNotification connNotification = new ConnectionNotification(db);
                    connNotification.loadnotification();

                    MPPDBIDELoggerUtility
                            .info(MessageConfigLoader.getProperty(IMessagesConstants.LOGIN_NOTIFICATION_ENDED));
                }

                ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                if (objectBrowserModel != null) {
                    objectBrowserModel.refreshObject(db.getServer());
                }
                DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
                if (null != databaseListControl) {
                    databaseListControl.refreshConnectionComboItems();
                    databaseListControl.setSelectedDatabase(db);
                }
                UIElement.getInstance().updateTextEditorsIconAndConnButtons(db.getServer());

                LoadLevel1Objects load = new LoadLevel1Objects(db, getStatusMessage());

                try {
                    load.loadObjects();
                } catch (DatabaseCriticalException exception) {
                    MPPDBIDELoggerUtility.error("ConnectToDB: loading objects failed.", exception);
                }

                if (db.getServer().isAleastOneDbConnected()) {
                    if (null != objectBrowserModel) {
                        objectBrowserModel.refreshTablespaceGrp(db.getServer().getTablespaceGroup());
                        objectBrowserModel.refreshUserRoleGrp(db.getServer().getUserRoleObjectGroup());
                    }
                }
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message
                        .getInfo(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_SUCCESSFULLY_LOADED)));
            }

        }

        @Override
        public void onExceptionUIAction(Exception exception) {

            if (exception instanceof PasswordExpiryException) {
                MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                        MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_EXPIRE_CONFIRMATION),
                        MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_EXPIRED),
                        new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK)}, 0);
            }
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception, db);

        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            if (cancelStatus.getCancel()) {

                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                        Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.CONNECT_DB_CANCEL_MSG)));

                return;

            }

            String msg = exception.getServerMessage();
            if (null == msg) {
                msg = exception.getMessage();
            }

            if (exception.getDBErrorMessage().contains(YOU_CANNOT_CHANGE_THE_STATE_FROM_CONNECT_TO_CONNECT)) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERR),
                        MessageConfigLoader.getProperty(IMessagesConstants.CONNECT_TO_DB_STATE_MACHINE_ERROR));
                return;

            }
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERR),
                    MessageConfigLoader.getProperty(IMessagesConstants.UNABLE_TO_CONNECT_TO_DATABASE_DB, db.getName(),
                            MPPDBIDEConstants.LINE_SEPARATOR, msg));

            String invalidMessageOLAP = INVALID_USERNAME_CIPHER_OLAP;

            if (exception.getServerMessage() != null && exception.getServerMessage().contains(invalidMessageOLAP)) {
                PasswordDialog dialogHelper = new PasswordDialog(Display.getCurrent().getActiveShell(), db);
                int returnVal = dialogHelper.open();
                if (returnVal == 0) {
                    runCunnectToDBJob(db);
                }
            }

        }

        @Override
        public void onOutOfMemoryUIError(OutOfMemoryError error) {
            handleOutofMemory(db, elapsedTime, exc, error);
        }

        @Override
        public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {
            String msg = exception.getServerMessage();
            if (null == msg) {

                msg = exception.getMessage();
            }
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERR),
                    MessageConfigLoader.getProperty(IMessagesConstants.UNABLE_TO_CONNECT_TO_DATABASE_DB, db.getName(),
                            MPPDBIDEConstants.LINE_SEPARATOR, msg));

        }

        @Override
        public void finalCleanup() throws MPPDBIDEException {
        }

        @Override
        public void finalCleanupUI() {
            if (cancelStatus.getCancel()) {
                DBConnProfCache.getInstance().destroyConnection(db);
                IHandlerUtilities.cleanupAllJobsInDB(db);
                UIDisplayFactoryProvider.getUIDisplayStateIf().cleanupUIItems();
                ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                if (objectBrowserModel != null) {
                    objectBrowserModel.refreshObject(db);
                }
            }
            btmStatusBar.hideStatusbar(statusMessage);
        }

        @Override
        protected void canceling() {

            super.canceling();
            cancelStatus.setCancel(true);

            cancelCurrentQueries();
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_IN_PROGRESS)));
        }

        /**
         * Cancel current queries.
         */
        private void cancelCurrentQueries() {
            if (null != db && db.isConnected()) {
                try {
                    db.getConnectionManager().cancelAllConnectionQueries();
                } catch (DatabaseCriticalException exception) {
                    MPPDBIDELoggerUtility.error("Operation cancelled on user request", exception);
                } catch (DatabaseOperationException exception) {
                    MPPDBIDELoggerUtility.error("Operation cancelled on user request", exception);
                }
            }
        }
    }

    /**
     * Show password expiry warning.
     *
     * @param database the database
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private static void showPasswordExpiryWarning(Database database)
            throws DatabaseCriticalException, DatabaseOperationException {
        float deadLine = 0;
        String deadlineStamp = DatabaseUtils.getDeadlineInfo(MPPDBIDEConstants.FETCH_COUNT, database);

        if (deadlineStamp != null) {
            deadLine = Float.parseFloat(deadlineStamp);
        }
        int deadLineTime = ((Double) (Math.ceil(deadLine))).intValue();
        float notifyTime = (float) DatabaseUtils.getNotifyInfo(MPPDBIDEConstants.FETCH_COUNT, database);

        if (deadLineTime <= 0) {
            loggingAllowedAfterPasswordExpiry();
        } else if (deadLineTime <= notifyTime) {
            showPasswordExpiryWarningPopup(deadLineTime);
        }

    }

    /**
     * Show password expiry warning popup.
     *
     * @param deadLineTime the dead line time
     */
    private static void showPasswordExpiryWarningPopup(final int deadLineTime) {

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {

                MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                        MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_EXPIRE_CONFIRMATION),
                        MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_YET_TO_EXPIRE, deadLineTime),
                        MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));

            }
        });

    }

    /**
     * Logging allowed after password expiry.
     */
    private static void loggingAllowedAfterPasswordExpiry() {

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                        MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_EXPIRY_MSG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_EXPIRY_INFORMATION),
                        MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));
            }
        });

    }

    /**
     * Handle outof memory.
     *
     * @param db the db
     * @param elapsedTimeParam the elapsed time param
     * @param exc the exc
     * @param errorOutOfMemory the e
     */
    private static void handleOutofMemory(final Database db, String elapsedTimeParam, IExecTimer exc,
            OutOfMemoryError errorOutOfMemory) {
        String elapsedTime = elapsedTimeParam;
        try {
            exc.stop();
            elapsedTime = exc.getElapsedTime();
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Execute timer operation failed.", exception);
        }
        UIElement.getInstance().outOfMemoryCatch(elapsedTime, errorOutOfMemory.getMessage());
        DBConnProfCache.getInstance().destroyConnection(db);
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj == null) {
            return false;
        }

        if (obj instanceof Database) {
            Database db = (Database) obj;
            return db.isConnected() ? false : true;
        }
        return false;
    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public static StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public static void setStatusMessage(StatusMessage statMessage) {
        statusMessage = statMessage;
    }

    /**
     * Connect.
     *
     * @param db the db
     * @param shell the shell
     */
    public void connect(Database db, Shell shell) {

        PasswordDialog dialogHelper = new PasswordDialog(shell, db);
        if (!db.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)) {
            runCunnectToDBJob(db);
            return;
        }
        int returnVal = dialogHelper.open();
        if (returnVal == 0) {

            runCunnectToDBJob(db);
        }
    }
}
