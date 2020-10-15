/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node.listitem.create;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: TDeclareResultColumn
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 30-Dec-2019]
 * @since 30-Dec-2019
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
