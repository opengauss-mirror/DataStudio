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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.groups.DatabaseObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.TablespaceObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.UserRoleObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.utils.CustomStringUtility;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.MemoryCleaner;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.exceptions.PasswordExpiryException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;

/**
 * 
 * Title: class
 * 
 * Description: The Class Server.
 * 
 */

public class Server implements IConnectionProfile, GaussOLAPDBMSObject {

    // should be static (used to get multiple connections)
    private static int sequence;

    /**
     * The server id.
     */
    protected int serverId;

    /**
     * The connection name.
     */
    protected String connectionName;

    /**
     * The server host.
     */
    protected String serverHost;

    /**
     * The server port.
     */
    protected int serverPort;
    private DatabaseObjectGroup dbGroup;
    private TablespaceObjectGroup tablespaceGroup;
    private OLAPObjectGroup<AccessMethod> accessMethods;
    private IServerConnectionInfo newConfigInfo;

    /**
     * The encrpyted profile prd.
     */
    protected String encrpytedProfilePrd;

    /**
     * The encrpytedssl prd.
     */
    protected String encrpytedsslPrd;
    private SavePrdOptions savePrdOption;

    /**
     * The is support node group privilege.
     */
    protected boolean isSupportNodeGroupPrivilege;
    private boolean isServerInProgress;
    private String driverName;

    /**
     * The server version.
     */
    protected String serverVersion;

    /**
     * The trimmed version.
     */
    protected String trimmedVersion;

    /**
     * The server IP.
     */
    protected String serverIP;

    private boolean privilegeFlag;
    private UserRoleObjectGroup userRoleObjectGroup;

    private static final Object LOCK = new Object();

    private HashMap<Long, ArrayList<DefaultParameter>> defaulParametertMap = new HashMap<>();

    /**
     * Checks if is server in progress.
     *
     * @return true, if is server in progress
     */
    public boolean isServerInProgress() {
        return isServerInProgress;
    }

    /**
     * Sets the server in progress.
     *
     * @param isServrInProgress the new server in progress
     */
    public void setServerInProgress(boolean isServrInProgress) {
        this.isServerInProgress = isServrInProgress;
    }

    /**
     * Instantiates a new server.
     *
     * @param info the info
     * @throws DataStudioSecurityException the data studio security exception
     */
    public Server(IServerConnectionInfo info) throws DataStudioSecurityException {
        synchronized (LOCK) {
            sequence++;
            serverId = sequence;
        }
        this.newConfigInfo = info.getClone();
        this.connectionName = info.getConectionName();
        this.serverHost = info.getServerIp();
        this.serverPort = info.getServerPort();
        this.dbGroup = new DatabaseObjectGroup(OBJECTTYPE.DATABASE_GROUP, this);
        this.tablespaceGroup = new TablespaceObjectGroup(OBJECTTYPE.TABLESPACE_GROUP, this);
        this.accessMethods = new OLAPObjectGroup<AccessMethod>(OBJECTTYPE.ACCESSMETHOD_GROUP, this);
        savePrdOption = info.getSavePrdOption();
        SecureUtil sec = new SecureUtil();
        String path = ConnectionProfileManagerImpl.getInstance().getProfilePath(info);
        sec.setPackagePath(path);
        encrpytedProfilePrd = sec.encryptPrd(info.getPrd());
        encrpytedsslPrd = sec.encryptPrd(info.getSSLPrd());

        newConfigInfo.clearPasrd();
        info.clearPasrd();
        this.privilegeFlag = info.isPrivilegeBasedObAccessEnabled();

        this.userRoleObjectGroup = new UserRoleObjectGroup(OBJECTTYPE.USER_ROLE_GROUP, this);
    }

    /**
     * Clear prds.
     */
    public void clearPrds() {
        if (savePrdOption.equals(SavePrdOptions.DO_NOT_SAVE)) {
            encrpytedProfilePrd = "";
        }
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return serverId;
    }

    /**
     * Gets the host.
     *
     * @return the host
     */
    public String getHost() {
        return serverHost;
    }

