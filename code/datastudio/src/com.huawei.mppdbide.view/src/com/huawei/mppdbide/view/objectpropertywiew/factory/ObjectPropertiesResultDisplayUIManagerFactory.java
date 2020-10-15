/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.objectpropertywiew.factory;

import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.SynonymMetaData;
import com.huawei.mppdbide.bl.serverdatacache.UserRole;
import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import com.huawei.mppdbide.view.objectpropertywiew.ViewObjectPropertiesResultDisplayUIManager;
import com.huawei.mppdbide.view.userrole.UserRolePropertiesResultDisplayUIManager;

/**
 * Title: ObjectPropertiesResultDisplayUIManagerFactory
 * 
 * Description:A factory for creating ObjectPropertiesResultDisplayUIManager
 * objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
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
