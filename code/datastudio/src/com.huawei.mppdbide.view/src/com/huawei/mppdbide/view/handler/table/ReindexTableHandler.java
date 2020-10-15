/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableOrientation;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.handler.ReindexTableWorker;

/**
 * 
 * Title: class
 * 
 * Description: The Class ReindexTableHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ReindexTableHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        TableMetaData selTable = IHandlerUtilities.getSelectedTable();
        if (null != selTable) {

            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(selTable.getName(),
                    selTable.getNamespace().getName(), selTable.getDatabaseName(), selTable.getServerName(),
                    IMessagesConstants.REINDEX_TABLE_PROGRESS_NAME);
            ReindexTableWorker worker = new ReindexTableWorker(progressLabel, selTable);
            worker.schedule();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        TableMetaData selectTable = IHandlerUtilities.getSelectedTable();
        if (null == selectTable) {
            return false;
        }
        return !IHandlerUtilities.isSelectedTableForignPartition();
    }

}