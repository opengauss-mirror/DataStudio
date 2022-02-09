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

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.autosave.AutoSaveManager;
import org.opengauss.mppdbide.view.data.DSViewDataManager;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.ui.ObjectBrowserFilterUtility;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExitApplication.
 *
 * @since 3.0.0
 */
public class ExitApplication {

    /**
     * 
     * Title: class
     * 
     * Description: The Class SqlHistoryPersistenece.
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
