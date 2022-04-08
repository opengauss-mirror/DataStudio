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

package org.opengauss.mppdbide.view.batchdrop;

import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.jface.viewers.StructuredSelection;

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.SequenceMetadata;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.SynonymMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.groups.UserRoleObjectGroup;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.presentation.grid.batchdrop.BatchDropDataProvider;
import org.opengauss.mppdbide.presentation.grid.batchdrop.BatchDropDataRow;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.observer.DSEvent;
import org.opengauss.mppdbide.utils.observer.DSEventTable;
import org.opengauss.mppdbide.utils.observer.IDSGridUIListenable;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.connection.ObjectBrowserOperationUIWorkerJob;
import org.opengauss.mppdbide.view.ui.terminal.BatchDropDisplayUIManager;
import org.opengauss.mppdbide.view.ui.uiif.ObjectBrowserIf;
import org.opengauss.mppdbide.view.utils.common.ObjectBrowserUtil;

/**
 * Title: class Description: The Class BatchDropWorkerJob.
 *
 * @since 3.0.0
 */
public class BatchDropWorkerJob extends ObjectBrowserOperationUIWorkerJob {
    /** 
     * The Constant JOB_NOT_EXIST_ERR_MSG_OPEN_GAUSS. 
     */
    private static final String JOB_NOT_EXIST_ERR_MSG_OPEN_GAUSS = "Can not find job id";

    private BatchDropDataProvider dataProvider;

    private List<ServerObject> objectsToDropParent;

    private DSEventTable eventTable;

    private String statusElement;

    private boolean isCancelFlow;

    private BatchDropDisplayUIManager uiManager;

    private List<UserRoleObjectGroup> userRoleToDropParent;

    /**
     * Instantiates a new batch drop worker job.
     *
     * @param name the name
     * @param obj the obj
     * @param msg the msg
     * @param family the family
     * @param ui the ui
     */
    public BatchDropWorkerJob(String name, ServerObject obj, String msg, Object family, BatchDropDisplayUIManager ui) {
        super(name, obj, msg, family);
        this.isCancelFlow = false;
        this.uiManager = ui;
    }

    /**
     * Inits the.
     *
     * @param dataProvider1 the data provider 1
     * @param objctsToDropParent the objcts to drop parent
     * @param usrRoleToDrpParent the usr role to drp parent
     * @param zUserRoleObjectGroup the z user role object group
     * @param zRoleToDropParent the z role to drop parent
     * @param zTableElementsToDropParent the z table elements to drop parent
     * @param eventTable2 the event table 2
     * @param statusEle the status ele
     */
    public void init(BatchDropDataProvider dataProvider1, List<ServerObject> objctsToDropParent,
            List<UserRoleObjectGroup> usrRoleToDrpParent, DSEventTable eventTable2, String statusEle) {
        this.dataProvider = dataProvider1;
        this.objectsToDropParent = objctsToDropParent;
        this.userRoleToDropParent = usrRoleToDrpParent;
        this.eventTable = eventTable2;
        this.statusElement = statusEle;
    }

