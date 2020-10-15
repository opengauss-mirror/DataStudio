/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.sqlhistory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;

import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.sqlhistory.manager.ISqlHistoryManager;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLHistoryManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public final class SQLHistoryManager implements ISqlHistoryManager {

    private static volatile SQLHistoryManager hismgr;
    private HashMap<String, SQLHistoryCore> profileHistroy;
    private int historyRetensionSize;

    private static final int GRACEFULLY_SHUTDOWN_HISTORY = 0;
    private static final int ABORT_SHUTDOWN_HISTORY = 1;
    private static final Object LOCK = new Object();
    private int queryMaxSize;

    private SQLHistoryManager() {
        profileHistroy = new HashMap<String, SQLHistoryCore>(10);
        historyRetensionSize = BLPreferenceManager.getInstance().getBLPreference().getSQLHistorySize();
        queryMaxSize = BLPreferenceManager.getInstance().getBLPreference().getSQLQueryLength();
    }

    /**
     * Gets the single instance of SQLHistoryManager.
     *
     * @return single instance of SQLHistoryManager
     */
    public static ISqlHistoryManager getInstance() {
        if (hismgr == null) {

            synchronized (LOCK) {
                if (hismgr == null) {
                    hismgr = new SQLHistoryManager();
                }

            }
        }

        return hismgr;
    }

    /**
     * Inits the.
     *
     * @param eventBroker the event broker
     */
    public void init(IEventBroker eventBroker) {
        SQLHistoryEventListener hisListener = new SQLHistoryEventListener();
        hisListener.init(eventBroker);
    }

    @Override
    public List<SQLHistoryItem> getHistoryContent(String profileId, int itemCount) throws MPPDBIDEException {

        SQLHistoryCore profHistoryCore = profileHistroy.get(profileId);
        if (null != profHistoryCore) {
            return profHistoryCore.getAllHistoryContent();
        } else {
            return new LinkedList<SQLHistoryItem>();
        }

    }

    @Override
    public void deleteHistoryItems(List<SQLHistoryItem> arr) {
        SQLHistoryCore profHistoryCore = profileHistroy.get(arr.get(0).getProfileId());
        profHistoryCore.deleteHistoryItems(arr);
    }

    @Override
    public boolean setPinStatus(SQLHistoryItem item, boolean needToPin) {
        if (null != item) {
            SQLHistoryCore profHistoryCore = profileHistroy.get(item.getProfileId());
            profHistoryCore.setPinStatus(item, needToPin);
            return true;
        }
        return false;
    }

    @Override
    public void setHistoryRetensionSize(int size) {
        historyRetensionSize = size;
        for (SQLHistoryCore core : profileHistroy.values()) {
            core.setHistorySize(size);
        }

    }

    @Override
    public void addNewQueryExecutionInfo(String profileId, QueryExecutionSummary summary) {
        SQLHistoryCore profHistoryCore = profileHistroy.get(profileId);
        if (null != profHistoryCore) {
            profHistoryCore.addQuerySummary(summary);
        }
    }

    @Override
    public void doHistoryManagementForProfile(String profileId, String path) {
        SQLHistoryCore sqlHistoryCore = profileHistroy.get(profileId);
        if (sqlHistoryCore == null) {
            SQLHistoryCore core = new SQLHistoryCore(profileId, path, historyRetensionSize, queryMaxSize, true);
            profileHistroy.put(profileId, core);
        }

    }

    @Override
    public void stopHistoryManagementForProfile(String profileId) {
        SQLHistoryCore core = profileHistroy.remove(profileId);
        if (core == null) {
            return;
        }

        core.destroy(GRACEFULLY_SHUTDOWN_HISTORY);
    }

    /**
     * Remove the history for the profile from disk and memory.
     *
     * @param profileId the profile id
     */
    @Override
    public void removeHistoryManagementForProfile(String profileId) {
        SQLHistoryCore core = profileHistroy.remove(profileId);
        if (core == null) {
            return;
        }
        core.destroy(ABORT_SHUTDOWN_HISTORY);
    }

    @Override
    public void purgeHistory(String profileId) {
        SQLHistoryCore profHistoryCore = profileHistroy.get(profileId);
        if (profHistoryCore == null) {
            return;
        }
        profHistoryCore.persistHistory();
    }

    /**
     * Purge historybefore close.
     */
    public void purgeHistorybeforeClose() {
        for (SQLHistoryCore profHistoryCore : profileHistroy.values()) {
            if (profHistoryCore != null) {
                profHistoryCore.persistHistory();
            }
        }

    }

    @Override
    public void setSQLQuerySize(Integer queryLength) {
        this.queryMaxSize = queryLength;
        for (SQLHistoryCore core : profileHistroy.values()) {
            core.setSQLQuerySize(queryLength);
        }

    }

}
