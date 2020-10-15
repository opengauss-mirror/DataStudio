/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.view.component.DSGridStateMachine;
import com.huawei.mppdbide.view.component.grid.core.DataGrid;
import com.huawei.mppdbide.view.component.grid.core.GridViewPortLayer;

/**
 * Title: GridScrollEventDataLoadListener
 * 
 * Description: The listener interface for receiving gridScrollEventDataLoad
 * events. The class that is interested in processing a gridScrollEventDataLoad
 * event implements this interface, and the object created with that class is
 * registered with a component using the component's
 * <code>addGridScrollEventDataLoadListener<code> method. When the
 * gridScrollEventDataLoad event occurs, that object's appropriate method is
 * invoked.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
 */

public class GridScrollEventDataLoadListener extends GridAndTextScrollEventDataLoadListener {
    private GridViewPortLayer viewportLayer;
    private DataGrid dataGrid;

    /**
     * Instantiates a new grid scroll event data load listener.
     *
     * @param viewportLayer the viewport layer
     * @param dataProvider the data provider
     * @param eventTable the event table
     * @param stateMachine the state machine
     * @param dataGrid the data grid
     */
    public GridScrollEventDataLoadListener(GridViewPortLayer viewportLayer, IDSGridDataProvider dataProvider,
            DSEventTable eventTable, DSGridStateMachine stateMachine, DataGrid dataGrid) {
        super(dataProvider, eventTable, stateMachine);
        this.viewportLayer = viewportLayer;
        this.dataGrid = dataGrid;
    }

    /**
     * Checks if is last row selected.
     *
     * @return true, if is last row selected
     */
    @Override
    public boolean isLastRowSelected() {
        if (this.getDataProvider() instanceof IDSEditGridDataProvider) {
            return this.dataGrid.getSelectedRowPosition() == (this.getDataProvider().getRecordCount()
                    + ((IDSEditGridDataProvider) this.getDataProvider()).getInsertedRowCount()) - 1;
        }
        return this.dataGrid.getSelectedRowPosition() == this.getDataProvider().getRecordCount() - 1;
    }

    /**
     * Trigger load more records.
     *
     * @param isKeyStrokeTriggeredScrollEvent the is key stroke triggered scroll
     * event
     */
    @Override
    public void triggerLoadMoreRecords(boolean isKeyStrokeTriggeredScrollEvent) {
        if ((isKeyStrokeTriggeredScrollEvent ? isLastRowSelected() : this.viewportLayer.isScrolledToEndOfPage())
                && !this.getDataProvider().isEndOfRecords() && !isSearchInProgress()
                && !isCurrentInitDataTextSatatu()) {
            if (ChronoUnit.MILLIS.between(this.getLastLoadTime(), LocalDateTime.now()) >= 2000) {
                this.setLastLoadTime(LocalDateTime.now());
                if (this.getStateMachine().set(DSGridStateMachine.State.LOADING)) {
                    this.getEventTable().sendEvent(
                            new DSEvent(IDSGridUIListenable.LISTEN_TYPE_ON_REEXECUTE_QUERY, getDataProvider()));
                }
            }
        }
    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        super.onPreDestroy();
        this.dataGrid = null;
        this.viewportLayer = null;
    }
}
