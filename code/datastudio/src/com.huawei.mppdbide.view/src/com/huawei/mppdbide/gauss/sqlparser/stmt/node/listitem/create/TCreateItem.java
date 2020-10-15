/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node.listitem.create;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: TCreateItem
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
public class TCreateItem extends TAbstractListItem {
    private TExpression table = null;
    private TExpression expression = null;

    public TExpression getTable() {
        return table;
    }

    public void setTable(TExpression table) {
        this.table = table;
    }

    @Override
    public TParseTreeNode getStartNode() {
        return table;
    }

    public TExpression getExpression() {
        return expression;
    }

    public void setExpression(TExpression expression) {
        this.expression = expression;
    }

}
