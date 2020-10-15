/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IViewMetaData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IViewMetaData {

    /**
     * Gets the view path qualified name.
     *
     * @return the view path qualified name
     */
    String getViewPathQualifiedName();

    /**
     * Gets the database.
     *
     * @return the database
     */
    Database getDatabase();

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
     * Gets the namespace name.
     *
     * @return the namespace name
     */
    String getNameSpaceName();

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    ServerObject getParent();

    /**
     * Gets the namespace qualified name.
     *
     * @return the namespace qualified name
     */
    String getNamespaceQualifiedName();

    /**
     * Rename.
     *
     * @param addInfo the add info
     * @param connection the connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    void rename(String addInfo, DBConnection connection) throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Sets the namespace to.
     *
     * @param addInfo the add info
     * @param connection the connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    void setNamespaceTo(String addInfo, DBConnection connection)
            throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Drop view.
     *
     * @param connection the connection
     * @param isAppendCascade the is append cascade
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    void dropView(DBConnection connection, boolean isAppendCascade)
            throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Checks if is db connected.
     *
     * @return true, if is db connected
     */
    boolean isDbConnected();

    /**
     * Gets the ddl.
     *
     * @param db the db
     * @return the ddl
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    String getDDL(Database db) throws MPPDBIDEException;

    /**
     * Checks if is view code loaded.
     *
     * @return true, if is view code loaded
     */
    boolean isViewCodeLoaded();
}
