/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.terminal.executioncontext;

import java.util.ArrayList;
import java.util.Observer;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.ContextExecutionOperationType;
import com.huawei.mppdbide.presentation.ExecutionFailureActionOptions;
import com.huawei.mppdbide.presentation.IExplainPlanExecutionContext;
import com.huawei.mppdbide.presentation.IResultDisplayUIManager;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminalResultDisplayUIManager;
import com.huawei.mppdbide.view.utils.BottomStatusBar;

/**
 * 
 * Title: class
 * 
 * Description: a00415838 This context is used when "execution plan" button is
 * clicked on sql terminal for selected queries, and the plan is shown in a new
 * execution plan tab
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     */
    @Override
    public ArrayList<DefaultParameter> getInputValues() {
        return null;
    }
}
