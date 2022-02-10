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
 * Description: The Interface IDSGridDataRow.
 *
 * @since 3.0.0
 */
public interface IDSGridDataRow {

    /**
     * Gets the values.
     *
     * @return the values
     */
    Object[] getValues();

    /**
     * Gets the value.
     *
     * @param columnIndex the column index
     * @return the value
     */
    Object getValue(int columnIndex);

    /**
     * Gets the cloned values.
     *
     * @return the cloned values
     */
    Object[] getClonedValues();
}
