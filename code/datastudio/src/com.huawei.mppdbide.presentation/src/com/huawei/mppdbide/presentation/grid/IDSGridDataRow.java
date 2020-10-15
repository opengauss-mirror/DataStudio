/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.grid;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSGridDataRow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IDSGridDataRow {

    /**
     * Gets the values.
     *
     * @return the values
     */
    Object[] getValues();

    /**
     * Gets the value.
     *
     * @param columnIndex the column index
     * @return the value
     */
    Object getValue(int columnIndex);

    /**
     * Gets the cloned values.
     *
     * @return the cloned values
     */
    Object[] getClonedValues();
}
