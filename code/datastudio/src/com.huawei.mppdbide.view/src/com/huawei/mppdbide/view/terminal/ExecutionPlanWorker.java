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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DatabaseUtils;
import com.huawei.mppdbide.bl.serverdatacache.IQueryMaterializer;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.QueryResult;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import com.huawei.mppdbide.explainplan.service.ExplainPlanAnlysisService;
import com.huawei.mppdbide.presentation.IExplainPlanExecutionContext;
import com.huawei.mppdbide.presentation.TerminalExecutionSQLConnectionInfra;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.edittabledata.QueryResultMaterializer;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.presentation.resultset.ConsoleDataWrapper;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelConverter;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.ILogger;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.utils.messaging.MessageType;
import com.huawei.mppdbide.view.handler.connection.AbstractTerminalConnReconnectWorkerUIJob;
import com.huawei.mppdbide.view.terminal.executioncontext.ExecutionExplainPlanContext;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.ui.visualexplainplan.VisualExplainPlanHandler.RunQueryAndStartPart;
import com.huawei.mppdbide.view.utils.GUISM;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecutionPlanWorker.
 *
 * @since 3.0.0
 */
public class ExecutionPlanWorker extends AbstractTerminalConnReconnectWorkerUIJob {
    private List<String> queryArray;
    private boolean restartJob = false;
    private volatile IExplainPlanExecutionContext context;
    private volatile UIModelAnalysedPlanNode analysedPlanOutput;
    private JobCancelStatus cancelStatus = null;
    private RefreshObservable ob = null;
    private final Object INSTANCE_LOCK = new Object();

    /**
     * 
     * Title: class
     * 
     * Description: The Class RefreshObservable.
     */
    private static class RefreshObservable extends Observable {

        /**
         * Sets the refresh is in progress.
         *
         * @param refreshing the new refresh is in progress
         */
        public void setRefreshIsInProgress(boolean refreshing) {
            setChanged();
            notifyObservers(refreshing);
        }
    }

    /**
     * Instantiates a new execution plan worker.
     *
     * @param queryArray2 the query array 2
     * @param context the context
     */
    public ExecutionPlanWorker(List<String> queryArray2, IExplainPlanExecutionContext context) {
        super(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_JOB), MPPDBIDEConstants.CANCELABLEJOB,
                IMessagesConstants.EXPLAIN_PLAN_ERROR_POPUP_HEADER, UIElement.getInstance().getSqlTerminalModel());
        this.queryArray = queryArray2;

        this.context = context;
        this.cancelStatus = new JobCancelStatus();

        if (context.getObserver() != null) {
            this.ob = new RefreshObservable();
            ob.addObserver(context.getObserver());
        }
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
        restartJob = false;

