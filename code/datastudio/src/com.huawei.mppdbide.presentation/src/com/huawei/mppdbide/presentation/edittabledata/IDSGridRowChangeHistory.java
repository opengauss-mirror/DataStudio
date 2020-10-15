/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.edittabledata;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSGridRowChangeHistory.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IDSGridRowChangeHistory {

    /**
     * Push.
     *
     * @param columnIndex the column index
     * @param oldValue the old value
     * @param newValue the new value
     */
    void push(int columnIndex, Object oldValue, Object newValue);

    /**
     * Pop.
     *
     * @param columnIndex the column index
     * @param oldValue the old value
     * @param newValue the new value
     * @return the object
     */
    Object pop(int columnIndex, Object oldValue, Object newValue);

    /**
     * Clear.
     */
    void clear();

    /**
     * Clear.
     *
     * @param columnIndex the column index
     */
    void clear(int columnIndex);
}
