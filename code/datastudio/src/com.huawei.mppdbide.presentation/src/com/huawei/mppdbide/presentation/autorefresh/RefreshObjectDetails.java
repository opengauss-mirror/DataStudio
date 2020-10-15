/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.autorefresh;

import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * Title: RefreshObjectDetails
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 03-Feb-2020]
 * @since 03-Feb-2020
 */

public class RefreshObjectDetails {
    private String operationType;
    private Object parent;
    private String objectName;
    private INamespace namespace;
    private ServerObject objToBeRefreshed;
    private INamespace desctNamespace;

    public INamespace getDesctNamespace() {
        return desctNamespace;
    }

    public void setDesctNamespace(INamespace desctNamespace) {
        this.desctNamespace = desctNamespace;
    }

    public ServerObject getObjToBeRefreshed() {
        return objToBeRefreshed;
    }

    public void setObjToBeRefreshed(ServerObject objToBeRefreshed) {
        this.objToBeRefreshed = objToBeRefreshed;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Object getParent() {
        return parent;
    }

    public INamespace getNamespace() {
        return namespace;
    }

    public void setNamespace(INamespace namespace) {
        this.namespace = namespace;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Clone.
     *
     * @return the refresh object details
     */
    public RefreshObjectDetails getClone() {
        RefreshObjectDetails refreshObj = new RefreshObjectDetails();
        refreshObj.setOperationType(this.operationType);
        refreshObj.setParent(this.parent);
        refreshObj.setObjectName(this.objectName);
        refreshObj.setNamespace(this.namespace);
        refreshObj.setObjToBeRefreshed(this.objToBeRefreshed);
        refreshObj.setDesctNamespace(this.desctNamespace);
        return refreshObj;
    }
}
