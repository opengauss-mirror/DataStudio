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
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.presentation.IResultDisplayUIManager;
import org.opengauss.mppdbide.presentation.resultsetif.IResultConfig;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.view.terminal.queryexecution.SqlQueryExecutionWorkingContext;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLTerminalExecutionContext.
 *
 * @since 3.0.0
 */
public class SQLTerminalExecutionContext extends AbstractTerminalExecutionContext {
    private Shell shell;
    private String selectedTextInTerminal;
    private int selectionStartOffset;
    private SQLTerminal terminal;

    /**
     * Instantiates a new SQL terminal execution context.
     *
     * @param parentShell the parent shell
     * @param terminal the terminal
     */
    public SQLTerminalExecutionContext(Shell parentShell, SQLTerminal terminal) {
        super(terminal.getTermConnection());
        this.shell = parentShell;
        this.terminal = terminal;
    }

    /**
     * Gets the context name.
     *
     * @return the context name
     */
    @Override
    public String getContextName() {
        return ProgressBarLabelFormatter.getProgressLabelForSchema(terminal.getPartLabel(),
                terminal.getSelectedDatabase().getName(), terminal.getSelectedDatabase().getServerName(),
                IMessagesConstants.SQL_QUERY_EXECUTE);
    }

    /**
     * Gets the result config.
     *
     * @return the result config
     */
    @Override
    public IResultConfig getResultConfig() {
        return this.terminal.getResultConfig();
    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    @Override
    public IResultDisplayUIManager getResultDisplayUIManager() {
        return this.terminal.getResultDisplayUIManager();
    }

    /**
     * Sets the job done.
     */
    @Override
    public void setJobDone() {
        this.terminal.setExecuteInProgress(false);
    }

    /**
     * Gets the terminal.
     *
     * @return the terminal
     */
    public SQLTerminal getTerminal() {
        return terminal;
    }

    /**
     * Sets the terminal.
     *
     * @param terminal the new terminal
     */
    public void setTerminal(SQLTerminal terminal) {
        this.terminal = terminal;
    }

    /**
     * Inits the.
     */
    public void init() {
        setNoticeMessageQueue(terminal.getConsoleMessageWindow(true).getMsgQueue());

        int selectedTextLength = 0;
        ITextSelection textSel = null;

        textSel = (ITextSelection) terminal.getTerminalCore().getSourceViewer().getSelectionProvider().getSelection();
        selectedTextInTerminal = null != textSel.getText() ? textSel.getText() : "";

        selectedTextLength = selectedTextInTerminal.length();

        if ("".equals(selectedTextInTerminal)) {
            this.selectionStartOffset = 0;
        } else {
            this.selectionStartOffset = textSel.getOffset();
        }

        if (selectedTextInTerminal.isEmpty()) {
            terminal.removeAllErrors();
        } else {
            terminal.removeErrorsInSelectedRange(selectionStartOffset, selectedTextLength, true, true);
        }

        if (null != terminal.getSelectedDocumentContent() && 0 < terminal.getSelectedDocumentContent().length()) {
            setQuery(terminal.getSelectedDocumentContent());
        } else {
            setQuery(terminal.getDocumentContent());
        }
        if ("".equals(selectedTextInTerminal) && terminal.getDocumentContent().length() > 0) {
            getResultDisplayUIManager().setCursorOffset(textSel.getOffset());
        } else {
            getResultDisplayUIManager().setCursorOffset(-1);
        }
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
     * Gets the current execution selected text.
     *
     * @return the current execution selected text
     */
    public String getCurrentExecutionSelectedText() {
        return this.selectedTextInTerminal;
    }

    /**
     * Gets the selection start offset.
     *
     * @return the selection start offset
     */
    public int getselectionStartOffset() {
        return selectionStartOffset;
    }

    /**
     * Sets the selection start offset.
     *
     * @param qryStartOffset the new selection start offset
     */
    public void setselectionStartOffset(int qryStartOffset) {
        selectionStartOffset = qryStartOffset;
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
     * Job type.
     *
     * @return the string
     */
    @Override
    public String jobType() {

        return MPPDBIDEConstants.CANCELABLEJOB;
    }

    /**
     * Hide exec progres bar.
     */
    @Override
    public void hideExecProgresBar() {
        if (null != this.terminal) {
            this.terminal.hideProgressBar();
        }
    }

    /**
     * Show exec progres bar.
     */
    @Override
    public void showExecProgresBar() {
        if (null != this.terminal) {
            this.terminal.showProgressBar();
        }
    }

    /**
     * gets the input values
     */
    @Override
    public ArrayList<DefaultParameter> getInputValues() {
        return this.terminal.getInputDailogValueTerminal();
    }

}
