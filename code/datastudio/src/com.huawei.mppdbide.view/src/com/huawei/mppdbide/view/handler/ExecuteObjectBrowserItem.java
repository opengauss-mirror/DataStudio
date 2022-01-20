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

import java.util.ArrayList;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DatabaseUtils;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.ObjectParameter;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.core.ExcuteSQLObjectTable;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.uidisplay.UIDisplayUtil;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecuteObjectBrowserItem.
 *
 * @since 3.0.0
 */
public class ExecuteObjectBrowserItem {

    /**
     * Instantiates a new execute object browser item.
     */
    public ExecuteObjectBrowserItem() {
    }

    /**
     * Instantiates a new execute object browser item.
     *
     * @param viewer the viewer
     */
    public ExecuteObjectBrowserItem(TreeViewer viewer) {
    }

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        MPPDBIDELoggerUtility.info(MessageConfigLoader
                .getProperty(IMessagesConstants.GUI_EXECUTEOBJECTBROWSERITEM_EXECUTE_OBJECT_BROWSER_CLICKED));

        ExcuteSQLObjectTable executeSQLObjectWindowCore = new ExcuteSQLObjectTable(
                Display.getDefault().getActiveShell());
        IDebugObject debugObject = IHandlerUtilities.getSelectedDebugObject();
        // Will not be null, because Menu will be enabled only when object
        // exists.
        PLSourceEditor editor = null;
        if (debugObject == null) {
            return;
        }
        executeSQLObjectWindowCore.setSyntax(getDatabaseSqlSyntax(debugObject));
        try {
            executeSQLObjectWindowCore.setDebugObject(debugObject);
        } catch (DatabaseCriticalException e) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(e, debugObject.getDatabase());
            return;
        } catch (MPPDBIDEException mppdException) {
            handleMppDbIdeException(mppdException);
            return;
        } catch (Exception exception) {
            MPPDBIDEDialogs.generateErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.PLSQL_ERR),
                    MessageConfigLoader.getProperty(IMessagesConstants.UNKNOWN_INTERNAL_ERR), exception);
            return;
        }
        executeSQLObjectWindowCore.defaultParameterValues();

        editor = UIElement.getInstance().getEditorById(debugObject, false);
        if (!validateOnEditorCodeChanged(editor, debugObject)) {
            return;
        }

        if (!setDebugObjInSqlObjWindowCore(debugObject, executeSQLObjectWindowCore)) {
            return;
        }

        if (debugObject.validateObjectType()) {
            ArrayList<ObjectParameter> params = debugObject.getTemplateParameters();

            if (isTemplateParametersZero(debugObject)) {
                executeSQLObjectWindowCore.executePressed();
                return;
            }

            if (!IHandlerUtilities.isFunctionExecutable(false, params)) {
                return;
            }
        }
        executeSQLObjectWindowCore.open();
    }

    private void handleMppDbIdeException(MPPDBIDEException exception) {
        UIDisplayUtil.getDebugConsole().logError(MessageConfigLoader
                .getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD, exception.getMessage()));
        MPPDBIDEDialogs.generateDSErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTE_DEBUGE),
                MessageConfigLoader.getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD,
                        exception.getServerMessage()),
                exception);
    }

    /**
     * Sets the debug obj in sql obj window core.
     *
     * @param debugObject the debug object
     * @param executeSQLObjectWindowCore the execute SQL object window core
     * @return true, if successful
     */
    private boolean setDebugObjInSqlObjWindowCore(IDebugObject debugObject,
            ExcuteSQLObjectTable executeSQLObjectWindowCore) {
        try {
            executeSQLObjectWindowCore.setDebugObject(debugObject);
        } catch (DatabaseCriticalException ex) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(ex, debugObject.getDatabase());
            return false;
        } catch (MPPDBIDEException ex) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                    .getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD, ex.getMessage())));
            MPPDBIDEDialogs.generateDSErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTE_DEBUGE),
                    MessageConfigLoader.getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD,
                            ex.getServerMessage()),
                    ex);
            return false;
        } catch (Exception ex) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_STARTING_EXECUTE_SQL), ex);
            MPPDBIDEDialogs.generateErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.PLSQL_ERR),
                    MessageConfigLoader.getProperty(IMessagesConstants.UNKNOWN_INTERNAL_ERR), ex);
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message
                    .getError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_STARTING_EXECUTE_SQL)));
        }
        return true;
    }

    /**
     * Validate on editor code changed.
     *
     * @param editor the editor
     * @param debugObject the debug object
     * @return true, if successful
     */
    private boolean validateOnEditorCodeChanged(PLSourceEditor editor, IDebugObject debugObject) {
        if (isEditorCodeChanged(editor)) {
            int returnVal = MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.SOURCE_CODE_CHANGE),
                    MessageConfigLoader.getProperty(IMessagesConstants.SOURCE_HAS_BEEN_CHANGED));
            if (0 == returnVal) {
                String query = editor.getSourceEditorCore().getSourceViewer().getDocument().get();
                Database db = debugObject.getDatabase();

                try {
                    DatabaseUtils.executeOnSqlTerminalAndReturnNothing(query, 0,
                            db.getConnectionManager().getSqlTerminalConn(),
                            editor.getConsoleMessageWindow(false).getMsgQueue());
                    editor.refreshDebugObjectAfterEdit(debugObject);
                } catch (DatabaseOperationException exception) {

                    String message = MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILED_ITEM,
                            MPPDBIDEConstants.LINE_SEPARATOR, exception.getErrorCode(),
                            exception.getServerMessage() == null ? exception.getDBErrorMessage()
                                    : exception.getServerMessage());
                    editor.getConsoleMessageWindow(true).logErrorInUI(message);
                    MPPDBIDELoggerUtility.error("ExecuteObjectBrowserItem: execute on sql terminal failed.", exception);
                    return false;
                } catch (DatabaseCriticalException e) {
                    UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(e, db);
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if is template parameters zero.
     *
     * @param debugObject the debug object
     * @return true, if is template parameters zero
     */
    private boolean isTemplateParametersZero(IDebugObject debugObject) {
        return debugObject.getTemplateParameters() == null || debugObject.getTemplateParameters().size() == 0;
    }

    /**
     * Gets the database sql syntax.
     *
     * @param debugObject the debug object
     * @return the database sql syntax
     */
    private SQLSyntax getDatabaseSqlSyntax(IDebugObject debugObject) {
        return debugObject.getDatabase() == null ? null : debugObject.getDatabase().getSqlSyntax();
    }

    /**
     * Checks if is editor code changed.
     *
     * @param editor the editor
     * @return true, if is editor code changed
     */
    private boolean isEditorCodeChanged(PLSourceEditor editor) {
        return editor != null && editor.isCodeChanged();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        IDebugObject debugObject = IHandlerUtilities.getSelectedDebugObject();
        return null != debugObject;
    }
}
