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

package org.opengauss.mppdbide.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title: class
 * Description: The Class CheckSelectQuery.
 *
 * @since 3.0.0
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
