/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
