/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.update;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TUpdateSetValuesNodeItem
 *
 * @since 3.0.0
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
