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

package org.opengauss.mppdbide.presentation.autorefresh;

import org.opengauss.mppdbide.bl.serverdatacache.INamespace;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;

/**
 * Title: RefreshObjectDetails
 * 
 * @since 3.0.0
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
