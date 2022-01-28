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
 * Description: The Class SetSechemaHandler.
 *
 * @since 3.0.0
 */
public class SetSechemaHandler {
    private StatusMessage statusMessage;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final TableMetaData selTable = IHandlerUtilities.getSelectedTable();

        UserComboDialog setSchemaDialog = new SetSchemaHandlerInner(shell, selTable, selTable);

        setSchemaDialog.open();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        TableMetaData selectTable = IHandlerUtilities.getSelectedTable();
        if (null == selectTable) {
            return false;
        } else {
            return !IHandlerUtilities.isSelectedTableForignPartition();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SetSchemaHandlerInner.
     */
    private final class SetSchemaHandlerInner extends UserComboDialog {
        private final TableMetaData selTable;

        /**
         * Instantiates a new sets the schema handler inner.
         *
         * @param prnt the prnt
         * @param serverObject the server object
         * @param selTable the sel table
         */
        private SetSchemaHandlerInner(Shell prnt, Object serverObject, TableMetaData selTable) {
            super(prnt, serverObject);
            this.selTable = selTable;
        }
        
        @Override
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);
            newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_NAMESPACE, this.getClass()));
        }

        @Override
        protected void performOkOperation() {
            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            TableMetaData tableMeta = (TableMetaData) getObject();
            StatusMessage statMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_SET_SCHEMA));

            String oldSchemaName = "\"" + tableMeta.getNamespace().getName() + "\".\"" + tableMeta.getName() + "\"";
            String userInput = getUserInput();

            if ("".equals(userInput)) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.SET_SCEMA_SELECT, oldSchemaName));
                if (null != bottomStatusBar) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                return;
            }

            printMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.SET_SCEMA_MOVING, oldSchemaName, userInput));

            SetSchemaHandlerWorker setSchemaHandlerWorker = new SetSchemaHandlerWorker(selTable, userInput, this,
                    statMessage);
            setStatusMessage(statMessage);
            StatusMessageList.getInstance().push(statMessage);
            if (null != bottomStatusBar) {
                bottomStatusBar.activateStatusbar();
            }

            setSchemaHandlerWorker.schedule();

        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.SET_SCEMA_TITLE);
        }

        @Override
        protected String getHeader() {
            TableMetaData selTbl = (TableMetaData) getObject();
            return MessageConfigLoader.getProperty(IMessagesConstants.SET_SCEMA_SELECT_NEW, selTbl.getName(),
                    selTbl.getNamespace().getName());
        }

        @Override
        protected void comboDisplayValues(final Combo inputCombo) {
            TableMetaData selTabl = (TableMetaData) getObject();
            UIUtils.displayNamespaceList(selTabl.getNamespace().getDatabase(), selTabl.getNamespace().getName(),
                    inputCombo, false);
            if (inputCombo.getSelectionIndex() >= 0) {
                inputCombo.remove(inputCombo.getSelectionIndex());
            }
            setOkButtonEnabled(false);

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
     * Description: The Class SetSchemaHandlerWorker.
     */
    private static final class SetSchemaHandlerWorker extends UIWorkerJob {

        private TableMetaData tableMeta;
        private String oldname;
        private String newname;
        private UserComboDialog dialog;
        private StatusMessage statusMsg;
        private TerminalExecutionConnectionInfra conn;
        private String oldSchemaNm;
        private Object oldSchema;

        /**
         * Instantiates a new sets the schema handler worker.
         *
         * @param tableMeta the table meta
         * @param nwname the nwname
         * @param dialog the dialog
         * @param statusMessage the status message
         */
        private SetSchemaHandlerWorker(TableMetaData tableMeta, String nwname, UserComboDialog dialog,
                StatusMessage statusMessage) {
            super("Set Schema Handler", null);
            this.tableMeta = tableMeta;
            this.dialog = dialog;
            this.oldname = tableMeta.getName();
            this.newname = nwname;
            this.statusMsg = statusMessage;
            this.oldSchemaNm = tableMeta.getNamespace().getName();
            this.oldSchema = tableMeta.getNamespace();
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            conn = PromptPrdGetConnection.getConnection(tableMeta.getDatabase());
            tableMeta.execSetSchema(newname, conn.getConnection());
            tableMeta.refresh(conn.getConnection());

            MPPDBIDELoggerUtility.info("Moving table to selected schema is succesfull");
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                    .getProperty(IMessagesConstants.SET_SCEMA_MOVED, oldSchemaNm, tableMeta.getName(), newname)));
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (null != objectBrowserModel) {
                objectBrowserModel.refreshObject(tableMeta.getNamespace());
                objectBrowserModel.refreshObject(oldSchema);
            }
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
            dialog.printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.SET_SCEMA_CONN_ERROR,
                    MPPDBIDEConstants.LINE_SEPARATOR, dbCriticalException.getDBErrorMessage()));

            dialog.setOkButtonEnabled(true);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            String msg = dbOperationException.getServerMessage();
            if (null == msg) {
                msg = MessageConfigLoader.getProperty(IMessagesConstants.ERR_PREFIX_DB_MESSAGE) + ' '
                        + dbOperationException.getDBErrorMessage();
            }

            dialog.printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.SET_SCEMA_ERROR, oldname,
                    newname, MPPDBIDEConstants.LINE_SEPARATOR, msg));
            dialog.setOkButtonEnabled(true);
        }

        @Override
        public void finalCleanupUI() {
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(this.statusMsg);
            }
        }

        @Override
        public void finalCleanup() {
            if (this.conn != null) {
                this.conn.releaseConnection();
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
