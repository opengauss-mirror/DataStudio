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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.create;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: TAsASTNode
 *
 * @since 3.0.0
 */
public class TAsASTNode extends TBasicASTNode {

    private TExpression asDefExpression = null;

    private TExpression commonExpression = null;

    private TSqlNode stratAs = null;

    private TSqlNode endAs = null;

    public TExpression getAsDefExpression() {
        return asDefExpression;
    }

    public void setAsDefExpression(TExpression asDefExpression) {
        this.asDefExpression = asDefExpression;
        setPreviousObject(this.asDefExpression);
    }

    public TExpression getCommonExpression() {
        return commonExpression;
    }

    public void setCommonExpression(TExpression commonExpression) {
        this.commonExpression = commonExpression;
        setPreviousObject(this.commonExpression);
    }

    public TSqlNode getStratAs() {
        return stratAs;
    }

    public void setStratAs(TSqlNode stratAs) {
        this.stratAs = stratAs;
        setPreviousObject(this.stratAs);
    }

    public TSqlNode getEndAs() {
        return endAs;
    }

    public void setEndAs(TSqlNode endAs) {
        this.endAs = endAs;
        setPreviousObject(this.endAs);
    }

}
