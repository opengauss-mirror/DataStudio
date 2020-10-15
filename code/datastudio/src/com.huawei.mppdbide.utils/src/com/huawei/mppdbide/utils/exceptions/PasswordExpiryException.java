/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.exceptions;

/**
 * 
 * Title: class
 * 
 * Description: The Class PasswordExpiryException.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class PasswordExpiryException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new password expiry exception.
     *
     * @param exception the exception
     */
    public PasswordExpiryException(String exception) {
        super(exception);
    }

}
