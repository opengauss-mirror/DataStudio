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

package org.opengauss.mppdbide.view.ui;

import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.ui.table.AlterViewColumnDefaultWorker;
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
 * Description: The Class AlterViewColumnDefaultDialog.
 *
 * @since 3.0.0
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
