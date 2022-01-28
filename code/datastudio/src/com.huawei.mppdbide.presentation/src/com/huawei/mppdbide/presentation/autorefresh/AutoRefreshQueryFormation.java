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

package com.huawei.mppdbide.presentation.autorefresh;

import java.util.HashSet;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: AutoRefreshQueryFormation
 * 
 * @since 3.0.0
 */
public class AutoRefreshQueryFormation {
    /**
     * Gets the object to be refreshed.
     *
     * @param objectDetail the object detail
     * @param listOfObjects the list of objects
     * @return the object to be refreshed
     */
    public static void getObjectToBeRefreshed(RefreshObjectDetails objectDetail, HashSet<Object> listOfObjects) {
        String operationType = objectDetail.getOperationType();
        ServerObject serverObj = null;
        switch (operationType) {
            case MPPDBIDEConstants.CREATE_TABLE: {
                serverObj = objectDetail.getNamespace().getNewlyCreatedTable(objectDetail.getObjectName());
                break;
            }
            case MPPDBIDEConstants.ALTER_TABLE: {
                serverObj = objectDetail.getNamespace().getNewlyUpdatedTable(objectDetail.getObjectName());
                break;
            }
            case MPPDBIDEConstants.DROP_TABLE: {
                serverObj = dropTable(objectDetail);
                break;
            }
            case MPPDBIDEConstants.CREATE_VIEW: {
                serverObj = objectDetail.getNamespace().getNewlyCreatedView(objectDetail.getObjectName());
                break;
            }
            case MPPDBIDEConstants.ALTER_VIEW: {
                serverObj = objectDetail.getNamespace().getNewlyUpdatedView(objectDetail.getObjectName());
                break;
            }
            case MPPDBIDEConstants.DROP_VIEW: {
                serverObj = dropView(objectDetail);
                break;
            }
            case MPPDBIDEConstants.SET_SCHEMA_VIEW: {
                serverObj = setSchemaForView(objectDetail, listOfObjects);
                break;
            }
            case MPPDBIDEConstants.SET_SCHEMA_TABLE: {
                serverObj = setSchemaForTable(objectDetail, listOfObjects);
                break;
            }
            case MPPDBIDEConstants.CREATE_TRIGGER: {
                serverObj = objectDetail.getNamespace().getNewlyCreateTrigger(objectDetail.getObjectName());
                break;
            }
            default: {
                break;
            }
        }
        objectDetail.setObjToBeRefreshed(serverObj);
        listOfObjects.add(objectDetail);
    }

    private static ServerObject setSchemaForTable(RefreshObjectDetails objectDetail, HashSet<Object> listOfObjects) {
        ServerObject serverObj = null;
        RefreshObjectDetails objectDetailCreateView = objectDetail.getClone();
        objectDetailCreateView.setOperationType(MPPDBIDEConstants.CREATE_TABLE);
        if (objectDetailCreateView.getDesctNamespace() != null) {
            serverObj = objectDetailCreateView.getDesctNamespace()
                    .getNewlyCreatedTable(objectDetailCreateView.getObjectName());
        }
        objectDetailCreateView.setObjToBeRefreshed(serverObj);
        objectDetailCreateView.setNamespace(objectDetailCreateView.getDesctNamespace());
        listOfObjects.add(objectDetailCreateView);
        objectDetail.setOperationType(MPPDBIDEConstants.DROP_TABLE);
        serverObj = dropTable(objectDetail);
        return serverObj;
    }

    private static ServerObject setSchemaForView(RefreshObjectDetails objectDetail, HashSet<Object> listOfObjects) {
        ServerObject serverObj;
        RefreshObjectDetails objectDetailCreate = objectDetail.getClone();
        objectDetailCreate.setOperationType(MPPDBIDEConstants.CREATE_VIEW);
        serverObj = objectDetailCreate.getDesctNamespace().getNewlyCreatedView(objectDetailCreate.getObjectName());
        objectDetailCreate.setObjToBeRefreshed(serverObj);
        objectDetailCreate.setNamespace(objectDetailCreate.getDesctNamespace());
        listOfObjects.add(objectDetailCreate);
        objectDetail.setOperationType(MPPDBIDEConstants.DROP_VIEW);
        serverObj = dropView(objectDetail);
        return serverObj;
    }

    private static ServerObject dropView(RefreshObjectDetails objectDetail) {
        ServerObject serverObj = null;
        if (objectDetail.getNamespace() instanceof Namespace) {
            Namespace ns = (Namespace) objectDetail.getNamespace();
            serverObj = ns.getViewGroup().get(objectDetail.getObjectName());
            ns.getViewGroup().remove((ViewMetaData) serverObj);
        }
        return serverObj;
    }

    private static ServerObject dropTable(RefreshObjectDetails objectDetail) {
        ServerObject serverObj = null;
        if (objectDetail.getNamespace() instanceof Namespace) {
            Namespace ns = (Namespace) objectDetail.getNamespace();
            serverObj = ns.getTables().get(objectDetail.getObjectName());
            if (null != serverObj) {
                ns.getTables().remove((TableMetaData) serverObj);
                TableMetaData tableMetaData = (TableMetaData) serverObj;
                objectDetail.getNamespace().getDatabase().getSearchPoolManager().getTableTrie()
                        .remove(tableMetaData.getSearchName());
            }
        }
        return serverObj;
    }

}
