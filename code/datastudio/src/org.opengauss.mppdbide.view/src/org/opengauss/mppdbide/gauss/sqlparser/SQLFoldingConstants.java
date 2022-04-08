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

package org.opengauss.mppdbide.gauss.sqlparser;

/**
 * Title: SQLFoldingConstants
 *
 * @since 3.0.0
 */
public interface SQLFoldingConstants {

    /**
     * The sql view.
     */
    String SQL_VIEW = "view";

    /**
     * The sql procedure.
     */
    String SQL_PROCEDURE = "procedure";

    /**
     * The sql procedure.
     */
    String SQL_PACKAGE = "package";

    /**
     * The package body.
     */
    String SQL_PACKAGE_BODY = "body";

    /**
     * The sql function.
     */
    String SQL_FUNCTION = "function";

    /**
     * The sql table.
     */
    String SQL_TABLE = "table";

    /**
     * The sql trigger.
     */
    String SQL_TRIGGER = "trigger";

    /**
     * The sql create.
     */
    String SQL_CREATE = "create";

    /**
     * The sql alter.
     */
    String SQL_ALTER = "alter";

    /**
     * The sql drop.
     */
    String SQL_DROP = "drop";

    /**
     * The sql grant.
     */
    String SQL_GRANT = "grant";

    /**
     * The sql revoke.
     */
    String SQL_REVOKE = "revoke";

    /**
     * The sql delim name.
     */
    String SQL_DELIM_NAME = "delim";

    /**
     * The sql keyword union.
     */
    String SQL_KEYWORD_UNION = "union";

    /**
     * The sql keyword union.
     */
    String SQL_KEYWORD_MINUS = "minus";

    /**
     * The sql keyword intersect.
     */
    String SQL_KEYWORD_INTERSECT = "intersect";

    /**
     * The sql keyword except.
     */
    String SQL_KEYWORD_EXCEPT = "except";

    /**
     * The sql keyword with.
     */
    String SQL_KEYWORD_WITH = "with";

    /**
     * The sql keyword if.
     */
    String SQL_KEYWORD_IF = "if";

    /**
     * The sql keyword else.
     */
    String SQL_KEYWORD_ELSE = "else";

    /**
     * The sql keyword elsif.
     */
    String SQL_KEYWORD_ELSIF = "elsif";

    /**
     * The sql keyword elseif.
     */
    String SQL_KEYWORD_ELSEIF = "elseif";

    /**
     * The sql keyword loop.
     */
    String SQL_KEYWORD_LOOP = "loop";

    /**
     * The sql keyword case.
     */
    String SQL_KEYWORD_CASE = "case";

    /**
     * The sql keyword select.
     */
    String SQL_KEYWORD_SELECT = "select";

    /**
     * The sql keyword insert.
     */
    String SQL_KEYWORD_INSERT = "insert";

    /**
     * The sql keyword delete.
     */
    String SQL_KEYWORD_DELETE = "delete";

    /**
     * The sql keyword update.
     */
    String SQL_KEYWORD_UPDATE = "update";

    /**
     * The sql keyword truncate.
     */
    String SQL_KEYWORD_TRUNCATE = "truncate";

    /**
     * The sql keyword language.
     */
    String SQL_KEYWORD_LANGUAGE = "language";

    /**
     * The sql keywork values.
     */
    String SQL_KEYWORK_VALUES = "values";

    /**
     * The sql keywork values upper.
     */
    String SQL_KEYWORK_VALUES_UPPER = "VALUES";

    /**
     * The sql keywork declare.
     */
    String SQL_KEYWORK_DECLARE = "declare";

    /**
     * The sql keywork begin.
     */
    String SQL_KEYWORK_BEGIN = "begin";

    /**
     * The sql keywork end.
     */
    String SQL_KEYWORK_END = "end";

    /**
     * The sql keywork as.
     */
    String SQL_KEYWORK_AS = "as";

