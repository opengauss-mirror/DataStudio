/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.exportimportdsconnections;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.presentation.exportimportdsconnectionprofiles.ExportConnectionCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportConnectionProfilesWorkerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
