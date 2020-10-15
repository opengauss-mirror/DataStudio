/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.utils.IMessagesConstants;
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
import com.huawei.mppdbide.view.handler.UserInputDialogUIWorkerJob;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameConstraint.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
