/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandSelectAll.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CommandSelectAll {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        if (Display.getDefault().getFocusControl() instanceof StyledText) {
            ((StyledText) Display.getDefault().getFocusControl()).selectAll();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {

        if (Display.getDefault().getFocusControl() instanceof StyledText) {
            return ((StyledText) Display.getDefault().getFocusControl()).getCharCount() > 0;
        }

        return false;
    }

}
