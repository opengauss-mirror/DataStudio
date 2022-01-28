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

package com.huawei.mppdbide.utils;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.huawei.mppdbide.utils.files.FileValidationUtils;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: EnvirnmentVariableValidator
 * 
 * @since 3.0.0
 */
public class EnvirnmentVariableValidator {
    /**
     * validateAndGetJavaPath
     * 
     * @return string value
     */
    public static String validateAndGetLineSeperator() {
        String lineSeperator = System.getProperty("line.separator");

        if (lineSeperator != null && (lineSeperator.equals("\n") || lineSeperator.equals("\r\n")
                || Integer.parseInt(lineSeperator) > 0)) {
            return lineSeperator;
        }
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.INVALID_LINE_SEPERATOR));
        return "\r\n";
    }

    /**
     * validateAndGetJavaPath
     * 
     * @return string value
     */
    public static String validateAndGetUserName() {
        String usrName = System.getProperty("user.name");
        String usrOsName = null;
        if (usrName != null && usrName.length() <= 20) {
            usrName = Normalizer.normalize(usrName, Normalizer.Form.NFD);
            char[] usrNameArray = usrName.toCharArray();
            usrOsName = new String(usrNameArray);
            Pattern pattern = Pattern.compile("\\[|\\]|\\:|\\;|\\||\\=|\\,|\\+|\\*|\\?|\\<|\\>|\\/|\\|\\\"");
            Matcher matcher = pattern.matcher(usrOsName);
            if (matcher.find()) {
                MPPDBIDELoggerUtility.error("User name contains invalid special characters.");
                return "";
            } else {
                return new String(usrNameArray);
            }
        } else {
            MPPDBIDELoggerUtility.error("User name length is more than allowed limit.");
            return "";
        }
    }

    /**
     * validateAndGetJavaPath
     * 
     * @return string value
     */
    public static String validateAndGetJavaPath() {
        String javaHomeStr = System.getProperty("java.home");
        if (javaHomeStr != null) {
            javaHomeStr = Normalizer.normalize(System.getProperty("java.home"), Normalizer.Form.NFD);
            if (FileValidationUtils.validateFilePathName(javaHomeStr)) {
                return javaHomeStr;
            }
        }
        return null;
    }

    /**
     * validateJavaVersion
     * 
     * @return string value
     */
    public static String validateJavaVersion() {
        String javaVersion = System.getProperty("java.version");
        Pattern pattern = Pattern.compile("^[0-9][0-9._]*$");
        Matcher matcher = pattern.matcher(javaVersion);
        if (matcher.matches()) {
            return javaVersion;
        }
        MPPDBIDELoggerUtility.error("Invalid java version is set");
        return "";
    }

    /**
     * validateAndGetFileSeperator
     * 
     * @return string value
     */
    public static String validateAndGetFileSeperator() {
        String fileSeperator = System.getProperty("file.separator");
        if (fileSeperator != null && (fileSeperator.equals("\\") || fileSeperator.equals("/"))) {
            return fileSeperator;
        }
        MPPDBIDELoggerUtility.error("Invalid file seperator is set");
        return MPPDBIDEConstants.FILE_SEPARATOR;
    }
}
