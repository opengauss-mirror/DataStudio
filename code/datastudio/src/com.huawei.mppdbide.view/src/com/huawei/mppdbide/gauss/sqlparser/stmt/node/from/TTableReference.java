/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node.from;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: TTableReference
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
public class TTableReference extends TParseTreeNode {
    private TExpression tableExpression = null;

    public TExpression getTableExpression() {
        return tableExpression;
    }

    public void setTableExpression(TExpression tableExpression) {
        this.tableExpression = tableExpression;
    }

    @Override
    public TParseTreeNode getStartNode() {
        return tableExpression;
    }
}
