/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
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
 * Description: The Class DropDatabase.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
