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

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.QueryResultType;
import org.opengauss.mppdbide.utils.ResultSetDatatypeMapping;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.ILogger;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.MessageQueue;

/**
 * Title: class Description: The Class StmtExecutor. 
 *
 * @since 3.0.0
 */
public class StmtExecutor {
    private static final String CURSOR_QUERY_PREPEND = "CURSOR %s NO SCROLL FOR %s";

    private static final String FETCH_QUERY = "FETCH FORWARD %d FROM %s";

    private static String cursorName = null;

    private static ArrayList<Object> outResultList;

    private int nRowsAffected;

    private int colCount;

    private ResultSetMetaData rsMetaData;

    private ResultSet rs;

    private PreparedStatement preparedStmt;

    private Statement stmt;

    private CallableStatement calStmt;

    private String strQry;

    private QueryResultType qryResultType;

    private DBConnection connection;

    private boolean isLastRecordFetched = false;

    private int fetchCount = 0;  

    private int regOutParameter = 0;

    private boolean isCursorResultSetType;

    /**
     * Gets the query.
     *
     * @return the query
     */
    public String getQuery() {
        return strQry;
    }

    /**
     * Sets the fetch count.
     *
     * @param fetchCount the new fetch count
     */
    public void setFetchCount(int fetchCount) {
        this.fetchCount = fetchCount;
    }

    /**
     * sets the list of out parameter result set
     * 
     * @param outResultList the out param result list
     */
    public void setOutResultList(ArrayList<Object> outResultList) {
        this.outResultList = outResultList;
    }

    /**
     * get the list of out parameter result set
     * 
     * @return outResultList the out param result list
     */
    public static ArrayList<Object> getOutResultList() {
        return outResultList;
    }

    /**
     * return true if Callable statement executed
     * 
     * @return return true is callable statement executed
     */
    public static boolean isCallableStmtExecuted() {
        return (outResultList != null);
    }

    /**
     * get the no of registered out parameter
     * 
     * @return regOutParameter the no of reg out parameter
     */
    public int getRegOutParameter() {
        return this.regOutParameter;
    }

    /**
     * Instantiates a new stmt executor.
     *
     * @param qry the qry
     * @param con the con
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */

