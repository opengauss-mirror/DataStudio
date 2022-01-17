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

package com.huawei.mppdbide.view.utils.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class NonCloseableDialog.
 *
 * @since 3.0.0
 */
public final class NonCloseableDialog extends Dialog {
    private Label lbl;
    private String msg;
    private Shell parent;
    private ProgressIndicator progressIndicator;

    private static final int BAR_DLUS = 9;

    /**
     * Instantiates a new non closeable dialog.
     *
     * @param msg1 the msg 1
     * @param parent1 the parent 1
     */
    public NonCloseableDialog(String msg1, Shell parent1) {
        super(Display.getDefault().getActiveShell());
        this.msg = msg1;
        this.parent = parent1;
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
        composite.setLayout(new GridLayout());
        createLabel(composite);

        progressIndicator = new ProgressIndicator(composite);

        GridData gd = new GridData();
        gd.heightHint = convertVerticalDLUsToPixels(BAR_DLUS);
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        progressIndicator.setLayoutData(gd);
        progressIndicator.beginAnimatedTask();

        // Set the size of the parent shell
        composite.getShell().setSize(400, 100);

        composite.getShell().addListener(SWT.Traverse, new Listener() {

            /**
             * Handle event.
             *
             * @param event the e
             */
            public void handleEvent(Event event) {
                if (event.detail == SWT.TRAVERSE_ESCAPE) {
                    event.detail = SWT.TRAVERSE_NONE;
                    event.doit = false;
                }
            }
        });

        // Set the dialog position in the middle of the monitor
        setDialogLocation();
        return composite;
    }

    private void createLabel(Composite composite) {
        lbl = new Label(composite, SWT.None);
        lbl.setText(msg);
        GridData lblData = new GridData();
        lblData.grabExcessHorizontalSpace = true;
        lblData.horizontalAlignment = SWT.CENTER;
        lblData.verticalAlignment = SWT.CENTER;
        lblData.grabExcessVerticalSpace = true;
        lbl.setLayoutData(lblData);
    }

    /**
     * Method used to set the dialog in the centre of the monitor
     * 
     */
    private void setDialogLocation() {
        if (parent != null) {
            Rectangle monitorArea = parent.getBounds();
            Rectangle shellArea = getShell().getBounds();
            int axisX = monitorArea.x + (monitorArea.width - shellArea.width) / 2;
            int axisY = monitorArea.y + (monitorArea.height - shellArea.height) / 2;
            getShell().setLocation(axisX, axisY);
        }
    }

    /**
     * Update msg.
     *
     * @param msg1 the msg 1
     */
    public void updateMsg(String msg1) {
        msg = msg1;
        lbl.setText(msg);
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // NOTHING TO DO
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_BAR_CLOSETWO, this.getClass()));
    }
    
}
