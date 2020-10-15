/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.statusbar;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectBrowserStatusBarProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class ObjectBrowserStatusBarProvider {

    /**
     * Gets the status bar.
     *
     * @return the status bar
     */
    public static ObjBrowserStatusbarIf getStatusBar() {
        return ObjBrowserStatusbar.getInstance();
    }

}
