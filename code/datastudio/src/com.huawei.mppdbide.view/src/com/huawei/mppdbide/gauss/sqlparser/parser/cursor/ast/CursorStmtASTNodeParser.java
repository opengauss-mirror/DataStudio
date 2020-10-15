/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.cursor.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.cursor.TCursorASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;

/**
 * Title: IfElseStmtASTNodeParser Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
 */
public class CursorStmtASTNodeParser extends BasicASTNodeParser {

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TCursorASTNode();
    }

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_CURSOR;
    }

    /**
     * Gets the node list parser.
     *
     * @return the node list parser
     */
    @Override
    public AbstractNodeListParser getNodeListParser() {
        Set<String> asList = new HashSet<>(Arrays.asList(SQLFoldingConstants.SQL_BRACKET_END));
        return new SelectResultListParser(asList);
    }

    /**
     * Prepare AST other stmt object.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTOtherStmtObject(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {
        TCursorASTNode orderByAstNode = (TCursorASTNode) fromAstNode;

        Set<String> onEndList = new HashSet<String>(Arrays.asList(SQLFoldingConstants.SQL_BRACKET_START));
        TExpression parseAndGetExpression = NodeExpressionConverter.parseAndGetExpression(listIterator, onEndList);
        parseAndGetExpression.setAddSpaceForCustomStmt(Boolean.TRUE);
        orderByAstNode.setCursorExpression(parseAndGetExpression);

        orderByAstNode
                .setParamStartBracket(ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_BRACKET_START));

    }

    /**
     * Prepare AST list item.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTListItem(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {
        TCursorASTNode orderByAstNode = (TCursorASTNode) fromAstNode;
        if (null != orderByAstNode.getParamStartBracket()) {
            super.prepareASTListItem(listIterator, fromAstNode);
        }
    }

    /**
     * Prepare AST after list item.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTAfterListItem(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {
        TCursorASTNode orderByAstNode = (TCursorASTNode) fromAstNode;
        orderByAstNode.setParamEndBracket(ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_BRACKET_END));

        orderByAstNode.setForOrIs(
                ParserUtils.handleToken(listIterator, Arrays.asList(SQLFoldingConstants.SQL_KEYWORD_FOR, "is")));

        TExpression cursorStmts = NodeExpressionConverter.parseAndGetExpression(listIterator, null);

        orderByAstNode.setCursorStmts(cursorStmts);

    }

}
