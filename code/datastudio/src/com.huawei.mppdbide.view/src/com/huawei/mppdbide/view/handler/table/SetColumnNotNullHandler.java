/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
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
 * Description: The Class SetColumnNotNullHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
