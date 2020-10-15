/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.begin;

import java.util.HashMap;
import java.util.Map;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.begin.ast.BeginASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.block.TBeginSqlStatement;

/**
 * Title: BeginStmtParser
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 30-Dec-2019]
 * @since 30-Dec-2019
 */

public class BeginStmtParser extends AbstractStmtParser {
    private static Map<String, Class<?>> astNodeParserMap = null;

    /**
     * Instantiates a new begin stmt parser.
     */
    public BeginStmtParser() {
        if (null == astNodeParserMap) {
            astNodeParserMap = new HashMap<String, Class<?>>(10);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORK_BEGIN, BeginASTNodeParser.class);
        }
    }

    /**
     * Gets the custom sql statement.
     *
     * @return the custom sql statement
     */
    @Override
    public TCustomSqlStatement getCustomSqlStatement() {
        return new TBeginSqlStatement();
    }

    /**
     * Gets the ast node parser map.
     *
     * @return the ast node parser map
     */
    @Override
    protected Map<String, Class<?>> getAstNodeParserMap() {
        return astNodeParserMap;
    }

}
