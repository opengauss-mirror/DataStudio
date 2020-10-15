/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title: class
 * Description: The Class CheckSelectQuery.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * @author a00560152
 * @version [DataStudio 8.0.2, 22 July, 2020]
 * @since 22 July, 2020
 */
public class CheckSelectQuery {
    /**
     * Checks if is select query.
     *
     * @param query the query
     * @return true, if is select query
     */
    public static boolean isSelectQuery(String query) {
        return query.toLowerCase(Locale.ENGLISH).startsWith("select") || checkForWithClauseAndSelect(query);
    }

    private static boolean checkForWithClauseAndSelect(String query) {
        Pattern withSelectPattern = Pattern.compile(MPPDBIDEConstants.WITH_CLAUSE_REGEX);
        String queryToCheck = query.replaceAll(MPPDBIDEConstants.LINE_SEPARATOR, " ");
        Matcher withSelectMatcher = withSelectPattern.matcher(queryToCheck);
        if (withSelectMatcher.find()) {
            String clauseFound = withSelectMatcher.group(4);
            return clauseFound.equalsIgnoreCase(MPPDBIDEConstants.PRIVILEGE_SELECT);
        } else {
            return false;
        }
    }
}
