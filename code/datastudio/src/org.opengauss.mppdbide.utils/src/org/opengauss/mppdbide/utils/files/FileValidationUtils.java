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

package org.opengauss.mppdbide.utils.files;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Title: class
 * 
 * Description: The Class FileValidationUtils.
 *
 * @since 3.0.0
 */
public class FileValidationUtils {
    /**
     * validateFileName the validateFileName
     * 
     * @param fileName the filename
     * @return true, if valid file
     */
    public static boolean validateFileName(String fileName) {
        Pattern patternWinFile = Pattern.compile("\\[|\\]|\\:|\\||\\*|\\?|\\<|\\>|\\/|\\\\|\\\"");
        Matcher matcher = patternWinFile.matcher(fileName);
        if (matcher.find()) {
            return false;
        }
        return true;
    }
    
    /**
     *  the validateFilePathName
     *  
     * @param filePath the filePath
     * @return true, if filePath is valid
     */
    public static boolean validateFilePathName(String filePath) {
        Pattern patternPath = Pattern.compile("(([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-])|\\\\+)+\\\\?");
        Matcher matcher = patternPath.matcher(filePath);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
}
