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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.ifstmt;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;

/**
 * 
 * Title: TIfElseASTNode
 *
 * @since 3.0.0
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
