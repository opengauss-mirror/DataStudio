/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.ForeignPartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.PartitionMetaData;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
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
 *
 * Title: class
 *
 * Description: The Class ViewPartitionDataHandler.
 *
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [DataStudio 2.0.0, 13 July, 2021]
 * @since 13 July, 2021
 */
public class ViewPartitionDataHandler {
    /**
     * Execute.
     *
     */
    @Execute
    public void execute() {
        PartitionMetaData partitionMetaData = IHandlerUtilities.getSelectedPartitionMetadata();
        if (partitionMetaData == null) {
            return;
        }
        if (UIElement.getInstance().isWindowLimitReached()) {
            UIElement.getInstance().openMaxSourceViewerDialog();
            return;
        }
        PartitionTable partitionTable = partitionMetaData.getParent();
        IViewTableDataCore viewTableDataCore = ViewTableDataFactory.getViewTableDataCore(partitionTable,
                "PARTITION (" + partitionMetaData.getName() + ")");
        viewTableDataCore.init(partitionTable);
        ViewTableDataResultDisplayUIManager uiManager = new ViewTableDataResultDisplayUIManager(viewTableDataCore);
        ViewTableDataExecutionContext context = null;
        try {
            context = new ViewTableDataExecutionContext(viewTableDataCore, uiManager);
        } catch (DatabaseOperationException exceptition) {
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
        PartitionMetaData partitionMetaData = IHandlerUtilities.getSelectedPartitionMetadata();
        if (partitionMetaData == null) {
            return false;
        } else {
            return !(partitionMetaData.getParent() instanceof ForeignPartitionTable);
        }
    }
}