    /**
     * Sets the host.
     *
     * @param host the new host
     */
    public void setHost(String host) {
        this.serverHost = host;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public int getPort() {
        return serverPort;
    }

    /**
     * Sets the port.
     *
     * @param port the new port
     */
    public void setPort(int port) {
        this.serverPort = port;
    }

    /**
     * Gets the db by id.
     *
     * @param oid the oid
     * @return the db by id
     */
    public Database getDbById(long oid) {
        return this.dbGroup.getObjectById(oid);
    }

    /**
     * Gets the db by name.
     *
     * @param name the name
     * @return the db by name
     */
    public Database getDbByName(String name) {
        Iterator<Database> dbItr = this.dbGroup.iterator();
        Database db = null;

        boolean hasNext = dbItr.hasNext();
        while (hasNext) {
            db = dbItr.next();
            if (name.equals(db.getName())) {
                return db;
            }

            hasNext = dbItr.hasNext();
        }

        return null;
    }

    /**
     * Removes the database.
     *
     * @param oid the oid
     */
    public void removeDatabase(long oid) {
        this.dbGroup.removeFromGroup(oid);
    }

    /**
     * Gets the all databases.
     *
     * @return the all databases
     */
    public Collection<Database> getAllDatabases() {
        return this.dbGroup.getSortedServerObjectList();
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return this.connectionName;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return this.connectionName + " (" + this.serverHost + ':' + this.serverPort + ')';
    }

    /**
     * Gets the display name for domain.
     *
     * @return the display name for domain
     */
    public String getDisplayNameForDomain() {
        String wrappedHost = null;
        int hostLength = this.serverHost.length();
        if (hostLength > 25) {
            wrappedHost = this.serverHost.substring(0, 11) + "..."
                    + this.serverHost.substring(hostLength - 11, hostLength);
        } else {
            wrappedHost = this.serverHost;
        }
        return this.connectionName + " (" + wrappedHost + ':' + this.serverPort + ')';
    }

    /**
     * Checks if is aleast one db connected.
     *
     * @return true, if is aleast one db connected
     */
    public boolean isAleastOneDbConnected() {
        return ConnectionUtils.isAleastOneDbConnected(this.dbGroup.getSortedServerObjectList());
    }

    /**
     * Adds the D bto list.
     *
     * @param db the db
     */
    public void addDBtoList(Database db) {
        this.dbGroup.addToGroup(db);
    }

    /**
     * Creates the DB connection profile.
     *
     * @param serverInfo the server info
     * @param status the status
     * @return the connection profile id
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws PasswordExpiryException the password expiry exception
     * @throws OutOfMemoryError the out of memory error
     */
    public ConnectionProfileId createDBConnectionProfile(IServerConnectionInfo serverInfo, IJobCancelStatus status)
            throws MPPDBIDEException, PasswordExpiryException, OutOfMemoryError {

        // First time we will not have oid
        Database db = new Database(this, 0, serverInfo.getDatabaseName());
        db.connectToServer();
        LoginNotificationManager.loginOnPswdExpiry(db);

        db.loadDBSpecificContents1(status);
        parseServerVersion(db.getServerVersion());
        addDBtoList(db);
        updateDBList(db);
        DatabaseUtils.checkCancelStatusAndAbort(status, db);
        db.loadDBSpecificContents2(status);
        setServerIP2(db.fetchServerIP());
        DatabaseUtils.checkCancelStatusAndAbort(status, db);
        refreshUserRoleObjectGroup();

        return db.getProfileId();
    }

    /**
     * Parses the server version.
     *
     * @param servVersion the serv version
     */
    protected void parseServerVersion(String servVersion) {
        this.setServerVersion(servVersion);
        isSupportNodeGroupPrivilege = false;
        this.trimmedVersion = CustomStringUtility.getFullServerVersionString(serverVersion);
    }

    /**
     * Update DB list.
     *
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void updateDBList(Database db) throws DatabaseCriticalException, DatabaseOperationException {
        Iterator<Database> otherDbItr = DatabaseUtils.getAllDBListInServer(db).iterator();
        Database otherDb = null;
        boolean hasNext = otherDbItr.hasNext();
        while (hasNext) {
            otherDb = otherDbItr.next();
            if (!this.dbGroup.contains(otherDb.getOid())) {
                this.dbGroup.addToGroup(otherDb);
            }
            hasNext = otherDbItr.hasNext();
        }
    }

    /**
     * Refresh.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws Exception the exception
     */
    public void refresh() throws DatabaseOperationException, DatabaseCriticalException, Exception {
        Database db = findOneActiveDb();
        refreshDBs(db);
        this.tablespaceGroup.clear();
        db.fetchAllTablespace();
        this.accessMethods.clear();
        AccessMethod.fetchAllAccessMethods(db);
    }

    /**
     * Refresh server IP.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refreshServerIP() throws DatabaseOperationException, DatabaseCriticalException {
        Database db = findOneActiveDb();
        db.fetchServerIP();
    }

    /**
     * Fetch server objects.
     *
     * @throws OutOfMemoryError the out of memory error
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void fetchServerObjects() throws OutOfMemoryError, DatabaseOperationException, DatabaseCriticalException {
        refreshServerIP();
        getDatabaseGroup().refresh();
    }

    /**
     * Refresh tablespace.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refreshTablespace() throws DatabaseOperationException, DatabaseCriticalException {
        varifyCounter();
        Database db = findOneActiveDb();
        this.tablespaceGroup.clear();
        db.fetchAllTablespace();
    }

    /**
     * Varify counter.
     */
    public void varifyCounter() {
        RefreshCounter refreshCounter = RefreshCounter.getInstance();
        int counter = refreshCounter.getCountValue();
        counter = counter + 1;
        RefreshCounter.getInstance().setCountValue(counter);
    }

    /**
     * Refresh tablespace metadata.
     *
     * @param oid the oid
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refreshTablespaceMetadata(Long oid) throws DatabaseOperationException, DatabaseCriticalException {
        Database db = findOneActiveDb();
        this.tablespaceGroup.removeFromGroup(oid);
        db.fetchTablespaceMetaData(oid);
    }

    /**
     * Refresh D bs.
     *
     * @param db the db
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refreshDBs(Database db) throws DatabaseOperationException, DatabaseCriticalException {

        ArrayList<Database> newDbs = DatabaseUtils.getAllDBListInServer(db);
        HashMap<Long, Database> newDbMap = new HashMap<Long, Database>(4);
        Iterator<Database> newDbItr = newDbs.iterator();
        boolean hasNext = newDbItr.hasNext();
        /*
         * Loop through the new db list and create a hashmap for quicker &
         * easier access.
         */
        hasNext = refreshDbGroup(newDbMap, newDbItr, hasNext);

        Iterator<Database> oldDbs = this.dbGroup.getSortedServerObjectList().iterator();
        hasNext = oldDbs.hasNext();
        long oid = 0;
        while (hasNext) {
            oid = oldDbs.next().getOid();
            oldDbs = refreshEachDatabase(newDbMap, oldDbs, oid);
            hasNext = oldDbs.hasNext();
        }
    }

