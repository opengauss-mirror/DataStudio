/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: WhereClauseItem
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
public class WhereClauseItem extends TWhereListItem {

    private TExpression whereExpression = null;

    private TSqlNode conditionSeparator = null;

    /**
     * Gets the where expression.
     *
     * @return the where expression
     */
    public TExpression getWhereExpression() {
        return whereExpression;
    }

    /**
     * Sets the where expression.
     *
     * @param whereExpression the new where expression
     */
    public void setWhereExpression(TExpression whereExpression) {
        this.whereExpression = whereExpression;
        setPreviousObject(this.whereExpression);
    }

    /**
     * Gets the con sep.
     *
     * @return the con sep
     */
    public TSqlNode getConSep() {
        return conditionSeparator;
    }

    /**
     * Sets the con sep.
     *
     * @param conSep the new con sep
     */
    public void setConSep(TSqlNode conSep) {
        this.conditionSeparator = conSep;
        setPreviousObject(this.conditionSeparator);
    }

    /**
     * Gets the start node.
     *
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode() {
        return whereExpression;
    }

    /**
     * Gets the item list node.
     *
     * @return the item list node
     */
    @Override
    public TParseTreeNode getItemListNode() {
        return whereExpression;
    }

}
