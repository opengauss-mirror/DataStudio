/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.edittabledata.EditTableCellState;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.grid.batchdrop.BatchDropDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.PropertiesConstants;
import com.huawei.mppdbide.presentation.objectproperties.PropertiesUserRoleImpl;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.component.grid.EditTableGridStyleConfiguration;
import com.huawei.mppdbide.view.component.grid.GridUIUtils;
import com.huawei.mppdbide.view.component.grid.IEditTableGridStyleLabelFactory;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridColumnLabelAccumulator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GridColumnLabelAccumulator extends ColumnOverrideLabelAccumulator {
    private DataLayer gridBodyDataLayer;
    private IDSGridDataProvider dataProvider;

    /**
     * Instantiates a new grid column label accumulator.
     *
     * @param layer the layer
     * @param dataProvider the data provider
     */
    public GridColumnLabelAccumulator(DataLayer layer, IDSGridDataProvider dataProvider) {
        super(layer);
        this.gridBodyDataLayer = layer;
        this.dataProvider = dataProvider;
    }

    /**
     * Accumulate config labels.
     *
     * @param configLabels the config labels
     * @param columnPosition the column position
     * @param rowPosition the row position
     */
    @Override
    public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
        super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);

        try {
            if ((dataProvider instanceof DSResultSetGridDataProvider
                    && !((DSResultSetGridDataProvider) dataProvider).isEditSupported())
                    || (dataProvider instanceof BatchDropDataProvider
                            && !((BatchDropDataProvider) dataProvider).isEditSupported())) {
                configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL);
            }

            accumulateEditConfigLabels(configLabels, columnPosition, rowPosition);

            // user role management
            if (dataProvider instanceof DSObjectPropertiesGridDataProvider) {
                accumulatePropertiesConfigLabels(configLabels, columnPosition, rowPosition);
            }
        } catch (MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.error("Error while accumulating config labels for columns", exception);
        }
    }

    /**
     * Accumulate properties config labels.
     *
     * @param configLabels the config labels
     * @param columnPosition the column position
     * @param rowPosition the row position
     */
    private void accumulatePropertiesConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
        DSObjectPropertiesGridDataProvider objectPropertiesGridDataProvider = (DSObjectPropertiesGridDataProvider) dataProvider;
        if (objectPropertiesGridDataProvider.getObjectPropertyObject() instanceof PropertiesUserRoleImpl) {
            String objectPropertyName = objectPropertiesGridDataProvider.getObjectPropertyName();
            if (PropertiesConstants.USER_ROLE_PROPERTY_TAB_GENERAL.equals(objectPropertyName)) {
                if (columnPosition == 1) {
                    switch (rowPosition) {
                        case 3:
                        case 4: {
                            configLabels.addLabel(IEditTableGridStyleLabelFactory.DATE_DATA_TYPE);
                            break;
                        }
                        case 5: {
                            configLabels.addLabel(IEditTableGridStyleLabelFactory.DROP_DOWN_LIST_DATA_TYPE);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            }

            if (PropertiesConstants.USER_ROLE_PROPERTY_TAB_PRIVILEGE.equals(objectPropertyName)) {
                if (columnPosition == 1) {
                    configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_BOOLEAN_DATATYPE);
                }
            }

            if (PropertiesConstants.USER_ROLE_PROPERTY_TAB_MEMBERSHIP.equals(objectPropertyName)) {
                if (columnPosition == 1) {
                    configLabels.addLabel(IEditTableGridStyleLabelFactory.COMBO_BOX_DATA_TYPE);
                }
            }
        }

        String name = objectPropertiesGridDataProvider.getObjectPropertyName();
        if ("General".equals(name)) {
            if (columnPosition == 1) {
                if (rowPosition == 11) {
                    configLabels.addLabel(EditTableGridStyleConfiguration.COL_LABEL_BOOLEAN_DATATYPE);
                }
            }
        }

    }

    /**
     * Adds the non editable label on cells.
     *
     * @param configLabels the config labels
     * @param columnPosition the column position
     * @param rowPosition the row position
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    protected void addNonEditableLabelOnCells(LabelStack configLabels, int columnPosition, int rowPosition)
            throws MPPDBIDEException {
        if (this.dataProvider instanceof IDSEditGridDataProvider) {
            IDSEditGridDataProvider editDataProvider = (IDSEditGridDataProvider) this.dataProvider;
            /*
             * below if check is handled for properties window
             */
            if (this.dataProvider instanceof DSResultSetGridDataProvider
                    && ((DSResultSetGridDataProvider) dataProvider).isIncludeEncoding()
                    && ((DSResultSetGridDataProvider) dataProvider).isEncodingChanged()) {
                configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL);
            }
            if (this.dataProvider instanceof DSObjectPropertiesGridDataProvider) {
                verifyReadOnlyCellForPropeties(configLabels, columnPosition, rowPosition);
            } else if (isMultiEditSupported(columnPosition, rowPosition)) {
                configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_NOT_SUPPORTED_MULTIDIALOG);
            } else {
                if (isDistributedColumn(columnPosition)
                        || !checkIfDatatypeSupported(columnPosition, editDataProvider)) {
                    configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL);
                }
            }
        }

    }

    // To check if multiEdit is supported
    private boolean isMultiEditSupported(int columnPosition, int rowPosition) {
        ILayerCell cellByPosition = gridBodyDataLayer.getCellByPosition(columnPosition, rowPosition);
        Object dataValue = cellByPosition != null ? cellByPosition.getDataValue() : null;
        return dataValue != null && dataValue instanceof String
                && ((String) dataValue).length() > IEditTableGridStyleLabelFactory.CANONICAL_LIMIT;
    }

    /**
     * Adds the non editable label on cells for insert.
     *
     * @param configLabels the config labels
     * @param columnPosition the column position
     * @param rowPosition the row position
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    protected void addNonEditableLabelOnCellsForInsert(LabelStack configLabels, int columnPosition, int rowPosition)
            throws MPPDBIDEException {
        if (this.dataProvider instanceof IDSEditGridDataProvider) {
            IDSEditGridDataProvider editDataProvider = (IDSEditGridDataProvider) this.dataProvider;
            if (this.dataProvider instanceof DSObjectPropertiesGridDataProvider) {
                verifyReadOnlyCellForPropeties(configLabels, columnPosition, rowPosition);
            } else {
                if (!checkIfDatatypeSupported(columnPosition, editDataProvider)) {
                    configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL);
                }
            }
        }

    }

    private boolean checkIfDatatypeSupported(int colPosition, IDSEditGridDataProvider editDataProvider) {
        String columnDataTypeName = editDataProvider.getColumnDataProvider().getColumnDataTypeName(colPosition);
        int colPrecisionValue = editDataProvider.getColumnDataProvider().getPrecision(colPosition);
        return GridUIUtils.isDatatypeEditSupported(columnDataTypeName, colPrecisionValue);
    }

    /*
     * The code for adding readonly label for table properties cells
     */
    private void verifyReadOnlyCellForPropeties(LabelStack configLabels, int columnPosition, int rowPosition) {
        String columnName = dataProvider.getColumnDataProvider().getColumnName(columnPosition);
        if (null != columnName) {

            if (!((IDSEditGridDataProvider) dataProvider).isEditSupported()) {
                configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL);
                return;
            }
            // dataValue is used to make only description value editable in
            // general tab
            Object dataValue = this.gridBodyDataLayer.getDataValue(0, rowPosition);
            String propName = ((DSObjectPropertiesGridDataProvider) dataProvider).getObjectPropertyName();

            if (dataValue != null
                    && !GridUIUtils.isEditablePropertiesAttributes(propName, columnName, dataValue.toString())) {
                configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL);
            }
        }
    }

    /**
     * Accumulate edit config labels.
     *
     * @param configLabels the config labels
     * @param columnPosition the column position
     * @param rowPosition the row position
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    protected void accumulateEditConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition)
            throws MPPDBIDEException {
        IDataProvider gridBodyDataProvider = this.gridBodyDataLayer.getDataProvider();
        if (gridBodyDataProvider.getDataValue(columnPosition, rowPosition) == null) {
            configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_NULL_VALUES);
        }

        if (columnPosition > 0 && gridBodyDataProvider.getDataValue(columnPosition - 1, rowPosition) != null) {
            if (gridBodyDataProvider.getDataValue(columnPosition - 1, rowPosition).equals(MPPDBIDEConstants.OUT)) {
                configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_OUT_PARA_TYPE_CELL);
            }
        }

        IDSGridEditDataRow editRow = getEditedRow(gridBodyDataProvider, rowPosition);

        if (editRow != null) {
            switch (editRow.getUpdatedState(columnPosition)) {
                case INSERT: {
                    configLabels.addLabel(editRow.getExecutionStatus() == EditTableRecordExecutionStatus.FAILED
                            ? IEditTableGridStyleLabelFactory.COL_LABEL_CHANGE_FAILED
                            : IEditTableGridStyleLabelFactory.COL_LABEL_INSERT);
                    addNonEditableLabelOnCellsForInsert(configLabels, columnPosition, rowPosition);

                    break;
                }
                case DELETE: {
                    configLabels.addLabel(editRow.getExecutionStatus() == EditTableRecordExecutionStatus.FAILED
                            ? IEditTableGridStyleLabelFactory.COL_LABEL_CHANGE_FAILED
                            : IEditTableGridStyleLabelFactory.COL_LABEL_DELETE);
                    configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL);
                    break;
                }
                case UPDATE: {
                    addLabelForUpdatedCell(configLabels, columnPosition, editRow);

                    break;
                }
                default: {
                    addNonEditableLabelOnCells(configLabels, columnPosition, rowPosition);
                    break;
                }
            }
        }
    }

    private void addLabelForUpdatedCell(LabelStack configLabels, int columnPosition, IDSGridEditDataRow editRow) {
        switch (editRow.getExecutionStatus()) {
            case NOT_EXECUTED: {
                configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_UPDATE);
                break;
            }
            case FAILED: {
                configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_CHANGE_FAILED);
                break;
            }
            case SUCCESS: {
                break;
            }
            case FAILED_AND_MODIFIED: {
                modifyDataForFailedAndModified(configLabels, columnPosition, editRow);
                break;
            }

            default: {
                break;
            }
        }
    }

    private void modifyDataForFailedAndModified(LabelStack configLabels, int columnPosition,
            IDSGridEditDataRow editRow) {
        EditTableCellState cellStatus = editRow.getCellStatus(columnPosition);
        if (cellStatus == null) {
            return;
        }
        modifiedDataStatus(configLabels, cellStatus);
    }

    private void modifiedDataStatus(LabelStack configLabels, EditTableCellState cellStatus) {
        switch (cellStatus) {
            case MODIFIED: {
                configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_FAILED_AND_MODIFIED);
                break;
            }
            case MODIFIED_FAILED: {
                configLabels.addLabel(IEditTableGridStyleLabelFactory.COL_LABEL_CHANGE_FAILED);
                break;
            }
            default: {
                break;
            }

        }
    }

    private IDSGridEditDataRow getEditedRow(IDataProvider gridDataProvider, int rowPosition) {
        if (gridDataProvider instanceof ListDataProvider<?>) {
            ListDataProvider<?> dp = (ListDataProvider<?>) gridDataProvider;

            Object row = dp.getRowObject(rowPosition);
            if (row instanceof IDSGridEditDataRow) {
                return (IDSGridEditDataRow) row;
            }
        }

        return null;
    }

    private boolean isDistributedColumn(int columnPosition) throws MPPDBIDEException {

        List<String> distributedRows = ((IDSEditGridDataProvider) this.dataProvider).getDistributedColumnList();
        String columnName = ((IDSEditGridDataProvider) this.dataProvider).getColumnDataProvider()
                .getColumnName(columnPosition);
        if (null != distributedRows && distributedRows.contains(columnName)) {
            return true;
        }

        return false;

    }

    /**
     * Sets the data provider.
     *
     * @param dataProvider the new data provider
     */
    public void setDataProvider(IDSGridDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        gridBodyDataLayer = null;
        dataProvider = null;
    }
}
