/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.exceptions;

/**
 * 
 * Title: class
 * 
 * Description: The Class DatabaseOperationException.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class DatabaseOperationException extends MPPDBIDEException {
    private static final long serialVersionUID = 1252806867923069413L;

    /**
     * Instantiates a new database operation exception.
     *
     * @param dbErrorMessageCode the db error message code
     * @param exception the exception
     */
    public DatabaseOperationException(String dbErrorMessageCode, Exception exception) {
        super(dbErrorMessageCode, exception);
    }

    /**
     * Instantiates a new database operation exception.
     *
     * @param dbErrorMessageCode the db error message code
     */
    public DatabaseOperationException(String dbErrorMessageCode) {
        super(dbErrorMessageCode);
    }

    /**
     * Instantiates a new database operation exception.
     *
     * @param dbErrorMessageCode the db error message code
     * @param params the params
     */
    public DatabaseOperationException(String dbErrorMessageCode, Object... params) {
        super(dbErrorMessageCode, params);
    }

    /**
     * Instantiates a new database operation exception.
     *
     * @param dbErrorMessageCode the db error message code
     * @param parsedMessage the parsed message
     * @param exception the exception
     */
    public DatabaseOperationException(String dbErrorMessageCode, String parsedMessage, Exception exception) {
        super(dbErrorMessageCode, parsedMessage, exception);
    }
}
