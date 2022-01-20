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
 * @since 3.0.0
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
