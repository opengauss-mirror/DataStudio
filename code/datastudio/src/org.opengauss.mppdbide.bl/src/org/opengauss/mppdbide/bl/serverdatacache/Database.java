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

package org.opengauss.mppdbide.bl.serverdatacache;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.opengauss.mppdbide.adapter.IConnectionDriver;
import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.GaussUtils;
import org.opengauss.mppdbide.adapter.keywordssyntax.SQLSyntax;
import org.opengauss.mppdbide.bl.errorlocator.ErrorLocator;
import org.opengauss.mppdbide.bl.errorlocator.IErrorLocator;
import org.opengauss.mppdbide.bl.executor.AbstractExecutor;
import org.opengauss.mppdbide.bl.executor.Executor;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ObjectList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.SystemNamespaceObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.UserNamespaceObjectGroup;
import org.opengauss.mppdbide.bl.util.BLUtils;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.SystemObjectName;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class Database.
 * 
 */

public class Database extends ServerObject implements GaussOLAPDBMSObject {

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum RETURNVALUES.
     * 
     */
    protected enum RETURNVALUES {

        SUPPORT_CURSOR, SUPPORT_AUTOCOMMIT, SUPPORT_EXPLAINPLAN, SUPPORT_INSERTRETURN, LOWERCASE, UPPERCASE,
        DATABASETYPE
    }

    /**
     * The return val hash map.
     */
    protected HashMap<RETURNVALUES, Object> returnValHashMap;

    /**
     * The is connected.
     */
    protected boolean isConnected;

    private boolean isDebugSupported;

    private boolean isExplainPlanSupported;

    private String dbName;

    /**
     * The server verstion.
     */
    protected String serverVerstion;

    private String defaultDBTblspc;

    private AbstractExecutor executor;

    private UserNamespaceObjectGroup userNamespaces;

    private SystemNamespaceObjectGroup systemNamespaces;

    private Server server;

    private OLAPObjectList<TypeMetaData> defaultDatatypes;

    private OLAPObjectList<TypeMetaData> orcDatatypes;

    private boolean isLoadingUserNamespaceInProgress;

    private boolean isLoadingSystemNamespaceInProgress;

    private String serverIP;


    private SearchPoolManager searchPoolManager = null;

    /**
     * The connection manager.
     */
    protected ConnectionManager connectionManager = null;

    private LoginNotificationManager loginNotifyManager = null;

    /**
     * The search path helper.
     */
    protected SearchPathHelper searchPathHelper = null;

    private FetchObjHelper fetchQueryHelper = null;

    private DataTypeProvider dataTypeProvider = null;

    private HashMap<String, boolean[]> dolphinTypes = null;

    private static final String DOLPHIN_EXTENSION_QUERY = "select count(*) from pg_extension where extname = 'dolphin'";
    private static final String DOLPHIN_TYPE_FUNCTION_QUERY = "select count(*) from pg_proc " +
                                                                "where proname = 'dolphin_types' and " +
                                                                "pronamespace = 11 and pronargs = 0";

    private static final String DOLPHIN_TYPES_QUERY = "select dolphin_types()";

    /**
     * Gets the db name.
     *
     * @return the db name
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * Checks if is show DDL support by server.
     *
     * @return true, if is show DDL support by server
     */
    public boolean isShowDDLSupportByServer() {
        return connectionManager.isShowDDLSupportByServer();
    }

    /**
     * Inits the return val hash map.
     */
    private void initReturnValHashMap() {
        returnValHashMap = new HashMap<RETURNVALUES, Object>(7);
        returnValHashMap.put(RETURNVALUES.SUPPORT_CURSOR, true);
        returnValHashMap.put(RETURNVALUES.SUPPORT_AUTOCOMMIT, true);
        returnValHashMap.put(RETURNVALUES.SUPPORT_EXPLAINPLAN, true);
        returnValHashMap.put(RETURNVALUES.SUPPORT_INSERTRETURN, true);
        returnValHashMap.put(RETURNVALUES.LOWERCASE, true);
        returnValHashMap.put(RETURNVALUES.UPPERCASE, false);
        returnValHashMap.put(RETURNVALUES.DATABASETYPE, DBTYPE.OPENGAUSS);
    }

    /**
     * Instantiates a new database.
     *
     * @param server the server
     * @param oid the oid
     * @param dbName the db name
     */
    public Database(Server server, long oid, String dbName) {
        // Initial database id will be zero, if not known
        super(oid, dbName, OBJECTTYPE.DATABASE, server != null ? server.getPrivilegeFlag() : true);
        initReturnValHashMap();
        this.userNamespaces = new UserNamespaceObjectGroup(OBJECTTYPE.USER_NAMESPACE_GROUP, this);
        this.systemNamespaces = new SystemNamespaceObjectGroup(OBJECTTYPE.SYSTEM_NAMESPACE_GROUP, this);
        this.server = server;
        this.dbName = dbName;
        this.executor = new Executor();
        this.defaultDatatypes = new OLAPObjectList<TypeMetaData>(OBJECTTYPE.DATATYPE_GROUP, this);
        searchPoolManager = new SearchPoolManager();

        connectionManager = new ConnectionManager(this);
        loginNotifyManager = new LoginNotificationManager(this);
        searchPathHelper = new SearchPathHelper(connectionManager);
        fetchQueryHelper = new FetchObjHelper();
        dataTypeProvider = new DataTypeProvider();
    }

