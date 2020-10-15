/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author yWX611925
 * @version [DataStudio 6.5.1, 2019-2-25]
 * @since 2019-2-25
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
