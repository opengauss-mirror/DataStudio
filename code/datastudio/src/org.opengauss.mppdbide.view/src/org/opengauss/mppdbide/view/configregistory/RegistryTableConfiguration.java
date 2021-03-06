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

package org.opengauss.mppdbide.view.configregistory;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import org.opengauss.mppdbide.presentation.grid.IDSGridColumnProvider;
import org.opengauss.mppdbide.view.component.IGridUIPreference;
import org.opengauss.mppdbide.view.component.grid.EditTableGridStyleConfiguration;
import org.opengauss.mppdbide.view.component.grid.IDataGridContext;
import org.opengauss.mppdbide.view.component.grid.core.DSAbstractRegistryConfiguration;

/**
 * 
 * Title: class
 * 
 * Description: The Class RegistryTableConfiguration.
 *
 * @since 3.0.0
 */
public abstract class RegistryTableConfiguration extends DSAbstractRegistryConfiguration
        implements DSConfigurationColumnsList {

    /**
     * The column label accumulator.
     */
    protected ColumnOverrideLabelAccumulator columnLabelAccumulator;

    /**
     * The data grid context.
     */
    protected IDataGridContext dataGridContext;

    /**
     * The ui pref.
     */
    protected IGridUIPreference uiPref;

    /**
     * Instantiates a new registry table configuration.
     *
     * @param columnLabelAccumulator the column label accumulator
     * @param dataGridContext the data grid context
     * @param uiPref the ui pref
     */
    public RegistryTableConfiguration(ColumnOverrideLabelAccumulator columnLabelAccumulator,
            IDataGridContext dataGridContext, IGridUIPreference uiPref) {
        this.columnLabelAccumulator = columnLabelAccumulator;
        this.dataGridContext = dataGridContext;
        this.uiPref = uiPref;
    }

    /**
     * Gets the UI preference.
     *
     * @return the UI preference
     */
    public IGridUIPreference getUIPreference() {
        return uiPref;
    }

    /**
     * Configure registry.
     *
     * @param configRegistry the config registry
     */
    @Override
    public void configureRegistry(IConfigRegistry configRegistry) {
        IDSGridColumnProvider columnDataProvider = dataGridContext.getDataProvider().getColumnDataProvider();
        registerConfigLabelsOnColumns(columnLabelAccumulator, columnDataProvider);
        registerUiRegistryToEachColumn(configRegistry, columnDataProvider);
    }

    /**
     * Sets the column label accumulator.
     *
     * @param columnLabelAccumulator the new column label accumulator
     */
    @Override
    public void setColumnLabelAccumulator(ColumnOverrideLabelAccumulator columnLabelAccumulator) {
        this.columnLabelAccumulator = columnLabelAccumulator;

    }

    /**
     * Register config labels on columns.
     *
     * @param lblAccumulator the lbl accumulator
     * @param colDataprovider the col dataprovider
     */
    @Override
    public void registerConfigLabelsOnColumns(ColumnOverrideLabelAccumulator lblAccumulator,
            IDSGridColumnProvider colDataprovider) {
        int colCount = colDataprovider.getColumnCount();
        for (int cnt = 0; cnt < colCount; cnt++) {
            registorLabel(lblAccumulator, colDataprovider, cnt);
        }
    }

    /**
     * Register ui registry to each column.
     *
     * @param configRegistry the config registry
     * @param columnDataProvider the column data provider
     */
    @Override
    public void registerUiRegistryToEachColumn(IConfigRegistry configRegistry,
            IDSGridColumnProvider columnDataProvider) {
        int colCount = columnDataProvider.getColumnCount();
        for (int index = 0; index < colCount; index++) {
            registerUiRegistory(configRegistry, columnDataProvider, index);
        }
    }

    /**
     * Sets the default labels.
     *
     * @param lblAccumulator the lbl accumulator
     * @param index the index
     */
    protected void setDefaultLabels(ColumnOverrideLabelAccumulator lblAccumulator, int index) {
        lblAccumulator.registerColumnOverrides(index, EditTableGridStyleConfiguration.COL_LABEL_COPY_READONLY_CELL);
        setReadOnlyLabel(lblAccumulator, index);
    }

    /**
     * Sets the read only label.
     *
     * @param lblAccumulator the lbl accumulator
     * @param cnt the cnt
     */
    protected void setReadOnlyLabel(ColumnOverrideLabelAccumulator lblAccumulator, int cnt) {
        lblAccumulator.registerColumnOverrides(cnt, EditTableGridStyleConfiguration.COL_LABEL_READONLY_CELL);
    }

    /**
     * Registor label.
     *
     * @param lblAccumulator the lbl accumulator
     * @param colDataprovider the col dataprovider
     * @param count the count
     */
    protected abstract void registorLabel(ColumnOverrideLabelAccumulator lblAccumulator,
            IDSGridColumnProvider colDataprovider, int count);

    /**
     * Register ui registory.
     *
     * @param configRegistry the config registry
     * @param columnDataProvider the column data provider
     * @param cnt the cnt
     */
    protected abstract void registerUiRegistory(IConfigRegistry configRegistry,
            IDSGridColumnProvider columnDataProvider, int cnt);

}