    /**
     * Gets the success msg for OB status bar.
     *
     * @return the success msg for OB status bar
     */
    @Override
    protected String getSuccessMsgForOBStatusBar() {
        String message = MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_JOB_FINISHED_STATUS,
                statusElement);
        return message;
    }

    /**
     * Gets the object browser refresh item.
     *
     * @return the object browser refresh item
     */
    @Override
    protected ServerObject getObjectBrowserRefreshItem() {
        return null;
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws Exception the exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        if (conn == null) {
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_CONN_FAILED, null));
            return null;
        }

        dataProvider.startExecute(conn);
        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        refresh();
        if (!isCancelFlow) {
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getInfo(getSuccessMsgForOBStatusBar()));
        }
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        handleException(exception);
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        handleException(exception);
    }

    /**
     * On pre setup failure.
     *
     * @param exception the exception
     */
    @Override
    public void onPreSetupFailure(MPPDBIDEException exception) {
        uiManager.userCancelledOperation();
    }

    private void handleException(MPPDBIDEException exception) {
        MPPDBIDELoggerUtility.error("BatchDropWorkerJob: handleException.", exception);
        String message = MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_JOB_FAILURE_STATUS,
                statusElement);
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
    }

    /**
     * Handle stop.
     */
    public void handleStop() {
        try {
            if (conn != null) {
                conn.cancelQuery();
                dataProvider.rollbackAndNotifyUIMgr(conn);
            }
        } catch (DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("BatchDropWorkerJob: handle stop failed.", exception);
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("BatchDropWorkerJob: handle stop failed.", exception);
        } finally {
            refresh();
        }
    }

    /**
     * Canceling.
     */
    @Override
    protected void canceling() {
        super.canceling();
        isCancelFlow = true;
        if (null != eventTable) {
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_JOB_CANCEL, null));
        }
    }

    private void refresh() {
        if (null == dataProvider) {
            /* Batch drop window is already closed. No need to refresh */
            return;
        }
        boolean isOneObjectDropped = false;
        List<IDSGridDataRow> rows = dataProvider.getAllFetchedRows();
        int objectDropped = dataProvider.getTotalObjectCnt();

        BatchDropDataRow row = null;
        if (this.getDatabase() != null && this.getDatabase().isConnected()) {
            for (int objCnt = 0; objCnt < objectDropped; objCnt++) {
                row = (rows.size() > objCnt) ? (BatchDropDataRow) rows.get(objCnt) : null;
                if (null != row && row.isDropped()) {
                    remove(row.getServerObject());
                    isOneObjectDropped = true;
                }
            }

            /* Set selection to nothing */
            if (isOneObjectDropped) {
                handleRefresh(rows);
            }
        }
    }

    private void handleRefresh(List<IDSGridDataRow> rows) {
        BatchDropDataRow row = null;
        ObjectBrowserIf objectBrowserModel = ObjectBrowserUtil.getObjectBrowserModel();
        if (null != objectBrowserModel) {
            objectBrowserModel.setSelection(StructuredSelection.EMPTY);
        }

        refreshServerObject(rows, objectBrowserModel);

        // support UserRoleObjectGroup by Martin
        int element = 0;
        boolean refreshed = false;
        UserRoleObjectGroup droppedUserRole = null;
        if (isUserRoleObjectGroupDrop()) {
            for (UserRoleObjectGroup obj : userRoleToDropParent) {
                row = (BatchDropDataRow) rows.get(element++);
                if (row.isDropped()) {
                    refreshed = true;
                    droppedUserRole = (UserRoleObjectGroup) obj;
                }
            }
        }

        if (null != droppedUserRole && refreshed == true) {
            refeshOLAPUserGroup(objectBrowserModel, droppedUserRole);
        }

    }

    private boolean isUserRoleObjectGroupDrop() {
        return null != userRoleToDropParent && userRoleToDropParent.size() > 0;
    }

    /**
     * Refesh OLAP user group.
     * 
     * @Title: refeshOLAPUserGroup
     * @param objectBrowserModel the object browser model
     * @param droppedUserRole the dropped user role
     */
    private void refeshOLAPUserGroup(ObjectBrowserIf objectBrowserModel, UserRoleObjectGroup droppedUserRole) {
        try {
            droppedUserRole.getServer().refreshUserRoleObjectGroup();
        } catch (MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.error("DropUserRoleWorkerJob: refresh failed.", exception);
        }
        if (null != objectBrowserModel) {
            objectBrowserModel.refreshObject(droppedUserRole);
        }
    }

    /**
     * Refresh server object.
     *
     * @param rows the rows
     * @param objectBrowserModel the object browser model
     */
    private void refreshServerObject(List<IDSGridDataRow> rows, ObjectBrowserIf objectBrowserModel) {
        BatchDropDataRow row;
        int cnt = 0;
        for (ServerObject obj : objectsToDropParent) {
            row = (BatchDropDataRow) rows.get(cnt++);
            if (row.isDropped()) {
                if (null != objectBrowserModel) {
                    objectBrowserModel.refreshObject(obj);
                }
            }
        }
    }

    private void remove(ServerObject obj) {
        if (obj instanceof Namespace) {
            ((Database) (obj.getParent())).remove(obj);
        } else if (obj instanceof TableMetaData || obj instanceof SequenceMetadata || obj instanceof ViewMetaData
                || obj instanceof DebugObjects || obj instanceof SynonymMetaData) {
            ((Namespace) (obj.getParent())).remove(obj);
        } else if (obj instanceof ColumnMetaData || obj instanceof ConstraintMetaData || obj instanceof IndexMetaData) {
            ((TableMetaData) (obj.getParent())).remove(obj);
        } else if (obj instanceof PartitionMetaData) {
            ((PartitionTable) (obj.getParent())).remove(obj);
        } else if (obj instanceof ViewColumnMetaData) {
            ((ViewMetaData) (obj.getParent())).remove(obj);
        } else {
            MPPDBIDELoggerUtility.info("Drop Object not supported.");
        }
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        this.dataProvider = null;
        this.objectsToDropParent = null;
        this.eventTable = null;
        this.userRoleToDropParent = null;
    }
}
