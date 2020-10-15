/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.loop;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;

/**
 * 
 * Title: TLoopASTNode
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
 */
public class TLoopASTNode extends TBasicASTNode {

    private TFullStmt fullStmt = null;

    private TExpression endLoop = null;

    // list of statements to the expression which should include including the
    // spaces

    public TFullStmt getFullStmt() {
        return fullStmt;
    }

    public void setFullStmt(TFullStmt fullStmt) {
        this.fullStmt = fullStmt;
        setPreviousObject(this.fullStmt);
    }

    public TExpression getEndLoop() {
        return endLoop;
    }

    public void setEndLoop(TExpression endLoop) {
        this.endLoop = endLoop;
        setPreviousObject(this.endLoop);
    }

}
