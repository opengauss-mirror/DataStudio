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

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ISQLHistoryItem.
 * 
 */

public interface ISQLHistoryItem {

    /**
     * Gets the query.
     *
     * @return the query
     */
    String getQuery();

    /**
     * Sets the query.
     *
     * @param query the new query
     */
    void setQuery(String query);

    /**
     * Gets the database name.
     *
     * @return the database name
     */
    String getDatabaseName();

    /**
     * Sets the database name.
     *
     * @param databaseName the new database name
     */
    void setDatabaseName(String databaseName);

    /**
     * Gets the profile id.
     *
     * @return the profile id
     */
    String getProfileId();

    /**
     * Sets the profile id.
     *
     * @param profileId the new profile id
     */
    void setProfileId(String profileId);

    /**
     * Gets the execution time.
     *
     * @return the execution time
     */
    String getExecutionTime();

    /**
     * Checks if is pinned.
     *
     * @return true, if is pinned
     */
    boolean isPinned();

    /**
     * Sets the pinned.
     *
     * @param isPinned the new pinned
     */
    void setPinned(boolean isPinned);

    /**
     * Gets the final status.
     *
     * @return the final status
     */
    boolean getFinalStatus();

    /**
     * Sets the final status.
     *
     * @param finalStatus the new final status
     */
    void setFinalStatus(boolean finalStatus);

    /**
     * Gets the result set size.
     *
     * @return the result set size
     */
    int getResultSetSize();

    /**
     * Sets the result set size.
     *
     * @param resultSetSize the new result set size
     */
    void setResultSetSize(int resultSetSize);

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
}
