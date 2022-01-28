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

package com.huawei.mppdbide.view.handler;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerUtil;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
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
import com.huawei.mppdbide.view.core.LoadLevel1Objects;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.DatabaseListControl;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.connectiondialog.ConnectionNameValidator;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameConnection.
 *
 * @since 3.0.0
 */
public class RenameConnection {

    private StatusMessage statusMessage;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final Server selectedServer = IHandlerUtilities.getSelectedServer();

        if (selectedServer != null) {
            IServerConnectionInfo serverInfo = selectedServer.getServerConnectionInfo();
            UserInputDialog userInputDialog = new RenameConnectionInner(shell, serverInfo, selectedServer);
            userInputDialog.open();
        }

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ServerConnectionInfo serInfo = HandlerUtilities.getServerInfo();

        return null != serInfo;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RenameConnectionInner.
     */
    private final class RenameConnectionInner extends UserInputDialog {
        private final Server selServer;

        /**
         * Instantiates a new rename connection inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param selectedServer the selected server
         */
        private RenameConnectionInner(Shell parent, Object serverObject, Server selectedServer) {
            super(parent, serverObject);
            this.selServer = selectedServer;
        }

        @Override
        protected Control createDialogArea(Composite parent) {

            Control createDialogArea = super.createDialogArea(parent);

            StyledText renameTextBox = (StyledText) inputControl;

            ConnectionNameValidator nameValidator = new ConnectionNameValidator(renameTextBox);
            renameTextBox.addVerifyListener(nameValidator);

            return createDialogArea;
        }

        @Override
        protected void performOkOperation() {

            performOkOperatn(selServer);

        }

        /**
         * Perform ok operatn.
         *
         * @param seltServer the selt server
         */
        private void performOkOperatn(final Server seltServer) {
            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            ServerConnectionInfo serverConnectionInfo = (ServerConnectionInfo) getObject();
            StatusMessage statMssage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_RENAME_CONNECTION));

            String userInput = getUserInput();

            IServerConnectionInfo profile = ConnectionProfileManagerImpl.getInstance().getProfile(userInput);

            if (profile != null) {
                printErrorMessage(MessageConfigLoader
                        .getProperty(IMessagesConstants.ERR_ALREADY_CONNECTION_PROFILE_EXISTS, userInput), false);
                if (bottomStatusBar != null) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                return;

            }

            else if ("".equals(userInput)) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.ERR_INVALID_CONNECTION_NAME),
                        false);
                if (bottomStatusBar != null) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                enableButtons();
                return;
            }

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_CONNECTION_WAIT,
                    serverConnectionInfo.getConectionName()), true);

            RenameConnectionWorker worker = new RenameConnectionWorker(seltServer, userInput, this, statMssage);
            setStatusMessage(statMssage);
            StatusMessageList.getInstance().push(statMssage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            worker.schedule();
        }

        @Override
        protected Image getWindowImage() {

            return IconUtility.getIconImage(IiconPath.ICO_CONNECTED, this.getClass());
        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_CONNECTION_TITILE);
        }

        @Override
        protected String getHeader() {

            ServerConnectionInfo serverInfo1 = (ServerConnectionInfo) getObject();
            String connName = (serverInfo1.getConectionName().length() > 50)
                    ? MPPDBIDEConstants.LINE_SEPARATOR + serverInfo1.getConectionName()
                    : serverInfo1.getConectionName();
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_CONNECTION_NEW_NAME, connName);
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
     * Description: The Class RenameConnectionWorker.
     */
    private static final class RenameConnectionWorker extends UIWorkerJob {

        private String newName;
        private String oldName;
        private UserInputDialog dialog;
        private StatusMessage statusMsg;
        private Server selctdServer;

        /**
         * Instantiates a new rename connection worker.
         *
         * @param server the server
         * @param newName the new name
         * @param dialog the dialog
         * @param statusMsg the status msg
         */
        private RenameConnectionWorker(Server server, String newName, UserInputDialog dialog, StatusMessage statusMsg) {
            super("Rename Connection", null);
            this.selctdServer = server;
            this.oldName = selctdServer.getServerConnectionInfo().getConectionName();
            this.newName = newName;
            this.dialog = dialog;
            this.statusMsg = statusMsg;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {

            IServerConnectionInfo serverConnectionInfo = selctdServer.getServerConnectionInfo();
            IServerConnectionInfo newInfo = ConnectionProfileManagerImpl.getInstance()
                    .getProfile(serverConnectionInfo.getConectionName());
            if (newInfo != null) {

                if (newInfo.getSavePrdOption().equals(SavePrdOptions.PERMANENTLY)) {
                    newInfo.setPrd(selctdServer.getEncrpytedProfilePrd().toCharArray());

                }

                newInfo.setConectionName(newName);
                ConnectionProfileManagerImpl.getInstance().renameProfile(newInfo);
                ServerUtil.clearConnectionInfo(newInfo);
                selctdServer.setServerConnectionInfo(newInfo);

            }

            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();

            selctdServer.setServerInProgress(true);

            ObjectBrowser objectBrowser = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowser != null) {
                objectBrowser.updatObject(selctdServer);
            }
            LoadLevel1Objects load = new LoadLevel1Objects(selctdServer, null);
            try {
                load.loadObjects();
            } catch (DatabaseCriticalException exception) {
                MPPDBIDELoggerUtility.error("RenameConnection: loading objects failed while renaming connection",
                        exception);
            }

            DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
            if (null != databaseListControl) {
                databaseListControl.refreshConnectionComboItems();
            }
            String newConnName = selctdServer.getServerConnectionInfo().getConectionName();
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(
                    MessageConfigLoader.getProperty(IMessagesConstants.RENAME_CONNECTION_TO, oldName, newConnName)));

        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
            dialog.printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_CONNECTION_ERROR,
                    MPPDBIDEConstants.LINE_SEPARATOR, dbCriticalException.getDBErrorMessage()), false);
            dialog.enableButtons();

        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException ex) {

            String serverMsg = ex.getServerMessage();
            if (null == serverMsg) {
                serverMsg = ex.getDBErrorMessage();
            }

            if (serverMsg.contains("Position:")) {
                serverMsg = serverMsg.split("Position:")[0];
            }
            dialog.printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_CONNECTION_ERROR,
                    oldName, MPPDBIDEConstants.LINE_SEPARATOR, serverMsg), false);
            dialog.enableButtons();

        }

        @Override
        public void finalCleanup() {

        }

        @Override
        public void finalCleanupUI() {
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(this.statusMsg);
            }

        }

    }

    // status message

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
     * @param statusMessage the new status message
     */
    public void setStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }

}
