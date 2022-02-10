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

package org.opengauss.mppdbide.view.init;

/**
 * 
 * Title: interface IDSCommandlineOptions
 * 
 * Description: string constants for ds commandline connection support
 *
 * @since 3.0.0
 */
public interface IDSCommandlineOptions {

    /*
     * Type of the database server Range of values : 1) openGauss
     */
    String DB_TYPE = "dbType";

    /* db type openGauss */
    String DB_TYPE_OPEN_GAUSS = "openGauss";

    /* name of the connection */
    String CONNECTION_NAME = "connectionName";

    /* server IP address */
    String HOST_IP = "host";

    /* server port number */
    String HOST_PORT = "hostPort";

    /*
     * database name to connect to Not required for Gauss 100
     */
    String DB_NAME = "dbName";

    /* database user name */
    String USER_NAME = "userName";

    /* database user password */
    String USER_CIPHER = "password";

    /*
     * save password option
     * 
     * Range of values: 1) current_session 2) do_not_save
     * 
     * Default value: current_session
     */
    String SAVE_CIPHER = "savePassword";

    /* save password do not save option */
    String SAVE_CIPHER_DONT_SAVE = "do_not_save";

    /* save password current session option */
    String SAVE_CIPHER_CURR_SESSION = "current_session";

    /* save password default option */
    String SAVE_CIPHER_DEFAULT = SAVE_CIPHER_CURR_SESSION;

    /* Enable SSL */
    String SSL_ENABLE = "sslEnable";

    /* client SSL certificate file absolute path (with extension .crt) */
    String SSL_CLIENT_CERT = "sslClientCert";

    /* client SSL key file absolute path (with extension .pk8) */
    String SSL_CLIENT_KEY = "sslClientKey";

    /* root SSL certificate file absolute path (with extension .crt) */
    String SSL_ROOT_CERT = "sslRootCert";

    /*
     * SSL Mode
     * 
     * Range of values: 1) require 2) verify_ca 3) verify_full
     * 
     * Default value: require
     */
    String SSL_MODE = "sslMode";

    /* require mode for ssl */
    String SSL_MODE_REQUIRE = "require";

    /* verify_ca mode for ssl */
    String SSL_MODE_VERIFY_CA = "verify_ca";

    /* verify_full mode for ssl */
    String SSL_MODE_VERIFY_FULL = "verify_full";
    
    /* allow mode for ssl */
    String SSL_MODE_ALLOW = "allow";
}
