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

package com.huawei.mppdbide.gauss.sqlparser.stmt.node.listitem.create;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: TDeclareResultColumn
 * 
 * @since 3.0.0
 */
public class TDeclareResultColumn extends TAbstractListItem {
    private TExpression expression = null;
    private TSqlNode seperator = null;

    /**
     * Gets the start node.
     *
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode() {
        return null;
    }

    /**
     * Gets the expression.
     *
     * @return the expression
     */
    public TExpression getExpression() {
        return expression;
    }

    /**
     * Sets the expression.
     *
     * @param expression the new expression
     */
    public void setExpression(TExpression expression) {
        this.expression = expression;
        setPreviousObject(this.expression);
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
    }

}
