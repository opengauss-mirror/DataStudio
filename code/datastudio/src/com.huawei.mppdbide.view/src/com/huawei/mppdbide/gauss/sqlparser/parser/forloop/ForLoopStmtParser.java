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

package com.huawei.mppdbide.gauss.sqlparser.parser.forloop;

import java.util.HashMap;
import java.util.Map;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.forloop.ast.ForLoopStmtASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.loop.TForLoopSqlStatement;

/**
 * Title: IfElseStmtParser
 *
 * @since 3.0.0
 */
public class ForLoopStmtParser extends AbstractStmtParser {

    private static Map<String, Class<?>> astNodeParserMap = null;

    public ForLoopStmtParser() {
        if (null == astNodeParserMap) {
            astNodeParserMap = new HashMap<String, Class<?>>(10);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_FOR, ForLoopStmtASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_WHILE, ForLoopStmtASTNodeParser.class);
        }
    }

    @Override
    public TCustomSqlStatement getCustomSqlStatement() {
        return new TForLoopSqlStatement();
    }

    @Override
    protected Map<String, Class<?>> getAstNodeParserMap() {
        return astNodeParserMap;
    }

}
