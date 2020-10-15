/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: TWhereListItem
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
public abstract class TWhereListItem extends TAbstractListItem {

    /**
     * Gets the item list node.
     *
     * @return the item list node
     */
    public abstract TParseTreeNode getItemListNode();

    /**
     * Gets the con sep.
     *
     * @return the con sep
     */
    public abstract TSqlNode getConSep();

}
