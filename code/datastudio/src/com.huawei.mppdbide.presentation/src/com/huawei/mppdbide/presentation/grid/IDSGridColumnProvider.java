/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.grid;

import java.util.Comparator;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSGridColumnProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IDSGridColumnProvider {

    /**
     * Gets the column count.
     *
     * @return the column count
     */
    int getColumnCount();

    /**
     * Gets the column names.
     *
     * @return the column names
     */
    String[] getColumnNames();

    /**
     * Gets the column name.
     *
     * @param columnIndex the column index
     * @return the column name
     */
    String getColumnName(int columnIndex);

    /**
     * Gets the column desc.
     *
     * @param columnIndex the column index
     * @return the column desc
     */
    String getColumnDesc(int columnIndex);

    /**
     * Gets the column index.
     *
     * @param columnLabel the column label
     * @return the column index
     */
    int getColumnIndex(String columnLabel);

    /**
     * Gets the comparator.
     *
     * @param columnIndex the column index
     * @return the comparator
     */
    Comparator<Object> getComparator(int columnIndex);

    /**
     * Gets the column datatype.
     *
     * @param columnIndex the column index
     * @return the column datatype
     */
    int getColumnDatatype(int columnIndex);

    /**
     * Gets the column data type name.
     *
     * @param columnIndex the column index
     * @return the column data type name
     */
    String getColumnDataTypeName(int columnIndex);

    /**
     * Gets the precision.
     *
     * @param columnIndex the column index
     * @return the precision
     */
    int getPrecision(int columnIndex);

    /**
     * Gets the scale.
     *
     * @param columnIndex the column index
     * @return the scale
     */
    int getScale(int columnIndex);

    /**
     * Gets the max length.
     *
     * @param columnIndex the column index
     * @return the max length
     */
    int getMaxLength(int columnIndex);

    /**
     * Gets the column comment.
     *
     * @param columnIndex the column index
     * @return the column comment
     */
    default String getColumnComment(int columnIndex) {
        return null;
    }

    /**
     * Gets the column default value.
     *
     * @param i the i
     * @return the column default value
     */
    String getDefaultValue(int i);

}
