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

package org.opengauss.mppdbide.presentation.edittabledata;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.GaussUtils;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.presentation.grid.IDSGridColumnProvider;
import org.opengauss.mppdbide.presentation.grid.IRowEffectedConfirmation;
import org.opengauss.mppdbide.presentation.util.DataTypeUtility;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: class Description: The Class EditTableExecuteQueryUtil.
 *
 * @since 3.0.0
 */
public interface IEditTableExecuteQuery {

    /**
     * Removes the extra commas.
     *
     * @param editValues the edit values
     * @param distributedCols the distributed cols
     * @param defaultValUsed the default val used
     * @param query the query
     * @param queryPlaceHolder the query place holder
     */
    public static void removeExtraCommas(List<Object> editValues, List<String> distributedCols, boolean defaultValUsed,
            StringBuilder query, StringBuilder queryPlaceHolder) {
        if (editValues.size() == 0 && !defaultValUsed && distributedCols.size() > 0) {
            query.append(ServerObject.getQualifiedObjectName(distributedCols.get(0)));
            editValues.add(null);
            filterPlaceHolderQuery(null, queryPlaceHolder);
        } else {
            // Delete the last extra comma added to the query with space
            query.delete(query.length() - 2, query.length());
            // Delete the last extra comma added to the query with space
            queryPlaceHolder.delete(queryPlaceHolder.length() - 2, queryPlaceHolder.length());
        }
    }

    /**
     * Generate insert query for edit query results.
     *
     * @param dsEditTableDataGridDataProvider the ds edit table data grid data
     * provider
     * @param values the values
     * @param tableName the table name
     * @param editValues the edit values
     * @param conn the conn
     * @param isInsertReturningSupported the is insert returning supported
     * @return the string
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public static String generateInsertQueryForEditQueryResults(
            DSEditTableDataGridDataProvider dsEditTableDataGridDataProvider, List<Object> values, String[] tableName,
            List<Object> editValues, DBConnection conn, boolean isInsertReturningSupported) throws MPPDBIDEException {
        boolean isNoEdit = false;
        StringBuilder query = new StringBuilder("INSERT into ");
        StringBuilder queryPlaceHolder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        if (dsEditTableDataGridDataProvider.getDatabse() == null) {
            return "";
        }
        query.append(dsEditTableDataGridDataProvider.getDatabse().getQualifiedObjectNameSplit(tableName));
        String[] columnList = dsEditTableDataGridDataProvider.getColumnDataProvider().getColumnNames();
        if (columnList == null) {
            return null;
        }
        getInsertQueryValues(dsEditTableDataGridDataProvider, values, editValues, conn, query, queryPlaceHolder,
                columnList);

        if (editValues.size() == 0) {
            // Delete ( in the query with space
            query.delete(query.length() - 2, query.length());
            isNoEdit = true;
            queryPlaceHolder.append("default");
        } else {
            // Delete the last extra comma added to the query with space
            query.delete(query.length() - 2, query.length());
            // Delete the last extra comma added to the query with space
            queryPlaceHolder.delete(queryPlaceHolder.length() - 2, queryPlaceHolder.length());
        }

        appendValues(isInsertReturningSupported, isNoEdit, query, queryPlaceHolder);

        return query.toString();
    }

    /**
     * Append values.
     *
     * @param isInsertReturningSupported the is insert returning supported
     * @param isNoEdit the is no edit
     * @param query the query
     * @param queryPlaceHolder the query place holder
     */
    static void appendValues(boolean isInsertReturningSupported, boolean isNoEdit, StringBuilder query,
            StringBuilder queryPlaceHolder) {
        if (!isNoEdit) {
            query.append(") ");
        }
        query.append(" values (");
        query.append(queryPlaceHolder.toString()).append(")");

        if (isInsertReturningSupported) {
            /* Generate query for getting values from server */
            query.append(" RETURNING *");
        }
    }

