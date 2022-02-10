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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.eclipse.dependent.EclipseInjections;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ImportConnectionProfileCore;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ImportConnectionProfileCore.MatchedConnectionProfiles;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ImportConnectionProfileManager;
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
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportConnectionProfileReaderWorkerJob.
 *
 * @since 3.0.0
 */
public class ImportConnectionProfileReaderWorkerJob extends UIWorkerJob {

    private ImportConnectionProfileCore core;
    private Shell shell;
    private BottomStatusBar bottomStatusBar;
    private StatusMessage statusMessage;

    /**
     * Instantiates a new import connection profile reader worker job.
     *
     * @param name the name
     * @param family the family
     * @param importManager the import manager
     * @param shell the shell
     * @param bottomStatusBar the bottom status bar
     * @param statusMessage the status message
     */
    public ImportConnectionProfileReaderWorkerJob(String name, Object family, ImportConnectionProfileCore importManager,
            Shell shell, BottomStatusBar bottomStatusBar, StatusMessage statusMessage) {
        super(name, family);
        this.core = importManager;
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
        core.importFiles();
        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        ImportConnectionProfileManager manager = new ImportConnectionProfileManager(core);
        List<MatchedConnectionProfiles> matchedProfilesList = core.getMatchedProfilesList();
        AtomicInteger atmint = new AtomicInteger();
        if (matchedProfilesList.size() > 0) {
            ImportOverridingDialog overrideDlg = null;
            boolean needToContinue = true;

            do {
                int counter = atmint.getAndIncrement();
                MatchedConnectionProfiles profile = matchedProfilesList.get(counter);
                overrideDlg = new ImportOverridingDialog(shell, manager, profile);
                int open = overrideDlg.open();
                if (open != IDialogConstants.OK_ID) {
                    return;
                }
                needToContinue = overrideDlg.isContinueOperation();
            } while (atmint.get() < matchedProfilesList.size() && needToContinue);

        }
        ImportConnectionProfileWriterWorkerJob profileWriter = new ImportConnectionProfileWriterWorkerJob(
                MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_CONNECTIONS_LOADING_STATUS_MSG),
                MPPDBIDEConstants.CANCELABLEJOB, manager);
        profileWriter.schedule();
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        MPPDBIDELoggerUtility.error("critical exception occurred while reading the file for import", exception);
        handleException(exception);
    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        MPPDBIDELoggerUtility.error("database operation exception occurred while reading the file for import",
                exception);
        handleException(exception);
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        MPPDBIDELoggerUtility.info("Reading of imported connection profile");
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
        String detailmsg = exception.getCause().toString();
        // trimming the type of an exception object to avoid showing in UI
        int charIndex = detailmsg.indexOf(':');
        detailmsg = detailmsg.substring(charIndex + 1).trim();
        MPPDBIDEDialogs.generateDSErrorDialog(
                MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_CONNECTIONS_PROFILE_DIALOG_HEADER),
                MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_CONNECTIONS_PROFILE_INVALID_FORMAT_EXCEPTION),
                detailmsg, exception);
    }

}
