/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.view.filesave.SaveReloadSQLQueries;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class OpenSQLHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class OpenSQLHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        /**
         * code for opening SQL file with database connection selection
         */

        SQLTerminal sqlTerminal = UIElement.getInstance().getVisibleTerminal();
        if (sqlTerminal == null) {
            return;
        }

        SaveReloadSQLQueries saveReloadSQLQueries = new SaveReloadSQLQueries();
        saveReloadSQLQueries.openSQLFile(sqlTerminal);
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        SQLTerminal sqlTerminal = UIElement.getInstance().getVisibleTerminal();
        if (sqlTerminal == null || sqlTerminal.isFileTerminalFlag()) {
            return false;
        }
        return true;
    }
}
