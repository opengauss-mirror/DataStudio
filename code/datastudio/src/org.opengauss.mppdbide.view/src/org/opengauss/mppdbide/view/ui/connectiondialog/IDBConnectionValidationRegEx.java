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

package org.opengauss.mppdbide.view.ui.connectiondialog;

/**
 * 
 * Title: interface IDBConnectionValidationRegEx
 * 
 * Description: file containing regex string constants for connection parameter
 * validation
 *
 * @since 3.0.0
 */
public interface IDBConnectionValidationRegEx {
    
    /** 
     * regular expression for connection name validation 
     */
    public static final String REGEX_CONNECTION_NAME = ".*[/:*?\"<>|=].*";
    
    /** 
     * The Constant REGEX_HOST_IPADDRESS. 
     */
    public static final String REGEX_HOST_IPADDRESS = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    /**
     * The Constant REGEX_IS_HOST_IPADDRESS. 
     */
    public static final String REGEX_IS_HOST_IPADDRESS = "^\\d+\\.\\d+\\.\\d+\\.\\d+$";
}
