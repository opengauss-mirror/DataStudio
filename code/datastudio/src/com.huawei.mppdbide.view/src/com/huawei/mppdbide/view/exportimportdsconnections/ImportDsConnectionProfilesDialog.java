/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.exportimportdsconnections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportDsConnectionProfilesDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ImportDsConnectionProfilesDialog {

    /**
     * Open dialog.
     *
     * @param shell the shell
     * @return the string
     */
    public String openDialog(Shell shell) {
        FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
        dialog.setFilterNames(new String[] {"json"});
        return dialog.open();

    }

}
