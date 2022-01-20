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

package com.huawei.mppdbide.view.visualexplainplanpropertyview;

import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.ILogger;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.AbstractPropertyViewWorker;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanPropertyViewWorker.
 *
 * @since 3.0.0
 */
public class VisualExplainPlanPropertyViewWorker extends AbstractPropertyViewWorker {

    /**
     * Instantiates a new visual explain plan property view worker.
     *
     * @param context the context
     */
    public VisualExplainPlanPropertyViewWorker(IExecutionContext context) {
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
        setupDisplayUIManager();

        if (!this.context.getTermConnection().isConnected()) {
            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_EXECUTE_SQLTERMINAL_QUERY, false);
            return null;
        }

        if (!(context instanceof VisualExplainPlanPropertiesContext)) {
            return null;
        }

        propertycore = ((VisualExplainPlanPropertiesContext) context).getPropertyHandlerCore();

        displayProperty();

        return null;

    }

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {

    }
}
