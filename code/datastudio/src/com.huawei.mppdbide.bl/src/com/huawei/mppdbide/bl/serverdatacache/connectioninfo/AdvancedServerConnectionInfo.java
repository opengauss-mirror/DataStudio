/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.connectioninfo;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * 
 * Title: class
 * 
 * Description: The Class AdvancedServerConnectionInfo.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public final class AdvancedServerConnectionInfo {

    @Expose
    private Set<String> schemaExclusionList = new LinkedHashSet<String>();
    @Expose
    private Set<String> schemaInclusionList = new LinkedHashSet<String>();
    @Expose
    private int loadLimit = 0;
    @Expose
    private boolean privilegeBasedObAcess;
    private Set<String> modifiedSchemaExclusionList = new LinkedHashSet<String>();
    private Set<String> modifiedSchemaInclusionList = new LinkedHashSet<String>();
    @Expose
    private boolean canLoadChildObj = true;

    /**
     * Gets the schema exclusion list.
     *
     * @return the schema exclusion list
     */
    public Set<String> getSchemaExclusionList() {
        return new LinkedHashSet<String>(schemaExclusionList);
    }

    /**
     * Sets the schema exclusion list.
     *
     * @param schemaExclusionList the new schema exclusion list
     */
    public void setSchemaExclusionList(Set<String> schemaExclusionList) {
        this.schemaExclusionList = schemaExclusionList;
        this.modifiedSchemaExclusionList.addAll(this.schemaExclusionList);
    }

    /**
     * Gets the schema inclusion list.
     *
     * @return the schema inclusion list
     */
    public Set<String> getSchemaInclusionList() {
        return new LinkedHashSet<String>(schemaInclusionList);
    }

    /**
     * Sets the schema inclusion list.
     *
     * @param schemaInclusionList the new schema inclusion list
     */
    public void setSchemaInclusionList(Set<String> schemaInclusionList) {
        this.schemaInclusionList = schemaInclusionList;
        this.modifiedSchemaInclusionList.addAll(this.schemaInclusionList);
    }

    /**
     * Gets the load limit.
     *
     * @return the load limit
     */
    public int getLoadLimit() {
        return loadLimit;
    }

    /**
     * Sets the load limit.
     *
     * @param loadLimit the new load limit
     */
    public void setLoadLimit(int loadLimit) {
        this.loadLimit = loadLimit;
    }

    /**
     * Checks if is privilege based ob acess.
     *
     * @return true, if is privilege based ob acess
     */
    public boolean isPrivilegeBasedObAcess() {
        return privilegeBasedObAcess;
    }

    /**
     * Sets the privilege based ob acess.
     *
     * @param privilegeBasedObAcess the new privilege based ob acess
     */
    public void setPrivilegeBasedObAcess(boolean privilegeBasedObAcess) {
        this.privilegeBasedObAcess = privilegeBasedObAcess;
    }

    /**
     * getModifiedSchemaExclusionList
     * 
     * @return schema list value
     */
    public Set<String> getModifiedSchemaExclusionList() {
        return modifiedSchemaExclusionList;
    }

    /**
     * setModifiedSchemaExclusionList
     * 
     * @param modifiedSchemaExclusionList set value
     */
    public void setModifiedSchemaExclusionList(Set<String> modifiedSchemaExclusionList) {
        this.modifiedSchemaExclusionList = modifiedSchemaExclusionList;
    }

    /**
     * getModifiedSchemaInclusionList
     * 
     * @return schema list value
     */
    public Set<String> getModifiedSchemaInclusionList() {
        return modifiedSchemaInclusionList;
    }

    /**
     * setModifiedSchemaInclusionList
     * 
     * @param modifiedSchemaInclusionList set value
     */
    public void setModifiedSchemaInclusionList(Set<String> modifiedSchemaInclusionList) {
        this.modifiedSchemaInclusionList = modifiedSchemaInclusionList;
    }

    /**
     * isCanLoadChildObj
     * 
     * @return isCanLoadChildObj can load child obj
     */
    public boolean isCanLoadChildObj() {
        return canLoadChildObj;
    }

    /**
     * setCanLoadChildObj
     * 
     * @param canLoadChildObj param can load child obj
     */
    public void setCanLoadChildObj(boolean canLoadChildObj) {
        this.canLoadChildObj = canLoadChildObj;
    }

}
