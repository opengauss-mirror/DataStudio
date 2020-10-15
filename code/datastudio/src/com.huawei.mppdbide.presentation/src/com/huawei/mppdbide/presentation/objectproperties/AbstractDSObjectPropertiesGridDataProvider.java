/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.huawei.mppdbide.presentation.edittabledata.CommitStatus;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordStates;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.utils.observer.IDSListener;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractDSObjectPropertiesGridDataProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class AbstractDSObjectPropertiesGridDataProvider {

    /**
     * The inserted list.
     */
    protected List<IDSGridEditDataRow> insertedList;

    /**
     * The delete list.
     */
    protected List<IDSGridEditDataRow> deleteList;

    /**
     * The altered list.
     */
    protected List<IDSGridEditDataRow> alteredList;

    /**
     * The event table.
     */
    protected DSEventTable eventTable;

    /**
     * The updated rows count.
     */
    protected int updatedRowsCount;

    /**
     * The row provider list.
     */
    protected List<IDSGridDataRow> rowProviderList;

    /**
     * Instantiates a new abstract DS object properties grid data provider.
     */
    public AbstractDSObjectPropertiesGridDataProvider() {
        insertedList = new ArrayList<>();
        deleteList = new ArrayList<>();
        alteredList = new ArrayList<>();
        this.eventTable = new DSEventTable();
        rowProviderList = new ArrayList<IDSGridDataRow>(5);
    }

    /**
     * Commit status.
     *
     * @return the commit status
     */
    public CommitStatus commitStatus() {
        List<IDSGridEditDataRow> successRowsList = new ArrayList<>();
        List<IDSGridEditDataRow> failureRowsList = new ArrayList<>();
        List<IDSGridEditDataRow> successCommits = new ArrayList<>();
        List<IDSGridEditDataRow> nonExecutedRowsList = new ArrayList<>();
        updatedRowsCount = 0;
        // maintain the list of failure rows and successful row and update the
        // parent rows
        for (IDSGridEditDataRow insertedRow : insertedList) {
            if (insertedRow.getExecutionStatus() == EditTableRecordExecutionStatus.SUCCESS) {
                insertedSuccessedRow(successRowsList, successCommits, insertedRow);
            } else if (insertedRow.getExecutionStatus() == EditTableRecordExecutionStatus.NOT_EXECUTED) {
                nonExecutedRowsList.add(insertedRow);
            } else {
                failureRowsList.add(insertedRow);
            }
        }

        insertedList.removeAll(successCommits);
        successCommits.clear();
        List<IDSGridDataRow> parentRows = rowProviderList;
        for (IDSGridDataRow rows : parentRows) {
            if (((IDSGridEditDataRow) rows).getUpdatedState() == EditTableRecordStates.UPDATE) {
                if (((IDSGridEditDataRow) rows).getExecutionStatus() == EditTableRecordExecutionStatus.SUCCESS) {
                    updatedSuccessedRows(successRowsList, rows);
                } else if (((IDSGridEditDataRow) rows)
                        .getExecutionStatus() == EditTableRecordExecutionStatus.NOT_EXECUTED) {
                    nonExecutedRowsList.add((IDSGridEditDataRow) rows);
                } else {
                    failureRowsList.add((IDSGridEditDataRow) rows);
                }

            }
        }

        Iterator<IDSGridEditDataRow> iterator = deleteList.iterator();
        while (iterator.hasNext()) {
            IDSGridEditDataRow deletedRow = (IDSGridEditDataRow) iterator.next();
            if (deletedRow.getExecutionStatus() == EditTableRecordExecutionStatus.SUCCESS) {
                successRowsList.add(deletedRow);
                successCommits.add(deletedRow);
                removeDeletedRowFromMetadata(deletedRow);
                this.rowProviderList.remove(deletedRow);
                deletedRow.clearAllRowUpdates();
            } else if (deletedRow.getExecutionStatus() == EditTableRecordExecutionStatus.NOT_EXECUTED) {
                nonExecutedRowsList.add(deletedRow);
            } else {
                failureRowsList.add(deletedRow);
            }

        }
        deleteList.removeAll(successCommits);
        successCommits.clear();
        removeSuccessFromAlterList(successRowsList);
        return new CommitStatus(successRowsList, failureRowsList, updatedRowsCount, nonExecutedRowsList);
    }

    /**
     * Updated successed rows.
     *
     * @param successRowsList the success rows list
     * @param rows the rows
     */
    private void updatedSuccessedRows(List<IDSGridEditDataRow> successRowsList, IDSGridDataRow rows) {
        List<Integer> modifiedColumns = ((IDSGridEditDataRow) rows).getModifiedColumns();
        int modifiedColumnsSize = modifiedColumns.size();
        for (int i = 0; i < modifiedColumnsSize; i++) {
            int colIndex = modifiedColumns.get(i);
            Object value = ((IDSGridEditDataRow) rows).getValue(colIndex);
            Object[] values = ((IDSGridEditDataRow) rows).getValues();
            values[colIndex] = value == null ? null : value.toString();
        }
        updatedRowsCount++;
        successRowsList.add((IDSGridEditDataRow) rows);
        ((IDSGridEditDataRow) rows).clearAllRowUpdates();
    }

    /**
     * Inserted successed row.
     *
     * @param successRowsList the success rows list
     * @param successCommits the success commits
     * @param insertedRow the inserted row
     */
    private void insertedSuccessedRow(List<IDSGridEditDataRow> successRowsList, List<IDSGridEditDataRow> successCommits,
            IDSGridEditDataRow insertedRow) {
        successCommits.add(insertedRow);
        successRowsList.add(insertedRow);
        Object[] values = insertedRow.getValues();
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                values[i] = insertedRow.getValue(i);
            }
        }
        rowProviderList.add(insertedRow.getRowIndex(), insertedRow);
        insertedRow.clearAllRowUpdates();
        removeAlteredRowInList(insertedRow);
    }

    /**
     * Removes the deleted row from metadata.
     *
     * @param deletedRow the deleted row
     */
    protected void removeDeletedRowFromMetadata(IDSGridEditDataRow deletedRow) {
        // This method is added to remove meta data information for deleted
        // row..Overridden in particular provider

    }

    /**
     * Adds the altered row in list.
     *
     * @param row the row
     */
    public void addAlteredRowInList(IDSGridEditDataRow row) {
        if (!alteredList.contains(row)) {
            alteredList.add(row);
        }

    }

    /**
     * Removes the altered row in list.
     *
     * @param row the row
     */
    public void removeAlteredRowInList(IDSGridEditDataRow row) {
        alteredList.remove(row);
    }

    /**
     * Gets the all fetched rows.
     *
     * @return the all fetched rows
     */
    public List<IDSGridDataRow> getAllFetchedRows() {

        return this.rowProviderList;
    }

    /**
     * Gets the record count.
     *
     * @return the record count
     */
    public int getRecordCount() {

        return rowProviderList.size();
    }

    /**
     * Delete record.
     *
     * @param row the row
     * @param isInserted the is inserted
     */
    public void deleteRecord(IDSGridEditDataRow row, boolean isInserted) {
        // check if the record is just inserted and being deleted
        // check if the record is modified and being deleted(add to delete list
        // and remove from the modified list)
        // check if the record is marked as deleted and user try update it(add
        // only to delete list and remove from parent

        if (isInserted) {
            insertedList.remove(row);
            rearrangeInsertedRowIndex(row.getRowIndex(), false);
            removeAlteredRowInList(row);
        } else {
            if (!deleteList.contains(row)) {
                deleteList.add(row);
                addAlteredRowInList(row);
                row.setStateDelete();
            }
        }

        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_ROW_DELETED, row));
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, null));
    }

    /**
     * Rearrange inserted row index.
     *
     * @param rowIndex the row index
     * @param isInsertOperation the is insert operation
     */
    protected void rearrangeInsertedRowIndex(int rowIndex, boolean isInsertOperation) {

        List<IDSGridEditDataRow> insList = insertedList;
        Iterator<IDSGridEditDataRow> it = insList.iterator();
        while (it.hasNext()) {
            IDSGridEditDataRow row = it.next();
            int index = row.getRowIndex();
            if (index > rowIndex) {
                if (isInsertOperation) {
                    row.setRowIndex(index + 1);
                } else {
                    row.setRowIndex(index - 1);
                }
            }
        }

    }

    /**
     * Gets the consolidated rows.
     *
     * @return the consolidated rows
     */
    public List<IDSGridDataRow> getConsolidatedRows() {
        List<IDSGridDataRow> rows = getAllFetchedRows();
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
     * Gets the inserted row count.
     *
     * @return the inserted row count
     */
    public int getInsertedRowCount() {

        return insertedList.size();
    }

    /**
     * Gets the deleted row count.
     *
     * @return the deleted row count
     */
    public int getDeletedRowCount() {

        return deleteList.size();
    }

    /**
     * Adds the listener.
     *
     * @param type the type
     * @param listener the listener
     */
    public void addListener(int type, IDSListener listener) {
        eventTable.hook(type, listener);
    }

    /**
     * Removes the listener.
     *
     * @param type the type
     * @param listener the listener
     */
    public void removeListener(int type, IDSListener listener) {
        eventTable.unhook(type, listener);
    }


    /**
     * Removes the success from alter list.
     *
     * @param success the success
     */
    public void removeSuccessFromAlterList(List<IDSGridEditDataRow> success) {
        for (IDSGridEditDataRow successUpdatedRows : success) {
            alteredList.remove(successUpdatedRows);
        }
    }

    /**
     * Roll back provider.
     */
    public void rollBackProvider() {
        insertedList.clear();
        deleteList.clear();
        alteredList.clear();
        List<IDSGridDataRow> allRows = getAllFetchedRows();

        for (Iterator<IDSGridDataRow> iterator = allRows.iterator(); iterator.hasNext();) {
            IDSGridEditDataRow idsGridDataRow = (IDSGridEditDataRow) iterator.next();
            idsGridDataRow.clearAllRowUpdates();
        }
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, null));

    }

    /**
     * Checks if is grid data edited.
     *
     * @return true, if is grid data edited
     */
    public boolean isGridDataEdited() {
        return getUpdatedRowCount() > 0 || getInsertedRowCount() > 0 || getDeletedRowCount() > 0;
    }

    /**
     * Gets the updated row count.
     *
     * @return the updated row count
     */
    public int getUpdatedRowCount() {
        int updatedRows = 0;
        for (IDSGridDataRow row : getAllFetchedRows()) {
            if (((IDSGridEditDataRow) row).getUpdatedState() == EditTableRecordStates.UPDATE) {
                updatedRows++;
            }
        }

        return updatedRows;
    }

    /**
     * Gets the all rows.
     *
     * @return the all rows
     */
    public List<IDSGridDataRow> getAllRows() {
        List<IDSGridDataRow> combinedRowproviderList = new ArrayList<>(rowProviderList);
        List<IDSGridEditDataRow> sortedInsertedList = new ArrayList<>(insertedList);
        Collections.sort(sortedInsertedList, new RowIndexComparator());
        for (IDSGridEditDataRow row : sortedInsertedList) {
            int rowIndex = row.getRowIndex();
            combinedRowproviderList.add(rowIndex, row);
        }

        return combinedRowproviderList;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RowIndexComparator.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class RowIndexComparator implements Comparator<IDSGridEditDataRow> {
        @Override
        public int compare(IDSGridEditDataRow o1, IDSGridEditDataRow o2) {
            if (o1.getRowIndex() > o2.getRowIndex()) {
                return 1;
            }
            if (o1.getRowIndex() < o2.getRowIndex()) {
                return -1;
            }
            return 0;
        }

    }

}
