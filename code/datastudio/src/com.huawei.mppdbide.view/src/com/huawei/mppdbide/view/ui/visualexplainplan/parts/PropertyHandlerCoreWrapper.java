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

package com.huawei.mppdbide.view.ui.visualexplainplan.parts;

import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertyHandlerCoreWrapper.
 *
 * @since 3.0.0
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
