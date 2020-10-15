/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.contentassist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.GaussOLAPDBMSObject;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ContentAssistProcesserData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ContentAssistProcesserData implements ISQLContentAssistProcessor {

    private Database database;

    private List<ServerObject> nonLoaded = new ArrayList<ServerObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

    private ContentAssistUtilIf contentAssistUtil;

    /**
     * Gets the content assist util.
     *
     * @return the content assist util
     */
    public ContentAssistUtilIf getContentAssistUtil() {
        return contentAssistUtil;
    }

    /**
     * Instantiates a new content assist processer data.
     *
     * @param servObj the serv obj
     */
    public ContentAssistProcesserData(ServerObject servObj) {
        if (servObj instanceof Database) {
            this.database = (Database) servObj;
            contentAssistUtil = new ContentAssistUtilOLAP(database);
        } else if (null == servObj) {
            this.database = null;
            contentAssistUtil = new ContentAssistUtilDefault();
        }

    }

    /**
     * Find exact matching objects.
     *
     * @param prefixs the prefixs
     * @return the sorted map
     */
    public LinkedHashMap<String, ServerObject> findExactMatchingObjects(String[] prefixs) {
        int prefixLength = prefixs.length;
        LinkedHashMap<String, ServerObject> objMap1 = new LinkedHashMap<String, ServerObject>();
        SortedMap<String, ServerObject> objMap = new TreeMap<String, ServerObject>(new AutoSuggestComparator());
        switch (prefixLength) {
            case 1: {
                objMap = findExactMatchingObjects(prefixs[0]);
                objMap = findAllChildObjects(objMap);
                break;
            }
            case 2: {
                objMap = findExactMatchingObjects(prefixs[0]);
                objMap = findExactMatchingChildObjects(prefixs[1], objMap);
                objMap = findAllChildObjects(objMap);
                break;
            }
            default: {
                // Ignore other types
                break;
            }
        }
        objMap1.putAll(objMap);
        return objMap1;
    }

    /**
     * Find all child objects.
     *
     * @param parentObjMap the parent obj map
     * @return the sorted map
     */
    private SortedMap<String, ServerObject> findAllChildObjects(SortedMap<String, ServerObject> parentObjMap) {
        SortedMap<String, ServerObject> found = new TreeMap<String, ServerObject>(new AutoSuggestComparator());
        for (ServerObject servObj : parentObjMap.values()) {
            if (servObj instanceof GaussOLAPDBMSObject) {
                if (!isInsert() && servObj instanceof Namespace) {
                    Namespace ns = (Namespace) servObj;
                    found.putAll(ns.findAllChildObjects());
                } else if (servObj instanceof ForeignTable) {
                    ForeignTable fTable = (ForeignTable) servObj;
                    found.putAll(fTable.findAllChildObjects());
                } else if (servObj instanceof PartitionTable) {
                    PartitionTable partitionTable = PartitionTable.class.cast(servObj);
                    found.putAll(partitionTable.findAllChildObjects());
                } else if (servObj instanceof TableMetaData) {
                    TableMetaData tbl = (TableMetaData) servObj;
                    found.putAll(tbl.findAllChildObjects());
                } else if (servObj instanceof ViewMetaData) {
                    ViewMetaData view = ViewMetaData.class.cast(servObj);
                    found.putAll(view.findAllChildObjects());
                }
            }
            if (isInsert()) {
                found = contentAssistUtil.getChildObject(found, parentObjMap.size() > 1);
            }
            if (!servObj.isLoaded() && nonLoaded != null) {
                nonLoaded.add(servObj);
            }
        }

        return found;
    }

    /**
     * Find case insensitive matching objects.
     *
     * @param prefixParam the prefix param
     * @return the sorted map
     */
    public LinkedHashMap<String, ServerObject> findCaseInsensitiveMatchingObjects(String prefixParam) {
        String prefix = prefixParam.toLowerCase(Locale.getDefault());
        LinkedHashMap<String, ServerObject> retChildMap = new LinkedHashMap<String, ServerObject>();
        SortedMap<String, ServerObject> retKeywordMap = new TreeMap<String, ServerObject>(new AutoSuggestComparator());
        retKeywordMap.putAll(findPrefixMatchingKeywords(prefix));
        SortedMap<String, ServerObject> retDatatypeMap = new TreeMap<String, ServerObject>(new AutoSuggestComparator());
        retDatatypeMap.putAll(findPrefixMatchingDatatypes(prefix));
        SortedMap<String, ServerObject> retObjMap = new TreeMap<String, ServerObject>(new AutoSuggestComparator());
        retObjMap.putAll(findPrefixMatchingObjects(prefix));
        retChildMap.putAll(retKeywordMap);
        retChildMap.putAll(retDatatypeMap);
        retChildMap.putAll(retObjMap);
        return retChildMap;
    }

    /**
     * Find case insensitive matching child objects.
     *
     * @param prefix the prefix
     * @param parentObjMap the parent obj map
     * @return the sorted map
     */
    public SortedMap<String, ServerObject> findCaseInsensitiveMatchingChildObjects(String prefix,
            SortedMap<String, ServerObject> parentObjMap) {
        SortedMap<String, ServerObject> retChildMap = new TreeMap<String, ServerObject>(new AutoSuggestComparator());

        retChildMap.putAll(findPrefixMatchingChildObjects(prefix, parentObjMap));
        if (!prefix.contains("\"")) {
            String strUpperPrefix = prefix.toUpperCase(Locale.ENGLISH);
            String strLowerPrefix = prefix.toLowerCase(Locale.ENGLISH);
            if (!prefix.equals(strUpperPrefix)) {
                retChildMap.putAll(findPrefixMatchingChildObjects(strUpperPrefix, parentObjMap));
            }
            if (!prefix.equals(strLowerPrefix)) {
                retChildMap.putAll(findPrefixMatchingChildObjects(strLowerPrefix, parentObjMap));
            }
        }
        return retChildMap;
    }

    /**
     * Find prefix matching objects.
     *
     * @param prefixs the prefixs
     * @return the sorted map
     */
    public LinkedHashMap<String, ServerObject> findPrefixMatchingObjects(String[] prefixs) {
        int prefixLength = prefixs.length;
        LinkedHashMap<String, ServerObject> objMap1 = new LinkedHashMap<String, ServerObject>();
        SortedMap<String, ServerObject> objMapChild = null;
        SortedMap<String, ServerObject> objMapClmChild = null;
        switch (prefixLength) {
            case 0: {
                objMap1 = findCaseInsensitiveMatchingObjects("");
                break;
            }
            case 1: {
                objMap1 = findCaseInsensitiveMatchingObjects(prefixs[0]);
                break;
            }
            case 2: {
                SortedMap<String, ServerObject> objMap = findExactMatchingObjects(prefixs[0]);
                objMapClmChild = findCaseInsensitiveMatchingChildObjects(prefixs[1], objMap);
                break;
            }
            case 3: {
                objMapChild = findExactMatchingObjects(prefixs[0]);
                objMapChild = findExactMatchingChildObjects(prefixs[1], objMapChild);
                objMapClmChild = findCaseInsensitiveMatchingChildObjects(prefixs[2], objMapChild);

                break;
            }
            default: {
                // Ignore other types
                break;
            }
        }
        if (objMapClmChild != null) {
            if (isInsert() && objMapChild != null) {
                objMapClmChild = contentAssistUtil.getChildObject(objMapClmChild, objMapChild.size() > 1);
            }
            objMap1.putAll(objMapClmChild);
        }
        return objMap1;
    }

    /**
     * Find prefix matching child objects.
     *
     * @param prefix the prefix
     * @param parentObjMap the parent obj map
     * @return the sorted map
     */
    private SortedMap<String, ServerObject> findPrefixMatchingChildObjects(String prefix,
            SortedMap<String, ServerObject> parentObjMap) {
        SortedMap<String, ServerObject> found = new TreeMap<String, ServerObject>(new AutoSuggestComparator());
        for (ServerObject servObj : parentObjMap.values()) {
            if (servObj instanceof Namespace) {
                Namespace ns = (Namespace) servObj;
                found.putAll(ns.findPrefixMatchingChildObjects(prefix));
            } else if (servObj instanceof ForeignTable) {
                ForeignTable fTable = (ForeignTable) servObj;
                found.putAll(fTable.findMatchingChildObjects(prefix));
            } else if (servObj instanceof PartitionTable) {
                PartitionTable partitionTable = PartitionTable.class.cast(servObj);
                found.putAll(partitionTable.findMatchingChildObjects(prefix));
            } else if (servObj instanceof TableMetaData) {
                TableMetaData tbl = (TableMetaData) servObj;
                found.putAll(tbl.findMatchingChildObjects(prefix));
            } else if (servObj instanceof ViewMetaData) {
                ViewMetaData view = ViewMetaData.class.cast(servObj);
                found.putAll(view.findMatchingChildObject(prefix));
            }
        }
        return found;
    }

    /**
     * Find prefix matching keywords.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    public SortedMap<String, ServerObject> findPrefixMatchingKeywords(String prefix) {

        SortedMap<String, ServerObject> keywordObjMap = new TreeMap<String, ServerObject>(new AutoSuggestComparator());
        keywordObjMap.putAll(contentAssistUtil.findMatchingKeywords(prefix));
        return keywordObjMap;
    }

    /**
     * Find prefix matching datatypes.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    public SortedMap<String, ServerObject> findPrefixMatchingDatatypes(String prefix) {
        SortedMap<String, ServerObject> datatypeObjMap = new TreeMap<String, ServerObject>(new AutoSuggestComparator());
        datatypeObjMap.putAll(contentAssistUtil.findMatchingDataypes(prefix));
        return datatypeObjMap;
    }

    /**
     * Find prefix matching objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findPrefixMatchingObjects(String prefix) {
        SortedMap<String, ServerObject> objMap = new TreeMap<String, ServerObject>(new AutoSuggestComparator());
        objMap.putAll(contentAssistUtil.findMatchingNamespace(prefix));
        objMap.putAll(contentAssistUtil.findMatchingTables(prefix));
        objMap.putAll(contentAssistUtil.findMatchingViewsObject(prefix));
        objMap.putAll(contentAssistUtil.findMatchingDebugObjects(prefix));
        objMap.putAll(contentAssistUtil.findMatchingSequenceObject(prefix));
        objMap.putAll(contentAssistUtil.findMatchingSynonyms(prefix));
        objMap.putAll(contentAssistUtil.findMatchingTriggerObject(prefix));

        return objMap;
    }

    /**
     * Find exact matching child objects.
     *
     * @param prefix the prefix
     * @param objMap the obj map
     * @return the sorted map
     */
    private SortedMap<String, ServerObject> findExactMatchingChildObjects(String prefix,
            SortedMap<String, ServerObject> objMap) {

        SortedMap<String, ServerObject> found = new TreeMap<String, ServerObject>(new AutoSuggestComparator());
        for (ServerObject serverObj : objMap.values()) {
            if (serverObj instanceof Namespace) {
                Namespace ns = (Namespace) serverObj;
                found.putAll(ns.findExactMatchingChildObjects(prefix));
            } else if (serverObj instanceof ForeignTable) {
                ForeignTable fTable = (ForeignTable) serverObj;
                found.putAll(fTable.findMatchingChildObjects(prefix));
            } else if (serverObj instanceof PartitionTable) {
                PartitionTable partitionTable = PartitionTable.class.cast(serverObj);
                found.putAll(partitionTable.findMatchingChildObjects(prefix));
            } else if (serverObj instanceof TableMetaData) {
                TableMetaData tbl = (TableMetaData) serverObj;
                found.putAll(tbl.findMatchingChildObjects(prefix));
            } else if (serverObj instanceof ViewMetaData) {
                ViewMetaData view = ViewMetaData.class.cast(serverObj);
                found.putAll(view.findMatchingChildObject(prefix));
            }
        }
        return found;
    }

    /**
     * Find exact matching objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingObjects(String prefix) {
        SortedMap<String, ServerObject> objMap = new TreeMap<String, ServerObject>(new AutoSuggestComparator());
        objMap.putAll(contentAssistUtil.findExactMatchingNamespaces(prefix));
        objMap.putAll(contentAssistUtil.findExactMatchingTables(prefix));
        objMap.putAll(contentAssistUtil.findExactMatchingViews(prefix));
        objMap.putAll(contentAssistUtil.findExactMatchingDebugObjects(prefix));
        objMap.putAll(contentAssistUtil.findExactMatchingSequences(prefix));
        objMap.putAll(contentAssistUtil.findExactMatchingSynonyms(prefix));
        objMap.putAll(contentAssistUtil.findExactMatchingTriggers(prefix));

        return objMap;

    }

    /**
     * Gets the non loaded.
     *
     * @return the non loaded
     */
    public List<ServerObject> getNonLoaded() {
        return nonLoaded;
    }

    /**
     * Gets the find string.
     *
     * @param pretext the pretext
     * @param workBreakCharList the work break char list
     * @return the find string
     */
    public String getFindString(String pretext, List<Character> workBreakCharList) {
        return contentAssistUtil.findString(pretext, workBreakCharList);
    }

    /**
     * Gets the removd literal escapes and quotes.
     *
     * @param str the str
     * @return the removd literal escapes and quotes
     */
    public String getRemovdLiteralEscapesAndQuotes(String str) {
        return contentAssistUtil.removeLiteralEscapesAndQuotes(str);
    }

    /**
     * Gets the split prefix by dots.
     *
     * @param prefix the prefix
     * @return the split prefix by dots
     */
    public String[] getSplitPrefixByDots(String prefix) {
        return contentAssistUtil.splitPrefixByDots(prefix);
    }

    /**
     * Find non loaded objects.
     *
     * @param connection the connection
     * @param prefixs the prefixs
     * @return the sorted map
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public LinkedHashMap<String, ServerObject> findNonLoadedObjects(DBConnection connection, String[] prefixs)
            throws MPPDBIDEException {
        loadDatabaseObjects(connection);
        return findExactMatchingObjects(prefixs);
    }

    /**
     * Load database objects.
     *
     * @param connection the connection
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private void loadDatabaseObjects(DBConnection connection) throws MPPDBIDEException {
        try {
            if (nonLoaded == null || nonLoaded.size() <= 0) {
                return;
            }
            for (ServerObject servObj : nonLoaded) {
                if (servObj instanceof Namespace) {
                    loadNamespace(connection, servObj);
                } else if (servObj instanceof ForeignTable) {
                    loadForeignTable(connection, servObj);
                } else if (servObj instanceof PartitionTable) {
                    loadPartitionTable(connection, servObj);
                } else if (servObj instanceof TableMetaData) {
                    loadTableMetadata(connection, servObj);
                } else if (servObj instanceof ViewMetaData) {
                    loadViewMetadata(connection, servObj);
                }
            }

        } catch (MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.error("ContenetAssistProcessor: Load database for content assist failed.", exception);
            throw exception;
        }
    }

    /**
     * Load partition table.
     *
     * @param connection the connection
     * @param servObj the serv obj
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void loadPartitionTable(DBConnection connection, ServerObject servObj)
            throws DatabaseCriticalException, DatabaseOperationException {
        PartitionTable partitionTable = PartitionTable.class.cast(servObj);
        if (!partitionTable.isLoaded()) {
            partitionTable.refreshTableDetails(connection);
        }
    }

    /**
     * Load view metadata.
     *
     * @param connection the connection
     * @param servObj the serv obj
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void loadViewMetadata(DBConnection connection, ServerObject servObj)
            throws DatabaseOperationException, DatabaseCriticalException {
        ViewMetaData view = ViewMetaData.class.cast(servObj);
        if (!view.isLoaded()) {
            view.getNamespace().refreshView(view, connection, false);
        }
    }

    /**
     * Load foreign table.
     *
     * @param connection the connection
     * @param servObj the serv obj
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void loadForeignTable(DBConnection connection, ServerObject servObj)
            throws DatabaseCriticalException, DatabaseOperationException {
        ForeignTable fTable = ForeignTable.class.cast(servObj);
        if (!fTable.isLoaded()) {
            fTable.refreshTableDetails(connection);
        }
    }

    /**
     * Load table metadata.
     *
     * @param connection the connection
     * @param servObj the serv obj
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void loadTableMetadata(DBConnection connection, ServerObject servObj)
            throws DatabaseCriticalException, DatabaseOperationException {
        TableMetaData tbl = TableMetaData.class.cast(servObj);
        if (!tbl.isLoaded()) {
            tbl.refreshTableDetails(connection);
        }
    }

    /**
     * Load namespace.
     *
     * @param connection the connection
     * @param servObj the serv obj
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void loadNamespace(DBConnection connection, ServerObject servObj)
            throws DatabaseOperationException, DatabaseCriticalException {
        Namespace ns = Namespace.class.cast(servObj);
        if (!ns.isLoaded()) {
            ns.getAllObjectsOnDemand(connection);
            ns.getDatabase().getSearchPathHelper().getSearchPath().add(ns.getName());
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class AutoSuggestComparator.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    public static class AutoSuggestComparator implements Comparator<String>, Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * Compare.
         *
         * @param o1 the o 1
         * @param o2 the o 2
         * @return the int
         */
        @Override
        public int compare(String o1, String o2) {

            if (o1.equalsIgnoreCase(o2)) {
                if (!o1.equals(o2)) {
                    return o1.compareTo(o2);
                }
            }
            return o1.compareToIgnoreCase(o2);

        }

    }

    /**
     * Checks if is insert.
     *
     * @return true, if is insert
     */
    public boolean isInsert() {
        return contentAssistUtil.isInsert();
    }
}
