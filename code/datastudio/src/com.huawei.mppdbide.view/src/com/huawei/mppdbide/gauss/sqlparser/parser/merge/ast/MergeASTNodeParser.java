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

package com.huawei.mppdbide.gauss.sqlparser.parser.merge.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.comm.ISQLSyntax;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.WhereCluaseListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.merge.TMergeASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: WithASTNodeParser
 *
 * @since 3.0.0
 */
public class MergeASTNodeParser extends BasicASTNodeParser {

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TMergeASTNode();
    }

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_MERGE;
    }

    /**
     * Gets the keyword token.
     *
     * @param listIterator the list iterator
     * @return the keyword token
     */
    protected TSqlNode getKeywordToken(ListIterator<ISQLTokenData> listIterator) {
        return ParserUtils.handleToken(listIterator, getKeywordTokenStr(), false);
    }

    /**
     * Prepare AST other stmt object.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTOtherStmtObject(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {

        TMergeASTNode orderByAstNode = (TMergeASTNode) fromAstNode;

        if (listIterator.hasNext()) {
            ISQLTokenData sqlTokenData = listIterator.next();

            if (null == sqlTokenData.getSubTokenBean()
                    && sqlTokenData.getToken().getData() == ISQLSyntax.SQL_MULTILINE_COMMENT) {
                TSqlNode lSqlHint = new TSqlNode();
                lSqlHint.setNodeText(sqlTokenData.getTokenStr());
                ParserUtils.addCommentsR(listIterator, lSqlHint);
                orderByAstNode.setHintInfo(lSqlHint);
            } else {
                listIterator.previous();
            }
        } else {
            ParserUtils.addCommentsR(listIterator, orderByAstNode.getKeywordNode());
        }

        orderByAstNode.setInto(ParserUtils.handleToken(listIterator, "into"));

        Set<String> endTableName = new HashSet<String>(Arrays.asList("using"));
        endTableName.addAll(getKeywordList());

        orderByAstNode.setSrcTable(NodeExpressionConverter.parseAndGetExpression(listIterator, endTableName));

        orderByAstNode.setUsing(ParserUtils.handleToken(listIterator, "using"));

        Set<String> onEndList = new HashSet<String>(Arrays.asList("on"));
        onEndList.addAll(getKeywordList());

        orderByAstNode.setDestTable(NodeExpressionConverter.parseAndGetExpression(listIterator, onEndList));

        orderByAstNode.setOn(ParserUtils.handleToken(listIterator, "on"));

        orderByAstNode.setOnStartBracket(ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_BRACKET_START));

    }

    /**
     * Gets the node list parser.
     *
     * @return the node list parser
     */
    @Override
    public AbstractNodeListParser getNodeListParser() {
        Set<String> lineBreakSet = new HashSet<String>(Arrays.asList(SQLFoldingConstants.SQL_BRACKET_END));
        lineBreakSet.addAll(getKeywordList());
        return new WhereCluaseListParser(lineBreakSet);
    }

    /**
     * Prepare AST after list item.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTAfterListItem(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {
        TMergeASTNode orderByAstNode = (TMergeASTNode) fromAstNode;
        orderByAstNode.setOnEndBracket(ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_BRACKET_END));
    }

}
