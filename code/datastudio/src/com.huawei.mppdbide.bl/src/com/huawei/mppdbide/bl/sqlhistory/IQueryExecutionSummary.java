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

import java.util.Date;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IQueryExecutionSummary.
 * 
 */

public interface IQueryExecutionSummary {

    /**
     * Gets the database name.
     *
     * @return the database name
     */
    String getDatabaseName();

    /**
     * Gets the profile id.
     *
     * @return the profile id
     */
    String getProfileId();

    /**
     * Gets the profile name.
     *
     * @return the profile name
     */
    String getProfileName();

    /**
     * Gets the query.
     *
     * @return the query
     */
    String getQuery();

    /**
     * Checks if is query execution success.
     *
     * @return true, if is query execution success
     */
    boolean isQueryExecutionSuccess();

    /**
     * Gets the execution time.
     *
     * @return the execution time
     */
    Date getExecutionTime();

    /**
     * Gets the query start date.
     *
     * @return the query start date
     */
    String getQueryStartDate();

    /**
     * Gets the num records fetched.
     *
     * @return the num records fetched
     */
    int getNumRecordsFetched();

    /**
     * Sets the num records fetched.
     *
     * @param numRecordsFetched the new num records fetched
     */
    void setNumRecordsFetched(int numRecordsFetched);

    /**
     * Gets the elapsed time.
     *
     * @return the elapsed time
     */
    String getElapsedTime();

    /**
     * Sets the elapsed time.
     *
     * @param elapsedTime the new elapsed time
     */
    void setElapsedTime(String elapsedTime);

    /**
     * Checks if is analyze.
     *
     * @return true, if is analyze
     */
    boolean isAnalyze();

    /**
     * Start query timer.
     */
    void startQueryTimer();

    /**
     * Stop query timer.
     */
    void stopQueryTimer();

    /**
     * Sets the query execution status.
     *
     * @param b the new query execution status
     */
    void setQueryExecutionStatus(boolean b);

    /**
     * Sets the analyze.
     *
     * @param analyze the new analyze
     */
    void setAnalyze(boolean analyze);

    /**
     * Sets the query start date.
     *
     * @param queryStatDate the new query start date
     */
    void setQueryStartDate(String queryStatDate);

    /**
     * Sets the current query.
     *
     * @param query the new current query
     */
    void setCurrentQuery(String query);

}
