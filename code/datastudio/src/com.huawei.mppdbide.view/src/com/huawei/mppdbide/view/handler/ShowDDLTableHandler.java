/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ShowDDLTableHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ShowDDLTableHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        StatusMessage statusMessage = null;
        TableMetaData object = IHandlerUtilities.getSelectedTable();
        if (object == null) {
            return;
        }

        Database db = object.getDatabase();

        if (!IHandlerUtilities.isDDLOperationsSupported(db)) {
            return;
        }

        statusMessage = new StatusMessage(MessageConfigLoader.getProperty(IMessagesConstants.SHOW_TABLE_DDL));
        BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bottomStatusBar != null) {
            bottomStatusBar.setStatusMessage(statusMessage.getMessage());
        }
        String progressLabel = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(object.getName(),
                object.getNamespace().getName(), object.getDatabaseName(), object.getServerName(),
                IMessagesConstants.SHOW_DDL_TABLE_PROGRESS_NAME);

        ShowTableDDLWorker ddlWorker = new ShowTableDDLWorker(progressLabel, object, bottomStatusBar, statusMessage,
                db);

        ddlWorker.setTaskDB(db);
        StatusMessageList.getInstance().push(statusMessage);
        if (bottomStatusBar != null) {
            bottomStatusBar.activateStatusbar();
        }
        ddlWorker.schedule();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return (null != IHandlerUtilities.getSelectedTable()) && !(IHandlerUtilities.isSelectedTableForignPartition());
    }
}
