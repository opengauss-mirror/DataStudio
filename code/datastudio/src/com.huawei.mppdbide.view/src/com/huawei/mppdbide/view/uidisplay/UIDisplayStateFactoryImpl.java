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
 * Description: The Class UIDisplayStateFactoryImpl.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class UIDisplayStateFactoryImpl implements UIDisplayStateFactoryIf {
    /**
     * Gets the UI display state.
     *
     * @return the UI display state
     */
    @Override
    public UIDisplayStateIf getUIDisplayState() {
        return UIDisplayState.getInstaDisplayState();
    }
}
