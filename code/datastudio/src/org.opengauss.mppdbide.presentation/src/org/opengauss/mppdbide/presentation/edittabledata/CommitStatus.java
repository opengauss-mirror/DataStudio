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

import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommitStatus.
 * 
 * @since 3.0.0
 */
public class CommitStatus {

    private List<IDSGridEditDataRow> listOfSuccessRows;
    private List<IDSGridEditDataRow> listOfFailureRows;
    private int updatedRecords;
    private List<IDSGridEditDataRow> listOfNonExecutedRows;

    /**
     * Instantiates a new commit status.
     *
     * @param success the success
     * @param failure the failure
     * @param updatedRecords the updated records
     * @param nonExecutedRowsList the non executed rows list
     */
    public CommitStatus(List<IDSGridEditDataRow> success, List<IDSGridEditDataRow> failure, int updatedRecords,
            List<IDSGridEditDataRow> nonExecutedRowsList) {
        listOfSuccessRows = success;
        listOfFailureRows = failure;
        listOfNonExecutedRows = nonExecutedRowsList;
        this.updatedRecords = updatedRecords;
    }

    /**
     * Gets the list of failure rows.
     *
     * @return the list of failure rows
     */
    public List<IDSGridEditDataRow> getListOfFailureRows() {
        return listOfFailureRows;
    }

    /**
     * Sets the list of failure rows.
     *
     * @param listOfFailureRows the new list of failure rows
     */
    public void setListOfFailureRows(List<IDSGridEditDataRow> listOfFailureRows) {
        this.listOfFailureRows = listOfFailureRows;
    }

    /**
     * Gets the list of success rows.
     *
     * @return the list of success rows
     */
    public List<IDSGridEditDataRow> getListOfSuccessRows() {
        return listOfSuccessRows;
    }

    /**
     * Sets the list of success rows.
     *
     * @param listOfSuccessRows the new list of success rows
     */
    public void setListOfSuccessRows(List<IDSGridEditDataRow> listOfSuccessRows) {
        this.listOfSuccessRows = listOfSuccessRows;
    }

    /**
     * Gets the updated records.
     *
     * @return the updated records
     */
    public int getUpdatedRecords() {
        return updatedRecords;
    }

    /**
     * Sets the updated records.
     *
     * @param updatedRecords the new updated records
     */
    public void setUpdatedRecords(int updatedRecords) {
        this.updatedRecords = updatedRecords;
    }

    /**
     * Gets the list of not executed rows.
     *
     * @return the list of not executed rows
     */
    public List<IDSGridEditDataRow> getListOfNotExecutedRows() {
        return listOfNonExecutedRows;
    }

    /**
     * Sets the list of not executed rows.
     *
     * @param listOfNotExecutedRows the new list of not executed rows
     */
    public void setListOfNotExecutedRows(List<IDSGridEditDataRow> listOfNotExecutedRows) {
        this.listOfNonExecutedRows = listOfNotExecutedRows;
    }
}
