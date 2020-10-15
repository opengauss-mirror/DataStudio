/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IObjectPropertyData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IObjectPropertyData extends IDSGridDataProvider {

    /**
     * Gets the object property name.
     *
     * @return the object property name
     */
    String getObjectPropertyName();
}
