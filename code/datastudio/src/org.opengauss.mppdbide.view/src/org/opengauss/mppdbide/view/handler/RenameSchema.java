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
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.UserNamespace;
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
import org.opengauss.mppdbide.view.search.SearchWindow;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserInputDialog;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameSchema.
 *
 * @since 3.0.0
 */
public class RenameSchema {
    private StatusMessage statusMessage;
    private RenameSchemaWorker renameSchemaWorker;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final UserNamespace selectedNs = IHandlerUtilities.getSelectedUserNamespace();

        if (selectedNs != null) {
            UserInputDialog renameNamespace = new RenameSchemaInner(shell, selectedNs, selectedNs);

            renameNamespace.open();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object obj = UIElement.getInstance().getActivePartObject();
        if (obj instanceof SearchWindow) {
            return false;
        }
        UserNamespace ns = IHandlerUtilities.getSelectedUserNamespace();
        if (ns != null) {
            return true;
        }
        return false;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameSchemaInner.
     */
    private final class RenameSchemaInner extends UserInputDialog {
        private final Namespace selectedNamespc;

        /**
         * Instantiates a new rename schema inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param selectedNs the selected ns
         */
        private RenameSchemaInner(Shell parent, Object serverObject, Namespace selectedNs) {
            super(parent, serverObject);
            this.selectedNamespc = selectedNs;
        }

        @Override
        public void performOkOperation() {
            renameSchemaWorker = null;

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            UserNamespace ns = (UserNamespace) getObject();
            StatusMessage statMssage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_RENAME_SCHEMA));

            String newName = getUserInput();

            if ("".equals(newName)) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.NEW_NAME_FOR_SCHEMA_TO_CONTINUE),
                        false);
                if (bottomStatusBar != null) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                return;
            }

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAMING_SCHEMA), true);

            renameSchemaWorker = new RenameSchemaWorker(ns, newName, this, statMssage, selectedNamespc.getDatabase());
            setStatusMessage(statMssage);
            StatusMessageList.getInstance().push(statMssage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            renameSchemaWorker.schedule();
            enableCancelButton();
        }

        @Override
        protected void cancelPressed() {
            performCancelOperation();
        }

        @Override
        protected void performCancelOperation() {
            if (renameSchemaWorker != null && renameSchemaWorker.getState() == Job.RUNNING) {
                int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG));

                if (0 == returnValue) {
                    renameSchemaWorker.cancelJob();
                    renameSchemaWorker = null;
                } else {
                    enableCancelButton();
                }
            } else {
                close();
            }

        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_SCHEMA);
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
        protected String getHeader() {
            Namespace ns = (Namespace) getObject();
            return MessageConfigLoader.getProperty(IMessagesConstants.ENTER_NEW_NAME_FOR, ns.getName());
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
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_NAMESPACE, this.getClass());
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameSchemaWorker.
     */
    private static final class RenameSchemaWorker extends UserInputDialogUIWorkerJob {

        private UserNamespace nameSpace;
        private String newname;
        private Database db;

        /**
         * Instantiates a new rename schema worker.
         *
         * @param ns the ns
         * @param nwname the nwname
         * @param dialog the dialog
         * @param statusMessage the status message
         * @param db the db
         */
        private RenameSchemaWorker(UserNamespace ns, String nwname, UserInputDialog dialog, StatusMessage statusMessage,
                Database db) {
            super("Rename Schema", null, dialog, statusMessage, ns.getName(), IMessagesConstants.DB_RENAME_RENAMING,
                    IMessagesConstants.CONNECTION_ERR_DURING_REMANING);
            this.nameSpace = ns;
            this.newname = nwname;
            this.db = db;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            setConnInfra(PromptPrdGetConnection.getConnection(db));
            nameSpace.rename(newname, getConnInfra().getConnection());
            if (nameSpace.isLoaded()) {
                LoadLevel1Objects obj = new LoadLevel1Objects(nameSpace, statusMsg);
                obj.loadObjects();
            }
            MPPDBIDELoggerUtility.info("Renaming schema succesfull.");

            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message
                    .getInfo(MessageConfigLoader.getProperty(IMessagesConstants.DB_RENAME_RENAMED, oldname, newname)));

            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.updatObject(nameSpace);
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
