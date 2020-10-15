/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.exceptions;

/**
 * 
 * Title: class
 * 
 * Description: The Class DatabaseCriticalException.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DatabaseCriticalException extends MPPDBIDEException {

    private static final long serialVersionUID = -3171277120266665426L;

    /**
     * Instantiates a new database critical exception.
     *
     * @param dbErrorMessageCode the db error message code
     * @param exception the exception
     */
    public DatabaseCriticalException(String dbErrorMessageCode, Exception exception) {
        super(dbErrorMessageCode, exception);
    }

    /**
     * Instantiates a new database critical exception.
     *
     * @param dbErrorMessageCode the db error message code
     */
    public DatabaseCriticalException(String dbErrorMessageCode) {
        super(dbErrorMessageCode);
        setServerMessage(dbErrorMessageCode);
    }

    /* Start DTS2013013108162 */
    /**
     * Instantiates a new database critical exception.
     *
     * @param dbErrorMessageCode the db error message code
     * @param errorOutOfMemory the errorOutOfMemory
     */
    public DatabaseCriticalException(String dbErrorMessageCode, OutOfMemoryError errorOutOfMemory) {
        super(dbErrorMessageCode, errorOutOfMemory.getCause());
        super.setServerMessage(errorOutOfMemory.getMessage());
    }
    /* End DTS2013013108162 */

}
