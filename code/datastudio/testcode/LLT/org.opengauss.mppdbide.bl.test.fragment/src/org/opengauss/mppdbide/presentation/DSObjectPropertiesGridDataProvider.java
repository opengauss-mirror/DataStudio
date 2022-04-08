package org.opengauss.mppdbide.presentation;

import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.presentation.IDSGridColumnProvider;
import org.opengauss.mppdbide.presentation.IDSGridDataRow;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

public class DSObjectPropertiesGridDataProvider implements IObjectPropertyData
{

    private List<String[]>        serverObjectPropertiesList;
    private IDSGridColumnProvider colProvider;
    private List<IDSGridDataRow>  rowProviderList;
    private String                propertyName;

    public DSObjectPropertiesGridDataProvider(
            List<String[]> ServerObjectPropertiesList, String propertyName)
    {
        this.serverObjectPropertiesList = ServerObjectPropertiesList;
        this.propertyName = propertyName;
        rowProviderList = new ArrayList<IDSGridDataRow>(5);
    }
    @Override
    public void init()
            throws DatabaseOperationException, DatabaseCriticalException
    {
        DSObjectPropertiesGridColumnDataProvider provider = new DSObjectPropertiesGridColumnDataProvider();
        provider.init(serverObjectPropertiesList);
        this.colProvider = provider;
        prepareGridRow();
    }

    private void prepareGridRow()
    {

        DSObjectPropertiesGridDataRow gridRowData = null;
        for (int i = 1; i < serverObjectPropertiesList.size(); i++)
        {
            String[] col = serverObjectPropertiesList.get(i);

            gridRowData = new DSObjectPropertiesGridDataRow(col);
            rowProviderList.add(gridRowData);
        }
    }

    @Override
    public void close()
            throws DatabaseOperationException, DatabaseCriticalException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public List<IDSGridDataRow> getNextBatch()
            throws DatabaseOperationException, DatabaseCriticalException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<IDSGridDataRow> getAllFetchedRows()
    {
        // TODO Auto-generated method stub
        return rowProviderList;
    }

    @Override
    public boolean isEndOfRecords()
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public int getRecordCount()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public IDSGridColumnProvider getColumnDataProvider()
    {
        // TODO Auto-generated method stub
        return this.colProvider;
    }

    @Override
    public String getObjectPropertyName()
    {
        // TODO Auto-generated method stub
        return this.propertyName;
    }

    @Override
    public void preDestroy()
    {
        // Nothing to do. Ignore
    }
    
}