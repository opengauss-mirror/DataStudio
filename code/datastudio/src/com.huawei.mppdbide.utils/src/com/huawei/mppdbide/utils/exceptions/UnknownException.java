/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.exceptions;

/**
 * 
 * Title: class
 * 
 * Description: The Class UnknownException.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class UnknownException extends MPPDBIDEException {

    private static final long serialVersionUID = -7056743612593978712L;

    /**
     * Instantiates a new unknown exception.
     *
     * @param dbErrorMessageCode the db error message code
     * @param exception the exception
     */
    public UnknownException(String dbErrorMessageCode, Exception exception) {
        super(dbErrorMessageCode, exception);
    }

}
