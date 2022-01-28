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

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.IViewTableDataCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.terminal.TerminalQueryExecutionWorker;
import com.huawei.mppdbide.view.terminal.executioncontext.ViewTableDataExecutionContext;
import com.huawei.mppdbide.view.ui.terminal.ViewTableDataResultDisplayUIManager;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * Title: ShowRelatedSequenceByTableHandler
 * 
 * Description:Show Related Sequence By Table
 * 
 * @since 3.0.0
 */
public class ShowRelatedSequenceByTableHandler {
    /** 
     * The Constant HANDLER_PARAMETER. 
     */
    private static final String HANDLER_PARAMETER = "RelatedSequence";

    /**
     * Execute.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    @Execute
    public void execute() {
        ServerObject serverObj = (ServerObject) IHandlerUtilities.getObjectBrowserSelectedObject();
        if (UIElement.getInstance().isWindowLimitReached()) {
            UIElement.getInstance().openMaxSourceViewerDialog();
            return;
        }
        if (null == serverObj) {
            return;
        }
        IViewTableDataCore dataCore = ViewTableDataFactory.getTableSequenceDataCore(serverObj, HANDLER_PARAMETER);
        dataCore.init(serverObj);
        ViewTableDataResultDisplayUIManager displayUIManager = new ViewTableDataResultDisplayUIManager(dataCore);
        ViewTableDataExecutionContext executionContext = null;
        try {
            executionContext = new ViewTableDataExecutionContext(dataCore, displayUIManager);
        } catch (DatabaseOperationException exception) {
            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABALE_DATA_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABALE_DATA_UNABLE));
            return;
        }
        TerminalQueryExecutionWorker executionWorker = new TerminalQueryExecutionWorker(executionContext);
        executionWorker.schedule();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object object = IHandlerUtilities.getObjectBrowserSelectedObject();

        if (object instanceof TableMetaData) {
            TableMetaData tableMetaData = (TableMetaData) object;
            boolean connected = tableMetaData.getDatabase().isConnected();
            return connected;

        } else {
            return false;
        }
    }
}
