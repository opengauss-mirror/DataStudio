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

package org.opengauss.mppdbide.gauss.sqlparser.parser.ifstmt.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.opengauss.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ParserUtils;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.FullStatementConverter;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import org.opengauss.mppdbide.gauss.sqlparser.parser.select.nodelist.WhereCluaseListParser;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.ifstmt.TIfElseASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: IfElseStmtASTNodeParser
 *
 * @since 3.0.0
 */
public class IfElseStmtASTNodeParser extends BasicASTNodeParser {

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TIfElseASTNode();
    }

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_IF;
    }

    /**
     * Gets the keyword token.
     *
     * @param listIterator the list iterator
     * @return the keyword token
     */
    protected TSqlNode getKeywordToken(ListIterator<ISQLTokenData> listIterator) {

        return ParserUtils.handleToken(listIterator, Arrays.asList(SQLFoldingConstants.SQL_KEYWORD_ELSIF,
                SQLFoldingConstants.SQL_KEYWORD_ELSE, SQLFoldingConstants.SQL_KEYWORD_IF));
    }

    /**
     * Prepare AST list item.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTListItem(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {

        TIfElseASTNode orderByAstNode = (TIfElseASTNode) fromAstNode;

        TSqlNode startNode = (TSqlNode) orderByAstNode.getStartNode();

        if (null != startNode && SQLFoldingConstants.SQL_KEYWORD_ELSE.equalsIgnoreCase(startNode.getNodeText())) {
            return;
        }

        TParseTreeNodeList<?> handleFromList = handleFromList(listIterator);

        if (null != handleFromList) {
            fromAstNode.setItemList(handleFromList);
        }

    }

    /**
     * Gets the node list parser.
     *
     * @return the node list parser
     */
    @Override
    public AbstractNodeListParser getNodeListParser() {
        Set<String> lineBreakSet = new HashSet<String>(
                Arrays.asList(SQLFoldingConstants.SQL_KEYWORK_END, SQLFoldingConstants.SQL_KEYWORD_THEN));
        return new WhereCluaseListParser(lineBreakSet);
    }

    /**
     * Prepare AST after list item.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTAfterListItem(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {
        TIfElseASTNode orderByAstNode = (TIfElseASTNode) fromAstNode;

        TSqlNode then = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_KEYWORD_THEN);
        orderByAstNode.setThen(then);

        Set<String> lineBreakSet = new HashSet<String>(Arrays.asList(SQLFoldingConstants.SQL_KEYWORD_ELSIF,
                SQLFoldingConstants.SQL_KEYWORD_ELSE, SQLFoldingConstants.SQL_KEYWORK_END));

        orderByAstNode.setFullStmt(FullStatementConverter.parseAndGetFullStmt(listIterator, lineBreakSet));

        // code to parse all the statements

        // if still have tokens those are with END IF

        TSqlNode end = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_KEYWORK_END);
        orderByAstNode.setEnd(end);

        orderByAstNode.setEndNode(NodeExpressionConverter.parseAndGetExpression(listIterator, null));

    }

}
