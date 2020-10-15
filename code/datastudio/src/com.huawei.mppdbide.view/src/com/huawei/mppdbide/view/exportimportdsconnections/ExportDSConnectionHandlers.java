/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.exportimportdsconnections;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.presentation.exportimportdsconnectionprofiles.ExportConnectionCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportDSConnectionProfiles.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExportDSConnectionHandlers {

    /**
     * Execute.
     *
     * @param shell the shell
     */

    @Execute
    public void execute(Shell shell) {

        ExportConnectionCore core = new ExportConnectionCore();
        StatusMessage statusMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CONNECTION_PROF_LOADING_STATUS_MESSAGE));
        BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bottomStatusBar != null) {
            bottomStatusBar.setStatusMessage(statusMessage.getMessage());
        }
        if (bottomStatusBar != null) {
            bottomStatusBar.activateStatusbar();
        }
        StatusMessageList.getInstance().push(statusMessage);
        LoadConnectionProfilesWorkerJob loadProfilesWorker = new LoadConnectionProfilesWorkerJob(
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CONNECTION_PROF_LOADING_STATUS_MESSAGE),
                MPPDBIDEConstants.CANCELABLEJOB, core, shell, bottomStatusBar, statusMessage);
        loadProfilesWorker.schedule();

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return true;
    }
}
