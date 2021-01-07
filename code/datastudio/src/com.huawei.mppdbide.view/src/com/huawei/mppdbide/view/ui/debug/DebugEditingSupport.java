/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.debug;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * Title: DebugEditingSupport for use
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-31]
 * @since 2020-12-31
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
        TextCellEditor cellEditor = new TextCellEditor(tableViewer.getTable()) {
            @Override
            protected void keyReleaseOccured(KeyEvent keyEvent) {
                if (!((keyEvent.stateMask & SWT.CTRL) != 0
                        && (keyEvent.keyCode == 'c'))) {
                    keyEvent.doit = false;
                }
            }
        };
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
}
