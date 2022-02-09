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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import org.opengauss.mppdbide.bl.sqlhistory.manager.ISqlHistoryManager;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.SSLUtility;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.ui.DatabaseListControl;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.ObjectBrowserFilterUtility;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.DBDisconnectConfirmationDialog;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class RemoveServer.
 *
 * @since 3.0.0
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
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof Server) {
            return true;
        }
        return false;
    }

}
