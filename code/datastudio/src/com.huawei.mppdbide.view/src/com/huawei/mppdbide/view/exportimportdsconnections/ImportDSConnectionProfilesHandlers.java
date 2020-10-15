/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
