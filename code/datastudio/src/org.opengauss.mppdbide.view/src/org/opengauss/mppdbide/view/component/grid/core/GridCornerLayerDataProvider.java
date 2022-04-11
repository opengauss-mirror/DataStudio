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

package org.opengauss.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.swt.events.MouseEvent;

import org.opengauss.mppdbide.view.component.grid.IDataGridContext;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridCornerLayerDataProvider.
 *
 * @since 3.0.0
 */
public class GridCornerLayerDataProvider implements IDataProvider {
    private int columncount;
    private int rowCount;
    private IDataGridContext dataGridContext;

    /**
     * Instantiates a new grid corner layer data provider.
     *
     * @param dataGridContext the data grid context
     */
    public GridCornerLayerDataProvider(IDataGridContext dataGridContext) {
        this.dataGridContext = dataGridContext;
    }

    /**
     * Gets the data value.
     *
     * @param columnIndex the column index
     * @param rowIndex the row index
     * @return the data value
     */
    @Override
    public Object getDataValue(int columnIndex, int rowIndex) {
        // Empty string. Will use image.
        return "";
    }

    /**
     * Sets the data value.
     *
     * @param columnIndex the column index
     * @param rowIndex the row index
     * @param newValue the new value
     */
    @Override
    public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
        // Ignore. Corner cell not part of data. So, never be allowed to set.
    }

    /**
     * Gets the column count.
     *
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        columncount = this.dataGridContext.getDataProvider().getColumnDataProvider().getColumnCount();
        return columncount;
    }

    /**
     * Gets the row count.
     *
     * @return the row count
     */
    @Override
    public int getRowCount() {
        rowCount = this.dataGridContext.getDataProvider().getRecordCount();
        return rowCount;
    }

    /**
     * Enable corner click select all.
     *
     * @param uiBindingRegistry the ui binding registry
     * @param selectionLayer the selection layer
     */
    public void enableCornerClickSelectAll(UiBindingRegistry uiBindingRegistry, SelectionLayer selectionLayer) {
        uiBindingRegistry.registerSingleClickBinding(new MouseEventMatcher(GridRegion.CORNER),
                new SelectAllEventListener(selectionLayer));

    }

    /**
     * The listener interface for receiving selectAllEvent events. The class
     * that is interested in processing a selectAllEvent event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addSelectAllEventListener<code>
     * method. When the selectAllEvent event occurs, that object's appropriate
     * method is invoked.
     *
     * SelectAllEventEvent
     */
    private static final class SelectAllEventListener implements IMouseAction {
        private SelectionLayer selectionLayer;

        private SelectAllEventListener(SelectionLayer selectionLayer) {
            this.selectionLayer = selectionLayer;
        }

        @Override
        public void run(NatTable natTable, MouseEvent event) {
            selectionLayer.selectAll();
        }
    }
    
    /**
     *  the onPreDestroy
     */
    public void onPreDestroy() {
        dataGridContext = null;
    }

}
