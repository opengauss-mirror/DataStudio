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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class NonCloseableDialogWithCancel.
 *
 * @since 3.0.0
 */
public final class NonCloseableDialogWithCancel extends Dialog {
    private Label lbl;
    private String msg;
    private Shell parent;
    private ProgressIndicator progressIndicator;
    private Button cancelButton;
    private volatile DBConnectionDialog connectionDialog;

    /**
     * Instantiates a new non closeable dialog.
     *
     * @param msg1 the msg 1
     * @param parent1 the parent 1
     */
    public NonCloseableDialogWithCancel(String msg1, Shell parent1, DBConnectionDialog connectionDialog) {
        super(Display.getDefault().getActiveShell());
        this.msg = msg1;
        this.parent = parent1;
        this.connectionDialog = connectionDialog;
    }

    /**
     * Sets the shell style.
     *
     * @param arg0 the new shell style
     */
    protected void setShellStyle(int arg0) {
        // Use the following not to show the default close X button in the title
        // bar and avoid the title bar itself
        super.setShellStyle((SWT.BORDER | SWT.APPLICATION_MODAL | getDefaultOrientation()) & ~SWT.CLOSE);
    }

    /**
     * Creates the dialog area.
     *
     * @param parent1 the parent 1
     * @return the control
     */
    protected Control createDialogArea(Composite parent1) {
        /*
         * Create the dialog area where you can place the UI components
         */
        Composite composite = (Composite) super.createDialogArea(parent1);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        createLabel(composite);

        progressIndicator = new ProgressIndicator(composite);

        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        progressIndicator.setLayoutData(gd);

        cancelButton = new Button(composite, SWT.PUSH);
        cancelButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC));
        cancelButton.setEnabled(true);
        cancelButton.setLayoutData(setLayoutDataForWidget());

        cancelButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseUp(MouseEvent e) {
                connectionDialog.onCancelButtonPressedCommandline();
            }

            @Override
            public void mouseDown(MouseEvent e) {
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });

        progressIndicator.beginAnimatedTask();

        // Set the size of the parent shell
        composite.getShell().setSize(400, 100);

        // Set the dialog position in the middle of the monitor
        setDialogLocation();
        return composite;
    }

    private GridData setLayoutDataForWidget() {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.CENTER;
        gridData.verticalAlignment = SWT.CENTER;
        gridData.grabExcessVerticalSpace = true;
        return gridData;
    }

    private void createLabel(Composite composite) {
        lbl = new Label(composite, SWT.FILL);
        lbl.setText(msg);
        lbl.setLayoutData(setLayoutDataForWidget());
    }

    /**
     * Method used to set the dialog in the centre of the monitor
     * 
     */
    private void setDialogLocation() {
        if (parent != null) {
            Rectangle screenArea = parent.getBounds();
            Rectangle shellArea = getShell().getBounds();
            int axisX = screenArea.x + (screenArea.width - shellArea.width) / 2;
            int axisY = screenArea.y + (screenArea.height - shellArea.height) / 2;
            getShell().setLocation(axisX, axisY);
        }
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // NOTHING TO DO
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setImage(IconUtility.getIconImage(IiconPath.LOAD_QUERY_SQL_CLOSE, this.getClass()));
    }
    
}
