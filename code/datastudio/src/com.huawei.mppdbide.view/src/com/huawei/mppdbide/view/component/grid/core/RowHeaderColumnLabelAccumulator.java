/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
