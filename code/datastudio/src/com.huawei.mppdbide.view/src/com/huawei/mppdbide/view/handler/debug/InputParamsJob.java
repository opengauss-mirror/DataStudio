/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug;

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 05,12,2020]
 * @since 05,12,2020
 */
public class InputParamsJob extends UIWorkerJob {
    private StartDebugHandler debugHandler;

    /**
     * descript: InputParam back thread Job
     *
     * @param name the job name
     * @param family the input family param
     * @param debugHandler start debug handler
     */
    public InputParamsJob(String name, Object family, StartDebugHandler debugHandler) {
        super(name, family);
        this.debugHandler = debugHandler;
    }

    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        return "";
    }

    @Override
    public void onSuccessUIAction(Object object) {
        debugHandler.executeSQLObjWindow(debugHandler.getSourceEditor().getDebugObject());
    }

    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
        MPPDBIDELoggerUtility.error("find error on:" + dbCriticalException);
    }

    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
        MPPDBIDELoggerUtility.error("find error on:" + dbOperationException);
    }

}
