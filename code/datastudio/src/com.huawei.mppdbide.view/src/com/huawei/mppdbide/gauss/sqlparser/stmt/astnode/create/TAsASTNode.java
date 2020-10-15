/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: TAsASTNode Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
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
