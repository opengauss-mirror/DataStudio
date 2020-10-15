/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.autosave;

import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IAutoSaveDbgObject.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
