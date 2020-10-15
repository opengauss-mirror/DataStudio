/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeListParserConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.utils.ExpressionTypeEnum;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TWhereClause;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.from.TFromItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.from.TFromItemList;

/**
 * Title: FromItemListParser Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
 */
public class FromItemListParser extends AbstractNodeListParser {

    private TFromItemList fromItemList = new TFromItemList();

    private TFromItem fromItem = null;

    private TExpression table = null;

    private TExpression aliasExpression = null;

    private TExpression joinType = null;

    private TWhereClause joinCondition = null;

    private boolean inEndNode = false;

    /**
     * Instantiates a new from item list parser.
     *
     * @param lineBreakSet the line break set
     */
    public FromItemListParser(Set<String> lineBreakSet) {
        super(lineBreakSet);
    }

    /**
     * Creates the T object.
     */
    @Override
    public void createTObject() {
        if (fromItem == null) {
            fromItem = new TFromItem();
            table = new TExpression();

            fromItem.setTable(table);
            fromItemList.addResultColumn(fromItem);
        }
    }

    /**
     * Handle node alias.
     *
     * @param listIterator the list iterator
     * @param next the next
     */
    @Override
    public void handleNodeAlias(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {
        table = null;
        TSqlNode asNode = new TSqlNode();
        asNode.setNodeText(next.getTokenStr());
        fromItem.setAs(asNode);
        ParserUtils.addCommentsR(listIterator, asNode);

        addNodeEndExpression();
    }

    private void addNodeEndExpression() {
        table = null;
        aliasExpression = new TExpression();
        fromItem.setAliasName(aliasExpression);
        inEndNode = true;
    }

    /**
     * Handle node end.
     *
     * @param listIterator the list iterator
     * @param next the next
     */
    @Override
    public void handleNodeEnd(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {
        inEndNode = false;
        if (",".equalsIgnoreCase(next.getTokenStr())) {
            TSqlNode seperator = new TSqlNode();
            seperator.setNodeText(next.getTokenStr());
            ParserUtils.addCommentsR(listIterator, seperator);
            fromItem.setSeperator(seperator);
            fromItem = null;
            aliasExpression = null;
            return;
        }

        if ("JOIN".equalsIgnoreCase(next.getTokenStr())) {
            table = null;
            if (joinType == null) {
                joinType = new TExpression(ExpressionTypeEnum.LIST);
                fromItemList.setJoinStmt(true);
                fromItem.setJoinType(joinType);
            }
            ParserUtils.addExpressionNode(joinType, listIterator, next);
            joinType = null;
            fromItem = null;
            aliasExpression = null;
            return;
        }
    }

    /**
     * Creates the T node.
     *
     * @param listIterator the list iterator
     * @param next the next
     * @param paramCount the param count
     */
    @Override
    public void createTNode(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next, int paramCount) {
        String tokenStr = next.getTokenStr();

        if (paramCount == 0) {
            if ("ON".equalsIgnoreCase(tokenStr)) {
                table = null;
                TSqlNode onCluase = new TSqlNode();
                onCluase.setNodeText(tokenStr);
                fromItem.setOn(onCluase);
                ParserUtils.addCommentsR(listIterator, onCluase);
                joinCondition = new TWhereClause();

                Set<String> lineBreakSet = new HashSet<String>(
                        Arrays.asList("where", "left", "right", "full", "inner", "outer", ",", "join"));
                lineBreakSet.addAll(listBreak);

                WhereCluaseListParser lWhereCluaseListParser = new WhereCluaseListParser(lineBreakSet);

                NodeListParserConverter.handleSelectList(listIterator, lWhereCluaseListParser);
                joinCondition = (TWhereClause) lWhereCluaseListParser.getItemList();
                fromItem.setJoinCondition(joinCondition);
                joinCondition = null;

                return;
            }

            if ("LEFT".equalsIgnoreCase(tokenStr) || "RIGHT".equalsIgnoreCase(tokenStr)
                    || "FULL".equalsIgnoreCase(tokenStr) || "INNER".equalsIgnoreCase(tokenStr)
                    || "OUTER".equalsIgnoreCase(tokenStr)) {
                table = null;
                if (joinType == null) {
                    joinType = new TExpression(ExpressionTypeEnum.LIST);
                    fromItemList.setJoinStmt(true);
                    fromItem.setJoinType(joinType);
                }

                ParserUtils.addExpressionNode(joinType, listIterator, next);

                return;
            }

        }

        if (table != null) {
            TExpressionNode tableName = new TExpressionNode();
            tableName.getExpNode().setNodeText(next.getTokenStr());
            ParserUtils.addCommentsR(listIterator, tableName.getExpNode());
            table.addExpressionNode(tableName);
        } else if (null != aliasExpression) {
            ParserUtils.addExpressionNode(aliasExpression, listIterator, next);
        } else {
            throw new GaussDBSQLParserException("Unable to position the statement in From Cluase");
        }
    }

    /**
     * Checks if is node end.
     *
     * @param nodeStr the node str
     * @return true, if is node end
     */
    @Override
    public boolean isNodeEnd(String nodeStr) {
        return ",".equalsIgnoreCase(nodeStr) || "JOIN".equalsIgnoreCase(nodeStr);
    }

    /**
     * Checks if is alias name.
     *
     * @param nodeStr the node str
     * @param previousNotEmptyToken the previous not empty token
     * @return true, if is alias name
     */
    @Override
    public boolean isAliasName(String nodeStr, String previousNotEmptyToken) {
        return !isInEndNode() && "AS".equalsIgnoreCase(nodeStr);
    }

    /**
     * Sets the list break.
     *
     * @param listBreak the new list break
     */
    public void setListBreak(Set<String> listBreak) {
        this.listBreak = listBreak;
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

        if (null != table) {
            TExpressionNode expNode = new TExpressionNode();
            expNode.setCustomStmt(customSqlStmt);
            table.addExpressionNode(expNode);
            ParserUtils.addCommentsR(listIterator, customSqlStmt);
        } else {
            throw new GaussDBSQLParserException(
                    "Unable to position the statement in From Cluase createTCustomStmtNode");
        }

    }

    /**
     * Gets the item list.
     *
     * @return the item list
     */
    @Override
    public TParseTreeNodeList<?> getItemList() {
        return fromItemList;
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

        if (listIterator.hasPrevious()) {
            ISQLTokenData previous = listIterator.previous();
            if (listIterator.hasPrevious()) {
                previous = listIterator.previous();
                listIterator.next();
            }

            if (StringUtils.isBlank(previous.getTokenStr()) && ParserUtils.isMathOperator(previousNotEmptyToken)) {

            } else if (StringUtils.isBlank(previous.getTokenStr()) && ParserUtils.isMathOperator(next.getTokenStr())) {

            } else if (StringUtils.isBlank(previous.getTokenStr()) && null != table && table.getExpList().size() > 0) {
                addNodeEndExpression();
            }

            listIterator.next();

        }

    }

    /**
     * Checks if is in end node.
     *
     * @return true, if is in end node
     */
    public boolean isInEndNode() {
        return inEndNode;
    }

}
