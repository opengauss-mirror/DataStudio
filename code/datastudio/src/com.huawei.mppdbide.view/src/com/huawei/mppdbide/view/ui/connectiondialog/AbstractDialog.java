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

package com.huawei.mppdbide.view.ui.connectiondialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractDialog.
 *
 * @since 3.0.0
 */
public abstract class AbstractDialog extends Dialog {

    /**
     * Instantiates a new abstract dialog.
     *
     * @param parentShell the parent shell
     */
    protected AbstractDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected Image getWindowImage() {
        return IconUtility.getIconImage(IiconPath.ICO_TOOL_128X128, this.getClass());
    }

    /**
     * Gets the header.
     *
     * @return the header
     */
    protected abstract String getHeader();

    /**
     * Checks if is read only.
     *
     * @return true, if is read only
     */
    protected boolean isReadOnly() {
        return true;
    }

    /**
     * Combo display values.
     *
     * @param inputCombo the input combo
     */
    protected abstract void comboDisplayValues(Combo inputCombo);

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    protected abstract String getWindowTitle();

    /**
     * Perform ok operation.
     */
    protected abstract void performOkOperation();
}
