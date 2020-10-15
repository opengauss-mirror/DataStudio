/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import com.huawei.mppdbide.bl.sqlhistory.manager.ISqlHistoryManager;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.SSLUtility;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.DatabaseListControl;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.ObjectBrowserFilterUtility;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.DBDisconnectConfirmationDialog;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class RemoveServer.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class RemoveServer {

    @Inject
    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell shell;

    /**
     * Execute.
     *
     * @return the object
     */
    @Execute
    public Object execute() {
        String succesConst = "SUCCESS";
        String failureConst = "FAILURE";
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof Server) {
            Server server = (Server) obj;

            int result = 0;
            DBDisconnectConfirmationDialog disconnectConfirmationDialog = new DBDisconnectConfirmationDialog(shell,
                    MessageConfigLoader.getProperty(IMessagesConstants.REMOVE_SEVER),
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.REMOVE_SERVER_CONFIRMATION,
                            MPPDBIDEConstants.LINE_SEPARATOR, server.getName()),
                    MessageDialog.WARNING, null, 1);

            disconnectConfirmationDialog.open();
            if (result != disconnectConfirmationDialog.getReturnCode()) {
                return failureConst;
            }

            if (UIDisplayFactoryProvider.getUIDisplayStateIf().cleanupOnServerRemoval(server)) {
                UIDisplayFactoryProvider.getUIDisplayStateIf().deleteSecurityFolderFromProfile(server);
                ISqlHistoryManager histmgr = SQLHistoryFactory.getInstance();
                ServerConnectionInfo profile = (ServerConnectionInfo) server.getServerConnectionInfo();
                histmgr.stopHistoryManagementForProfile(profile.getProfileId());
                server.close();
                ObjectBrowserFilterUtility.getInstance().removeRefreshedServerFromList(server.getName());
                DBConnProfCache.getInstance().removeServer(server.getId());

                String key = profile.getServerIp() + ':' + profile.getServerPort();
                SSLUtility.removeSSLLoginStatus(key);
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message
                        .getInfo(MessageConfigLoader.getProperty(IMessagesConstants.SERVER_REMOVED, server.getName())));
                UIElement.getInstance().updateTextEditorsIconAndConnButtons(server);
            }
        }

        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (objectBrowserModel != null) {
            objectBrowserModel.remove(obj);
        }
        UIElement.getInstance().resetAllSQLTerminalConnections();
        UIElement.getInstance().resetConnectionRelatedButtons(false);
        UIElement.getInstance().refreshBatchDeleteTerminal();
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        if (null != databaseListControl) {
            databaseListControl.refreshConnectionComboItems();
        }
        return succesConst;
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        // DTS2014102907592 start
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof Server) {
            return true;
        }
        // DTS2014102907592 end
        return false;
    }

}
