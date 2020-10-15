/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.select;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TStartWithASTNode
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
public class TStartWithASTNode extends TBasicASTNode {
    private TSqlNode with = null;

    /**
     * Gets the start.
     *
     * @return the start
     */
    public TSqlNode getStart() {
        return getKeywordNode();
    }

    /**
     * Gets the with.
     *
     * @return the with
     */
    public TSqlNode getWith() {
        return with;
    }

    /**
     * Sets the with.
     *
     * @param with the new with
     */
    public void setWith(TSqlNode with) {
        this.with = with;
        setPreviousObject(this.with);
    }
}
