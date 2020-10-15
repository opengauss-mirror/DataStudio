/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.autosave.AutoSaveManager;
import com.huawei.mppdbide.view.data.DSViewDataManager;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.ui.ObjectBrowserFilterUtility;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExitApplication.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExitApplication {

    /**
     * 
     * Title: class
     * 
     * Description: The Class SqlHistoryPersistenece.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class SqlHistoryPersistenece implements Runnable {
        @Override
        public void run() {

            SQLHistoryFactory.getInstance().purgeHistorybeforeClose();
        }
    }

    /**
     * Execute.
     *
     * @param workbench the workbench
     */
    @Execute
    public void execute(IWorkbench workbench) {

        MPPDBIDELoggerUtility
                .info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_EXITAPPLICATION_EXIT_CLICKED));
        final String forceExit = "     " + MessageConfigLoader.getProperty(IMessagesConstants.FORCE_EXIT_DATSTUDIO)
                + "     ";
        final String gracefullExit = "     "
                + MessageConfigLoader.getProperty(IMessagesConstants.GRACEFULL_EXIT_DATASTUDIO) + "     ";

        final String cancel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_MSG) + "     ";

        int returnVal = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.EXIT_APPLICATION),
                MessageConfigLoader.getProperty(IMessagesConstants.EXIT_APPLICATION_CONFIRMATION), forceExit,
                gracefullExit, cancel);

        if (0 == returnVal) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().cleanUpSecurityOnWindowClose();
            DBConnProfCache.getInstance().closeAllNodes();
            ObjectBrowserFilterUtility.getInstance().clearServerList();
            DSViewDataManager.getInstance().setWbGoingToClose(true);
            boolean applicationExited = workbench.close();
            if (applicationExited) {
                MPPDBIDELoggerUtility.info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_APPLICATION_EXITED));
            }
        } else if (returnVal == 1) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().cleanUpSecurityOnWindowClose();
            BusyIndicator.showWhile(Display.getDefault(), new SqlHistoryPersistenece());
            AutoSaveManager.getInstance().gracefulExit();
            DSViewDataManager.getInstance().setWbGoingToClose(true);
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
        /* Has to change during state machine */
        return true;
    }

}
