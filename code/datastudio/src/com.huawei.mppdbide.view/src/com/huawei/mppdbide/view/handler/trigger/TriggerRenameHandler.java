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

package com.huawei.mppdbide.view.handler.trigger;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.TriggerMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.handler.UserInputDialogUIWorkerJob;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class
 * Description: The Class TriggerRenameHandler.
 *
 * @since 3.0.0
 */
public class TriggerRenameHandler {
    private StatusMessage statusMessage;
    private RenameTriggerWorker renameTriggerworker;

    /**
     * Execute
     *
     * @param Shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final TriggerMetaData selTrigger = TriggerUtils.getTrigger();

        UserInputDialog renameTriggerDialog = new RenameTriggerHandlerInner(shell, selTrigger);

        renameTriggerDialog.open();
    }

    /**
     * Can execute
     *
     * @return boolean if can execute
     */
    @CanExecute
    public boolean canExecute() {
        TriggerMetaData selTrigger = TriggerUtils.getTrigger();
        if (selTrigger == null) {
            return false;
        } else {
            return true;
        }
    }

    private final class RenameTriggerHandlerInner extends UserInputDialog {
        private TriggerMetaData selectTrigger;

        public RenameTriggerHandlerInner(Shell parent, Object serverObject) {
            super(parent, serverObject);
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
        public void onPresetupFailureUIAction(MPPDBIDEException e) {
            return;
        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TRIGGER);
        }

        @Override
        protected String getHeader() {
            Object obj = getObject();
            if (!(obj instanceof TriggerMetaData)) {
                return "";
            }
            selectTrigger = (TriggerMetaData) obj;
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TRIGGER_TITLE,
                    selectTrigger.getNamespace().getName() + "." + selectTrigger.getName());
        }

        @Override
        protected void performOkOperation() {
            renameTriggerworker = null;

            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            Object object = getObject();
            if (!(object instanceof TriggerMetaData)) {
                return;
            }
            selectTrigger = (TriggerMetaData) object;
            StatusMessage statMssage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_RENAME_TABLE));

            String userInput = getUserInput();
            if ("".equals(userInput)) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TABLE_NEW_NAME,
                        selectTrigger.getName()), false);
                if (bttmStatusBar != null) {
                    bttmStatusBar.hideStatusbar(getStatusMessage());
                }
                enableButtons();
                return;
            }

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TRIGGER_WAIT,
                    selectTrigger.getName()), true);

            renameTriggerworker = new RenameTriggerWorker(selectTrigger, userInput, this, statMssage);

            setStatusMessage(statMssage);
            StatusMessageList.getInstance().push(statMssage);
            if (bttmStatusBar != null) {
                bttmStatusBar.activateStatusbar();
            }
            renameTriggerworker.schedule();
            enableCancelButton();
        }

        @Override
        protected void cancelPressed() {
            performCancelOperation();
        }

        @Override
        protected void performCancelOperation() {
            if (renameTriggerworker != null && renameTriggerworker.getState() == Job.RUNNING) {
                int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG));
                if (returnValue == 0) {
                    renameTriggerworker.cancelJob();
                    renameTriggerworker = null;
                } else {
                    enableCancelButton();
                }
            } else {
                close();
            }
        }

        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_TRIGGERS, this.getClass());
        }
    }

    /**
     * Title: class
     * Description: The Class RenameTriggerWorker.
     */
    private static final class RenameTriggerWorker extends UserInputDialogUIWorkerJob {
        private TriggerMetaData trigger;
        private String newName;

        /**
         * Instantiates a new rename trigger worker.
         *
         * @param TriggerMetaData the trigger metadata
         * @param String the new name
         * @param UserInputDialog the dialog
         * @param StatusMessage the status msg
         */
        private RenameTriggerWorker(TriggerMetaData obj, String newName,
                UserInputDialog dialog, StatusMessage statusMsg) {
            super("Rename Trigger", null, dialog, statusMsg, obj.getName(), IMessagesConstants.RENAME_TABLE_ERROR,
                    IMessagesConstants.RENAME_TABLE_CONN_ERROR);
            this.trigger = obj;
            this.newName = newName;
        }

        @Override
        public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException {
            setConnInfra(PromptPrdGetConnection.getConnection(trigger.getDatabase()));
            trigger.execRename(newName, trigger.getTableoid(), trigger.getNamespace(), getConnInfra().getConnection());
            MPPDBIDELoggerUtility.info("Rename trigger succesfull ");
            trigger.getNamespace().loadTriggers(getConnInfra().getConnection());
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            IHandlerUtilities.pritnAndRefresh(trigger.getParent());
        }
    }

    /**
     * Gets the status message.
     *
     * @return StatusMessage the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param StatusMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMessage = statMessage;
    }
}
