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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableOrientation;
import com.huawei.mppdbide.bl.serverdatacache.TableValidatorRules;
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
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateIndexDialog.
 *
 * @since 3.0.0
 */
public class CreateIndexDialog extends Dialog implements IDialogWorkerInteraction {
    private TableMetaData tableMetaData;

    /**
     * The lbl generated query.
     */
    protected Label lblGeneratedQuery;

    /**
     * The lbl query.
     */
    protected Label lblQuery;

    /**
     * The btn cancel.
     */
    protected Button btnCancel;

    /**
     * The btn create index.
     */
    protected Button btnCreateIndex;
    private IndexMetaData idx;

    /**
     * The ui.
     */
    protected IndexUI ui;
    private TableUIValidator uIValidator;
    private TableValidatorRules validatorRules;
    private Composite compositeIndices;

    /**
     * Gets the idx.
     *
     * @return the idx
     */
    public IndexMetaData getIdx() {
        return idx;
    }

    /**
     * Sets the idx.
     *
     * @param idx the new idx
     */
    public void setIdx(IndexMetaData idx) {
        this.idx = idx;
    }

    /**
     * Instantiates a new creates the index dialog.
     *
     * @param parent the parent
     * @param tbl the tbl
     * @param server the server
     */
    public CreateIndexDialog(Shell parent, TableMetaData tbl, Server server) {
        super(parent);
        this.tableMetaData = tbl;

        if (this.tableMetaData instanceof PartitionTable) {
            ui = new IndexUIPartitionTable(this.tableMetaData, server);
        } else {
            ui = new IndexUI(this.tableMetaData, server);
        }
        setDefaultImage(IconUtility.getIconImage(IiconPath.ICO_INDEX, this.getClass()));
        this.validatorRules = new TableValidatorRules(tableMetaData);
        this.uIValidator = new TableUIValidator(ui, null, validatorRules, null, null);
    }

