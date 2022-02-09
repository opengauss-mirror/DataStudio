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

package org.opengauss.mppdbide.view.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.SQLTerminalQuerySplit;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.data.DSViewDataManager;
import org.opengauss.mppdbide.view.terminal.ExecutionPlanWorker;
import org.opengauss.mppdbide.view.terminal.executioncontext.ExecutionExplainPlanContext;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.consts.WHICHOPTION;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogsWithDoNotShowAgain;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecutionPlanHandler.
 *
 * @since 3.0.0
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
