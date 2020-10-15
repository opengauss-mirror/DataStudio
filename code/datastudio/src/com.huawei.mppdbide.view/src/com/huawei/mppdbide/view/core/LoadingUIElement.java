/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import org.eclipse.swt.graphics.Image;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.icon.IconUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class LoadingUIElement.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class LoadingUIElement {
    private static final String DISPLAY_NAME = MessageConfigLoader.getProperty(IMessagesConstants.LOADING_NS);
    private static final Image DISPLAY_IMAGE = IconUtility.getIconImage(IconUtility.ICO_LOAD, LoadingUIElement.class);

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    /**
     * Gets the displayimage.
     *
     * @return the displayimage
     */
    public Image getDisplayimage() {
        return DISPLAY_IMAGE;
    }
}
