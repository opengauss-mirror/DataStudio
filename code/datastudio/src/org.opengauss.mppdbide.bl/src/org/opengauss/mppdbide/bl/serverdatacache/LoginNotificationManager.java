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

package org.opengauss.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.PasswordExpiryException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class LoginNotificationManager.
 * 
 */

public class LoginNotificationManager {

    private Database database;
    private static final Object LOGIN_NOTIF_LOCK = new Object();

    /**
     * The Constant LOGIN_NOTIFICATION.
     */
    public static final String LOGIN_NOTIFICATION = "login notification";

    /**
     * The Constant LAST_LOGING_SUCCESS_DURATION.
     */
    public static final String LAST_LOGING_SUCCESS_DURATION = "Time taken for success query execution for last login notification : ";

    private boolean cancelLastSuccessfulLoginRetrieval;
    private boolean cancelFailureLoginAttempts;

    /**
     * Instantiates a new login notification manager.
     *
     * @param db the db
     */
    public LoginNotificationManager(Database db) {
        this.database = db;
    }

    /**
     * Gets the last successfull login.
     *
     * @return the last successfull login
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public NotificationData getLastSuccessfullLogin() throws DatabaseOperationException, DatabaseCriticalException {
        // to get the success login
        String qry = "SELECT logintime, client_conninfo from login_audit_messages_pid(true)";
        ResultSet rs = null;
        NotificationData notifData = null;
        try {

            synchronized (LOGIN_NOTIF_LOCK) {
                rs = database.getConnectionManager().executeLoginDetailsQuery(qry);
            }
            if (!cancelLastSuccessfulLoginRetrieval && rs.next()) {
                notifData = DatabaseUtils.getNotifDataFromRS(rs);
            }
            if (cancelLastSuccessfulLoginRetrieval) {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL));
                throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL);
            }

        } catch (SQLException sqlExcept) {
            throw new DatabaseOperationException(IMessagesConstants.LAST_LOGING_FAILED_RETREIVAL_DATABASE);
        } finally {
            postLoginInfoRetrieval(rs, "Database : get last successful login failed.");
        }

        return notifData;
    }

    /**
     * Post login info retrieval.
     *
     * @param rs the rs
     * @param errorMsg the error msg
     */
    private void postLoginInfoRetrieval(ResultSet rs, String errorMsg) {
        if (null != rs) {
            Statement stmt = null;
            try {
                stmt = rs.getStatement();

            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Obtaining Statement failure.", exception);
            } finally {
                try {
                    rs.close();
                } catch (SQLException exception) {
                    MPPDBIDELoggerUtility.error(errorMsg, exception);
                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException exception) {
                    MPPDBIDELoggerUtility.error("ADAPTER: statement close returned exception.", exception);
                }
            }
        }

        else {
            MPPDBIDELoggerUtility.info("Failed to retrieve login information");
        }
        cancelLastSuccessfulLoginRetrieval = false;
    }

    /**
     * Gets the failure login attempts.
     *
     * @return the failure login attempts
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public List<NotificationData> getFailureLoginAttempts()
            throws DatabaseCriticalException, DatabaseOperationException {
        // to get the failure login
        String qry = "SELECT logintime, client_conninfo from login_audit_messages_pid(false)";
        ResultSet rs = null;

        List<NotificationData> failureNotifs = new ArrayList<NotificationData>(MPPDBIDEConstants.RECORD_ARRAY_SIZE);
        try {
            synchronized (LOGIN_NOTIF_LOCK) {
                rs = database.getConnectionManager().executeLoginDetailsQuery(qry);
            }
            while (!cancelFailureLoginAttempts && rs.next()) {
                failureNotifs.add(DatabaseUtils.getNotifDataFromRS(rs));
            }
            if (cancelFailureLoginAttempts) {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL));
                throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL);
            }
        }

        catch (SQLException e) {
            throw new DatabaseOperationException(IMessagesConstants.LAST_LOGING_FAILED_RETREIVAL_DATABASE);
        } finally {
            postLoginInfoRetrieval(rs, "Database: getting failure login attempts failed.");
        }
        return failureNotifs;
    }

    /**
     * Cancel last successful login retrieval.
     */
    public void cancelLastSuccessfulLoginRetrieval() {

        try {
            cancelLastSuccessfulLoginRetrieval = true;
            cancelFailureLoginAttempts = true;
            synchronized (LOGIN_NOTIF_LOCK) {
                database.getConnectionManager().cancelLoginQuery();
            }
            
        } catch (DatabaseCriticalException e) {

            e.getServerMessage();
        } catch (DatabaseOperationException e) {

            e.getServerMessage();
        }

    }

    /**
     * Login on pswd expiry.
     *
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws PasswordExpiryException the password expiry exception
     */
    public static void loginOnPswdExpiry(Database db)
            throws DatabaseCriticalException, DatabaseOperationException, PasswordExpiryException {
        float deadLine = 0;
        boolean isLoginAllowedOnPasswordExpiry;
        isLoginAllowedOnPasswordExpiry = SystemSetting.getInstance().isLoginAllowedOnPasswordExpiry();
        String deadlineStamp = DatabaseUtils.getDeadlineInfo(MPPDBIDEConstants.FETCH_COUNT, db);

        if (deadlineStamp != null) {
            deadLine = Float.parseFloat(deadlineStamp);
        }
        if (deadLine < 0) {
            // isPasswordExpired
            if (!isLoginAllowedOnPasswordExpiry) {
                db.setConnected(false);
                throw new PasswordExpiryException(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_CIPHER_EXPIRED));
            }
        }
    }

}
