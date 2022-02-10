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

package org.opengauss.mppdbide.view.handler.table;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.bl.serverdatacache.ForeignPartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.presentation.IViewTableDataCore;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
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
 * Description: The Class ViewPartitionDataHandler.
 *
 * @since 3.0.0
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
