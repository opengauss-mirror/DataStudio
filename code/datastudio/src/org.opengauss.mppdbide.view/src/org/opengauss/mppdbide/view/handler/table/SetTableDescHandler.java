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

import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
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
import org.opengauss.mppdbide.view.handler.UserInputDialogUIWorkerJob;
import org.opengauss.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserInputDialog;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetTableDescHandler.
 *
 * @since 3.0.0
 */
public class SetTableDescHandler {
    private StatusMessage statusMessage;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final TableMetaData selTable = IHandlerUtilities.getSelectedTable();
        if (selTable != null) {
            UserInputDialog renameDbDialog = new SetTableDescHandlerInner(shell, selTable, selTable);
            renameDbDialog.open();
        }

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        TableMetaData selTable = IHandlerUtilities.getSelectedTable();
        if (null == selTable) {
            return false;
        }
        return !IHandlerUtilities.isSelectedTableForignPartition();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SetTableDescHandlerInner.
     */
    private final class SetTableDescHandlerInner extends UserInputDialog {
        private final TableMetaData selctTable;
        private String olddesc;

        /**
         * Instantiates a new sets the table desc handler inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param selTable the sel table
         */
        private SetTableDescHandlerInner(Shell parent, Object serverObject, TableMetaData selTable) {
            super(parent, serverObject);
            this.selctTable = selTable;
            olddesc = null == selTable.getDescription() ? "" : selTable.getDescription();
        }

        @Override
        public void performOkOperation() {
            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            TableMetaData table = (TableMetaData) getObject();
            StatusMessage statMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_SET_TBL_DESC));

            String userInput = getUserInput();

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLE_UPDATE_DESC,
                    table.getNamespace().getName(), table.getName()), true);

            SetTableDescHandlerWorker setTableDescHandlerWorker = new SetTableDescHandlerWorker(table, userInput, this,
                    statMessage);
            setStatusMessage(statMessage);
            StatusMessageList.getInstance().push(statMessage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            setTableDescHandlerWorker.schedule();
        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLE_DESC_TITLE);
        }

        @Override
        protected String getHeader() {
            return MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLE_NEW_DESC_FOR,
                    selctTable.getNamespace().getName(), selctTable.getName(), MPPDBIDEConstants.LINE_SEPARATOR,
                    olddesc);
        }

        @Override
        protected String getInitialText() {
            if (null == selctTable.getDescription()) {
                return "";
            }
            return selctTable.getDescription();
        }

        @Override
        protected boolean isSetTableDescription() {
            return true;
        }

        @Override
        protected boolean forTableDescription() {
            return true;
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
        public void onSuccessUIAction(Object obj) {
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
     * Description: The Class SetTableDescHandlerWorker.
     */
    private static final class SetTableDescHandlerWorker extends UserInputDialogUIWorkerJob {

        private TableMetaData table;
        private String newname;

        /**
         * Instantiates a new sets the table desc handler worker.
         *
         * @param table the table
         * @param nwname the nwname
         * @param dialog the dialog
         * @param statusMessage the status message
         */
        private SetTableDescHandlerWorker(TableMetaData table, String nwname, UserInputDialog dialog,
                StatusMessage statusMessage) {
            super("Set Table Desc", null, dialog, statusMessage, table.getName(),
                    IMessagesConstants.SET_TABLE_UPDATE_ERROR, IMessagesConstants.SET_TABLE_CONN_ERROR);
            this.table = table;
            this.newname = nwname;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            setConnInfra(PromptPrdGetConnection.getConnection(table.getDatabase()));
            table.execSetTableDescription(newname, getConnInfra().getConnection());
            table.refresh(getConnInfra().getConnection());
            MPPDBIDELoggerUtility.info("Table description successfully updated.");
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLE_DESC_SUCCES,
                            table.getNamespace().getName(), table.getName())));
            dialog.close();
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
