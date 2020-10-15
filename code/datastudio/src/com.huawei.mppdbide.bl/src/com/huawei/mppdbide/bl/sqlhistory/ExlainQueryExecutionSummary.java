/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.sqlhistory;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExlainQueryExecutionSummary.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
