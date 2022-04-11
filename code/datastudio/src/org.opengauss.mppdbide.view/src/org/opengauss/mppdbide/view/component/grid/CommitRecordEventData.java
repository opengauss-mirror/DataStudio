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

import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.utils.observer.DSEventTable;
import org.opengauss.mppdbide.view.component.grid.core.DataGrid;
import org.opengauss.mppdbide.view.component.grid.core.DataText;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommitRecordEventData.
 *
 * @since 3.0.0
 */
public class CommitRecordEventData {

    private CommitInputData commitData;
    private DataGrid datagrid;
    private DataText datatext;
    private DSEventTable eventTable;
    private IDSGridDataProvider dataProvider;

    /**
     * Instantiates a new commit record event data.
     */
    public CommitRecordEventData() {

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
     * Gets the commit data.
     *
     * @return the commit data
     */
    public CommitInputData getCommitData() {
        return commitData;
    }

    /**
     * Gets the data grid.
     *
     * @return the data grid
     */
    public DataGrid getDataGrid() {
        return datagrid;
    }

    /**
     * Gets the data text.
     *
     * @return the data text
     */
    public DataText getDataText() {
        return datatext;
    }

    /**
     * Gets the event table.
     *
     * @return the event table
     */
    public DSEventTable getEventTable() {
        return eventTable;
    }

    /**
     * Sets the commit data.
     *
     * @param commitData the new commit data
     */
    public void setCommitData(CommitInputData commitData) {
        this.commitData = commitData;
    }

    /**
     * Sets the datagrid.
     *
     * @param datagrid the new datagrid
     */
    public void setDatagrid(DataGrid datagrid) {
        this.datagrid = datagrid;
    }

    /**
     * Sets the data text.
     *
     * @param text the new data text
     */
    public void setDataText(DataText text) {
        this.datatext = text;
    }

    /**
     * Sets the event table.
     *
     * @param eventTable the new event table
     */
    public void setEventTable(DSEventTable eventTable) {
        this.eventTable = eventTable;
    }

}
