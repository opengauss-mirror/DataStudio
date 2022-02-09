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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.node.from;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TListItem;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TWhereClause;

/**
 * 
 * Title: TFromItem
 *
 * @since 3.0.0
 */
public class TFromItem extends TListItem {

    private TExpression table = null;
    private TSqlNode as = null;
    private TExpression aliasName = null;

    private TExpression joinType = null;
    private TSqlNode on = null;
    private TWhereClause joinCondition = null;

    private TSqlNode seperator = null;

    private boolean hasJoin = false;

    /**
     * Gets the table.
     *
     * @return the table
     */
    public TExpression getTable() {
        return table;
    }

    /**
     * Sets the table.
     *
     * @param table the new table
     */
    public void setTable(TExpression table) {
        this.table = table;
        setPreviousObject(this.table);
    }

    /**
     * Gets the as.
     *
     * @return the as
     */
    public TSqlNode getAs() {
        return as;
    }

    /**
     * Sets the as.
     *
     * @param as the new as
     */
    public void setAs(TSqlNode as) {
        this.as = as;
        setPreviousObject(this.as);
    }

    /**
     * Gets the end node.
     *
     * @return the end node
     */
    public TExpression getEndNode() {
        return aliasName;
    }

    /**
     * Sets the alias name.
     *
     * @param aliasName the new alias name
     */
    public void setAliasName(TExpression aliasName) {
        this.aliasName = aliasName;
        setPreviousObject(this.aliasName);
    }

    /**
     * Gets the join type.
     *
     * @return the join type
     */
    public TExpression getJoinType() {
        return joinType;
    }

    /**
     * Sets the join type.
     *
     * @param joinType the new join type
     */
    public void setJoinType(TExpression joinType) {
        this.joinType = joinType;
        setPreviousObject(this.joinType);
    }

    /**
     * Gets the on.
     *
     * @return the on
     */
    public TSqlNode getOn() {
        return on;
    }

    /**
     * Sets the on.
     *
     * @param on the new on
     */
    public void setOn(TSqlNode on) {
        this.on = on;
        setPreviousObject(this.on);
    }

    /**
     * Gets the join condition.
     *
     * @return the join condition
     */
    public TWhereClause getJoinCondition() {
        return joinCondition;
    }

    /**
     * Sets the join condition.
     *
     * @param joinCondition the new join condition
     */
    public void setJoinCondition(TWhereClause joinCondition) {
        this.joinCondition = joinCondition;
        setPreviousObject(this.joinCondition);
    }

    /**
     * Gets the start node.
     *
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode() {
        return table;
    }

    /**
     * Gets the item list node.
     *
     * @return the item list node
     */
    @Override
    public TParseTreeNode getItemListNode() {
        return table;
    }

    /**
     * Gets the seperator.
     *
     * @return the seperator
     */
    public TSqlNode getSeperator() {
        return seperator;
    }

    /**
     * Sets the seperator.
     *
     * @param seperator the new seperator
     */
    public void setSeperator(TSqlNode seperator) {
        this.seperator = seperator;
        setPreviousObject(this.seperator);
    }

    /**
     * Checks if is checks for join.
     *
     * @return true, if is checks for join
     */
    public boolean isHasJoin() {
        return hasJoin;
    }

    /**
     * Sets the checks for join.
     *
     * @param hasJoin the new checks for join
     */
    public void setHasJoin(boolean hasJoin) {
        this.hasJoin = hasJoin;
    }

}
