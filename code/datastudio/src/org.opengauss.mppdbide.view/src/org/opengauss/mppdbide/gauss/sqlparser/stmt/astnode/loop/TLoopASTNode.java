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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.loop;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;

/**
 * 
 * Title: TLoopASTNode
 *
 * @since 3.0.0
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
