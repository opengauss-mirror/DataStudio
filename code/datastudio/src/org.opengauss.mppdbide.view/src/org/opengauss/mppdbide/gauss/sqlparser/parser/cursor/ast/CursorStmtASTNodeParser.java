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

package org.opengauss.mppdbide.gauss.sqlparser.parser.cursor.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.opengauss.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ParserUtils;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import org.opengauss.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.cursor.TCursorASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;

/**
 * Title: IfElseStmtASTNodeParser
 *
 * @since 3.0.0
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
