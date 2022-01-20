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

package com.huawei.mppdbide.gauss.sqlparser.parser.casestmt.nodelist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeListParserConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.WhereCluaseListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TWhereClause;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.casenode.TCaseExprList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.casenode.TCaseStmtExpr;

/**
 * 
 * Title: CaseItemListParser
 *
 * @since 3.0.0
 */
public class CaseItemListParser extends AbstractNodeListParser {

    /** 
     * The list item. 
     */
    protected TCaseStmtExpr listItem = null;

    /** 
     * The condition expr.
     */
    protected TWhereClause conditionExpr = null;

    /** 
     * The end node. 
     */
    protected TExpression endNode = null;

    private TCaseExprList resultColumnList = new TCaseExprList();

    /**
     * Instantiates a new case item list parser.
     *
     * @param lineBreakSet the line break set
     */
    public CaseItemListParser(Set<String> lineBreakSet) {
        super(lineBreakSet);
    }

    /**
     * Creates the T object.
     */
    public void createTObject() {
        // create and then it is the start of the expression

    }

    private void createLocalObject() {
        // create and then it is the start of the expression
        if (null == listItem) {
            listItem = new TCaseStmtExpr();

            resultColumnList.addResultColumn(listItem);
        }
    }

    /**
     * Handle node alias.
     *
     * @param listIterator the list iterator
     * @param next the next
     */
    public void handleNodeAlias(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {
        // close the TExpressionNode
        TSqlNode thenNode = new TSqlNode();
        thenNode.setNodeText(next.getTokenStr());
        ParserUtils.addCommentsR(listIterator, thenNode);
        listItem.setThen(thenNode);

        prepareEndNode(listIterator);

    }

    private void prepareEndNode(ListIterator<ISQLTokenData> listIterator) {
        Set<String> lineBreakSet = new HashSet<String>(Arrays.asList(SQLFoldingConstants.SQL_KEYWORK_END,
                SQLFoldingConstants.SQL_KEYWORD_WHEN, SQLFoldingConstants.SQL_KEYWORD_ELSE));

        TExpression parseAndGetExpression = NodeExpressionConverter.parseAndGetExpression(listIterator, lineBreakSet);

        listItem.setEndNode(parseAndGetExpression);
    }

    /**
     * Handle node end.
     *
     * @param listIterator the list iterator
     * @param next the next
     */
    public void handleNodeEnd(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {

        listItem = null;

        createLocalObject();

        TSqlNode whenOrElse = new TSqlNode();
        whenOrElse.setNodeText(next.getTokenStr());
        ParserUtils.addCommentsR(listIterator, whenOrElse);
        listItem.setWhenOrElse(whenOrElse);

        if (SQLFoldingConstants.SQL_KEYWORD_ELSE.equalsIgnoreCase(next.getTokenStr())) {
            prepareEndNode(listIterator);
        } else {
            prepareWhenCondition(listIterator);
        }

    }

    private void prepareWhenCondition(ListIterator<ISQLTokenData> listIterator) {
        Set<String> lineBreakSet = new HashSet<String>(
                Arrays.asList(SQLFoldingConstants.SQL_KEYWORK_END, SQLFoldingConstants.SQL_KEYWORD_THEN));
        lineBreakSet.addAll(listBreak);

        WhereCluaseListParser lWhereCluaseListParser = new WhereCluaseListParser(lineBreakSet);

        NodeListParserConverter.handleSelectList(listIterator, lWhereCluaseListParser);
        conditionExpr = (TWhereClause) lWhereCluaseListParser.getItemList();

        listItem.setConditionExpr(conditionExpr);
    }

    /**
     * Gets the item list.
     *
     * @return the item list
     */
    @Override
    public TParseTreeNodeList<?> getItemList() {
        return resultColumnList;
    }

    /**
     * Creates the T node.
     *
     * @param listIterator the list iterator
     * @param next the next
     * @param paramCount the param count
     */
    public void createTNode(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next, int paramCount) {

    }

    /**
     * Checks if is node end.
     *
     * @param nodeStr the node str
     * @return true, if is node end
     */
    public boolean isNodeEnd(String nodeStr) {
        return "WHEN".equalsIgnoreCase(nodeStr) || "ELSE".equalsIgnoreCase(nodeStr);
    }

    /**
     * Checks if is alias name.
     *
     * @param nodeStr the node str
     * @param previousNotEmptyToken the previous not empty token
     * @return true, if is alias name
     */
    public boolean isAliasName(String nodeStr, String previousNotEmptyToken) {
        return "THEN".equalsIgnoreCase(nodeStr);
    }

    /**
     * Creates the T custom stmt node.
     *
     * @param customSqlStmt the custom sql stmt
     * @param listIterator the list iterator
     * @param next the next
     */
    @Override
    public void createTCustomStmtNode(TParseTreeNode customSqlStmt, ListIterator<ISQLTokenData> listIterator,
            ISQLTokenData next) {
    }

    /**
     * Handle start end node.
     *
     * @param previousNotEmptyToken the previous not empty token
     * @param next the next
     * @param listIterator the list iterator
     */
    public void handleStartEndNode(String previousNotEmptyToken, ISQLTokenData next,
            ListIterator<ISQLTokenData> listIterator) {
    }

}
