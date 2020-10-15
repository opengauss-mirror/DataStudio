/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.ifstmt;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;

/**
 * 
 * Title: TIfElseASTNode
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
public class TIfElseASTNode extends TBasicASTNode {

    private TSqlNode then = null;

    private TFullStmt fullStmt = null;

    private TSqlNode end = null;

    private TExpression endNode = null;

    // list of statements to the expression which should include including the
    // spaces

    public TSqlNode getThen() {
        return then;
    }

    public void setThen(TSqlNode then) {
        this.then = then;
        setPreviousObject(this.then);
    }

    public TFullStmt getFullStmt() {
        return fullStmt;
    }

    public void setFullStmt(TFullStmt fullStmt) {
        this.fullStmt = fullStmt;
        setPreviousObject(this.fullStmt);
    }

    public TSqlNode getEnd() {
        return end;
    }

    public void setEnd(TSqlNode end) {
        this.end = end;
        setPreviousObject(this.end);
    }

    public TExpression getEndNode() {
        return endNode;
    }

    public void setEndNode(TExpression endNode) {
        this.endNode = endNode;
        setPreviousObject(this.endNode);
    }

}
