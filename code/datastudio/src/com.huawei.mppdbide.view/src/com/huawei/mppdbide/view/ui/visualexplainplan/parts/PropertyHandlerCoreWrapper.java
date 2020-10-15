/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.visualexplainplan.parts;

import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertyHandlerCoreWrapper.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author g00408002
 * @version [DataStudio 8.0.1, 09 Dec, 2019]
 * @since 09 Dec, 2019
 */
public class PropertyHandlerCoreWrapper {
    private PropertyHandlerCore propertyHandlerCoreInstance;
    private String tabId;

    public PropertyHandlerCoreWrapper(PropertyHandlerCore propHandlerCore, String tabId) {
        this.propertyHandlerCoreInstance = propHandlerCore;
        this.tabId = tabId;
    }

    /**
     * get PropertyHandlerCoreInstance
     * 
     * @return return value
     */
    public PropertyHandlerCore getPropertyHandlerCoreInstance() {
        return propertyHandlerCoreInstance;
    }

    /**
     * get tab id
     * 
     * @return value
     */
    public String getTabId() {
        return tabId;
    }
}
