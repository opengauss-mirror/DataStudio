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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
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
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.connectiondialog.CreateRenamDatabaseDialog;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameDatabase.
 *
 * @since 3.0.0
 */
public class RenameDatabase {

    private StatusMessage statusMessage;

    private Database db;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final Database selectedDb = IHandlerUtilities.getSelectedDatabase();
        if (selectedDb == null) {
            return;
        } else if (selectedDb.isConnected()) {
            return;
        }
        CreateRenamDatabaseDialog renameDbDialog = new RenameDBDlg(shell, selectedDb, shell);
        renameDbDialog.open();
    }

    /**
     * Cancel rename connection.
     */
    private void cancelRenameConnection() {
        if (db.isConnected()) {
            try {
                db.getConnectionManager().cancelAllConnectionQueries();
            } catch (DatabaseCriticalException e) {
                MPPDBIDELoggerUtility.info("Operation cancelled on user request");
            } catch (DatabaseOperationException e) {
                MPPDBIDELoggerUtility.info("Operation cancelled on user request");
            }
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameDBDlg.
     */
    private final class RenameDBDlg extends CreateRenamDatabaseDialog {
        private final Shell shell;
        private int returnType;

        /**
         * Instantiates a new rename DB dlg.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param shell the shell
         */
        private RenameDBDlg(Shell parent, Object serverObject, Shell shell) {
            super(parent, serverObject);
            this.shell = shell;
        }

        @Override
        protected void performOkOperation() {
            db = (Database) getObject();
            String userInput = getUserInput();
            String oldName = db.getName();
            boolean needToConnect = false;

            if (db.isConnected()) {
                returnType = generateRenameDBPopup();
            }
            if (returnType == IDialogConstants.OK_ID) {
                if ("".equals(userInput)) {
                    printErrorMessage(
                            MessageConfigLoader.getProperty(IMessagesConstants.ENTER_NEW_NAME_FOR_DB, oldName));
                }
                if (isPasswordRequired.getSelection()) {
                    needToConnect = true;
                }
                enableOKButton(false);
                enableDisableText(false);
                printMessage(MessageConfigLoader.getProperty(IMessagesConstants.DB_RENAME_RENAMING_DATABASE, oldName));
                setStatusMessage();
                final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
                if (bottomStatusBar != null) {
                    bottomStatusBar.activateStatusbar();
                }
                String progressLabel = ProgressBarLabelFormatter.getProgressLabelForDatabase(oldName,
                        db.getServerName(), IMessagesConstants.RENAME_DATABASE_PROGRESS_NAME);
                RenameDatabaseWorker worker = new RenameDatabaseWorker(progressLabel, db, bottomStatusBar, userInput,
                        oldName, needToConnect, this, shell);
                worker.setTaskDB(db);
                worker.schedule();
            }

        }

        @Override
        protected void cancelPressed() {
            close();
        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_DATABASE);
        }

        @Override
        protected String getHeader() {
            Database databse = (Database) getObject();
            return MessageConfigLoader.getProperty(IMessagesConstants.ENTER_NEW_NAME_FOR_DB, databse.getName());
        }

        @Override
        protected String getHeaderPswd() {
            return MessageConfigLoader.getProperty(IMessagesConstants.CURRENT_USER_CIPHER_TO_CONNECT_TO_DB);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameDatabaseWorker.
     */
    private final class RenameDatabaseWorker extends UIWorkerJob {
        private Database db;
        private String userInput;
        private String oldName;
        private boolean needToConnect;
        private CreateRenamDatabaseDialog dialog;
        private Shell shell;
        private JobCancelStatus status;

        public RenameDatabaseWorker(String name, Database db, BottomStatusBar bottomStatusBar, String userInput,
                String oldname, boolean needToConnect, CreateRenamDatabaseDialog dialog, Shell shell) {
            super(name, MPPDBIDEConstants.CANCELABLEJOB);
            this.db = db;
            this.oldName = oldname;
            this.needToConnect = needToConnect;
            this.userInput = userInput;
            this.dialog = dialog;
            this.shell = shell;
            this.status = new JobCancelStatus();

        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {

            if (!status.getCancel()) {
                db.renameDatabase(userInput);
            }
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                    .getProperty(IMessagesConstants.DB_RENAME_RENAMED_DATABASE, oldName, db.getName())));
            return null;

        }

        @Override
        public void onSuccessUIAction(Object obj) {
            if (needToConnect) {
                if (!status.getCancel()) {

                    ConnectToDB connectDb = new ConnectToDB();
                    connectDb.connect(db, shell);
                    ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                            .getProperty(IMessagesConstants.DB_RENAME_RENAMED_DATABASE, oldName, db.getName())));

                }

                else {
                    UIDisplayFactoryProvider.getUIDisplayStateIf().cleanupUIItems();
                    DBConnProfCache.getInstance().destroyConnection(db);
                }
            }
            dialog.close();

            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();

            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(getStatusMessage());
            }

        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {

            String messg = dbCriticalException.getServerMessage();
            if (null == messg) {
                messg = dbCriticalException.getDBErrorMessage();
            }

            if (messg.contains("Position:")) {
                messg = messg.split("Position:")[0];
            }
            dialog.printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_RENAMING_DATABASE,
                    MPPDBIDEConstants.LINE_SEPARATOR, messg));
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(getStatusMessage());
            }
            if (db.isConnected() && !dialog.getShell().isDisposed()) {
                dialog.enableOKButton(true);
            }
            dialog.enableDisableText(true);
        }

        @Override
        public void onExceptionUIAction(Exception uiException) {
            super.onExceptionUIAction(uiException);
            String msg = uiException.getMessage();
            if (null != msg && msg.contains("Position:")) {
                msg = msg.split("Position:")[0];
            }
            dialog.printErrorMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_RENAMING_DATABASE, msg));
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(getStatusMessage());
            }
            if (db.isConnected() && !dialog.getShell().isDisposed()) {
                dialog.enableOKButton(true);
            }
            dialog.enableDisableText(true);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            handleException(dbOperationException);
            if (db.isConnected() && !dialog.isDisposed()) {
                dialog.enableOKButton(true);
            }

            dialog.enableDisableText(true);
        }

        private void handleException(DatabaseOperationException dbOperationException) {
            String msg = dbOperationException.getServerMessage();
            if (null == msg) {
                msg = dbOperationException.getDBErrorMessage();
            }

            if (msg.contains("Position:")) {
                msg = msg.split("Position:")[0];
            }
            dialog.printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_RENAMING_DATABASE,
                    MPPDBIDEConstants.LINE_SEPARATOR, msg));
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();

            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(getStatusMessage());
            }
        }

        @Override
        public void finalCleanup() throws MPPDBIDEException {

        }

        @Override
        public void finalCleanupUI() {
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObject(db.getServer());
            }
            UIElement.getInstance().updateTextEditorsIconAndConnButtons(db.getServer());

            MPPDBIDELoggerUtility.info("Database renamed successfully");

        }

        @Override
        protected void canceling() {

            if (needToConnect) {
                status.setCancel(true);
                cancelRenameConnection();
                super.canceling();
            }
        }

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Database selDatabase = IHandlerUtilities.getSelectedDatabase();
        if (null != selDatabase) {
            return !(selDatabase.isConnected());
        }

        return false;

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
     * @param statusMsg the new status message
     */
    public void setStatusMessage(StatusMessage statusMsg) {
        this.statusMessage = statusMsg;
    }

    /**
     * Generate rename DB popup.
     *
     * @return the int
     */
    private int generateRenameDBPopup() {
        return MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                MessageConfigLoader.getProperty(IMessagesConstants.RENAME_DATABASE),
                MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_RENAME_CONFIRMATION));
    }

    /**
     * Sets the status message.
     */
    private void setStatusMessage() {
        StatusMessage statMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_UPDATE_DATABASE));
        setStatusMessage(statMessage);
        StatusMessageList.getInstance().push(statMessage);
    }
}
