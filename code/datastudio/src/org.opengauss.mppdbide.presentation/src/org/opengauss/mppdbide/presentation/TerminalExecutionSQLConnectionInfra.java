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

package org.opengauss.mppdbide.presentation;

import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class TerminalExecutionSQLConnectionInfra.
 * 
 * @since 3.0.0
 */
public class TerminalExecutionSQLConnectionInfra extends TerminalExecutionConnectionInfra {
    private AtomicInteger waitingThreadCount;
    private boolean isReuseConnection;
    private PriorityQueue<IUIWorkerJobNotifier> waitingJobQueue;

    /**
     * Instantiates a new terminal execution SQL connection infra.
     */
    public TerminalExecutionSQLConnectionInfra() {
        // By default DS supports Auto commit ON.
        super();
        this.isReuseConnection = true;
        this.waitingJobQueue = new PriorityQueue<IUIWorkerJobNotifier>(1);
        this.waitingThreadCount = new AtomicInteger(0);
    }

    /**
     * Gets the secure connection.
     *
     * @param callerThread the caller thread
     * @return the secure connection
     */
    public DBConnection getSecureConnection(IUIWorkerJobNotifier callerThread) {
        if (getReuseConnectionFlag()) {
            if (null == getConnection()) {
                callerThread.setCancelled(true);
                return null;
            }

            if (testAndSet()) {
                return getConnection();
            } else {
                if (!waitingJobQueue.contains(callerThread)) {
                    waitingJobQueue.add(callerThread);
                }
                return null;
            }
        } else {
            try {
                return this.database.getConnectionManager().getFreeConnection();
            } catch (MPPDBIDEException exception) {
                MPPDBIDELoggerUtility.error(
                        "TerminalExecutionSQLConnectionInfra: getting free connection from Database failed.",
                        exception);
            }
        }
        return null;
    }

    private boolean testAndSet() {
        return waitingThreadCount.compareAndSet(0, 1);
    }

    /**
     * Release secure connection.
     *
     * @param conn the conn
     */
    public void releaseSecureConnection(DBConnection conn) {
        if (null != getConnection() && getConnection().equals(conn)) {
            waitingThreadCount.compareAndSet(1, 0);
            if (!waitingJobQueue.isEmpty()) {
                IUIWorkerJobNotifier topWorker = waitingJobQueue.remove();
                topWorker.setNotified(true);
            }
        } else {
            if (null == getConnection()) {
                waitingThreadCount.compareAndSet(1, 0);
                this.cancelAllWaitingJobs();
            }
            releaseGivenConnection(conn);
        }
    }

    /**
     * Notify all waiting jobs.
     */
    public void notifyAllWaitingJobs() {
        while (!waitingJobQueue.isEmpty()) {
            IUIWorkerJobNotifier topWorker = waitingJobQueue.remove();
            topWorker.setNotified(true);
        }
    }

    /**
     * Cancel all waiting jobs.
     */
    public void cancelAllWaitingJobs() {
        while (!waitingJobQueue.isEmpty()) {
            IUIWorkerJobNotifier topWorker = waitingJobQueue.remove();
            topWorker.setCancelled(true);
        }
    }

    /**
     * Checks if is connection busy.
     *
     * @return true, if is connection busy
     */
    public boolean isConnectionBusy() {
        return waitingThreadCount.intValue() == 1;
    }

    private void releaseGivenConnection(DBConnection conParam) {
        DBConnection conn = conParam;
        if (null != conn) {
            try {
                if (!conn.isClosed()) {
                    this.getDatabase().getConnectionManager().releaseAndDisconnection(conn);
                }
            } catch (DatabaseOperationException e) {
                conn = null;
            }
            conn = null;
        }
    }

    /**
     * Gets the reuse connection flag.
     *
     * @return the reuse connection flag
     */
    public boolean getReuseConnectionFlag() {
        return isReuseConnection;
    }

    /**
     * Sets the reuse connection flag.
     *
     * @param isReuseConn the new reuse connection flag
     */
    public void setReuseConnectionFlag(boolean isReuseConn) {
        this.isReuseConnection = isReuseConn;
    }

    /**
     * Checks if is database valid.
     *
     * @return true, if is database valid
     */
    public boolean isDatabaseValid() {
        return this.database != null;
    }

    /**
     * Gets the database name.
     *
     * @return the database name
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public String getDatabaseName() throws MPPDBIDEException {
        if (this.database != null) {
            return this.getDatabase().getName();
        }
        throw new MPPDBIDEException("Database is not valid");
    }
}
