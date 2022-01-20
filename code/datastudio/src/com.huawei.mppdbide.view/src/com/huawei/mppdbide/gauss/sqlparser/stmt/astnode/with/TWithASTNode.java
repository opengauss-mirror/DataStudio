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

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.with;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TWithASTNode
 *
 * @since 3.0.0
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
