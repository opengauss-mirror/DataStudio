/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.messaging;

/**
 * 
 * Title: class
 * 
 * Description: The Class GlobaMessageQueueUtil.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class GlobaMessageQueueUtil {
    private MessageQueue messageQueue;

    /**
     * Gets the message queue.
     *
     * @return the message queue
     */
    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    private static volatile GlobaMessageQueueUtil instance;
    private static final Object LOCK = new Object();

    /**
     * Instantiates a new globa message queue util.
     */
    private GlobaMessageQueueUtil() {

        messageQueue = new MessageQueue();
    }

    /**
     * Gets the single instance of GlobaMessageQueueUtil.
     *
     * @return single instance of GlobaMessageQueueUtil
     */
    public static GlobaMessageQueueUtil getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new GlobaMessageQueueUtil();
                }
            }
        }

        return instance;
    }
}
