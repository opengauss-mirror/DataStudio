package org.opengauss.mppdbide.presentation;

public interface IDSGridDataRow
{
    /**
     * @return All the grid displayable data will be returned as Objects.
     * The data will be handled by the grid display component based on the 
     * type of Object.
     */
    Object[] getValues();

    /**
     * @param columnIndex
     * @return Column value. Index start with 0.
     */
    Object getValue(int columnIndex);

}
