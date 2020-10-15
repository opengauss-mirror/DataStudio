/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.ui.connectiondialog.EditConnectionDialog;
import com.huawei.mppdbide.view.utils.PasswordExpiryNotifier;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditConnection.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class EditConnection {
    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;
    @Inject
    private ECommandService commandService;
    @Inject
    private EHandlerService handlerService;

    /**
     * Instantiates a new edits the connection.
     */
    public EditConnection() {

    }

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {

        MPPDBIDELoggerUtility
                .info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_NEWDBCONNECTION_OPEN_CONNECTION_WIZARD));
        EditConnectionDialog connDialog = new EditConnectionDialog(shell, modelService, application, commandService,
                handlerService);

        connDialog.open();

        boolean flag = connDialog.isFlag();
        if (flag) {
            Database database = DBConnProfCache.getInstance().getDbForProfileId(connDialog.getProfileId());
            PasswordExpiryNotifier passwordExpiryNotifier = new PasswordExpiryNotifier(database);
            passwordExpiryNotifier.checkAndNotifyPasswordExpiry();
            connDialog.setFlag(false);
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        // DTS2014102907592 satrt
        return UIVersionHandler.isVersionCompatible();
        // DTS2014102907592 end
    }
}