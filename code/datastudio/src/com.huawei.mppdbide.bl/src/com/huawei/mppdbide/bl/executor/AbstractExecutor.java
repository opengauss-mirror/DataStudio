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

package com.huawei.mppdbide.bl.executor;

import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.exceptions.UnknownException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: AbstractExecutor
 * 
 * Description: AbstractExecutor
 * 
 * @since 3.0.0
 */
public abstract class AbstractExecutor implements ExecutorWrapper {

    /**
     * The target executor.
     */
    protected TargetExecutor targetExecutor;

    /**
     * The is critical failure cleanup in progress.
     */
    protected volatile boolean isCriticalFailureCleanupInProgress;

    /**
     * Connect to server.
     *
     * @param serverInfo the server info
     * @param iConnectionDriver the i connection driver
     * @throws UnknownException the unknown exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void connectToServer(IServerConnectionInfo serverInfo, IConnectionDriver iConnectionDriver)
            throws UnknownException, MPPDBIDEException {
        try {
            // Create target executor and connects to server.
            targetExecutor = new TargetExecutor();
            targetExecutor.connect(serverInfo, iConnectionDriver);
            MPPDBIDELoggerUtility.debug("Executor: Connected to target executor.");

        } catch (MPPDBIDEException exp) {
            handleConnectFailure();
            throw exp;
        } catch (Exception exp) {
            handleConnectFailure();
            throw new UnknownException(IMessagesConstants.ERR_BL_CONNECT_FAILED, exp);
        }

    }

    /**
     * * Handle connection failure, performs the following operations: 1) Try
     * disconnecting debug/query/target executor and 2) Log in case of
     * exception. When to call - connectToServer failure case
     */
    private void handleConnectFailure() {

        // Check if target executor is created.
        if (null != targetExecutor) {
            // Disconnect target executor's connection.
            targetExecutor.disconnect();
            targetExecutor = null;
        }
    }

    /**
     * Disconnect.
     */
    public abstract void disconnect();

    /**
     * Gets the query exectuion string.
     *
     * @param dbgobj the dbgobj
     * @return the query exectuion string
     * @throws DatabaseOperationException the database operation exception
     */
    public void getQueryExectuionString(IDebugObject dbgobj) throws DatabaseOperationException {
        checkIsCriticalExceptionOccurred();

        MPPDBIDELoggerUtility.debug("Executor: Get execution string.");
        targetExecutor.getQueryExectuionString(dbgobj);
    }

    /**
     * Check is critical exception occurred.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    public void checkIsCriticalExceptionOccurred() throws DatabaseOperationException {
        if (isCriticalFailureCleanupInProgress) {
            if (MPPDBIDELoggerUtility.isDebugEnabled()) {
                MPPDBIDELoggerUtility.debug("Executor: critical exception "
                        + "cleanup is already in progress, so skipping the" + " current operation.");
            }
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_SERVER_CONNECTION_FAILED));
            throw new DatabaseOperationException(IMessagesConstants.ERR_BL_SERVER_CONNECTION_FAILED);
        }
    }

    /**
     * Fetch server IP.
     *
     * @return the string
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String fetchServerIP() throws DatabaseOperationException, DatabaseCriticalException {
        return null;
    }

    /**
     * Gets the server version.
     *
     * @return the server version
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String getServerVersion() throws DatabaseOperationException, DatabaseCriticalException {
        return null;
    }
}
