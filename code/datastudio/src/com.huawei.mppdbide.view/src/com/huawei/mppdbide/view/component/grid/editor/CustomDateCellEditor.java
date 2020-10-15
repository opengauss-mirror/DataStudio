/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.editor;

import java.text.SimpleDateFormat;

import org.eclipse.nebula.widgets.nattable.extension.nebula.cdatetime.CDateTimeCellEditor;

/**
 * 
 * Title: class
 * 
 * Description: The Class CustomDateCellEditor.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
