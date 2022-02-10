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

package org.opengauss.mppdbide.view.ui.table;

import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.handler.connection.AbstractDialogWindowOperationUIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateIndexWorker.
 *
 * @since 3.0.0
 */
public class CreateIndexWorker extends AbstractDialogWindowOperationUIWorkerJob {
    private IndexMetaData idx;
    private TableMetaData tableMetaData;

    /**
     * Instantiates a new creates the index worker.
     *
     * @param name the name
     * @param idx the idx
     * @param table the table
     * @param msg the msg
     * @param dialogWorkerInteraction the dialog worker interaction
     */
    public CreateIndexWorker(String name, IndexMetaData idx, TableMetaData table, String msg,
            IDialogWorkerInteraction dialogWorkerInteraction) {
        super(name, table, msg, dialogWorkerInteraction, MPPDBIDEConstants.CANCELABLEJOB);
        this.tableMetaData = table;
        this.idx = idx;
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
        tableMetaData.execCreateIndex(idx, conn);
        tableMetaData.refresh(conn);
        MPPDBIDELoggerUtility.info("Index created successfully");
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
