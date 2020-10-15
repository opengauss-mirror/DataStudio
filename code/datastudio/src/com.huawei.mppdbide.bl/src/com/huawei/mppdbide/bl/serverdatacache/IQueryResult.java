/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.StmtExecutor;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.QueryResultType;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IQueryResult.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IQueryResult {

    /**
     * Close stament.
     */
    void closeStament();

    /**
     * Rollback.
     */
    void rollback();

    /**
     * Commit connection.
     */
    void commitConnection();

    /**
     * Gets the rows affected.
     *
     * @return the rows affected
     */
    int getRowsAffected();

    /**
     * Gets the return type.
     *
     * @return the return type
     */
    QueryResultType getReturnType();

    /**
     * Gets the column meta data.
     *
     * @return the column meta data
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    ResultSetColumn[] getColumnMetaData() throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Gets the next record batch.
     *
     * @param count the count
     * @return the next record batch
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    String[][] getNextRecordBatch(int count) throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Gets the next object record batch.
     *
     * @param count the count
     * @return the next object record batch
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    Object[][] getNextObjectRecordBatch(int count) throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Gets the results set.
     *
     * @return the results set
     */
    ResultSet getResultsSet();

    /**
     * Gets the column count.
     *
     * @return the column count
     * @throws DatabaseOperationException the database operation exception
     */
    int getColumnCount() throws DatabaseOperationException;

    /**
     * Checks if is end of records reached.
     *
     * @return true, if is end of records reached
     */
    boolean isEndOfRecordsReached();

    /**
     * Gets the column comment of OLAP.
     *
     * @param connection the connection
     * @param stmt the stmt
     * @return the column comment of OLAP
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @Author: lijialiang(l00448174)
     * @Date: May 31, 2019
     * @Title: getColumnCommentOfOLAP
     * @Description: get column comment
     */
    default Map<String, String> getColumnCommentOfOLAP(DBConnection connection, Statement stmt)
            throws DatabaseOperationException, DatabaseCriticalException {
        Map<String, String> columnComments = new HashMap<>();

        PreparedStatement prepareStmt = null;
        ResultSet commentRs = null;

        try {
            if (stmt == null || stmt.isClosed()) {
                return columnComments;
            }
            ResultSetMetaData resultdata = stmt.getResultSet().getMetaData();
            if (connection.isOLAPConnection()) {
                StringBuilder queryBuilder = getColumnDescriptionSQL();

                int columnCount = resultdata.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    queryBuilder.append("(?,?,?),");
                }
                if (columnCount >= 1) {
                    queryBuilder.deleteCharAt(queryBuilder.length() - 1);
                }
                queryBuilder.append(");");

                prepareStmt = connection.getPrepareStmt(queryBuilder.toString());

                int inColsSize = 3;
                for (int clmIndex = 1; clmIndex <= columnCount; clmIndex++) {
                    prepareStmt.setString(1 + (clmIndex - 1) * inColsSize, resultdata.getSchemaName(clmIndex));
                    prepareStmt.setString(2 + (clmIndex - 1) * inColsSize, resultdata.getTableName(clmIndex));
                    prepareStmt.setString(3 + (clmIndex - 1) * inColsSize, resultdata.getColumnName(clmIndex));
                }

                commentRs = prepareStmt.executeQuery();
                if (null != commentRs) {
                    while (commentRs.next()) {
                        String key = commentRs.getString("schema_name") + commentRs.getString("table_name")
                                + MPPDBIDEConstants.COLUMN_KEY_SIGN + commentRs.getString("column_name");
                        columnComments.put(key, commentRs.getString("description"));
                    }
                }
            }
        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_FETCH_RESULT_SET_COLUMN_COMMENT), exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_FETCH_RESULT_SET_COLUMN_COMMENT);
        } finally {
            connection.closeStatement(prepareStmt);
            connection.closeResultSet(commentRs);
        }

        return columnComments;
    }

    /**
     * gets the column description SQL
     * 
     * @return the query builder
     */
    default StringBuilder getColumnDescriptionSQL() {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT s.nspname     schema_name, " + 
                "       o.relname     table_name, " + 
                "       a.attname     column_name, " + 
                "       d.description " + 
                "  FROM pg_catalog.pg_class       o, " + 
                "       pg_catalog.pg_namespace   s, " + 
                "       pg_catalog.pg_attribute   a, " + 
                "       pg_catalog.pg_description d " + 
                " WHERE o.relnamespace = s.oid " + 
                "   and o.oid = a.attrelid " + 
                "   and o.oid = d.objoid " + 
                "   and d.objsubid = a.attnum " + 
                "   and o.relkind in('r', 'v', 'f', 't') " + 
                "   and (s.nspname, o.relname, a.attname) in (");
        return queryBuilder;
    }

    /**
     * gets the next batch record object
     * 
     * @param count the count
     * @param columnCount the column count
     * @param isFuncProcResultFlow the function procedure flag
     * @param isInputParaVisited the input param visited flag
     * @return the object
     * @throws DatabaseOperationException
     * @throws DatabaseCriticalException
     */
    Object[][] getNextObjectRecordBatch(int count, int columnCount, boolean isInputParaVisited,
            boolean isFuncProcResultFlow) throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * gets the db connection
     * 
     * @return Database connection
     */
    DBConnection getConnection();

    /** 
     * gets the StmtExecutor
     * 
     * @return the stmt executor
     */
    StmtExecutor getStmtExecutor();

}