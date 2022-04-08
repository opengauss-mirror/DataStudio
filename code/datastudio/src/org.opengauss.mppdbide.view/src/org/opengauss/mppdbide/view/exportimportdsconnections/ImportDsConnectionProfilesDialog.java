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

package org.opengauss.mppdbide.view.exportimportdsconnections;

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
 * @since 3.0.0
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
