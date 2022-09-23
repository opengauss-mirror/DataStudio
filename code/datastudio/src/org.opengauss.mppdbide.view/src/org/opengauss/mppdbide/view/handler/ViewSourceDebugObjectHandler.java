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

package org.opengauss.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectChange;
import org.opengauss.mppdbide.utils.DebuggerStartVariable;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.ConsoleCoreWindow;
import org.opengauss.mppdbide.view.functionchange.ExecuteWrapper;
import org.opengauss.mppdbide.view.functionchange.ObjectChangeEvent;
import org.opengauss.mppdbide.view.functionchange.ObjectChangeEvent.ButtonPressed;
import org.opengauss.mppdbide.view.functionchange.ObjectChangeWorker;
import org.opengauss.mppdbide.view.search.SearchWindow;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewSourceDebugObjectHandler.
 *
 * @since 3.0.0
 */
public class ViewSourceDebugObjectHandler implements ExecuteWrapper {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        PLSourceEditor editorObject = null;
        Object partObject = UIElement.getInstance().getActivePartObject();
        IDebugObject debugObject = IHandlerUtilities.getSelectedDebugObject();
        if (debugObject != null) {
            Long oid = debugObject.getOid();
            DebuggerStartVariable.getStartInfo(oid).remarLinesStr = "";
        }
        if (debugObject != null) {
            if (!(UIElement.getInstance().isEditorExistByDbgObj(debugObject))) {
                try {
                    HandlerUtilities.displaySourceCodeInEditor(debugObject, true);
                } catch (DatabaseOperationException e) {
                    if (!e.getMessage()
                            .contentEquals(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_INVALID_STATE))) {
                        IHandlerUtilities.handleGetSrcCodeException(debugObject);
                    }

                    return;
                } catch (DatabaseCriticalException e) {
                    UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(e, debugObject.getDatabase());
                    return;
                }
            } else {
                editorObject = UIElement.getInstance().getEditorModelByIdAndActivate(debugObject);
                if (partObject instanceof SearchWindow) {
                    IDebugObject objectBrowserDO = UIElement.getInstance().getObjectBrowserDebugObj(debugObject);
                    if (objectBrowserDO != null) {
                        debugObject = objectBrowserDO;
                        objectBrowserDO.setSourceCode(editorObject.getDebugObject().getSourceCode());
                    } else {
                        debugObject.setSourceCode(editorObject.getDebugObject().getSourceCode());
                    }
                }
                ObjectChangeWorker<ObjectChange> objWorker = new ObjectChangeWorker<ObjectChange>(
                        "Function Change Worker", null, debugObject, editorObject, this,
                        IMessagesConstants.FUNCTN_CHANGE_VIEWSOURCE_MSG, IMessagesConstants.FUNCTN_CHANGE_CANCEL);
                objWorker.schedule();
            }
        }
    }

    /**
     * Handle execute.
     *
     * @param event the event
     */
    @Override
    public void handleExecute(ObjectChangeEvent event) {
        if (event.getStatus() != null && event.getStatus() == ButtonPressed.REFRESH) {
            IDebugObject debugObject = event.getDbgObj();
            PLSourceEditor editor = event.getEditor();
            if (debugObject != null) {
                try {
                    HandlerUtilities.displayRefreshSourceCodeInEditor(debugObject, editor);
                } catch (DatabaseCriticalException e) {
                    UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(e, debugObject.getDatabase());
                    return;
                }
            }
        }
    }

    /**
     * Handle exception.
     *
     * @param e     the e
     * @param event the event
     */
    @Override
    public void handleException(Throwable exception, ObjectChangeEvent event) {
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE),
                exception);
        ConsoleCoreWindow.getInstance()
                .logWarning(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE));
        String msg = MessageConfigLoader.getProperty(IMessagesConstants.FUNCT_CHANGE_VIEWSOURCE_ERR)
                + exception.getMessage();
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.PL_SOURCE_VIEWER_ERROR), msg);
    }

}
