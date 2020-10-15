/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.UserNamespace;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.LoadLevel1Objects;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.search.SearchWindow;
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
 * Description: The Class RenameSchema.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
