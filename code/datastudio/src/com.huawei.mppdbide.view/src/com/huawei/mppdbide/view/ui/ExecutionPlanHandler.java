/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.SQLTerminalQuerySplit;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.data.DSViewDataManager;
import com.huawei.mppdbide.view.terminal.ExecutionPlanWorker;
import com.huawei.mppdbide.view.terminal.executioncontext.ExecutionExplainPlanContext;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.consts.WHICHOPTION;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogsWithDoNotShowAgain;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecutionPlanHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExecutionPlanHandler {

    private String query;
    private Database db;

    private Job job;
    private StatusMessage statusMessage;

    /**
     * Execute.
     *
     * @param terminal the terminal
     * @param analyzeFlag the analyze flag
     */
    public void execute(SQLTerminal terminal, boolean analyzeFlag) {
        if (analyzeFlag) {
            if (!DSViewDataManager.getInstance().isShowExplainPlanWarningsAnalyzeQuery()) {
                int res = MPPDBIDEDialogsWithDoNotShowAgain.generateYesNoMessageDialog(
                        WHICHOPTION.ANALYZE_QUERY_EXECUTION, MESSAGEDIALOGTYPE.WARNING, getWindowImage(), true,
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPLAIN_ANALYZE_WINDOW_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPLAIN_ANALYZE_WARNING_MSG));
                if (UIConstants.OK_ID != res) {
                    terminal.resetExplainPlanInProgress();
                    return;
                }
            }
        }

        try {
            runPlanQuery(terminal, analyzeFlag);
        } catch (MPPDBIDEException exception) {
            return;
        }
    }

    private void runPlanQuery(SQLTerminal terminal, boolean analyzeFlag) throws MPPDBIDEException {
        getSelectedQry(terminal);

        final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();

        if (null == bottomStatusBar) {
            MPPDBIDELoggerUtility.error("Bottom statusbar error");
            return;
        }

        final ArrayList<String> queryArray = splitQueries(bottomStatusBar);

        ExecutionExplainPlanContext context = getSingleQueryIfNothingSelected(terminal, queryArray);

        /*
         * discard empty lines and comments. This fix needs to be in query
         * splitter function. To DO: Analyze the impact on content assistant and
         * then fix in splitter function.
         */
        ArrayList<String> actualQueryArray = seperateInValidQueryLines(queryArray);

        if (actualQueryArray.size() > 1) {
            if (!DSViewDataManager.getInstance().isShowExplainPlanWarningsMultipleQuery()) {
                int res = MPPDBIDEDialogsWithDoNotShowAgain.generateYesNoMessageDialog(WHICHOPTION.MULTIPLE_QUERY,
                        MESSAGEDIALOGTYPE.WARNING, getWindowImage(), true,
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPLAIN_PLAN_MORE_THAN_ONE_QUERY_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPLAIN_PLAN_MORE_THAN_ONE_QUERY_MSG));
                if (UIConstants.OK_ID != res) {
                    terminal.resetExplainPlanInProgress();
                    return;
                }
            }
        }

        context.setAnalyze(analyzeFlag);
        context.setBottomStatusBar(bottomStatusBar);
        terminal.getTerminalResultManager().preResultTabGeneration();
        job = new ExecutionPlanWorker(actualQueryArray, context);
        ((UIWorkerJob) job).setTaskDB(db);

        StatusMessage statMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_EXECUTE_STATUSBAR));
        setStatusMessage(statMessage);
        StatusMessageList.getInstance().push(statMessage);
        bottomStatusBar.activateStatusbar();
        job.schedule();
    }

    private ArrayList<String> seperateInValidQueryLines(final ArrayList<String> queryArray) {
        ArrayList<String> actualQueryArray = new ArrayList<String>(2);
        for (String qry : queryArray) {
            if (qry.trim().length() > 1) {
                actualQueryArray.add(qry.trim());
            }
        }
        return actualQueryArray;
    }

    private ExecutionExplainPlanContext getSingleQueryIfNothingSelected(SQLTerminal terminal,
            final ArrayList<String> queryArray) {
        ExecutionExplainPlanContext context = new ExecutionExplainPlanContext(Display.getDefault().getActiveShell(),
                terminal);

        /*
         * if nothing is selected, get the query where cursor is currently
         */
        if (terminal.getSelectedDocumentContent() != null && terminal.getSelectedDocumentContent().trim().isEmpty()) {
            context.getResultDisplayUIManager().getSingleQueryArray(queryArray, query);
        }
        return context;
    }

    private ArrayList<String> splitQueries(final BottomStatusBar bottomStatusBar) throws MPPDBIDEException {
        final ArrayList<String> queryArray = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        SQLTerminalQuerySplit querySplitter = new SQLTerminalQuerySplit();
        try {
            querySplitter.splitQuerries(queryArray, query, true);
        } catch (DatabaseOperationException e1) {
            bottomStatusBar.hideStatusbar(getStatusMessage());
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXE_PLAN_QUERY_PARSE_FAILED));
            throw new MPPDBIDEException(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXE_PLAN_QUERY_PARSE_FAILED));
        }
        return queryArray;
    }

    private void getSelectedQry(SQLTerminal terminal) {
        /*
         * If any query is selected in the SQL terminal window, then get only
         * the selected query part Otherwise get the entire content
         */
        if (null != terminal.getSelectedDocumentContent() && 0 < terminal.getSelectedDocumentContent().length()) {
            query = terminal.getSelectedDocumentContent();
            db = terminal.getSelectedDatabase();
        } else {
            query = terminal.getDocumentContent();
            db = terminal.getSelectedDatabase();
        }
    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMessage = statMessage;
    }

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected Image getWindowImage() {

        return IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass());
    }

}
