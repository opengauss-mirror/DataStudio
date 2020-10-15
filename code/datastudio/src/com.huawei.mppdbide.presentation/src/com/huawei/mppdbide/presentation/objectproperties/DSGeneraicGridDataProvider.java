/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnGroupProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSGeneraicGridDataProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DSGeneraicGridDataProvider implements IObjectPropertyData {

    private List<Object[]> serverObjectPropertiesList;
    private IDSGridColumnProvider colProvider;
    private IDSGridColumnGroupProvider colGroupProvider;
    private List<DNIntraNodeDetailsColumn> colgrpDetails;
    private List<IDSGridDataRow> rowProviderList;
    private String propertyName;

    /**
     * Instantiates a new DS generaic grid data provider.
     *
     * @param list the list
     * @param propertyName the property name
     */
    public DSGeneraicGridDataProvider(List<Object[]> list, String propertyName) {
        this(list, propertyName, null);
    }

    /**
     * Instantiates a new DS generaic grid data provider.
     *
     * @param list the list
     * @param propertyName the property name
     * @param colgrp the colgrp
     */
    public DSGeneraicGridDataProvider(List<Object[]> list, String propertyName, List<DNIntraNodeDetailsColumn> colgrp) {
        this.serverObjectPropertiesList = list;
        this.propertyName = propertyName;
        rowProviderList = new ArrayList<IDSGridDataRow>(5);
        colgrpDetails = colgrp;
    }

    @Override
    public void init() throws DatabaseOperationException, DatabaseCriticalException {
        DSGenericGridColumnProvider provider = new DSGenericGridColumnProvider();
        provider.init(serverObjectPropertiesList);
        this.colProvider = provider;
        prepareGridRow();
        if (null != colgrpDetails) {
            DSGenericGroupedGridColumnProvider grpprovider = new DSGenericGroupedGridColumnProvider(colgrpDetails);
            colGroupProvider = grpprovider;
        }

    }

    /**
     * Prepare grid row.
     */
    private void prepareGridRow() {
        DSObjectPropertiesGridDataRow gridRowData = null;
        Object[] col = null;
        int size = serverObjectPropertiesList.size();
        for (int i = 1; i < size; i++) {
            col = serverObjectPropertiesList.get(i);

            gridRowData = new DSObjectPropertiesGridDataRow(col);
            rowProviderList.add(gridRowData);
        }

    }

    @Override
    public void close() throws DatabaseOperationException, DatabaseCriticalException {

    }

    @Override
    public List<IDSGridDataRow> getNextBatch() throws DatabaseOperationException, DatabaseCriticalException {

        return null;
    }

    @Override
    public List<IDSGridDataRow> getAllFetchedRows() {
        return rowProviderList;
    }

    @Override
    public boolean isEndOfRecords() {
        return true;
    }

    @Override
    public int getRecordCount() {
        return 0;
    }

    @Override
    public IDSGridColumnProvider getColumnDataProvider() {
        return this.colProvider;
    }

    @Override
    public String getObjectPropertyName() {
        return this.propertyName;
    }

    @Override
    public IDSGridColumnGroupProvider getColumnGroupProvider() {
        return this.colGroupProvider;
    }

    @Override
    public void preDestroy() {

    }

    @Override
    public TableMetaData getTable() {

        return null;
    }

    @Override
    public boolean getResultTabDirtyFlag() {

        return false;
    }

    @Override
    public void setResultTabDirtyFlag(boolean flag) {

    }

    @Override
    public Database getDatabse() {

        return null;
    }

    /**
     * init
     */
    @Override
    public void init(IQueryResult irq, ArrayList<DefaultParameter> debugInputValueList, boolean isCallableStmt)
            throws DatabaseOperationException, DatabaseCriticalException {
    }

    /**
     * gets next batch
     */
    @Override
    public List<IDSGridDataRow> getNextBatch(ArrayList<DefaultParameter> debugInputValueList)
            throws DatabaseOperationException, DatabaseCriticalException {
        return null;
    }

    @Override
    public void setFuncProcExport(boolean isFuncProcExport) {
    }

    @Override
    public boolean isFuncProcExport() {
        return false;
    }

}
