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

package com.huawei.mppdbide.view.ui.table;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.FontAndColorUtility;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class AddColumn.
 *
 * @since 3.0.0
 */
public class AddColumn extends Dialog implements IDialogWorkerInteraction {
    private Shell crntShell;

    private TableMetaData tableMetaData;

    /**
     * The column UI.
     */
    protected ColumnUI columnUI;

    /**
     * The ok button.
     */
    protected Button okButton;

    /**
     * The cancel button.
     */
    protected Button cancelButton;

    /**
     * The lbl lblerrormsg.
     */
    protected Label lblLblerrormsg;
    private ColumnMetaData newcolumn;
    private StatusMessage statusMessage;

    /**
     * Instantiates a new adds the column.
     *
     * @param shell the shell
     * @param tableMetaData the table meta data
     */
    @Inject
    public AddColumn(Shell shell, TableMetaData tableMetaData) {
        super(shell);
        this.tableMetaData = tableMetaData;
    }

    /**
     * Open.
     *
     * @return the object
     */
    public Object open() {
        /*
         * For adding column only for create table
         */

        Shell parent = getParent();

        crntShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        crntShell.setLayout(new GridLayout(1, false));
        GridData crntShellGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        crntShell.setLayoutData(crntShellGD);
        crntShell.setSize(650, 480);
        crntShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.ADD_NEW_COLUMN));

        columnUI = new ColumnUI(tableMetaData.getNamespace().getDatabase(), tableMetaData);

        Composite mainComposite = new Composite(crntShell, SWT.NONE);
        mainComposite.setLayout(new GridLayout(1, false));
        GridData mainCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        mainComposite.setLayoutData(mainCompositeGD);

        columnUI.createColumnInfoGui(crntShell, false);

        Composite btnsAndErrorComposite = new Composite(crntShell, SWT.NONE);
        btnsAndErrorComposite.setLayout(new GridLayout(3, false));
        GridData btnsAndErrorCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnsAndErrorComposite.setLayoutData(btnsAndErrorCompositeGD);

        lblLblerrormsg = new Label(btnsAndErrorComposite, SWT.NONE);
        lblLblerrormsg.setForeground(FontAndColorUtility.getColor(SWT.COLOR_RED));
        GridData lblLblerrormsgGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        lblLblerrormsgGD.horizontalIndent = 10;
        lblLblerrormsgGD.heightHint = 20;
        lblLblerrormsgGD.widthHint = 500;
        lblLblerrormsg.setLayoutData(lblLblerrormsgGD);

        okButton = new Button(btnsAndErrorComposite, SWT.NONE);
        GridData okBtnGD = new GridData(SWT.NONE, SWT.NONE, true, true);
        okButton.setLayoutData(okBtnGD);
        performOkPressed();
        okButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_ADD));

        cancelButton = new Button(btnsAndErrorComposite, SWT.NONE);
        GridData cancelBtnGD = new GridData(SWT.NONE, SWT.NONE, true, true);
        cancelButton.setLayoutData(cancelBtnGD);
        performCancel();
        cancelButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC));

        crntShell.open();
        Display dply = parent.getDisplay();
        boolean hasDispsed = crntShell.isDisposed();
        while (!hasDispsed) {
            if (!dply.readAndDispatch()) {
                dply.sleep();
            }
            hasDispsed = crntShell.isDisposed();
        }

        return crntShell;
    }

    /**
     * Perform cancel.
     */
    protected void performCancel() {
        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                crntShell.dispose();
            }
        });
    }

    /**
     * Perform ok pressed.
     */
    protected void performOkPressed() {

        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                newcolumn = columnUI.getDBColumn(null, false, tableMetaData.getOrientation());
                lblLblerrormsg.setText("");
                if (null != newcolumn) {
                    if (null == newcolumn.getName() || "".equals(newcolumn.getName())) {
                        lblLblerrormsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.PLS_ENTER_COL_NAME));
                    } else if (null == newcolumn.getDataType()) {
                        lblLblerrormsg
                                .setText(MessageConfigLoader.getProperty(IMessagesConstants.PLS_SELECT_DATA_TYPE));
                    } else {
                        okButton.setEnabled(false);
                        String progressLabel = ProgressBarLabelFormatter.getProgressLabelForColumn(newcolumn.getName(),
                                newcolumn.getParentTable().getName(),
                                newcolumn.getParentTable().getNamespace().getName(),
                                newcolumn.getParentTable().getDatabaseName(),
                                newcolumn.getParentTable().getServerName(),
                                IMessagesConstants.ADD_COLUMN_PROGRESS_NAME);
                        AddColumnWorker worker = new AddColumnWorker(progressLabel, newcolumn,
                                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_ADD_COLUMN),
                                AddColumn.this);
                        worker.schedule();
                    }
                } else {
                    lblLblerrormsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.PLS_ENTER_COL_NAME));
                }
            }
        });

    }

    /**
     * Close.
     */
    protected void close() {
        if (!crntShell.isDisposed()) {
            crntShell.dispose();
        }
    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMessage = statMessage;
    }

    /**
     * Gets the ok button.
     *
     * @return the ok button
     */
    public Button getOkButton() {
        return this.okButton;
    }

    /**
     * Gets the cancel button.
     *
     * @return the cancel button
     */
    public Button getCancelButton() {
        return this.cancelButton;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.ADD_COLUMN,
                        newcolumn.getParentTable().getBottombarDisplayName(), newcolumn.getName())));
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (objectBrowserModel != null) {
            objectBrowserModel.refreshObject(newcolumn.getParentTable());
        }
        crntShell.dispose();
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
     * @param ex the ex
     */
    private void commonErrorHandling(MPPDBIDEException ex) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_ADDING_COL),
                MessageConfigLoader.getProperty(IMessagesConstants.UNABLE_TO_ADD_COL, MPPDBIDEConstants.LINE_SEPARATOR,
                        null == ex.getServerMessage() ? "" : ex.getServerMessage().split("Position:")[0]));
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.ADD_COLUMN_ERROR,
                        newcolumn.getParentTable().getDisplayName(), newcolumn.getName())));
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

    /**
     * Gets the shell.
     *
     * @return the shell
     */
    @Override
    public Shell getShell() {
        return crntShell;
    }
}
