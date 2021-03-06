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

package org.opengauss.mppdbide.view.core.edittabledata;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

/**
 * 
 * Title: class
 * 
 * Description: The Class CellEdit.
 *
 * @since 3.0.0
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
