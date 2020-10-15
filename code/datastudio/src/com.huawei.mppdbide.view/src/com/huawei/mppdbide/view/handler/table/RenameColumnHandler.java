/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameColumnHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class RenameColumnHandler {

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameColumnHandlerInner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
