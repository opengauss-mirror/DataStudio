
package com.huawei.mppdbide.presentation;

import java.util.Comparator;

/**
 * Column data provider for the grid.
 *
 */
public interface IDSGridColumnProvider {
    /**
     * @return Count of columns to be displayed by the grid.
     */
    int getColumnCount();

    /**
     * @return all the column names applicable for the grid.
     */
    String[] getColumnNames();

    /**
     * @param columnIndex Column name for given index. Column Index Column
     *            Starts with 1.
     * @return
     */
    String getColumnName(int columnIndex);

    /**
     * @param columnIndex Description of the column, to be used by Grid tool
     *            tip. Column index starts with 1.
     * @return
     */
    String getColumnDesc(int columnIndex);

    int getColumnIndex(String columnLabel);

    Comparator<Object> getComparator(int columnIndex);
}
