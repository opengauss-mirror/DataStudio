/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.ObjectChange;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.ConsoleCoreWindow;
import com.huawei.mppdbide.view.functionchange.ExecuteWrapper;
import com.huawei.mppdbide.view.functionchange.ObjectChangeEvent;
import com.huawei.mppdbide.view.functionchange.ObjectChangeEvent.ButtonPressed;
import com.huawei.mppdbide.view.functionchange.ObjectChangeWorker;
import com.huawei.mppdbide.view.search.SearchWindow;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewSourceDebugObjectHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * @param e the e
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
