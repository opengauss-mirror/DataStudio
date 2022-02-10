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

import org.opengauss.mppdbide.bl.serverdatacache.Database;
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
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropDatabase.
 *
 * @since 3.0.0
 */
public class DropDatabase {
    private StatusMessage statusMessage;

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        Database selectedDb = IHandlerUtilities.getSelectedDatabase();
        if (selectedDb == null) {
            return;
        } else if (selectedDb.isConnected()) {
            return;
        }

        int returnType = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                MessageConfigLoader.getProperty(IMessagesConstants.DROP_DATABASE),
                MessageConfigLoader.getProperty(IMessagesConstants.DROP_DATABASE_ALERT,
                        MPPDBIDEConstants.LINE_SEPARATOR, selectedDb.getName()));

        if (returnType != 0) {
            return;
        }

        final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        StatusMessage statMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DROP_DATABASE));
        DropDatabaseWorker dropDb = new DropDatabaseWorker("Drop Database", MPPDBIDEConstants.CANCELABLEJOB, selectedDb,
                statMessage, bottomStatusBar);
        setStatusMessage(statMessage);
        StatusMessageList.getInstance().push(statMessage);
        if (bottomStatusBar != null) {
            bottomStatusBar.activateStatusbar();
        }
        dropDb.schedule();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Database selectedDb = IHandlerUtilities.getSelectedDatabase();
        if (null != selectedDb) {
            return !(selectedDb.isConnected());
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
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMessage = statMessage;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DropDatabaseWorker.
     */
    private static final class DropDatabaseWorker extends UIWorkerJob {

        private Database selectedDb;
        private StatusMessage statusMsg;
        private BottomStatusBar bottomStatusBar;

        /**
         * Instantiates a new drop database worker.
         *
         * @param name the name
         * @param family the family
         * @param selectedDb the selected db
         * @param statusMsg the status msg
         * @param bottomStatusBar the bottom status bar
         */
        public DropDatabaseWorker(String name, Object family, Database selectedDb, StatusMessage statusMsg,
                BottomStatusBar bottomStatusBar) {
            super(name, family);
            this.selectedDb = selectedDb;
            this.statusMsg = statusMsg;
            this.bottomStatusBar = bottomStatusBar;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            selectedDb.dropDatabase();
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.DB_DROPPED_DATABASE,
                            selectedDb.getServer().getServerConnectionInfo().getConectionName(),
                            selectedDb.getName())));
            MPPDBIDELoggerUtility.info("Dropped database successfully.");
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(dbCriticalException, selectedDb);

        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            String conectionName = selectedDb.getServer().getServerConnectionInfo().getConectionName();
            String dbName = selectedDb.getName();
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHEN_DROPPING_DB),
                    MessageConfigLoader.getProperty(IMessagesConstants.UNABLE_TO_DROP_DB, conectionName, dbName));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(
                    MessageConfigLoader.getProperty(IMessagesConstants.UNABLE_TO_DROP_DB, conectionName, dbName)));
            MPPDBIDELoggerUtility.error("Error dropping database", exception);

        }

        @Override
        public void onOutOfMemoryUIError(OutOfMemoryError errorOutOfMemory) {
            String msg = errorOutOfMemory.getMessage();
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHEN_DROPPING_DB),

                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_DURING_SCHEMA_CREATION,
                            MPPDBIDEConstants.LINE_SEPARATOR, msg));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_DURING_SCHEMA_CREATION,
                            MPPDBIDEConstants.LINE_SEPARATOR, msg)));
            bottomStatusBar.hideStatusbar(statusMsg);
        }

        @Override
        public void finalCleanup() throws MPPDBIDEException {
            // Auto-generated method stub

        }

        @Override
        public void finalCleanupUI() {
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (null != objectBrowserModel) {

                objectBrowserModel.refreshObject(selectedDb.getServer());
            }
            UIElement.getInstance().resetAllSQLTerminalConnections();
            if (null != bottomStatusBar) {

                bottomStatusBar.hideStatusbar(this.statusMsg);
            }

        }

    }

}
