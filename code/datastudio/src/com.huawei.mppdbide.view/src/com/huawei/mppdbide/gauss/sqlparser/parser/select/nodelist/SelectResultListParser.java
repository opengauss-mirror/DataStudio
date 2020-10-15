/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist;

import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TResultColumn;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TResultColumnList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: SelectResultListParser
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
public class SelectResultListParser extends AbstractNodeListParser {

    /**
     *  The result column. 
     */
    protected TResultColumn resultColumn = null;

    /**
     *  The expression. 
     */
    protected TExpression expression = null;

    /** 
     * The end node expression. 
     */
    protected TExpression endNodeExpression = null;

    /** 
     * The in end node.
     */
    protected boolean inEndNode = false;

    private TResultColumnList resultColumnList = new TResultColumnList();

    /**
     * Instantiates a new select result list parser.
     *
     * @param lineBreakSet the line break set
     */
    public SelectResultListParser(Set<String> lineBreakSet) {
        super(lineBreakSet);
    }

    /**
     * Creates the T object.
     */
    public void createTObject() {
        // create and then it is the start of the expression
        if (null == resultColumn) {
            resultColumn = getResultColumn();
            expression = new TExpression();
            endNodeExpression = null;
            resultColumn.setExpression(expression);

            resultColumnList.addResultColumn(resultColumn);
        }
    }

    /**
     * Gets the result column.
     *
     * @return the result column
     */
    protected TResultColumn getResultColumn() {
        return new TResultColumn();
    }

    /**
     * Handle node alias.
     *
     * @param listIterator the list iterator
     * @param next the next
     */
    public void handleNodeAlias(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {
        // close the TExpressionNode
        TSqlNode asCluase = new TSqlNode();
        asCluase.setNodeText(next.getTokenStr());
        resultColumn.setAs(asCluase);

        ParserUtils.addCommentsR(listIterator, asCluase);
        addNodeEndExpression();
    }

    private void addNodeEndExpression() {
        expression = null;
        endNodeExpression = new TExpression();
        resultColumn.setEndNode(endNodeExpression);
        inEndNode = true;
    }

    /**
     * Handle node end.
     *
     * @param listIterator the list iterator
     * @param next the next
     */
    public void handleNodeEnd(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {
        TSqlNode seperator = new TSqlNode();
        seperator.setNodeText(next.getTokenStr());
        ParserUtils.addCommentsR(listIterator, seperator);
        resultColumn.setSeperator(seperator);

        resultColumn = null;
        expression = null;
        endNodeExpression = null;
        inEndNode = false;
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

        TExpressionNode expNode = new TExpressionNode();
        expNode.getExpNode().setNodeText(next.getTokenStr());

        if (null != expression) {

            expression.addExpressionNode(expNode);

        } else {

            endNodeExpression.addExpressionNode(expNode);

        }

        ParserUtils.addCommentsR(listIterator, expNode.getExpNode());

    }

    /**
     * Checks if is node end.
     *
     * @param nodeStr the node str
     * @return true, if is node end
     */
    public boolean isNodeEnd(String nodeStr) {
        return ",".equalsIgnoreCase(nodeStr);
    }

    /**
     * Checks if is alias name.
     *
     * @param nodeStr the node str
     * @param previousNotEmptyToken the previous not empty token
     * @return true, if is alias name
     */
    public boolean isAliasName(String nodeStr, String previousNotEmptyToken) {
        return !isInEndNode() && "AS".equalsIgnoreCase(nodeStr);
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

        if (null != expression) {
            ParserUtils.addCustomStmtToExpression(expression, customSqlStmt, listIterator);
        } else if (null != endNodeExpression) {

            ParserUtils.addCustomStmtToExpression(endNodeExpression, customSqlStmt, listIterator);

        } else {
            throw new GaussDBSQLParserException("Unable to position the statement");

        }

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

            } else if (StringUtils.isBlank(previous.getTokenStr()) && null != expression
                    && expression.getExpList().size() > 0) {
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
