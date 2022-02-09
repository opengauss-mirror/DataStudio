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

package org.opengauss.mppdbide.view.terminal;

import java.sql.Statement;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseUtils;
import org.opengauss.mppdbide.bl.serverdatacache.IQueryMaterializer;
import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.presentation.IExecutionContext;
import org.opengauss.mppdbide.presentation.edittabledata.CursorQueryExecutor;
import org.opengauss.mppdbide.presentation.edittabledata.QueryResultMaterializer;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.resultset.ConsoleDataWrapper;
import org.opengauss.mppdbide.presentation.resultsetif.IResultConfig;
import org.opengauss.mppdbide.utils.CheckSelectQuery;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.JSQLParserUtils;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.MultiLineComment;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.autorefresh.AutoRefreshFactory;
import org.opengauss.mppdbide.view.terminal.executioncontext.EditTableDataExecutionContext;
import org.opengauss.mppdbide.view.terminal.executioncontext.SQLTerminalExecutionContext;
import org.opengauss.mppdbide.view.ui.terminal.resulttab.ResultTabQueryExecuteContext;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * Title: class Description: The Class QueryExecutionOrchestrator.
 *
 * @since 3.0.0
 */
public class QueryExecutionOrchestrator {
    private IQueryExecutionSummary execSummary;

    private IExecutionContext execContext;

    private ConsoleDataWrapper consoleWrapper;

    private DBConnection connection;
    
    private CursorQueryExecutor queryExecutor = null;
    
    private Statement stmt = null;

    private volatile int okClickCursorDialog;

    /**
     * Instantiates a new query execution orchestrator.
     *
     * @param summary the summary
     * @param consoleData the console data
     * @param context the context
     * @param connection the connection
     */
    public QueryExecutionOrchestrator(IQueryExecutionSummary summary, ConsoleDataWrapper consoleData,
            IExecutionContext context, DBConnection connection) {
        this.execSummary = summary;
        this.execContext = context;
        this.consoleWrapper = consoleData;
        this.connection = connection;
    }