    private Iterator<Database> refreshEachDatabase(HashMap<Long, Database> newDbMap, Iterator<Database> oldDbs,
            long oid) throws DatabaseOperationException, DatabaseCriticalException, OutOfMemoryError {
        Database currDb = null;
        Database newDb = null;
        if (!newDbMap.containsKey(oid)) {
            /* Remove non existing DBs */
            this.dbGroup.getObjectById(oid).destroy();
            this.dbGroup.removeFromGroup(oid);
            // Bala issue List #14 start
            oldDbs = this.dbGroup.getSortedServerObjectList().iterator();
            // Bala issue List #14 end
        } else {
            /*
             * Update the name & other objects, might have changed via other
             * means
             */
            newDb = newDbMap.get(oid);
            currDb = this.dbGroup.getObjectById(oid);
            currDb.setName(newDb.getName());

            fetchDatabaseObjects(currDb);
        }
        return oldDbs;
    }

    private void fetchDatabaseObjects(Database currDb)
            throws DatabaseOperationException, DatabaseCriticalException, OutOfMemoryError {
        if (currDb.isConnected()) {
            currDb.fetchSearchPathObjects(false);
            currDb.fetchDefaultDatatypes(true);
            DatabaseHelper.fetchTablespaceName(currDb);
        }
    }

    private boolean refreshDbGroup(HashMap<Long, Database> newDbMap, Iterator<Database> newDbItr, boolean hasNext) {
        Database currDb;
        while (hasNext) {
            currDb = newDbItr.next();
            newDbMap.put(currDb.getOid(), currDb);
            Database database = this.dbGroup.getObjectById(currDb.getOid());
            /* Add non existing db's in db list. */
            addDatabaseInGroup(currDb, database);
            hasNext = newDbItr.hasNext();
        }
        return hasNext;
    }

