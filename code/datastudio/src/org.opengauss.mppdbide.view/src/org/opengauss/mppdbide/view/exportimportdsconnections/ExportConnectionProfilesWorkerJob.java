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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ExportConnectionCore;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportConnectionProfilesWorkerJob.
 *
 * @since 3.0.0
 */
public class ExportConnectionProfilesWorkerJob extends UIWorkerJob {
    private ExportConnectionCore connectionCore;
    private BottomStatusBar bottomStatusBar;
    private StatusMessage statusMessage;

    /**
     * Instantiates a new export connection profiles worker job.
     *
     * @param name the name
     * @param family the family
     * @param connectionCore the connection core
     * @param bottomStatusBar the bottom status bar
     * @param statusMessage the status message
     */
    public ExportConnectionProfilesWorkerJob(String name, Object family, ExportConnectionCore connectionCore,
            BottomStatusBar bottomStatusBar, StatusMessage statusMessage) {
        super(name, family);
        this.connectionCore = connectionCore;
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
        ConnectionProfileManagerImpl connectionProfMgr = ConnectionProfileManagerImpl.getInstance();
        connectionProfMgr.exportConnectionProfiles(connectionCore.getExportFileList(),
                connectionCore.getFileOutputPath());
        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CONNECTION_PROFILE_DIALOG_HEADER),
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CONNECTION_PROFILE_SUCCESS_MESSAGE),
                MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        handleException(exception);
        MPPDBIDELoggerUtility.error("critical exception occurred while exporting the connection profiles", exception);
    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {

        handleException(exception);
        MPPDBIDELoggerUtility.error("database operation occurred while exporting the connection profiles", exception);
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        MPPDBIDELoggerUtility.info("Export of connections");
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

        StringBuilder errMsgBuilder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        errMsgBuilder.append(exception.getMessage());
        if (exception.getServerMessage() != null) {
            errMsgBuilder.append(exception.getServerMessage());
        }
        MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CONNECTION_PROFILE_DIALOG_HEADER),
                errMsgBuilder.toString(), MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));
        if (!"File with same name already exist".contains(exception.getMessage())) {
            deleteStaleFiles();
        }
    }

    private void deleteStaleFiles() {
        try {
            Files.deleteIfExists(Paths.get(connectionCore.getFileOutputPath()));
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("unable to delete exported connection profiles on exception", exception);
        }
    }

}
