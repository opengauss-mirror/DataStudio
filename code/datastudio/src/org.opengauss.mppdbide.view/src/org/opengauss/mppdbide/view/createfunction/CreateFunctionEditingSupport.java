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

package org.opengauss.mppdbide.view.createfunction;

import java.util.Optional;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

/**
 * Title: DebugEditingSupport for use
 *
 * @since 3.0.0
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
