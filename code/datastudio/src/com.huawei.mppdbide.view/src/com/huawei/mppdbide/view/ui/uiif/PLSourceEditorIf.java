/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.uiif;

import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface PLSourceEditorIf.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface PLSourceEditorIf {

    /**
     * Destroy.
     */
    void destroy();

    /**
     * Gets the debug object type.
     *
     * @return the debug object type
     */
    OBJECTTYPE getDebugObjectType();

}
