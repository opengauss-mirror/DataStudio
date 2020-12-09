/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.thread;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: the DebugServerThreadProxy class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/20]
 * @since 2020/11/20
 */
public class DebugServerThreadProxy {
    private static final int DEFAULT_WAIT_LOCK_TIME = 2000; // ms
    private Thread proxyThread;
    private DebugServerRunable debugServerRunable;

    public void setDebugServerRunable(DebugServerRunable runable) {
        this.debugServerRunable = runable;
    }

    public Thread start() {
        if (isAlive()) {
            MPPDBIDELoggerUtility.warn("old thread not exit, please check!");
            return proxyThread;
        }
        proxyThread = new Thread(debugServerRunable);
        proxyThread.start();
        return proxyThread;
    }

    public boolean isAlive() {
        return proxyThread != null && proxyThread.isAlive();
    }

    public void join() {
        if (this.proxyThread != null) {
            try {
                this.proxyThread.join(DEFAULT_WAIT_LOCK_TIME);
            } catch (InterruptedException e) {
                MPPDBIDELoggerUtility.warn("proxyThread join with error!" + e.getMessage());
            }
            this.proxyThread = null;
        }
    }
}