    /**
     * Execute query.
     *
     * @param query the query
     * @param listOfObjects the list of objects
     * @return the object
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public Object executeQuery(String query, HashSet<Object> listOfObjects) throws MPPDBIDEException {
        if (isValidateQueryForSelect(query)) {
            queryExecutor = new CursorQueryExecutor(query, this.execContext, execSummary, isEditTableData(),
                    isQueryResultEditing(query), connection);
            try {
                IDSGridDataProvider result = queryExecutor.execute(this.execSummary);
                report();
                return result;
            }
            catch (DatabaseOperationException exception) {
                String serverMsg = exception.getServerMessage();
                String matchTitle = MessageConfigLoader.getProperty(IMessagesConstants.CURSOR_FAILURE_SQL_ERROR);
                if (serverMsg.contains(matchTitle) && serverMsg.contains("000")) {
                    Display.getDefault().syncExec(new Runnable() {
                        /**
                         * run
                         */
                        public void run() {
                            okClickCursorDialog = MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.QUESTION,
                                    true,
                                    MessageConfigLoader.getProperty(IMessagesConstants.CURSOR_IMPLEMENT_DIALOG_TITLE),
                                    MessageConfigLoader.getProperty(IMessagesConstants.CURSOR_IMPLEMENT_DIALOG_HEADER));
                        }
                    });
                    if (okClickCursorDialog == 0) {
                        return executeQueryWithoutCursor(query, listOfObjects);
                    }
                }
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE), exception);
                throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, exception);
            }
        } else {
            return executeQueryWithoutCursor(query, listOfObjects);
        }
    }

    private boolean isValidateQueryForSelect(String queryParam) {
        String query = removeCommentsifAny(queryParam);
        return CheckSelectQuery.isSelectQuery(query) && isCursorSupported() && !JSQLParserUtils.isCopyQuery(query);
    }
    
    private String removeCommentsifAny(String queryParam) {
        String query = queryParam.trim();
        MultiLineComment commentParser = new MultiLineComment(query);
        while (commentParser.find()) {
            String originalStr = query.substring(commentParser.start(), commentParser.end());
            query = query.replace(originalStr, "");
            commentParser = new MultiLineComment(query);
        }
        Pattern pattern = Pattern.compile("(?m)(--.*?(" + MPPDBIDEConstants.LINE_SEPARATOR + "|$)|\\/\\*.*?\\*\\/)");
        Matcher matcher = pattern.matcher(query.trim());
        while (matcher.find()) {
            String originalStr = query.trim().substring(matcher.start(), matcher.end());
            query = query.replace(originalStr, "");
            matcher = pattern.matcher(query.trim());
        }
        return query.trim();
    }

    /**
     * Execute query without cursor.
     *
     * @param query the query
     * @param listOfObjects the list of objects
     * @return the object
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private Object executeQueryWithoutCursor(String query, HashSet<Object> listOfObjects) throws MPPDBIDEException {
        Object executeNonSelect = executeNonSelect(query);
        report();
        if (!(CheckSelectQuery.isSelectQuery(query) || isDMLQuery(query) || isShowQuery(query))) {
            AutoRefreshFactory.getAutoRefreshInstance(listOfObjects, execContext).executeAutoRefresh(query);
        }
        return executeNonSelect;
    }
    
    /**
     * gets the statement
     * 
     * @return the statement
     */
    public Statement getStatement() {
        if (null != queryExecutor) {
            stmt = this.queryExecutor.getStatement();
        }
        return stmt;
    }

    /**
     * Checks if is cursor supported.
     *
     * @return true, if is cursor supported
     */
    private boolean isCursorSupported() {
        return this.execContext.getTermConnection().getDatabase().isCursorSupported();
    }

    /**
     * Report.
     */
    private void report() {
        consoleWrapper.add(0, MessageConfigLoader.getProperty(IMessagesConstants.ROWS_AFFECTED,
                this.execSummary.getNumRecordsFetched()));
        this.execSummary.setQueryExecutionStatus(true);
        this.execSummary.stopQueryTimer();
        MPPDBIDELoggerUtility.info(MessageConfigLoader.getProperty(
                IMessagesConstants.TIME_BY_TAKEN_APPLICATION_EXECUTE_QUERY, this.execSummary.getElapsedTime()));
        consoleWrapper.add(MessageConfigLoader.getProperty(IMessagesConstants.EXE_TERMINAL_EXC_TIME_MSG_RESULT,
                this.execSummary.getElapsedTime()));
        consoleWrapper.add(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_STATUS));

    }

    /**
     * Execute non select.
     *
     * @param query the query
     * @return the object
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private Object executeNonSelect(String query) throws MPPDBIDEException {
        IResultConfig resultConfig = this.execContext.getResultConfig();
        if (execContext.getInputValues() == null) {
            this.connection.setIsInOutValueExists(false);
        }
        IQueryMaterializer queryMaterializer = new QueryResultMaterializer(resultConfig, execSummary, consoleWrapper,
                execContext, isEditTableData(), isQueryResultEditing(query));

        return DatabaseUtils.executeOnQueryWithMaterializer(query, resultConfig.getFetchCount(), this.connection,
                this.execContext.getNoticeMessageQueue(), queryMaterializer);
    }

    /**
     * Checks if is edits the table data.
     *
     * @return true, if is edits the table data
     */
    private boolean isEditTableData() {
        return this.execContext instanceof EditTableDataExecutionContext;
    }

    /**
     * Checks if is query result editing.
     *
     * @param query the query
     * @return true, if is query result editing
     * @throws DatabaseCriticalException the database critical exception
     */
    private boolean isQueryResultEditing(String query) throws DatabaseCriticalException {
        return (this.execContext instanceof SQLTerminalExecutionContext
                || this.execContext instanceof ResultTabQueryExecuteContext)
                && JSQLParserUtils.isQueryResultEditSupported(query);
    }

    /**
     * Checks if is DML query.
     *
     * @param query the query
     * @return true, if is DML query
     */
    private boolean isDMLQuery(String query) {
        if (query.toLowerCase(Locale.ENGLISH).startsWith("insert")
                || query.toLowerCase(Locale.ENGLISH).startsWith("update")
                || query.toLowerCase(Locale.ENGLISH).startsWith("delete")
                || query.toLowerCase(Locale.ENGLISH).startsWith("merge")
                || query.toLowerCase(Locale.ENGLISH).startsWith("commit")
                || query.toLowerCase(Locale.ENGLISH).startsWith("rollback")) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is show query.
     *
     * @param query the query
     * @return true, if is show query
     */
    private boolean isShowQuery(String query) {
        return query.toLowerCase(Locale.ENGLISH).startsWith("show");
    }

}
