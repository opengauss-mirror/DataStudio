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

package com.huawei.mppdbide.view.handler;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.ObjectChange;
import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.ConsoleCoreWindow;
import com.huawei.mppdbide.view.core.sourceeditor.ExecuteSourceEditor;
import com.huawei.mppdbide.view.functionchange.ExecuteWrapper;
import com.huawei.mppdbide.view.functionchange.ObjectChangeEvent;
import com.huawei.mppdbide.view.functionchange.ObjectChangeEvent.ButtonPressed;
import com.huawei.mppdbide.view.functionchange.ObjectChangeWorker;
import com.huawei.mppdbide.view.terminal.TerminalQueryExecutionWorker;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.FunctionProcNameParser;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class CompileFunction.
 *
 * @since 3.0.0
 */
public class CompileFunction implements ExecuteWrapper {

    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;

    private IDebugObject debugObject;

    private PLSourceEditor plSourceEditor;

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        MPPDBIDELoggerUtility.info("Compiling PLSQL ..");
        if (UIElement.getInstance().isNewEditorOnTop()) {
            plSourceEditor = UIElement.getInstance().getEditorOnTop();
            if (plSourceEditor != null) {
                plSourceEditor.setCompileInProgress(true);
            }
            
            Command command = commandService
                    .getCommand("com.huawei.mppdbide.command.id.executeobjectbrowseritemfromtoolbar");
            ParameterizedCommand parameterizedCommand = new ParameterizedCommand(command, null);
            handlerService.executeHandler(parameterizedCommand);
        } else {
            plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
            if (plSourceEditor != null) {
                debugObject = plSourceEditor.getDebugObject();
                if (debugObject == null) {
                    return;
                }
                ObjectChangeWorker<ObjectChange> objWorker = new ObjectChangeWorker<ObjectChange>(
                        "Function Change Worker", null, debugObject, plSourceEditor, this,
                        IMessagesConstants.FUNCTN_CHANGE_MSG, IMessagesConstants.FUNCTN_CHANGE_OVERWRITE);
                objWorker.schedule();
            }
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        UIElement uiEle = UIElement.getInstance();
        PLSourceEditor ed = uiEle.getEditorOnTopExisting(false);
        if (ed == null) {
            ed = uiEle.getEditorOnTopExisting(true);
        }
        if (ed != null) {
            Database db = ed.getTermConnection().getDatabase();

            if (ed.isExecuteInProgress() ) {
                return false;
            }
            return (uiEle.isNewEditorOnTop() || uiEle.isEditorOnTopById()) && db != null && db.isConnected()
                    && !ed.isCompileInProgress();
        }
        return false;
    }

    /**
     * Handle execute.
     *
     * @param event the event
     */
    @Override
    public void handleExecute(ObjectChangeEvent event) {
        PLSourceEditor editor = event.getEditor();

        editor.setExecuteInProgress(false);
        editor.setCompileInProgress(true);
        editor.getSourceEditorCore().getSourceViewer().getTextWidget().setEnabled(false);

        if (event.getStatus() != null && event.getStatus().equals(ButtonPressed.REFRESH)) {
            event.getEditor().getSourceEditorCore()
                    .setDocument(new Document(event.getDbgObj().getSourceCode().getCode()), 0);
            event.getEditor().registerModifyListener();
        }

        IExecutionContext context = generateContext(Display.getDefault().getActiveShell(), editor);
        TerminalQueryExecutionWorker worker = new TerminalQueryExecutionWorker(context);
        worker.setTaskDB(context.getTermConnection().getDatabase());

        worker.schedule();
    }

