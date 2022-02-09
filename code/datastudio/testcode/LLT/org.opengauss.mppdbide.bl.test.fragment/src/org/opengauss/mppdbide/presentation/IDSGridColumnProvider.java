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

package org.opengauss.mppdbide.presentation;

import java.util.Comparator;

/**
 * Column data provider for the grid.
 *
 * @since 3.0.0
 */
public interface IDSGridColumnProvider {
    /**
     * @return Count of columns to be displayed by the grid.
     */
    int getColumnCount();

    /**
     * @return all the column names applicable for the grid.
     */
    String[] getColumnNames();

    /**
     * @param columnIndex Column name for given index. Column Index Column
     *            Starts with 1.
     * @return
     */
    String getColumnName(int columnIndex);

    /**
     * @param columnIndex Description of the column, to be used by Grid tool
     *            tip. Column index starts with 1.
     * @return
     */
    String getColumnDesc(int columnIndex);

    int getColumnIndex(String columnLabel);

    Comparator<Object> getComparator(int columnIndex);
}
