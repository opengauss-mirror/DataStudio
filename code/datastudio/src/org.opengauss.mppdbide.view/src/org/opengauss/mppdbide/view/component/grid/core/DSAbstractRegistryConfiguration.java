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

package org.opengauss.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.IComboBoxDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.ComboBoxPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

import org.opengauss.mppdbide.presentation.grid.IDSGridColumnProvider;
import org.opengauss.mppdbide.presentation.objectproperties.PropertiesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.view.component.grid.IEditTableGridStyleLabelFactory;
import org.opengauss.mppdbide.view.component.grid.convert.DisplayConverterFactoryProvider;
import org.opengauss.mppdbide.view.component.grid.editor.CustomComboBoxCellEditor;
import org.opengauss.mppdbide.view.component.grid.editor.CustomDateCellEditor;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSAbstractRegistryConfiguration.
 *
 * @since 3.0.0
 */
public abstract class DSAbstractRegistryConfiguration extends AbstractRegistryConfiguration
        implements IEditTableGridStyleLabelFactory {

    /**
     * Sets the column label accumulator.
     *
     * @param columnLabelAccumulator the new column label accumulator
     */
    public abstract void setColumnLabelAccumulator(ColumnOverrideLabelAccumulator columnLabelAccumulator);

    /**
     * Register config labels on columns.
     *
     * @param lblAccumulator the lbl accumulator
     * @param colDataprovider the col dataprovider
     */
    public abstract void registerConfigLabelsOnColumns(final ColumnOverrideLabelAccumulator lblAccumulator,
            IDSGridColumnProvider colDataprovider);

    /**
     * Register ui registry to each column.
     *
     * @param configRegistry the config registry
     * @param columnDataProvider the column data provider
     */
    public abstract void registerUiRegistryToEachColumn(IConfigRegistry configRegistry,
            IDSGridColumnProvider columnDataProvider);

    /**
     * Date str register.
     *
     * @param configRegistry the config registry
     */
    public static void dateStrRegister(IConfigRegistry configRegistry) {
        CustomDateCellEditor dateCellEditor = new CustomDateCellEditor(MPPDBIDEConstants.USER_ROLE_DATE_DISPLAY_FORMAT,
                true, CDT.DROP_DOWN | CDT.DATE_SHORT);

        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, dateCellEditor, DisplayMode.EDIT,
                DATE_DATA_TYPE);
        configRegistry.registerConfigAttribute(
                CellConfigAttributes.DISPLAY_CONVERTER, DisplayConverterFactoryProvider.getDisplayConverterFactory()
                        .getCustomDefaultDateDisplayConverter(MPPDBIDEConstants.USER_ROLE_DATE_DISPLAY_FORMAT),
                DisplayMode.NORMAL, DATE_DATA_TYPE);
    }

    /**
     * Combo box registry.
     *
     * @param configRegistry the config registry
     * @param comboBoxDataProvider the combo box data provider
     */
    public static void comboBoxRegistry(IConfigRegistry configRegistry, IComboBoxDataProvider comboBoxDataProvider) {
        CustomComboBoxCellEditor comboBoxCellEditor = new CustomComboBoxCellEditor(comboBoxDataProvider,
                PropertiesConstants.COMBO_BOX_MAX_VISIBLE_ITEM);
        comboBoxCellEditor.setMultiselect(true);
        comboBoxCellEditor.setUseCheckbox(true);

        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT,
                COMBO_BOX_DATA_TYPE);
        configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
                DisplayConverterFactoryProvider.getDisplayConverterFactory().getCustomComboxDefaultDisplayConverter(),
                DisplayMode.NORMAL, COMBO_BOX_DATA_TYPE);

        Style cellStyle = new Style();
        cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.EDIT,
                COMBO_BOX_DATA_TYPE);
    }

    /**
     * Drop down list registry.
     *
     * @param configRegistry the config registry
     * @param comboBoxDataProvider the combo box data provider
     * @param configLabel the config label
     */
    public static void dropDownListRegistry(IConfigRegistry configRegistry, IComboBoxDataProvider comboBoxDataProvider,
            String configLabel) {
        ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboBoxDataProvider,
                PropertiesConstants.COMBO_BOX_MAX_VISIBLE_ITEM);

        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT,
                configLabel);

        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new ComboBoxPainter(),
                DisplayMode.NORMAL, configLabel);

        Style cellStyle = new Style();
        cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
                configLabel);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.EDIT,
                configLabel);
    }

    /**
     *  the onPreDestroy
     */
    protected abstract void onPreDestroy();
}
