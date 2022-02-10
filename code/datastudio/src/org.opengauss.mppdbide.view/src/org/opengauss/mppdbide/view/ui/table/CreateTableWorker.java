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

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.view.handler.connection.AbstractDialogWindowOperationUIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateTableWorker.
 *
 * @since 3.0.0
 */
public class CreateTableWorker extends AbstractDialogWindowOperationUIWorkerJob {
    private TableMetaData newTable;
    private ExecTimer timer;

    /**
     * Instantiates a new creates the table worker.
     *
     * @param name the name
     * @param tableMetaDatata the table meta datata
     * @param msg the msg
     * @param dialog the dialog
     */
    public CreateTableWorker(String name, TableMetaData tableMetaDatata, String msg, IDialogWorkerInteraction dialog) {
        super(name, tableMetaDatata, msg, dialog, MPPDBIDEConstants.CANCELABLEJOB);
        newTable = tableMetaDatata;
        timer = new ExecTimer("Create table from wizard timer");
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
        timer.start();
        newTable.execCreate(conn);
        newTable = newTable.getNamespace().getTables().get(newTable.getName());
        return newTable;
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

    @Override
    public void finalCleanup() throws MPPDBIDEException {
        super.finalCleanup();
        timer.stopAndLog();
    }
}
