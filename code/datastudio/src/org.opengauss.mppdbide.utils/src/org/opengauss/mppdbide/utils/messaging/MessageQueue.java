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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * Title: class
 * 
 * Description: The Class MessageQueue.
 *
 * @since 3.0.0
 */
public final class MessageQueue implements IMessageQueue {
    private BlockingQueue<Message> messages;

    /**
     * Instantiates a new message queue.
     */
    public MessageQueue() {
        messages = new LinkedBlockingQueue<Message>();
    }

    /**
     * Push.
     *
     * @param msg the msg
     * org.opengauss.mppdbide.utils.messaging.IMessageQueue#push(org.opengauss.
     * mppdbide.utils.messaging.Message)
     */
    @Override
    public void push(Message msg) {
        messages.add(msg);
    }

    /**
     * Pop.
     *
     * @return the message *
     * org.opengauss.mppdbide.utils.messaging.IMessageQueue#pop()
     * 
     */
    @Override
    public Message pop() {
        return messages.poll();
    }

    /**
     * Checks if is empty.
     *
     * @return true, if is empty *
     * org.opengauss.mppdbide.utils.messaging.IMessageQueue#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return messages.isEmpty();
    }

    @Override
    public int size() {
        return messages.size();
    }
}
