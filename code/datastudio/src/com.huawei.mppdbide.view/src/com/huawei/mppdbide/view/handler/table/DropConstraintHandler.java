/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
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
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropConstraintHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DropConstraintHandler {
    private StatusMessage statusMessage;

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        ConstraintMetaData selConstraint = IHandlerUtilities.getSelectedConstraint();
        if (null != selConstraint) {
            int returnType = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_CONSTRAINT_DIA_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_CONSTRAINT_FROM, selConstraint.getName(),
                            selConstraint.getTable().getNamespace().getName(), selConstraint.getTable().getName()));

            if (returnType != 0) {
                return;
            }

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            StatusMessage statMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DROP_CONSTRAINT));
            DropConstraintWorker dropConstraint = new DropConstraintWorker("Drop Constraint",
                    MPPDBIDEConstants.CANCELABLEJOB, selConstraint, statMessage, bottomStatusBar);
            setStatusMessage(statMessage);
            StatusMessageList.getInstance().push(statMessage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            dropConstraint.schedule();

        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ConstraintMetaData selConstrnt = IHandlerUtilities.getSelectedConstraint();
        if (null == selConstrnt) {
            return false;
        } else {
            return true;
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

    /**
     * 
     * Title: class
     * 
     * Description: The Class DropConstraintWorker.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class DropConstraintWorker extends UIWorkerJob {
        private ConstraintMetaData selConstraint;
        private StatusMessage staMsg;
        private BottomStatusBar bottomStatusBar;
        private TerminalExecutionConnectionInfra connInfra;

        /**
         * Instantiates a new drop constraint worker.
         *
         * @param name the name
         * @param family the family
         * @param selConstraint the sel constraint
         * @param statusMsg the status msg
         * @param bottomStatusBar the bottom status bar
         */
        private DropConstraintWorker(String name, Object family, ConstraintMetaData selConstraint,
                StatusMessage statusMsg, BottomStatusBar bottomStatusBar) {
            super(name, family);
            this.selConstraint = selConstraint;
            this.staMsg = statusMsg;
            this.bottomStatusBar = bottomStatusBar;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            Database db = selConstraint.getDatabase();
            connInfra = PromptPrdGetConnection.getConnection(db);
            selConstraint.execDrop(connInfra.getConnection());
            selConstraint.getTable().refresh(connInfra.getConnection());
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (null != objectBrowserModel) {

                objectBrowserModel.refreshObject(selConstraint.getParent());
            }

            String message = MessageConfigLoader.getProperty(IMessagesConstants.DROP_CONSTRAINT_DROPPING,
                    selConstraint.getTable().getNamespace().getName(), selConstraint.getTable().getName(),
                    selConstraint.getName());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(dbCriticalException,
                    selConstraint.getDatabase());

        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_CONSTRAINT_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_CONSTRAINT_UNABLE_TO_DROP,
                            selConstraint.getTable().getNamespace().getName(), selConstraint.getTable().getName(),
                            selConstraint.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.DROP_CONSTRAINT_UNABLE_TO_DROP,
                            selConstraint.getTable().getNamespace().getName(), selConstraint.getTable().getName(),
                            selConstraint.getName())));

        }

        @Override
        public void finalCleanup() throws MPPDBIDEException {
            if (this.connInfra != null) {
                this.connInfra.releaseConnection();
            }
        }

        @Override
        public void finalCleanupUI() {
            if (null != bottomStatusBar) {
                bottomStatusBar.hideStatusbar(this.staMsg);
            }

        }
    }
}
