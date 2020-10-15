/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
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
 * Description: The Class DropIndex.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
