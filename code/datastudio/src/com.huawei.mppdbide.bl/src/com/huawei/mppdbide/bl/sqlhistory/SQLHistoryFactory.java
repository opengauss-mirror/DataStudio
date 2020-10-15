/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.sqlhistory;

import com.huawei.mppdbide.bl.sqlhistory.manager.ISqlHistoryManager;

/**
 * Title: SQLHistoryFactory
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 11-Oct-2019]
 * @since 11-Oct-2019
 */

public abstract class SQLHistoryFactory {

    /**
     * Gets the single instance of SQLHistoryFactory.
     *
     * @return single instance of SQLHistoryFactory
     */
    public static ISqlHistoryManager getInstance() {
        return SQLHistoryManager.getInstance();
    }

    /**
     * Gets the new query execution summary.
     *
     * @param dbname the dbname
     * @param profileName the profile name
     * @param profileID the profile ID
     * @param query the query
     * @return the new query execution summary
     */
    public static IQueryExecutionSummary getNewQueryExecutionSummary(String dbname, String profileName,
            String profileID, String query) {
        return new QueryExecutionSummary(dbname, profileName, profileID, query);
    }

    /**
     * Gets the new exlain query execution summary.
     *
     * @param dbname the dbname
     * @param profileId the profile id
     * @param query the query
     * @param querySubmitTime the query submit time
     * @return the new exlain query execution summary
     */
    public static IQueryExecutionSummary getNewExlainQueryExecutionSummary(String dbname, String profileId,
            String query, String querySubmitTime) {
        return new ExlainQueryExecutionSummary(dbname, profileId, query, querySubmitTime);
    }

}
