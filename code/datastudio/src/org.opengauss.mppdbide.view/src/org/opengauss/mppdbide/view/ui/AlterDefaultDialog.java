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

package org.opengauss.mppdbide.view.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.ui.table.IDialogWorkerInteraction;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class AlterDefaultDialog.
 *
 * @since 3.0.0
 */
public abstract class AlterDefaultDialog extends Dialog implements IDialogWorkerInteraction {

    /**
     * The text default expr.
     */
    protected StyledText textDefaultExpr;

    /**
     * The defaut value.
     */
    protected String defautValue;
    private ColumnMetaData selectedColumn;

    /**
     * The ok button.
     */
    protected Button okButton;

    /**
     * The cancel button.
     */
    protected Button cancelButton;

    /**
     * The is default val exp.
     */
    protected Button isDefaultValExp;
    private ViewColumnMetaData column;

    /**
     * Instantiates a new alter default dialog.
     *
     * @param parent the parent
     */
    public AlterDefaultDialog(Shell parent) {
        super(parent);
    }

    /**
     * Instantiates a new alter default dialog.
     *
     * @param parentShell the parent shell
     * @param columnMetaData the column meta data
     */
    public AlterDefaultDialog(Shell parentShell, ColumnMetaData columnMetaData) {
        super(parentShell);
        selectedColumn = columnMetaData;
        defautValue = null != selectedColumn.getDefaultValue() ? selectedColumn.getDefaultValue() : "";
    }

    /**
     * Instantiates a new alter default dialog.
     *
     * @param parentShell the parent shell
     * @param viewColumn the view column
     */
    public AlterDefaultDialog(Shell parentShell, ViewColumnMetaData viewColumn) {
        super(parentShell);
        column = viewColumn;
        defautValue = null != column.getDefaultValue() ? column.getDefaultValue() : "";
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite topComposite = new Composite(parent, SWT.FILL);
        topComposite.setLayout(new GridLayout(1, true));

        Text lblcurrentValue = new Text(topComposite, SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        lblcurrentValue.setText(MessageConfigLoader.getProperty(IMessagesConstants.CUURENT_DFLT_VAL, defautValue));
        lblcurrentValue.setBackground(topComposite.getBackground());
        lblcurrentValue.setEditable(false);
        GridData layoutData = new GridData(SWT.NONE, SWT.NONE, true, true, 1, 1);
        // Initial length to current default value.
        layoutData.heightHint = 40;
        layoutData.widthHint = 400;
        // If the default value is more than 255 then set the bigger size.

        lblcurrentValue.setLayoutData(layoutData);

        Composite findComposite = new Composite(topComposite, SWT.FILL);

        GridData gdDta1 = new GridData();
        gdDta1.grabExcessHorizontalSpace = true;
        gdDta1.horizontalAlignment = GridData.FILL;
        gdDta1.verticalAlignment = GridData.FILL;
        gdDta1.horizontalIndent = 0;
        gdDta1.verticalIndent = 0;
        gdDta1.minimumWidth = 265;

        findComposite.setLayoutData(gdDta1);

        GridLayout gridLayout = new GridLayout(2, false);
        findComposite.setLayout(gridLayout);

        Label lblFindFor = new Label(findComposite, SWT.NONE);
        lblFindFor.setText(MessageConfigLoader.getProperty(IMessagesConstants.CUURENT_DFLT_VALUE));
        lblFindFor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        textDefaultExpr = new StyledText(findComposite, SWT.BORDER);
        textDefaultExpr.setBounds(176, 31, 198, 23);
        textDefaultExpr.forceFocus();
        GridData textDefaultExprData = new GridData();
        textDefaultExprData.widthHint = 280;
        textDefaultExpr.setLayoutData(textDefaultExprData);

        Label defltExpText = new Label(findComposite, SWT.NONE);
        isDefaultValExp = new Button(findComposite, SWT.CHECK);
        defltExpText.setText(MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_DEFAULT_VALUE_EXPRESSION));

        Composite buttonBarComposite = new Composite(topComposite, SWT.FILL);
        buttonBarComposite.setLayout(new GridLayout(1, true));

        buttonBarComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        Label lblPlaceHolder = new Label(buttonBarComposite, SWT.NONE);
        lblPlaceHolder.setText("");
        createButtonsForButtonBar(buttonBarComposite);

        return parent;
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        String cancelLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        String okLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";

        okButton = createButton(parent, UIConstants.OK_ID, okLbl, true);
        cancelButton = createButton(parent, UIConstants.CANCEL_ID, cancelLbl, false);

        textDefaultExpr.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (textDefaultExpr.getText().isEmpty()) {
                    okButton.setEnabled(false);
                } else {
                    okButton.setEnabled(true);
                }

            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Auto-generated method stub

            }
        });
        okButton.setEnabled(false);
        setButtonLayoutData(okButton);
    }

}
