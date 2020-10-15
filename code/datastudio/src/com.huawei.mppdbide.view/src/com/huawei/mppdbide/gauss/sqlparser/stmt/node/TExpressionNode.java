/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: TExpressionNode
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
public class TExpressionNode extends TParseTreeNode {

    private TParseTreeNode customStmt = null;

    private TSqlNode expNode = new TSqlNode();

    public TParseTreeNode getCustomStmt() {
        return customStmt;
    }

    public void setCustomStmt(TParseTreeNode customStmt) {
        this.customStmt = customStmt;
    }

    public TSqlNode getExpNode() {
        return expNode;
    }

    public void setExpNode(TSqlNode expNode) {
        this.expNode = expNode;
    }

    @Override
    public TParseTreeNode getStartNode() {
        return null == customStmt ? expNode : customStmt;
    }

}
