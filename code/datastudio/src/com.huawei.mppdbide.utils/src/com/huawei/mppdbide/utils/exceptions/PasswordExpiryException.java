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
 * Description: The Class PasswordExpiryException.
 * 
 * @since 3.0.0
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
