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
 * Description: The Class TruncateTableHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class TruncateTableHandler {
    private StatusMessage statusMesage;

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        TableMetaData selTable = IHandlerUtilities.getSelectedTable();
        if (selTable != null) {
            int returnType = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TRUNCATE_TABLE_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.TRUNCATE_TABLE_MSG,
                            selTable.getNamespace().getName(), selTable.getName()));
            if (returnType != 0) {
                return;
            }

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            StatusMessage statusMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_TRUNCATE_TABLE));
            TruncateTableWorker job = new TruncateTableWorker("Truncate table", null, selTable, statusMessage);
            setStatusMessage(statusMessage);
            StatusMessageList.getInstance().push(statusMessage);
            if (null != bottomStatusBar) {
                bottomStatusBar.activateStatusbar();
            }
            job.schedule();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        TableMetaData selectedTableMeta = IHandlerUtilities.getSelectedTable();
        if (null == selectedTableMeta) {
            return false;
        }
        return !IHandlerUtilities.isSelectedTableForignPartition();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TruncateTableWorker.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class TruncateTableWorker extends UIWorkerJob {
        private TableMetaData selTable;
        private StatusMessage statusMessage;
        private TerminalExecutionConnectionInfra conn;

        /**
         * Instantiates a new truncate table worker.
         *
         * @param name the name
         * @param family the family
         * @param selTable the sel table
         * @param statusMesage the status mesage
         */
        private TruncateTableWorker(String name, Object family, TableMetaData selTable, StatusMessage statusMesage) {
            super(name, family);
            this.selTable = selTable;
            this.statusMessage = statusMesage;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            conn = PromptPrdGetConnection.getConnection(selTable.getDatabase());
            selTable.execTruncate(conn.getConnection());
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {

            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TRUNCATE_TABLE_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.TRUNCATE_TABLE_TRUNCATED,
                            selTable.getNamespace().getName(), selTable.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.TRUNCATE_TABLE_TRUNCATED,
                            selTable.getNamespace().getName(), selTable.getName())));
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {

            String msg = MessageConfigLoader.getProperty(IMessagesConstants.TRUNCATE_TABLE_ERROR,
                    selTable.getNamespace().getName(), selTable.getName()) + MPPDBIDEConstants.LINE_SEPARATOR
                    + exception.getServerMessage();
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TRUNCATE_TABLE_TITLE), msg);
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.TRUNCATE_TABLE_ERROR,
                            selTable.getNamespace().getName(), selTable.getName())));
            MPPDBIDELoggerUtility.error("TruncateTableHandler: truncating table failed.", exception);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {

            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TRUNCATE_TABLE_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.TRUNCATE_TABLE_CONN_ERROR,
                            selTable.getNamespace().getName(), selTable.getName(), exception.getServerMessage()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.TRUNCATE_TABLE_ERROR,
                            selTable.getNamespace().getName(), selTable.getName())));
            MPPDBIDELoggerUtility.error("TruncateTableHandler: truncating table failed.", exception);
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
            if (null != bttmStatusBar) {

                bttmStatusBar.hideStatusbar(this.statusMessage);
            }
        }

    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMesage;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMesage = statMessage;
    }
}
