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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: WhereClauseItem
 *
 * @since 3.0.0
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
