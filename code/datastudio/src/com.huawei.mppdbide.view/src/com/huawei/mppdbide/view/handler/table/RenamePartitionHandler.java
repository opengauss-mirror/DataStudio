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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ForeignPartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.PartitionMetaData;
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
 * Description: The Class RenamePartitionHandler.
 *
 * @since 3.0.0
 */
public class RenamePartitionHandler {
    private StatusMessage statusMessage;
    private RenamePartitionTableWorker renamePartitionTableWorker;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {

        final PartitionMetaData partitionMetaData = IHandlerUtilities.getSelectedPartitionMetadata();
        UserInputDialog userInputDialog = new RenamePartitionHandlerInner(shell, partitionMetaData, partitionMetaData);
        userInputDialog.open();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenamePartitionHandlerInner.
     */
    private final class RenamePartitionHandlerInner extends UserInputDialog {

        /**
         * Instantiates a new rename partition handler inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param partitionMetaData the partition meta data
         */
        private RenamePartitionHandlerInner(Shell parent, Object serverObject, PartitionMetaData partitionMetaData) {
            super(parent, serverObject);
        }

        @Override
        protected void performOkOperation() {
            renamePartitionTableWorker = null;

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            final PartitionMetaData pmd = (PartitionMetaData) getObject();
            StatusMessage statMssage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_RENAME_PARTITION_TABLE));

            String userInput = getUserInput();

            if ("".equals(userInput)) {
                printErrorMessage(
                        MessageConfigLoader.getProperty(IMessagesConstants.RENAME_PARTITION_TABLE_NEW, pmd.getName()),
                        false);
                if (null != bottomStatusBar) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                enableButtons();
                return;
            }

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_PARTITION_TABLE_WAIT, pmd.getName()),
                    true);

            renamePartitionTableWorker = new RenamePartitionTableWorker(pmd, userInput, this, statMssage);
            setStatusMessage(statMssage);
            StatusMessageList.getInstance().push(statMssage);
            if (null != bottomStatusBar) {
                bottomStatusBar.activateStatusbar();
            }
            renamePartitionTableWorker.schedule();
            enableCancelButton();
        }

        @Override
        protected void cancelPressed() {
            performCancelOperation();
        }

        @Override
        protected void performCancelOperation() {
            if (renamePartitionTableWorker != null && renamePartitionTableWorker.getState() == Job.RUNNING) {
                int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG));

                if (0 == returnValue) {
                    renamePartitionTableWorker.cancelJob();
                    renamePartitionTableWorker = null;
                } else {
                    enableCancelButton();
                }
            } else {
                close();
            }

        }

        @Override
        protected String getWindowTitle() {
            // to be changed
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_PARTITION_TITLE);
        }

        @Override
        protected String getHeader() {

            PartitionMetaData pmd = (PartitionMetaData) getObject();

            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_PARTITION_TABLE_NEW, pmd.getName(),
                    pmd.getParent().getDisplayName());

        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            return;
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            return;
        }

        @Override
        public void onPresetupFailureUIAction(MPPDBIDEException exception) {
            return;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            return;
        }

        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.PARTITION_TABLE, this.getClass());
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        PartitionMetaData partitionMetaData = IHandlerUtilities.getSelectedPartitionMetadata();
        if (null == partitionMetaData) {
            return false;
        } else {
            return !(partitionMetaData.getParent() instanceof ForeignPartitionTable);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenamePartitionTableWorker.
     */
    private static final class RenamePartitionTableWorker extends UserInputDialogUIWorkerJob {

        private PartitionMetaData parttnMetaData;
        private String newname;

        /**
         * Instantiates a new rename partition table worker.
         *
         * @param pmd the pmd
         * @param nwname the nwname
         * @param dialog the dialog
         * @param statusMessage the status message
         */
        private RenamePartitionTableWorker(PartitionMetaData pmd, String nwname, UserInputDialog dialog,
                StatusMessage statusMessage) {
            super("Rename Partition Table", null, dialog, statusMessage, pmd.getName(),
                    IMessagesConstants.RENAME_TABLE_ERROR, IMessagesConstants.RENAME_TABLE_CONN_ERROR);
            this.parttnMetaData = pmd;
            this.newname = nwname;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            setConnInfra(PromptPrdGetConnection.getConnection(parttnMetaData.getDatabase()));
            parttnMetaData.execRename(this.newname, getConnInfra().getConnection());

            MPPDBIDELoggerUtility.info("Rename partitiontable succesful");
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            String schemaname = parttnMetaData.getParent().getNamespace().getName();
            String tablename = parttnMetaData.getParent().getName();
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_PARTITION_TABLE_TO,
                            oldname, schemaname, tablename, newname)));
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObject(parttnMetaData.getParent());
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
