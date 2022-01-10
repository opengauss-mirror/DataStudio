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

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class Executor.
 * 
 */

public class Executor extends AbstractExecutor {

    /**
     * Instantiates a new executor.
     *
     * @param db the db
     */
    public Executor() {
    }

    /**
     * Gets the server version.
     *
     * @return the server version
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String getServerVersion() throws DatabaseOperationException, DatabaseCriticalException {
        return targetExecutor.getServerVersion();
    }

    /**
     * Fetch server IP.
     *
     * @return the string
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String fetchServerIP() throws DatabaseOperationException, DatabaseCriticalException {
        return targetExecutor.fetchServerIP();
    }

    /**
     * Disconnect.
     */
    public void disconnect() {
        MPPDBIDELoggerUtility.info("Executor: Disconnecting debug connection.");

        MPPDBIDELoggerUtility.info("Executor: Disconnecting target connection.");
        // Disconnecting target Executor.
        if (null != targetExecutor) {
            disconnectTargetExecutor();
        }
        MPPDBIDELoggerUtility.info("Executor: Disconnecting query connection.");
    }

    private void disconnectTargetExecutor() {
        addLoggerForDisconnectTargetExec();
        targetExecutor.disconnect();
        targetExecutor = null;
    }

    private void addLoggerForDisconnectTargetExec() {
        if (MPPDBIDELoggerUtility.isInfoEnabled()) {
            MPPDBIDELoggerUtility.info("Executor: Skipping set debug off" + " to avoid crash");
        }
    }
}
