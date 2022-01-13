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

package com.huawei.mppdbide.view.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class TextCellDialog.
 *
 * @since 3.0.0
 */
public class TextCellDialog extends Dialog {
    /**
     * the default style text ui width
     */
    public static final int STYLE_TEXT_DEFAULT_WIDTH = 600;

    /**
     * the default style text ui height
     */
    public static final int STYLE_TEXT_DEFAULT_HEIGHT = 300;

    private String textValue;
    private StyledText styledText;

    /**
     * Instantiates a new text cell dialog.
     *
     * @param maxColumnSize the max column size
     * @param shell the shell
     */
    public TextCellDialog(int maxColumnSize, Shell shell) {
        super(shell);
    }

    /**
     * Sets the text value.
     *
     * @param value the new text value
     */
    public void setTextValue(String value) {
        this.textValue = value;
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    // Construct the interface content in Dialog in this method
    @Override
    protected Control createDialogArea(Composite parent) {
        super.createDialogArea(parent);
        Composite topComp = new Composite(parent, SWT.NONE);
        topComp.setLayout(new RowLayout());
        new Label(topComp, SWT.NONE).setText(
                MessageConfigLoader.getProperty(
                        IMessagesConstants.RESULT_WINDOW_TEXT_CELL_VALUE_DIALOG_TITLE)
                );
        RowData tableNameTextGridData = new RowData();
        tableNameTextGridData.width = STYLE_TEXT_DEFAULT_WIDTH;
        tableNameTextGridData.height = STYLE_TEXT_DEFAULT_HEIGHT;
        int style = SWT.READ_ONLY | SWT.WRAP | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
        styledText = new StyledText(topComp, style);
        styledText.setBlockSelection(false);
        styledText.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_IBEAM));
        styledText.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
        styledText.setLayoutData(tableNameTextGridData);
        styledText.setText(textValue);
        styledText.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent event) {

            }

            @Override
            public void keyPressed(KeyEvent event) {

                if (event.stateMask == SWT.CTRL && event.keyCode == 'a') {
                    styledText.selectAll();
                }
            }
        });
        ;
        return topComp;
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        String okLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";

        createButton(parent, UIConstants.OK_ID, okLbl, true);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_TEXT, getClass()));
    }
    
}
