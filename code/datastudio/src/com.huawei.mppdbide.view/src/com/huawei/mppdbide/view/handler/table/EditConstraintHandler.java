/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.groups.ConstraintList;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.table.AddConstraint;
import com.huawei.mppdbide.view.ui.table.ModifyConstraintData;

/**
 *
 * Title: class
 *
 * Description: The Class AddConstraintHandler.
 *
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class EditConstraintHandler {
    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        ConstraintMetaData constraintMetaData = IHandlerUtilities.getSelectedConstraint();
        if (constraintMetaData != null) {
            AddConstraint addColumnDlg = new AddConstraint(shell, constraintMetaData.getTable());
            addColumnDlg.setModifyConstraint(new ModifyConstraintData(constraintMetaData));
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
        ConstraintMetaData constraintMetaData = IHandlerUtilities.getSelectedConstraint();

        if (constraintMetaData != null && (constraintMetaData.getTable() instanceof ForeignTable)) {
            return false;
        } else {
            return constraintMetaData != null;
        }
    }
}
