/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.edit.editor.AbstractCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.IEditErrorHandler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.view.component.grid.IEditTableGridStyleLabelFactory;

/**
 * Title: DSUnstructuredDataCellEditor
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * 
 * @author aWX619007
 * @version [DataStudio 6.5.1, 22-Jan-2020]
 * @since 22-Jab-2020
 */
public class DSUnstructuredDataCellEditor extends AbstractCellEditor {
    /** 
     * The is cell editable. 
     */
    private boolean isCellEditable = false;

    /** 
     * The byte value. 
     */
    private byte[] byteValue = null;

    /**
     * Checks if is cell editable.
     *
     * @return true, if is cell editable
     */
    public boolean isCellEditable() {
        return isCellEditable;
    }

    @Override
    public Object getEditorValue() {
        return byteValue;
    }

    @Override
    public void setEditorValue(Object value) {

        this.byteValue = (byte[]) value;
    }

    @Override
    public Control getEditorControl() {
        return null;
    }

    @Override
    public Control createEditorControl(Composite parent) {
        return null;
    }

    @Override
    public boolean supportMultiEdit(IConfigRegistry configRegistry, List<String> configLabels) {
        return false;
    }

    /**
     * Handle conversion.
     *
     * @param displayValue the display value
     * @param conversionErrorHandler the conversion error handler
     * @return the object
     */
    protected Object handleConversion(Object displayValue, IEditErrorHandler conversionErrorHandler) {
        return displayValue;
    }

    @Override
    protected Control activateCell(Composite parent, Object originalCanonicalValue) {
        isCellEditable = labelStack.hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL);
        Shell shell = Display.getDefault().getActiveShell();
        byteValue = convertObjectToBytes(originalCanonicalValue);
        getTableDataEditor(originalCanonicalValue, shell);
        super.close();
        return getEditorControl();
    }

    /**
     * Gets the table data editor.
     *
     * @param originalCanonicalValue the original canonical value
     * @param shell the shell
     * @return the table data editor
     */
    protected void getTableDataEditor(Object originalCanonicalValue, Shell shell) {
        DSUnstructuredDataTableDataEditor dialog = new DSUnstructuredDataTableDataEditor(shell, this,
                originalCanonicalValue);
        dialog.open();
    }

    /**
     * Convert object to bytes.
     *
     * @param value the value
     * @return the byte[]
     */
    public byte[] convertObjectToBytes(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        return null;
    }
}
