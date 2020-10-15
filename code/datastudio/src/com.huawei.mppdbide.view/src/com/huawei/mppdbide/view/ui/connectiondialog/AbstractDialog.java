/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
