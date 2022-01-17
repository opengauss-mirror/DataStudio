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

package com.huawei.mppdbide.view.terminal;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import com.huawei.mppdbide.presentation.CanContextContinueExecuteRule;
import com.huawei.mppdbide.presentation.ExecutionFailureActionOptions;
import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.TerminalExecutionSQLConnectionInfra;
import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;
import com.huawei.mppdbide.presentation.resultset.ConsoleDataWrapper;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.IQuerrySplitter;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.SQLTerminalQuerySplit;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.ILogger;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.edittabledata.AbstractEditTableDataResultDisplayUIManager;
import com.huawei.mppdbide.view.terminal.queryexecution.SqlQueryExecutionWorkingContext;
import com.huawei.mppdbide.view.ui.DBAssistantWindow;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.ui.terminal.resulttab.ResultTabQueryExecuteContext;
import com.huawei.mppdbide.view.utils.IDEMemoryAnalyzer;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * Title: TerminalQueryExecutionWorker
 * 
 * Description:The class TerminalQueryExecutionWorker
 * 
 * @since 3.0.0
 */
public class TerminalQueryExecutionWorker extends UIWorkerJob {

    /**
     * The context.
     */
    protected volatile IExecutionContext context;
    private IQueryExecutionSummary latestQuerysummary;
    private static final String QUERY_EXEC_RESULT = "query_execution_result";
    private QueryExecutionOrchestrator orchestrator;

    /**
     * The conn.
     */
    DBConnection conn;
    private boolean canExecute = false;
    private SQLTerminal terminal;
    private final Object INSTANCE_LOCK = new Object();
    private HashSet<Object> listOfObjects = new HashSet<>();

    /**
     * Instantiates a new terminal query execution worker.
     *
     * @param context the context
     */
    public TerminalQueryExecutionWorker(IExecutionContext context) {
        super(context.getContextName(), context.jobType());
        this.context = context;
        conn = null;

        context.getResultDisplayUIManager().initDisplayManager(context.getCurrentExecution());
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws Exception the exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        if (this.context instanceof ResultTabQueryExecuteContext) {
            listOfObjects = null;
            TerminalExecutionSQLConnectionInfra sqlConnection = (TerminalExecutionSQLConnectionInfra) context
                    .getTermConnection();
            if (isNotSavePwdAndNotReuseCon(sqlConnection)) {
                TerminalExecutionConnectionInfra dummyConnection = new TerminalExecutionSQLConnectionInfra();
                dummyConnection.setDatabase(sqlConnection.getDatabase());
                this.context.getResultDisplayUIManager().handlePreExecutionUIDisplaySetup(dummyConnection, false);
                if (this.context.getResultDisplayUIManager()
                        .canContextExecutionContinue() == CanContextContinueExecuteRule.CONTEXT_EXECUTION_STOP) {
                    this.context.getResultDisplayUIManager().handleGridComponentOnDialogCancel();
                    return Status.CANCEL_STATUS;
                }
                conn = dummyConnection.getConnection();
            } else {
                conn = ((TerminalExecutionSQLConnectionInfra) context.getTermConnection()).getSecureConnection(this);
            }
            while (conn == null) {
                Thread.sleep(SQL_TERMINAL_THREAD_SLEEP_TIME);
                if (this.isCancel()) {
                    return null;
                }
                setConnectionIfNotified();
            }
        }

        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_EXECUTE_SQLTERMINAL_QUERY, true);

        SqlQueryExecutionWorkingContext jobContext = getJobContext();

        showExecProgressBarOnExecutionStop();
        /*
         * Pick queries from the SQLQueryExecutionContext and start executing
         * them. Also check if the UI manager is ready to accept the result if
         * the execution is done. It is possible that there is some temporary
         * error because of which the execution has to be stopped for now and
         * tried again later(later here means, UI will correct the problem and
         * start over again).
         */
        executeQueries(jobContext);

