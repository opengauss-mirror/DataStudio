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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.bl.serverdatacache.TablespaceProperties;
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
import com.huawei.mppdbide.view.ui.connectiondialog.UserIputSetOptions;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetTablespaceOption.
 *
 * @since 3.0.0
 */
public class SetTablespaceOption {
    private Tablespace tablespace = null;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(Shell shell) {
        tablespace = IHandlerUtilities.getSelectedTablespace();
        if (tablespace != null && (tablespace.getServer() == null)) {
            return;
        }
        UserIputSetOptions setOption = new UserIputSetOptions(shell, tablespace) {

            @Override
            protected void performOkOperation() {
                final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
                StatusMessage statMssage = new StatusMessage(
                        MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_OPTION_TABLESPACE));
                TablespaceProperties properties = new TablespaceProperties(tablespace.getQualifiedObjectName(),
                        getSeqInputtext(), getrandomInputtext());
                String qry = properties.buildSetOptionQry();
                SetoptionWorker worker = new SetoptionWorker(tablespace, qry, this, statMssage, bttmStatusBar);
                StatusMessageList.getInstance().push(statMssage);
                if (bttmStatusBar != null) {
                    bttmStatusBar.activateStatusbar();
                }
                worker.schedule();

            }

            @Override
            public boolean close() {
                return super.close();
            }

            @Override
            protected String getWindowTitle() {
                return MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_OPTION);
            }
        };
        setOption.open();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SetoptionWorker.
     */
    private static final class SetoptionWorker extends UIWorkerJob {
        private Tablespace tablespace;
        private String query;
        private UserIputSetOptions dialog;
        private StatusMessage message;
        private BottomStatusBar statusBar;

        /**
         * Instantiates a new setoption worker.
         *
         * @param tablespace the tablespace
         * @param query the query
         * @param dialog the dialog
         * @param message the message
         * @param statusBar the status bar
         */
        private SetoptionWorker(Tablespace tablespace, String query, UserIputSetOptions dialog, StatusMessage message,
                BottomStatusBar statusBar) {
            super("Set Options", null);
            this.tablespace = tablespace;
            this.query = query;
            this.dialog = dialog;
            this.message = message;
            this.statusBar = statusBar;

        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            tablespace.setTablespaceOption(query, tablespace.getServer().getAnotherConnection(tablespace.getOid()));
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_OPTION_SUCCESS,
                            tablespace.getServer().getServerConnectionInfo().getConectionName(),
                            tablespace.getName())));
            statusBar.hideStatusbar(message);
            IHandlerUtilities.pritnAndRefresh(tablespace.getServer().getTablespaceGroup());
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            dialog.printMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERR_DURING_SETTING_TABLESPACE_OPTION,
                            tablespace.getServer().getServerConnectionInfo().getConectionName(), tablespace.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getError(MessageConfigLoader.getProperty(
                            IMessagesConstants.CONNECTION_ERR_DURING_SETTING_TABLESPACE_OPTION,
                            tablespace.getServer().getServerConnectionInfo().getConectionName(),
                            tablespace.getName())));
            statusBar.hideStatusbar(message);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            dialog.printMessage(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_OPTION_ERROR,
                    tablespace.getServer().getServerConnectionInfo().getConectionName(), tablespace.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.SET_TABLESPACE_OPTION_ERROR,
                            tablespace.getServer().getServerConnectionInfo().getConectionName(),
                            tablespace.getName())));
            statusBar.hideStatusbar(message);
            MPPDBIDELoggerUtility.error("SetTablespaceOption: Set tablespace option failed. ", exception);
        }

        @Override
        public void finalCleanup() {

        }

        @Override
        public void finalCleanupUI() {
            MPPDBIDELoggerUtility.info("Tablespace options are successfully changed ");
            statusBar.hideStatusbar(message);

        }

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Tablespace tblspc = IHandlerUtilities.getSelectedTablespace();
        Server server = tblspc != null ? tblspc.getServer() : null;
        return server != null && server.isAleastOneDbConnected();
    }

}
