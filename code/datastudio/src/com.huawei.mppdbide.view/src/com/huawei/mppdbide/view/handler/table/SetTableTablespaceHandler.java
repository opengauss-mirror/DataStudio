/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableOrientation;
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
import com.huawei.mppdbide.view.ui.connectiondialog.UserComboDialog;
import com.huawei.mppdbide.view.ui.table.UIUtils;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetTableTablespaceHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
