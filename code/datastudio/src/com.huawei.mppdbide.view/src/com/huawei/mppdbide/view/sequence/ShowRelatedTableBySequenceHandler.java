/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.sequence;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
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
 * The Class ShowRelatedTableBySequenceHandler.
 *
 * @ClassName: ShowRelatedTableBySequenceHandler
 * @Description: Show Related Table By Sequence
 * @Author: yWX611925
 * @since: 2019-2-25
 */

public class ShowRelatedTableBySequenceHandler {

    /**
     * Execute.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    @Execute
    public void execute() {
        ServerObject serverObject = (ServerObject) IHandlerUtilities.getObjectBrowserSelectedObject();
        if (UIElement.getInstance().isWindowLimitReached()) {
            UIElement.getInstance().openMaxSourceViewerDialog();
            return;
        }

        if (null == serverObject) {
            return;
        }
        IViewTableDataCore tableDataCore = ViewTableDataFactory.getTableSequenceDataCore(serverObject, "");
        tableDataCore.init(serverObject);
        ViewTableDataResultDisplayUIManager resultUIManager = new ViewTableDataResultDisplayUIManager(tableDataCore);
        ViewTableDataExecutionContext dataExecutionContext = null;
        try {
            dataExecutionContext = new ViewTableDataExecutionContext(tableDataCore, resultUIManager);
        } catch (DatabaseOperationException databaseOperationException) {
            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABALE_DATA_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABALE_DATA_UNABLE));
            return;
        }

        TerminalQueryExecutionWorker queryExecutionWorker = new TerminalQueryExecutionWorker(dataExecutionContext);
        queryExecutionWorker.schedule();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();

        if (obj instanceof SequenceMetadata) {
            SequenceMetadata sequenceMetadata = (SequenceMetadata) obj;
            return sequenceMetadata.getDatabase().isConnected();
        } else {
            return false;
        }
    }

}
