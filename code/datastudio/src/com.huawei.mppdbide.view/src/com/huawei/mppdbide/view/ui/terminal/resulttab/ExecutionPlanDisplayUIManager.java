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

import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecutionPlanDisplayUIManager.
 *
 * @since 3.0.0
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