    /**
     * Gets the insert query values.
     *
     * @param dsEditTableDataGridDataProvider the ds edit table data grid data
     * provider
     * @param values the values
     * @param editValues the edit values
     * @param conn the conn
     * @param query the query
     * @param queryPlaceHolder the query place holder
     * @param columnList the column list
     * @return the insert query values
     */
    static void getInsertQueryValues(DSEditTableDataGridDataProvider dsEditTableDataGridDataProvider,
            List<Object> values, List<Object> editValues, DBConnection conn, StringBuilder query,
            StringBuilder queryPlaceHolder, String[] columnList) {
        query.append(" (");
        int valueCount = values.size();
        Object value = null;
        String col = null;
        for (int index = 0; index < valueCount; index++) {
            value = values.get(index);
            col = columnList[index];

            if (null != value) {
                value = transformToSqlDatatypes(dsEditTableDataGridDataProvider.getColumnDataProvider(), index, value,
                        conn.getConnection());
                if (!isNonPlaceholderType(value)) {
                    editValues.add(value);
                }
                query.append(dsEditTableDataGridDataProvider.getDatabse().getValidObjectName(col));
                query.append(", ");

                filterPlaceHolderQuery(values.get(index), queryPlaceHolder);
                queryPlaceHolder.append(", ");

            }
        }
    }

    /**
     * Append non place holder type.
     *
     * @param query the query
     * @param value the value
     */
    static void appendNonPlaceHolderType(StringBuilder query, Object value) {
        if (value instanceof Boolean) {
            Boolean boolVal = (Boolean) value;
            query.append(boolVal ? "'1'" : "'0'");
        }
    }

    /**
     * Checks if is non placeholder type.
     *
     * @param value the value
     * @return true, if is non placeholder type
     */
    public static boolean isNonPlaceholderType(Object value) {
        return value instanceof Boolean;
    }

    /**
     * Update status and recordsin row.
     *
     * @param updatedRow the updated row
     * @param rowEffectedConfirm the row effected confirm
     * @param executeStatus the execute status
     * @param result the result
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    public static boolean updateStatusAndRecordsinRow(IDSGridEditDataRow updatedRow,
            IRowEffectedConfirmation rowEffectedConfirm, boolean executeStatus, int result) throws SQLException {
        if (result == 0) {
            updatedRow.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
            updatedRow.setUpdatedRecords(result);
            updatedRow.setCommitStatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED));
            executeStatus = false;
        } else if (result > 1) {
            getUserConfirmationOnMultiRowOprt(rowEffectedConfirm);
            updatedRow.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
            updatedRow.setUpdatedRecords(result);
        } else {
            updatedRow.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
            updatedRow.setUpdatedRecords(result);
        }
        return executeStatus;
    }

    /**
     * Prepare place holder stmt.
     *
     * @param columnProvider the column provider
     * @param valueParam the value param
     * @param placeholderIdx the placeholder idx
     * @param stmt the stmt
     * @param index the index
     * @throws SQLException the SQL exception
     */
    public static void preparePlaceHolderStmt(IDSGridColumnProvider columnProvider, Object valueParam,
            int placeholderIdx, PreparedStatement stmt, int index) throws SQLException {
        Object value = valueParam;
        value = transformToSqlDatatypes(columnProvider, index, value, stmt.getConnection());
        if (value instanceof String) {
            stmt.setObject(placeholderIdx, value, Types.OTHER);
        } else {
            stmt.setObject(placeholderIdx, value);
        }
    }

    /**
     * Transform to sql datatypes.
     *
     * @param columnProvider the column provider
     * @param index the index
     * @param value the value
     * @param con the con
     * @return the object
     */
    public static Object transformToSqlDatatypes(IDSGridColumnProvider columnProvider, int index, Object value,
            Connection con) {
        switch (columnProvider.getColumnDatatype(index)) {
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE:
                return convertToTimestamp(value);
            case Types.DOUBLE:
                return convertToDouble(value);
            case Types.BLOB:
                return convertToBlob(con, value);
            default:
                return value;
        }
    }

