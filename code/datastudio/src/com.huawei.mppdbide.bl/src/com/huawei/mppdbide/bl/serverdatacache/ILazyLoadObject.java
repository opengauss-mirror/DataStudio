/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ILazyLoadObject.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface ILazyLoadObject {

    /**
     * Gets the database.
     *
     * @return the database
     */
    Database getDatabase();

    /**
     * Gets the load status.
     *
     * @return the load status
     */
    LOADSTATUS getLoadStatus();

    /**
     * Sets the load status.
     *
     * @param status the new load status
     */
    void setLoadStatus(LOADSTATUS status);

    /**
     * Checks if is loaded.
     *
     * @return true, if is loaded
     */
    default boolean isLoaded() {
        return getLoadStatus() == LOADSTATUS.LOADED;
    }

    /**
     * Sets the loaded.
     */
    default void setLoaded() {
        setLoadStatus(LOADSTATUS.LOADED);
    }

    /**
     * Checks if is not loaded.
     *
     * @return true, if is not loaded
     */
    default boolean isNotLoaded() {
        return getLoadStatus() == LOADSTATUS.NOT_LOADED;
    }

    /**
     * Sets the not loaded.
     */
    default void setNotLoaded() {
        setLoadStatus(LOADSTATUS.NOT_LOADED);
    }

    /**
     * Checks if is loading in progress.
     *
     * @return true, if is loading in progress
     */
    default boolean isLoadingInProgress() {
        return getLoadStatus() == LOADSTATUS.LOADING_IN_PROGRESS;
    }

    /**
     * Sets the loading in progress.
     */
    default void setLoadingInProgress() {
        setLoadStatus(LOADSTATUS.LOADING_IN_PROGRESS);

    }

    /**
     * Sets the load failed.
     */
    default void setLoadFailed() {
        setLoadStatus(LOADSTATUS.LOAD_FAILED);
    }

    /**
     * Checks if is load failed.
     *
     * @return true, if is load failed
     */
    default boolean isLoadFailed() {
        return getLoadStatus() == LOADSTATUS.LOAD_FAILED;
    }

    /**
     * Gets the all objects.
     *
     * @param conn the conn
     * @param status the status
     * @return the all objects
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    void getAllObjects(DBConnection conn, JobCancelStatus status)
            throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the oid.
     *
     * @return the oid
     */
    long getOid();

    /**
     * Gets the server.
     *
     * @return the server
     */
    Server getServer();

}
