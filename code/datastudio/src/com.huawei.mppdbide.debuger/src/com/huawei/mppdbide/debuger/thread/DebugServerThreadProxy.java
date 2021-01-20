/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: the DebugServerThreadProxy class
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/20]
 * @since 2020/11/20
 */
public class DebugServerThreadProxy {
    private static final int DEFAULT_WAIT_TIME = 2000; // ms
    private static final int DEFAULT_WAIT_PER_COUNT = 10; // ms
    private static final int DEFAULT_MAX_THREADS = 2;
    private int runCount = 1; // the max run count

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            DEFAULT_MAX_THREADS,
            DEFAULT_MAX_THREADS,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    private DebugServerRunable debugServerRunable;

    /**
     * set which torrun
     *
     * @param runable the runable
     * @return void
     */
    public void setDebugServerRunable(DebugServerRunable runable) {
        this.debugServerRunable = runable;
    }

    /**
     * start debug
     *
     * @return void
     */
    public void start() {
        if (runCount < 0) {
            return;
        }
        if (isAlive()) {
            MPPDBIDELoggerUtility.warn("old thread not exit, please check!");
            return;
        }
        MPPDBIDELoggerUtility.info("debug server run again!" + runCount);
        runCount -= 1;
        executor.execute(debugServerRunable);
    }

    /**
     * is back run alive
     *
     * @return true if alive
     */
    public boolean isAlive() {
        if (executor == null) { // this main executor already closed
            return true;
        }
        return executor.getActiveCount() != 0;
    }

    /**
     * wait back thread exit
     *
     * @return void
     */
    public void join() {
        if (this.executor != null) {
            this.executor.shutdown();
            boolean shutDownSuccess = waitExecutorShutDown(DEFAULT_WAIT_TIME);
            if (!shutDownSuccess) {
                MPPDBIDELoggerUtility.warn("executor shutdown failed!");
            }
            this.executor = null;
        }
    }

    private boolean waitExecutorShutDown(int timeout) {
        int curWaitTime = 0;
        while (curWaitTime < timeout) {
            if (this.executor.isShutdown()) {
                return true;
            }
            curWaitTime += DEFAULT_WAIT_PER_COUNT;
            try {
                Thread.sleep(DEFAULT_WAIT_PER_COUNT);
            } catch (InterruptedException e) {
                MPPDBIDELoggerUtility.warn("wait executor shutdown have error:" + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }
}
