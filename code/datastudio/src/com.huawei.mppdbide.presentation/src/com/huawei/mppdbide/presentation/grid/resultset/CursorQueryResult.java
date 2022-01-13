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

package com.huawei.mppdbide.presentation.grid.resultset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.adapter.gauss.StmtExecutor;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.serverdatacache.ResultSetColumn;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.QueryResultType;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class CursorQueryResult.
 * 
 * @since 3.0.0
 */
public class CursorQueryResult implements IQueryResult {
    private Statement stmt;
    private DBConnection connection;
    private boolean fetchCommentFlag = false;

    /**
     * Instantiates a new cursor query result.
     *
     * @param stmt the stmt
     */
    public CursorQueryResult(Statement stmt, DBConnection connection, boolean fetchCommentFlag) {
        this.stmt = stmt;
        this.connection = connection;
        this.fetchCommentFlag = fetchCommentFlag;
    }

    @Override
    public void closeStament() {
        // Not supported
    }

    @Override
    public void rollback() {
        // Not supported
    }

    @Override
    public void commitConnection() {
        // Not supported
    }

    @Override
    public int getRowsAffected() {
        try {
            return this.stmt.getUpdateCount();
        } catch (SQLException exception) {
            // ignore
            MPPDBIDELoggerUtility.error("Error fetching rows affected", exception);
            return 0;
        }
    }

    @Override
    public QueryResultType getReturnType() {
        return QueryResultType.RESULTTYPE_RESULTSET;
    }

    @Override
    public ResultSetColumn[] getColumnMetaData() throws DatabaseOperationException, DatabaseCriticalException {
        Map<String, String> columnComment = new HashMap<>();
        if (this.fetchCommentFlag && connection.isOLAPConnection()) {
            columnComment = getColumnCommentOfOLAP(connection, stmt);
        }

        int count = getColumnCount();
        ResultSetColumn[] metaData = new ResultSetColumn[count];

        for (int i = 0; i < count; i++) {
            metaData[i] = new ResultSetColumn(i + 1);
            metaData[i].collectColumnData(stmt, columnComment);
        }

        return metaData;
    }

    @Override
    public String[][] getNextRecordBatch(int count) throws DatabaseOperationException, DatabaseCriticalException {
        // Do nothing. Because cursor materializing will be based on visitor
        return new String[0][0];
    }

    @Override
    public Object[][] getNextObjectRecordBatch(int count) throws DatabaseOperationException, DatabaseCriticalException {
        // Do nothing. Because cursor materializing will be based on visitor
        return new Object[0][0];
    }

    @Override
    public Object[][] getNextObjectRecordBatch(int count, int columnCount, boolean isInputParaVisited,
            boolean isFuncProcResultFlow) throws DatabaseOperationException, DatabaseCriticalException {
        // Do nothing. Because cursor materializing will be based on visitor
        return new Object[0][0];
    }

    @Override
    public ResultSet getResultsSet() {
        try {
            return stmt.getResultSet();
        } catch (SQLException e) {
            // Ignore
            return null;
        }

    }

    @Override
    public int getColumnCount() throws DatabaseOperationException {
        try {
            if (stmt.getResultSet().getMetaData() != null) {
                return stmt.getResultSet().getMetaData().getColumnCount();
            }
            return 0;
        } catch (SQLException exp) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.UNKNOW_CLOB_TYPE), exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        }
    }

    @Override
    public boolean isEndOfRecordsReached() {

        return false;
    }

    /**
     * gets the database connection
     */
    @Override
    public DBConnection getConnection() {
        return this.connection;
    }

    /**
     * gets the ColumnHeaderName
     * 
     * @param columnCount the columnCount
     * @param columnHeaderName the columnHeaderName
     * @param isCallableStmt the isCallableStmt
     * @param isCursorType the isCursorType
     * @return metaData the meta data
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     */
    public ResultSetColumn[] getColumnHeaderName(int columnCount, List<String> columnHeaderName, boolean isCallableStmt,
            boolean isCursorType) throws DatabaseOperationException, DatabaseCriticalException {
        ResultSetColumn[] metaData = new ResultSetColumn[columnCount];
        ResultSet rs = null;
        try {
            if (isCallableStmt || (stmt != null && !stmt.isClosed())) {
                rs = stmt.getResultSet();
            }
        } catch (SQLException exe) {
            GaussUtils.handleCriticalException(exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exe);
        }
        for (int i = 0; i < columnCount; i++) {
            metaData[i] = new ResultSetColumn(i + 1);
            metaData[i].setColumnHeaderName(rs, columnHeaderName.get(i), i + 1, isCursorType, isCallableStmt);
        }
        return metaData;
    }

    @Override
    public StmtExecutor getStmtExecutor() {
        return null;
    }

}
