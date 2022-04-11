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

package org.opengauss.mppdbide.view.exportimportdsconnections;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ExportConnectionCore;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportDSConnectionProfiles.
 *
 * @since 3.0.0
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
