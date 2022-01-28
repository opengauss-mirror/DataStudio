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

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.UserRole;
import com.huawei.mppdbide.bl.serverdatacache.UserRoleManager;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserRoleObjectGroup;
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
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropUserRoleHandler.
 *
 * @since 3.0.0
 */
public class DropUserRoleHandler {
    private DBConnection conn;

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        UserRole selectedRole = IHandlerUtilities.getSelectedUserRole();

        if (selectedRole != null) {
            int returnValue = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_ROLE_DIA_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_ROLE, selectedRole.getName()),
                    MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_YES),
                    MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_NO));

            if (returnValue != 0) {
                return;
            }

            try {
                conn = selectedRole.getServer().getAnotherConnection(0);
            } catch (DatabaseOperationException exception) {

                MPPDBIDELoggerUtility.error("Failed to get another connection while dropping user role", exception);
            }

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            StatusMessage statusMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DROP_ROLE));
            String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.DROP_USERROLE_PROGRESS_NAME,
                    selectedRole.getName(), selectedRole.getServer().getServerConnectionInfo().getConectionName());
            DropRoleHandlerWorker dropjob = new DropRoleHandlerWorker(progressLabel, MPPDBIDEConstants.CANCELABLEJOB,
                    selectedRole, statusMessage, conn);
            StatusMessageList.getInstance().push(statusMessage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            dropjob.schedule();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DropRoleHandlerWorker.
     */
    private static final class DropRoleHandlerWorker extends UIWorkerJob {
        private UserRole userRole;
        private StatusMessage staMsg;
        private DBConnection conn;
        private JobCancelStatus cancelStatus = null;

        /**
         * Instantiates a new drop role handler worker.
         *
         * @param name the name
         * @param family the family
         * @param userRole the user role
         * @param statusMsg the status msg
         * @param conn the conn
         */
        private DropRoleHandlerWorker(String name, Object family, UserRole userRole, StatusMessage statusMsg,
                DBConnection conn) {
            super(name, family);
            this.userRole = userRole;
            this.staMsg = statusMsg;
            this.conn = conn;
            this.cancelStatus = new JobCancelStatus();
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            UserRoleManager.execDrop(conn, userRole);
            return null;
        }

        @Override
        protected void canceling() {
            super.canceling();
            try {
                conn.cancelQuery();
                cancelStatus.setCancel(true);
            } catch (Exception exception) {
                MPPDBIDELoggerUtility.error("Failed to cancel query", exception);
            }
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            if (cancelStatus.getCancel()) {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                        Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUREY_CANCEL_MSG)));
                return;
            }

            UserRoleObjectGroup userRoleObjectGroup = (UserRoleObjectGroup) userRole.getParent();
            try {
                userRoleObjectGroup.getServer().refreshUserRoleObjectGroup();
            } catch (MPPDBIDEException exception) {
                MPPDBIDELoggerUtility.error("DropUserRoleWorkerJob: refresh failed.", exception);
            }

            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();

            if (objectBrowserModel != null) {
                objectBrowserModel.setSelection(userRole.getParent());
                objectBrowserModel.refreshObject(userRole.getParent());
            }

            String message = MessageConfigLoader.getProperty(IMessagesConstants.DROP_USERROLE_SUCCESS,
                    userRole.getName());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            showErrorPopupMsg(exception);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            showErrorPopupMsg(exception);
        }

        @Override
        public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {

            super.onMPPDBIDEExceptionUIAction(exception);
            showErrorPopupMsg(exception);
        }

        /**
         * Show error popup msg.
         *
         * @param exception the exception
         */
        private void showErrorPopupMsg(MPPDBIDEException exception) {
            MPPDBIDEDialogs.generateDSErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_USERROLE_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_USERROLE_UNABLE),
                    exception.getServerMessage(), null);

            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getError(MessageConfigLoader.getProperty(
                            IMessagesConstants.DROP_USERROLE_UNABLE_MSG, userRole.getName(),
                            userRole.getServer().getServerConnectionInfo().getConectionName())));
        }

        @Override
        public void finalCleanup() {

        }

        @Override
        public void finalCleanupUI() {
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (null != bttmStatusBar) {

                bttmStatusBar.hideStatusbar(this.staMsg);
            }
        }

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        UserRole selectedRole = IHandlerUtilities.getSelectedUserRole();
        return null != selectedRole;
    }

}
