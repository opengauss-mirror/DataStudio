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

import java.util.List;

import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSGridEditDataRow.
 * 
 * @since 3.0.0
 */
public interface IDSGridEditDataRow extends IDSGridDataRow {

    /**
     * Sets the value.for persisiting the record
     *
     * @param columnIndex the column index
     * @param newValue the new value
     */
    void setValue(int columnIndex, Object newValue);

    /**
     * Creates the new row.
     *
     * @param value the value
     */
    void createNewRow(Object[] value);

    /**
     * Undo.
     *
     * @param columnIndex the column index
     */
    void undo(int columnIndex);

    /**
     * Gets the updated state.
     *
     * @return the updated state
     */
    EditTableRecordStates getUpdatedState();

    /**
     * Gets the updated state.
     *
     * @param columnIndex the column index
     * @return the updated state
     */
    EditTableRecordStates getUpdatedState(int columnIndex);

    /**
     * Clear all row updates.
     */
    void clearAllRowUpdates();

    /**
     * Sets the execution status.
     *
     * @param status the new execution status
     */
    void setExecutionStatus(EditTableRecordExecutionStatus status);

    /**
     * Gets the execution status.
     *
     * @return the execution status
     */
    EditTableRecordExecutionStatus getExecutionStatus();

    /**
     * Gets the original value.
     *
     * @param columnIndex the column index
     * @return the original value
     */
    Object getOriginalValue(int columnIndex);

    /**
     * Gets the modified columns.
     *
     * @return the modified columns
     */
    List<Integer> getModifiedColumns();

    /**
     * Sets the state delete.
     */
    void setStateDelete();

    /**
     * Gets the commit status message.
     *
     * @return the commit status message
     */
    String getCommitStatusMessage();

    /**
     * Sets the commit status message.
     *
     * @param commitStatusMessage the new commit status message
     */
    void setCommitStatusMessage(String commitStatusMessage);

    /**
     * Gets the row index.
     *
     * @return the row index
     */
    int getRowIndex();

    /**
     * Sets the row index.
     *
     * @param rowIndex the new row index
     */
    void setRowIndex(int rowIndex);

    /**
     * Gets the updated records.
     *
     * @return the updated records
     */
    int getUpdatedRecords();

    /**
     * Sets the updated records.
     *
     * @param updatedRecords the new updated records
     */
    void setUpdatedRecords(int updatedRecords);

    /**
     * Sets the cell satus.
     *
     * @param cellState the cell state
     * @param columnIndex the column index
     */
    void setCellSatus(EditTableCellState cellState, int columnIndex);

    /**
     * Gets the cell status.
     *
     * @param columnIndex the column index
     * @return the cell status
     */
    EditTableCellState getCellStatus(int columnIndex);
}
