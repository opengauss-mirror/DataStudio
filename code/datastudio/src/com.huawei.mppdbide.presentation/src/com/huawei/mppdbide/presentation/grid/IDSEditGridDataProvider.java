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

package com.huawei.mppdbide.presentation.grid;

import java.util.List;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.presentation.edittabledata.CommitStatus;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSEditGridDataProvider.
 *
 * @since 3.0.0
 */
public interface IDSEditGridDataProvider extends IDSGridDataProvider {

    /**
     * Checks if is edits the supported.
     *
     * @return true, if is edits the supported
     */
    boolean isEditSupported();

    /**
     * Commit.
     *
     * @param uniqueKeys the unique keys
     * @param isAtomic the is atomic
     * @param rowEffectedConfirm the row effected confirm
     * @param termConnection the term connection
     * @return the commit status
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    CommitStatus commit(List<String> uniqueKeys, boolean isAtomic, IRowEffectedConfirmation rowEffectedConfirm,
            DBConnection termConnection) throws MPPDBIDEException;

    /**
     * Roll back.
     */
    void rollBackProvider();

    /**
     * Checks if is unique key present.
     *
     * @return true, if is unique key present
     */
    default boolean isUniqueKeyPresent() {
        return false;
    }

    /**
     * Gets the empty row for insert.
     *
     * @param index the index
     * @return the empty row for insert
     */
    IDSGridEditDataRow getEmptyRowForInsert(int index);

    /**
     * Delete record.
     *
     * @param row the row
     * @param isInserted the is inserted
     */
    void deleteRecord(IDSGridEditDataRow row, boolean isInserted);

    /**
     * Gets the last commit status.
     *
     * @return the last commit status
     */
    default CommitStatus getLastCommitStatus() {
        return null;
    }

    /**
     * Checks if is distribution columns required.
     *
     * @return true, if is distribution columns required
     */
    default boolean isDistributionColumnsRequired() {
        return false;
    }

    /**
     * Checks if is grid data edited.
     *
     * @return true, if is grid data edited
     */
    boolean isGridDataEdited();

    /**
     * Gets the distributed column list.
     *
     * @return the distributed column list
     */
    default List<String> getDistributedColumnList() {
        return null;
    }

    /**
     * Gets the consolidated rows.
     *
     * @return the consolidated rows
     */
    List<IDSGridDataRow> getConsolidatedRows();

    /**
     * Gets the updated row count.
     *
     * @return the updated row count
     */
    int getUpdatedRowCount();

    /**
     * Gets the inserted row count.
     *
     * @return the inserted row count
     */
    int getInsertedRowCount();

    /**
     * Gets the deleted row count.
     *
     * @return the deleted row count
     */
    int getDeletedRowCount();

    /**
     * Cancel commit.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    void cancelCommit() throws DatabaseCriticalException, DatabaseOperationException;

    /**
     * Checks if is cancelled.
     *
     * @return true, if is cancelled
     */
    default boolean isCancelled() {
        return false;
    }

    /**
     * Sets the cancel.
     *
     * @param cancel the new cancel
     */
    default void setCancel(boolean cancel) {

    }

    /**
     * Checks if is distribution column.
     *
     * @param columnIndex the column index
     * @return true, if is distribution column
     */
    default boolean isDistributionColumn(int columnIndex) {
        return false;
    }

    /**
     * Gets the table name.
     *
     * @return the table name
     */
    default String getTableName() {
        return "";
    }

    /**
     * Gets the column count.
     *
     * @return the column count
     */
    default int getColumnCount() {
        return 0;
    }

    /**
     * Gets the column names.
     *
     * @return the column names
     */
    default List<String> getColumnNames() {
        return null;
    }

    /**
     * Gets the column data type names.
     *
     * @return the column data type names
     */
    default List<String> getColumnDataTypeNames() {
        return null;
    }

}
