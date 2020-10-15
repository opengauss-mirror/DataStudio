/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.messaging;


/**
 * 
 * Title: class
 * 
 * Description: The Class StatusMessage.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class StatusMessage {
    private String message;

    /**
     * Instantiates a new status message.
     *
     * @param message the message
     * @param isExecutionInProgress the is execution in progress
     * @param manager the manager
     */
    public StatusMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the new message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
