/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.sqlhistory;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class QueryExecutionSummary.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class QueryExecutionSummary implements IQueryExecutionSummary {

    private String databaseName;
    private String profileId;
    private String query;
    private String elapsedTime;
    private boolean isQueryExecutionSuccess;
    private String queryStartDate;
    private int numRecordsFetched;
    private IExecTimer timer;
    private String profileName;

    /**
     * Instantiates a new query execution summary.
     *
     * @param dbname the dbname
     * @param profileId the profile id
     * @param query the query
     * @param executionResult the execution result
     * @param querySubmitTime the query submit time
     * @param elaspedTime the elasped time
     * @param numRecordsFetched the num records fetched
     */
    public QueryExecutionSummary(String dbname, String profileId, String query, boolean executionResult,
            String querySubmitTime, long elaspedTime, int numRecordsFetched) {
        this.databaseName = dbname;
        this.profileId = profileId;
        this.query = query;
        this.elapsedTime = ExecTimer.getElapsedTimeWithUnits(elaspedTime);
        this.isQueryExecutionSuccess = executionResult;
        this.queryStartDate = querySubmitTime;
        this.numRecordsFetched = numRecordsFetched;
    }

    /**
     * Instantiates a new query execution summary.
     *
     * @param dbname the dbname
     * @param profileName the profile name
     * @param profileID the profile ID
     * @param query the query
     */
    public QueryExecutionSummary(String dbname, String profileName, String profileID, String query) {
        this.databaseName = dbname;
        this.profileId = profileID;
        this.profileName = profileName;
        this.query = query;
        this.isQueryExecutionSuccess = false;
        this.numRecordsFetched = 0;
    }

    /**
     * startQueryTimer.
     */
    public void startQueryTimer() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MPPDBIDEConstants.DATE_FORMAT);
        this.queryStartDate = simpleDateFormat.format(new Date());
        this.timer = new ExecTimer("End to end Query execution ");
        this.timer.start();
    }

    /**
     * stopQueryTimer.
     */
    public void stopQueryTimer() {
        long timetaken = 0;
        try {
            this.timer.stop();
            timetaken = this.timer.getElapsedTimeInMs();
        } catch (DatabaseOperationException e) {
            /* Nothing to do here */
            MPPDBIDELoggerUtility.none("Nothing to do here");
        }

        finally {
            this.setElapsedTime(ExecTimer.getElapsedTimeWithUnits(timetaken));
        }
    }

    /**
     * Sets the query execution status.
     *
     * @param isQueryExecutnSuccess the new query execution status
     */
    public void setQueryExecutionStatus(boolean isQueryExecutnSuccess) {
        this.isQueryExecutionSuccess = isQueryExecutnSuccess;
    }

    /**
     * Gets the database name.
     *
     * @return getDatabaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Gets the profile id.
     *
     * @return getProfileId
     */
    public String getProfileId() {
        return profileId;
    }

    /**
     * Gets the query.
     *
     * @return getQuery
     */
    public String getQuery() {
        return query;
    }

    /**
     * Checks if is query execution success.
     *
     * @return true, if is query execution success
     */
    public boolean isQueryExecutionSuccess() {
        return isQueryExecutionSuccess;
    }

    /**
     * Gets the execution time.
     *
     * @return getExecutionTime
     */
    public Date getExecutionTime() {

        return null;
    }

    /**
     * Gets the query start date.
     *
     * @return getQueryStartDate
     */
    public String getQueryStartDate() {
        return queryStartDate;
    }

    /**
     * Gets the num records fetched.
     *
     * @return getNumRecordsFetched
     */
    public int getNumRecordsFetched() {
        return numRecordsFetched;
    }

    /**
     * Sets the num records fetched.
     *
     * @param numRecordsFetched the new num records fetched
     */
    public void setNumRecordsFetched(int numRecordsFetched) {
        this.numRecordsFetched = numRecordsFetched;
    }

    /**
     * Gets the elapsed time.
     *
     * @return the elapsed time
     */
    public String getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Sets the elapsed time.
     *
     * @param elapsedTime the new elapsed time
     */
    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    /**
     * Gets the profile name.
     *
     * @return getProfileName
     */
    @Override
    public String getProfileName() {
        return this.profileName;
    }

    /**
     * Checks if is analyze.
     *
     * @return isAnalyze
     */
    @Override
    public boolean isAnalyze() {
        return false;
    }

    /**
     * Sets the analyze.
     *
     * @param analyze the new analyze
     */
    @Override
    public void setAnalyze(boolean analyze) {

    }

    /**
     * Sets the query start date.
     *
     * @param queryStatDate the new query start date
     */
    @Override
    public void setQueryStartDate(String queryStatDate) {
        this.queryStartDate = queryStatDate;
    }

    @Override
    public void setCurrentQuery(String query) {
        this.query = query;
    }
}
