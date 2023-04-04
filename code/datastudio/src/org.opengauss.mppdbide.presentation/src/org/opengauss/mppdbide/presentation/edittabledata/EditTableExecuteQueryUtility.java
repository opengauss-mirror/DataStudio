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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.ITableMetaData;
import org.opengauss.mppdbide.presentation.grid.IDSGridColumnProvider;
import org.opengauss.mppdbide.presentation.grid.IRowEffectedConfirmation;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditTableExecuteQueryUtility.
 * 
 * @since 3.0.0
 */
public interface EditTableExecuteQueryUtility {

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
    boolean executeInsertRowCommand(IDSGridEditDataRow insertedRow, IDSGridEditDataRow insertedRowBuffer,
            DSEditTableDataGridDataProvider dsEditTableDataGridDataProvider, ITableMetaData table, DBConnection conn,
            String[] tableName, boolean isSupportReturning, boolean isAtomic) throws MPPDBIDEException;

    /**
     * Execute update row command.
     *
     * @param updatedRow the updated row
     * @param tableName the table name
     * @param dsEditTableDataGridDataProvider the ds edit table data grid data
     * provider
     * @param conn the conn
     * @param uniqueKeys the unique keys
     * @param columnProvider the column provider
     * @param rowEffectedConfirm the row effected confirm
     * @param isAtomic the is atomic
     * @return true, if successful
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public default boolean executeUpdateRowCommand(IDSGridEditDataRow updatedRow, String[] tableName,
            DSEditTableDataGridDataProvider dsEditTableDataGridDataProvider, DBConnection conn, List<String> uniqueKeys,
            IDSGridColumnProvider columnProvider, IRowEffectedConfirmation rowEffectedConfirm, boolean isAtomic)
            throws MPPDBIDEException {
        boolean executeStatus = true;
        Object value = null;
        int placeholderIdx = 0;
        updatedRow.setUpdatedRecords(0);
        final String query = IEditTableExecuteQuery.generateUpdateQuery(updatedRow, tableName,
                dsEditTableDataGridDataProvider, uniqueKeys, columnProvider);
        PreparedStatement stmt = null;
        try {
            stmt = conn.getConnection().prepareStatement(query);
            placeholderIdx = addPlaceHolderForUpdatedColumns(updatedRow, columnProvider, placeholderIdx, stmt);

            int uniqueKeysSize = uniqueKeys.size();
            for (int index = 0; index < uniqueKeysSize; index++) {
                String colName = uniqueKeys.get(index);
                int columnIndex = columnProvider.getColumnIndex(colName);
                value = updatedRow.getOriginalValue(columnIndex);
                if (value != null && !IEditTableExecuteQuery.isNonPlaceholderType(value)
                        && IEditTableExecuteQuery.isDatatypeSupported(columnProvider.getColumnDataTypeName(columnIndex),
                                columnProvider.getPrecision(columnIndex), dsEditTableDataGridDataProvider.getDatabse().getDolphinTypes())) {
                    placeholderIdx++;
                    IEditTableExecuteQuery.preparePlaceHolderStmt(columnProvider, value, placeholderIdx, stmt, index);
                }
            }
            int result = stmt.executeUpdate();
            executeStatus = IEditTableExecuteQuery.updateStatusAndRecordsinRow(updatedRow, rowEffectedConfirm,
                    executeStatus, result);
        } catch (SQLException exception) {
            updatedRow.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
            updatedRow.setCommitStatusMessage(IEditTableExecuteQuery.generateCommitFailureMessage(conn, exception));
            executeStatus = false;
            IEditTableExecuteQuery.handleSQLException(conn, exception, updatedRow);
        } finally {
            IEditTableExecuteQuery.closeStatement(stmt);
        }
        return executeStatus;
    }

    /**
     * Gets the place holder for updated columns.
     *
     * @param updatedRow the updated row
     * @param columnProvider the column provider
     * @param placeholderIdx the placeholder idx
     * @param stmt the stmt
     * @return the place holder for updated columns
     * @throws SQLException the SQL exception
     */
    public default int addPlaceHolderForUpdatedColumns(IDSGridEditDataRow updatedRow,
            IDSGridColumnProvider columnProvider, int placeholderIdx, PreparedStatement stmt) throws SQLException {
        List<Integer> modifiedColumns = updatedRow.getModifiedColumns();
        int clmnCount = 0;
        int modifiedColumnsSize = (modifiedColumns != null) ? modifiedColumns.size() : 0;

        for (clmnCount = 0; clmnCount < modifiedColumnsSize; clmnCount++) {
            int index = modifiedColumns.get(clmnCount);
            Object value1 = updatedRow.getValue(index);

            if (!IEditTableExecuteQuery.isNonPlaceholderType(value1)) {
                if (!IEditTableExecuteQuery.isMoneyDataType(columnProvider.getColumnDataTypeName(index))) {
                    placeholderIdx++;
                    IEditTableExecuteQuery.preparePlaceHolderStmt(columnProvider, value1, placeholderIdx, stmt, index);
                }
            }
        }
        return placeholderIdx;
    }

