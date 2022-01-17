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

package com.huawei.mppdbide.presentation.synonym.olap;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: Class
 * 
 * Description: The Class SynonymInfo.
 *
 * @since 3.0.0
 */
public class SynonymInfo {

    private String owner;
    private String synonymName;
    private String objectOwner;
    private String objectName;
    private boolean replaceIfExist;
    private Namespace namespace;
    private String objectType;

    /**
     * Instantiates a new z sequence info.
     *
     * @param zSynonymObjectGroup the z synonym object group
     */
    public SynonymInfo(SynonymObjectGroup synonymObjectGroup) {
        this.namespace = (Namespace) synonymObjectGroup.getParent();
    }

    /**
     * Gets the namespace.
     *
     * @return the namespace
     * @Title: getNamespace
     * @Description: get the namespace
     */
    public Namespace getNamespace() {
        return namespace;
    }

    /**
     * Gets the z namespace name.
     *
     * @return get the namespace name
     * @Title: getSynonymName
     * @Description: get synonym name
     */
    public String getNameSpaceName() {
        return namespace.getDisplayName();
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     * @Title: getSynonymName
     * @Description: get the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner.
     *
     * @param owner the owner
     * @Title: setSynonymName
     * @Description: set the owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets the synonym name.
     *
     * @return the synonym name
     * @Title: getSynonymName
     * @Description: get synonym name
     */
    public String getSynonymName() {
        return synonymName;
    }

    /**
     * Sets the synonym name.
     *
     * @param synonymName the synonym name
     * @Title: setSynonymName
     * @Description: set the synonym name
     */
    public void setSynonymName(String synonymName) {
        this.synonymName = synonymName;
    }

    /**
     * Gets the object owner.
     *
     * @return the object owner
     * @Title: getObjectOwner
     * @Description: get the object owner
     */
    public String getObjectOwner() {
        return objectOwner;
    }

    /**
     * Sets the object owner.
     *
     * @param objectOwner the object owner
     * @Title: setObjectOwner
     * @Description: set the object owner
     */
    public void setObjectOwner(String objectOwner) {
        this.objectOwner = objectOwner;
    }

    /**
     * Gets the object type.
     *
     * @return the object type
     * @Title: getObjectType
     * @Description: get the object type
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Sets the object owner.
     *
     * @param objectOwner the object owner
     * @Title: setObjectOwner
     * @Description: set the object owner
     */
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * Gets the object name.
     *
     * @return the object name
     * @Title: getObjectName
     * @Description: get the object name
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Sets the object name.
     *
     * @param objectName the object name
     * @Title: setObjectName
     * @Description: set the object name
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Checks if is replace if exist.
     *
     * @return true, if selected replace if exist button
     * @Title: isReplaceIfExist
     * @Description: check if select replace if exist button
     */
    public boolean isReplaceIfExist() {
        return replaceIfExist;
    }

    /**
     * Sets the replace if exist.
     *
     * @param replaceIfExist the new replace if exist
     * @Title: setReplaceIfExist
     * @Description: set replace if exist
     */
    public void setReplaceIfExist(boolean replaceIfExist) {
        this.replaceIfExist = replaceIfExist;
    }

    /**
     * Checks if is field empty.
     *
     * @param name the name
     * @return true, if is field empty
     */
    public boolean isFieldEmpty(String name) {
        return name.isEmpty();
    }

    /**
     * Generate create synonym sql.
     *
     * @return String generated sql
     * @Title: generateCreateSynonymSql
     * @Description: generate create synonym sql
     */
    public String generateCreateSynonymSql() {
        StringBuffer queryBuff = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        queryBuff.append("CREATE");
        if (isReplaceIfExist()) {
            queryBuff.append(" OR REPLACE ");
        }
        queryBuff.append(MPPDBIDEConstants.LINE_SEPARATOR);
        queryBuff.append(" SYNONYM ");

        if (!isFieldEmpty(getObjectOwner())) {
            queryBuff.append(ServerObject.getQualifiedSimpleObjectName(getObjectOwner())).append(".");
        }
        if (!isFieldEmpty(getSynonymName())) {
            queryBuff.append(ServerObject.getQualifiedSimpleObjectName(getSynonymName()));
        }

        queryBuff.append(MPPDBIDEConstants.LINE_SEPARATOR);
        queryBuff.append(" FOR ");
        queryBuff.append(MPPDBIDEConstants.LINE_SEPARATOR);
        if (!isFieldEmpty(getObjectOwner())) {
            queryBuff.append(ServerObject.getQualifiedSimpleObjectName(getObjectOwner())).append(".");
        }

        if (!isFieldEmpty(getObjectName())) {
            queryBuff.append(ServerObject.getQualifiedSimpleObjectName(getObjectName()));
        }
        queryBuff.append(MPPDBIDEConstants.LINE_SEPARATOR);
        return queryBuff.toString();
    }

}
