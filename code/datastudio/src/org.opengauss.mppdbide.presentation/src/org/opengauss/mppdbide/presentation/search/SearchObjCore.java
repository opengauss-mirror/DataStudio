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

package org.opengauss.mppdbide.presentation.search;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.search.SearchDatabase;
import org.opengauss.mppdbide.bl.search.SearchNameMatchEnum;
import org.opengauss.mppdbide.bl.search.SearchNamespace;
import org.opengauss.mppdbide.bl.search.SearchObjectEnum;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.NamespaceUtilsBase;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.SequenceMetadata;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.SynonymMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TriggerMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class SearchObjCore.
 * 
 * @since 3.0.0
 */
public class SearchObjCore extends Observable {
    private SearchCoreJob innerSearch = null;
    private SearchObjInfo searchInfo;
    private DBConnection conn;
    private String executionTime;
    private HashMap<Integer, Server> connectionMap;
    private HashMap<Integer, Database> dBMap;
    private HashMap<Integer, Namespace> nsMap;
    private SearchDatabase searchedDatabase;
    private Database selectedDb;
    private Namespace selectedNs;
    private ArrayList<String> nameMatchList;
    private SearchNamespace searchNamespace;
    private Server server;
    private ArrayList<String> connectionDetails;
    private ArrayList<String> dataBases;
    private ArrayList<String> namespacesList;

    /**
     * Instantiates a new search obj core.
     */
    public SearchObjCore() {
        dBMap = new HashMap<Integer, Database>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        connectionMap = new HashMap<Integer, Server>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        nameMatchList = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        nsMap = new HashMap<Integer, Namespace>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        innerSearch = new SearchCoreJob();
    }

    /**
     * Gets the selected ns.
     *
     * @return the selected ns
     */
    public Namespace getSelectedNs() {
        this.selectedNs = nsMap.get(searchInfo.getSelectedNamespace());
        return this.selectedNs;
    }

    /**
     * Sets the search info.
     *
     * @param searchInfo the new search info
     */
    public void setSearchInfo(SearchObjInfo searchInfo) {
        this.searchInfo = searchInfo;
    }

    /**
     * Gets the selected db.
     *
     * @return the selected db
     */
    public Database getSelectedDb() {
        this.selectedDb = dBMap.get(searchInfo.getSelectedDB());
        return this.selectedDb;
    }

    /**
     * Gets the search namespace.
     *
     * @return the search namespace
     */
    public SearchNamespace getSearchNamespace() {
        return this.searchNamespace;
    }

    /**
     * Gets the connection.
     *
     * @return the connection
     * @throws DatabaseOperationException the database operation exception
     */
    public void getConnection() throws DatabaseOperationException {

        try {
            setConn(selectedDb.getConnectionManager().getFreeConnection());
        } catch (MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.error("SearchObjCore: getting connection failed.", exception);
            throw new DatabaseOperationException(IMessagesConstants.SEARCH_OBJ_ERROR, exception);
        }
    }

    /**
     * Gets the searched database.
     *
     * @return the searched database
     */
    public SearchDatabase getSearchedDatabase() {
        return this.searchedDatabase;
    }

    /**
     * Gets the search info.
     *
     * @return the search info
     */
    public SearchObjInfo getSearchInfo() {
        return this.searchInfo;
    }

    private String getSearchedText() {
        String searchText = "";
        if (searchInfo != null) {
            searchText = searchInfo.getSearchText();
        }
        return searchText;
    }

    private SearchNameMatchEnum getMatchIndex() {
        SearchNameMatchEnum enumMatch = null;
        if (searchInfo != null) {
            enumMatch = searchInfo.getNameMatchIndex();
        }
        return enumMatch;
    }

