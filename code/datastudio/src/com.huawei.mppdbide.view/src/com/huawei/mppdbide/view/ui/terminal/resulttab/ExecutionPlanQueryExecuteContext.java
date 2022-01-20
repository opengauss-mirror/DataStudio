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

package com.huawei.mppdbide.view.ui.terminal.resulttab;

import java.util.Observer;

import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.IExplainPlanExecutionContext;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;

/**
 * Title: ExecutionPlanQueryExecuteContext
 * 
 * Description:The Class ExecutionPlanQueryExecuteContext.@author a00415838 this
 * context is used in case of explain query is re-executed due to refresh from
 * execution plan tab
 *
 * @since 3.0.0
 */
public class ExecutionPlanQueryExecuteContext extends ResultTabQueryExecuteContext
        implements IExplainPlanExecutionContext {
    private boolean analyse = false;
    private Observer ob = null;

    /**
     * Sets the observer.
     *
     * @param ov the new observer
     */
    public void setObserver(Observer ov) {
        this.ob = ov;
    }

    /**
     * Instantiates a new execution plan query execute context.
     *
     * @param name the name
     * @param resultTab the result tab
     * @param resultDisplayUIManager the result display UI manager
     * @param termConnectionCopy the term connection copy
     */
    public ExecutionPlanQueryExecuteContext(String name, ResultTab resultTab,
            ExecutionPlanDisplayUIManager resultDisplayUIManager, TerminalExecutionConnectionInfra termConnectionCopy) {
        super(name, resultTab, resultDisplayUIManager, termConnectionCopy);
        if (resultSummary != null) {
            this.analyse = resultSummary.isAnalyze();
        }
    }

    /**
     * Handle result display.
     *
     * @param result the result
     * @param consoleData the console data
     * @param queryExecSummary the query exec summary
     * @param totalRuntime the total runtime
     */
    @Override
    public void handleResultDisplay(Object result, IConsoleResult consoleData, IQueryExecutionSummary queryExecSummary,
            double totalRuntime) {
        ((ExecutionPlanDisplayUIManager) this.getResultDisplayUIManager()).handleResultDisplay(result, consoleData,
                queryExecSummary, totalRuntime);
    }

    /**
     * Checks if is analyze.
     *
     * @return true, if is analyze
     */
    @Override
    public boolean isAnalyze() {
        return analyse;
    }

    /**
     * Gets the observer.
     *
     * @return the observer
     */
    @Override
    public Observer getObserver() {
        return ob;
    }

}
