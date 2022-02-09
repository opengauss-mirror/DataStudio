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

package org.opengauss.mppdbide.view.view.handler;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.Document;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.IViewMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

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
