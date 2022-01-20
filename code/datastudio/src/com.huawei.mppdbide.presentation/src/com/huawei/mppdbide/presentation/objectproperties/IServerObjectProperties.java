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

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.List;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IServerObjectProperties.
 *
 * @since 3.0.0
 */
public interface IServerObjectProperties {

    /**
     * Gets the object name.
     *
     * @return the object name
     */
    String getObjectName();

    /**
     * Gets the header.
     *
     * @return the header
     */
    String getHeader();

    /**
     * Gets the unique ID.
     *
     * @return the unique ID
     */
    String getUniqueID();

    /**
     * Gets the all properties.
     *
     * @param conn the conn
     * @return the all properties
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    List<IObjectPropertyData> getAllProperties(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException, MPPDBIDEException;

    /**
     * Gets the parent properties.
     *
     * @param conn the conn
     * @return the parent properties
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    default IObjectPropertyData getParentProperties(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException, MPPDBIDEException {
        return null;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    Database getDatabase();
}
