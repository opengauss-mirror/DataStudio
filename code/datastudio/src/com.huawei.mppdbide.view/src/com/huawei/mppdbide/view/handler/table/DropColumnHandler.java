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

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
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
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
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
 * Description: The Class DropColumnHandler.
 *
 * @since 3.0.0
 */
public class DropColumnHandler {
    private StatusMessage statusMessage;

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        ColumnMetaData selColumn = IHandlerUtilities.getSelectedColumn();

        if (selColumn != null) {
            int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_COLUMN_DIA_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_COLUMN_DIA_MSG, selColumn.getName(),
                            selColumn.getParentTable().getNamespace().getName(), selColumn.getParentTable().getName()));

            if (returnValue != 0) {
                return;
            }

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            StatusMessage statusMsg = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DROP_COLUMN));
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForColumn(selColumn.getName(),
                    selColumn.getParentTable().getName(), selColumn.getParentTable().getNamespace().getName(),
                    selColumn.getDatabase().getName(), selColumn.getDatabase().getServerName(),
                    IMessagesConstants.DROP_COLUMN_PROGRESS_NAME);
            DropColumnHandlerWorker dropjob = new DropColumnHandlerWorker(progressLabel, selColumn, statusMsg);
            setStatusMessage(statusMsg);
            StatusMessageList.getInstance().push(statusMsg);
            if (null != bottomStatusBar) {

                bottomStatusBar.activateStatusbar();
            }
            dropjob.schedule();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ColumnMetaData ns = IHandlerUtilities.getSelectedColumn();

        if (null != ns && (ns.getParentTable() instanceof ForeignTable)) {
            return false;
        } else {
            return null != ns;
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DropColumnHandlerWorker.
     */
    private static final class DropColumnHandlerWorker extends UIWorkerJob {
        private ColumnMetaData selColumn;
        private StatusMessage staMsg;
        private TerminalExecutionConnectionInfra conn;
        private JobCancelStatus cancelStatus = null;

        /**
         * Instantiates a new drop column handler worker.
         *
         * @param name the name
         * @param selColumn the sel column
         * @param statusMsg the status msg
         */
        private DropColumnHandlerWorker(String name, ColumnMetaData selColumn, StatusMessage statusMsg) {
            super(name, MPPDBIDEConstants.CANCELABLEJOB);
            this.selColumn = selColumn;
            this.staMsg = statusMsg;
            this.cancelStatus = new JobCancelStatus();
            this.cancelStatus.setCancel(false);
        }

        @Override
        protected void canceling() {
            super.canceling();
            try {
                conn.getConnection().cancelQuery();
                this.cancelStatus.setCancel(true);
            } catch (DatabaseCriticalException exception) {
                MPPDBIDELoggerUtility.error("Error while cancelling query..", exception);
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("Error while cancelling query..", exception);
            }
        }

        /**
         * Check and show cancel message.
         */
        private void checkAndShowCancelMessage() {
            if (cancelStatus.getCancel()) {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                        Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUREY_CANCEL_MSG)));
                return;
            }
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            conn = PromptPrdGetConnection.getConnection(selColumn.getDatabase());
            selColumn.execDrop(conn.getConnection());
            selColumn.getParentTable().refresh(conn.getConnection());
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            checkAndShowCancelMessage();
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObject(selColumn.getParentTable());
            }

            String message = MessageConfigLoader.getProperty(IMessagesConstants.DROP_COLUMN_SUCCESS,
                    selColumn.getParentTable().getNamespace().getName(), selColumn.getParentTable().getName(),
                    selColumn.getName());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
            showErrorPopupMsg(dbCriticalException);
        }

        @Override
        public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {

            super.onMPPDBIDEExceptionUIAction(exception);
            showErrorPopupMsg(exception);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            showErrorPopupMsg(dbOperationException);
        }

        /**
         * Show error popup msg.
         *
         * @param exception the e
         */
        private void showErrorPopupMsg(MPPDBIDEException exception) {
            checkAndShowCancelMessage();
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_COLUMN_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_COLUMN_UNABLE_MSG,
                            selColumn.getParentTable().getNamespace().getName(), selColumn.getParentTable().getName(),
                            selColumn.getName()) + MPPDBIDEConstants.LINE_SEPARATOR + exception.getServerMessage());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.DROP_COLUMN_UNABLE_MSG,
                            selColumn.getParentTable().getNamespace().getName(), selColumn.getParentTable().getName(),
                            selColumn.getName())));
        }

        @Override
        public void finalCleanup() {
            if (this.conn != null) {
                this.conn.releaseConnection();
            }
        }

        @Override
        public void finalCleanupUI() {
            final BottomStatusBar bttmStsBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStsBar != null) {
                bttmStsBar.hideStatusbar(this.staMsg);
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
