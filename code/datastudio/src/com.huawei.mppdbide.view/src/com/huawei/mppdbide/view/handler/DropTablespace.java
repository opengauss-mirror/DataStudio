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

package com.huawei.mppdbide.view.handler;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropTablespace.
 *
 * @since 3.0.0
 */
public class DropTablespace {
    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        Tablespace tablespace = IHandlerUtilities.getSelectedTablespace();
        if (null != tablespace) {

            int returnType = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_TABLESPACE_DIA_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_TABLESPACE, tablespace.getName()));
            if (returnType != 0) {
                return;
            }
            StatusMessage statMssage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DROP_TABLESPACE));

            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();

            DropTablespaceWorker worker = new DropTablespaceWorker(tablespace, statMssage, bttmStatusBar);
            StatusMessageList.getInstance().push(statMssage);
            if (null != bttmStatusBar) {
                bttmStatusBar.activateStatusbar();
            }
            worker.schedule();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DropTablespaceWorker.
     */

    private static final class DropTablespaceWorker extends UIWorkerJob {
        private Tablespace tablespace;
        private StatusMessage statusMsg;
        private BottomStatusBar statusBar;

        /**
         * Instantiates a new drop tablespace worker.
         *
         * @param obj the obj
         * @param message the message
         * @param statusBar the status bar
         */
        private DropTablespaceWorker(Tablespace obj, StatusMessage message, BottomStatusBar statusBar) {
            super("Drop Tablespace", null);
            this.tablespace = obj;
            this.statusMsg = message;
            this.statusBar = statusBar;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            tablespace.dropTablespace(tablespace.getServer().getAnotherConnection(tablespace.getOid()));
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.DROP_TABLESPACE_SUCCESS,
                            tablespace.getServer().getServerConnectionInfo().getConectionName(),
                            tablespace.getName())));
            MPPDBIDELoggerUtility.info("Dropped tablespace successfully.");
            IHandlerUtilities.pritnAndRefresh(tablespace.getServer().getTablespaceGroup());
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHEN_DROPPING_TABLESPACE),
                    MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERR_WHEN_DROPPING_TABLESPACE,
                            tablespace.getServer().getServerConnectionInfo().getConectionName(), tablespace.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getError(
                            MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERR_WHEN_DROPPING_TABLESPACE,
                                    tablespace.getServer().getServerConnectionInfo().getConectionName(),
                                    tablespace.getName())));

        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            String errorMsg = e.getServerMessage();
            int index = StringUtils.indexOf(errorMsg, "ERROR:");
            if (index < 0) {
                index = 0;
            }

            String errorTip = StringUtils.substring(errorMsg, index);
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHEN_DROPPING_TABLESPACE), errorTip);
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(errorTip));
        }

        @Override
        public void finalCleanup() {

        }

        @Override
        public void finalCleanupUI() {
            statusBar.hideStatusbar(statusMsg);

        }

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Tablespace selectedTablespace = IHandlerUtilities.getSelectedTablespace();
        if (selectedTablespace == null) {
            return false;
        }

        if (!IHandlerUtilities.getActiveDB(selectedTablespace.getServer())) {
            return false;
        }
        return true;
    }

}
