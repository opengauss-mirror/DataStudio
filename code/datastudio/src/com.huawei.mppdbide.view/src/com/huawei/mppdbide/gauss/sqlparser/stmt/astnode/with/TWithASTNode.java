/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.with;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TWithASTNode
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
public class TWithASTNode extends TBasicASTNode {

    private TSqlNode recursive = null;

    private TExpression stmtExpression = null;

    public TSqlNode getRecursive() {
        return recursive;

    }

    public void setRecursive(TSqlNode recursive) {
        this.recursive = recursive;
        setPreviousObject(this.recursive);
    }

    public TExpression getStmtExpression() {
        return stmtExpression;
    }

    public void setStmtExpression(TExpression stmtExpression) {
        this.stmtExpression = stmtExpression;
        setPreviousObject(this.stmtExpression);
    }

}