    /**
     * Generate context.
     *
     * @param shell the shell
     * @param editor the editor
     * @return the i execution context
     */
    private IExecutionContext generateContext(Shell shell, PLSourceEditor editor) {
        ExecuteSourceEditor executeSourceEditor = null;
        executeSourceEditor = new ExecuteSourceEditor(shell, editor);

        String code1 = debugObject.getSourceCode().getCode();
        String code2 = editor.getFunctionDocumentContent();
        FunctionProcNameParser parser1 = new FunctionProcNameParser(code1);
        FunctionProcNameParser parser2 = new FunctionProcNameParser(code2);
        parser1.doParse();
        parser2.doParse();

        List<String[]> args1 = parser1.getArgs();
        List<String[]> args2 = parser2.getArgs();

        Boolean argsnotchange = true;
        if (args1.size() != args2.size()) {
            argsnotchange = false;
        } else {
            for (int i = 0; i < args1.size() && argsnotchange; i++) {
                for (int j = 0; j < 3; j++) {
                    if (args1.get(i)[j] != null && args2.get(i)[j] != null) {
                        argsnotchange = args1.get(i)[j].equals(args2.get(i)[j]);
                    } else if (args1.get(i)[j] != null && args2.get(i)[j] == null) {
                        argsnotchange = false;
                    } else if (args1.get(i)[j] == null && args2.get(i)[j] != null) {
                        argsnotchange = false;
                    }

                    if (!argsnotchange) {
                        break;
                    }
                }
            }
        }

        if (isGetContextForExitsingEditor(parser1, parser2, argsnotchange)) {
            return executeSourceEditor.getContextForExitsingEditor();
        } else {
            return executeSourceEditor.getContextForNewEditor();
        }
    }

    /**
     * Checks if is gets the context for exitsing editor.
     *
     * @param parser1 the parser 1
     * @param parser2 the parser 2
     * @param argsnotchange the argsnotchange
     * @return true, if is gets the context for exitsing editor
     */
    private boolean isGetContextForExitsingEditor(FunctionProcNameParser parser1, FunctionProcNameParser parser2,
            Boolean argsnotchange) {
        return null != parser1.getObjectName() && null != parser2.getObjectName()
                && parser1.getObjectName().replace("\"", "").equals(parser2.getObjectName().replace("\"", ""))
                && argsnotchange;
    }

    /**
     * Handle exception.
     *
     * @param e the e
     * @param event the event
     */
    @Override
    public void handleException(Throwable exception, ObjectChangeEvent event) {
        PLSourceEditor sourceEditor = event.getEditor();
        String msg = exception.getMessage();
        if (msg.contains(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE))) {
            int result = MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.PL_SOURCE_VIEWER_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.FUNCT_CHANGE_COMPILE_ERR, msg,
                            MessageConfigLoader.getProperty(IMessagesConstants.FUNCT_CHANGE_ERR_COMPILE_BACKEND)));
            if (0 == result) {

                if (sourceEditor != null) {
                    ExecuteSourceEditor executeSourceEditor = null;
                    executeSourceEditor = new ExecuteSourceEditor(Display.getDefault().getActiveShell(), sourceEditor);
                    IExecutionContext context = executeSourceEditor.getContextForNewEditor();
                    TerminalQueryExecutionWorker worker = new TerminalQueryExecutionWorker(context);
                    worker.setTaskDB(context.getTermConnection().getDatabase());
                    worker.schedule();
                } else {
                    PLSourceEditor srcEditor = event.getEditor();
                    if (srcEditor != null) {
                        srcEditor.setCompileInProgress(false);
                        plSourceEditor.getSourceEditorCore().getSourceViewer().getTextWidget().setEnabled(true);
                    }
                }
            } else {
                sourceEditor.setCompileInProgress(false);
                plSourceEditor.getSourceEditorCore().getSourceViewer().getTextWidget().setEnabled(true);
            }

        } else {
            sourceEditor.setCompileInProgress(false);
            plSourceEditor.getSourceEditorCore().getSourceViewer().getTextWidget().setEnabled(true);
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.PL_SOURCE_VIEWER_ERROR), msg);
            ConsoleCoreWindow.getInstance()
                    .logWarning(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE));
        }
        MPPDBIDELoggerUtility.error("CompileFunction: handle exception flow.", exception);
    }
}
