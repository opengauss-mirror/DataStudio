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

package org.opengauss.mppdbide.view.terminal.executioncontext;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.presentation.IResultDisplayUIManager;
import org.opengauss.mppdbide.presentation.resultsetif.IResultConfig;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.view.terminal.queryexecution.SqlQueryExecutionWorkingContext;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;

/**
 * 
 * Title: class
 * 
 * Description: The Class FuncProcEditorTerminalExecutionContext.
 *
 * @since 3.0.0
 */
public class FuncProcEditorTerminalExecutionContext extends AbstractTerminalExecutionContext {
    private PLSourceEditor plSourceEditor;
    private Shell shell;
    private String selectedTextInTerminal;
    private int selectionStartOffset;
    private IDebugObject debugObject;

    /**
     * Instantiates a new func proc editor terminal execution context.
     *
     * @param parentshell the parentshell
     * @param plSourceEditor2 the pl source editor 2
     */
    public FuncProcEditorTerminalExecutionContext(Shell parentshell, PLSourceEditor plSourceEditor2) {
        super(plSourceEditor2.getTermConnection());
        this.shell = parentshell;
        this.plSourceEditor = plSourceEditor2;
        this.plSourceEditor.getTermConnection().setAutoCommitFlag(true);
    }

    /**
     * Gets the context name.
     *
     * @return the context name
     */
    @Override
    public String getContextName() {
        return ProgressBarLabelFormatter.getProgressLabelForSchema(plSourceEditor.getUiID(),
                plSourceEditor.getFunctionSelectedDatabase().getDbName(),
                plSourceEditor.getFunctionSelectedDatabase().getServerName(), IMessagesConstants.SQL_QUERY_EXECUTE);
    }

    /**
     * Gets the result config.
     *
     * @return the result config
     */
    @Override
    public IResultConfig getResultConfig() {
        return plSourceEditor.getResultConfig();
    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    @Override
    public IResultDisplayUIManager getResultDisplayUIManager() {
        return plSourceEditor.getResultDisplayUIManager();
    }

    /**
     * Sets the job done.
     */
    @Override
    public void setJobDone() {
        plSourceEditor.setCompileInProgress(false);
        plSourceEditor.setExecuteInProgress(false);
    }

    /**
     * Inits the.
     */
    public void init() {

        setNoticeMessageQueue(plSourceEditor.getConsoleMessageWindow(true).getMsgQueue());

        int selectedTextLength = 0;
        ITextSelection textSel = null;

        textSel = (ITextSelection) plSourceEditor.getSourceEditorCore().getSourceViewer().getSelectionProvider()
                .getSelection();
        selectedTextInTerminal = null != textSel.getText() ? textSel.getText() : "";

        selectedTextLength = selectedTextInTerminal.length();

        if ("".equals(selectedTextInTerminal)) {
            this.selectionStartOffset = 0;
        } else {
            this.selectionStartOffset = textSel.getOffset();
        }

        if (selectedTextInTerminal.isEmpty()) {
            plSourceEditor.removeFunctAllErrors();
        } else {
            plSourceEditor.removeFunctErrorsInSelectedRange(selectionStartOffset, selectedTextLength, true, true);
        }
        if (null != plSourceEditor.getSelectedDocumentContent()
                && 0 < plSourceEditor.getSelectedDocumentContent().length()) {
            setQuery(plSourceEditor.getSelectedDocumentContent());
            SourceViewer sourceViewer = plSourceEditor.getTerminalCore().getSourceViewer();
            StyledText styledText = sourceViewer.getTextWidget();
            int caretOffset = styledText.getCaretOffset();
            plSourceEditor.getTerminalCore().gotoLine(styledText.getLineAtOffset(caretOffset) + 1,
                    this.plSourceEditor.getFunctionSelectedDatabase());

        } else {
            setQuery(plSourceEditor.getFunctionDocumentContent());
        }

    }

    /**
     * Job type.
     *
     * @return the string
     */
    @Override
    public String jobType() {

        return "PLSource viewer Job";
    }

    /**
     * Gets the shell.
     *
     * @return the shell
     */
    public Shell getShell() {
        return shell;
    }

    /**
     * Gets the current query in execution.
     *
     * @return the current query in execution
     */
    public String getCurrentQueryInExecution() {
        SqlQueryExecutionWorkingContext context = (SqlQueryExecutionWorkingContext) this.getWorkingJobContext();
        return context.getCurrentQuery();
    }

    /**
     * Gets the current funct execution selected text.
     *
     * @return the current funct execution selected text
     */
    public String getCurrentFunctExecutionSelectedText() {
        return this.selectedTextInTerminal;
    }

    /**
     * Gets the functselection start offset.
     *
     * @return the functselection start offset
     */
    public int getFunctselectionStartOffset() {
        return selectionStartOffset;
    }

    /**
     * Sets the functselection start offset.
     *
     * @param qryStartOffset the new functselection start offset
     */
    public void setFunctselectionStartOffset(int qryStartOffset) {
        selectionStartOffset = qryStartOffset;
    }

    /**
     * Hide exec progres bar.
     */
    @Override
    public void hideExecProgresBar() {
        if (null != this.plSourceEditor) {
            this.plSourceEditor.hideProgressBar();
        }
    }

    /**
     * Show exec progres bar.
     */
    @Override
    public void showExecProgresBar() {
        if (null != this.plSourceEditor) {
            this.plSourceEditor.showProgressBar();
        }
    }

    /**
     * Gets the debug object.
     *
     * @return the debug object
     */
    public IDebugObject getDebugObject() {
        return debugObject;
    }

    /**
     * Sets the debug object.
     *
     * @param debugObject the new debug object
     */
    public void setDebugObject(IDebugObject debugObject) {
        this.debugObject = debugObject;
    }

    /**
     * gets the input values
     */
    @Override
    public ArrayList<DefaultParameter> getInputValues() {
        return null;
    }

}
