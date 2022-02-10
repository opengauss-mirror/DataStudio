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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionColumnExpr;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.TableOrientation;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreatePartitionTable.
 * 
 * @since 3.0.0
 */
public class CreatePartitionTable extends CreateTable {

    /* Step: 6 - Partition */
    private PartitionUI partitionUI;

    /**
     * The Constant CREATE_TABLE_PARTITIONS.Other index are reused from super
     * class.
     */
    static final int CREATE_TABLE_PARTITIONS = 5;

    /**
     * The Constant CREATE_TABLE_SQL_PREVIEW.
     */
    static final int CREATE_TABLE_SQL_PREVIEW = 6;

    /**
     * The Constant PARTITION.
     */
    static final String PARTITION = MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_GROUP_NAME);

    /**
     * Instantiates a new creates the partition table.
     *
     * @param shell the shell
     * @param server the server
     * @param ns the ns
     */
    @Inject
    public CreatePartitionTable(Shell shell, Server server, Namespace ns) {
        super(shell, server, ns, OBJECTTYPE.PARTITION_TABLE);
        partitionUI = new PartitionUI(server, (PartitionTable) this.newTable);
        newTable.setOrientation(TableOrientation.ROW);
    }

    /**
     * Gets the title text.
     *
     * @return the title text
     */
    protected String getTitleText() {
        return MessageConfigLoader.getProperty(IMessagesConstants.CREATE_NEW_PARTITION_TABLE);
    }

    /**
     * Edits the column.
     */
    protected void editColumn() {
        super.editColumn();
        disableSpinner();
    }

    /**
     * Removes the column.
     */
    protected void removeColumn() {
        super.removeColumn();

        disableSpinner();
    }

    /**
     * Adds the column.
     */
    protected void addColumn() {
        super.addColumn();
        disableSpinner();
    }

    /**
     * Move column.
     *
     * @param up the up
     */
    protected void moveColumn(boolean up) {
        super.moveColumn(up);
        disableSpinner();
    }

    /**
     * Validate query.
     *
     * @return true, if successful
     */
    private boolean validateQuery() {

        String query = textSqlPreview.getDocument().get();

        if (query.contains("PARTITION BY RANGE") || query.contains("PARTITION BY VALUES")
                || query.contains("PARTITION BY HASH") || query.contains("PARTITION BY LIST")) {
            return true;
        }
        return false;
    }

    /**
     * Form queries.
     *
     * @return the string
     */
    public String formQueries() {
        StringBuilder qry = new StringBuilder(newTable.formCreateQuery());

        String partitionQueries = ((PartitionTable) newTable).formPartitionQueries();
        if (!partitionQueries.isEmpty()) {
            qry.deleteCharAt(qry.length() - 1);
            qry.append(partitionQueries);
        }
        qry.append(newTable.formTableCommentQuery());
        qry.append(newTable.formColumnCommentQuery());
        qry.append(newTable.formIndexQueries());

        return qry.toString();
    }

    /**
     * Adds the control pannel.
     *
     * @param tblComposite the tbl composite
     */
    protected void addControlPannel(Composite tblComposite) {

        createControlButtons(tblComposite);

        FinishBtnSelectionAdapter finishButtonSelAdapter = new FinishBtnSelectionAdapter();

        btnFinish.addSelectionListener(finishButtonSelAdapter);

        BtnNxtSelectionAdapter nxtButtonSelAdapter = new BtnNxtSelectionAdapter();

        btnNext.addSelectionListener(nxtButtonSelAdapter);

        BtnBackSelectionAdapter backButtonSelAdapter = new BtnBackSelectionAdapter();

        btnBack.addSelectionListener(backButtonSelAdapter);

        tabFolder.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (!isClmUpdate) {
                    setErrorMsg("");
                }
                int currentTab = tabFolder.getSelectionIndex();
                buttonToggling(currentTab);

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class BtnBackSelectionAdapter.
     */
    private class BtnBackSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            int currentTab = tabFolder.getSelectionIndex();
            setErrorMsg("");
            buttonToggling(currentTab - 1);
            if (currentTab > 0) {
                updateTableFields(currentTab - 1);
                if (CREATE_TABLE_INDEXES == currentTab - 1) {
                    indexUi.refreshColumns();
                }
                if (CREATE_TABLE_PARTITIONS == currentTab - 1) {
                    partitionUI.refreshColumns();
                }
                tabFolder.setSelection(currentTab - 1);
                setFocusOnText(currentTab - 1);
            }

        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class BtnNxtSelectionAdapter.
     */
    private class BtnNxtSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            int currentTab = tabFolder.getSelectionIndex();
            setErrorMsg("");
            buttonToggling(currentTab + 1);
            switch (currentTab) {
                case 0: {
                    try {
                        validateTableFillFactor();
                    } catch (DatabaseOperationException ex1) {
                        setErrorMsg(ex1.getMessage());
                        return;
                    }
                    break;
                }
                default: {
                    break;
                }
            }

            if (currentTab + 1 != tabFolder.getItemCount()) {
                updateTableFields(currentTab + 1);
                if (CREATE_TABLE_INDEXES == currentTab + 1) {
                    indexUi.refreshColumns();
                }
                if (CREATE_TABLE_PARTITIONS == currentTab + 1) {
                    partitionUI.refreshColumns();
                }
                tabFolder.setSelection(currentTab + 1);

                setFocusOnText(currentTab + 1);
            }
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TabFolderSelectionAdapter.
     */
    private class TabFolderSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            int curTab = tabFolder.getSelectionIndex();
            setFocusOnText(curTab);

            if (CREATE_TABLE_GENERAL_INFO != curTab) {
                try {
                    validateTableFillFactor();
                } catch (DatabaseOperationException ex1) {
                    setErrorMsg(ex1.getMessage());
                    event.doit = false;
                    tabFolder.setSelection(CREATE_TABLE_GENERAL_INFO);
                    return;
                }
            }
            if (CREATE_TABLE_INDEXES == curTab) {
                indexUi.refreshColumns();
            }
            if (curTab == CREATE_TABLE_PARTITIONS || curTab == CREATE_TABLE_SQL_PREVIEW) {
                partitionUI.refreshColumns();
            }

            updateTableFields(curTab);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class FinishBtnSelectionAdapter.
     */
    private class FinishBtnSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            btnFinish.setEnabled(false);
            setErrorMsg("");
            try {
                if (currentShell.isDisposed()) {
                    return;
                }

                if (!validateTableInputs()) {
                    btnFinish.setEnabled(true);
                    return;
                }
            } catch (MPPDBIDEException e1) {
                btnFinish.setEnabled(true);
                hanldeCreateTableError(e1);
                return;
            } catch (OutOfMemoryError e1) {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                        .getProperty(IMessagesConstants.CREATE_TABLE_CREATE_ERROR, newTable.getDisplayName())));
                return;
            }

            tabFolder.setSelection(tabFolder.getItemCount() - 1);
            updateTableFields(CREATE_TABLE_SQL_PREVIEW);
            if (!validateQuery()) {
                setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_ERROR_MESSAGE));
                btnFinish.setEnabled(true);
                return;
            }
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(newTable.getName(),
                    newTable.getNamespace().getName(), newTable.getDatabaseName(), newTable.getServerName(),
                    IMessagesConstants.CREATE_TABLE_PROGRESS_NAME);
            CreateTableWorker worker = new CreateTableWorker(progressLabel, newTable,
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CREATE_TABLE),
                    CreatePartitionTable.this);
            worker.schedule();
        }

    }

    /**
     * Creates the table GUI.
     *
     * @param parent the parent
     */
    protected void createTableGUI(Composite parent) {
        boolean isCreateTbl = true;

        parent.setLayout(new GridLayout(1, false));
        GridData parentGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        parent.setLayoutData(parentGD);

        columnUI = new ColumnUI(db, newTable);
        partitionUI = new PartitionUI(super.getServer(), (PartitionTable) newTable);
        dataDistributionUi = new DataDistributionUI();
        constraintUI = new ConstraintUI(db);

        final ScrolledComposite mainSc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        mainSc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite gridComposite = createGridComp(mainSc);
        mainSc.setContent(gridComposite);

        Composite tabFolderComposite = new Composite(gridComposite, SWT.NONE);
        tabFolderComposite.setLayout(new GridLayout(1, false));
        GridData tabFolderCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        tabFolderCompositeGD.heightHint = 608;
        tabFolderCompositeGD.widthHint = 570;
        tabFolderComposite.setLayoutData(tabFolderCompositeGD);

        tabFolder = new TabFolder(tabFolderComposite, SWT.NONE);
        tabFolder.setLayout(new GridLayout(1, false));
        GridData tabFolderGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tabFolder.setLayoutData(tabFolderGD);
        partitionUI.setTabfolder(tabFolder);
        partitionUI.setPartTabInstance(this);
        tabFolder.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABFOLDER_CREATE_TBL_TAB_CONTAINER_001");
        tabFolder.addSelectionListener(new TabFolderSelectionAdapter());
        createPartTableStepIndicesTab();
        createStepIndicesClm(isCreateTbl);
        createPartTableDistributionTab();
        createConstrantTab();
        createPartTableIndexTab();
        createPartitionTab();
        createPartTableSqlPreviewTab(tabFolderComposite);
        setFocusOnText(CREATE_TABLE_GENERAL_INFO);

        Composite errorAndButtonComposite = new Composite(gridComposite, SWT.NONE);
        errorAndButtonComposite.setLayout(new GridLayout(1, false));
        GridData errorAndButtonCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        errorAndButtonComposite.setLayoutData(errorAndButtonCompositeGD);

        createErrorMsgArea(errorAndButtonComposite);
        addControlPannel(errorAndButtonComposite);

        mainSc.setExpandHorizontal(true);
        mainSc.setExpandVertical(true);
        mainSc.setMinSize(gridComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mainSc.pack();
    }

    /**
     * Creates the sql preview tab.
     *
     * @param gridComposite the grid composite
     */
    private void createPartTableSqlPreviewTab(Composite gridComposite) {
        TabItem tbtmStepSQLPreview = new TabItem(tabFolder, SWT.NONE);
        tbtmStepSQLPreview.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_SQL_PREVIEW_TAB_001");
        tbtmStepSQLPreview.setText(SQL_PREVIEW);
        Composite compositeSqlpreview = new Composite(tabFolder, SWT.NONE);
        tbtmStepSQLPreview.setControl(compositeSqlpreview);
        createSqlPreviewInfoGui(compositeSqlpreview);
    }

    /**
     * Creates the partition tab.
     */
    private void createPartitionTab() {
        TabItem tbtmPartition = new TabItem(tabFolder, SWT.NONE);
        tbtmPartition.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_PRIVILEGE_TAB_001");
        tbtmPartition.setText(PARTITION);
        Composite compositePartition = new Composite(tabFolder, SWT.NONE);
        tbtmPartition.setControl(compositePartition);
        partitionUI.createPartitionInfoGui(compositePartition);
    }

    /**
     * Creates the index tab.
     */
    private void createPartTableIndexTab() {
        TabItem tabtmStepIndexes = new TabItem(tabFolder, SWT.NONE);
        tabtmStepIndexes.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_TNDEX_TAB_001");
        tabtmStepIndexes.setText(INDEXES);
        compositeIndices = new Composite(tabFolder, SWT.NONE);
        tabtmStepIndexes.setControl(compositeIndices);
        createIndexInfoGui(compositeIndices);
    }

    /**
     * Creates the constrant tab.
     */
    private void createConstrantTab() {
        TabItem tbtmStepConstrnts = new TabItem(tabFolder, SWT.NONE);
        tbtmStepConstrnts.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_CONSTRAINT_TAB_001");
        tbtmStepConstrnts.setText(TABLE_CONSTRAINTS);
        compositeConstraints = new Composite(tabFolder, SWT.NONE);
        tbtmStepConstrnts.setControl(compositeConstraints);

        constraintUI.createConstraintsInfoGui(compositeConstraints);
        createConstraintPannel(compositeConstraints);
    }

    /**
     * Creates the distribution tab.
     */
    private void createPartTableDistributionTab() {
        TabItem tabItemStepDistribution = new TabItem(tabFolder, SWT.NONE);
        tabItemStepDistribution.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_DATA_DIST_TAB_001");
        tabItemStepDistribution.setText(DATA_DISTRIBUTION);
        compositeDataDistribution = new Composite(tabFolder, SWT.NONE);
        tabItemStepDistribution.setControl(compositeDataDistribution);
        dataDistributionUi.createDataDistributionInfoGui(compositeDataDistribution);
    }

    /**
     * Creates the step indices clm.
     * 
     * @param isCreateTbl the is create table
     */
    private void createStepIndicesClm(boolean isCreateTbl) {
        TabItem tbtmStepColumns = new TabItem(tabFolder, SWT.NONE);
        tbtmStepColumns.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_COLUMNS_TAB_001");
        tbtmStepColumns.setText(MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_MSG));
        compositeColumns = new Composite(tabFolder, SWT.NONE);
        compositeColumns.setLayout(new GridLayout(1, false));
        GridData compositeColumnsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositeColumns.setLayoutData(compositeColumnsGD);
        tbtmStepColumns.setControl(compositeColumns);
        columnUI.createColumnInfoGui(compositeColumns, isCreateTbl);
        createColumnPannel();
    }

    /**
     * Creates the step indices tab.
     */
    private void createPartTableStepIndicesTab() {
        TabItem tbtmStepIndices = new TabItem(tabFolder, SWT.NONE);
        tbtmStepIndices.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_GENERAL_TAB_001");
        tbtmStepIndices.setText(GENERAL);
        Composite generalComposite = new Composite(tabFolder, SWT.NONE);
        tbtmStepIndices.setControl(generalComposite);
        createGeneralInfoGui(generalComposite);
    }

    /**
     * Creates the error msg area.
     */
    private void createErrorMsgArea(Composite comp) {
        txtErrorMsg = new Text(comp, SWT.BOLD | SWT.READ_ONLY | SWT.WRAP);
        GridData txtErrorMsgGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        txtErrorMsgGD.heightHint = 20;
        txtErrorMsg.setLayoutData(txtErrorMsgGD);
        txtErrorMsg.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CREATE_TBL_ERROR_MSG_001");
        txtErrorMsg.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        txtErrorMsg.setVisible(false);
    }

    /**
     * Creates the grid comp.
     *
     * @param parent the parent
     * @return the composite
     */
    private Composite createGridComp(Composite parent) {
        Composite gridComposite = new Composite(parent, SWT.NONE);
        gridComposite.setLayout(new GridLayout(1, false));
        GridData gridCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        gridComposite.setLayoutData(gridCompositeGD);
        return gridComposite;
    }

    /**
     * Register table orientation listener.
     *
     * @param tableOrientationCombo the table orientation combo
     */
    public void registerTableOrientationListener(final Combo tableOrientationCombo) {
        tableOrientationCombo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                newTable.setOrientation(TableOrientation.valueOf(tableOrientationCombo.getText()));
                if (ROW_ORIENTATION_INDEX == tableOrientationCombo.getSelectionIndex()) {
                    rowOrientationSelected();
                } else {
                    orientationType = TableOrientation.COLUMN;
                    newTable.setOrientation(orientationType);
                    // UI validation for general tab
                    spinnerFillFactor.setEnabled(false);
                    cmbTableType.setEnabled(false);
                    chkWithoid.setEnabled(false);
                    // UI validation for
                    if (isDataOnOtherTabsPresent() || isPartitionTab()) {
                        showInfoMessageToUserOnEditOrientationChange(ORIENTATION, true);
                    }
                    tableUIValidator.constraintHandleColumnSelection(compositeConstraints);
                    tableUIValidator.columnsComponents();
                    tableUIValidator.indexHandleRowColumnSelection(compositeIndices);
                    tableUIValidator.distributionHandleRowColSelection(compositeDataDistribution);
                    partitionUI.handleRowColumnSelection(TableOrientation.COLUMN);
                    UIUtils.displayTablespaceList(db, cmbTblspcName, true, orientationType);
                    partitionUI.getDecofk().show();
                    tableUIValidator.removeDataDistributionOnOrientationChange();
                    partitionUI.removeALL();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
    }

    /**
     * Row orientation selected.
     */
    private void rowOrientationSelected() {
        orientationType = TableOrientation.ROW;
        newTable.setOrientation(orientationType);
        spinnerFillFactor.setEnabled(true);
        if (isDataOnOtherTabsPresent() || isPartitionTab()) {
            showInfoMessageToUserOnEditOrientationChange(ORIENTATION, true);
        }
        tableUIValidator.constraintHandleRowSelection(compositeConstraints);
        tableUIValidator.columnsComponents();
        tableUIValidator.indexHandleRowColumnSelection(compositeIndices);
        tableUIValidator.distributionHandleRowColSelection(compositeDataDistribution);
        partitionUI.handleRowColumnSelection(TableOrientation.ROW);
        chkWithoid.setEnabled(false);
        UIUtils.displayTablespaceList(db, cmbTblspcName, true, orientationType);
        cmbTableType.setEnabled(false);
        partitionUI.getDecofk().show();
        tableUIValidator.removeDataDistributionOnOrientationChange();
        partitionUI.removeALL();
    }

    /**
     * Creates the general info gui.
     *
     * @param compositeGeneral the composite general
     */
    @Override
    protected void createGeneralInfoGui(Composite compositeGeneral) {
        super.createGeneralInfoGui(compositeGeneral);
        tableOrientation.setItems(new String[] {TableOrientation.ROW.toString(), TableOrientation.COLUMN.toString()});
        tableOrientation.select(0);
        cmbTableType.setEnabled(false);
        chkWithoid.setEnabled(false);
    }

    /**
     * Checks if is table name in valid.
     *
     * @return true, if is table name in valid
     */
    protected boolean isTableNameInValid() {

        return textTableName.getText().trim().length() < 1;

    }

    /**
     * Disable spinner.
     */
    private void disableSpinner() {
        if (TableOrientation.ROW != ConvertToOrientation.convertToOrientationEnum(tableOrientation.getText())) {
            columnUI.checkspinner();
        }
    }

    /**
     * Sets the button enable.
     *
     * @param value the new button enable
     */
    public void setButtonEnable(boolean value) {
        btnNext.setEnabled(value);
        btnBack.setEnabled(value);
        btnFinish.setEnabled(value);
    }

    /**
     * Show info message to user on edit orientation change.
     *
     * @param name the name
     * @param ispartiton the ispartiton
     */
    @Override
    protected void showInfoMessageToUserOnEditOrientationChange(String name, boolean ispartiton) {
        if (ispartiton) {
            super.showInfoMessageToUserOnEditOrientationChange(name, true);
        } else {
            super.showInfoMessageToUserOnEditOrientationChange(name, false);
        }
    }

    /**
     * Checks if is partition present.
     *
     * @return true, if is partition present
     */
    @Override
    protected boolean isPartitionPresent() {
        boolean isPartitionDataPresent = false;
        boolean isPartitionSelForOrc = false;
        boolean isPatitionTable = false;
        Table partitions = partitionUI.getTblPartitions();
        List<PartitionColumnExpr> selCols = partitionUI.getSelCols();

        if ((partitionUI.getTblPartitions().getItemCount() > 0)) {

            isPatitionTable = isMetaDataPresentOnEdit(copyEditColName.replace("\"", ""), partitions);

            Pattern pattern = Pattern.compile(copyEditColName.replace("\"", ""));
            for (int index = 0; index < selCols.size(); index++) {
                Matcher matcher = pattern.matcher(selCols.get(index).toString());
                isPartitionSelForOrc = matcher.find();
                if (isPartitionSelForOrc) {
                    break;
                }
            }
            partitions.removeAll();
            ((PartitionTable) newTable).removeAllPartition();

            isPartitionDataPresent = isPatitionTable || isPartitionSelForOrc;
        }
        return isPartitionDataPresent;

    }

    /**
     * method to detect any data present on partition tab and is called through
     * orientation event listener.
     *
     * @return true, if is partition tab
     */
    private boolean isPartitionTab() {
        if ((null != partitionUI.getTblPartitions() && partitionUI.getTblPartitions().getItemCount() > 0)
                || null != partitionUI.getSelCols() && partitionUI.getSelCols().size() > 0) {
            return true;
        }

        else {
            return false;
        }

    }

    /**
     * On operational exception UI action.
     *
     * @param dbOperationException the db operation exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
        super.onOperationalExceptionUIAction(dbOperationException);
        btnFinish.setEnabled(true);
    }

    /**
     * Update table fields.
     *
     * @param event the event
     */
    @Override
    protected void updateTableFields(int event) {
        if (event == CREATE_TABLE_SQL_PREVIEW) {
            createTableSqlPreview();
        }
    }

    /**
     * Button toggling.
     *
     * @param curTab the cur tab
     */
    @Override
    protected void buttonToggling(int curTab) {
        if (curTab != -1) {
            if (curTab == CREATE_TABLE_GENERAL_INFO) {
                btnBack.setVisible(false);
            } else {
                btnBack.setVisible(true);
            }

            if (curTab == CREATE_TABLE_SQL_PREVIEW) {
                btnNext.setVisible(false);
                btnFinish.setFocus();
            } else {
                btnNext.setVisible(true);
            }
        }
    }
}
