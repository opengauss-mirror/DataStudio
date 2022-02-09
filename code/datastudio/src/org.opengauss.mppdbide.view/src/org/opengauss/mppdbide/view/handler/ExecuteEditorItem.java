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

package org.opengauss.mppdbide.view.handler;

import java.util.ArrayList;

import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.keywordssyntax.SQLSyntax;
import org.opengauss.mppdbide.bl.errorlocator.IErrorLocator;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseUtils;
import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectChange;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.presentation.CanContextContinueExecuteRule;
import org.opengauss.mppdbide.presentation.IExecutionContext;
import org.opengauss.mppdbide.presentation.IResultDisplayUIManager;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.ConsoleCoreWindow;
import org.opengauss.mppdbide.view.core.ExcuteSQLObjectTable;
import org.opengauss.mppdbide.view.core.sourceeditor.ExecuteSourceEditor;
import org.opengauss.mppdbide.view.functionchange.ExecuteWrapper;
import org.opengauss.mppdbide.view.functionchange.ObjectChangeEvent;
import org.opengauss.mppdbide.view.functionchange.ObjectChangeEvent.ButtonPressed;
import org.opengauss.mppdbide.view.functionchange.ObjectChangeWorker;
import org.opengauss.mppdbide.view.terminal.TerminalQueryExecutionWorker;
import org.opengauss.mppdbide.view.terminal.executioncontext.FuncProcEditorTerminalExecutionContext;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.ui.QueryInfo;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayUtil;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * Title: class Description: The Class ExecuteEditorItem.
 *
 * @since 3.0.0
 */
public class ExecuteEditorItem implements ExecuteWrapper {

    /**
     * Executor
     * 
     * @param application
     * @param service
     */
    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;

    private QueryInfo queryInfo = new QueryInfo();

