/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.init;

import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.parser.begin.BeginStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.begin.DeclareStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.casestmt.CaseStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.create.CreateStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.cursor.CursorStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.delete.DeleteStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.forloop.ForLoopStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.handler.ParserHandlerConfig;
import com.huawei.mppdbide.gauss.sqlparser.parser.ifstmt.IfElseStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.insert.InsertStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.loop.LoopStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.merge.MergeStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.SelectStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.union.UnionStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.update.UpdateStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.with.WithStmtParser;

/**
 * Title: ParserInitilizer Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public final class ParserInitilizer {

    /**
     * Inits the parser.
     */
    public static void initParser() {

        if (!ParserHandlerConfig.getInstance().isInitilized()) {
            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_DML_SELECT, new SelectStmtParser());
            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_DML_WITH, new WithStmtParser());
            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_KEYWORK_CASE, new CaseStmtParser());

            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_DML_INSERT, new InsertStmtParser());
            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_DML_DELETE, new DeleteStmtParser());
            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_DML_UPDATE, new UpdateStmtParser());

            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_DDL_CREATE, new CreateStmtParser());
            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_DDL_CREATE_FUNC,
                    new CreateStmtParser());
            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_DDL_CREATE_PROC,
                    new CreateStmtParser());
            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_BLOCK_DECLARE,
                    new DeclareStmtParser());
            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_BLOCK_BEGIN, new BeginStmtParser());

            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_DDL_CONTROL_IF,
                    new IfElseStmtParser());
            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_DDL_CONTROL_ELSE,
                    new IfElseStmtParser());
            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_DDL_CONTROL_ELSIF,
                    new IfElseStmtParser());

            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_LOOP, new LoopStmtParser());

            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_KEYWORD_CURSOR,
                    new CursorStmtParser());

            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_KEYWORD_FOR,
                    new ForLoopStmtParser());

            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_KEYWORK_UNION,
                    new UnionStmtParser());

            ParserHandlerConfig.getInstance().addRuleHandle(SQLTokenConstants.T_SQL_MERGE, new MergeStmtParser());

            ParserHandlerConfig.getInstance().setInitilized(true);
        }

    }

}
