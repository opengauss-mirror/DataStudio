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

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.groups.ConstraintList;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.table.AddConstraint;

/**
 * 
 * Title: class
 * 
 * Description: The Class AddConstraintHandler.
 *
 * @since 3.0.0
 */
public class AddConstraintHandler {

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        ConstraintList constraintList = IHandlerUtilities.getSelectedConstraintGroup();
        if (constraintList != null) {
            AddConstraint addColumnDlg = new AddConstraint(shell, constraintList.getParent());
            addColumnDlg.open();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ConstraintList constraintList = IHandlerUtilities.getSelectedConstraintGroup();

        if (null != constraintList && (constraintList.getParent() instanceof ForeignTable)) {
            return false;
        } else {
            return null != constraintList;
        }
    }
}
