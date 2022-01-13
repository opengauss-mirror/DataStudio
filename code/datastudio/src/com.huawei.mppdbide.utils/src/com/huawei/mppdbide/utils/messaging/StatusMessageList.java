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

package com.huawei.mppdbide.utils.messaging;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * Title: class
 * 
 * Description: The Class StatusMessageList.
 * 
 * @since 3.0.0
 */
public final class StatusMessageList implements IStatusMessageList {
    private Queue<StatusMessage> messages;
    private static volatile IStatusMessageList instance = null;
    private static final Object INSTANCE_LOCK = new Object();

    /**
     * Instantiates a new status message list.
     */
    private StatusMessageList() {
        messages = new LinkedList<StatusMessage>();
    }

    /**
     * Gets the single instance of StatusMessageList.
     *
     * @return single instance of StatusMessageList
     */
    public static IStatusMessageList getInstance() {
        if (null == instance) {
            synchronized (INSTANCE_LOCK) {
                if (null == instance) {
                    instance = new StatusMessageList();
                }
            }
        }
        return instance;
    }

    /**
     * Push.
     *
     * @param msg the msg
     * com.huawei.mppdbide.utils.messaging.IStatusMessageList#push(com.huawei.
     * mppdbide.utils.messaging.StatusMessage)
     */
    @Override
    public void push(StatusMessage msg) {
        messages.add(msg);
    }

    /**
     * Pop.
     *
     * @return the status message
     * com.huawei.mppdbide.utils.messaging.IStatusMessageList#pop()
     */
    @Override
    public StatusMessage pop() {
        return messages.remove();
    }

    /**
     * Pop.
     *
     * @param message the message
     * @return true, if successful
     * com.huawei.mppdbide.utils.messaging.IStatusMessageList#pop(com.huawei.
     * mppdbide.utils.messaging.StatusMessage)
     */
    @Override
    public boolean pop(StatusMessage message) {
        return messages.remove(message);
    }

    /**
     * Checks if is empty.
     *
     * @return true, if is empty *
     * com.huawei.mppdbide.utils.messaging.IStatusMessageList#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return messages.isEmpty();
    }
}
