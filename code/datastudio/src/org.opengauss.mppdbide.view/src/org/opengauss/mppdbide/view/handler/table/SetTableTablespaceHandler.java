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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TableOrientation;
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
import org.opengauss.mppdbide.view.ui.connectiondialog.UserComboDialog;
import org.opengauss.mppdbide.view.ui.table.UIUtils;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetTableTablespaceHandler.
 *
 * @since 3.0.0
 */
public class SetTableTablespaceHandler {
    private StatusMessage statusMessage;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final TableMetaData selTable = IHandlerUtilities.getSelectedTable();

        UserComboDialog setTablespaceDialog = new SetTableTablespaceHandlerInner(shell, selTable, selTable, shell);

        setTablespaceDialog.open();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        TableMetaData selTab = IHandlerUtilities.getSelectedTable();
        return null != selTab;
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
     * Description: The Class SetTableTablespaceHandlerInner.
     */
    private final class SetTableTablespaceHandlerInner extends UserComboDialog {
        private final Shell shell;

        /**
         * Instantiates a new sets the table tablespace handler inner.
         *
         * @param prnt the prnt
         * @param serverObject the server object
         * @param selTable the sel table
         * @param shell the shell
         */
        private SetTableTablespaceHandlerInner(Shell prnt, Object serverObject, TableMetaData selTable, Shell shell) {
            super(prnt, serverObject);
            this.shell = shell;
        }
        
        @Override
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);
            newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_TABLESPACE, this.getClass()));
        }

        @Override
        protected void performOkOperation() {
            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            TableMetaData tableMetaData = (TableMetaData) getObject();
            String oldTablespaceName = null;
            try {
                oldTablespaceName = tableMetaData.getTablespaceForTable(null);
                if (oldTablespaceName == null) {
                    oldTablespaceName = tableMetaData.getDatabase().getDBDefaultTblSpc();
                }
            } catch (DatabaseCriticalException | DatabaseOperationException dbException) {
                oldTablespaceName = "";
            }
            String userInput = getUserInput();

            if ("".equals(userInput)) {

                printErrorMessage(
                        MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_NEW, oldTablespaceName));
                if (null != bottomStatusBar) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                return;
            }

            if (tableMetaData.getTablespaceName() != null) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_FROM_CURRENT,
                        oldTablespaceName, tableMetaData.getTablespaceName(), userInput));
            } else {
                printMessage(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_FROM_CURRENT_NULL,
                        oldTablespaceName, userInput));
            }
            StatusMessage statMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_SET_TBLSPACE));
            setStatusMessage(statMessage);
            StatusMessageList.getInstance().push(statMessage);
            if (null != bottomStatusBar) {
                bottomStatusBar.activateStatusbar();
            }
            SeTablespaceWorkerJob workerJob = new SeTablespaceWorkerJob(tableMetaData, userInput, oldTablespaceName,
                    this, shell);
            workerJob.schedule();
            setOkButtonEnabled(false);

        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_TITLE);
        }

        @Override
        protected String getHeader() {
            TableMetaData selTbl = (TableMetaData) getObject();

            return MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_IN,
                    selTbl.getNamespace().getName(), selTbl.getName(),
                    selTbl.getTablespaceName() == null ? "" : selTbl.getTablespaceName());

        }

        @Override
        protected void comboDisplayValues(final Combo inputCombo) {
            TableMetaData selTabl = (TableMetaData) getObject();
            UIUtils.displayTablespaceListHandler(selTabl.getNamespace().getDatabase(), inputCombo, true);
            setOkButtonEnabled(true);

            inputCombo.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    boolean isSelected = inputCombo.getSelectionIndex() >= 0;
                    setOkButtonEnabled(isSelected);
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {

                }
            });
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SeTablespaceWorkerJob.
     */
    private final class SeTablespaceWorkerJob extends UIWorkerJob {

        private TableMetaData tableMetaData;
        private UserComboDialog userComboDialog;
        private String userInput;
        private String oldTablespaceName;
        TerminalExecutionConnectionInfra conn;

        /**
         * Instantiates a new se tablespace worker job.
         *
         * @param tableMetaData the table meta data
         * @param userInput the user input
         * @param oldTablespaceName the old tablespace name
         * @param userComboDialog the user combo dialog
         * @param shell the shell
         */
        public SeTablespaceWorkerJob(TableMetaData tableMetaData, String userInput, String oldTablespaceName,
                UserComboDialog userComboDialog, Shell shell) {

            super("Set Tablespace", null);
            this.tableMetaData = tableMetaData;
            this.userComboDialog = userComboDialog;
            this.userInput = userInput;
            this.oldTablespaceName = oldTablespaceName;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            conn = PromptPrdGetConnection.getConnection(tableMetaData.getDatabase());
            tableMetaData.execSetTableSpace(userInput, conn.getConnection());
            tableMetaData.refresh(conn.getConnection());
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {

            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObject(tableMetaData);
            }
            MPPDBIDELoggerUtility.info(
                    MessageConfigLoader.getProperty(IMessagesConstants.MOVING_TABLE_SELECTED_TABLESPACE_SUCCESFULL));
            userComboDialog.close();
            if (tableMetaData.getTablespaceName() != null) {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                        Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLE_FROM_TABLESPACE,
                                oldTablespaceName, tableMetaData.getTablespaceName(), userInput)));
            } else {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                        .getProperty(IMessagesConstants.SET_TABLE_FROM_TABLESPACE_NULL, tableMetaData.getName(), oldTablespaceName, userInput)));
            }
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {

            userComboDialog
                    .printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_CONN_ERROR,
                            MPPDBIDEConstants.LINE_SEPARATOR, dbCriticalException.getDBErrorMessage()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_FAIL)));
            userComboDialog.setOkButtonEnabled(true);
            return;
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {

            String msg = dbOperationException.getServerMessage();
            if (null == msg) {
                msg = dbOperationException.getDBErrorMessage();
            }
            if (tableMetaData.getTablespaceName() != null) {
                userComboDialog.printErrorMessage(
                        MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_ERROR, oldTablespaceName,
                                tableMetaData.getTablespaceName(), userInput, MPPDBIDEConstants.LINE_SEPARATOR, msg));
            } else {
                userComboDialog
                        .printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_ERROR_NULL,
                                oldTablespaceName, userInput, MPPDBIDEConstants.LINE_SEPARATOR, msg));
            }
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_FAIL)));
            userComboDialog.setOkButtonEnabled(true);
            return;
        }

        @Override
        public void finalCleanup() throws MPPDBIDEException {

            if (this.conn != null) {
                this.conn.releaseConnection();
            }
        }

        @Override
        public void finalCleanupUI() {

            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(getStatusMessage());
            }

        }

    }

}
