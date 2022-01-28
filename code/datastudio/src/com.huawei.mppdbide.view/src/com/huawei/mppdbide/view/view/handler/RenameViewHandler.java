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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.view.handler.ViewWorkerJob.VIEWOPTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameViewHandler.
 *
 * @since 3.0.0
 */
public class RenameViewHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        ViewMetaData view = IHandlerUtilities.getSelectedViewObject();
        if (null != view) {
            PasswordDialog dialog = new PasswordDialog(Display.getDefault().getActiveShell(), view);
            dialog.open();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ViewMetaData view = IHandlerUtilities.getSelectedViewObject();
        if (null != view) {
            Namespace ns = (Namespace) view.getNamespace();
            if (null != ns && ns.getDatabase().isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class PasswordDialog.
     */
    private static class PasswordDialog extends UserInputDialog {
        private ViewMetaData view;
        private ViewWorkerJob job;

        /**
         * Instantiates a new password dialog.
         *
         * @param parent the parent
         * @param serverObject the server object
         */
        protected PasswordDialog(Shell parent, Object serverObject) {
            super(parent, serverObject);
            this.view = (ViewMetaData) serverObject;
        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_VIEW_TITLE);
        }

        @Override
        protected String getHeader() {
            return MessageConfigLoader.getProperty(IMessagesConstants.ENTER_NEW_VIEW_NAME,
                    view.getNamespace().getDisplayName() + '.' + view.getName());
        }

        @Override
        protected void performOkOperation() {
            job = null;
            String newName = getUserInput();
            job = new ViewWorkerJob("Rename View", VIEWOPTYPE.RENAME_VIEW, "", view, newName, this);

            job.schedule();
            enableCancelButton();
        }

        @Override
        protected void cancelPressed() {
            performCancelOperation();
        }

        @Override
        protected void performCancelOperation() {
            if (job != null && job.getState() == Job.RUNNING) {
                int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG));

                if (0 == returnValue) {
                    job.cancelJob();
                    job = null;
                } else {
                    enableCancelButton();
                }
            } else {
                close();
            }

        }

        @Override
        public void onSuccessUIAction(Object obj) {
            return;
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            return;
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            return;
        }

        @Override
        public void onPresetupFailureUIAction(MPPDBIDEException exception) {
            return;
        }

        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_VIEW, this.getClass());
        }

    }
}
