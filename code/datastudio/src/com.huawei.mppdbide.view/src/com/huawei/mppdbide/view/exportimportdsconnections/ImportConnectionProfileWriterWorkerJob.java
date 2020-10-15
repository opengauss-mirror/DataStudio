/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.exportimportdsconnections;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;

import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.presentation.exportimportdsconnectionprofiles.ImportConnectionProfileManager;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportConnectionProfileWriterWorkerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ImportConnectionProfileWriterWorkerJob extends UIWorkerJob {

    private ImportConnectionProfileManager importprfMngr;

    /**
     * Instantiates a new import connection profile writer worker job.
     *
     * @param name the name
     * @param family the family
     * @param manager the manager
     */
    public ImportConnectionProfileWriterWorkerJob(String name, Object family, ImportConnectionProfileManager manager) {
        super(name, family);
        importprfMngr = manager;
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
        importprfMngr.mergeAllProfiles();
        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        Command cmd = EclipseInjections.getInstance().getCommandService()
                .getCommand("com.huawei.mppdbide.command.id.newdbconnection");
        ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
        EclipseInjections.getInstance().getHandlerService().executeHandler(parameterizedCmd);
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        MPPDBIDELoggerUtility.error("critical exception ocurred while writing the imported profile to the disk",
                exception);
        handleException(exception);
    }

    private void handleException(MPPDBIDEException exception) {
        MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_CONNECTIONS_PROFILE_DIALOG_HEADER),
                MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_CONNECTIONS_PROFILE_ERR_MSG),
                MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));
    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        MPPDBIDELoggerUtility.error(
                "database operation exception ocurred while writing the imported profile to the disk", exception);
        handleException(exception);
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        MPPDBIDELoggerUtility.info("importing connections");
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {

    }

}
