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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintType;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.presentation.grid.IDSResultRowVisitor;
import com.huawei.mppdbide.presentation.grid.IRowEffectedConfirmation;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.JSQLParserUtils;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSEditTableDataGridDataProvider.
 * 
 * @since 3.0.0
 */
public class DSEditTableDataGridDataProvider extends DSResultSetGridDataProvider implements IDSEditGridDataProvider {

    /**
     * The inserted list.
     */
    protected List<IDSGridEditDataRow> insertedList;

    /**
     * The insert returning list.
     */
    protected List<IDSGridEditDataRow> insertReturningList;

    /**
     * The delete list.
     */
    protected List<IDSGridEditDataRow> deleteList;
    private TableMetaData table;

    /**
     * The context.
     */
    protected IExecutionContext context;

    /**
     * The last commit status.
     */
    protected CommitStatus lastCommitStatus;

    /**
     * The conn.
     */
    protected DBConnection conn;
    private boolean cancelled;

    /**
     * The column count.
     */
    protected int columnCount;

    /**
     * The column names.
     */
    protected List<String> columnNames;

    /**
     * The data type names.
     */
    protected List<String> dataTypeNames;

    /**
     * The table name.
     */
    protected String tableName = null;

    /**
     * The full table name.
     */
    protected String[] fullTableName;

    /**
     * The is query result edit.
     */
    protected boolean isQueryResultEdit;

    /**
     * The updated row count.
     */
    protected int updatedRowCount;

    /**
     * The savepoint name.
     */
    protected String savepointName = "";

