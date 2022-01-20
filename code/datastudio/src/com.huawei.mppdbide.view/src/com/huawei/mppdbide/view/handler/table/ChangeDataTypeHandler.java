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
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.TableOrientation;
import com.huawei.mppdbide.bl.serverdatacache.TypeMetaData;
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
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.ui.table.ChangeDataTypeDialog;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ChangeDataTypeHandler.
 *
 * @since 3.0.0
 */
public class ChangeDataTypeHandler {
    private StatusMessage statusMessage;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final ColumnMetaData columnMetaData = IHandlerUtilities.getSelectedColumn();
        if (columnMetaData != null) {
            ChangeDataTypeDialog changeDataTypeDialog = new ChangeDataTypeDialog(shell, columnMetaData.getParentTable(),
                    columnMetaData) {
                private ChangeDataTypeWorker workerJob;
                private int scale = columnMetaData.getScale();
                private int lenOrPrecision = columnMetaData.getLenOrPrecision();

                @Override
                public void performOkButtonPressed() {
                    final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
                    setStatusMessage();
                    if (null != columnMetaData.getDataType()) {
                        TypeMetaData existingDt = columnMetaData.getDataType();
                        getDBColumn();
                        workerJob = new ChangeDataTypeWorker(existingDt, columnMetaData, this, statusMessage, scale,
                                lenOrPrecision);
                        workerJob.setTaskDB(columnMetaData.getParentDB());
                        setStatusMessage(statusMessage);
                        StatusMessageList.getInstance().push(statusMessage);
                        if (bttmStatusBar != null) {
                            bttmStatusBar.activateStatusbar();
                        }
                        workerJob.schedule();
                        disableOKButton();
                    }
                }

                @Override
                public void cancelPressed() {
                    if (null != workerJob && workerJob.getState() == Job.RUNNING) {
                        int returnValue = getReturnValueFromDialog();
                        if (0 == returnValue) {
                            workerJob.canceling();
                            workerJob = null;
                        } else {
                            enableCancelButton();
                        }
                    } else {
                        close();
                    }
                }
            };
            changeDataTypeDialog.open();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ColumnMetaData columnMetaData = IHandlerUtilities.getSelectedColumn();

        if (null != columnMetaData && (columnMetaData.getParentTable() instanceof ForeignTable)) {
            return false;
        } else {
            return null != columnMetaData;
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
     * @param statusMessage the new status message
     */
    public void setStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Gets the return value from dialog.
     *
     * @return the return value from dialog
     */
    private int getReturnValueFromDialog() {
        int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG));
        return returnValue;
    }

    /**
     * Sets the status message.
     */
    private void setStatusMessage() {
        StatusMessage statMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CHANGE_DATATYPE));
        this.statusMessage = statMessage;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ChangeDataTypeWorker.
     */
    private static final class ChangeDataTypeWorker extends UIWorkerJob {

        private TypeMetaData existingDatatype;
        private ColumnMetaData columnData;
        private StatusMessage statMessage;
        private ChangeDataTypeDialog dialog;
        private int existingScaleValue;
        private int exisitingPrecisionValue;
        private TerminalExecutionConnectionInfra conn;

        /**
         * Instantiates a new change data type worker.
         *
         * @param exisingDatatype the exising datatype
         * @param columnData the column data
         * @param changeDataTypeDialog the change data type dialog
         * @param statMessage the stat message
         * @param scale the scale
         * @param lenOrPrecision the len or precision
         */
        private ChangeDataTypeWorker(TypeMetaData exisingDatatype, ColumnMetaData columnData,
                ChangeDataTypeDialog changeDataTypeDialog, StatusMessage statMessage, int scale, int lenOrPrecision) {
            super(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CHANGE_DATATYPE),
                    MPPDBIDEConstants.CANCELABLEJOB);
            this.existingDatatype = exisingDatatype;
            this.columnData = columnData;
            this.statMessage = statMessage;
            this.dialog = changeDataTypeDialog;
            this.exisitingPrecisionValue = lenOrPrecision;
            this.existingScaleValue = scale;
        }

        @Override
        public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, Exception {
            conn = PromptPrdGetConnection.getConnection(columnData.getDatabase());
            columnData.execChangeDataType(conn.getConnection());
            columnData.getParentTable().refresh(conn.getConnection());
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            String message = MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_DATA_TYPE_FOR_COL,
                    columnData.getParentTable().getNamespace().getName(), columnData.getParentTable().getName(),
                    columnData.getName());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObject(columnData.getParent());
            }
            if (!dialog.getParent().isDisposed()) {
                dialog.dispose();
            }
        }

        /**
         * On change data type failure.
         *
         * @param existingDt the existing dt
         * @param ex the ex
         */
        private void onChangeDataTypeFailure(TypeMetaData existingDt, MPPDBIDEException ex) {
            resetColumnValueOnFailureOrCancel(existingDt);
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_CHANGE_DTYPE), ex.getServerMessage());
            MPPDBIDELoggerUtility.error("Error while changing datatype..", ex);
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.UNABLE_TO_CHANGE_DTYPE_OF,
                            columnData.getParentTable().getNamespace().getName(), columnData.getParentTable().getName(),
                            columnData.getDisplayName())));
            if (!dialog.isDisposed()) {
                dialog.enableButton();
            }
        }

        /**
         * Reset column value on failure or cancel.
         *
         * @param existingDt the existing dt
         */
        private void resetColumnValueOnFailureOrCancel(TypeMetaData existingDt) {
            columnData.setDataType(existingDt);
            columnData.setLenOrPrecision(exisitingPrecisionValue);
            columnData.setScale(existingScaleValue);
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            onChangeDataTypeFailure(existingDatatype, e);

        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            if (e.getServerMessage() != null
                    && e.getServerMessage().contains("canceling statement due to user request")) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));
                return;
            }
            onChangeDataTypeFailure(existingDatatype, e);

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
                bttmStatusBar.hideStatusbar(this.statMessage);
            }

        }

        @Override
        protected void canceling() {

            super.canceling();
            this.setName(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CHANGE_DATATYPE));
            try {
                conn.getConnection().cancelQuery();
                columnData.setDataType(existingDatatype);
                columnData.setScale(existingScaleValue);
                columnData.setLenOrPrecision(exisitingPrecisionValue);
            } catch (DatabaseCriticalException e) {
                onChangeDataTypeFailure(existingDatatype, e);
            } catch (DatabaseOperationException e) {
                onChangeDataTypeFailure(existingDatatype, e);
            }

        }

    }
}
