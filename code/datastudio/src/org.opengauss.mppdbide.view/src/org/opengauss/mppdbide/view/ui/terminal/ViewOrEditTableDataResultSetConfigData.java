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

package org.opengauss.mppdbide.view.ui.terminal;

import org.opengauss.mppdbide.presentation.resultset.ActionAfterResultFetch;
import org.opengauss.mppdbide.presentation.resultsetif.IResultConfig;
import org.opengauss.mppdbide.view.utils.UserPreference;

/**
 * Title: ViewOrEditTableDataResultSetConfigData
 * 
 * Description:result set config data for View Table Data window or Edit Table
 * Data window
 * 
 * @since 3.0.0
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