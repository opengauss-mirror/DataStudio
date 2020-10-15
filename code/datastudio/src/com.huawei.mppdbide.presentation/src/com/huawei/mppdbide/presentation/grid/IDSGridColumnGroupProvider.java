/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.grid;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSGridColumnGroupProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IDSGridColumnGroupProvider {

    /**
     * Gets the group count.
     *
     * @return the group count
     */
    int getGroupCount();

    /**
     * Gets the column group name.
     *
     * @param idx the idx
     * @return the column group name
     */
    String getColumnGroupName(int idx);

    /**
     * Gets the column index in group.
     *
     * @param idx the idx
     * @return the column index in group
     */
    int[] getColumnIndexInGroup(int idx);

    /**
     * Gets the column group index.
     *
     * @param columnIndex the column index
     * @return the column group index
     */
    int getColumnGroupIndex(int columnIndex);
}
