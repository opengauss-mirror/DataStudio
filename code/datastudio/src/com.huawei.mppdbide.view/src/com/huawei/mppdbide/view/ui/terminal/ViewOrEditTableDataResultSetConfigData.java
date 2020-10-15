/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.terminal;

import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.view.utils.UserPreference;

/**
 * Title: ViewOrEditTableDataResultSetConfigData
 * 
 * Description:result set config data for View Table Data window or Edit Table
 * Data window
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author lijialiang(l00448174)
 * @version [DataStudio 6.5.1, Aug 28, 2019]
 * @since Aug 28, 2019
 */

public class ViewOrEditTableDataResultSetConfigData implements IResultConfig {
    private int numRecordsToFetch = 0;

    /**
     * Instantiates a new view or edit table data result set config data.
     *
     * @param numRecordsFetchedSoFar the num records fetched so far
     */
    public ViewOrEditTableDataResultSetConfigData(int numRecordsFetchedSoFar) {
        int prefFetchCount = UserPreference.getInstance().getResultDataFetchCount();
        this.numRecordsToFetch = (prefFetchCount == -1) ? -1 : numRecordsFetchedSoFar + prefFetchCount;
    }

    /**
     * Gets the fetch count.
     *
     * @return the fetch count
     */
    @Override
    public int getFetchCount() {
        return this.numRecordsToFetch;
    }

    /**
     * Gets the action after fetch.
     *
     * @return the action after fetch
     */
    @Override
    public ActionAfterResultFetch getActionAfterFetch() {
        return ActionAfterResultFetch.CLOSE_CONNECTION_AFTER_FETCH;
    }

}