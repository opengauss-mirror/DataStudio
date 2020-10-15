/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.terminal.resulttab;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IResultTab.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IResultTab {

    /**
     * Checks if is result tab dirty.
     *
     * @return true, if is result tab dirty
     */
    public boolean isResultTabDirty();

    /**
     * Pre destroy.
     *
     * @return true, if successful
     */
    public boolean preDestroy();

    /**
     * Dispose.
     */
    public void dispose();

    /**
     * Gets the parent tab manager.
     *
     * @return the parent tab manager
     */
    public ResultTabManager getParentTabManager();
}
