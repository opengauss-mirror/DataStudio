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

package org.opengauss.mppdbide.gauss.sqlparser.parser.begin.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.opengauss.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ParserUtils;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.begin.nodelist.WhenItemListParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.FullStatementConverter;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.NodeListParserConverter;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.begin.TBeginASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;

/**
 * Title: BeginASTNodeParser
 * 
 * @since 3.0.0
 */
public class BeginASTNodeParser extends BasicASTNodeParser {

    /**
     * Prepare AST stmt object.
     *
     * @param listIterator the list iterator
     * @return the t begin AST node
     */
    @Override
    public TBeginASTNode prepareASTStmtObject(ListIterator<ISQLTokenData> listIterator) {

        TBeginASTNode beginAstNode = new TBeginASTNode();

        TSqlNode lBegin = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_KEYWORK_BEGIN);
        beginAstNode.setKeywordNode(lBegin);

        Set<String> newKeyList = new HashSet<String>();
        newKeyList.add(SQLFoldingConstants.SQL_KEYWORK_END);
        newKeyList.add(SQLFoldingConstants.SQL_KEYWORK_EXCEPTION);

        TFullStmt fullStmt = FullStatementConverter.parseAndGetFullStmt(listIterator, newKeyList);
        beginAstNode.setFullStmt(fullStmt);

        TSqlNode exception = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_KEYWORK_EXCEPTION);
        beginAstNode.setException(exception);

        if (null != exception) {
            // handle when list

            Set<String> lineBreakSet = new HashSet<String>(Arrays.asList(SQLFoldingConstants.SQL_KEYWORK_END));

            WhenItemListParser lWhenItemListParser = new WhenItemListParser(lineBreakSet);

            NodeListParserConverter.handleSelectList(listIterator, lWhenItemListParser);
            beginAstNode.setExceptionWhenList(lWhenItemListParser.getItemList());
        }

        TSqlNode lend = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_KEYWORK_END);
        beginAstNode.setEnd(lend);

        TExpression expression = NodeExpressionConverter.parseAndGetExpression(listIterator, getKeywordList());
        beginAstNode.setEndExpression(expression);

        return beginAstNode;
    }

    /**
     * Prepare AST other stmt object.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTOtherStmtObject(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {

        TBeginASTNode beginAstNode = (TBeginASTNode) fromAstNode;

        Set<String> lineBreakSet = new HashSet<String>(Arrays.asList(SQLFoldingConstants.SQL_KEYWORK_END));

        beginAstNode.setFullStmt(FullStatementConverter.parseAndGetFullStmt(listIterator, lineBreakSet));

        // code to parse all the statements

        beginAstNode.setEnd(ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_KEYWORK_END));

        beginAstNode.setEndExpression(NodeExpressionConverter.parseAndGetExpression(listIterator, null));
    }

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TBeginASTNode();
    }

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORK_BEGIN;
    }

    /**
     * Gets the node list parser.
     *
     * @return the node list parser
     */
    @Override
    public AbstractNodeListParser getNodeListParser() {
        return null;
    }

}
