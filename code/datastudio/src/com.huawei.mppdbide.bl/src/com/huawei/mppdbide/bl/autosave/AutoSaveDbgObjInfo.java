/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.autosave;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class AutoSaveDbgObjInfo.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class AutoSaveDbgObjInfo {

    @SerializedName("oid")
    private long oid;

    @SerializedName("name")
    private String name;

    @SerializedName("schema")
    private String schemaName;

    @SerializedName("objtype")
    private OBJECTTYPE objType;

    @SerializedName("isdirty")
    private boolean isDirty;

    /**
     * Gets the oid.
     *
     * @return the oid
     */
    public long getOid() {
        return oid;
    }

    /**
     * Gets the obj type.
     *
     * @return the obj type
     */
    public OBJECTTYPE getObjType() {
        return objType;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the oid.
     *
     * @param oid the new oid
     */
    public void setOid(long oid) {
        this.oid = oid;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the schema name.
     *
     * @return the schema name
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Sets the schema name.
     *
     * @param schemaName the new schema name
     */
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * Sets the obj type.
     *
     * @param objecttype the new obj type
     */
    public void setObjType(OBJECTTYPE objecttype) {
        this.objType = objecttype;
    }

    /**
     * Checks if is dirty.
     *
     * @return true, if is dirty
     */
    public boolean isDirty() {
        return isDirty;
    }

    /**
     * Sets the dirty.
     *
     * @param isDirty1 the new dirty
     */
    public void setDirty(boolean isDirty1) {
        this.isDirty = isDirty1;
    }

}
