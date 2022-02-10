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

package org.opengauss.mppdbide.view.handler.debug;

import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
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
