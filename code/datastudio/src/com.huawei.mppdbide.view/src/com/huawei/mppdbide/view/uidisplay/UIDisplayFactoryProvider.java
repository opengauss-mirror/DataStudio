/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.uidisplay;

import com.huawei.mppdbide.view.uidisplay.uidisplayif.UIDisplayStateFactoryIf;
import com.huawei.mppdbide.view.uidisplay.uidisplayif.UIDisplayStateIf;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIDisplayFactoryProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class UIDisplayFactoryProvider {

    private static UIDisplayStateFactoryIf factoryIf = new UIDisplayStateFactoryImpl();

    /**
     * Gets the UI display state if.
     *
     * @return the UI display state if
     */
    public static UIDisplayStateIf getUIDisplayStateIf() {
        return factoryIf.getUIDisplayState();
    }

}
