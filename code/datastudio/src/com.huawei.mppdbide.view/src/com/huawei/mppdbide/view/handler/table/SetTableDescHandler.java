/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
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
import com.huawei.mppdbide.view.handler.UserInputDialogUIWorkerJob;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetTableDescHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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

        // DTS2014111203329 start
        @Override
        protected boolean forTableDescription() {
            return true;
        }
        // DTS2014111203329 end

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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
