/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableValidatorRules;
import com.huawei.mppdbide.view.utils.UIMandatoryAttribute;

/**
 * 
 * Title: class
 * 
 * Description: The Class TableUIValidator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class TableUIValidator {
    private IndexUI indexUI;
    private ConstraintUI constraintUI;
    private TableValidatorRules validator;
    private ColumnUI columnUI;
    private DataDistributionUI distribution;
    private TableMetaData tableMetadata;

    /**
     * Instantiates a new table UI validator.
     *
     * @param indexUI the index UI
     * @param constraintUI the constraint UI
     * @param validator the validator
     * @param columnUI the column UI
     * @param distribution the distribution
     */
    public TableUIValidator(IndexUI indexUI, ConstraintUI constraintUI, TableValidatorRules validator,
            ColumnUI columnUI, DataDistributionUI distribution) {
        this.indexUI = indexUI;
        this.constraintUI = constraintUI;
        this.validator = validator;
        this.columnUI = columnUI;
        this.distribution = distribution;
    }

    /**
     * Sets the table metatadata.
     *
     * @param table the new table metatadata
     */
    public void setTableMetatadata(TableMetaData table) {

        this.tableMetadata = table;
    }

    /**
     * Index handle row column selection.
     *
     * @param compositeIndices the composite indices
     */
    public void indexHandleRowColumnSelection(Composite compositeIndices) {
        indexComponents(compositeIndices, true);

    }

    /**
     * Index handle ORC selection.
     *
     * @param compositeIndices the composite indices
     */
    public void indexHandleORCSelection(Composite compositeIndices) {
        indexComponents(compositeIndices, false);

    }

    /**
     * Index components.
     *
     * @param compositeIndices the composite indices
     * @param value the value
     */
    private void indexComponents(Composite compositeIndices, boolean value) {

        Control[] cmpIndices = compositeIndices.getChildren();
        enableDisableFieldsComponents(cmpIndices, value);
        Table indexExprTable = indexUI.getTableIndexesUI();
        boolean validatorValue = validator.enableDisable();
        if (value) {

            indexUI.getBtnUniqueIndex().setEnabled(validatorValue);

            indexUI.getTxtWhereExpr().setEnabled(validatorValue);
            indexUI.getTxtUserExpr().setEnabled(validatorValue);
            indexUI.getFillFactor().setEnabled(validatorValue);
            indexUI.getDecofk().show();
            UIMandatoryAttribute.enableDisableIndexName(value);
        }
        if (!validatorValue) {
            indexUI.getBtnUniqueIndex().setSelection(false);
            indexUI.getTxtWhereExpr().setText("");
            indexUI.getTxtUserExpr().setText("");
            indexUI.getFillFactor().setSelection(100);
            // clear the index expression from the index tab
        } else {
            indexUI.setUILabelsColorBlack();
        }
        if (null != indexExprTable) {
            indexExprTable.removeAll();
            // clear the indexmetadata to update the sql preview
            this.tableMetadata.getIndexes().clear();
        }

    }

    /**
     * Constraint handle column selection.
     *
     * @param compositeConstraints the composite constraints
     */
    public void constraintHandleColumnSelection(Composite compositeConstraints) {
        constraintsComponents(compositeConstraints, false);

    }

    /**
     * Enable disable fields components.
     *
     * @param ctrl the ctrl
     * @param value the value
     */
    private void enableDisableFieldsComponents(Control[] ctrl, boolean value) {

        for (Control child : ctrl) {

            child.setEnabled(value);
        }
    }

    /**
     * Contraint handle ORC selection.
     *
     * @param compositeConstraints the composite constraints
     */
    public void contraintHandleORCSelection(Composite compositeConstraints) {
        constraintsComponents(compositeConstraints, false);

    }

    /**
     * Constraint handle row selection.
     *
     * @param compositeConstraints the composite constraints
     */
    public void constraintHandleRowSelection(Composite compositeConstraints) {
        constraintsComponents(compositeConstraints, true);

    }

    /**
     * Constraint handle ORC selection.
     *
     * @param compositeConstraints the composite constraints
     */
    public void constraintHandleORCSelection(Composite compositeConstraints) {
        constraintsComponents(compositeConstraints, false);

    }

    /**
     * Columns components.
     */
    public void columnsComponents() {
        boolean validatorValue = validator.enableDisable();

        columnUI.getCmbClmDataSchema().setEnabled(validatorValue);
        columnUI.getTextArrayDim().setEnabled(validatorValue);
        columnUI.getSpinnerColumnArray().setEnabled(validatorValue);
        columnUI.getChkUnique().setEnabled(validatorValue);
        columnUI.getTextCheckExpr().setEnabled(validatorValue);
        UIUtils.displayDatatypeList(tableMetadata.getDatabase().getDefaultDatatype().getList(), null,
                columnUI.getCmbClmDataType());
        if (!validatorValue) {

            columnUI.getTextArrayDim().setText("");

            columnUI.getSpinnerColumnArray().setSelection(0);

            columnUI.getChkUnique().setSelection(false);
            columnUI.getTextCheckExpr().setText("");
            columnUI.getCmbClmDataSchema().select(0);
            columnUI.getCmbClmDataType().select(0);

            UIUtils.displayDatatypeList(tableMetadata.getDatabase().getDefaultDatatype().getList(), null,
                    columnUI.getCmbClmDataType());

            for (int i = 0; i < columnUI.getColumnUITable().getItemCount()
                    && columnUI.getColumnUITable().getItemCount() > 0; i++) {

                if (!validator.enableDisable()) {

                    TableItem item = columnUI.getColumnUITable().getItem(i);
                    String text = item.getText(1);
                    String text1 = item.getText(2);

                    if (text.contains("[]") || text1.contains("UNIQUE")) {
                        columnUI.getColumnUITable().remove(i);
                        this.tableMetadata.getColumnMetaDataList().remove(i);
                        distribution.getTableHmAvailableCols().remove(i);
                        constraintUI.getTableCheckAvailableCols().remove(i);
                        constraintUI.getTablePukAvailableCol().remove(i);
                        i--;
                    }

                }

            }

        } else {
            columnUI.setUILabelsColorBlack();
        }

    }

    /**
     * Constraints components.
     *
     * @param compositeConstraints the composite constraints
     * @param value the value
     */
    private void constraintsComponents(Composite compositeConstraints, boolean value) {
        Control[] cmpConstraints = compositeConstraints.getChildren();
        enableDisableFieldsComponents(cmpConstraints, value);
        Control[] grpCons = constraintUI.getGrpAddConstrains().getChildren();
        enableDisableFieldsComponents(grpCons, value);
        constraintUI.addConstraintComponent(value, this.tableMetadata);
    }

    /**
     * Distribution handle row col selection.
     *
     * @param compositeDataDistribution the composite data distribution
     */
    public void distributionHandleRowColSelection(Composite compositeDataDistribution) {
        if (distribution.getCmbDistributionType() != null && distribution.getGrpColumnList() != null) {
            distributionComponents(true, 0);
        }

    }

    /**
     * Distribution handle ORC selection.
     *
     * @param compositeDataDistribution the composite data distribution
     */
    public void distributionHandleORCSelection(Composite compositeDataDistribution) {

        if (distribution.getCmbDistributionType() != null && distribution.getGrpColumnList() != null) {
            distributionComponents(false, 2);
            distribution.getCmbDistributionType().remove(0, 1);
        }

    }

    /**
     * Distribution components.
     *
     * @param value the value
     * @param index the index
     */
    private void distributionComponents(boolean value, int index) {
        distribution.populateDataDistributionSelection();
        distribution.getCmbDistributionType().select(index);
        distribution.getCmbDistributionType().setEnabled(value);
        distribution.getGrpColumnList().setVisible(!value);
    }

    /**
     * Removes the data distribution on orientation change.
     */
    public void removeDataDistributionOnOrientationChange() {
        int size = distribution.getTableHmSelCols().getItemCount();
        for (int i = 0; i < size; i++) {
            distribution.getTableHmSelCols().remove(size - (i + 1));
        }
    }

}
