/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
 * @since 3.0.0
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
