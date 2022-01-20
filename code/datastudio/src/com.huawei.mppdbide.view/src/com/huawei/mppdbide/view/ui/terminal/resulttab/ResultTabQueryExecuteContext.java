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

import java.util.ArrayList;

import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.ExecutionFailureActionOptions;
import com.huawei.mppdbide.presentation.IResultDisplayUIManager;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.view.terminal.executioncontext.AbstractTerminalExecutionContext;
import com.huawei.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class ResultTabQueryExecuteContext.
 *
 * @since 3.0.0
 */
public class ResultTabQueryExecuteContext extends AbstractTerminalExecutionContext {

    /**
     * The result summary.
     */
    protected IQueryExecutionSummary resultSummary;
    private ResultSetConfig rsConfig;
    private ResultTabResultDisplayUIManager resultDisplayUIManager;
    private String contextName;

    /**
     * Instantiates a new result tab query execute context.
     *
     * @param name the name
     * @param resultTab the result tab
     * @param resultDisplayUIManager the result display UI manager
     * @param termConnectionCopy the term connection copy
     */
    public ResultTabQueryExecuteContext(String name, ResultTab resultTab,
            ResultTabResultDisplayUIManager resultDisplayUIManager,
            TerminalExecutionConnectionInfra termConnectionCopy) {
        super(termConnectionCopy);
        this.resultSummary = resultTab.getResultSummary();
        contextName = name;
        this.resultDisplayUIManager = resultDisplayUIManager;
    }

    /**
     * Can free connection after use.
     *
     * @return true, if successful
     */
    @Override
    public boolean canFreeConnectionAfterUse() {
        return false;
    }

    /**
     * Need query parse and split.
     *
     * @return true, if successful
     */
    @Override
    public boolean needQueryParseAndSplit() {
        return false;
    }

    /**
     * Gets the action on query failure.
     *
     * @return the action on query failure
     */
    @Override
    public ExecutionFailureActionOptions getActionOnQueryFailure() {
        return ExecutionFailureActionOptions.EXECUTION_FAILURE_ACTION_ABORT;
    }

    /**
     * Gets the context name.
     *
     * @return the context name
     */
    @Override
    public String getContextName() {
        return this.contextName;
    }

    /**
     * Gets the query.
     *
     * @return the query
     */
    @Override
    public String getQuery() {
        return this.resultSummary.getQuery();
    }

    /**
     * Gets the notice message queue.
     *
     * @return the notice message queue
     */
    @Override
    public MessageQueue getNoticeMessageQueue() {
        return null;
    }

    /**
     * Gets the connection profile ID.
     *
     * @return the connection profile ID
     */
    @Override
    public String getConnectionProfileID() {
        return this.resultSummary.getProfileId();
    }

    /**
     * Gets the result config.
     *
     * @return the result config
     */
    @Override
    public IResultConfig getResultConfig() {
        if (null == this.rsConfig) {
            this.rsConfig = new ResultSetConfig(this.resultSummary.getNumRecordsFetched());
        }
        return this.rsConfig;
    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    @Override
    public IResultDisplayUIManager getResultDisplayUIManager() {
        return this.resultDisplayUIManager;
    }

    /**
     * Sets the job done.
     */
    @Override
    public void setJobDone() {

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ResultSetConfig.
     */
    private static final class ResultSetConfig implements IResultConfig {
        private int numRecordsToFetch = 0;

        private ResultSetConfig(int numRecordsFetchedSoFar) {
            int prefFetchCount = UserPreference.getInstance().getResultDataFetchCount();
            this.numRecordsToFetch = (prefFetchCount == -1) ? -1 : numRecordsFetchedSoFar + prefFetchCount;
        }

        @Override
        public ActionAfterResultFetch getActionAfterFetch() {
            return ActionAfterResultFetch.ISSUE_NO_OP;
        }

        @Override
        public int getFetchCount() {
            return this.numRecordsToFetch;
        }
    }

    /**
     * Job type.
     *
     * @return the string
     */
    @Override
    public String jobType() {

        return MPPDBIDEConstants.CANCELABLEJOB;
    }

    /**
     * gets the input values
     *
     * @return the input values
     */
    @Override
    public ArrayList<DefaultParameter> getInputValues() {
        return null;
    }
}
