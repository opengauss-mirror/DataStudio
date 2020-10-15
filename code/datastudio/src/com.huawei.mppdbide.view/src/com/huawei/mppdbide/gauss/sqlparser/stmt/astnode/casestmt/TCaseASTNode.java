/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.casestmt;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TCaseASTNode
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
public class TCaseASTNode extends TBasicASTNode {

    private TExpression colExpression = null;

    private TSqlNode endNode = null;

    public TExpression getColExpression() {
        return colExpression;
    }

    public void setColExpression(TExpression colExpression) {
        this.colExpression = colExpression;
        setPreviousObject(this.colExpression);
    }

    public TSqlNode getEndNode() {
        return endNode;
    }

    public void setEndNode(TSqlNode endNode) {
        this.endNode = endNode;
        setPreviousObject(this.endNode);
    }

}
