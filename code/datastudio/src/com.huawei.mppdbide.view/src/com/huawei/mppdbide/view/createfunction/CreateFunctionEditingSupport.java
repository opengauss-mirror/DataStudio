/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.createfunction;

import java.util.Optional;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

/**
 * Title: DebugEditingSupport for use
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-31]
 * @since 2020-12-31
 */
public class CreateFunctionEditingSupport extends EditingSupport {
    private int index;
    private ComboBoxCellEditor comboBoxCellEditor;

    public CreateFunctionEditingSupport(ColumnViewer viewer, int index) {
        super(viewer);
        this.index = index;
    }

    @Override
    protected void setValue(Object element, Object value) {
        CreateFunctionParam data = (CreateFunctionParam) element;
        if (data.isSupportCombo(index)) {
            data.setComboValue(index, (Integer)value);
        } else {
            data.setObject(index, value.toString());
        }
        getViewer().refresh();
    }

    @Override
    protected Object getValue(Object element) {
        CreateFunctionParam data = (CreateFunctionParam) element;
        if (data.isSupportCombo(index)) {
            return data.getComboValue(index);
        } else {
            return data.getValue(index);
        }
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        ColumnViewer viewer = getViewer();
        if (!(viewer instanceof TableViewer)) {
            return null;
        }
        TableViewer tableViewer = (TableViewer) viewer;
        CreateFunctionParam data = (CreateFunctionParam) element;
        if (data.isSupportCombo(index)) {
            if (comboBoxCellEditor == null) {
                comboBoxCellEditor = new ComboBoxCellEditor(
                        tableViewer.getTable(),
                        data.getSupportItems(index)
                        );
            }
            return comboBoxCellEditor;
        } else {
            return new TextCellEditor(tableViewer.getTable());
        }
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }
}
