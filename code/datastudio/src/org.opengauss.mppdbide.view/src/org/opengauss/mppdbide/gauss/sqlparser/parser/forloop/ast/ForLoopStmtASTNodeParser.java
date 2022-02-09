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

package org.opengauss.mppdbide.gauss.sqlparser.parser.forloop.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.opengauss.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import org.opengauss.mppdbide.gauss.sqlparser.SQLTokenConstants;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ParserUtils;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.condition.ConditionBreakIf;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import org.opengauss.mppdbide.gauss.sqlparser.parser.select.nodelist.WhereCluaseListParser;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.forloop.TForLoopASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: IfElseStmtASTNodeParser
 *
 * @since 3.0.0
 */
public class ForLoopStmtASTNodeParser extends BasicASTNodeParser {

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TForLoopASTNode();
    }

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_FOR;
    }

    /**
     * Gets the keyword token.
     *
     * @param listIterator the list iterator
     * @return the keyword token
     */
    protected TSqlNode getKeywordToken(ListIterator<ISQLTokenData> listIterator) {

        return ParserUtils.handleToken(listIterator,
                Arrays.asList(SQLFoldingConstants.SQL_KEYWORD_FOR, SQLFoldingConstants.SQL_KEYWORD_WHILE));
    }

    /**
     * Gets the node list parser.
     *
     * @return the node list parser
     */
    @Override
    public AbstractNodeListParser getNodeListParser() {
        Set<String> lineBreakSet = new HashSet<String>();
        return new WhereCluaseListParser(lineBreakSet) {
            @Override
            public boolean isListBreakWithCustomSQL(String previousNotEmptyToken,
                    SQLStmtTokenListBean sqlStmtTokenListBean) {
                if (null != sqlStmtTokenListBean
                        && sqlStmtTokenListBean.getStatementType() == SQLTokenConstants.T_SQL_LOOP) {
                    return true;
                }
                return false;
            }

        };
    }

    /**
     * Prepare AST other stmt object.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTOtherStmtObject(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {

        TSqlNode startNode = (TSqlNode) fromAstNode.getStartNode();

        if (null != startNode && SQLFoldingConstants.SQL_KEYWORD_WHERE.equalsIgnoreCase(startNode.getNodeText())) {
            return;
        }

        TForLoopASTNode orderByAstNode = (TForLoopASTNode) fromAstNode;
        TExpression parseAndGetExpression = NodeExpressionConverter.parseAndGetExpression(listIterator, null,
                new ConditionBreakIf() {
                    @Override
                    public boolean isBreakCondition(ISQLTokenData next, ListIterator<ISQLTokenData> listIterator) {
                        if (null != next.getSubTokenBean()
                                && next.getSubTokenBean().getStatementType() == SQLTokenConstants.T_SQL_LOOP) {
                            return true;
                        }
                        return false;
                    }
                });
        parseAndGetExpression.setAddSpaceForCustomStmt(Boolean.TRUE);
        orderByAstNode.setTargetName(parseAndGetExpression);
    }

    /**
     * Prepare AST list item.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTListItem(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {

        TForLoopASTNode orderByAstNode = (TForLoopASTNode) fromAstNode;

        TSqlNode startNode = (TSqlNode) orderByAstNode.getStartNode();

        if (null != startNode && SQLFoldingConstants.SQL_KEYWORD_FOR.equalsIgnoreCase(startNode.getNodeText())) {
            return;
        }

        TParseTreeNodeList<?> handleFromList = handleFromList(listIterator);

        if (null != handleFromList) {
            fromAstNode.setItemList(handleFromList);
        }

    }

}
