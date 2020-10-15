/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.view.handler.connection.AbstractDialogWindowOperationUIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateTableWorker.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
