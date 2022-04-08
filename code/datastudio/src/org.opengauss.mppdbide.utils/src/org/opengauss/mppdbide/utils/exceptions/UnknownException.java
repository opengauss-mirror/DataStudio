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

package org.opengauss.mppdbide.utils.exceptions;

/**
 * 
 * Title: class
 * 
 * Description: The Class UnknownException.
 * 
 * @since 3.0.0
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
