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

package com.huawei.mppdbide.view.ui.terminal;

import java.util.ArrayList;
import java.util.Locale;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.errorlocator.IErrorLocator;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.CanContextContinueExecuteRule;
import com.huawei.mppdbide.presentation.ContextExecutionOperationType;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.stringparse.IServerMessageParseUtils;
import com.huawei.mppdbide.view.core.ConsoleCoreWindow;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.QueryInfo;
import com.huawei.mppdbide.view.ui.UIErrorLocator;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLTerminalResultDisplayUIManager.
 *
 * @since 3.0.0
 */
public class SQLTerminalResultDisplayUIManager extends AbstractResultDisplayUIManager {
    private SQLTerminal terminal;

    private boolean checkBoxInErrDialog = false;
    private int cursorOffset = -1;
    private Database database;
    private UIErrorLocator uiErrorLocator = new UIErrorLocator();
    private IErrorLocator errorlocator;

    private QueryInfoManager queryInfoManager = new QueryInfoManager();

    /**
     * Instantiates a new SQL terminal result display UI manager.
     *
     * @param terminal the terminal
     */
    public SQLTerminalResultDisplayUIManager(SQLTerminal terminal) {
        super(terminal.getTermConnection());
        this.terminal = terminal;
        database = terminal.getDatabase();
        errorlocator = database.getErrorLocator();

    }

    /**
     * Handle successfull completion.
     */
    @Override
    public void handleSuccessfullCompletion() {
        terminal.setCancelQueryPressed(false);
    }

    /**
     * Handle step completion.
     */
    @Override
    public void handleStepCompletion() {
        queryInfoManager.handleStepCompletion();

    }

    /**
     * Handle final cleanup.
     */
    @Override
    public void handleFinalCleanup() {
        if (this.getIsContextInCancelState()) {
            handleUserCancelUIChanges();
        }
        super.handleFinalCleanup();
        setExecuteCancelEnable(terminal, true, false);
    }

