/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.view.component.grid.core.DataGrid;
import com.huawei.mppdbide.view.component.grid.core.DataText;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommitRecordEventData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