    public StmtExecutor(String qry, DBConnection con)
            throws DatabaseCriticalException, DatabaseOperationException {
        strQry = qry;
        try {
            this.connection = con;
            if (!this.connection.isOLAPConnection() && (qry != null && qry.endsWith(";"))) {
                qry = qry.substring(0, qry.length() - 1);
            }
            if (isCALLOrEXECStatement(qry)) {
                calStmt = connection.getConnection().prepareCall(qry);
            } else {
                preparedStmt = connection.getConnection().prepareStatement(qry);
            }
        } catch (SQLException excep) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_STMT_EXCEPTION),
                    excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_STMT_EXCEPTION, excep);
        }
    }

    private boolean isCALLOrEXECStatement(String qry) {
        return null != qry && (qry.toLowerCase(Locale.ENGLISH).startsWith("exec")
                || qry.toLowerCase(Locale.ENGLISH).startsWith("call")) && qry.contains("?");
    }

    /**
     * Close result set.
     */
    public void closeResultSet() {
        try {
            if (null != rs) {
                rs.close();
            }
        } catch (SQLException excep) {
            MPPDBIDELoggerUtility.error("ADAPTER: resultset close returned exception");
        }
    }

    /**
     * Close statement.
     */
    public void closeStatement() {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (null != calStmt) {
                calStmt.close();
            }
            if (null != preparedStmt) {
                preparedStmt.close();
            }
        } catch (SQLException excep) {
            MPPDBIDELoggerUtility.error("ADAPTER: statement close returned exception");
        }
    }

    /**
     * Commit connection.
     */
    public void commitConnection() {
        try {
            if (!connection.getConnection().getAutoCommit()) {

                connection.getConnection().commit();
            }
            connection.getConnection().setAutoCommit(true);
        } catch (SQLException excep) {
            MPPDBIDELoggerUtility.error("ADAPTER: statement close returned exception");
        }
    }

    /**
     * Rollback.
     */
    public void rollback() {
        try {
            connection.getConnection().rollback();
            if (!connection.getConnection().getAutoCommit()) {
                connection.getConnection().setAutoCommit(true);
            }
        } catch (SQLException excep) {
            MPPDBIDELoggerUtility.error("ADAPTER: statement close returned exception");
        }
    }

    /**
     * Gets the colum count.
     *
     * @return the colum count
     */
    public int getColumCount() {
        return colCount;
    }

    /**
     * Checks if is query DML.
     *
     * @return true, if is query DML
     */
    private boolean isQueryDML() {
        String[] dmlList = {"INSERT ", "UPDATE ", "DELETE "};
        String tempQuery = strQry;

        tempQuery = tempQuery.trim();

        tempQuery = tempQuery.toUpperCase(Locale.ENGLISH);

        for (int index = 0; index < dmlList.length; index++) {
            if (tempQuery.startsWith(dmlList[index])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Execute.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */

    public void execute() throws DatabaseOperationException, DatabaseCriticalException {
        boolean retType = false;
        try {
            MPPDBIDELoggerUtility.info("ADAPTER: Sending user query execution request to server");
            MPPDBIDELoggerUtility.perf("EXECUTION", ILogger.PERF_EXECUTE_STMT, true);
            if (preparedStmt != null) {
                preparedStmt.setFetchSize(fetchCount);
                retType = preparedStmt.execute();
                setOutResultList(null);
            } else if (stmt != null) {
                retType = handleStmtExecution();
            } else {
                retType = handleCallStmtExecution();
            }

            MPPDBIDELoggerUtility.perf("EXECUTION", ILogger.PERF_EXECUTE_STMT, false);

            /*
             * This code is to get the last result set of the query, incase of
             * multiple sql statements are executed in one shot. Later point of
             * time, consider this for implementation
             */
            handleQueryResultType(retType);
            MPPDBIDELoggerUtility.info("ADAPTER: User query Executed successfully");
        } catch (SQLException excep) {
            closeStatement();
            GaussUtils.handleCriticalException(excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_BL_EXECUTE_FAILED,
                    connection.extractErrorCodeAndErrorMsgFromServerError(excep), excep);
        } catch (OutOfMemoryError excep) { 
            closeStatement();
            MPPDBIDELoggerUtility.error("OutOfMemoryError ocurred");
            throw new DatabaseCriticalException(IMessagesConstants.ERR_MSG_OUT_OF_MEMORY_ERROR_OCCURRED, excep);
        }
    }

    /**
     * handle type of query result
     * 
     * @param retType the return type
     * @throws SQLException
     */
    private void handleQueryResultType(boolean retType) throws SQLException {
        // false means update query
        if (!retType) {
            qryResultType = QueryResultType.RESULTTYPE_DML;
            if (preparedStmt != null) {
                nRowsAffected = preparedStmt.getUpdateCount();

            } else if (stmt != null) {
                nRowsAffected = stmt.getUpdateCount();
            } else {
                nRowsAffected = calStmt.getUpdateCount();
            }

            // If number of rows affected is 0, then it can be either DDL or
            // DML.
            if (nRowsAffected == 0) {
                if (!isQueryDML()) {
                    qryResultType = QueryResultType.RESULTTYPE_OTHERS;
                }
            }
        } else {
            qryResultType = QueryResultType.RESULTTYPE_RESULTSET;
            if (preparedStmt != null) {
                rs = preparedStmt.getResultSet();
            }
            if (stmt != null) {
                rs = stmt.getResultSet();
            }
            if (rs != null) {
                rsMetaData = rs.getMetaData();
                colCount = rsMetaData.getColumnCount();
            }
        }
    }

    /**
     * handle callable statement execution
     * 
     * @param outResultList the out parameter result list
     * @return return true is return type is result set
     * @throws SQLException
     */
    private boolean handleCallStmtExecution() throws SQLException {
        boolean retType = false;
        ArrayList<Object> outResultLists = new ArrayList<Object>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        calStmt.setFetchSize(fetchCount);
        calStmt.execute();
        if (!this.connection.getIsInOutValueExists()) {
            return false;
        }
        for (int i = 1; i <= getRegOutParameter(); i++) {
            Object rsObj = calStmt.getObject(i);
            outResultLists.add(rsObj);
            if (rsObj != null) {
                if (rsObj instanceof ResultSet) {
                    rs = (ResultSet) rsObj;
                    isCursorResultSetType = true;
                }
                retType = true;
            }
        }
        setOutResultList(outResultLists);
        return retType;
    }

    /**
     * handle Statement execution
     * 
     * @return return true if statement execution
     * @throws SQLException
     * @throws DatabaseCriticalException
     * @throws DatabaseOperationException
     */
    private boolean handleStmtExecution() throws SQLException, DatabaseCriticalException, DatabaseOperationException {
        stmt.setFetchSize(fetchCount);
        startTxn();
        stmt.execute(getCursorQuery());
        rs = stmt.executeQuery(getFetchQuery(1000));
        setUniqCursorName(null);
        return true;
    }

    /**
     * start transaction
     * 
     * @throws DatabaseCriticalException
     * @throws DatabaseOperationException
     */
    private void startTxn() throws DatabaseCriticalException, DatabaseOperationException {
        try {
            if (connection.getConnection().getAutoCommit()) {
                // if already inside a transaction, no need to reset.
                connection.getConnection().setAutoCommit(false);
            }
        } catch (SQLException ex) {
            GaussUtils.handleCriticalException(ex);
            throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, ex);
        }
    }

    /**
     * fetch query
     * 
     * @param currentFetchSize the current fetch size
     * @return the fetch query
     */
    private String getFetchQuery(int currentFetchSize) {
        return String.format(Locale.ENGLISH, FETCH_QUERY, currentFetchSize, getUniqCursorName());
    }

    /**
     * get cursor query
     * 
     * @return the cursor query
     */
    private String getCursorQuery() {
        return String.format(Locale.ENGLISH, CURSOR_QUERY_PREPEND, getUniqCursorName(), strQry);
    }

    /**
     * get cursor name
     * 
     * @return cursorName the name of cursor
     */
    public String getUniqCursorName() {
        if (cursorName == null) {
            String timeStamp = new SimpleDateFormat("HHmmssSSS").format(new Date());
            cursorName = "__DS_QRY_CRSR_" + timeStamp + "__";
        }
        return cursorName;
    }

    /**
     * sets cursor name
     * 
     * @param lcursorName the cursor name
     */
    public static void setUniqCursorName(String lcursorName) {
        cursorName = lcursorName;
    }

    /**
     * return true if result type is Cursor resultSet
     * 
     * @return isCursorResultSetType true if cursor type resultset
     */
    public boolean getCursorResultSetType() {
        return this.isCursorResultSetType;
    }

    /**
     * Gets the column name.
     *
     * @param col the col
     * @return the column name
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String getColumnName(int col) throws DatabaseOperationException, DatabaseCriticalException {
        String colName = null;
        try {
            if (rsMetaData != null) {
                colName = rsMetaData.getColumnLabel(col);
            }
        } catch (SQLException excep) {
            GaussUtils.handleCriticalException(excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, excep);
        }
        return colName;
    }

    /**
     * Gets the next object record batch.
     *
     * @param maxFetchCount the max fetch count
     * @return the next object record batch
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public Object[][] getNextObjectRecordBatch(int maxFetchCount)
            throws DatabaseOperationException, DatabaseCriticalException {
        ArrayList<Object[]> rows = new ArrayList<>();
        int columnCount = getColumCount();
        int fetchIndex = 0;
        Object[] row;

        try {
            // Debug scenario will pass maxFetchCount as -1. If so, fetch
            // everything.
            while (maxFetchCount < 0 || fetchIndex < maxFetchCount) {
                if (null != rs && rs.next()) {
                    row = new Object[columnCount];
                    for (int index = 1; index <= columnCount; index++) {
                        row[index - 1] = ResultSetDatatypeMapping.getReadColumnValueObject(rs, index);
                    }

                    rows.add(row);
                    fetchIndex++;
                } else {
                    break;
                }
            }
        } catch (SQLException excep) {
            GaussUtils.handleCriticalException(excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, excep);
        } catch (OutOfMemoryError excep) {
            outOfMemoryErrorHandle(excep);
        }
        if (rows.size() == 0) {
            this.isLastRecordFetched = true;
        }
        return (Object[][]) rows.toArray(new Object[rows.size()][columnCount]);
    }

    /**
     * Gets the next object record batch.
     * 
     * @param maxFetchCount the max fetch count
     * @param columnCount no of column
     * @param isFuncProcResultFlow is function or procedure flow
     * @param isInputParaVisited is input parameter visited
     * @return the next object record batch
     * @throws DatabaseOperationException
     * @throws DatabaseCriticalException
     */
    public Object[][] getNextObjectRecordBatch(int maxFetchCount, int columnCount, boolean isInputParaVisited)
            throws DatabaseOperationException, DatabaseCriticalException {
        ArrayList<Object[]> valueRows = new ArrayList<>();
        try {
            if (!getCursorResultSetType()) {
                if (null != rs && rs.next()) {
                    getFuncProcResultValue(rs, valueRows,
                            new GetFuncProcResultValueParam(columnCount, calStmt != null, false));
                }
            } else {
                getFuncProcResultValue(rs, valueRows,
                        new GetFuncProcResultValueParam(columnCount, calStmt != null, isInputParaVisited));
            }
        } catch (SQLException exe) {
            GaussUtils.handleCriticalException(exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exe);
        } catch (OutOfMemoryError exe) {
            outOfMemoryErrorHandle(exe);
        }
        if (valueRows.size() == 0) {
            this.isLastRecordFetched = true;
        }
        return (Object[][]) valueRows.toArray(new Object[valueRows.size()][columnCount]);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class GetFuncProcResultValueParam.
     * 
     */
    public static class GetFuncProcResultValueParam {
        private int columnCount;

        private boolean isCallableStmt;

        private boolean isInputParaVisited;

        public GetFuncProcResultValueParam(int columnCount, boolean isCallableStmt, boolean isInputParaVisited) {
            this.columnCount = columnCount;
            this.isCallableStmt = isCallableStmt;
            this.isInputParaVisited = isInputParaVisited;
        }

        public int getColumnCount() {
            return columnCount;
        }

        public void setColumnCount(int columnCount) {
            this.columnCount = columnCount;
        }

        public boolean isCallableStmt() {
            return isCallableStmt;
        }

        public void setCallableStmt(boolean isCallableStmt) {
            this.isCallableStmt = isCallableStmt;
        }

        public boolean isInputParaVisited() {
            return isInputParaVisited;
        }

        public void setInputParaVisited(boolean isInputParaVisited) {
            this.isInputParaVisited = isInputParaVisited;
        }
    }

    private void getFuncProcResultValue(ResultSet rs, ArrayList<Object[]> rows, GetFuncProcResultValueParam paramObj)
            throws NumberFormatException, DatabaseOperationException, SQLException {
        Object[] row = new Object[paramObj.getColumnCount()];
        int colIndex;
        boolean isShowCursorPopup = getShowCursorPopup(rs);
        for (colIndex = 1; colIndex < paramObj.getColumnCount(); colIndex++) {
            row[colIndex - 1] = ResultSetDatatypeMapping.getFuncProcColObjectExceptValue(rs, colIndex,
                    getCursorResultSetType());
        }
        if (isShowCursorPopup) {
            row[colIndex - 1] = ResultSetDatatypeMapping.getReadColumnValueObject(rs,
                    rs.getMetaData().getColumnCount());
        } else if (getCursorResultSetType()) {
            row[colIndex - 1] = ResultSetDatatypeMapping.convertResultSetToObject(rs, rs.getMetaData().getColumnCount(),
                    true, false);
        } else {
            row[colIndex - 1] = ResultSetDatatypeMapping.getReadColumnValueObject(rs,
                    rs.getMetaData().getColumnCount());
        }
        rows.add(row);
    }

    private boolean getShowCursorPopup(ResultSet rs2) throws SQLException {
        if (MPPDBIDEConstants.REF_CURSOR.equals(rs.getMetaData().getColumnTypeName(rs.getMetaData().getColumnCount()))
                || MPPDBIDEConstants.RECORD
                        .equals(rs.getMetaData().getColumnTypeName(rs.getMetaData().getColumnCount()))) {
            return true;
        }
        return false;
    }

    /**
     * Out of memory error handle.
     *
     * @param exe the e
     * @throws DatabaseCriticalException the database critical exception
     */
    private void outOfMemoryErrorHandle(OutOfMemoryError exe) throws DatabaseCriticalException {
        try {
            stmt.close();
        } catch (SQLException excep1) {
            MPPDBIDELoggerUtility
                    .error("After OutOfMemoryError unable" + " to close statement. Will throw critical exception.");
        }
        MPPDBIDELoggerUtility
                .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_MSG_OUT_OF_MEMORY_ERROR_OCCURRED), exe);
        throw new DatabaseCriticalException(IMessagesConstants.ERR_MSG_OUT_OF_MEMORY_ERROR_OCCURRED, exe);
    }

    /**
     * Gets the next record batch.
     *
     * @param count the count
     * @return the next record batch
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String[][] getNextRecordBatch(int count) throws DatabaseOperationException, DatabaseCriticalException {
        int fetchcnt = 0;
        ArrayList<String[]> rowset = new ArrayList<String[]>(MPPDBIDEConstants.RECORD_ARRAY_SIZE);

        try {
            String[] row = null;

            while ((-1 == count || fetchcnt < count) && rs.next()) {
                row = new String[colCount + 1];
                row[0] = String.valueOf(fetchcnt + 1);
                for (int index = 1; index <= colCount; index++) {
                    row[index] = ResultSetDatatypeMapping.convertStringToValue(rs, index);
                }

                rowset.add(row);
                fetchcnt++;
            }
        } catch (SQLException excep) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, excep);
        } catch (OutOfMemoryError excep) {
            outOfMemoryErrorHandle(excep);
        }

        return (String[][]) rowset.toArray(new String[rowset.size()][colCount]);
    }

    /**
     * Gets the result set.
     *
     * @return the result set
     */
    public ResultSet getResultSet() {
        return rs;
    }

    /**
     * Gets the rows affected.
     *
     * @return the rows affected
     */
    public int getRowsAffected() {
        return nRowsAffected;
    }

    /**
     * Gets the result type.
     *
     * @return the result type
     */
    public QueryResultType getResultType() {
        return qryResultType;
    }

    /**
     * Checks if is last record.
     *
     * @return true, if is last record
     * @throws DatabaseOperationException the database operation exception
     */
    public boolean isLastRecord() throws DatabaseOperationException {

        try {
            if (this.isLastRecordFetched) {
                return true;
            }
            if (rs != null) {
                return rs.isLast() || rs.isAfterLast();
            }
            return true;
        } catch (SQLException excep) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, excep);
        }
    }

    /**
     * Register notice listner.
     *
     * @param messageQueue the message queue
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */

    public void registerNoticeListner(MessageQueue messageQueue)
            throws DatabaseCriticalException, DatabaseOperationException {
        if (null != stmt) {
            connection.registerNoticeListner(stmt, messageQueue);
        } else if (null != calStmt) {
            connection.registerNoticeListner(calStmt, messageQueue);
        } else if (null != preparedStmt) {
            connection.registerNoticeListner(preparedStmt, messageQueue);
        }
    }

    /**
     * Gets the column data type.
     *
     * @param index the index
     * @return the column data type
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */

    public int getColumnDataTypeStmt(int index) throws DatabaseOperationException, DatabaseCriticalException {
        try {
            if (rs != null && rs.getMetaData() != null) {
                return this.rs.getMetaData().getColumnType(index);
            }
            return 0;
        } catch (SQLException excep) {
            GaussUtils.handleCriticalException(excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, excep);
        }
    }

    /**
     * Gets the column type name.
     *
     * @param index the index
     * @return the column type name
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String getColumnTypeName(int index) throws DatabaseOperationException, DatabaseCriticalException {
        try {
            if (rs != null && rs.getMetaData() != null) {
                return this.rs.getMetaData().getColumnTypeName(index);
            }
            return "";
        } catch (SQLException excep) {
            // this main MOT error
            return "";
        }
    }

    /**
     * Gets the precision.
     *
     * @param index the index
     * @return the precision
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public int getPrecision(int index) throws DatabaseOperationException, DatabaseCriticalException {
        try {
            if (rs != null && rs.getMetaData() != null) {
                return this.rs.getMetaData().getPrecision(index);
            }
            return 0;
        } catch (SQLException excep) {
            GaussUtils.handleCriticalException(excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, excep);
        }
    }

    /**
     * Gets the scale.
     *
     * @param index the index
     * @return the scale
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public int getScale(int index) throws DatabaseOperationException, DatabaseCriticalException {
        try {
            if (rs != null && rs.getMetaData() != null) {
                return this.rs.getMetaData().getScale(index);
            }
            return 0;
        } catch (SQLException excep) {
            GaussUtils.handleCriticalException(excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, excep);
        }
    }

    /**
     * Gets the max length.
     *
     * @param index the index
     * @return the max length
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public int getMaxLength(int index) throws DatabaseOperationException, DatabaseCriticalException {
        try {
            if (rs != null && rs.getMetaData() != null) {
                return this.rs.getMetaData().getColumnDisplaySize(index);
            }
            return 0;
        } catch (SQLException excep) {
            GaussUtils.handleCriticalException(excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, excep);
        }
    }

    /**
     * Gets the table name.
     *
     * @param col the col
     * @return the table name
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @Title: getTableName
     * @Description: (use a sentence to describe the usage of method)
     */
    public String getTableName(int col) throws DatabaseOperationException, DatabaseCriticalException {
        String tableName = null;
        try {
            if (rsMetaData != null) {
                tableName = rsMetaData.getTableName(col);
            }
        } catch (SQLException excep) {
            GaussUtils.handleCriticalException(excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, excep);
        }

        return tableName;
    }

    /**
     * Gets the schema name.
     *
     * @param col the col
     * @return the schema name
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @Title: getTableName
     * @Description: (use a sentence to describe the usage of method)
     */
    public String getSchemaName(int col) throws DatabaseOperationException, DatabaseCriticalException {
        String schemaName = "";
        try {
            if (rsMetaData != null) {
                schemaName = rsMetaData.getSchemaName(col);
            }
        } catch (SQLException excep) {
            GaussUtils.handleCriticalException(excep);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, excep);
        }

        return schemaName;
    }

    public CallableStatement getCalStmt() {
        return calStmt;
    }

}
