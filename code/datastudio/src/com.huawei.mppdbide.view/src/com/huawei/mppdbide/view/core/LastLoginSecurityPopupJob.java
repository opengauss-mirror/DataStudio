/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.NotificationData;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class LastLoginSecurityPopupJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
