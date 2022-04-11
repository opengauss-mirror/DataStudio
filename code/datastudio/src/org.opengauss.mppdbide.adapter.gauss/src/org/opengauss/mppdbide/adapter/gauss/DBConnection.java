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

package org.opengauss.mppdbide.adapter.gauss;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Properties;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.postgresql.core.BaseStatement;
import org.postgresql.core.TransactionState;
import org.postgresql.jdbc.PgConnection;

import org.opengauss.mppdbide.adapter.IConnectionDriver;
import org.opengauss.mppdbide.utils.DsEncodingEnum;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.ILogger;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.GlobaMessageQueueUtil;
import org.opengauss.mppdbide.utils.messaging.MessageQueue;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBConnection.
 * 
 * @since 3.0.0
 */
public class DBConnection {

    private static final long DELAY_MILLI = 1000L;
    private Connection dbConnection;
    private int fetchSize;
    private IConnectionDriver dbmsDriver;
    private boolean isInOutValueExists = true;

    private static final String SERVER_SUPPORT_DDL_QUERY = "select count(*) from pg_catalog.pg_proc "
            + "where proname='pg_get_tabledef';";

    /**
     * Instantiates a new DB connection.
     */
    public DBConnection() {

    }

    /**
     * Instantiates a new DB connection.
     *
     * @param dbmsDriver the dbms driver
     */
    public DBConnection(IConnectionDriver dbmsDriver) {
        this.dbmsDriver = dbmsDriver;
    }

    /**
     * Instantiates a new DB connection.
     *
     * @param driver the driver
     * @param actualConnection the actual connection
     */
    public DBConnection(IConnectionDriver driver, Connection actualConnection) {
        this.dbmsDriver = driver;
        this.dbConnection = actualConnection;
    }

    /**
     * sets the setIsInOutValueExists
     * 
     * @param isInOutValueExists the isInOutValueExists
     */
    public void setIsInOutValueExists(boolean isInOutValueExists) {
        this.isInOutValueExists = isInOutValueExists;
    }

    /**
     * Gets the setIsInOutValueExists
     * 
     * @return isInOutValueExists the isInOutValueExists
     */
    public boolean getIsInOutValueExists() {
        return this.isInOutValueExists;
    }

    /**
     * Gets the fetch size.
     *
     * @return the fetch size
     */
    private int getFetchSize() {
        return fetchSize;
    }