        if (establishConnection()) {
            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_EXECUTE_SQLTERMINAL_QUERY, true);
            this.terminal.setExplainPlanInProgress();
            execPlanAndDisplayResultJob(queryArray, context);
        }

        return null;
    }

    /**
     * Exec plan and display result job.
     *
     * @param queryAray the query aray
     * @param eContext the e context
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void execPlanAndDisplayResultJob(final List<String> queryAray, IExplainPlanExecutionContext eContext)
            throws DatabaseCriticalException, DatabaseOperationException {
        boolean isAnalyze = this.context.isAnalyze();
        boolean isAutoCommitChanged = false;
        Database db = getTaskDB();
        MessageQueue msgQ = this.terminal.getConsoleMessageWindow(false).getMsgQueue();

        /*
         * autocommit off
         */
        try {
            if (conn.getConnection().getAutoCommit()) {
                conn.getConnection().setAutoCommit(false);
                isAutoCommitChanged = true;
            }
        } catch (SQLException exception) {
            msgQ.push(new Message(MessageType.ERROR, exception.getMessage()));
            MPPDBIDELoggerUtility.error("unable to set proper autocommit state", exception);
            return;
        }

        /*
         * now actual queries, except the last one
         */
        executeQueries(queryAray, isAutoCommitChanged, db, msgQ);

        explainPlanForLastQuery(queryAray, eContext, isAnalyze, isAutoCommitChanged, db, msgQ);

        /*
         * following is test flow
         */
        scheduleRunQueryAndStartPartJob(queryAray, isAnalyze, msgQ);
    }

    private void explainPlanForLastQuery(final List<String> queryAray, IExplainPlanExecutionContext eContext,
            boolean isAnalyze, boolean isAutoCommitChanged, Database db, MessageQueue msgQ)
            throws DatabaseCriticalException, DatabaseOperationException {
        DSResultSetGridDataProvider dataProvider = null;
        IQueryExecutionSummary summary = null;
        ConsoleDataWrapper consoleData = null;
        double totalRuntime = 0.0;
        try {
            // now explain plan for the first query
            String selectedQry = queryAray.get(0);
            String qry = getExecutionPlanCostQuery() + selectedQry;
            summary = SQLHistoryFactory.getNewExlainQueryExecutionSummary(db.getDbName(), db.getServerName(),
                    db.getServer().getServerConnectionInfo().getConectionName(), selectedQry);
            summary.setAnalyze(this.context.isAnalyze());
            consoleData = new ConsoleDataWrapper();
            IResultConfig resultConfig = eContext.getResultConfig();

            IQueryMaterializer materializer = new QueryResultMaterializer(resultConfig, summary, consoleData, eContext,
                    false, false);
            summary.startQueryTimer();
            dataProvider = (DSResultSetGridDataProvider) DatabaseUtils.executeOnQueryWithMaterializer(qry, 0, conn,
                    msgQ, materializer);
            summary.stopQueryTimer();
        } finally {
            resetAutoCommit(isAutoCommitChanged);
        }

        if (null == dataProvider) {
            DatabaseOperationException exception = new DatabaseOperationException(
                    IMessagesConstants.DATABASE_CONNECTION_ERR);
            consoleLogExecutionFailure(exception);
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR));
            throw exception;
        }

        /*
         * create tree hierarchy of the plan
         */
        totalRuntime = preparePlanModel(isAnalyze, msgQ, dataProvider);

        /*
         * display plan
         */
        displayPlanInNewTab(eContext, summary, consoleData, dataProvider, totalRuntime);
    }

    private void scheduleRunQueryAndStartPartJob(final List<String> queryAray, boolean isAnalyze, MessageQueue msgQ) {
        if (isAnalyze && ((UserPreference) UserPreference.getInstance()).isIsenableTestability()) {
            // show a visual explain window
            // slowest, heaviest, costliest info should be the same
            RunQueryAndStartPart job = new RunQueryAndStartPart(null, (ArrayList<String>) queryAray, msgQ,
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_JOB_DETAILS),
                    MPPDBIDEConstants.CANCELABLEJOB, null, terminal, true);
            // plan will be taken from dumped file, so no need to pass query

            job.schedule();
        }
    }

    /**
     * Gets the execution plan cost query.
     *
     * @return the execution plan cost query
     */
    private String getExecutionPlanCostQuery() {
        boolean analyseCheck = this.context.isAnalyze();
        StringBuilder planQuery = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        planQuery.append("EXPLAIN ");
        planQuery.append("(");
        planQuery.append("ANALYSE ");
        planQuery.append(analyseCheck);
        planQuery.append(",");
        planQuery.append("VERBOSE ");
        planQuery.append(true);
        planQuery.append(",");
        planQuery.append("COSTS ");
        planQuery.append(true);
        planQuery.append(",");
        planQuery.append("BUFFERS ");
        planQuery.append(analyseCheck);
        planQuery.append(",");
        planQuery.append("TIMING ");
        planQuery.append(analyseCheck);
        planQuery.append(",");
        planQuery.append("FORMAT ");
        planQuery.append("JSON");
        planQuery.append(")");
        return planQuery.toString();
    }

    /**
     * Prepare plan model.
     *
     * @param isAnalyze the is analyze
     * @param msgQ the msg Q
     * @param dataProvider the data provider
     * @param totalRuntime the total runtime
     * @return the double
     * @throws DatabaseOperationException the database operation exception
     */
    private double preparePlanModel(boolean isAnalyze, MessageQueue msgQ, DSResultSetGridDataProvider dataProvider)
            throws DatabaseOperationException {
        double totalRuntime = 0.0;
        try {
            List<IDSGridDataRow> rows = dataProvider.getAllFetchedRows();
            String jsonPlan = "";
            if (!rows.isEmpty()) {
                jsonPlan = (String) rows.get(0).getValue(0);
            }
            if (isAnalyze && ((UserPreference) UserPreference.getInstance()).isIsenableTestability()) {
                File file = null;
                try {
                    file = new File(MPPDBIDEConstants.JSON_PLAN_DUMP_FILE);
                    Files.deleteIfExists(file.toPath());
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("error while deleting old file", exception);
                    throw new DatabaseOperationException(IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT);
                }

                OutputStreamWriter fileWriter = null;
                try {
                    fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
                    fileWriter.write(jsonPlan);
                    fileWriter.flush();
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("error while writing json plan to file for test", exception);
                    throw new DatabaseOperationException(IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT);
                } finally {
                    try {
                        if (null != fileWriter) {
                            fileWriter.close();
                            fileWriter = null;
                        }
                    } catch (IOException exception) {
                        /* No way to recover from close failure */
                        MPPDBIDELoggerUtility.error("Error while closing file writer", exception);
                    }
                }
            }

            ExplainPlanAnlysisService planAnalysis = new ExplainPlanAnlysisService(jsonPlan);
            this.setAnalysedPlanOutput(UIModelConverter.covertToUIModel(planAnalysis.doAnalysis()));
            if (isAnalyze) {
                totalRuntime = planAnalysis.getRootPlan().getTotalRuntime();
            }
        } catch (Exception exception) {
            msgQ.push(new Message(MessageType.ERROR, exception.getMessage()));
            MPPDBIDELoggerUtility.error("error while fetching or parsing json plan", exception);
            throw new DatabaseOperationException(IMessagesConstants.VIS_EXPLAIN_JSON_PARSING_FAILED);
        }
        return totalRuntime;
    }

    /**
     * Execute queries.
     *
     * @param queryAray the query aray
     * @param isAutoCommitChanged the is auto commit changed
     * @param db the db
     * @param msgQ the msg Q
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void executeQueries(final List<String> queryAray, boolean isAutoCommitChanged, Database db,
            MessageQueue msgQ) throws DatabaseCriticalException, DatabaseOperationException {

        try {
            /*
             * execute all queries but the first one in Analyze case The same is
             * existing code of visual explain plan
             */
            executeNonExplainQueries(conn, queryAray, msgQ);
        } catch (DatabaseOperationException e) {
            resetAutoCommit(isAutoCommitChanged);
            throw e;
        } catch (DatabaseCriticalException e) {
            resetAutoCommit(isAutoCommitChanged);
            throw e;
        }
    }

    /**
     * Reset auto commit.
     *
     * @param isAutoCommitChanged the is auto commit changed
     */
    private void resetAutoCommit(boolean isAutoCommitChanged) {
        if (isAutoCommitChanged) {
            conn.rollback();
            try {
                conn.getConnection().setAutoCommit(true);
            } catch (SQLException e1) {
                MPPDBIDELoggerUtility.error("Unable to reset Autocommit state", e1);
            }
        }
    }

    /**
     * Display plan in new tab.
     *
     * @param contxt the contxt
     * @param summary the summary
     * @param consoleData the console data
     * @param dataProvider the data provider
     * @param totalRuntime the total runtime
     */
    private void displayPlanInNewTab(final IExplainPlanExecutionContext contxt, final IQueryExecutionSummary summary,
            final ConsoleDataWrapper consoleData, final DSResultSetGridDataProvider dataProvider,
            final double totalRuntime) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                boolean bool = GUISM.isExecutionInProgress(GUISM.RESULTSET);
                while (bool) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException exception) {
                        MPPDBIDELoggerUtility.error("Display plan is Interrupted", exception);
                    }

                    bool = GUISM.isExecutionInProgress(GUISM.RESULTSET);
                }

                displayUIResult(contxt, analysedPlanOutput, consoleData, summary, totalRuntime);

            }

            private void displayUIResult(IExplainPlanExecutionContext contxt,
                    UIModelAnalysedPlanNode analyzedPlanOutput, ConsoleDataWrapper consoleData,
                    IQueryExecutionSummary summary, double totalRuntime) {
                contxt.handleResultDisplay(analyzedPlanOutput, consoleData, summary, totalRuntime);
            }
        });
    }

    /**
     * Execute non explain queries.
     *
     * @param termCon the term con
     * @param queryAray the query aray
     * @param msgQ the msg Q
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void executeNonExplainQueries(final DBConnection termCon, final List<String> queryAray,
            final MessageQueue msgQ) throws DatabaseCriticalException, DatabaseOperationException {
        int aaraySize = queryAray.size();
        // more than one query selected
        if (aaraySize > 1) {
            // execute the queries but the last one
            int queryCounter = aaraySize - 1;
            int fetchCount = UserPreference.getInstance().getResultDataFetchCount();
            for (int i = 0; i < aaraySize - 1; i++) {
                DatabaseUtils.executeOnSqlTerminalAndReturnNothing(queryAray.get(i), fetchCount, termCon, msgQ);
            }

            StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append(queryCounter);
            if (queryCounter > 1) {
                sb.append(" queries");
            } else {
                sb.append(" query");
            }
            sb.append(" executed successfully");
            final String message = sb.toString();

            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    terminal.getConsoleMessageWindow(true).logInfoInUI(message);
                }
            });
        }
    }

    /**
     * Gets the analysed plan output.
     *
     * @return the analysed plan output
     */
    public UIModelAnalysedPlanNode getAnalysedPlanOutput() {
        return analysedPlanOutput;
    }

    /**
     * Sets the analysed plan output.
     *
     * @param analysedPlanOutput the new analysed plan output
     */
    public void setAnalysedPlanOutput(UIModelAnalysedPlanNode analysedPlanOutput) {
        this.analysedPlanOutput = analysedPlanOutput;
        this.analysedPlanOutput.setAnalyze(this.context.isAnalyze());
    }

    /**
     * Pre UI setup.
     *
     * @param preHandlerObject the pre handler object
     * @return true, if successful
     */
    @Override
    public boolean preUISetup(Object preHandlerObject) {
        /*
         * termConnection should not be null. It is set here, released in
         * finalCleanup
         */
        termConnection = terminal.getTermConnection();
        if (null == termConnection.getConnection()) {
            try {
                termConnection = (TerminalExecutionSQLConnectionInfra) PromptPrdGetConnection
                        .getConnection(this.context.getTermConnection());
                this.terminal.setAutoCommitStatus();
            } catch (MPPDBIDEException exception) {
                onCriticalExceptionUIAction(new DatabaseCriticalException(IMessagesConstants.DATABASE_CONNECTION_ERR));
                finalCleanup();
                return false;
            }
        }

        if (this.context instanceof ExecutionExplainPlanContext) {
            /*
             * setup display
             */
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    synchronized (INSTANCE_LOCK) {
                        context.getResultDisplayUIManager().initDisplayManager(context.getCurrentExecution());
                    }
                }
            });
        }

        if (termConnection.getReuseConnectionFlag()) {
            return true;
        }
        return promptAndValidatePassword();
    }

    /**
     * Canceling.
     */
    @Override
    protected void canceling() {
        super.canceling();
        try {
            if (conn != null) {
                conn.cancelQuery();
            }
            cancelStatus.setCancel(true);
        } catch (DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("Error while cancelling query..", exception);
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Error while cancelling query..", exception);
        }
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_EXECUTE_SQLTERMINAL_QUERY, false);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DisplayConsoleMsg.
     */
    private static class DisplayConsoleMsg implements Runnable {
        private MPPDBIDEException exception;

        /**
         * Instantiates a new display console msg.
         *
         * @param exception the e
         */
        protected DisplayConsoleMsg(MPPDBIDEException exception) {
            this.exception = exception;
        }

        @Override
        public void run() {
            final SQLTerminal terminal = UIElement.getInstance().getSqlTerminalModel();

            // Terminal closed, so nothing to execute.
            if (null == terminal) {
                return;
            }

            String message = MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_FAIL_MSG,
                    MPPDBIDEConstants.LINE_SEPARATOR, exception.getErrorCode(),
                    exception.getServerMessage() == null ? exception.getDBErrorMessage()
                            : exception.getServerMessage());

            terminal.getConsoleMessageWindow(true).logError(message);
            MPPDBIDELoggerUtility.error("ExecutionPlanPopUp: displaying console message failed.");
        }
    }

    /**
     * Print the error message on console.
     *
     * @param exception the e
     */
    private void consoleLogExecutionFailure(MPPDBIDEException exception) {
        Display.getDefault().asyncExec(new DisplayConsoleMsg(exception));
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        if (cancelStatus.getCancel()) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));

            return;
        }
        consoleLogExecutionFailure(exception);
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        super.finalCleanupUI();
        terminal.resetCommitAndRollbackButton();
        if (ob != null) {
            ob.setRefreshIsInProgress(false);
        }
        context.getResultDisplayUIManager().handleFinalCleanup();
    }

    /**
     * Sets the reconnect flag.
     *
     * @param isReconnect the new reconnect flag
     */
    @Override
    protected void setReconnectFlag(boolean isReconnect) {
        this.restartJob = isReconnect;
    }

    /**
     * Gets the reconnect flag.
     *
     * @return the reconnect flag
     */
    @Override
    protected boolean getReconnectFlag() {
        return restartJob;
    }

    /**
     * Reset workin progress.
     */
    @Override
    protected void resetWorkinProgress() {
        /*
         * re-enable the terminal button
         */
        this.terminal.resetExplainPlanInProgress();
    }
}
