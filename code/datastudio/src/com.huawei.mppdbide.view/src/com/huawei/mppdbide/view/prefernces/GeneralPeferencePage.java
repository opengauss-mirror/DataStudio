/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class GeneralPeferencePage.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GeneralPeferencePage extends PreferencePage {

    /**
     * Instantiates a new general peference page.
     */
    public GeneralPeferencePage() {

        super(MessageConfigLoader.getProperty(IMessagesConstants.GENERAL_NODE));
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        Label lable = new Label(parent, SWT.NONE);
        lable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
        lable.setText(MessageConfigLoader.getProperty(IMessagesConstants.GENERAL_NODE_LABLE));

        return null;
    }

    /**
     * Creates the control.
     *
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {

        super.createControl(parent);
        getDefaultsButton().setVisible(false);
        getApplyButton().setVisible(false);

    }

}
