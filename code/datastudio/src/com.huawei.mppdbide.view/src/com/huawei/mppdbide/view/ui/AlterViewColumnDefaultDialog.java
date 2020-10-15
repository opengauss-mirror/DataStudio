/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.table.AlterViewColumnDefaultWorker;
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
 * Description: The Class AlterViewColumnDefaultDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class AlterViewColumnDefaultDialog extends AlterDefaultDialog {
    private ViewColumnMetaData column;
    private String newDefaultValue;
    private String clsName = "AlterViewColumnDefaultDialog";

    /**
     * Instantiates a new alter view column default dialog.
     *
     * @param shell the shell
     * @param viewColumn the view column
     */
    public AlterViewColumnDefaultDialog(Shell shell, ViewColumnMetaData viewColumn) {
        super(shell, viewColumn);
        this.column = viewColumn;
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
            this.newDefaultValue = textDefaultExpr.getText().trim();
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForColumn(column.getName(),
                    column.getParent().getName(), column.getParent().getNamespace().getName(),
                    column.getDatabase().getName(), column.getDatabase().getServerName(),
                    IMessagesConstants.ALTER_VIEWCOLUMN_DEFAULT_PROGRESS_NAME);
            AlterViewColumnDefaultWorker worker = new AlterViewColumnDefaultWorker(progressLabel, column,
                    this.newDefaultValue,
                    MessageConfigLoader.getProperty(IMessagesConstants.SET_VIEW_COL_DEFAULT_STATUS_BAR), this);
            worker.schedule();
        } else {
            close();
        }
    }

    /**
     * Configure shell.
     *
     * @param newShell the new shell
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.VIEW_COLUMN_SET_DEFAULT_TITLE,
                column.getParent().getNamespace().getName(), column.getParent().getName(), column.getName()));
        newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_COLUMN, this.getClass()));
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
        ViewMetaData view = this.column.getParent();
        Namespace ns = view.getNamespace();
        String msg = MessageConfigLoader.getProperty(IMessagesConstants.SET_COL_VAL_OF_DEFAULT_TYPE, ns.getName(),
                view.getName(), this.column.getName());
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(msg));
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (objectBrowserModel != null) {
            objectBrowserModel.refreshObject(view);
        }
        close();
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(msg));
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        ViewMetaData view = this.column.getParent();
        Namespace ns = view.getNamespace();
        exceptionUIHandler(exception.getServerMessage());
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.SET_COL_VAL_ERROR,
                        ns.getName(), view.getName(), this.column.getName())));
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        ViewMetaData view = this.column.getParent();
        Namespace ns = view.getNamespace();
        exceptionUIHandler(exception.getServerMessage());
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.SET_COL_VAL_ERROR,
                        ns.getName(), view.getName(), this.column.getName())));
    }

    /**
     * Exception UI handler.
     *
     * @param serverMsg the server msg
     */
    public void exceptionUIHandler(String serverMsg) {
        String title = MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_STNG_COL_VAL);
        String msg = MessageConfigLoader.getProperty(IMessagesConstants.UNABLE_TO_SET_COL_VAL,
                MPPDBIDEConstants.LINE_SEPARATOR, serverMsg);

        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, title, msg);

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
