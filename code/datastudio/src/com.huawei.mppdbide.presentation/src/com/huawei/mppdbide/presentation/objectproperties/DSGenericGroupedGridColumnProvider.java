/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.List;

import com.huawei.mppdbide.presentation.grid.IDSGridColumnGroupProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSGenericGroupedGridColumnProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
