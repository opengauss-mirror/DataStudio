/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.sqlhistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.SQLTerminalQuerySplit;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLHistoryCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class SQLHistoryCore {

    // holds the history items that are valid.
    private List<SQLHistoryItemDetail> unpinnedQueryHistory;

    // holds the history items that are pinned.
    private List<SQLHistoryItemDetail> pinnedQueryHistory;

    private SQLHistoryCorePersistence historyPersistence;

    // this lock object is to protect the concurrent access to the lists.
    private final Object lockObject = new Object();

    // holds the max allowed size of the valid list + pinned history items
    private int historySize;

    // if true,makes the history details to disk.
    private boolean needSupportPersistence;

    private boolean loadingInProgress;

    private ScheduledExecutorService executor;

    private static final int GRACEFULLY_SHUTDOWN_HISTORY = 0;

    private static final long SQL_HISTORY_THREAD_SLEEP_DURATION_IN_MS = 10;

    private int querySizeToStore;

    /**
     * Instantiates a new SQL history core.
     *
     * @param profileName the profile name
     * @param path the path
     * @param historyRetensionSize the history retension size
     * @param queryMaxSize the query max size
     * @param needPersistence the need persistence
     */
    public SQLHistoryCore(String profileName, String path, int historyRetensionSize, int queryMaxSize,
            boolean needPersistence) {
        querySizeToStore = queryMaxSize;
        historySize = historyRetensionSize;
        needSupportPersistence = needPersistence;

        unpinnedQueryHistory = new LinkedList<SQLHistoryItemDetail>();
        pinnedQueryHistory = new LinkedList<SQLHistoryItemDetail>();

        if (needSupportPersistence) {
            historyPersistence = new SQLHistoryCorePersistence(path, historySize);
            loadingInProgress = true;

            executor = Executors.newSingleThreadScheduledExecutor();

            Runnable periodicTask = new Runnable() {

                /**
                 * Run.
                 */
                public void run() {
                    doPersistence();
                }

                private void doPersistence() {
                    if (loadingInProgress) {
                        performInitailLoad();
                        return;
                    }

                    persistHistory();
                }

                private void performInitailLoad() {
                    try {
                        List<SQLHistoryItemDetail> unpinnedQueryHistory1 = historyPersistence.loadValidQueries();
                        List<SQLHistoryItemDetail> pinnedQueryHistory1 = historyPersistence.loadPinnedQueries();
                        synchronized (lockObject) {
                            mergeLists(unpinnedQueryHistory1, unpinnedQueryHistory);
                            mergeLists(pinnedQueryHistory1, pinnedQueryHistory);
                        }

                        historyPersistence.deleteOlderUnwantedFiles();
                    } catch (DatabaseOperationException exception) {
                        MPPDBIDELoggerUtility.error("Database operation failed.", exception);
                    } finally {
                        loadingInProgress = false;
                    }
                }

                private void mergeLists(List<SQLHistoryItemDetail> srcList, List<SQLHistoryItemDetail> destList) {
                    int size = srcList.size();
                    for (int i = 0; i < size; i++) {
                        addItemToList(destList, srcList.get(i));
                    }

                }
            };

            executor.scheduleAtFixedRate(periodicTask, 0, 10, TimeUnit.SECONDS);
        }

    }

    /**
     * Adds the query summary.
     *
     * @param summary the summary
     */
    public void addQuerySummary(QueryExecutionSummary summary) {
        if (!validateQuery(summary)) {
            SQLHistoryItemDetail newItem = new SQLHistoryItemDetail(summary, querySizeToStore);
            addItemToList(unpinnedQueryHistory, newItem);
        }

    }

    private boolean validateQuery(QueryExecutionSummary summary) {
        String regex = ".*(?i)create\\s*user.*|.*alter\\s*user.*|.*alter\\s*role.*|.*create\\s*role.*"
                + "|.*identified\\s*by.*|.*password.*|.*gs_encrypt.*|.*gs_decrypt.*";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        String query = SQLTerminalQuerySplit.splitQueryForquotes(summary.getQuery().trim()).toString();
        /*
         * Regex is very expensive so cutting down the greedy search expence
         */
        if (query.toLowerCase(Locale.ENGLISH).contains("identified")
                || query.toLowerCase(Locale.ENGLISH).contains("password")
                || query.toLowerCase(Locale.ENGLISH).contains("user")
                || query.toLowerCase(Locale.ENGLISH).contains("role")
                || query.toLowerCase(Locale.ENGLISH).contains("gs_encrypt")
                || query.toLowerCase(Locale.ENGLISH).contains("gs_decrypt")) {
            if (pattern.matcher(query).find()) {
                return true;
            }
        }
        if (!query.isEmpty() && query.charAt(0) == '/') {
            return isOnlyCommentedQuery(query);
        }
        if (query.charAt(0) == '-') {
            return !containsQuery(query);
        } else {
            return false;
        }
    }

    private boolean containsQuery(String query) {
        String[] actualQuery = null;
        actualQuery = query.split(MPPDBIDEConstants.LINE_SEPARATOR);
        for (int i = 0; i < actualQuery.length; i++) {
            if (!actualQuery[i].isEmpty() && actualQuery[i].charAt(0) != '-') {
                return true;
            }
        }
        return false;
    }

    private boolean isOnlyCommentedQuery(String query) {
        if (query.startsWith("/*") && query.endsWith("*/")) {
            return true;
        } else {
            return false;
        }
    }

    private void addItemToList(List<SQLHistoryItemDetail> addToList, SQLHistoryItemDetail newItem) {
        synchronized (lockObject) {
            int nonExistingIndex = 0;
            int insertionPoint = Collections.binarySearch(addToList, newItem, newItem.getComparator());

            if (insertionPoint < nonExistingIndex) {
                insertionPoint = -(insertionPoint + 1);
            }

            if (unpinnedQueryHistory.size() + pinnedQueryHistory.size() >= historySize) {
                removeOldestHistoryItem();
            }

            if (insertionPoint > addToList.size()) {
                insertionPoint = addToList.size();
            }

            if ((unpinnedQueryHistory.size() + pinnedQueryHistory.size() < historySize) || newItem.isPinned()) {
                addToList.add(insertionPoint, newItem);
            }
        }
    }

    private void removeOldestHistoryItem() {
        if (unpinnedQueryHistory.size() > 0) {
            int oldestItemPosition = unpinnedQueryHistory.size() - 1;
            SQLHistoryItemDetail oldestItem = unpinnedQueryHistory.get(oldestItemPosition);

            if (needSupportPersistence) {
                historyPersistence.addtoDeleteList(oldestItem);
            }

            unpinnedQueryHistory.remove(oldestItemPosition);
        }
    }

    /**
     * Sets the pin status.
     *
     * @param itemToPin the item to pin
     * @param needToPin the need to pin
     */
    public void setPinStatus(SQLHistoryItem itemToPin, boolean needToPin) {

        if (!(itemToPin instanceof SQLHistoryItemDetail)) {
            return;
        }

        SQLHistoryItemDetail detailItem = (SQLHistoryItemDetail) itemToPin;
        itemToPin.setPinned(needToPin);
        if (needToPin) {
            synchronized (lockObject) {
                boolean isremoved = unpinnedQueryHistory.remove(detailItem);
                if (isremoved) {
                    addItemToList(pinnedQueryHistory, detailItem);
                }
            }
        } else {
            /* Unpin */
            synchronized (lockObject) {
                pinnedQueryHistory.remove(detailItem);
                addItemToList(unpinnedQueryHistory, detailItem);
            }

        }
    }

    /**
     * Gets the all history content.
     *
     * @return the all history content
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public List<SQLHistoryItem> getAllHistoryContent() throws MPPDBIDEException {
        if (loadingInProgress) {
            try {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_SQL_HISTORY_NOT_LOADED));
                throw new MPPDBIDEException(IMessagesConstants.ERR_BL_SQL_HISTORY_NOT_LOADED);
            } catch (MPPDBIDEException exe) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_SQL_HISTORY_NOT_LOADED), exe);
                throw new MPPDBIDEException(IMessagesConstants.ERR_BL_SQL_HISTORY_NOT_LOADED, exe);
            }
        }

        List<SQLHistoryItem> returnList = new LinkedList<SQLHistoryItem>();

        synchronized (lockObject) {
            returnList.addAll(0, unpinnedQueryHistory);
            returnList.addAll(0, pinnedQueryHistory);
        }
        return returnList;
    }

    /**
     * Gets the history content.
     *
     * @param itemCount the item count
     * @return the history content
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public List<SQLHistoryItem> getHistoryContent(int itemCount) throws MPPDBIDEException {
        List<SQLHistoryItem> allHistoryContent = getAllHistoryContent();
        if (allHistoryContent.size() > itemCount) {
            return allHistoryContent.subList(0, itemCount);
        } else {
            return allHistoryContent;
        }
    }

    /**
     * Persist history.
     */
    public void persistHistory() {
        if (needSupportPersistence) {
            List<SQLHistoryItemDetail> pinnedqueryHistoryToPersist = new LinkedList<SQLHistoryItemDetail>();
            List<SQLHistoryItemDetail> unpinnedqueryHistoryToPersist = new LinkedList<SQLHistoryItemDetail>();

            synchronized (lockObject) {
                pinnedqueryHistoryToPersist.addAll(pinnedQueryHistory);
                unpinnedqueryHistoryToPersist.addAll(unpinnedQueryHistory);
            }

            historyPersistence.persistHistory(unpinnedqueryHistoryToPersist, pinnedqueryHistoryToPersist);
        }
    }

    /**
     * Cancel persist.
     */
    public void cancelPersist() {
        if (needSupportPersistence) {
            historyPersistence.cancelPersistenceOperation();
        }
    }

    /**
     * Delete history items.
     *
     * @param arr the arr
     */
    public void deleteHistoryItems(List<SQLHistoryItem> arr) {
        int size = arr.size();
        for (int i = 0; i < size; i++) {
            int pos = unpinnedQueryHistory.indexOf(arr.get(i));
            SQLHistoryItemDetail itemToDel = null;

            if (-1 != pos) {
                synchronized (lockObject) {
                    itemToDel = unpinnedQueryHistory.get(pos);
                    unpinnedQueryHistory.remove(itemToDel);
                }

                if (needSupportPersistence) {
                    historyPersistence.addtoDeleteList(itemToDel);
                }
            }

        }

    }

    /**
     * Destroy.
     *
     * @param flag the flag
     */
    public void destroy(int flag) {
        if (needSupportPersistence) {
            if (flag == GRACEFULLY_SHUTDOWN_HISTORY) {

                /*
                 * executor is running,then allow to complete. else trigger
                 * once.
                 */
                executor.shutdown();
            } else {
                historyPersistence.cancelPersistenceOperation();
                /* if running terminal */
                executor.shutdown();

            }
            while (!executor.isShutdown()) {
                boolean exited = false;
                try {
                    exited = executor.awaitTermination(SQL_HISTORY_THREAD_SLEEP_DURATION_IN_MS, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    MPPDBIDELoggerUtility.warn("Warning: InterruptedException occurs but execution can be continue");
                    continue; // cant do anything;
                }
                if (exited) {
                    break;
                }
            }

            historyPersistence = null;
        }
        synchronized (lockObject) {

            pinnedQueryHistory.clear();
            unpinnedQueryHistory.clear();
        }

    }

    /**
     * Sets the SQL query size.
     *
     * @param querySize the new SQL query size
     */
    public void setSQLQuerySize(int querySize) {
        synchronized (lockObject) {

            this.querySizeToStore = querySize;
        }
    }

    /**
     * Sets the history size.
     *
     * @param historySize the new history size
     */
    public void setHistorySize(int historySize) {
        int currentHistorySize = 0;
        synchronized (lockObject) {
            this.historySize = historySize;
            currentHistorySize = pinnedQueryHistory.size() + unpinnedQueryHistory.size();
        }
        List<SQLHistoryItem> listToDelete = new ArrayList<>();

        if (currentHistorySize > historySize) {
            int numToDelete = currentHistorySize - historySize;
            if (unpinnedQueryHistory.size() < numToDelete) {
                numToDelete = unpinnedQueryHistory.size();
            }

            for (int index = 1; index <= numToDelete; index++) {
                listToDelete.add(unpinnedQueryHistory.get(unpinnedQueryHistory.size() - index));
            }

        }

        deleteHistoryItems(listToDelete);
    }

}
