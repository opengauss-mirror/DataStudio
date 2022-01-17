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

package com.huawei.mppdbide.view.init;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.cmdline.CmdLineCharObject;
import com.huawei.mppdbide.view.ui.connectiondialog.IDBConnectionValidationRegEx;

/**
 * 
 * Title: interface IDSCommandlineOptionValidationUtils
 * 
 * Description: The Class IDSCommandlineOptionValidationUtils : Utility class
 * for DS commandline argument support.
 * 
 * @since 3.0.0
 */
public interface IDSCommandlineOptionValidationUtils {

    /**
     * Form error message for not all mandatory parameters supplied
     * 
     * @param dbType database type
     * @return formatted error msg
     */
    public static String formLessParamErrorMsg() {
        String msg = MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ERROR) + " "
                + MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_MANDATORY_PARAM_MISSING)
                + MPPDBIDEConstants.LINE_SEPARATOR + "1) " + IDSCommandlineOptions.CONNECTION_NAME
                + MPPDBIDEConstants.LINE_SEPARATOR + "2) " + IDSCommandlineOptions.HOST_IP
                + MPPDBIDEConstants.LINE_SEPARATOR + "3) " + IDSCommandlineOptions.HOST_PORT
                + MPPDBIDEConstants.LINE_SEPARATOR + "4) " + IDSCommandlineOptions.DB_NAME
                + MPPDBIDEConstants.LINE_SEPARATOR + "5) " + IDSCommandlineOptions.USER_NAME ;
        return msg;
    }

    /**
     * Form error message for not unidentified parameters supplied
     * 
     * @return formatted error msg
     */
    public static String formInvalidParamErrorMsg() {
        return MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ERROR) + " "
                + MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_UNIDENTIFIED_PARAM)
                + MPPDBIDEConstants.LINE_SEPARATOR + " 1) " + IDSCommandlineOptions.CONNECTION_NAME
                + MPPDBIDEConstants.LINE_SEPARATOR + " 2) " + IDSCommandlineOptions.HOST_IP
                + MPPDBIDEConstants.LINE_SEPARATOR + " 3) " + IDSCommandlineOptions.HOST_PORT
                + MPPDBIDEConstants.LINE_SEPARATOR + " 4) " + IDSCommandlineOptions.DB_NAME
                + MPPDBIDEConstants.LINE_SEPARATOR + " 5) " + IDSCommandlineOptions.USER_NAME
                + MPPDBIDEConstants.LINE_SEPARATOR + " 6) " + IDSCommandlineOptions.SAVE_PASSWORD
                + MPPDBIDEConstants.LINE_SEPARATOR + " 7) " + IDSCommandlineOptions.SSL_ENABLE
                + MPPDBIDEConstants.LINE_SEPARATOR + " 8) " + IDSCommandlineOptions.SSL_CLIENT_CERT
                + MPPDBIDEConstants.LINE_SEPARATOR + " 9) " + IDSCommandlineOptions.SSL_CLIENT_KEY
                + MPPDBIDEConstants.LINE_SEPARATOR + "10) " + IDSCommandlineOptions.SSL_ROOT_CERT
                + MPPDBIDEConstants.LINE_SEPARATOR + "11) " + IDSCommandlineOptions.SSL_MODE;
    }

    /**
     * Form error message for invalid connectionName parameter value
     * 
     * @return formatted error msg
     */
    public static String formInvalidConnNameErrorMsg() {
        return MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ERROR) + " "
                + MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_INVALID_CONNECTION_NAME_VALUE);
    }

    /**
     * Form error message for text length more than 63 chars
     * 
     * @param key : input text
     * @return formatted error msg
     */
    public static String formInvalidTextLengthErrorMsg(String key) {
        return MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ERROR) + " "
                + MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_INVALID_VALUE_LENGTH, key);
    }

    /**
     * Form error message for null value supplied
     * 
     * @param key input key
     * @return formatted error msg
     */
    public static String formNullValueErrorMsg(String key) {
        return MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ERROR) + " "
                + MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_NULL_VALUE_FOR_PARAM, key);
    }

    /**
     * Form error message for invalid file path. Either file doesn't exist or is
     * a directory
     * 
     * @param key input key
     * @return formatted error msg
     */
    public static String formInvalidFileErrorMsg(String key) {
        return MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ERROR) + " "
                + MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_INVALID_FILE_PATH, key);
    }

    /**
     * Form error message for invalid hostPort parameter value
     * 
     * @return formatted error msg
     */
    public static String formInvalidHostPortErrorMsg() {
        return MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ERROR) + " "
                + MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_INVALID_HOST_PORT_VALUE,
                        MPPDBIDEConstants.MAX_HOST_PORT);
    }

    /**
     * Form error message for invalid host parameter value
     * 
     * @return formatted error msg
     */
    public static String formInvalidHostIpErrorMsg() {
        return MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ERROR) + " "
                + MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_INVALID_SERVER_IP_MSG);
    }

    /**
     * Form error message for invalid savePassword parameter value
     * 
     * @return formatted error msg
     */
    public static String formInvalidPasswordSaveOptionErrorMsg() {
        return MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ERROR) + " "
                + MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_INVALID_SAVE_PASSWORD_VALUE)
                + MPPDBIDEConstants.LINE_SEPARATOR + "1) " + IDSCommandlineOptions.SAVE_PASSWORD_CURR_SESSION
                + MPPDBIDEConstants.LINE_SEPARATOR + "2) " + IDSCommandlineOptions.SAVE_PASSWORD_DONT_SAVE;
    }

    /**
     * Form warning message for invalid sslEnable value
     * 
     * @return formatted error msg
     */
    public static String formEnableSslWarningMsg() {
        return MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ERROR) + " "
                + MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_INVALID_SSL_ENABLE_VALUE);
    }

    /**
     * Form error message for invalid sslMode parameter value
     * 
     * @return formatted error msg
     */
    public static String formInvalidSslModeErrorMsg() {
        return MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ERROR) + " "
                + MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_INVALID_SSL_MODE_VALUE)
                + MPPDBIDEConstants.LINE_SEPARATOR + "1) " + IDSCommandlineOptions.SSL_MODE_ALLOW
                + MPPDBIDEConstants.LINE_SEPARATOR + "2) " + IDSCommandlineOptions.SSL_MODE_REQUIRE
                + MPPDBIDEConstants.LINE_SEPARATOR + "3) " + IDSCommandlineOptions.SSL_MODE_VERIFY_CA
                + MPPDBIDEConstants.LINE_SEPARATOR + "4) " + IDSCommandlineOptions.SSL_MODE_VERIFY_FULL;
    }

    /**
     * Form error message for invalid hostPort parameter value
     * 
     * @param name port number
     * @return valid/invalid
     */
    public static boolean isHostPortNumberValid(String name) {
        try {
            if ((Integer.parseInt(name) > MPPDBIDEConstants.MAX_HOST_PORT) || Integer.parseInt(name) < 0) {
                return false;
            }
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }

    /**
     * Form error message for invalid server ip parameter value
     * 
     * @param ipAddress ip address
     * @return valid/invalid
     */
    public static boolean validateServerIpAddressForCommandline(String ipAddress) {
        if (ipAddress.matches(IDBConnectionValidationRegEx.REGEX_HOST_IPADDRESS)
                && !ipAddress.matches(IDBConnectionValidationRegEx.REGEX_IS_HOST_IPADDRESS)) {
            return false;
        }
        return true;
    }

    /**
     * connectionName sanity check
     * 
     * @param name connection name
     * @return valid/invalid
     */
    public static boolean isConnectionNameValid(String name) {
        Pattern pattern = Pattern.compile(IDBConnectionValidationRegEx.REGEX_CONNECTION_NAME);
        if (pattern.matcher(name).matches() || "\\".equalsIgnoreCase(name)) {
            return false;
        }
        return true;
    }

    /**
     * text length validation
     * 
     * @param name input name
     * @return valid/invalid
     */
    public static boolean isTextLengthValid(String name) {
        if (name.length() > 63) {
            return false;
        }
        return true;
    }

    /**
     * file path validation
     * 
     * @param filePath input filepath
     * @return valid/invalid
     */
    public static boolean isFilePathValid(String filePath) {
        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            return true;
        }
        return false;
    }

    /**
     * get all mandatory param value list for non gaussdb100
     * 
     * @return list of values
     */
    public static List<String> getMandatoryParamListForNonGauss100() {
        return Stream
                .of(IDSCommandlineOptions.CONNECTION_NAME, IDSCommandlineOptions.HOST_IP,
                        IDSCommandlineOptions.HOST_PORT, IDSCommandlineOptions.DB_NAME, IDSCommandlineOptions.USER_NAME)
                .collect(Collectors.toList());
    }

    /**
     * get all values for savePassword param
     * 
     * @return list of values
     */
    public static List<String> getSavePasswordValueList() {
        return Stream
                .of(IDSCommandlineOptions.SAVE_PASSWORD_CURR_SESSION, IDSCommandlineOptions.SAVE_PASSWORD_DONT_SAVE)
                .collect(Collectors.toList());
    }

    /**
     * get all values for sslMode param
     * 
     * @return list of values
     */
    public static List<String> getSslModeValueList() {
        return Stream
                .of(IDSCommandlineOptions.SSL_MODE_ALLOW, IDSCommandlineOptions.SSL_MODE_REQUIRE,
                        IDSCommandlineOptions.SSL_MODE_VERIFY_CA, IDSCommandlineOptions.SSL_MODE_VERIFY_FULL)
                .collect(Collectors.toList());
    }
    
    /**
     * text length validation
     * 
     * @param name input name
     * @return valid/invalid
     */
    public static boolean isCharLengthValid(CmdLineCharObject name) {
        if (name.getPrd().length > 63) {
                return false;
        }
        return true;
    }
}
