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

package com.huawei.mppdbide.view.handler.connection;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectBrowserOperationUIWorkerJob.
 *
 * @since 3.0.0
 */
public abstract class ObjectBrowserOperationUIWorkerJob extends DBOperationUIWorkerJob {

    /**
     * The conn.
     */
    protected DBConnection conn;

    /**
     * The obj.
     */
    protected ServerObject obj;

    /**
     * The msg.
     */
    protected String msg;

    /**
     * Instantiates a new object browser operation UI worker job.
     *
     * @param name the name
     * @param obj the obj
     * @param msg the msg
     * @param family the family
     */
    public ObjectBrowserOperationUIWorkerJob(String name, ServerObject obj, String msg, Object family) {
        super(name, family);
        this.obj = obj;
        this.msg = msg;
    }

    /**
     * Pre UI setup.
     *
     * @param preHandlerObject the pre handler object
     * @return true, if successful
     */
    @Override
    public boolean preUISetup(Object preHandlerObject) {
        return super.preUISetup(preHandlerObject);
    }

    /**
     * Db conn operation.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void dbConnOperation() throws MPPDBIDEException {
        if (getDatabase() != null) {
            conn = getDatabase().getConnectionManager().getFreeConnection();
        }
    }

    /**
     * Log connection failure.
     *
     * @param failureMsg the failure msg
     */
    @Override
    public void logConnectionFailure(String failureMsg) {
        MPPDBIDELoggerUtility.error(failureMsg);
    }

    /**
     * Log msgs.
     *
     * @param msgs the msgs
     */
    @Override
    public void logMsgs(String msgs) {
        MPPDBIDELoggerUtility.error(msgs);
    }

    /**
     * Gets the status bar msg.
     *
     * @return the status bar msg
     */
    @Override
    public String getStatusBarMsg() {
        return this.msg;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    public Database getDatabase() {
        return obj.getDatabase();
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        if (this.conn != null && getDatabase() != null) {
            getDatabase().getConnectionManager().releaseAndDisconnection(this.conn);
            this.conn = null;
        }
    }

    /**
     * Canceling.
     */
    @Override
    protected void canceling() {
        super.canceling();
        try {
            if (null != conn) {
                conn.cancelQuery();
            }
        } catch (DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("Error occured while cancelling query..", exception);
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Error occured while cancelling query..", exception);
        }
    }

    /**
     * Gets the success msg for OB status bar.
     *
     * @return the success msg for OB status bar
     */
    protected abstract String getSuccessMsgForOBStatusBar();

    /**
     * Gets the object browser refresh item.
     *
     * @return the object browser refresh item
     */
    protected abstract ServerObject getObjectBrowserRefreshItem();

}
