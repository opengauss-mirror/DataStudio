/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.connectiondialog;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.connection.PasswordDialog;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class PromptPrdGetConnection.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class PromptPrdGetConnection {

    /*
     * if the connection is success then returns the
     * TerminalExecutionConnectionInfra. If user gives up in case of wrong input
     * or other cases, throws exception
     */

    private static final String INVALID_USERNAME_PASSWORD_OLAP = "Invalid username/password";
    private static int returnVal;
    private static String msg;

    /**
     * Gets the connection.
     *
     * @param db the db
     * @return the connection
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public static TerminalExecutionConnectionInfra getConnection(Database db) throws MPPDBIDEException {
        TerminalExecutionConnectionInfra termConnection = new TerminalExecutionConnectionInfra();
        termConnection.setDatabase(db);
        establishConnection(termConnection);
        return termConnection;
    }

    /**
     * Gets the connection.
     *
     * @param tc the tc
     * @return the connection
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public static TerminalExecutionConnectionInfra getConnection(TerminalExecutionConnectionInfra tc)
            throws MPPDBIDEException {
        establishConnection(tc);
        return tc;
    }

    private static void establishConnection(TerminalExecutionConnectionInfra termConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        boolean isExceptionForInvalidPswd = false;

        Database db = termConnection.getDatabase();
        DBConnection conn = termConnection.getConnection();
        msg = null;
        while (true) {
            if (isConnectionClosed(conn) && isDbConnected(db)) {
                if (isShowPasswordDialog(isExceptionForInvalidPswd, db)) {
                    handlePasswordPromptDialog(db);
                    isExceptionForInvalidPswd = setConnectionOnShowPasswordDialog(termConnection,
                            isExceptionForInvalidPswd, db);
                } else {
                    try {
                        termConnection.setConnection(db.getConnectionManager().getFreeConnection());
                    } catch (MPPDBIDEException exp) {
                        msg = getServerErrorMessage(exp);
                        displayConnectionErrorMessage();
                        if (UIDisplayFactoryProvider.getUIDisplayStateIf().needPromptPasswordError(msg)) {
                            isExceptionForInvalidPswd = true;
                        } else if (msg.contains("Connection refused")) {
                            MPPDBIDELoggerUtility.error(
                                    MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR), exp);
                            throw new DatabaseCriticalException(IMessagesConstants.DATABASE_CONNECTION_ERR, exp);
                        } else {
                            MPPDBIDELoggerUtility
                                    .error(MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR));
                            throw new DatabaseOperationException(IMessagesConstants.DATABASE_CONNECTION_ERR);
                        }
                    }
                }
                conn = termConnection.getConnection();

            } else {
                break;
            }
        }
    }

    private static void displayConnectionErrorMessage() {
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR), msg);

            }
        });
    }

    private static boolean setConnectionOnShowPasswordDialog(TerminalExecutionConnectionInfra termConnection,
            boolean isExceptionForInvalidPswd, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        try {
            termConnection.setConnection(db.getConnectionManager().getFreeConnection());
        } catch (MPPDBIDEException exception) {
            msg = getServerErrorMessage(exception);

            if (msg.contains(INVALID_USERNAME_PASSWORD_OLAP)) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_DATABASE_CRITICAL_ERROR),
                        exception);
                isExceptionForInvalidPswd = true;
            } else if (msg.contains("Connection refused")) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_DATABASE_CRITICAL_ERROR),
                        exception);
                throw new DatabaseCriticalException(IMessagesConstants.MSG_HINT_DATABASE_CRITICAL_ERROR);
            } else {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR),
                        exception);
                throw new DatabaseOperationException(IMessagesConstants.DATABASE_CONNECTION_ERR);
            }
        }
        return isExceptionForInvalidPswd;
    }

    private static String getServerErrorMessage(MPPDBIDEException exception) {
        return exception.getServerMessage() != null ? exception.getServerMessage() : exception.getMessage();
    }

    private static void handlePasswordPromptDialog(Database db) throws DatabaseOperationException {
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                Shell shell = Display.getDefault().getActiveShell();
                PasswordDialog dialog;
                if (null == msg) {
                    dialog = new PasswordDialog(shell, db);
                } else {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERR),
                            MessageConfigLoader.getProperty(IMessagesConstants.UNABLE_TO_CONNECT_TO_DATABASE_DB,
                                    db.getName(), MPPDBIDEConstants.LINE_SEPARATOR, msg));

                    dialog = new PasswordDialog(shell, db);

                }

                msg = null;
                returnVal = dialog.open();

            }
        });
        if (PasswordDialog.CANCEL == returnVal) {
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR),
                            MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));

                }
            });
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_SERVER_CONNECTION_FAILED));
            throw new DatabaseOperationException(IMessagesConstants.ERR_BL_SERVER_CONNECTION_FAILED);
        }
    }

    private static boolean isShowPasswordDialog(boolean isExceptionForInvalidPswd, Database db) {
        return db.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE) || isExceptionForInvalidPswd;
    }

    private static boolean isDbConnected(Database db) {
        return null != db && db.isConnected();
    }

    private static boolean isConnectionClosed(DBConnection conn) {
        try {
            return null == conn || conn.isClosed();
        } catch (DatabaseOperationException e) {
            return true;
        }
    }
}
