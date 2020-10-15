/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.StmtExecutor;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.QueryResultType;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: class Description: The Class QueryResult. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class QueryResult implements IQueryResult {

    private StmtExecutor resultStmt;

    private DBConnection connection;

    private boolean fetchCommentFlag = false;

    /**
     * Instantiates a new query result.
     *
     * @param resultStmt the result stmt
     */
    public QueryResult(StmtExecutor resultStmt) {
        this.resultStmt = resultStmt;
    }

    /**
     * Instantiates a new query result.
     *
     * @param resultStmt the result stmt
     * @param connection the connection
     * @param fetchCommentFlag the fetch comment flag
     */
    public QueryResult(StmtExecutor resultStmt, DBConnection connection, boolean fetchCommentFlag) {
        this.resultStmt = resultStmt;
        this.connection = connection;
        this.fetchCommentFlag = fetchCommentFlag;
    }

    /**
     * Checks if is end of records reached.
     *
     * @return true, if is end of records reached
     * com.huawei.mppdbide.bl.serverdatacache.IQueryResult#isEndOfRecordsReached
     */

    @Override
    public boolean isEndOfRecordsReached() {
        try {
            return resultStmt.isLastRecord();
        } catch (DatabaseOperationException e) {
            // Ignore the exception as its harmless.
            return false;
        }
    }

    /**
     * Close stament.
     * com.huawei.mppdbide.bl.serverdatacache.IQueryResult#closeStament()
     */

    @Override
    public void closeStament() {
        if (resultStmt != null) {
            resultStmt.closeResultSet();
            resultStmt.closeStatement();
            resultStmt = null;
        }
    }

    /**
     * Rollback. com.huawei.mppdbide.bl.serverdatacache.IQueryResult#rollback()
     */

    @Override
    public void rollback() {
        if (resultStmt != null) {
            resultStmt.rollback();
        }
    }

    /**
     * Gets the rows affected.
     *
     * @return the rows affected
     * com.huawei.mppdbide.bl.serverdatacache.IQueryResult#getRowsAffected()
     */

    @Override
    public int getRowsAffected() {
        return resultStmt.getRowsAffected();
    }

    /**
     * Gets the statementExecutor.
     *
     * @return the stmtExecutor
     */

    @Override
    public StmtExecutor getStmtExecutor() {
        return this.resultStmt;
    }

    /**
     * Gets the return type.
     *
     * @return the return type
     * com.huawei.mppdbide.bl.serverdatacache.IQueryResult#getReturnType()
     */

    @Override
    public QueryResultType getReturnType() {
        // DTS2014102808375 start
        if (null == resultStmt) {
            return null;
        }
        // DTS2014102808375 end
        return resultStmt.getResultType();
    }

    /**
     * Gets the column meta data.
     *
     * @return the column meta data
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * com.huawei.mppdbide.bl.serverdatacache.IQueryResult#getColumnMetaData()
     */
    @Override
    public ResultSetColumn[] getColumnMetaData() throws DatabaseOperationException, DatabaseCriticalException {
        Statement statement = null;
        try {
            statement = resultStmt.getResultSet().isClosed() ? null : resultStmt.getResultSet().getStatement();
        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_FETCH_RESULT_SET_COLUMN_COMMENT), exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_FETCH_RESULT_SET_COLUMN_COMMENT);
        }

        Map<String, String> columnComment = new HashMap<>();
        if (statement != null && this.fetchCommentFlag && connection.isOLAPConnection()) {
            columnComment = getColumnCommentOfOLAP(connection, statement);
        }

        int count = resultStmt.getColumCount();
        ResultSetColumn[] metaData = new ResultSetColumn[count];

        for (int index = 0; index < count; index++) {
            metaData[index] = new ResultSetColumn(index + 1);
            metaData[index].collectColumnData(resultStmt, columnComment);
        }
        return metaData;
    }

    /**
     * gets the column header name
     * 
     * @param columnCount the column count
     * @param columnHeaderName the column header name
     * @param isCallableStmt the callable statement flag
     * @param isCursorType the cursor type flag
     * @return ResultSetColumn the result set column
     * @throws DatabaseOperationException
     * @throws DatabaseCriticalException
     */
    public ResultSetColumn[] getColumnHeaderName(int columnCount, List<String> columnHeaderName, boolean isCallableStmt,
            boolean isCursorType) throws DatabaseOperationException, DatabaseCriticalException {
        ResultSetColumn[] metaData = new ResultSetColumn[columnCount];
        boolean isStatementNull = false;
        ResultSet rs = null;
        try {
            if (resultStmt.getResultSet() != null) {
                rs = resultStmt.getResultSet().isClosed() ? null : resultStmt.getResultSet();
            } else {
                isStatementNull = true;
            }

            if (resultStmt.getCursorResultSetType()) {
                isStatementNull = true;
            }
        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_FETCH_RESULT_SET_COLUMN_COMMENT), exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_FETCH_RESULT_SET_COLUMN_COMMENT);
        }
        for (int i = 0; i < columnCount; i++) {
            metaData[i] = new ResultSetColumn(i + 1);
            metaData[i].setColumnHeaderName(rs, columnHeaderName.get(i), i + 1, isCursorType, isStatementNull);
        }
        return metaData;
    }

    /**
     * Gets the next record batch.
     *
     * @param count the count
     * @return the next record batch
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * com.huawei.mppdbide.bl.serverdatacache.IQueryResult#getNextRecordBatch
     */

    @Override
    public String[][] getNextRecordBatch(int count) throws DatabaseOperationException, DatabaseCriticalException {
        return resultStmt.getNextRecordBatch(count);
    }

    /**
     * Gets the next object record batch.
     *
     * @param count the count
     * @param columnCount the columnCount
     * @param isFuncProcResultFlow the isFuncProcResultFlow
     * @param isInputParaVisited the isInputParaVisited
     * @return the next object record batch
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public Object[][] getNextObjectRecordBatch(int count, int columnCount, boolean isInputParaVisited,
            boolean isFuncProcResultFlow) throws DatabaseOperationException, DatabaseCriticalException {
        return resultStmt.getNextObjectRecordBatch(count, columnCount, isInputParaVisited);
    }

    /**
     * Gets the next object record batch.
     *
     * @param count the count
     * @return the next object record batch
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    @Override
    public Object[][] getNextObjectRecordBatch(int count) throws DatabaseOperationException, DatabaseCriticalException {
        return resultStmt.getNextObjectRecordBatch(count);
    }

    /**
     * gets Database connection
     */
    @Override
    public DBConnection getConnection() {
        return this.connection;
    }

    /**
     * Gets the results set.
     *
     * @return the results set
     */
    @Override
    public ResultSet getResultsSet() {
        if (resultStmt != null) {
            return resultStmt.getResultSet();
        }
        return null;

    }

    @Override
    public void commitConnection() {
        if (null != resultStmt) {
            resultStmt.commitConnection();
        }

    }

    @Override
    public int getColumnCount() throws DatabaseOperationException {
        return resultStmt.getColumCount();
    }
}
