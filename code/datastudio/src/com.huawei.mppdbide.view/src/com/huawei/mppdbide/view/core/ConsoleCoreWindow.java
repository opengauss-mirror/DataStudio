/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import com.huawei.mppdbide.utils.messaging.GlobaMessageQueueUtil;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.MessageQueue;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConsoleCoreWindow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class ConsoleCoreWindow extends ConsoleMessageWindow {
    private static volatile ConsoleCoreWindow instance;
    private MessageQueue queue;
    private static final Object LOCK = new Object();

    /**
     * Instantiates a new console core window.
     */
    private ConsoleCoreWindow() {
        this.queue = GlobaMessageQueueUtil.getInstance().getMessageQueue();
    }

    /**
     * Gets the single instance of ConsoleCoreWindow.
     *
     * @return single instance of ConsoleCoreWindow
     */
    public static ConsoleCoreWindow getInstance() {
        if (null == instance) {
            synchronized (LOCK) {
                if (null == instance) {
                    instance = new ConsoleCoreWindow();
                }
            }
        }
        return instance;
    }

    /**
     * Log error.
     *
     * @param logMessage the log message
     */
    @Override
    public void logError(String logMessage) {
        this.queue.push(Message.getError(logMessage));
    }

    /**
     * Log error in UI.
     *
     * @param logMessage the log message
     */
    @Override
    public void logErrorInUI(String logMessage) {
        logError(logMessage);
    }

    /**
     * Log warning.
     *
     * @param logMessage the log message
     */
    @Override
    public void logWarning(String logMessage) {
        this.queue.push(Message.getWarn(logMessage));
    }

    /**
     * Log warning in UI.
     *
     * @param logMessage the log message
     */
    @Override
    public void logWarningInUI(String logMessage) {
        logWarning(logMessage);
    }

    /**
     * Log fatal.
     *
     * @param logMessage the log message
     */
    @Override
    public void logFatal(String logMessage) {
        this.queue.push(Message.getError(logMessage));
    }

    /**
     * Log fatal in UI.
     *
     * @param logMessage the log message
     */
    @Override
    public void logFatalInUI(String logMessage) {
        logFatal(logMessage);
    }
}
