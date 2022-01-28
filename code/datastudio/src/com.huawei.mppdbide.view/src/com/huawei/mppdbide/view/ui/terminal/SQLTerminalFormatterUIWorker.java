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

import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.gauss.sqlparser.comm.SQLFoldingRuleManager;
import com.huawei.mppdbide.gauss.sqlparser.comm.SQLFormatEditorParser;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.prefernces.DSFormatterPreferencePage;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * Title: class Description: The Class SQLTerminalFormatterUIWorker.
 *
 * @since 3.0.0
 */
public class SQLTerminalFormatterUIWorker extends UIWorkerJob {
    private static final String UNEXPECTED_INTERRUPT_EXCEPTION_FOUND = "Unexpected interrupt exception found.";

    private PLSourceEditorCore sourceViewer;

    private StringBuilder stringToReplace;

    private String selectedQuery;

    private int startOffset;

    private int endOffset;

    private String formattedQuery;

    private int formatterOffset;

    private boolean prevTermEditFlag;

    private boolean canReschedule = false;

    private boolean discardFormattingOutput = false;

    /**
     * Instantiates a new SQL terminal formatter UI worker.
     *
     * @param sourceViewer1 the source viewer 1
     * @param jobTitle the job title
     */
    public SQLTerminalFormatterUIWorker(PLSourceEditorCore sourceViewer1, String jobTitle) {

        super(jobTitle, MPPDBIDEConstants.CANCELABLEJOB);
        this.sourceViewer = sourceViewer1;

        String documentcontent = sourceViewer.getDocument().get();
        stringToReplace = new StringBuilder(documentcontent);

        selectedQuery = sourceViewer.getSelectedQry();
        if (sourceViewer.getSelectionCount() > 0) {
            // Get the start and end point of the particular text selected
            int[] ranges = sourceViewer.getSourceViewer().getTextWidget().getSelectionRanges();
            startOffset = ranges[0];

            endOffset = ranges[ranges.length - 2] + ranges[ranges.length - 1];
        } else {
            startOffset = 0;
            endOffset = null != selectedQuery ? selectedQuery.length() : 0;
        }

        if (selectedQuery != null && !selectedQuery.isEmpty()) {
            int lineAtOffset = sourceViewer.getSourceViewer().getTextWidget().getLineAtOffset(startOffset);
            int offsetAtLine = sourceViewer.getSourceViewer().getTextWidget().getOffsetAtLine(lineAtOffset);

            /*
             * Convert tabs to spaces before deciding what is the actual offset.
             * hence a plan formatterOffset = startOffset - offsetAtLine wont be
             * sufficient, as tabs will be counted as 1 char
             */
            formatterOffset = stringToReplace.subSequence(offsetAtLine, startOffset).toString()
                    .replace("\t", DSFormatterPreferencePage.getStringWithSpaces()).length();
        }

        prevTermEditFlag = sourceViewer.isEditable();
        sourceViewer.setEditable(false);

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
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        selectedQuery = selectedQuery.trim();
        if (selectedQuery.length() == 0) {
            return null;
        }

        SQLFormatEditorParser lSQLEditorParser = new SQLFormatEditorParser();

        lSQLEditorParser.setDocument(sourceViewer.getDocument());

        SQLFoldingRuleManager lSQLRuleManager = new SQLFoldingRuleManager();

        lSQLRuleManager.refreshRules();

        lSQLEditorParser.setRuleManager(lSQLRuleManager);

        try {
            formattedQuery = lSQLEditorParser.parseSQLDocuement(PreferenceWrapper.getInstance().getPreferenceStore(),
                    startOffset, endOffset, formatterOffset);
        } catch (GaussDBSQLParserException excep) {
            formattedQuery = null;
            MPPDBIDELoggerUtility.error("Error occurred while parsing SQL Document");
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    generateUnableToParseDialog();
                }
            });
        } catch (Exception exception) {
            onCancelInterruptParseHandle(exception);
        }

        return null;
    }

    private void generateUnableToParseDialog() {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_NODE),
                MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_UNABLE_TO_PARSE_STMT));
    }

    private void onCancelInterruptParseHandle(Exception exception) throws DatabaseOperationException {
        if (this.isCancel()) {
            formattedQuery = null;
            MPPDBIDELoggerUtility.error("Formatting cancelled by user.", exception);
        } else {
            MPPDBIDELoggerUtility.error(UNEXPECTED_INTERRUPT_EXCEPTION_FOUND);
            throw new DatabaseOperationException(UNEXPECTED_INTERRUPT_EXCEPTION_FOUND);
        }
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException e) {

    }

    /**
     * On operational exception UI action.
     *
     * @param dbOperationException the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
        if (dbOperationException.getMessage().equals(UNEXPECTED_INTERRUPT_EXCEPTION_FOUND)) {
            canReschedule = true;
        }

    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        if (canReschedule) {
            MPPDBIDELoggerUtility.info("The formatting job is going to get rescheduled.");
            this.schedule();
        } else {

        }

    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        if (discardFormattingOutput) {
            return;
        }

        if ((sourceViewer != null) && (!sourceViewer.getSourceViewer().getTextWidget().isDisposed())) {
            sourceViewer.setEditable(prevTermEditFlag);
            if (selectedQuery.length() == 0 || formattedQuery == null || formattedQuery.trim().length() == 0) {
                return;
            }
            stringToReplace.replace(startOffset, endOffset, formattedQuery);
            sourceViewer.getDocument().set(stringToReplace.toString());
            sourceViewer.getSourceViewer().getTextWidget().setSelectionRange(startOffset, formattedQuery.length());
            sourceViewer.getSourceViewer().revealRange(startOffset, endOffset - startOffset);
        }

    }

    /**
     * Cancel job.
     *
     * @return true, if successful
     */
    @Override
    public boolean cancelJob() {
        discardFormattingOutput = true;
        if ((sourceViewer != null && sourceViewer.getSourceViewer() != null
                && sourceViewer.getSourceViewer().getTextWidget() != null)
                && (!sourceViewer.getSourceViewer().getTextWidget().isDisposed())) {
            this.sourceViewer.setEditable(prevTermEditFlag);
        }
        boolean ret = super.cancelJob();
        this.getThread().interrupt();
        return ret;
    }

}
