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

package org.opengauss.mppdbide.view.component.grid;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.edit.editor.AbstractCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.presentation.edittabledata.EditTableRecordStates;
import org.opengauss.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import org.opengauss.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataRow;
import org.opengauss.mppdbide.presentation.objectproperties.ObjectPropColumnTabInfo;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.ui.table.ChangeDataTypeDialog;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class CustomDialogCellEditor.
 *
 * @since 3.0.0
 */
public class CustomDialogCellEditor extends AbstractCellEditor {

    private IDSGridDataProvider dataProvider;
    private ChangePropertiesDatatypeDialog dilg;
    private ColumnMetaData columnMetadata;
    private ObjectPropColumnTabInfo info;
    private TableMetaData table;

    /**
     * Instantiates a new custom dialog cell editor.
     *
     * @param dataProvider the data provider
     */
    public CustomDialogCellEditor(IDSGridDataProvider dataProvider) {
        this.dataProvider = dataProvider;
        this.table = (TableMetaData) dataProvider.getTable();

    }

    /**
     * Gets the editor value.
     *
     * @return the editor value
     */
    @Override
    public Object getEditorValue() {
        return this.info;
    }

    /**
     * Sets the editor value.
     *
     * @param value the new editor value
     */
    @Override
    public void setEditorValue(Object value) {

    }

    /**
     * Sets the canonical value.
     *
     * @param canonicalValue the new canonical value
     */
    @Override
    public void setCanonicalValue(Object canonicalValue) {
    }

    /**
     * Gets the canonical value.
     *
     * @return the canonical value
     */
    @Override
    public Object getCanonicalValue() {
        return info;
    }

    /**
     * Gets the editor control.
     *
     * @return the editor control
     */
    @Override
    public Control getEditorControl() {
        return null;
    }

    /**
     * Creates the editor control.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    public Control createEditorControl(Composite parent) {

        return null;
    }

    /**
     * Activate cell.
     *
     * @param parent the parent
     * @param originalCanonicalValue the original canonical value
     * @return the control
     */
    @Override
    protected Control activateCell(Composite parent, Object originalCanonicalValue) {
        boolean colNotFound = true;
        List<IDSGridDataRow> allRows = ((DSObjectPropertiesGridDataProvider) dataProvider).getAllRows();
        IDSGridEditDataRow idsGridDataRow;
        if (allRows.size() > getRowIndex()) {
            idsGridDataRow = (IDSGridEditDataRow) allRows.get(getRowIndex());
        } else {
            return null;
        }
        /*
         * The below check is used for getting the columnmetadata directly from
         * the row while changing the datatype of a column
         */
        if (((IDSGridEditDataRow) idsGridDataRow).getUpdatedState(getColumnIndex()) == EditTableRecordStates.INSERT) {
            this.columnMetadata = (ColumnMetaData) ((DSObjectPropertiesGridDataRow) idsGridDataRow).getServerObject();
        } else {

            List<ColumnMetaData> columnMetaDataList = this.table.getColumnMetaDataList();
            for (ColumnMetaData col : columnMetaDataList) {
                if (idsGridDataRow.getOriginalValue(0).equals(col.getName())) {
                    colNotFound = false;
                    this.columnMetadata = col;
                    break;
                }
            }

            if (colNotFound) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WINDOW_ERROR_POPUP_HEADER),
                        MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_COMPLEX_DATATYPE_ERROR_MSG,
                                idsGridDataRow.getOriginalValue(0)));
                return null;
            }
        }

        dilg = new ChangePropertiesDatatypeDialog(parent.getShell(), this.table, this.columnMetadata);
        dilg.open();
        return null;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ChangePropertiesDatatypeDialog.
     */
    private final class ChangePropertiesDatatypeDialog extends ChangeDataTypeDialog {
        private ColumnMetaData modifiedColumn;

        /**
         * Instantiates a new change properties datatype dialog.
         *
         * @param shell the shell
         * @param tableMetaData the table meta data
         * @param column the column
         */
        public ChangePropertiesDatatypeDialog(Shell shell, TableMetaData tableMetaData, ColumnMetaData column) {
            super(shell, tableMetaData, column);
            this.modifiedColumn = column;
        }

        @Override
        public Object open() {

            return super.open();
        }

        @Override
        public void performOkButtonPressed() {
            super.performOkButtonPressed();
            getDBColumn();
            collectUserData();
            String formattedTextToDisplay = formatDataForChangeDatatye();
            setEditorValue(formattedTextToDisplay);
            commit(MoveDirectionEnum.NONE, true);
            dilg.close();
        }

        @Override
        public void cancelPressed() {
            dilg.close();
        }

        private void collectUserData() {
            info = new ObjectPropColumnTabInfo();
            info.setDataTypeSchema(comboClmDataSchema.getText());
            info.setColDataType(modifiedColumn.getDataType());
            info.setPrecision(Integer.parseInt(spinnerPrevSize.getText()));
            info.setScale(Integer.parseInt(spinnerScale.getText()));
        }

    }

    /**
     * Support multi edit.
     *
     * @param configRegistry the config registry
     * @param configLabels the config labels
     * @return true, if successful
     */
    @Override
    public boolean supportMultiEdit(IConfigRegistry configRegistry, List<String> configLabels) {
        if (configLabels.contains(IEditTableGridStyleLabelFactory.COL_LABEL_CUSTOM_DIALOG)) {
            return false;
        }
        return super.supportMultiEdit(configRegistry, configLabels);
    }

    /**
     * Format data for change datatye.
     *
     * @return the string
     */
    public String formatDataForChangeDatatye() {

        StringBuilder formattedString = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        formattedString.append(info.getColDatatype());
        if (info.getPrecision() > 0) {
            formattedString.append('(');
            formattedString.append(info.getPrecision());
            if (info.getScale() > 0) {
                formattedString.append(',');
                formattedString.append(info.getScale());
            }
            formattedString.append(')');
        }

        return formattedString.toString();
    }

}
