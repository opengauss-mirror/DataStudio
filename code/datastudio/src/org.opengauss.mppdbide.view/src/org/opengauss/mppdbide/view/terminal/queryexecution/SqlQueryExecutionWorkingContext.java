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

package org.opengauss.mppdbide.view.terminal.queryexecution;

import java.util.ArrayList;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class SqlQueryExecutionWorkingContext.
 *
 * @since 3.0.0
 */
public class SqlQueryExecutionWorkingContext {
    /* Array will never be accessed out of bound, this is ensured by design */
    private ArrayList<String> queryArray = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    private int nextQueryId = 0;

    /**
     * Gets the query array.
     *
     * @return the query array
     */
    public ArrayList<String> getQueryArray() {
        return queryArray;
    }

    /**
     * Checks for next.
     *
     * @return true, if successful
     */
    public boolean hasNext() {
        return nextQueryId < queryArray.size() ? true : false;
    }

    /**
     * Next.
     *
     * @return the string
     */
    public String next() {
        return queryArray.get(nextQueryId++);
    }

    /**
     * Gets the current query.
     *
     * @return the current query
     */
    public String getCurrentQuery() {
        return queryArray.get(nextQueryId - 1);
    }

    /**
     * Update current query.
     *
     * @param query the query
     */
    public void updateCurrentQuery(String query) {
        queryArray.add(nextQueryId - 1, query);
        queryArray.remove(nextQueryId);
    }

    /**
     * Previous.
     *
     * @return the string
     */
    public String previous() {
        return queryArray.get(--nextQueryId);
    }
}
