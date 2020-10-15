/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.stringparse;
/**
 * Title: class Description: The Class IServerMessageParseUtils. Copyright
 * (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author g00408002
 * @version [DataStudio 8.0.1, 17 Jan, 2020]
 * @since 17 Jan, 2020
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Title: IServerMessageParseUtils
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author s00428892
 * @version [DataStudio 6.5.1, Apr 9, 2020]
 * @since Apr 9, 2020
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
