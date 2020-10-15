/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

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

import com.huawei.mppdbide.bl.contentassist.ContentAssistKeywords;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.ILogger;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.DatabaseListControl;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.ObjectBrowserFilterUtility;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.DBDisconnectConfirmationDialog;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class DisconnectDatabase.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
