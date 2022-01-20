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

package com.huawei.mppdbide.view.component.grid;

import java.util.List;

import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.extension.nebula.cdatetime.CDateTimeCellEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSCDateTimeCellEditor.
 *
 * @since 3.0.0
 */
public class DSCDateTimeCellEditor extends CDateTimeCellEditor {
    private boolean isCellEditable = false;

    /**
     * Instantiates a new DSC date time cell editor.
     *
     * @param moveSelectionOnEnter the move selection on enter
     * @param style the style
     */
    public DSCDateTimeCellEditor(boolean moveSelectionOnEnter, int style) {
        super(moveSelectionOnEnter, style);
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

        super.activateCell(parent, originalCanonicalValue);
        isCellEditable = labelStack.hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL);
        Text text = (Text) getEditorControl().getChildren()[0];
        DSGridMenuManager.addContextMenuWithID(text, !isCellEditable);
        getEditorControl().addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                if (labelStack.hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL)) {
                    event.doit = false;
                }

            }
        });

        getEditorControl().addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent mouseEvent) {
                if (labelStack.hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL)) {
                    CDateTime editorControl = getEditorControl();
                    editorControl.setEditable(false);
                }
                getEditorControl().getDisplay().getActiveShell().forceFocus();
            }

            @Override
            public void mouseDown(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseDoubleClick(MouseEvent mouseEvent) {

            }
        });

        return getEditorControl();
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
        if (configLabels.contains(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL)) {
            return false;
        }
        return super.supportMultiEdit(configRegistry, configLabels);
    }
}
