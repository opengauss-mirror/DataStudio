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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.presentation.grid.IDSResultRowVisitor;
import com.huawei.mppdbide.presentation.grid.IRowEffectedConfirmation;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.observer.DSEvent;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSCursorTableDataGridDataProvider.
 * 
 * @since 3.0.0
 */
public class DSCursorTableDataGridDataProvider extends DSResultSetGridDataProvider implements IDSEditGridDataProvider {

    /**
     * The column count.-
     */
    protected int columnCount;

    /**
     * The column names.
     */
    protected List<String> columnNames;

    /**
     * The column data provider.
     */
    private IDSGridColumnProvider columnDataProvider;

    /**
     * The grid data row.
     */
    private List<IDSGridDataRow> rows;

    /**
     * The inserted list.
     */
    protected List<IDSGridEditDataRow> insertedList;

    /**
     * The isEndOfRecordsReached flag.
     */
    private boolean isEndOfRecordsReached;

    public DSCursorTableDataGridDataProvider(Object[] valueList) {
        super(null, null, null);
        insertedList = new ArrayList<IDSGridEditDataRow>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        this.rows = new ArrayList<IDSGridDataRow>(5);
        this.columnCount = valueList.length;
        this.columnNames = new ArrayList<String>(this.columnCount);
        for (int index = 0; index < this.columnCount; index++) {
            this.columnNames.add((String) valueList[index]);
        }
    }

    /**
     * Gets the consolidated rows.
     *
     * @return the consolidated rows
     */
    public List<IDSGridDataRow> getConsolidatedRows() {

        List<IDSGridDataRow> allFetchedRows = getAllFetchedRows();
        IDSGridEditDataRow insertedRow;
        List<IDSGridDataRow> consolidatedRows = new ArrayList<IDSGridDataRow>();
        consolidatedRows.addAll(allFetchedRows);
        Iterator<IDSGridEditDataRow> iterator = insertedList.iterator();
        while (iterator.hasNext()) {
            insertedRow = (IDSGridEditDataRow) iterator.next();
            consolidatedRows.add(insertedRow.getRowIndex(), insertedRow);
        }

        return consolidatedRows;
    }

    /**
     * Gets the all fetched rows.
     *
     * @return the all fetched rows
     */
    @Override
    public List<IDSGridDataRow> getAllFetchedRows() {
        return rows;
    }

    @Override
    protected IDSGridDataRow createRowFromValues(Object[] rowValues) {
        DSCursorDataGridRow row = new DSCursorDataGridRow(eventTable, this);
        row.setValues(rowValues);
        row.setIncludeEncoding(isIncludeEncoding());

        return row;
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
        return false;
    }

    /**
     * Inits the by visitor.
     *
     * @return the IDS result row visitor
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public IDSResultRowVisitor initByVisitor(Object[] colValueList)
            throws DatabaseOperationException, DatabaseCriticalException {
        DSResultSetGridColumnDataProvider colData = new DSResultSetGridColumnDataProvider();
        colData.initByVisitorPopUpHeaderName(colValueList);
        this.columnDataProvider = colData;

        return this;
    }

    @Override
    public void visit(DSResultSetGridDataRow gridDataRow)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException {
        if (null == this.columnDataProvider) {
            DSResultSetGridColumnDataProvider colData = new DSResultSetGridColumnDataProvider();
            colData.init(queryResult);
            this.columnDataProvider = colData;
        }

        Object[] rowValues = gridDataRow.getValues();
        IDSGridDataRow row = createRowFromValues(rowValues);
        this.rows.add(row);
    }

    /**
     * Gets the column data provider.
     *
     * @return the column data provider
     */
    @Override
    public IDSGridColumnProvider getColumnDataProvider() {
        return this.columnDataProvider;
    }

    /**
     * Sets the end of records.
     */
    @Override
    public void setEndOfRecords() {
        this.isEndOfRecordsReached = true;
    }

    /**
     * Checks if is end of records.
     *
     * @return true, if is end of records
     */
    @Override
    public boolean isEndOfRecords() {
        return this.isEndOfRecordsReached;
    }

    @Override
    public CommitStatus commit(List<String> uniqueKeys, boolean isAtomic, IRowEffectedConfirmation rowEffectedConfirm,
            DBConnection termConnection) throws MPPDBIDEException {
        return null;
    }

    @Override
    public void rollBackProvider() {

    }

    @Override
    public void deleteRecord(IDSGridEditDataRow row, boolean isInserted) {

    }

    @Override
    public boolean isGridDataEdited() {
        return false;
    }

    @Override
    public int getUpdatedRowCount() {
        return 0;
    }

    @Override
    public int getInsertedRowCount() {
        return 0;
    }

    @Override
    public int getDeletedRowCount() {
        return 0;
    }

    @Override
    public void cancelCommit() throws DatabaseCriticalException, DatabaseOperationException {

    }

    @Override
    public IDSGridEditDataRow getEmptyRowForInsert(int index) {
        return null;
    }

    public void decrementUpdatedRowCount() {

    }

    public void incrementUpdatedRowCount() {

    }
}
