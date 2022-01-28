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

package com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist;

import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TWhereClause;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.WhereClauseItem;

/**
 * 
 * Title: WhereCluaseListParser
 *
 * @since 3.0.0
 */
public class WhereCluaseListParser extends AbstractNodeListParser {

    private TWhereClause resultColumnList = new TWhereClause();

    private TExpression expression = null;

    private WhereClauseItem whereItem = null;

    /**
     * Instantiates a new where cluase list parser.
     *
     * @param lineBreakSet the line break set
     */
    public WhereCluaseListParser(Set<String> lineBreakSet) {
        super(lineBreakSet);
    }

    /**
     * Creates the T object.
     */
    public void createTObject() {
        // create and then it is the start of the expression
        if (null == whereItem) {
            whereItem = new WhereClauseItem();
            expression = new TExpression();
            whereItem.setWhereExpression(expression);
            resultColumnList.addResultColumn(whereItem);
        }
    }

    /**
     * Handle node alias.
     *
     * @param listIterator the list iterator
     * @param next the next
     */
    public void handleNodeAlias(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {
    }

    /**
     * Handle node end.
     *
     * @param listIterator the list iterator
     * @param next the next
     */
    public void handleNodeEnd(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {

        TSqlNode conditionSep = new TSqlNode();
        conditionSep.setNodeText(next.getTokenStr());
        ParserUtils.addCommentsR(listIterator, conditionSep);
        whereItem.setConSep(conditionSep);

        whereItem = null;
        createTObject();

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
        ParserUtils.addCommentsR(listIterator, expNode.getExpNode());
        expression.addExpressionNode(expNode);

    }

    /**
     * Checks if is node end.
     *
     * @param nodeStr the node str
     * @return true, if is node end
     */
    public boolean isNodeEnd(String nodeStr) {
        return ParserUtils.getWhereSeperateList().contains(nodeStr.toUpperCase());
    }

    /**
     * Checks if is alias name.
     *
     * @param nodeStr the node str
     * @param previousNotEmptyToken the previous not empty token
     * @return true, if is alias name
     */
    public boolean isAliasName(String nodeStr, String previousNotEmptyToken) {
        return "AS".equalsIgnoreCase(nodeStr);
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
            TExpressionNode expNode = new TExpressionNode();
            expNode.setCustomStmt(customSqlStmt);
            expression.addExpressionNode(expNode);
            ParserUtils.addCommentsR(listIterator, customSqlStmt);
        } else {
            throw new IllegalStateException("Unable to position the statement");
        }

    }

    /**
     * Handle start end node.
     *
     * @param previousNotEmptyToken the previous not empty token
     * @param next the next
     * @param listIterator the list iterator
     */
    @Override
    public void handleStartEndNode(String previousNotEmptyToken, ISQLTokenData next,
            ListIterator<ISQLTokenData> listIterator) {
    }

}
