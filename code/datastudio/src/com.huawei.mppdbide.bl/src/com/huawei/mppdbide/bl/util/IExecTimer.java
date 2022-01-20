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

package com.huawei.mppdbide.bl.util;

import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IExecTimer.
 */

public interface IExecTimer {

    /**
     * Start.
     *
     * @return the i exec timer
     */
    IExecTimer start();

    /**
     * Stop.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    void stop() throws DatabaseOperationException;

    /**
     * Log time.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    void logTime() throws DatabaseOperationException;

    /**
     * Stop and log.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    void stopAndLog() throws DatabaseOperationException;

    /**
     * Stop and log no exception.
     */
    void stopAndLogNoException();

    /**
     * Checks if is timer stop.
     *
     * @return true, if is timer stop
     */
    boolean isTimerStop();

    /**
     * Gets the elapsed time.
     *
     * @return the elapsed time
     * @throws DatabaseOperationException the database operation exception
     */
    String getElapsedTime() throws DatabaseOperationException;

    /**
     * Gets the elapsed time in ms.
     *
     * @return the elapsed time in ms
     * @throws DatabaseOperationException the database operation exception
     */
    long getElapsedTimeInMs() throws DatabaseOperationException;

    /**
     * Gets the dynamic elapsed time.
     *
     * @param isVisible the is visible
     * @return the dynamic elapsed time
     */
    String getDynamicElapsedTime(boolean isVisible);
}
