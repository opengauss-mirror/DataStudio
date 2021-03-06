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

import org.opengauss.mppdbide.bl.contentassist.ContentAssistKeywords;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionUtils;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.groups.DatabaseObjectGroup;
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
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.DBDisconnectConfirmationDialog;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class DisconnectAllDbs.
 *
 * @since 3.0.0
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
