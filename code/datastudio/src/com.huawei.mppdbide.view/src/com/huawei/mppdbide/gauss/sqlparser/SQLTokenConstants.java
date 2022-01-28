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

package com.huawei.mppdbide.gauss.sqlparser;

/**
 * 
 * Title: SQLFoldingConstants
 *
 * @since 3.0.0
 */
public interface SQLTokenConstants {

    /**
     * The t sql nonexist.
     */
    int T_SQL_NONEXIST = -1;

    /**
     * The t sql dummy.
     */
    int T_SQL_DUMMY = -2;

    /**
     * The t sql unknown.
     */
    int T_SQL_UNKNOWN = 1000;

    /**
     * The t sql block begin.
     */
    int T_SQL_BLOCK_BEGIN = 1001;

    /**
     * The t sql block end.
     */
    int T_SQL_BLOCK_END = 1002;

    /**
     * The t sql delimiter.
     */
    int T_SQL_DELIMITER = 1007;

    /**
     * The t sql dml select.
     */
    int T_SQL_DML_SELECT = 1008;

    /**
     * The t sql dml insert.
     */
    int T_SQL_DML_INSERT = 1009;

    /**
     * The t sql dml delete.
     */
    int T_SQL_DML_DELETE = 1010;

    /**
     * The t sql dml update.
     */
    int T_SQL_DML_UPDATE = 1011;

    /**
     * The t sql dml truncate.
     */
    int T_SQL_DML_TRUNCATE = 1012;

    /**
     * The t sql ddl create.
     */
    int T_SQL_DDL_CREATE = 1013;

    /**
     * The t sql ddl create func.
     */
    int T_SQL_DDL_CREATE_FUNC = 1014;

    /**
     * The t sql ddl create proc.
     */
    int T_SQL_DDL_CREATE_PROC = 1015;

    /**
     * The t sql ddl create table.
     */
    int T_SQL_DDL_CREATE_TABLE = 1016;

    /**
     * The t sql ddl control if.
     */
    int T_SQL_DDL_CONTROL_IF = 1017;

    /**
     * The t sql ddl control else.
     */
    int T_SQL_DDL_CONTROL_ELSE = 1018;

    /**
     * The t sql ddl control elsif.
     */
    int T_SQL_DDL_CONTROL_ELSIF = 1019;

    /**
     * The t sql bracket begin.
     */
    int T_SQL_BRACKET_BEGIN = 1020;

    /**
     * The t sql bracket end.
     */
    int T_SQL_BRACKET_END = 1021;

    /**
     * The t sql function end.
     */
    int T_SQL_FUNCTION_END = 1022;

    /**
     * The t sql dml insert values.
     */
    int T_SQL_DML_INSERT_VALUES = 1023;

    /**
     * The t sql dml with.
     */
    int T_SQL_DML_WITH = 1024;

    /**
     * The t sql block declare.
     */
    int T_SQL_BLOCK_DECLARE = 1025;

    /**
     * The t sql ddl alter.
     */
    int T_SQL_DDL_ALTER = 1026;

    /**
     * The t sql keywork view.
     */
    int T_SQL_KEYWORK_VIEW = 1027;

    /**
     * The t sql keywork language.
     */
    int T_SQL_KEYWORK_LANGUAGE = 1028;

    /**
     * The t sql keywork as.
     */
    int T_SQL_KEYWORK_AS = 1029;

    /**
     * The t sql keywork union.
     */
    int T_SQL_KEYWORK_UNION = 1030;

    /**
     * The t sql keywork intersect.
     */
    int T_SQL_KEYWORK_INTERSECT = 1031;

    /**
     * The t sql keywork except.
     */
    int T_SQL_KEYWORK_EXCEPT = 1032;

    /**
     * The t sql keywork case.
     */
    int T_SQL_KEYWORK_CASE = 1033;

    /**
     * The t sql loop.
     */
    int T_SQL_LOOP = 1034;

    /**
     * The t sql trigger.
     */
    int T_SQL_TRIGGER = 1035;

    /**
     * The t sql drop.
     */
    int T_SQL_DROP = 1036;

    /**
     * The t sql grant.
     */
    int T_SQL_GRANT = 1037;

    /**
     * The t sql revoke.
     */
    int T_SQL_REVOKE = 1038;

    /**
     * The t sql keywork is.
     */
    int T_SQL_KEYWORK_IS = 1039;

    /**
     * The t sql delimiter fslash.
     */
    int T_SQL_DELIMITER_FSLASH = 1040;

    /**
     * The t sql function end.
     */
    int T_SQL_NUMBER = 1041;

    int T_SQL_KEYWORD_FOR = 1042;

    int T_SQL_KEYWORD_CURSOR = 1043;

    int T_SQL_MERGE = 1044;

    int T_SQL_WHEN = 1045;

    /**
     * The t sql ddl create package.
     */
    int T_SQL_DDL_CREATE_PACKAGE = 1046;

    /**
     * The t sql ddl create package body.
     */
    int T_SQL_DDL_CREATE_PACKAGE_BODY = 1047;

}
