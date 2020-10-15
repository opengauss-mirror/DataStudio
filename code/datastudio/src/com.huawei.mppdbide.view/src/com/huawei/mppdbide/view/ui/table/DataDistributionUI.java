/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DataDistributionUI.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DataDistributionUI {

    private Table tableHmAvailableCols;

    /**
     * Gets the table hm available cols.
     *
     * @return the table hm available cols
     */
    public Table getTableHmAvailableCols() {
        return tableHmAvailableCols;
    }

    private Table tableHmSelCols;

    /**
     * Gets the table hm sel cols.
     *
     * @return the table hm sel cols
     */
    public Table getTableHmSelCols() {
        return tableHmSelCols;
    }

    private boolean flag;

    /**
     * Sets the flag.
     *
     * @param flag the new flag
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /**
     * Checks if is flag.
     *
     * @return true, if is flag
     */
    public boolean isFlag() {
        return flag;
    }

    private Combo cmbDistributionType;

    /**
     * Gets the cmb distribution type.
     *
     * @return the cmb distribution type
     */
    public Combo getCmbDistributionType() {
        return cmbDistributionType;
    }

    private Group grpColumnList;

    /**
     * Gets the grp column list.
     *
     * @return the grp column list
     */
    public Group getGrpColumnList() {
        return grpColumnList;
    }

    /**
     * Creates the data distribution info gui.
     *
     * @param compositeDataDistribution the composite data distribution
     */
    public void createDataDistributionInfoGui(Composite compositeDataDistribution) {
        /**
         * STEP: 3 DATA DISTRIBUTION
         */
        compositeDataDistribution.setLayout(new GridLayout(1, false));

        Group group3 = new Group(compositeDataDistribution, SWT.NONE);
        group3.setLayout(new GridLayout(1, false));
        GridData group3GD = new GridData(SWT.FILL, SWT.NONE, true, false);
        group3.setLayoutData(group3GD);

        createDistributionTypeUi(group3);

        grpColumnList = new Group(compositeDataDistribution, SWT.NONE);
        grpColumnList.setLayout(new GridLayout(1, false));
        GridData grpColumnListGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        grpColumnListGD.verticalIndent = 30;
        grpColumnList.setLayoutData(grpColumnListGD);
        grpColumnList.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATA_DIST_UI_HAST_FUN));

        grpColumnList.setVisible(false);

        Composite hashMapColComposite = new Composite(grpColumnList, SWT.NONE);
        hashMapColComposite.setLayout(new GridLayout(3, false));
        GridData hashMapColCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        hashMapColComposite.setLayoutData(hashMapColCompositeGD);

        createHmAvailableClmTable(hashMapColComposite);
        addRightLeftButtons(hashMapColComposite);
        createHmSelectedBtn(hashMapColComposite);
    }

    /**
     * Creates the hm selected btn.
     */
    private void createHmSelectedBtn(Composite comp) {
        tableHmSelCols = new Table(comp, SWT.BORDER | SWT.FULL_SELECTION);
        GridData tableHmSelColsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableHmSelCols.setLayoutData(tableHmSelColsGD);
        tableHmSelCols.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_DATADIST_HM_SEL_COLSTBL_001");
        tableHmSelCols.setLinesVisible(true);
        tableHmSelCols.setHeaderVisible(true);

        TableColumn tblclmnHashMod = new TableColumn(tableHmSelCols, SWT.NONE);
        tblclmnHashMod.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_DATADIST_HM_SEL_COLSTBL_HMCOL_001");
        tblclmnHashMod.setWidth(205);
        tblclmnHashMod.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATA_DIST_UI_HASH_CLMNS));
    }

    /**
     * Adds the right left buttons.
     */
    private void addRightLeftButtons(Composite comp) {
        Composite buttonGrpComposite = new Composite(comp, SWT.NONE);
        buttonGrpComposite.setLayout(new GridLayout(1, false));
        GridData buttonGrpCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        buttonGrpCompositeGD.verticalAlignment = SWT.CENTER;
        buttonGrpComposite.setLayoutData(buttonGrpCompositeGD);

        Button buttonhmRight = new Button(buttonGrpComposite, SWT.ARROW | SWT.RIGHT);
        GridData buttonhmRightGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        buttonhmRightGD.heightHint = 30;
        buttonhmRight.setLayoutData(buttonhmRightGD);
        buttonhmRight.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_DATADIST_HM_RIGHT_001");
        buttonhmRight.addSelectionListener(btnHmRightSelectionListener());
        buttonhmRight.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATA_DIST_UI_NEW_FUN));

        Button buttonhmLeft = new Button(buttonGrpComposite, SWT.ARROW | SWT.LEFT);
        GridData buttonhmLeftGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        buttonhmLeftGD.heightHint = 30;
        buttonhmLeft.setLayoutData(buttonhmLeftGD);
        buttonhmLeft.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_DATADIST_HM_LEFT_001");
        buttonhmLeft.addSelectionListener(btnHmLeftSelectionListener());
        buttonhmLeft.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATA_DIST_UI_NEW_FUN));
    }

    /**
     * Creates the hm available clm table.
     */
    private void createHmAvailableClmTable(Composite comp) {
        tableHmAvailableCols = new Table(comp, SWT.BORDER | SWT.FULL_SELECTION);
        GridData tableHmAvailableColsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableHmAvailableCols.setLayoutData(tableHmAvailableColsGD);
        tableHmAvailableCols.setLinesVisible(true);
        tableHmAvailableCols.setHeaderVisible(true);
        tableHmAvailableCols.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_DATADIST_HM_AVL_COLSTBL_001");

        TableColumn tblclmnAvailableColumns = new TableColumn(tableHmAvailableCols, SWT.NONE);
        tblclmnAvailableColumns.setWidth(205);
        tblclmnAvailableColumns.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATA_DIST_UI_AVAI_CLMS));
        tblclmnAvailableColumns.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_DATADIST_HM_AVL_COLSTBL_AVLCOL_001");
    }

    /**
     * Creates the distribution type ui.
     *
     * @param group3 the group 3
     */
    private void createDistributionTypeUi(Group group3) {
        Label lblDistributionType = new Label(group3, SWT.NONE);
        lblDistributionType.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATA_DIST_UI_DIST_TYPE));
        lblDistributionType.pack();

        cmbDistributionType = new Combo(group3, SWT.READ_ONLY);
        GridData cmbDistributionTypeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        cmbDistributionType.setLayoutData(cmbDistributionTypeGD);
        populateDataDistributionSelection();
    }

    /**
     * Btn hm left selection listener.
     *
     * @return the selection adapter
     */
    private SelectionAdapter btnHmLeftSelectionListener() {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                UIUtils.removeSelectedCol(tableHmSelCols);
            }
        };
    }

    /**
     * Btn hm right selection listener.
     *
     * @return the selection adapter
     */
    private SelectionAdapter btnHmRightSelectionListener() {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                UIUtils.addSelectedCol(tableHmAvailableCols, tableHmSelCols);
            }
        };
    }

    /**
     * Populate data distribution selection.
     */
    public void populateDataDistributionSelection() {
        if (cmbDistributionType != null) {
            cmbDistributionType.setItems(new String[] {

                MessageConfigLoader.getProperty(IMessagesConstants.DATA_DIST_UI_DEFAULT_DIST),
                MPPDBIDEConstants.REPLICATION, MPPDBIDEConstants.HASH});
            cmbDistributionType.select(0);

            cmbDistributionType.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_DATADIST_NODE_DIST_TYPE_001");

            cmbDistributionType.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {

                    if (2 == cmbDistributionType.getSelectionIndex()) {
                        grpColumnList.setVisible(true);
                    } else {
                        grpColumnList.setVisible(false);
                    }

                }
            });
        }
    }

    /**
     * Gets the distribution string.
     *
     * @param distributeOpt the distribute opt
     * @return the distribution string
     */
    public void getDistributionString(StringBuilder distributeOpt) {
        if (cmbDistributionType != null) {
            switch (cmbDistributionType.getText()) {
                case MPPDBIDEConstants.REPLICATION: {
                    distributeOpt.append(MPPDBIDEConstants.REPLICATION);
                    break;
                }
                case MPPDBIDEConstants.HASH: {
                    String colList = null;
                    colList = UIUtils.getColumnwiseString(tableHmSelCols, 0, true);
                    if (!colList.isEmpty()) {
                        distributeOpt.append(MPPDBIDEConstants.HASH + " (");
                        distributeOpt.append(colList);
                        distributeOpt.append(')');
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
        // end DTS2014070905348
    }

    /**
     * Adds the column.
     *
     * @param rowdata the rowdata
     */
    public void addColumn(String[] rowdata) {
        TableItem tableItem = new TableItem(tableHmAvailableCols, SWT.NONE);
        tableItem.setText(rowdata);
    }

    /**
     * Adds the column.
     *
     * @param rowdata the rowdata
     * @param index the index
     */
    public void addColumn(String[] rowdata, int index) {
        TableItem tableItem = new TableItem(tableHmAvailableCols, SWT.NONE, index);
        tableItem.setText(rowdata);
    }

    /**
     * Removes the column.
     *
     * @param index the index
     */
    public void removeColumn(int index) {

        if (tableHmAvailableCols != null) {
            String str = tableHmAvailableCols.getItem(index).getText(0);
            TableItem[] items = getTableHmSelCols().getItems();
            if (items.length > 0) {
                setFlag(true);
            }

            for (int itmIndex = 0; itmIndex < items.length; itmIndex++) {
                if (items[itmIndex].getText(0).equals(str)) {
                    tableHmSelCols.remove(itmIndex);
                }
            }

            tableHmAvailableCols.remove(index);
        }
    }

    /**
     * Update column.
     *
     * @param index the index
     * @param rowdata the rowdata
     */
    public void updateColumn(int index, String[] rowdata) {
        if (tableHmAvailableCols != null) {
            TableItem tableItem = tableHmAvailableCols.getItem(index);
            tableItem.setText(rowdata);
        }
    }

    /**
     * Sets the table distribution UI.
     *
     * @param tblDistribution the new table distribution UI
     */
    public void setTableDistributionUI(Table tblDistribution) {

        this.tableHmSelCols = tblDistribution;

    }

    /**
     * Gets the table indexes UI.
     *
     * @return the table indexes UI
     */
    public Table getTableIndexesUI() {
        return this.tableHmSelCols;
    }

    /**
     * Validate table hm cols.
     *
     * @return true, if successful
     */
    public boolean validateTableHmCols() {
        return getTableHmSelCols().getItemCount() > 0;
    }
}
