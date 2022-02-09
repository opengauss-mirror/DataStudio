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

package org.opengauss.mppdbide.view.component.grid.editor;

import java.text.SimpleDateFormat;

import org.eclipse.nebula.widgets.nattable.extension.nebula.cdatetime.CDateTimeCellEditor;

/**
 * 
 * Title: class
 * 
 * Description: The Class CustomDateCellEditor.
 *
 * @since 3.0.0
 */
public class CustomDateCellEditor extends CDateTimeCellEditor implements ICustomDateCellEditor {

    private final SimpleDateFormat sdf;
    private final Object INSTANCE_LOCK = new Object();

    /**
     * Instantiates a new custom date cell editor.
     *
     * @param format the format
     * @param moveSelectionOnEnter the move selection on enter
     * @param style the style
     */
    public CustomDateCellEditor(String format, boolean moveSelectionOnEnter, int style) {
        super(moveSelectionOnEnter, style);
        this.sdf = new SimpleDateFormat(format);
    }

    /**
     * Sets the canonical value.
     *
     * @param canonicalValue the new canonical value
     */
    @Override
    public void setCanonicalValue(Object canonicalValue) {
        // canonicalValue is the current value of editor
        super.setCanonicalValue(canonicalValue);
    }

    /**
     * Gets the canonical value.
     *
     * @return the canonical value
     */
    @Override
    public Object getCanonicalValue() {
        Object canonicalVal = null;
        synchronized (INSTANCE_LOCK) {
            canonicalVal = getEditorValue() == null ? "" : this.sdf.format(getEditorValue());
        }
        // dataValue is the old value at the NatTable
        Object dataValue = this.layerCell.getDataValue();
        if (dataValue instanceof String) {
            return canonicalVal;
        }
        return super.getCanonicalValue();
    }

}
