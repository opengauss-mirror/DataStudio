/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.terminal.executioncontext;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.presentation.ContextExecutionOperationType;
import com.huawei.mppdbide.presentation.ExecutionFailureActionOptions;
import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractTerminalExecutionContext.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class AbstractTerminalExecutionContext implements IExecutionContext {
    private String query;
    private TerminalExecutionConnectionInfra termConnection;
    private boolean isCriticalErrorThrown;
    private ContextExecutionOperationType currentExecution;
    private Object workingJobContext;
    private MessageQueue noticeMessageQueue;

    /**
     * Instantiates a new abstract terminal execution context.
     *
     * @param termConnectionInfra the term connection infra
     */
    protected AbstractTerminalExecutionContext(TerminalExecutionConnectionInfra termConnectionInfra) {
        this.query = null;
        this.isCriticalErrorThrown = false;
        this.setTermConnection(termConnectionInfra);
    }

    /**
     * Gets the query.
     *
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query.
     *
     * @param query the new query
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Checks if is critical error thrown.
     *
     * @return true, if is critical error thrown
     */
    public boolean isCriticalErrorThrown() {
        return isCriticalErrorThrown;
    }

    /**
     * Sets the critical error thrown.
     *
     * @param isCriticalErrThrown the new critical error thrown
     */
    public void setCriticalErrorThrown(boolean isCriticalErrThrown) {
        this.isCriticalErrorThrown = isCriticalErrThrown;
    }

    /**
     * Gets the current execution.
     *
     * @return the current execution
     */
    public ContextExecutionOperationType getCurrentExecution() {
        return currentExecution;
    }

    /**
     * Sets the current execution.
     *
     * @param contextOperationTypeNewPlSqlCreation the new current execution
     */
    public void setCurrentExecution(ContextExecutionOperationType contextOperationTypeNewPlSqlCreation) {
        this.currentExecution = contextOperationTypeNewPlSqlCreation;
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
        return true;
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
     * Gets the working job context.
     *
     * @return the working job context
     */
    @Override
    public Object getWorkingJobContext() {
        return workingJobContext;
    }

    /**
     * Sets the working job context.
     *
     * @param jobContext the new working job context
     */
    @Override
    public void setWorkingJobContext(Object jobContext) {
        this.workingJobContext = jobContext;

    }

    /**
     * Gets the notice message queue.
     *
     * @return the notice message queue
     */
    @Override
    public MessageQueue getNoticeMessageQueue() {
        return this.noticeMessageQueue;
    }

    /**
     * Sets the notice message queue.
     *
     * @param msgQueue the new notice message queue
     */
    public void setNoticeMessageQueue(MessageQueue msgQueue) {
        noticeMessageQueue = msgQueue;
    }

    /**
     * Gets the connection profile ID.
     *
     * @return the connection profile ID
     */
    @Override
    public String getConnectionProfileID() {
        return this.getTermConnection().getDatabase().getServer().getServerConnectionInfo().getProfileId();
    }

    /**
     * Handle execution exception.
     *
     * @param exception the e
     */
    @Override
    public void handleExecutionException(Exception exception) {
        this.getResultDisplayUIManager().handleExceptionDisplay(exception);
    }

    /**
     * Handle successfull completion.
     */
    @Override
    public void handleSuccessfullCompletion() {
        this.getResultDisplayUIManager().handleSuccessfullCompletion();

    }

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    @Override
    public TerminalExecutionConnectionInfra getTermConnection() {
        return termConnection;
    }

    private void setTermConnection(TerminalExecutionConnectionInfra termConnection) {
        this.termConnection = termConnection;
    }

    /**
     * Gets the current server object.
     *
     * @return the current server object
     */
    @Override
    public ServerObject getCurrentServerObject() {
        return null;
    }

    /**
     * gets the terminal
     * 
     * @return the terminal
     */
    public SQLTerminal getTerminal() {
        return null;
    }

}
