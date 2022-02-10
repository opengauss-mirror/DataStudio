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

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;

import org.opengauss.mppdbide.eclipse.dependent.EclipseInjections;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ImportConnectionProfileManager;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportConnectionProfileWriterWorkerJob.
 *
 * @since 3.0.0
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
                .getCommand("org.opengauss.mppdbide.command.id.newdbconnection");
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
