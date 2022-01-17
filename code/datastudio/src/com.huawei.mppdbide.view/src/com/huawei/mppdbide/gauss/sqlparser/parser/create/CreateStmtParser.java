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

package com.huawei.mppdbide.gauss.sqlparser.parser.create;

import java.util.HashMap;
import java.util.Map;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.CTEASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.CustomASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.begin.ast.BeginASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.begin.ast.DeclareASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.create.ast.AsASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.create.ast.CreateASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.create.ast.LanguageASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.create.ast.ReturnsASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TCustomASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.debugobj.TCreateSqlStatement;

/**
 * 
 * Title: CreateStmtParser
 *
 * @since 3.0.0
 */
public class CreateStmtParser extends AbstractStmtParser {

    private static Map<String, Class<?>> astNodeParserMap = null;

    /**
     * Instantiates a new creates the stmt parser.
     */
    public CreateStmtParser() {
        if (null == astNodeParserMap) {
            astNodeParserMap = new HashMap<String, Class<?>>(10);
            astNodeParserMap.put(SQLFoldingConstants.SQL_CREATE, CreateASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_FUNCTION, CreateASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_PROCEDURE, CreateASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_RETURN, ReturnsASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_RETURNS, ReturnsASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORK_AS, AsASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORK_IS, AsASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_LANGUAGE, LanguageASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORK_DECLARE, DeclareASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORK_BEGIN, BeginASTNodeParser.class);
        }
    }

    /**
     * Gets the AST parser.
     *
     * @param tokenStr the token str
     * @param astParserMap the ast parser map
     * @return the AST parser
     */
    public AbstractASTNodeParser<TCustomASTNode> getASTParser(String tokenStr, Map<String, Class<?>> astParserMap) {
        AbstractASTNodeParser<TCustomASTNode> astNodeParser = super.getASTParser(tokenStr, astParserMap);
        if (null == astNodeParser) {
            AbstractASTNodeParser customastNodeParser = new CustomASTNodeParser();
            return customastNodeParser;
        }
        return astNodeParser;
    }

    /**
     * the getDefaultNodeParser
     */
    protected CTEASTNodeParser getDefaultNodeParser() {
        return new CustomASTNodeParser();
    }

    /**
     * Gets the custom sql statement.
     *
     * @return the custom sql statement
     */
    @Override
    public TCustomSqlStatement getCustomSqlStatement() {
        return new TCreateSqlStatement();
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
