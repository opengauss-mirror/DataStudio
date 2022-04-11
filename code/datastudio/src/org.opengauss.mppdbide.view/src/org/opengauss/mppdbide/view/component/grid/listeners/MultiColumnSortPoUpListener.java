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

package org.opengauss.mppdbide.view.component.grid.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import org.opengauss.mppdbide.view.component.grid.core.DataGrid;
import org.opengauss.mppdbide.view.component.grid.sort.SortPopUpDialog;

/**
 * Title: MultiColumnSortPoUpListener
 * 
 * Description:The listener interface for receiving multiColumnSortPoUp events.
 * The class that is interested in processing a multiColumnSortPoUp event
 * implements this interface, and the object created with that class is
 * registered with a component using the component's
 * <code>addMultiColumnSortPoUpListener<code> method. When the
 * multiColumnSortPoUp event occurs, that object's appropriate method is
 * invoked.
 * 
 * @since 3.0.0
 */
public class MultiColumnSortPoUpListener implements SelectionListener {

    /**
     * The grid.
     */
    DataGrid grid;

    /**
     * Gets the data grid.
     *
     * @return the data grid
     */
    public DataGrid getDataGrid() {
        return grid;
    }

    /**
     * Instantiates a new multi column sort po up listener.
     *
     * @param grid the grid
     */
    public MultiColumnSortPoUpListener(DataGrid grid) {
        this.grid = grid;
    }

    /**
     * Widget selected.
     *
     * @param e the e
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        SortPopUpDialog dialog = new SortPopUpDialog(getDataGrid());
        dialog.open();

    }

    /**
     * Widget default selected.
     *
     * @param e the e
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {

    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        this.grid = null;
    }
}