    private void handleUserCancelUIChanges() {
        ConsoleMessageWindow consoleWindow = getConsoleMessageWindow(true);
        if (null != consoleWindow) {
            consoleWindow.logInfo(MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUREY_CANCEL_MSG));
            consoleWindow.logInfo(MessageConfigLoader.getProperty(IMessagesConstants.UI_CANCEL_QUERY));
        }

    }

    private void setExecuteCancelEnable(final SQLTerminal sqlTerminal, final boolean isExecuteEnable,
            final boolean isCancelEnable) {
        sqlTerminal.disableExecutionButton(isExecuteEnable);
    }

    /**
     * Gets the event broker.
     *
     * @return the event broker
     */
    @Override
    public IEventBroker getEventBroker() {
        return this.terminal.getEventBroker();
    }

    /**
     * Gets the part ID.
     *
     * @return the part ID
     */
    @Override
    protected String getPartID() {
        return terminal.getUiID();
    }

    /**
     * Gets the console message window.
     *
     * @param bringOnTop the bring on top
     * @return the console message window
     */
    @Override
    protected ConsoleMessageWindow getConsoleMessageWindow(boolean bringOnTop) {
        return terminal.getConsoleMessageWindow(bringOnTop);
    }

    /**
     * Creates the result new.
     *
     * @param resultsetDisplaydata the resultset displaydata
     * @param consoledata the consoledata
     * @param queryExecSummary the query exec summary
     */
    @Override
    protected void createResultNew(IDSGridDataProvider resultsetDisplaydata, IConsoleResult consoledata,
            IQueryExecutionSummary queryExecSummary) {
        try {
            terminal.createResultNew(resultsetDisplaydata, consoledata, queryExecSummary);
        } catch (Exception exception) {
            handleSpecificUIUpdatesForException(exception);
        }
    }

    /**
     * Creates the exec plan result.
     *
     * @param analysedPlanOutput the analysed plan output
     * @param consoleDisplayData the console display data
     * @param queryExecSummary the query exec summary
     * @param totalRuntime the total runtime
     */
    protected void createExecPlanResult(UIModelAnalysedPlanNode analysedPlanOutput, IConsoleResult consoleDisplayData,
            IQueryExecutionSummary queryExecSummary, double totalRuntime) {
        terminal.createExecPlanNew(analysedPlanOutput, consoleDisplayData, queryExecSummary, totalRuntime);
    }

    /**
     * Handle specific UI updates for exception.
     *
     * @param exception the exception
     */
    @Override
    protected void handleSpecificUIUpdatesForException(Exception exception) {

        queryInfoManager.handleSpecificUIUpdatesForException(exception);

    }

    /**
     * Handle exec plan result display.
     *
     * @param analysedPlanOutput the analysed plan output
     * @param consoleDisplayData the console display data
     * @param queryExecSummary the query exec summary
     * @param totalRuntime the total runtime
     */
    public void handleExecPlanResultDisplay(final UIModelAnalysedPlanNode analysedPlanOutput,
            final IConsoleResult consoleDisplayData, final IQueryExecutionSummary queryExecSummary,
            final double totalRuntime) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if (isDisposed()) {
                    return;
                }

                if (canDislayResult()) {
                    createExecPlanResult(analysedPlanOutput, consoleDisplayData, queryExecSummary, totalRuntime);
                } else {
                    if (!isUserNotified) {
                        handleResultDisplayFailureDialog();
                        isUserNotified = true;
                    }
                    return;
                }
            }
        });
    }

    private boolean errorPopUpOnCritical(MPPDBIDEException exception) {

        if (!terminal.isVisible()) {
            return false;
        }

        int popUpreturn = generateErrorPopup(exception);

        if (popUpreturn == IDialogConstants.OK_ID) {

            terminal.setTerminalExecutionSQLConnectionReconnectOnTerminal(true);
            return false;
        } else if (popUpreturn == IDialogConstants.CANCEL_ID) {
            return true;

        } else {
            terminal.setTerminalExecutionSQLConnectionReconnectOnTerminal(false);
            onCriticalErrorDisconnectDB();
            return false;
        }
    }

    private void onCriticalErrorDisconnectDB() {
        Database db = terminal.getSelectedDatabase();
        DBConnProfCache.getInstance().destroyConnection(db);

        if (null != db) {
            ObjectBrowser obModel = UIElement.getInstance().getObjectBrowserModel();
            if (obModel != null) {
                obModel.refreshObject(db);
            }
            UIElement.getInstance().updateTextEditorsIconAndConnButtons(db.getServer());
            ConsoleCoreWindow.getInstance()
                    .logFatal(MessageConfigLoader.getProperty(IMessagesConstants.DISCONNECTED_FROM_SERVER,
                            db.getServer().getServerConnectionInfo().getConectionName(), db.getName()));
        }

    }

    /**
     * Generate error popup.
     *
     * @param exception the e
     * @return the int
     */
    public int generateErrorPopup(MPPDBIDEException exception) {
        String termName = terminal.getPartLabel();
        final String forcefulExit = "     "
                + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_RECONNECT) + "     ";
        final String gracefulExit = "     "
                + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_RECONNECT_CONTINUE) + "     ";

        final String cancel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        int btnPressed = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_DEBUG_CONNECTION_ERROR) + " : " + termName,
                MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ATTEMPT_ON_CRITICAL_ERROR), forcefulExit,
                gracefulExit, cancel);

        return btnPressed;

    }

    /**
     * Handle specific UI updates for presetup.
     */
    @Override
    protected void handleSpecificUIUpdatesForPresetup() {
        setExecuteCancelEnable(terminal, false, true);
    }

    /**
     * Can dislay result.
     *
     * @return true, if successful
     */
    @Override
    protected boolean canDislayResult() {
        if (UIElement.getInstance().isMoreResultWindowAllowed()) {
            return true;
        }

        // need to set that the backend execution cannot continue as max number
        // of resource limit is reached.
        setcanContextExecutionContinue(CanContextContinueExecuteRule.CONTEXT_EXECUTION_STOP);
        return false;
    }

    /**
     * Gets the single query array.
     *
     * @param queryArray the query array
     * @param query the query
     * @return the single query array
     */
    @Override
    public void getSingleQueryArray(ArrayList<String> queryArray, String query) {
        ArrayList<QueryInfo> queryInfoArray = new ArrayList<QueryInfo>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        String text = terminal.getDocumentText();
        int cursorLineOffset = -1;
        boolean firstLineFlag = false;
        try {
            cursorLineOffset = terminal.getsourceViewerLineOfOffset(cursorOffset);
        } catch (BadLocationException exception) {
            MPPDBIDELoggerUtility.error("getting single query array failed.", exception);
            if (MPPDBIDELoggerUtility.isDebugEnabled()) {
                MPPDBIDELoggerUtility
                        .debug(terminal.getConnectionName() + ":invalid offset in sourceviewer " + cursorOffset);
            }
        }
        int aryStartOffset = 0;
        if (cursorOffset < 0) {
            return;
        }
        QueryInfo singleQueryInfo = null;
        for (String str : queryArray) {
            singleQueryInfo = new QueryInfo();
            int startOffset = text.indexOf(str, aryStartOffset);
            int endOffset = startOffset + str.length();
            try {
                if (isFirstLineWithLineSeperator(firstLineFlag, str)) {
                    startOffset += 2;
                } else if (isFirslineWithOnlyNewline(firstLineFlag, str)) {
                    startOffset += 1;
                }
                int startLineNo = terminal.getsourceViewerLineOfOffset(startOffset);
                int endLineNo = terminal.getsourceViewerLineOfOffset(endOffset);
                singleQueryInfo.setStartOffset(startOffset);
                singleQueryInfo.setEndOffset(endOffset);
                singleQueryInfo.setStartLine(startLineNo);
                singleQueryInfo.setEndLine(endLineNo);
                singleQueryInfo.setQuery(str);
                queryInfoArray.add(singleQueryInfo);
                firstLineFlag = true;
            } catch (BadLocationException exception) {
                MPPDBIDELoggerUtility.error("SQLTerminalResultDisplayUIManager: BadLocationException occurred.",
                        exception);
            }
            aryStartOffset = endOffset + 1;

        }
        addDatatoQueryArray(queryArray, queryInfoArray, cursorLineOffset);
    }

    private boolean isFirslineWithOnlyNewline(boolean firstLineFlag, String str) {
        return str.startsWith("\n") && firstLineFlag;
    }

    private boolean isFirstLineWithLineSeperator(boolean firstLineFlag, String str) {
        return str.startsWith(MPPDBIDEConstants.LINE_SEPARATOR) && firstLineFlag;
    }

    private void addDatatoQueryArray(ArrayList<String> queryArray, ArrayList<QueryInfo> queryInfoArray,
            int cursorLineOffset) {
        for (QueryInfo cursorQueryInfo : queryInfoArray) {
            if (cursorLineOffset >= cursorQueryInfo.getStartLine()
                    && cursorLineOffset <= cursorQueryInfo.getEndLine()) {
                queryArray.clear();
                queryArray.add(cursorQueryInfo.getQuery());
                break;

            }

        }
    }

    /**
     * Reset display UI manager.
     */
    @Override
    public void resetDisplayUIManager() {
        super.resetControlVariables();
    }

    /**
     * Inits the display manager.
     *
     * @param execType the exec type
     */
    @Override
    public void initDisplayManager(ContextExecutionOperationType execType) {
        super.initDisplayManager(execType);
        checkBoxInErrDialog = false;
        queryInfoManager.initDisplayManager();

    }

    /**
     * Sets the disposed.
     */
    @Override
    public void setDisposed() {
        super.setDisposed();
        super.setcanContextExecutionContinue(CanContextContinueExecuteRule.CONTEXT_EXECUTION_STOP);
    }

    /**
     * Sets the cursor offset.
     *
     * @param offset the new cursor offset
     */
    @Override
    public void setCursorOffset(int offset) {
        cursorOffset = offset;

    }

    /**
     * Handle grid component on dialog cancel.
     */
    @Override
    public void handleGridComponentOnDialogCancel() {

    }

    /**
     * Gets the query info.
     *
     * @return the query info
     */
    protected int getqueryInfo() {
        return queryInfoManager.getqueryInfo();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class QueryInfoManager.
     */
    private class QueryInfoManager {
        private QueryInfo queryInfo = new QueryInfo();

        /**
         * Inits the display manager.
         */
        public void initDisplayManager() {
            queryInfo = new QueryInfo();
        }

        /**
         * Handle step completion.
         */
        public void handleStepCompletion() {
            String query = terminal.getCurrentQueryInExecution();
            int endOffset = getSearchOffset();
            queryInfo.setEndOffset(endOffset + query.length());

        }

        /**
         * Handle specific UI updates for exception.
         *
         * @param exception the e
         */
        protected void handleSpecificUIUpdatesForException(Exception exception) {
            if (exception instanceof DatabaseOperationException) {
                checkAndUpdateFailureQueryInfo(exception);
            } else if (exception instanceof DatabaseCriticalException) {
                updateFailureQueryInfoCritical((DatabaseCriticalException) exception);

            }

        }

        private void updateFailureQueryInfo(DatabaseOperationException databaseOperationException) {
            try {
                updateFailureQueryInfoInternal(databaseOperationException);
                terminal.showError(queryInfo);
            } catch (Exception exception) {
                MPPDBIDELoggerUtility.error("SQLTerminalResultDisplayUIManager: updating failure query info failed.",
                        exception);
            }

            boolean focused = terminal.isVisible();

            if (!checkBoxInErrDialog && focused) {
                showPopup(terminal.getShell(), databaseOperationException, queryInfo.getErrLineNo());
            }
        }

        private void showPopup(final Shell shell, final DatabaseOperationException databaseOperationException,
                final int lineNumber) {
            SQLTerminalResultDisplayUIManager.this
                    .setcanContextExecutionContinue(CanContextContinueExecuteRule.CONTEXT_EXECUTION_UNKNOWN);
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    boolean popupResponse = false;

                    int lineNum = databaseOperationException.getServerMessage() != null
                            ? (lineNumber != -1 ? lineNumber : 0)
                            : 0;

                    String queryWithComments = IServerMessageParseUtils
                            .extractQueryFromErrorMessage(databaseOperationException.getServerMessage());
                    boolean isViewSource = isSourceViewable(
                            IServerMessageParseUtils.extractQueryWithoutComments(queryWithComments));
                    popupResponse = uiErrorLocator.errorPopUpOnQryExecutionFailure(shell, databaseOperationException,
                            lineNum, isViewSource, terminal.getTermConnection());

                    checkBoxInErrDialog = uiErrorLocator.getCheckErrorDialog();
                    if (popupResponse) {
                        SQLTerminalResultDisplayUIManager.this.setcanContextExecutionContinue(
                                CanContextContinueExecuteRule.CONTEXT_EXECUTION_PROCEED);
                    } else {
                        SQLTerminalResultDisplayUIManager.this
                                .setcanContextExecutionContinue(CanContextContinueExecuteRule.CONTEXT_EXECUTION_STOP);
                    }

                }
            });
        }

        /**
         * Gets the query info.
         *
         * @return the query info
         */
        protected int getqueryInfo() {
            return queryInfo.getErrLineNo() != 0 ? queryInfo.getErrLineNo() : -1;
        }

        private void updateFailureQueryInfoInternal(MPPDBIDEException mppdbideException) throws BadLocationException {

            String query = terminal.getCurrentQueryInExecution();
            String text = terminal.getDocumentText();
            String selectedTextInTerminal = terminal.getCurrentExecutionSelectedText();

            int qryStartOffset = 0;
            int qryEndOffset = 0;
            int errorLineNo = -1;
            int errorPosition = 0;

            int selectedDocumentTextLength = selectedTextInTerminal.trim().length();
            int getDocumentTextLength = text.trim().length();

            query = getQuery(query, text, getDocumentTextLength);

            // handling the case of first query when selection of query is done.
            int searchOffset = getSearchOffset();

            if (selectedTextInTerminal.isEmpty() || (selectedDocumentTextLength == getDocumentTextLength)) {
                qryStartOffset = errorlocator.textStartOffset(query, text, searchOffset);
                qryEndOffset = errorlocator.textEndOffset(query, text, searchOffset);
            } else {
                qryStartOffset = errorlocator.textStartOffset(query, text, searchOffset);
                qryEndOffset = errorlocator.textEndOffset(query, text, searchOffset);
                terminal.setselectionStartOffset(qryEndOffset);
            }

            int startLineNo = terminal.getsourceViewerLineOfOffset(qryStartOffset);
            int endLineNo = terminal.getsourceViewerLineOfOffset(qryEndOffset);

            queryInfo.setStartOffset(qryStartOffset);
            queryInfo.setEndOffset(qryEndOffset);
            queryInfo.setStartLine(startLineNo + 1);
            queryInfo.setEndLine(endLineNo + 1);

            errorPosition = errorlocator.errorPosition(query, qryStartOffset, qryEndOffset, startLineNo, endLineNo,
                    mppdbideException);
            queryInfo.setErrorPosition(errorPosition);

            setErrorLineNo(errorLineNo);

            String errorMsg = errorlocator.errorMessage(text, queryInfo.getErrorPosition(), qryStartOffset,
                    qryEndOffset);
            queryInfo.setErrorMsgString(errorMsg);

            String serverError = errorlocator.serverErrorMessage(mppdbideException);
            queryInfo.setServerMessageString(serverError);

        }

        private String getQuery(String lquery, String text, int getDocumentTextLength) {
            String query = lquery;
            if (query.length() > getDocumentTextLength) {
                query = text.trim();
            }
            return query;
        }

        private int getSearchOffset() {
            int searchOffset = queryInfo.getEndOffset();
            if (searchOffset == 0) {
                searchOffset = terminal.getselectionStartOffset();
            }
            return searchOffset;
        }

        private void setErrorLineNo(int errorLineNo) throws BadLocationException {
            int errorLineNum = errorLineNo;
            int errorLineNumber = errorlocator.errorLineNumber(queryInfo.getStartOffset(),
                    queryInfo.getErrorPosition());
            if (errorLineNumber != -1) {
                errorLineNum = terminal.getsourceViewerLineOfOffset(errorLineNumber);
            }
            queryInfo.setErrLineNo(errorLineNum + 1);
        }

        private void checkAndUpdateFailureQueryInfo(Exception exception) {
            String serverMessage = ((DatabaseOperationException) exception).getServerMessage();
            if (serverMessage != null && (isPreExecutionUIDisplaySetupOk()
                    && !serverMessage.contains(MessageConfigLoader.getProperty(IMessagesConstants.UI_CANCEL_QUERY))
                    && !serverMessage.contains("canceling statement due to user request"))) {
                updateFailureQueryInfo((DatabaseOperationException) exception);
            }
        }

        private void updateFailureQueryInfoCritical(DatabaseCriticalException databaseCriticalException) {
            if (database != null && database.isConnected()) {
                showCriticalPopup(databaseCriticalException);
            }

        }

        private void showCriticalPopup(final DatabaseCriticalException databaseCriticalException) {
            SQLTerminalResultDisplayUIManager.this
                    .setcanContextExecutionContinue(CanContextContinueExecuteRule.CONTEXT_EXECUTION_UNKNOWN);
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    boolean popupResponse = false;

                    popupResponse = errorPopUpOnCritical(databaseCriticalException);

                    if (popupResponse) {
                        SQLTerminalResultDisplayUIManager.this.setcanContextExecutionContinue(
                                CanContextContinueExecuteRule.CONTEXT_EXECUTION_PROCEED);
                    } else {
                        SQLTerminalResultDisplayUIManager.this
                                .setcanContextExecutionContinue(CanContextContinueExecuteRule.CONTEXT_EXECUTION_STOP);
                    }

                }
            });
        }

    }

}
