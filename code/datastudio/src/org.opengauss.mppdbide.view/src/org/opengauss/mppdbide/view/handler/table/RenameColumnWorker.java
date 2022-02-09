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

package org.opengauss.mppdbide.view.handler.table;

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.handler.connection.AbstractDialogWindowOperationUIWorkerJob;
import org.opengauss.mppdbide.view.ui.table.IDialogWorkerInteraction;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameColumnWorker.
 *
 * @since 3.0.0
 */
public class RenameColumnWorker extends AbstractDialogWindowOperationUIWorkerJob {
    private ColumnMetaData column;
    private String newColName;

    /**
     * Instantiates a new rename column worker.
     *
     * @param name the name
     * @param col the col
     * @param oldColName the old col name
     * @param newName the new name
     * @param statMsg the stat msg
     * @param dialogWorkerInteraction the d
     */
    public RenameColumnWorker(String name, ColumnMetaData col, String oldColName, String newName, String statMsg,
            IDialogWorkerInteraction dialogWorkerInteraction) {
        super(name, col, statMsg, dialogWorkerInteraction, MPPDBIDEConstants.CANCELABLEJOB);
        this.column = col;
        this.newColName = newName;
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
        column.execRename(newColName, conn);
        column.getParentTable().refresh(conn);
        MPPDBIDELoggerUtility.info("Rename column succesfull ");
        return null;
    }

    /**
     * Gets the success msg for OB status bar.
     *
     * @return the success msg for OB status bar
     */
    @Override
    protected String getSuccessMsgForOBStatusBar() {

        return null;
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

}