    /**
     * Convert to double.
     *
     * @param value the value
     * @return the double
     */
    static Double convertToDouble(Object value) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof String) {
            return Double.valueOf((String) value);
        }

        return null;
    }

    /**
     * Convert to blob.
     *
     * @param con the con
     * @param value the value
     * @return the blob
     */
    static Blob convertToBlob(Connection con, Object value) {

        if (null == value || !(value instanceof byte[])) {
            return null;
        }

        Blob createBlob = null;
        try {
            createBlob = con.createBlob();
            createBlob.setBytes(1, (byte[]) value);
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("Failed to convert bytes to blob object", exception);
        }

        return createBlob;
    }

    /**
     * Convert to timestamp.
     *
     * @param value the value
     * @return the timestamp
     */
    static Timestamp convertToTimestamp(Object value) {
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        } else if (value instanceof java.util.Date) {
            return new Timestamp(((java.util.Date) value).getTime());
        }

        return null;
    }

    /**
     * Generate commit failure message.
     *
     * @param conn the conn
     * @param exception the e
     * @return the string
     */
    public static String generateCommitFailureMessage(DBConnection conn, SQLException exception) {
        String erromsgSplit = conn.extractErrorCodeAndErrorMsgFromServerError(exception);

        return erromsgSplit.trim();
    }

    /**
     * Generate update query.
     *
     * @param updatedRow the updated row
     * @param tableName the table name
     * @param dsEditTableDataGridDataProvider the ds edit table data grid data
     * provider
     * @param uniqueKeys the unique keys
     * @param columnProvider the column provider
     * @return the string
     */
    public static String generateUpdateQuery(IDSGridEditDataRow updatedRow, String[] tableName,
            DSEditTableDataGridDataProvider dsEditTableDataGridDataProvider, List<String> uniqueKeys,
            IDSGridColumnProvider columnProvider) {
        Object value = null;
        StringBuilder strBldr = new StringBuilder("UPDATE ");
        Database dataBase = dsEditTableDataGridDataProvider.getDatabse();
        if (dataBase == null) {
            return "";
        }
        strBldr.append(dataBase.getQualifiedObjectNameSplit(tableName));

        strBldr.append(" SET ");
        List<Integer> modifiedColumns = updatedRow.getModifiedColumns();

        for (Iterator<Integer> iterator = modifiedColumns.iterator(); iterator.hasNext();) {
            Integer colIndex = (Integer) iterator.next();
            String colName = columnProvider.getColumnName(colIndex);
            if (colName != null) {
                strBldr.append(dataBase.getValidObjectName(colName));
                strBldr.append("= ");
            }

            filterPlaceHolderQuery(updatedRow.getValue(colIndex), strBldr);

            if (iterator.hasNext()) {
                strBldr.append(", ");
            }
        }

        strBldr.append(" WHERE");

        for (Iterator<String> iterator = uniqueKeys.iterator(); iterator.hasNext();) {
            String column = (String) iterator.next();
            int columnIndex = columnProvider.getColumnIndex(column);
            value = updatedRow.getOriginalValue(columnIndex);
            String columnDatatype = columnProvider.getColumnDataTypeName(columnIndex);
            if (isComplexDataType(columnDatatype)) {
                strBldr.append(' ' + dataBase.getValidObjectName(column) + "::text");
            } else {
                strBldr.append(' ' + dataBase.getValidObjectName(column));
            }
            if (null == value) {
                strBldr.append(" is null");
            } else {
                strBldr.append(" = ");
                handleNonEditableDatatypeQuery(columnProvider, value, columnIndex, strBldr, dsEditTableDataGridDataProvider.getDatabse().getDolphinTypes());
            }

            if (iterator.hasNext()) {
                strBldr.append(" AND");
            }
        }

        return strBldr.toString();
    }

    /**
     * Gets the user confirmation on multi row oprt.
     *
     * @param rowEffectedConfirm the row effected confirm
     * @return the user confirmation on multi row oprt
     * @throws SQLException the SQL exception
     */
    public static void getUserConfirmationOnMultiRowOprt(IRowEffectedConfirmation rowEffectedConfirm)
            throws SQLException {
        rowEffectedConfirm.promptUerConfirmation();

    }

    /**
     * Generate delete query.
     *
     * @param deletedRow the deleted row
     * @param tableName the table name
     * @param dsEditTableDataGridDataProvider the ds edit table data grid data
     * provider
     * @param uniqueKeys the unique keys
     * @param columnProvider the column provider
     * @return the string
     */
    public static String generateDeleteQuery(IDSGridEditDataRow deletedRow, String[] tableName,
            DSEditTableDataGridDataProvider dsEditTableDataGridDataProvider, List<String> uniqueKeys,
            IDSGridColumnProvider columnProvider) {
        Object value = null;
        String column = null;
        int columnIndex = 0;
        StringBuilder queryBuilder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        if (dsEditTableDataGridDataProvider.getDatabse() == null) {
            return "";
        }
        queryBuilder.append("DELETE FROM "
                + dsEditTableDataGridDataProvider.getDatabse().getQualifiedObjectNameSplit(tableName) + " WHERE");

        for (Iterator<String> iterator = uniqueKeys.iterator(); iterator.hasNext();) {
            column = (String) iterator.next();
            columnIndex = columnProvider.getColumnIndex(column);
            value = deletedRow.getOriginalValue(columnIndex);
            String columnDatatype = columnProvider.getColumnDataTypeName(columnIndex);
            if (isComplexDataType(columnDatatype)) {
                queryBuilder.append(
                        ' ' + dsEditTableDataGridDataProvider.getDatabse().getValidObjectName(column) + "::text");
            } else {
                queryBuilder.append(' ' + dsEditTableDataGridDataProvider.getDatabse().getValidObjectName(column));
            }

            if (null == value) {
                queryBuilder.append(" is null");
            } else {
                queryBuilder.append(" = ");
                handleNonEditableDatatypeQuery(columnProvider, value, columnIndex, queryBuilder, dsEditTableDataGridDataProvider.getDatabse().getDolphinTypes());
            }

            if (iterator.hasNext()) {
                queryBuilder.append(" AND");
            }
        }

        return queryBuilder.toString();

    }

    /**
     * Handle non editable datatype query.
     *
     * @param columnProvider the column provider
     * @param value the value
     * @param columnIndex the column index
     * @param query the query
     */
    static void handleNonEditableDatatypeQuery(IDSGridColumnProvider columnProvider, Object value, int columnIndex,
            StringBuilder query, HashMap<String, boolean[]> dolphinTypes) {
        if (!isDatatypeSupported(columnProvider.getColumnDataTypeName(columnIndex),
                columnProvider.getPrecision(columnIndex), dolphinTypes)) {
            query.append(ServerObject.getLiteralName(value.toString()));
        } else {
            filterPlaceHolderQuery(value, query);
        }
    }

    /**
     * Filter place holder query.
     *
     * @param value the value
     * @param query the query
     */
    public static void filterPlaceHolderQuery(Object value, StringBuilder query) {
        if (isNonPlaceholderType(value)) {
            appendNonPlaceHolderType(query, value);
        } else {
            query.append("?");
        }
    }

    /**
     * Close statement.
     *
     * @param stmt the stmt
     */
    public static void closeStatement(PreparedStatement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("Statement close failed. ", exception);
        }
    }

    /**
     * Checks if is datatype supported.
     *
     * @param sqlType the sql type
     * @param precision the precision
     * @return true, if is datatype supported
     */
    public static boolean isDatatypeSupported(String sqlType, int precision, HashMap<String, boolean[]> dolphinTypes) {
        switch (sqlType.toLowerCase(Locale.ENGLISH)) {
            case "bpchar":
            case "char":
            case "varchar":
            case "text":
            case "int4":
            case "int2":
            case "int8":
            case "date":
            case "numeric":
            case "decimal":
            case "float8":
            case "time":
            case "timetz":
            case "timestamp":
            case "timestamptz":
            case "bool":
            case "serial":
                return true;
            case "bit":
                if (precision > 1) {
                    return false;
                }
                return true;
            default:
                if (dolphinTypes != null && dolphinTypes.containsKey(sqlType.toLowerCase(Locale.ENGLISH))) {
                    return true;
                }
                return false;
        }
    }

    /**
     * Handle SQL exception.
     *
     * @param con the con
     * @param exception the exception
     * @param row the row
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public static void handleSQLException(DBConnection con, SQLException exception, IDSGridEditDataRow row)
            throws MPPDBIDEException {
        GaussUtils.handleCriticalException(exception);
        row.setCommitStatusMessage(generateCommitFailureMessage(con, exception));

        /*
         * Should not throw exception. To be analyzed. throw new
         * DatabaseOperationException
         */
    }

    /**
     * Checks if is money data type.
     *
     * @param colDataType the col data type
     * @return true, if is money data type
     */
    public static boolean isMoneyDataType(String colDataType) {
        return "money".equals(DataTypeUtility.convertToDisplayDatatype(colDataType));
    }

    /**
     * Checks if is complex data type.
     *
     * @param colDataType the col data type
     * @return true, if is complex data type
     */
    static boolean isComplexDataType(String colDataType) {
        switch (colDataType.toLowerCase(Locale.ENGLISH)) {
            case "point":
            case "polygon":
            case "json":
            case "circle":
            case "path":
            case "box":
            case "lseg":
                return true;
            default:
                return false;
        }
    }
}
