/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.custom.condition;

import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: TCaseSqlStatement
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
public class TCaseSqlStatement extends TCustomSqlStatement {

    @Override
    public TParseTreeNode getStartNode() {
        if (astNodeMap.isEmpty()) {
            return null;
        }
        return astNodeMap.entrySet().iterator().next().getValue();
    }

}
