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

package org.opengauss.mppdbide.view.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.presentation.IViewTableDataCore;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.handler.table.ViewTableDataFactory;
import org.opengauss.mppdbide.view.terminal.TerminalQueryExecutionWorker;
import org.opengauss.mppdbide.view.terminal.executioncontext.ViewTableDataExecutionContext;
import org.opengauss.mppdbide.view.ui.terminal.ViewTableDataResultDisplayUIManager;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewViewDataHandler.
 *
 * @since 3.0.0
 */
public class ViewViewDataHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        // check limit by using the getViewTableDataModel of UIElement
        if (UIElement.getInstance().isWindowLimitReached()) {
            UIElement.getInstance().openMaxSourceViewerDialog();
            return;
        }
        ServerObject servObject = (ServerObject) IHandlerUtilities.getObjectBrowserSelectedObject();
        if (null == servObject) {
            return;
        }
        IViewTableDataCore core = ViewTableDataFactory.getViewTableDataCore(servObject, "");
        core.init(servObject);
        ViewTableDataResultDisplayUIManager uiViewManager = new ViewTableDataResultDisplayUIManager(core);
        ViewTableDataExecutionContext context = null;
        try {
            context = new ViewTableDataExecutionContext(core, uiViewManager);
        } catch (DatabaseOperationException exception) {
            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABALE_DATA_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABALE_DATA_UNABLE));
            return;
        }

        TerminalQueryExecutionWorker worker = new TerminalQueryExecutionWorker(context);
        worker.schedule();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object view = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (null != view) {
            if (view instanceof ViewMetaData) {
                Namespace ns = (Namespace) ((ViewMetaData) view).getNamespace();
                if (null != ns && ns.getDatabase().isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
}
