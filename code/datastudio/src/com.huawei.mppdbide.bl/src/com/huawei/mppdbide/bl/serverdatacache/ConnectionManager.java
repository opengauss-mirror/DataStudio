/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.adapter.driver.DBMSDriverManager;
import com.huawei.mppdbide.adapter.factory.ConnectionDriverFactory;
import com.huawei.mppdbide.adapter.factory.DBConnectionFactory;
import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.adapter.gauss.ObjectBrowserDBConnection;
import com.huawei.mppdbide.bl.executor.AbstractExecutor;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.util.BLUtils;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.DsEncodingEnum;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.exceptions.UnknownException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ConnectionManager {
    private Server server;
    private DBConnection objBrowserConn;
    private DBConnection loginNotifConn;
    private DBConnection sqlTerminalConn;
    private TerminalConnectionPoolManager connPoolManager = null;
    private IConnectionDriver connectionDriver;
    private EncodingDetail encodingDetail = null;
    private DBInternals dbInternals = null;
    private String lastSearchPathQuery = null;

    /**
     * 
     * Title: class
     * 
     * Description: The Class TerminalConnectionPoolManager.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class TerminalConnectionPoolManager {
        private final Object poolLock = new Object();
        private List<DBConnection> freeTerminalConnections;
        private List<DBConnection> usedTerminalConnections;

        /**
         * The Constant MAX_TERMINAL_CONNECTIONS_ALLOWED.
         */
        public static final int MAX_TERMINAL_CONNECTIONS_ALLOWED = 20;

        private static final int MAX_TERMINAL_CONNECTIONS_RETAINED = 1;

        /**
         * Instantiates a new terminal connection pool manager.
         */
        public TerminalConnectionPoolManager() {
            freeTerminalConnections = Collections
                    .synchronizedList(new ArrayList<DBConnection>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE));
            usedTerminalConnections = Collections
                    .synchronizedList(new ArrayList<DBConnection>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE));
        }

        /**
         * Disconnect terminal cons.
         */
        public void disconnectTerminalCons() {
            synchronized (poolLock) {
                for (DBConnection con : freeTerminalConnections) {
                    con.disconnect();
                    con = null;
                }

                for (DBConnection con : usedTerminalConnections) {
                    con.disconnect();
                    con = null;
                }

                freeTerminalConnections.clear();
                usedTerminalConnections.clear();
            }
        }

        /**
         * Removes the connection from pool.
         *
         * @param conn the conn
         * @return true, if successful
         */
        public boolean removeConnectionFromPool(DBConnection conn) {
            synchronized (poolLock) {
                return usedTerminalConnections.remove(conn);
            }
        }

        /**
         * Release and disconnection.
         *
         * @param conn the conn
         */
        public void releaseAndDisconnection(DBConnection conn) {
            boolean isRemoved = false;
            synchronized (poolLock) {
                isRemoved = removeConnectionFromPool(conn);

                if (isRemoved) {
                    conn.disconnect();
                }
            }
        }

        /**
         * Release connection.
         *
         * @param conn the conn
         * @param savePrdOption the save prd option
         */
        public void releaseConnection(DBConnection conn,
                com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions savePrdOption) {
            boolean isRemoved = false;
            synchronized (poolLock) {
                if (!savePrdOption.equals(SavePrdOptions.DO_NOT_SAVE)) {
                    isRemoved = usedTerminalConnections.remove(conn);

                    if (isRemoved) {
                        if (freeTerminalConnections.size() < MAX_TERMINAL_CONNECTIONS_RETAINED) {
                            freeTerminalConnections.add(conn);
                        } else {
                            conn.disconnect();
                        }
                    }
                } else {
                    releaseAndDisconnection(conn);
                }
            }

        }

        /**
         * Adds the to used term pool.
         *
         * @param conn the conn
         */
        public void addToUsedTermPool(DBConnection conn) {
            synchronized (poolLock) {
                usedTerminalConnections.add(conn);
            }
        }

        /**
         * Gets the new free term conn.
         *
         * @param dbname the dbname
         * @param servername the servername
         * @return the new free term conn
         * @throws MPPDBIDEException the MPPDBIDE exception
         */
        public DBConnection getNewFreeTermConn(String dbname, String servername) throws MPPDBIDEException {
            DBConnection conn = null;
            synchronized (poolLock) {
                if (freeTerminalConnections.size() > 0) {
                    conn = freeTerminalConnections.get(0);
                    freeTerminalConnections.remove(conn);
                    usedTerminalConnections.add(conn);
                    return conn;
                }

                if (usedTerminalConnections.size() >= MAX_TERMINAL_CONNECTIONS_ALLOWED) {
                    MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(
                            IMessagesConstants.DATABASE_CONNECTION_LIMIT_REACHED, MAX_TERMINAL_CONNECTIONS_ALLOWED));
                    throw new MPPDBIDEException(IMessagesConstants.DATABASE_CONNECTION_LIMIT_REACHED,
                            MAX_TERMINAL_CONNECTIONS_ALLOWED, dbname + '@' + servername);
                }
            }
            return null;
        }

        /**
         * Execute query for all used connections.
         *
         * @param query the query
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         */
        public void executeQueryForAllUsedConnections(String query)
                throws DatabaseOperationException, DatabaseCriticalException {
            synchronized (poolLock) {
                /* Set default schema for all used connections in the pool */
                for (DBConnection connectionIter : this.usedTerminalConnections) {
                    if (!connectionIter.isClosed()) {
                        connectionIter.execNonSelect(query);
                    }
                }
            }
        }
    }

    /**
     * Sets the default schema for all used connections.
     *
     * @param query the new default schema for all used connections
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void setDefaultSchemaForAllUsedConnections(String query)
            throws DatabaseOperationException, DatabaseCriticalException {

        this.connPoolManager.executeQueryForAllUsedConnections(query);

        /* Set default schema for OB and SQL Terminal connections */
        if (null != this.objBrowserConn && !this.objBrowserConn.isClosed()) {
            this.objBrowserConn.execNonSelect(query);
        }

        if (null != this.sqlTerminalConn && !this.sqlTerminalConn.isClosed()) {
            this.sqlTerminalConn.execNonSelect(query);
        }

        this.lastSearchPathQuery = query;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class EncodingDetail.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class EncodingDetail {

        private String encoding;

        /**
         * Update encoding prop.
         *
         * @param props the props
         */
        public void updateEncodingProp(Properties props) {
            if (DsEncodingEnum.LATIN1.getEncoding().equalsIgnoreCase(this.encoding)) {
                props.setProperty("characterEncoding", DsEncodingEnum.LATIN1.getEncoding());
            }
        }

        /**
         * Sets the encoding.
         *
         * @param dbconn the new encoding
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        public void setEncoding(DBConnection dbconn) throws DatabaseCriticalException, DatabaseOperationException {
            this.encoding = DatabaseUtils.getServerEncoding(dbconn);

            if (DsEncodingEnum.LATIN1.getEncoding().equalsIgnoreCase(this.encoding)) {
                dbconn.executeClientEncoding(DsEncodingEnum.LATIN1.getEncoding());
            }
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DBInternals.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class DBInternals {

        Database db;

        /**
         * Gets the dbasename.
         *
         * @return the dbasename
         */
        public String getDbasename() {
            return db.getName();
        }

        /**
         * Gets the db type.
         *
         * @return the db type
         */
        public String getDbType() {
            return db.getDabaseType();
        }

        /**
         * Instantiates a new DB internals.
         *
         * @param dbName the db name
         * @param type the type
         * @param abstractExecutor the ex
         * @param id the id
         */
        public DBInternals(Database db) {
            this.db = db;
        }

        /**
         * Checks if is DB type OLAP.
         *
         * @return true, if is DB type OLAP
         */
        public boolean isDBTypeOLAP() {
            return getDbType().equals(DBTYPE.OPENGAUSS.toString());
        }
    }

    /**
     * Instantiates a new connection manager.
     *
     * @param db the db
     */
    public ConnectionManager(Database db) {
        if (null != db.getServer()) {
            this.server = db.getServer();
        }
        this.connPoolManager = new TerminalConnectionPoolManager();

        encodingDetail = new EncodingDetail();

        dbInternals = new DBInternals(db);

    }

    /**
     * Sets the connection driver name.
     *
     * @param driverName the new connection driver name
     */
    private void setConnectionDriverName(String driverName) {
        this.server.setDriverName(driverName);
    }

    /**
     * Gets the free connection.
     *
     * @return the free connection
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public DBConnection getFreeConnection() throws MPPDBIDEException {

        DBConnection conn = null;
        IServerConnectionInfo connectInfo = server.getServerConnectionInfo(dbInternals.getDbasename());

        try {
            conn = connPoolManager.getNewFreeTermConn(dbInternals.getDbasename(), server.getName());
        } catch (MPPDBIDEException exp) {
            throw exp;
        }
        if (null == conn) {
            conn = DBConnectionFactory.getConnectionObj(this.getConnectionDriver());
            connPoolManager.addToUsedTermPool(conn);
        }

        if (conn != null && conn.getConnection() != null ? conn.isClosed() : true) {
            Properties props = connectInfo.composeProperty(server.getDriverName());
            encodingDetail.updateEncodingProp(props);
            doConnect(conn, connectInfo.composeUrl(), props);

            ServerUtil.clearPropertyDetails(props);
        }

        if (null != this.lastSearchPathQuery && conn != null) {
            conn.execNonSelect(this.lastSearchPathQuery);
        }

        ServerUtil.clearConnectionInfo(connectInfo);
        server.clearPrds();

        return conn;
    }

    /**
     * Do connect.
     *
     * @param conn the conn
     * @param url the url
     * @param props the props
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void doConnect(DBConnection conn, String url, Properties props)
            throws DatabaseOperationException, DatabaseCriticalException {
        if (conn != null) {
            conn.connectViaDriver(props, url);
        }
    }

    /**
     * Do connections to server.
     *
     * @param connObjects the conn objects
     * @param props the props
     * @param url the url
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void doConnectionsToServer(DBConnection[] connObjects, Properties props, String url)
            throws DatabaseOperationException, DatabaseCriticalException {
        int connCount = 0;
        try {

            IConnectionDriver driver = this.getConnectionDriver();
            if (null == driver) {
                driver = getNewDriver(connObjects, props, url, connCount);

                connCount++;
            }
            connCount = connectViaDriver(connObjects, props, url, connCount, driver);

        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED, exp);
        }

        finally {
            diconnectConnObjects(connObjects, props, connCount);
        }
    }

    /**
     * Gets the new driver.
     *
     * @param connObjects the conn objects
     * @param props the props
     * @param url the url
     * @param connCount the i
     * @return the new driver
     * @throws SQLException the SQL exception
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private IConnectionDriver getNewDriver(DBConnection[] connObjects, Properties props, String url, int connCount)
            throws SQLException, DatabaseOperationException, DatabaseCriticalException {
        IConnectionDriver driver;
        DBMSDriverManager instance = DBMSDriverManager.getInstance(BLUtils.getInstance().getInstallationLocation());
        DBConnection dbconn = null;
        dbconn = instance.getConnection(props, url, dbInternals.getDbType());

        if (null == dbconn) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED));
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED);
        }
        driver = getDriverAndSetConnection(connObjects, connCount, dbconn);
        return driver;
    }

    /**
     * Diconnect conn objects.
     *
     * @param connObjects the conn objects
     * @param props the props
     * @param connCount the i
     */
    private void diconnectConnObjects(DBConnection[] connObjects, Properties props, int connCount) {
        if (connCount != connObjects.length) {
            for (int j1 = 0; j1 < connCount; j1++) {
                connObjects[j1].disconnect();
            }
        }
        SecureUtil.cleanKeyString(props.getProperty("password"));
        props.setProperty("password", "");
        props.remove("password");
    }

    /**
     * Connect via driver.
     *
     * @param connObjects the conn objects
     * @param props the props
     * @param url the url
     * @param i the i
     * @param driver the driver
     * @return the int
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private int connectViaDriver(DBConnection[] connObjects, Properties props, String url, int indexParam,
            IConnectionDriver driver) throws DatabaseOperationException, DatabaseCriticalException {
        int index = indexParam;
        for (; index < connObjects.length; index++) {
            connObjects[index].setDriver(driver);
            Properties driverspecific = driver.getDriverSpecificProperties();
            driverspecific.putAll(props);
            if (dbInternals.isDBTypeOLAP()) {
                encodingDetail.updateEncodingProp(driverspecific);
            }
            connObjects[index].connectViaDriver(driverspecific, url);
            driverspecific.setProperty("password", "");
            driverspecific.remove("password");
        }
        return index;
    }

    /**
     * Gets the driver and set connection.
     *
     * @param connObjects the conn objects
     * @param connCount the i
     * @param dbconn the dbconn
     * @return the driver and set connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private IConnectionDriver getDriverAndSetConnection(DBConnection[] connObjects, int connCount, DBConnection dbconn)
            throws DatabaseCriticalException, DatabaseOperationException {
        IConnectionDriver driver;
        driver = dbconn.getDriver();
        this.connectionDriver = driver;
        this.setConnectionDriverName(driver.getDriverName());
        connObjects[connCount].setDriver(driver);
        connObjects[connCount].setConnection(dbconn.getConnection());

        if (dbInternals.isDBTypeOLAP()) {
            encodingDetail.setEncoding(dbconn);
        }
        return driver;
    }

    /**
     * Connect to gauss.
     *
     * @param props the props
     * @param url the url
     * @param type the type
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void connectToGauss(Properties props, String url, DBTYPE type)
            throws DatabaseOperationException, DatabaseCriticalException {
        DBConnection[] connObjects = null;
        int numConnObj;

        numConnObj = 3;
        this.loginNotifConn = new DBConnection();
        this.sqlTerminalConn = new DBConnection();
        this.objBrowserConn = new ObjectBrowserDBConnection();

        boolean allConnected = false;

        connObjects = new DBConnection[numConnObj];
        connObjects[numConnObj - 2] = this.objBrowserConn;
        connObjects[numConnObj - 1] = this.sqlTerminalConn;

        if (connObjects.length == 3) {
            connObjects[0] = this.loginNotifConn;
        }

        try {
            doConnectionsToServer(connObjects, props, url);
            allConnected = true;
        } finally {
            if (!allConnected) {
                this.loginNotifConn = null;
                this.objBrowserConn = null;
                this.sqlTerminalConn = null;
            }
        }
    }

    /**
     * Release connection.
     *
     * @param conn the conn
     */
    public void releaseConnection(DBConnection conn) {
        connPoolManager.releaseConnection(conn, server.getSavePrdOption());
    }

    /**
     * Release and disconnection.
     *
     * @param conn the conn
     */
    public void releaseAndDisconnection(DBConnection conn) {
        connPoolManager.releaseAndDisconnection(conn);
    }

    /**
     * Removes the connection from pool.
     *
     * @param conn the conn
     * @return true, if successful
     */
    public boolean removeConnectionFromPool(DBConnection conn) {
        return connPoolManager.removeConnectionFromPool(conn);
    }

    /**
     * Disconnect conns.
     */
    public void disconnectConns() {
        if (null != objBrowserConn) {
            objBrowserConn.disconnect();
        }

        if (null != loginNotifConn) {
            loginNotifConn.disconnect();
        }

        if (null != sqlTerminalConn) {
            sqlTerminalConn.disconnect();
        }
    }

    /**
     * Disconnect terminal cons.
     */
    public void disconnectTerminalCons() {
        connPoolManager.disconnectTerminalCons();
        lastSearchPathQuery = null;
    }

    /**
     * Gets the obj browser conn.
     *
     * @return the obj browser conn
     */
    public DBConnection getObjBrowserConn() {
        return objBrowserConn;
    }

    /**
     * Sets the obj browser conn.
     *
     * @param objBrowserConn the new obj browser conn
     */
    public void setObjBrowserConn(DBConnection objBrowserConn) {
        this.objBrowserConn = objBrowserConn;
    }

    /**
     * Gets the sql terminal conn.
     *
     * @return the sql terminal conn
     */
    public DBConnection getSqlTerminalConn() {
        return sqlTerminalConn;
    }

    /**
     * Gets the connection driver.
     *
     * @return the connection driver
     * @throws DatabaseOperationException the database operation exception
     */
    public IConnectionDriver getConnectionDriver() throws DatabaseOperationException {
        if (null != this.connectionDriver) {
            return connectionDriver;
        }

        String driverName = this.server.getDriverName();
        if (null == driverName) {
            return null;
        }

        IConnectionDriver connDriver = ConnectionDriverFactory.getInstance().getDriver(driverName);
        if (null == connDriver) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_GET_DRIVER_VERSION_FAILED));
            throw new DatabaseOperationException(IMessagesConstants.ERR_BL_GET_DRIVER_VERSION_FAILED);
        }
        return connDriver;
    }

    /**
     * Cancel all connection queries.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void cancelAllConnectionQueries() throws DatabaseCriticalException, DatabaseOperationException {

        if (null != this.objBrowserConn) {
            this.objBrowserConn.cancelQuery();
        }

        if (null != this.loginNotifConn) {
            this.loginNotifConn.cancelQuery();
        }

        if (null != this.sqlTerminalConn) {
            this.sqlTerminalConn.cancelQuery();
        }

    }

    /*
     * API to expose getLastSuccessfulLogin() which is further used to close the
     * loginNotifConn
     * 
     */

    /**
     * Gets the successfull login.
     *
     * @return the successfull login
     */
    public DBConnection getSuccessfullLogin() {
        return loginNotifConn;
    }

    /**
     * Execute login details query.
     *
     * @param qry the qry
     * @return the result set
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public ResultSet executeLoginDetailsQuery(String qry) throws DatabaseCriticalException, DatabaseOperationException {
        ResultSet rs;
        IExecTimer timer = new ExecTimer(LoginNotificationManager.LOGIN_NOTIFICATION);
        timer.start();

        rs = loginNotifConn.execSelectAndReturnRs(qry);

        timer.stop();
        if (MPPDBIDELoggerUtility.isInfoEnabled()) {
            MPPDBIDELoggerUtility.info(LoginNotificationManager.LAST_LOGING_SUCCESS_DURATION + timer.getElapsedTime());
        }
        return rs;
    }

    /**
     * Exec select and get first val on obj browser conn.
     *
     * @param query the query
     * @return the string
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public String execSelectAndGetFirstValOnObjBrowserConn(String query)
            throws DatabaseCriticalException, DatabaseOperationException {
        return objBrowserConn.execSelectAndGetFirstVal(query);
    }

    /**
     * Exec select and return rs on obj browser conn.
     *
     * @param qry the qry
     * @return the result set
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public ResultSet execSelectAndReturnRsOnObjBrowserConn(String qry)
            throws DatabaseCriticalException, DatabaseOperationException {
        return this.objBrowserConn.execSelectAndReturnRs(qry);
    }
    
    /**
     * Exec select and return true if admin
     *
     * @return if admin
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public boolean execSelectCheckIfAdmin() throws DatabaseCriticalException, DatabaseOperationException {
        return this.objBrowserConn.execSelectAndReturnAdmin();
    }

    /**
     * Exec non select on obj browser conn.
     *
     * @param qry the qry
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execNonSelectOnObjBrowserConn(String qry) throws DatabaseCriticalException, DatabaseOperationException {
        this.objBrowserConn.execNonSelect(qry);
    }

    /**
     * Close RS on obj browser conn.
     *
     * @param rs the rs
     */
    public void closeRSOnObjBrowserConn(ResultSet rs) {
        objBrowserConn.closeResultSet(rs);
    }

    /**
     * Cancel login query.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void cancelLoginQuery() throws DatabaseCriticalException, DatabaseOperationException {
        loginNotifConn.cancelQuery();
    }

    /**
     * Checks if is show DDL support by server.
     *
     * @return true, if is show DDL support by server
     */
    public boolean isShowDDLSupportByServer() {
        try {
            IConnectionDriver driver = getConnectionDriver();
            if (driver != null) {
                if (!driver.getShowDDLSupportCheck()) {
                    return driver.getShowDDLSupport(objBrowserConn);
                }
                return driver.getShowDDLSupport();
            }
            return false;
        } catch (MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.error("connection error", exception);
            return false;
        }
    }
}
