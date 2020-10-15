/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.sort;

import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortColumnComboEditingSupport.implements
 * ITableColorProvider
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SortColumnComboEditingSupport extends EditingSupport {
    private final TableViewer viewer;
    private final CellEditor editor;
    private String[] comboOptions = null;
    private String defaultComboText = null;
    private String[] datatypes = null;
    private static final String[] SORT_ORDER = {
        MessageConfigLoader.getProperty(IMessagesConstants.COMBO_OPTION_ASCENDING),
        MessageConfigLoader.getProperty(IMessagesConstants.COMBO_OPTION_DESCENDING)};

    /**
     * Instantiates a new sort column combo editing support.
     *
     * @param viewer the viewer
     * @param options the options
     * @param datatypes the datatypes
     * @param noSelText the no sel text
     */
    public SortColumnComboEditingSupport(TableViewer viewer, String[] options, String[] datatypes, String noSelText) {
        // used for column name combo. Datatype will get filled when column name
        // is chosen
        super(viewer);
        this.viewer = viewer;
        this.editor = new ComboBoxCellEditor(this.viewer.getTable(), options, SWT.READ_ONLY);
        this.comboOptions = options.clone();
        this.defaultComboText = noSelText;
        this.datatypes = datatypes.clone();
    }

    /**
     * Instantiates a new sort column combo editing support.
     *
     * @param viewer the viewer
     */
    public SortColumnComboEditingSupport(TableViewer viewer) {
        // used by sort order combo. No other field is dependent on it
        super(viewer);
        this.viewer = viewer;
        this.comboOptions = SORT_ORDER;
        this.editor = new ComboBoxCellEditor(this.viewer.getTable(), comboOptions, SWT.READ_ONLY);
        this.defaultComboText = MessageConfigLoader.getProperty(IMessagesConstants.COMBO_TEXT_SORT_OREDER);
    }

    /**
     * Gets the cell editor.
     *
     * @param element the element
     * @return the cell editor
     */
    @Override
    protected CellEditor getCellEditor(Object element) {
        return editor;
    }

    /**
     * Can edit.
     *
     * @param element the element
     * @return true, if successful
     */
    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    /**
     * Gets the value.
     *
     * @param element the element
     * @return the value
     */
    @Override
    protected Object getValue(Object element) {
        if (datatypes != null) {
            return Arrays.asList(comboOptions).indexOf(((SortColumnSetting) element).getColumnName());
        } else {
            return Arrays.asList(comboOptions).indexOf(((SortColumnSetting) element).getSortOrder());
        }
    }

    /**
     * Sets the value.
     *
     * @param element the element
     * @param value the value
     */
    @Override
    protected void setValue(Object element, Object value) {
        String setVal = ((Integer) value == -1) ? defaultComboText : comboOptions[(int) value];

        if (datatypes != null) {
            ((SortColumnSetting) element).setColumnName(setVal, comboOptions, datatypes);
            // datatype and sort_dir gets automatically filled if column name
            // chosen
        } else {
            ((SortColumnSetting) element).setSortOrder(setVal);
        }
        viewer.update(element, null);
    }

}
