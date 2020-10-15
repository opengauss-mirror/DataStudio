/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.update;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TUpdateSetValuesNodeItem
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
public class TUpdateSetValuesNodeItem extends TAbstractListItem {

    private TUpdateSetValuesBean leftNodeItem = null;

    private TSqlNode equalNode = null;

    private TUpdateSetValuesBean rightNodeItem = null;

    private TSqlNode seperator = null;

    public TUpdateSetValuesBean getLeftNodeItem() {
        return leftNodeItem;
    }

    public void setLeftNodeItem(TUpdateSetValuesBean leftNodeItem) {
        this.leftNodeItem = leftNodeItem;
        setPreviousObject(this.leftNodeItem);
    }

    public TSqlNode getEqualNode() {
        return equalNode;
    }

    public void setEqualNode(TSqlNode equalNode) {
        this.equalNode = equalNode;
        setPreviousObject(this.equalNode);
    }

    public TUpdateSetValuesBean getRightNodeItem() {
        return rightNodeItem;
    }

    public void setRightNodeItem(TUpdateSetValuesBean rightNodeItem) {
        this.rightNodeItem = rightNodeItem;
        setPreviousObject(this.rightNodeItem);
    }

    public TSqlNode getSeperator() {
        return seperator;
    }

    public void setSeperator(TSqlNode seperator) {
        this.seperator = seperator;
        setPreviousObject(this.seperator);
    }

    @Override
    public TParseTreeNode getStartNode() {
        return this.getAutoStartBean();
    }

}
