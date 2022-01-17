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

import java.util.HashMap;
import java.util.Locale;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLKeywords.
 *
 * @since 3.0.0
 */
public class SQLKeywords {

    private static final HashMap<String, Integer> KEYWORDSMAPPER = new HashMap<>();
    private static final int CONSTVALUE = 1;

    private static final String[] RESERVEDWORDS = {"ALL", "ANALYSE", "ANALYZE", "AND", "ANY", "ARRAY", "AS", "ASC",
        "ASYMMETRIC", "AUTHID", "BOTH", "CASE", "CAST", "CHECK", "COLLATE", "COLUMN", "CONSTRAINT", "CREATE",
        "CURRENT_CATALOG", "CURRENT_DATE", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER",
        "DEFAULT", "DEFERRABLE", "DESC", "DISTINCT", "DO", "ELSE", "END", "EXCEPT", "FALSE", "FETCH", "FOR", "FOREIGN",
        "FROM", "FUNCTION", "GRANT", "GROUP", "HAVING", "IN", "INITIALLY", "INTERSECT", "INTO", "IS", "LEADING", "LESS",
        "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "MINUS", "MODIFY", "NLSSORT", "NOT", "NULL", "OFFSET", "ON", "ONLY",
        "OR", "ORDER", "PERFORMANCE", "PLACING", "PRIMARY", "PROCEDURE", "REFERENCES", "RETURN", "RETURNING", "SELECT",
        "SESSION_USER", "SOME", "SPLIT", "SYMMETRIC", "SYSDATE", "TABLE", "THEN", "TO", "TRAILING", "TRUE", "UNION",
        "UNIQUE", "USER", "USING", "VARIADIC", "WHEN", "WHERE", "WINDOW", "WITH", "DIAGNOSTICS", "ELSEIF", "ELSIF",
        "EXCEPTION", "EXIT", "FORALL", "FOREACH", "GET", "OPEN", "PERFORM", "RAISE", "WHILE", "BUCKETS", "REJECT",
        "ADD", "ALTER", "BEGIN", "BETWEEN", "BY", "COLLATION", "COMPRESS", "CONCURRENTLY", "CONNECT", "CROSS",
        "CURRENT", "DELETE", "DROP", "FREEZE", "FULL", "IDENTIFIED", "ILIKE", "INCREMENT", "INDEX", "INNER", "INSERT",
        "ISNULL", "JOIN", "LEFT", "LEVEL", "LIKE", "LOCK", "NATURAL", "NOTNULL", "NOWAIT", "OF", "OUTER", "OVER",
        "OVERLAPS", "PRIVILEGES", "RAW", "RENAME", "RIGHT", "ROWS", "SESSION", "SET", "SIMILAR", "START", "TRIGGER",
        "UNTIL", "UPDATE", "VERBOSE", "VIEW"};

    /**
     * Gets the reservedwords.To be accessible only to SQL Syntax
     *
     * @return the reservedwords
     */
    public static final String[] getRESERVEDWORDS() {

        return RESERVEDWORDS.clone();
    }

    /**
     * Inits the map.
     */
    public static void initMap() {
        final int length = RESERVEDWORDS.length;
        if (KEYWORDSMAPPER.isEmpty()) {
            for (int i = 0; i < length; i++) {
                KEYWORDSMAPPER.put(RESERVEDWORDS[i].toLowerCase(Locale.ENGLISH), CONSTVALUE);
            }
        }

    }

    /**
     * Gets the keywords.
     *
     * @return the keywords
     */
    public static HashMap<String, Integer> getKeywords() {

        return KEYWORDSMAPPER;

    }

}
