/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.userrole.handler;

import java.sql.ResultSet;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserRoleObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import com.huawei.mppdbide.view.userrole.CreateUserRole;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateUserRoleHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
