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

package org.opengauss.mppdbide.view.ui.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.ui.AlterDefaultDialog;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class AlterColumnDefaultDialog.
 *
 * @since 3.0.0
 */
public class AlterColumnDefaultDialog extends AlterDefaultDialog {
    private ColumnMetaData selectedColumn;
    private String clsName = "AlterColumnDefaultDialog";
    private AlterColumnDefaultWorker worker;

    /**
     * Instantiates a new alter column default dialog.
     *
     * @param parentShell the parent shell
     * @param columnMetaData the column meta data
     */
    public AlterColumnDefaultDialog(Shell parentShell, ColumnMetaData columnMetaData) {
        super(parentShell, columnMetaData);
        selectedColumn = columnMetaData;
        worker = null;
    }

    /**
     * Configure shell.
     *
     * @param newShell the new shell
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.ALTER_COLUMN_SHELL_TEXT,
                selectedColumn.getParentTable().getNamespace().getName(), selectedColumn.getParentTable().getName(),
                selectedColumn.getName()));
        newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_COLUMN, getClass()));
    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        okButton.setEnabled(false);
        cancelButton.setEnabled(false);

        if (UIConstants.OK_ID == buttonId) {
            worker = null;
            StringBuilder expr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            expr.append(textDefaultExpr.getText().trim());
            boolean isFunc = super.isDefaultValExp.getSelection();
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForColumn(selectedColumn.getName(),
                    selectedColumn.getParentTable().getName(), selectedColumn.getParentTable().getNamespace().getName(),
                    selectedColumn.getDatabase().getName(), selectedColumn.getDatabase().getServerName(),
                    IMessagesConstants.ALTER_COLUMN_DEFAULT_PROGRESS_NAME);
            worker = new AlterColumnDefaultWorker(progressLabel, selectedColumn, expr.toString(), isFunc,
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_SET_COLUMN_DEFAULT_VALUE), this);
            worker.schedule();
            cancelButton.setEnabled(true);
        } else if (UIConstants.CANCEL_ID == buttonId) {
            if (worker != null && worker.getState() == Job.RUNNING) {
                int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG,
                                selectedColumn.getName(), selectedColumn.getParentTable().getNamespace().getName(),
                                selectedColumn.getParentTable().getName()));

                if (0 == returnValue) {
                    worker.cancel();
                    worker = null;
                } else {
                    cancelButton.setEnabled(true);
                }
            } else {
                cancelPressed();
            }
        }
    }

    /**
     * Gets the cls name.
     *
     * @return the cls name
     */
    public String getClsName() {
        return clsName;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (objectBrowserModel != null) {
            objectBrowserModel.refreshObject(selectedColumn.getParentTable());
        }
        close();

        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.SET_COL_VAL_OF_DEFAULT_TYPE,
                        selectedColumn.getParentTable().getNamespace().getName(),
                        selectedColumn.getParentTable().getName(), selectedColumn.getName())));
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        commonErrorHandling(exception);
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        commonErrorHandling(exception);
    }

    /**
     * Common error handling.
     *
     * @param exception the exception
     */
    private void commonErrorHandling(MPPDBIDEException exception) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_STNG_COL_VAL),
                MessageConfigLoader.getProperty(IMessagesConstants.UNABLE_TO_SET_COL_VAL,
                        MPPDBIDEConstants.LINE_SEPARATOR, exception.getServerMessage()));
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.SET_COL_VAL_ERROR,
                        selectedColumn.getParentTable().getNamespace().getName(),
                        selectedColumn.getParentTable().getName(), selectedColumn.getName())));

        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    /**
     * On presetup failure UI action.
     *
     * @param exception the e
     */
    @Override
    public void onPresetupFailureUIAction(MPPDBIDEException exception) {
        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }
}
