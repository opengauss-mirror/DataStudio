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

package com.huawei.mppdbide.presentation.edittabledata;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ITableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditTableExecuteQuery.
 *
 * @since 3.0.0
 */
public class EditTableExecuteQuery implements EditTableExecuteQueryUtility {

    /**
     * Execute insert row command.
     *
     * @param insertedRow the inserted row
     * @param insertedRowBuffer the inserted row buffer
     * @param dsEditTableDataGridDataProvider the ds edit table data grid data
     * provider
     * @param table the table
     * @param conn the conn
     * @param tableName the table name
     * @param isSupportReturning the is support returning
     * @param isAtomic the is atomic
     * @return true, if successful
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public boolean executeInsertRowCommand(IDSGridEditDataRow insertedRow, IDSGridEditDataRow insertedRowBuffer,
            DSEditTableDataGridDataProvider dsEditTableDataGridDataProvider, ITableMetaData table, DBConnection conn,
            String[] tableName, boolean isSupportReturning, boolean isAtomic) throws MPPDBIDEException {
        boolean executeStatus = true;
        int noOfColumns = insertedRow.getValues().length;
        List<Object> values = new ArrayList<Object>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        for (int colIndex = 0; colIndex < noOfColumns; ++colIndex) {
            values.add(insertedRow.getValue(colIndex));
        }
        List<Object> editValues = new ArrayList<Object>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        String query = null;
        if (table == null) {
            query = IEditTableExecuteQuery.generateInsertQueryForEditQueryResults(dsEditTableDataGridDataProvider,
                    values, tableName, editValues, conn, isSupportReturning);
        } else if (table instanceof TableMetaData) {
            query = generateInsertQuery(dsEditTableDataGridDataProvider, values, (TableMetaData) table, editValues,
                    conn, isSupportReturning);
        }

        PreparedStatement stmt = null;
        int editValueCount = editValues.size();
        try {
            Object value = null;
            stmt = conn.getConnection().prepareStatement(query);

            for (int index = 0; index < editValueCount; index++) {
                value = editValues.get(index);
                if (!IEditTableExecuteQuery.isNonPlaceholderType(value)) {
                    stmt.setObject(index + 1, value);
                }
            }

            boolean result = stmt.execute();
            if (result || stmt.getUpdateCount() > 0) {
                insertedRow.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
                getResultValuesFromResultSet(insertedRowBuffer, dsEditTableDataGridDataProvider, noOfColumns, stmt);
            } else {
                insertedRow.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);

                insertedRow.setCommitStatusMessage(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED));
            }
        } catch (SQLException exception) {
            insertedRow.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
            insertedRow.setCommitStatusMessage(IEditTableExecuteQuery.generateCommitFailureMessage(conn, exception));
            executeStatus = false;
            IEditTableExecuteQuery.handleSQLException(conn, exception, insertedRow);
        } finally {
            IEditTableExecuteQuery.closeStatement(stmt);
        }

        return executeStatus;
    }

    /**
     * Generate insert query.
     *
     * @param dsEditTableDataGridDataProvider the ds edit table data grid data
     * provider
     * @param values the values
     * @param table the table
     * @param editValues the edit values
     * @param conn the conn
     * @param isSupportReturning the is support returning
     * @return the string
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private static String generateInsertQuery(DSEditTableDataGridDataProvider dsEditTableDataGridDataProvider,
            List<Object> values, TableMetaData table, List<Object> editValues, DBConnection conn,
            boolean isSupportReturning) throws MPPDBIDEException {

        boolean useDefaultVal = false;
        boolean defaultValUsed = false;
        StringBuilder query = new StringBuilder("INSERT into ");
        StringBuilder queryPlaceHolder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        int valueCount = values.size();
        Object value = null;
        Object col = null;
        List<String> distributedCols = table.getDistributionColumnList();
        List<ColumnMetaData> colMetData = table.getColumnMetaDataList();
        query.append(table.getDisplayName());
        query.append(" (");
        for (int index = 0; index < valueCount; index++) {
            value = values.get(index);
            col = colMetData.get(index);
            useDefaultVal = false;

            if (null == value) {
                useDefaultVal = colMetData.get(index).getHasDefVal();
                if (useDefaultVal) {
                    value = colMetData.get(index).getDefaultValue();
                    defaultValUsed = true;
                }
            }
            if (null == value) {
                editValues.add(null);
                query.append(table.getQualifiedObjectName(((ColumnMetaData) col).getName()));
                query.append(", ");
                IEditTableExecuteQuery.filterPlaceHolderQuery(values.get(index), queryPlaceHolder);
                queryPlaceHolder.append(", ");
            } else {
                generateQuerywhenValueisNotNull(dsEditTableDataGridDataProvider, values, table, editValues,
                        useDefaultVal, query, queryPlaceHolder, value, col, index, conn);
            }
        }

        IEditTableExecuteQuery.removeExtraCommas(editValues, distributedCols, defaultValUsed, query, queryPlaceHolder);

        query.append(") ");
        query.append(" values (");
        query.append(queryPlaceHolder.toString()).append(")");

        /* Generate query for getting values from server */
        if (isSupportReturning) {
            query.append(" RETURNING *");
        }
        return query.toString();
    }

    /**
     * Gets the result values from result set.
     *
     * @param insertedRowBuffer the inserted row buffer
     * @param dsEditTableDataGridDataProvider the ds edit table data grid data
     * provider
     * @param noOfColumns the no of columns
     * @param stmt the stmt
     * @return the result values from result set
     * @throws SQLException the SQL exception
     */
    private static void getResultValuesFromResultSet(IDSGridEditDataRow insertedRowBuffer,
            DSEditTableDataGridDataProvider dsEditTableDataGridDataProvider, int noOfColumns, PreparedStatement stmt)
            throws SQLException {
        try (ResultSet rs = stmt.getResultSet()) {
            int colIndex = 0;
            IDSGridColumnProvider columnDataProvider = dsEditTableDataGridDataProvider.getColumnDataProvider();
            while (rs.next()) {
                while (colIndex < noOfColumns) {
                    // if rs.getString() is used for the boolean/bit it
                    // will store it as 't'
                    insertedRowBuffer.setValue(colIndex, getDatatypeFromString(columnDataProvider, rs, colIndex));

                    colIndex++;
                }
                dsEditTableDataGridDataProvider.decrementUpdatedRowCount();
            }
        }
    }

    /**
     * Generate querywhen valueis not null.
     *
     * @param dsEditTableDataGridDataProvider the ds edit table data grid data
     * provider
     * @param values the values
     * @param table the table
     * @param editValues the edit values
     * @param useDefaultVal the use default val
     * @param query the query
     * @param queryPlaceHolder the query place holder
     * @param value the value
     * @param col the col
     * @param idx the idx
     */
    protected static void generateQuerywhenValueisNotNull(
            DSEditTableDataGridDataProvider dsEditTableDataGridDataProvider, List<Object> values, TableMetaData table,
            List<Object> editValues, boolean useDefaultVal, StringBuilder query, StringBuilder queryPlaceHolder,
            Object value, Object col, int idx, DBConnection conn) {
        if (null != value) {

            if (!useDefaultVal) {
                value = IEditTableExecuteQuery.transformToSqlDatatypes(
                        dsEditTableDataGridDataProvider.getColumnDataProvider(), idx, value, conn.getConnection());
                if (!IEditTableExecuteQuery.isNonPlaceholderType(value)) {
                    editValues.add(value);
                }
            }
            query.append(table.getQualifiedObjectName(((ColumnMetaData) col).getName()));

            query.append(", ");

            if (!useDefaultVal) {
                IEditTableExecuteQuery.filterPlaceHolderQuery(values.get(idx), queryPlaceHolder);
            } else {
                queryPlaceHolder.append("default");
            }
            queryPlaceHolder.append(", ");

        }
    }

    /**
     * Gets the datatype from string.
     *
     * @param columnDataProvider the column data provider
     * @param rs the rs
     * @param colIndex the col index
     * @return the datatype from string
     * @throws SQLException the SQL exception
     */
    private static Object getDatatypeFromString(IDSGridColumnProvider columnDataProvider, ResultSet rs, int colIndex)
            throws SQLException {
        switch (columnDataProvider.getColumnDatatype(colIndex)) {
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE: {
                if (IEditTableExecuteQuery.isMoneyDataType(columnDataProvider.getColumnDataTypeName(colIndex))) {
                    return rs.getString(colIndex + 1);
                } else {
                    return rs.getObject(colIndex + 1);
                }
            }
            case Types.BOOLEAN:
            case Types.BIT: {
                if (columnDataProvider.getPrecision(colIndex) <= 1) {
                    return rs.getObject(colIndex + 1);
                } else {
                    return rs.getString(colIndex + 1);
                }
            }
            case Types.NUMERIC: {
                return rs.getBigDecimal(colIndex + 1);
            }
            case Types.DATE: {
                return rs.getDate(colIndex + 1);
            }
            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE:
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE: {
                return rs.getTimestamp(colIndex + 1);
            }
            case Types.BINARY: {
                if (MPPDBIDEConstants.BYTEA.equals(columnDataProvider.getColumnDataTypeName(colIndex))) {
                    return rs.getBytes(colIndex + 1);
                } else {
                    return rs.getString(colIndex + 1);
                }
            }
            default: {
                return rs.getString(colIndex + 1);
            }
        }
    }
}