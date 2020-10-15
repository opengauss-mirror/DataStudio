/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class TextCellDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class TextCellDialog extends Dialog {
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
                MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_TEXT_CELL_VALUE_DIALOG_TITLE));
        RowData tableNameTextGridData = new RowData();
        tableNameTextGridData.width = Dialog.DIALOG_DEFAULT_BOUNDS;
        tableNameTextGridData.height = Dialog.DIALOG_DEFAULT_BOUNDS;
        styledText = new StyledText(topComp, SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
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
