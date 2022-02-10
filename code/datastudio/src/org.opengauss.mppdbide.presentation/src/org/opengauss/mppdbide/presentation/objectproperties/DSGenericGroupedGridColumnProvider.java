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

package org.opengauss.mppdbide.presentation.objectproperties;

import java.util.List;

import org.opengauss.mppdbide.presentation.grid.IDSGridColumnGroupProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSGenericGroupedGridColumnProvider.
 * 
 * @since 3.0.0
 */
public class DSGenericGroupedGridColumnProvider implements IDSGridColumnGroupProvider {
    private List<DNIntraNodeDetailsColumn> colgrpDetails;

    /**
     * Instantiates a new DS generic grouped grid column provider.
     *
     * @param colgrpDetails the colgrp details
     */
    public DSGenericGroupedGridColumnProvider(List<DNIntraNodeDetailsColumn> colgrpDetails) {
        this.colgrpDetails = colgrpDetails;
    }

    @Override
    public int getGroupCount() {
        return this.colgrpDetails.size();
    }

    @Override
    public String getColumnGroupName(int idx) {
        return this.colgrpDetails.get(idx).getGroupColumnName();
    }

    @Override
    public int[] getColumnIndexInGroup(int idx) {
        int[] colno = new int[this.colgrpDetails.get(idx).getColCount()];

        int idxSofar = 0;
        for (int index = 0; index < idx; index++) {
            idxSofar += this.colgrpDetails.get(index).getColCount();
        }

        int colCount = this.colgrpDetails.get(idx).getColCount();
        for (int jindex = 0; jindex < colCount; jindex++) {
            colno[jindex] = idxSofar++;
        }

        return colno;
    }

    @Override
    public int getColumnGroupIndex(int columnIndex) {
        int colgroupCount = this.getGroupCount();

        int colSofar = 0;
        for (int index = 0; index < colgroupCount; index++) {
            colSofar += this.colgrpDetails.get(index).getColCount();
            if (colSofar > columnIndex) {
                return index;
            }
        }

        return 0;
    }

}
