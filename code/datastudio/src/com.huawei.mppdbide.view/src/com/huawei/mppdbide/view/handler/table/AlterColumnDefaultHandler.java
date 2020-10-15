/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.table.AlterColumnDefaultDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class AlterColumnDefaultHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class AlterColumnDefaultHandler {

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof ColumnMetaData) {
            ColumnMetaData columnMetaData = (ColumnMetaData) obj;
            AlterColumnDefaultDialog alterColumnDefaultDialog = new AlterColumnDefaultDialog(shell, columnMetaData);
            alterColumnDefaultDialog.open();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ColumnMetaData columnMetaData = IHandlerUtilities.getSelectedColumn();
        if (columnMetaData == null) {
            return false;
        }
        if (columnMetaData.getParentTable() instanceof ForeignTable) {
            return false;
        } else {
            return true;
        }

    }

}
