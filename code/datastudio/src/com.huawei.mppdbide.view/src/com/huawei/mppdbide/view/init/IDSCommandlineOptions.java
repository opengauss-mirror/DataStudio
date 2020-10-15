/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.init;

/**
 * 
 * Title: interface IDSCommandlineOptions
 * 
 * Description: string constants for ds commandline connection support
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author g00408002
 * @version [DataStudio 8.0.1, 20 Nov, 2019]
 * @since 20 Nov, 2019
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
    String USER_PASSWORD = "password";

    /*
     * save password option
     * 
     * Range of values: 1) current_session 2) do_not_save
     * 
     * Default value: current_session
     */
    String SAVE_PASSWORD = "savePassword";

    /* save password do not save option */
    String SAVE_PASSWORD_DONT_SAVE = "do_not_save";

    /* save password current session option */
    String SAVE_PASSWORD_CURR_SESSION = "current_session";

    /* save password default option */
    String SAVE_PASSWORD_DEFAULT = SAVE_PASSWORD_CURR_SESSION;

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
