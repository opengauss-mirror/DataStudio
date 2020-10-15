/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
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
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class VacuumTableHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class VacuumTableHandler {
    private StatusMessage statusMessage;

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        TableMetaData selTable = IHandlerUtilities.getSelectedTable();
        if (selTable != null) {
            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            StatusMessage statusMsg = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_VACUUM_TABLE));
            VacuumTableHandlerWorker vacuumjob = new VacuumTableHandlerWorker(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_VACUUM_TABLE), null, selTable,
                    statusMsg);
            setStatusMessage(statusMsg);
            StatusMessageList.getInstance().push(statusMsg);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            vacuumjob.schedule();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        TableMetaData selTableMetaData = IHandlerUtilities.getSelectedTable();
        if (null == selTableMetaData) {
            return false;
        }
        return !IHandlerUtilities.isSelectedTableForignPartition();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class VacuumTableHandlerWorker.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class VacuumTableHandlerWorker extends UIWorkerJob {
        private TableMetaData selTable;
        private StatusMessage staMsg;
        private TerminalExecutionConnectionInfra conn;

        /**
         * Instantiates a new vacuum table handler worker.
         *
         * @param name the name
         * @param family the family
         * @param selTable the sel table
         * @param statusMsg the status msg
         */
        private VacuumTableHandlerWorker(String name, Object family, TableMetaData selTable, StatusMessage statusMsg) {
            super(name, family);
            this.selTable = selTable;
            this.staMsg = statusMsg;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            conn = PromptPrdGetConnection.getConnection(selTable.getDatabase());
            selTable.execVacumm(conn.getConnection());
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.VACCUME_TABLE_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.VACCUM_TABLE_SUCCESS,
                            selTable.getNamespace().getName(), selTable.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.VACCUM_TABLE_SUCCESS,
                            selTable.getNamespace().getName(), selTable.getName())));
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.VACCUME_TABLE_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.VACCUME_TABLE_CONN_ERROR,
                            selTable.getNamespace().getName(), selTable.getName(), exception.getServerMessage()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.VACCUME_TABLE_ERROR,
                            selTable.getNamespace().getName(), selTable.getName())));
            MPPDBIDELoggerUtility.error("VacuumTableHandler: Vacuuming table failed.", exception);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            String msg = MessageConfigLoader.getProperty(IMessagesConstants.VACCUME_TABLE_ERROR,
                    selTable.getNamespace().getName(), selTable.getName()) + MPPDBIDEConstants.LINE_SEPARATOR
                    + exception.getServerMessage();
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.VACCUME_TABLE_TITLE), msg);
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.VACCUME_TABLE_ERROR,
                            selTable.getNamespace().getName(), selTable.getName())));
            MPPDBIDELoggerUtility.error("VacuumTableHandler: Vacuuming table failed.", exception);
        }

        @Override
        public void finalCleanup() {
            if (this.conn != null) {
                this.conn.releaseConnection();
            }
        }

        @Override
        public void finalCleanupUI() {
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(this.staMsg);
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
}
