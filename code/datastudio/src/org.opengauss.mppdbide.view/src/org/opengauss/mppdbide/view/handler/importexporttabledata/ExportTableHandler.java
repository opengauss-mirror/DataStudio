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

package org.opengauss.mppdbide.view.handler.importexporttabledata;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.ITableMetaData;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportTableHandler.
 *
 * @since 3.0.0
 */
public class ExportTableHandler {

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {

        // Get the user selected table
        Object object = IHandlerUtilities.getObjectBrowserSelectedObject();
        ExportTableDataUiFactory.getExportTableDataUIInitializer(object);
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ITableMetaData table = IHandlerUtilities.getSelectedITableMetaData();
        if (null == table) {
            return false;
        }

        if (!IHandlerUtilities.getExportDataSelectionOptions()) {
            return false;
        }

        return !IHandlerUtilities.isSelectedTableForignPartition();
    }
}
