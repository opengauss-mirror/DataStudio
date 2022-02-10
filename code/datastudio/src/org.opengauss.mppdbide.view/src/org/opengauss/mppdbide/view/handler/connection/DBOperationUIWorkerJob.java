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

package org.opengauss.mppdbide.view.handler.connection;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.exceptions.UserOperationCancelException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBOperationUIWorkerJob.
 *
 * @since 3.0.0
 */
public abstract class DBOperationUIWorkerJob extends UIWorkerJob {
    private StatusMessage statusBarMsg;
    private BottomStatusBar globalStatusBar;

    /**
     * Instantiates a new DB operation UI worker job.
     *
     * @param name the name
     * @param family the family
     */
    public DBOperationUIWorkerJob(String name, Object family) {
        super(name, MPPDBIDEConstants.CANCELABLEJOB);
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public abstract Database getDatabase();

    /**
     * Db conn operation.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public abstract void dbConnOperation() throws MPPDBIDEException;

    /**
     * Log connection failure.
     *
     * @param failureMsg the failure msg
     */
    public abstract void logConnectionFailure(String failureMsg);

    /**
     * Log msgs.
     *
     * @param msgs the msgs
     */
    public abstract void logMsgs(String msgs);

    /**
     * Gets the status bar msg.
     *
     * @return the status bar msg
     */
    public abstract String getStatusBarMsg();

    /**
     * Pre UI setup.
     *
     * @param preHandlerObject the pre handler object
     * @return true, if successful
     */
    @Override
    public boolean preUISetup(Object preHandlerObject) {
        try {
            boolean returnVal = promptConnection();
            if (returnVal) {
                initiateStatusBar(getStatusBarMsg());
            }
            return returnVal;
        } catch (UserOperationCancelException exception) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));
            onPreSetupFailure(exception);
            return false;
        } catch (MPPDBIDEException exception) {
            String msg = exception.getServerMessage() != null ? exception.getServerMessage() : exception.getMessage();
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR), msg);
            if (exception instanceof DatabaseCriticalException) {
                UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception, getDatabase());
            }
            logConnectionFailure(MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR));

            onPreSetupFailure(exception);

            return false;
        }

    }

    /**
     * On pre setup failure.
     *
     * @param exception the exception
     */
    public void onPreSetupFailure(MPPDBIDEException exception) {
        // Nothing
    }

    /**
     * Initiate status bar.
     *
     * @param msg the msg
     */
    private void initiateStatusBar(String msg) {
        globalStatusBar = UIElement.getInstance().getProgressBarOnTop();

        statusBarMsg = new StatusMessage(msg);
        StatusMessageList.getInstance().push(statusBarMsg);
        if (globalStatusBar != null) {
            globalStatusBar.activateStatusbar();
        }
    }

    /**
     * Core logic of prompting the user for password and attempt connection.
     *
     * @return true, if successful
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private boolean promptConnection() throws MPPDBIDEException {
        if (null == getDatabase()) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_SERVER_CONNECTION_FAILED));
            throw new DatabaseOperationException(IMessagesConstants.ERR_BL_SERVER_CONNECTION_FAILED);
        }

        if (!getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)) {
            dbConnOperation();
            return true;
        }

        String userMsg = null;

        while (true) {
            if (0 != promptPasswordUI(userMsg)) {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_CONNECTION_FAILED));
                throw new UserOperationCancelException(IMessagesConstants.ERR_CONNECTION_FAILED);
            }

            try {
                dbConnOperation();
                return true;
            } catch (MPPDBIDEException exception) {
                String msg = exception.getServerMessage() != null ? exception.getServerMessage()
                        : exception.getMessage();
                userMsg = msg;
            }
        }
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        if (null != globalStatusBar) {
            // When pre-setup fails, globalStatusBar can be null.
            globalStatusBar.hideStatusbar(this.statusBarMsg);
        }
    }

    /**
     * Open password dialog. On subsequent dialog opens, facilitates to provide
     * user messages.
     *
     * @param msg the msg
     * @return the int
     */
    private int promptPasswordUI(String msg) {
        Shell shell = Display.getDefault().getActiveShell();
        PasswordDialog dialog = null;
        if (msg == null) {
            dialog = new PasswordDialog(shell, getDatabase());
        } else {
            dialog = new PasswordDialog(shell, getDatabase(), msg);
        }
        return dialog.open();
    }

}
