/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.uiif;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ObjectBrowserIf.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface ObjectBrowserIf {

    /**
     * Sets the selection.
     *
     * @param selectObject the new selection
     */
    void setSelection(Object selectObject);

    /**
     * Refresh object.
     *
     * @param obj the obj
     */
    void refreshObject(Object obj);
}
