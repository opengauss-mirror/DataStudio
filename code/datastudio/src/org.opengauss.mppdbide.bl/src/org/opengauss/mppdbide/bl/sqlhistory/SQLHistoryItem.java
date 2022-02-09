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

package org.opengauss.mppdbide.bl.sqlhistory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLHistoryItem.
 * 
 */

public class SQLHistoryItem implements ISQLHistoryItem {

    private String query;
    private String databaseName;
    private String profileId;
    private String executionTime;
    private String elapsedTime;
    private boolean isPinned;
    private boolean finalStatus;
    private int resultSetSize;
    private int querySizeToStore;


    /**
     * Instantiates a new SQL history item.
     *
     * @param summary the summary
     * @param querySize the query size
     */
    public SQLHistoryItem(QueryExecutionSummary summary, int querySize) {
        this.querySizeToStore = querySize;
        String qry = summary.getQuery();

        setQuery(qry);
        this.databaseName = summary.getDatabaseName();
        this.isPinned = false;
        this.executionTime = summary.getQueryStartDate();
        this.elapsedTime = summary.getElapsedTime();
        this.finalStatus = summary.isQueryExecutionSuccess();
        this.profileId = summary.getProfileId();
        this.resultSetSize = summary.getNumRecordsFetched();
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
     * Sets the query.
     *
     * @param query the new query
     */
    public final void setQuery(String query) {
        String qry = query + ';';
        // If 0, then consider storing the complete query
        if (querySizeToStore == 0) {
            this.query = qry;
        } else {
            int length = qry.length() <= querySizeToStore ? qry.length() : querySizeToStore;
            this.query = qry.substring(0, length);
        }
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
     * Sets the database name.
     *
     * @param databaseName the new database name
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
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
     * Sets the profile id.
     *
     * @param profileId the new profile id
     */
    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    /**
     * Gets the execution time.
     *
     * @return getExecutionTime
     */
    public String getExecutionTime() {
        if (executionTime == null) {
            return null;
        }
        return executionTime;
    }


    /**
     * Checks if is pinned.
     *
     * @return isPinned
     */
    public boolean isPinned() {
        return isPinned;
    }

    /**
     * Sets the pinned.
     *
     * @param isPined the new pinned
     */
    public void setPinned(boolean isPined) {
        this.isPinned = isPined;
    }

    /**
     * Gets the final status.
     *
     * @return getFinalStatus
     */
    public boolean getFinalStatus() {
        return finalStatus;
    }

    /**
     * Sets the final status.
     *
     * @param finalStatus the new final status
     */
    public void setFinalStatus(boolean finalStatus) {
        this.finalStatus = finalStatus;
    }

    /**
     * Gets the result set size.
     *
     * @return getResultSetSize
     */
    public int getResultSetSize() {
        return resultSetSize;
    }

    /**
     * Sets the result set size.
     *
     * @param resultSetSize the new result set size
     */
    public void setResultSetSize(int resultSetSize) {
        this.resultSetSize = resultSetSize;
    }

    /**
     * Gets the elapsed time.
     *
     * @return getElapsedTime
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
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }

        if (!(obj instanceof SQLHistoryItem)) {
            return false;
        }

        SQLHistoryItem compareObj = (SQLHistoryItem) obj;

        if (this.databaseName.equals(compareObj.databaseName) && this.profileId.equals(compareObj.profileId)
                && this.query.equals(compareObj.query) && this.executionTime.equals(compareObj.executionTime)) {
            return true;
        }

        return false;
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getDatabaseName().hashCode() + getProfileId().hashCode() + getQuery().hashCode()
                + (null == getExecutionTime() ? 0 : getExecutionTime().hashCode());

        return result;
    }

    /**
     * Gets the comparator.
     *
     * @return the comparator
     */
    public SQLHistoryItemComapartor getComparator() {
        return new SQLHistoryItemComapartor();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SQLHistoryItemComapartor.
     * 
     */
    private static class SQLHistoryItemComapartor implements Comparator<SQLHistoryItem>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(SQLHistoryItem object1, SQLHistoryItem object2) {
            // since the latest should be given first
            return object2.executionTime.compareTo(object1.executionTime);
        }
    }
}
