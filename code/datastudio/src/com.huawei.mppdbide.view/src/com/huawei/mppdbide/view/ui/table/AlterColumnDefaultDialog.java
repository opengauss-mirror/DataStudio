/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.AlterDefaultDialog;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class AlterColumnDefaultDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
