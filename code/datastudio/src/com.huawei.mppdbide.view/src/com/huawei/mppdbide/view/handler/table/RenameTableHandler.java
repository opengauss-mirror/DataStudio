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

import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableOrientation;
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
 * Description: The Class RenameTableHandler.
 *
 * @since 3.0.0
 */
public class RenameTableHandler {
    private StatusMessage statusMessage;
    private RenameTableWorker renameTableworker;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final TableMetaData selTable = IHandlerUtilities.getSelectedTable();

        UserInputDialog renameTableDialog = new RenameTableHandlerInner(shell, selTable, selTable);

        renameTableDialog.open();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        TableMetaData selTabl = IHandlerUtilities.getSelectedTable();
        if (null == selTabl) {
            return false;
        } else {
            return !IHandlerUtilities.isSelectedTableForignPartition();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameTableHandlerInner.
     */
    private final class RenameTableHandlerInner extends UserInputDialog {
        private TableMetaData selectTable;

        /**
         * Instantiates a new rename table handler inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param selTable the sel table
         */
        private RenameTableHandlerInner(Shell parent, Object serverObject, TableMetaData selTable) {
            super(parent, serverObject);
        }

        @Override
        public void performOkOperation() {
            renameTableworker = null;

            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            selectTable = (TableMetaData) getObject();
            StatusMessage statMssage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_RENAME_TABLE));

            String userInput = getUserInput();
            if ("".equals(userInput)) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TABLE_NEW_NAME,
                        selectTable.getName()), false);
                if (null != bttmStatusBar) {
                    bttmStatusBar.hideStatusbar(getStatusMessage());
                }
                enableButtons();
                return;
            }

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TABLE_WAIT, selectTable.getName()),
                    true);

            renameTableworker = new RenameTableWorker(selectTable, userInput, this, statMssage);

            setStatusMessage(statMssage);
            StatusMessageList.getInstance().push(statMssage);
            if (null != bttmStatusBar) {
                bttmStatusBar.activateStatusbar();
            }
            renameTableworker.schedule();
            enableCancelButton();

        }

        @Override
        protected void cancelPressed() {
            performCancelOperation();
        }

        @Override
        protected void performCancelOperation() {
            if (renameTableworker != null && renameTableworker.getState() == Job.RUNNING) {
                int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG));

                if (0 == returnValue) {
                    renameTableworker.cancelJob();
                    renameTableworker = null;
                } else {
                    enableCancelButton();
                }
            } else {
                close();
            }

        }

        @Override
        protected String getWindowTitle() {
            if (selectTable instanceof PartitionTable) {
                return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_PARTITION_TABLE_TITLE);
            } else {
                return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TABLE_TITLE);
            }
        }

        @Override
        protected String getHeader() {
            TableMetaData selTbl = (TableMetaData) getObject();

            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TABLE_NEW_NAME,
                    selTbl.getNamespace().getDisplayName() + '.' + selTbl.getName());

        }

        @Override
        public void onSuccessUIAction(Object obj) {
            return;
        }

        @Override
        public void onPresetupFailureUIAction(MPPDBIDEException exception) {
            return;
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            return;
        }
        
        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            return;
        }

        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_TABLE, this.getClass());
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameTableWorker.
     */
    private static final class RenameTableWorker extends UserInputDialogUIWorkerJob {
        private TableMetaData table;
        private String newName;

        /**
         * Instantiates a new rename table worker.
         *
         * @param obj the obj
         * @param newName the new name
         * @param dialog the dialog
         * @param statusMsg the status msg
         */
        private RenameTableWorker(TableMetaData obj, String newName, UserInputDialog dialog, StatusMessage statusMsg) {
            super("Rename Table", null, dialog, statusMsg, obj.getName(), IMessagesConstants.RENAME_TABLE_ERROR,
                    IMessagesConstants.RENAME_TABLE_CONN_ERROR);
            this.table = obj;
            this.newName = newName;
        }

        @Override
        public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException {
            setConnInfra(PromptPrdGetConnection.getConnection(table.getDatabase()));
            table.getNamespace().execRenameTable(table, this.newName, getConnInfra().getConnection());
            MPPDBIDELoggerUtility.info("Rename table succesfull ");

            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            String schemaname = table.getNamespace().getName();
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(
                    MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TABLE_TO, oldname, schemaname, newName)));
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObject(table);
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
