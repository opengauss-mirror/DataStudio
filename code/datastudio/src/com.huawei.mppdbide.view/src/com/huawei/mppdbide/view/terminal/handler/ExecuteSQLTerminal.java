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

package com.huawei.mppdbide.view.terminal.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.presentation.ContextExecutionOperationType;
import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.sourceeditor.ExecuteSourceEditor;
import com.huawei.mppdbide.view.terminal.TerminalQueryExecutionWorker;
import com.huawei.mppdbide.view.terminal.executioncontext.SQLTerminalExecutionContext;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.ResultSetWindow;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecuteSQLTerminal.
 *
 * @since 3.0.0
 */
public class ExecuteSQLTerminal {
    @Inject
    private ECommandService commandService;
    @Inject
    private EHandlerService handlerService;

    /**
     * Execute.
     *
     * @param terminalID the terminal ID
     * @param newTab the new tab
     * @param shell the shell
     */
    /**
     * Updated for AR.Tools.IDE.030.002 This method will check for multiple
     * queries and execute them one by one
     * 
     * Updated for AR.Tools.IDE.030.006 Execution plan and costs for multiple
     * Query starts
     */

    @Execute
    public void execute(@Optional @Named("terminal.id") String terminalID, @Optional @Named("new.tab") String newTab,
            Shell shell) {
        if ("truekey".equals(newTab) && !UserPreference.getInstance().isGenerateNewResultWindow()) {
            return;
        }
        MPPDBIDELoggerUtility
                .info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_EXECUTED_SQL_TERMINAL_STATEMENT));

        SQLTerminal terminal = UIElement.getInstance().getTerminal(terminalID);
        if (UIElement.getInstance().isSqlTerminalOnTop() && terminal == null) {
            terminal = UIElement.getInstance().getSqlTerminalModel();
        }
        // Terminal closed, so nothing to execute.
        boolean editorOnTopById = UIElement.getInstance().isEditorOnTopById();
        if (null == terminal && !UIElement.getInstance().isNewEditorOnTop() && !editorOnTopById) {
            return;
        }

        if (null == terminal && editorOnTopById) {
            Command command = commandService.getCommand("com.huawei.mppdbide.command.id.compilefunctionfromtoolbar");
            ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, null);
            handlerService.executeHandler(parameterizedCommand);
        } else {
            IExecutionContext context = generateContext(shell, terminal);

            if (terminal != null) {
                boolean isAdditionalInitOK = additionalInitForTerminal(terminal, context);
                if (!isAdditionalInitOK) {
                    return;
                }
            }
            if ("true".equals(newTab) || "truekey".equals(newTab)) {
                ResultSetWindow.setOpenNewTAb(true);
            } else {
                ResultSetWindow.setOpenNewTAb(false);
            }
            if (terminal != null && terminal.getTerminalResultManager() != null) {
                terminal.getTerminalResultManager().preResultTabGeneration();
            }
            TerminalQueryExecutionWorker worker = new TerminalQueryExecutionWorker(context);
            worker.setTerminal(terminal);
            worker.setTaskDB(context.getTermConnection().getDatabase());
            worker.schedule();
        }
    }

    /**
     * Additional init for terminal.
     *
     * @param terminal the terminal
     * @param context1 the context 1
     * @return true, if successful
     */
    public static boolean additionalInitForTerminal(SQLTerminal terminal, IExecutionContext context1) {
        if (terminal.isExecuteInProgress()) {
            return false;
        }

        terminal.setExecuteInProgress(true);

        return true;

    }

    /**
     * Generate context.
     *
     * @param shell the shell
     * @param terminal the terminal
     * @return the i execution context
     */
    public IExecutionContext generateContext(Shell shell, SQLTerminal terminal) {
        PLSourceEditor plSourceEditor = null;
        ExecuteSourceEditor executeSourceEditor = null;
        if (UIElement.getInstance().isNewEditorOnTop()) {
            plSourceEditor = UIElement.getInstance().getEditorOnTop();
            if (null != plSourceEditor) {
                executeSourceEditor = new ExecuteSourceEditor(shell, plSourceEditor);
                return executeSourceEditor.getContextForNewEditor();
            }
        } else if (UIElement.getInstance().isEditorOnTopById()) {
            plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
            if (null != plSourceEditor) {
                executeSourceEditor = new ExecuteSourceEditor(shell, plSourceEditor);
                return executeSourceEditor.getContextForExitsingEditor();
            }
        } else if (terminal != null) {
            SQLTerminalExecutionContext execContext;
            execContext = new SQLTerminalExecutionContext(shell, terminal);
            execContext.init();
            execContext
                    .setCurrentExecution(ContextExecutionOperationType.CONTEXT_OPERATION_TYPE_SQL_TERMINAL_EXECUTION);
            terminal.setExecutionContext(execContext);
            return execContext;
        }

        return null;
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute(@Optional @Named("new.tab") String newTab) {
        UIElement uiEle = UIElement.getInstance();
        if (uiEle == null) {
            return false;
        }
        SQLTerminal terminal = uiEle.getSqlTerminalModel();
        PLSourceEditor editorOnTop = uiEle.getEditorOnTopExisting(false);
        if (editorOnTop == null) {
            editorOnTop = uiEle.getEditorOnTopExisting(true);
        }
        boolean isDdlOperationSupported = editorOnTop != null;
        boolean editorConstraints = uiEle.isNewEditorOnTop() || uiEle.isEditorOnTopById();
        if (newTab != null && "true".equals(newTab)) {

            // EnableDisable of New Tab Execution
            return null != terminal && terminal.isSqlTerminalNewTabExecuteBtnEnabled();
        }
        return null != terminal && !"".equals(terminal.getDocumentContent().trim()) && terminal.isButtonEnabled()
                || (editorConstraints && isDdlOperationSupported);
    }

}
