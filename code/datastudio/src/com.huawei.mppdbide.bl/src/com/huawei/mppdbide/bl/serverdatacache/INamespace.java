/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
}
