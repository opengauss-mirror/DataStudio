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

package org.opengauss.mppdbide.view.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.NotificationData;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class LastLoginSecurityPopupJob.
 *
 * @since 3.0.0
 */
public class LastLoginSecurityPopupJob extends UIWorkerJob {
    private Database db;

    private StringBuilder successfulLoginTxt;
    private List<String> failureLoginTxt;

    private NotificationData lastSuccessfullLogin = null;
    private JobCancelStatus status = null;

    /**
     * Instantiates a new last login security popup job.
     *
     * @param db the db
     */
    public LastLoginSecurityPopupJob(Database db) {
        super(ProgressBarLabelFormatter.getProgressLabelForDatabase(db.getDbName(), db.getServerName(),
                IMessagesConstants.LAST_LOGIN_MESSAGE), MPPDBIDEConstants.CANCELABLEJOB);
        this.db = db;
        successfulLoginTxt = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        failureLoginTxt = new ArrayList<String>(MPPDBIDEConstants.RECORD_ARRAY_SIZE);
        setTaskDB(db);
        this.status = new JobCancelStatus();
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException {

        lastSuccessfullLogin = this.db.getLoginNotifyManager().getLastSuccessfullLogin();
        if (lastSuccessfullLogin != null) {
            successfulLoginTxt.append(lastSuccessfullLogin.getTime());
            successfulLoginTxt.append("  ");
            successfulLoginTxt.append(lastSuccessfullLogin.getClientInfo());

            List<NotificationData> failureLoginAttempts = this.db.getLoginNotifyManager().getFailureLoginAttempts();

            for (int cnt = 0; cnt < failureLoginAttempts.size(); cnt++) {
                failureLoginTxt.add(
                        failureLoginAttempts.get(cnt).getTime() + "  " + failureLoginAttempts.get(cnt).getClientInfo());
            }

        }
        return null;
    }

    /**
     * Canceling.
     */
    @Override
    protected void canceling() {
        super.canceling();
        if (!db.isConnected()) {
            return;
        }

        status.setCancel(true);

        db.getLoginNotifyManager().cancelLastSuccessfulLoginRetrieval();
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_IN_PROGRESS)));
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        if (status.getCancel()) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG)));
            return;
        }
        SystemNotificationPopUI popup = new SystemNotificationPopUI(Display.getCurrent(), lastSuccessfullLogin != null);

        popup.setDatabaseName(db.getDbName());
        popup.setConName(db.getServerName());

        if (lastSuccessfullLogin != null) {
            popup.setSuccessLoginInfo(successfulLoginTxt.toString());
            popup.setFailureLoginInfo(failureLoginTxt);
        }

        popup.open();
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        if (!db.isConnected()) {
            MPPDBIDELoggerUtility.error("Database critical exception occurred as database has been disconnected",
                    exception);
            return;
        }
        MPPDBIDELoggerUtility.error("Database critical exception occurred while getting last login information ",
                exception);
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getErrorFromConst(IMessagesConstants.LAST_LOGIN_UNAVAILABE));
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        if (status.getCancel()) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG)));
            return;
        } else if (!db.isConnected()) {
            MPPDBIDELoggerUtility.error("Database operation exception occurred as database has been disconnected",
                    exception);
            return;
        } else {
            SystemNotificationPopUI popup = new SystemNotificationPopUI(Display.getCurrent(),
                    lastSuccessfullLogin != null);

            popup.setDatabaseName(db.getDbName());
            popup.setConName(db.getServerName());
            popup.open();
            return;
        }

    }

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {
        if (null != db && db.getDBType() == DBTYPE.OPENGAUSS) {
            DBConnection successfullLogin = db.getConnectionManager().getSuccessfullLogin();
            if (null != successfullLogin) {
                successfullLogin.disconnect();
            }
        }
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {

    }
}
