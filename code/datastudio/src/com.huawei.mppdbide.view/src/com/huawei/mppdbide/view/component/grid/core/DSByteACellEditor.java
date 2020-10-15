/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * Title: DSByteACellEditor
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * 
 * @author aWX619007
 * @version [DataStudio 6.5.1, 22-Jan-2020]
 * @since 22-Jan-2020
 */

public class DSByteACellEditor extends DSUnstructuredDataCellEditor {
    @Override
    protected void getTableDataEditor(Object originalCanonicalValue, Shell shell) {
        DSByteATableDataEditor dialog = new DSByteATableDataEditor(shell, this, originalCanonicalValue);
        dialog.open();
    }

    /**
     * The Class DSByteATableDataEditor.
     */
    private static class DSByteATableDataEditor extends DSUnstructuredDataTableDataEditor {
        /**
         * Instantiates a new DS byte A table data editor.
         *
         * @param parentShell the parent shell
         * @param cellEditor the cell editor
         * @param originalCanonicalValue the original canonical value
         */
        public DSByteATableDataEditor(Shell parentShell, DSUnstructuredDataCellEditor cellEditor,
                Object originalCanonicalValue) {
            super(parentShell, cellEditor, originalCanonicalValue);
        }

        @Override
        protected String getEditorText() {
            return editor.getText();
        }

        @Override
        protected void addEditorTextKeyListener() {
            editor.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    status.setText("");
                    keyEvent.doit = false;
                    if (isSelectAll(keyEvent)) {
                        editor.selectAll();
                        return;
                    } else if (editor.getText().startsWith("\\x")) {
                        actionOnHexEditor(keyEvent);
                    } else {
                        enableOkBtnWhenCellEditable();
                        keyEvent.doit = true;
                    }
                }

                private void actionOnHexEditor(KeyEvent keyEvent) {
                    if (String.valueOf(keyEvent.character).matches("[0-9A-Fa-f]+") || isBackSpace(keyEvent)
                            || isPaste(keyEvent)) {
                        enableOkBtnWhenCellEditable();
                        keyEvent.doit = true;
                    } else if (isArrowKeys(keyEvent) || isCopy(keyEvent)) {
                        keyEvent.doit = true;
                    } else {
                        keyEvent.doit = false;
                    }
                }

                private boolean isSelectAll(KeyEvent keyEvent) {
                    return keyEvent.stateMask == SWT.CTRL && keyEvent.keyCode == 'a';
                }

                private boolean isPaste(KeyEvent keyEvent) {
                    return keyEvent.stateMask == SWT.CTRL && keyEvent.keyCode == 'v';
                }

                private boolean isBackSpace(KeyEvent event) {
                    return event.character == '\b';
                }

                private boolean isCopy(KeyEvent keyEvent) {
                    return keyEvent.stateMask == SWT.CTRL && keyEvent.keyCode == 'c';
                }

                private boolean isArrowKeys(KeyEvent keyEvent) {
                    return keyEvent.keyCode == SWT.ARROW_UP || keyEvent.keyCode == SWT.ARROW_DOWN
                            || keyEvent.keyCode == SWT.ARROW_LEFT
                            || keyEvent.keyCode == SWT.ARROW_RIGHT;
                }

                private void enableOkBtnWhenCellEditable() {
                    if (!cellEditor.isCellEditable()) {
                        clearBtn.setEnabled(true);
                    }
                }
            });
        }

        @Override
        protected byte[] getBytesFromEditorText(String editorTxt) {
            return editorDataToByteArray(editorTxt);
        }

        @Override
        protected String loadHexInEditorText() {
            return DSUnstructuredDataConversionHelper.bytesToHexFormated(valueBytes);
        }

        /**
         * Editor data to byte array.
         *
         * @param editorData the editor data
         * @return the byte[]
         * @throws StringIndexOutOfBoundsException the string index out of
         * bounds exception
         */
        private byte[] editorDataToByteArray(String editorData) throws StringIndexOutOfBoundsException {
            String hex = null;
            if (editorData.startsWith("\\x")) {
                hex = editorData.substring(2, editorData.length());
            } else {
                hex = DSUnstructuredDataConversionHelper.convertStringToHex(editorData);
            }
            return DSUnstructuredDataConversionHelper.hexStringToByteArray(hex);
        }
    }
}