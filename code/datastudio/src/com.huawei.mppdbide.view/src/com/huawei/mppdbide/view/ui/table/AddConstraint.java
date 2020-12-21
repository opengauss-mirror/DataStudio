/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import java.util.ArrayList;

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
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.FontAndColorUtility;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class AddConstraint.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class AddConstraint extends Dialog implements IDialogWorkerInteraction {
    private Shell currentShell;

    private TableMetaData tableMetaData;

    /**
     * The constraint UI.
     */
    protected ConstraintUI constraintUI;

    /**
     * The ok button.
     */
    protected Button okButton;

    /**
     * The cancel button.
     */
    protected Button cancelButton;

    /**
     * The lbl errormsg.
     */
    protected Label lblErrormsg;
    private ConstraintMetaData newconstraint;

    /**
     * Instantiates a new adds the constraint.
     *
     * @param shell the shell
     * @param tableMetaData the table meta data
     */
    @Inject
    public AddConstraint(Shell shell, TableMetaData tableMetaData) {
        super(shell);
        this.tableMetaData = tableMetaData;
    }

    /**
     * Open.
     *
     * @return the object
     */
    public Object open() {
        Shell parent = getParent();

        getCurrentShell(parent);

        constraintUI = new ConstraintUI(tableMetaData.getNamespace().getDatabase(),
                tableMetaData.getOrientation());

        Composite mainComposite = new Composite(currentShell, SWT.NONE);
        mainComposite.setLayout(new GridLayout(1, false));
        GridData mainCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, false);
        mainComposite.setLayoutData(mainCompositeGD);

        constraintUI.createConstraintsInfoGui(mainComposite);

        OLAPObjectList<ColumnMetaData> columns = tableMetaData.getColumns();

        addColumnInConstraintUi(columns);

        Composite btnsAndErrorComposite = new Composite(currentShell, SWT.NONE);
        btnsAndErrorComposite.setLayout(new GridLayout(3, false));
        GridData btnsAndErrorCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        btnsAndErrorComposite.setLayoutData(btnsAndErrorCompositeGD);

        getErrorMsgLbl(btnsAndErrorComposite);

        okButton = new Button(btnsAndErrorComposite, SWT.NONE);
        GridData okButtonGD = new GridData(SWT.NONE, SWT.NONE, true, true);
        okButton.setLayoutData(okButtonGD);
        performOkPressed();
        okButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_ADD));

        cancelButton = new Button(btnsAndErrorComposite, SWT.NONE);
        GridData cancelButtonGD = new GridData(SWT.NONE, SWT.NONE, true, true);
        cancelButton.setLayoutData(cancelButtonGD);
        performCancel();
        cancelButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC));

        currentShell.open();
        validateForShellDispose(parent);

        return currentShell;
    }

    private void getErrorMsgLbl(Composite btnsAndErrorComposite) {
        lblErrormsg = new Label(btnsAndErrorComposite, SWT.WRAP);
        lblErrormsg.setForeground(FontAndColorUtility.getColor(SWT.COLOR_RED));
        GridData lblErrormsgGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        lblErrormsgGD.horizontalIndent = 10;
        lblErrormsgGD.widthHint = 500;
        lblErrormsg.setLayoutData(lblErrormsgGD);
        lblErrormsg.setText("");
    }

    private void getCurrentShell(Shell parent) {
        currentShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        currentShell.setLayout(new GridLayout(1, false));
        GridData currentShellGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        currentShell.setLayoutData(currentShellGD);
        currentShell.setSize(630, 420);
        currentShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.ADD_NEW_CONSTRAINT));
        currentShell.setImage(IconUtility.getIconImage(IiconPath.ICO_CONSTRAINTS, this.getClass()));
    }

    private void validateForShellDispose(Shell parent) {
        Display display = parent.getDisplay();
        boolean isDisposed = currentShell.isDisposed();
        while (!isDisposed) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
            isDisposed = currentShell.isDisposed();
        }
    }

    private void addColumnInConstraintUi(OLAPObjectList<ColumnMetaData> columns) {
        if (null != columns && columns.getList().size() > 0) {
            ArrayList<ColumnMetaData> list = columns.getList();
            int clmIndex = 0;
            int size = list.size();
            for (; clmIndex < size; clmIndex++) {
                constraintUI.addColumn(list.get(clmIndex).columnDetails(3, false));
            }
        }
    }

    /**
     * Perform cancel.
     */
    protected void performCancel() {
        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                currentShell.dispose();
            }
        });
    }

    /**
     * Perform ok pressed.
     */
    protected void performOkPressed() {
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {

                newconstraint = constraintUI.getConstraint(true);
                lblErrormsg.setText("");

                if (null != newconstraint) {
                    String progressLabel = ProgressBarLabelFormatter.getProgressLabelForColumn(newconstraint.getName(),
                            tableMetaData.getName(), tableMetaData.getNamespace().getName(),
                            tableMetaData.getDatabase().getDbName(), tableMetaData.getDatabase().getServerName(),
                            IMessagesConstants.ADD_CONSTRAINT_PROGRESS_NAME);
                    AddConstraintWorker worker = new AddConstraintWorker(progressLabel, newconstraint, tableMetaData,
                            MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_ADD_CONSTRAINT),
                            AddConstraint.this);
                    worker.schedule();
                } else {
                    lblErrormsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.PLS_ENTER_TABLE_FOR_CONS));
                }
            }
        });
    }

    /**
     * Close.
     */
    protected void close() {
        if (!currentShell.isDisposed()) {
            currentShell.dispose();
        }
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
            objectBrowserModel.refreshObject(tableMetaData);
        }
        boolean emptyConstraintName = (newconstraint.getName() == null)
                || (newconstraint.getName().trim().length() == 0);
        if (emptyConstraintName) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.NEW_NONAME_CONSTRAINT_CREATED,
                            tableMetaData.getNamespace().getName(), tableMetaData.getName())));
        } else {
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getInfo(MessageConfigLoader.getProperty(
                            IMessagesConstants.NEW_CONSTRAINT_CREATED, tableMetaData.getNamespace().getName(),
                            tableMetaData.getName(), newconstraint.getName())));
        }

        currentShell.dispose();

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
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_ADDING_CONSTRAINT),

                MessageConfigLoader.getProperty(IMessagesConstants.UNABLE_TO_ADD_CONS, MPPDBIDEConstants.LINE_SEPARATOR,
                        null == ex.getServerMessage() ? "" : ex.getServerMessage().split("Position:")[0]));
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.NEW_CONSTRAINT_CREATED_ERROR,
                        tableMetaData.getNamespace().getName(), tableMetaData.getName())));

        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    /**
     * On presetup failure UI action.
     *
     * @param exception the exception
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
        return currentShell;
    }
}
