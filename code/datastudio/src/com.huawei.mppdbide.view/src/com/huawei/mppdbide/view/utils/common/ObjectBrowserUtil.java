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

package com.huawei.mppdbide.view.utils.common;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.huawei.mppdbide.view.data.DSViewApplicationObjectManager;
import com.huawei.mppdbide.view.ui.uiif.ObjectBrowserIf;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectBrowserUtil.
 *
 * @since 3.0.0
 */
public abstract class ObjectBrowserUtil {

    /**
     * Gets the object browser model.
     *
     * @return the object browser model
     */
    public static ObjectBrowserIf getObjectBrowserModel() {
        MPart part = DSViewApplicationObjectManager.getInstance().getPartService()
                .findPart(UIConstants.UI_PART_OBJECT_BROWSER_ID);
        if (part == null) {
            return null;
        }
        if (null == part.getObject()) {
            DSViewApplicationObjectManager.getInstance().getPartService().activate(part);
        }

        if (!(part.getObject() instanceof ObjectBrowserIf)) {
            return null;
        }

        return (ObjectBrowserIf) part.getObject();
    }
}
