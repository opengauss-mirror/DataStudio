/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.insert.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.condition.ConditionBreakIf;
import com.huawei.mppdbide.gauss.sqlparser.parser.insert.nodelist.InsertIntoListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertIntoASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: InsertIntoASTNodeParser
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
public class InsertIntoASTNodeParser extends BasicASTNodeParser {

    /**
     * Prepare AST other stmt object.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTOtherStmtObject(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {
        TInsertIntoASTNode groupByAstNode = (TInsertIntoASTNode) fromAstNode;
        TSqlNode into = ParserUtils.handleToken(listIterator, "into");
        groupByAstNode.setInto(into);

        // prepare the table name in insert

        Set<String> endTableName = new HashSet<String>(
                Arrays.asList(SQLFoldingConstants.SQL_BRACKET_START, SQLFoldingConstants.SQL_KEYWORK_VALUES));

        endTableName.addAll(getKeywordList());

        groupByAstNode.setTableName(
                NodeExpressionConverter.parseAndGetExpression(listIterator, endTableName, new ConditionBreakIf() {
                    @Override
                    public boolean isBreakCondition(ISQLTokenData next, ListIterator<ISQLTokenData> listIterator) {
                        if (null != next.getSubTokenBean()) {
                            return true;
                        }
                        return false;
                    }
                }));

        TSqlNode startBracket = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_BRACKET_START);

        groupByAstNode.setStartInsertAstBracket(startBracket);

    }

    /**
     * Prepare AST after list item.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTAfterListItem(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {
        TInsertIntoASTNode groupByAstNode = (TInsertIntoASTNode) fromAstNode;
        TSqlNode endBracket = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_BRACKET_END);

        groupByAstNode.setEndInsertAstBracket(endBracket);
    }

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TInsertIntoASTNode();
    }

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_INSERT;
    }

    /**
     * Gets the node list parser.
     *
     * @return the node list parser
     */
    @Override
    public AbstractNodeListParser getNodeListParser() {
        Set<String> asList = new HashSet<>(
                Arrays.asList(SQLFoldingConstants.SQL_BRACKET_END, SQLFoldingConstants.SQL_KEYWORK_VALUES));
        asList.addAll(getKeywordList());
        return new InsertIntoListParser(asList);
    }
}
