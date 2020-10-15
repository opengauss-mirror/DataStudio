/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.DBDisconnectConfirmationDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class RowEffectedConfirmationPrompt.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
