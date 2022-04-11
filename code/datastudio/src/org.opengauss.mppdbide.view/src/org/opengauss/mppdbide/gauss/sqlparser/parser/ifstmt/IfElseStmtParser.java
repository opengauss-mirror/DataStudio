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

package org.opengauss.mppdbide.gauss.sqlparser.parser.ifstmt;

import java.util.HashMap;
import java.util.Map;

import org.opengauss.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import org.opengauss.mppdbide.gauss.sqlparser.parser.AbstractStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ifstmt.ast.IfElseStmtASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.condition.TIfElseSqlStatement;

/**
 * Title: IfElseStmtParser
 *
 * @since 3.0.0
 */
public class IfElseStmtParser extends AbstractStmtParser {

    private static Map<String, Class<?>> astNodeParserMap = null;

    public IfElseStmtParser() {
        if (null == astNodeParserMap) {
            astNodeParserMap = new HashMap<String, Class<?>>(10);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_IF, IfElseStmtASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_ELSE, IfElseStmtASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_ELSIF, IfElseStmtASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_ELSEIF, IfElseStmtASTNodeParser.class);
        }
    }

    @Override
    public TCustomSqlStatement getCustomSqlStatement() {
        return new TIfElseSqlStatement();
    }

    @Override
    protected Map<String, Class<?>> getAstNodeParserMap() {
        return astNodeParserMap;
    }

}
