/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

import java.util.ArrayList;

import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.messaging.MessageQueue;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IExecutionContext.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IExecutionContext {

    /**
     * Can free connection after use.
     *
     * @return true, if successful
     */
    boolean canFreeConnectionAfterUse();

    /**
     * Need query parse and split.
     *
     * @return true, if successful
     */
    boolean needQueryParseAndSplit();

    /**
     * Gets the action on query failure.
     *
     * @return the action on query failure
     */
    ExecutionFailureActionOptions getActionOnQueryFailure();

    /**
     * Gets the context name.
     *
     * @return the context name
     */
    String getContextName();

    /**
     * Sets the critical error thrown.
     *
     * @param b the new critical error thrown
     */
    void setCriticalErrorThrown(boolean b);

    /**
     * Gets the query.
     *
     * @return the query
     */
    String getQuery();

    /**
     * Gets the working job context.
     *
     * @return the working job context
     */
    Object getWorkingJobContext();

    /**
     * Sets the working job context.
     *
     * @param jobContext the new working job context
     */
    void setWorkingJobContext(Object jobContext);

    /**
     * Gets the notice message queue.
     *
     * @return the notice message queue
     */
    MessageQueue getNoticeMessageQueue();

    /**
     * Gets the connection profile ID.Get the profile in which the query will be
     * executed.
     *
     * @return the connection profile ID
     */
    String getConnectionProfileID();

    /**
     * Gets the result config.User preferences of the result management from the
     * preference store
     *
     * @return the result config
     */
    IResultConfig getResultConfig();

    /**
     * Gets the result display UI manager.when the execution of the query is ok,
     * UI worker will call this and hand over the results
     *
     * @return the result display UI manager
     */
    IResultDisplayUIManager getResultDisplayUIManager();

    /**
     * Handle execution exception.
     *
     * @param e the e
     */
    void handleExecutionException(Exception e);

    /**
     * Handle successfull completion.
     */
    void handleSuccessfullCompletion();

    /**
     * Sets the job done.
     */
    void setJobDone();

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    TerminalExecutionConnectionInfra getTermConnection();

    /**
     * Job type.
     *
     * @return the string
     */
    String jobType();

    /**
     * Gets the current execution.
     *
     * @return the current execution
     */
    ContextExecutionOperationType getCurrentExecution();

    /**
     * Sets the current execution.
     *
     * @param contextOperationTypeNewPlSqlCreation the new current execution
     */
    void setCurrentExecution(ContextExecutionOperationType contextOperationTypeNewPlSqlCreation);

    /**
     * Gets the current server object.
     *
     * @return the current server object
     */
    ServerObject getCurrentServerObject();

    /**
     * Hide exec progres bar.
     */
    default void hideExecProgresBar() {
        return;
    };

    /**
     * Show exec progres bar.
     */
    default void showExecProgresBar() {
        return;
    };

    /**
     * gets the input values
     * 
     * @return the list of input values
     */
    ArrayList<DefaultParameter> getInputValues();

}