/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.terminal;

import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.view.core.LoadLevel1Objects;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditTableDataWorker.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class EditTableDataWorker extends TerminalQueryExecutionWorker {

    /**
     * Instantiates a new edits the table data worker.
     *
     * @param context the context
     */
    public EditTableDataWorker(IExecutionContext context) {
        super(context);
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
        TableMetaData tableMetaData = (TableMetaData) this.context.getCurrentServerObject();
        if (tableMetaData != null && !tableMetaData.isLoaded()) {
            LoadLevel1Objects load = new LoadLevel1Objects(tableMetaData, null);
            load.loadObjects();
        }
        super.doJob();
        return null;
    }
}
