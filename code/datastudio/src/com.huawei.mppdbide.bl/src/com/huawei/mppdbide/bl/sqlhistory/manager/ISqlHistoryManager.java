/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.sqlhistory.manager;

import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;

import com.huawei.mppdbide.bl.sqlhistory.QueryExecutionSummary;
import com.huawei.mppdbide.bl.sqlhistory.SQLHistoryItem;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ISqlHistoryManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface ISqlHistoryManager {

    /**
     * Gets the history content.
     *
     * @param profileId the profile id
     * @param itemCount the item count
     * @return the history content
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    List<SQLHistoryItem> getHistoryContent(String profileId, int itemCount) throws MPPDBIDEException;

    /**
     * Delete history items.
     *
     * @param arr the arr
     */
    void deleteHistoryItems(List<SQLHistoryItem> arr);

    /**
     * Sets the pin status.
     *
     * @param item the item
     * @param needToPin the need to pin
     * @return true, if successful
     */
    boolean setPinStatus(SQLHistoryItem item, boolean needToPin);

    /**
     * Sets the history retension size.
     *
     * @param size the new history retension size
     */
    void setHistoryRetensionSize(int size);

    /**
     * Adds the new query execution info.
     *
     * @param profileId the profile id
     * @param executionItem the execution item
     */
    void addNewQueryExecutionInfo(String profileId, QueryExecutionSummary executionItem);

    /**
     * Do history management for profile.
     *
     * @param profileId the profile id
     * @param path the path
     */
    void doHistoryManagementForProfile(String profileId, String path);

    /**
     * Removes the history management for profile.
     *
     * @param profileId the profile id
     */
    void removeHistoryManagementForProfile(String profileId);

    /**
     * Purge history.
     *
     * @param profileId the profile id
     */
    void purgeHistory(String profileId);

    /**
     * Stop history management for profile.
     *
     * @param profileId the profile id
     */
    void stopHistoryManagementForProfile(String profileId);

    /**
     * Purge historybefore close.
     */
    void purgeHistorybeforeClose();

    /**
     * Sets the SQL query size.
     *
     * @param queryLength the new SQL query size
     */
    void setSQLQuerySize(Integer queryLength);

    /**
     * Inits the.
     *
     * @param eventBroker the event broker
     */
    void init(IEventBroker eventBroker);

}
