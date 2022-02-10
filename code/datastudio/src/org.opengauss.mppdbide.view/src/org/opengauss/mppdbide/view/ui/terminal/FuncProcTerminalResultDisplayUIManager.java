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

package org.opengauss.mppdbide.view.ui.terminal;

import java.util.ArrayList;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.errorlocator.IErrorLocator;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.presentation.CanContextContinueExecuteRule;
import org.opengauss.mppdbide.presentation.ContextExecutionOperationType;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.presentation.autorefresh.RefreshObjectDetails;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.resultsetif.IConsoleResult;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.stringparse.IServerMessageParseUtils;
import org.opengauss.mppdbide.view.autorefresh.RefreshObjects;
import org.opengauss.mppdbide.view.core.ConsoleCoreWindow;
import org.opengauss.mppdbide.view.core.ConsoleMessageWindow;
import org.opengauss.mppdbide.view.terminal.executioncontext.FuncProcEditorTerminalExecutionContext;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.ui.QueryInfo;
import org.opengauss.mppdbide.view.ui.UIErrorLocator;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * Title: class Description: The Class FuncProcTerminalResultDisplayUIManager.
 *
 * @since 3.0.0
 */
public class FuncProcTerminalResultDisplayUIManager extends AbstractResultDisplayUIManager {
    private PLSourceEditor plSourceEditor;

    private boolean checkBoxInErrDialog = false;

    private UIErrorLocator uiErrorlocator = new UIErrorLocator();

    private IErrorLocator errorLocator;

    private FuncQueryInfoManager funcQueryInfoManager = new FuncQueryInfoManager();

    /**
     * Instantiates a new func proc terminal result display UI manager.
     *
     * @param plSourceEditor the pl source editor
     */
    public FuncProcTerminalResultDisplayUIManager(PLSourceEditor plSourceEditor) {
        super(plSourceEditor.getTermConnection());
        this.plSourceEditor = plSourceEditor;
        errorLocator = plSourceEditor.getDatabaseErrorLocator();
    }

    /**
     * Gets the event broker.
     *
     * @return the event broker
     */
    @Override
    public IEventBroker getEventBroker() {
        // passing null deliberately as no need to send events to history.
        return null;
    }

    /**
     * Gets the part ID.
     *
     * @return the part ID
     */
    @Override
    protected String getPartID() {
        return plSourceEditor.getUiID();
    }

    /**
     * Gets the console message window.
     *
     * @param bringOnTop the bring on top
     * @return the console message window
     */
    @Override
    protected ConsoleMessageWindow getConsoleMessageWindow(boolean bringOnTop) {
        return plSourceEditor.getConsoleMessageWindow(bringOnTop);
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

        this.plSourceEditor.createResultNew(resultsetDisplaydata, consoledata, queryExecSummary);

    }

