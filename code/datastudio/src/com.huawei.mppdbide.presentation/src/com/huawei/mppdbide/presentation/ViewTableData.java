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

package com.huawei.mppdbide.presentation;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewTableData.
 *
 * @since 3.0.0
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
