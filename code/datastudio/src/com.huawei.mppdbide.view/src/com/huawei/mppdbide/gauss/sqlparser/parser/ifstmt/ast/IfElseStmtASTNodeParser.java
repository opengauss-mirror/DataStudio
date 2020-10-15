/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.ifstmt.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.FullStatementConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.WhereCluaseListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.ifstmt.TIfElseASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: IfElseStmtASTNodeParser
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
