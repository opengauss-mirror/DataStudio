/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.common;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TCustomASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: TCTEASTNode
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
public class TCTEASTNode extends TCustomASTNode {

    private TExpression stmtExpression = null;

    public TExpression getStmtExpression() {
        return stmtExpression;
    }

    public void setStmtExpression(TExpression stmtExpression) {
        this.stmtExpression = stmtExpression;
    }

    @Override
    public TParseTreeNode getStartNode() {
        return this.stmtExpression;
    }

}
