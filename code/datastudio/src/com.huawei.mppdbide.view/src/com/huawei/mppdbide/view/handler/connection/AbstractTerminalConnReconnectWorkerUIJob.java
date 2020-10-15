/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.connection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.presentation.TerminalExecutionSQLConnectionInfra;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.ConsoleCoreWindow;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: a00415838 The following two features are similar: Visual Explain
 * plan, Execution plan & Cost.
 * 
 * This Class is abstracting the logic which involves connections and terminal.
 * 
 * 11-Jan-2019: ExecutionPlanWorker is inherited from this class to fix
 * DTS2019010905324.
 * 
 * Later visual explain plan flow should also inherit from this class to avoid
 * code duplication. Currently that change is not being done to avoid too much
 * code change.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class AbstractTerminalConnReconnectWorkerUIJob extends PromptPasswordUIWorkerJob {

    /**
     * The conn.
     */
    protected volatile DBConnection conn;

    /**
     * The term connection.
     */
    protected volatile TerminalExecutionSQLConnectionInfra termConnection;

    /**
     * The terminal.
     */
    protected volatile SQLTerminal terminal;

    /**
     * Instantiates a new abstract terminal conn reconnect worker UI job.
     *
     * @param name the name
     * @param family the family
     * @param errorWindowTitle the error window title
     * @param terminal the terminal
     */
    public AbstractTerminalConnReconnectWorkerUIJob(String name, Object family, String errorWindowTitle,
            SQLTerminal terminal) {
        super(name, family, errorWindowTitle);
        this.terminal = terminal;
        this.conn = null;
    }

    /**
     * Sets the reconnect flag.
     *
     * @param b the new reconnect flag
     */
    protected abstract void setReconnectFlag(boolean b);

    /**
     * Establish connection.
     *
     * @return true, if successful
     * @throws Exception the exception
     */
    protected boolean establishConnection() throws Exception {
        setServerPwd(!termConnection.getReuseConnectionFlag()
                && termConnection.getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
        conn = this.termConnection.getSecureConnection(this);
        while (conn == null) {
            Thread.sleep(SQL_TERMINAL_THREAD_SLEEP_TIME);
            if (this.isCancel()) {
                return false;
            }
            if (this.isNotified()) {
                conn = this.termConnection.getConnection();
            }
        }
        return true;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    protected Database getDatabase() {
        return this.termConnection.getDatabase();
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
    public abstract Object doJob()
            throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception;

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public abstract void onSuccessUIAction(Object obj);

    /**
     * Gets the reconnect pop up.
     *
     * @param termnal the termnal
     * @return the reconnect pop up
     */
    private int getReconnectPopUp(final SQLTerminal termnal) {
        String termName = termnal.getPartLabel();
        int btnPressed = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_DEBUG_CONNECTION_ERROR) + " : " + termName,
                MessageConfigLoader.getProperty(IMessagesConstants.RECONNECT_FOR_EXECUTION_PLAN_VISUAL_EXPLAIN));
        return btnPressed;
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        int btnPressed = getReconnectPopUp(terminal);
        if (btnPressed == IDialogConstants.OK_ID) {
            setReconnectFlag(true);
        } else if (btnPressed == IDialogConstants.CANCEL_ID) {
            Database db = terminal.getSelectedDatabase();
            DBConnProfCache.getInstance().destroyConnection(db);

            if (null != db) {
                ConsoleCoreWindow.getInstance()
                        .logFatal(MessageConfigLoader.getProperty(IMessagesConstants.DISCONNECTED_FROM_SERVER,
                                db.getServer().getServerConnectionInfo().getConectionName(), db.getName()));
            }
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (null != objectBrowserModel) {
                objectBrowserModel.refreshObject(db);
            }
        }
        MPPDBIDELoggerUtility.error("Operation Failed", exception);
    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        MPPDBIDELoggerUtility.error("Operation Failed", exception);
    }

    /**
     * Gets the reconnect flag.
     *
     * @return the reconnect flag
     */
    protected abstract boolean getReconnectFlag();

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {
        if (getReconnectFlag()) {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    try {
                        termConnection.releaseConnection();
                        TerminalExecutionSQLConnectionInfra termConn1 = (TerminalExecutionSQLConnectionInfra) PromptPrdGetConnection
                                .getConnection(terminal.getTermConnection());
                        termConnection.setConnection(termConn1.getConnection());
                        conn = termConnection.getConnection();

                    } catch (MPPDBIDEException exception) {
                        final String errMsg = exception.getServerMessage();
                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                                        MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR),
                                        MessageConfigLoader
                                                .getProperty(IMessagesConstants.MSG_HINT_DATABASE_CRITICAL_ERROR)
                                                + MPPDBIDEConstants.LINE_SEPARATOR + errMsg);
                                DBConnProfCache.getInstance().destroyConnection(terminal.getSelectedDatabase());

                                if (null != terminal.getSelectedDatabase()) {
                                    ConsoleCoreWindow.getInstance().logFatal(MessageConfigLoader.getProperty(
                                            IMessagesConstants.DISCONNECTED_FROM_SERVER, terminal.getSelectedDatabase()
                                                    .getServer().getServerConnectionInfo().getConectionName(),
                                            terminal.getSelectedDatabase().getName()));
                                }
                                ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                                if (null != objectBrowserModel) {

                                    objectBrowserModel.refreshObject(terminal.getSelectedDatabase());
                                }
                            }
                        });
                        termConnection.releaseSecureConnection(conn);
                        MPPDBIDELoggerUtility.error("Error while attempting to reconnect", exception);
                        return;
                    }
                }
            });
            this.termConnection.releaseSecureConnection(this.conn);
            this.schedule();
        } else {
            super.finalCleanup();
            this.termConnection.releaseSecureConnection(this.conn);
        }
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        resetWorkinProgress();
    }

    /**
     * Reset workin progress.
     */
    protected abstract void resetWorkinProgress();

}
