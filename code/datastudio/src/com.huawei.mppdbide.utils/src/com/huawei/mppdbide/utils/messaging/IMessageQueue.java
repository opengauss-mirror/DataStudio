/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.messaging;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IMessageQueue.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IMessageQueue {

    /**
     * Push.
     *
     * @param msg the msg
     */
    void push(Message msg);

    /**
     * Pop.
     *
     * @return the message
     */
    Message pop();

    /**
     * Checks if is empty.
     *
     * @return true, if is empty
     */
    boolean isEmpty();

    /**
     * Size.
     *
     * @return the int
     */
    /*
     * Returns the current size of the queue
     */
    int size();
}
