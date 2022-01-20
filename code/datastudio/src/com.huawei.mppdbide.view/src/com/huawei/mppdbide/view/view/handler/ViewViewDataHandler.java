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

package com.huawei.mppdbide.view.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.presentation.IViewTableDataCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.handler.table.ViewTableDataFactory;
import com.huawei.mppdbide.view.terminal.TerminalQueryExecutionWorker;
import com.huawei.mppdbide.view.terminal.executioncontext.ViewTableDataExecutionContext;
import com.huawei.mppdbide.view.ui.terminal.ViewTableDataResultDisplayUIManager;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

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
