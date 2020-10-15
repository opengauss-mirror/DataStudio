/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;

import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.grid.batchdrop.BatchDropColumnProvider;
import com.huawei.mppdbide.presentation.grid.batchdrop.BatchDropDataProvider;
import com.huawei.mppdbide.view.component.grid.core.DSAbstractRegistryConfiguration;

/**
 * 
 * Title: class
 * 
 * Description: The Class BatchDropGridStyleConfiguration.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class BatchDropGridStyleConfiguration extends AbstractRegistryConfiguration {

    /**
     * The Constant COL_LABEL_DROP_STATUS.
     */
    public static final String COL_LABEL_DROP_STATUS = "BATCHDROP_STATUS";

    /**
     * Configure registry.
     *
     * @param configRegistry the config registry
     */
    @Override
    public void configureRegistry(IConfigRegistry configRegistry) {
        batchDropRegistry(configRegistry);
    }

    /**
     * Batch drop registry.
     *
     * @param configRegistry the config registry
     */
    public static void batchDropRegistry(IConfigRegistry configRegistry) {
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new BatchDropObjectStatusPainter(),
                DisplayMode.NORMAL, COL_LABEL_DROP_STATUS);
    }

    /**
     * Gets the column accumulator.
     *
     * @param columnLabelAccumulator the column label accumulator
     * @param dataGridContext the data grid context
     * @return the column accumulator
     */
    public static DSAbstractRegistryConfiguration getColumnAccumulator(
            final ColumnOverrideLabelAccumulator columnLabelAccumulator, final IDataGridContext dataGridContext) {
        return new RegistryConfiguration(columnLabelAccumulator, dataGridContext);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RegistryConfiguration.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class RegistryConfiguration extends DSAbstractRegistryConfiguration {
        private IDataGridContext dataGridContext;
        private ColumnOverrideLabelAccumulator columnLabelAccumulator;
        private int statusColumnIdx = 3;

        private RegistryConfiguration(ColumnOverrideLabelAccumulator columnLabelAccumulator,
                IDataGridContext dataGridContext) {
            this.columnLabelAccumulator = columnLabelAccumulator;
            this.dataGridContext = dataGridContext;
        }

        @Override
        public void configureRegistry(IConfigRegistry configRegistry) {

            IDSGridDataProvider dataProvider = dataGridContext.getDataProvider();
            registerConfigLabelsOnColumns(columnLabelAccumulator, dataProvider.getColumnDataProvider());

            if (dataProvider instanceof BatchDropDataProvider) {
                batchDropRegistry(configRegistry);
            }
        }

        @Override
        public void registerConfigLabelsOnColumns(final ColumnOverrideLabelAccumulator columnLblAccumulator,
                IDSGridColumnProvider columnDataProvider) {
            if (columnDataProvider instanceof BatchDropColumnProvider) {
                columnLblAccumulator.registerColumnOverrides(statusColumnIdx, COL_LABEL_DROP_STATUS);
            }
        }

        /**
         * Sets the column label accumulator.
         *
         * @param columnLabelAccumulator the new column label accumulator
         */
        public void setColumnLabelAccumulator(ColumnOverrideLabelAccumulator columnLabelAccumulator) {
            this.columnLabelAccumulator = columnLabelAccumulator;
        }

        @Override
        public void registerUiRegistryToEachColumn(IConfigRegistry configRegistry,
                IDSGridColumnProvider columnDataProvider) {

        }

        @Override
        protected void onPreDestroy() {
            this.columnLabelAccumulator = null;
            this.dataGridContext = null;
        }
    }
}
