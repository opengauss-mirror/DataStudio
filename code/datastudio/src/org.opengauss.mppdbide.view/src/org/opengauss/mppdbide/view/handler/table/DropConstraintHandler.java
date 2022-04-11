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

import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
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
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropConstraintHandler.
 *
 * @since 3.0.0
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
