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

package org.opengauss.mppdbide.adapter.keywordssyntax;

/**
 * 
 * Title: class
 * 
 * Description: The Class Keywords.
 * 
 * @since 3.0.0
 */
public class Keywords implements KeywordsIf {
    private String[] reservedkeywords = {"ADD", "ALL", "ANALYZE", "AND", "ANY", "ARRAY", "AS", "ASC", "BEGIN",
        "BETWEEN", "BY", "CHECK", "COLLATION", "COLUMN", "COMPRESS", "CONCURRENTLY", "CONNECT", "CONSTRAINT", "CREATE",
        "CROSS", "CURRENT", "CURRENT_DATE", "DEFAULT", "DELETE", "DESC", "DESCRIBE", "DISTINCT", "DO", "DROP", "ELSE",
        "FALSE", "FOR", "FREEZE", "FROM", "FULL", "FUNCTION", "GRANT", "GROUP", "HAVING", "IDENTIFIED", "ILIKE", "IN",
        "INCREMENT", "INDEX", "INNER", "INSERT", "INTERSECT", "INTO", "IS", "ISNULL", "JOIN", "LEFT", "LEVEL", "LIKE",
        "lOCALTIME", "LOCALTIMESTAMP", "LOCK", "MINUS", "MODIFY", "NATURAL", "NOT", "NOTNULL", "NOWAIT", "NULL", "OF",
        "ON", "ONLY", "OR", "ORDER", "OUTER", "OVER", "OVERLAPS", "PRIMARY", "PRIVILEGES", "PROCEDURE", "RAW", "RENAME",
        "RIGHT", "ROWS", "SELECT", "SESSION", "SET", "SIMILAR", "START", "SYSDATE", "TABLE", "THEN", "TO", "TRIGGER",
        "TRUE", "UNION", "UNIQUE", "UNTIL", "UPDATE", "USER", "VERBOSE", "VIEW", "WHERE", "WITH"};

    private String[] unreservedkeywords = {"AUTHORIZATION", "BIGINT", "BINARY", "BINARY_DOUBLE", "BINARY_INTEGER",
        "BIT", "BOOLEAN", "CHARACTER", "COALESCE", "DEC", "DECODE", "EXTRACT", "FLOAT", "GREATEST", "INOUT", "INT",
        "INTERVAL", "LEAST", "NATIONAL", "NCHAR", "NONE", "NULLIF", "NUMERIC", "NVARCHAR2", "NVL", "OUT", "OVERLAY",
        "POSITION", "PRECISION", "REAL", "ROW", "SETOF", "SMALLDATETIME", "SMALLINT", "SUBSTRING", "TIME", "TIMESTAMP",
        "TINYINT", "TREAT", "TRIM", "VARCHAR", "VARCHAR2", "XMLATTRIBUTES", "XMLCONCAT", "XMLELEMENT", "XMLEXISTS",
        "XMLFOREST", "XMLPARSE", "XMLPI", "XMLROOT", "XMLSERIALIZE"};

    private String[] types = {"INTEGER", "BIGINT", "REAL", "DECIMAL", "NUMBER", "CHAR", "VARCHAR", "VARCHAR2",
        "DATETIME", "TIMESTAMP", "INTERVAL", "BOOLEAN"};

    private String[] constants = {"AUTHORIZATION", "BINARY", "COLLATION", "CONCURRENTLY", "CROSS", "CURRENT_SCHEMA",
        "FREEZE", "FULL", "ILIKE", "INNER", "ISNULL", "JOIN", "LEFT", "LIKE", "NATURAL", "NOTNULL", "OUTER", "OVER",
        "OVERLAPS", "RIGHT", "SIMILAR", "VERBOSE"};

    private String[] predicates = {"::", "..", ":=", "=>", "=", "<>", "<", ">", "<=", ">=", "||", "!", "!!", "%", "@",
        "-", "<<", "&<", "&>", ">>", "<@", "@>", "~=", "&&", ">^", "<^", "@@", "*", "<->", "/", "+", "#=", "#<>", "#<",
        "#>", "#<=", "#>=", "<?>", "|/", "||/", "|", "<#>", "~", "!~", "#", "?#", "@-@", "?-", "?|", "^", "~~", "!~~",
        "~*", "!~*", "|>>", "<<|", "?||", "?-|", "##", "&", "<<=", ">>=", "~~*", "!~~*", "~<~", "~<=~", "~>=~", "~>~",
        "&<|", "|&>", "@@@", "-|-"};

    @Override
    public String[] getReservedKeywords() {
        return reservedkeywords.clone();
    }

    @Override
    public String[] getUnReservedKeywords() {
        return unreservedkeywords.clone();
    }

    @Override
    public String[] getUnRetentionKeywords() {
        return new String[] {};
    }

    @Override
    public String[] getTypes() {
        return types.clone();
    }

    @Override
    public String[] getConstants() {
        return constants.clone();
    }

    @Override
    public String[] getPredicates() {
        return predicates.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Keywords) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String[] getDataTypes() {
        return new String[] {};
    }
}
