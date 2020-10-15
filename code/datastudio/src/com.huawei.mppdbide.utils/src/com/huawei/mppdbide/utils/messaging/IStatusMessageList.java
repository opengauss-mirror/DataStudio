/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.messaging;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IStatusMessageList.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IStatusMessageList {

    /**
     * Push.
     *
     * @param msg the msg
     */
    void push(StatusMessage msg);

    /**
     * Pop.
     *
     * @return the status message
     */
    StatusMessage pop();

    /**
     * Pop.
     *
     * @param message the message
     * @return true, if successful
     */
    boolean pop(StatusMessage message);

    /**
     * Checks if is empty.
     *
     * @return true, if is empty
     */
    boolean isEmpty();

}
