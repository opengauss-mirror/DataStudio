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

package org.opengauss.mppdbide.view.handler.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ForeignTable;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
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
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetColumnNotNullHandler.
 *
 * @since 3.0.0
 */
public class SetColumnNotNullHandler {
    private StatusMessage statusMessageSetColumnNotNullHandler;

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        ColumnMetaData selColumn = IHandlerUtilities.getSelectedColumn();
        if (selColumn == null) {
            return;
        }
        TableMetaData table = selColumn.getParentTable();
        String schemaName = table.getNamespace().getName();
        int returnType = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                MessageConfigLoader.getProperty(IMessagesConstants.SET_COLUMN_TOGGLE_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.SET_COLUMN_TOGGLE_MSG, selColumn.getName()));

        if (0 != returnType) {
            return;
        }

        StatusMessage statMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_SET_COLUMN_NOTNULL));
        setStatusMessage(statMessage);
        StatusMessageList.getInstance().push(statMessage);
        final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (null != bottomStatusBar) {

            bottomStatusBar.activateStatusbar();
        }
        SetColumnNotNullWorkerJob workerJob = new SetColumnNotNullWorkerJob(selColumn, schemaName);
        workerJob.schedule();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ColumnMetaData selColumn1 = IHandlerUtilities.getSelectedColumn();
        if (null != selColumn1) {

            if (selColumn1.getParentTable() instanceof ForeignTable) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMessageSetColumnNotNullHandler;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMessageSetColumnNotNullHandler = statMessage;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SetColumnNotNullWorkerJob.
     */
    private final class SetColumnNotNullWorkerJob extends UIWorkerJob {

        private TableMetaData table;
        private ColumnMetaData selColumn;
        private String schemaName;
        private TerminalExecutionConnectionInfra conn;

        /**
         * Instantiates a new sets the column not null worker job.
         *
         * @param selColumn the sel column
         * @param name the name
         */
        public SetColumnNotNullWorkerJob(ColumnMetaData selColumn, String name) {
            super("Set Column Not Null", null);
            this.selColumn = selColumn;
            this.table = selColumn.getParentTable();
            this.schemaName = table.getNamespace().getName();
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            conn = PromptPrdGetConnection.getConnection(selColumn.getDatabase());
            selColumn.execAlterToggleSetNull(conn.getConnection());
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            UIElement.getInstance().toggleSetColumnNotNullCheck(selColumn.isNotNull());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.SET_COLUMN_NOT_NULL_SUCCESSFUL,
                            schemaName, table.getName(), selColumn.getName())));
        }

        @Override
        public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {

            handleExceptions(exception);
        }

        /**
         * Handle exceptions.
         *
         * @param exception the e
         */
        private void handleExceptions(MPPDBIDEException exception) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.SET_COLUMN_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.SET_COLUMN_UNABLE,
                            MPPDBIDEConstants.LINE_SEPARATOR, exception.getServerMessage()));
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.SET_COLUMN_FAIL,
                            schemaName, table.getName(), selColumn.getName())));
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {

            handleExceptions(dbCriticalException);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {

            handleExceptions(dbOperationException);
        }

        @Override
        public void finalCleanup() throws MPPDBIDEException {
            if (this.conn != null) {
                this.conn.releaseConnection();
            }
        }

        @Override
        public void finalCleanupUI() {

            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(getStatusMessage());
            }
        }

    }
}
