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

package com.huawei.mppdbide.gauss.sqlparser.parser.update;

import java.util.HashMap;
import java.util.Map;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.FromASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.ReturningASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.WhereASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.update.ast.UpdateSetASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.update.ast.UpdateASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.dml.TUpdateSqlStatement;

/**
 * 
 * Title: UpdateStmtParser
 *
 * @since 3.0.0
 */
public class UpdateStmtParser extends AbstractStmtParser {

    private static Map<String, Class<?>> astNodeParserMap = null;

    public UpdateStmtParser() {
        if (null == astNodeParserMap) {
            astNodeParserMap = new HashMap<String, Class<?>>(10);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_UPDATE, UpdateASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_SET, UpdateSetASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_FROM, FromASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_WHERE, WhereASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_RETURNING, ReturningASTNodeParser.class);

        }
    }

    @Override
    public TCustomSqlStatement getCustomSqlStatement() {
        return new TUpdateSqlStatement();
    }

    @Override
    protected Map<String, Class<?>> getAstNodeParserMap() {
        return astNodeParserMap;
    }

}
