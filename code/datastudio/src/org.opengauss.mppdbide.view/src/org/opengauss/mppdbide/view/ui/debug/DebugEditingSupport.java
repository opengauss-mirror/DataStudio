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

package org.opengauss.mppdbide.view.ui.debug;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * Title: DebugEditingSupport for use
 *
 * @since 3.0.0
 */
public class DebugEditingSupport extends EditingSupport {
    private int index;

    public DebugEditingSupport(ColumnViewer viewer, int index) {
        super(viewer);
        this.index = index;
    }

    @Override
    protected void setValue(Object element, Object value) {
    }

    @Override
    protected Object getValue(Object element) {
        return ((IDebugSourceData) element).getValue(index);
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        ColumnViewer viewer = getViewer();
        if (!(viewer instanceof TableViewer)) {
            return null;
        }
        TableViewer tableViewer = (TableViewer) viewer;
        TextCellEditor cellEditor = new ToolTextCellEditor(tableViewer.getTable());
        return cellEditor;
    }

    @Override
    protected boolean canEdit(Object element) {
        if (element instanceof IDebugSourceData) {
            IDebugSourceData data = (IDebugSourceData) element;
            if (index == 0 && data.isShowOrder()) {
                return false;
            }
            return data.isEditable(index);
        }
        return true;
    }

    private static class ToolTextCellEditor extends TextCellEditor {
        public ToolTextCellEditor(Composite parent) {
            super(parent);
        }

        @Override
        protected void keyReleaseOccured(KeyEvent keyEvent) {
            if (!((keyEvent.stateMask & SWT.CTRL) != 0
                    && (keyEvent.keyCode == 'c'))) {
                keyEvent.doit = false;
            }
        }
    }
}
