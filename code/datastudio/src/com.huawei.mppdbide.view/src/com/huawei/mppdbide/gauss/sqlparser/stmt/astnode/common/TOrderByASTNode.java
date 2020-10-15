/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.common;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TOrderByASTNode
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
public class TOrderByASTNode extends TBasicASTNode {

    private TSqlNode SIBLINGS = null;

    private TSqlNode by = null;

    public TSqlNode getSIBLINGS() {
        return SIBLINGS;
    }

    public void setSIBLINGS(TSqlNode sIBLINGS) {
        SIBLINGS = sIBLINGS;
        setPreviousObject(this.SIBLINGS);
    }

    public TSqlNode getBy() {
        return by;
    }

    public void setBy(TSqlNode by) {
        this.by = by;
        setPreviousObject(this.by);
    }

}
