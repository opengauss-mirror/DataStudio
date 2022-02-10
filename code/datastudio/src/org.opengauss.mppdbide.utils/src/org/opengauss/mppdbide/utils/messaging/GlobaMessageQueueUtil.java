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

package org.opengauss.mppdbide.utils.messaging;

/**
 * 
 * Title: class
 * 
 * Description: The Class GlobaMessageQueueUtil.
 *
 * @since 3.0.0
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
