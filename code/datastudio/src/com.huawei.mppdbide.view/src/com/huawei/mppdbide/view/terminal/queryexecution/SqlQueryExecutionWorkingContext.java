/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.terminal.queryexecution;

import java.util.ArrayList;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class SqlQueryExecutionWorkingContext.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
