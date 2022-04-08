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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.casestmt;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TCaseASTNode
 *
 * @since 3.0.0
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
