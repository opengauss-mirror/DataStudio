/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.messaging;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * Title: class
 * 
 * Description: The Class MessageQueue.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * com.huawei.mppdbide.utils.messaging.IMessageQueue#push(com.huawei.
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
     * com.huawei.mppdbide.utils.messaging.IMessageQueue#pop()
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
     * com.huawei.mppdbide.utils.messaging.IMessageQueue#isEmpty()
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