    private static final String ERROR_POSITION_IDENTIFIER = "Position:";

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        MPPDBIDELoggerUtility
                .info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_EXECUTEEDITORITEM_EXECUTE_CLICKED));
        PLSourceEditor editor = UIElement.getInstance().getVisibleSourceViewer();
        if (null == editor) {
            Command command = commandService.getCommand("org.opengauss.mppdbide.command.id.executeobjectbrowseritem");
            ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, null);
            handlerService.executeHandler(parameterizedCommand);
            return;
        }
        editor.setExecuteInProgress(true);
        editor.enabledisableTextWidget(false);
        IDebugObject debugObject = editor.getDebugObject();
        ObjectChangeWorker<ObjectChange> objWorker = new ObjectChangeWorker<ObjectChange>("Function Change Worker",
                null, debugObject, editor, this, IMessagesConstants.FUNCTN_CHANGE_MSG,
                IMessagesConstants.FUNCTN_CHANGE_OVERWRITE);
        objWorker.schedule();
    }

    /**
     * Handle execute.
     *
     * @param event the event
     */
    @Override
    public void handleExecute(ObjectChangeEvent event) {
        PLSourceEditor editor = event.getEditor();
        IDebugObject debugObject = event.getDbgObj();
        Database db = debugObject.getDatabase();
        boolean iscontinue = true;

        if (validateForRefreshStatus(event)) {
            iscontinue = refreshDebugObjectOnTerminal(editor, debugObject, db);
        } else {
            boolean codeChanged = editor.isCodeChanged();
            int returnVal = displayIfCodeChanged(codeChanged);

            boolean debugObjChanged = true;
            debugObjChanged = getDebugObjectChanged(debugObject, debugObjChanged);

            iscontinue = executeIfCodeNotChanged(editor, debugObject, db, codeChanged, returnVal, debugObjChanged);

            iscontinue = executeIfCodeIsChanged(editor, iscontinue, codeChanged, returnVal, debugObjChanged);
        }
        if (iscontinue) {
            editor.enabledisableTextWidget(true);
            editor.setCompileInProgress(false);
            editor.setExecuteInProgress(false);
            executeSQLObjWindow(debugObject, false);
        }
    }

    /**
     * Execute if code is changed.
     *
     * @param editor the editor
     * @param iscontinue the iscontinue
     * @param codeChanged the code changed
     * @param returnVal the return val
     * @param debugObjChanged the debug obj changed
     * @return true, if successful
     */
    private boolean executeIfCodeIsChanged(PLSourceEditor editor, boolean iscontinue, boolean codeChanged,
            int returnVal, boolean debugObjChanged) {
        iscontinue = refreshOnDebugObjChange(editor, iscontinue, codeChanged, returnVal, debugObjChanged);
        if (iscontinue && codeChanged && (returnVal != 0)) {
            editor.enabledisableTextWidget(true);
            editor.setCompileInProgress(false);
            editor.setExecuteInProgress(false);
            return false;
        }
        return iscontinue;
    }

    /**
     * Refresh on debug obj change.
     *
     * @param editor the editor
     * @param iscontinue the iscontinue
     * @param codeChanged the code changed
     * @param returnVal the return val
     * @param debugObjChanged the debug obj changed
     * @return true, if successful
     */
    private boolean refreshOnDebugObjChange(PLSourceEditor editor, boolean iscontinue, boolean codeChanged,
            int returnVal, boolean debugObjChanged) {
        if (iscontinue && codeChanged && (returnVal == 0)) {
            refreshDebugObjectOnChanged(editor, debugObjChanged);
            editor.setCompileInProgress(false);
            editor.setExecuteInProgress(false);
            editor.enabledisableTextWidget(true);
            editor.getConsoleMessageWindow(true)
                    .logInfo(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_STATUS));
            iscontinue = executeIfDebugObjectIsNotChanged(editor, debugObjChanged);
        }
        return iscontinue;
    }

    /**
     * Execute if debug object is not changed.
     *
     * @param editor the editor
     * @param debugObjChanged the debug obj changed
     * @return true, if successful
     */
    private boolean executeIfDebugObjectIsNotChanged(PLSourceEditor editor, boolean debugObjChanged) {
        if (!debugObjChanged) {

            // source code changed, but the debug object didnt change as new
            // debug object compiled open the new function in a new tab

            IExecutionContext context = generateContext(Display.getDefault().getActiveShell(), editor);
            TerminalQueryExecutionWorker worker = new TerminalQueryExecutionWorker(context) {

                private boolean isExecuted = false;

                /**
                 * Final cleanup.
                 *
                 * @throws MPPDBIDEException the MPPDBIDE exception
                 */
                public void finalCleanup() throws MPPDBIDEException {
                    super.finalCleanup();
                    if (this.context instanceof FuncProcEditorTerminalExecutionContext) {
                        FuncProcEditorTerminalExecutionContext funcContext = (FuncProcEditorTerminalExecutionContext) this.context;
                        IResultDisplayUIManager canExecute = funcContext.getResultDisplayUIManager();
                        if (CanContextContinueExecuteRule.CONTEXT_EXECUTION_PROCEED == canExecute
                                .canContextExecutionContinue() && !isExecuted) {
                            Display.getDefault().syncExec(new Runnable() {
                                @Override
                                public void run() {
                                    PLSourceEditor editor1 = UIElement.getInstance()
                                            .getEditorModelById(funcContext.getDebugObject());
                                    if (editor1 != null) {
                                        editor1.enabledisableTextWidget(true);
                                        editor1.setCompileInProgress(false);
                                        editor1.setExecuteInProgress(false);
                                        executeSQLObjWindow(editor1.getDebugObject(), false);
                                    }
                                }
                            });

                            isExecuted = true;
                        }

                    }

                }
            };
            worker.setTaskDB(context.getTermConnection().getDatabase());
            worker.schedule();
            return false;
        }
        return true;
    }

    /**
     * Refresh debug object on changed.
     *
     * @param editor the editor
     * @param debugObjChanged the debug obj changed
     */
    private void refreshDebugObjectOnChanged(PLSourceEditor editor, boolean debugObjChanged) {
        if (debugObjChanged) {
            editor.refreshDebugObjectAfterEdit(editor.getDebugObject());
        }
    }

    /**
     * Execute if code not changed.
     *
     * @param editor the editor
     * @param debugObject the debug object
     * @param db the db
     * @param codeChanged the code changed
     * @param returnVal the return val
     * @param debugObjChanged the debug obj changed
     * @return true, if successful
     */
    private boolean executeIfCodeNotChanged(PLSourceEditor editor, IDebugObject debugObject, Database db,
            boolean codeChanged, int returnVal, boolean debugObjChanged) {
        if (validateIfCodeChanged(codeChanged, returnVal) && debugObjChanged) {
            return executeOnCodeChange(editor, debugObject, db);
        }
        return true;
    }

    /**
     * Execute on code change.
     *
     * @param editor the editor
     * @param debugObject the debug object
     * @param db the db
     * @return true, if successful
     */
    private boolean executeOnCodeChange(PLSourceEditor editor, IDebugObject debugObject, Database db) {
        try {
            executeOnTerminal(editor, debugObject, db);
            editor.enabledisableTextWidget(true);
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("error while execute debug obj.", exception);
            editor.enabledisableTextWidget(true);
            return false;
        }
        return true;
    }

    /**
     * Validate if code changed.
     *
     * @param codeChanged the code changed
     * @param returnVal the return val
     * @return true, if successful
     */
    private boolean validateIfCodeChanged(boolean codeChanged, int returnVal) {
        return (!codeChanged) || (returnVal == 0);
    }

    /**
     * Display if code changed.
     *
     * @param codeChanged the code changed
     * @return the int
     */
    private int displayIfCodeChanged(boolean codeChanged) {
        int returnVal = -1;
        if (codeChanged) {
            returnVal = MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.SOURCE_CODE_CHANGE),
                    MessageConfigLoader.getProperty(IMessagesConstants.SOURCE_HAS_BEEN_CHANGED));
        }
        return returnVal;
    }

    /**
     * Gets the debug object changed.
     *
     * @param debugObject the debug object
     * @param debugObjChanged the debug obj changed
     * @return the debug object changed
     */
    private boolean getDebugObjectChanged(IDebugObject debugObject, boolean debugObjChanged) {
        try {
            debugObjChanged = debugObject.isChanged(debugObject.getLatestInfo());
        } catch (DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("error while comparing debug obj info with server", exception);
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("error while comparing debug obj info with server", exception);
        }
        return debugObjChanged;
    }

    /**
     * Refresh debug object on terminal.
     *
     * @param editor the editor
     * @param debugObject the debug object
     * @param db the db
     * @return true, if successful
     */
    private boolean refreshDebugObjectOnTerminal(PLSourceEditor editor, IDebugObject debugObject, Database db) {
        editor.setDocument(new Document(debugObject.getSourceCode().getCode()));
        editor.registerModifyListener();
        try {
            executeOnTerminal(editor, debugObject, db);
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("error while execute debug obj.", exception);
            editor.enabledisableTextWidget(true);
            return false;
        }
        return true;
    }

    /**
     * Validate for refresh status.
     *
     * @param event the event
     * @return true, if successful
     */
    private boolean validateForRefreshStatus(ObjectChangeEvent event) {
        return event.getStatus() != null && event.getStatus().equals(ButtonPressed.REFRESH);
    }

    /**
     * Generate context.
     *
     * @param shell the shell
     * @param editor the editor
     * @return the i execution context
     */
    private IExecutionContext generateContext(Shell shell, PLSourceEditor editor) {
        ExecuteSourceEditor executeSourceEditor = null;
        executeSourceEditor = new ExecuteSourceEditor(shell, editor);

        return executeSourceEditor.getContextForNewEditor();

    }

    /**
     * Execute on terminal.
     *
     * @param editor the editor
     * @param debugObject the debug object
     * @param db the db
     * @throws DatabaseOperationException the database operation exception
     */
    private void executeOnTerminal(PLSourceEditor editor, IDebugObject debugObject, Database db)
            throws DatabaseOperationException {
        String query = editor.getDocument();
        DBConnection conn = null;

        try {
            conn = executeOnTerminal(editor, debugObject, query);
        } catch (DatabaseOperationException exception) {
            editor.setExecuteInProgress(false);
            String message = "";
            message = getServerMessageDetails(editor, query, exception);
            message = getErrorMessage(message, exception);
            editor.getConsoleMessageWindow(true).logErrorInUI(message);
            MPPDBIDELoggerUtility.error("ExecuteEditorItem: execute on terminal failed.", exception);
            throw new DatabaseOperationException("error occured.");
        } catch (DatabaseCriticalException e) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(e, db);
            return;
        } finally {
            releaseConnection(db, conn);
        }

    }

    /**
     * Execute on terminal.
     *
     * @param editor the editor
     * @param debugObject the debug object
     * @param query the query
     * @return the DB connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private DBConnection executeOnTerminal(PLSourceEditor editor, IDebugObject debugObject, String query)
            throws DatabaseCriticalException, DatabaseOperationException {
        DBConnection conn = null;
        if (query.trim().isEmpty()) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.EMPTY_PLSOURCE_TITEL),
                    MessageConfigLoader.getProperty(IMessagesConstants.EMPTY_PLSOURCE_VIEWER));
            return conn;
        }

        conn = debugObject.getDatabase().getConnectionManager().getObjBrowserConn();
        DatabaseUtils.executeOnSqlTerminalAndReturnNothing(query, 0, conn, editor.getConsoleMsgQueue(false));
        editor.setCriticalErr(true);
        editor.refreshDebugObjectAfterEdit(debugObject);
        editor.setModified(false);
        editor.setModifiedAfterCreate(false);
        editor.setExecuteInProgress(false);
        return conn;
    }

    /**
     * Release connection.
     *
     * @param db the db
     * @param conn the conn
     */
    private void releaseConnection(Database db, DBConnection conn) {
        if (conn != null) {
            db.getConnectionManager().releaseAndDisconnection(conn);
        }
    }

    /**
     * Gets the error message.
     *
     * @param message the message
     * @param e the e
     * @return the error message
     */
    private String getErrorMessage(String message, DatabaseOperationException e) {
        if (message.isEmpty()) {
            message = MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILED_ITEM,
                    MPPDBIDEConstants.LINE_SEPARATOR, e.getErrorCode(),
                    e.getServerMessage() == null ? e.getDBErrorMessage() : e.getServerMessage());
        }
        return message;
    }

    /**
     * Gets the server message details.
     *
     * @param editor the editor
     * @param query the query
     * @param e the e
     * @return the server message details
     */
    private String getServerMessageDetails(PLSourceEditor editor, String query, DatabaseOperationException e) {
        String message = "";
        if (validateServerMessage(e)) {
            try {
                getErrorDetailsForLocator(editor, query, e);
            } catch (BadLocationException exception) {
                MPPDBIDELoggerUtility.error("ExecuteEditorItem: execute on terminal failed.", exception);
            }

            message = MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILED_ITEM,
                    MPPDBIDEConstants.LINE_SEPARATOR, e.getErrorCode(),
                    (e.getServerMessage() == null ? e.getDBErrorMessage() : e.getServerMessage())
                            + MPPDBIDEConstants.LINE_SEPARATOR + "  Line Number: " + queryInfo.getErrLineNo());
        }
        return message;
    }

    /**
     * Validate server message.
     *
     * @param e the e
     * @return true, if successful
     */
    private boolean validateServerMessage(DatabaseOperationException e) {
        return e.getServerMessage() != null && e.getServerMessage().contains(ERROR_POSITION_IDENTIFIER);
    }

    /**
     * Gets the error details for locator.
     *
     * @param editor the editor
     * @param query the query
     * @param e the e
     * @return the error details for locator
     * @throws BadLocationException the bad location exception
     */
    private void getErrorDetailsForLocator(PLSourceEditor editor, String query, DatabaseOperationException e)
            throws BadLocationException {
        Database database = editor.getDatabase();

        if (database != null) {
            IErrorLocator errorLocator = database.getErrorLocator();
            String msg = e.getServerMessage();
            int errorPos = -1;
            errorPos = getErrorPOsition(msg, errorPos);

            errorPos = getErrorPositionForGreaterThanSize(query, errorPos);
            queryInfo.setErrorPosition(errorPos);
            int errLineNo = editor.getsourceViewerLineOfOffset(getErrorLineNumber(errorLocator));
            queryInfo.setErrLineNo(errLineNo + 1);
        }
    }

    /**
     * Gets the error line number.
     *
     * @param errorLocator the error locator
     * @return the error line number
     */
    private int getErrorLineNumber(IErrorLocator errorLocator) {
        return errorLocator.errorLineNumber(queryInfo.getStartOffset(), queryInfo.getErrorPosition());
    }

    /**
     * Gets the error P osition.
     *
     * @param msg the msg
     * @param errorPos the error pos
     * @return the error P osition
     */
    private int getErrorPOsition(String msg, int errorPos) {
        if (null != msg) {

            int idx = msg.lastIndexOf(ERROR_POSITION_IDENTIFIER);
            String newStr;
            newStr = getErrorPositionString(msg, idx);

            newStr = getStringForNewLine(newStr);
            errorPos = convertErrorPositionToInt(errorPos, newStr);
        }

        else {
            errorPos++;
        }
        return errorPos;
    }

    /**
     * Gets the error position for greater than size.
     *
     * @param query the query
     * @param errorPos the error pos
     * @return the error position for greater than size
     */
    private int getErrorPositionForGreaterThanSize(String query, int errorPos) {
        if (errorPos > query.length()) {
            errorPos = query.length();
        }
        return errorPos;
    }

    /**
     * Convert error position to int.
     *
     * @param errorPos the error pos
     * @param newStr the new str
     * @return the int
     */
    private int convertErrorPositionToInt(int errorPos, String newStr) {
        try {
            errorPos = Integer.parseInt(newStr.trim());
        } catch (NumberFormatException ex) {
            errorPos++;
        }
        return errorPos;
    }

    /**
     * Gets the error position string.
     *
     * @param msg the msg
     * @param idx the idx
     * @return the error position string
     */
    private String getErrorPositionString(String msg, int idx) {
        String newStr;
        if (idx != -1) {
            newStr = msg.substring(idx + ERROR_POSITION_IDENTIFIER.length() + 1);
        } else {
            newStr = msg.substring(ERROR_POSITION_IDENTIFIER.length() + 1);
        }
        return newStr;
    }

    /**
     * Gets the string for new line.
     *
     * @param newStr the new str
     * @return the string for new line
     */
    private String getStringForNewLine(String newStr) {
        if (newStr.contains(MPPDBIDEConstants.LINE_SEPARATOR)) {
            newStr = newStr.substring(0, newStr.indexOf(MPPDBIDEConstants.LINE_SEPARATOR));
        }
        return newStr;
    }

    /**
     * Execute SQL obj window.
     *
     * @param debugObject the debug object
     */
    public void executeSQLObjWindow(IDebugObject debugObject, boolean isCurrentTerminal) {
        ExcuteSQLObjectTable executeSQLObjectWindowCoreEdit = new ExcuteSQLObjectTable(
                Display.getDefault().getActiveShell());
        executeSQLObjectWindowCoreEdit.setSyntax(getSqlSyntax(debugObject));
        try {
            executeSQLObjectWindowCoreEdit.setDebugObject(debugObject);
        } catch (DatabaseCriticalException exception) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception, debugObject.getDatabase());
            return;
        } catch (MPPDBIDEException exception) {
            handleMppDbIdeException(exception);
            return;
        } catch (Exception exception) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_STARTING_TO_EXECUTE_SQL_OBJECT),
                    exception);
            MPPDBIDEDialogs.generateErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.PLSQL_ERR),
                    MessageConfigLoader.getProperty(IMessagesConstants.UNKNOWN_INTERNAL_ERR), exception);
        }
        executeSQLObjectWindowCoreEdit.defaultParameterValues();
        debugObject.setIsCurrentTerminal(isCurrentTerminal);

        openParameterWindow(debugObject, executeSQLObjectWindowCoreEdit);
    }

    private void handleMppDbIdeException(MPPDBIDEException exception) {
        UIDisplayUtil.getDebugConsole().logError(MessageConfigLoader
                .getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD, exception.getMessage()));
        MPPDBIDEDialogs.generateDSErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTE_DEBUGE),
                MessageConfigLoader.getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD,
                        exception.getServerMessage()),
                exception);
    }

    /**
     * Open parameter window.
     *
     * @param debugObject the debug object
     * @param executeSQLObjectWindowCoreEdit the execute SQL object window core
     * edit
     */
    private void openParameterWindow(IDebugObject debugObject, ExcuteSQLObjectTable executeSQLObjectWindowCoreEdit) {
        if (executeFunctionProcedure(debugObject, executeSQLObjectWindowCoreEdit)) {

            executeSQLObjectWindowCoreEdit.open();
        }
    }

    /**
     * Gets the sql syntax.
     *
     * @param debugObject the debug object
     * @return the sql syntax
     */
    private SQLSyntax getSqlSyntax(IDebugObject debugObject) {
        return debugObject.getDatabase() == null ? null : debugObject.getDatabase().getSqlSyntax();
    }

    /**
     * Execute function procedure.
     *
     * @param debugObject the debug object
     * @param executeSQLObjectWindowCoreEdit the execute SQL object window core
     * edit
     * @return true, if successful
     */
    private boolean executeFunctionProcedure(IDebugObject debugObject,
            ExcuteSQLObjectTable executeSQLObjectWindowCoreEdit) {
        boolean isContinue = true;
        if (validateOlapFunctions(debugObject)) {
            ArrayList<ObjectParameter> params = debugObject.getTemplateParameters();

            isContinue = executeForNoParameters(executeSQLObjectWindowCoreEdit, params);

            isContinue = validateExecutableFunction(isContinue, params);
        }
        return isContinue;
    }

    /**
     * Validate executable function.
     *
     * @param isContinue the is continue
     * @param params the params
     * @return true, if successful
     */
    private boolean validateExecutableFunction(boolean isContinue, ArrayList<ObjectParameter> params) {
        if (isContinue && !IHandlerUtilities.isFunctionExecutable(false, params)) {
            return false;
        }
        return isContinue;
    }

    /**
     * Execute for no parameters.
     *
     * @param executeSQLObjectWindowCoreEdit the execute SQL object window core
     * edit
     * @param params the params
     * @return true, if successful
     */
    private boolean executeForNoParameters(ExcuteSQLObjectTable executeSQLObjectWindowCoreEdit,
            ArrayList<ObjectParameter> params) {
        if (params == null || params.size() == 0) {
            executeSQLObjectWindowCoreEdit.executePressed();
            return false;
        }
        return true;
    }

    /**
     * Validate olap functions.
     *
     * @param debugObject the debug object
     * @return true, if successful
     */
    private boolean validateOlapFunctions(IDebugObject debugObject) {
        return debugObject.getObjectType() == OBJECTTYPE.PLSQLFUNCTION
                || debugObject.getObjectType() == OBJECTTYPE.PROCEDURE
                || debugObject.getObjectType() == OBJECTTYPE.SQLFUNCTION
                || debugObject.getObjectType() == OBJECTTYPE.CFUNCTION;
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        IDebugObject object = IHandlerUtilities.getSelectedDebugObject();
        boolean isObjectBrowserActive = UIElement.getInstance().isObjectBrowserActive();

        if (validatePlEditorExists(object)) {
            return false;
        }

        object = getDebugObject(object, isObjectBrowserActive);

        PLSourceEditor editor = UIElement.getInstance().getVisibleSourceViewer();

        if (getDbConnected(object)) {
            return validateOperationInProgress(editor);
        }
        return false;

    }

    /**
     * Validate pl editor exists.
     *
     * @param object the object
     * @return true, if successful
     */
    private boolean validatePlEditorExists(IDebugObject object) {
        return object == null && null == UIElement.getInstance().getVisibleSourceViewer();
    }

    /**
     * Gets the db connected.
     *
     * @param object the object
     * @return the db connected
     */
    private boolean getDbConnected(IDebugObject object) {
        return object != null && object.getDatabase().isConnected();
    }

    /**
     * Validate operation in progress.
     *
     * @param editor the editor
     * @return true, if successful
     */
    private boolean validateOperationInProgress(PLSourceEditor editor) {
        if (validateExecutionInProgress(editor)) {
            return false;
        }
        return true;
    }

    /**
     * Validate execution in progress.
     *
     * @param editor the editor
     * @return true, if successful
     */
    private boolean validateExecutionInProgress(PLSourceEditor editor) {
        return editor != null && (editor.isCompileInProgress() || editor.isExecuteInProgress());
    }

    /**
     * Gets the debug object.
     *
     * @param object the object
     * @param isObjectBrowserActive the is object browser active
     * @return the debug object
     */
    private IDebugObject getDebugObject(IDebugObject object, boolean isObjectBrowserActive) {
        PLSourceEditor sourceViewerEditor = UIElement.getInstance().getVisibleSourceViewer();
        if (sourceViewerEditor != null && (object == null || !isObjectBrowserActive)) {
            object = sourceViewerEditor.getDebugObject();
        }
        return object;
    }

    /**
     * Handle exception.
     *
     * @param e the e
     * @param event the event
     */
    @Override
    public void handleException(Throwable exception, ObjectChangeEvent event) {
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE),
                exception);
        ConsoleCoreWindow.getInstance()
                .logWarning(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE));
        String msg = MessageConfigLoader.getProperty(IMessagesConstants.FUNCT_CHANGE_EXECUTE_ERR)
                + exception.getMessage();
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.PL_SOURCE_VIEWER_ERROR), msg);
        PLSourceEditor srcEditor = event.getEditor();
        srcEditor.setExecuteInProgress(false);
        srcEditor.setCompileInProgress(false);
        srcEditor.enabledisableTextWidget(true);
    }

}
