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

package org.opengauss.mppdbide.view.component.grid;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.opengauss.mppdbide.presentation.grid.IDSEditGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.utils.observer.DSEvent;
import org.opengauss.mppdbide.utils.observer.DSEventTable;
import org.opengauss.mppdbide.utils.observer.IDSGridUIListenable;
import org.opengauss.mppdbide.view.component.DSGridStateMachine;
import org.opengauss.mppdbide.view.component.grid.core.DataGrid;
import org.opengauss.mppdbide.view.component.grid.core.GridViewPortLayer;

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
 * @since 3.0.0
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

    @Override
    public void updateScrolledPosition(int position, int pageIncrement, int increment) {
        int pageRowSize = pageIncrement / increment ;
        dataGrid.updateScrolledInfo(position, pageRowSize);
    }
}