    private void addDatabaseInGroup(Database currDb, Database database) {
        if (!this.dbGroup.contains(currDb.getOid())) {
            this.dbGroup.addToGroup(currDb);
        } else {
            // We have added this else condition
            // ISSUE : If Database is renamed from SQL Terminal then
            // Database name is not updated in Ds list ( DB IS NOT GETTING
            // CONNECTED)
            addIfDBIsDifferent(currDb, database);
        }
    }

    private void addIfDBIsDifferent(Database currDb, Database database) {
        if (!database.isSameName(currDb)) {
            dbGroup.remove(currDb);
            this.dbGroup.addToGroup(currDb);
        }
    }

    /**
     * Close.
     */
    public void close() {
        Iterator<Database> nodesItr = this.dbGroup.getSortedServerObjectList().iterator();
        boolean hasNext = nodesItr.hasNext();
        Database db = null;
        while (hasNext) {
            db = nodesItr.next();
            db.destroy();
            hasNext = nodesItr.hasNext();
        }

        this.accessMethods.clear();
        this.tablespaceGroup.clear();
        this.userRoleObjectGroup.clear();
    }

    /**
     * Destroy.
     */
    public void destroy() {
        if (accessMethods != null) {
            accessMethods.clear();
            accessMethods = null;
        }

        if (tablespaceGroup != null) {
            tablespaceGroup.clear();
            tablespaceGroup = null;
        }

        this.dbGroup.clear();
    }

