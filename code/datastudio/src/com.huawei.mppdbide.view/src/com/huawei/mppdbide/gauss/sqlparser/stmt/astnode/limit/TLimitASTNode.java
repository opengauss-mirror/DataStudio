/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.limit;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;

/**
 * 
 * Title: TLimitASTNode
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
public class TLimitASTNode extends TBasicASTNode {

    private TExpression limitExpression = null;

    public TExpression getLimitExpression() {
        return limitExpression;
    }

    public void setLimitExpression(TExpression limitExpression) {
        this.limitExpression = limitExpression;
        setPreviousObject(this.limitExpression);
    }

}
