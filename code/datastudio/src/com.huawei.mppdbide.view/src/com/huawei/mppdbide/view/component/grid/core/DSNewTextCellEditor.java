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

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.config.RenderErrorHandling;
import org.eclipse.nebula.widgets.nattable.edit.editor.EditorSelectionEnum;
import org.eclipse.nebula.widgets.nattable.edit.editor.IEditErrorHandler;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.widget.EditModeEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.view.component.grid.DSGridMenuManager;
import com.huawei.mppdbide.view.component.grid.IEditTableGridStyleLabelFactory;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSNewTextCellEditor.
 *
 * @since 3.0.0
 */
public class DSNewTextCellEditor extends TextCellEditor {

    /**
     * Flag to configure whether the text control should enable automatic line
     * wrap behaviour or not. By default this editor will support automatic line
     * wrapping.
     */
    private boolean lineWrap = true;
    private Text text = null;
    private IEditErrorHandler inputConversionErrorHandler = new RenderErrorHandling(this.decorationProvider);
    private boolean isCellEditable = false;

    /**
     * Instantiates a new DS new text cell editor.
     */
    public DSNewTextCellEditor() {
        this(true);
    }

    /**
     * Instantiates a new DS new text cell editor.
     *
     * @param lineWrap the line wrap
     */
    public DSNewTextCellEditor(boolean lineWrap) {
        this.commitOnEnter = true;
        this.lineWrap = lineWrap;
    }

    /**
     * Activate cell.
     *
     * @param parent the parent
     * @param originalCanonicalValue the original canonical value
     * @return the control
     */
    @Override
    public Control activateCell(final Composite parent, Object originalCanonicalValue) {

        this.text = createEditorControl(parent);
        isCellEditable = labelStack.hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL);
        DSGridMenuManager.addContextMenuWithID(getEditorControl(), !isCellEditable);
        // If the originalCanonicalValue is a Character it is possible the
        // editor is activated by keypress
        if (originalCanonicalValue instanceof Character) {
            this.text.setText(originalCanonicalValue.toString());
            selectNewText(EditorSelectionEnum.END);
        }
        // if there is no initial value, handle the original canonical value to
        // transfer it to the text control
        else {
            setCanonicalValue(originalCanonicalValue);
            selectNewText(EditorSelectionEnum.ALL);
        }

        this.text.setEditable(isEditable());

        // show an error decoration if this is enabled
        this.decorationProvider.createErrorDecorationIfRequired(this.text);

        // if the input error handlers are of type RenderErrorHandler (default)
        // than we also check for a possible configured error styling in the
        // configuration
        // Note: this is currently only implemented in here, as the
        // TextCellEditor is the only editor that supports just in time
        // conversion/validation

        IStyle conversionErrorStyle = this.configRegistry.getConfigAttribute(
                EditConfigAttributes.CONVERSION_ERROR_STYLE, DisplayMode.EDIT, this.labelStack.getLabels());

        ((RenderErrorHandling) this.inputConversionErrorHandler).setErrorStyle(conversionErrorStyle);

        // if a IControlContentAdapter is registered, create and register a
        // ContentProposalAdapter
        if (this.controlContentAdapter != null) {
            configureContentProposalAdapter(new ContentProposalAdapter(this.text, this.controlContentAdapter,
                    this.proposalProvider, this.keyStroke, this.autoActivationCharacters));
        }

        this.text.forceFocus();

        return this.text;

    }

    /**
     * Creates the editor control.
     *
     * @param parent the parent
     * @return the text
     */
    @Override
    public Text createEditorControl(Composite parent) {

        boolean openInline = openInline(this.configRegistry, this.labelStack.getLabels());
        int style = HorizontalAlignmentEnum.getSWTStyle(this.cellStyle) | SWT.MULTI | SWT.BORDER;

        if (!openInline) {
            // if the editor control is opened in a dialog, we add scrolling as
            // the size of the control is dependent on the dialog size
            style = style | SWT.V_SCROLL | SWT.H_SCROLL;
        } else {
            this.commitOnEnter = true;
        }

        if (this.lineWrap) {
            style = style | SWT.WRAP;
        }

        final Text textControl = new Text(parent, style);

        textControl.forceFocus();
        if (!openInline) {
            // add the layout data directly so it will not be layouted by the
            // CellEditDialog
            GridDataFactory.fillDefaults().grab(true, true).hint(100, 50).applyTo(textControl);
        }

        // add a key listener that will commit or close the editor for special
        // key strokes and executes conversion/validation on input to the editor

        textControl.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent event) {
                if (DSNewTextCellEditor.this.commitOnEnter
                        && (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR)) {
                    MoveDirectionEnum move = MoveDirectionEnum.NONE;
                    if (DSNewTextCellEditor.this.editMode == EditModeEnum.INLINE) {
                        move = MoveDirectionEnum.DOWN;
                    }

                    if (openInline) {
                        commit(move);
                    } else {
                        event.doit = false;
                    }

                }
                if (event.stateMask == SWT.CTRL && event.keyCode == 'a') {
                    textControl.selectAll();
                }
            }

        });

        return textControl;
    }

    /**
     * Calculate control bounds.
     *
     * @param cellBounds the cell bounds
     * @return the rectangle
     */
    @Override
    public Rectangle calculateControlBounds(final Rectangle cellBounds) {
        Point size = getEditorControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);

        // add a listener that increases/decreases the size of the control if
        // the text is modified as the calculateControlBounds method is only
        // called in case of inline editing, this listener shouldn't hurt
        // anybody else
        getEditorControl().addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                Point point = getEditorControl().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
                Point loc = getEditorControl().getLocation();
                getEditorControl().setBounds(loc.x, loc.y, cellBounds.width, Math.max(point.y, cellBounds.height));
            }
        });

        return new Rectangle(cellBounds.x, cellBounds.y, cellBounds.width, Math.max(size.y, cellBounds.height));
    }

    /**
     * Sets the line wrap.
     *
     * @param lineWrap the new line wrap
     */
    public void setLineWrap(boolean lineWrap) {
        this.lineWrap = lineWrap;
    }

    /**
     * Gets the editor control.
     *
     * @return the editor control
     */
    @Override
    public Text getEditorControl() {
        return this.text;
    }

    /**
     * Gets the editor value.
     *
     * @return the editor value
     */
    @Override
    public String getEditorValue() {
        return this.text.getText();
    }

    /*
     * * Will set the selection to the wrapped text control regarding the
     * configured {@link EditorSelectionEnum}.
     *
     * <p> This method is called
     *
     * Text#setSelection(int, int)
     */
    private void selectNewText(EditorSelectionEnum editorSelectionMode) {
        int textLength = this.text.getText().length();
        if (textLength > 0) {
            if (editorSelectionMode == EditorSelectionEnum.ALL) {
                this.text.setSelection(0, textLength);
            } else if (editorSelectionMode == EditorSelectionEnum.END) {
                this.text.setSelection(textLength, textLength);
            } else if (editorSelectionMode == EditorSelectionEnum.START) {
                this.text.setSelection(0);
            }
        }
    }

    /**
     * Sets the editor value.
     *
     * @param value the new editor value
     */
    @Override
    public void setEditorValue(Object value) {
        this.text.setText(value != null && value.toString().length() > 0 ? value.toString() : ""); // $NON-NLS-1$
    }

}
