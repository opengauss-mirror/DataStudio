/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.adapter.gauss.StmtExecutor;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.JSQLParserUtils;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.MessageQueue;

/**
 * 
 * Title: class
 * 
 * Description: The Class DatabaseUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface DatabaseUtils {
    /**
     * Serial data type oid
     */
    static final int SERIAL_DATA_TYPE_OID = -1;

    /**
     * Check cancel status and abort.
     *
     * @param cancelStatus the cancel status
     * @param db the db
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    static void checkCancelStatusAndAbort(IJobCancelStatus cancelStatus, Database db)
            throws DatabaseOperationException, DatabaseCriticalException {
        if (cancelStatus.getCancel() && db != null) {
            db.getConnectionManager().cancelAllConnectionQueries();
            DBConnProfCache.getInstance().destroyConnection(db);

            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));
            throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL_MSG);
        }
    }

    /**
     * Gets the deadline info.
     *
     * @param preferenceCount the preference count
     * @param db the db
     * @return the deadline info
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    static String getDeadlineInfo(int preferenceCount, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        String deadlineValue = null;
        ResultSet rs = null;
        String qry = "select intervaltonum(gs_password_deadline()) as DEADLINE;";

        rs = db.getConnectionManager().getObjBrowserConn().execSelectToExportCSV(qry, preferenceCount);

        try {
            if (rs.next()) {
                deadlineValue = rs.getString("DEADLINE");
            }
        } catch (SQLException exp) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        }
        // Next Time Transaction needs to be handled.28/06/2016
        finally {
            db.getConnectionManager().closeRSOnObjBrowserConn(rs);
        }
        return deadlineValue;
    }

    /**
     * Gets the notify info.
     *
     * @param preferenceCount the preference count
     * @param database the db
     * @return the notify info
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    static int getNotifyInfo(int preferenceCount, Database database)
            throws DatabaseCriticalException, DatabaseOperationException {
        int notifyValue = 0;
        ResultSet resultSet = null;
        String qry = "select * from gs_password_notifytime() as NOTIFYTIME;";
        resultSet = database.getConnectionManager().getObjBrowserConn().execSelectToExportCSV(qry, preferenceCount);
        try {
            if (resultSet.next()) {
                notifyValue = resultSet.getInt("NOTIFYTIME");
            }
        } catch (SQLException excp) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    excp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, excp);
        }
        // Next Time Transaction needs to be handled.28/06/2016
        finally {
            database.getConnectionManager().closeRSOnObjBrowserConn(resultSet);
        }
        return notifyValue;
    }

    /**
     * Gets the server encoding.
     *
     * @param dbconn the dbconn
     * @return the server encoding
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    static String getServerEncoding(DBConnection dbconn) throws DatabaseCriticalException, DatabaseOperationException {
        String encodingQry = "show server_encoding";
        ResultSet rs = dbconn.execSelectAndReturnRs(encodingQry);
        try {
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_STMT_EXCEPTION, exp);
        } finally {
            dbconn.closeResultSet(rs);
        }

        return "";
    }

    /**
     * Gets the character list.
     *
     * @param db the db
     * @return the character list
     */
    static List<Character> getCharacterList(Database db) {
        List<Character> charList1 = new ArrayList<Character>(25);

        charList1.add('.');
        charList1.add('&');
        charList1.add('$');
        charList1.add('*');
        charList1.add('#');
        charList1.add('@');
        charList1.add('+');
        charList1.add('-');
        charList1.add('/');
        charList1.add('<');
        charList1.add('>');
        charList1.add('=');
        charList1.add('~');
        charList1.add('!');
        charList1.add('%');
        charList1.add('^');
        charList1.add('|');
        charList1.add('`');
        charList1.add('?');
        charList1.add('(');
        charList1.add('[');
        charList1.add(',');

        return charList1;
    }

    /**
     * Execute on query with materializer.
     *
     * @param query the query
     * @param fetchCount the fetch count
     * @param conn the conn
     * @param messageQueue the message queue
     * @param materializer the materializer
     * @return the object
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    static Object executeOnQueryWithMaterializer(String query, int fetchCount, DBConnection conn,
            MessageQueue messageQueue, IQueryMaterializer materializer)
            throws DatabaseCriticalException, DatabaseOperationException {
        Connection connection = conn.getConnection();
        boolean isChanged = false;
        StmtExecutor stmt = null;
        boolean isSelectQuery = false;
        boolean isCallableStmt = false;

        try {
            if (connection.isClosed()) {
                return null;
            }

            // On Auto Commit ON fetch size is ignored and it will fetch all
            // data at a time and may cause out of memory .So we are setting
            // auto commit false for select
            // queries .Auto commit will turned ON in finally block if required.
            if (query.toLowerCase(Locale.ENGLISH).startsWith("select") && !JSQLParserUtils.isCopyQuery(query)) {
                if (connection.getAutoCommit()) {
                    isSelectQuery = true;
                    IExecTimer timer = new ExecTimer("Pre AC False");
                    timer.start();
                    isChanged = true;
                    connection.setAutoCommit(false);
                    timer.stop();
                }
            }

            stmt = executeQuery(query, conn, messageQueue);

            if (stmt.getCalStmt() != null) {
                isCallableStmt = true;
            }

            QueryResult queryResult = new QueryResult(stmt, conn, false);
            if (null != materializer) {
                // Better to materialize the query result before transaction
                // rollback.
                IExecTimer timer1 = new ExecTimer("Materilizing");
                timer1.start();

                materializer.materializeQueryResult(queryResult, isCallableStmt);
                timer1.stop();
                return materializer.getMaterializedQueryResult();
            }

            // Not expected to come here as this has a single caller.
            return queryResult;
        } catch (SQLException objSQL) {
            handleSQLException(connection, objSQL);

            throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, objSQL);
        } finally {
            executeOnQueryWithMaterializerCleanUp(connection, isChanged, stmt, isSelectQuery);
        }
    }

    /**
     * handles the SQLException
     * 
     * @param connection the connection
     * @param objSQL the sql object
     */
    static void handleSQLException(Connection connection, SQLException objSQL) {
        try {
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (SQLException objExp) {
            MPPDBIDELoggerUtility.error("Database : Database operation exeception", objExp);
        }
        MPPDBIDELoggerUtility.error("Database : Database operation exeception", objSQL);
    }

    /**
     * execute the Query
     * 
     * @param query the query
     * @param conn the connection
     * @param messageQueue the message queue
     * @return StmtExecutor the statement executor
     * @throws DatabaseCriticalException the DatabaseCriticalException
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws SQLException the SQLException
     */
    static StmtExecutor executeQuery(String query, DBConnection conn, MessageQueue messageQueue)
            throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        StmtExecutor stmt;
        IExecTimer timer = new ExecTimer("Executing");
        timer.start();

        stmt = new StmtExecutor(query, conn);
        stmt.setFetchCount(1000);
        stmt.registerNoticeListner(messageQueue);

        stmt.execute();
        timer.stop();
        return stmt;
    }

    /** 
     * execute statement On Query With Materializer CleanUp 
     * 
     * @param connection the connection
     * @param isChanged the boolean flag
     * @param stmt the statement
     * @param isSelectQuery the boolean flag
     */
    static void executeOnQueryWithMaterializerCleanUp(Connection connection, boolean isChanged, StmtExecutor stmt,
            boolean isSelectQuery) {
        if (isSelectQuery) {
            try {
                connection.rollback();
                if (null != stmt) {
                    stmt.closeResultSet();
                    stmt.closeStatement();
                }
                if (isChanged) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Database operation exeception", exception);
            }
        } else {
            if (stmt != null) {
                stmt.closeStatement();
            }
        }
    }

    /**
     * Execute on sql terminal.
     *
     * @param query the query
     * @param fetchCount the fetch count
     * @param conn the conn
     * @param messageQueue the message queue
     * @return the query result
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    static QueryResult executeOnSqlTerminal(String query, int fetchCount, DBConnection conn, MessageQueue messageQueue)
            throws DatabaseCriticalException, DatabaseOperationException {
        Connection connection = conn.getConnection();
        boolean isChanged = false;

        try {
            if (connection.isClosed()) {
                return null;
            }

            /*
             * On Auto Commit ON fetch size is ignored and it will fetch all
             * data at a time and may cause out of memory .So we are setting
             * auto commit false for select queries .Auto commit will turned ON
             * in finally block if required.
             */
            if (query.toLowerCase(Locale.ENGLISH).startsWith("select")) {
                if (connection.getAutoCommit()) {
                    isChanged = true;
                    connection.setAutoCommit(false);
                }
            }
            StmtExecutor stmt = new StmtExecutor(query, conn);
            int fetchSize = fetchCount < 1 ? 0 : fetchCount;
            stmt.setFetchCount(fetchSize);
            stmt.registerNoticeListner(messageQueue);
            stmt.execute();

            return new QueryResult(stmt);

        } catch (SQLException objSQL) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }

            } catch (SQLException objExp) {
                MPPDBIDELoggerUtility.error("Database operation exeception", objExp);
            }
            MPPDBIDELoggerUtility.error("Database operation exeception", objSQL);

            throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, objSQL);
        } finally {
            if (isChanged) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException exception) {
                    MPPDBIDELoggerUtility.error("Database operation exeception", exception);
                }
            }
        }
    }

    /**
     * Execute on sql terminal and dont return resultset
     *
     * @param query the query
     * @param fetchCount the fetch count
     * @param conn the conn
     * @param messageQueue the message queue
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    static void executeOnSqlTerminalAndReturnNothing(String query, int fetchCount, DBConnection conn,
            MessageQueue messageQueue) throws DatabaseCriticalException, DatabaseOperationException {
        Connection connection = conn.getConnection();
        boolean isChanged = false;
        StmtExecutor statement = null;

        try {
            if (connection.isClosed()) {
                return;
            }

            /*
             * On Auto Commit ON fetch size is ignored and it will fetch all
             * data at a time and may cause out of memory .So we are setting
             * auto commit false for select queries .Auto commit will turned ON
             * in finally block if required.
             */
            if (query.toLowerCase(Locale.ENGLISH).startsWith("select")) {
                if (connection.getAutoCommit()) {
                    isChanged = true;
                    connection.setAutoCommit(false);
                }
            }
            statement = new StmtExecutor(query, conn);
            int fetchSize = fetchCount < 1 ? 0 : fetchCount;
            statement.setFetchCount(fetchSize);
            statement.registerNoticeListner(messageQueue);
            statement.execute();
        } catch (SQLException objSQL) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }

            } catch (SQLException sqlException) {
                MPPDBIDELoggerUtility.error("Database operation exeception", sqlException);
            }
            MPPDBIDELoggerUtility.error("Database operation exeception", objSQL);

            throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, objSQL);
        } finally {
            if (isChanged) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException exception) {
                    MPPDBIDELoggerUtility.error("Database operation exeception", exception);
                }
            }
            if (null != statement) {
                statement.closeResultSet();
                statement.closeStatement();
            }
        }
    }

    /**
     * Gets the notif data from RS.
     *
     * @param rs the rs
     * @return the notif data from RS
     * @throws SQLException the SQL exception
     */
    static NotificationData getNotifDataFromRS(ResultSet rs) throws SQLException {
        NotificationData notifData;
        notifData = new NotificationData();

        Timestamp timestamp = rs.getTimestamp(1);
        if (timestamp != null) {
            String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(timestamp);
            notifData.setTime(time);
            String client = rs.getString(2);
            notifData.setClientInfo(client);
        }
        return notifData;
    }

    /**
     * Check exception for no R sor no access.
     *
     * @param hasNext the has next
     * @param privilegeFlag the privilege flag
     * @throws DatabaseOperationException the database operation exception
     */
    static void checkExceptionForNoRSorNoAccess(boolean hasNext, boolean privilegeFlag)
            throws DatabaseOperationException {
        if (!hasNext) {
            if (privilegeFlag) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE));
                throw new DatabaseOperationException(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE);
            } else {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED));
                throw new DatabaseOperationException(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED);
            }
        }
    }

    /**
     * Check exception for no access no renameflow.
     *
     * @param isRenameFlow the is rename flow
     * @param privilegeFlag the privilege flag
     * @throws DatabaseOperationException the database operation exception
     */
    static void checkExceptionForNoAccessNoRenameflow(boolean isRenameFlow, boolean privilegeFlag)
            throws DatabaseOperationException {
        if (privilegeFlag) {
            if (!isRenameFlow) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE));
                throw new DatabaseOperationException(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE);
            }
        } else {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED));
            throw new DatabaseOperationException(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED);
        }
    }

    /**
     * Gets the ORC data types to be displayed.
     *
     * @return the ORC data types to be displayed
     */
    static String[][] getORCDataTypesToBeDisplayed() {
        String[][] orcDataTypes = {{"char", "char"}, {"char", "character"}, {"char", "nchar"}, {"varchar", "varchar"},
            {"varchar", "character varying"}, {"nvarchar2", null}, {"varchar2(n)", null}, {"clob", null},
            {"text", null}, {"int1", "tinyint"}, {"int2", "smallint"}, {"int4", "int"}, {"int8", "bigint"},
            {"date", null}, {"money", null}, {"numeric", null}, {"numeric", "decimal"}, {"float8", "double precision"},
            {"float4", "real"}, {"float", "binary double"}, {"float4", null}, {"float8", null}, {"interval", null},
            {"time", "time without time zone"}, {"timestamp", "timestamp without time zone"}, {"bool", null},
            {"timetz", "time with time zone"}, {"timestamptz", "timestamp with time zone"}, {"smalldatetime", null},
            {"oid", null}};
        return orcDataTypes;
    }

    /**
     * Gets the default datatype list.
     *
     * @return the default datatype list
     */
    static String[][] getdefaultDatatypeList() {
        String[][] defaultDatatype = {{"char", null}, {"varchar", null}, {"text", null}, {"int4", "integer"},
            {"int2", "smallint"}, {"int8", "bigint"}, {"date", null}, {"money", null}, {"numeric", null},
            {"numeric", "decimal"}, {"float8", "double precision"}, {"float4", "real"}, {"interval", null},
            {"time", "time without time zone"}, {"timestamp", "timestamp without time zone"}, {"bool", "boolean"},
            {"bit", null}, {"box", null}, {"bytea", null}, {"cidr", null}, {"circle", null}, {"inet", null},
            {"lseg", null}, {"macaddr", null}, {"path", null}, {"point", null}, {"polygon", null},
            {"timetz", "time with time zone"}, {"timestamptz", "timestamp with time zone"}, {"tsquery", null},
            {"tsvector", null}, {"txid_snapshot", null}, {"uuid", null}, {"varbit", null}, {"xml", null},
            {"clob", null}, {"blob", null}};
        return defaultDatatype;
    }

    /**
     * Gets the serial datatype list.
     *
     * @return String[] the serial datatype list
     */
    static String[] getSerialDatatypeList() {
        String[] serialDatatype = {"smallserial", "serial", "bigserial"};
        return serialDatatype;
    }

    /**
     * Gets the all DB list in server.
     *
     * @param db the db
     * @return the all DB list in server
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    static ArrayList<Database> getAllDBListInServer(Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        int oid = 0;
        String name = null;
        Database newDb = null;
        ArrayList<Database> dbList = new ArrayList<Database>(4);
        String qry = "select oid, datname from pg_database where datistemplate='f'";
        if (db.getPrivilegeFlag()) {
            qry += " and has_database_privilege(datname, 'CONNECT')";
        }
        qry += " order by datname;";

        ResultSet rs = null;
        try {
            rs = db.getConnectionManager().execSelectAndReturnRsOnObjBrowserConn(qry);
            boolean hasNext = rs.next();
            while (hasNext) {
                oid = rs.getInt(1);
                name = rs.getString(2);

                newDb = new Database(db.getServer(), oid, name);
                newDb.setOid(oid);

                dbList.add(newDb);
                hasNext = rs.next();
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
            db.getConnectionManager().closeRSOnObjBrowserConn(rs);
        }

        return dbList;
    }

    /**
     * Gets the debug objects.
     *
     * @param db the db
     * @param debugObjectId the debug object id
     * @return the debug objects
     */
    static DebugObjects getDebugObjects(Database db, long debugObjectId) {
        IDebugObject obj = null;
        ArrayList<Namespace> namespaceSortedList = db.getAllNameSpaces();

        Iterator<Namespace> itrNamespace = namespaceSortedList.iterator();
        boolean hasMoreElements = itrNamespace.hasNext();
        Namespace namespace = null;
        while (hasMoreElements) {
            namespace = itrNamespace.next();
            obj = namespace.getDebugObjectById(debugObjectId);
            if (null != obj) {
                return (DebugObjects) obj;
            }
            hasMoreElements = itrNamespace.hasNext();
        }
        return null;
    }

}
