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

package com.huawei.mppdbide.gauss.sqlparser.comm;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ISQLSyntax.
 *
 * @since 3.0.0
 */
public interface ISQLSyntax {

    /**
     * The sql comment.
     */
    String SQL_COMMENT = "sql_comment";

    /**
     * The sql multiline comment.
     */
    String SQL_MULTILINE_COMMENT = "sql_multiline_comment";

    /**
     * The sql code.
     */
    String SQL_CODE = "sql_code";

    /**
     * The single line comment.
     */
    String SINGLE_LINE_COMMENT = "sql_singleline_comment";

    /**
     * The sql partitioning.
     */
    String SQL_PARTITIONING = "___sql_partitioning";

    /**
     * The sql double quotes identifier.
     */
    String SQL_DOUBLE_QUOTES_IDENTIFIER = "sql_double_quotes_identifier";

    /**
     * The sql string.
     */
    String SQL_STRING = "sql_character";
}
