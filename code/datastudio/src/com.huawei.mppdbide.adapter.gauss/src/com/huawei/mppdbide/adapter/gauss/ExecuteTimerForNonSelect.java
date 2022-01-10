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
