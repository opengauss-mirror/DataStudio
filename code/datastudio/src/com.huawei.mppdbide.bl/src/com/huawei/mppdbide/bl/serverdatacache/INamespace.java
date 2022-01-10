/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface INamespace.
 * 
 */

public interface INamespace {

    /**
     * Gets the functions.
     *
     * @return the functions
     */
    ObjectGroup<?> getFunctions();

    /**
     * Gets the packages.
     *
     * @return the packages
     */
    ObjectGroup<?> getPackages();

    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the qualified object name.
     *
     * @return the qualified object name
     */
    String getQualifiedObjectName();

    /**
     * Refresh debug object group.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    void refreshDebugObjectGroup() throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Gets the database.
     *
     * @return the database
     */
    Database getDatabase();

    /**
     * Gets the debug object by id.
     *
     * @param oid the oid
     * @return the debug object by id
     */
    IDebugObject getDebugObjectById(long oid);

    /**
     * Drop db object.
     *
     * @param debugObject the debug object
     * @param conn the conn
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    void dropDbObject(IDebugObject debugObject, DBConnection conn) throws MPPDBIDEException;

    /**
     * Checks if is loaded.
     *
     * @return true, if is loaded
     */
    public boolean isLoaded();

    /**
     * Sets the loaded value non loaded object.
     *
     * @param objGroup the new loaded value non loaded object
     */
    default void setLoadedValueNonLoadedObject(ObjectGroup objGroup) {
        for (Object serverObj : objGroup.getChildren()) {
            ServerObject serverObject = (ServerObject) serverObj;
            serverObject.setLoaded(true);
        }
    }

    /** 
     * gets the NewlyCreatedTable
     * 
     * @param tableName the tableName
     * @return the server object
     */
    ServerObject getNewlyCreatedTable(String tableName);

    /** 
     * gets the NewlyUpdatedTable
     * 
     * @param tableName the tableName
     * @return the server object
     */
    ServerObject getNewlyUpdatedTable(String tableName);

    /** 
     * gets the NewlyCreatedView
     * 
     * @param viewName the viewName
     * @return the server object
     */
    ServerObject getNewlyCreatedView(String viewName);

    /** 
     * gets the NewlyUpdatedView
     * 
     * @param viewName the viewName
     * @return the server object
     */
    ServerObject getNewlyUpdatedView(String viewName);

    /**
     * gets the New Create Trigger view
     *
     * @param triggerName the trigger
     * @return the server object
     */
    ServerObject getNewlyCreateTrigger(String triggerName);
}
