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

package com.huawei.mppdbide.view.view.handler;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.Document;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.IViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ShowViewDDLHandler.
 *
 * @since 3.0.0
 */
public class ShowViewDDLHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        StatusMessage statusMessage = null;
        IViewMetaData view = IHandlerUtilities.getSelectedIViewObject();
        if (null != view) {

            if (!IHandlerUtilities.isDDLOperationsSupported(view.getDatabase())) {
                return;
            }

            statusMessage = new StatusMessage(MessageConfigLoader.getProperty(IMessagesConstants.SHOW_TABLE_DDL));
            BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bottomStatusBar != null) {
                bottomStatusBar.setStatusMessage(statusMessage.getMessage());
            }

            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForView(view.getName(),
                    view.getNameSpaceName(), view.getDatabase().getName(), view.getDatabase().getServerName(),
                    IMessagesConstants.SHOW_VIEW_DDL_PROGRESS_NAME);
            ShowViewDDLWorker showViewWorker = null;
            showViewWorker = new ShowViewDDLWorker(progressLabel, view, bottomStatusBar, statusMessage);
            showViewWorker.setTaskDB(view.getDatabase());
            StatusMessageList.getInstance().push(statusMessage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            showViewWorker.schedule();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        IViewMetaData view = IHandlerUtilities.getSelectedIViewObject();
        if (null != view) {
            return view.isDbConnected();
        }

        return false;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ShowViewDDLWorker.
     */
    private static final class ShowViewDDLWorker extends PromptPasswordUIWorkerJob {
        private IViewMetaData view;
        private String viewSrc;
        private BottomStatusBar bottomStatusBar;
        private StatusMessage statMessage;

        private ShowViewDDLWorker(String name, IViewMetaData view, BottomStatusBar bottomStatusBar,
                StatusMessage statusMessage) {
            super(name, MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.SHOW_DDL_FAILED_TITLE);
            this.view = view;
            this.bottomStatusBar = bottomStatusBar;
            this.statMessage = statusMessage;
        }

        @Override
        protected Database getDatabase() {
            return view.getDatabase();
        }

        @Override
        protected void passwordValidationFailed(MPPDBIDEException exception) {
            super.passwordValidationFailed(exception);
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                    .getProperty(IMessagesConstants.SHOW_TABLE_DDL_FAILED, view.getNameSpaceName(), view.getName())));
            MPPDBIDELoggerUtility.error("ShowViewDDLHandler: Validation failed.", exception);
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            Database db = getDatabase();
            if (null != db) {
                if (db.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)
                        && !this.view.isViewCodeLoaded() && getEncrpytedProfilePrd() != null) {
                    db.getServer().setPrd(getEncrpytedProfilePrd());
                }
                viewSrc = view.getDDL(db);
            }

            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            SQLTerminal terminal = UIElement.getInstance().createNewTerminal(view.getDatabase());
            if (null != terminal) {
                Document doc = new Document(viewSrc);
                terminal.getTerminalCore().setDocument(doc, 0);
                terminal.resetSQLTerminalButton();
                terminal.resetAutoCommitButton();
                terminal.setModified(true);
                terminal.setModifiedAfterCreate(true);
                terminal.registerModifyListener();
            }

        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.SHOW_DDL_FAILED_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.SHOW_TABLE_DDL_FAILED, view.getName(),
                            exception.getServerMessage()));
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception, view.getDatabase());

        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            String title = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_PROCESS_TITLE);
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, title, exception.getServerMessage());
        }

        @Override
        public void finalCleanupUI() {
            bottomStatusBar.hideStatusbar(statMessage);

        }
    }
}