    /**
     * Checks if is connected.
     *
     * @return true, if is connected
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Sets the connected.
     *
     * @param isConected the new connected
     */
    public void setConnected(boolean isConected) {
        this.isConnected = isConected;
    }

    /**
     * Gets the user namespace group.
     *
     * @return the user namespace group
     */
    public UserNamespaceObjectGroup getUserNamespaceGroup() {
        return this.getUserNamespaces();
    }

    /**
     * Gets the system namespace group.
     *
     * @return the system namespace group
     */
    public SystemNamespaceObjectGroup getSystemNamespaceGroup() {
        return this.getSystemNamespaces();
    }

    /**
     * Gets the all user name spaces.
     *
     * @return the all user name spaces
     */
    public List<UserNamespace> getAllUserNameSpaces() {
        return getUserNamespaces().getSortedServerObjectList();
    }

    /**
     * Gets the all system name spaces.
     *
     * @return the all system name spaces
     */
    public List<SystemNamespace> getAllSystemNameSpaces() {
        return getSystemNamespaces().getSortedServerObjectList();
    }

    /**
     * Gets the all name spaces.
     *
     * @return the all name spaces
     */
    public ArrayList<Namespace> getAllNameSpaces() {
        /* Need to optimize this function */
        List<UserNamespace> userNamespaceList = getAllUserNameSpaces();
        List<SystemNamespace> systemNamespaceList = getAllSystemNameSpaces();
        ArrayList<Namespace> allNamespaces = new ArrayList<Namespace>();
        allNamespaces.addAll(userNamespaceList);
        allNamespaces.addAll(systemNamespaceList);
        return allNamespaces;
    }

    /**
     * Destroy.
     */
    public void destroy() {
        this.setConnected(false);
        MPPDBIDELoggerUtility.info("ConnectionProfile: clear all cache.");

        if (null != executor) {
            executor.disconnect();
        }

        connectionManager.disconnectConns();
        connectionManager.disconnectTerminalCons();
        executor = null;
        clear();
    }

    /**
     * Gets the executor.
     *
     * @return the executor
     */
    public AbstractExecutor getExecutor() {
        return this.executor;
    }

    /**
     * Gets the server version.
     *
     * @return the server version
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String getServerVersion() throws DatabaseOperationException, DatabaseCriticalException {
        if (serverVerstion == null) {
            serverVerstion = executor.getServerVersion();
        }
        return serverVerstion;
    }

    /**
     * Fetch server IP.
     *
     * @return the string
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String fetchServerIP() throws DatabaseOperationException, DatabaseCriticalException {
        if (serverIP == null) {
            serverIP = executor.fetchServerIP();
        }
        return serverIP;
    }

    /**
     * Gets the dabase type.
     *
     * @return the dabase type
     */
    protected String getDabaseType() {
        return getDBType().toString();
    }

    /**
     * Sets the DB default tbl spc.
     *
     * @param defaultDBTablespc the new DB default tbl spc
     */
    public void setDBDefaultTblSpc(String defaultDBTablespc) {
        this.defaultDBTblspc = defaultDBTablespc;
    }

    /**
     * Gets the DB default tbl spc.
     *
     * @return the DB default tbl spc
     */
    public String getDBDefaultTblSpc() {
        return this.defaultDBTblspc;
    }

    /**
     * Gets the server.
     *
     * @return the server
     */
    public Server getServer() {
        return this.server;
    }

    /**
     * Gets the name space by id.
     *
     * @param namespaceId the namespace id
     * @return the name space by id
     * @throws DatabaseOperationException the database operation exception
     */
    public Namespace getNameSpaceById(long namespaceId) throws DatabaseOperationException {
        Namespace namespace = this.getUserNamespaces().getObjectById(namespaceId);
        if (namespace == null) {
            namespace = this.getSystemNamespaces().getObjectById(namespaceId);
        }
        return returnNamespaceAfterExceptionCheck(namespace);
    }

    /**
     * Gets the name space by name.
     *
     * @param namespaceName the namespace name
     * @return the name space by name
     * @throws DatabaseOperationException the database operation exception
     */
    public INamespace getNameSpaceByName(String namespaceName) throws DatabaseOperationException {
        Namespace namespace = this.getUserNamespaces().get(namespaceName);
        if (namespace == null) {
            namespace = this.getSystemNamespaces().get(namespaceName);
        }
        return returnNamespaceAfterExceptionCheck(namespace);
    }

    /**
     * Gets the schem inclusion list.
     *
     * @return the schem inclusion list
     */
    public Set<String> getSchemInclusionList() {
        return this.server.getServerConnectionInfo().getSchemaInclusionList();
    }

    /**
     * Sets the client SSL private key.
     *
     * @param keyFileName the new client SSL private key
     */
    public void setClientSSLPrivateKey(String keyFileName) {
        this.server.getServerConnectionInfo().setClientSSLPrivateKey(keyFileName);
    }

    /**
     * Gets the schem exclusion list.
     *
     * @return the schem exclusion list
     */
    public Set<String> getSchemExclusionList() {
        return this.server.getServerConnectionInfo().getSchemaExclusionList();
    }

