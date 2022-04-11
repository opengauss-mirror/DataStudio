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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: TListItem
 *
 * @since 3.0.0
 */
public abstract class TListItem extends TAbstractListItem {

    private int itemSize;

    /**
     * Gets the item list node.
     *
     * @return the item list node
     */
    public abstract TParseTreeNode getItemListNode();

    /**
     * Gets the as.
     *
     * @return the as
     */
    public abstract TSqlNode getAs();

    /**
     * Gets the end node.
     *
     * @return the end node
     */
    public abstract TParseTreeNode getEndNode();

    /**
     * Gets the seperator.
     *
     * @return the seperator
     */
    public abstract TSqlNode getSeperator();

    /**
     * Gets the item size.
     *
     * @return the item size
     */
    public int getItemSize() {
        return itemSize;
    }

    /**
     * Sets the item size.
     *
     * @param itemSize the new item size
     */
    public void setItemSize(int itemSize) {
        this.itemSize = itemSize;
    }

}
