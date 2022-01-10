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

package com.huawei.mppdbide.bl.sqlhistory;

import com.huawei.mppdbide.bl.sqlhistory.manager.ISqlHistoryManager;

/**
 * Title: SQLHistoryFactory
 * 
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
