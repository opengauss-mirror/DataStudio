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

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ForeignTable;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserInputDialog;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameColumnHandler.
 *
 * @since 3.0.0
 */
public class RenameColumnHandler {

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameColumnHandlerInner.
     */
    private final class RenameColumnHandlerInner extends UserInputDialog {
        private String oldColumnName;
        private String userInput;
        private ColumnMetaData column;

        /**
         * Instantiates a new rename column handler inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         */
        private RenameColumnHandlerInner(Shell parent, Object serverObject) {
            super(parent, serverObject);
        }

        @Override
        public void performOkOperation() {
            worker = null;
            column = (ColumnMetaData) getObject();

            oldColumnName = MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_OLD_NAME, column.getName(),
                    column.getParentTable().getNamespace().getName(), column.getParentTable().getName());
            userInput = getUserInput();

            if ("".equals(userInput)) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_COLUMN_NEW, oldColumnName),
                        false);

                enableButtons();
                return;
            }
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForColumn(column.getName(),
                    column.getParentTable().getName(), column.getParentTable().getNamespace().getName(),
                    column.getDatabase().getName(), column.getDatabase().getServerName(),
                    IMessagesConstants.RENAME_COLUMN_PROGRESS_NAME);
            worker = new RenameColumnWorker(progressLabel, column, oldColumnName, userInput,
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_RENAME_COLUMN), this);
            worker.schedule();
            enableCancelButton();
        }

        @Override
        protected void cancelPressed() {
            performCancelOperation();
        }

        @Override
        protected void performCancelOperation() {
            if (worker != null && worker.getState() == Job.RUNNING) {
                int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG));

                if (0 == returnValue) {
                    worker.cancelJob();
                    worker = null;
                } else {
                    enableCancelButton();
                }
            } else {
                close();
            }

        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_COLUMN_TITLE);
        }

        @Override
        protected String getHeader() {
            ColumnMetaData selColumn = (ColumnMetaData) getObject();

            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_COLUMN_NEW_NAME, selColumn.getName(),
                    selColumn.getParentTable().getNamespace().getName(), selColumn.getParentTable().getName());
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            close();
            String message = MessageConfigLoader.getProperty(IMessagesConstants.RENAME_COLUMN_RENAMED,
                    this.oldColumnName, this.userInput, column.getParentTable().getNamespace().getName(),
                    column.getParentTable().getName());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));

            IHandlerUtilities.pritnAndRefresh(column.getParentTable());
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
            printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_COLUMN_CONN_ERROR,
                    MPPDBIDEConstants.LINE_SEPARATOR, dbCriticalException.getDBErrorMessage()), false);
            enableButtons();
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            String clmErrMsg = dbOperationException.getServerMessage();
            if (null == clmErrMsg) {
                clmErrMsg = dbOperationException.getDBErrorMessage();
            }

            if (clmErrMsg.contains("Position:")) {
                clmErrMsg = clmErrMsg.split("Position:")[0];
            }

            printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_COLUMN_ERROR,
                    this.oldColumnName, MPPDBIDEConstants.LINE_SEPARATOR, clmErrMsg), false);
            enableButtons();
        }

        @Override
        public void onPresetupFailureUIAction(MPPDBIDEException exception) {
            enableButtons();
        }
        
        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_COLUMN, this.getClass());
        }
    }

    private RenameColumnWorker worker;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        ColumnMetaData selColumn = IHandlerUtilities.getSelectedColumn();

        UserInputDialog renameTableDialog = new RenameColumnHandlerInner(shell, selColumn);

        renameTableDialog.open();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ColumnMetaData columnMetaData = IHandlerUtilities.getSelectedColumn();

        if (null != columnMetaData && (columnMetaData.getParentTable() instanceof ForeignTable)) {
            return false;
        } else {
            return null != columnMetaData;
        }
    }

}
