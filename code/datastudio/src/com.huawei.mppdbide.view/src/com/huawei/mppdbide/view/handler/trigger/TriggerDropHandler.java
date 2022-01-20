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

package com.huawei.mppdbide.view.handler.trigger;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.TriggerMetaData;
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
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * Title: class
 * Description: The Class TriggerDropHandler.
 *
 * @since 3.0.0
 */
public class TriggerDropHandler {
    private StatusMessage statusMsg;

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        TriggerMetaData selTrigger = IHandlerUtilities.getSelectedTriggerMetaData();
        if (selTrigger != null) {
            int returnValue = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_TRIGGER_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_TRIGGER,
                            selTrigger.getNamespace().getName(), selTrigger.getName()),
                    MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_YES),
                    MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_NO));
            if (returnValue != 0) {
                return;
            }
            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            StatusMessage statusMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DROP_TABLE));
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(selTrigger.getName(),
                    selTrigger.getNamespace().getName(),
                    selTrigger.getDatabase().getName(),
                    selTrigger.getNamespace().getServerName(),
                    IMessagesConstants.DROP_TABLE_PROGRESS_NAME);
            DropTriggerHandlerWorker dropjob = new DropTriggerHandlerWorker(
                    progressLabel, MPPDBIDEConstants.CANCELABLEJOB, selTrigger, statusMessage);
            setStatusMessage(statusMessage);
            StatusMessageList.getInstance().push(statusMessage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            dropjob.schedule();
        }
    }

    /**
     * Can execute.
     *
     * @return boolean true if can execute
     */
    @CanExecute
    public boolean canExecute() {
        TriggerMetaData trigger = TriggerUtils.getTrigger();
        if (trigger == null) {
            return false;
        }
        return true;
    }

    private static final class DropTriggerHandlerWorker extends UIWorkerJob {
        private TriggerMetaData selTrigger;
        private StatusMessage staMsg;
        private TerminalExecutionConnectionInfra conn;
        private JobCancelStatus cancelStatus = null;

        /**
         * Instantiates a new drop table handler worker.
         *
         * @param String the name
         * @param Object the family
         * @param TriggerMetaData the trigger metadata
         * @param StatusMessage the status msg
         */
        private DropTriggerHandlerWorker(String name, Object family,
                TriggerMetaData selTrigger, StatusMessage statusMsg) {
            super(name, family);
            this.selTrigger = selTrigger;
            this.staMsg = statusMsg;
            this.cancelStatus = new JobCancelStatus();
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            conn = PromptPrdGetConnection.getConnection(selTrigger.getNamespace().getDatabase());
            selTrigger.execDrop(conn.getConnection());
            selTrigger.getNamespace().loadTriggers(conn.getConnection());
            return selTrigger;
        }

        @Override
        protected void canceling() {
            super.canceling();
            try {
                conn.getConnection().cancelQuery();
                cancelStatus.setCancel(true);
            } catch (DatabaseCriticalException | DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("Error while cancelling query..", exception);
            }
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            if (cancelStatus.getCancel()) {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                        Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUREY_CANCEL_MSG)));
                return;
            }
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.setSelection(selTrigger.getParent());
                objectBrowserModel.refreshObject(selTrigger.getParent());
            }
            String message = MessageConfigLoader.getProperty(IMessagesConstants.DROP_TABLE_SUCCESS,
                    selTrigger.getNamespace().getName(), selTrigger.getName());
            IHandlerUtilities.pritnAndRefresh(selTrigger.getParent());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
            showErrorPopupMsg(dbCriticalException);
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(dbCriticalException,
                    selTrigger.getDatabase());
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            showErrorPopupMsg(dbOperationException);
        }

        @Override
        public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {
            super.onMPPDBIDEExceptionUIAction(exception);
            showErrorPopupMsg(exception);
        }

        /**
         * Show error popup msg.
         *
         * @param MPPDBIDEException the exception
         */
        private void showErrorPopupMsg(MPPDBIDEException exception) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_TABLE_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_TABLE_UNABLE,
                            MPPDBIDEConstants.LINE_SEPARATOR, exception.getServerMessage()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.DROP_TABLE_UNABLE_MSG,
                            selTrigger.getNamespace().getName(), selTrigger.getName())));
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
        return statusMsg;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMsg = statMessage;
    }

}
