/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.util;

import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IExecTimer.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
