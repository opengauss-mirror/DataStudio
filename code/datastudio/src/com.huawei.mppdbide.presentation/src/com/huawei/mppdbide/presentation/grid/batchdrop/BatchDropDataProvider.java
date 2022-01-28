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

package com.huawei.mppdbide.presentation.grid.batchdrop;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.IServerObjectBatchOperations;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnGroupProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.utils.messaging.MessageType;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;

/**
 * 
 * Title: class
 * 
 * Description: The Class BatchDropDataProvider.
 * 
 * @since 3.0.0
 */
public class BatchDropDataProvider implements IDSGridDataProvider {
    private List<IDSGridDataRow> rows;
    private boolean isAtomic;
    private boolean isCascade;
    private List<?> serverObjects = null;
    private DSEventTable eventTable = null;
    private int objectsProcessedCnt;
    private boolean pauseStopOperation;
    private boolean cancelOperation;
    private int objectTotalCount;
    private final Object lock = new Object();
    private boolean editSupported = true;
    private Database db;

    private static final String TRIM_FOR_OBJECT_NOT_FOUND = "ERROR:";
    private static final String APPEND_FOR_OBJECT_NOT_FOUND = ", skipping";

    /**
     * Instantiates a new batch drop data provider.
     *
     * @param objectsToDrop the objects to drop
     */
    public BatchDropDataProvider(List<?> objectsToDrop) {
        this.isAtomic = false;
        this.isCascade = false;
        this.pauseStopOperation = false;
        rows = new ArrayList<IDSGridDataRow>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        this.serverObjects = objectsToDrop;
        objectsProcessedCnt = 0;
        this.cancelOperation = false;
        this.objectTotalCount = serverObjects.size();
        ServerObject serverObj = (ServerObject) (this.serverObjects.get(0));
        this.db = serverObj.getDatabase();
    }

    @Override
    public void init() throws DatabaseOperationException, DatabaseCriticalException {
        for (int i = 0; i < objectTotalCount; i++) {
            Object obj = serverObjects.get(i);
            if (obj instanceof ServerObject && obj instanceof IServerObjectBatchOperations) {
                rows.add(new BatchDropDataRow((IServerObjectBatchOperations) serverObjects.get(i), isCascade,
                        this.getColumnDataProvider().getColumnCount()));
            }
        }

        serverObjects = null;
    }

