/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class DNIntraNodeDetailsColumn.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DNIntraNodeDetailsColumn {
    private String groupColumnName;
    private int colCount;
    private List<String> colnames;

    /**
     * Gets the group column name.
     *
     * @return the group column name
     */
    public String getGroupColumnName() {
        return groupColumnName;
    }

    /**
     * Sets the group column name.
     *
     * @param groupColumnName the new group column name
     */
    public void setGroupColumnName(String groupColumnName) {
        this.groupColumnName = groupColumnName;
    }

    /**
     * Gets the col count.
     *
     * @return the col count
     */
    public int getColCount() {
        return colCount;
    }

    /**
     * Sets the col count.
     *
     * @param colCount the new col count
     */
    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    /**
     * Gets the colnames.
     *
     * @return the colnames
     */
    public List<String> getColnames() {
        return colnames;
    }

    /**
     * Sets the colnames.
     *
     * @param colnames the new colnames
     */
    public void setColnames(List<String> colnames) {
        this.colnames = colnames;
    }

}
