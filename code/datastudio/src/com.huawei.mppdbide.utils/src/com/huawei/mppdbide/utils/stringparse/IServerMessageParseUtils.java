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

package com.huawei.mppdbide.utils.stringparse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Title: IServerMessageParseUtils
 * 
 * @since 3.0.0
 */
public interface IServerMessageParseUtils {
    /**
     * String constant ioClient
     */
    String IO_CLIENT = "ioClient";

    /**
     * utility class to extract query from server error message
     * 
     * @param errorMessage : error msg
     * @return query query
     */
    static String extractQueryFromErrorMessage(String errorMessage) {
        Pattern pattern = Pattern.compile("sql\\s*=\\s*((.|\\r?\\n)*?)(sessionId)");
        Matcher matcher = pattern.matcher(errorMessage);

        if (matcher.find()) {
            String matchedText = matcher.group(1);
            if (null != matchedText) {
                return matchedText.trim();
            }
        }
        return "";
    }

    /**
     * utility class to extract query without comments from query
     * 
     * @param query : input query
     * @return query without comments
     */
    static String extractQueryWithoutComments(String query) {
        Pattern pattern = Pattern.compile("(\\/\\*(.|\\s)*?\\*\\/)?((.|\\s)*)");
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String matchedText = matcher.group(3);
            if (null != matchedText) {
                return matchedText.trim();
            }
        }
        return "";
    }

    /** 
     * the isQuerySelectFunction
     * 
     * @param query the query
     * @return true, if is select query
     */
    static boolean isQuerySelectFunction(String query) {
        Pattern pattern = Pattern.compile("^\\s*SELECT\\s*(.|\\s)*\\((.|\\s)*\\)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            return true;
        }
        return false;
    }

}
