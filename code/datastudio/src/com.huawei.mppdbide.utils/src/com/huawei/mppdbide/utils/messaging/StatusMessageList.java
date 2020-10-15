/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
