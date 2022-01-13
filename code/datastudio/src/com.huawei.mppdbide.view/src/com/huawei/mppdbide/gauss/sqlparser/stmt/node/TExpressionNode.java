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

package com.huawei.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: TExpressionNode
 *
 * @since 3.0.0
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
