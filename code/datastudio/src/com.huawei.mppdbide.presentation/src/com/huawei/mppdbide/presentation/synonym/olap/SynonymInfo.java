/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author gWX773294
 * @version
 * @since Nov 6, 2019
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
     * @Author: gWX773294
     * @Date: Nov 7, 2019
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
     * @Author: gWX773294
     * @Date: Nov 7, 2019
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
     * @Author: gWX773294
     * @Date: Nov 15, 2019
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
     * @Author: gWX773294
     * @Date: Nov 15, 2019
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
     * @Author: gWX773294
     * @Date: Nov 7, 2019
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
     * @Author: gWX773294
     * @Date: Nov 7, 2019
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
     * @Author: gWX773294
     * @Date: Nov 7, 2019
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
     * @Author: gWX773294
     * @Date: Nov 7, 2019
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
     * @Author: c00550043
     * @Date: Mar9 , 2020
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
     * @Author: gWX773294
     * @Date: Nov 7, 2019
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
     * @Author: gWX773294
     * @Date: Nov 7, 2019
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
     * @Author: gWX773294
     * @Date: Nov 7, 2019
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
     * @Author: gWX773294
     * @Date: Nov 7, 2019
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
     * @Author: gWX773294
     * @Date: Nov 7, 2019
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
     * @Author: gWX773294
     * @Date: Nov 7, 2019
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
