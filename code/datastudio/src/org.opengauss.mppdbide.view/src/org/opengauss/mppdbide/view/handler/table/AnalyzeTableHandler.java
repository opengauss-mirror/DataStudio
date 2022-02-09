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

import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
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
 * Description: The Class AnalyzeTableHandler.
 *
 * @since 3.0.0
 */
public class AnalyzeTableHandler {
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
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_ANALYSE_TABLE));
            AnalyzeTableHandlerWorker analyzejob = new AnalyzeTableHandlerWorker(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_ANALYSE_TABLE), null, selTable,
                    statusMsg);
            setStatusMessage(statusMsg);
            StatusMessageList.getInstance().push(statusMsg);

            if (null != bottomStatusBar) {

                bottomStatusBar.activateStatusbar();
            }
            analyzejob.schedule();
        }

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        TableMetaData selTabl = IHandlerUtilities.getSelectedTable();
        if (null == selTabl) {
            return false;
        }
        return !IHandlerUtilities.isSelectedTableForignPartition();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class AnalyzeTableHandlerWorker.
     */
    private static final class AnalyzeTableHandlerWorker extends UIWorkerJob {
        private TableMetaData selTable;
        private StatusMessage staMsg;
        private TerminalExecutionConnectionInfra conn;

        /**
         * Instantiates a new analyze table handler worker.
         *
         * @param name the name
         * @param family the family
         * @param selTable the sel table
         * @param statusMsg the status msg
         */
        private AnalyzeTableHandlerWorker(String name, Object family, TableMetaData selTable, StatusMessage statusMsg) {
            super(name, family);
            this.selTable = selTable;
            this.staMsg = statusMsg;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            conn = PromptPrdGetConnection.getConnection(selTable.getDatabase());
            selTable.execAnalyze(conn.getConnection());
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ANALYZING_TABLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.TABLE_ANALYZED,
                            selTable.getNamespace().getName(), selTable.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.TABLE_ANALYZED,
                            selTable.getNamespace().getName(), selTable.getName())));
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ANALYSE_TABLE_ERROR_MSG),
                    MessageConfigLoader.getProperty(IMessagesConstants.TABLE_ANALYZED_CONNECTION_ERROR,
                            selTable.getNamespace().getName(), selTable.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.TABLE_ANALYZED_CONNECTION_ERROR,
                            selTable.getNamespace().getName(), selTable.getName())));
            MPPDBIDELoggerUtility.error("Error while analyzing table..", exception);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ANALYSE_TABLE_ERROR_MSG),
                    MessageConfigLoader.getProperty(IMessagesConstants.TABLE_ANALYZED_ERROR,
                            selTable.getNamespace().getName(), selTable.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.TABLE_ANALYZED_ERROR,
                            selTable.getNamespace().getName(), selTable.getName())));
            MPPDBIDELoggerUtility.error("Error while analyzing table..", exception);
        }

        @Override
        public void finalCleanupUI() {
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(this.staMsg);
            }
        }

        @Override
        public void finalCleanup() {
            if (this.conn != null) {
                this.conn.releaseConnection();
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
