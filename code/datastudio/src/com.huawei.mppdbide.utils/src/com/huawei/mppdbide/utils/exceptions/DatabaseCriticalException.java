/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.huawei.mppdbide.utils.exceptions;

/**
 * 
 * Title: class
 * 
 * Description: The Class DatabaseCriticalException.
 *
 * @since 3.0.0
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

}
