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

package org.opengauss.mppdbide.view.handler.connection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.presentation.TerminalExecutionSQLConnectionInfra;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.ConsoleCoreWindow;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The following two features are similar: Visual Explain
 * plan, Execution plan & Cost.
 * 
 * This Class is abstracting the logic which involves connections and terminal.
 * 
 * ExecutionPlanWorker is inherited from this class
 * 
 * Later visual explain plan flow should also inherit from this class to avoid
 * code duplication. Currently that change is not being done to avoid too much
 * code change.
 *
 * @since 3.0.0
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
