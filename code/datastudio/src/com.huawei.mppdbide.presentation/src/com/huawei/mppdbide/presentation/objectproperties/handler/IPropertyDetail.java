/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties.handler;

import java.util.List;

import com.huawei.mppdbide.presentation.objectproperties.IObjectPropertyData;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IPropertyDetail.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IPropertyDetail {

    /**
     * Gets the property core.
     *
     * @return the property core
     */
    PropertyHandlerCore getPropertyCore();

    /**
     * Gets the header.return the Property window Title
     *
     * @return the header
     */
    String getHeader();

    /**
     * Objectproperties.return the object Properties detail
     *
     * @return the list
     */
    List<IObjectPropertyData> objectproperties();

    /**
     * Gets the unique ID.return the Property window UID
     *
     * @return the unique ID
     */
    String getUniqueID();

    /**
     * Gets the parent property.
     *
     * @return the parent property
     */
    default IObjectPropertyData getParentProperty() {
        return null;

    }

}
