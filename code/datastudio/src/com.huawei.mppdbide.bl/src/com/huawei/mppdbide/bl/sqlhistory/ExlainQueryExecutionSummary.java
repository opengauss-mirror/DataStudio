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

/**
 * 
 * Title: class
 * 
 * Description: The Class ExlainQueryExecutionSummary.
 * 
 */

public class ExlainQueryExecutionSummary extends QueryExecutionSummary {

    private boolean analyse;

    /**
     * Instantiates a new exlain query execution summary.
     *
     * @param dbname the dbname
     * @param profileId the profile id
     * @param query the query
     * @param querySubmitTime the query submit time
     */
    public ExlainQueryExecutionSummary(String dbname, String profileId, String query, String querySubmitTime) {
        super(dbname, profileId, query, querySubmitTime);
    }

    @Override
    public boolean isAnalyze() {
        return analyse;
    }

    @Override
    public void setAnalyze(boolean anlyse) {
        this.analyse = anlyse;
    }
}
