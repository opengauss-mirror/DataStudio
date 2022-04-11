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

package org.opengauss.mppdbide.gauss.sqlparser.parser.init;

import org.opengauss.mppdbide.gauss.sqlparser.SQLTokenConstants;
import org.opengauss.mppdbide.gauss.sqlparser.parser.begin.BeginStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.begin.DeclareStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.casestmt.CaseStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.create.CreateStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.cursor.CursorStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.delete.DeleteStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.forloop.ForLoopStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.handler.ParserHandlerConfig;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ifstmt.IfElseStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.insert.InsertStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.loop.LoopStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.merge.MergeStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.select.SelectStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.union.UnionStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.update.UpdateStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.with.WithStmtParser;

/**
 * Title: ParserInitilizer
 *
 * @since 3.0.0
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
