/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.files;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Title: class
 * 
 * Description: The Class FileValidationUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, 04 Aug, 2020]
 * @since 04 Aug, 2020
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
