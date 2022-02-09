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

package org.opengauss.mppdbide.presentation.grid;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSGridColumnGroupProvider.
 *
 * @since 3.0.0
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