    /**
     * Gets the name match list.
     *
     * @return the name match list
     */
    public ArrayList<String> getNameMatchList() {
        nameMatchList.clear();
        nameMatchList.add(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_NAME_MATCH_CONTAINS));
        nameMatchList.add(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_NAME_MATCH_STARTS_LBL));
        nameMatchList.add(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_NAME_MATCH_EXACT_LBL));
        nameMatchList.add(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_NAME_MATCH_REGU_EXPRESS_LBL));
        return nameMatchList;
    }

    /**
     * Gets the all profiles.
     *
     * @return the all profiles
     */
    public ArrayList<String> getAllProfiles() {
        connectionMap.clear();
        connectionDetails = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        DBConnProfCache.getInstance().getAllProfilesForSearch(connectionDetails, connectionMap);
        return connectionDetails;

    }

    /**
     * Gets the selected server.
     *
     * @return the selected server
     */
    public Server getSelectedServer() {
        this.server = connectionMap.get(searchInfo.getSelectedserver());
        return this.server;

    }

    /**
     * Gets the all databases.
     *
     * @return the all databases
     */
    public ArrayList<String> getAllDatabases() {
        dataBases = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        dBMap.clear();
        Server servr = getSelectedServer();
        if (null != servr) {
            servr.getAllDatabasesForSearch(dataBases, dBMap);
        }
        return dataBases;

    }

    /**
     * Gets the namespace list.
     *
     * @return the namespace list
     */
    public ArrayList<String> getNamespaceList() {
        namespacesList = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        nsMap.clear();
        selectedDb = getSelectedDb();
        if (null != selectedDb) {

            namespacesList = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
            nsMap.clear();
            NamespaceUtilsBase.getNamespaceListForSearch(namespacesList, nsMap, selectedDb.getAllNameSpaces());
        }
        return namespacesList;
    }

    private void setConn(DBConnection conn) {
        this.conn = conn;
    }

    /**
     * Search.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void search() throws DatabaseCriticalException, DatabaseOperationException {
        innerSearch.search();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SearchCoreJob.
     */
    private class SearchCoreJob {
        /*
         * creation of this class is for fixing UADP God class defect.
         */

        /**
         * Search.
         *
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        public void search() throws DatabaseCriticalException, DatabaseOperationException {
            selectedNs = getSelectedNs();
            searchedDatabase = new SearchDatabase(getSelectedServer(), selectedDb.getOid(), selectedDb);
            searchNamespace = new SearchNamespace(selectedNs.getOid(), selectedNs.getName(), selectedDb);
            searchedDatabase.addSearchNamespaces(searchNamespace);
            try {
                if (searchInfo.isTableSelected() || searchInfo.isViewsSelected() || searchInfo.isSequenceSelected()) {
                    searchTableData(selectedNs.getPrivilegeFlag());
                }
                if (searchInfo.isFunProcSelected()) {
                    executeTogetSearchedFunctionProcedure(selectedNs.getPrivilegeFlag());

                }
                if (searchInfo.isSynonymSelected()) {
                    executeTogetSearchedSynonym(selectedNs.getPrivilegeFlag());
                }
                if (searchInfo.isTriggerSelected()) {
					searchTriggerData(selectedNs.getPrivilegeFlag());
				}
            } catch (DatabaseCriticalException exception) {
                MPPDBIDELoggerUtility.error("SearchObjCore: search operation failed.", exception);
                throw new DatabaseCriticalException(IMessagesConstants.SEARCH_OBJ_ERROR, exception);
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("SearchObjCore: search operation failed.", exception);
                throw new DatabaseOperationException(IMessagesConstants.SEARCH_OBJ_ERROR, exception);
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("SearchObjCore: search operation failed.", exception);
                throw new DatabaseOperationException(IMessagesConstants.SEARCH_OBJ_ERROR, exception);
            }

            searchNamespace.setLoaded();
        }

        private void executeTogetSearchedFunctionProcedure(boolean privilegeFlag)
                throws DatabaseCriticalException, DatabaseOperationException, SQLException {
            ResultSet rs = null;
            DebugObjects debugObj = null;
            try {
                SearchNameMatchEnum matchIndex = getMatchIndex();
                if (null != matchIndex) {
                    rs = conn.execSelectForSearch(
                            searchInfo.formSearchQueryForFunProc(selectedNs.getOid(), matchIndex, privilegeFlag),
                            AbstractSearchObjUtils.formQuerybyNameMatch(matchIndex, getSearchedText()));

                    boolean hasNext = rs.next();
                    while (hasNext) {
                        debugObj = DebugObjects.DebugObjectsUtils.convertToObject(rs, selectedDb);
                        if (searchNamespace.getOid() != debugObj.getNameSpaceId()) {
                            searchNamespace = new SearchNamespace(debugObj.getNameSpaceId(),
                                    debugObj.getDbgNameSpaceName(), selectedDb);
                            searchedDatabase.addSearchNamespaces(searchNamespace);
                        }
                        searchNamespace.addDebugObjectToSearchPool(debugObj);
                        hasNext = rs.next();
                    }
                }
            } finally {
                conn.closeResultSet(rs);
            }
        }

        private void executeTogetSearchedSynonym(boolean privilegeFlag)
                throws DatabaseCriticalException, DatabaseOperationException, SQLException {
            ResultSet rs = null;
            try {
                int namespaceId;
                String nsName;
                Namespace ns;
                SearchNameMatchEnum matchIndex = getMatchIndex();
                if (null != matchIndex) {
                    rs = conn.execSelectForSearch(
                            searchInfo.formSearchQueryForSyn(selectedNs.getOid(), matchIndex, privilegeFlag),
                            AbstractSearchObjUtils.formQuerybyNameMatch(matchIndex, getSearchedText()));

                    boolean hasNext = rs.next();
                    while (hasNext) {
                        namespaceId = rs.getInt("synnamespace");
                        ns = selectedDb.getNameSpaceById(namespaceId);
                        nsName = rs.getString("synobjschema");
                        if (searchNamespace.getOid() != namespaceId) {
                            searchNamespace = new SearchNamespace(namespaceId, nsName, selectedDb);
                            searchedDatabase.addSearchNamespaces(searchNamespace);
                        }
                        getSearchedSynonym(rs, ns);
                        hasNext = rs.next();
                    }
                }
            } finally {
                conn.closeResultSet(rs);
            }
        }

        private void searchTableData(boolean privilegeFlag)
                throws DatabaseCriticalException, DatabaseOperationException, SQLException {
            ResultSet rs = null;
            try {
                int namespaceId;
                String nsName;
                Namespace ns;
                SearchNameMatchEnum matchIndex = getMatchIndex();
                if (null != matchIndex) {

                    rs = conn.execSelectForSearch(
                            searchInfo.formSearchQueryForTablesnViews(selectedNs.getOid(), matchIndex, privilegeFlag,
                                    server.isServerCompatibleToNodeGroupPrivilege()),
                            AbstractSearchObjUtils.formQuerybyNameMatch(getMatchIndex(), getSearchedText()));
                    boolean hasNext = rs.next();
                    while (hasNext) {
                        namespaceId = rs.getInt("relnamespace");
                        ns = selectedDb.getNameSpaceById(namespaceId);
                        nsName = rs.getString("nsname");

                        if (searchNamespace.getOid() != namespaceId) {
                            searchNamespace = new SearchNamespace(namespaceId, nsName, selectedDb);
                            searchedDatabase.addSearchNamespaces(searchNamespace);
                        }
                        getSearchedObject(rs, ns);
                        hasNext = rs.next();
                    }
                }
            } finally {
                conn.closeResultSet(rs);
            }
        }
    }
    
    private void searchTriggerData(boolean privilegeFlag)
            throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        ResultSet rs = null;
        try {
            int namespaceId;
            String nsName;
            Namespace ns;
            SearchNameMatchEnum matchIndex = getMatchIndex();
            if (null != matchIndex) {
                rs = conn.execSelectForSearch(
                        searchInfo.formSearchQueryForTriggers(selectedNs.getOid(), matchIndex, privilegeFlag,
                                server.isServerCompatibleToNodeGroupPrivilege()),
                        AbstractSearchObjUtils.formQuerybyNameMatch(getMatchIndex(), getSearchedText()));
                boolean hasNext = rs.next();
                while (hasNext) {
                    namespaceId = rs.getInt("relnamespace");
                    ns = selectedDb.getNameSpaceById(namespaceId);
                    nsName = rs.getString("nsname");
                   
                    if (searchNamespace.getOid() != namespaceId) {
                        searchNamespace = new SearchNamespace(namespaceId, nsName, selectedDb);
                        searchedDatabase.addSearchNamespaces(searchNamespace);
                    }
                    getSearchedTrigger(rs, ns);
                    hasNext = rs.next();
                }
            }
        } finally {
            conn.closeResultSet(rs);
        }
    }

    private void getSearchedObject(ResultSet rs, Namespace ns) throws SQLException {
        PartitionTable parTable;
        ViewMetaData viewTable;
        TableMetaData forTable;
        String ftOptions;
        TableMetaData tbl;
        SequenceMetadata seq;
        int childId;
        String childName;
        String relkind = null;
        String parttype = null;
        childId = rs.getInt("oid");
        childName = rs.getString("relname");
        relkind = rs.getString("relkind");
        parttype = rs.getString("parttype");
        switch (relkind) {
            case "r": {
                if ("n".equals(parttype)) {
                    tbl = ns.getSearchedTable(childId, childName);
                    searchNamespace.addTableToSearchPool(tbl);
                } else {
                    parTable = ns.getSearchedPartitionTable(childId, childName);
                    searchNamespace.addTableToSearchPool(parTable);
                }
                break;
            }
            case "f": {
                ftOptions = rs.getString("ftoptions");
                forTable = ns.getSearchedForeignTable(childId, ftOptions);
                if (null != forTable) {
                    searchNamespace.addToForeignGroup(forTable);
                }
                break;
            }
            case "S": {
                seq = ns.getSearchedSequence(childId, childName);
                if (null != seq) {
                    searchNamespace.addToSequenceGroup(seq);
                }
                break;
            }
            default: {
                viewTable = ns.getSearchedView(childId, childName);
                searchNamespace.addViewToGroup(viewTable);
                break;
            }
        }
    }

    private void getSearchedSynonym(ResultSet rs, Namespace ns) throws SQLException {
        SynonymMetaData syn;
        int childId;
        String childName = null;
        childId = rs.getInt("oid");
        childName = rs.getString("synname");
        syn = ns.getSearchedSynonym(childId, childName);
        searchNamespace.addToSynonymGroup(syn);
    }
    
    private void getSearchedTrigger(ResultSet rs, Namespace ns) throws SQLException {
    	TriggerMetaData triggerMetaData;
        int childId;
        String childName = null;
        childId = rs.getInt("oid");
        childName = rs.getString("tgname");
        triggerMetaData = ns.getSearchedTrigger(childId, childName);
        searchNamespace.addToTrigerGroup(triggerMetaData);
    }

    /**
     * Gets the rows fetched.
     *
     * @return the rows fetched
     */
    public int getRowsFetched() {
        return searchNamespace.getSize();
    }

    /**
     * Gets the execution time.
     *
     * @return the execution time
     */
    public String getExecutionTime() {
        return executionTime;
    }

    /**
     * Sets the execution time.
     *
     * @param executionTime the new execution time
     */
    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
    }

    private void releaseConnection() {
        if (conn != null) {
            selectedDb.getConnectionManager().releaseAndDisconnection(conn);
        }

    }

    /**
     * Clear data.
     */
    public void clearData() {
        if (null != searchNamespace) {
            searchNamespace.clearAllObjects();
        }
        searchNamespace = null;
        searchedDatabase = null;
    }

    /**
     * Cancel query.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void cancelQuery() throws DatabaseCriticalException, DatabaseOperationException {
        conn.cancelQuery();

    }

    /**
     * Clean up search.
     */
    public void cleanUpSearch() {
        releaseConnection();
    }

    /**
     * Sets the search status.
     *
     * @param searchStatus the new search status
     */
    public void setSearchStatus(SearchObjectEnum searchStatus) {
        setChanged();
        notifyObservers(searchStatus);
    }

    /**
     * Gets the object browser selected server.
     *
     * @param serverName the server name
     * @return the object browser selected server
     */
    public int getObjectBrowserSelectedServer(String serverName) {
        return AbstractSearchObjUtils.getIndexByValue(connectionDetails, serverName);

    }

    /**
     * Gets the object browser selected database.
     *
     * @param dbName the db name
     * @return the object browser selected database
     */
    public int getObjectBrowserSelectedDatabase(String dbName) {
        return AbstractSearchObjUtils.getIndexByValue(dataBases, dbName);
    }

    /**
     * Gets the object browser selected schema.
     *
     * @param schemaName the schema name
     * @return the object browser selected schema
     */
    public int getObjectBrowserSelectedSchema(String schemaName) {
        return AbstractSearchObjUtils.getIndexByValue(namespacesList, schemaName);
    }
}
