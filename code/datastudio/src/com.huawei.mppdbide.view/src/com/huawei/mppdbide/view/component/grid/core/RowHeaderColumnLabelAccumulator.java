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

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;

import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.view.component.grid.IEditTableGridStyleLabelFactory;

/**
 * 
 * Title: class
 * 
 * Description: The Class RowHeaderColumnLabelAccumulator.
 *
 * @since 3.0.0
 */
public class RowHeaderColumnLabelAccumulator extends ColumnLabelAccumulator {
    private DataLayer gridBodyDataLayer;

    /**
     * Instantiates a new row header column label accumulator.
     *
     * @param layer the layer
     */
    public RowHeaderColumnLabelAccumulator(DataLayer layer) {
        this.gridBodyDataLayer = layer;
    }

    /**
     * Accumulate config labels.
     *
     * @param configLabels the config labels
     * @param columnPosition the column position
     * @param rowPosition the row position
     */
    @Override
    public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
        super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);

        IDataProvider dataProvider = this.gridBodyDataLayer.getDataProvider();

        IDSGridEditDataRow editRow = getEditedRow(dataProvider, rowPosition);
        if (editRow != null && editRow.getExecutionStatus() == EditTableRecordExecutionStatus.FAILED) {
            configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_CHANGE_FAILED);
        }
    }

    private IDSGridEditDataRow getEditedRow(IDataProvider dataProvider, int rowPosition) {
        if (dataProvider instanceof ListDataProvider<?>) {
            ListDataProvider<?> dp = (ListDataProvider<?>) dataProvider;

            Object row = dp.getRowObject(rowPosition);
            if (row instanceof IDSGridEditDataRow) {
                return (IDSGridEditDataRow) row;
            }
        }

        return null;
    }

}
