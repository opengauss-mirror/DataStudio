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

package org.opengauss.mppdbide.view.component.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.DBDisconnectConfirmationDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class RowEffectedConfirmationPrompt.
 *
 * @since 3.0.0
 */
public class RowEffectedConfirmationPrompt extends DBDisconnectConfirmationDialog {
    private static final int YES = 1;

    /**
     * Instantiates a new row effected confirmation prompt.
     *
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param dialogTitleImage the dialog title image
     * @param dialogMessage the dialog message
     * @param dialogImageType the dialog image type
     * @param subMessage the sub message
     * @param defaultIndex the default index
     */
    public RowEffectedConfirmationPrompt(Shell parentShell, String dialogTitle, Image dialogTitleImage,
            String dialogMessage, int dialogImageType, String[] subMessage, int defaultIndex) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, subMessage, defaultIndex);

    }

    /**
     * Creates the custom area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createCustomArea(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridData data = new GridData();
        data.horizontalAlignment = SWT.CENTER;
        comp.setLayout(new GridLayout());
        comp.setLayoutData(data);

        return comp;
    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        final String labelContinueBtn = "     " + MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK) + "     ";

        Button continueBtn = createButton(parent, YES, labelContinueBtn, false);
        continueBtn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CONTINUE_001");

    }

}
