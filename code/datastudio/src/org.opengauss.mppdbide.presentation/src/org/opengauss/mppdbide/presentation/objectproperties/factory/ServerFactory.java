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

package org.opengauss.mppdbide.presentation.objectproperties.factory;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.SynonymMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.UserRole;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.presentation.PropertyOperationType;
import org.opengauss.mppdbide.presentation.objectproperties.ConnectionPropertiesImpl;
import org.opengauss.mppdbide.presentation.objectproperties.IServerObjectProperties;
import org.opengauss.mppdbide.presentation.objectproperties.PropertiesDatabaseImpl;
import org.opengauss.mppdbide.presentation.objectproperties.PropertiesPartitionTableImpl;
import org.opengauss.mppdbide.presentation.objectproperties.PropertiesSynonymImpl;
import org.opengauss.mppdbide.presentation.objectproperties.PropertiesTableImpl;
import org.opengauss.mppdbide.presentation.objectproperties.PropertiesUserRoleImpl;
import org.opengauss.mppdbide.presentation.objectproperties.PropertiesViewImpl;

/**
 * 
 * Title: ServerFactory
 * 
 * Description: The Class ServerFactory.
 *
 * @since 3.0.0
 */
public class ServerFactory {

    /**
     * Gets the object.
     *
     * @param obj the obj
     * @param propType the prop type
     * @return the object
     */
    public IServerObjectProperties getObject(Object obj, PropertyOperationType propType) {
        if (obj == null) {
            return null;
        }
        if (PropertyOperationType.PROPERTY_OPERATION_VIEW == propType) {
            return handleViewObjectProperties(obj);
        }
        return null;
    }

    /**
     * Handle view object properties.
     *
     * @param obj the obj
     * @return the i server object properties
     */
    protected IServerObjectProperties handleViewObjectProperties(Object obj) {
        if (obj instanceof Database) {
            return new PropertiesDatabaseImpl(obj);

        } else if (obj instanceof PartitionTable) {
            return new PropertiesPartitionTableImpl(obj);
        } else if (obj instanceof TableMetaData) {
            return new PropertiesTableImpl(obj);
        } else if (obj instanceof ViewMetaData) {
            return new PropertiesViewImpl(obj);
        } else if (obj instanceof SynonymMetaData) {
            return new PropertiesSynonymImpl(obj);
        } else if (obj instanceof Server) {
            return new ConnectionPropertiesImpl(obj);
        } else if (obj instanceof UserRole) {
            return new PropertiesUserRoleImpl(obj);
        }

        return null;
    }
}
