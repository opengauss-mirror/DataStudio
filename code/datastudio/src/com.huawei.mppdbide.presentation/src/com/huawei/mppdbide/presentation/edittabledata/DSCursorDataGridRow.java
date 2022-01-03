/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020. All rights reserved.
 */

package com.huawei.mppdbide.presentation.edittabledata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSCursorDataGridRow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 8.0.2, 23 Jan, 2020]
 * @since 23 Jan, 2019
 */

public class DSCursorDataGridRow extends DSResultSetGridDataRow implements IDSGridEditDataRow {
    private Map<Integer, Object> modifiedData = null;
    private EditTableRecordStates state;
    private EditTableRecordExecutionStatus execState = EditTableRecordExecutionStatus.NOT_EXECUTED;
    private String commitStatusMessage = "";
    private int rowIndex = 0;
    private DSEventTable eventTable;
    private int updatedRecords;
    private DSCursorTableDataGridDataProvider dataProvider;
    private Map<Integer, EditTableCellState> cellStateMap;

    /**
     * Instantiates a new DS edit table data grid row.
     *
     * @param isNewRow the is new row
     * @param index the index
     * @param eventTable the event table
     * @param dataProvider the data provider
     */
    public DSCursorDataGridRow(boolean isNewRow, int index, DSEventTable eventTable,
            DSCursorTableDataGridDataProvider dataProvider) {
        super(dataProvider);
        this.eventTable = eventTable;
        state = isNewRow ? EditTableRecordStates.INSERT : EditTableRecordStates.NOT_EDITED;
        this.rowIndex = index;
        updatedRecords = 0;
        this.dataProvider = dataProvider;
    }

    /**
     * Instantiates a new DS cursor table data grid row.
     *
     * @param eventTable the event table
     * @param dsCursorTableDataGridDataProvider the data provider
     */
    public DSCursorDataGridRow(DSEventTable eventTable,
            DSCursorTableDataGridDataProvider dsCursorTableDataGridDataProvider) {
        super(dsCursorTableDataGridDataProvider);
        this.eventTable = eventTable;
        state = EditTableRecordStates.NOT_EDITED;
        updatedRecords = 0;
        this.dataProvider = dsCursorTableDataGridDataProvider;
    }

    /**
     * Gets the cell state map.
     *
     * @return the cell state map
     */
    private Map<Integer, EditTableCellState> getCellStateMap() {
        if (this.cellStateMap == null) {
            cellStateMap = new HashMap<Integer, EditTableCellState>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        }
        return cellStateMap;
    }

    /**
     * Gets the modified data map.
     *
     * @return the modified data map
     */
    private Map<Integer, Object> getModifiedDataMap() {
        if (this.modifiedData == null) {
            modifiedData = new HashMap<Integer, Object>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        }
        return modifiedData;
    }

    @Override
    public void setValue(int columnIndex, Object newValue) {
        getModifiedDataMap().put(columnIndex, newValue);

        if (state == EditTableRecordStates.NOT_EDITED) {
            state = EditTableRecordStates.UPDATE;
            this.dataProvider.incrementUpdatedRowCount();
        }
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, null));

    }

    @Override
    public void undo(int columnIndex) {
        if (getModifiedDataMap().containsKey(columnIndex)) {
            getModifiedDataMap().remove(columnIndex);

            // Enum comparison doesn't need equals check as suggested by
            if (getModifiedDataMap().isEmpty() && state == EditTableRecordStates.UPDATE) {
                this.dataProvider.decrementUpdatedRowCount();
                setState(EditTableRecordStates.NOT_EDITED);
                eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, null));
            }

        }
    }

    @Override
    public EditTableRecordStates getUpdatedState() {
        return this.state;
    }

    @Override
    public EditTableRecordStates getUpdatedState(int columnIndex) {
        if (this.state == EditTableRecordStates.UPDATE && null != modifiedData
                && !modifiedData.containsKey(columnIndex)) {
            return EditTableRecordStates.NOT_EDITED;
        }

        return this.state;
    }

    @Override
    public void createNewRow(Object[] value) {
        values = value.clone();
        setState(EditTableRecordStates.INSERT);
    }

    /**
     * Sets the state.
     *
     * @param insertState the new state
     */
    private void setState(EditTableRecordStates insertState) {
        this.state = insertState;

    }

    @Override
    public Object getValue(int columnIndex) {
        if (null != modifiedData && modifiedData.containsKey(columnIndex)) {
            if (isIncludeEncoding() && !isUnstructuredDatatype(columnIndex)) {
                return getEncodedValue(modifiedData.get(columnIndex));
            }
            return modifiedData.get(columnIndex);
        }
        return super.getValue(columnIndex);
    }

    @Override
    public Object[] getValues() {
        Object[] values = super.getValues();
        for (Map.Entry<Integer, Object> modifiedEntry : getModifiedDataMap().entrySet()) {
            if (isIncludeEncoding() && !isUnstructuredDatatype(modifiedEntry.getKey())) {
                values[modifiedEntry.getKey()] = getEncodedValue(modifiedEntry.getValue());
            } else {
                values[modifiedEntry.getKey()] = modifiedEntry.getValue();
            }
        }

        return values;
    }

    @Override
    public void clearAllRowUpdates() {
        getModifiedDataMap().clear();
        setState(EditTableRecordStates.NOT_EDITED);
        setExecutionStatus(EditTableRecordExecutionStatus.NOT_EXECUTED);
        commitStatusMessage = "";
    }

    @Override
    public Object getOriginalValue(int columnIndex) {

        if (getUpdatedState() == EditTableRecordStates.INSERT) {
            getValue(columnIndex);
        }

        return super.getOriginalValue(columnIndex);
    }

    @Override
    public void setExecutionStatus(EditTableRecordExecutionStatus status) {
        execState = status;
    }

    @Override
    public EditTableRecordExecutionStatus getExecutionStatus() {
        return execState;
    }

    @Override
    public List<Integer> getModifiedColumns() {
        return new ArrayList<Integer>(getModifiedDataMap().keySet());
    }

    @Override
    public void setStateDelete() {
        this.state = EditTableRecordStates.DELETE;
    }

    @Override
    public String getCommitStatusMessage() {
        return commitStatusMessage;
    }

    @Override
    public void setCommitStatusMessage(String commitStatusMessage) {
        this.commitStatusMessage = commitStatusMessage;
    }

    @Override
    public int getRowIndex() {
        return rowIndex;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public Object[] getClonedValues() {
        // Cannot use Super.getClonedValues() as it would call this.getValues()
        // So, the original value will be changed.
        Object[] values = super.getValues().clone();

        for (Map.Entry<Integer, Object> modifiedEntry : getModifiedDataMap().entrySet()) {
            values[modifiedEntry.getKey()] = modifiedEntry.getValue();
        }

        return values;
    }

    @Override
    public int getUpdatedRecords() {

        return updatedRecords;
    }

    @Override
    public void setUpdatedRecords(int updatedRecords) {
        this.updatedRecords = updatedRecords;
    }

    @Override
    public EditTableCellState getCellStatus(int colIndex) {
        EditTableCellState editTableCellState = EditTableCellState.MODIFIED_FAILED;
        if (getCellStateMap().containsKey(colIndex)) {
            editTableCellState = getCellStateMap().get(colIndex);
        }
        return editTableCellState;
    }

    @Override
    public void setCellSatus(EditTableCellState cellState, int colIndex) {
        getCellStateMap().put(colIndex, cellState);
    }
}