    /**
     * Creates the database.
     *
     * @param databaseName the database name
     * @param encodingType the encoding type
     * @param freedb the freedb
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void createDatabase(String databaseName, String encodingType, Database freedb, DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        String qry = null;

        if (!encodingType.isEmpty()) {
            qry = "Create database " + ServerObject.getQualifiedObjectName(databaseName) + " encoding='" + encodingType
                    + "'" + " template =" + "template0" + ';';
        } else {
            qry = "Create database " + ServerObject.getQualifiedObjectName(databaseName) + ';';
        }

        dbConnection.execNonSelect(qry);
        updateDBList(freedb);
    }

    /**
     * Creates the tablespace.
     *
     * @param query the query
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void createTablespace(String query) throws DatabaseOperationException, DatabaseCriticalException {
        Database db = findOneActiveDb();
        db.getConnectionManager().execNonSelectOnObjBrowserConn(query);
        this.refreshTablespace();
    }

    /**
     * Creates the tablespace.
     *
     * @param query the query
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void createTablespace(String query, DBConnection conn)
            throws DatabaseOperationException, DatabaseCriticalException {
        conn.execNonSelect(query);
        this.refreshTablespace();
    }

    /**
     * Find one active db.
     *
     * @return the database
     * @throws DatabaseOperationException the database operation exception
     */
    public Database findOneActiveDb() throws DatabaseOperationException {
        Iterator<Database> dbItr = this.dbGroup.getSortedServerObjectList().iterator();
        boolean hasNext = dbItr.hasNext();
        Database db = null;

        while (hasNext) {
            db = dbItr.next();
            if (db.isConnected()) {
                return db;
            }

            hasNext = dbItr.hasNext();
        }
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_NO_CONNECTION_AVAILABLE));
        throw new DatabaseOperationException(IMessagesConstants.ERR_NO_CONNECTION_AVAILABLE);
    }

    /**
     * Gets the tablespace group.
     *
     * @return the tablespace group
     */
    public TablespaceObjectGroup getTablespaceGroup() {
        return this.tablespaceGroup;
    }

    /**
     * Gets the database group.
     *
     * @return the database group
     */
    public DatabaseObjectGroup getDatabaseGroup() {
        return this.dbGroup;
    }

    /**
     * Gets the another connection.
     *
     * @param oid the oid
     * @return the another connection
     * @throws DatabaseOperationException the database operation exception
     */
    public DBConnection getAnotherConnection(long oid) throws DatabaseOperationException {
        return ConnectionUtils.getAnotherConnection(oid, this.dbGroup.getSortedServerObjectList());
    }

    /**
     * Adds the to access methods.
     *
     * @param am the am
     */
    public void addToAccessMethods(AccessMethod am) {
        this.accessMethods.addToGroup(am);
    }

    /**
     * Removes the from access method.
     *
     * @param oid the oid
     */
    public void removeFromAccessMethod(long oid) {
        this.accessMethods.removeFromGroup(oid);
    }

    /**
     * Gets the access method.
     *
     * @param oid the oid
     * @return the access method
     */
    public AccessMethod getAccessMethod(long oid) {
        return this.accessMethods.getObjectById(oid);
    }

    /**
     * Gets the access methods.
     *
     * @return the access methods
     */
    public Collection<AccessMethod> getAccessMethods() {
        return this.accessMethods.getSortedServerObjectList();
    }

    /**
     * Sets the server IP 2.
     *
     * @param serverIP the new server IP 2
     */
    protected void setServerIP2(String serverIP) {
        this.serverIP = serverIP;
    }

    @Override
    public int hashCode() {
        final int prime = MPPDBIDEConstants.PRIME_31;
        int result = 1;
        result = prime * result + ((getHost() == null) ? 0 : getHost().hashCode() + (getPort() + "").hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Server)) {
            return false;
        }
        Server other = (Server) obj;
        return getId() == other.getId();
    }

    /**
     * Gets the server connection info.
     *
     * @param dbname the dbname
     * @return the server connection info
     * @throws DataStudioSecurityException the data studio security exception
     */
    public IServerConnectionInfo getServerConnectionInfo(String dbname) throws DataStudioSecurityException {
        IServerConnectionInfo connInfo = this.newConfigInfo.getClone();
        connInfo.setDatabaseName(dbname);
        SecureUtil sec = new SecureUtil();
        String path = ConnectionProfileManagerImpl.getInstance().getProfilePath(connInfo);
        sec.setPackagePath(path);
        connInfo.setPrd(sec.decryptPrd(encrpytedProfilePrd));
        connInfo.setSSLPrd(sec.decryptPrd(encrpytedsslPrd));

        return connInfo;
    }

    /**
     * Sets the prd.
     *
     * @param prd the new prd
     */
    public void setPrd(String prd) {
        try {
            encrpytedProfilePrd = prd;

            IServerConnectionInfo info = newConfigInfo.getClone();
            if (isPermanantOptionSelected()) {
                info.setPrd(encrpytedProfilePrd.toCharArray());

                info.setSavePrdOption(savePrdOption);
                ConnectionProfileManagerImpl.getInstance().saveProfile(info);
                info.setPrd(new char[0]);
                newConfigInfo.setSavePrdOption(savePrdOption);
                newConfigInfo.setPrd(encrpytedProfilePrd.toCharArray());

            } else if (isCurrentSessionSelected()) {
                info.setSavePrdOption(savePrdOption);
                info.setPrd(new char[0]);
                ConnectionProfileManagerImpl.getInstance().saveProfile(info);
                newConfigInfo.setSavePrdOption(savePrdOption);
            }
        } catch (DataStudioSecurityException exception) {
            MPPDBIDELoggerUtility.error("Server: set password failed.", exception);
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Server: set password failed.", exception);
        }
    }

    private boolean isCurrentSessionSelected() {
        return savePrdOption.equals(SavePrdOptions.CURRENT_SESSION_ONLY);
    }

    private boolean isPermanantOptionSelected() {
        return savePrdOption.equals(SavePrdOptions.PERMANENTLY);
    }

    /**
     * Sets the save prd option.
     *
     * @param ordinal the new save prd option
     */
    public void setSavePrdOption(int ordinal) {
        switch (ordinal) {
            case 0: {
                savePrdOption = SavePrdOptions.PERMANENTLY;
                break;
            }
            case 1: {
                savePrdOption = SavePrdOptions.CURRENT_SESSION_ONLY;
                break;
            }
            default: {
                savePrdOption = SavePrdOptions.DO_NOT_SAVE;
            }
        }
    }

    /**
     * Gets the save prd option.
     *
     * @return the save prd option
     */
    public SavePrdOptions getSavePrdOption() {
        return this.savePrdOption;
    }

    /**
     * Gets the encrpyted profile prd.
     *
     * @return the encrpyted profile prd
     */
    public String getEncrpytedProfilePrd() {
        return this.encrpytedProfilePrd;
    }

    /**
     * Gets the encrpytedssl prd.
     *
     * @return the encrpytedssl prd
     */
    public String getEncrpytedsslPrd() {
        return this.encrpytedsslPrd;
    }

    /**
     * Persist connection details.
     *
     * @param info the info
     * @throws DatabaseOperationException the database operation exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public void persistConnectionDetails(IServerConnectionInfo info)
            throws DatabaseOperationException, DataStudioSecurityException {
        if (SavePrdOptions.PERMANENTLY.equals(info.getSavePrdOption())) {
            info.setPrd(this.getEncrpytedProfilePrd().toCharArray());

        }

        ConnectionProfileManagerImpl.getInstance().saveProfile(info);
        info.clearPasrd();

        this.newConfigInfo = info.getClone();
    }

    @Override
    public IServerConnectionInfo getServerConnectionInfo() {
        return newConfigInfo;
    }

    /**
     * Gets the user name from server conn info.
     *
     * @return the user name from server conn info
     */
    public String getUserNameFromServerConnInfo() {
        return newConfigInfo.getDsUsername();
    }

    /**
     * Sets the server connection info.
     *
     * @param info the new server connection info
     */
    public void setServerConnectionInfo(IServerConnectionInfo info) {
        newConfigInfo = info.getClone();
        this.connectionName = newConfigInfo.getConectionName();
    }

    /**
     * Belongs to.
     *
     * @param object the object
     * @param server the server
     * @return true, if successful
     */
    public boolean belongsTo(Object object, Server server) {
        return this.getName().equals(server.getName());
    }

    /**
     * Checks if is database refreshin progress.
     *
     * @return true, if is database refreshin progress
     */
    public boolean isDatabaseRefreshinProgress() {
        Iterator<Database> dbItr = this.dbGroup.getSortedServerObjectList().iterator();
        boolean hasNext = dbItr.hasNext();
        Database db = null;

        while (hasNext) {
            db = dbItr.next();
            if (isDBLoadingINProgress(db)) {
                return true;
            }

            hasNext = dbItr.hasNext();
        }
        return false;
    }

    private boolean isDBLoadingINProgress(Database db) {
        return db.isConnected() && db.isLoadingNamespaceInProgress();
    }

    /**
     * Refresh user role object group.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refreshUserRoleObjectGroup() throws DatabaseOperationException, DatabaseCriticalException {
        List<UserRole> userRoles = UserRoleManager.fetchAllUserRoleWithOutSuperUser(this,
                this.findOneActiveDb().getConnectionManager().getObjBrowserConn());
        this.userRoleObjectGroup.clear();
        userRoles.stream().forEach(userRole -> this.userRoleObjectGroup.addToGroup(userRole));
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public Object[] getChildren() {
        Collection<Object> servers = new ArrayList<Object>(4);
        servers.add(this.getDatabaseGroup());
        servers.add(this.getTablespaceGroup());
        servers.add(this.userRoleObjectGroup);

        return servers.toArray();

    }

    /**
     * Gets the driver name.
     *
     * @return the driver name
     */
    public String getDriverName() {
        return this.driverName;
    }

    /**
     * Sets the driver name.
     *
     * @param driverName the new driver name
     */
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    /**
     * Checks if is support tablespace relative path.
     *
     * @return true, if is support tablespace relative path
     */
    public boolean isSupportTablespaceRelativePath() {
        return true;
    }

    /**
     * Sets the server version.
     *
     * @param vers the new server version
     */
    public void setServerVersion(String vers) {
        this.serverVersion = vers;
    }

    /**
     * Gets the server version.
     *
     * @param isTrim the is trim
     * @return the server version
     */
    public String getServerVersion(boolean isTrim) {
        if (isTrim) {
            return this.trimmedVersion;
        }

        return this.serverVersion;
    }

    /**
     * Sets the server compatible to node group. use for testing
     *
     * @param flag the new server compatible to node group
     */
    public void setServerCompatibleToNodeGroup(boolean flag) {
        isSupportNodeGroupPrivilege = true;
    }

    /**
     * Checks if is server compatible to node group privilege.
     *
     * @return true, if is server compatible to node group privilege
     */
    public boolean isServerCompatibleToNodeGroupPrivilege() {
        return isSupportNodeGroupPrivilege;
    }

    /**
     * Gets the server IP.
     *
     * @return the server IP
     */
    public String getServerIP2() {
        return serverIP;
    }

    /**
     * Gets the privilege flag.
     *
     * @return the privilege flag
     */
    public boolean getPrivilegeFlag() {
        return privilegeFlag;
    }

    /**
     * Sets the privilege flag.
     *
     * @param privilegeFlag the new privilege flag
     */
    public void setPrivilegeFlag(boolean privilegeFlag) {
        this.privilegeFlag = privilegeFlag;
    }

    /**
     * Gets the user role object group.
     *
     * @return the user role object group
     */
    public UserRoleObjectGroup getUserRoleObjectGroup() {
        return userRoleObjectGroup;
    }

    /**
     * Gets the jdbc connection string prefix.
     *
     * @return the jdbc connection string prefix
     */
    public String getJdbcConnectionStringPrefix() {
        return "jdbc:postgresql://";
    }

    /**
     * Gets the all databases for search.
     *
     * @param dataBases the data bases
     * @param dBMap the d B map
     * @return the all databases for search
     */
    public void getAllDatabasesForSearch(ArrayList<String> dataBases, HashMap<Integer, Database> dBMap) {
        Iterator<Database> dBs = this.getAllDatabases().iterator();

        boolean dbHasNext = dBs.hasNext();
        Database databse = null;
        int idex = 0;
        while (dbHasNext) {
            databse = dBs.next();
            if (databse.isConnected()) {
                dataBases.add(databse.getName());
                dBMap.put(idex, databse);
                idex++;
            }
            dbHasNext = dBs.hasNext();
        }
    }

    /**
     * Removes the from database group.
     *
     * @param oid the oid
     */
    public void removeFromDatabaseGroup(long oid) {
        dbGroup.removeFromGroup(oid);
    }

    /**
     * Adds the to database group.
     *
     * @param obj the obj
     */
    public void addToDatabaseGroup(Database obj) {
        dbGroup.addToGroup(obj);
    }

    /**
     * Adds the to tablespace group.
     *
     * @param obj the obj
     */
    public void addToTablespaceGroup(Tablespace obj) {
        tablespaceGroup.addToGroup(obj);
    }

    /**
     * Gets the tablespaces.
     *
     * @return the tablespaces
     */
    public Iterator<Tablespace> getTablespaces() {
        return this.getTablespaceGroup().getSortedServerObjectList().iterator();
    }

    /**
     * Gets the tablespace by id.
     *
     * @param oid the oid
     * @return the tablespace by id
     */
    public Tablespace getTablespaceById(long oid) {
        return getTablespaceGroup().getObjectById(oid);
    }

    /**
     * Gets the defaul parametert map.
     *
     * @return the defaul parametert map
     */
    public HashMap<Long, ArrayList<DefaultParameter>> getDefaulParametertMap() {
        return defaulParametertMap;
    }

    /**
     * Sets the defaul parametert map.
     *
     * @param id the id
     * @param debugDefaultList the debug default list
     */
    public void setDefaulParametertMap(Long id, ArrayList<DefaultParameter> debugDefaultList) {
        defaulParametertMap.put(id, debugDefaultList);
    }

    /**
     * Gets the user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return this.getServerConnectionInfo().getDsUsername();
    }
    
    /**
     * is version after 930.
     *
     * @return boolean true if after 930 else false
     */
    public boolean versionAfter930() {
        String serverVersionString = getServerVersion(true);
        String compareTrimVersionFor1230 = "PostgreSQL 9.2.4 (GaussDB Kernel V500R001C20 build )";
        if (serverVersionString.equals(compareTrimVersionFor1230)) {
            return true;
        }
        String compareTrimVersion = "openGauss 1.0.0";
        int length = serverVersionString.length() < compareTrimVersion.length() ?
                serverVersionString.length() : compareTrimVersion.length();
        String trimVersion = serverVersionString.substring(0, length);
        if (trimVersion.compareTo(compareTrimVersion) > 0) {
            return true;
        }
        boolean ret = true;
        String version = getServerVersion(false);
        try {
            String regex = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Matcher matcher = Pattern.compile(regex).matcher(version);
            if (matcher.find()) {
                String curVersionTime = matcher.group().trim();
                Date curDate = format.parse(curVersionTime);
                if (curDate.compareTo(format.parse("2020-08-30 00:00:00")) <= 0) {
                    ret = false;
                }
            }
        } catch (ParseException dateFormatExecept) {
            ret = true;
        }
        return ret;
    }
}