    /**
     * Return namespace after exception check.
     *
     * @param namespace the namespace
     * @return the namespace
     * @throws DatabaseOperationException the database operation exception
     */
    private Namespace returnNamespaceAfterExceptionCheck(Namespace namespace) throws DatabaseOperationException {
        if (null == namespace) {
            DatabaseOperationException exception = new DatabaseOperationException(
                    IMessagesConstants.ERR_BL_NO_NAMESPACE_AVAILABLE);
            exception.setServerMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_NO_NAMESPACE_AVAILABLE));
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_NO_NAMESPACE_AVAILABLE));
            throw exception;
        }
        return namespace;
    }

    /**
     * Clear all the debug objects from all the namespaces.
     */
    protected void clearAllDebugObjects() {
        ArrayList<Namespace> namespaceSortedList = this.getAllNameSpaces();

        Iterator<Namespace> itrNamespace = namespaceSortedList.iterator();
        boolean hasMoreElements = itrNamespace.hasNext();
        Namespace namespace = null;
        while (hasMoreElements) {
            namespace = itrNamespace.next();
            namespace.clearAllObjects();

            namespace.clear();
            namespace = null;

            hasMoreElements = itrNamespace.hasNext();
        }
    }

    /**
     * Clear.
     */
    public void clear() {
        clearAllDebugObjects();
        this.getUserNamespaces().clear();
        this.getSystemNamespaces().clear();
        getSearchPoolManager().clearTrie();
        defaultDatatypes.clear();
        if (orcDatatypes != null) {
            orcDatatypes.clear();
            // When disconnected to make list null.
            orcDatatypes = null;
        }
    }

    /**
     * Gets the debug object by id.
     *
     * @param debugObjectId the debug object id
     * @param namepsaceId the namepsace id
     * @return the debug object by id
     * @throws DatabaseOperationException the database operation exception
     */
    public IDebugObject getDebugObjectById(long debugObjectId, long namepsaceId) throws DatabaseOperationException {
        MPPDBIDELoggerUtility.debug("ConnectionProfile: get debug object by id.");
        DebugObjects object = (DebugObjects) getNameSpaceById(namepsaceId).getDebugObjectById(debugObjectId);

        return object;
    }

    /**
     * Fetch search path objects.
     *
     * @param needToGetSearchPath the need to get search path
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws OutOfMemoryError the out of memory error
     */
    public void fetchSearchPathObjects(boolean needToGetSearchPath)
            throws DatabaseOperationException, DatabaseCriticalException, OutOfMemoryError {
        MPPDBIDELoggerUtility.info("ConnectionProfile: refresh all objects.");
        NamespaceUtilsBase.fetchAllNamespaces(this);

        if (needToGetSearchPath) {
            searchPathHelper.fetchUserSearchPath(server.getUserNameFromServerConnInfo());
        }
    }

    /**
     * Fetch default datatypes.
     *
     * @param isReloadDefaultDatatype the is reload default datatype
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void fetchDefaultDatatypes(boolean isReloadDefaultDatatype)
            throws DatabaseCriticalException, DatabaseOperationException {
        fetchAllDatatypes();
        if (isReloadDefaultDatatype) {
            // Creating alias of few pg_catalog datatypes
            dataTypeProvider.loadDefaultDatatype();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DataTypeProvider.
     * 
     */
    private class DataTypeProvider {

        /**
         * Load default datatype.
         *
         * @throws DatabaseOperationException the database operation exception
         */
        public void loadDefaultDatatype() throws DatabaseOperationException {
            defaultDatatypes.clear();
            ServerObject servObj = getSystemNamespaces().get(SystemObjectName.PG_CATALOG);

            if (!(servObj instanceof Namespace)) {
                return;
            }

            String[][] datatypesToBeDisplayed = DatabaseUtils.getdefaultDatatypeList();
            for (int index = 0; index < datatypesToBeDisplayed.length; index++) {
                cloneToDifferentDatatype((Namespace) servObj, datatypesToBeDisplayed[index][0],
                        datatypesToBeDisplayed[index][1], defaultDatatypes);
            }
            String[] serialDataTypeList = DatabaseUtils.getSerialDatatypeList();
            for (int index = 0; index < serialDataTypeList.length; index++) {
                addSerialDatatype((Namespace) servObj, serialDataTypeList[index], defaultDatatypes);
            }
            /* Frequently used dataType to be maintained here */
        }

        /**
         * Load ORC datatype.
         *
         * @throws DatabaseOperationException the database operation exception
         */
        public void loadORCDatatype() throws DatabaseOperationException {
            if (orcDatatypes != null) {
                return;
            }

            orcDatatypes = new OLAPObjectList<TypeMetaData>(OBJECTTYPE.DATATYPE_GROUP, this);

            ServerObject servObj = getSystemNamespaces().get(SystemObjectName.PG_CATALOG);

            if (!(servObj instanceof Namespace)) {
                return;
            }

            String[][] orcDatatypesToBeDisplayed = DatabaseUtils.getORCDataTypesToBeDisplayed();
            for (int index = 0; index < orcDatatypesToBeDisplayed.length; index++) {

                cloneToDifferentDatatype((Namespace) servObj, orcDatatypesToBeDisplayed[index][0],
                        orcDatatypesToBeDisplayed[index][1], orcDatatypes);
            }
        }
    }

    /**
     * Gets the default datatype.
     *
     * @return the default datatype
     */
    public ObjectList<TypeMetaData> getDefaultDatatype() {
        return this.defaultDatatypes;
    }

    /**
     * Gets the ORC datatype.
     *
     * @return the ORC datatype
     */
    public ObjectList<TypeMetaData> getORCDatatype() {
        try {

            dataTypeProvider.loadORCDatatype();
        } catch (DatabaseOperationException databaseOperationException) {
            MPPDBIDELoggerUtility.error("Database operation exception", databaseOperationException);
        }
        return this.orcDatatypes;
    }

    /**
     * Clone to different datatype.
     *
     * @param ns the ns
     * @param name the name
     * @param newName the new name
     * @param datatypes the datatypes
     * @throws DatabaseOperationException the database operation exception
     */
    protected void cloneToDifferentDatatype(Namespace ns, String name, String newName,
            ObjectList<TypeMetaData> datatypes) throws DatabaseOperationException {
        TypeMetaData type = ns.getTypeByName(name);
        if (null == type) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DEFAULT_DATATYPE_NOT_FOUND, name));
            throw new DatabaseOperationException(IMessagesConstants.ERR_DEFAULT_DATATYPE_NOT_FOUND, name);
        }
        TypeMetaData newType = type.getCloneObjWithName(newName);

        datatypes.addItem(newType);
    }

    /**
     * Add serial data type.
     *
     * @param ns the ns
     * @param name the name
     * @param datatypes the datatypes
     */
    protected void addSerialDatatype(Namespace ns, String name, ObjectList<TypeMetaData> datatypes) {
        int oid = DatabaseUtils.SERIAL_DATA_TYPE_OID;
        TypeMetaData newType = new TypeMetaData(oid, name, ns);
        datatypes.addItem(newType);
    }

    /**
     * Gets the profile id.
     *
     * @return the profile id
     */
    public ConnectionProfileId getProfileId() {
        return new ConnectionProfileId(this.server.getId(), getOid());
    }

    /**
     * Fetch all tablespace.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void fetchAllTablespace() throws DatabaseCriticalException, DatabaseOperationException {
        Tablespace.fetchAllTablespace(this);
    }

    /**
     * Fetch tablespace meta data.
     *
     * @param tableOid the table oid
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void fetchTablespaceMetaData(long tableOid) throws DatabaseCriticalException, DatabaseOperationException {
        String qry = Tablespace.getTablespaceQuery(tableOid, privilegeFlag);

        ResultSet resultset = connectionManager.execSelectAndReturnRsOnObjBrowserConn(qry);
        boolean isAdmin = connectionManager.execSelectCheckIfAdmin();
        try {
            boolean hasNext = resultset.next();
            DatabaseUtils.checkExceptionForNoRSorNoAccess(hasNext, privilegeFlag);
            while (hasNext) {
                Tablespace tablespace = Tablespace.convertToTablesapce(resultset, this.server, isAdmin);
                this.server.addToTablespaceGroup(tablespace);
                hasNext = resultset.next();
            }
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        } finally {
            connectionManager.closeRSOnObjBrowserConn(resultset);
        }
    }

    /**
     * Rename database.
     *
     * @param newName the new name
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void renameDatabase(String newName) throws DatabaseOperationException, DatabaseCriticalException {
        fetchQueryHelper.renameDatabase(newName);
    }

    /**
     * Drop database.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void dropDatabase() throws DatabaseOperationException, DatabaseCriticalException {
        fetchQueryHelper.dropDatabase();
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return MPPDBIDEConstants.PRIME_31 + getName().hashCode() + getServer().hashCode();
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Database)) {
            return false;
        }

        Database other = (Database) obj;
        return getOid() == other.getOid() && getServer().getId() == other.getServer().getId();
    }

    /**
     * Gets the server name.
     *
     * @return the server name
     */
    public String getServerName() {
        return server.getName();
    }

    /**
     * Belongs to.
     *
     * @param database the database
     * @param server2 the server 2
     * @return true, if successful
     */
    public boolean belongsTo(Database database, Server server2) {
        if (this.getName().equals(database.getName())) {
            return this.getServer().belongsTo(null, server2);
        }

        return false;
    }

    /**
     * Checks if is same name.
     *
     * @param otherDb the other db
     * @return true, if is same name
     */
    public boolean isSameName(Database otherDb) {
        return getDbName().equals(otherDb.getDbName());
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return this;
    }

    /**
     * Checks if is loading namespace in progress.
     *
     * @return true, if is loading namespace in progress
     */
    public boolean isLoadingNamespaceInProgress() {
        return isLoadingUserNamespaceInProgress || isLoadingSystemNamespaceInProgress;
    }

    /**
     * Checks if is loading user namespace in progress.
     *
     * @return true, if is loading user namespace in progress
     */
    public boolean isLoadingUserNamespaceInProgress() {
        return isLoadingUserNamespaceInProgress;
    }

    /**
     * Checks if is loading system namespace in progress.
     *
     * @return true, if is loading system namespace in progress
     */
    public boolean isLoadingSystemNamespaceInProgress() {
        return isLoadingSystemNamespaceInProgress;
    }

    /**
     * Sets the loading namespace in progress.
     *
     * @param isLoadingNamespaceInProgres the new loading namespace in progress
     */
    public void setLoadingNamespaceInProgress(boolean isLoadingNamespaceInProgres) {
        this.isLoadingUserNamespaceInProgress = isLoadingNamespaceInProgres;
        this.isLoadingSystemNamespaceInProgress = isLoadingNamespaceInProgres;
    }

    /**
     * Sets the loading user namespace in progress.
     *
     * @param isLoadingUserNamespaceInProgres the new loading user namespace in
     * progress
     */
    public void setLoadingUserNamespaceInProgress(boolean isLoadingUserNamespaceInProgres) {
        this.isLoadingUserNamespaceInProgress = isLoadingUserNamespaceInProgres;
    }

    /**
     * Sets the loading system namespace in progress.
     *
     * @param isLoadingSystemNamespaceInProgres the new loading system namespace
     * in progress
     */
    public void setLoadingSystemNamespaceInProgress(boolean isLoadingSystemNamespaceInProgres) {
        this.isLoadingSystemNamespaceInProgress = isLoadingSystemNamespaceInProgres;
    }

    /**
     * Gets the display label.
     *
     * @return the display label
     */
    @Override
    public String getDisplayLabel() {
        return this.getName();
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    @Override
    public Object[] getChildren() {
        Collection<Object> schemas = new ArrayList<Object>(2);
        schemas.add(this.getSystemNamespaceGroup());
        schemas.add(this.getUserNamespaceGroup());
        return schemas.toArray();
    }

    /**
     * Load DB specific contents 1.
     *
     * @param status the status
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void loadDBSpecificContents1(IJobCancelStatus status)
            throws DatabaseOperationException, DatabaseCriticalException {
        checkCancelStatusAndAbort(status);
        DatabaseHelper.fetchDBOid(this);
        checkCancelStatusAndAbort(status);

        fetchSearchPathObjects(true);
        try {
            DatabaseHelper.fetchTablespaceName(this);
        } catch (DatabaseOperationException databaseOperationException) {
            MPPDBIDELoggerUtility.none("Skipping the tablespace error");
        }
        checkCancelStatusAndAbort(status);
        fetchDefaultDatatypes(true);
        checkCancelStatusAndAbort(status);
    }

    /**
     * Load DB specific contents 2.
     *
     * @param status the status
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void loadDBSpecificContents2(IJobCancelStatus status)
            throws DatabaseOperationException, DatabaseCriticalException {
        fetchAllTablespace();
        checkCancelStatusAndAbort(status);
        AccessMethod.fetchAllAccessMethods(this);
        checkCancelStatusAndAbort(status);
        fetchSearchPathObjects(true);
        try {
            DatabaseHelper.fetchTablespaceName(this);
        } catch (DatabaseOperationException databaseOperationException) {
            MPPDBIDELoggerUtility.none("Skipping the tablespace error");
        }
        checkCancelStatusAndAbort(status);
        fetchDefaultDatatypes(true);
        checkCancelStatusAndAbort(status);
    }

    /**
     * Check cancel status and abort.
     *
     * @param cancelStatus the cancel status
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void checkCancelStatusAndAbort(IJobCancelStatus cancelStatus)
            throws DatabaseOperationException, DatabaseCriticalException {
        if (null != cancelStatus && cancelStatus.getCancel()) {
            connectionManager.cancelAllConnectionQueries();
            DBConnProfCache.getInstance().destroyConnection(this);
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));
            throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL_MSG);
        }
    }

    /**
     * Removes the.
     *
     * @param obj the obj
     */
    public void remove(ServerObject obj) {
        if (obj instanceof UserNamespace) {
            getUserNamespaces().remove((UserNamespace) obj);
        } else if (obj instanceof SystemNamespace) {
            getSystemNamespaces().remove((SystemNamespace) obj);
        }
    }

    /**
     * Gets the user namespaces.
     *
     * @return the user namespaces
     */
    public UserNamespaceObjectGroup getUserNamespaces() {
        return userNamespaces;
    }

    /**
     * Gets the system namespaces.
     *
     * @return the system namespaces
     */
    public SystemNamespaceObjectGroup getSystemNamespaces() {
        return systemNamespaces;
    }

    /**
     * Gets the DB type.
     *
     * @return the DB type
     */
    public DBTYPE getDBType() {
        return (DBTYPE) returnValHashMap.get(RETURNVALUES.DATABASETYPE);
    }

    /**
     * Checks if is cursor supported.
     *
     * @return true, if is cursor supported
     */
    public boolean isCursorSupported() {
        return (boolean) returnValHashMap.get(RETURNVALUES.SUPPORT_CURSOR);
    }

    /**
     * Checks if is word break special char.
     *
     * @param ch the ch
     * @return true, if is word break special char
     */
    public boolean isWordBreakSpecialChar(char ch) {
        List<Character> charList = DatabaseUtils.getCharacterList(this);

        if (!charList.isEmpty() && charList.contains(ch)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is supported explain plan.
     *
     * @return true, if is supported explain plan
     */
    public boolean isSupportedExplainPlan() {
        return (boolean) returnValHashMap.get(RETURNVALUES.SUPPORT_EXPLAINPLAN);
    }

    /**
     * Checks if is support insert returing.
     *
     * @return true, if is support insert returing
     */
    public boolean isSupportInsertReturing() {
        return (boolean) returnValHashMap.get(RETURNVALUES.SUPPORT_INSERTRETURN);
    }

    /**
     * Checks if is lower case.
     *
     * @return true, if is lower case
     */
    public boolean isLowerCase() {
        return (boolean) returnValHashMap.get(RETURNVALUES.LOWERCASE);
    }

    /**
     * Checks if is upper case.
     *
     * @return true, if is upper case
     */
    public boolean isUpperCase() {
        return (boolean) returnValHashMap.get(RETURNVALUES.UPPERCASE);
    }

    /**
     * Gets the search pool manager.
     *
     * @return the search pool manager
     */
    public SearchPoolManager getSearchPoolManager() {
        if (null == searchPoolManager) {
            searchPoolManager = new SearchPoolManager();
        }
        return searchPoolManager;
    }

    /**
     * Gets the connection manager.
     *
     * @return the connection manager
     */
    public ConnectionManager getConnectionManager() {
        if (null == connectionManager) {
            connectionManager = new ConnectionManager(this);
        }
        return connectionManager;
    }

    /**
     * Gets the login notify manager.
     *
     * @return the login notify manager
     */
    public LoginNotificationManager getLoginNotifyManager() {
        if (null == loginNotifyManager) {
            loginNotifyManager = new LoginNotificationManager(this);
        }
        return loginNotifyManager;
    }

    /**
     * Gets the search path helper.
     *
     * @return the search path helper
     */
    public SearchPathHelper getSearchPathHelper() {
        if (null == searchPathHelper) {
            searchPathHelper = new SearchPathHelper(getConnectionManager());
        }
        return searchPathHelper;
    }

    /**
     * Fetch all datatypes.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void fetchAllDatatypes() throws DatabaseCriticalException, DatabaseOperationException {
        fetchQueryHelper.fetchAllDatatypes();
    }

    /**
     * Connect to server.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void connectToServer() throws MPPDBIDEException {
        fetchQueryHelper.connectToServer();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class FetchObjHelper.
     * 
     */
    private class FetchObjHelper {

        /**
         * Fetch all datatypes.
         *
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        public void fetchAllDatatypes() throws DatabaseCriticalException, DatabaseOperationException {
            String qry = "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, "
                    + "typ.typlen as typlen, pg_catalog.format_type(oid,typ.typtypmod) as displaycolumns , "
                    + " typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, "
                    + "typ.typtypmod as typtypmod, "
                    + "typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc "
                    + "from pg_type typ left join pg_description des on (typ.oid = des.objoid) "
                    + "where typ.typnamespace in (select oid from pg_namespace where nspname in "
                    + "('information_schema', 'pg_catalog')) " + "order by typ.typname";
            ResultSet rs = null;
            boolean hasMoreResult = false;

            try {
                rs = connectionManager.execSelectAndReturnRsOnObjBrowserConn(qry);
                hasMoreResult = rs.next();

                while (hasMoreResult) {
                    TypeMetaData.convertToTypeMetaData(rs, Database.this, true);
                    hasMoreResult = rs.next();
                }
            } catch (SQLException exceptionSql) {
                try {
                    GaussUtils.handleCriticalException(exceptionSql);
                } catch (DatabaseCriticalException dc) {
                    throw dc;
                }
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID), exceptionSql);
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exceptionSql);
            } finally {
                connectionManager.closeRSOnObjBrowserConn(rs);
            }
        }

        /**
         * Connect to server.
         *
         * @throws MPPDBIDEException the MPPDBIDE exception
         */
        public void connectToServer() throws MPPDBIDEException {
            IServerConnectionInfo connInfo = server.getServerConnectionInfo(getDbName());
            Properties props = connInfo.composeProperty(server.getDriverName());

            String url = connInfo.composeUrl();

            try {
                connectionManager.connectToGauss(props, url, getDBType());

                if (null == executor) {
                    executor = new Executor();
                }

                if (connInfo instanceof ServerConnectionInfo) {
                    executor.connectToServer((ServerConnectionInfo) connInfo, connectionManager.getConnectionDriver());
                }

            } catch (MPPDBIDEException exp) {
                destroy();
                throw exp;
            } finally {
                ServerUtil.clearPropertyDetails(props);
                ServerUtil.clearConnectionInfo(connInfo);
                server.clearPrds();

            }
            setConnected(true);
            checkForDebugSupport();
            checkExplainPlanSupport();
        }

        private void checkExplainPlanSupport() {
            String debugSupportCheckQuery = "EXPLAIN SELECT 1";
            ResultSet rs = null;
            try {
                rs = connectionManager.getObjBrowserConn().execSelectAndReturnRs(debugSupportCheckQuery);
                if (rs.next()) {
                    isExplainPlanSupported = true;
                } else {
                    isExplainPlanSupported = false;
                }
            } catch (DatabaseCriticalException | DatabaseOperationException | SQLException exception) {
                MPPDBIDELoggerUtility.error("Failed to run explain plan query", exception);
                isExplainPlanSupported = false;
            } finally {
                connectionManager.getObjBrowserConn().closeResultSet(rs);
            }
        }

        private void checkForDebugSupport() {
            String debugSupportCheckQuery = "SELECT count(1) from pg_proc where proname='pldbg_attach_session';";
            ResultSet rs = null;
            try {
                rs = connectionManager.getObjBrowserConn().execSelectAndReturnRs(debugSupportCheckQuery);
                if (rs.next()) {
                    int procedureCount = rs.getInt(1);
                    if (procedureCount > 0) {
                        setDebugSupported(true);
                    } else {
                        setDebugSupported(false);
                    }
                }
            } catch (DatabaseCriticalException | DatabaseOperationException | SQLException exception) {
                MPPDBIDELoggerUtility.error("Failed to check debug support for the database", exception);
                setDebugSupported(false);
            } finally {
                connectionManager.getObjBrowserConn().closeResultSet(rs);
            }
        }

        /**
         * Rename database.
         *
         * @param newName the new name
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         */
        public void renameDatabase(String newName) throws DatabaseOperationException, DatabaseCriticalException {
            // Need to check more in order to set states here as it will be
            // using other connection for renaming
            String qry = String.format(Locale.ENGLISH, "ALTER DATABASE \"%s\" RENAME TO %s ;", getName(),
                    ServerObject.getQualifiedObjectName(newName));
            DBConnection anotherConn = null;
            long oid = getOid();
            try {
                anotherConn = server.getAnotherConnection(oid);
            } catch (DatabaseOperationException databaseOperationException) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_RENAME_NEED_ANOTHER_CON));
                throw new DatabaseOperationException(IMessagesConstants.ERR_RENAME_NEED_ANOTHER_CON);
            }

            if (isConnected) {
                destroy();
            }

            anotherConn.execNonSelect(qry);

            qry = "SELECT datname FROM PG_DATABASE WHERE OID=" + oid;
            dbName = anotherConn.execSelectAndGetFirstVal(qry);
            server.removeFromDatabaseGroup(oid);
            setName(dbName);
            server.addToDatabaseGroup(Database.this);
        }

        /**
         * Drop database.
         *
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         */
        public void dropDatabase() throws DatabaseOperationException, DatabaseCriticalException {
            // Need to check more in order to set states here as it will be
            // using other connection for droping
            String qry = "drop database " + ServerObject.getQualifiedObjectName(getName()) + ';';
            DBConnection anotherConn = null;
            try {
                anotherConn = server.getAnotherConnection(getOid());
            } catch (DatabaseOperationException databaseOperationException) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_NO_CONNECTION_AVAILABLE),
                        databaseOperationException);
                throw new DatabaseOperationException(IMessagesConstants.ERR_NO_CONNECTION_AVAILABLE);
            }

            destroy();
            anotherConn.execNonSelect(qry);
            server.removeDatabase(getOid());
        }
    }

    /**
     * Gets the sql syntax.
     *
     * @return the sql syntax
     */
    public SQLSyntax getSqlSyntax() {
        try {
            IConnectionDriver connectionDriver = connectionManager.getConnectionDriver();
            return connectionDriver != null ? connectionDriver.loadSQLSyntax() : null;
        } catch (DatabaseOperationException databaseOperationException) {
            MPPDBIDELoggerUtility.error("error in getting driver", databaseOperationException);
            return null;
        }
    }

    /**
     * Gets the error locator.
     *
     * @return the error locator
     */
    public IErrorLocator getErrorLocator() {
        return new ErrorLocator();
    }

    /**
     * Checks for support for atomic DDL.
     *
     * @return true, if successful
     */
    public boolean hasSupportForAtomicDDL() {
        return true;
    }

    /**
     * Gets the valid object name.
     *
     * @param objectName the object name
     * @return the valid object name
     */
    public String getValidObjectName(String objectName) {
        return getQualifiedObjectName(objectName);
    }

    /**
     * Gets the load limit.
     *
     * @return the load limit
     */
    public int getLoadLimit() {
        return this.server.getServerConnectionInfo().getLoadLimit();
    }

    /**
     * canChildObjectsLoaded loaded
     * 
     * @return boolean value
     */
    public boolean canChildObjectsLoaded() {
        return this.server.getServerConnectionInfo().canLoadChildObjects();
    }

    /**
     * Gets the default schema name.
     *
     * @param dbConnection the db connection
     * @return the default schema name
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String getDefaultSchemaName(DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        ArrayList<String> nameSpacearray = getNameSpaceFromSearchPath(dbConnection);
        return ServerObject.getQualifiedObjectName(nameSpacearray.get(0));
    }

    /**
     * Gets the name space from search path.
     *
     * @param connection the connection
     * @return the name space from search path
     */
    private ArrayList<String> getNameSpaceFromSearchPath(DBConnection connection) {
        ArrayList<String> nameSpacearray = new ArrayList<String>();
        String userName = this.getServer().getUserName();
        ResultSet resultSet = null;
        try {
            String searchQuery = MPPDBIDEConstants.SHOW_SEARCHPATH_QUERY;
            resultSet = connection.execSelectAndReturnRs(searchQuery);
            handleResultSet(nameSpacearray, userName, resultSet);
        } catch (DatabaseOperationException | SQLException exception) {
            MPPDBIDELoggerUtility.error("Exception occured while getting the namespace from search path", exception);
        } catch (DatabaseCriticalException databaseCriticalException) {
            MPPDBIDELoggerUtility.error("Exception occured while getting the namespace from search path",
                    databaseCriticalException);
        } finally {
            connection.closeResultSet(resultSet);
        }
        return nameSpacearray;
    }

    private void handleResultSet(ArrayList<String> nameSpacearray, String userName, ResultSet resultSet)
            throws SQLException {
        boolean hasMoreRs = false;
        hasMoreRs = resultSet.next();
        if (hasMoreRs) {
            String searchPathStr = resultSet.getString(1);
            handleSearchString(nameSpacearray, userName, searchPathStr);
        }
    }

    private void handleSearchString(ArrayList<String> nameSpacearray, String userName, String searchPathStr) {
        if (searchPathStr != null) {
            String[] searchPathList = searchPathStr.replace("$user", userName).split(",");
            for (String str : searchPathList) {
                String strTrim = str.trim();
                nameSpacearray.add(BLUtils.getUnQuotedIdentifier(strTrim, "\""));
            }
        } else {
            nameSpacearray.add(MPPDBIDEConstants.PUBLIC_SCHEMA_NAME);
        }
    }

    /**
     * Find matching datatype.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    public SortedMap<String, ServerObject> findMatchingDatatype(String prefix) {
        SortedMap<String, ServerObject> datatypes = new TreeMap<String, ServerObject>();
        datatypes.putAll(defaultDatatypes.findMatching(prefix));
        if (orcDatatypes != null) {
            datatypes.putAll(orcDatatypes.findMatching(prefix));
        }
        return datatypes;
    }

    /**
     * Checks if is supported edit do not save option.
     *
     * @param isAtomic the is atomic
     * @return true, if is supported edit do not save option
     */
    public boolean isSupportedEditDoNotSaveOption(boolean isAtomic) {
        return isAtomic;
    }

    public boolean isDebugSupported() {
        return isDebugSupported;
    }

    public void setDebugSupported(boolean isDebugSupported) {
        this.isDebugSupported = isDebugSupported;
    }

    public boolean isExplainPlanSupported() {
        return isExplainPlanSupported;
    }

    public void initDolphinTypesIfNeeded() throws DatabaseCriticalException, DatabaseOperationException {
        if (hasDolphin()) {
            initDolphinTypes();
        }
    }

    /**
     * Checks has dolphin plugin and dolphin type function
     *
     * @return true, if has dolphin plugin and dolphin type function
     */
    private boolean hasDolphin()
            throws DatabaseCriticalException, DatabaseOperationException {
        String dolphinExtensionResult =
                this.getConnectionManager().execSelectAndGetFirstValOnObjBrowserConn(DOLPHIN_EXTENSION_QUERY);
        String dolphinFunctionResult =
                this.getConnectionManager().execSelectAndGetFirstValOnObjBrowserConn(DOLPHIN_TYPE_FUNCTION_QUERY);
        boolean hasDolphinExtention = Integer.parseInt(dolphinExtensionResult) == 1;
        boolean hasDolphinTypesFunction = Integer.parseInt(dolphinFunctionResult) == 1;

        return hasDolphinExtention && hasDolphinTypesFunction;
    }


    public void initDolphinTypes() throws DatabaseCriticalException, DatabaseOperationException
    {
        ResultSet rs = null;
        try {
            rs = this.getConnectionManager().execSelectAndReturnRsOnObjBrowserConn(DOLPHIN_TYPES_QUERY);
            while (rs.next()) {
                 Array array = rs.getArray(1);
                 if (array != null) {
                     String[][] strs = (String[][]) array.getArray();
                     dolphinTypes = new HashMap<String, boolean[]>(strs.length);
                     for (int i = 0; i < strs.length; i++) {
                         dolphinTypes.put(strs[i][0], new boolean[] {Boolean.parseBoolean(strs[i][1]),
                                 Boolean.parseBoolean(strs[i][2])});
                     }
                 }
            }
        } catch (SQLException exp) {
            try {
                GaussUtils.handleCriticalException(exp);
            } catch (DatabaseCriticalException dc) {
                throw dc;
            }
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_FETCH_DATABASE_OPERATION), exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_FETCH_DATABASE_OPERATION, exp);
        } finally {
            this.getConnectionManager().closeRSOnObjBrowserConn(rs);
        }

        return;
    }

    /**
     * Get the dolphinTypes
     *
     * @return dolphinTypes
     */
    public HashMap<String, boolean[]> getDolphinTypes() {
        return this.dolphinTypes;
    }

    /**
     * Get the oid of type
     *
     * @return integer, the dolphin type oid
     */
    public int getDolphinTypeOid(String typname) {
        int oid = 0;
        try {
            String getTypeOidQuery = "select oid from pg_type where typname=\'" + typname + "\' and typnamespace = 11";
            String result  = this.getConnectionManager().execSelectAndGetFirstValOnObjBrowserConn(getTypeOidQuery);
            oid = Integer.parseInt(result);
        } catch (DatabaseOperationException | DatabaseCriticalException exp) {
            MPPDBIDELoggerUtility.error("getDolphinTypeOid: Failed to get the type oid.");
        }
        return oid;
    }
}
