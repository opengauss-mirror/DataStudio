/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node.from;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;

/**
 * 
 * Title: TFromItemList
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
public class TFromItemList extends TParseTreeNodeList<TAbstractListItem> {

    private boolean joinStmt = false;

    public boolean isJoinStmt() {
        return joinStmt;
    }

    public void setJoinStmt(boolean joinStmt) {
        this.joinStmt = joinStmt;
    }

}
