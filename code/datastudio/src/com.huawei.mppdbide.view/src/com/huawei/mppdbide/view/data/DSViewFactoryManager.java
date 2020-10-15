/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.data;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSViewFactoryManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class DSViewFactoryManager {

    /**
     * Gets the DS view application object manager.
     *
     * @return the DS view application object manager
     */
    public static DSViewApplicationObjectManagerIf getDSViewApplicationObjectManager() {
        return DSViewApplicationObjectManager.getInstance();
    }

}
