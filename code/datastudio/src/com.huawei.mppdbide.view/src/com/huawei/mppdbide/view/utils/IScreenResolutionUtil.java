/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Title: IScreenResolutionUtil
 * 
 * Description:IScreenResolutionUtil
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 11-Oct-2019]
 * @since 11-Oct-2019
 */

public interface IScreenResolutionUtil {

    /**
     * The Constant screenSize.
     */
    static final Dimension SCREENSIZE = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * Gets the screen height.
     *
     * @return the screen height
     */
    public static int getScreenHeight() {
        return SCREENSIZE.height;
    }

    /**
     * Gets the screen width.
     *
     * @return the screen width
     */
    public static int getScreenWidth() {
        return SCREENSIZE.width;
    }

}
