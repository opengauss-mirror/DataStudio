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

package org.opengauss.mppdbide.gauss.sqlparser.parser.with;

import java.util.HashMap;
import java.util.Map;

import org.opengauss.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import org.opengauss.mppdbide.gauss.sqlparser.parser.AbstractASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.AbstractStmtParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.CTEASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.with.ast.WithASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TCustomASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.dml.TWithSqlStatement;

/**
 * 
 * Title: WithStmtParser
 *
 * @since 3.0.0
 */
public class WithStmtParser extends AbstractStmtParser {

    private static Map<String, Class<?>> astNodeParserMap = null;

    /**
     * Instantiates a new with stmt parser.
     */
    public WithStmtParser() {
        if (null == astNodeParserMap) {
            astNodeParserMap = new HashMap<String, Class<?>>(10);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_WITH, WithASTNodeParser.class);
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

        AbstractASTNodeParser<TCustomASTNode> astParser = super.getASTParser(tokenStr, astParserMap);

        if (null == astParser) {
            AbstractASTNodeParser lAbstractASTNodeParser = new CTEASTNodeParser();

            return lAbstractASTNodeParser;
        }

        return astParser;
    }

    /**
     * Gets the custom sql statement.
     *
     * @return the custom sql statement
     */
    @Override
    public TCustomSqlStatement getCustomSqlStatement() {
        return new TWithSqlStatement();
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
