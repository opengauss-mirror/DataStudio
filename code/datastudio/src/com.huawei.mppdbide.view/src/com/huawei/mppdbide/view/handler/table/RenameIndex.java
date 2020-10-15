/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
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
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameIndex.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
