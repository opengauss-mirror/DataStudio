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

package org.opengauss.mppdbide.view.ui.connectiondialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.bl.serverdatacache.PartitionColumnExpr;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class PartitionValueDialog.
 *
 * @since 3.0.0
 */
public class PartitionValueDialog extends PartitionCommonDialog {
    private List<PartitionColumnExpr> selCols;
    private String[] colNames;
    private Text textBox;

    private boolean okPressedFlag = false;
    private Button okBtn;
    private Button cancelBtn;
    private LinkedHashMap<String, String> partitionValueMap = new LinkedHashMap<>();
    private List<String> partitionValueList = new ArrayList<String>();
    private boolean isPartitionUpdate;

    /**
     * Instantiates a new partition value dialog.
     *
     * @param activeShell the active shell
     * @param selCols the sel cols
     * @param partitionValueMap the partition value map
     * @param isPartitionUpdate the is partition update
     * @param partitionValueList the partition value list
     */
    public PartitionValueDialog(Shell activeShell, List<PartitionColumnExpr> selCols,
            LinkedHashMap<String, String> partitionValueMap, boolean isPartitionUpdate,
            List<String> partitionValueList) {
        super(activeShell);
        this.selCols = selCols;
        this.partitionValueMap = partitionValueMap;
        this.isPartitionUpdate = isPartitionUpdate;
        this.partitionValueList = partitionValueList;
    }

    /**
     * Configure shell.
     *
     * @param newShell the new shell
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_VALUE_POPUP_TITLE));
        newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()));
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        int columnCount = 0;

        createPartitionUI(parent);

        colNames = new String[selCols.size()];

        for (PartitionColumnExpr col : selCols) {
            colNames[columnCount] = col.getCol().getName();
            columnCount++;
        }

        createPartitionValueItems(colNames, colTable);
        colTable.pack();

        return parent;
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param prnt the prnt
     */
    @Override
    protected void createButtonsForButtonBar(Composite prnt) {
        String cancelLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        String okLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";

        okBtn = createButton(prnt, UIConstants.OK_ID, okLbl, true);
        validateOkButtonStatus();
        cancelBtn = createButton(prnt, UIConstants.CANCEL_ID, cancelLbl, false);
        cancelBtn.setEnabled(true);
    }

    private void validateOkButtonStatus() {
        for (int i = 0; i < colTable.getItemCount(); i++) {
            Object data = colTable.getItem(i).getData("TEXT_BTN");
            Text text = (Text) data;
            if (text != null && text.getText().isEmpty()) {
                setOkButtonState(false);
                return;
            }
        }
        setOkButtonState(true);
    }

    private void setOkButtonState(boolean state) {
        if (null != okBtn && !okBtn.isDisposed()) {
            okBtn.setEnabled(state);
        }
    }

    private void createPartitionValueItems(String[] columnNames, Table table) {
        TableItem item = null;
        TableEditor editor = null;

        for (int i = 0; i < columnNames.length; i++) {
            item = new TableItem(table, SWT.NONE);
            editor = new TableEditor(table);
            textBox = new Text(table, SWT.BORDER | SWT.SINGLE);
            textBox.setSize(100, 100);
            editor.minimumWidth = textBox.getSize().x;
            editor.horizontalAlignment = SWT.LEFT;
            if (partitionValueMap.size() > 0) {
                setTextForTableItem(textBox, columnNames[i]);
            }
            if (isPartitionUpdate) {
                textBox.setText(partitionValueList.get(i));
            }
            editor.setEditor(textBox, item, 1);
            item.setData("TEXT_BTN", textBox);
            item.setText(0, columnNames[i]);
            textBox.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    validateOkButtonStatus();
                }
            });
        }

        validateOkButtonStatus();
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        this.okPressedFlag = true;
        for (int i = 0; i < colTable.getItemCount(); i++) {
            Object data = colTable.getItem(i).getData("TEXT_BTN");
            Text text = (Text) data;
            partitionValueMap.put(colTable.getItem(i).getText(0), text == null ? "" : text.getText());
        }
        super.okPressed();
    }

    private void setTextForTableItem(Text text, String colName) {
        if (partitionValueMap.get(colName) != null) {
            text.setText(partitionValueMap.get(colName).trim());
        }
    }

    /**
     * Gets the ok pressed.
     *
     * @return the ok pressed
     */
    public boolean getOkPressed() {
        return this.okPressedFlag;
    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        if (UIConstants.OK_ID == buttonId) {
            okPressed();
        } else if (UIConstants.CANCEL_ID == buttonId) {
            cancelPressed();
        }
    }
}
