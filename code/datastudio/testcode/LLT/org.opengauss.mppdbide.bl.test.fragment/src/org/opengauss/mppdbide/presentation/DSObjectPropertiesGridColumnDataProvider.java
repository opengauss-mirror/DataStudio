package org.opengauss.mppdbide.presentation;

import java.util.Comparator;
import java.util.List;

import org.opengauss.mppdbide.presentation.IDSGridColumnProvider;

public class DSObjectPropertiesGridColumnDataProvider
        implements IDSGridColumnProvider
{
    private List<String[]> serverObjectProperty;
    private int            colCount;

    public void init(List<String[]> serverObjectProperty)
    {
        this.serverObjectProperty = serverObjectProperty;
        this.colCount = serverObjectProperty.get(0).length;
    }

    @Override
    public int getColumnCount()
    {
        return colCount;
    }

    @Override
    public String[] getColumnNames()
    {

        return serverObjectProperty.get(0);
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        // TODO Auto-generated method stub
        return serverObjectProperty.get(0)[columnIndex];
    }

    @Override
    public String getColumnDesc(int columnIndex)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getColumnIndex(String columnLabel)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Comparator<Object> getComparator(int columnIndex)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
