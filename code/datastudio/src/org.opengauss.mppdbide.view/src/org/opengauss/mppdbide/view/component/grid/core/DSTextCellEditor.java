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

package org.opengauss.mppdbide.view.component.grid.core;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.component.grid.DSGridMenuManager;
import org.opengauss.mppdbide.view.component.grid.IEditTableGridStyleLabelFactory;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSTextCellEditor.
 *
 * @since 3.0.0
 */
public class DSTextCellEditor extends TextCellEditor {
    private boolean isCellEditable = false;

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
        DSGridMenuManager.addContextMenuWithID(getEditorControl(), !isCellEditable);
        getEditorControl().addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent event) {
                if (isCellEditable
                        && !(event.stateMask == SWT.CTRL && event.keyCode == MPPDBIDEConstants.KEY_CODE_FOR_COPY)) {
                    event.doit = false;
                }
            }

            @Override
            public void keyPressed(KeyEvent event) {
                if (isCellEditable
                        && !(event.stateMask == SWT.CTRL && event.keyCode == MPPDBIDEConstants.KEY_CODE_FOR_COPY)) {
                    event.doit = false;
                }
            }
        });

        if (null != originalCanonicalValue
                && originalCanonicalValue.toString().length() > IEditTableGridStyleLabelFactory.CANONICAL_LIMIT) {
            Shell shell = Display.getDefault().getActiveShell();
            DSTableDataTextEditor dialog = new DSTableDataTextEditor(shell, originalCanonicalValue.toString());
            dialog.open();
            super.close();
        }

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
        if (configLabels.contains(IEditTableGridStyleLabelFactory.COL_LABEL_NOT_SUPPORTED_MULTIDIALOG)) {
            generateCellSizeExceededDialog();
            return false;
        }
        if (configLabels.contains(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL)) {
            return false;
        }
        return super.supportMultiEdit(configRegistry, configLabels);
    }

    /**
     * Generate cell size exceeded dialog.
     */
    protected void generateCellSizeExceededDialog() {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                MessageConfigLoader.getProperty(IMessagesConstants.DATA_TOO_LARGE_WARNING),
                MessageConfigLoader.getProperty(IMessagesConstants.DATA_TOO_LARGE_DISPLAY_EDIT));
    }

    private class DSTableDataTextEditor extends Dialog {
        private Text editor = null;
        private String value = null;
        private Button okBtn = null;

        /**
         * Gets the editor text.
         *
         * @return the editor text
         */
        public String getEditorText() {
            return editor.getText();
        }

        /**
         * Instantiates a new DS table data text editor.
         *
         * @param parentShell the parent shell
         * @param originalCanonicalValue the original canonical value
         */
        public DSTableDataTextEditor(Shell parentShell, String originalCanonicalValue) {
            super(parentShell);
            this.value = originalCanonicalValue;
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite maincomp = (Composite) super.createDialogArea(parent);
            maincomp.setLayout(new GridLayout(1, false));
            GridData maincompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
            maincomp.setLayoutData(maincompGD);

            editor = new Text(maincomp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
            GridData editorGD = new GridData(SWT.FILL, SWT.FILL, true, true);
            editor.setLayoutData(editorGD);
            editor.setText(value);

            editor.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent event) {
                    if (event.stateMask == SWT.CTRL && event.keyCode == 'a') {
                        editor.selectAll();
                        event.doit = false;
                    }
                }
            });

            return maincomp;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            String okLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";
            final String cancelLabel = "     "
                    + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC) + "     ";
            okBtn = createButton(parent, UIConstants.OK_ID, okLbl, true);
            createButton(parent, UIConstants.CANCEL_ID, cancelLabel, false);

            configureEditorAndButton();
        }

        private void configureEditorAndButton() {
            editor.setEditable(!isCellEditable);
            okBtn.setEnabled(!isCellEditable);
        }

        @Override
        protected void configureShell(Shell newShell) {
            newShell.setSize(800, 400);
            super.configureShell(newShell);

            newShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDITOR_NODE));
            newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_EDIT_EDIT, this.getClass()));
        }

        @Override
        protected void buttonPressed(int buttonId) {

            if (buttonId == UIConstants.OK_ID) {
                setEditorValue(getEditorText());
                commit(MoveDirectionEnum.NONE, true);
                close();
            } else {
                close();
            }
        }
    }
}
