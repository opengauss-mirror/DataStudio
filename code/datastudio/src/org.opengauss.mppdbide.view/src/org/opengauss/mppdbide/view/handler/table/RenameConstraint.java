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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
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
import org.opengauss.mppdbide.view.handler.UserInputDialogUIWorkerJob;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserInputDialog;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameConstraint.
 *
 * @since 3.0.0
 */
public class RenameConstraint {
    private StatusMessage statusMessage;
    private RenameConstraintWorker renameConstraintWorker;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final ConstraintMetaData constraintMetaData = IHandlerUtilities.getSelectedConstraint();
        UserInputDialog renameDbDialog = new RenameConstraintInner(shell, constraintMetaData, constraintMetaData);

        renameDbDialog.open();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ConstraintMetaData constraintMetaData = IHandlerUtilities.getSelectedConstraint();
        if (null == constraintMetaData) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameConstraintInner.
     */
    private final class RenameConstraintInner extends UserInputDialog {

        /**
         * Instantiates a new rename constraint inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param constraintMetaData the constraint meta data
         */
        private RenameConstraintInner(Shell parent, Object serverObject, ConstraintMetaData constraintMetaData) {
            super(parent, serverObject);
        }

        @Override
        public void performOkOperation() {
            renameConstraintWorker = null;

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            final ConstraintMetaData constrtMetaData = (ConstraintMetaData) getObject();
            StatusMessage statMsg = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_RENAME_CONSTRAINT));

            String oldConstraintName = constrtMetaData.getName();
            String userInput = getUserInput();

            if ("".equals(userInput)) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_CONSTRAINT_NEW_NAME,
                        oldConstraintName), false);
                if (null != bottomStatusBar) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                enableButtons();
                return;
            }

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_CONSTRAINT_WAIT, oldConstraintName),
                    true);

            renameConstraintWorker = new RenameConstraintWorker(constrtMetaData, userInput, this, statMsg);
            setStatusMessage(statMsg);
            setStatusMessage(statMsg);
            StatusMessageList.getInstance().push(statMsg);
            if (null != bottomStatusBar) {
                bottomStatusBar.activateStatusbar();
                renameConstraintWorker.schedule();
                enableCancelButton();
            }
        }

        @Override
        protected void cancelPressed() {
            performCancelOperation();
        }

        @Override
        protected void performCancelOperation() {
            if (renameConstraintWorker != null && renameConstraintWorker.getState() == Job.RUNNING) {
                int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG));

                if (0 == returnValue) {
                    renameConstraintWorker.cancelJob();
                    renameConstraintWorker = null;
                } else {
                    enableCancelButton();
                }
            } else {
                close();
            }

        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_CONSTRAINT_TITLE);
        }

        @Override
        protected String getHeader() {
            ConstraintMetaData db = (ConstraintMetaData) getObject();

            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_CONSTRAINT_NEW_NAME, db.getName());
        }


        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            return;
        }

        @Override
        public void onPresetupFailureUIAction(MPPDBIDEException exception) {
            return;
        }

        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_CONSTRAINTS, this.getClass());
        }
        
        @Override
        public void onSuccessUIAction(Object obj) {
            return;
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            return;
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameConstraintWorker.
     */
    private static final class RenameConstraintWorker extends UserInputDialogUIWorkerJob {

        private ConstraintMetaData cnstrntMetaData;
        private String newname;

        /**
         * Instantiates a new rename constraint worker.
         *
         * @param constraintMetaData the constraint meta data
         * @param nwname the nwname
         * @param dialog the dialog
         * @param statusMessage the status message
         */
        private RenameConstraintWorker(ConstraintMetaData constraintMetaData, String nwname, UserInputDialog dialog,
                StatusMessage statusMessage) {
            super("Rename Constraint", null, dialog, statusMessage, constraintMetaData.getName(),
                    IMessagesConstants.RENAME_CONSTRAINT_ERROR, IMessagesConstants.RENAME_CONSTRAINT_CONN_ERROR);
            this.cnstrntMetaData = constraintMetaData;
            this.newname = nwname;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            setConnInfra(PromptPrdGetConnection.getConnection(cnstrntMetaData.getDatabase()));
            cnstrntMetaData.execRenameConstraint(newname, getConnInfra().getConnection());
            MPPDBIDELoggerUtility.info("Rename constraint succesfull");
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            String message = MessageConfigLoader.getProperty(IMessagesConstants.RENAME_CONSTRAINT_RENAMED, oldname,
                    cnstrntMetaData.getName());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObject(cnstrntMetaData.getParent());
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
