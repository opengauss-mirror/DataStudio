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

import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
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
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserInputDialog;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameIndex.
 *
 * @since 3.0.0
 */
public class RenameIndex {
    private StatusMessage statusMessage;
    private RenameIndexWorker renameIndexWorker;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof IndexMetaData) {
            final IndexMetaData index = (IndexMetaData) obj;

            UserInputDialog input = new RenameIndexInner(shell, index, index);

            input.open();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        IndexMetaData idx = IHandlerUtilities.getSelectedIndex();

        if (idx == null) {
            return false;
        }
        return true;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameIndexInner.
     */
    private final class RenameIndexInner extends UserInputDialog {
        private IndexMetaData idex;

        /**
         * Instantiates a new rename index inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param index the index
         */
        private RenameIndexInner(Shell parent, Object serverObject, IndexMetaData index) {
            super(parent, serverObject);
            idex = index;
        }

        @Override
        public void performOkOperation() {
            renameIndexWorker = null;

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            idex = (IndexMetaData) getObject();
            StatusMessage statMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_RENAME_INDEX));

            String oldIndexName = idex.getName();
            String userInput = getUserInput();

            if ("".equals(userInput)) {
                printErrorMessage(
                        MessageConfigLoader.getProperty(IMessagesConstants.RENAME_INDEX_NEW_NAME, oldIndexName), false);
                if (bottomStatusBar != null) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                enableButtons();
                return;
            }

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_INDEX_WAIT, oldIndexName), true);

            renameIndexWorker = new RenameIndexWorker(idex, userInput, this, statMessage);
            setStatusMessage(statMessage);
            StatusMessageList.getInstance().push(statMessage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            renameIndexWorker.schedule();
            enableCancelButton();
        }

        @Override
        protected void cancelPressed() {
            performCancelOperation();
        }

        @Override
        protected void performCancelOperation() {
            if (renameIndexWorker != null && renameIndexWorker.getState() == Job.RUNNING) {
                int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG));

                if (0 == returnValue) {
                    renameIndexWorker.cancelJob();
                    renameIndexWorker = null;
                } else {
                    enableCancelButton();
                }
            } else {
                close();
            }

        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_INDEX_TITLE);
        }

        @Override
        protected String getHeader() {
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_INDEX_NEW, idex.getName());

        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {

        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {

        }

        @Override
        public void onPresetupFailureUIAction(MPPDBIDEException exception) {

        }

        @Override
        public void onSuccessUIAction(Object obj) {

        }

        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_INDEX, this.getClass());
        }
        
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameIndexWorker.
     */
    private static final class RenameIndexWorker extends UIWorkerJob {

        private IndexMetaData idx;
        private String oldname;
        private String newname;
        private UserInputDialog dialog;
        private StatusMessage statusMsg;
        private TerminalExecutionConnectionInfra conn;

        /**
         * Instantiates a new rename index worker.
         *
         * @param idx the idx
         * @param nwname the nwname
         * @param dialog the dialog
         * @param statusMessage the status message
         */
        private RenameIndexWorker(IndexMetaData idx, String nwname, UserInputDialog dialog,
                StatusMessage statusMessage) {
            super("Rename Index", null);
            this.idx = idx;
            this.dialog = dialog;
            this.oldname = idx.getName();
            this.newname = nwname;
            this.statusMsg = statusMessage;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            conn = PromptPrdGetConnection.getConnection(idx.getDatabase());
            idx.rename(newname, conn.getConnection());
            MPPDBIDELoggerUtility.info("Rename Index succesful ");
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            String message = MessageConfigLoader.getProperty(IMessagesConstants.RENAME_INDEX_RENAMED, oldname,
                    idx.getName());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObject(idx.getParent());
            }
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
            dialog.printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_INDEX_CONN_ERROR,
                    MPPDBIDEConstants.LINE_SEPARATOR, dbCriticalException.getDBErrorMessage()), false);
            dialog.enableButtons();
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            String indexErrMsg = dbOperationException.getServerMessage();
            if (null == indexErrMsg) {
                indexErrMsg = dbOperationException.getDBErrorMessage();
            }
            // Bala issue List #12 start
            if (indexErrMsg.contains("Position:")) {
                indexErrMsg = indexErrMsg.split("Position:")[0];
            }

            dialog.printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_INDEX_ERROR, oldname,
                    MPPDBIDEConstants.LINE_SEPARATOR, indexErrMsg), false);

            dialog.enableButtons();
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
                bttmStatusBar.hideStatusbar(this.statusMsg);
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
     * @param statusMessage the new status message
     */
    public void setStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }
}
