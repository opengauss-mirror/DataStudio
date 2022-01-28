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

package com.huawei.mppdbide.gauss.sqlparser.parser.select;

import java.util.HashMap;
import java.util.Map;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.FromASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.WhereASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.ast.ConnectByASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.ast.FetchASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.ast.ForASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.ast.GroupByASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.ast.HavingASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.ast.LimitASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.ast.OffsetASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.ast.OrderByASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.ast.SelectASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.ast.StartWithASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.ast.WindowASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.dml.TSelectSqlStatement;

/**
 * 
 * Title: SelectStmtParser
 *
 * @since 3.0.0
 */
public class SelectStmtParser extends AbstractStmtParser {

    private static Map<String, Class<?>> astNodeParserMap = null;

    public SelectStmtParser() {
        if (null == astNodeParserMap) {
            astNodeParserMap = new HashMap<String, Class<?>>(10);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_SELECT, SelectASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_FROM, FromASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_WHERE, WhereASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_START, StartWithASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_CONNECT, ConnectByASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_GROUP, GroupByASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_ORDER, OrderByASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_HAVING, HavingASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_WINDOW, WindowASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_LIMIT, LimitASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_OFFSET, OffsetASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_FETCH, FetchASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_FOR, ForASTNodeParser.class);
        }
    }

    @Override
    public TCustomSqlStatement getCustomSqlStatement() {
        return new TSelectSqlStatement();
    }

    @Override
    protected Map<String, Class<?>> getAstNodeParserMap() {
        return astNodeParserMap;
    }

}