    /**
     * Sets the cascade.
     *
     * @param isCascade1 the new cascade
     */
    public void setCascade(boolean isCascade1) {
        this.isCascade = isCascade1;
        for (int i = 0; i < objectTotalCount; i++) {
            ((BatchDropDataRow) rows.get(i)).updateQuery(isCascade1);
        }

        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_GRID_INPUT_CHANGED, null));
    }

    private void handleSQLException(SQLException e) throws DatabaseCriticalException, DatabaseOperationException {
        GaussUtils.handleCriticalException(e);
        throw new DatabaseOperationException(IMessagesConstants.ERR_QUERY_EXECUTION_FAILED, e);
    }

    private void handleObjectNotFound(DatabaseOperationException e, MessageQueue queue)
            throws DatabaseOperationException {
        if (e.getCause() instanceof SQLException && GaussUtils.isObjectNotFoundErr((SQLException) e.getCause())) {
            String str = e.getServerMessage();
            if (str != null) {
                str = str.replace(TRIM_FOR_OBJECT_NOT_FOUND, "").trim();
            }
            queue.push(new Message(MessageType.NOTICE, str + APPEND_FOR_OBJECT_NOT_FOUND));
        } else {
            throw e;
        }
    }

    private void execute(DBConnection conn, BatchDropDataRow row)
            throws DatabaseCriticalException, DatabaseOperationException {
        MessageQueue queue = new MessageQueue();
        Message msg = null;
        try {
            row.execute(conn, queue, this.isCascade);
        } catch (DatabaseOperationException e) {
            handleObjectNotFound(e, queue);
        }

        if (!queue.isEmpty()) {
            msg = queue.pop();
            if (msg != null) {
                row.updateError(msg.getMessage());
            }
        } else {
            row.updateError("");
        }
    }

    /**
     * Start execute.
     *
     * @param conn the conn
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void startExecute(DBConnection conn) throws MPPDBIDEException {
        setAutoCommit(conn, !isAtomic);
        objectsProcessedCnt = 0;

        while (objectsProcessedCnt < objectTotalCount) {
            if (conn.isClosed()) {
                break;
            }
            executeBatchDrop(conn);

            objectsProcessedCnt++;
            while (isPauseStopOperation()) {
                if (isCancelOperation()) {
                    // Job will be cancelled by UI thread itself
                    return;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException exception) {
                    MPPDBIDELoggerUtility.error("BatchDropDataProvider: start execute for batch drop failed.",
                            exception);
                }
            }
        }

        commitDataOnAtomic(conn);

        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_OP_COMPLETE, null));

        setAutoCommit(conn, true);
    }

    private void commitDataOnAtomic(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        if (isAtomic) {
            try {
                synchronized (lock) {
                    conn.getConnection().commit();
                    objectsProcessedCnt = 0;
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("BatchDropDataProvider: commit for batch drop failed.", exception);
                eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_OP_ROLLBACK, null));
                handleSQLException(exception);
            }
        }
    }

    private void setAutoCommit(DBConnection conn, boolean isCommit)
            throws DatabaseCriticalException, DatabaseOperationException {
        try {
            conn.getConnection().setAutoCommit(isCommit);
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("BatchDropDataProvider: Auto commit for batch drop failed.", exception);
            handleSQLException(exception);
        }
    }

    private BatchDropDataRow executeBatchDrop(DBConnection conn) throws MPPDBIDEException {
        BatchDropDataRow row = null;
        try {
            row = (BatchDropDataRow) rows.get(objectsProcessedCnt);
            row.updateStatus(BatchDropStatusEnum.IN_PROGRESS, false);
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_GRID_INPUT_CHANGED, null));

            execute(conn, row);

            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_DROP_SUCCESS, row));
            row.updateStatus(BatchDropStatusEnum.COMPLETED, true);
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_GRID_INPUT_CHANGED, null));
        } catch (DatabaseOperationException | DatabaseCriticalException e) {

            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_DROP_FAILED, row));
            if (row != null) {
                row.updateStatus(BatchDropStatusEnum.ERROR, false);
                row.updateError(e.getServerMessage());
            }
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_GRID_INPUT_CHANGED, null));

            if (isAtomic) {
                eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_OP_ROLLBACK, null));
                throw e;
            }
        }
        return row;
    }

    /**
     * Rollback and notify UI mgr.
     *
     * @param conn the conn
     */
    public void rollbackAndNotifyUIMgr(DBConnection conn) {
        if (isAtomic) {
            int objectsAlreadyDropped = 0;
            try {
                synchronized (lock) {
                    objectsAlreadyDropped = objectsProcessedCnt;
                    objectsProcessedCnt = 0;

                    conn.getConnection().rollback();
                    conn.getConnection().setAutoCommit(true);
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("BatchDropDataProvider: rollback for batch drop failed.", exception);
            }

            BatchDropDataRow row = null;
            for (int i = 0; i < objectsAlreadyDropped; i++) {
                row = (BatchDropDataRow) rows.get(i);
                if (row.isDropped()) {
                    row.updateStatus(BatchDropStatusEnum.TO_START, false);
                    eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_DROP_REVERTED, row));
                } else {
                    row.updateStatus(BatchDropStatusEnum.TO_START, false);
                    eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_DROP_FAIL_REVERTED, row));
                }
            }

            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_GRID_INPUT_CHANGED, null));
        }
    }

    @Override
    public void close() throws DatabaseOperationException, DatabaseCriticalException {

    }

    @Override
    public List<IDSGridDataRow> getNextBatch() throws DatabaseOperationException, DatabaseCriticalException {
        return rows;
    }

    @Override
    public List<IDSGridDataRow> getAllFetchedRows() {
        return rows;
    }

    @Override
    public boolean isEndOfRecords() {
        return true;
    }

    @Override
    public int getRecordCount() {
        return rows.size();
    }

    @Override
    public IDSGridColumnProvider getColumnDataProvider() {
        return new BatchDropColumnProvider();
    }

    @Override
    public void preDestroy() {
        setCancelOperation(true);
        if (null != this.eventTable) {
            this.eventTable.unhookall();
            this.eventTable = null;
        }

        if (null != this.rows) {
            this.rows.clear();
            this.rows = null;
        }

        if (null != this.serverObjects) {
            this.serverObjects.clear();
            this.serverObjects = null;
        }
    }

    @Override
    public IDSGridColumnGroupProvider getColumnGroupProvider() {
        // No grouping expected here.
        return null;
    }

    @Override
    public TableMetaData getTable() {

        return null;
    }

    /**
     * Sets the atomic.
     *
     * @param isAtomic1 the new atomic
     */
    public void setAtomic(boolean isAtomic1) {
        this.isAtomic = isAtomic1;
    }

    /**
     * Sets the event table.
     *
     * @param eventTable the new event table
     */
    public void setEventTable(DSEventTable eventTable) {
        this.eventTable = eventTable;
    }

    /**
     * Gets the total object cnt.
     *
     * @return the total object cnt
     */
    public int getTotalObjectCnt() {
        return objectTotalCount;
    }

    /**
     * Checks if is pause stop operation.
     *
     * @return true, if is pause stop operation
     */
    public boolean isPauseStopOperation() {
        return pauseStopOperation;
    }

    /**
     * Sets the pause stop operation.
     *
     * @param pauseStopOperation1 the new pause stop operation
     */
    public void setPauseStopOperation(boolean pauseStopOperation1) {
        this.pauseStopOperation = pauseStopOperation1;
    }

    /**
     * Checks if is cancel operation.
     *
     * @return true, if is cancel operation
     */
    public boolean isCancelOperation() {
        return cancelOperation;
    }

    /**
     * Sets the cancel operation.
     *
     * @param cancelOperation1 the new cancel operation
     */
    public void setCancelOperation(boolean cancelOperation1) {
        this.cancelOperation = cancelOperation1;
    }

    /**
     * Checks if is edits the supported.
     *
     * @return true, if is edits the supported
     */
    public boolean isEditSupported() {
        return editSupported;
    }

    /**
     * Sets the edits the supported.
     *
     * @param isEditSupported the new edits the supported
     */
    public void setEditSupported(boolean isEditSupported) {
        this.editSupported = isEditSupported;
    }

    @Override
    public boolean getResultTabDirtyFlag() {

        return false;
    }

    @Override
    public void setResultTabDirtyFlag(boolean flag) {

    }

    @Override
    public Database getDatabse() {
        return this.db;
    }

    /**
     * init
     */
    @Override
    public void init(IQueryResult irq, ArrayList<DefaultParameter> debugInputValueList, boolean isCallableStmt)
            throws DatabaseOperationException, DatabaseCriticalException {
    }

    /**
     * gets next batch
     */
    @Override
    public List<IDSGridDataRow> getNextBatch(ArrayList<DefaultParameter> debugInputValueList)
            throws DatabaseOperationException, DatabaseCriticalException {
        return null;
    }

    @Override
    public void setFuncProcExport(boolean isFuncProcExport) {
    }

    @Override
    public boolean isFuncProcExport() {
        return false;
    }

}
