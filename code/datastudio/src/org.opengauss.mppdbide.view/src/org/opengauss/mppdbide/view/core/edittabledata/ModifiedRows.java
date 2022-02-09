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

package org.opengauss.mppdbide.view.core.edittabledata;

import java.util.ArrayList;
import java.util.Vector;

/**
 * 
 * Title: class
 * 
 * Description: The Class ModifiedRows.
 *
 * @since 3.0.0
 */
public class ModifiedRows implements IModifiedRows {
    private int uiRowIndex;
    private String tableOid;
    private String nodeId;
    private String ctid;
    private ArrayList<String> originalValues;
    private ArrayList<String> modifiedValues;
    private ArrayList<String> modifiedColumns;
    private ArrayList<String> updatedColumnValues;

    // Can be used if only modified columns can be identified for Update.
    private String operationType;

    private boolean isPostSuccess;
    private String failureMsg;

    /**
     * Instantiates a new modified rows.
     */
    public ModifiedRows() {
        nodeId = "";
        tableOid = "";
        ctid = "";
        originalValues = new ArrayList<String>(10);
        modifiedValues = new ArrayList<String>(10);
        modifiedColumns = new ArrayList<String>(10);
        updatedColumnValues = new ArrayList<String>(10);
        operationType = "";
        isPostSuccess = false;
        failureMsg = "";
    }

    /**
     * Gets the ui row index.
     *
     * @return the ui row index
     */
    @Override
    public int getUiRowIndex() {
        return uiRowIndex;
    }

    /**
     * Sets the ui row index.
     *
     * @param index the new ui row index
     */
    public void setUiRowIndex(int index) {
        this.uiRowIndex = index;
    }

    /**
     * Gets the node id.
     *
     * @return the node id
     */
    @Override
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Sets the node id.
     *
     * @param nodeId the new node id
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * Gets the ctid.
     *
     * @return the ctid
     */
    @Override
    public String getCtid() {
        return ctid;
    }

    /**
     * Sets the ctid.
     *
     * @param ctid the new ctid
     */
    public void setCtid(String ctid) {
        this.ctid = ctid;
    }

    /**
     * Gets the original values.
     *
     * @return the original values
     */
    @Override
    public ArrayList<String> getOriginalValues() {
        return originalValues;
    }

    /**
     * Gets the display row vector.
     *
     * @return the display row vector
     */
    @Override
    public Vector<String> getDisplayRowVector() {
        if (originalValues.size() != 0) {
            Vector<String> row = new Vector<String>(originalValues);
            return row;
        }
        return null;
    }

    /**
     * Sets the original values.
     *
     * @param originalValues the new original values
     */
    public void setOriginalValues(ArrayList<String> originalValues) {
        this.originalValues = originalValues;
    }

    /**
     * Gets the modified values.
     *
     * @return the modified values
     */
    @Override
    public ArrayList<String> getModifiedValues() {
        return modifiedValues;
    }

    /**
     * Sets the modified values.
     *
     * @param modifiedValues the new modified values
     */
    public void setModifiedValues(ArrayList<String> modifiedValues) {
        this.modifiedValues = modifiedValues;
    }

    /**
     * Gets the operation type.
     *
     * @return the operation type
     */
    @Override
    public String getOperationType() {
        return operationType;
    }

    /**
     * Sets the operation type.
     *
     * @param operationType the new operation type
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    /**
     * Checks if is post success.
     *
     * @return true, if is post success
     */
    public boolean isPostSuccess() {
        return isPostSuccess;
    }

    /**
     * Sets the post success.
     *
     * @param isPostSucess the new post success
     */
    public void setPostSuccess(boolean isPostSucess) {
        this.isPostSuccess = isPostSucess;
    }

    /**
     * Gets the failure msg.
     *
     * @return the failure msg
     */
    @Override
    public String getFailureMsg() {
        return failureMsg;
    }

    /**
     * Sets the failure msg.
     *
     * @param failureMsg the new failure msg
     */
    public void setFailureMsg(String failureMsg) {
        this.failureMsg = failureMsg;
    }

    /**
     * Sets the table oid.
     *
     * @param oid the new table oid
     */
    public void setTableOid(String oid) {
        this.tableOid = oid;
    }

    /**
     * Gets the table oid.
     *
     * @return the table oid
     */
    @Override
    public String getTableOid() {
        return this.tableOid;
    }

    /**
     * Gets the modified columns.
     *
     * @return the modified columns
     */
    @Override
    public ArrayList<String> getModifiedColumns() {
        return modifiedColumns;
    }

    /**
     * Sets the modified columns.
     *
     * @param modifiedColumns the new modified columns
     */
    public void setModifiedColumns(ArrayList<String> modifiedColumns) {
        this.modifiedColumns = modifiedColumns;
    }

    /**
     * Gets the updated column values.
     *
     * @return the updated column values
     */
    @Override
    public ArrayList<String> getUpdatedColumnValues() {
        return updatedColumnValues;
    }

    /**
     * Sets the updated column values.
     *
     * @param updatedColumnValues the new updated column values
     */
    public void setUpdatedColumnValues(ArrayList<String> updatedColumnValues) {
        this.updatedColumnValues = updatedColumnValues;
    }

    /**
     * Copy original to modified.
     */
    public void copyOriginalToModified() {
        for (String val : originalValues) {
            modifiedValues.add(val);
        }
    }
}
