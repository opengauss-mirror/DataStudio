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

package org.opengauss.mppdbide.view.handler;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseHelper;
import org.opengauss.mppdbide.bl.serverdatacache.groups.UserNamespaceObjectGroup;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.core.LoadLevel1Objects;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserInputDialog;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.GUISM;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateSchema.
 *
 * @since 3.0.0
 */
public class CreateSchema {
    private StatusMessage statusMessage;
    private CreateSchemaWorker createSchemaWorker;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final UserNamespaceObjectGroup userNsGroup = (UserNamespaceObjectGroup) IHandlerUtilities
                .getObjectBrowserSelectedObject();
        if (userNsGroup != null) {
            Database db = userNsGroup.getDatabase();

            if (null != db && (db.getServer().isServerInProgress()
                    || db.getServer().getDatabaseGroup().isLoadingDatabaseGroupInProgress()
                    || db.isLoadingNamespaceInProgress())) {
                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.REFRESH_IN_PROGRESS),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXECTION_IN_PROGRESS, GUISM.REFRESH));
                return;
            }
            UserInputDialog createSchemaDialog = new CreateSchemaInner(shell, db, db);

            createSchemaDialog.open();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        UserNamespaceObjectGroup userNsGroup = (UserNamespaceObjectGroup) IHandlerUtilities
                .getObjectBrowserSelectedObject();
        if (userNsGroup == null) {
            return false;
        } else {
            Database selectedDb = userNsGroup.getDatabase();
            return selectedDb.isConnected();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CreateSchemaInner.
     */
    private final class CreateSchemaInner extends UserInputDialog {

        /**
         * Instantiates a new creates the schema inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param selectedDb the selected db
         */
        private CreateSchemaInner(Shell parent, Object serverObject, Database selectedDb) {
            super(parent, serverObject);
        }

        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_NAMESPACE, this.getClass());
        }

        @Override
        public void performOkOperation() {
            createSchemaWorker = null;

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            Database db = (Database) getObject();
            StatusMessage statMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CREATE_SCHEMA));

            String schemaName = getUserInput();

            if ("".equals(schemaName)) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.ENTER_SCHEMA_NAME_TO_CONTINUE),
                        false);
                if (bottomStatusBar != null) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                return;
            }

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_CREATING_SCHEMA), true);

            createSchemaWorker = new CreateSchemaWorker(db, schemaName, this, statMessage);
            setStatusMessage(statMessage);
            StatusMessageList.getInstance().push(statMessage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            createSchemaWorker.schedule();
            enableCancelButton();
        }

        @Override
        protected void cancelPressed() {
            performCancelOperation();
        }

        @Override
        protected void performCancelOperation() {
            if (createSchemaWorker != null && createSchemaWorker.getState() == Job.RUNNING) {
                int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG));

                if (0 == returnValue) {
                    createSchemaWorker.cancelJob();
                    createSchemaWorker = null;
                } else {
                    enableCancelButton();
                }
            } else {
                close();
            }

        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.CREATE_SCHEMA);
        }

        @Override
        protected String getHeader() {
            return MessageConfigLoader.getProperty(IMessagesConstants.ENTER_SCHMEA_NAME);
        }

        @Override
        public void onSuccessUIAction(Object obj) {

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
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CreateSchemaWorker.
     */
    private static final class CreateSchemaWorker extends UserInputDialogUIWorkerJob {

        private Database db;
        private String schemaName;

        /**
         * Instantiates a new creates the schema worker.
         *
         * @param db the db
         * @param schemaName the schema name
         * @param dialog the dialog
         * @param statusMessage the status message
         */
        private CreateSchemaWorker(Database db, String schemaName, UserInputDialog dialog,
                StatusMessage statusMessage) {
            super("Create Schema", null, dialog, statusMessage, schemaName,
                    IMessagesConstants.ERROR_WHILE_CREATING_SCHEMA,
                    IMessagesConstants.CONNECTION_ERROR_DURING_SCHEMA_CREATION);
            this.db = db;
            this.schemaName = schemaName;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            setConnInfra(PromptPrdGetConnection.getConnection(db));
            DatabaseHelper.createNewSchema(schemaName, db);

            LoadLevel1Objects load = new LoadLevel1Objects(db.getUserNamespaceGroup(), statusMsg);
            load.loadObjects();
            MPPDBIDELoggerUtility.info("New schema created ");
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObjectInUIThread(db);
            }
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                    .getProperty(IMessagesConstants.CREATE_SCHEMA_SUCCESS, db.getName(), schemaName)));
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
