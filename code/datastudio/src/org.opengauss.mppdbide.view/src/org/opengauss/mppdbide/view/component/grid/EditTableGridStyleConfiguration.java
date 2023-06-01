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

import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.extension.nebula.cdatetime.CDateTimeCellEditor;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultColumnHeaderStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.presentation.grid.IDSGridColumnProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.objectproperties.DSObjectPropertiesGridColumnDataProvider;
import org.opengauss.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import org.opengauss.mppdbide.presentation.objectproperties.PropertiesConstants;
import org.opengauss.mppdbide.presentation.objectproperties.PropertiesUserRoleImpl;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.SystemObjectName;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.component.IGridUIPreference;
import org.opengauss.mppdbide.view.component.grid.core.DSAbstractRegistryConfiguration;
import org.opengauss.mppdbide.view.component.grid.core.DSBlobCellEditor;
import org.opengauss.mppdbide.view.component.grid.core.DSCursorResultSetTable;
import org.opengauss.mppdbide.view.component.grid.core.DSByteACellEditor;
import org.opengauss.mppdbide.view.component.grid.core.DSTextCellEditor;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditTableGridStyleConfiguration.
 *
 * @since 3.0.0
 */
public class EditTableGridStyleConfiguration extends AbstractRegistryConfiguration
        implements IEditTableGridStyleLabelFactory {
    private static final String COL_LABEL_TIMESTAMP = "TIMESTAMP";
    private static HashSet<String> extraTypes = new HashSet<>();
    private static HashMap<String, String> typeCell = new HashMap<>();


    static {
        extraTypes.add("int1");
        extraTypes.add("nvarchar2");
        extraTypes.add("interval");
        extraTypes.add("blob");
        extraTypes.add("clob");
        extraTypes.add("varbit");
        extraTypes.add("box");
        extraTypes.add("path");
        extraTypes.add("circle");
        extraTypes.add("lseg");
        extraTypes.add("point");
        extraTypes.add("polygon");
        extraTypes.add("binary");

        typeCell.put(MPPDBIDEConstants.TINYBLOB, COL_LABEL_TINYBLOB_TYPE_CELL);
        typeCell.put(MPPDBIDEConstants.MEDIUMBLOB, COL_LABEL_MEDIUMBLOB_TYPE_CELL);
        typeCell.put(MPPDBIDEConstants.LONGBLOB, COL_LABEL_LONGBLOB_TYPE_CELL);
    }
    /**
     * Configure registry.
     *
     * @param configRegistry the config registry
     */
    @Override
    public void configureRegistry(IConfigRegistry configRegistry) {
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
                DisplayMode.NORMAL);

        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE,
                DisplayMode.NORMAL, COL_LABEL_READONLY_CELL);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
                DisplayMode.NORMAL, COL_LABEL_COPY_READONLY_CELL);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE,
                DisplayMode.NORMAL, GridRegion.ROW_HEADER);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE,
                DisplayMode.NORMAL, GridRegion.COLUMN_HEADER);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE,
                DisplayMode.NORMAL, GridRegion.COLUMN_GROUP_HEADER);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE,
                DisplayMode.NORMAL, GridRegion.ROW_GROUP_HEADER);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE,
                DisplayMode.NORMAL, GridRegion.CORNER);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
                DisplayMode.NORMAL, ITableGridStyleLabelFactory.COL_LABEL_TIME_WITHTIMEZONE);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
                DisplayMode.NORMAL, ITableGridStyleLabelFactory.COL_LABEL_DATE_DATATYPE);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
                DisplayMode.NORMAL, ITableGridStyleLabelFactory.COL_LABEL_TIME_DATATYPE);

        cellLabelConfigUration(configRegistry);
        applyRowHeaderStyles(configRegistry);
    }

    private void cellLabelConfigUration(IConfigRegistry configRegistry) {
        applyCellStyle(configRegistry, GUIHelper.COLOR_GREEN, COL_LABEL_INSERT, DisplayMode.NORMAL);
        applyCellStyle(configRegistry, GUIHelper.COLOR_GRAY, COL_LABEL_DELETE, DisplayMode.NORMAL);
        applyCellStyle(configRegistry, GUIHelper.COLOR_GREEN, COL_LABEL_UPDATE, DisplayMode.NORMAL);
        applyCellStyle(configRegistry, GUIHelper.COLOR_GREEN, COL_LABEL_FAILED_AND_MODIFIED, DisplayMode.NORMAL);
        applyCellStyle(configRegistry, GUIHelper.COLOR_GREEN, COL_LABEL_INSERT, DisplayMode.SELECT);
        applyCellStyle(configRegistry, GUIHelper.COLOR_GRAY, COL_LABEL_DELETE, DisplayMode.SELECT);
        applyCellStyle(configRegistry, GUIHelper.COLOR_GREEN, COL_LABEL_UPDATE, DisplayMode.SELECT);
        applyCellStyle(configRegistry, GUIHelper.COLOR_GREEN, COL_LABEL_FAILED_AND_MODIFIED, DisplayMode.SELECT);

        applyCellStyle(configRegistry, GUIHelper.COLOR_RED, COL_LABEL_CHANGE_FAILED, DisplayMode.NORMAL);
        applyCellStyle(configRegistry, GUIHelper.COLOR_RED, COL_LABEL_CHANGE_FAILED, DisplayMode.SELECT);
        applyCellStyle(configRegistry, GUIHelper.COLOR_DARK_GRAY, COL_LABEL_OUT_PARA_TYPE_CELL, DisplayMode.NORMAL);
        applyCellStyle(configRegistry, GUIHelper.COLOR_DARK_GRAY, COL_LABEL_OUT_PARA_TYPE_CELL, DisplayMode.SELECT);

        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
                DisplayMode.NORMAL, COL_LABEL_EDIT_INSERTED_DISTRIBUTABLE_COLUMN);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
                DisplayMode.NORMAL, COL_LABEL_CUSTOM_DIALOG);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
                DisplayMode.NORMAL, COL_LABEL_CUSTOM_DIALOG_REFERENCING_TABLE);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
                DisplayMode.NORMAL, COL_LABEL_CUSTOM_CLMS_DIALOG);
    }

    private void applyRowHeaderStyles(IConfigRegistry configRegistry) {
        GridEditColumnHeaderStyleConfig editColumnHeaderStyleConfig = new GridEditColumnHeaderStyleConfig();
        editColumnHeaderStyleConfig.configureRegistry(configRegistry);
    }

    private void applyCellStyle(IConfigRegistry configRegistry, Color color, String cellLabel, String displayMode) {
        /*
         * registerConfigAttribute accepts 1. attribute to apply 2. value of the
         * attribute 3. apply during normal rendering i.e not during selection
         * or edit 4. apply the above for all cells with this label
         */
        Style style = new Style();
        style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, color);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, displayMode, cellLabel);

        applyCellStyleForEditableCells(configRegistry, GUIHelper.COLOR_GRAY, COL_LABEL_NULL_VALUES, DisplayMode.SELECT);
        applyCellStyleForEditableCells(configRegistry, GUIHelper.COLOR_BLACK, COL_LABEL_NULL_VALUES, DisplayMode.EDIT);

    }

    private void applyCellStyleForEditableCells(IConfigRegistry configRegistry, Color color, String cellLabel,
            String displayMode) {
        Style nullStyle = new Style();
        nullStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, color);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, nullStyle, displayMode, cellLabel);

        Style nullStyleForEditMode = new Style();
        nullStyleForEditMode.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, color);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, nullStyleForEditMode, displayMode,
                cellLabel);
    }

    /**
     * Date edit configuration.
     *
     * @param configRegistry the config registry
     */
    public static void dateEditConfiguration(IConfigRegistry configRegistry) {

        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,
                new CDateTimeCellEditor(false, DROP_DOWN | DATE_SHORT), DisplayMode.EDIT,
                ITableGridStyleLabelFactory.COL_LABEL_DATE_DATATYPE);

    }

    /**
     * Edits the date configuration.
     *
     * @param configRegistry the config registry
     */
    public static void editDateConfiguration(IConfigRegistry configRegistry) {
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,
                new CDateTimeCellEditor(false, DROP_DOWN | DATE_SHORT), DisplayMode.EDIT, COL_LABEL_TIMESTAMP);
    }

    /**
     * Check box registry.
     *
     * @param configRegistry the config registry
     */
    public static void checkBoxRegistry(IConfigRegistry configRegistry) {
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new CheckBoxCellEditor(),
                DisplayMode.EDIT, COL_LABEL_BOOLEAN_DATATYPE);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new CheckBoxPainter(),
                DisplayMode.NORMAL, COL_LABEL_BOOLEAN_DATATYPE);
        configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
                new DSdefaultBooleanDisplayConverter(), DisplayMode.NORMAL, COL_LABEL_BOOLEAN_DATATYPE);
        configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
                new DSdefaultBooleanDisplayConverter(), DisplayMode.EDIT, COL_LABEL_BOOLEAN_DATATYPE);

    }

    /**
     * Date and time configuration.
     *
     * @param configRegistry the config registry
     */
    public static void dateAndTimeConfiguration(IConfigRegistry configRegistry) {
        /*
         * Date time Column to use default pattern of the client system. And use
         * Calendar + Clock for edit
         */
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,
                new DSCDateTimeCellEditor(true, CDT.DROP_DOWN | CDT.DATE_SHORT | CDT.TIME_MEDIUM), DisplayMode.EDIT,
                ITableGridStyleLabelFactory.COL_LABEL_TIME_WITHTIMEZONE);

        /*
         * Date time Column to use default date pattern of the client system.
         * And use Calendar for edit
         */
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,
                new DSCDateTimeCellEditor(true, CDT.DROP_DOWN | CDT.DATE_SHORT), DisplayMode.EDIT,
                ITableGridStyleLabelFactory.COL_LABEL_DATE_DATATYPE);

        /*
         * Date time Column to use default time pattern of the client system.
         * And use Number based time editor.
         */
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,
                new DSCDateTimeCellEditor(true, CDT.DROP_DOWN | CDT.TIME_MEDIUM), DisplayMode.EDIT,
                ITableGridStyleLabelFactory.COL_LABEL_TIME_DATATYPE);

    }

    /**
     * Gets the datatype column accumulator.
     *
     * @param columnLabelAccumulator the column label accumulator
     * @param dataGridContext the data grid context
     * @param uiPref the ui pref
     * @return the datatype column accumulator
     */
    public static DSAbstractRegistryConfiguration getDatatypeColumnAccumulator(
            final ColumnOverrideLabelAccumulator columnLabelAccumulator, final IDataGridContext dataGridContext,
            IGridUIPreference uiPref) {
        return new RegistryConfiguration(columnLabelAccumulator, dataGridContext, uiPref);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class GridEditColumnHeaderStyleConfig.
     */
    private static class GridEditColumnHeaderStyleConfig extends DefaultColumnHeaderStyleConfiguration {
        @Override
        public void configureRegistry(IConfigRegistry configRegistry) {
            super.configureRegistry(configRegistry);
            Image myImage = IconUtility.getIconSmallImage(IconUtility.ICON_COLUMN_EDIT_LOCK, getClass());
            ImagePainter decoratorImagePainter = new ImagePainter(myImage);
            CellPainterDecorator painter = new CellPainterDecorator(this.cellPainter, CellEdgeEnum.LEFT, 100,
                    decoratorImagePainter, false, true);

            configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, painter, DisplayMode.NORMAL,
                    IEditTableGridStyleLabelFactory.COL_HEADER_LABEL_READONLY_CELL);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RegistryConfiguration.
     */
    private static final class RegistryConfiguration extends DSAbstractRegistryConfiguration {

        private static final String IS_NULLABLE = MessageConfigLoader
                .getProperty(IMessagesConstants.PROPERTIES_COLUMNS_ISNULLABLE);
        private static final String DATA_TYPE = MessageConfigLoader
                .getProperty(IMessagesConstants.PROPERTIES_COLUMNS_DATATYPE);
        private IDataGridContext dataGridContext;
        private ColumnOverrideLabelAccumulator columnLabelAccumulator;
        private IGridUIPreference uiPref;

        private RegistryConfiguration(ColumnOverrideLabelAccumulator columnLabelAccumulator,
                IDataGridContext dataGridContext, IGridUIPreference uiPref) {
            this.columnLabelAccumulator = columnLabelAccumulator;
            this.dataGridContext = dataGridContext;
            this.uiPref = uiPref;
        }

        @Override
        public void configureRegistry(IConfigRegistry configRegistry) {

            IDSGridDataProvider dataProvider = dataGridContext.getDataProvider();
            registerConfigLabelsOnColumns(columnLabelAccumulator, dataProvider.getColumnDataProvider());

            int columnCount = dataProvider.getColumnDataProvider().getColumnCount();

            if (dataProvider instanceof DSObjectPropertiesGridDataProvider) {
                handleDSObjectGridConfigureRegistry(configRegistry, dataProvider, columnCount);
            }
            handleCommonColumnConfigureRegistry(configRegistry, dataProvider, columnCount,
                    dataProvider.getDatabse() != null ?dataProvider.getDatabse().getDolphinTypes() : null);

        }

        private void handleCommonColumnConfigureRegistry(IConfigRegistry configRegistry,
                IDSGridDataProvider dataProvider, int columnCount, HashMap<String, boolean[]>dolphinTypes) {
            int colDatatype = 0;
            for (int i = 0; i < columnCount; i++) {
                colDatatype = dataProvider.getColumnDataProvider().getColumnDatatype(i);
                switch (colDatatype) {
                    case Types.BIT:
                    case Types.BOOLEAN: {
                        checkBoxRegistry(configRegistry);
                        break;
                    }
                    case Types.TIME:
                    case Types.DATE:
                    case Types.TIME_WITH_TIMEZONE:
                    case Types.TIMESTAMP_WITH_TIMEZONE:
                    case Types.TIMESTAMP: {
                        dateAndTimeConfiguration(configRegistry);
                        break;
                    }
                    case Types.BLOB: {
                        handleCommonColumnConfigureForBlob(configRegistry, dataProvider, i);
                        break;
                    }
                    case Types.OTHER: {
                        handleCommonColumnConfigureForOther(configRegistry, dataProvider, dolphinTypes, i);
                        break;
                    }

                    case Types.BINARY: {
                        handleCommonColumnConfigureForBinary(configRegistry, dataProvider, i);
                        break;
                    }
                    default: {
                        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new DSTextCellEditor(),
                                DisplayMode.NORMAL, COL_LABEL_COPY_READONLY_CELL);
                        break;
                    }
                }
            }
        }

        private void handleCommonColumnConfigureForBinary(IConfigRegistry configRegistry,
                IDSGridDataProvider dataProvider, int i) {
            if (MPPDBIDEConstants.BYTEA.equals(dataProvider.getColumnDataProvider().getColumnDataTypeName(i))) {
                byteAConfiguration(configRegistry, dataProvider);
            } else {
                configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new DSTextCellEditor(),
                        DisplayMode.NORMAL, COL_LABEL_COPY_READONLY_CELL);
            }
        }

        private void handleCommonColumnConfigureForBlob(IConfigRegistry configRegistry,
                IDSGridDataProvider dataProvider, int i) {
            if (MPPDBIDEConstants.BLOB.equals(dataProvider.getColumnDataProvider().getColumnDataTypeName(i))) {
                blobConfiguration(configRegistry, dataProvider, COL_LABEL_BLOB_TYPE_CELL);
            } else {
                configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new DSTextCellEditor(),
                        DisplayMode.NORMAL, COL_LABEL_COPY_READONLY_CELL);
            }
        }

        private void handleCommonColumnConfigureForOther(IConfigRegistry configRegistry,
                IDSGridDataProvider dataProvider, HashMap<String, boolean[]> dolphinTypes, int i) {
            String colDatatypeName = dataProvider.getColumnDataProvider().getColumnDataTypeName(i);
            String cell = typeCell.get(colDatatypeName);
            if (cell != null) {
                blobConfiguration(configRegistry, dataProvider, cell);
                return;
            }
            if (extraTypes.contains(colDatatypeName)
                    || (dolphinTypes != null && dolphinTypes.containsKey(colDatatypeName))) {
                configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new DSTextCellEditor(),
                        DisplayMode.NORMAL, COL_LABEL_COPY_READONLY_CELL);
                return;
            }
            try {
                if (GridUIUtils.checkTypType(colDatatypeName, SystemObjectName.SET_TYP_TYPE, dataProvider.getDatabse())
                        || GridUIUtils.checkTypType(colDatatypeName, SystemObjectName.ENUM_TYP_TYPE,
                                dataProvider.getDatabse())) {
                    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new DSTextCellEditor(),
                            DisplayMode.NORMAL, COL_LABEL_COPY_READONLY_CELL);
                    return;
                }
            } catch (DatabaseCriticalException exception) {
                MPPDBIDELoggerUtility.error("checkTypType query failed", exception);
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("checkTypType query failed", exception);
            }
            cursorTypeConfiguration(configRegistry, dataProvider);
        }

        /**
         * Blob configuration.
         *
         * @param configRegistry the config registry
         * @param dataProvider the data provider
         * @param cell the data label
         */
        public static void blobConfiguration(IConfigRegistry configRegistry, IDSGridDataProvider dataProvider,
            String cell) {
            configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new DSBlobCellEditor(),
                    DisplayMode.NORMAL, cell);
            linkStyleConfiguration(configRegistry, cell);
        }

        /**
         * Byte A configuration.
         *
         * @param configRegistry the config registry
         * @param dataProvider the data provider
         */
        public static void byteAConfiguration(IConfigRegistry configRegistry, IDSGridDataProvider dataProvider) {
            configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new DSByteACellEditor(),
                    DisplayMode.NORMAL, COL_LABEL_BYTEA_TYPE_CELL);
            linkStyleConfiguration(configRegistry, COL_LABEL_BYTEA_TYPE_CELL);
        }

        private static void linkStyleConfiguration(IConfigRegistry configRegistry, String label) {
            IStyle linkStyle = new Style();
            linkStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR,
                    Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
            configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, linkStyle, DisplayMode.NORMAL,
                    label);
        }

        /**
         * Blob configuration.
         *
         * @param configRegistry the config registry
         * @param dataProvider the data provider
         */
        public static void cursorTypeConfiguration(IConfigRegistry configRegistry, IDSGridDataProvider dataProvider) {
            configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new DSCursorResultSetTable(),
                    DisplayMode.NORMAL, COL_LABEL_CURSOR_TYPE_CELL);
        }

        private void handleDSObjectGridConfigureRegistry(IConfigRegistry configRegistry,
                IDSGridDataProvider dataProvider, int columnCount) {
            String colName;
            for (int i = 0; i < columnCount; i++) {
                colName = dataProvider.getColumnDataProvider().getColumnName(i);
                if (DATA_TYPE.equalsIgnoreCase(colName)) {
                    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,
                            new CustomDialogCellEditor(dataProvider), DisplayMode.NORMAL, COL_LABEL_CUSTOM_DIALOG);
                } else if (IS_NULLABLE.equalsIgnoreCase(colName)) {
                    checkBoxRegistry(configRegistry);
                }
            }

            // user role management
            if (((DSObjectPropertiesGridDataProvider) dataProvider)
                    .getObjectPropertyObject() instanceof PropertiesUserRoleImpl) {
                if (columnCount >= 2
                        && MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_TAB_COLUMN_TITLE_VALUE)
                                .equals(dataProvider.getColumnDataProvider().getColumnName(1))) {
                    PropertiesUserRoleImpl propertiesUserRoleImpl = (PropertiesUserRoleImpl) ((DSObjectPropertiesGridDataProvider) dataProvider)
                            .getObjectPropertyObject();

                    dateStrRegister(configRegistry);

                    dropDownListRegistry(configRegistry,
                            this.uiPref.getComboBoxDataProviders()
                                    .get(PropertiesConstants.RESOURCE_POOL_COMBO_BOX_DATA_PROVIDER),
                            DROP_DOWN_LIST_DATA_TYPE);
                }

                if (columnCount >= 2
                        && MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_TAB_COLUMN_TITLE_IS_ENABLE)
                                .equals(dataProvider.getColumnDataProvider().getColumnName(1))) {
                    checkBoxRegistry(configRegistry);
                }

                if (columnCount >= 2
                        && MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_TAB_COLUMN_TITLE_BELONG_TO)
                                .equals(dataProvider.getColumnDataProvider().getColumnName(1))) {
                    comboBoxRegistry(configRegistry, this.uiPref.getComboBoxDataProviders()
                            .get(PropertiesConstants.MEMBER_SHIP_COMBO_BOX_DATA_PROVIDER));
                }
            }
        }

        @Override
        public void registerConfigLabelsOnColumns(final ColumnOverrideLabelAccumulator columnLblAccumulator,
                IDSGridColumnProvider columnDataProvider) {
            int colCount = columnDataProvider.getColumnCount();
            int colDatatype = 0;
            String colName = "";

            if (columnDataProvider instanceof DSObjectPropertiesGridColumnDataProvider) {
                for (int i = 0; i < colCount; i++) {
                    colName = columnDataProvider.getColumnName(i);
                    if (DATA_TYPE.equalsIgnoreCase(colName)) {
                        columnLblAccumulator.registerColumnOverrides(i, COL_LABEL_CUSTOM_DIALOG);
                    } else if (IS_NULLABLE.equalsIgnoreCase(colName)) {
                        columnLblAccumulator.registerColumnOverrides(i, COL_LABEL_BOOLEAN_DATATYPE);
                    }
                }
            }
            registerConfigLblForClmDatatypes(columnLblAccumulator, columnDataProvider, colCount);
        }

        private void registerConfigLblForClmDatatypes(final ColumnOverrideLabelAccumulator columnLblAccumulator,
                IDSGridColumnProvider columnDataProvider, int colCount) {
            int colDatatype;
            for (int i = 0; i < colCount; i++) {
                colDatatype = columnDataProvider.getColumnDatatype(i);
                int colPrecisionVal = columnDataProvider.getPrecision(i);

                switch (colDatatype) {
                    case Types.BIT:
                    case Types.BOOLEAN: {
                        if (colPrecisionVal <= 1) {
                            columnLblAccumulator.registerColumnOverrides(i, COL_LABEL_BOOLEAN_DATATYPE);
                        }
                        break;
                    }
                    case Types.DATE:
                    case Types.TIMESTAMP:
                    case Types.TIME_WITH_TIMEZONE:
                    case Types.TIMESTAMP_WITH_TIMEZONE: {
                        columnLblAccumulator.registerColumnOverrides(i,
                                ITableGridStyleLabelFactory.COL_LABEL_TIME_WITHTIMEZONE);
                        break;
                    }
                    case Types.TIME: {
                        columnLblAccumulator.registerColumnOverrides(i,
                                ITableGridStyleLabelFactory.COL_LABEL_TIME_DATATYPE);
                        break;
                    }
                    case Types.BLOB: {
                        registerConfigLblForClmTypeBlob(columnLblAccumulator, columnDataProvider, i);
                        break;
                    }
                    case Types.OTHER: {
                        columnLblAccumulator.registerColumnOverrides(i, COL_LABEL_CURSOR_TYPE_CELL);
                        columnLblAccumulator.registerColumnOverrides(i, COL_LABEL_COPY_READONLY_CELL);
                        break;
                    }
                    case Types.BINARY: {
                        registerConfigLblForClmTypeBinary(columnLblAccumulator, columnDataProvider, i);
                        break;
                    }
                    default: {
                        columnLblAccumulator.registerColumnOverrides(i, COL_LABEL_COPY_READONLY_CELL);
                        break;
                    }

                }

            }
        }

        private void registerConfigLblForClmTypeBinary(final ColumnOverrideLabelAccumulator columnLblAccumulator,
                IDSGridColumnProvider columnDataProvider, int i) {
            if (MPPDBIDEConstants.BYTEA.equals(columnDataProvider.getColumnDataTypeName(i))) {
                columnLblAccumulator.registerColumnOverrides(i, COL_LABEL_BYTEA_TYPE_CELL);
                columnLblAccumulator.registerColumnOverrides(i, COL_LABEL_COPY_READONLY_CELL);
            } else {
                columnLblAccumulator.registerColumnOverrides(i, COL_LABEL_COPY_READONLY_CELL);
            }
        }

        private void registerConfigLblForClmTypeBlob(final ColumnOverrideLabelAccumulator columnLblAccumulator,
                IDSGridColumnProvider columnDataProvider, int i) {
            if (MPPDBIDEConstants.BLOB.equals(columnDataProvider.getColumnDataTypeName(i))) {
                columnLblAccumulator.registerColumnOverrides(i, COL_LABEL_BLOB_TYPE_CELL);
                columnLblAccumulator.registerColumnOverrides(i, COL_LABEL_COPY_READONLY_CELL);
            } else {
                columnLblAccumulator.registerColumnOverrides(i, COL_LABEL_COPY_READONLY_CELL);
            }
        }

        /**
         * Sets the column label accumulator.
         *
         * @param columnLabelAccumulator the new column label accumulator
         */
        public void setColumnLabelAccumulator(ColumnOverrideLabelAccumulator columnLabelAccumulator) {
            this.columnLabelAccumulator = columnLabelAccumulator;
        }

        @Override
        public void registerUiRegistryToEachColumn(IConfigRegistry configRegistry,
                IDSGridColumnProvider columnDataProvider) {

        }
        
        /**
         * the onPreDestroy
         */
        public void onPreDestroy() {
            columnLabelAccumulator = null;
            dataGridContext = null;
            uiPref = null;
        }
    }

}
