/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: TListItem
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