    /**
     * Instantiates a new DS edit table data grid data provider.
     *
     * @param result the result
     * @param rsConfig the rs config
     * @param summary the summary
     * @param context the context
     * @param isQueryResultEditSupported the is query result edit supported
     */
    public DSEditTableDataGridDataProvider(IQueryResult result, IResultConfig rsConfig, IQueryExecutionSummary summary,
            IExecutionContext context, boolean isQueryResultEditSupported) {
        super(result, rsConfig, summary);
        this.context = context;
        deleteList = new ArrayList<IDSGridEditDataRow>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        insertedList = new ArrayList<IDSGridEditDataRow>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        insertReturningList = new ArrayList<IDSGridEditDataRow>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        this.isQueryResultEdit = isQueryResultEditSupported;
        fullTableName = new String[] {""};
        if (isQueryResultEdit) {
            this.table = null;
            try {
                ResultSet rs = result.getResultsSet();
                if (rs != null) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    this.tableName = JSQLParserUtils.getQualifiedTableName(summary.getQuery());

                    if (null != rsmd && rsmd.getColumnCount() > 0) {
                        if (this.tableName == null || "".equals(this.tableName)) {
                            this.tableName = rsmd.getTableName(1);
                        }
                        this.columnCount = rsmd.getColumnCount();
                        this.columnNames = new ArrayList<String>(this.columnCount);
                        this.dataTypeNames = new ArrayList<String>(this.columnCount);
                        for (int index = 1; index <= this.columnCount; index++) {
                            this.columnNames.add(rsmd.getColumnName(index));
                            this.dataTypeNames.add(rsmd.getColumnTypeName(index));
                        }
                    }
                    fullTableName = JSQLParserUtils.getSplitQualifiedName(this.tableName, false);
                }
            } catch (SQLException ex) {
                MPPDBIDELoggerUtility.error("DSEditTableDataGridDataProvider: edit table grid data provider failed.",
                        ex);
            }
        } else {
            if (context.getCurrentServerObject() instanceof TableMetaData) {
                this.table = (TableMetaData) context.getCurrentServerObject();
                this.tableName = this.table.getDisplayName();
                fullTableName = JSQLParserUtils.getSplitQualifiedName(this.tableName, false);
            }
        }

    }

    /**
     * Inits the.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    @Override
    public void init() throws DatabaseOperationException, DatabaseCriticalException {

        super.init();
    }

    /**
     * Checks if is edits the supported.
     *
     * @return true, if is edits the supported
     */
    @Override
    public boolean isEditSupported() {
        return true;
    }

    /**
     * Inits the by visitor.
     *
     * @return the IDS result row visitor
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    @Override
    public IDSResultRowVisitor initByVisitor(boolean isfuncProcResultFlow)
            throws DatabaseOperationException, DatabaseCriticalException {
        super.initByVisitor(isfuncProcResultFlow);
        return this;
    }

    /**
     * Update insert row grid.
     */
    protected void updateInsertRowGrid() {

        int idx = 0;

        for (IDSGridEditDataRow insertReturnedRow : insertReturningList) {
            /*
             * If the buffer is null, that means the insert operation is failed.
             * Else, update the insert row with the returned row values.
             */
            if (insertReturnedRow != null) {
                IDSGridEditDataRow originalRow = insertedList.get(idx);
                Object[] values = originalRow.getValues();
                if (values != null && values.length > 0) {
                    for (int colIndex = 0; colIndex < values.length; colIndex++) {
                        originalRow.setValue(colIndex, insertReturnedRow.getValue(colIndex));
                    }
                }
            }

            idx++;
        }

        insertReturningList.clear();

    }

    /**
     * Commit.
     *
     * @param uniqueKeysParam the unique keys param
     * @param isAtomic the is atomic
     * @param rowEffectedConfirm the row effected confirm
     * @param connection the connection
     * @return the commit status
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public CommitStatus commit(List<String> uniqueKeysParam, boolean isAtomic,
            IRowEffectedConfirmation rowEffectedConfirm, DBConnection connection) throws MPPDBIDEException {
        List<String> uniqueKeys = uniqueKeysParam;
        getConnectionForExecution(connection);
        boolean success = false;
        boolean autoCommitState = false;
        boolean commitStatus = true;
        try {

            if (uniqueKeys == null || uniqueKeys.isEmpty()) {
                uniqueKeys = getUniqueKeys();

            }

            // All or nothing
            if (isAtomic) {
                autoCommitState = executeStatementsOnAtomic();
            }

            success = updateSuccessstatus(uniqueKeys, isAtomic, rowEffectedConfirm);
        } finally {
            commitStatus = doPostOperationTasks(isAtomic, success, autoCommitState);

            if (this.lastCommitStatus.getListOfFailureRows().isEmpty()) {
                this.updatedRowCount = 0;
            }
        }
        // Only when commit fails, fail the transaction, so user would be
        // notified.
        if (!commitStatus) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_TO_BEGIN_TRANSACTION));
            throw new DatabaseOperationException(IMessagesConstants.ERR_TO_BEGIN_TRANSACTION);
        }
        return lastCommitStatus;
    }

    /**
     * Gets the connection for execution.
     *
     * @param connection the connection
     * @return the connection for execution
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    protected void getConnectionForExecution(DBConnection connection)
            throws DatabaseCriticalException, DatabaseOperationException {
        if (table != null) {
            conn = getConnection();
        } else {
            conn = connection;
        }
    }

    /**
     * Update successstatus.
     *
     * @param uniqueKeys the unique keys
     * @param isAtomic the is atomic
     * @param rowEffectedConfirm the row effected confirm
     * @return true, if successful
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    protected boolean updateSuccessstatus(List<String> uniqueKeys, boolean isAtomic,
            IRowEffectedConfirmation rowEffectedConfirm) throws MPPDBIDEException {
        boolean success;
        success = insertRows(conn, isAtomic);
        if (success || !isAtomic) {
            // to detect failure in update
            success = false;
            success = updateRows(conn, isAtomic, uniqueKeys, rowEffectedConfirm);
        }
        if (success || !isAtomic) {
            // to detect failure in delete
            success = false;
            success = deleteRows(conn, isAtomic, uniqueKeys, rowEffectedConfirm);
        }
        return success;
    }

    /**
     * Execute statements on atomic.
     *
     * @return true, if successful
     * @throws DatabaseOperationException the database operation exception
     */
    protected boolean executeStatementsOnAtomic() throws DatabaseOperationException {
        boolean autoCommitState = false;
        Statement stmt = null;
        try {
            autoCommitState = conn.getConnection().getAutoCommit();
            conn.getConnection().setAutoCommit(false);
            stmt = conn.getConnection().createStatement();
            savepointName = "SP" + System.currentTimeMillis();
            stmt.execute("SAVEPOINT " + savepointName);

        } catch (SQLException ex) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_TO_BEGIN_TRANSACTION),
                    ex);
            throw new DatabaseOperationException(IMessagesConstants.ERR_TO_BEGIN_TRANSACTION, ex);
        } finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Error while closing statement while create savepoint.", exception);
            }
        }
        return autoCommitState;
    }

    /**
     * Do post operation tasks.
     *
     * @param isAtomic the is atomic
     * @param success the success
     * @param resetAutoCommit the reset auto commit
     * @return true, if successful
     */
    protected boolean doPostOperationTasks(boolean isAtomic, boolean success, boolean resetAutoCommit) {
        if (isAtomic) {
            Statement stmt = null;
            try {
                // Perform rollback or commit
                if (!success) {
                    stmt = conn.getConnection().createStatement();
                    stmt.execute("rollback to savepoint " + savepointName);
                    // As the mode is Atomic, none of the records are
                    // commited.
                    setFailedStatus();
                } else {
                    if (resetAutoCommit) {
                        conn.getConnection().commit();
                    }
                    updateInsertRowGrid();
                }
                // Reset the AutoCommit
                conn.getConnection().setAutoCommit(resetAutoCommit);
            } catch (SQLException ex) {
                MPPDBIDELoggerUtility.none("Unable to close transaction");
                return false;
            } finally {
                try {
                    if (null != stmt) {
                        stmt.close();
                    }
                } catch (SQLException exception) {
                    MPPDBIDELoggerUtility.error("Error while closing statement while rollback to savepoint.",
                            exception);
                }
                if (null != table) {
                    releaseConnection(conn);
                }
                this.lastCommitStatus = commitStatus();
            }
        } else {
            updateInsertRowGrid();
            if (null != table) {
                releaseConnection(conn);
            }
            this.lastCommitStatus = commitStatus();
        }

        return true;
    }

    /**
     * Sets the failed status.
     */
    protected void setFailedStatus() {

        List<IDSGridEditDataRow> rows = getUpdatedRows();

        for (Iterator<IDSGridEditDataRow> iterator = rows.iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridEditDataRow = (IDSGridEditDataRow) iterator.next();
            if (idsGridEditDataRow.getExecutionStatus() == EditTableRecordExecutionStatus.SUCCESS) {

                idsGridEditDataRow.setExecutionStatus(EditTableRecordExecutionStatus.NOT_EXECUTED);
            } else {

                idsGridEditDataRow.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
            }
        }

        rows = deleteList;
        for (Iterator<IDSGridEditDataRow> iterator = rows.iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridEditDataRow = (IDSGridEditDataRow) iterator.next();
            if (idsGridEditDataRow.getExecutionStatus() == EditTableRecordExecutionStatus.SUCCESS) {
                idsGridEditDataRow.setExecutionStatus(EditTableRecordExecutionStatus.NOT_EXECUTED);
            } else {
                idsGridEditDataRow.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
            }
        }

        rows = insertedList;
        for (Iterator<IDSGridEditDataRow> iterator = rows.iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridEditDataRow = (IDSGridEditDataRow) iterator.next();
            idsGridEditDataRow.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
        }

    }

    /**
     * Gets the consolidated rows.
     *
     * @return the consolidated rows
     */
    public List<IDSGridDataRow> getConsolidatedRows() {

        List<IDSGridDataRow> rows = super.getAllFetchedRows();
        IDSGridEditDataRow insertedRow;
        List<IDSGridDataRow> consolidatedRows = new ArrayList<IDSGridDataRow>();
        consolidatedRows.addAll(rows);
        Iterator<IDSGridEditDataRow> iterator = insertedList.iterator();
        while (iterator.hasNext()) {
            insertedRow = (IDSGridEditDataRow) iterator.next();
            consolidatedRows.add(insertedRow.getRowIndex(), insertedRow);
        }

        return consolidatedRows;
    }

    /**
     * Commit status.
     *
     * @return the commit status
     */
    protected CommitStatus commitStatus() {
        List<IDSGridEditDataRow> success = new ArrayList<IDSGridEditDataRow>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        List<IDSGridEditDataRow> failure = new ArrayList<IDSGridEditDataRow>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        int updatedRecords = 0;

        // Insert
        updatedRecords = updateCommitStatusForInsertedRows(success, failure);

        // Update
        updatedRecords += updateCommitStatusForUpdatedRows(success, failure);

        // Delete
        updatedRecords += updatedCommitStatusForDeletedRows(success, failure);
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, null));
        return new CommitStatus(success, failure, updatedRecords, new ArrayList<IDSGridEditDataRow>());
    }

    /**
     * Updated commit status for deleted rows.
     *
     * @param success the success
     * @param failure the failure
     * @param updatedRecords the updated records
     * @return the int
     */
    private int updatedCommitStatusForDeletedRows(List<IDSGridEditDataRow> success, List<IDSGridEditDataRow> failure) {
        int updatedRecords = 0;
        List<IDSGridEditDataRow> successCommits = new ArrayList<IDSGridEditDataRow>(
                MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        for (Iterator<IDSGridEditDataRow> iterator = deleteList.iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridDataRow = (IDSGridEditDataRow) iterator.next();
            if (idsGridDataRow.getExecutionStatus() == EditTableRecordExecutionStatus.SUCCESS) {
                success.add(idsGridDataRow);
                updatedRecords = updatedRecords + idsGridDataRow.getUpdatedRecords();
                successCommits.add(idsGridDataRow);
                getAllFetchedRows().remove(idsGridDataRow);
                idsGridDataRow.clearAllRowUpdates();
            } else {
                failure.add(idsGridDataRow);
            }
        }
        deleteList.removeAll(successCommits);
        successCommits.clear();
        return updatedRecords;
    }

    /**
     * Update commit status for updated rows.
     *
     * @param success the success
     * @param failure the failure
     * @param updatedRecords the updated records
     * @return the int
     */
    private int updateCommitStatusForUpdatedRows(List<IDSGridEditDataRow> success, List<IDSGridEditDataRow> failure) {
        int updatedRecords = 0;
        List<IDSGridDataRow> parentRows = getAllFetchedRows();

        for (Iterator<IDSGridDataRow> iterator = parentRows.iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridDataRow = (IDSGridEditDataRow) iterator.next();
            if (idsGridDataRow.getUpdatedState() == EditTableRecordStates.UPDATE) {
                if (idsGridDataRow.getExecutionStatus() == EditTableRecordExecutionStatus.SUCCESS) {
                    List<Integer> modifiedColumns = idsGridDataRow.getModifiedColumns();
                    int modifiedColumnsSize = modifiedColumns.size();
                    for (int indx = 0; indx < modifiedColumnsSize; indx++) {
                        int colIndex = modifiedColumns.get(indx);
                        Object value = idsGridDataRow.getValue(colIndex);
                        Object[] values = idsGridDataRow.getValues();
                        if (values != null) {
                            values[colIndex] = value;
                        }
                    }
                    success.add(idsGridDataRow);
                    updatedRecords = updatedRecords + idsGridDataRow.getUpdatedRecords();
                    idsGridDataRow.clearAllRowUpdates();
                } else {
                    failure.add(idsGridDataRow);
                }
            }

        }
        return updatedRecords;
    }

    /**
     * Update commit status for inserted rows.
     *
     * @param success the success
     * @param failure the failure
     * @param updatedRecords the updated records
     * @return the int
     */
    private int updateCommitStatusForInsertedRows(List<IDSGridEditDataRow> success, List<IDSGridEditDataRow> failure) {
        int updatedRecords = 0;
        List<IDSGridEditDataRow> successCommits = new ArrayList<IDSGridEditDataRow>(
                MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        for (Iterator<IDSGridEditDataRow> iterator = insertedList.iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridDataRow = (IDSGridEditDataRow) iterator.next();
            if (idsGridDataRow.getExecutionStatus() == EditTableRecordExecutionStatus.SUCCESS) {
                success.add(idsGridDataRow);
                updatedRecords = updatedRecords + 1;
                successCommits.add(idsGridDataRow);
                Object[] values = idsGridDataRow.getValues();
                if (values != null && values.length > 0) {
                    for (int indx = 0; indx < values.length; indx++) {
                        values[indx] = idsGridDataRow.getValue(indx);
                    }
                }
                getAllFetchedRows().add(idsGridDataRow.getRowIndex(), idsGridDataRow);
                idsGridDataRow.clearAllRowUpdates();
            } else {
                failure.add(idsGridDataRow);
            }
        }

        insertedList.removeAll(successCommits);
        successCommits.clear();
        return updatedRecords;
    }

    /**
     * Release connection.
     *
     * @param dbConn the db conn
     */
    protected void releaseConnection(DBConnection dbConn) {
        context.getTermConnection().getDatabase().getConnectionManager().releaseConnection(dbConn);
    }

    /**
     * Checks if is insert returning record supported.
     *
     * @return true, if is insert returning record supported
     */
    protected boolean isInsertReturningRecordSupported() {
        return context.getTermConnection().getDatabase().isSupportInsertReturing();
    }

    /**
     * Delete rows.
     *
     * @param dbConn the db conn
     * @param isAtomic the is atomic
     * @param uniqueKeys the unique keys
     * @param rowEffectedConfirm the row effected confirm
     * @return true, if successful
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    protected boolean deleteRows(DBConnection dbConn, boolean isAtomic, List<String> uniqueKeys,
            IRowEffectedConfirmation rowEffectedConfirm) throws MPPDBIDEException {
        List<IDSGridEditDataRow> deletedRows = deleteList;

        boolean status = true;

        for (Iterator<IDSGridEditDataRow> iterator = deletedRows.iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridEditDataRow = (IDSGridEditDataRow) iterator.next();
            if (!isCancelled()) {
                EditTableExecuteQueryUtility editTableExcutequery = EditTableExecuteQueryFactory
                        .getEditTableExecuteQuery(this.getDatabse().getDBType());
                status = editTableExcutequery.executeDeleteRowCommand(idsGridEditDataRow, fullTableName, this, dbConn,
                        uniqueKeys, getColumnDataProvider(), rowEffectedConfirm, isAtomic);
            } else {
                break;
            }
            if (!status && isAtomic) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED));
                throw new DatabaseOperationException(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED);
            }
        }

        return status;
    }

    /**
     * Update rows.
     *
     * @param dbConn the db conn
     * @param isAtomic the is atomic
     * @param uniqueKeys the unique keys
     * @param rowEffectedConfirm the row effected confirm
     * @return true, if successful
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    protected boolean updateRows(DBConnection dbConn, boolean isAtomic, List<String> uniqueKeys,
            IRowEffectedConfirmation rowEffectedConfirm) throws MPPDBIDEException {
        List<IDSGridEditDataRow> updatedRows = getUpdatedRows();

        boolean status = true;

        for (Iterator<IDSGridEditDataRow> iterator = updatedRows.iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridEditDataRow = (IDSGridEditDataRow) iterator.next();

            if (!isCancelled()) {
                EditTableExecuteQueryUtility editTableExcutequery = EditTableExecuteQueryFactory
                        .getEditTableExecuteQuery(this.getDatabse().getDBType());
                status = editTableExcutequery.executeUpdateRowCommand(idsGridEditDataRow, fullTableName, this, dbConn,
                        uniqueKeys, getColumnDataProvider(), rowEffectedConfirm, isAtomic);
            } else {
                break;
            }
            if (!status && isAtomic) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED));
                throw new DatabaseOperationException(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED);
            } else if (status && !isAtomic) {
                decrementUpdatedRowCount();
            }
        }

        return status;
    }

    /**
     * Gets the updated rows.
     *
     * @return the updated rows
     */
    private List<IDSGridEditDataRow> getUpdatedRows() {
        List<IDSGridDataRow> allRows = getAllFetchedRows();
        List<IDSGridEditDataRow> updatedRows = new ArrayList<IDSGridEditDataRow>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        for (Iterator<IDSGridDataRow> iterator = allRows.iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridDataRow = (IDSGridEditDataRow) iterator.next();
            if (idsGridDataRow.getUpdatedState() == EditTableRecordStates.UPDATE) {
                updatedRows.add(idsGridDataRow);
            }
        }

        return updatedRows;
    }

    /**
     * Checks if is distribution columns required.
     *
     * @return true, if is distribution columns required
     */
    public boolean isDistributionColumnsRequired() {

        if (!deleteList.isEmpty()) {
            return true;
        }

        for (Iterator<IDSGridDataRow> iterator = getAllFetchedRows().iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridDataRow = (IDSGridEditDataRow) iterator.next();
            if (idsGridDataRow.getUpdatedState() == EditTableRecordStates.UPDATE) {
                return true;
            }
        }

        return false;
    }

    /**
     * Insert rows.
     *
     * @param dbConn the db conn
     * @param isAtomic the is atomic
     * @return true, if successful
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private boolean insertRows(DBConnection dbConn, boolean isAtomic) throws MPPDBIDEException {
        List<IDSGridEditDataRow> insertedRows = insertedList;
        boolean status = true;

        boolean insertReturningRecordSupported = isInsertReturningRecordSupported();
        for (Iterator<IDSGridEditDataRow> iterator = insertedRows.iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridEditDataRow = (IDSGridEditDataRow) iterator.next();
            DSEditTableDataGridRow emptyRow = new DSEditTableDataGridRow(eventTable, this);
            if (idsGridEditDataRow instanceof DSEditTableDataGridRow) {
                DSEditTableDataGridRow newRow = (DSEditTableDataGridRow) idsGridEditDataRow;
                emptyRow.setEncoding(getEncoding());
                emptyRow.setIncludeEncoding(newRow.isIncludeEncoding());
            }
            if (!isCancelled()) {
                EditTableExecuteQueryUtility editTableExcutequery = EditTableExecuteQueryFactory
                        .getEditTableExecuteQuery(this.getDatabse().getDBType());
                status = editTableExcutequery.executeInsertRowCommand(idsGridEditDataRow, emptyRow, this, table, dbConn,
                        fullTableName, insertReturningRecordSupported, isAtomic);

                if (status) {
                    /*
                     * save the post insert returning row in the list.
                     */
                    insertReturningList.add(emptyRow);
                } else {
                    /*
                     * if there is failure, insert null in the returning list.
                     * the method to show to the editor, must check for null and
                     * assume the failure and act properly.
                     */
                    insertReturningList.add(null);
                }
            } else {
                break;
            }
            if (!status && isAtomic) {
                /*
                 * if during the insert operations, one of them fails and user
                 * has chosen the operation set to be atomic then, we need to
                 * stop the operation any further and return immediately.
                 */
                insertReturningList.clear();
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED));
                throw new DatabaseOperationException(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED);
            }
        }

        return status;
    }

    /**
     * Gets the connection.
     *
     * @return the connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    protected DBConnection getConnection() throws DatabaseCriticalException, DatabaseOperationException {

        try {
            if (!context.getTermConnection().getDatabase().isConnected()) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_DATA_CONNECTION_LOST_ERR_MSG));
                throw new DatabaseCriticalException(IMessagesConstants.EDIT_TABLE_DATA_CONNECTION_LOST_ERR_MSG);

            }
            return context.getTermConnection().getDatabase().getConnectionManager().getFreeConnection();
        } catch (MPPDBIDEException exception) {
            if (exception instanceof DatabaseCriticalException) {
                throw (DatabaseCriticalException) exception;
            } else {
                throw (DatabaseOperationException) exception;
            }
        }
    }

    /**
     * Roll back provider.
     */
    @Override
    public void rollBackProvider() {
        insertedList.clear();
        deleteList.clear();
        updatedRowCount = 0;
        List<IDSGridDataRow> allRows = getAllFetchedRows();

        for (Iterator<IDSGridDataRow> iterator = allRows.iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridDataRow = (IDSGridEditDataRow) iterator.next();
            idsGridDataRow.clearAllRowUpdates();
        }
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, null));

    }

    /**
     * Gets the empty row for insert.
     *
     * @param index the index
     * @return the empty row for insert
     */
    @Override
    public IDSGridEditDataRow getEmptyRowForInsert(int index) {
        // check the use of index and remove index
        DSEditTableDataGridRow emptyRow = new DSEditTableDataGridRow(true, index, eventTable, this);
        int colCount = getColumnDataProvider().getColumnCount();
        Object[] value = new Object[colCount];
        emptyRow.createNewRow(value);
        emptyRow.setOriginalValues(value.clone());
        emptyRow.setIncludeEncoding(BLPreferenceManager.getInstance().getBLPreference().isIncludeEncoding());
        emptyRow.setEncoding(super.getEncoding());
        addToInsertList(emptyRow);

        return emptyRow;
    }

    /**
     * Creates the row from values.
     *
     * @param rowValues the row values
     * @return the IDS grid data row
     */
    @Override
    protected IDSGridDataRow createRowFromValues(Object[] rowValues) {
        DSEditTableDataGridRow row = new DSEditTableDataGridRow(eventTable, this);
        row.setValues(rowValues);
        row.setOriginalValues(rowValues.clone());
        row.setIncludeEncoding(isIncludeEncoding());

        return row;
    }

    /**
     * Update data to rows.
     *
     * @param values the values
     */
    @Override
    protected void updateDataToRows(Object[][] values) {
        for (int rowIndex = 0; rowIndex < values.length; rowIndex++) {
            getAllFetchedRows().add(createRowFromValues(values[rowIndex]));
        }

        summary.setNumRecordsFetched(getAllFetchedRows().size());
    }

    /**
     * Adds the to insert list.
     *
     * @param emptyRow the empty row
     */
    private void addToInsertList(IDSGridEditDataRow emptyRow) {
        /*
         * 1. Create Empty table, insert 1 row, fill data, again insert 1 row
         * fill data, again insert 1 row fill data.
         */

        this.insertedList.add(emptyRow);
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, null));
    }

    /**
     * Delete record.
     *
     * @param row the row
     * @param insertedRecord the inserted record
     */
    @Override
    public void deleteRecord(IDSGridEditDataRow row, boolean insertedRecord) {

        // check if the record is just inserted and being deleted
        // check if the record is modified and being deleted(add to delete list
        // and remove from the modified list)
        // check if the record is marked as deleted and user try update it(add
        // only to delete list and remove from parent

        if (insertedRecord) {
            this.insertedList.remove(row);
            // move the row index to one above for all rows
            // below the currently deleted row.
            rearrangeInsertedRowIndex(row.getRowIndex());
        } else if (!deleteList.contains(row)) {
            this.deleteList.add(row);
            row.setStateDelete();
        }
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, null));
    }

    /**
     * Call this method to rearrange the row index by reducing by 1 as one
     * element is removed.
     *
     * @param rowIndex the row index
     */
    private void rearrangeInsertedRowIndex(int rowIndex) {

        List<IDSGridEditDataRow> insList = insertedList;
        Iterator<IDSGridEditDataRow> it = insList.iterator();
        while (it.hasNext()) {
            IDSGridEditDataRow row = it.next();
            int index = row.getRowIndex();
            if (index > rowIndex) {
                row.setRowIndex(index - 1);
            }
        }

    }

    /**
     * Gets the unique keys.
     *
     * @return the unique keys
     */
    public List<String> getUniqueKeys() {
        List<String> uniqueKeys = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        if (table != null) {
            List<ColumnMetaData> columnMetaDataList = table.getColumnMetaDataList();

            for (Iterator<ColumnMetaData> iterator = columnMetaDataList.iterator(); iterator.hasNext();) {
                ColumnMetaData columnMetaData = (ColumnMetaData) iterator.next();
                uniqueKeys.add(columnMetaData.getName());
            }

        }
        return uniqueKeys;
    }

    /**
     * Checks if is unique key present.
     *
     * @return true, if is unique key present
     */
    @Override
    public boolean isUniqueKeyPresent() {
        if (table != null) {
            List<ConstraintMetaData> constraintMetaDataList = table.getConstraintMetaDataList();

            for (Iterator<ConstraintMetaData> iterator = constraintMetaDataList.iterator(); iterator.hasNext();) {
                ConstraintMetaData constraintMetaData = (ConstraintMetaData) iterator.next();
                if (constraintMetaData.getConstraintType() == ConstraintType.UNIQUE_KEY_CONSTRSINT
                        || constraintMetaData.getConstraintType() == ConstraintType.PRIMARY_KEY_CONSTRSINT) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the table.
     *
     * @return the table
     */
    @Override
    public ServerObject getTable() {
        if (table != null) {
            return this.table;
        } else {
            return null;
        }
    }

    /**
     * Gets the table name.
     *
     * @return the table name
     */
    @Override
    public String getTableName() {
        return this.tableName;
    }

    /**
     * Gets the databse.
     *
     * @return the databse
     */
    @Override
    public Database getDatabse() {
        if (null != super.getDatabse()) {
            return super.getDatabse();
        } else if (null != context && null != context.getTermConnection()) {
            return context.getTermConnection().getDatabase();
        }

        return null;
    }

    /**
     * Gets the distributed column list.
     *
     * @return the distributed column list
     */
    public List<String> getDistributedColumnList() {
        if (this.table == null) {
            return null;
        }
        return this.table.getDistributionColumnList();
    }

    /**
     * Checks if is distribution column.
     *
     * @param columnIndex the column index
     * @return true, if is distribution column
     */
    public boolean isDistributionColumn(int columnIndex) {
        if (this.table == null) {
            return false;
        } else if (columnIndex < this.table.getColumns().getSize()) {
            return this.table.isDistributionColumn(columnIndex);
        }
        return false;
    }

    /**
     * Sets the cancel.
     *
     * @param cancel the new cancel
     */
    @Override
    public void setCancel(boolean cancel) {
        this.cancelled = cancel;

    }

    /**
     * Checks if is cancelled.
     *
     * @return true, if is cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Checks if is grid data edited.
     *
     * @return true, if is grid data edited
     */
    @Override
    public boolean isGridDataEdited() {
        return getUpdatedRowCount() > 0 || insertedList.size() > 0 || deleteList.size() > 0;
    }

    /**
     * Gets the updated row count.
     *
     * @return the updated row count
     */
    @Override
    public int getUpdatedRowCount() {
        return this.updatedRowCount;
    }

    /**
     * Gets the inserted row count.
     *
     * @return the inserted row count
     */
    @Override
    public int getInsertedRowCount() {
        return insertedList.size();
    }

    /**
     * Gets the deleted row count.
     *
     * @return the deleted row count
     */
    @Override
    public int getDeletedRowCount() {
        return deleteList.size();
    }

    /**
     * Gets the last commit status.
     *
     * @return the last commit status
     */
    @Override
    public CommitStatus getLastCommitStatus() {

        return this.lastCommitStatus;
    }

    /**
     * Cancel commit.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    @Override
    public void cancelCommit() throws DatabaseCriticalException, DatabaseOperationException {

        if (null != conn) {
            conn.cancelQuery();
        }

    }

    /**
     * Increment updated row count.
     */
    public void incrementUpdatedRowCount() {
        this.updatedRowCount++;
    }

    /**
     * Decrement updated row count.
     */
    public void decrementUpdatedRowCount() {
        this.updatedRowCount--;

    }

    /**
     * Gets the column count.
     *
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        return this.columnCount;
    }

    /**
     * Gets the column names.
     *
     * @return the column names
     */
    @Override
    public List<String> getColumnNames() {
        return this.columnNames;
    }

    /**
     * Gets the column data type names.
     *
     * @return the column data type names
     */
    @Override
    public List<String> getColumnDataTypeNames() {
        return this.dataTypeNames;
    }

    /**
     * Change encoding.
     *
     * @param newEncoding the new encoding
     */
    @Override
    public void changeEncoding(String newEncoding) {
        super.changeEncoding(newEncoding);
        setEncodingForEditRow(insertedList, newEncoding);
        setEncodingForEditRow(insertReturningList, newEncoding);
    }

    /**
     * Sets the encoding for edit row.
     *
     * @param rows2 the rows 2
     * @param newEncoding the new encoding
     */
    private void setEncodingForEditRow(List<IDSGridEditDataRow> rows2, String newEncoding) {
        for (IDSGridDataRow row : rows2) {
            if (row instanceof DSResultSetGridDataRow) {
                // Checking instance for one record is sufficient, but static
                // tools may not think so.
                DSResultSetGridDataRow rsRow = (DSResultSetGridDataRow) row;
                rsRow.setIncludeEncoding(isIncludeEncoding());
                rsRow.setEncoding(newEncoding);
            }
        }
    }

    /**
     * Pre destroy.
     */
    @Override
    public void preDestroy() {
        super.preDestroy();
        this.context = null;
        this.table = null;
    }

}
