/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import java.util.Comparator;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.NullComparator;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.action.SortColumnAction;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.swt.SWT;

import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.view.component.IGridUIPreference;
import com.huawei.mppdbide.view.component.grid.IEditTableGridStyleLabelFactory;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortRegistryConfiguration.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SortRegistryConfiguration extends AbstractRegistryConfiguration {
    private IDSGridDataProvider dataProvider;
    private IGridUIPreference pref;

    /**
     * The Constant CONFIG_TYPE_SORT.
     */
    public static final String CONFIG_TYPE_SORT = "SORT"; // $NON-NLS-1$

    /**
     * The Constant CONFIG_TYPE_SORT_DOWN.
     */
    public static final String CONFIG_TYPE_SORT_DOWN = "SORT_DOWN"; // $NON-NLS-1$

    /**
     * The Constant CONFIG_TYPE_SORT_UP.
     */
    public static final String CONFIG_TYPE_SORT_UP = "SORT_UP"; // $NON-NLS-1$

    /**
     * The Constant CONFIG_TYPE_SORT_SEQ.
     */
    public static final String CONFIG_TYPE_SORT_SEQ = "SORT_SEQ_"; // $NON-NLS-1$

    private ICellPainter cellPaintr;

    /**
     * Instantiates a new sort registry configuration.
     *
     * @param dataProvider the data provider
     * @param uiPref the ui pref
     */
    public SortRegistryConfiguration(IDSGridDataProvider dataProvider, IGridUIPreference uiPref) {
        this.dataProvider = dataProvider;
        this.pref = uiPref;
        this.cellPaintr = new SortablePainterWrapper();
    }

    /**
     * Configure registry.
     *
     * @param configuredRegistry the configured registry
     */
    @Override
    public void configureRegistry(IConfigRegistry configuredRegistry) {
        if (null == getDataProvider()) {
            return;
        }

        int columnCount = getDataProvider().getColumnDataProvider().getColumnCount();
        columnCount = handleDisableSortForFunctionProcExeResult(columnCount);
        for (int i = 0; i < columnCount; i++) {
            configuredRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
                    getComparator(i, pref.isEnableSort()), DisplayMode.NORMAL,
                    ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + i);
        }

        configuredRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.cellPaintr,
                DisplayMode.NORMAL, GridRegion.COLUMN_HEADER);
        configuredRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.cellPaintr,
                DisplayMode.NORMAL, IEditTableGridStyleLabelFactory.COL_HEADER_LABEL_READONLY_CELL);
        configuredRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.cellPaintr,
                DisplayMode.NORMAL, CONFIG_TYPE_SORT_DOWN);
        configuredRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.cellPaintr,
                DisplayMode.NORMAL, CONFIG_TYPE_SORT_UP);
    }

    /**
     * Handle disable sort for function proc exe result.
     *
     * @param columnCount the column count
     * @return the int
     */
    private int handleDisableSortForFunctionProcExeResult(int columnCount) {
        int cnt = columnCount;
        if (getDataProvider().isFuncProcExport()) {
            cnt = cnt - 1;
        }
        return cnt;
    }

    /**
     * Configure ui bindings.
     *
     * @param uiBindRegistry the ui bind registry
     */
    @Override
    public void configureUiBindings(UiBindingRegistry uiBindRegistry) {
        // Register new bindings
        uiBindRegistry.unregisterSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.MOD3));
        uiBindRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.MOD3),
                new SortColumnAction(false));
    }

    /**
     * Gets the comparator.
     *
     * @param colIndex the col index
     * @param isSortSupported the is sort supported
     * @return the comparator
     */
    public Comparator<Object> getComparator(int colIndex, boolean isSortSupported) {
        return isSortSupported ? getDataProvider().getColumnDataProvider().getComparator(colIndex)
                : new NullComparator();
    }

    /**
     * Gets the data provider.
     *
     * @return the data provider
     */
    public IDSGridDataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * Sets the data provider.
     *
     * @param dataProvider the new data provider
     */
    public void setDataProvider(IDSGridDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        this.dataProvider = null;
        if (this.cellPaintr != null) {
            ((SortablePainterWrapper) cellPaintr).onPreDestroy();
        }
        this.cellPaintr = null;
        this.pref = null;
    }

}
