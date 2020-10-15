/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: TResultColumn
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
public class TResultColumn extends TListItem {

    private TExpression expression = null;

    private TSqlNode as = null;

    private TExpression endNode = null;

    private TSqlNode seperator = null;

    public TExpression getExpression() {
        return expression;
    }

    public void setExpression(TExpression expression) {
        this.expression = expression;
        setPreviousObject(this.expression);

    }

    public TSqlNode getAs() {
        return as;
    }

    public void setSeperator(TSqlNode seperator) {
        this.seperator = seperator;
        setPreviousObject(this.seperator);
    }

    public void setAs(TSqlNode as) {
        this.as = as;
        setPreviousObject(this.as);
    }

    public TExpression getEndNode() {
        return endNode;
    }

    public void setEndNode(TExpression aliasName) {
        this.endNode = aliasName;
        setPreviousObject(this.endNode);
    }

    @Override
    public TParseTreeNode getItemListNode() {
        return expression;
    }

    @Override
    public TSqlNode getSeperator() {
        return seperator;
    }

    @Override
    public TParseTreeNode getStartNode() {
        return expression;
    }

}
