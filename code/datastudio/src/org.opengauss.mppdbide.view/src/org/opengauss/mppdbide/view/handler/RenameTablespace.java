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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Tablespace;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserInputDialog;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameTablespace.
 *
 * @since 3.0.0
 */
public class RenameTablespace {
    private StatusMessage statusMessage;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        Tablespace tablespace = IHandlerUtilities.getSelectedTablespace();

        UserInputDialog renameTableDialog = new RenameTSDlg(shell, tablespace);

        renameTableDialog.open();
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
     * Description: The Class RenameTSDlg.
     */
    private final class RenameTSDlg extends UserInputDialog {

        /**
         * Instantiates a new rename TS dlg.
         *
         * @param parent the parent
         * @param serverObject the server object
         */
        private RenameTSDlg(Shell parent, Object serverObject) {
            super(parent, serverObject);
        }

        @Override
        protected void performOkOperation() {
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            Tablespace tablespace = (Tablespace) getObject();
            StatusMessage statMssage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_RENAME_TABLESPACE));

            String userInput = getUserInput();
            if ("".equals(userInput)) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TABLESPACE_NEW_NAME),
                        false);
                if (bttmStatusBar != null) {
                    bttmStatusBar.hideStatusbar(getStatusMessage());
                }
                enableButtons();
                return;
            }

            printMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TABLESPACE_WAIT, tablespace.getName()),
                    true);

            RenameTablespaceWorker worker = new RenameTablespaceWorker(tablespace, userInput, this, statMssage,
                    bttmStatusBar);

            setStatusMessage(statMssage);
            StatusMessageList.getInstance().push(statMssage);
            if (bttmStatusBar != null) {
                bttmStatusBar.activateStatusbar();
            }
            worker.schedule();
        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TABLESPACE_DIA_TITILE);
        }

        @Override
        protected String getHeader() {
            Tablespace selTable = (Tablespace) getObject();

            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TABLESPACE_NEW, selTable.getName());

        }

        @Override
        public void onSuccessUIAction(Object obj) {
            return;
        }

        @Override
        public void onPresetupFailureUIAction(MPPDBIDEException exception) {
            return;
        }
        
        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.TABLESPACE, this.getClass());
        }
        
        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            return;
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            return;
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameTablespaceWorker.
     */
    private static final class RenameTablespaceWorker extends PromptPasswordUIWorkerJob {
        private Tablespace tablespace;
        private String newName;
        private String oldName;
        private UserInputDialog dialog;
        private StatusMessage statusMsg;
        private BottomStatusBar btmstatusbar;
        private DBConnection connection;
        private Database db;
        private static final String RENAME_TABLESPACE = "Renaming Tablespace";

        /**
         * Instantiates a new rename tablespace worker.
         *
         * @param obj the obj
         * @param newName the new name
         * @param dialog the dialog
         * @param statusMsg the status msg
         * @param btmstatusbar the btmstatusbar
         */
        private RenameTablespaceWorker(Tablespace obj, String newName, UserInputDialog dialog, StatusMessage statusMsg,
                BottomStatusBar btmstatusbar) {
            super(RENAME_TABLESPACE, MPPDBIDEConstants.CANCELABLEJOB,
                    IMessagesConstants.RENAME_TABLESPACE_FAILED_TITLE);
            this.tablespace = obj;
            this.dialog = dialog;
            this.oldName = tablespace.getName();
            this.newName = newName;
            this.statusMsg = statusMsg;
            this.btmstatusbar = btmstatusbar;
        }

        @Override
        public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException {
            setServerPwd(tablespace.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
            if (getDatabase() != null) {
                this.connection = getDatabase().getConnectionManager().getObjBrowserConn();
            }
            tablespace.renameTablespace(this.newName, this.connection);
            MPPDBIDELoggerUtility.info("Rename tablespace successful");

            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TABLESPACE_TO, oldName,
                            tablespace.getServer().getServerConnectionInfo().getConectionName(), newName)));
            IHandlerUtilities.pritnAndRefresh(tablespace.getServer().getTablespaceGroup());

        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
            dialog.printErrorMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERR_DURING_RENAMING_TABLESPACE,
                            MPPDBIDEConstants.LINE_SEPARATOR, dbCriticalException.getDBErrorMessage()),
                    false);
            btmstatusbar.hideStatusbar(statusMsg);
            dialog.enableButtons();
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
            dialog.printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_RENAMING_TABLESPACE,
                    oldName, MPPDBIDEConstants.LINE_SEPARATOR, msg), false);
            btmstatusbar.hideStatusbar(statusMsg);
            dialog.enableButtons();
        }

        @Override
        public void finalCleanupUI() {
            if (isPromptCancelledByUser()) {
                dialog.printErrorMessage("", false);
                dialog.enableButtons();
            } else {
                MPPDBIDELoggerUtility.info(" Tablespace successfully renamed");
            }
            btmstatusbar.hideStatusbar(this.statusMsg);
        }

        @Override
        public void finalCleanup() {
            super.finalCleanup();
            // Nothing to be done.
        }

        @Override
        protected Database getDatabase() {
            if (this.db != null) {
                return this.db;
            }

            try {
                this.db = tablespace.getServer().findOneActiveDb();
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("failed to get database", exception);
            }
            return this.db;
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
}
