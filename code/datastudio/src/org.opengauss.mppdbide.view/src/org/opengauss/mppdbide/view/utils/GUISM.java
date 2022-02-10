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

package org.opengauss.mppdbide.view.utils;

import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class GUISM.
 *
 * @since 3.0.0
 */
public abstract class GUISM {
    private static final List<String> CURRENT_EXECUTIONS = new ArrayList<String>(4);

    /**
     * The Constant RESULTSET.
     */
    public static final String RESULTSET = "RESULTSET";

    /**
     * The Constant REFRESH.
     */
    public static final String REFRESH = "REFRESH";

    /**
     * The Constant SQLTERMINAL.
     */
    public static final String SQLTERMINAL = "SQLTERMINAL";

    /**
     * The Constant DROPITEM.
     */
    public static final String DROPITEM = "DROPITEM";

    /**
     * The Constant DEBUGPL.
     */
    public static final String DEBUGPL = "DEBUGPL";

    /**
     * The Constant EXECUTEPL.
     */
    public static final String EXECUTEPL = "EXECUTEPL";
    private static final Object LOCK = new Object();

    /**
     * Sets the current execution.
     *
     * @param operation the operation
     * @param isStart the is start
     * @throws DatabaseOperationException the database operation exception
     */
    public static void setCurrentExecution(String operation, boolean isStart) throws DatabaseOperationException {
        synchronized (LOCK) {
            if (CURRENT_EXECUTIONS.contains(operation) && isStart) {
                throwException(operation);
            }
            if (isStart) {
                CURRENT_EXECUTIONS.add(operation);
            } else {
                CURRENT_EXECUTIONS.remove(operation);
            }
        }
    }

    /**
     * Checks if is execution in progress.
     *
     * @param operation the operation
     * @return true, if is execution in progress
     */
    public static boolean isExecutionInProgress(String operation) {
        synchronized (LOCK) {
            return CURRENT_EXECUTIONS.contains(operation);
        }
    }

    /**
     * Throw the exception based on operation.
     *
     * @param operation the operation
     * @throws DatabaseOperationException the database operation exception
     */
    private static void throwException(String operation) throws DatabaseOperationException {
        DatabaseOperationException exception = new DatabaseOperationException(
                IMessagesConstants.ERR_EXECTION_IN_PROGRESS, operation, "");
        MPPDBIDELoggerUtility
                .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXECTION_IN_PROGRESS, operation, ""));
        throw exception;
    }

    /**
     * Checks if is debugging in progress.
     *
     * @return true, if is debugging in progress
     */
    public static boolean isDebuggingInProgress() {
        return isExecutionInProgress(DEBUGPL);
    }
}
