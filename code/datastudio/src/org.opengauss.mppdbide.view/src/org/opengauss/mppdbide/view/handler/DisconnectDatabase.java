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

package org.opengauss.mppdbide.view.handler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.contentassist.ContentAssistKeywords;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.ILogger;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.ui.DatabaseListControl;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.ObjectBrowserFilterUtility;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.DBDisconnectConfirmationDialog;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class DisconnectDatabase.
 *
 * @since 3.0.0
 */
public class DisconnectDatabase {
    private Job job;
    private StatusMessage statusMessage;

    /**
     * Instantiates a new disconnect database.
     */
    public DisconnectDatabase() {
    }

    /**
     * Execute.
     *
     * @param parentShell the parent shell
     */
    @Execute
    public void execute(Shell parentShell) {

        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        Database database = null;
        if (obj instanceof Database) {
            database = (Database) obj;
        }

        Database db = database;
        if (null != db) {

            int result = UIConstants.OK_ID;

            DBDisconnectConfirmationDialog disconnectConfirmationDialog = new DBDisconnectConfirmationDialog(
                    parentShell, MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_DISCONNECT),
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.DISCONNECT_CONFIRMATION, db.getName()),
                    MessageDialog.WARNING, null, 1);
            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_DISCON, true);
            disconnectConfirmationDialog.open();

            if (result != disconnectConfirmationDialog.getReturnCode()) {
                return;
            }

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            
            UIDisplayFactoryProvider.getUIDisplayStateIf().cleanupOnDatabaseDisconnect(db, db.getServer());
            disconnectDatabase(db, bottomStatusBar);

        }
    }

    /**
     * Disconnect database.
     *
     * @param db the db
     * @param bottomStatusBar the bottom status bar
     */
    private void disconnectDatabase(Database db, final BottomStatusBar bottomStatusBar) {
        job = new Job("Disconnect Database") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                DBConnProfCache.getInstance().destroyConnection(db);

                initCancelJobOperation(db);

                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        performRefreshOnDisconnect(db, bottomStatusBar);

                    }

                    private void performRefreshOnDisconnect(Database db, final BottomStatusBar bottomStatusBar) {
                        ObjectBrowser objectBrowserModel = performRefreshOperation(db);

                        MPPDBIDELoggerUtility.info("Disconnected from server.");
                        ObjectBrowserStatusBarProvider.getStatusBar()
                                .displayMessage(Message.getInfo(MessageConfigLoader.getProperty(
                                        IMessagesConstants.DISCONNECTED_FROM_SERVER,
                                        db.getServer().getServerConnectionInfo().getConectionName(), db.getName())));

                        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_DISCON, false);
                        ContentAssistKeywords.getInstance().clearOLAPKeywords(db.getServer().getAllDatabases());
                        if (!db.getServer().isAleastOneDbConnected()) {
                            disconnectAllDBCleanup(db.getServer());
                            if (objectBrowserModel != null) {
                                objectBrowserModel.refreshObject(db.getServer());
                            }
                        }

                        bottomStatusBar.hideStatusbar(getStatusMessage());
                    }
                });

                return Status.OK_STATUS;
            }
        };

        StatusMessage statMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.DISCONN_IN_PROGRESS));
        setStatusMessage(statMessage);
        StatusMessageList.getInstance().push(statMessage);
        if (bottomStatusBar != null) {
            bottomStatusBar.activateStatusbar();
        }

        job.schedule();
    }


    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj == null) {
            return false;
        }
        if (obj instanceof Database) {
            Database db = (Database) obj;
            return db.isConnected();
        }
        return false;
    }

    /**
     * Disconnect all DB cleanup.
     *
     * @param serv the server
     */
    private void disconnectAllDBCleanup(Server serv) {
        serv.close();
        ObjectBrowserFilterUtility.getInstance().removeRefreshedServerFromList(serv.getName());
        ContentAssistKeywords.getInstance().clear();
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message
                .getInfo(MessageConfigLoader.getProperty(IMessagesConstants.DISCONNECT_ALL_DB, serv.getName())));
        MPPDBIDELoggerUtility.info("Disconnected all dbs.");
    }
    
    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMessage = statMessage;
    }

    /**
     * Perform refresh operation.
     *
     * @param db the db
     * @return the object browser
     */
    private ObjectBrowser performRefreshOperation(Database db) {
        UIDisplayFactoryProvider.getUIDisplayStateIf().resetConnectedProfileId();
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (null != objectBrowserModel) {

            objectBrowserModel.getTreeViewer().refresh(db, true);
        }

        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        if (null != databaseListControl) {
            databaseListControl.refreshConnectionComboItems();
        }
        UIElement.getInstance().resetSQLTerminalConnections(db.getServer());
        UIElement.getInstance().refreshBatchDeleteTerminal();
        UIElement.getInstance().updateTextEditorsIconAndConnButtons(db.getServer());
        return objectBrowserModel;
    }

    /**
     * Inits the cancel job operation.
     *
     * @param db the db
     */
    private void initCancelJobOperation(Database db) {
        final IJobManager jm = Job.getJobManager();
        Job[] allJobs = jm.find(MPPDBIDEConstants.CANCELABLEJOB);

        UIWorkerJob uiWorkJob = null;
        for (final Job workJob : allJobs) {
            if (workJob instanceof UIWorkerJob) {
                uiWorkJob = (UIWorkerJob) workJob;
                if (uiWorkJob.getTaskDB() != null && uiWorkJob.getTaskDB().equals(db)) {
                    workJob.cancel();
                }
            }
        }
    }
}