    /**
     * Gets the prepare stmt.
     *
     * @param qry the qry
     * @return the prepare stmt
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public PreparedStatement getPrepareStmt(final String qry)
            throws DatabaseCriticalException, DatabaseOperationException {
        PreparedStatement stmt = null;
        try {
            stmt = dbConnection.prepareStatement(qry);

        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_STMT_EXCEPTION, exp);
        }

        return stmt;
    }

    /**
     * Close statement.
     *
     * @param stmt the stmt
     */
    public void closeStatement(final Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("ADAPTER: statement close returned exception.", exception);
        }
    }

    /**
     * Connect via driver.
     *
     * @param props the props
     * @param url the url
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void connectViaDriver(Properties props, String url)
            throws DatabaseOperationException, DatabaseCriticalException {


        Driver driver = dbmsDriver.getJDBCDriver();

        if (null != driver) {
            try {
                dbConnection = driver.connect(url, props);
            } catch (SQLException excep) {
                GaussUtils.handleCriticalException(excep);
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED,
                        extractErrorCodeAndErrorMsgFromServerError(excep), excep);
            }
            // Temporary solution to avoid frequent timeout issue.
            // Need to be removed in V1R3C20.
            setSessionTimeoutToNever();
            setDSEncoding(props);
            return;
        }
    }

    /**
     * Db connect.
     *
     * @param props the props
     * @param url the url
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void dbConnect(Properties props, String url) throws DatabaseOperationException, DatabaseCriticalException {
        try {
            MPPDBIDELoggerUtility.info("ADAPTER: Sending connection request");

            dbConnection = DriverManager.getConnection(url, props);

            MPPDBIDELoggerUtility.info("ADAPTER: Successfully connected");
        } catch (SQLException excep) {
            if (MessageConfigLoader.getProperty(MPPDBIDEConstants.PROTOCOL_VERSION_ERROR)
                    .equalsIgnoreCase(excep.getMessage())) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.DOMAINNAME_REDIRECT_FAILURE,
                                MessageConfigLoader.getProperty(MPPDBIDEConstants.PROTOCOL_VERSION_ERROR)
                                        + MPPDBIDEConstants.LINE_SEPARATOR));
                throw new DatabaseOperationException(IMessagesConstants.DOMAINNAME_REDIRECT_FAILURE,
                        MessageConfigLoader.getProperty(MPPDBIDEConstants.PROTOCOL_VERSION_ERROR)
                                + MPPDBIDEConstants.LINE_SEPARATOR);
            } else {
                GaussUtils.handleCriticalException(excep);
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED, excep);
            }
        }
    }

    /**
     * Sets the DS encoding.
     *
     * @param props the new DS encoding
     */
    private void setDSEncoding(Properties props) {
        String encoding = "";
        String clientEncodingToSet = "";

        if (props.containsKey(MPPDBIDEConstants.SERVER_ENCODING_KEY)) {
            encoding = (String) props.get(MPPDBIDEConstants.SERVER_ENCODING_KEY);
        }

        if (DsEncodingEnum.LATIN1.getEncoding().equalsIgnoreCase(encoding)) {
            clientEncodingToSet = DsEncodingEnum.LATIN1.getEncoding();
        } else {
            encoding = props.getProperty("characterEncoding");

            for (DsEncodingEnum dsencoding : DsEncodingEnum.values()) {
                if (encoding.equals(dsencoding.getEncoding())) {
                    clientEncodingToSet = dsencoding.getEncoding();
                    break;
                }
            }
        }

        executeClientEncoding(clientEncodingToSet);
    }

    /**
     * Execute client encoding.
     *
     * @param clientEncodingToSet the client encoding to set
     */
    public void executeClientEncoding(String clientEncodingToSet) {
        PreparedStatement stmt = null;

        try {
            if (!"".equals(clientEncodingToSet)) {
                MPPDBIDELoggerUtility.error("No client encoding passed for execution.");
            }

            String query = String.format(Locale.ENGLISH, "set client_encoding=\"%s\";", clientEncodingToSet);
            stmt = dbConnection.prepareStatement(query);
            stmt.execute();

        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("Setting client encoding is failed .", exception);
        } finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Setting client encoding is failed .", exception);
            }
        }
    }

    /**
     * Sets the session timeout to never.Reset the session timeout to 0
     */
    private void setSessionTimeoutToNever() {
        try {
            execNonSelect("set session_timeout=0");
        } catch (DatabaseOperationException e) {
            // Not necessary to report error when fails. Its not a business
            // blocker.
            MPPDBIDELoggerUtility.warn("Unable set session timeout to infinity.");
        } catch (DatabaseCriticalException e) {
            MPPDBIDELoggerUtility.warn("Unable set session timeout to infinity.");
        }

    }

    /**
     * Gets the connection.
     *
     * @return the connection
     */
    public Connection getConnection() {
        return dbConnection;
    }

    /**
     * Checks if is closed.
     *
     * @return true, if is closed
     * @throws DatabaseOperationException the database operation exception
     */
    public boolean isClosed() throws DatabaseOperationException {
        try {
            return dbConnection.isClosed();
        } catch (SQLException exp) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED), exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED, exp);
        }
    }

    /**
     * Checks if is transaction open.
     *
     * @param serverVersion the server version
     * @return true, if is transaction open
     * @throws SQLException the SQL exception
     */
    public boolean isTransactionOpen(String serverVersion) throws SQLException {
        boolean state = false;
        if (dbConnection instanceof PgConnection) {
            PgConnection aConn = (PgConnection) dbConnection;
            TransactionState transactionState = aConn.getTransactionState();

            switch (transactionState) {
                case OPEN:
                case FAILED: {
                    state = true;
                    break;
                }
                default: {
                    state = false;
                    break;
                }
            }
        }

        return state;
    }

    /**
     * Cancel query.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void cancelQuery() throws DatabaseCriticalException, DatabaseOperationException {
        Statement stmt = null;

        try {
            stmt = dbConnection.createStatement();
            MPPDBIDELoggerUtility.info("ADAPTER: Sending cancle request");
            stmt.cancel();
            MPPDBIDELoggerUtility.info("ADAPTER: Cancle successfully executed");
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            MPPDBIDELoggerUtility.error("ADAPTER: cancel query returned exception.", exp);
        } finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("ADAPTER: statement close returned exception.", exception);
            }
        }

        try {
            stmt = dbConnection.createStatement();
            stmt.execute("select 1");
            stmt.cancel();
        } catch (SQLException e) {
            // No need to handle the exception
            // this code is added because
            // the server is trying to the cancel the statement if the cancel is
            // not done on previous operation
            MPPDBIDELoggerUtility.info("SQL Exception occured during cancel query");
        } finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("ADAPTER: statement close returned exception.", exception);
            }
        }

    }

    /**
     * Disconnect.
     */
    public void disconnect() {
        try {
            MPPDBIDELoggerUtility.info("ADAPTER: Disconecting from server");
            if (null != dbConnection && !dbConnection.isClosed()) {
                dbConnection.close();
            }
            MPPDBIDELoggerUtility.info("ADAPTER: Connection closed");

        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("ADAPTER: disconnect returned exception", exception);
        }
    }

    /**
     * Exec select and get first val.
     *
     * @param query the query
     * @return the string
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public String execSelectAndGetFirstVal(String query) throws DatabaseCriticalException, DatabaseOperationException {
        ResultSet rs = null;
        PreparedStatement stmt = getPrepareStmt(query);

        try {
            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.ADAPTER, ILogger.PERF_EXECUTE_STMT, true);
            rs = stmt.executeQuery();
            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.ADAPTER, ILogger.PERF_EXECUTE_STMT, false);

            if (rs.next()) {
                String val = rs.getString(1);
                return val;
            }
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID));
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);

            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_EXECUTE_FUN_PROC_TRIG_QUERY_FAILED,
                    extractErrorCodeAndErrorMsgFromServerError(exp), exp);
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }

    }

    /**
     * Exec select and return rs.
     *
     * @param query the query
     * @return the result set
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public ResultSet execSelectAndReturnRs(String query) throws DatabaseCriticalException, DatabaseOperationException {
        PreparedStatement stmt = getPrepareStmt(query);

        try {
            ResultSet rs = setFetchSizeAndExecuteQuery(stmt);
            return rs;
        } catch (SQLException exp) {
            handleSqlException(stmt, exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_EXECUTE_FUN_PROC_TRIG_QUERY_FAILED,
                    extractErrorCodeAndErrorMsgFromServerError(exp), exp);
        } catch (OutOfMemoryError e1) {
            closeStatement(stmt);
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED),
                    e1);
            throw new DatabaseCriticalException(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED, e1);
        }

    }
    
    /**
     * Exec select and return rs.
     *
     * @return the sql state
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public boolean execSelectAndReturnAdmin() throws DatabaseCriticalException, DatabaseOperationException {
        String query = "select pg_tablespace_location(oid) as location from pg_tablespace;";
        PreparedStatement stmt = getPrepareStmt(query);
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery();
            return true;
        } catch (SQLException exp) {       
            // throws exception if user has no previlege for pg_tablespace_location function 
            // This also concludes user is not an admin          
            if (exp.getSQLState().equals("42501")) {
                return false;
            } else {
                return true;
            }
        } finally {
            closeResultSet(rs);
        }
    }

    private void handleSqlException(PreparedStatement stmt, SQLException exp) throws DatabaseCriticalException {
        closeStatement(stmt);
        GaussUtils.handleCriticalException(exp);
    }

    /**
     * Exec select for search.
     *
     * @param query the query
     * @param parameter the parameter
     * @return the result set
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public ResultSet execSelectForSearch(String query, String parameter)
            throws DatabaseCriticalException, DatabaseOperationException {
        PreparedStatement stmt = null;
        try {
            stmt = getPrepareStmt(query);
            // For regular expression we don't need parameter so we are passing
            // parameter empty string
            if (!parameter.trim().isEmpty()) {
                stmt.setString(1, parameter);
            }
            ResultSet rs = setFetchSizeAndExecuteQuery(stmt);
            return rs;
        } catch (SQLException eexp) {
            handleSqlException(stmt, eexp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_EXECUTE_FUN_PROC_TRIG_QUERY_FAILED,
                    extractErrorCodeAndErrorMsgFromServerError(eexp), eexp);
        } catch (OutOfMemoryError exp) {
            closeStatement(stmt);
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED),
                    exp);
            throw new DatabaseCriticalException(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED, exp);
        }
    }

    /**
     * Exec select for search 2 parmeters.
     *
     * @param query the query
     * @param parameter the parameter
     * @param parameter2 the parameter 2
     * @return the result set
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public ResultSet execSelectForSearch2Parmeters(String query, String parameter, String parameter2)
            throws DatabaseCriticalException, DatabaseOperationException {
        PreparedStatement stmt = null;
        try {
            stmt = getPrepareStmt(query);
            // For regular expression we don't need parameter so we are passing
            // parameter empty string
            if (!parameter.trim().isEmpty()) {
                stmt.setString(1, parameter);
                stmt.setString(2, parameter2);
            }
            ResultSet rs = setFetchSizeAndExecuteQuery(stmt);
            return rs;
        } catch (SQLException eex2) {
            handleSqlException(stmt, eex2);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_EXECUTE_FUN_PROC_TRIG_QUERY_FAILED,
                    extractErrorCodeAndErrorMsgFromServerError(eex2), eex2);
        } catch (OutOfMemoryError exp) {
            closeStatement(stmt);
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED),
                    exp);
            throw new DatabaseCriticalException(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED, exp);
        }
    }

    /**
     * Sets the fetch size and execute query.
     *
     * @param stmt the stmt
     * @return the result set
     * @throws SQLException the SQL exception
     */
    private ResultSet setFetchSizeAndExecuteQuery(PreparedStatement stmt) throws SQLException {
        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.ADAPTER, ILogger.PERF_EXECUTE_STMT, true);
        stmt.setFetchSize(getFetchSize());
        ResultSet rs = stmt.executeQuery();
        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_EXECUTE_STMT, false);
        return rs;
    }

    /**
     * Exec select to export CSV.
     *
     * @param query the query
     * @param preferenceCount the preference count
     * @return the result set
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public ResultSet execSelectToExportCSV(String query, int preferenceCount)
            throws DatabaseCriticalException, DatabaseOperationException {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isExpOccurred = false;
        try {
            stmt = getPrepareStmt(query);
            stmt.setFetchSize(preferenceCount);

            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.ADAPTER, ILogger.PERF_EXECUTE_STMT, true);
            rs = stmt.executeQuery();
            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_EXECUTE_STMT, false);
            return rs;
        } catch (SQLException exp) {
            isExpOccurred = true;

            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_EXECUTE_FUN_PROC_TRIG_QUERY_FAILED,
                    extractErrorCodeAndErrorMsgFromServerError(exp), exp);
        } catch (OutOfMemoryError expe) {
            isExpOccurred = true;

            throw new DatabaseCriticalException(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED, expe);
        } finally {

            if (isExpOccurred) {
                try {
                    closeResultSet(rs);
                } finally {
                    closeStatement(stmt);
                }
            }
        }
    }

    /**
     * Exec import table data.
     *
     * @param query the query
     * @param fileStream the file stream
     * @return the long
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public long execImportTableData(String query, FileInputStream fileStream)
            throws DatabaseCriticalException, DatabaseOperationException {
        long totalRows = 0;
        try {
            CopyManager cm = new CopyManager((BaseConnection) dbConnection);
            totalRows = cm.copyIn(query, fileStream);
        } catch (SQLException excep) {
            commitConnection(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV);
            GaussUtils.handleCriticalException(excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV,
                    extractErrorCodeAndErrorMsgFromServerError(excep), excep);
        } catch (IOException eexp) {
            commitConnection(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV);
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV),
                    eexp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV, eexp);
        }
        return totalRows;
    }

    /**
     * Exec export data.
     *
     * @param query the query
     * @param fileOutStream the file out stream
     * @return the long
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public long execExportData(String query, FileOutputStream fileOutStream)
            throws DatabaseCriticalException, DatabaseOperationException {
        long totalRows = 0;
        try {
            CopyManager cm = new CopyManager((BaseConnection) dbConnection);
            totalRows = cm.copyOut(query, fileOutStream);
        } catch (SQLException exe) {
            commitConnection(IMessagesConstants.ERR_EXPORT_TABLE);
            GaussUtils.handleCriticalException(exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_EXPORT_TABLE,
                    extractErrorCodeAndErrorMsgFromServerError(exe), exe);
        } catch (IOException exp) {
            commitConnection(IMessagesConstants.ERR_EXPORT_TABLE);
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT),
                    exp);
            throw new DatabaseOperationException(IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT, exp);
        }
        return totalRows;
    }

    /**
     * Checks if is connected.
     *
     * @return true, if is connected
     */
    private boolean isConnected() {
        try {
            return (null != dbConnection) ? !dbConnection.isClosed() : false;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Commit connection.
     *
     * @param error the error
     * @throws DatabaseOperationException the database operation exception
     */
    public void commitConnection(String error) throws DatabaseOperationException {
        if (!isConnected()) {
            return;
        }

        try {
            if (!dbConnection.getAutoCommit()) {
                dbConnection.commit();
                dbConnection.setAutoCommit(true);
            }
        } catch (SQLException e1) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(error), e1);
            throw new DatabaseOperationException(error, e1);
        }
    }

    /**
     * Exec non select.
     *
     * @param query the query
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execNonSelect(final String query) throws DatabaseOperationException, DatabaseCriticalException {
        PreparedStatement stmt = null;
        try {
            stmt = dbConnection.prepareStatement(query);
            registerNoticeListner(stmt, GlobaMessageQueueUtil.getInstance().getMessageQueue());
            MPPDBIDELoggerUtility.perf("EXECUTION", ILogger.PERF_EXECUTE_STMT, true);
            stmt.execute();
            MPPDBIDELoggerUtility.perf("EXECUTION", ILogger.PERF_EXECUTE_STMT, false);

        } catch (SQLException excep) {
            GaussUtils.handleCriticalException(excep);
            try {
                if (!dbConnection.getAutoCommit()) {
                    dbConnection.rollback();
                }
            } catch (SQLException e1) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED), e1);
                throw new DatabaseOperationException(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED,
                        extractErrorCodeAndErrorMsgFromServerError(e1), excep);
            }

            throw new DatabaseOperationException(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED,
                    extractErrorCodeAndErrorMsgFromServerError(excep), excep);
        } finally {

            try {
                if (!dbConnection.getAutoCommit()) {
                    dbConnection.commit();
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Statement close failed for execNonselect", exception);
                // no need to do anytihng
            }
            try {

                if (null != stmt) {
                    stmt.close();
                }

            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Statement close failed for execNonselect ", exception);
                // no need to do anytihng
            }
        }
    }

    /**
     * Exec query with msg queue.
     *
     * @param query the query
     * @param queue the queue
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execQueryWithMsgQueue(final String query, MessageQueue queue)
            throws DatabaseOperationException, DatabaseCriticalException {
        PreparedStatement stmt = null;
        try {
            stmt = dbConnection.prepareStatement(query);
            registerNoticeListner(stmt, queue);
            MPPDBIDELoggerUtility.perf("EXECUTION", ILogger.PERF_EXECUTE_STMT, true);
            stmt.execute();
            MPPDBIDELoggerUtility.perf("EXECUTION", ILogger.PERF_EXECUTE_STMT, false);
        } catch (SQLException eexc) {
            GaussUtils.handleCriticalException(eexc);
            throw new DatabaseOperationException(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED,
                    extractErrorCodeAndErrorMsgFromServerError(eexc), eexc);
        } finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Statement close failed for execQueryWithMsgQueue", exception);
                // no need to do anytihng
            }
        }
    }

    /**
     * 
     * We have added ExecuteTimerForNonSelect timer to cancel query if table is
     * locked by any other operation
     *
     *
     * @param query the query
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */

    public void execNonSelectForTimeout(final String query)
            throws DatabaseOperationException, DatabaseCriticalException {
        ExecuteTimerForNonSelect exec = null;
        long delay = 24 * 60 * 60 * DELAY_MILLI; // 1 day in ms

        PreparedStatement stmtForTimeout = null;
        try {
            // Temp fix. Need to find proper fix.
            exec = new ExecuteTimerForNonSelect(this, delay);
            exec.start();
            stmtForTimeout = dbConnection.prepareStatement(query);
            registerNoticeListner(stmtForTimeout, GlobaMessageQueueUtil.getInstance().getMessageQueue());
            MPPDBIDELoggerUtility.perf("EXECUTION", ILogger.PERF_EXECUTE_STMT, true);
            stmtForTimeout.execute();
            MPPDBIDELoggerUtility.perf("EXECUTION", ILogger.PERF_EXECUTE_STMT, false);
        } catch (SQLException exe) {
            GaussUtils.handleCriticalException(exe);
            try {
                if (!dbConnection.getAutoCommit()) {
                    dbConnection.rollback();
                }
            } catch (SQLException e1) {
                logAndThrowException(e1);
            }

            if (exec.isQueryCanceled()) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_NONSELECT_CANCELLED));
                throw new DatabaseOperationException(IMessagesConstants.ERR_NONSELECT_CANCELLED);
            }
            logAndThrowException(exe);
        } finally {
            if (null != exec) {
                exec.stop();
            }
            try {
                if (!dbConnection.getAutoCommit()) {
                    dbConnection.commit();
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Statement close failed.", exception);
            }
            try {
                if (null != stmtForTimeout) {
                    stmtForTimeout.close();
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Statement close failed.", exception);
            }
        }
    }

    private void logAndThrowException(SQLException e1) throws DatabaseOperationException {
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED,
                extractErrorCodeAndErrorMsgFromServerError(e1)), e1);
        throw new DatabaseOperationException(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED,
                extractErrorCodeAndErrorMsgFromServerError(e1), e1);
    }

    /**
     * Register Notice listner to read Raise messages and dbms_output
     * results.This check is a testability requirement
     *
     * @param stmt the stmt
     * @param messageQueue the message queue
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void registerNoticeListner(Statement stmt, MessageQueue messageQueue)
            throws DatabaseCriticalException, DatabaseOperationException {
        try {
            if (stmt instanceof BaseStatement) {
                ((BaseStatement) stmt).addNoticeListener(new GaussMppDbNoticeListner(messageQueue));
            }
        }

        catch (SQLException excep) {
            GaussUtils.handleCriticalException(excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_STMT_EXCEPTION, excep);
        }
    }

    /**
     * Close result set.
     *
     * @param rs the rs
     */

    public void closeResultSet(ResultSet rs) {
        if (null == rs) {
            return;
        }

        Statement stmt = null;
        try {
            stmt = rs.getStatement();

        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("Obtaining Statement failure.", exception);
        }
        try {
            rs.close();
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("Statement close failure. Nothing can be done.", exception);
        }
        closeStatement(stmt);
    }

    /**
     * Rollback.
     */
    public void rollback() {
        if (!isConnected()) {
            return;
        }

        try {
            if (!dbConnection.getAutoCommit()) {
                dbConnection.rollback();
                dbConnection.setAutoCommit(true);
            }
        } catch (SQLException e1) {
            MPPDBIDELoggerUtility.error("Error while closing the transcation", e1);
        }
    }

    /**
     * Sets the driver.
     *
     * @param driver the new driver
     */
    public void setDriver(IConnectionDriver driver) {
        this.dbmsDriver = driver;
    }

    /**
     * Gets the driver.
     *
     * @return the driver
     */
    public IConnectionDriver getDriver() {
        return this.dbmsDriver;
    }

    /**
     * Sets the connection.
     *
     * @param connection the new connection
     */
    public void setConnection(Connection connection) {
        this.dbConnection = connection;
    }

    /**
     * Extract error code and error msg from server error.
     *
     * @param eex the e
     * @return the string
     */
    public String extractErrorCodeAndErrorMsgFromServerError(SQLException eex) {
        return this.getDriver().extractErrCodeAdErrMsgFrmServErr(eex);
    }

    /**
     * Gets the show DDL support from server.
     *
     * @return the show DDL support from server
     */
    public boolean getShowDDLSupportFromServer() {
        String count = "0";
        try {
            count = execSelectAndGetFirstVal(SERVER_SUPPORT_DDL_QUERY);
        } catch (MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.error("error while checking show DDL server support", exception);
            return false;
        }
        if (MPPDBIDELoggerUtility.isDebugEnabled()) {
            MPPDBIDELoggerUtility
                    .debug("server does " + ("0".equals(count) ? "not" : "") + " support fetch DDL for tables");
        }

        return !"0".equals(count);
    }

    /**
     * Checks if is OLAP connection.
     *
     * @return true, if is OLAP connection
     * @Title: isOLAPConnection
     * @Description: check connection whether is belonged to OLAP
     */
    public boolean isOLAPConnection() {
        String driverName = this.getDriver().getDriverName();
        return (driverName.contains(MPPDBIDEConstants.GAUSS200V1R5DRIVER)
                || driverName.contains(MPPDBIDEConstants.GAUSS200V1R6DRIVER)
                || driverName.contains(MPPDBIDEConstants.GAUSS200V1R7DRIVER)) ? true : false;
    }

}