    /**
     * Handle console only result display.
     *
     * @param consoledata the consoledata
     */
    @Override
    protected void handleConsoleOnlyResultDisplay(IConsoleResult consoledata) {
        ContextExecutionOperationType opType = getContextExecutionOperationType();
        if (ContextExecutionOperationType.CONTEXT_OPERATION_TYPE_NEW_PL_SQL_CREATION == opType) {
            handleNewFunctionCreation();
        } else if (ContextExecutionOperationType.CONTEXT_OPERATION_TYPE_PL_SQL_COMPILATION == opType) {
            PLSourceEditor editor = UIElement.getInstance().getVisibleSourceViewer();
            if (null != editor) {
                editor.setSourceChangedInEditor(false);
                editor.setModified(false);
                editor.setModifiedAfterCreate(false);
            }

            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    plSourceEditor.setCriticalErr(true);
                    plSourceEditor.refreshDebugObjectAfterEdit(plSourceEditor.getDebugObject());
                    plSourceEditor.getConsoleMessageWindow(true)
                            .logInfo(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_STATUS));
                    plSourceEditor.setCompileInProgress(false);
                    plSourceEditor.setExecuteInProgress(false);
                    plSourceEditor.getSourceEditorCore().getSourceViewer().getTextWidget().setEnabled(true);
                }
            });

        }
    }

    /**
     * Handle specific UI updates for exception.
     *
     * @param exception the exception
     */
    @Override
    protected void handleSpecificUIUpdatesForException(Exception exception) {

        funcQueryInfoManager.handleSpecificUIUpdatesForException(exception);

    }

    /**
     * Error pop up on critical.
     *
     * @param exception the exception
     * @return true, if successful
     */
    public boolean errorPopUpOnCritical(MPPDBIDEException exception) {
        int popUpreturn = generateErrorPopup(exception);

        if (popUpreturn == IDialogConstants.OK_ID) {
            plSourceEditor.setTermConnectionReconnectOnTerminal(true);
            return false;
        } else if (popUpreturn == IDialogConstants.CANCEL_ID) {
            return true;

        } else {
            plSourceEditor.setTermConnectionReconnectOnTerminal(false);
            onCriticalErrorDisconnectDB();
            return false;
        }
    }

    private void onCriticalErrorDisconnectDB() {
        Database db = plSourceEditor.getDatabase();
        DBConnProfCache.getInstance().destroyConnection(db);

        if (null != db) {
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObject(db);
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
     * @param exception the exception
     * @return the int
     */
    public int generateErrorPopup(MPPDBIDEException exception) {
        final String reconnectBtn = "     "
                + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_RECONNECT) + "     ";
        final String reconnectAndContBtn = "     "
                + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_RECONNECT_CONTINUE) + "     ";

        final String cancel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        int btnPressed = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_DEBUG_CONNECTION_ERROR),
                MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ATTEMPT_ON_CRITICAL_ERROR), reconnectBtn,
                reconnectAndContBtn, cancel);

        return btnPressed;

    }

    private void handleNewFunctionCreation() {
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (objectBrowserModel != null) {
            FunctionProcNameParser parser = getFunProcParser();
            TerminalExecutionConnectionInfra dbConnection = plSourceEditor.getTermConnection();
            RefreshObjectDetails refObj = FunctionUtils.refreshFuncWithoutSchema(parser, objectBrowserModel,
                    plSourceEditor, dbConnection);
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    plSourceEditor.hideProgressBar();
                    openNewFunction(parser, refObj, objectBrowserModel, dbConnection);
                    plSourceEditor.enabledisableTextWidget(true);
                }
            });
        }
    }

    private void openNewFunction(FunctionProcNameParser parser, RefreshObjectDetails refObj,
            ObjectBrowser objectBrowserModel, TerminalExecutionConnectionInfra dbConnection) {

        IDebugObject debugObj = null;
        String grp = null;
        String schemaName = null;
        String functionName = null;

        RefreshObjects.refreshObjectsInTreeViewer(refObj, objectBrowserModel.getTreeViewer());
        if (null != parser.getSchemaName() && null != parser.getObjectName() && null != parser.getFuncName()) {
            schemaName = parser.getSchemaName();
            functionName = parser.getObjectName();
            if (!parser.getFuncName().equals("")) {
                grp = parser.getFuncName().substring(1);
            }
        }
        try {
            debugObj = isObjectChanged(grp, schemaName, functionName, dbConnection, parser);
        } catch (DatabaseCriticalException exception) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR),
                    MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_DATABASE_CRITICAL_ERROR)
                            + MPPDBIDEConstants.LINE_SEPARATOR + exception.getLocalizedMessage());
            plSourceEditor.setTermConnectionReconnectOnTerminal(false);
            onCriticalErrorDisconnectDB();
        }

        if (debugObj != null) {
            plSourceEditor.getExecutionContext().setDebugObject(debugObj);
        } else {
            plSourceEditor.getExecutionContext().setDebugObject(plSourceEditor.getDebugObject());
        }
        if (debugObj != null && plSourceEditor.getDebugObject() != null
                && debugObj.getOid() != plSourceEditor.getDebugObject().getOid()) {
            UIElement.getInstance().closeSourceViewerById(plSourceEditor.getDebugObject().getPLSourceEditorElmId());
        }
    }

    private FunctionProcNameParser getFunProcParser() {
        String code2 = plSourceEditor.getFunctionDocumentContent();

        FunctionProcNameParser parser = new FunctionProcNameParser(code2);
        parser.doParse();
        return parser;
    }

    private IDebugObject isObjectChanged(String grp, String schemaName, String functionName,
            TerminalExecutionConnectionInfra dbConnection, FunctionProcNameParser parser)
            throws DatabaseCriticalException {
        IDebugObject dbgObj = plSourceEditor.getDebugObject();
        IDebugObject newdbgObj = null;
        if (dbgObj instanceof DebugObjects && grp != null) {
            newdbgObj = FunctionUtils.openDebugObjects(grp, schemaName, dbConnection, plSourceEditor, parser);
        }
        if (dbgObj instanceof DebugObjects && grp == null && functionName != null
                && null != dbConnection.getConnection()) {
            newdbgObj = FunctionUtils.openDebugObjectsWithoutSchema(functionName, dbConnection, plSourceEditor, parser);
        }
        return newdbgObj;
    }

    /**
     * Can dislay result.
     *
     * @return true, if successful
     */
    @Override
    protected boolean canDislayResult() {
        return UIElement.getInstance().isMoreResultWindowAllowed();
    }

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    @Override
    public TerminalExecutionConnectionInfra getTermConnection() {
        return this.plSourceEditor.getTermConnection();
    }

    /**
     * Reset display UI manager.
     */
    @Override
    public void resetDisplayUIManager() {

    }

    /**
     * Gets the query info.
     *
     * @return the query info
     */
    @Override
    protected int getqueryInfo() {

        return funcQueryInfoManager.getqueryInfo();
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
        funcQueryInfoManager.initQueryInfoManager();
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

    }

    /**
     * Sets the cursor offset.
     *
     * @param offset the new cursor offset
     */
    @Override
    public void setCursorOffset(int offset) {

    }

    /**
     * Handle pre execution UI display setup critical.
     *
     * @param termConn the term conn
     * @param isCriticalErr the is critical err
     */
    @Override
    public void handlePreExecutionUIDisplaySetupCritical(TerminalExecutionConnectionInfra termConn,
            boolean isCriticalErr) {
    }

    /**
     * Handle grid component on dialog cancel.
     */
    @Override
    public void handleGridComponentOnDialogCancel() {

    }

    /**
     * Title: class Description: The Class FuncQueryInfoManager.
     */
    private class FuncQueryInfoManager {
        private QueryInfo queryInfo = new QueryInfo();

        /**
         * Inits the query info manager.
         */
        public void initQueryInfoManager() {
            queryInfo = new QueryInfo();
        }

        /**
         * Handle specific UI updates for exception.
         *
         * @param exception the e
         */
        public void handleSpecificUIUpdatesForException(Exception exception) {
            plSourceEditor.setCompileInProgress(false);
            plSourceEditor.setExecuteInProgress(false);
            plSourceEditor.getSourceEditorCore().getSourceViewer().getTextWidget().setEnabled(true);
            if (exception instanceof DatabaseOperationException) {
                String serverMessage = ((DatabaseOperationException) exception).getServerMessage();
                if (serverMessage != null && (isPreExecutionUIDisplaySetupOk()
                        && !serverMessage.contains(MessageConfigLoader.getProperty(IMessagesConstants.UI_CANCEL_QUERY))
                        && !serverMessage.contains("canceling statement due to user request"))) {
                    updateFunctionFailureQueryInfo((DatabaseOperationException) exception);
                }
            } else if (exception instanceof DatabaseCriticalException) {
                updateFailureQueryInfoCritical((DatabaseCriticalException) exception);

            }

        }

        private void updateFunctionFailureQueryInfo(DatabaseOperationException databaseOperationException) {
            try {
                updateFunctionFailureQueryInfoInternal(databaseOperationException);
                plSourceEditor.showFunctionError(queryInfo);
            } catch (Exception exception) {
                MPPDBIDELoggerUtility.error("Update function failure query info failed.", exception);
            }

            boolean focused = plSourceEditor.isFunctionVisible();

            if (!checkBoxInErrDialog && focused) {
                showFunctionPopup(plSourceEditor.getExecutionContext().getShell(), databaseOperationException,
                        queryInfo.getErrLineNo());
            }
        }

        private void updateFunctionFailureQueryInfoInternal(MPPDBIDEException exception) throws BadLocationException {
            FuncProcEditorTerminalExecutionContext executionContext = plSourceEditor.getExecutionContext();
            String query = executionContext.getCurrentQueryInExecution();
            String text = plSourceEditor.getFunctionDocumentText();
            String selectedTextInTerminal = executionContext.getCurrentFunctExecutionSelectedText();

            int qryStartOffset = 0;
            int qryEndOffset = 0;

            int selectedDocumentTextLength = selectedTextInTerminal.trim().length();
            int getDocumentTextLength = text.trim().length();

            if (query.length() > getDocumentTextLength) {
                query = text.trim();
            }

            int searchOffset = queryInfo.getEndOffset();
            if (searchOffset == 0) {
                searchOffset = executionContext.getFunctselectionStartOffset();
            }

            if (errorLocator != null) {
                if (selectedTextInTerminal.isEmpty() || (selectedDocumentTextLength == getDocumentTextLength)) {
                    qryStartOffset = errorLocator.textStartOffset(query, text, searchOffset);
                    qryEndOffset = errorLocator.textEndOffset(query, text, searchOffset);
                } else {
                    qryStartOffset = errorLocator.textStartOffset(query, text, searchOffset);
                    qryEndOffset = errorLocator.textEndOffset(query, text, searchOffset);
                    executionContext.setFunctselectionStartOffset(qryEndOffset);
                }
            }

            int startLineNo = plSourceEditor.getFunctsourceViewerLineOfOffset(qryStartOffset);
            int endLineNo = plSourceEditor.getFunctsourceViewerLineOfOffset(qryEndOffset);

            queryInfo.setStartOffset(qryStartOffset);
            queryInfo.setEndOffset(qryEndOffset);
            queryInfo.setStartLine(startLineNo + 1);
            queryInfo.setEndLine(endLineNo + 1);

            if (errorLocator != null) {
                int errorPosition = errorLocator.errorPosition(query, qryStartOffset, qryEndOffset, startLineNo,
                        endLineNo, exception);
                queryInfo.setErrorPosition(errorPosition);

                int errorLineNo = plSourceEditor.getFunctsourceViewerLineOfOffset(
                        errorLocator.errorLineNumber(queryInfo.getStartOffset(), queryInfo.getErrorPosition()));
                queryInfo.setErrLineNo(errorLineNo + 1);

                String errorMsg = errorLocator.errorMessage(text, queryInfo.getErrorPosition(), qryStartOffset,
                        qryEndOffset);
                queryInfo.setErrorMsgString(errorMsg);

                String serverError = errorLocator.serverErrorMessage(exception);
                queryInfo.setServerMessageString(serverError);
            }

        }

        private void showFunctionPopup(final Shell shell, final DatabaseOperationException exception,
                final int lineNumber) {
            FuncProcTerminalResultDisplayUIManager.this
                    .setcanContextExecutionContinue(CanContextContinueExecuteRule.CONTEXT_EXECUTION_UNKNOWN);
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    boolean popupResponse = false;

                    String serverMessage = exception.getServerMessage();
                    if (null != serverMessage) {

                        int lineNum = lineNumber != -1 ? lineNumber : 0;
                        String queryWithComments = IServerMessageParseUtils
                                .extractQueryFromErrorMessage(exception.getServerMessage());
                        boolean isViewSource = isSourceViewable(
                                IServerMessageParseUtils.extractQueryWithoutComments(queryWithComments));
                        popupResponse = uiErrorlocator.errorPopUpOnQryExecutionFailure(shell, exception, lineNum,
                                isViewSource, plSourceEditor.getTermConnection());

                        checkBoxInErrDialog = uiErrorlocator.getCheckErrorDialog();
                        if (popupResponse) {
                            FuncProcTerminalResultDisplayUIManager.this.setcanContextExecutionContinue(
                                    CanContextContinueExecuteRule.CONTEXT_EXECUTION_PROCEED);
                        } else {
                            FuncProcTerminalResultDisplayUIManager.this.setcanContextExecutionContinue(
                                    CanContextContinueExecuteRule.CONTEXT_EXECUTION_STOP);
                        }
                    }

                }
            });
        }

        private void updateFailureQueryInfoCritical(DatabaseCriticalException exception) {

            showCriticalPopup(exception);

        }

        private void showCriticalPopup(final DatabaseCriticalException exception) {
            FuncProcTerminalResultDisplayUIManager.this
                    .setcanContextExecutionContinue(CanContextContinueExecuteRule.CONTEXT_EXECUTION_UNKNOWN);
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    boolean popupResponse = false;
                    popupResponse = errorPopUpOnCritical(exception);
                    if (popupResponse) {
                        FuncProcTerminalResultDisplayUIManager.this.setcanContextExecutionContinue(
                                CanContextContinueExecuteRule.CONTEXT_EXECUTION_PROCEED);
                    } else {
                        FuncProcTerminalResultDisplayUIManager.this
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
        public int getqueryInfo() {

            return queryInfo.getErrLineNo();
        }

    }

}
