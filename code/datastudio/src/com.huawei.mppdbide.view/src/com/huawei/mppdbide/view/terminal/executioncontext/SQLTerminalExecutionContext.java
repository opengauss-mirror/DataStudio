/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.terminal.executioncontext;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.presentation.IResultDisplayUIManager;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.terminal.queryexecution.SqlQueryExecutionWorkingContext;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLTerminalExecutionContext.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
