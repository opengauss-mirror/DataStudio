/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.huawei.mppdbide.view.component.grid.core.DataGrid;
import com.huawei.mppdbide.view.component.grid.sort.SortPopUpDialog;

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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
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
