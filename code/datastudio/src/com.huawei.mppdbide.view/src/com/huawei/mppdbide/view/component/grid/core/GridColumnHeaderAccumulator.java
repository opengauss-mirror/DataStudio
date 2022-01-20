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

import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;

import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import com.huawei.mppdbide.view.component.grid.GridUIUtils;
import com.huawei.mppdbide.view.component.grid.IDataGridContext;
import com.huawei.mppdbide.view.component.grid.IEditTableGridStyleLabelFactory;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridColumnHeaderAccumulator.
 *
 * @since 3.0.0
 */
public class GridColumnHeaderAccumulator extends ColumnLabelAccumulator {
    private IDataGridContext dataGridContext;

    /**
     * Instantiates a new grid column header accumulator.
     *
     * @param dataGridContext the data grid context
     */
    public GridColumnHeaderAccumulator(IDataGridContext dataGridContext) {
        this.dataGridContext = dataGridContext;
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

        handleColumnHeaderLabels(configLabels, columnPosition);
    }

    /**
     * Handle column header labels.
     *
     * @param configLabels the config labels
     * @param columnPosition the column position
     */
    protected void handleColumnHeaderLabels(LabelStack configLabels, int columnPosition) {
        IDSGridDataProvider dataProvider = this.dataGridContext.getDataProvider();
        if (dataProvider instanceof IDSEditGridDataProvider) {
            IDSEditGridDataProvider editDP = (IDSEditGridDataProvider) dataProvider;
            String columnDTName = editDP.getColumnDataProvider().getColumnDataTypeName(columnPosition);
            int precisionVal = editDP.getColumnDataProvider().getPrecision(columnPosition);
            if (dataProvider instanceof DSObjectPropertiesGridDataProvider) {
                return;
            }

            if (!GridUIUtils.isDatatypeEditSupported(columnDTName, precisionVal)
                    || editDP.isDistributionColumn(columnPosition)) {
                configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_HEADER_LABEL_READONLY_CELL);
            }
            handleHeadersForProperties(dataProvider, columnPosition, configLabels);

        }
    }

    private void handleHeadersForProperties(IDSGridDataProvider dataProvider, int columnPosition,
            LabelStack configLabels) {
        if (dataProvider instanceof DSObjectPropertiesGridDataProvider) {
            String objectPropertyName = ((DSObjectPropertiesGridDataProvider) dataProvider).getObjectPropertyName();
            IDSGridColumnProvider columnDataProvider = dataProvider.getColumnDataProvider();
            if (null != columnDataProvider) {

                String columnName = columnDataProvider.getColumnName(columnPosition);
                if (columnName != null
                        && GridUIUtils.isEditablePropertiesAttributes(objectPropertyName, columnName, null)) {
                    configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_HEADER_LABEL_READONLY_CELL);
                }
            } else {
                return;
            }
        }
    }
    
    /**
     *  the onPreDestroy
     */
    public void onPreDestroy() {
        dataGridContext = null;
    }
}
