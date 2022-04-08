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

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.UserRole;
import org.opengauss.mppdbide.bl.serverdatacache.UserRoleManager;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;

/**
 * 
 * Title: class
 * 
 * Description: The Class ShowUserRoleDDL.
 *
 * @since 3.0.0
 */
public class ShowUserRoleDDL {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        UserRole userRole = IHandlerUtilities.getSelectedUserRole();

        if (userRole != null) {
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForUserRole(userRole.getName(),
                    userRole.getServer().getName());
            ShowUserRoleDDLWorker showUserRoleDDLWorker = new ShowUserRoleDDLWorker(
                    MessageConfigLoader.getProperty(IMessagesConstants.SHOW_USER_ROLE_DDL_JOB_NAME, progressLabel),
                    userRole);
            showUserRoleDDLWorker.schedule();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof UserRole) {
            UserRole role = (UserRole) obj;
            if (IHandlerUtilities.getActiveDB(role.getServer())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ShowUserRoleDDLWorker.
     */
    private static final class ShowUserRoleDDLWorker extends PromptPasswordUIWorkerJob {
        private UserRole userRole;
        private Database db;
        private String userRoleDDL;
        private DBConnection conn;

        /**
         * Instantiates a new show user role DDL worker.
         *
         * @param name the name
         * @param userRole the user role
         */
        private ShowUserRoleDDLWorker(String name, UserRole userRole) {
            super(name, MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.SHOW_DDL_FAILED_TITLE);
            this.userRole = userRole;
        }

        @Override
        protected Database getDatabase() {
            if (this.db != null) {
                return this.db;
            }

            try {
                this.db = userRole.getServer().findOneActiveDb();
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("failed to get database", exception);
            }
            return this.db;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            if (getDatabase() == null) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.SHOW_USER_ROLE_DDL_NO_DATABASE));
                throw new MPPDBIDEException(IMessagesConstants.SHOW_USER_ROLE_DDL_NO_DATABASE);
            }
            
            if (getDatabase() != null) {
                setServerPwd(getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
                this.conn = getDatabase().getConnectionManager().getObjBrowserConn();
            }

            this.userRoleDDL = UserRoleManager.getDDL(this.conn, userRole);

            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            SQLTerminal terminal = UIElement.getInstance().createNewTerminal(userRole);
            if (null != terminal && StringUtils.isNotBlank(this.userRoleDDL)) {
                terminal.setDocumentContent(this.userRoleDDL);
                terminal.resetSQLTerminalButton();
                terminal.resetAutoCommitButton();
                terminal.setModified(true);
                terminal.setModifiedAfterCreate(true);
            }
        }

        @Override
        protected void canceling() {
            super.canceling();
            try {
                if (null != this.conn) {
                    this.conn.cancelQuery();
                }
            } catch (MPPDBIDEException exception) {
                exceptionEventCall(exception);
            }
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            exceptionEventCall(exception);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            if (!isCancel()) {
                exceptionEventCall(exception);
            }
        }

        @Override
        public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {
            exceptionEventCall(exception);
        }

        /**
         * Exception event call.
         *
         * @param exception the exception
         */
        public void exceptionEventCall(Exception exception) {
            String message = null;
            if (exception instanceof MPPDBIDEException) {
                message = ((MPPDBIDEException) exception).getServerMessage();
            } else {
                message = exception.getMessage();
            }
            MPPDBIDEDialogs.generateDSErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.SHOW_DDL_FAILED_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.SHOW_USER_ROLE_DDL_ERROR), message, exception);
        }

        @Override
        public void finalCleanupUI() {
        }
    }
}
