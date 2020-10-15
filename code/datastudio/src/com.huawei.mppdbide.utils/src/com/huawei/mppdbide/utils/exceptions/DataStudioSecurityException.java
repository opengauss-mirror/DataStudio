/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.exceptions;

/**
 * 
 * Title: class
 * 
 * Description: The Class DataStudioSecurityException.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DataStudioSecurityException extends MPPDBIDEException {

    private static final long serialVersionUID = 1252806867923069413L;

    /**
     * Instantiates a new data studio security exception.
     *
     * @param dbErrorMessageCode the db error message code
     * @param exception the exception
     */
    public DataStudioSecurityException(String dbErrorMessageCode, Exception exception) {
        super(dbErrorMessageCode, exception);
    }

    /**
     * Instantiates a new data studio security exception.
     *
     * @param dbErrorMessageCode the db error message code
     */
    public DataStudioSecurityException(String dbErrorMessageCode) {
        super(dbErrorMessageCode);
    }
}
