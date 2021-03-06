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

package org.opengauss.mppdbide.presentation.grid.batchdrop;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.IServerObjectBatchOperations;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.messaging.MessageQueue;

/**
 * 
 * Title: class
 * 
 * Description: The Class BatchDropDataRow.
 * 
 * @since 3.0.0
 */
public class BatchDropDataRow implements IDSGridDataRow {
    private IServerObjectBatchOperations serverObject;
    private Object[] values;
    private boolean isDropped = false;
    private static final int QUERY_IDX = 2;
    private static final int STATUS_IDX = 3;
    private static final int ERROR_IDX = 4;

    /**
     * Instantiates a new batch drop data row.
     *
     * @param servObj the serv obj
     * @param isCascade the is cascade
     * @param columnCount the column count
     */
    public BatchDropDataRow(IServerObjectBatchOperations servObj, boolean isCascade, int columnCount) {
        this.serverObject = servObj;
        init(isCascade, columnCount);
    }

    private void init(boolean isCascade, int columnCount) {
        this.values = new Object[columnCount];
        values[0] = serverObject.getObjectTypeName();
        values[1] = serverObject.getObjectFullName();
        values[QUERY_IDX] = serverObject.getDropQuery(isCascade);

        values[STATUS_IDX] = BatchDropStatusEnum.TO_START;
        values[ERROR_IDX] = "";
    }

    @Override
    public Object[] getValues() {
        return new Object[0];
    }

    @Override
    public Object getValue(int columnIndex) {
        return values[columnIndex];
    }

    @Override
    public Object[] getClonedValues() {
        return values.clone();
    }

    /**
     * Execute.
     *
     * @param connection the connection
     * @param queue the queue
     * @param isCascade the is cascade
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execute(DBConnection connection, MessageQueue queue, boolean isCascade)
            throws DatabaseOperationException, DatabaseCriticalException {
        connection.execQueryWithMsgQueue(this.serverObject.getDropQuery(isCascade), queue);
    }

    /**
     * Update query.
     *
     * @param isCascade the is cascade
     */
    public void updateQuery(boolean isCascade) {
        values[QUERY_IDX] = ((IServerObjectBatchOperations) serverObject).getDropQuery(isCascade);
    }

    /**
     * Update status.
     *
     * @param status the status
     * @param isDropped1 the is dropped 1
     */
    public void updateStatus(BatchDropStatusEnum status, boolean isDropped1) {
        values[STATUS_IDX] = status;
        this.isDropped = isDropped1;
    }

    /**
     * Update error.
     *
     * @param serverMessage the server message
     */
    public void updateError(String serverMessage) {
        if (!(serverMessage.contains("  Detail") && serverMessage.contains("  Hint"))) {
            values[ERROR_IDX] = serverMessage;
            return;
        }
        int index1 = serverMessage.indexOf("  Detail");
        int index2 = serverMessage.indexOf("  Hint");
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(serverMessage.substring(0, index1));
        sb.append(serverMessage.substring(index2));
        values[ERROR_IDX] = sb.toString();
    }

    /**
     * Gets the server object.
     *
     * @return the server object
     */
    public ServerObject getServerObject() {
        return (ServerObject) serverObject;
    }

    /**
     * Checks if is dropped.
     *
     * @return true, if is dropped
     */
    public boolean isDropped() {
        return isDropped;
    }
}
