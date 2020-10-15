/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.contentassist.ContentAssistKeywords;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionUtils;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.groups.DatabaseObjectGroup;
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
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.DBDisconnectConfirmationDialog;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class DisconnectAllDbs.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DisconnectAllDbs {
    private Job job;
    private StatusMessage statusMessage;

    /**
     * Execute.
     *
     * @param parentShell the parent shell
     */
    @Execute
    public void execute(final Shell parentShell) {
        final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        job = new Job("Disconnect All DBs") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {

                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
                        if (obj instanceof DatabaseObjectGroup) {
                            Server server = ((DatabaseObjectGroup) obj).getServer();

                            int result = 0;
                            DBDisconnectConfirmationDialog disconConfmDlg = generateDBConfirmationDialog(parentShell,
                                    server);
                            if (result != disconConfmDlg.getReturnCode()) {
                                bottomStatusBar.hideStatusbar(getStatusMessage());
                                return;
                            }

                            if (UIDisplayFactoryProvider.getUIDisplayStateIf().cleanupOnServerRemoval(server)) {
                                disconnectAllDBCleanup(server);
                            }
                        }

                        prformInitOperation();
                        performRefreshActions(obj, bottomStatusBar);
                    }

                });
                return Status.OK_STATUS;
            }
        };

        StatusMessage statMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DISCONNECT_ALL_DB));
        setStatusMessage(statMessage);
        StatusMessageList.getInstance().push(statMessage);
        if (bottomStatusBar != null) {
            bottomStatusBar.activateStatusbar();
        }
        job.schedule();
    }

    /**
     * Perform refresh actions.
     *
     * @param obj the obj
     * @param bottomStatusBar the bottom status bar
     */
    public void performRefreshActions(Object obj, BottomStatusBar bottomStatusBar) {
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (objectBrowserModel != null) {
            objectBrowserModel.refreshObject(obj);
        }
        UIElement.getInstance().resetAllSQLTerminalConnections();
        UIElement.getInstance().refreshBatchDeleteTerminal();
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        if (null != databaseListControl) {
            databaseListControl.refreshConnectionComboItems();
        }
        if (obj != null) {
            UIElement.getInstance().updateTextEditorsIconAndConnButtons(((DatabaseObjectGroup) obj).getServer());
        }
        if (bottomStatusBar != null) {
            bottomStatusBar.hideStatusbar(getStatusMessage());
        }
    }

    /**
     * Disconnect all DB cleanup.
     *
     * @param server the server
     */
    private void disconnectAllDBCleanup(Server server) {
        server.close();
        ObjectBrowserFilterUtility.getInstance().removeRefreshedServerFromList(server.getName());
        ContentAssistKeywords.getInstance().clear();
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message
                .getInfo(MessageConfigLoader.getProperty(IMessagesConstants.DISCONNECT_ALL_DB, server.getName())));
        MPPDBIDELoggerUtility.info("Disconnected all dbs.");
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof DatabaseObjectGroup) {
            Server server = ((DatabaseObjectGroup) obj).getServer();
            return server.isAleastOneDbConnected();
        }
        SQLTerminal sqlTerminal = UIElement.getInstance().getVisibleTerminal();
        if (null != sqlTerminal) {
            sqlTerminal.resetSQLTerminalButton();
            sqlTerminal.resetAutoCommitButton();
        }

        return false;
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
     * Generate DB confirmation dialog.
     *
     * @param parentShell the parent shell
     * @param server the server
     * @return the DB disconnect confirmation dialog
     */
    private DBDisconnectConfirmationDialog generateDBConfirmationDialog(final Shell parentShell, Server server) {
        DBDisconnectConfirmationDialog disconConfmDlg = new DBDisconnectConfirmationDialog(parentShell,
                MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_DISCONNECT),
                IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                MessageConfigLoader.getProperty(IMessagesConstants.DISCONNECT_CONFIRMATION, server.getName()),
                MessageDialog.WARNING, null, 1);
        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_DISCON, true);
        disconConfmDlg.open();
        return disconConfmDlg;
    }

    /**
     * Prform init operation.
     */
    private void prformInitOperation() {
        Iterator<Server> servers = DBConnProfCache.getInstance().getServers().iterator();
        boolean isOneDBConnected = false;
        boolean dbHasNextVal = false;
        boolean serverHasNextRcrd = servers.hasNext();
        Server serv = null;
        Iterator<Database> dbs = null;
        Database db = null;
        while (serverHasNextRcrd) {
            serv = servers.next();
            dbs = serv.getAllDatabases().iterator();
            dbHasNextVal = dbs.hasNext();
            while (dbHasNextVal) {
                if (isOneDBConnected) {
                    break;
                }
                db = dbs.next();
                if (db.isConnected()) {
                    isOneDBConnected = true;
                    break;
                }
                dbHasNextVal = dbs.hasNext();
            }

            serverHasNextRcrd = servers.hasNext();
        }
    }
}
