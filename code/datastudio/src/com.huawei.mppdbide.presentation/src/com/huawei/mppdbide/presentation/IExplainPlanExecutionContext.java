/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

import java.util.Observer;

import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IExplainPlanExecutionContext.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IExplainPlanExecutionContext extends IExecutionContext {

    /**
     * Handle result display.
     *
     * @param result the result
     * @param consoleData the console data
     * @param queryExecSummary the query exec summary
     * @param totalRuntime the total runtime
     */
    void handleResultDisplay(Object result, IConsoleResult consoleData, IQueryExecutionSummary queryExecSummary,
            double totalRuntime);

    /**
     * Checks if is analyze.
     *
     * @return true, if is analyze
     */
    boolean isAnalyze();

    /**
     * Gets the observer.
     *
     * @return the observer
     */
    Observer getObserver();
}
