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
 * @since 3.0.0
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
