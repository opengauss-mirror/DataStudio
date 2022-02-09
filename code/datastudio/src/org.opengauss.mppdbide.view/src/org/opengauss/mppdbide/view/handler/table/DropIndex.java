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

import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
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
 * Description: The Class DropIndex.
 *
 * @since 3.0.0
 */
public class DropIndex {
    private StatusMessage statusMessageDropIndex;

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        final IndexMetaData index = IHandlerUtilities.getSelectedIndex();
        if (index != null) {
            int returnType = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_INDEX_DIA_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_INDEX, index.getName()));

            if (0 != returnType) {
                return;
            }

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            StatusMessage statMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DROP_INDEX));
            DropIndexWorker dropIndexjob = new DropIndexWorker(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DROP_INDEX), null, index,
                    statMessage);
            setStatusMessage(statMessage);
            StatusMessageList.getInstance().push(statMessage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            dropIndexjob.schedule();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {

        IndexMetaData index = IHandlerUtilities.getSelectedIndex();
        if (index == null) {
            return false;
        }
        return true;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DropIndexWorker.
     */
    private static final class DropIndexWorker extends UIWorkerJob {
        private IndexMetaData index;
        private StatusMessage staMsg;
        private TerminalExecutionConnectionInfra conn;

        /**
         * Instantiates a new drop index worker.
         *
         * @param name the name
         * @param family the family
         * @param index the index
         * @param statusMsg the status msg
         */
        private DropIndexWorker(String name, Object family, IndexMetaData index, StatusMessage statusMsg) {
            super(name, family);
            this.index = index;
            this.staMsg = statusMsg;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            conn = PromptPrdGetConnection.getConnection(index.getDatabase());
            index.drop(conn.getConnection());
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.setSelection(index.getParent());
                objectBrowserModel.refreshObject(index.getParent());
            }
            String message = MessageConfigLoader.getProperty(IMessagesConstants.DROP_INDEX_SUCCESS,
                    index.getParent().getDisplayName(), index.getName());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));

        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_INDEX_ERROR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_INDEX_CONN_FAIL,
                            index.getTable().getNamespace().getName(), index.getTable().getName(), index.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getError(MessageConfigLoader.getProperty(
                            IMessagesConstants.DROP_INDEX_CONN_FAIL, index.getTable().getNamespace().getName(),
                            index.getTable().getName(), index.getName())));

        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_INDEX_ERROR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_INDEX_ERROR_MSG,
                            index.getTable().getNamespace().getName(), index.getTable().getName(), index.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getError(MessageConfigLoader.getProperty(
                            IMessagesConstants.DROP_INDEX_ERROR_MSG, index.getTable().getNamespace().getName(),
                            index.getTable().getName(), index.getName())));

        }

        @Override
        public void finalCleanup() throws MPPDBIDEException {
            if (this.conn != null) {
                this.conn.releaseConnection();
            }
        }

        @Override
        public void finalCleanupUI() {
            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bottomStatusBar != null) {
                bottomStatusBar.hideStatusbar(this.staMsg);
            }

        }
    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMessageDropIndex;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMessageDropIndex = statMessage;
    }
}
