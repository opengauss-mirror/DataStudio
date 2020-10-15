/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.select;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TGroupByASTNode
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
public class TGroupByASTNode extends TBasicASTNode {
    private TSqlNode by = null;

    /**
     * Gets the group.
     *
     * @return the group
     */
    public TSqlNode getGroup() {
        return getKeywordNode();
    }

    /**
     * Gets the by.
     *
     * @return the by
     */
    public TSqlNode getBy() {
        return by;
    }

    /**
     * Sets the by.
     *
     * @param by the new by
     */
    public void setBy(TSqlNode by) {
        this.by = by;
        setPreviousObject(this.by);
    }

}