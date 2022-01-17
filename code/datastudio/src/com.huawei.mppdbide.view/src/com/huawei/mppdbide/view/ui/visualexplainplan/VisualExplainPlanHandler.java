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

package com.huawei.mppdbide.view.ui.visualexplainplan;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.presentation.TerminalExecutionSQLConnectionInfra;
import com.huawei.mppdbide.presentation.visualexplainplan.ExplainPlanPresentation;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.SQLTerminalQuerySplit;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.ConsoleCoreWindow;
import com.huawei.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanHandler.
 * 
 * @since 3.0.0
 */
public class VisualExplainPlanHandler {
    private static boolean isVisExplainJobRunning = false;
    private static boolean isReconnect = false;

    @Inject
    private ECommandService commandService;
    @Inject
    private EHandlerService handlerService;

    /**
     * Execute.
     *
     * @param activePart the active part
     * @param modelService the model service
     * @param app the app
     * @param partService the part service
     */
    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_PART) @Optional MPart activePart, EModelService modelService,
            MApplication app, EPartService partService) {
        String query = null;

        String jobName = MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_JOB_DETAILS);
        if (null == activePart) {
            return;
        }

        Object partObject = activePart.getObject();
        if (partObject instanceof SQLTerminal) {
            SQLTerminal terminal = (SQLTerminal) partObject;
            setVisExplainJobRunning(true);
            query = terminal.getSelectedQry();

            ArrayList<String> queryArray = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
            SQLTerminalQuerySplit querySplitter = new SQLTerminalQuerySplit();
            try {
                querySplitter.splitQuerries(queryArray, null != query ? query : "", true);
            } catch (DatabaseOperationException e) {
                MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_PARSE_FAIL),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXE_PLAN_QUERY_PARSE_FAILED),
                        MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));
                setVisExplainJobRunning(false);
                return;
            }

            // the ownership of the connection is now moved to
            // ExplainPlanPresentation. It needs to execute the query and
            // release it at its own will.

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();

            StatusMessage statMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_GENERATEQUERYPLAN_STATUSBAR));
            RunQueryAndStartPart job = new RunQueryAndStartPart(null != query ? query : "", queryArray,
                    terminal.getConsoleMessageWindow(true).getMsgQueue(), jobName, MPPDBIDEConstants.CANCELABLEJOB,
                    statMessage, terminal, false);

            StatusMessageList.getInstance().push(statMessage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            job.schedule();
        }
    }

    /**
     * Can execute.
     *
     * @param activePart the active part
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute(@Named(IServiceConstants.ACTIVE_PART) @Optional MPart activePart) {
        if (null == activePart) {
            return false;
        }

        Object partObject = activePart.getObject();
        if (partObject instanceof SQLTerminal) {
            SQLTerminal terminal = (SQLTerminal) partObject;
            UIElement uiEle = UIElement.getInstance();
            boolean isSQLTerminalEmpty = (!"".equals(terminal.getDocumentContent().trim())) || uiEle.isNewEditorOnTop()
                    || uiEle.isEditorOnTopById();
            boolean isVisExplainValid = isVisExplainJobRunning() || !isServerCompatible(terminal)
                    || !terminal.getSelectedDatabase().isConnected() || UIElement.getInstance()
                            .checkIfVisualWindowVisible(VisualExplainPartsManager.getInstance().getNewWindowHandler());
            if (isSQLTerminalEmpty && isVisExplainValid) {
                return false;
            }
            return isSQLTerminalEmpty;
        }
        return false;
    }

    private boolean isServerCompatible(SQLTerminal terminal) {
        if (terminal.getSelectedDatabase() != null && terminal.getSelectedDatabase().isExplainPlanSupported()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is vis explain job running.
     *
     * @return true, if is vis explain job running
     */
    public static boolean isVisExplainJobRunning() {
        return isVisExplainJobRunning;
    }

    /**
     * Sets the vis explain job running.
     *
     * @param isVisExplnJobRunning the new vis explain job running
     */
    public static void setVisExplainJobRunning(boolean isVisExplnJobRunning) {
        VisualExplainPlanHandler.isVisExplainJobRunning = isVisExplnJobRunning;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RunQueryAndStartPart.
     */
    public static class RunQueryAndStartPart extends PromptPasswordUIWorkerJob {
        private static final String VISUAL_EXPLAIN_PLAN = "Visual Explain Plan ";
        private ExplainPlanPresentation presentation;
        private StatusMessage statMessage;
        private DBConnection conn;
        private TerminalExecutionSQLConnectionInfra termConnection;
        private String query;
        private ArrayList<String> queryArray;
        private MessageQueue messageQueue;
        private SQLTerminal terminal;
        private LinkedHashMap<String, ExplainPlanPresentation> explainPlanPresentations;
        private boolean dummyExecution;

        /**
         * Instantiates a new run query and start part.
         *
         * @param query the query
         * @param queryArray the query array
         * @param messageQueue the message queue
         * @param name the name
         * @param family the family
         * @param statMessage2 the stat message 2
         * @param terminal the terminal
         * @param isTesting the is testing
         */
        public RunQueryAndStartPart(String query, ArrayList<String> queryArray, MessageQueue messageQueue, String name,
                Object family, StatusMessage statMessage2, SQLTerminal terminal, boolean isTesting) {
            super(name, family, IMessagesConstants.VIS_EXPLAIN_PLAN_ERROR_POPUP_HEADER);
            this.query = query;
            this.queryArray = new ArrayList<String>(4);
            this.queryArray.addAll(queryArray);
            this.messageQueue = messageQueue;
            this.statMessage = statMessage2;
            this.conn = null;
            this.terminal = terminal;
            this.dummyExecution = isTesting;
            explainPlanPresentations = new LinkedHashMap<String, ExplainPlanPresentation>(queryArray.size());
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
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            isReconnect = false;
            setServerPwd(!termConnection.getReuseConnectionFlag()
                    && termConnection.getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
            conn = this.termConnection.getSecureConnection(this);
            while (conn == null) {
                Thread.sleep(SQL_TERMINAL_THREAD_SLEEP_TIME);
                if (this.isCancel()) {
                    return null;
                }
                if (this.isNotified()) {
                    conn = this.termConnection.getConnection();
                }
            }

            int aaraySize = queryArray.size();

            ArrayList<String> queryArrExe = new ArrayList<String>(10);

            /* Remove the Empty Queries */
            for (int varFor = 0; varFor < aaraySize; varFor++) {
                String qury = queryArray.get(varFor);
                String modifiedQuery = qury.replace(MPPDBIDEConstants.LINE_SEPARATOR, " ").trim();
                if (!(StringUtils.isBlank(modifiedQuery))) {
                    queryArrExe.add(qury);
                }
            }
            int windowCount = 0;
            for (int index = 0; index < queryArrExe.size(); index++) {
                presentation = new ExplainPlanPresentation(query, queryArrExe.get(index), messageQueue, conn,
                        termConnection.getDatabase());
                presentation.doExplainPlanAnalysis(dummyExecution);
                // adding in the one to show the title for user- reference
                windowCount++;
                String key = VISUAL_EXPLAIN_PLAN + windowCount;
                explainPlanPresentations.put(key, presentation);
            }
            windowCount = 0;

            return explainPlanPresentations;
        }

        /**
         * On success UI action.
         *
         * @param obj the obj
         */
        @Override
        public void onSuccessUIAction(Object obj) {
            if (null == explainPlanPresentations) {
                return;
            }
            if (explainPlanPresentations.entrySet().iterator().hasNext()) {
                for (Map.Entry<String, ExplainPlanPresentation> pres : explainPlanPresentations.entrySet()) {
                    UIElement.getInstance().newTabInWindow(VisualExplainPartsManager.getVisualPlanWindowId(),
                            VisualExplainPartsManager.getMainPartStackid(), pres.getKey());
                }

                // call first tab in window to render
                Entry<String, ExplainPlanPresentation> entry = explainPlanPresentations.entrySet().iterator().next();
                String key = entry.getKey();
                ExplainPlanPresentation value = entry.getValue();
                VisualExplainPlanUIPresentation prep = new VisualExplainPlanUIPresentation(key, value);
                VisualExplainPartsManager.getInstance().createVisualExplainPlanParts(prep.getExplainPlanTabId(), prep);
                getWindowsDetailsForSelectionListener();
            }
        }

        /**
         * Gets the windows details for selection listener.
         * 
         * @return the windows details for selection listener
         */
        private void getWindowsDetailsForSelectionListener() {
            final Shell shell = UIElement.getInstance()
                    .getVisualExplainPlanWindow(VisualExplainPartsManager.getVisualPlanWindowId());
            if (shell.getChildren().length > 0 && shell.getChildren()[0] instanceof Composite) {
                Composite composite = (Composite) shell.getChildren()[0];
                if (composite.getChildren().length > 0 && composite.getChildren()[0] instanceof CTabFolder) {
                    CTabFolder ctabfolder = (CTabFolder) composite.getChildren()[0];
                    ctabfolder.setMaximizeVisible(false);
                    ctabfolder.setMinimizeVisible(false);
                    CTabItem[] items = ctabfolder.getItems();
                    for (int i = 0; i < items.length; i++) {
                        items[i].addListener(SWT.Dispose, new Listener() {
                            @Override
                            public void handleEvent(Event event) {
                                if (ctabfolder.getItemCount() == 0 && event.widget != null) {
                                    shell.close();
                                } else {
                                    if (event.widget instanceof CTabItem) {
                                        CTabItem item = (CTabItem) event.widget;
                                        item.dispose();
                                    }
                                }
                            }
                        });
                    }
                    ctabfolder.addListener(SWT.MouseUp,
                            new VisualExplainPlanCompositeListener(explainPlanPresentations));
                }
            }
        }

        /**
         * On critical exception UI action.
         *
         * @param dbCriticalException the db critical exception
         */
        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
            String termName = terminal.getPartLabel();
            int btnPressed = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_DEBUG_CONNECTION_ERROR) + " : "
                            + termName,
                    MessageConfigLoader.getProperty(IMessagesConstants.RECONNECT_FOR_EXECUTION_PLAN_VISUAL_EXPLAIN));
            if (btnPressed == IDialogConstants.OK_ID) {
                isReconnect = true;
            } else if (btnPressed == IDialogConstants.CANCEL_ID) {
                Database database = terminal.getSelectedDatabase();
                DBConnProfCache.getInstance().destroyConnection(database);

                if (null != database) {
                    ConsoleCoreWindow.getInstance()
                            .logFatal(MessageConfigLoader.getProperty(IMessagesConstants.DISCONNECTED_FROM_SERVER,
                                    database.getServer().getServerConnectionInfo().getConectionName(),
                                    database.getName()));
                }
                ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                if (null != objectBrowserModel) {
                    objectBrowserModel.refreshObject(database);
                }
            }
            MPPDBIDELoggerUtility.error("VisualExplainPlan Failed");
        }

        /**
         * On operational exception UI action.
         *
         * @param dbOperationException the db operation exception
         */
        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            MPPDBIDELoggerUtility.error("VisualExplainPlan Failed");
        }

        /**
         * Final cleanup.
         */
        @Override
        public void finalCleanup() {
            if (isReconnect) {
                try {
                    termConnection.releaseConnection();
                    TerminalExecutionSQLConnectionInfra termConnection1 = (TerminalExecutionSQLConnectionInfra) PromptPrdGetConnection
                            .getConnection(terminal.getTermConnection());
                    termConnection.setConnection(termConnection1.getConnection());
                    conn = termConnection.getConnection();

                } catch (MPPDBIDEException exception) {
                    final String errMsg = exception.getServerMessage();
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                                    MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR),
                                    MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_DATABASE_CRITICAL_ERROR)
                                            + MPPDBIDEConstants.LINE_SEPARATOR + errMsg);
                        }
                    });
                    DBConnProfCache.getInstance().destroyConnection(terminal.getSelectedDatabase());

                    if (null != terminal.getSelectedDatabase()) {
                        ConsoleCoreWindow.getInstance()
                                .logFatal(MessageConfigLoader.getProperty(
                                        IMessagesConstants.DISCONNECTED_FROM_SERVER, terminal.getSelectedDatabase()
                                                .getServer().getServerConnectionInfo().getConectionName(),
                                        terminal.getSelectedDatabase().getName()));
                    }
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                            if (null != objectBrowserModel) {

                                objectBrowserModel.refreshObject(terminal.getSelectedDatabase());
                            }
                        }
                    });

                    termConnection.releaseSecureConnection(conn);
                    MPPDBIDELoggerUtility.error("Error while attempting to reconnect");
                    return;
                }

                this.termConnection.releaseSecureConnection(this.conn);
                this.schedule();
            } else {
                super.finalCleanup();
                this.termConnection.releaseSecureConnection(this.conn);
            }
            presentation = null;
            statMessage = null;
        }

        /**
         * Final cleanup UI.
         */
        @Override
        public void finalCleanupUI() {
            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (terminal != null) {
                terminal.resetCommitAndRollbackButton();
            }
            if (!isReconnect && bottomStatusBar != null) {
                bottomStatusBar.hideStatusbar(statMessage);
            }
            VisualExplainPlanHandler.setVisExplainJobRunning(false);
        }

        /**
         * Canceling.
         */
        @Override
        protected void canceling() {
            super.canceling();
        }

        /**
         * Pre UI setup.
         *
         * @param preHandlerObject the pre handler object
         * @return true, if successful
         */
        @Override
        public boolean preUISetup(Object preHandlerObject) {
            termConnection = terminal.getTermConnection();
            if (null == termConnection.getConnection()) {
                try {
                    termConnection = (TerminalExecutionSQLConnectionInfra) PromptPrdGetConnection
                            .getConnection(termConnection);
                    terminal.setAutoCommitStatus();
                } catch (MPPDBIDEException exception) {
                    onCriticalExceptionUIAction(
                            new DatabaseCriticalException(IMessagesConstants.DATABASE_CONNECTION_ERR));
                    finalCleanup();
                    return false;
                }
            }

            if (this.termConnection.getReuseConnectionFlag()) {
                return true;
            }
            return promptAndValidatePassword();
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
    }
}