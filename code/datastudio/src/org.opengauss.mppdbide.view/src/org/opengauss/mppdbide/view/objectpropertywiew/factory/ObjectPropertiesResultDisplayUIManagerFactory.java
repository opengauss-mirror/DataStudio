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

package org.opengauss.mppdbide.view.objectpropertywiew.factory;

import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.SynonymMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.UserRole;
import org.opengauss.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import org.opengauss.mppdbide.view.objectpropertywiew.ViewObjectPropertiesResultDisplayUIManager;
import org.opengauss.mppdbide.view.userrole.UserRolePropertiesResultDisplayUIManager;

/**
 * Title: ObjectPropertiesResultDisplayUIManagerFactory
 * 
 * Description:A factory for creating ObjectPropertiesResultDisplayUIManager
 * objects.
 * 
 * @since 3.0.0
 */
public final class ObjectPropertiesResultDisplayUIManagerFactory {

    private ObjectPropertiesResultDisplayUIManagerFactory() {

    }

    /**
     * Gets the u imanager object.
     *
     * @param core the core
     * @param object the object
     * @return the u imanager object
     */
    public static ViewObjectPropertiesResultDisplayUIManager getUImanagerObject(PropertyHandlerCore core,
            Object object) {
        if (object instanceof UserRole) {
            return new UserRolePropertiesResultDisplayUIManager(core);
        }
        if (object instanceof SynonymMetaData) {
            return new ViewObjectPropertiesResultDisplayUIManager(core);
        }
        if (object instanceof ServerObject) {
            return new ViewObjectPropertiesResultDisplayUIManager(core);
        }
        if (object instanceof Server) {
            return new ViewObjectPropertiesResultDisplayUIManager(core);
        }
        return null;
    }
}
