/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.presentation.ContextExecutionOperationType;
import com.huawei.mppdbide.view.terminal.executioncontext.FuncProcEditorTerminalExecutionContext;
import com.huawei.mppdbide.view.ui.PLSourceEditor;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecuteSourceEditor.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExecuteSourceEditor {

    /**
     * The exec context.
     */
    FuncProcEditorTerminalExecutionContext execContext;

    /**
     * The shell.
     */
    Shell shell;

    /**
     * The pl source editor.
     */
    PLSourceEditor plSourceEditor;

    /**
     * Instantiates a new execute source editor.
     *
     * @param shell the shell
     * @param plSourceEditor the pl source editor
     */
    public ExecuteSourceEditor(Shell shell, PLSourceEditor plSourceEditor) {
        this.shell = shell;
        this.plSourceEditor = plSourceEditor;
    }

    /**
     * Gets the context for new editor.
     *
     * @return the context for new editor
     */
    public FuncProcEditorTerminalExecutionContext getContextForNewEditor() {
        execContext = new FuncProcEditorTerminalExecutionContext(shell, plSourceEditor);
        execContext.init();
        execContext.setCurrentExecution(ContextExecutionOperationType.CONTEXT_OPERATION_TYPE_NEW_PL_SQL_CREATION);
        plSourceEditor.setExecutionContext(execContext);
        return execContext;
    }

    /**
     * Gets the context for exitsing editor.
     *
     * @return the context for exitsing editor
     */
    public FuncProcEditorTerminalExecutionContext getContextForExitsingEditor() {
        execContext = new FuncProcEditorTerminalExecutionContext(shell, plSourceEditor);
        execContext.init();
        execContext.setCurrentExecution(ContextExecutionOperationType.CONTEXT_OPERATION_TYPE_PL_SQL_COMPILATION);
        plSourceEditor.setExecutionContext(execContext);
        return execContext;
    }
}
