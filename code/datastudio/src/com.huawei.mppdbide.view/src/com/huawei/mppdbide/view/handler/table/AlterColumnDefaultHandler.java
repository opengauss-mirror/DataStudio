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
 * @since 3.0.0
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
