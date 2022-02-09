
package org.opengauss.mppdbide.presentation;

import org.opengauss.mppdbide.presentation.IDSGridDataRow;

public class DSObjectPropertiesGridDataRow implements IDSGridDataRow
{
    private Object[] rows;

    public DSObjectPropertiesGridDataRow(Object[] row)
    {
        this.rows = row;
    }

    @Override
    public Object[] getValues()
    {

        return rows;
    }

    @Override
    public Object getValue(int columnIndex)
    {
        
        return rows[columnIndex];
    }

}
