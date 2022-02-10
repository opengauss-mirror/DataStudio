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

package org.opengauss.mppdbide.presentation.objectproperties;

import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class DNIntraNodeDetailsColumn.
 *
 * @since 3.0.0
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
