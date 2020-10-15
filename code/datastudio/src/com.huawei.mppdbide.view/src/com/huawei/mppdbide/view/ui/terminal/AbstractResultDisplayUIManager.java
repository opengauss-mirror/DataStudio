/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.terminal;

import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.CanContextContinueExecuteRule;
import com.huawei.mppdbide.presentation.ContextExecutionOperationType;
import com.huawei.mppdbide.presentation.IResultDisplayUIManager;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.utils.stringparse.IServerMessageParseUtils;
import com.huawei.mppdbide.view.core.ConsoleCoreWindow;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.handler.connection.PasswordDialog;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractResultDisplayUIManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class AbstractResultDisplayUIManager implements IResultDisplayUIManager {

    /**
     * The is disposed.
     */
    protected boolean isDisposed;

    /**
     * The stat msg.
     */
    protected StatusMessage statMsg;

    /**
     * The term connection.
     */
    protected TerminalExecutionConnectionInfra termConnection;

    /**
     * The is user notified.
     */
    protected boolean isUserNotified = false;
    private ContextExecutionOperationType ctxtExecOperType;
    private boolean isCancelContextExecution = false;
    private boolean isCriticalErrOccurred = false;
    private boolean printToConsole = false;
    private boolean cancelPressedDoNotSave = false;

    // this flag informs if the execution context can continue its execution or
    // not. There can be multiple cases when the UI display manager might be
    // forced to stop the execution.
    // Some of the known cases are
    // 1)user key entered is wrong-SQLTerminal.
    // 2)Max result set reached.
    /**
     * The can ctxt exec continue.
     */
    protected CanContextContinueExecuteRule canCtxtExecContinue;

    /**
     * The saved exception.
     */
    protected MPPDBIDEException savedException;
    private int returnVal;
    private int btnPressed;

    /**
     * Gets the part ID.
     *
     * @return the part ID
     */
    protected abstract String getPartID();

    /**
     * Gets the console message window.
     *
     * @param bringOnTop the bring on top
     * @return the console message window
     */
    protected abstract ConsoleMessageWindow getConsoleMessageWindow(boolean bringOnTop);

    /**
     * Handle specific UI updates for presetup.
     */
    protected void handleSpecificUIUpdatesForPresetup() {
        // do nothing
    }

    /**
     * Can dislay result.
     *
     * @return true, if successful
     */
    protected abstract boolean canDislayResult();

    /**
     * Creates the result new.
     *
     * @param resultsetDisplaydata the resultset displaydata
     * @param consoledata the consoledata
     * @param queryExecSummary the query exec summary
     */
    protected abstract void createResultNew(IDSGridDataProvider resultsetDisplaydata, IConsoleResult consoledata,
            IQueryExecutionSummary queryExecSummary);

    /**
     * Instantiates a new abstract result display UI manager.
     *
     * @param termConnection the term connection
     */
    public AbstractResultDisplayUIManager(TerminalExecutionConnectionInfra termConnection) {
        canCtxtExecContinue = CanContextContinueExecuteRule.CONTEXT_EXECUTION_PROCEED;
        this.setTermConnection(termConnection);
    }

    private void setTermConnection(TerminalExecutionConnectionInfra termConnection2) {
        this.termConnection = termConnection2;
    }

    /**
     * Sets the status msg.
     *
     * @param statusMsg the new status msg
     */
    void setStatusMsg(StatusMessage statusMsg) {
        this.statMsg = statusMsg;
    }

    /**
     * Gets the status msg.
     *
     * @return the status msg
     */
    StatusMessage getStatusMsg() {
        return this.statMsg;
    }

    /**
     * Gets the query info.
     *
     * @return the query info
     */
    protected int getqueryInfo() {
        return -1;
    }

    /**
     * Reset control variables.
     */
    protected void resetControlVariables() {
        this.isCancelContextExecution = false;
        this.isUserNotified = false;
        this.savedException = null;
        this.canCtxtExecContinue = CanContextContinueExecuteRule.CONTEXT_EXECUTION_PROCEED;
    }

    /**
     * Handle step completion.
     */
    @Override
    public void handleStepCompletion() {

    }

    /**
     * Handle pre execution UI display setup critical.
     *
     * @param termConn the term conn
     * @param isCriticalError the is critical error
     */
    @Override
    public void handlePreExecutionUIDisplaySetupCritical(final TerminalExecutionConnectionInfra termConn,
            final boolean isCriticalError) {
        printToConsole = false;
        isCriticalErrOccurred = isCriticalError;
        establishConn(termConn);
        boolean bool = UIElement.getInstance().isPartOnTop(getPartID());

        if (printToConsole && null != getConsoleMessageWindow(bool)) {
            getConsoleMessageWindow(bool)
                    .logInfo(MessageConfigLoader.getProperty(IMessagesConstants.RECONNECT_SUCCESSFUL_MSG));
        }
    }

    /**
     * Handle pre execution UI display setup.
     *
     * @param termConn the term conn
     * @param isActivateStatusBar the is activate status bar
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void handlePreExecutionUIDisplaySetup(final TerminalExecutionConnectionInfra termConn,
            final boolean isActivateStatusBar) throws MPPDBIDEException {
        if (isDisposed()) {
            return;
        }

        establishConn(termConn);

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if (isActivateStatusBar) {
                    final BottomStatusBar btmStatusBar = UIElement.getInstance().getProgressBarOnTop();
                    setStatusMsg(new StatusMessage(
                            MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_EXECUTE_STATUSBAR)));
                    StatusMessageList.getInstance().push(getStatusMsg());
                    if (btmStatusBar != null) {
                        btmStatusBar.activateStatusbar();
                    }
                }

            }

        });

        if (null != this.savedException) {
            throw savedException;
        }

    }

    private void establishConn(final TerminalExecutionConnectionInfra termConn) {
        boolean isExceptionForInvalidPswd = false;

        Database db = termConn.getDatabase();
        DBConnection conn = termConn.getConnection();

        while (isConnectionClosed(conn)) {
            if (cancelPressedDoNotSave) {
                break;
            }
            if (isDBConnected(db)) {
                try {
                    if (isShowPasswordDialog(isExceptionForInvalidPswd, db)) {
                        getPasswordDialog(db);
                        if (returnVal != 0) {
                            // user gives up Prd can cancels the flow.
                            // Set to inform the context to stop.
                            cancelPressedOnDoNotSavePswdDialog();
                            printToConsole = false;
                            break;
                        }
                    }

                    conn = getFreeConn(termConn, db);
                    setPrintToConsole(conn);
                } catch (MPPDBIDEException exception) {
                    if (exception instanceof DatabaseOperationException) {
                        String regex = "(?i)(incorrect|invalid)\\s+(username|user)\\s*(or\\s+|\\/)password";

                        String msg = onDatabaseOperationException(exception);
                        Matcher errMsg = Pattern.compile(regex).matcher(msg);
                        if (!errMsg.find()) {
                            isExceptionForInvalidPswd = false;
                            onFailureThrowDBOperationExp();
                            break;
                        } else {
                            isExceptionForInvalidPswd = true;
                        }
                    } else if (exception instanceof DatabaseCriticalException) {

                        conn = handleDbCriticalExceptionOnEstablishConn(termConn, db, conn, exception);
                    } else {
                        ifOtherExceptionOccured(exception);
                        return;
                    }
                    MPPDBIDELoggerUtility.error("AbstractResultDisplayUIManager: Establishing connection failed.",
                            exception);
                } catch (SQLException sqlException) {
                    MPPDBIDELoggerUtility.error("Error while setting Auto Commit flag", sqlException);
                }
            } else {
                onFailureThrowDBOperationExp();
                break;
            }
        }
    }

    private DBConnection handleDbCriticalExceptionOnEstablishConn(final TerminalExecutionConnectionInfra termConn,
            Database db, DBConnection conn, MPPDBIDEException exception) {
        if (isCriticalErrOccurred) {
            conn = handleReconnectFailOpearation(termConn, db, conn, exception);
            refreshObjectBrowser(db);

        } else {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception,
                    termConnection.getDatabase());
        }
        return conn;
    }

    private void refreshObjectBrowser(Database db) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                if (null != objectBrowserModel) {

                    objectBrowserModel.refreshObject(db);
                }

            }
        });
    }

    private void setPrintToConsole(DBConnection conn) {
        if (null != conn) {
            printToConsole = true;
        }
    }

    private boolean isShowPasswordDialog(boolean isExceptionForInvalidPswd, Database db) {
        return db.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE) || isExceptionForInvalidPswd;
    }

    private boolean isDBConnected(Database db) {
        return null != db && db.isConnected();
    }

    private DBConnection handleReconnectFailOpearation(final TerminalExecutionConnectionInfra termConn, Database db,
            DBConnection conn, MPPDBIDEException mppdbideException) {
        int popupAppeared = 1;
        boolean isExceptionForInvalidPswd = false;
        while (popupAppeared < MPPDBIDEConstants.RECONNECT_POPUP_LIMIT) {
            if (cancelPressedDoNotSave) {
                break;
            }
            reconnectFailedGetReattemptConnDialog();
            popupAppeared++;
            if (btnPressed == IDialogConstants.OK_ID) {
                while (isConnectionClosed(conn)) {
                    if (isDBConnected(db)) {
                        try {
                            if (isInvalidPassword(isExceptionForInvalidPswd, db)) {
                                break;
                            }
                            conn = handlegetConnectionOnReconnect(termConn, db);
                        } catch (MPPDBIDEException exp) {
                            if (exp instanceof DatabaseOperationException) {
                                String msg = onDatabaseOperationException(exp);
                                if (validateMsgContent(msg)) {
                                    isExceptionForInvalidPswd = false;
                                    onFailureThrowDBOperationExp();
                                    break;
                                } else if (UIDisplayFactoryProvider.getUIDisplayStateIf()
                                        .needPromptPasswordError(msg)) {
                                    isExceptionForInvalidPswd = true;
                                }
                            } else {
                                handleOtherExceptions(db, popupAppeared, exp);
                                break;
                            }
                            addLogger(exp);
                        } catch (SQLException exception) {
                            disconnectConnection(conn, exception);
                            break;
                        }
                    } else {
                        onFailureThrowDBOperationExp();
                        break;
                    }
                }
            } else {
                handleAndLogReconnectFailCancelOperation(db, mppdbideException);
                break;
            }
        }
        return conn;
    }

    private DBConnection handlegetConnectionOnReconnect(final TerminalExecutionConnectionInfra termConn, Database db)
            throws MPPDBIDEException, SQLException {
        DBConnection conn;
        cancelPressedDoNotSave = false;
        conn = getFreeConn(termConn, db);
        setPrintToConsole(conn);
        return conn;
    }

    private void addLogger(MPPDBIDEException exp) {
        MPPDBIDELoggerUtility.error(
                "AbstractResultDisplayUIManager:" + "Attempt to reestablish conncetion,Ok operation failed.", exp);
    }

    private boolean validateMsgContent(String msg) {
        return msg == null || !msg.contains("Invalid username/password") || !msg.contains("Incorrect user or password");
    }

    private void handleOtherExceptions(Database db, int popupAppeared, MPPDBIDEException exp) {
        if (exp instanceof DatabaseCriticalException) {
            handleDatabaseCriticalException(db, popupAppeared, exp);
        } else {
            ifOtherExceptionOccured(exp);
        }
    }

    private boolean isInvalidPassword(boolean isExceptionForInvalidPswd, Database db) {
        if (isShowPasswordDialog(isExceptionForInvalidPswd, db)) {
            getPasswordDialog(db);
            if (returnVal != 0) {
                // user gives up Prd can cancels the flow.
                // Set to inform the context to stop.
                cancelPressedOnDoNotSavePswdDialog();
                cancelPressedDoNotSave = true;
                printToConsole = false;
                return true;
            }
        }
        return false;
    }

    private void handleDatabaseCriticalException(Database db, int popupAppeared, MPPDBIDEException exp) {
        if (popupAppeared == MPPDBIDEConstants.RECONNECT_POPUP_LIMIT) {
            reconnectFailedForManyTimesDisconnectDB(db, exp);
            printToConsole = false;
        }
    }

    private void disconnectConnection(DBConnection conn, SQLException exception) {
        if (conn != null) {
            conn.disconnect();
        }
        MPPDBIDELoggerUtility.error("Error while setting Auto Commit flag", exception);
    }

    private void handleAndLogReconnectFailCancelOperation(Database db, MPPDBIDEException mppdbideException) {
        if (null != db) {
            ConsoleCoreWindow.getInstance()
                    .logFatal(MessageConfigLoader.getProperty(IMessagesConstants.DISCONNECTED_FROM_SERVER,
                            db.getServer().getServerConnectionInfo().getConectionName(), db.getName()));
            handleReconnectFailCancelOperation(db);
        }
        MPPDBIDELoggerUtility.error(
                "AbstractResultDisplayUIManager:Attempt to reestablish conncetion, Cancel operation failed .",
                mppdbideException);
    }

    private void reconnectFailedGetReattemptConnDialog() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                setBtnPressed(MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERR),
                        MessageConfigLoader.getProperty(IMessagesConstants.RECONNECT_ATTEMP_FAILURE_MSG)));

            }
        });

    }

    private void reconnectFailedForManyTimesDisconnectDB(Database db, MPPDBIDEException exception) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERR), MessageConfigLoader
                                .getProperty(IMessagesConstants.MULTIPLE_CONNECTION_ATTEMPT_ON_CRITICAL_ERROR));

            }
        });
        handleReconnectFailCancelOperation(db);
        MPPDBIDELoggerUtility.error(
                "AbstractResultDisplayUIManager: Attempt to reestablish connection for many times failed.", exception);

    }

    private void getPasswordDialog(Database db) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                Shell shell = Display.getDefault().getActiveShell();
                PasswordDialog dialog = new PasswordDialog(shell, db);
                setReturnVal(dialog.open());

            }
        });
    }

    private void ifOtherExceptionOccured(MPPDBIDEException mppdbideException) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if (mppdbideException.getDBErrorMessage().contains("Maximum connection limit")) {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR),
                            mppdbideException.getDBErrorMessage());
                }
            }
        });
    }

    private String onDatabaseOperationException(MPPDBIDEException mppdbideException) {
        String msg = mppdbideException.getServerMessage() != null ? mppdbideException.getServerMessage()
                : mppdbideException.getMessage();
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR), msg);
                getConsoleMessageWindow(false).logError(msg);

            }
        });

        return msg;
    }

    private void onFailureThrowDBOperationExp() {
        cancelPressedOnDoNotSavePswdDialog();
        savedException = new DatabaseOperationException(IMessagesConstants.ERR_BL_SERVER_CONNECTION_FAILED);
    }

    private void cancelPressedOnDoNotSavePswdDialog() {
        AbstractResultDisplayUIManager.this
                .setcanContextExecutionContinue(CanContextContinueExecuteRule.CONTEXT_EXECUTION_STOP);
    }

    private void handleReconnectFailCancelOperation(Database db) {
        DBConnProfCache.getInstance().destroyConnection(db);
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                if (null != objectBrowserModel) {

                    objectBrowserModel.refreshObject(db);
                }
                UIElement.getInstance().updateTextEditorsIconAndConnButtons(db.getServer());

            }
        });

    }

    private DBConnection getFreeConn(final TerminalExecutionConnectionInfra termConn, Database db)
            throws MPPDBIDEException, SQLException {
        DBConnection conn;
        termConn.setConnection(db.getConnectionManager().getFreeConnection());
        conn = termConn.getConnection();
        conn.getConnection().setAutoCommit(termConn.getAutoCommitFlag());
        return conn;
    }

    private boolean isConnectionClosed(DBConnection conn) {
        try {
            return null == conn || conn.isClosed();
        } catch (DatabaseOperationException e) {
            return true;
        }
    }

    /**
     * Handle result display.
     *
     * @param result the result
     * @param consoleData the console data
     * @param queryExecSummary the query exec summary
     */
    @Override
    public void handleResultDisplay(Object result, IConsoleResult consoleData,
            IQueryExecutionSummary queryExecSummary) {
        if (isDisposed()) {
            return;
        }

        if (result instanceof IDSGridDataProvider) {
            this.handleResultSetDisplay((IDSGridDataProvider) result, consoleData, queryExecSummary);
        } else {
            this.handleConsoleOnlyResultDisplay(consoleData);
        }
    }

    /**
     * Handle result display.
     *
     * @param result the result
     */
    public void handleResultDisplay(Object result) {
        if (isDisposed()) {
            return;
        }
        this.handleResultSetDisplay(result);
    }

    private void handleResultSetDisplay(final Object result) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (isDisposed()) {
                    return;
                }

                if (canDislayResult()) {
                    createResultNew(result);
                } else {
                    handleResultDisplayFailureDialog();

                    return;
                }
            }
        });

    }

    /**
     * Creates the result new.
     *
     * @param result the result
     */
    protected void createResultNew(Object result) {

    }

    private void handleResultSetDisplay(final IDSGridDataProvider result, final IConsoleResult consoleData,
            final IQueryExecutionSummary queryExecSummary) {
        if (canDislayResult()) {
            doAsyncResultDisplay(result, consoleData, queryExecSummary);
        } else {
            if (!isUserNotified) {
                isUserNotified = true;
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        handleResultDisplayFailureDialog();
                    }
                });

            }
        }
    }

    private void doAsyncResultDisplay(final IDSGridDataProvider result, final IConsoleResult consoleData,
            final IQueryExecutionSummary queryExecSummary) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if (isDisposed()) {
                    return;
                }

                if (canDislayResult()) {
                    createResultNew(result, consoleData, queryExecSummary);
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

    /**
     * Handle result display failure dialog.
     */
    protected void handleResultDisplayFailureDialog() {
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.MAX_SOURCE_VIEWER),
                MessageConfigLoader.getProperty(IMessagesConstants.INFO_MAX_RESULT_SOURCE_VIEWER));

    }

    /**
     * Handle console display.
     *
     * @param consoleData the console data
     */
    @Override
    public void handleConsoleDisplay(IConsoleResult consoleData) {
        if (isDisposed()) {
            return;
        }

        handleConsoleOnlyResultDisplay(consoleData);
    }

    /**
     * Handle exception display.
     *
     * @param object the object
     */
    @Override
    public void handleExceptionDisplay(Object object) {
        if (isDisposed()) {
            return;
        }
        if (object instanceof DatabaseCriticalException) {
            DatabaseCriticalException databaseCriticalException = (DatabaseCriticalException) object;

            consoleLogExecutionFailure(databaseCriticalException);
            handleSpecificUIUpdatesForException((Exception) object);
        }
        if (object instanceof DatabaseOperationException) {
            handleSpecificUIUpdatesForException((Exception) object);
            consoleLogExecutionFailure((DatabaseOperationException) object);
        }
        if (object instanceof OutOfMemoryError) {
            OutOfMemoryError ome = (OutOfMemoryError) object;
            boolean bool = UIElement.getInstance().isPartOnTop(getPartID());

            getConsoleMessageWindow(bool).logInfo(ome.getMessage());
        }

    }

    private void consoleLogExecutionFailure(final MPPDBIDEException mppdbideException) {
        ConsoleMessageWindow consoleWindow = getConsoleMessageWindow(true);
        String message = "";
        if (getqueryInfo() == -1) {
            message = MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILED_ITEM,
                    MPPDBIDEConstants.LINE_SEPARATOR, mppdbideException.getErrorCode(),
                    mppdbideException.getServerMessage() == null ? mppdbideException.getDBErrorMessage()
                            : mppdbideException.getServerMessage());
        } else {
            message = MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILED_ITEM,
                    MPPDBIDEConstants.LINE_SEPARATOR, mppdbideException.getErrorCode(),
                    (mppdbideException.getServerMessage() == null ? mppdbideException.getDBErrorMessage()
                            : mppdbideException.getServerMessage()) + "Line Number: " + getqueryInfo()
                            + MPPDBIDEConstants.LINE_SEPARATOR);
        }

        if (mppdbideException.getServerMessage() != null && mppdbideException.getServerMessage()
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.UI_CANCEL_QUERY))) {
            handleCancelRequest();
        } else {
            if (null != consoleWindow) {
                consoleWindow.logError(message);
            }
        }

        MPPDBIDELoggerUtility.debug(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILED_ITEM,
                MPPDBIDEConstants.LINE_SEPARATOR, "", "Exception happened. Console error logging here"));

    }

    /**
     * Handle final cleanup.
     */
    @Override
    public void handleFinalCleanup() {
        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        MPPDBIDEDialogs.clearExistingDialog();

        if (null != bttmStatusBar) {
            bttmStatusBar.hideStatusbar(getStatusMsg());
        }
    }

    /**
     * Handle console only result display.
     *
     * @param consoleDisplayData the console display data
     */
    protected void handleConsoleOnlyResultDisplay(final IConsoleResult consoleDisplayData) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (!isDisposed()) {
                    getConsoleMessageWindow(true).logInfo(consoleDisplayData);
                }
            }
        });
    }

    /**
     * Handle successfull completion.
     */
    @Override
    public void handleSuccessfullCompletion() {
        // generally nothing to do. Specific classes can realize this

    }

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    @Override
    public TerminalExecutionConnectionInfra getTermConnection() {
        return this.termConnection;
    }

    /**
     * Checks if is disposed.
     *
     * @return true, if is disposed
     */
    public boolean isDisposed() {
        return isDisposed;
    }

    /**
     * Sets the disposed.
     */
    public void setDisposed() {
        this.isDisposed = true;
    }

    /**
     * Can context execution continue.
     *
     * @return the can context continue execute rule
     */
    public CanContextContinueExecuteRule canContextExecutionContinue() {
        return canCtxtExecContinue;
    }

    /**
     * Sets the can context execution continue.
     *
     * @param contextExecutionStop the new can context execution continue
     */
    protected void setcanContextExecutionContinue(CanContextContinueExecuteRule contextExecutionStop) {
        this.canCtxtExecContinue = contextExecutionStop;
    }

    /**
     * Handle specific UI updates for exception.
     *
     * @param o the o
     */
    protected void handleSpecificUIUpdatesForException(Exception o) {
        // nothing to do...
    }

    /**
     * Inits the display manager.
     *
     * @param execType the exec type
     */
    @Override
    public void initDisplayManager(ContextExecutionOperationType execType) {
        this.ctxtExecOperType = execType;
        resetControlVariables();
        handleSpecificUIUpdatesForPresetup();
    }

    /**
     * Checks if is pre execution UI display setup ok.
     *
     * @return true, if is pre execution UI display setup ok
     */
    protected boolean isPreExecutionUIDisplaySetupOk() {
        return this.savedException == null ? true : false;
    }

    /**
     * Gets the context execution operation type.
     *
     * @return the context execution operation type
     */
    protected final ContextExecutionOperationType getContextExecutionOperationType() {
        return this.ctxtExecOperType;
    }

    /**
     * Handle cancel request.
     */
    public void handleCancelRequest() {
        /*
         * Just marking that the cancel request has come. Also marking along
         * side that the context can no longer continue. The actual handling for
         * cancel query is now moved to handleFinalCleanup
         */
        isCancelContextExecution = true;
        canCtxtExecContinue = CanContextContinueExecuteRule.CONTEXT_EXECUTION_STOP;
    }

    /**
     * Gets the checks if is context in cancel state.
     *
     * @return the checks if is context in cancel state
     */
    protected boolean getIsContextInCancelState() {
        return isCancelContextExecution;
    }

    /**
     * Show result.
     *
     * @param parentComposite the parent composite
     */
    public void showResult(Composite parentComposite) {

    }

    /**
     * Sets the dirty handler.
     *
     * @param dirtyHandler the new dirty handler
     */
    public void setDirtyHandler(MDirtyable dirtyHandler) {

    }

    /**
     * Reset data result.
     */
    public void resetDataResult() {

    }

    /**
     * Sets the return val.
     *
     * @param returnVal the new return val
     */
    public void setReturnVal(int returnVal) {
        this.returnVal = returnVal;
    }

    /**
     * Sets the btn pressed.
     *
     * @param btnPressed the new btn pressed
     */
    public void setBtnPressed(int btnPressed) {
        this.btnPressed = btnPressed;
    }

    /**
     * @param query input query
     * @return is execute statement
     */
    protected boolean isSourceViewable(String query) {
        if (query.toUpperCase(Locale.ENGLISH).startsWith("EXEC") || query.toUpperCase(Locale.ENGLISH).startsWith("CALL")
                || (query.toUpperCase(Locale.ENGLISH).startsWith("SELECT")
                        && IServerMessageParseUtils.isQuerySelectFunction(query))) {
            return true;
        }
        return false;
    }

}
