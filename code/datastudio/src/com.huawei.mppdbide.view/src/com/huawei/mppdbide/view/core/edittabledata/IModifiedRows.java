/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
