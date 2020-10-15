/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.terminal.resulttab;

import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecutionPlanDisplayUIManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExecutionPlanDisplayUIManager extends ResultTabResultDisplayUIManager {

    /**
     * Instantiates a new execution plan display UI manager.
     *
     * @param tabMgr the tab mgr
     * @param rTab the r tab
     */
    public ExecutionPlanDisplayUIManager(ResultTabManager tabMgr, ExecutionPlanTab rTab) {
        super(tabMgr, rTab);
    }

    /**
     * Handle result display.
     *
     * @param result the result
     * @param consoleData the console data
     * @param queryExecSummary the query exec summary
     * @param totalRuntime the total runtime
     */
    public void handleResultDisplay(Object result, IConsoleResult consoleData, IQueryExecutionSummary queryExecSummary,
            double totalRuntime) {
        if (result instanceof UIModelAnalysedPlanNode) {
            ExecutionPlanTab rTab = null;
            if (this.resultTab instanceof ExecutionPlanTab) {
                rTab = (ExecutionPlanTab) this.resultTab;
                rTab.setTreeView((UIModelAnalysedPlanNode) result);
                rTab.setTextView(((UIModelAnalysedPlanNode) result).getModelInTextFormat(totalRuntime));
                rTab.resetData(consoleData, queryExecSummary);
            }
        } else {
            this.handleConsoleOnlyResultDisplay(consoleData);
        }

    }

}
