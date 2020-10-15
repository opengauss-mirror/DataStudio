/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
