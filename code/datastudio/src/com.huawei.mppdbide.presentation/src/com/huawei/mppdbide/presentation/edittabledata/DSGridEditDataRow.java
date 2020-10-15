/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.edittabledata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.utils.observer.DSEventTable;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSGridEditDataRow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class DSGridEditDataRow implements IDSGridEditDataRow {

    /**
     * The rows.
     */
    protected Object[] rows;

    /**
     * The modified data.
     */
    protected Map<Integer, Object> modifiedData = new HashMap<Integer, Object>();

    /**
     * The rowstate.
     */
    protected EditTableRecordStates rowstate;

    /**
     * The event table.
     */
    protected DSEventTable eventTable;

    /**
     * The row index.
     */
    protected int rowIndex;

    /**
     * The execution status.
     */
    protected EditTableRecordExecutionStatus executionStatus;

    /**
     * The commit message.
     */
    protected String commitMessage;

    /**
     * Gets the values.
     *
     * @return the values
     */
    @Override
    public Object[] getValues() {
        return rows.clone();
    }

    /**
     * Gets the value.
     *
     * @param columnIndex the column index
     * @return the value
     */
    @Override
    public Object getValue(int columnIndex) {

        if (modifiedData.containsKey(columnIndex)) {
            return modifiedData.get(columnIndex);
        }
        return rows[columnIndex];
    }

    /**
     * Gets the cloned values.
     *
     * @return the cloned values
     */
    @Override
    public Object[] getClonedValues() {
        return getValues().clone();
    }

    /**
     * Sets the value.
     *
     * @param columnIndex the column index
     * @param newValue the new value
     */
    @Override
    public void setValue(int columnIndex, Object newValue) {

    }

    /**
     * Sets the state.
     *
     * @param state the new state
     */
    protected void setState(EditTableRecordStates state) {
        this.rowstate = state;

    }

    /**
     * Creates the new row.
     *
     * @param value the value
     */
    @Override
    public void createNewRow(Object[] value) {
        rows = value.clone();
        setState(EditTableRecordStates.INSERT);
    }

    /**
     * Undo.
     *
     * @param columnIndex the column index
     */
    @Override
    public void undo(int columnIndex) {

    }

    /**
     * Gets the updated state.
     *
     * @return the updated state
     */
    @Override
    public EditTableRecordStates getUpdatedState() {
        return this.rowstate;
    }

    /**
     * Gets the updated state.
     *
     * @param columnIndex the column index
     * @return the updated state
     */
    @Override
    public EditTableRecordStates getUpdatedState(int columnIndex) {
        if (this.rowstate == EditTableRecordStates.UPDATE && !modifiedData.containsKey(columnIndex)) {
            return EditTableRecordStates.NOT_EDITED;
        }

        return this.rowstate;
    }

    /**
     * Clear all row updates.
     */
    @Override
    public void clearAllRowUpdates() {
        modifiedData.clear();
        setState(EditTableRecordStates.NOT_EDITED);
        setExecutionStatus(EditTableRecordExecutionStatus.NOT_EXECUTED);

    }

    /**
     * Sets the execution status.
     *
     * @param status the new execution status
     */
    @Override
    public void setExecutionStatus(EditTableRecordExecutionStatus status) {
        this.executionStatus = status;
    }

    /**
     * Gets the execution status.
     *
     * @return the execution status
     */
    @Override
    public EditTableRecordExecutionStatus getExecutionStatus() {
        return this.executionStatus;
    }

    /**
     * Gets the original value.
     *
     * @param columnIndex the column index
     * @return the original value
     */
    @Override
    public Object getOriginalValue(int columnIndex) {
        if (getUpdatedState() == EditTableRecordStates.INSERT) {
            return getValue(columnIndex);
        }
        return rows[columnIndex];
    }

    /**
     * Gets the modified columns.
     *
     * @return the modified columns
     */
    @Override
    public List<Integer> getModifiedColumns() {
        return new ArrayList<>(modifiedData.keySet());
    }

    /**
     * Sets the state delete.
     */
    @Override
    public void setStateDelete() {
        this.rowstate = EditTableRecordStates.DELETE;
    }

    /**
     * Gets the commit status message.
     *
     * @return the commit status message
     */
    @Override
    public String getCommitStatusMessage() {

        return this.commitMessage;
    }

    /**
     * Sets the commit status message.
     *
     * @param commitStatusMessage the new commit status message
     */
    @Override
    public void setCommitStatusMessage(String commitStatusMessage) {
        this.commitMessage = commitStatusMessage;
    }

    /**
     * Gets the row index.
     *
     * @return the row index
     */
    @Override
    public int getRowIndex() {
        return this.rowIndex;
    }

    /**
     * Sets the row index.
     *
     * @param rowIndex the new row index
     */
    @Override
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    /**
     * Gets the updated records.
     *
     * @return the updated records
     */
    @Override
    public int getUpdatedRecords() {

        return 0;
    }

    /**
     * Sets the updated records.
     *
     * @param updatedRecords the new updated records
     */
    @Override
    public void setUpdatedRecords(int updatedRecords) {

    }

    /**
     * Sets the cell satus.
     *
     * @param cellState the cell state
     * @param columnIndex the column index
     */
    @Override
    public void setCellSatus(EditTableCellState cellState, int columnIndex) {

    }

    /**
     * Gets the cell status.
     *
     * @param columnIndex the column index
     * @return the cell status
     */
    @Override
    public EditTableCellState getCellStatus(int columnIndex) {

        return null;
    }

    /**
     * Gets the event table.
     *
     * @return the event table
     */
    public DSEventTable getEventTable() {
        return eventTable;
    }

    /**
     * Sets the event table.
     *
     * @param eventTable the new event table
     */
    public void setEventTable(DSEventTable eventTable) {
        this.eventTable = eventTable;
    }

}
