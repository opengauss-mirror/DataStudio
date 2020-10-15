/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConvertToObjectPropertyData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class ConvertToObjectPropertyData {

    /**
     * Gets the object property data.
     *
     * @param tabName the tab name
     * @param properties the properties
     * @param table the table
     * @param objectPropertyObject the object property object
     * @return the object property data
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public List<IObjectPropertyData> getObjectPropertyData(List<String> tabName, List<List<String[]>> properties,
            ServerObject table, IServerObjectProperties objectPropertyObject)
            throws DatabaseOperationException, DatabaseCriticalException {
        int size = tabName.size();
        List<IObjectPropertyData> objectPropData = new ArrayList<IObjectPropertyData>();
        for (int i = 0; i < size; i++) {
            DSObjectPropertiesGridDataProvider prop = new DSObjectPropertiesGridDataProvider(properties.get(i),
                    tabName.get(i), table, objectPropertyObject);
            prop.init();
            objectPropData.add(prop);
        }
        return objectPropData;
    }

    /**
     * Gets the object property data generic.
     *
     * @param tabName the tab name
     * @param properties the properties
     * @return the object property data generic
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static List<IObjectPropertyData> getObjectPropertyDataGeneric(List<String> tabName,
            List<List<Object[]>> properties) throws DatabaseOperationException, DatabaseCriticalException {
        int size = tabName.size();
        List<IObjectPropertyData> objectPropData = new ArrayList<IObjectPropertyData>();
        for (int i = 0; i < size; i++) {
            DSGeneraicGridDataProvider prop = new DSGeneraicGridDataProvider(properties.get(i), tabName.get(i));
            prop.init();
            objectPropData.add(prop);
        }
        return objectPropData;
    }

    /**
     * Gets the object property data generic grouped column.
     *
     * @param tabName the tab name
     * @param data the data
     * @param colgrp the colgrp
     * @return the object property data generic grouped column
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static List<IObjectPropertyData> getObjectPropertyDataGenericGroupedColumn(List<String> tabName,
            Map<String, List<Object>> data, List<DNIntraNodeDetailsColumn> colgrp)
            throws DatabaseOperationException, DatabaseCriticalException {
        List<IObjectPropertyData> objectPropData = new ArrayList<IObjectPropertyData>();
        ArrayList<Object[]> objects = new ArrayList<Object[]>(data.size());
        ArrayList<String> colName = new ArrayList<String>(5);

        for (DNIntraNodeDetailsColumn dncolumns : colgrp) {
            colName.addAll(dncolumns.getColnames());
        }

        objects.add(colName.toArray());

        for (Map.Entry<String, List<Object>> entry : data.entrySet()) {
            String dnName = entry.getKey();
            List<Object> dnpropVals = entry.getValue();

            Object[] allvals = new Object[dnpropVals.size() + 1];
            allvals[0] = dnName;
            int index = 1;
            for (Object obj : dnpropVals) {
                allvals[index] = obj;
                index++;
            }

            objects.add(allvals);
        }

        DSGeneraicGridDataProvider prop = new DSGeneraicGridDataProvider(objects, tabName.get(0), colgrp);

        prop.init();
        objectPropData.add(prop);
        return objectPropData;
    }

}
