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

package com.huawei.mppdbide.view.exportimportdsconnections;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.presentation.exportimportdsconnectionprofiles.ImportConnectionProfileCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.Preferencekeys;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportDSConnectionProfiles.
 *
 * @since 3.0.0
 */
public class ImportDSConnectionProfilesHandlers {

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(Shell shell) {

        ImportDsConnectionProfilesDialog dialog = new ImportDsConnectionProfilesDialog();
        String importFilePath = dialog.openDialog(shell);
        double fileSizeLimit = PreferenceWrapper.getInstance().getPreferenceStore()
                .getInt(Preferencekeys.FILE_LIMIT_FOR_SQL);
        ImportConnectionProfileCore importPrfMgr = new ImportConnectionProfileCore(importFilePath, fileSizeLimit);
        StatusMessage statusMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_CONNECTIONS_LOADING_STATUS_MSG));
        BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bottomStatusBar != null) {
            bottomStatusBar.setStatusMessage(statusMessage.getMessage());
        }
        ImportConnectionProfileReaderWorkerJob worker = new ImportConnectionProfileReaderWorkerJob(
                MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_CONNECTIONS_LOADING_STATUS_MSG),
                MPPDBIDEConstants.CANCELABLEJOB, importPrfMgr, shell, bottomStatusBar, statusMessage);
        StatusMessageList.getInstance().push(statusMessage);
        if (bottomStatusBar != null) {
            bottomStatusBar.activateStatusbar();
        }
        worker.schedule();
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
