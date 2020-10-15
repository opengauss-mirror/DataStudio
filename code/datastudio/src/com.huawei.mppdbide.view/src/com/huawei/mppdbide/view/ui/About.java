/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.view.core.AboutMPPDBIDEDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class About.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class About {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        AboutMPPDBIDEDialog aboutDialog = new AboutMPPDBIDEDialog(Display.getDefault().getActiveShell());
        aboutDialog.open();
    }

}
