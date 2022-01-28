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

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.presentation.exportimportdsconnectionprofiles.ExportConnectionCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class LoadConnectionProfilesWorkerJob.
 *
 * @since 3.0.0
 */
public class LoadConnectionProfilesWorkerJob extends UIWorkerJob {

    private ExportConnectionCore exportCore;
    private Shell shell;
    private BottomStatusBar bottomStatusBar;
    private StatusMessage statusMessage;

    /**
     * Instantiates a new load connection profiles worker job.
     *
     * @param name the name
     * @param family the family
     * @param exportCore the export core
     * @param shell the shell
     * @param bottomStatusBar the bottom status bar
     * @param statusMessage the status message
     */
    public LoadConnectionProfilesWorkerJob(String name, Object family, ExportConnectionCore exportCore, Shell shell,
            BottomStatusBar bottomStatusBar, StatusMessage statusMessage) {
        super(name, "");
        this.exportCore = exportCore;
        this.shell = shell;
        this.bottomStatusBar = bottomStatusBar;
        this.statusMessage = statusMessage;
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws Exception the exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        List<IServerConnectionInfo> allProfiles = ConnectionProfileManagerImpl.getInstance().getAllProfiles();
        for (IServerConnectionInfo info : allProfiles) {
            info.clearPasrd();
            info.setPrd(new char[0]);
            info.setSSLPrd(new char[0]);
        }
        exportCore.setLoadedProfileList(allProfiles);
        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        if (bottomStatusBar != null) {
            bottomStatusBar.hideStatusbar(statusMessage);
        }
        ExportConnectionProfilesDialog dialog = new ExportConnectionProfilesDialog(shell, exportCore);
        int open = dialog.open();
        if (open == IDialogConstants.OK_ID) {
            StatusMessage statusMsg = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORTING_CONNECTION_PROFILES));
            BottomStatusBar btmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (btmStatusBar != null) {
                btmStatusBar.setStatusMessage(statusMsg.getMessage());
            }

            ExportConnectionProfilesWorkerJob worker = new ExportConnectionProfilesWorkerJob(
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORTING_CONNECTION_PROFILES),
                    MPPDBIDEConstants.CANCELABLEJOB, exportCore, btmStatusBar, statusMsg);
            StatusMessageList.getInstance().push(statusMsg);
            if (btmStatusBar != null) {
                btmStatusBar.activateStatusbar();
            }
            worker.schedule();
        }
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        MPPDBIDELoggerUtility.error("critical exception occurred while loading the connection profiles for exporting",
                exception);
        handleException(exception);
    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        MPPDBIDELoggerUtility.error(
                "database operation exception occurred while loading the connection profiles for exporting", exception);
        handleException(exception);
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        MPPDBIDELoggerUtility.info("Loading connection profiles for export");
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        if (bottomStatusBar != null) {
            bottomStatusBar.hideStatusbar(statusMessage);
        }
    }

    private void handleException(MPPDBIDEException exception) {

        MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CONNECTION_PROFILE_DIALOG_HEADER),
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CONNECTION_PROFILE_LOAD_ERR_MSG),
                MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));
    }

}
