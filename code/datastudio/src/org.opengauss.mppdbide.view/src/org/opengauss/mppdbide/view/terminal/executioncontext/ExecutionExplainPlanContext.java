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

package org.opengauss.mppdbide.view.terminal.executioncontext;

import java.util.ArrayList;
import java.util.Observer;

import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.presentation.ContextExecutionOperationType;
import org.opengauss.mppdbide.presentation.ExecutionFailureActionOptions;
import org.opengauss.mppdbide.presentation.IExplainPlanExecutionContext;
import org.opengauss.mppdbide.presentation.IResultDisplayUIManager;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.presentation.resultsetif.IConsoleResult;
import org.opengauss.mppdbide.presentation.resultsetif.IResultConfig;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import org.opengauss.mppdbide.utils.messaging.MessageQueue;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminalResultDisplayUIManager;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;

/**
 * 
 * Title: class
 * 
 * Description: This context is used when "execution plan" button is
 * clicked on sql terminal for selected queries, and the plan is shown in a new
 * execution plan tab
 *
 * @since 3.0.0
 */
public class ExecutionExplainPlanContext implements IExplainPlanExecutionContext {

    private SQLTerminalExecutionContext termContext;
    private BottomStatusBar bottomStatusBar;
    private boolean analyze;

    /**
     * Checks if is analyze.
     *
     * @return true, if is analyze
     */
    public boolean isAnalyze() {
        return analyze;
    }

    /**
     * Sets the analyze.
     *
     * @param isAnalyze the new analyze
     */
    public void setAnalyze(boolean isAnalyze) {
        this.analyze = isAnalyze;
    }

    /**
     * Gets the bottom status bar.
     *
     * @return the bottom status bar
     */
    public BottomStatusBar getBottomStatusBar() {
        return bottomStatusBar;
    }

    /**
     * Sets the bottom status bar.
     *
     * @param bottomStatusBar the new bottom status bar
     */
    public void setBottomStatusBar(BottomStatusBar bottomStatusBar) {
        this.bottomStatusBar = bottomStatusBar;
    }

    /**
     * Gets the SQL terminal execution context.
     *
     * @return the SQL terminal execution context
     */
    public SQLTerminalExecutionContext getSQLTerminalExecutionContext() {
        return this.termContext;
    }

    /**
     * Instantiates a new execution explain plan context.
     *
     * @param parentShell the parent shell
     * @param terminal the terminal
     */
    public ExecutionExplainPlanContext(Shell parentShell, SQLTerminal terminal) {
        termContext = new SQLTerminalExecutionContext(parentShell, terminal);
        termContext.init();
        termContext.setCurrentExecution(ContextExecutionOperationType.CONTEXT_OPERATION_TYPE_SQL_TERMINAL_EXECUTION);
        termContext.getTerminal().setExecutionContext(termContext);
    }

    /**
     * Can free connection after use.
     *
     * @return true, if successful
     */
    @Override
    public boolean canFreeConnectionAfterUse() {

        return termContext.canFreeConnectionAfterUse();
    }

    /**
     * Need query parse and split.
     *
     * @return true, if successful
     */
    @Override
    public boolean needQueryParseAndSplit() {

        return termContext.needQueryParseAndSplit();
    }

    /**
     * Gets the action on query failure.
     *
     * @return the action on query failure
     */
    @Override
    public ExecutionFailureActionOptions getActionOnQueryFailure() {

        return termContext.getActionOnQueryFailure();
    }

    /**
     * Gets the context name.
     *
     * @return the context name
     */
    @Override
    public String getContextName() {

        return termContext.getContextName();
    }

    /**
     * Sets the critical error thrown.
     *
     * @param booleanValue the new critical error thrown
     */
    @Override
    public void setCriticalErrorThrown(boolean booleanValue) {
        termContext.setCriticalErrorThrown(booleanValue);

    }

    /**
     * Gets the query.
     *
     * @return the query
     */
    @Override
    public String getQuery() {
        return termContext.getQuery();
    }

    /**
     * Gets the working job context.
     *
     * @return the working job context
     */
    @Override
    public Object getWorkingJobContext() {

        return termContext.getWorkingJobContext();
    }

    /**
     * Sets the working job context.
     *
     * @param jobContext the new working job context
     */
    @Override
    public void setWorkingJobContext(Object jobContext) {
        termContext.setWorkingJobContext(jobContext);

    }

    /**
     * Gets the notice message queue.
     *
     * @return the notice message queue
     */
    @Override
    public MessageQueue getNoticeMessageQueue() {

        return termContext.getNoticeMessageQueue();
    }

    /**
     * Gets the connection profile ID.
     *
     * @return the connection profile ID
     */
    @Override
    public String getConnectionProfileID() {

        return termContext.getConnectionProfileID();
    }

    /**
     * Gets the result config.
     *
     * @return the result config
     */
    @Override
    public IResultConfig getResultConfig() {

        return termContext.getResultConfig();
    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    @Override
    public IResultDisplayUIManager getResultDisplayUIManager() {
        return termContext.getResultDisplayUIManager();
    }

    /**
     * Handle execution exception.
     *
     * @param exception the e
     */
    @Override
    public void handleExecutionException(Exception exception) {
        termContext.handleExecutionException(exception);

    }

    /**
     * Handle successfull completion.
     */
    @Override
    public void handleSuccessfullCompletion() {
        termContext.handleSuccessfullCompletion();

    }

    /**
     * Sets the job done.
     */
    @Override
    public void setJobDone() {
        termContext.setJobDone();

    }

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    @Override
    public TerminalExecutionConnectionInfra getTermConnection() {

        return termContext.getTermConnection();
    }

    /**
     * Job type.
     *
     * @return the string
     */
    @Override
    public String jobType() {

        return termContext.jobType();
    }

    /**
     * Gets the current execution.
     *
     * @return the current execution
     */
    @Override
    public ContextExecutionOperationType getCurrentExecution() {

        return termContext.getCurrentExecution();
    }

    /**
     * Sets the current execution.
     *
     * @param contextOperationTypeNewPlSqlCreation the new current execution
     */
    @Override
    public void setCurrentExecution(ContextExecutionOperationType contextOperationTypeNewPlSqlCreation) {
        termContext.setCurrentExecution(contextOperationTypeNewPlSqlCreation);

    }

    /**
     * Gets the current server object.
     *
     * @return the current server object
     */
    @Override
    public ServerObject getCurrentServerObject() {

        return termContext.getCurrentServerObject();
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

        if (result instanceof UIModelAnalysedPlanNode) {
            ((SQLTerminalResultDisplayUIManager) this.termContext.getResultDisplayUIManager())
                    .handleExecPlanResultDisplay((UIModelAnalysedPlanNode) result, consoleData, queryExecSummary,
                            totalRuntime);
        }
    }

    /**
     * Gets the observer.
     *
     * @return the observer
     */
    @Override
    public Observer getObserver() {
        return null;
    }

    /**
     * gets the input values
     *
     * @return the InputValues
     */
    @Override
    public ArrayList<DefaultParameter> getInputValues() {
        return null;
    }
}
