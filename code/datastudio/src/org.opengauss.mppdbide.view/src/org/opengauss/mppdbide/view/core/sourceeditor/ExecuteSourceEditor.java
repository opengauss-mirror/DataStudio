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

package org.opengauss.mppdbide.view.core.sourceeditor;

import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.presentation.ContextExecutionOperationType;
import org.opengauss.mppdbide.view.terminal.executioncontext.FuncProcEditorTerminalExecutionContext;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecuteSourceEditor.
 *
 * @since 3.0.0
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
