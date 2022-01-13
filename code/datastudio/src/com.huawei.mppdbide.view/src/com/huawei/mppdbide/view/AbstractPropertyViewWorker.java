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

package com.huawei.mppdbide.view;

import java.sql.SQLException;

import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.presentation.objectproperties.handler.IPropertyDetail;
import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.ILogger;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.terminal.TerminalQueryExecutionWorker;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractPropertyViewWorker.
 *
 * @since 3.0.0
 */
public abstract class AbstractPropertyViewWorker extends TerminalQueryExecutionWorker {

    /**
     * The propertycore.
     */
    protected PropertyHandlerCore propertycore;

    /**
     * The properties object.
     */
    protected IPropertyDetail propertiesObject;

    /**
     * Instantiates a new abstract property view worker.
     *
     * @param context the context
     */
    public AbstractPropertyViewWorker(IExecutionContext context) {
        super(context);
    }

    /**
     * Display property.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws SQLException the SQL exception
     */
    protected void displayProperty() throws MPPDBIDEException, SQLException {
        propertiesObject = propertycore.getproperty();

        displayUIResult();
        performPostExecutionAction();
    }

    /**
     * Setup display UI manager.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    protected void setupDisplayUIManager() throws MPPDBIDEException {
        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_EXECUTE_SQLTERMINAL_QUERY, true);
        this.context.setCriticalErrorThrown(false);

        this.context.getResultDisplayUIManager().handlePreExecutionUIDisplaySetup(this.context.getTermConnection(),
                true);
    }

    /**
     * Display UI result.
     */
    protected void displayUIResult() {
        context.getResultDisplayUIManager().handleResultDisplay(propertiesObject);
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        context.getResultDisplayUIManager().handleExceptionDisplay(exception);
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        context.getResultDisplayUIManager().handleExceptionDisplay(exception);

    }

    /**
     * On MPPDBIDE exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {
        context.getResultDisplayUIManager().handleExceptionDisplay(exception);
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        this.context.getResultDisplayUIManager().handleFinalCleanup();
    }
}
