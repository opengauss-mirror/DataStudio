/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.edittabledata;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

/**
 * 
 * Title: class
 * 
 * Description: The Class CellEdit.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CellEdit {
    private static boolean isColEditSelect = false;

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        CellEdit.setEditSelect(true);
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return true;
    }

    /**
     * Checks if is edits the select.
     *
     * @return true, if is edits the select
     */
    public static boolean isEditSelect() {
        return isColEditSelect;
    }

    /**
     * Sets the edits the select.
     *
     * @param isColEditSelec the new edits the select
     */
    public static void setEditSelect(boolean isColEditSelec) {
        CellEdit.isColEditSelect = isColEditSelec;
    }

}
