/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.connectiondialog;

/**
 * 
 * Title: interface IDBConnectionValidationRegEx
 * 
 * Description: file containing regex string constants for connection parameter
 * validation
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author g00408002
 * @version [DataStudio 8.0.1, 20 Nov, 2019]
 * @since 20 Nov, 2019
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