    /**
     * The sql keywork is.
     */
    String SQL_KEYWORK_IS = "is";

    /**
     * The sql delim semicolon.
     */
    String SQL_DELIM_SEMICOLON = ";";

    /**
     * The sql bracket start.
     */
    String SQL_BRACKET_START = "(";

    /**
     * The sql bracket end.
     */
    String SQL_BRACKET_END = ")";

    /**
     * The sql double doller.
     */
    String SQL_DOUBLE_DOLLER = "$$";

    /**
     * The sql token unknown.
     */
    String SQL_TOKEN_UNKNOWN = "unknown";

    /**
     * The sql comment singleline.
     */
    String SQL_COMMENT_SINGLELINE = "--";

    /**
     * The sql comment multiline start.
     */
    String SQL_COMMENT_MULTILINE_START = "/*";

    /**
     * The sql comment multiline end.
     */
    String SQL_COMMENT_MULTILINE_END = "*/";

    /**
     * The sql literal double quotes.
     */
    String SQL_LITERAL_DOUBLE_QUOTES = "\"";

    /**
     * The sql literal single quotes.
     */
    String SQL_LITERAL_SINGLE_QUOTES = "'";

    /**
     * The sql delim fslash.
     */
    String SQL_DELIM_FSLASH = "/";

    /**
     * The sql token dummy.
     */
    String SQL_TOKEN_DUMMY = "dummy";

    /**
     * The sql literal double quotes char.
     */
    char SQL_LITERAL_DOUBLE_QUOTES_CHAR = '"';

    /**
     * The sql literal single quotes char.
     */
    char SQL_LITERAL_SINGLE_QUOTES_CHAR = '\'';

    /**
     * The monoreconciler.
     */
    String MONORECONCILER = "MonoReconciler";

    /**
     * The sqlreconcilingstrategy.
     */
    String SQLRECONCILINGSTRATEGY = "SQLReconcilingStrategy";

    /**
     * The sql keyword from.
     */
    String SQL_KEYWORD_FROM = "from";

    /**
     * The sql keyword where.
     */
    String SQL_KEYWORD_WHERE = "where";

    /**
     * The sql keyword start.
     */
    String SQL_KEYWORD_START = "start";

    /**
     * The sql keyword connect.
     */
    String SQL_KEYWORD_CONNECT = "connect";

    /**
     * The sql keyword where.
     */
    String SQL_KEYWORD_GROUP = "group";

    /**
     * The sql keyword where.
     */
    String SQL_KEYWORD_ORDER = "order";

    /**
     * The sql keyword where.
     */
    String SQL_KEYWORD_HAVING = "having";

    /**
     * The sql keyword where.
     */
    String SQL_KEYWORD_LIMIT = "limit";

    String SQL_KEYWORD_OFFSET = "offset";

    String SQL_KEYWORD_FETCH = "fetch";

    String SQL_KEYWORD_FOR = "for";

    String SQL_KEYWORD_WHILE = "while";

    String SQL_KEYWORD_CURSOR = "cursor";

    String SQL_KEYWORD_WINDOW = "window";

    String SQL_KEYWORD_RETURNS = "returns";

    String SQL_KEYWORD_RETURN = "return";

    /**
     * The sql keyword where.
     */
    String SQL_KEYWORD_WHEN = "when";

    /**
     * The sql keyword where.
     */
    String SQL_KEYWORD_THEN = "then";

    /**
     * The sql keyword where.
     */
    String SQL_KEYWORD_DEFAULT = "default";

    String SQL_KEYWORD_VALUES = "values";

    String SQL_KEYWORD_RETURNING = "returning";

    String SQL_KEYWORD_USING = "using";

    String SQL_KEYWORD_ONLY = "only";

    String SQL_KEYWORD_SET = "set";

    String SQL_KEYWORD_MERGE = "merge";

    /**
     * The sql keywork end.
     */
    String SQL_KEYWORK_EXCEPTION = "exception";

}
