/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.comm;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ISQLSyntax.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
