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

import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.prefernces.KeyBindingWrapper;
import org.opengauss.mppdbide.view.prefernces.PreferenceWrapper;
import org.opengauss.mppdbide.view.ui.connectiondialog.DBConnectionDialog;
import org.opengauss.mppdbide.view.utils.PasswordExpiryNotifier;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class NewDbConnection.
 *
 * @since 3.0.0
 */
public class NewDbConnection {

    @Optional
    @Inject
    private IWorkbench workbench;
    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    /**
     * Instantiates a new new db connection.
     */
    public NewDbConnection() {
    }

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        MPPDBIDELoggerUtility
                .info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_NEWDBCONNECTION_OPEN_CONNECTION_WIZARD));
        DBConnectionDialog connDialog = new DBConnectionDialog(Display.getDefault().getActiveShell(), modelService,
                application, false);

        IExecTimer timer = new ExecTimer("Initial Connection");
        timer.start();
        connDialog.open();
        try {
            timer.stop();
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Error getting time", exception);

        }
        if (PreferenceWrapper.getInstance().isPreferenceValid()) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, false,
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_LOAD_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_LOAD_MSG));
            
            PreferenceWrapper.getInstance().setPreferenceValid(false);
            return;
        }
        boolean flag = connDialog.isFlag();
        if (flag) {
            Database database = DBConnProfCache.getInstance().getDbForProfileId(connDialog.getProfileId());
            PasswordExpiryNotifier passwordExpiryNotifier = new PasswordExpiryNotifier(database);
            passwordExpiryNotifier.checkAndNotifyPasswordExpiry();
            connDialog.setFlag(false);
        }
        if (connDialog.isExceptionOccured()) {

            workbench.close();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return UIVersionHandler.isVersionCompatible();
    }
}
