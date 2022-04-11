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

package org.opengauss.mppdbide.view.userrole.handler;

import java.sql.ResultSet;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.groups.UserRoleObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import org.opengauss.mppdbide.view.userrole.CreateUserRole;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateUserRoleHandler.
 *
 * @since 3.0.0
 */
public class CreateUserRoleHandler {

    /**
     * Execute.
     *
     * @param shell the shell
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Execute
    public void execute(final Shell shell) throws MPPDBIDEException {
        InitialCreateUserRoleWorker initialCreateUserRoleWorker = new InitialCreateUserRoleWorker(shell,
                IMessagesConstants.CREATE_NEW_ROLE, MPPDBIDEConstants.CANCELABLEJOB,
                IMessagesConstants.CREATE_NEW_ROLE);
        initialCreateUserRoleWorker.schedule();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        UserRoleObjectGroup userRoleGroup = IHandlerUtilities.getSelectedUserRoleGroup();
        if (userRoleGroup == null) {
            return false;
        }
        Server selectedServer = userRoleGroup.getServer();
        return null != selectedServer && selectedServer.isAleastOneDbConnected();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class InitialCreateUserRoleWorker.
     */
    private static class InitialCreateUserRoleWorker extends PromptPasswordUIWorkerJob {

        private Shell shell;
        private Database db;
        private DBConnection conn;
        private ResultSet createRoleResultSet;
        private boolean hasCreateUserRolePrivilege = false;

        /**
         * Instantiates a new initial create user role worker.
         *
         * @param shell the shell
         * @param name the name
         * @param family the family
         * @param errorWindowTitle the error window title
         */
        public InitialCreateUserRoleWorker(Shell shell, String name, Object family, String errorWindowTitle) {
            super(name, family, errorWindowTitle);
            this.shell = shell;
        }

        @Override
        protected Database getDatabase() {
            if (this.db != null) {
                return this.db;
            }

            try {
                if (IHandlerUtilities.getSelectedUserRoleGroup() != null) {
                    this.db = IHandlerUtilities.getSelectedUserRoleGroup().getServer().findOneActiveDb();
                }
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
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USER_ROLE_NO_DATABASE));
                throw new MPPDBIDEException(IMessagesConstants.CREATE_USER_ROLE_NO_DATABASE);
            }

            setServerPwd(getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));

            this.conn = getDatabase().getConnectionManager().getFreeConnection();

            if (IHandlerUtilities.getSelectedUserRoleGroup() != null) {
                String query = "select rolcreaterole, rolsystemadmin from pg_roles where rolname = ?;";
                String userName = IHandlerUtilities.getSelectedUserRoleGroup().getServer().getServerConnectionInfo()
                        .getDsUsername();
                createRoleResultSet = conn.execSelectForSearch(query, String.valueOf(userName));
                boolean hasNext = createRoleResultSet.next();
                if (hasNext) {
                    if (createRoleResultSet.getBoolean("rolcreaterole")
                            || createRoleResultSet.getBoolean("rolsystemadmin")) {
                        hasCreateUserRolePrivilege = true;
                    }
                }
            }

            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            if (!hasCreateUserRolePrivilege) {
                MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                        MessageConfigLoader.getProperty(IMessagesConstants.CREATE_NEW_ROLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_NO_PRIVILEGE_CREATE),
                        MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));
                return;
            }

            try {
                if (IHandlerUtilities.getSelectedUserRoleGroup() != null) {
                    CreateUserRole dlog = new CreateUserRole(this.shell,
                            IHandlerUtilities.getSelectedUserRoleGroup().getServer(), this.conn, getDatabase());
                    dlog.open();
                }
            } catch (MPPDBIDEException mppDbException) {
                exceptionEventCall(mppDbException);
            }
        }

        @Override
        public void finalCleanupUI() {
            if (this.conn != null) {
                this.conn.closeResultSet(createRoleResultSet);
                getDatabase().getConnectionManager().releaseConnection(this.conn);
            }
        }

        /**
         * On critical exception UI action.
         *
         * @param e the e
         */
        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
            exceptionEventCall(dbCriticalException);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            exceptionEventCall(dbOperationException);
        }

        /**
         * Exception event call.
         *
         * @param exception the e
         */
        public void exceptionEventCall(Exception exception) {
            String message = null;
            if (exception instanceof MPPDBIDEException) {
                message = ((MPPDBIDEException) exception).getServerMessage();
            } else {
                message = exception.getMessage();
            }
            MPPDBIDEDialogs.generateDSErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_NEW_ROLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_CREATE_ERROR, message), message,
                    null);
        }
    }
}
