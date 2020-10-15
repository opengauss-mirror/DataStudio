/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
