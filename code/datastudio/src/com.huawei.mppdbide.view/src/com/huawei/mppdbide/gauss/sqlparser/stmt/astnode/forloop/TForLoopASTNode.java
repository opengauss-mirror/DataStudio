/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.forloop;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;

/**
 * Title: TLoopASTNode Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
 */
public class TForLoopASTNode extends TBasicASTNode {

    private TExpression targetName = null;

    public TExpression getTargetName() {
        return targetName;
    }

    public void setTargetName(TExpression targetName) {
        this.targetName = targetName;
        setPreviousObject(this.targetName);
    }

}