    /**
     * Configure shell.
     *
     * @param newShell the new shell
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_INDEX));
        newShell.setSize(630, 600);
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        final ScrolledComposite mainSc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        GridData mainScGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        mainSc.setLayoutData(mainScGD);

        compositeIndices = new Composite(mainSc, SWT.BORDER);
        mainSc.setContent(compositeIndices);
        compositeIndices.setLayout(new GridLayout(1, false));
        GridData compositeIndicesGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositeIndices.setLayoutData(compositeIndicesGD);

        ui.createUI(compositeIndices);

        registerUniqueIndex(ui, tableMetaData);

        createGenerateQuery(compositeIndices);

        createBelowButtons();

        /* Just a place holder to give space around elements in dialog */
        Label lblSpaceHolder = new Label(compositeIndices, SWT.NONE);
        lblSpaceHolder.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_INDEX_SET_TEXT));
        lblSpaceHolder.pack();
        lblSpaceHolder.setVisible(false);

        enableDisableIndexComponents();

        mainSc.setExpandHorizontal(true);
        mainSc.setExpandVertical(true);
        mainSc.setMinSize(compositeIndices.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mainSc.pack();

        return parent;
    }

    /**
     * Register unique index listener
     *
     * @param IndexUI the index ui
     * @param TableMetaData the tableMetaData
     */
    private void registerUniqueIndex(IndexUI ui, TableMetaData tableMetaData) {
        CreateTable.registerUniqueIndexButtonListener(ui, tableMetaData);
    }

    private void createBelowButtons() {
        Composite belowButtonsComposite = new Composite(compositeIndices, SWT.NONE);
        belowButtonsComposite.setLayout(new GridLayout(4, false));
        GridData belowButtonsCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, false);
        belowButtonsCompositeGD.verticalAlignment = SWT.BOTTOM;
        belowButtonsComposite.setLayoutData(belowButtonsCompositeGD);

        Label emptyLabel = new Label(belowButtonsComposite, SWT.NONE);
        GridData emptyLabelGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        emptyLabelGD.widthHint = 200;
        emptyLabel.setLayoutData(emptyLabelGD);

        createPreviewButton(belowButtonsComposite);
        createIndexButton(belowButtonsComposite);
        createCancelButton(belowButtonsComposite);
    }

    /**
     * Creates the generate query.
     *
     * @param comp the comp
     */
    private void createGenerateQuery(Composite comp) {

        Composite gerenateQueryComposite = new Composite(comp, SWT.NONE);
        gerenateQueryComposite.setLayout(new GridLayout(1, false));
        GridData gerenateQueryCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        gerenateQueryCompositeGD.horizontalSpan = 4;
        gerenateQueryComposite.setLayoutData(gerenateQueryCompositeGD);

        lblGeneratedQuery = new Label(gerenateQueryComposite, SWT.NONE);
        lblGeneratedQuery.setFont(FontAndColorUtility.getFont("Segoe UI", 9, SWT.BOLD, comp));
        lblGeneratedQuery.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_INDEX_GENERATE_QUERY));
        lblGeneratedQuery.pack();
        lblGeneratedQuery.setVisible(false);

        lblQuery = new Label(gerenateQueryComposite, SWT.WRAP);
        lblQuery.setText("");
        lblQuery.setFont(FontAndColorUtility.getFont("Segoe UI", 9, SWT.NORMAL, comp));
        GridData lblQueryGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        lblQuery.setLayoutData(lblQueryGD);
        lblQuery.setVisible(false);
    }

    /**
     * Creates the preview button.
     *
     * @param comp the comp
     */
    private void createPreviewButton(Composite comp) {
        Button btnPreviewQuery = new Button(comp, SWT.END);
        GridData btnPreviewQueryGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnPreviewQuery.setLayoutData(btnPreviewQueryGD);
        btnPreviewQuery.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_INDEX_PREVIEW));
        btnPreviewQuery.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                showQuery();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
    }

    /**
     * Creates the cancel button.
     *
     * @param comp the comp
     */
    private void createCancelButton(Composite comp) {
        btnCancel = new Button(comp, SWT.END);
        GridData btnCancelGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnCancel.setLayoutData(btnCancelGD);
        btnCancel.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.NORMAL, comp));
        btnCancel.setText(MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC));
        btnCancel.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                close();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // Nothing to do
            }
        });
    }

    /**
     * Creates the index button.
     *
     * @param comp the comp
     */
    private void createIndexButton(Composite comp) {
        btnCreateIndex = new Button(comp, SWT.END);
        GridData btnCreateIndexGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnCreateIndex.setLayoutData(btnCreateIndexGD);
        btnCreateIndex.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_INDEX_BTN));
        performOkPressed();
    }

    /**
     * Perform ok pressed.
     */
    protected void performOkPressed() {
        btnCreateIndex.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                try {
                    idx = ui.getIndexMetaData();
                } catch (DatabaseOperationException e1) {
                    showError(e1.getMessage());
                    btnCancel.setEnabled(true);
                    return;
                }
                String progressLabel = ProgressBarLabelFormatter.getProgressLabelForColumn(idx.getName(),
                        idx.getTable().getName(), idx.getNamespace().getName(), idx.getDatabase().getName(),
                        idx.getDatabase().getServerName(), IMessagesConstants.CREATE_INDEX_PROGRESS_NAME);
                CreateIndexWorker worker = new CreateIndexWorker(progressLabel, idx, tableMetaData,
                        MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CREATE_INDEX),
                        CreateIndexDialog.this);
                worker.schedule();

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }

        });
    }

    /**
     * Show error.
     *
     * @param msg the msg
     */
    protected void showError(String msg) {
        lblQuery.setForeground(FontAndColorUtility.getColor(SWT.COLOR_RED));
        lblQuery.setText(msg);
        lblQuery.setVisible(true);
        lblGeneratedQuery.setVisible(false);
    }

    /**
     * Show query.
     */
    private void showQuery() {
        lblGeneratedQuery.setVisible(true);
        lblQuery.setVisible(true);

        lblQuery.setForeground(FontAndColorUtility.getColor(SWT.COLOR_BLACK));

        try {
            String query = ui.getQuery();
            lblQuery.setText(query);
        } catch (DatabaseOperationException e) {
            lblQuery.setForeground(FontAndColorUtility.getColor(SWT.COLOR_RED));
            lblQuery.setText(e.getMessage());
            return;
        }

    }

    /**
     * Enable disable index components.
     */
    private void enableDisableIndexComponents() {
        if (!validatorRules.enableDisable()) {
            uIValidator.indexHandleRowColumnSelection(compositeIndices);
        }
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                .getProperty(IMessagesConstants.CREATE_INDEX_SUCCESS, tableMetaData.getDisplayName(), idx.getName())));
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (objectBrowserModel != null) {
            objectBrowserModel.refreshObject(idx.getTable());
        }
        close();

    }

    /**
     * On critical exception UI action.
     *
     * @param dbCriticalException the db critical exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
        showError(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_INDEX_CONN_ERROR));
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                .getProperty(IMessagesConstants.CREATE_INDEX_ERROR, tableMetaData.getDisplayName(), idx.getName())));
        btnCancel.setEnabled(true);

    }

    /**
     * On operational exception UI action.
     *
     * @param dbOperationException the db operation exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
        String msg = dbOperationException.getServerMessage();
        if (null == msg) {
            msg = dbOperationException.getDBErrorMessage();
        }
        if (msg.contains("Position:")) {
            msg = msg.split("Position:")[0];
        }

        String finalmsg = MessageConfigLoader.getProperty(IMessagesConstants.CREATE_INDEX_SUCCESS,
                tableMetaData.getDisplayName(), idx.getName()) + MPPDBIDEConstants.LINE_SEPARATOR + msg;
        showError(finalmsg);
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                .getProperty(IMessagesConstants.CREATE_INDEX_ERROR, tableMetaData.getDisplayName(), idx.getName())));
        btnCancel.setEnabled(true);
    }

    /**
     * On presetup failure UI action.
     *
     * @param mppDbException the mpp db exception
     */
    @Override
    public void onPresetupFailureUIAction(MPPDBIDEException mppDbException) {
        btnCreateIndex.setEnabled(true);
        btnCancel.setEnabled(true);
    }
}