        return null;
    }

    private SqlQueryExecutionWorkingContext getJobContext() throws MPPDBIDEException, DatabaseOperationException {
        this.context.setCriticalErrorThrown(false);
        this.context.getResultDisplayUIManager().resetDisplayUIManager();

        /*
         * get the SQLQueryExecutionContext. This actually holds the control
         * details of queries to be executed and next query to be executed by
         * this worker.
         */
        SqlQueryExecutionWorkingContext jobContext = (SqlQueryExecutionWorkingContext) this.context
                .getWorkingJobContext();

        /*
         * if this is NULL, it means the query (batch) is getting executed for
         * the first time. In such cases the SQLQueryExecutionContext must be
         * created. We also parse the queries(if needed) and save it in the
         * context. if the context already exists, just continue using it.
         */
        if (jobContext == null) {
            jobContext = createSQLQueryExecutionContext();
        }
        return jobContext;
    }

    private void showExecProgressBarOnExecutionStop() {
        if (this.context.getResultDisplayUIManager()
                .canContextExecutionContinue() != CanContextContinueExecuteRule.CONTEXT_EXECUTION_STOP) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    synchronized (INSTANCE_LOCK) {
                        context.showExecProgresBar();
                    }
                }
            });
        }
    }

    private void executeQueries(SqlQueryExecutionWorkingContext jobContext)
            throws MPPDBIDEException, DatabaseOperationException {
        while (hasQuries(jobContext)) {
            latestQuerysummary = null;
            String initialQuery = jobContext.next();
            String query = convertShowAndDescSql(initialQuery);
            if ("".equals(query) || query == null) {
                return;
            }
            /*
             * If the job needs to be cancelled, the context might have some
             * work to do. We move the handing to onMPPDBIDEExceptionUIAction
             * method by raising a simple exception.
             */
            if (isCancel()) {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.UI_CANCEL_QUERY));
                throw new DatabaseOperationException(IMessagesConstants.UI_CANCEL_QUERY);
            }
            if (query.trim().length() < 1) {
                continue;
            }
            query = query.trim();
            jobContext.updateCurrentQuery(query);
            /*
             * prepare the data for summary/statistics on execution, like start
             * the timer etc.
             */
            IQueryExecutionSummary summary = SQLHistoryFactory.getNewQueryExecutionSummary(
                    this.context.getTermConnection().getDatabase().getDbName(),
                    this.context.getTermConnection().getDatabase().getServerName(),
                    this.context.getConnectionProfileID(), query);
            latestQuerysummary = summary;
            summary.startQueryTimer();
            ConsoleDataWrapper consoleData = new ConsoleDataWrapper();
            setTerminalConnectionWhenClosed();
            orchestrator = new QueryExecutionOrchestrator(summary, consoleData, context,
                    conn);
            Object materializedResult = orchestrator.executeQuery(query, listOfObjects);
            handlePostExecutionQuery(initialQuery, summary, consoleData, materializedResult);
        }
    }

    private void handlePostExecutionQuery(String initialQuery, IQueryExecutionSummary summary,
            ConsoleDataWrapper consoleData, Object materializedResult) throws MPPDBIDEException {
        /*
         * There are many queries to execute and thus this task's work is not
         * done. displayUIResult will create some UIworkerJobs for the display
         * of result purpose. The result Job is started and this task continue
         * to execute rest of the query.
         */
        displayMaterializedResultToUI(consoleData, materializedResult);
        /*
         * Now that one query is executed and the results materialized, before
         * executing the next one, the execution context might want to do some
         * action. Now is the place and time to do it.
         */
        performPostExecutionAction();
    }

    /**
     * Convert show and desc sql.
     *
     * @param query the query
     * @return the string
     * @throws MPPDBIDEException the MPPDBIDE exception
     *
     * @Title: convertShowAndDescSql
     * @Description: convert Show And Desc Sql
     */
    private String convertShowAndDescSql(String query) throws MPPDBIDEException {
        return query;
    }

    /**
     * Creates the SQL query execution context.
     *
     * @return the sql query execution working context
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws DatabaseOperationException the database operation exception
     */
    private SqlQueryExecutionWorkingContext createSQLQueryExecutionContext()
            throws MPPDBIDEException, DatabaseOperationException {
        IQuerrySplitter querySplitter;
        SqlQueryExecutionWorkingContext jobContext;
        this.context.getResultDisplayUIManager().handlePreExecutionUIDisplaySetup(this.context.getTermConnection(),
                true);

        jobContext = new SqlQueryExecutionWorkingContext();
        this.context.setWorkingJobContext(jobContext);

        if (this.context.needQueryParseAndSplit()) {
            boolean isOLAP = isOLAPCon();
            // Split functionalities moved into SQLTerminalQuerySplit
            querySplitter = new SQLTerminalQuerySplit();
            querySplitter.splitQuerries(jobContext.getQueryArray(), context.getQuery(), isOLAP);
        } else {
            jobContext.getQueryArray().add(context.getQuery());
        }

        this.context.getResultDisplayUIManager().getSingleQueryArray(jobContext.getQueryArray(), context.getQuery());
        return jobContext;
    }

    /**
     * Handle progres bar.
     */
    private void handleProgresBar() {
        Display.getDefault().asyncExec(new Runnable() {

            /**
             * run
             */
            public void run() {
                synchronized (INSTANCE_LOCK) {
                    context.hideExecProgresBar();
                }
            }
        });
    }

    /**
     * Checks if is not save pwd and not reuse con.
     *
     * @param sqlConnection the sql connection
     * @return true, if is not save pwd and not reuse con
     */
    private boolean isNotSavePwdAndNotReuseCon(TerminalExecutionSQLConnectionInfra sqlConnection) {
        return !sqlConnection.getReuseConnectionFlag()
                && sqlConnection.getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE);
    }

    /**
     * Checks for quries.
     *
     * @param jobContext the job context
     * @return true, if successful
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private boolean hasQuries(SqlQueryExecutionWorkingContext jobContext) throws MPPDBIDEException {
        return jobContext.hasNext() && canProceedWithExecution();
    }

    /**
     * Sets the connection if notified.
     */
    private void setConnectionIfNotified() {
        if (this.isNotified()) {
            conn = this.context.getTermConnection().getConnection();
        }
    }

    /**
     * Sets the terminal connection when closed.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    private void setTerminalConnectionWhenClosed() throws DatabaseOperationException {
        if (this.conn == null || this.conn.isClosed()) {
            conn = context.getTermConnection().getConnection();
        }
    }

    /**
     * Display materialized result to UI.
     *
     * @param consoleData the console data
     * @param materializedResult the materialized result
     */
    private void displayMaterializedResultToUI(ConsoleDataWrapper consoleData, Object materializedResult) {
        if (materializedResult != null) {
            displayUIResult(materializedResult, consoleData);
        }
    }

    /**
     * Can proceed with execution silent.
     *
     * @return true, if successful
     */
    private boolean canProceedWithExecutionSilent() {
        for (;;) {
            switch (this.context.getResultDisplayUIManager().canContextExecutionContinue()) {
                case CONTEXT_EXECUTION_UNKNOWN: {
                    /*
                     * If the job needs to be cancelled, the context might have
                     * some work to do. We move the handing to
                     * onMPPDBIDEExceptionUIAction method by raising a simple
                     * exception.
                     */
                    if (isCancel()) {
                        return false;
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException exception) {
                        break;
                    }
                    break;
                }

                case CONTEXT_EXECUTION_STOP: {
                    return false;
                }

                case CONTEXT_EXECUTION_PROCEED: {
                    if (isCancel()) {
                        return false;
                    }

                    return haveMoreQueriesInJobContext();
                }
            }
        }
    }

    /**
     * Can proceed with execution.
     *
     * @return true, if successful
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private boolean canProceedWithExecution() throws MPPDBIDEException {
        for (;;) {
            switch (this.context.getResultDisplayUIManager().canContextExecutionContinue()) {
                case CONTEXT_EXECUTION_UNKNOWN: {
                    /*
                     * If the job needs to be cancelled, the context might have
                     * some work to do. We move the handing to
                     * onMPPDBIDEExceptionUIAction method by raising a simple
                     * exception.
                     */
                    if (isCancel()) {
                        MPPDBIDELoggerUtility
                                .error(MessageConfigLoader.getProperty(IMessagesConstants.UI_CANCEL_QUERY));
                        throw new DatabaseOperationException(IMessagesConstants.UI_CANCEL_QUERY);
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException exception) {
                        break;
                    }
                    break;
                }

                case CONTEXT_EXECUTION_STOP: {
                    this.context.getResultDisplayUIManager().handleGridComponentOnDialogCancel();
                    return false;
                }

                case CONTEXT_EXECUTION_PROCEED: {
                    if (isCancel()) {
                        throw new DatabaseOperationException(IMessagesConstants.UI_CANCEL_QUERY);
                    }

                    return haveMoreQueriesInJobContext();
                }
            }
        }
    }

    /**
     * Have more queries in job context.
     *
     * @return true, if successful
     */
    private boolean haveMoreQueriesInJobContext() {
        SqlQueryExecutionWorkingContext jobContext = (SqlQueryExecutionWorkingContext) this.context
                .getWorkingJobContext();

        if (null != jobContext && jobContext.hasNext()) {
            return true;
        }

        return false;
    }

    /**
     * Perform post execution action.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    protected void performPostExecutionAction() throws MPPDBIDEException {
        ActionAfterResultFetch action = this.context.getResultConfig().getActionAfterFetch();
        switch (action) {
            case ISSUE_COMMIT_CONNECTION_AFTER_FETCH: {
                performCommitConnection();
                break;
            }
            case ISSUE_ROLLBACK_CONNECTION_AFTER_FETCH: {
                performRollbackConnection();
                break;
            }
            case ISSUE_NO_OP: {
                // Do nothing. This happens when Auto Commit is set
                // to OFF by User. User has to manually COMMIT or
                // ROLLBACK the transaction.
                break;
            }
            case CLOSE_CONNECTION_AFTER_FETCH:
            default: {
                closeConnection();
                break;
            }
        }

        /*
         * now the UI manager might want to do something. Like for example
         * update progress bar etc.
         */
        context.getResultDisplayUIManager().handleStepCompletion();
        getMemoryUsage();

    }

    /**
     * Gets the memory usage.
     *
     * @return the memory usage
     */
    private void getMemoryUsage() {
        if (!IDEMemoryAnalyzer.is90PercentReached() && IDEMemoryAnalyzer.getTotalUsedMemoryPercentage() >= 90) {
            IDEMemoryAnalyzer.setIs90PercentReached(true);
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.MEMORY_USAGE),
                    MessageConfigLoader.getProperty(IMessagesConstants.MEMORY_USAGE_WARNING));
        } else if (IDEMemoryAnalyzer.is90PercentReached() && IDEMemoryAnalyzer.getTotalUsedMemoryPercentage() < 90) {
            IDEMemoryAnalyzer.setIs90PercentReached(false);
        }
    }

    /**
     * Perform rollback connection.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private void performRollbackConnection() throws MPPDBIDEException {
        this.context.getTermConnection().getConnection().rollback();

    }

    /**
     * Perform commit connection.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private void performCommitConnection() throws MPPDBIDEException {
        this.context.getTermConnection().getConnection().commitConnection("Error while completing transaction");

    }

    /**
     * Close connection.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private void closeConnection() throws MPPDBIDEException {
        this.context.getTermConnection().releaseConnection();
    }

    /**
     * Display UI result.
     *
     * @param queryMatResult the query mat result
     * @param consoleData the console data
     */
    private void displayUIResult(Object queryMatResult, ConsoleDataWrapper consoleData) {
        /**
         * Invoke the ResultDisplay Ui Manager to handle the UI part of
         * displaying the result.
         */
        if (null != this.context.getResultDisplayUIManager().getEventBroker()) {
            this.context.getResultDisplayUIManager().getEventBroker().post(QUERY_EXEC_RESULT, this.latestQuerysummary);
        }
        context.getResultDisplayUIManager().handleResultDisplay(queryMatResult, consoleData, this.latestQuerysummary);
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        handleProgresBar();

        /*
         * the control might have jumped out from the doJob method, but it is
         * not for sure that it was success case. So we check if the UI manager
         * had set the flag for this context to complete and it actually
         * completed. Only then we claim successful and do its related
         * handling...
         */
        if (CanContextContinueExecuteRule.CONTEXT_EXECUTION_PROCEED == this.context.getResultDisplayUIManager()
                .canContextExecutionContinue()) {
            this.context.handleSuccessfullCompletion();
        }
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        handleCritcalExceptionPreOperation(exception);

        if (CanContextContinueExecuteRule.CONTEXT_EXECUTION_PROCEED == this.context.getResultDisplayUIManager()
                .canContextExecutionContinue()) {
            reconnectTerminalConnectionOnBtnClik();
            this.conn = this.context.getTermConnection().getConnection();

            if (context.getTermConnection().getAutoCommitFlag() == true) {
                handleExceptionOnAutoCommitTrue();

            } else if (context.getTermConnection().getAutoCommitFlag() == false) {
                SqlQueryExecutionWorkingContext workingJobContext;
                IQuerrySplitter querySplitter = null;

                // In reconnect pop up if reconnect clicked, the job content
                // should reset to new
                workingJobContext = new SqlQueryExecutionWorkingContext();
                this.context.setWorkingJobContext(workingJobContext);

                if (this.context.needQueryParseAndSplit()) {
                    boolean isOLAP = isOLAPCon();
                    // Split functionalities moved into SQLTerminalQuerySplit
                    querySplitter = new SQLTerminalQuerySplit();
                    try {
                        querySplitter.splitQuerries(workingJobContext.getQueryArray(), context.getQuery(), isOLAP);
                    } catch (DatabaseOperationException e1) {
                        handleDbOperationExceptionOnSplitQuery(e1);
                        return;
                    }
                } else {
                    workingJobContext.getQueryArray().add(context.getQuery());
                }

                handleExceptionOnAutoCommitFalse(workingJobContext);

            }
        } else if (context.getTermConnection().isReconnectOnTerminal()) {
            reconnectTerminalConnectionOnBtnClik();
            this.conn = this.context.getTermConnection().getConnection();
            return;
        }

    }

    private void handleCritcalExceptionPreOperation(DatabaseCriticalException exception) {
        handleProgresBar();
        handleQueryExecutionFailure();
        this.context.setCriticalErrorThrown(true);
        this.context.handleExecutionException(exception);
        context.getTermConnection().releaseConnection();
        // In reconnect pop up if reconnect & continue is clicked, the job
        // content should start from where it failed
    }

    private void handleDbOperationExceptionOnSplitQuery(DatabaseOperationException e1) {
        MPPDBIDELoggerUtility.error("TerminalqueryExecutionWorker: splitting queries failed.", e1);
        if (this.context instanceof ResultTabQueryExecuteContext) {
            ((TerminalExecutionSQLConnectionInfra) context.getTermConnection()).releaseSecureConnection(this.conn);
        }
    }

    private void handleExceptionOnAutoCommitFalse(SqlQueryExecutionWorkingContext workingJobContext) {
        this.context.getResultDisplayUIManager().getSingleQueryArray(workingJobContext.getQueryArray(),
                context.getQuery());
        if (this.context instanceof ResultTabQueryExecuteContext) {
            ((TerminalExecutionSQLConnectionInfra) context.getTermConnection()).releaseSecureConnection(this.conn);
        }
    }

    private void handleExceptionOnAutoCommitTrue() {
        SqlQueryExecutionWorkingContext workingJobContext = (SqlQueryExecutionWorkingContext) this.context
                .getWorkingJobContext();
        if (this.context instanceof ResultTabQueryExecuteContext) {
            ((TerminalExecutionSQLConnectionInfra) context.getTermConnection()).releaseSecureConnection(this.conn);
        }
        workingJobContext.previous();
    }

    private boolean isOLAPCon() {
        boolean isOLAP = true;
        if (null != this.context.getTermConnection().getConnection()) {
            isOLAP = this.context.getTermConnection().getConnection().isOLAPConnection();
        }
        return isOLAP;
    }

    /**
     * Reconnect terminal connection on btn clik.
     */
    private void reconnectTerminalConnectionOnBtnClik() {
        this.context.getResultDisplayUIManager()
                .handlePreExecutionUIDisplaySetupCritical(this.context.getTermConnection(), true);
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        handleProgresBar();

        /**
         * if the context wishes to abort the execution for an error,then stop
         * the work and return to the context owner.
         */
        handleQueryExecutionFailure();
        if (this.context.getActionOnQueryFailure() == ExecutionFailureActionOptions.EXECUTION_FAILURE_ACTION_ABORT) {
            DBAssistantWindow.execErr(exception.getServerMessage() != null ? exception.getServerMessage() : "");
            this.context.handleExecutionException(exception);
        } else {
            MPPDBIDELoggerUtility.debug("Continuing Execution inspite of error during execution as per configuration");
        }

    }

    /**
     * Handle query execution failure.
     */
    private void handleQueryExecutionFailure() {
        if (null != this.latestQuerysummary) {
            this.latestQuerysummary.stopQueryTimer();
            this.latestQuerysummary.setQueryExecutionStatus(false);
            if (null != this.context.getResultDisplayUIManager().getEventBroker()) {
                this.context.getResultDisplayUIManager().getEventBroker().post(QUERY_EXEC_RESULT,
                        this.latestQuerysummary);
            }
        }
    }

    /**
     * On MPPDBIDE exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {
        handleProgresBar();
        handleQueryExecutionFailure();
        this.context.handleExecutionException(exception);
    }

    /**
     * On exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onExceptionUIAction(Exception exception) {
        handleProgresBar();
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        if (canExecute) {
            this.schedule();
        } else {
            this.context.setJobDone();
            if (this.context.canFreeConnectionAfterUse()) {
                this.context.getTermConnection().releaseConnection();
                MPPDBIDELoggerUtility.debug("Connection released after use.");
            }
            if (this.context instanceof ResultTabQueryExecuteContext) {
                ((TerminalExecutionSQLConnectionInfra) context.getTermConnection()).releaseSecureConnection(this.conn);
            }
        }
        canExecute = false;
    }

    /**
     * Pre final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void preFinalCleanup() throws MPPDBIDEException {
        canExecute = canProceedWithExecutionSilent();
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        UIElement.getInstance().autoRefresh(listOfObjects);
        if (terminal != null) {
            terminal.resetCommitAndRollbackButton();
        }
        if (!canExecute) {
            if (this.context.getResultDisplayUIManager() instanceof AbstractEditTableDataResultDisplayUIManager) {
                ((AbstractEditTableDataResultDisplayUIManager) this.context.getResultDisplayUIManager())
                        .handlePostGridDataLoadEvent();
            }
            this.context.getResultDisplayUIManager().handleFinalCleanup();
        }
    }

    /**
     * Canceling.
     */
    @Override
    protected void canceling() {
        if (isCancel()) {
            return;
        } else {
            handleProgresBar();
            ConsoleDataWrapper consoleMsg = new ConsoleDataWrapper();
            consoleMsg.add(MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUERY_CANCELMSG_PROGRESS));
            this.context.getResultDisplayUIManager().handleConsoleDisplay(consoleMsg);

            super.canceling();
            this.context.getResultDisplayUIManager().handleCancelRequest();

            try {
                Statement stmt = this.orchestrator.getStatement();
                if (null != stmt) {
                    cancelSQLExecutionQuery(stmt);
                } else {
                    DBConnection conne = this.context.getTermConnection().getConnection();
                    if (null != conne) {
                        conne.cancelQuery();
                    }
                }
            } catch (MPPDBIDEException e1) {
                ConsoleDataWrapper consoleData = new ConsoleDataWrapper();

                consoleData.add(MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUERY_CANCEL_CANCELMSG));
                this.context.getResultDisplayUIManager().handleConsoleDisplay(consoleData);
            }
        }
    }
      
    /**
     * Cancel SQL Execution query.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void cancelSQLExecutionQuery(Statement stmt) throws DatabaseCriticalException, DatabaseOperationException {
        try { 
            MPPDBIDELoggerUtility.info("ADAPTER: Sending cancel request");
                if (stmt != null && !stmt.isClosed()) {
                    stmt.cancel();
                }
            
            MPPDBIDELoggerUtility.info("ADAPTER: Cancel successfully executed");
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            MPPDBIDELoggerUtility.error("ADAPTER: cancel query returned exception.", exp);
        } finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("ADAPTER: statement close returned exception.", exception);
            }
        }
    }

    /**
     * Sets the terminal.
     *
     * @param terminal the new terminal
     */
    public void setTerminal(SQLTerminal terminal) {
        this.terminal = terminal;
    }
}
