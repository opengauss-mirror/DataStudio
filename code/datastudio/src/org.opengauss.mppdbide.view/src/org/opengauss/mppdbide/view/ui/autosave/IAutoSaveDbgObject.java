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

package org.opengauss.mppdbide.view.ui.autosave;

import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IAutoSaveDbgObject.
 *
 * @since 3.0.0
 */
public interface IAutoSaveDbgObject {

    /**
     * Checks if is obj dirty.
     *
     * @return true, if is obj dirty
     */
    boolean isObjDirty();

    /**
     * Gets the dbg obj type.
     *
     * @return the dbg obj type
     */
    OBJECTTYPE getDbgObjType();

    /**
     * Gets the oid.
     *
     * @return the oid
     */
    long getOid();

    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the name space name.
     *
     * @return the name space name
     */
    String getNameSpaceName();

    /**
     * Sets the dirty.
     *
     * @param objDirty the new dirty
     */
    void setDirty(boolean objDirty);

    /**
     * Sets the namespace name.
     *
     * @param schemaName the new namespace name
     */
    void setNamespaceName(String schemaName);

    /**
     * Gets the debug object.
     *
     * @return the debug object
     */
    IDebugObject getDebugObject();

}
