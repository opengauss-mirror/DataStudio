/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.trigger;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

/**
 * Title: class
 * Description: The Class TriggerDisableHandler.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 30,04,2021]
 * @since 30,04,2021
 */
public class TriggerDisableHandler extends TriggerEnableHandler {
    /**
     * Execute.
     *
     * @param Shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        baseExecute(shell, false);
    }

    /**
     * Can execute.
     *
     * @return boolean true if can execute
     */
    @CanExecute
    public boolean canExecute() {
        return baseCanExecute(false);
    }
}
