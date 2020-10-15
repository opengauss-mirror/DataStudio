/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.gauss;

import java.util.Timer;
import java.util.TimerTask;

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecuteTimerForNonSelect.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExecuteTimerForNonSelect {
    private DBConnection dbConn;
    private Long delay;
    private Timer timer;
    private boolean isQueryCanceled;

    /**
     * Instantiates a new execute timer for non select.
     *
     * @param dbConnection the db connection
     * @param delay the delay
     */
    public ExecuteTimerForNonSelect(DBConnection dbConnection, Long delay) {
        this.dbConn = dbConnection;
        this.delay = delay;
        timer = new Timer();
    }

    /**
     * Start.
     */
    public void start() {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                try {
                    if (!dbConn.isClosed()) {
                        dbConn.cancelQuery();
                        isQueryCanceled = true;

                    }
                } catch (DatabaseOperationException exception) {
                    MPPDBIDELoggerUtility.error("Cancel query failed", exception);
                } catch (DatabaseCriticalException exception) {
                    MPPDBIDELoggerUtility.error("Cancel query failed", exception);
                }

            }
        }, delay);
    }

    /**
     * Checks if is query canceled.
     *
     * @return true, if is query canceled
     */
    public boolean isQueryCanceled() {
        return isQueryCanceled;
    }

    /**
     * Stop.
     */
    public void stop() {
        timer.cancel();
    }
}
