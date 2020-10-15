/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.insert;

import java.util.HashMap;
import java.util.Map;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractStmtParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.CTEASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.ReturningASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.WhereASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.insert.ast.InsertDefaultValuesASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.insert.ast.InsertIntoASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.insert.ast.InsertValuesListASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TCustomASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.dml.TInsertSqlStatement;

/**
 * 
 * Title: InsertStmtParser
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
 */
public class InsertStmtParser extends AbstractStmtParser {

    private static Map<String, Class<?>> astNodeParserMap = null;

    /**
     * Instantiates a new insert stmt parser.
     */
    public InsertStmtParser() {
        if (null == astNodeParserMap) {
            astNodeParserMap = new HashMap<String, Class<?>>(10);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_INSERT, InsertIntoASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_DEFAULT, InsertDefaultValuesASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_VALUES, InsertValuesListASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_RETURNING, ReturningASTNodeParser.class);
            astNodeParserMap.put(SQLFoldingConstants.SQL_KEYWORD_WHERE, WhereASTNodeParser.class);

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
        return new TInsertSqlStatement();
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
