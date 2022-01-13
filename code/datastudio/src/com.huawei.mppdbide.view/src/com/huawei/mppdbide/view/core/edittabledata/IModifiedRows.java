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

package com.huawei.mppdbide.view.core.edittabledata;

import java.util.ArrayList;
import java.util.Vector;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IModifiedRows.
 *
 * @since 3.0.0
 */
public interface IModifiedRows {

    /**
     * Gets the ui row index.
     *
     * @return the ui row index
     */
    int getUiRowIndex();

    /**
     * Gets the node id.
     *
     * @return the node id
     */
    String getNodeId();

    /**
     * Gets the ctid.
     *
     * @return the ctid
     */
    String getCtid();

    /**
     * Gets the original values.
     *
     * @return the original values
     */
    ArrayList<String> getOriginalValues();

    /**
     * Gets the display row vector.
     *
     * @return the display row vector
     */
    Vector<String> getDisplayRowVector();

    /**
     * Gets the modified values.
     *
     * @return the modified values
     */
    ArrayList<String> getModifiedValues();

    /**
     * Gets the operation type.
     *
     * @return the operation type
     */
    String getOperationType();

    /**
     * Gets the failure msg.
     *
     * @return the failure msg
     */
    String getFailureMsg();

    /**
     * Gets the table oid.
     *
     * @return the table oid
     */
    String getTableOid();

    /**
     * Gets the modified columns.
     *
     * @return the modified columns
     */
    ArrayList<String> getModifiedColumns();

    /**
     * Gets the updated column values.
     *
     * @return the updated column values
     */
    ArrayList<String> getUpdatedColumnValues();

}
