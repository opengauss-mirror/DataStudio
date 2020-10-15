/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewTableData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ViewTableData {

    private String[] columnNames;
    private List<String[]> columnValue;
    private boolean isEndOfTableReached;
    private String elapsedTime;

    /**
     * Instantiates a new view table data.
     */
    public ViewTableData() {
        columnNames = new String[0];
        columnValue = new ArrayList<String[]>(0);
        isEndOfTableReached = true;
        elapsedTime = "0 ms";
    }

    /**
     * Gets the column names.
     *
     * @return the column names
     */
    public String[] getColumnNames() {
        return columnNames.clone();
    }

    /**
     * Sets the column names.
     *
     * @param columnNames the new column names
     */
    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames.clone();
    }

    /**
     * Sets the column values.
     *
     * @param columnValues the new column values
     */
    public void setColumnValues(List<String[]> columnValues) {
        this.columnValue = columnValues;
    }

    /**
     * Gets the column values.
     *
     * @return the column values
     */
    public List<String[]> getColumnValues() {
        return columnValue;
    }

    /**
     * Checks if is end of table reached.
     *
     * @return true, if is end of table reached
     */
    public boolean isEndOfTableReached() {
        return isEndOfTableReached;
    }

    /**
     * Sets the end of table reached.
     *
     * @param isEndOfTableReachd the new end of table reached
     */
    public void setEndOfTableReached(boolean isEndOfTableReachd) {
        this.isEndOfTableReached = isEndOfTableReachd;
    }

    /**
     * Gets the elapsed time.
     *
     * @return the elapsed time
     */
    public String getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Sets the elapsed time.
     *
     * @param elapsedTime the new elapsed time
     */
    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

}