    /**
     * Execute delete row command.
     *
     * @param deletedRow the deleted row
     * @param tableName the table name
     * @param dsEditTableDataGridDataProvider the ds edit table data grid data
     * provider
     * @param conn the conn
     * @param uniqueKeys the unique keys
     * @param columnProvider the column provider
     * @param rowEffectedConfirm the row effected confirm
     * @param isAtomic the is atomic
     * @return true, if successful
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public default boolean executeDeleteRowCommand(IDSGridEditDataRow deletedRow, String[] tableName,
            DSEditTableDataGridDataProvider dsEditTableDataGridDataProvider, DBConnection conn, List<String> uniqueKeys,
            IDSGridColumnProvider columnProvider, IRowEffectedConfirmation rowEffectedConfirm, boolean isAtomic)
            throws MPPDBIDEException {
        boolean executeStatus = true;
        deletedRow.setUpdatedRecords(0);
        final String query = IEditTableExecuteQuery.generateDeleteQuery(deletedRow, tableName,
                dsEditTableDataGridDataProvider, uniqueKeys, columnProvider);
        PreparedStatement stmt = null;
        try {
            stmt = conn.getConnection().prepareStatement(query);
            addPlaceHolderForDeletedClms(deletedRow, uniqueKeys, columnProvider, stmt, conn, dsEditTableDataGridDataProvider.getDatabse().getDolphinTypes());

            int result = stmt.executeUpdate();
            if (result == 0) {
                deletedRow.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
                deletedRow.setUpdatedRecords(result);
                deletedRow.setCommitStatusMessage(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED));

            } else if (result > 1) {
                IEditTableExecuteQuery.getUserConfirmationOnMultiRowOprt(rowEffectedConfirm);
                deletedRow.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
                deletedRow.setUpdatedRecords(result);
            } else {
                deletedRow.setUpdatedRecords(result);
                deletedRow.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
            }
        } catch (SQLException exception) {
            deletedRow.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
            deletedRow.setCommitStatusMessage(generateCommitFailureMessage(conn, exception));
            executeStatus = false;
            IEditTableExecuteQuery.handleSQLException(conn, exception, deletedRow);
        } finally {
            IEditTableExecuteQuery.closeStatement(stmt);
        }
        return executeStatus;
    }

    /**
     * Gets the place holder for deleted clms.
     *
     * @param deletedRow the deleted row
     * @param uniqueKeys the unique keys
     * @param columnProvider the column provider
     * @param stmt the stmt
     * @param conn the conn
     * @return the place holder for deleted clms
     * @throws SQLException the SQL exception
     */
    public default void addPlaceHolderForDeletedClms(IDSGridEditDataRow deletedRow, List<String> uniqueKeys,
            IDSGridColumnProvider columnProvider, PreparedStatement stmt, DBConnection conn, HashMap<String, boolean[]> dolphinTypes) throws SQLException {
        Object value;
        String colName;
        int columnIndex;
        int placeHolder = 0;
        int uniqueKeysSize = uniqueKeys.size();
        for (int index = 0; index < uniqueKeysSize; index++) {
            colName = uniqueKeys.get(index);
            columnIndex = columnProvider.getColumnIndex(colName);
            value = deletedRow.getOriginalValue(columnIndex);
            if (null != value && !IEditTableExecuteQuery.isNonPlaceholderType(value)
                    && IEditTableExecuteQuery.isDatatypeSupported(columnProvider.getColumnDataTypeName(columnIndex),
                            columnProvider.getPrecision(columnIndex), dolphinTypes)) {
                placeHolder++;
                value = IEditTableExecuteQuery.transformToSqlDatatypes(columnProvider, index, value,
                        conn.getConnection());
                stmt.setObject(placeHolder, value);
            }
        }
    }

    /**
     * Generate commit failure message.
     *
     * @param conn the conn
     * @param exception the e
     * @return the string
     */
    static String generateCommitFailureMessage(DBConnection conn, SQLException exception) {
        String erromsgSplit = conn.extractErrorCodeAndErrorMsgFromServerError(exception);

        return erromsgSplit.trim();
    }
}
